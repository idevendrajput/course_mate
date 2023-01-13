package com.dr.coursemate.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dr.coursemate.utils.AppConstant.CATEGORY
import com.dr.coursemate.utils.AppConstant.CONTENT
import com.dr.coursemate.utils.AppConstant.ID_
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.TITLE

@Entity(tableName = "word_press_data")
data class WordPressEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "randomId")
    val randomId: String,
    @ColumnInfo(name = ID_)
    val id: String,
    @ColumnInfo(name = TITLE)
    val title: String,
    @ColumnInfo(name = IMAGE_URL)
    val imageUrl: String,
    @ColumnInfo(name = CATEGORY)
    val category: String,
    @ColumnInfo(name = CONTENT)
    val content: String,
    @ColumnInfo(name = TIMESTAMP_FIELD)
    val timestamp: Long
)