package com.zion.remember.word

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.zion.remember.R
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.WordsVo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordsAddDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Remember_dialog)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_words_add, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatButton>(R.id.word_add_ensure).setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val titleStr = view.findViewById<AppCompatEditText>(R.id.word_add_title).text
                val explainStr = view.findViewById<AppCompatEditText>(R.id.word_add_explain).text
                if(titleStr.isNullOrBlank() || explainStr.isNullOrBlank()) {
                    Toast.makeText(activity, "单词或者释义未填写", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val vo = WordsVo(titleStr.toString(), explainStr.toString())
                //如何确认save成功
                activity?.application?.let { it1 ->
                    AppDatabase.getInstance(it1).wordsDao().saveWord(vo)
                    dismiss()
                }

            }
        }
    }

}