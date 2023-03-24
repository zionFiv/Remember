package com.zion.remember.db

import android.app.Application
import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zion.remember.BaseApplication

@Database(
    entities = [NoteInformationVo::class, WordsVo::class], version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun wordsDao(): WordsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        //1升级到2 2升级到3， 1升级到3需要额外定义吗？
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `words` (`word` TEXT NOT NULL, `wordExplain` TEXT NOT NULL, " +
                        "PRIMARY KEY(`word`))")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'words' ADD COLUMN recordTime TEXT")
                database.execSQL("ALTER TABLE 'words' ADD COLUMN percent INTEGER")
            }
        }
        fun getInstance(context: Context ?= null): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(BaseApplication.instance, AppDatabase::class.java, "note")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
            }
        }

    }


}