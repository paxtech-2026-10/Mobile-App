package com.paxtech.mobileapp.features.clientDashboard.domain.di

import com.paxtech.mobileapp.features.clientDashboard.data.repository.LocalSalonRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repository.SalonRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReviewRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repository.WorkerRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlotRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepositoryImpl
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.LocalSalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.SalonRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.ReviewRepository
import com.paxtech.mobileapp.features.clientDashboard.domain.repository.WorkerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import com.paxtech.mobileapp.features.clientDashboard.data.repository.TimeSlotRepository
import com.paxtech.mobileapp.features.clientDashboard.data.repository.ReservationRepository

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