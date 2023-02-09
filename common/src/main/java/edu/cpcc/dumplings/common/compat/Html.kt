@file:Suppress("DEPRECATION")

package edu.cpcc.dumplings.common.compat

import android.os.Build
import android.text.Html
import android.text.Spanned

fun fromHtmlCompat(content: String): Spanned {
    return if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(content)
    }
}