package com.dr.coursemate.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.ITEM_TYPE
import com.dr.coursemate.utils.AppConstant.MAP_KEY
import com.dr.coursemate.utils.AppConstant.ORDER_ID
import com.dr.coursemate.utils.AppConstant.PRICE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD

@Entity(tableName = "my_notes")
data class MyNotesEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = MAP_KEY)
    val mapId: String,
    @ColumnInfo(name = DOCUMENT_ID)
    val documentId: String,
    @ColumnInfo(name = ITEM_TYPE)
    val itemType: String,
    @ColumnInfo(name = ORDER_ID)
    val orderId: String,
    @ColumnInfo(name = PRICE)
    val price: Int,
    @ColumnInfo(name = TIMESTAMP_FIELD)
    val timestamp: Long,
)
