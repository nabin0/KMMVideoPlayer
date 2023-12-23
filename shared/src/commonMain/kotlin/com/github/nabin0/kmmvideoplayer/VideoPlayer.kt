package com.github.nabin0.kmmvideoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    videoPlayerController: VideoPlayerController,
) {
    videoPlayerController.buildPlayer {
    }
    videoPlayerController.setMediaItem(videoLink = videoUrl, null, null, null, null)
    videoPlayerController.prepare()
    videoPlayerController.playWhenReady(true)



//    val a = videoPlayerController.mediaDuration.collectAsState()

    Box(modifier = modifier) {
        videoPlayerController.PlayerView(modifier = Modifier.fillMaxWidth())
        VideoPlayerOverlay(modifier = Modifier.matchParentSize(), videoPlayerController = videoPlayerController)
    }


}

@Composable
fun BoxScope.VideoPlayerOverlay(
    modifier: Modifier = Modifier,
    videoPlayerController: VideoPlayerController,
) {
    var videoDuration by remember { mutableStateOf<Long?>(null) }
    var currentPosition by remember { mutableStateOf<Long?>(null) }

    // TODO: Optimize this so that this runs only when video is playing
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(300)
             currentPosition = videoPlayerController.currentPosition()
        }
    }

    val isPlaying by videoPlayerController.isPlaying.collectAsState()

    Box(modifier = modifier.fillMaxWidth()) {
        PlayPauseButtons(
            isPaused = !isPlaying,
            pause = {videoPlayerController.pause()},
            resume = {videoPlayerController.play()},
            seekForward = {videoPlayerController.seekTo(currentPosition?.plus(it) ?: 0)},
            seekBackward = {videoPlayerController.seekTo(currentPosition?.plus(it) ?: 0)}
        )
    }
}

@Composable
fun BoxScope.PlayPauseButtons(
    isPaused: Boolean,
    pause: () -> Unit,
    resume: () -> Unit,
    seekForward: (millis: Long) -> Unit,
    seekBackward: (millis: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seekAmount = 10000L
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.align(Alignment.Center).fillMaxWidth()
    ) {
        val playerControlIcon: @Composable (imageVector: ImageVector, size: Dp) -> Unit =
            @Composable { imageVector, size ->
                Icon(
                    imageVector = imageVector,
                    tint = Color.White,
                    modifier = Modifier.size(size),
                    contentDescription = "",
                )
            }

        val modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
        IconButton(modifier = modifier,onClick = {
            seekBackward(seekAmount)
        }) {
            playerControlIcon(Icons.Rounded.Replay10, 35.dp)
        }
        if(isPaused){
            IconButton(modifier = modifier,onClick = { resume() }) {
                playerControlIcon(Icons.Rounded.PlayArrow, 50.dp)
            }
        }else{
            IconButton(modifier = modifier,onClick = { pause() }) {
                playerControlIcon(Icons.Rounded.Pause, 50.dp)
            }
        }
        IconButton(modifier = modifier,onClick = { seekForward(seekAmount) }) {
            playerControlIcon(Icons.Rounded.Forward10, 35.dp)
        }

    }
}
