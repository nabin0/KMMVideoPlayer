package com.github.nabin0.kmmvideoplayer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.utils.noRippleClickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoItem: VideoItem?,
    videoPlayerController: VideoPlayerController,
    listOfVideoUrls: List<VideoItem>?, // Change url to a custom video object with required params
) {
    val rememberedPlayerController = remember { videoPlayerController }
    var isControllerCreated by rememberSaveable { mutableStateOf(false) }
    if (!isControllerCreated) {
        rememberedPlayerController.BuildPlayer { }
        videoItem?.let { url ->
            rememberedPlayerController.setMediaItem(videoItem)
        }
        listOfVideoUrls?.let {
            rememberedPlayerController.setPlayList(it)
        }
        rememberedPlayerController.prepare()
        rememberedPlayerController.setCCEnabled(false)
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

//    LaunchedEffect(showOverlay) {
//        yield()
//        delay(5000)
//        showOverlay = false
//    }


    var enableLandscapeMode by remember { mutableStateOf(false) }
    if (enableLandscapeMode) {
        videoPlayerController.EnableLandscapeScreenMode()
    } else {
        videoPlayerController.EnablePortraitScreenMode()
    }

    val videoViewModifier = if (enableLandscapeMode) Modifier else modifier.fillMaxHeight(0.27f)

    Box(modifier = videoViewModifier.background(Color.Black)) {
            videoPlayerController.PlayerView(modifier = Modifier.fillMaxWidth().noRippleClickable {
                showOverlay = true
                println("aaaaaaa")
            }, useDefaultController = false)

        if(showOverlay){
            Column(Modifier.fillMaxSize(0.8f).background(Color.Red).clickable {
                showOverlay = false
            }) {
                Text("djkfdhfjkad")
            }
        }


//        VideoPlayerOverlay(
//            modifier = Modifier.matchParentSize(),
//            videoPlayerController = videoPlayerController,
//            showOverLay = showOverlay,
//            onClickOverlayToHide = {
//                println("wwwwwwwwwww")
//                showOverlay = false
//            },
//            onToggleScreenOrientation = { enableLandscapeMode = !enableLandscapeMode },
//            isLandscapeView = enableLandscapeMode,
//            onClickShowPlaybackSpeedControls = {
//                showCustomDialogBoxForVideoControls = true
//            }
//        )

        if (showCustomDialogBoxForVideoControls) {
            CustomDialogBox(
                onHideBoxClicked = {
                    showCustomDialogBoxForVideoControls = false
                },
                content = {
                    VideoPreferencesBox(
                        videoPlayerController = videoPlayerController,
                        modifier = Modifier.fillMaxSize()
                    )

                },
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 16.dp).fillMaxSize()
                    .background(Color.Black)
            )
        }
    }
}


@Composable
fun test() {
    var a by remember { mutableStateOf(false) }

    Column {
        Text("kdjakhsdkhd" , Modifier.clickable { 
            a = !a
        })
        if (a) {
            Text("djakljsijelnkdnfadfiodfadgsd")
        }
        
    }

}
