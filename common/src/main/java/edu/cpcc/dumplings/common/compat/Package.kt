@file:Suppress("DEPRECATION")

package edu.cpcc.dumplings.common.compat

import android.content.pm.PackageInfo

val PackageInfo.versionCodeCompat: Long
    get() {
        return if (android.os.Build.VERSION.SDK_INT >= 28) {
            longVersionCode
        } else {
            versionCode.toLong()
        }
    }
