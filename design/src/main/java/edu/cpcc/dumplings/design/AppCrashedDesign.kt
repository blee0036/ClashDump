package edu.cpcc.dumplings.design

import android.content.Context
import android.view.View
import edu.cpcc.dumplings.design.databinding.DesignAppCrashedBinding
import edu.cpcc.dumplings.design.util.applyFrom
import edu.cpcc.dumplings.design.util.bindAppBarElevation
import edu.cpcc.dumplings.design.util.layoutInflater
import edu.cpcc.dumplings.design.util.root

class AppCrashedDesign(context: Context) : Design<Unit>(context) {
    private val binding = DesignAppCrashedBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    fun setAppLogs(logs: String) {
        binding.logsView.text = logs
    }

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)
    }
}