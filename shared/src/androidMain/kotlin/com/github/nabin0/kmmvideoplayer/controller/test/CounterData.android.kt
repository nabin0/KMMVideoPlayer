package com.github.nabin0.kmmvideoplayer.controller.test

import android.util.Log
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

actual class CounterData {
    init {
        Log.d("TAG", "CounterDatag: called")
    }
    actual val count: MutableStateFlow<Int> = MutableStateFlow(0)
    actual fun updateCounter() {
        count.value = count.value +1
        Log.d("TAG", "updateCounter: called")
    }

    @Composable
    actual fun player() {
    }


}


actual class CounterFactory{
    actual fun getCounterData(): CounterData {
        return CounterData()
    }

}