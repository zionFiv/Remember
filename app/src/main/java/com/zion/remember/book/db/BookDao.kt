package com.zion.remember.book.db

import androidx.room.*

@Dao
interface BookDao {
    @Query("SELECT * FROM Book ")
    fun getBooks() : MutableList<BookVo>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveBook(book : BookVo)
    @Delete
    fun deleteBook(book:BookVo)
}