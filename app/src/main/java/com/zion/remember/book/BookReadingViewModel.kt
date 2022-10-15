package com.zion.remember.book

import android.net.Uri
import android.text.TextPaint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zion.remember.util.MobileUtil
import com.zion.remember.vo.ChapterPageVo
import com.zion.remember.vo.ChapterVo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.URI
import java.nio.charset.Charset
import java.util.regex.Pattern

class BookReadingViewModel() : ViewModel() {
    private val CHAPTER_PATTERNS = arrayOf(
        "^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
        "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\u000C\t])(.{0,30})$",
        "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
        "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
        "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"
    )
    private val BUFFER_SIZE = 512 * 1024
    private var mChapterPattern: Pattern? = null
     var chapterList = ArrayList<ChapterVo>()
    private var mBookFile: File? = null


    //将File转换成BookVo 章节 段落
    fun parseBook(path: String?) {
        if (path.isNullOrBlank()) return
        chapterList.clear()
        mBookFile = File(path)
        if (mBookFile?.exists() == false) {
            return
        }
        val bookStream = RandomAccessFile(mBookFile, "r")
        val buffer = ByteArray(BUFFER_SIZE)
        var manualOffset = 0
        var length = 0
        val hasChapter = hasChapter(bookStream)

        while ((bookStream.read(buffer, 0, buffer.size).also { length = it }) > 0) {
            var content = String(buffer, 0, length, Charset.forName("GBK"))
            var seekPos = 0
            if (hasChapter) {
                val matcher = mChapterPattern?.matcher(content)
                while (matcher?.find() == true) {
                    var chapterStart = matcher.start()
                    val chapterTitle = matcher.group()
                    var chapterContent = content.substring(seekPos, chapterStart)
                    var endBufferIndex = chapterContent.toByteArray(Charset.forName("GBK")).size
                    if (seekPos == 0) {
                        if (chapterStart != 0) {//当前buffer最前面还有部分内容

                            seekPos += chapterContent.length

                            if (chapterList.size == 0) {//如果没有章节，说明在最前面，直接添加章节
                                val vo =
                                    ChapterVo(
                                        chapterTitle = chapterTitle,
                                        start = 0,
                                        end = endBufferIndex.toLong()
                                    )
                                chapterList.add(vo)
                                chapterList.add(ChapterVo(chapterTitle, endBufferIndex.toLong(), 0))
                            } else {//需要把这部分内容加到前面的章节中
                                val lastChapter = chapterList[chapterList.size - 1]
                                lastChapter.end += endBufferIndex
                                chapterList.add(ChapterVo(chapterTitle, lastChapter.end, 0))
                            }
                        }
                    } else {// seekPos != 0
                        seekPos += chapterContent.length
                        var start = 0L
                        if (chapterList.size > 0) {
                            val lastChapter = chapterList[chapterList.size - 1]
                            lastChapter.end = lastChapter.start + endBufferIndex
                            start = lastChapter.end

                        }
                        chapterList.add(ChapterVo(chapterTitle, start, 0))
                    }
                }
            } else {
                var chapterManualOffset: Int = 0
                var remainLength = length
                while (remainLength > 0) {
                    if (remainLength > 20 * 1024) {
                        var end = length
                        val BLANK: Byte = 0x0a
                        //寻找换行符作为终止点
                        for (i in (chapterManualOffset + 20 * 1024) until length) {
                            if (buffer[i] == BLANK) {
                                end = i
                                break
                            }
                        }
                        chapterList.add(
                            ChapterVo(
                                "",
                                (manualOffset + chapterManualOffset + 1).toLong(),
                                (manualOffset + end).toLong()
                            )
                        )
                        remainLength -= (end - chapterManualOffset)
                        chapterManualOffset = end
                    } else {
                        chapterList.add(
                            ChapterVo(
                                "",
                                (manualOffset + chapterManualOffset + 1).toLong(),
                                (manualOffset + remainLength).toLong()
                            )
                        )
                        remainLength = 0
                    }
                }

            }
            manualOffset += length
            if (hasChapter) {
                chapterList[chapterList.size - 1].end = manualOffset.toLong()
            }

        }

        bookStream.close()
    }

    private fun hasChapter(bookStream: RandomAccessFile): Boolean {
        val buffer = ByteArray(BUFFER_SIZE / 4)
        var length = bookStream.read(buffer, 0, buffer.size)
        var content = String(buffer, 0, length, Charset.forName("GBK"))

        for (str in CHAPTER_PATTERNS) {
            val pattern = Pattern.compile(str, Pattern.MULTILINE)
            val matcher = pattern.matcher(content)
            if (matcher.find()) {
                mChapterPattern = pattern
                bookStream.seek(0)
                return true
            }
        }

        bookStream.seek(0)
        return false
    }

    var data = MutableLiveData<List<ChapterPageVo>>()
    var currentPos = 0
    var currentPagePos = 0
    var mFontSize: Float = MobileUtil.sp2px(16f)
    private val showChapterList = mutableListOf<ChapterPageVo>()
    var mPageWidth = 0
    var mPageHeight = 0
    fun refreshChapter(fontSize: Float, pageWidth: Int, pageHeight: Int) {
        mFontSize = fontSize
        mPageWidth = pageWidth - MobileUtil.dp2px(30f).toInt()
        mPageHeight = pageHeight - MobileUtil.dp2px(74f).toInt()//上下留白各20 + 上下文字各12dp
        openChapter(currentPos, currentPagePos)
    }

