package com.zion.remember.book

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.ViewModelProvider
import com.zion.remember.R
import com.zion.remember.book.page.*
import com.zion.remember.databinding.ActivityBookReadingBinding
import com.zion.remember.util.LogUtil
import com.zion.remember.util.MobileUtil
import com.zion.remember.util.SpUtil

class BookReadingActivity : AppCompatActivity() {
    private val TAG = "BOOK_READING"
    private var binding: ActivityBookReadingBinding? = null
    private lateinit var viewModel: BookReadingViewModel
    private var fontSize = MobileUtil.sp2px(16f)
    private var screenWidth = 0
    private var screenHeight = 0
    private var lineMargin: LineMargin = LineMargin.Small
    private var horizontalMargin = HorizontalMargin.Big

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookReadingBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val path = intent.getStringExtra("BOOK_FILE")

        viewModel = ViewModelProvider(this).get(BookReadingViewModel::class.java)
        viewModel.parseBook(path)
        viewModel.data.observe(this) {
            LogUtil.i(
                "PageView wrong page -- setBitmap ${it[0].position} /n ${it[1].position} /n ${it[2].position}"
            )

            binding?.pageView?.apply {
                setBitmap(it)
            }
        }

        fontSize =
            if (SpUtil.getFloat(Edge.FONT_SIZE) == 0f) fontSize else SpUtil.getFloat(Edge.FONT_SIZE)
        horizontalMargin = when (SpUtil.getFloat(Edge.HORIZONTAL_MARGIN)) {
            MobileUtil.dp2px(HorizontalMargin.Small.space) -> HorizontalMargin.Small
            MobileUtil.dp2px(HorizontalMargin.Middle.space) -> HorizontalMargin.Middle
            MobileUtil.dp2px(HorizontalMargin.Big.space) -> HorizontalMargin.Big
            else -> HorizontalMargin.Big
        }
        lineMargin = when (SpUtil.getFloat(Edge.LINE_MARGIN)) {
            MobileUtil.dp2px(LineMargin.Small.space) -> LineMargin.Small
            MobileUtil.dp2px(LineMargin.Middle.space) -> LineMargin.Middle
            MobileUtil.dp2px(LineMargin.Big.space) -> LineMargin.Big
            else -> LineMargin.Small
        }
        screenWidth = MobileUtil.getScreenWidth(this)
        screenHeight = MobileUtil.getScreenHeight(this)
        fontChange(fontSize)
        viewLogic()
    }

    private fun viewLogic() {

        binding?.pageView?.apply {
            setPageMode(AnimMode.Simulate)
            setTouchListener(object : PageView.TouchListener {
                override fun onTouch(): Boolean {
                    binding?.bookToolLayout?.let {
                        ObjectAnimator.ofFloat(it, "translationY", 0f, 300f)
                            .setDuration(500).start()
                        it.postDelayed({
                            it.isVisible = false
                        }, 500)
                        statusBarShow(false)
                        binding?.bookToolSetting?.isVisible = false
                    }
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
                            statusBarShow(false)
                            binding?.bookToolSetting?.isVisible = false
                        } else {
                            it.isVisible = true
                            ObjectAnimator.ofFloat(it, "translationY", 300f, 0f)
                                .setDuration(500).start()
                            statusBarShow(true)
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

                override fun longTouch(param: String, positionStart: Float, positionEnd: Float) {
                    var yPosition = 0.0f
                    yPosition =  if (positionStart < positionEnd) positionStart else positionEnd;
//                    showPop(yPosition.toInt())
                }

            })
        }


        binding?.fontDec?.setOnClickListener {
            fontSize -= MobileUtil.sp2px(1f)
            fontChange(fontSize)
        }

        binding?.fontInc?.setOnClickListener {
            fontSize += MobileUtil.sp2px(1f)
            fontChange(fontSize)
        }

        binding?.bgGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
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
                setDialListener(object : BookChapterDialog.DialogListener {
                    override fun dismiss() {
                        statusBarShow(false)
                    }

                    override fun itemClick(position: Int) {
                        viewModel.openChapter(position, 0)
                    }

                })

            }.show(supportFragmentManager, "book_chapter")
        }

        binding?.animModeGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.anim_mode_simulate ->
                    binding?.pageView?.setPageMode(AnimMode.Simulate)
                R.id.anim_mode_slide ->
                    binding?.pageView?.setPageMode(AnimMode.Slide)
                R.id.anim_mode_cover ->
                    binding?.pageView?.setPageMode(AnimMode.Cover)
            }
        }
    }


    private fun fontChange(f_Size: Float) {//font改变后，需要重新设置章节list
        binding?.fontSize?.text = f_Size.toInt().toString()
        binding?.pageView?.run {
            setFontSize(f_Size)
            changeHorizontalEdge(horizontalMargin)
            changeLineEdge(lineMargin)
        }
        viewModel.refreshChapter(
            f_Size,
            lineMargin,
            screenWidth - horizontalMargin.space.toInt() * 2,
            screenHeight - Edge.topMargin.toInt() - Edge.bottomMargin.toInt()
        )
    }

    override fun onStop() {
        super.onStop()
        SpUtil.putFloat(Edge.FONT_SIZE, fontSize)
        SpUtil.putFloat(Edge.HORIZONTAL_MARGIN, horizontalMargin.space)
        SpUtil.putFloat(Edge.LINE_MARGIN, lineMargin.space)
    }


    override fun onResume() {
        super.onResume()
        statusBarShow(false)
    }

    fun statusBarShow(show: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.getWindowInsetsController(binding!!.root)?.apply {
            if (show) {
                show(WindowInsetsCompat.Type.systemBars())
            } else {
                hide(WindowInsetsCompat.Type.systemBars())
            }
            isAppearanceLightNavigationBars = show
            isAppearanceLightStatusBars = show
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }

    private fun showPop(yPosition : Int){
        val textView = TextView(this)
        textView.text = "标记"
        val pop =  PopupWindow(textView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        pop.showAtLocation(binding?.root, Gravity.TOP or Gravity.CENTER, 0 , yPosition )
    }
}