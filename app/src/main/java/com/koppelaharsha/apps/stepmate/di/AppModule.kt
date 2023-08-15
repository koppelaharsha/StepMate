package com.koppelaharsha.apps.stepmate.di

import com.koppelaharsha.apps.stepmate.data.BtRepository
import com.koppelaharsha.apps.stepmate.data.BtRepositoryImpl
import com.koppelaharsha.apps.stepmate.data.UserPreferencesRepository
import com.koppelaharsha.apps.stepmate.data.UserPreferencesRepositoryImpl
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
    fun btRepository(btRepositoryImpl: BtRepositoryImpl): BtRepository {
        return btRepositoryImpl
    }

    @Provides
    @Singleton
    fun userPreferencesRepository(userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl): UserPreferencesRepository {
        return userPreferencesRepositoryImpl
    }

}
