package com.github.nabin0.kmmvideoplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.nabin0.kmmvideoplayer.data.ClosedCaptionForTrackSelector
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.data.VideoQuality
import kotlinx.coroutines.flow.MutableStateFlow
import platform.UIKit.UIViewController

actual class VideoPlayerController(val rootController: UIViewController) {
    actual val mediaDuration: MutableStateFlow<Long> = MutableStateFlow(0L)
    actual val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    actual val isBuffering: MutableStateFlow<Boolean> = MutableStateFlow(false)
    actual val listOfVideoResolutions: MutableStateFlow<List<VideoQuality>?> = MutableStateFlow(null)
    actual val listOfAudioFormats: MutableStateFlow<List<String>?> = MutableStateFlow(null)
    actual val listOfCC: MutableStateFlow<List<ClosedCaptionForTrackSelector>?> = MutableStateFlow(null)

    @Composable
    actual fun BuildPlayer(onPlayerCreated: (player: Any) -> Unit) {
    }

    @Composable
    actual fun PlayerView(
        useDefaultController: Boolean,
        modifier: Modifier
    ) {
    }

    @Composable
    actual fun EnablePortraitScreenMode() {
    }

    @Composable
    actual fun EnableLandscapeScreenMode() {
    }

    @Composable
    actual fun HandleActivityLifecycleStageChanges() {
    }

    actual fun setMediaItem(videoItem: VideoItem) {
    }

    actual fun prepare() {
    }

    actual fun play() {
    }

    actual fun pause() {
    }

    actual fun seekTo(millis: Long) {
    }

    actual fun currentPosition(): Long {
        TODO("Not yet implemented")
    }

    actual fun releasePlayer() {
    }

    actual fun stop() {
    }

    actual fun playWhenReady(boolean: Boolean) {
    }

    actual fun addPlayList(listOfVideos: List<VideoItem>) {
    }

    actual fun setPlayList(listOfVideos: List<VideoItem>) {
    }

    actual fun playNextFromPlaylist() {
    }

    actual fun playPreviousFromPlaylist() {
    }

    actual fun setPlaybackSpeed(selectedPlaybackSpeed: Float) {
    }

    actual fun getCurrentPlaybackSpeed(): Float {
        TODO("Not yet implemented")
    }

    actual fun getCurrentVideoStreamingQuality(): VideoQuality {
        TODO("Not yet implemented")
    }

    actual fun setSpecificVideoQuality(videoQuality: VideoQuality) {
    }

    actual fun setSpecificCC(cc: ClosedCaptionForTrackSelector) {
    }

    actual fun getCurrentCC(): ClosedCaptionForTrackSelector {
        TODO("Not yet implemented")
    }

    actual fun setCCEnabled(enabled: Boolean) {
    }

    actual fun setVolumeLevel(volumeLevel: Float) {
    }

}