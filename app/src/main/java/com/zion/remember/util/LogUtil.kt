package com.zion.remember.util

import android.content.Context
import android.util.Log
import com.dianping.logan.Logan
import com.dianping.logan.LoganConfig
import com.dianping.logan.LoganParse
import com.dianping.logan.SendLogRunnable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object LogUtil {
    private const val TAG = "log_util"
    fun init(context: Context) {
        Logan.init(
            LoganConfig.Builder()
                .setCachePath(context.filesDir.absolutePath)
                .setPath(context.getExternalFilesDir(null)?.absolutePath + File.separator + "logan")
                .setEncryptIV16("X83yWMD9iKhLxfwX".toByteArray())
                .setEncryptKey16("S9u978T13NLCGc5W".toByteArray())
                .build())
    }

    fun d(logStr : String){
        Log.d(TAG, logStr)
        Logan.w(logStr, 0x12)
    }


    fun v(logStr: String){
        Log.v(TAG, logStr)
        Logan.w(logStr, 0x12)
    }

    fun i(logStr: String){
        Log.i(TAG, logStr)
        Logan.w(logStr, 0x12)
    }

    fun w(logStr: String){
        Log.w(TAG, logStr)
        Logan.w(logStr, 0x12)
    }

    fun e(logStr: String){
        Log.e(TAG, logStr)
        Logan.w(logStr, 0x12)
    }




    fun parse(context: Context, dates: Array<String>) {
        Logan.s(dates, object : SendLogRunnable() {
            override fun sendLog(logFile: File?) {
                Log.d("JdxLogUtil", "logFile : ${logFile?.path}")

                val file = File(logFile?.parentFile?.path + File.separator + "123.txt")

                val pl = LoganParse("S9u978T13NLCGc5W".toByteArray(),"X83yWMD9iKhLxfwX".toByteArray() )
                pl.parse(FileInputStream(logFile), FileOutputStream(file))
//                var renameFilePath = if(logFile?.name?.endsWith(".copy") == true) {
//                    logFile.name.replace(".copy", "")
//                } else {
//                    logFile?.name?: ""
//                }
//                renameFilePath = SimpleDateFormat("yyyy-MM-dd").format(Date(renameFilePath.toLong()))
//                val renameFile = File(logFile?.parent, renameFilePath)
//
//                if (logFile?.renameTo(renameFile) == true) {
//                    val file = File(getExternalFilesDir(null)?.absolutePath + File.separator + "logan" + "${renameFile.name}.txt")
//
//                    val pl = LoganParse("X83BLD9ikhLxfwx".toByteArray(),"S9u978TL3NGDSLS".toByteArray() )
//                    pl.parse(FileInputStream(logFile), FileOutputStream(file))
//                } else {
//
//                }

            }
        })
    }
}