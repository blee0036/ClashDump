package edu.cpcc.dumplings.service.clash.module

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import edu.cpcc.dumplings.common.compat.getColorCompat
import edu.cpcc.dumplings.common.compat.pendingIntentFlags
import edu.cpcc.dumplings.common.constants.Components
import edu.cpcc.dumplings.common.constants.Intents
import edu.cpcc.dumplings.common.util.ticker
import edu.cpcc.dumplings.core.util.trafficDownload
import edu.cpcc.dumplings.core.util.trafficUpload
import edu.cpcc.dumplings.service.R
import edu.cpcc.dumplings.service.StatusProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import java.util.concurrent.TimeUnit

class DynamicNotificationModule(service: Service) : Module<Unit>(service) {
    private val builder = NotificationCompat.Builder(service, StaticNotificationModule.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_logo_service)
        .setOngoing(true)
        .setColor(service.getColorCompat(R.color.color_clash))
        .setOnlyAlertOnce(true)
        .setShowWhen(false)
        .setContentTitle("Not Selected")
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        .setContentIntent(
            PendingIntent.getActivity(
                service,
                R.id.nf_clash_status,
                Intent().setComponent(Components.MAIN_ACTIVITY)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT)
            )
        )

    private fun update() {
        val now = _root_ide_package_.com.github.kr328.clash.core.Clash.queryTrafficNow()
        val total = _root_ide_package_.com.github.kr328.clash.core.Clash.queryTrafficTotal()

        val uploading = now.trafficUpload()
        val downloading = now.trafficDownload()
        val uploaded = total.trafficUpload()
        val downloaded = total.trafficDownload()

        val notification = builder
            .setContentText(
                service.getString(
                    R.string.clash_notification_content,
                    "$uploading/s", "$downloading/s"
                )
            )
            .setSubText(
                service.getString(
                    R.string.clash_notification_content,
                    uploaded, downloaded
                )
            )
            .build()

        service.startForeground(R.id.nf_clash_status, notification)
    }

    override suspend fun run() = coroutineScope {
        var shouldUpdate = service.getSystemService<PowerManager>()?.isInteractive ?: true

        val screenToggle = receiveBroadcast(false, Channel.CONFLATED) {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        val profileLoaded = receiveBroadcast(capacity = Channel.CONFLATED) {
            addAction(Intents.ACTION_PROFILE_LOADED)
        }

        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

        while (true) {
            select<Unit> {
                screenToggle.onReceive {
                    when (it.action) {
                        Intent.ACTION_SCREEN_ON ->
                            shouldUpdate = true
                        Intent.ACTION_SCREEN_OFF ->
                            shouldUpdate = false
                    }
                }
                profileLoaded.onReceive {
                    builder.setContentTitle(StatusProvider.currentProfile ?: "Not selected")
                }
                if (shouldUpdate) {
                    ticker.onReceive {
                        update()
                    }
                }
            }
        }
    }
}