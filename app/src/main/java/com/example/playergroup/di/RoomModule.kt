package com.example.playergroup.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playergroup.data.room.VoteDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideVoteData(@ApplicationContext context: Context): VoteDb {
        return Room.databaseBuilder(context, VoteDb::class.java, "vote.db")
            .addCallback(object : RoomDatabase.Callback() {}).build()
    }
}