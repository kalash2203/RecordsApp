package com.example.recordsapp.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordsapp.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
   private val noteRepository: NoteRepository
) : ViewModel() {

    var isNoteDeleted = MutableLiveData(false)

    fun deleteNoteById(id: Int) {
        viewModelScope.launch {
            isNoteDeleted.postValue(noteRepository.deleteNoteById(id))
        }
    }
}