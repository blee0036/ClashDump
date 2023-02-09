package edu.cpcc.dumplings.design

import android.content.Context
import android.view.View
import edu.cpcc.dumplings.design.databinding.DesignSettingsCommonBinding
import edu.cpcc.dumplings.design.preference.category
import edu.cpcc.dumplings.design.preference.clickable
import edu.cpcc.dumplings.design.preference.preferenceScreen
import edu.cpcc.dumplings.design.preference.tips
import edu.cpcc.dumplings.design.util.applyFrom
import edu.cpcc.dumplings.design.util.bindAppBarElevation
import edu.cpcc.dumplings.design.util.layoutInflater
import edu.cpcc.dumplings.design.util.root

class ApkBrokenDesign(context: Context) : Design<ApkBrokenDesign.Request>(context) {
    data class Request(val url: String)

    private val binding = DesignSettingsCommonBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.surface = surface

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)

        val screen = preferenceScreen(context) {
            tips(R.string.application_broken_tips)

            category(R.string.reinstall)

            clickable(
                title = R.string.google_play,
                summary = R.string.google_play_url
            ) {
                clicked {
                    requests.trySend(Request(context.getString(R.string.google_play_url)))
                }
            }

            clickable(
                title = R.string.github_releases,
                summary = R.string.github_releases_url
            ) {
                clicked {
                    requests.trySend(Request(context.getString(R.string.github_releases_url)))
                }
            }
        }

        binding.content.addView(screen.root)
    }
}