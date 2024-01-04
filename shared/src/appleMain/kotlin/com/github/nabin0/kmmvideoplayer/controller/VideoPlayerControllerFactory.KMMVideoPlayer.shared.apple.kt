package com.github.nabin0.kmmvideoplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIViewController

actual class VideoPlayerControllerFactory(
    val rootController: UIViewController
) {
    @Composable
    actual fun createVideoPlayer(): VideoPlayerController {
        return VideoPlayerController(rootController)
    }
}