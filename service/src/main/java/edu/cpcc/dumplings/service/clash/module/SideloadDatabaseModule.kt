package edu.cpcc.dumplings.service.clash.module

import android.app.Service
import android.content.Intent
import edu.cpcc.dumplings.common.constants.Intents
import edu.cpcc.dumplings.common.log.Log
import edu.cpcc.dumplings.core.Clash
import edu.cpcc.dumplings.service.sideload.readGeoipDatabaseFrom
import edu.cpcc.dumplings.service.store.ServiceStore
import edu.cpcc.dumplings.service.util.packageName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import java.io.FileNotFoundException
import java.io.IOException

class SideloadDatabaseModule(service: Service) :
    Module<SideloadDatabaseModule.LoadException>(service) {
    data class LoadException(val message: String)

    private val store = ServiceStore(service)

    private var current: String = ""

    override suspend fun run() {
        val packagesChanged = receiveBroadcast(false) {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addDataScheme("package")
        }
        val profileChanged = receiveBroadcast(capacity = Channel.CONFLATED) {
            addAction(Intents.ACTION_PROFILE_CHANGED)
        }
        val initial = Channel<Unit>(1).apply { send(Unit) }

        while (true) {
            val (reload, force) = select<Pair<Boolean, Boolean>> {
                packagesChanged.onReceive {
                    when (it.action) {
                        Intent.ACTION_PACKAGE_ADDED ->
                            (it.packageName == store.sideloadGeoip) to true
                        Intent.ACTION_PACKAGE_REPLACED ->
                            (it.packageName == current) to true
                        Intent.ACTION_PACKAGE_FULLY_REMOVED ->
                            (it.packageName == current) to true
                        else -> false to false
                    }
                }
                profileChanged.onReceive {
                    true to false
                }
                initial.onReceive {
                    true to true
                }
            }

            if (!reload) continue

            val pkg = store.sideloadGeoip

            try {
                if (!force && pkg == current)
                    continue

                current = pkg

                if (pkg.isNotBlank()) {
                    val data = service.readGeoipDatabaseFrom(pkg)

                    Clash.installSideloadGeoip(data)

                    if (data != null) {
                        Log.d("Sideload geoip loaded, pkg = $pkg")
                    } else {
                        Log.d("Sideload geoip not found")
                    }
                }
            } catch (e: FileNotFoundException) {
                return enqueueEvent(LoadException("file $pkg/assets/${e.message} not found"))
            } catch (e: IOException) {
                return enqueueEvent(LoadException("read data from $pkg: ${e.message}"))
            } catch (e: Exception) {
                return enqueueEvent(LoadException(e.toString()))
            }
        }
    }
}