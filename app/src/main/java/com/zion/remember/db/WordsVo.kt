package com.zion.remember.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
class WordsVo(
    @PrimaryKey val word: String,
    var wordExplain: String

)