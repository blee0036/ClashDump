package edu.cpcc.dumplings.service.clash.module

import android.app.Service
import edu.cpcc.dumplings.common.constants.Intents
import edu.cpcc.dumplings.common.log.Log

class CloseModule(service: Service) : Module<CloseModule.RequestClose>(service) {
    object RequestClose

    override suspend fun run() {
        val broadcasts = receiveBroadcast {
            addAction(Intents.ACTION_CLASH_REQUEST_STOP)
        }

        broadcasts.receive()

        Log.d("User request close")

        return enqueueEvent(RequestClose)
    }
}