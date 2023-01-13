package com.dr.coursemate.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dr.coursemate.utils.AppConstant.DATE_TIME
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.IMAGE_URL
import com.dr.coursemate.utils.AppConstant.THE_TITLE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD

@Entity
data class QuizEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = DOCUMENT_ID)
    val documentId : String,

    @ColumnInfo(name = THE_TITLE)
    val title : String,

    @ColumnInfo(name = IMAGE_URL)
    val imageUrl : String,

    @ColumnInfo(name = DATE_TIME)
    val dateTime : String,

    @ColumnInfo(name = TIMESTAMP_FIELD)
    val timestamp : String
)