package edu.cpcc.dumplings.service.clash

import edu.cpcc.dumplings.common.log.Log
import edu.cpcc.dumplings.service.clash.module.Module
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val globalLock = Mutex()

interface ClashRuntimeScope {
    fun <E, T : Module<E>> install(module: T): T
}

interface ClashRuntime {
    fun launch()
    fun requestGc()
}

fun CoroutineScope.clashRuntime(block: suspend ClashRuntimeScope.() -> Unit): ClashRuntime {
    return object : ClashRuntime {
        override fun launch() {
            launch(Dispatchers.IO) {
                globalLock.withLock {
                    Log.d("ClashRuntime: initialize")

                    try {
                        val modules = mutableListOf<Module<*>>()

                        com.github.kr328.clash.core.Clash.reset()
                        com.github.kr328.clash.core.Clash.clearOverride(
                            com.github.kr328.clash.core.Clash.OverrideSlot.Session)

                        val scope = object : ClashRuntimeScope {
                            override fun <E, T : Module<E>> install(module: T): T {
                                launch {
                                    modules.add(module)

                                    module.execute()
                                }

                                return module
                            }
                        }

                        scope.block()

                        cancel()
                    } finally {
                        withContext(NonCancellable) {
                            com.github.kr328.clash.core.Clash.reset()
                            com.github.kr328.clash.core.Clash.clearOverride(
                                com.github.kr328.clash.core.Clash.OverrideSlot.Session)

                            Log.d("ClashRuntime: destroyed")
                        }
                    }
                }
            }
        }

        override fun requestGc() {
            com.github.kr328.clash.core.Clash.forceGc()
        }
    }
}