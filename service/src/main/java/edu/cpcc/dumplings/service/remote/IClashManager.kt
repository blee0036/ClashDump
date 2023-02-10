package edu.cpcc.dumplings.service.remote

import edu.cpcc.dumplings.core.model.*
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface IClashManager {
    fun queryTunnelState(): TunnelState
    fun queryTrafficTotal(): Long
    fun queryProxyGroupNames(excludeNotSelectable: Boolean): List<String>
    fun queryProxyGroup(name: String, proxySort: ProxySort): ProxyGroup
    fun queryConfiguration(): UiConfiguration
    fun queryProviders(): ProviderList

    fun patchSelector(group: String, name: String): Boolean

    suspend fun healthCheck(group: String)
    suspend fun updateProvider(type: Provider.Type, name: String)

    fun queryOverride(slot: _root_ide_package_.com.github.kr328.clash.core.Clash.OverrideSlot): ConfigurationOverride
    fun patchOverride(slot: _root_ide_package_.com.github.kr328.clash.core.Clash.OverrideSlot, configuration: ConfigurationOverride)
    fun clearOverride(slot: _root_ide_package_.com.github.kr328.clash.core.Clash.OverrideSlot)

    fun setLogObserver(observer: ILogObserver?)
}