package com.zion.remember.book

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.ViewModelProvider
import com.zion.remember.R
import com.zion.remember.databinding.ActivityBookReadingBinding
import com.zion.remember.util.MobileUtil
import com.zion.remember.util.SpUtil
import com.zion.remember.book.page.BgColor
import com.zion.remember.book.page.PageView

class BookReadingActivity : AppCompatActivity() {
    private val TAG = "BOOK_READING"
    private var binding: ActivityBookReadingBinding? = null
    private lateinit var viewModel: BookReadingViewModel
    private var fontSize = MobileUtil.sp2px(16f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookReadingBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val path = intent.getStringExtra("BOOK_FILE")

        fontSize =
            if (SpUtil.getFloat("reader_font_size") == 0f) fontSize else SpUtil.getFloat("reader_font_size")
        binding?.pageView?.apply {
            setPageMode()
            setTouchListener(object : PageView.TouchListener {
                override fun onTouch(): Boolean {
                    return true
                }

                override fun center() {

                    binding?.bookToolLayout?.let {
                        if (it.isVisible) {
                            ObjectAnimator.ofFloat(it, "translationY", 0f, 300f)
                                .setDuration(500).start()
                            it.postDelayed({
                                it.isVisible = false
                            }, 500)
                            statusbarShow(false)
                            binding?.bookToolSetting?.isVisible = false
                        } else {
                            it.isVisible = true
                            ObjectAnimator.ofFloat(it, "translationY", 300f, 0f)
                                .setDuration(500).start()
                            statusbarShow(true)
                        }
                    }
                }

                override fun prePage() {
                    viewModel.openPreChapter()
                }

                override fun nextPage() {
                    viewModel.openNextChapter()
                }

                override fun cancel() {
                }

            })
        }



        viewModel = ViewModelProvider(this).get(BookReadingViewModel::class.java)
        viewModel.parseBook(path)
        viewModel.data.observe(this) {
            Log.i("PageView", "wrong page -- setBitmap ${it[0].position} /n ${it[1].position} /n ${it[2].position}")

            binding?.pageView?.apply {
                setBitmap(it)
            }
        }
        fontChange(fontSize)

        binding?.fontDec?.setOnClickListener {
            fontSize -= MobileUtil.sp2px(1f)
            fontChange(fontSize)
        }

        binding?.fontInc?.setOnClickListener {
            fontSize += MobileUtil.sp2px(1f)
            fontChange(fontSize)
        }

        binding?.bgGroup?.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.bg_ccebcc ->
                    binding?.pageView?.changeBgColor(BgColor.Bg1)
                R.id.bg_cec29c ->
                    binding?.pageView?.changeBgColor(BgColor.Bg2)

            }
            fontChange(fontSize)
        }

        binding?.toolSettingBtn?.setOnClickListener {
            binding?.bookToolSetting?.let {
                if (it.isVisible) {
                    ObjectAnimator.ofFloat(it, "translationY", 0f, 300f)
                        .setDuration(500).start()
                    it.postDelayed({
                        it.isVisible = false
                    }, 500)
                } else {
                    it.isVisible = true
                    ObjectAnimator.ofFloat(it, "translationY", 300f, 0f)
                        .setDuration(500).start()
                }
            }
        }

        binding?.toolChapterBtn?.setOnClickListener {
            BookChapterDialog().apply {
               setChapterList(viewModel.chapterList)
                setDialListener(object : BookChapterDialog.DialogListener{
                    override fun dismiss() {
                        statusbarShow(false)
                    }

                    override fun itemClick(position: Int) {
                        viewModel.openChapter(position, 0)
                    }

                })

            }.show(supportFragmentManager, "book_chapter")
        }

    }

    private fun fontChange(f_Size: Float) {//font改变后，需要重新设置章节list
        binding?.fontSize?.text = f_Size.toInt().toString()
        binding?.pageView?.setFontSize(f_Size)
        viewModel.refreshChapter(f_Size, MobileUtil.getScreenWidth(this), MobileUtil.getScreenHeight(this))
    }

    override fun onStop() {
        super.onStop()
        SpUtil.putFloat("reader_font_size", fontSize)
    }


    override fun onResume() {
        super.onResume()
        statusbarShow(false)
    }

    fun statusbarShow(show : Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.getWindowInsetsController(binding!!.root)?.apply {
            if(show){
                show(WindowInsetsCompat.Type.systemBars())
            } else {
                hide(WindowInsetsCompat.Type.systemBars())
            }
            isAppearanceLightNavigationBars = show
            isAppearanceLightStatusBars = show
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }
}