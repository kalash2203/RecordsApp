package com.example.recordsapp.data.mapper

import com.example.recordsapp.data.local.entity.NoteEntity
import com.example.recordsapp.domain.model.Note

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        description = description,
        image = image
    )
}

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        description = description,
        image = image
    )
}