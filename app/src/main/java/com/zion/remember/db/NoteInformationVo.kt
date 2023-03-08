package com.zion.remember.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteInformationVo(
    @PrimaryKey val noteDate: String,
    @ColumnInfo(name = "content") var content: String?
) {
    @Ignore
    var edit: Boolean = false
}