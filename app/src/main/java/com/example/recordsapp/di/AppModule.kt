package com.example.recordsapp.di

import android.app.Application
import androidx.room.Room
import com.example.recordsapp.data.local.RecordDatabase
import com.example.recordsapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDatabase(app:Application): RecordDatabase {
        return Room.databaseBuilder(
            app,
            RecordDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

}