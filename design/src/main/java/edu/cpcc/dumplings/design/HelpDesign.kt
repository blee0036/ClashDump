package edu.cpcc.dumplings.design

import android.content.Context
import android.net.Uri
import android.view.View
import edu.cpcc.dumplings.common.compat.preferredLocale
import edu.cpcc.dumplings.design.databinding.DesignSettingsCommonBinding
import edu.cpcc.dumplings.design.preference.category
import edu.cpcc.dumplings.design.preference.clickable
import edu.cpcc.dumplings.design.preference.preferenceScreen
import edu.cpcc.dumplings.design.preference.tips
import edu.cpcc.dumplings.design.util.applyFrom
import edu.cpcc.dumplings.design.util.bindAppBarElevation
import edu.cpcc.dumplings.design.util.layoutInflater
import edu.cpcc.dumplings.design.util.root

class HelpDesign(
    context: Context,
    openLink: (Uri) -> Unit,
) : Design<Unit>(context) {
    private val binding = DesignSettingsCommonBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.surface = surface

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)

        val screen = preferenceScreen(context) {
            tips(R.string.tips_help)

            category(R.string.document)

            clickable(
                title = R.string.clash_wiki,
                summary = R.string.clash_wiki_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.clash_wiki_url)))
                }
            }

            category(R.string.feedback)

            if (BuildConfig.PREMIUM) {
                clickable(
                    title = R.string.google_play,
                    summary = R.string.google_play_url
                ) {
                    clicked {
                        openLink(Uri.parse(context.getString(R.string.google_play_url)))
                    }
                }
            }

            clickable(
                title = R.string.github_issues,
                summary = R.string.github_issues_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.github_issues_url)))
                }
            }

            if (!BuildConfig.PREMIUM) {
                category(R.string.sources)

                clickable(
                    title = R.string.clash_for_android,
                    summary = R.string.github_url
                ) {
                    clicked {
                        openLink(Uri.parse(context.getString(R.string.github_url)))
                    }
                }

                clickable(
                    title = R.string.clash_core,
                    summary = R.string.clash_core_url
                ) {
                    clicked {
                        openLink(Uri.parse(context.getString(R.string.clash_core_url)))
                    }
                }
            }

            if (context.resources.configuration.preferredLocale.language == "zh") {
                category(R.string.donate)

                clickable(
                    title = R.string.donate,
                    summary = R.string.donate_url
                ) {
                    clicked {
                        openLink(Uri.parse(context.getString(R.string.donate_url)))
                    }
                }
            }
        }

        binding.content.addView(screen.root)
    }
}