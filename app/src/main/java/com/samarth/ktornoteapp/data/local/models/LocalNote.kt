package com.samarth.ktornoteapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

/**
 * Created By Eslam Ghazy on 8/6/2022
 */
@Entity
data class LocalNote(
    var noteTitle: String? = null,
    var description: String? = null,
    var date: Long = System.currentTimeMillis(),
    var connected: Boolean = false,
    var locallyDeleted:Boolean = false,
    @PrimaryKey(autoGenerate = false)
    val noteId:String = UUID.randomUUID().toString()
):Serializable
