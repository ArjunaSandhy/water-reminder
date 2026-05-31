package com.waterreminder.di

import android.content.Context
import androidx.room.Room
import com.waterreminder.data.local.AppDatabase
import com.waterreminder.data.local.dao.WaterEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideWaterEntryDao(database: AppDatabase): WaterEntryDao {
        return database.waterEntryDao()
    }
}
