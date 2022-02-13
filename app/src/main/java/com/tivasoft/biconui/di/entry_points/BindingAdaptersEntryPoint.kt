package com.tivasoft.biconui.di.entry_points


import androidx.databinding.DataBindingComponent
import com.tivasoft.biconui.di.scopes.BindingAdaptersScope
import com.tivasoft.biconui.databinding.BindingAdapters
import com.tivasoft.biconui.di.components.BindingAdaptersComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn

@EntryPoint
@BindingAdaptersScope
@InstallIn(BindingAdaptersComponent::class)
interface BindingAdaptersEntryPoint:DataBindingComponent {
    @BindingAdaptersScope
    override fun getBindingAdapters(): BindingAdapters
}