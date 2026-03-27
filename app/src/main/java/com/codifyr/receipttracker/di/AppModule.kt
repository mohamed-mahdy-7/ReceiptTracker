package com.codifyr.receipttracker.di

import com.codifyr.receipttracker.data.remote.SupabaseProvider
import com.codifyr.receipttracker.data.repository.ReceiptRepositoryImpl
import com.codifyr.receipttracker.domain.repository.ReceiptRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseProvider.client
    }

    @Provides
    @Singleton
    fun provideReceiptRepository(
        supabaseClient: SupabaseClient
    ): ReceiptRepository {
        return ReceiptRepositoryImpl(supabaseClient)
    }
}