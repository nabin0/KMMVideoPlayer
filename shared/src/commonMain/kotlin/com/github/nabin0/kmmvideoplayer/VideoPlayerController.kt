package com.github.nabin0.kmmvideoplayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow

expect class VideoPlayerController {
    val mediaDuration: MutableStateFlow<Long>
    val isPlaying: MutableStateFlow<Boolean>

    @Composable
    fun buildPlayer(onPlayerCreated: (player: Any) -> Unit)

    @Composable
    fun PlayerView(modifier: Modifier = Modifier)
    fun setMediaItem(
        videoLink: String,
        mediaTag: String?,
        displayTitle: String?,
        mediaId: String?,
        subtitleLink: String?,
    )

    fun prepare()
    fun play()
    fun pause()
    fun seekTo(millis: Long)
    fun currentPosition(): Long
    fun playWhenReady(boolean: Boolean)
}