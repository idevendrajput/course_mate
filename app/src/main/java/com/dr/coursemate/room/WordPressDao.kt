package com.dr.coursemate.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dr.coursemate.utils.AppConstant.CURRENT_AFFAIRS
import com.dr.coursemate.utils.AppConstant.NEWS

@Dao
interface WordPressDao {

    @Insert
    suspend fun insert(entity: WordPressEntity)

    @Update
    suspend fun update(entity: WordPressEntity)

    @Query("DELETE FROM word_press_data")
    fun delete()

    @Query("DELETE FROM word_press_data WHERE id ==:id")
    fun delete(id: String)

    @Query("DELETE FROM word_press_data WHERE category ==:category")
    fun deleteByCategory(category: String)

    @Query("SELECT * From word_press_data")
    fun getAllWPFeed(): List<WordPressEntity>

    @Query("SELECT * From word_press_data WHERE category ==:category")
    fun getCurrentAffairs(category: String = CURRENT_AFFAIRS) : List<WordPressEntity>

    @Query("SELECT * From word_press_data WHERE category ==:category")
    fun getNews(category: String = NEWS) : List<WordPressEntity>


}