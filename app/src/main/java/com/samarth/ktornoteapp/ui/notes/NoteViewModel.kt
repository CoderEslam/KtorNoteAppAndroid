package com.samarth.ktornoteapp.ui.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.ktornoteapp.data.local.models.LocalNote
import com.samarth.ktornoteapp.repositoy.NoteRepo
import com.samarth.ktornoteapp.utils.MyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created By Eslam Ghazy on 8/6/2022
 */

@HiltViewModel
class NoteViewModel @Inject constructor(var noteRepo: NoteRepo) : ViewModel() {

    private val TAG = "NoteViewModel"
    val notes = noteRepo.getAllNotes()
    var oldNote: LocalNote? = null
    var searchQuery: String = ""

    fun syncNotes(onDone: (() -> Unit)? = null) = viewModelScope.launch {

        noteRepo.syncNotes()
        onDone?.invoke()

    }

    fun createNote(
        noteTitle: String?,
        description: String?
    ) = viewModelScope.launch(Dispatchers.IO) {
        val localNote = LocalNote(
            noteTitle = noteTitle,
            description = description
        )
        val result: MyResult<String> = noteRepo.createNote(localNote)
        Log.e(
            TAG,
            result.toString()
        ); /*MyResult(data=Note is Saved in Local Database!, errorMessage=null)*/
    }

    fun deleteNote(
        noteId: String
    ) = viewModelScope.launch {
        noteRepo.deleteNote(noteId)
    }

    fun undoDelete(
        note: LocalNote
    ) = viewModelScope.launch {
        noteRepo.createNote(note)
    }


    fun updateNote(
        noteTitle: String?,
        description: String?
    ) = viewModelScope.launch(Dispatchers.IO) {

        if (noteTitle == oldNote?.noteTitle && description == oldNote?.description && oldNote?.connected == true) {
            return@launch
        }

        val note = LocalNote(
            noteTitle = noteTitle,
            description = description,
            noteId = oldNote!!.noteId
        )
        val result = noteRepo.updateNote(note)
        Log.e(TAG, "updateNote: " + result) /*updateNote: MyResult(data=Note is Updated in Local Database!, errorMessage=null)*/
    }


    fun milliToDate(time: Long): String {
        val date = Date(time)
        val simpleDateFormat = SimpleDateFormat("hh:mm a | MMM d, yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}