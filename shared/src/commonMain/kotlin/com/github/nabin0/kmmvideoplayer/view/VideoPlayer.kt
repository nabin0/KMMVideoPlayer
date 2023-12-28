package com.github.nabin0.kmmvideoplayer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerController
import com.github.nabin0.kmmvideoplayer.noRippleClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String?,
    videoPlayerController: VideoPlayerController,
    listOfVideoUrls: List<String>?, // Change url to a custom video object with required params
) {
    val rememberedPlayerController = remember { videoPlayerController }
    var isControllerCreated by rememberSaveable { mutableStateOf(false) }
    if (!isControllerCreated) {
        rememberedPlayerController.BuildPlayer { }
        videoUrl?.let { url ->
            rememberedPlayerController.setMediaItem(videoLink = url, null, null, null, null)
        }
        listOfVideoUrls?.let {
            rememberedPlayerController.setPlayList(it)
        }
        rememberedPlayerController.prepare()
        rememberedPlayerController.playWhenReady(true)
        rememberedPlayerController.HandleActivityLifecycleStageChanges()
        isControllerCreated = true
    }

    VideoPlayerView(modifier = modifier, videoPlayerController = rememberedPlayerController)

}

@Composable
fun VideoPlayerView(modifier: Modifier = Modifier, videoPlayerController: VideoPlayerController) {
    var showOverlay by remember { mutableStateOf(true) }
    var showCustomDialogBoxForVideoControls by remember { mutableStateOf(false) }

    LaunchedEffect(showOverlay) {
        yield()
        delay(5000)
        showOverlay = false
    }


    var enableLandscapeMode by remember { mutableStateOf(false) }
    if (enableLandscapeMode) {
        videoPlayerController.EnableLandscapeScreenMode()
    } else {
        videoPlayerController.EnablePortraitScreenMode()
    }

    val videoViewModifier = if (enableLandscapeMode) Modifier else modifier.fillMaxHeight(0.27f)
    Box(modifier = videoViewModifier) {

        videoPlayerController.PlayerView(modifier = Modifier.fillMaxWidth().noRippleClickable {
            showOverlay = true
        }, useDefaultController = false)



        VideoPlayerOverlay(
            modifier = Modifier.matchParentSize(),
            videoPlayerController = videoPlayerController,
            showOverLay = showOverlay,
            onClickOverlayToHide = { showOverlay = false },
            onToggleScreenOrientation = { enableLandscapeMode = !enableLandscapeMode },
            isLandscapeView = enableLandscapeMode,
            onClickShowPlaybackSpeedControls = {
                showCustomDialogBoxForVideoControls = true
            }
        )

        if (showCustomDialogBoxForVideoControls) {

            CustomDialogBox(onHideBoxClicked = {
                showCustomDialogBoxForVideoControls = false
            }, content = {
                PlaybackSpeedList(currentPlaybackSpeed = videoPlayerController.getCurrentPlaybackSpeed(), onPlaybackSpeedSelected = {
                    videoPlayerController.setPlaybackSpeed(it)
                }, modifier = Modifier.fillMaxWidth())
            }, modifier = Modifier.padding(horizontal = 30.dp, vertical = 16.dp).fillMaxSize().background(Color.Black))
        }
    }
}
