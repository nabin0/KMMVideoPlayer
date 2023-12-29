package com.github.nabin0.kmmvideoplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.data.VideoQuality
import kotlinx.coroutines.flow.MutableStateFlow

expect class VideoPlayerController {

    val mediaDuration: MutableStateFlow<Long>
    val isPlaying: MutableStateFlow<Boolean>
    val isBuffering: MutableStateFlow<Boolean>

    val listOfVideoResolutions: MutableStateFlow<List<VideoQuality>?>
    val listOfAudioFormats: MutableStateFlow<List<VideoQuality>?>
    val listOfSubtitles: MutableStateFlow<List<VideoQuality>?>

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

    fun setMediaItem(videoItem: VideoItem)

    fun prepare()
    fun play()
    fun pause()
    fun seekTo(millis: Long)
    fun currentPosition(): Long
    fun releasePlayer()
    fun stop()
    fun playWhenReady(boolean: Boolean)

    fun addPlayList(listOfVideos: List<VideoItem>)

    fun setPlayList(listOfVideos: List<VideoItem>)

    fun playNextFromPlaylist()

    fun playPreviousFromPlaylist()

    fun setPlaybackSpeed(selectedPlaybackSpeed: Float)

    fun getCurrentPlaybackSpeed(): Float

    fun getCurrentVideoStreamingQuality(): VideoQuality

    fun setSpecificVideoQuality(videoQuality: VideoQuality)
}