package com.tivasoft.biconui.di.components


import com.tivasoft.biconui.di.scopes.BindingAdaptersScope
import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@BindingAdaptersScope
@DefineComponent(parent = SingletonComponent::class)
interface BindingAdaptersComponent
