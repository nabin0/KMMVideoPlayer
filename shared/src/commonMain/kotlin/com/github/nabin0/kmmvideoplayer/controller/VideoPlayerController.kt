package com.github.nabin0.kmmvideoplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow

expect class VideoPlayerController {

    val mediaDuration: MutableStateFlow<Long>
    val isPlaying: MutableStateFlow<Boolean>
    val isBuffering: MutableStateFlow<Boolean>

    @Composable
    fun BuildPlayer(onPlayerCreated: (player: Any) -> Unit)

    @Composable
    fun PlayerView(useDefaultController: Boolean = true, modifier: Modifier = Modifier)

    @Composable
    fun EnablePortraitScreenMode()

    @Composable
    fun EnableLandscapeScreenMode()

    @Composable
    fun HandleActivityLifecycleStageChanges()

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
    fun releasePlayer()
    fun stop()
    fun playWhenReady(boolean: Boolean)

    // TODO: Change to list of a data object with required data to create media item
    fun addPlayList(listOfVideoUrls: List<String>)

    fun setPlayList(listOfVideoUrls: List<String>)

    fun playNextFromPlaylist()

    fun playPreviousFromPlaylist()

    fun setPlaybackSpeed(selectedPlaybackSpeed: Float)

    fun getCurrentPlaybackSpeed(): Float
}