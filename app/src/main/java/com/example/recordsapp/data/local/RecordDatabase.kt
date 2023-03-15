package com.example.recordsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recordsapp.data.local.convertors.BitmapConvertor
import com.example.recordsapp.data.local.dao.NoteDao
import com.example.recordsapp.data.local.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1)
@TypeConverters(BitmapConvertor::class)
abstract class RecordDatabase  : RoomDatabase(){
    abstract fun getNoteDao(): NoteDao
}