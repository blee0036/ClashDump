package edu.cpcc.dumplings

import android.app.Application
import android.content.Context
import edu.cpcc.dumplings.common.Global
import edu.cpcc.dumplings.common.compat.currentProcessName
import edu.cpcc.dumplings.common.log.Log
import edu.cpcc.dumplings.remote.Remote
import edu.cpcc.dumplings.service.util.sendServiceRecreated

@Suppress("unused")
class MainApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        Global.init(this)
    }

    override fun onCreate() {
        super.onCreate()

        val processName = currentProcessName

        Log.d("Process $processName started")

        if (processName == packageName) {
            Remote.launch()
        } else {
            sendServiceRecreated()
        }
    }

    fun finalize() {
        Global.destroy()
    }
}