package com.github.nabin0.kmmvideoplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    videoPlayerController: VideoPlayerController,
) {
    val rememberedPlayerController = remember { videoPlayerController }
    var isControllerCreated by rememberSaveable { mutableStateOf(false) }
    if (!isControllerCreated) {
        rememberedPlayerController.buildPlayer { }
        rememberedPlayerController.setMediaItem(videoLink = videoUrl, null, null, null, null)
        rememberedPlayerController.prepare()
        rememberedPlayerController.playWhenReady(true)
        rememberedPlayerController.handleActivityLifecycleStageChanges()
        isControllerCreated = true
    }

    VideoPlayerView(modifier = modifier, videoPlayerController = rememberedPlayerController)

}

@Composable
fun VideoPlayerView(modifier: Modifier = Modifier, videoPlayerController: VideoPlayerController) {
    var showOverlay by remember { mutableStateOf(true) }

    LaunchedEffect(showOverlay) {
        yield()
        delay(5000)
        showOverlay = false
    }


    var enableLandscapeMode by remember { mutableStateOf(false) }
    if (enableLandscapeMode) {
        videoPlayerController.enableLandscapeScreenMode()
    } else {
        videoPlayerController.enablePortraitScreenMode()
    }

    val videoViewModifier = if(enableLandscapeMode) Modifier else modifier.fillMaxHeight(0.28f)
    Box(modifier = videoViewModifier) {

        videoPlayerController.PlayerView(modifier = Modifier.fillMaxWidth().noRippleClickable {
            showOverlay = true
        }, useDefaultController = false)

        VideoPlayerOverlay(
            modifier = Modifier.matchParentSize(),
            videoPlayerController = videoPlayerController,
            showOverLay = showOverlay,
            onClickOverlayToHide = { showOverlay = false },
            onToggleScreenOrientation = {enableLandscapeMode = !enableLandscapeMode},
            isLandscapeView = enableLandscapeMode
        )
    }
}
