package edu.cpcc.dumplings.log

import android.content.Context
import edu.cpcc.dumplings.core.model.LogMessage
import edu.cpcc.dumplings.design.model.LogFile
import edu.cpcc.dumplings.util.logsDir
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

class LogcatReader(context: Context, file: LogFile) : AutoCloseable {
    private val reader = BufferedReader(FileReader(context.logsDir.resolve(file.fileName)))

    override fun close() {
        reader.close()
    }

    fun readAll(): List<LogMessage> {
        return reader.lineSequence()
            .map { it.trim() }
            .filter { !it.startsWith("#") }
            .map { it.split(":", limit = 3) }
            .map {
                LogMessage(
                    time = Date(it[0].toLong()),
                    level = LogMessage.Level.valueOf(it[1]),
                    message = it[2]
                )
            }
            .toList()
    }
}