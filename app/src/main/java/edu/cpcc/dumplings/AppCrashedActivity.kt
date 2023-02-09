package edu.cpcc.dumplings

import edu.cpcc.dumplings.common.compat.versionCodeCompat
import edu.cpcc.dumplings.common.log.Log
import edu.cpcc.dumplings.design.AppCrashedDesign
import edu.cpcc.dumplings.log.SystemLogcat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class AppCrashedActivity : BaseActivity<AppCrashedDesign>() {
    override suspend fun main() {
        val design = AppCrashedDesign(this)

        setContentDesign(design)

        val packageInfo = withContext(Dispatchers.IO) {
            packageManager.getPackageInfo(packageName, 0)
        }

        Log.i("App version: versionName = ${packageInfo.versionName} versionCode = ${packageInfo.versionCodeCompat}")

        val logs = withContext(Dispatchers.IO) {
            SystemLogcat.dumpCrash()
        }

        design.setAppLogs(logs)

        while (isActive) {
            events.receive()
        }
    }
}