package com.tivasoft.biconui

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.tivasoft.biconui.di.builder.BindingAdaptersComponentBuilder
import com.tivasoft.biconui.di.entry_points.BindingAdaptersEntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var bindingComponentProvider: Provider<BindingAdaptersComponentBuilder>

    override fun onCreate() {
        super.onCreate()

        val dataBindingComponent = bindingComponentProvider.get().build()
        val dataBindingEntryPoint = EntryPoints.get(
            dataBindingComponent, BindingAdaptersEntryPoint::class.java
        )
        DataBindingUtil.setDefaultComponent(dataBindingEntryPoint)
    }
}