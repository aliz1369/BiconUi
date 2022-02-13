package com.tivasoft.biconui.di.modules

import com.tivasoft.biconui.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {
    @Singleton
    @Provides
    fun provideSocketIO(): Socket = IO.socket(Constants.SOCKET_URL)
}