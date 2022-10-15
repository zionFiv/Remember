package com.zion.remember.main

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zion.remember.R
import com.zion.remember.databinding.FragmentGameBinding
import com.zion.remember.main.placeholder.GameholderContent



/**
 *
 *
 */
class GameItemRecyclerViewAdapter(
    private val values: List<GameholderContent.GameHolderItem>
) : RecyclerView.Adapter<GameItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentGameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.contentView.text = item.content
        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, item.instance))
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentGameBinding) : RecyclerView.ViewHolder(binding.root) {

        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}