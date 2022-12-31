package com.zion.remember.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object UriParse {

    fun getFileName(uri: Uri, context: Context) : String {

        var fileName: String? = null
        val filePathColumn =
            arrayOf( MediaStore.MediaColumns.DISPLAY_NAME)
        context.contentResolver.query(uri, filePathColumn, null, null, null)?.apply {
            moveToFirst()

            fileName = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            close()
        }
        var finalFileName = ""
        fileName?.toCharArray()?.asList()?.filter { char -> !char.isWhitespace() }?.map {
            finalFileName = finalFileName.plus(it)
        }
        if (finalFileName.contains(".")) {
           val pos =  finalFileName.lastIndexOf(".")
            finalFileName = finalFileName.substring(0, pos)
        }
        return finalFileName
    }

    fun parseFile(uri: Uri, context: Context): String? {
        return when (uri.scheme) {
            "content" ->
                getFileFromContentUri(uri, context)
            "file" ->
                uri.path
            else ->
                ""
        }
    }

    private fun getFileFromContentUri(uri: Uri, context: Context): String? {
        var filePath: String? = null
        var fileName: String? = null
        val filePathColumn =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        context.contentResolver.query(uri, filePathColumn, null, null, null)?.apply {
            moveToFirst()
            filePath = getString(getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
            fileName = getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            close()
        }
        var finalFileName = ""
        fileName?.toCharArray()?.asList()?.filter { char -> !char.isWhitespace() }?.map {
            finalFileName = finalFileName.plus(it)
        }

        if (!filePath.isNullOrBlank()) {
            File(filePath).let {
                if (!it.exists() || it.length() <= 0 || filePath.isNullOrBlank()) {
                    filePath = getPathFromInputStreamUri(uri, finalFileName ?: it.name, context)
                }
            }
        } else {
            filePath = getPathFromInputStreamUri(uri, finalFileName, context)
        }


        return filePath


    }

    private fun getPathFromInputStreamUri(uri: Uri, fileName: String, context: Context): String {
        return uri.authority?.let {
            val stream = context.contentResolver.openInputStream(uri)
            val file = createTempFileFrom(stream, fileName, context)
            stream?.close()
            file?.path
        } ?: ""
    }

    private fun createTempFileFrom(
        stream: InputStream?,
        fileName: String,
        context: Context
    ): File? {
        var tempFile: File? = null
        stream?.let {
            val buffer = ByteArray(8 * 1024)
            tempFile = File(context.filesDir, fileName)
            if (tempFile?.exists() == true) {
                tempFile?.delete()
            }
            val outputStream = FileOutputStream(tempFile)
            var read: Int
            while (stream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            outputStream.close()
        }
        return tempFile
    }
}