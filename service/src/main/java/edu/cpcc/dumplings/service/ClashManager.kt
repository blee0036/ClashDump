package edu.cpcc.dumplings.service

import android.content.Context
import edu.cpcc.dumplings.common.log.Log
import com.github.kr328.clash.core.model.*
import edu.cpcc.dumplings.service.data.Selection
import edu.cpcc.dumplings.service.data.SelectionDao
import edu.cpcc.dumplings.service.remote.IClashManager
import edu.cpcc.dumplings.service.remote.ILogObserver
import edu.cpcc.dumplings.service.store.ServiceStore
import edu.cpcc.dumplings.service.util.sendOverrideChanged
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel

class ClashManager(private val context: Context) : IClashManager,
    CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val store = ServiceStore(context)
    private var logReceiver: ReceiveChannel<LogMessage>? = null

    override fun queryTunnelState(): TunnelState {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.queryTunnelState()
    }

    override fun queryTrafficTotal(): Long {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.queryTrafficTotal()
    }

    override fun queryProxyGroupNames(excludeNotSelectable: Boolean): List<String> {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.queryGroupNames(excludeNotSelectable)
    }

    override fun queryProxyGroup(name: String, proxySort: ProxySort): ProxyGroup {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.queryGroup(name, proxySort)
    }

    override fun queryConfiguration(): UiConfiguration {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.queryConfiguration()
    }

    override fun queryProviders(): ProviderList {
        return ProviderList(_root_ide_package_.com.github.kr328.clash.core.Clash.queryProviders())
    }

    override fun queryOverride(slot: _root_ide_package_.com.github.kr328.clash.core.Clash.OverrideSlot): ConfigurationOverride {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.queryOverride(slot)
    }

    override fun patchSelector(group: String, name: String): Boolean {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.patchSelector(group, name).also {
            val current = store.activeProfile ?: return@also

            if (it) {
                SelectionDao().setSelected(Selection(current, group, name))
            } else {
                SelectionDao().removeSelected(current, group)
            }
        }
    }

    override fun patchOverride(slot: _root_ide_package_.com.github.kr328.clash.core.Clash.OverrideSlot, configuration: ConfigurationOverride) {
        _root_ide_package_.com.github.kr328.clash.core.Clash.patchOverride(slot, configuration)

        context.sendOverrideChanged()
    }

    override fun clearOverride(slot: _root_ide_package_.com.github.kr328.clash.core.Clash.OverrideSlot) {
        _root_ide_package_.com.github.kr328.clash.core.Clash.clearOverride(slot)
    }

    override suspend fun healthCheck(group: String) {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.healthCheck(group).await()
    }

    override suspend fun updateProvider(type: Provider.Type, name: String) {
        return _root_ide_package_.com.github.kr328.clash.core.Clash.updateProvider(type, name).await()
    }

    override fun setLogObserver(observer: ILogObserver?) {
        synchronized(this) {
            logReceiver?.apply {
                cancel()

                _root_ide_package_.com.github.kr328.clash.core.Clash.forceGc()
            }

            if (observer != null) {
                logReceiver = _root_ide_package_.com.github.kr328.clash.core.Clash.subscribeLogcat().also { c ->
                    launch {
                        try {
                            while (isActive) {
                                observer.newItem(c.receive())
                            }
                        } catch (e: CancellationException) {
                            // intended behavior
                            // ignore
                        } catch (e: Exception) {
                            Log.w("UI crashed", e)
                        } finally {
                            withContext(NonCancellable) {
                                c.cancel()

                                _root_ide_package_.com.github.kr328.clash.core.Clash.forceGc()
                            }
                        }
                    }
                }
            }
        }
    }
}