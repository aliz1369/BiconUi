package com.tivasoft.biconui.di.modules

import android.content.Context
import androidx.room.Room
import com.tivasoft.biconui.data.source.local.ChatDatabase
import com.tivasoft.biconui.data.source.local.ChatMessagesDao
import com.tivasoft.biconui.data.source.local.PrescriptionsDao
import com.tivasoft.biconui.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Singleton
    @Provides
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room
            .databaseBuilder(
                context,
                ChatDatabase::class.java,
                Constants.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideChatMessagesDao(db: ChatDatabase): ChatMessagesDao {
        return db.chatMessageDao()
    }

    @Singleton
    @Provides
    fun providePrescriptionsDao(db: ChatDatabase): PrescriptionsDao {
        return db.prescriptionsDao()
    }
}