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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordsListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notesList = MutableStateFlow<List<Note>>(emptyList())
    val notesList = _notesList.asStateFlow()
    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()

    var imageUri: Uri? = null


    init {
        getAllNotes()
    }


    fun insertNote(note: Note) {
        viewModelScope.launch {
            val isNoteInserted = noteRepository.insertNote(note)

            if (!isNoteInserted) {
                _toast.emit("Something went wrong")
                return@launch
            }
            _toast.emit("Note inserted")

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