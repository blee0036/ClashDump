package edu.cpcc.dumplings.design

import android.content.Context
import android.view.View
import edu.cpcc.dumplings.core.model.Provider
import edu.cpcc.dumplings.design.adapter.ProviderAdapter
import edu.cpcc.dumplings.design.databinding.DesignProvidersBinding
import edu.cpcc.dumplings.design.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProvidersDesign(
    context: Context,
    providers: List<Provider>,
) : Design<ProvidersDesign.Request>(context) {
    sealed class Request {
        data class Update(val index: Int, val provider: Provider) : Request()
    }

    private val binding = DesignProvidersBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    private val adapter = ProviderAdapter(context, providers) { index, provider ->
        requests.trySend(Request.Update(index, provider))
    }

    fun updateElapsed() {
        adapter.updateElapsed()
    }

    suspend fun notifyUpdated(index: Int) {
        withContext(Dispatchers.Main) {
            adapter.notifyUpdated(index)
        }
    }

    suspend fun notifyChanged(index: Int) {
        withContext(Dispatchers.Main) {
            adapter.notifyChanged(index)
        }
    }

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.mainList.recyclerList.bindAppBarElevation(binding.activityBarLayout)
        binding.mainList.recyclerList.applyLinearAdapter(context, adapter)
    }

    fun requestUpdateAll() {
        adapter.states.filter { !it.updating }.forEachIndexed { index, state ->
            state.updating = true

            requests.trySend(Request.Update(index, state.provider))
        }
    }
}