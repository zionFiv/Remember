package com.zion.remember.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    fun setText(id: Int, text: String?) {
        val view = getView<TextView>(id)
        view.text = text
    }

    fun <T : View?> getView(viewId: Int): T {
        return itemView.findViewById(viewId)
    }

}