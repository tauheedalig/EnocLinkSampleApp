package com.android.enoc.enoclinksampleapp.di

import com.android.enoc.enoclinksampleapp.storage.SharedPreferencesStorage
import com.android.enoc.enoclinksampleapp.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
abstract class StorageModule {

    // Makes Dagger provide SharedPreferencesStorage when a Storage type is requested
    @Binds
    abstract fun provideStorage(storage: SharedPreferencesStorage): Storage
}