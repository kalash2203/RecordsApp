package com.example.recordsapp.presentation.viewmodel

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordsapp.domain.model.Note
import com.example.recordsapp.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordsListViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val app: Application
) : ViewModel() {

    private val _notesList = MutableStateFlow<List<Note>>(emptyList())
    val notesList = _notesList.asStateFlow()

    var imageUri: Uri? = null
    var isLoggedIn = MutableLiveData(true)


    init {
        getAllNotes()
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            val isNoteInserted = noteRepository.insertNote(note)

            if (!isNoteInserted) {
                Toast.makeText(app, "Something went wrong", Toast.LENGTH_SHORT).show()
                return@launch
            }
            Toast.makeText(app, "Note is Inserted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes().collectLatest {
                _notesList.value = it
            }
        }
    }


}