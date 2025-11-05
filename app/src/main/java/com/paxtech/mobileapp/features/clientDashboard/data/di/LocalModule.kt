package com.paxtech.mobileapp.features.clientDashboard.data.di

import android.app.Application
import androidx.room.Room
import com.paxtech.mobileapp.features.clientDashboard.data.local.dao.SalonDao
import com.paxtech.mobileapp.features.clientDashboard.data.local.database.SalonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    
    @Provides
    @Singleton
    fun provideSalonDatabase(application: Application): SalonDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            SalonDatabase::class.java,
            "salon-db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideSalonDao(salonDatabase: SalonDatabase): SalonDao {
        return salonDatabase.salonDao()
    }
}
