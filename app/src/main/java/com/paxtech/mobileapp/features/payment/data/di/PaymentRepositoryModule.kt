package com.paxtech.mobileapp.features.payment.data.di

import com.paxtech.mobileapp.features.payment.data.repository.PaymentRepositoryImpl
import com.paxtech.mobileapp.features.payment.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface PaymentRepositoryModule {
    @Binds
    fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository
}


