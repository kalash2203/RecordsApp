package com.example.recordsapp.data.repository

import com.example.recordsapp.data.local.RecordDatabase
import com.example.recordsapp.data.mapper.toNote
import com.example.recordsapp.data.mapper.toNoteEntity
import com.example.recordsapp.domain.model.Note
import com.example.recordsapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val db: RecordDatabase
) : NoteRepository {
    override suspend fun getAllNotes(): Flow<List<Note>> {
        return db.getNoteDao().getAllNotes().map { it.map { it.toNote() } }
    }

    override suspend fun insertNote(note: Note): Boolean {
        return try {
            db.getNoteDao().insertNote(note.toNoteEntity())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteNoteById(id: Int): Boolean {
        return try {
            db.getNoteDao().deleteNoteById(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}