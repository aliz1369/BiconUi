package com.tivasoft.biconui.di.builder

import com.tivasoft.biconui.di.components.BindingAdaptersComponent
import dagger.hilt.DefineComponent

@DefineComponent.Builder
interface BindingAdaptersComponentBuilder {
    fun build(): BindingAdaptersComponent
}