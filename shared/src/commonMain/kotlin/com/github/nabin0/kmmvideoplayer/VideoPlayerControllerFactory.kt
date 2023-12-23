package com.github.nabin0.kmmvideoplayer

import androidx.compose.runtime.Composable

expect class VideoPlayerControllerFactory {
    @Composable
    fun createVideoPlayer(): VideoPlayerController
}