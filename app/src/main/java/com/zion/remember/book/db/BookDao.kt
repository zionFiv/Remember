package com.zion.remember.book.db

import androidx.room.*
import com.zion.remember.db.NoteInformation

@Dao
interface BookDao {
    @Query("SELECT * FROM Book ")
    fun getBooks() : MutableList<BookVo>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveBook(book : BookVo)
    @Delete
    fun deleteBook(book:BookVo)
}