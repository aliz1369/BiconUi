package com.tivasoft.biconui.di.modules

import android.content.SharedPreferences
import com.tivasoft.biconui.data.mapper.*
import com.tivasoft.biconui.data.source.local.ChatMessagesDao
import com.tivasoft.biconui.data.source.local.PrescriptionsDao
import com.tivasoft.biconui.data.source.remote.NetworkApi
import com.tivasoft.biconui.data.source.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAuthRepository(networkApi: NetworkApi) = AuthRepository(networkApi)

    @Singleton
    @Provides
    fun provideUploadRepository(networkApi: NetworkApi) = UploadRepository(networkApi)

    @Singleton
    @Provides
    fun provideSearchRepository(networkApi: NetworkApi) = SearchRepository(networkApi)

    @Singleton
    @Provides
    fun provideDoctorRepository(networkApi: NetworkApi) = DoctorRepository(networkApi)

    @Singleton
    @Provides
    fun providePatientRepository(
        networkApi: NetworkApi,
        mapper: ScheduleMapper,
        conversationListMapper: ConversationListMapper,
        prescriptionsDao: PrescriptionsDao,
        prescriptionsCacheMapper: PrescriptionsCacheMapper
    ) = PatientRepository(
        networkApi,
        mapper,
        conversationListMapper,
        prescriptionsDao,
        prescriptionsCacheMapper
    )

    @Singleton
    @Provides
    fun provideTestRepository(networkApi: NetworkApi, backPackMapper: BackPackMapper) =
        TestRepository(networkApi, backPackMapper)

    @Singleton
    @Provides
    fun provideScheduleRepository(
        networkApi: NetworkApi,
        mapper: ScheduleMapper
    ) = ScheduleRepository(networkApi, mapper)

    @Singleton
    @Provides
    fun providePushyRepository(networkApi: NetworkApi) = PushyRepository(networkApi)

    @Singleton
    @Provides
    fun provideMeetRepository(networkApi: NetworkApi) = MeetRepository(networkApi)

    @Singleton
    @Provides
    fun provideAssistanceRepository(networkApi: NetworkApi) = AssistanceRepository(networkApi)

    @Singleton
    @Provides
    fun provideConversationRepository(
        networkApi: NetworkApi,
        chatMessagesDao: ChatMessagesDao,
        chatMapper: ChatMapper,
        sharedPreferences: SharedPreferences
    ) = ConversationRepository(networkApi, chatMessagesDao, chatMapper, sharedPreferences)

    @Singleton
    @Provides
    fun providePackageRepository(networkApi: NetworkApi) = PackageRepository(networkApi)
}