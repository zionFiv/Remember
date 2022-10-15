package com.zion.remember.book.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zion.remember.db.AppDatabase
import com.zion.remember.db.NoteDao

@Database(entities = [BookVo::class], version = 1)
abstract class BookDatabase : RoomDatabase(){
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var instance: BookDatabase? = null

        fun getInstance(context: Context): BookDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, BookDatabase::class.java, "Book").build()
            }
        }

    }
}