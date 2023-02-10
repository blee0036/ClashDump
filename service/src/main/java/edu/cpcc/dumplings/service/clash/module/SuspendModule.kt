package edu.cpcc.dumplings.service.clash.module

import android.app.Service
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.getSystemService
import edu.cpcc.dumplings.common.log.Log
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext

class SuspendModule(service: Service) : Module<Unit>(service) {
    override suspend fun run() {
        val interactive = service.getSystemService<PowerManager>()?.isInteractive ?: true

        com.github.kr328.clash.core.Clash.suspendCore(!interactive)

        val screenToggle = receiveBroadcast(false, Channel.CONFLATED) {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }

        try {
            while (true) {
                when (screenToggle.receive().action) {
                    Intent.ACTION_SCREEN_ON -> {
                        com.github.kr328.clash.core.Clash.suspendCore(false)

                        Log.d("Clash resumed")
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        com.github.kr328.clash.core.Clash.suspendCore(true)

                        Log.d("Clash suspended")
                    }
                    else -> {
                        // unreachable

                        com.github.kr328.clash.core.Clash.healthCheckAll()
                    }
                }
            }
        } finally {
            withContext(NonCancellable) {
                com.github.kr328.clash.core.Clash.suspendCore(false)
            }
        }
    }
}