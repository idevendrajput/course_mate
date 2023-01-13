package com.dr.coursemate.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WordPressEntity::class, MyNotesEntity::class], version = 4)
abstract class MyDatabase: RoomDatabase() {

    abstract fun wpDao(): WordPressDao
    abstract fun myNotes(): MyNotesDao

    companion object {
        private var INSTANCE: MyDatabase? = null
        fun getDatabase(mContext: Context): MyDatabase {
            if (INSTANCE == null) {
              INSTANCE =  synchronized(this) {
                    Room.databaseBuilder(mContext,MyDatabase::class.java, "agniveer_database")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }

            }
            return INSTANCE!!
        }

    }

}