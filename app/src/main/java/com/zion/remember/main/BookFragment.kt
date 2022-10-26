package com.zion.remember.main

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.RecyclerView
import com.zion.remember.R
import com.zion.remember.book.db.BookDatabase
import com.zion.remember.databinding.FragmentBookBinding
import com.zion.remember.util.UriParse
import com.zion.remember.book.db.BookVo
import com.zion.remember.db.AppDatabase
import com.zion.remember.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.stream.Collectors


/**
 *  权限请求
 *      registerForResult
 *  获取手机内的小说
 *      Environment.getExternalStorageDirectory()无效
 *
 *      解析file文件 通过registerForResult获得的uri path不是实际的path，需要转换
 *      章节怎么确认
 *      标题怎么确认
 *      每一页的end怎么确认
 *
 *      uri怎么直接转File

 */
class BookFragment : Fragment() {

    private var _binding: FragmentBookBinding? = null
    private lateinit var books : MutableList<BookVo>
    private val txtLaunch = registerForActivityResult(ActivityResultContracts.GetContent()) {
        //
        it?.let {
          val vo =   BookVo(
                img = "",
                title = UriParse.getFileName(it, requireContext()),
                path = UriParse.parseFile(it, requireContext()) ?: ""
            )
          val paths =  books.stream().map(BookVo::path).collect(Collectors.toList())
            if (!paths.contains(vo.path)) {
                CoroutineScope(Dispatchers.Default).launch {
                    BookDatabase.getInstance(requireContext()).bookDao().saveBook(
                        vo
                    )
                }
                bookAdapter.add(
                    vo
                )
            } else {
                books.filterIndexed { index, bookVo ->
                    if (bookVo.path == vo.path)
                        _binding?.localBookRv?.scrollToPosition(index)
                    true
                }
            }
        }
    }
    private val bookAdapter = BookLocalRvAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("BookFragment", "viewCreated")

        val autoAddBook = view.findViewById<TextView>(R.id.auto_add_book)
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            if (isGranted) {
//
//            }
//        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        autoAddBook.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                LoaderManager.getInstance(requireActivity()).initLoader(
                    1, null, object : LoaderManager.LoaderCallbacks<Cursor> {
                        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                            return CursorLoader(requireContext()).apply {
                                uri = Uri.parse("content://media/external/file")
                                projection = arrayOf(
                                    MediaStore.Files.FileColumns.DATA,
                                    MediaStore.Files.FileColumns.DISPLAY_NAME
                                )
                                selection = MediaStore.Files.FileColumns.DISPLAY_NAME + " like ?"
                                selectionArgs = arrayOf(".txt")
                                sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC"
                            }
                        }

                        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                            data?.run {
                                moveToPosition(-1)
                                while (moveToNext()) {
                                    val path =
                                        getString(getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                                    Log.e("path", path)
                                }
                            }
                        }

                        override fun onLoaderReset(loader: Loader<Cursor>) {
                        }

                    }
                )
//                Environment.getExternalStorageDirectory().listFiles()?.forEach { file ->
//                    Log.e("file", file.absolutePath)
//                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                        if (isGranted) {

                        }
                    }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                }
            }
        }

        _binding?.manualAddBook?.setOnClickListener {
            txtLaunch.launch("text/plain")
        }
        _binding?.localBookRv?.run {
            adapter = bookAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.right = 10
                }
            })
        }

        CoroutineScope(Dispatchers.Default).launch {
            books =  BookDatabase.getInstance(requireContext()).bookDao().getBooks()
            LogUtil.d("books ${books.toString()}")
            withContext(Dispatchers.Main) {
                books.stream().distinct().filter { book ->
                    File(book.path).isFile
                }.collect(Collectors.toList()).let {
                    bookAdapter.refresh(it)
                }

                LogUtil.i("BookFragment refresh")
            }

        }

    }


}