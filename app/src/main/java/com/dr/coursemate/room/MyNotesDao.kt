package com.dr.coursemate.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MyNotesDao {
    
    @Insert
    suspend fun insert(entity: MyNotesEntity)
    
    @Update
    suspend fun update(entity: MyNotesEntity)
    
    @Query("DELETE FROM my_notes")
    fun delete()
    
    @Query("DELETE FROM my_notes WHERE documentId ==:id")
    fun deleteById(id: String)
    
    @Query("SELECT * From my_notes")
    fun getMyNotes(): List<MyNotesEntity>

    @Query("SELECT * From my_notes WHERE documentId ==:id")
    fun getMyNotesById(id: String): List<MyNotesEntity>


}