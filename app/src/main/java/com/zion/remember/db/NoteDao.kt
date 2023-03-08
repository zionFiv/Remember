package com.zion.remember.db

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM note order by noteDate")
    fun getNotes() : MutableList<NoteInformationVo>
    // select * from user where id >=10000 order by id asc limit 10

    //   select * from user order by id asc limit 10000,10
    @Query("select * from note order by noteDate asc limit :page, :num")
    fun getNoteFrom(page : Int, num : Int) : MutableList<NoteInformationVo>

    @Query("select * from note order by noteDate desc limit :page, :num")
    fun getNoteFromDesc(page:Int, num : Int) : MutableList<NoteInformationVo>

    //获取总数量
    @Query("select COUNT(*) from note")
    fun getNoteCount() :Int

    @Query("SELECT * FROM note WHERE noteDate LIKE :date ")
    fun findByDate(date : String) : NoteInformationVo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(noteInfoVo : NoteInformationVo)

    @Update
    fun updateNote(noteInfoVo : NoteInformationVo)

    @Delete
    fun deleteNote(noteInfoVo : NoteInformationVo)
}