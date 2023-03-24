package com.zion.remember.db

import androidx.room.*

@Dao
interface
WordsDao {

    @Query("select * from words order by recordTime desc ")
    fun getAllWords() : MutableList<WordsVo>

    @Query("select * from words order by recordTime   limit :page , :num")
    fun getWords(page : Int, num : Int) : MutableList<WordsVo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveWord(word : WordsVo)

    @Delete
    fun deleteWord(word : WordsVo)
}