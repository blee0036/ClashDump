package edu.cpcc.dumplings.remote

import android.content.Context
import android.net.Uri
import edu.cpcc.dumplings.common.constants.Authorities
import edu.cpcc.dumplings.common.log.Log
import edu.cpcc.dumplings.service.StatusProvider

class StatusClient(private val context: Context) {
    private val uri: Uri
        get() {
            return Uri.Builder()
                .scheme("content")
                .authority(Authorities.STATUS_PROVIDER)
                .build()
        }

    fun currentProfile(): String? {
        return try {
            val result = context.contentResolver.call(
                uri,
                StatusProvider.METHOD_CURRENT_PROFILE,
                null,
                null
            )

            result?.getString("name")
        } catch (e: Exception) {
            Log.w("Query current profile: $e", e)

            null
        }
    }
}