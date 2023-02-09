package edu.cpcc.dumplings.util

import android.content.Context
import android.content.Intent
import android.net.VpnService
import edu.cpcc.dumplings.common.compat.startForegroundServiceCompat
import edu.cpcc.dumplings.common.constants.Intents
import edu.cpcc.dumplings.common.util.intent
import edu.cpcc.dumplings.design.store.UiStore
import edu.cpcc.dumplings.service.ClashService
import edu.cpcc.dumplings.service.TunService
import edu.cpcc.dumplings.service.util.sendBroadcastSelf

fun Context.startClashService(): Intent? {
    val startTun = UiStore(this).enableVpn

    if (startTun) {
        val vpnRequest = VpnService.prepare(this)
        if (vpnRequest != null)
            return vpnRequest

        startForegroundServiceCompat(TunService::class.intent)
    } else {
        startForegroundServiceCompat(ClashService::class.intent)
    }

    return null
}

fun Context.stopClashService() {
    sendBroadcastSelf(Intent(Intents.ACTION_CLASH_REQUEST_STOP))
}