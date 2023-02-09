package edu.cpcc.dumplings.log

import android.content.Context
import edu.cpcc.dumplings.core.model.LogMessage
import edu.cpcc.dumplings.design.model.LogFile
import edu.cpcc.dumplings.util.logsDir
import java.io.BufferedWriter
import java.io.FileWriter

class LogcatWriter(context: Context) : AutoCloseable {
    private val file = LogFile.generate()
    private val writer = BufferedWriter(FileWriter(context.logsDir.resolve(file.fileName)))

    override fun close() {
        writer.close()
    }

    fun appendMessage(message: LogMessage) {
        writer.appendLine(FORMAT.format(message.time.time, message.level.name, message.message))
    }

    companion object {
        private const val FORMAT = "%d:%s:%s"
    }
}