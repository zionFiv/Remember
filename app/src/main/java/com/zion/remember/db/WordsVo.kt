package com.zion.remember.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
class WordsVo(
    @PrimaryKey val word: String,
    var wordExplain: String,
    var recordTime : String?,
    var percent : Int?,//记录百分比，0-100 按照记忆谱图，第一次添加50%，一周内第二次点击70% 一月内第三次点击90% 半年内第四次点击100%

)