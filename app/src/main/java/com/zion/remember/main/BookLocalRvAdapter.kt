package com.zion.remember.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion.remember.book.BookReadingActivity
import com.zion.remember.databinding.ItemBookBinding
import com.zion.remember.book.db.BookVo

class BookLocalRvAdapter() : RecyclerView.Adapter<BookLocalRvAdapter.ViewHolder>() {
    private var datas: MutableList<BookVo> = mutableListOf()

    inner class ViewHolder(binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleTv = binding.bookTitle
        val imgIv = binding.bookImg
    }

    fun refresh(newDatas: MutableList<BookVo>) {
        datas.clear()
        datas.addAll(newDatas)
        notifyDataSetChanged()
    }

    fun add(book: BookVo) {
        datas.add(book)
        notifyItemInserted(datas.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTv.text = datas[position].title
        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(
                Intent(
                    holder.itemView.context,
                    BookReadingActivity::class.java
                ).apply {
                    putExtra("BOOK_FILE",  datas[position].path)
                }
            )

        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}