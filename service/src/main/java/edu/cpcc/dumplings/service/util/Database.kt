package edu.cpcc.dumplings.service.util

import edu.cpcc.dumplings.service.data.ImportedDao
import edu.cpcc.dumplings.service.data.PendingDao
import java.util.*

suspend fun generateProfileUUID(): UUID {
    var result = UUID.randomUUID()

    while (ImportedDao().exists(result) || PendingDao().exists(result)) {
        result = UUID.randomUUID()
    }

    return result
}
