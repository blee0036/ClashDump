package edu.cpcc.dumplings.service.clash.module

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInfo
import edu.cpcc.dumplings.common.log.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class AppListCacheModule(service: Service) : Module<Unit>(service) {
    private fun PackageInfo.uniqueUidName(): String =
        if (sharedUserId != null && sharedUserId.isNotBlank()) sharedUserId else packageName

    private fun reload() {
        val packages = service.packageManager.getInstalledPackages(0)
            .groupBy { it.uniqueUidName() }
            .map { (_, v) ->
                val info = v[0]

                if (v.size == 1) {
                    // Force use package name if only one app in a single sharedUid group
                    // Example: firefox

                    info.applicationInfo.uid to info.packageName
                } else {
                    info.applicationInfo.uid to info.uniqueUidName()
                }
            }

        com.github.kr328.clash.core.Clash.notifyInstalledAppsChanged(packages)

        Log.d("Installed ${packages.size} packages cached")
    }

    override suspend fun run() {
        val packageChanged = receiveBroadcast(false, Channel.CONFLATED) {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

        while (true) {
            reload()

            packageChanged.receive()

            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }
}