package com.github.nabin0.kmmvideoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    videoPlayerController: VideoPlayerController,
) {
    val rememberedPlayerController = remember { videoPlayerController }
    var isControllerCreated by rememberSaveable{ mutableStateOf(false) }
    if (!isControllerCreated) {
        rememberedPlayerController.buildPlayer { }
        rememberedPlayerController.setMediaItem(videoLink = videoUrl, null, null, null, null)
        rememberedPlayerController.prepare()
        rememberedPlayerController.playWhenReady(true)
        isControllerCreated = true
    }

    var showOverlay by remember { mutableStateOf(true) }

    LaunchedEffect(showOverlay) {
        yield()
        delay(5000)
        showOverlay = false
    }

    var enableLandscapeMode by remember { mutableStateOf(false) }
    if(enableLandscapeMode){
        rememberedPlayerController.enableLandscapeScreenMode()
    }else{
        rememberedPlayerController.enablePortraitScreenMode()
    }

    Box(modifier = modifier) {
        rememberedPlayerController.PlayerView(modifier = Modifier.fillMaxWidth().noRippleClickable {
            showOverlay = true
            enableLandscapeMode = !enableLandscapeMode
        }, useDefaultController = false)
        VideoPlayerOverlay(
            modifier = Modifier.matchParentSize(),
            videoPlayerController = rememberedPlayerController,
            showOverLay = showOverlay,
            onClickOverlayToHide = { showOverlay = false }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            // Release the player here
        }
    }


}

@Composable
fun BoxScope.VideoPlayerOverlay(
    modifier: Modifier = Modifier,
    videoPlayerController: VideoPlayerController,
    showOverLay: Boolean = false,
    onClickOverlayToHide: () -> Unit,
) {
    var currentPosition by remember { mutableStateOf<Long?>(null) }
    val videoDuration by videoPlayerController.mediaDuration.collectAsState()
    val isPlaying by videoPlayerController.isPlaying.collectAsState()
    if (showOverLay && videoDuration > 0) {

        LaunchedEffect(Unit) {
            while (true) {
                yield()
                delay(100)
                currentPosition = videoPlayerController.currentPosition()
            }
        }

        Box(
            modifier = modifier.background(color = Color.Black.copy(alpha = 0.4f)).fillMaxWidth()
                .noRippleClickable {
                    onClickOverlayToHide()
                }) {
            PlayPauseButtons(
                isPaused = !isPlaying,
                pause = { videoPlayerController.pause() },
                resume = { videoPlayerController.play() },
                seekForward = { videoPlayerController.seekTo(currentPosition?.plus(it) ?: 0) },
                seekBackward = { videoPlayerController.seekTo(currentPosition?.plus(it) ?: 0) }
            )
            VideoCurrentProgressData(
                totalDuration = videoDuration.toFloat(),
                currentPosition = currentPosition?.toFloat() ?: 0F,
                onProgressValueChanged = { videoPlayerController.seekTo(millis = it.toLong()) },
                modifier = Modifier
            )
            println("toatl duration $videoDuration current $currentPosition")

        }
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

        val modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
        IconButton(modifier = modifier, onClick = {
            seekBackward(seekAmount)
        }) {
            playerControlIcon(Icons.Rounded.Replay10, 35.dp)
        }
        if (isPaused) {
            IconButton(modifier = modifier, onClick = { resume() }) {
                playerControlIcon(Icons.Rounded.PlayArrow, 50.dp)
            }
        } else {
            IconButton(modifier = modifier, onClick = { pause() }) {
                playerControlIcon(Icons.Rounded.Pause, 50.dp)
            }
        }
        IconButton(modifier = modifier, onClick = { seekForward(seekAmount) }) {
            playerControlIcon(Icons.Rounded.Forward10, 35.dp)
        }

    }
}


@Composable
fun VideoProgressBar(
    totalDuration: Float,
    currentPosition: Float,
    onProgressValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (totalDuration <= 0) return
    Slider(
        modifier = modifier,
        value = currentPosition,
        onValueChange = {
            onProgressValueChanged(it)
        },
        valueRange = 0f..totalDuration,
        colors = SliderDefaults.colors(
            thumbColor = Color.Red,
            activeTrackColor = Color.Red,
            inactiveTrackColor = Color.White.copy(alpha = 0.7f)
        )
    )
}

@Composable
fun BoxScope.VideoCurrentProgressData(
    totalDuration: Float,
    currentPosition: Float,
    onProgressValueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (totalDuration <= 0) return

    Row(
        modifier = modifier.align(Alignment.BottomStart).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoProgressBar(
            totalDuration = totalDuration,
            currentPosition = currentPosition,
            onProgressValueChanged = onProgressValueChanged,
            modifier = modifier.fillMaxWidth(0.8f)
        )
        val textStyle =
            TextStyle(color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
        Row(modifier = Modifier) {
            Text(
                text = "${convertMillisToReadableTime(currentPosition.toLong())}",
                style = textStyle
            )
            Text(
                text = "/ ${convertMillisToReadableTime(totalDuration.toLong())}",
                style = textStyle.copy(color = Color.White.copy(alpha = 0.85f))
            )
        }
    }
}
