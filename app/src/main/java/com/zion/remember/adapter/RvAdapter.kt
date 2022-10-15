package com.zion.remember.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class RvAdapter<T>(val context : Context ,val datas : MutableList<T>, val resourceId : Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RVHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        convert(holder as RVHolder, datas[position], position )
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutResId(datas[position])
    }

    fun getLayoutResId(item : T) : Int {
        return resourceId
    }

    fun replaceAll(items : MutableList<T>) {
        datas.clear()
        datas.addAll(items)
        notifyDataSetChanged()
    }

    abstract fun convert(holder : RVHolder, data : T, position: Int)

}