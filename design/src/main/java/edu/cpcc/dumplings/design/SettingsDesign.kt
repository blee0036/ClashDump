package edu.cpcc.dumplings.design

import android.content.Context
import android.view.View
import edu.cpcc.dumplings.design.databinding.DesignSettingsBinding
import edu.cpcc.dumplings.design.util.applyFrom
import edu.cpcc.dumplings.design.util.bindAppBarElevation
import edu.cpcc.dumplings.design.util.layoutInflater
import edu.cpcc.dumplings.design.util.root

class SettingsDesign(context: Context) : Design<SettingsDesign.Request>(context) {
    enum class Request {
        StartApp, StartNetwork, StartOverride,
    }

    private val binding = DesignSettingsBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)
    }

    fun request(request: Request) {
        requests.trySend(request)
    }
}