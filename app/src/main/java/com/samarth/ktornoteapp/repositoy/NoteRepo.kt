package com.samarth.ktornoteapp.repositoy

import com.samarth.ktornoteapp.data.local.models.LocalNote
import com.samarth.ktornoteapp.data.remote.Models.User
import com.samarth.ktornoteapp.utils.MyResult
import kotlinx.coroutines.flow.Flow

/**
 * Created By Eslam Ghazy on 8/6/2022
 */
interface NoteRepo {

    suspend fun createUser(user: User): MyResult<String>
    suspend fun login(user: User): MyResult<String>
    suspend fun getUser(): MyResult<User>
    suspend fun logout(): MyResult<String>


    suspend fun createNote(note: LocalNote): MyResult<String>
    suspend fun updateNote(note: LocalNote): MyResult<String>
    fun getAllNotes(): Flow<List<LocalNote>>
    suspend fun getAllNotesFromServer()


    suspend fun deleteNote(noteId: String)
    suspend fun syncNotes()
}