     fun openChapter(
        position: Int,
        pagePos: Int,
    ) {
        if (chapterList.isEmpty()) {
            return
        }
        currentPos = position
        currentPagePos = pagePos
        CoroutineScope(Dispatchers.Default).launch {

            var posChapterPages = getChapterPages(chapterList[currentPos])

            if (currentPagePos > posChapterPages.size - 1) {
                currentPagePos = posChapterPages.size - 1
            }

            showChapterList.clear()
            if (currentPagePos == 0) {
                if (currentPos == 0) {
                    showChapterList.add(ChapterPageVo("", 0, mutableListOf(), 0))
                } else {
                    val preChapterPage = getChapterPages(chapterList[currentPos - 1])
                    showChapterList.add(preChapterPage[preChapterPage.size - 1])
                }
            } else {
                showChapterList.add(posChapterPages[currentPagePos - 1])
            }
            showChapterList.add(posChapterPages[currentPagePos])//当前页的数据

            if (currentPagePos + 1 > posChapterPages.size - 1) {
                var position = currentPos + 1
                if (position > chapterList.size - 1) {
                    showChapterList.add(ChapterPageVo("", 0, mutableListOf(), 0))
                } else {
                    showChapterList.add(
                        getChapterPages(chapterList[position])[0]
                    )
                }
            } else {
                showChapterList.add(posChapterPages[currentPagePos + 1])
            }

            data.postValue(showChapterList)
        }

    }

    fun openNextChapter() {
        if (chapterList.isEmpty() || showChapterList.isEmpty()) {
            return
        }
        currentPagePos++
        CoroutineScope(Dispatchers.Default).launch {

            var posChapterPages = getChapterPages(chapterList[currentPos])
            //计算实际的位置
            if (currentPagePos > posChapterPages.size - 1) {
                if (currentPos + 1 > chapterList.size - 1) {
                    return@launch
                } else {
                    currentPagePos = 0
                    currentPos++
                }
            }
            posChapterPages = getChapterPages(chapterList[currentPos])
            showChapterList[0] = showChapterList[1]
            showChapterList[1] = showChapterList[2]
            var nextPagePos = currentPagePos + 1
            if (nextPagePos > posChapterPages.size - 1) {
                val nextChapterPos = currentPos + 1
                if (nextChapterPos > chapterList.size - 1) {
                    showChapterList[2] = ChapterPageVo("", 0, mutableListOf(), 0)
                } else {
                    showChapterList[2] =
                        getChapterPages(chapterList[nextChapterPos])[0]
                }
            } else {
                showChapterList[2] = posChapterPages[nextPagePos]
            }
            data.postValue(showChapterList)
        }
    }

    fun openPreChapter() {
        if (chapterList.isEmpty() || showChapterList.isEmpty()) {
            return
        }
        currentPagePos--
        CoroutineScope(Dispatchers.Default).launch {

            if (currentPagePos < 0) {
                if (currentPos == 0) {
                    currentPagePos = 0
                    return@launch
                } else {
                    currentPos--
                    currentPagePos = getChapterPages(chapterList[currentPos]).size - 1
                }
            }
            var posChapterPages = getChapterPages(chapterList[currentPos])
            showChapterList[2] = showChapterList[1]
            showChapterList[1] = showChapterList[0]
            var prePagePos = currentPagePos - 1
            if (prePagePos < 0) {
                var preChapterPos = currentPos - 1
                if (preChapterPos < 0) {
                    showChapterList[0] = ChapterPageVo("", 0, mutableListOf(), 0)
                    currentPos = 0
                    currentPagePos = 0
                } else {
                    val preChapter = getChapterPages(chapterList[preChapterPos])
                    prePagePos = preChapter.size - 1
                    showChapterList[0] = preChapter[prePagePos]
                }
            } else {
                showChapterList[0] = posChapterPages[prePagePos]
            }
            data.postValue(showChapterList)
        }
    }

    private fun getChapterPages(chapter: ChapterVo): List<ChapterPageVo> {
        val chapterBR = getChapterBR(chapter)
        val pages = mutableListOf<ChapterPageVo>()
        var rHeight: Int = mPageHeight
        var tSize: Int = (mFontSize + MobileUtil.dp2px(1f)).toInt()

        val title = chapter.chapterTitle
        var paragraph = ""
        var textPaint = TextPaint()
        var lines = mutableListOf<String>()
        textPaint.textSize = mFontSize

        while (chapterBR.readLine()?.also { paragraph = it } != null) {
            if (paragraph.replace("\\s", "").also { paragraph = it }.isBlank()) {
                continue
            }
            while (paragraph.isNotEmpty()) {
                rHeight -= tSize
                if (rHeight <= 0) {
                    pages.add(
                        ChapterPageVo(title, 1, ArrayList<String>(lines), pages.size + 1)
                    )
                    lines.clear()
                    rHeight = mPageHeight
                } else {
                    var wordCount = textPaint.breakText(paragraph, true, mPageWidth.toFloat(), null)
                    var subStr = paragraph.substring(0, wordCount)
                    if (subStr.isNotBlank()) {
                        rHeight -= (tSize / 2)
                        lines.add(subStr)
                    }
                    paragraph = paragraph.substring(wordCount)
                }
            }
            rHeight -= tSize
        }
        if (lines.isNotEmpty()) {
            pages.add(
                ChapterPageVo(title, 1, lines, pages.size + 1)
            )
        }

        return pages
    }

    //获取当前章节的数据
    private fun getChapterBR(chapter: ChapterVo): BufferedReader {
//        val bookStream = RandomAccessFile(mBookFile, "r")
        val bookStream = FileInputStream(mBookFile)
//        bookStream.seek(chapter.start)
        bookStream.skip(chapter.start)
        val len = (chapter.end - chapter.start).toInt()
        val content = ByteArray(len)
        bookStream.read(content, 0, len)
        return BufferedReader(
            InputStreamReader(
                ByteArrayInputStream(content),
                Charset.forName("GBK")
            )
        )
    }

}