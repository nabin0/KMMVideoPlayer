package com.github.nabin0.kmmvideoplayer

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class VideoPlayerControllerFactory {

    @Composable
    actual fun createVideoPlayer(): VideoPlayerController {
        val activity = LocalContext.current as ComponentActivity
        return remember(activity) {
            VideoPlayerController()
        }
    }
}