package com.paxtech.mobileapp.features.clientDashboard.data.di

import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.SalonService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReviewService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.WorkerService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.TimeSlotService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.ReservationService
import com.paxtech.mobileapp.features.clientDashboard.data.remote.services.DiscountService
import com.paxtech.mobileapp.features.payment.data.remote.services.PaymentService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun provideSalonService(retrofit: Retrofit): SalonService{
        return retrofit.create(SalonService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService{
        return retrofit.create(ReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkerService(retrofit: Retrofit): WorkerService{
        return retrofit.create(WorkerService::class.java)
    }

    @Provides
    @Singleton
    fun provideTimeSlotService(retrofit: Retrofit): TimeSlotService{
        return retrofit.create(TimeSlotService::class.java)
    }

    @Provides
    @Singleton
    fun provideReservationService(retrofit: Retrofit): ReservationService{
        return retrofit.create(ReservationService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentService(retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }

    @Provides
    @Singleton
    fun provideDiscountService(retrofit: Retrofit): DiscountService {
        return retrofit.create(DiscountService::class.java)
    }
}