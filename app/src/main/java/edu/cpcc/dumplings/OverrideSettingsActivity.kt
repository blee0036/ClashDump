package edu.cpcc.dumplings

import android.content.pm.PackageManager
import edu.cpcc.dumplings.common.compat.getDrawableCompat
import edu.cpcc.dumplings.common.constants.Metadata
import edu.cpcc.dumplings.core.Clash
import edu.cpcc.dumplings.design.OverrideSettingsDesign
import edu.cpcc.dumplings.design.model.AppInfo
import edu.cpcc.dumplings.design.util.toAppInfo
import edu.cpcc.dumplings.service.store.ServiceStore
import edu.cpcc.dumplings.util.withClash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class OverrideSettingsActivity : BaseActivity<OverrideSettingsDesign>() {
    override suspend fun main() {
        val configuration = withClash { queryOverride(Clash.OverrideSlot.Persist) }
        val service = ServiceStore(this)

        defer {
            withClash {
                patchOverride(Clash.OverrideSlot.Persist, configuration)
            }
        }

        val design = OverrideSettingsDesign(
            this,
            configuration
        )

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        OverrideSettingsDesign.Request.ResetOverride -> {
                            if (design.requestResetConfirm()) {
                                defer {
                                    withClash {
                                        clearOverride(Clash.OverrideSlot.Persist)
                                    }

                                    service.sideloadGeoip = ""
                                }

                                finish()
                            }
                        }
                        OverrideSettingsDesign.Request.EditSideloadGeoip -> {
                            withContext(Dispatchers.IO) {
                                val list = querySideloadProviders()
                                val initial = service.sideloadGeoip
                                val exist = list.any { info -> info.packageName == initial }

                                service.sideloadGeoip =
                                    design.requestSelectSideload(if (exist) initial else "", list)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun querySideloadProviders(): List<AppInfo> {
        val apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .filter {
                it.applicationInfo.metaData?.containsKey(Metadata.GEOIP_FILE_NAME)
                    ?: false
            }
            .map { it.toAppInfo(packageManager) }

        return listOf(
            AppInfo(
                packageName = "",
                label = getString(R.string.use_built_in),
                icon = getDrawableCompat(R.drawable.ic_baseline_work)!!,
                installTime = 0,
                updateDate = 0,
            )
        ) + apps
    }
}