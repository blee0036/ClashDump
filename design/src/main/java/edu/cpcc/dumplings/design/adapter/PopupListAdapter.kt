package edu.cpcc.dumplings.design.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.cpcc.dumplings.design.R
import edu.cpcc.dumplings.design.util.layoutInflater
import edu.cpcc.dumplings.design.util.resolveThemedColor

class PopupListAdapter(
    private val context: Context,
    private val texts: List<CharSequence>,
    private val selected: Int,
) : BaseAdapter() {
    private val colorPrimary = context.resolveThemedColor(R.attr.colorPrimary)
    private val colorOnPrimary = context.resolveThemedColor(R.attr.colorOnPrimary)
    private val colorControlNormal = context.resolveThemedColor(R.attr.colorControlNormal)

    override fun getCount(): Int {
        return texts.size
    }

    override fun getItem(position: Int): Any {
        return texts[position]
    }

    override fun getItemId(position: Int): Long {
        return texts[position].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: context.layoutInflater
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        val text: TextView = view.findViewById(android.R.id.text1)

        text.text = texts[position]

        if (position == selected) {
            text.setBackgroundColor(
                Color.argb(
                    200,
                    Color.red(colorPrimary),
                    Color.green(colorPrimary),
                    Color.blue(colorPrimary)
                )
            )
            text.setTextColor(colorOnPrimary)
        } else {
            text.setBackgroundColor(Color.TRANSPARENT)
            text.setTextColor(colorControlNormal)
        }

        return view
    }
}