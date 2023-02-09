package edu.cpcc.dumplings.remote

import android.content.Context
import android.content.Intent
import edu.cpcc.dumplings.ApkBrokenActivity
import edu.cpcc.dumplings.AppCrashedActivity
import edu.cpcc.dumplings.common.Global
import edu.cpcc.dumplings.common.util.intent
import edu.cpcc.dumplings.store.AppStore
import edu.cpcc.dumplings.util.ApplicationObserver
import edu.cpcc.dumplings.util.verifyApk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

object Remote {
    val broadcasts: Broadcasts = Broadcasts(Global.application)
    val service: Service = Service(Global.application) {
        ApplicationObserver.createdActivities.forEach { it.finish() }

        val intent = AppCrashedActivity::class.intent
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Global.application.startActivity(intent)
    }

    private val visible = Channel<Boolean>(Channel.CONFLATED)

    fun launch() {
        ApplicationObserver.attach(Global.application)

        ApplicationObserver.onVisibleChanged { visible.trySend(it) }

        Global.launch(Dispatchers.IO) {
            run()
        }
    }

    private suspend fun run() {
        val context = Global.application
        val store = AppStore(context)
        val updatedAt = getLastUpdated(context)

        if (store.updatedAt != updatedAt) {
            if (!context.verifyApk()) {
                ApplicationObserver.createdActivities.forEach { it.finish() }

                val intent = ApkBrokenActivity::class.intent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                return context.startActivity(intent)
            } else {
                store.updatedAt = updatedAt
            }
        }

        while (true) {
            if (visible.receive()) {
                service.bind()
                broadcasts.register()
            } else {
                service.unbind()
                broadcasts.unregister()
            }
        }
    }

    private fun getLastUpdated(context: Context): Long {
        return context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
    }
}