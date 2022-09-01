package com.samarth.ktornoteapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.samarth.ktornoteapp.data.local.Dao.NoteDao
import com.samarth.ktornoteapp.data.local.models.LocalNote

/**
 * Created By Eslam Ghazy on 8/6/2022
 */

@Database(
    entities = [LocalNote::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao


}