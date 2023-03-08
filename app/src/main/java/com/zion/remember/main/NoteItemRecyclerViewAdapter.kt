package com.zion.remember.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zion.remember.databinding.ItemNoteBinding
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.NoteInformationVo
import com.zion.remember.util.MobileUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteItemRecyclerViewAdapter(private val values: MutableList<NoteInformationVo>) :
    RecyclerView.Adapter<NoteItemRecyclerViewAdapter.ViewHolder>() {

    fun addHeadData(noteVos: List<NoteInformationVo>) {
        values.addAll(0, noteVos)
        notifyItemRangeInserted(0, noteVos.size)
    }

    fun addData(noteVos: List<NoteInformationVo>) {
        var befSize = values.size
        values.addAll(noteVos)
        notifyItemInserted(befSize)
    }

    inner class ViewHolder(binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        val dateTv = binding.noteItemDate
        val contentEt = binding.contentEdit
        val contentTv = binding.contentTv
        val saveTv = binding.saveNote
        val editCv = binding.contentEditCv
        val deleteTv = binding.deleteTv
        val leftDel = binding.leftDel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dateTv.text = values[position].noteDate
        holder.contentEt.setText(values[position].content)
        holder.contentTv.text = values[position].content
        if (values[position].edit) {
            holder.editCv.visibility = View.VISIBLE
            holder.contentTv.visibility = View.GONE
            holder.saveTv.visibility = View.VISIBLE
        } else {
            holder.editCv.visibility = View.GONE
            holder.contentTv.visibility = View.VISIBLE
            holder.saveTv.visibility = View.GONE
        }
        holder.saveTv.setOnClickListener {
            if (holder.contentEt.text.isNullOrBlank()) {
                Toast.makeText(holder.itemView.context, "并没有输入任何内容", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            MobileUtil.hideSoftInput(it)
            var noteInfo =
                NoteInformationVo(holder.dateTv.text.toString(), holder.contentEt.text.toString())
            CoroutineScope(Dispatchers.Main).launch {

                var job = CoroutineScope(Dispatchers.Default).async {
                    AppDatabase.getInstance(holder.itemView.context).noteDao().insertNote(noteInfo)
                    true
                }
                if (job.await()) {
                    values[position].content = noteInfo.content
                    values[position].edit = false
                    notifyItemChanged(position)
                }
            }
        }
        holder.leftDel.mStatusChangeLister = {
            values[position].edit = true
            notifyItemChanged(position)
        }

//        holder.leftDel.setOnLongClickListener {
//            values[position].edit = true
//            notifyItemChanged(position)
//            true
//        }

        holder.deleteTv.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                var noteInfo =
                    NoteInformationVo(holder.dateTv.text.toString(), holder.contentTv.text.toString())
                var job = CoroutineScope(Dispatchers.Default).async {

                    AppDatabase.getInstance(holder.itemView.context).noteDao().deleteNote(noteInfo)
                    true
                }
                if (job.await()) {
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }
}