package edu.cpcc.dumplings.design.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import edu.cpcc.dumplings.common.compat.foreground
import edu.cpcc.dumplings.design.model.AppInfo

fun PackageInfo.toAppInfo(pm: PackageManager): AppInfo {
    return AppInfo(
        packageName = packageName,
        icon = applicationInfo.loadIcon(pm).foreground(),
        label = applicationInfo.loadLabel(pm).toString(),
        installTime = firstInstallTime,
        updateDate = lastUpdateTime,
    )
}
