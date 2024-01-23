package com.github.nabin0.kmmvideoplayer.controller.test

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

expect class CounterData {
    val count: MutableStateFlow<Int>

    fun updateCounter()

    @Composable
    fun player()
}

expect class CounterFactory{
    fun getCounterData(): CounterData
}