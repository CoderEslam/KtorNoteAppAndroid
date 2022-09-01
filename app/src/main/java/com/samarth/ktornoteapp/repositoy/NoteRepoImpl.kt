package com.samarth.ktornoteapp.repositoy

import com.samarth.ktornoteapp.data.local.Dao.NoteDao
import com.samarth.ktornoteapp.data.local.models.LocalNote
import com.samarth.ktornoteapp.data.remote.Models.RemoteNote
import com.samarth.ktornoteapp.data.remote.Models.Simpleresponse
import com.samarth.ktornoteapp.data.remote.Models.User
import com.samarth.ktornoteapp.data.remote.NoteApi
import com.samarth.ktornoteapp.utils.MyResult
import com.samarth.ktornoteapp.utils.SessionManger
import com.samarth.ktornoteapp.utils.isNetworkConnected
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created By Eslam Ghazy on 8/6/2022
 */
class NoteRepoImpl : NoteRepo {

    var noteApi: NoteApi
    var noteDao: NoteDao
    var sessionManager: SessionManger

    @Inject /* you can do it as a primary constructor*/
    constructor(noteApi: NoteApi, noteDao: NoteDao, sessionManager: SessionManger) {
        this.noteApi = noteApi;
        this.noteDao = noteDao;
        this.sessionManager = sessionManager;

    }

    override suspend fun syncNotes() {
        try {
            sessionManager.getJwtToken() ?: return
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }

            val locallyDeletedNotes = noteDao.getAllLocallyDeletedNotes()
            locallyDeletedNotes.forEach {
                deleteNote(it.noteId)
            }

            val notConnectedNotes = noteDao.getAllLocalNotes()
            notConnectedNotes.forEach {
                createNote(it)
            }

            val notUpdatedNotes = noteDao.getAllLocalNotes()
            notUpdatedNotes.forEach {
                updateNote(it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteNote(noteId: String) {
        try {
            noteDao.deleteNoteLocally(noteId)
            val token = sessionManager.getJwtToken() ?: kotlin.run {
                noteDao.deleteNote(noteId)
                return
            }
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }

            val response = noteApi.deleteNote(
                "Bearer $token",
                noteId
            )

            if (response.success) {
                noteDao.deleteNote(noteId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAllNotes(): Flow<List<LocalNote>> = noteDao.getAllNotesOrderedByDate()

    override suspend fun getAllNotesFromServer() {
        try {
            val token = sessionManager.getJwtToken() ?: return
            if (!isNetworkConnected(sessionManager.context)) {
                return
            }
            val MyResult = noteApi.getAllNote("Bearer $token")
            MyResult.forEach { remoteNote ->
                noteDao.insertNote(
                    LocalNote(
                        noteTitle = remoteNote.noteTitle,
                        description = remoteNote.description,
                        date = remoteNote.date,
                        connected = true,
                        noteId = remoteNote.id
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override suspend fun createNote(note: LocalNote): MyResult<String> {
        try {
            noteDao.insertNote(note)
            val token = sessionManager.getJwtToken()
                ?: return MyResult.Success("Note is Saved in Local Database!")
            if (!isNetworkConnected(sessionManager.context)) {
                return MyResult.Error("No Internet connection!")
            }

            val Result = noteApi.createNote(
                "Bearer $token",
                RemoteNote(
                    noteTitle = note.noteTitle,
                    description = note.description,
                    date = note.date,
                    id = note.noteId
                )
            )

            return if (Result.success) {
                noteDao.insertNote(note.also { it.connected = true })
                MyResult.Success("Note Saved Successfully!")
            } else {
                MyResult.Error(Result.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MyResult.Error(e.message ?: "Some Problem Occurred!")
        }

    }

    override suspend fun updateNote(note: LocalNote): MyResult<String> {
        try {
            noteDao.insertNote(note)
            val token = sessionManager.getJwtToken()
                ?: return MyResult.Success("Note is Updated in Local Database!")

            if (!isNetworkConnected(sessionManager.context)) {
                return MyResult.Error("No Internet connection!")
            }

            val Result = noteApi.updateNote(
                "Bearer $token",
                RemoteNote(
                    noteTitle = note.noteTitle,
                    description = note.description,
                    date = note.date,
                    id = note.noteId
                )
            )

            return if (Result.success) {
                noteDao.insertNote(note.also { it.connected = true })
                MyResult.Success("Note Updated Successfully!")
            } else {
                MyResult.Error(Result.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MyResult.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    /**/
    override suspend fun createUser(user: User): MyResult<String> {

        return try {
            if (!isNetworkConnected(sessionManager.context)) {
                MyResult.Error<String>("No Internet Connection!")
            }

            val Result: Simpleresponse = noteApi.createAccount(user)
            if (Result.success) {
                sessionManager.updateSession(Result.message/*token*/, user.name ?: "", user.email)
                MyResult.Success("User Created Successfully!") /* message fom myself => done */
            } else {
                MyResult.Error<String>(Result.message) /* if error happened show */
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MyResult.Error<String>(e.message ?: "Some Problem Occurred!")
        }

    }

    /*
    * to login and save email, name and token in stringPreferencesKey
    * */
    override suspend fun login(user: User): MyResult<String> {
        return try {
            if (!isNetworkConnected(sessionManager.context)) {
                MyResult.Error<String>("No Internet Connection!")
            }
            val Result = noteApi.login(user)
            if (Result.success) {
                sessionManager.updateSession(Result.message, user.name ?: "", user.email)
                /*
                * to sync notes with server db
                * */
                getAllNotesFromServer()
                MyResult.Success("Logged In Successfully!")
            } else {
                MyResult.Error<String>(Result.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MyResult.Error<String>(e.message ?: "Some Problem Occurred!")
        }
    }

    /*
    * to get user from stringPreferencesKey if it exist
    * */
    override suspend fun getUser(): MyResult<User> {
        return try {
            val name = sessionManager.getCurrentUserName()
            val email = sessionManager.getCurrentUserEmail()
            if (name == null || email == null) {
                MyResult.Error<User>("User not Logged In!")
            }
            MyResult.Success(User(name, email!!, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            MyResult.Error(e.message ?: "Some Problem Occurred!")
        }
    }

    /*
    * to logout and clear data.
    * */
    override suspend fun logout(): MyResult<String> {
        return try {
            sessionManager.logout()
            MyResult.Success("Logged Out Successfully!")
        } catch (e: Exception) {
            e.printStackTrace()
            MyResult.Error(e.message ?: "Some Problem Occurred!")
        }
    }
}