package edu.cpcc.dumplings.service.clash.module

import android.app.Service
import android.content.Intent
import java.util.*

class TimeZoneModule(service: Service) : Module<Unit>(service) {
    override suspend fun run() {
        val timeZones = receiveBroadcast {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }

        while (true) {
            val timeZone = TimeZone.getDefault()

            _root_ide_package_.com.github.kr328.clash.core.Clash.notifyTimeZoneChanged(timeZone.id, timeZone.rawOffset)

            timeZones.receive()
        }
    }
}