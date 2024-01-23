package com.example.demotest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.nabin0.kmmvideoplayer.controller.test.CounterData

@Composable
fun TestComposable(counterData: CounterData) {
    val rememberedCounter = remember { counterData }
    val count by rememberedCounter.count.collectAsState()

    rememberedCounter.player()

//    Button(onClick = {
//        rememberedCounter.updateCounter()
//    }) {
//        Text("current count $count")
//    }

}





@Composable
fun CustomExo() {
}