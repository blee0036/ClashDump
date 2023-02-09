package edu.cpcc.dumplings

import edu.cpcc.dumplings.common.util.intent
import edu.cpcc.dumplings.common.util.setFileName
import edu.cpcc.dumplings.design.LogsDesign
import edu.cpcc.dumplings.design.model.LogFile
import edu.cpcc.dumplings.util.logsDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class LogsActivity : BaseActivity<LogsDesign>() {
    override suspend fun main() {
        if (LogcatService.running) {
            return startActivity(LogcatActivity::class.intent)
        }

        val design = LogsDesign(this)

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ActivityStart -> {
                            val files = withContext(Dispatchers.IO) {
                                loadFiles()
                            }

                            design.patchLogs(files)
                        }
                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        LogsDesign.Request.StartLogcat -> {
                            startActivity(LogcatActivity::class.intent)

                            finish()
                        }
                        LogsDesign.Request.DeleteAll -> {
                            if (design.requestDeleteAll()) {
                                withContext(Dispatchers.IO) {
                                    deleteAllLogs()
                                }

                                events.trySend(Event.ActivityStart)
                            }
                        }
                        is LogsDesign.Request.OpenFile -> {
                            startActivity(LogcatActivity::class.intent.setFileName(it.file.fileName))
                        }
                    }
                }
            }
        }
    }

    private fun loadFiles(): List<LogFile> {
        val list = cacheDir.resolve("logs").listFiles()?.toList() ?: emptyList()

        return list.mapNotNull { LogFile.parseFromFileName(it.name) }
    }

    private fun deleteAllLogs() {
        logsDir.deleteRecursively()
    }
}