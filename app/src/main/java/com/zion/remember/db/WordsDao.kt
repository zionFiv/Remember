package com.zion.remember.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordsDao {

    @Query("select * from words order by word desc ")
    fun getAllWords() : MutableList<WordsVo>

    @Query("select * from words order by word desc limit :page , :num")
    fun getWords(page : Int, num : Int) : MutableList<WordsVo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveWord(word : WordsVo)
}