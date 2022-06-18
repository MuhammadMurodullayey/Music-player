package uz.gita.serviceapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.serviceapp.domain.AppRepository
import uz.gita.serviceapp.domain.impl.AppRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @[Binds Singleton]
    fun getRepository(impl: AppRepositoryImpl) : AppRepository
}