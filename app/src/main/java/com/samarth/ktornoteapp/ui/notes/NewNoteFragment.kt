package com.samarth.ktornoteapp.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavArgsLazy
import androidx.navigation.fragment.navArgs
import com.samarth.ktornoteapp.R
import com.samarth.ktornoteapp.databinding.FragmentNewNoteBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created By Eslam Ghazy on 8/6/2022
 */
@AndroidEntryPoint
class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    val binding: FragmentNewNoteBinding?
        get() = _binding

    private val TAG = "NewNoteFragment"


    val noteViewModel: NoteViewModel by activityViewModels()
    val args: NavArgsLazy<NewNoteFragmentArgs> = navArgs<NewNoteFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewNoteBinding.bind(view)

        noteViewModel.oldNote = args.value.note

        noteViewModel.oldNote?.noteTitle?.let {
            binding?.newNoteTitleEditText?.setText(it)
        }

        noteViewModel.oldNote?.description?.let {
            binding?.newNoteDescriptionEditText?.setText(it)
        }
        binding?.date?.isVisible = noteViewModel.oldNote != null
        noteViewModel.oldNote?.date?.let {
            binding?.date?.text = noteViewModel.milliToDate(it)
        }

    }

    override fun onPause() {
        super.onPause()
        if (noteViewModel.oldNote == null) {
            createNote()
        } else {
            updateNote()
        }
    }


    private fun createNote() {

        val noteTitle = binding?.newNoteTitleEditText?.text?.toString()?.trim()
        val description = binding?.newNoteDescriptionEditText?.text?.toString()?.trim()

        if (noteTitle.isNullOrEmpty() && description.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Note is Empty!", Toast.LENGTH_SHORT).show()
            return
        }

        noteViewModel.createNote(noteTitle, description)
    }

    private fun updateNote() {

        val noteTitle = binding?.newNoteTitleEditText?.text?.toString()?.trim()
        val description = binding?.newNoteDescriptionEditText?.text?.toString()?.trim()

        if (noteTitle.isNullOrEmpty() && description.isNullOrEmpty()) {
            noteViewModel.deleteNote(noteViewModel.oldNote!!.noteId)
            return
        }
        noteViewModel.updateNote(noteTitle, description)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}