package com.zion.remember.book.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class BookVo(
    @ColumnInfo val img: String,
    @ColumnInfo val title: String,
    @PrimaryKey val path: String )
