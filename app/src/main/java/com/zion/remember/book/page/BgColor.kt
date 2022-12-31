package com.zion.remember.book.page

import com.zion.remember.R
import com.zion.remember.util.MobileUtil

/*
    PageView的基础设置
 */

enum class BgColor(val color: Int) {
    Bg1(R.color.color_CCEBCC),
    Bg2(R.color.color_CEC29C),
}

enum class AnimMode() {
    Simulate,
    Slide,
    Cover
}

enum class HorizontalMargin(val space : Float) {
    Small(MobileUtil.dp2px(15f)),
    Middle(MobileUtil.dp2px(20f)),
    Big(MobileUtil.dp2px(30f))
}

enum class LineMargin(val space : Float) {
    Small(MobileUtil.dp2px(3f)),
    Middle(MobileUtil.dp2px(4f)),
    Big(MobileUtil.dp2px(6f))
}

class Edge() {
    companion object {
        // //上边距
        val topMargin: Float = MobileUtil.dp2px(45f)
        //下边距
        val bottomMargin: Float = MobileUtil.dp2px(50f)

        //文字大小
        const val FONT_SIZE = "reader_font_size"
        const val HORIZONTAL_MARGIN = "reader_horizontal_margin"
        const val LINE_MARGIN = "reader_line_margin"

        //段间距
        const val PARAGRAPH_EDGE = "reader_paragraph_edge"
    }
}

