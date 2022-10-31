package com.zion.remember.book

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.zion.remember.R
import com.zion.remember.adapter.RVHolder
import com.zion.remember.adapter.RvAdapter
import com.zion.remember.util.MobileUtil
import com.zion.remember.vo.ChapterVo


/*
  dialogfragment statusbar 隐藏问题
  调用了windowInset hide也不会隐藏
  点击outside不会调用dismiss，重新打开在点击outside同时也会隐藏statusbar f*ck
 */
class BookChapterDialog : DialogFragment(R.layout.fragment_item_list) {
    private var mChapterList = mutableListOf<ChapterVo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_Remember_dialog)
    }

    override fun onStart() {
        super.onStart()
        val width = MobileUtil.getScreenWidth(requireActivity()) * 3 / 4
        dialog?.window?.apply {
            setLayout(width, ViewGroup.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.START)
            WindowCompat.setDecorFitsSystemWindows(this, false)
            ViewCompat.getWindowInsetsController(decorView)?.apply {
                hide(WindowInsetsCompat.Type.systemBars())
                isAppearanceLightNavigationBars = false
                isAppearanceLightStatusBars = false
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            }
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//这个是控制没有padding的，f*ck

        }

    }

    override fun dismiss() {
        super.dismiss()
        mListener.dismiss()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.item_list)

        recyclerView.adapter = object : RvAdapter<ChapterVo>(requireContext(), mChapterList, R.layout.item_list_content){
            override fun convert(holder: RVHolder, data: ChapterVo, position: Int) {
                holder.getView<TextView>(R.id.id_text).text = data.chapterTitle
                holder.itemView.setOnClickListener {
                    mListener.itemClick(position)
                    dismiss()
                }
            }

        }
    }

    fun setChapterList(chapterList: ArrayList<ChapterVo>) {
        mChapterList.clear()
        mChapterList.addAll(chapterList)
    }
    private lateinit var mListener:DialogListener
    fun setDialListener(listener : DialogListener){
        mListener = listener
    }

    interface DialogListener{
        fun dismiss()
        fun itemClick(position:Int)
    }

}