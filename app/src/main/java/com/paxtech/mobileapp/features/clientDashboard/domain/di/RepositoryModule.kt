package com.paxtech.mobileapp.features.clientDashboard.domain.di

import com.paxtech.mobileapp.features.clientDashboard.data.repositories.LocalSalonRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.SalonRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.ReviewRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.WorkerRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.TimeSlotRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.ReservationRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.domain.domain.LocalSalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.domain.SalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.domain.ReviewRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.domain.WorkerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.TimeSlotRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repositories.ReservationRepository

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    fun provideProductRepository(impl: SalonRepositoryImpl): SalonRepository
    
    @Binds
    fun provideReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository
    
    @Binds
    fun provideLocalSalonRepository(impl: LocalSalonRepositoryImpl): LocalSalonRepository

    @Binds
    fun provideWorkerRepository(impl: WorkerRepositoryImpl): WorkerRepository

    @Binds
    fun bindTimeSlotRepository(impl: TimeSlotRepositoryImpl): TimeSlotRepository

    @Binds
    fun bindReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository
}