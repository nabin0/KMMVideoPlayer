package com.github.nabin0.kmmvideoplayer.controller

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

actual class VideoPlayerController {

    private var exoPlayer: ExoPlayer? = null

    actual val mediaDuration: MutableStateFlow<Long> = MutableStateFlow(0L)

    actual val isPlaying: MutableStateFlow<Boolean> =
        MutableStateFlow(exoPlayer?.isPlaying ?: false)

    actual val isBuffering: MutableStateFlow<Boolean> =
        MutableStateFlow(true)

    actual fun currentPosition(): Long = exoPlayer?.currentPosition ?: 0L

    @Composable
    actual fun BuildPlayer(onPlayerCreated: (player: Any) -> Unit) {
        val context = LocalContext.current
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            this.addListener(
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events,
                    ) {
                        super.onEvents(player, events)
                        // Triggers collector when next media item is auto player after finishing first one
                        mediaDuration.value = player.contentDuration
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == ExoPlayer.STATE_READY && (mediaDuration.value.toInt() == 0)) {
                            mediaDuration.value = contentDuration
                            isBuffering.value = false
                        }


                        if (playbackState == Player.STATE_BUFFERING) {
                            isBuffering.value = true
                        } else if (playbackState == Player.STATE_READY) {
                            isBuffering.value = false
                        }

                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        super.onTimelineChanged(timeline, reason)
                    }


                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        this@VideoPlayerController.isPlaying.value = isPlaying
                    }
                }
            )
        }
        exoPlayer?.let {
            onPlayerCreated(it)

        }
    }

    actual fun prepare() {
        exoPlayer?.prepare()
    }

    actual fun play() {
        exoPlayer?.play()
    }

    actual fun pause() {
        exoPlayer?.pause()
    }

    actual fun playWhenReady(boolean: Boolean) {
        exoPlayer?.playWhenReady = boolean
    }

    actual fun setMediaItem(
        videoLink: String,
        mediaTag: String?,
        displayTitle: String?,
        mediaId: String?,
        subtitleLink: String?,
    ) {
        exoPlayer?.setMediaItem(
            buildMediaItem(
                videoLink = videoLink,
                mediaTag = mediaTag,
                displayTitle = displayTitle,
                mediaId = mediaId,
                subtitleLink = subtitleLink
            )
        )
    }

    private fun buildMediaItem(
        videoLink: String,
        mediaTag: String?,
        displayTitle: String?,
        mediaId: String?,
        subtitleLink: String?,
    ): MediaItem {
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(videoLink)
            .setMediaId(mediaId.toString())
            .setTag(mediaTag.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(displayTitle.toString())
                    .build()
            )
        when (Util.inferContentType(Uri.parse(videoLink))) {
            C.CONTENT_TYPE_SS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_SS)
            C.CONTENT_TYPE_DASH -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
            C.CONTENT_TYPE_HLS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            C.CONTENT_TYPE_OTHER -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MP4)
            else -> {}
        }

        val mediaItem =
            if (subtitleLink != null) {
                mediaItemBuilder.setSubtitleConfigurations(
                    listOf(
                        MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleLink))
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            .setLanguage("en")
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()
                    )
                ).build()
            } else {
                mediaItemBuilder.build()
            }
        return mediaItem
    }

    @Composable
    actual fun PlayerView(useDefaultController: Boolean, modifier: Modifier) {
        AndroidView(modifier = modifier, factory = {
            androidx.media3.ui.PlayerView(it)
        }) {
            it.player = exoPlayer
            it.useController = useDefaultController
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT

            )
        }
    }


    actual fun seekTo(millis: Long) {
        exoPlayer?.seekTo(millis)
    }

    @Composable
    actual fun EnableLandscapeScreenMode() {
        val activity = LocalContext.current as Activity
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    @Composable
    actual fun EnablePortraitScreenMode() {
        val activity = LocalContext.current as Activity
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    actual fun releasePlayer() {
        stop()
        exoPlayer?.release()
    }

    actual fun stop() {
        exoPlayer?.stop()
    }

    @Composable
    actual fun HandleActivityLifecycleStageChanges() {
        DisposableEffectWithLifeCycle(
            onResume = { play() },
            onPause = { pause() },
            onDispose = { releasePlayer() })
    }

    actual fun addPlayList(listOfVideoUrls: List<String>) {
        val listOfMediaItems = mutableListOf<MediaItem>()

        for (videoUrlString in listOfVideoUrls) {
            listOfMediaItems.add(
                buildMediaItem(
                    videoLink = videoUrlString,
                    mediaTag = null,
                    displayTitle = null,
                    mediaId = null,
                    subtitleLink = null
                )
            )
        }

        if (listOfMediaItems.isNotEmpty())
            exoPlayer?.addMediaItems(listOfMediaItems)
    }

    actual fun setPlayList(listOfVideoUrls: List<String>) {
        val listOfMediaItems = mutableListOf<MediaItem>()

        for (videoUrlString in listOfVideoUrls) {
            listOfMediaItems.add(
                buildMediaItem(
                    videoLink = videoUrlString,
                    mediaTag = null,
                    displayTitle = null,
                    mediaId = null,
                    subtitleLink = null
                )
            )
        }

        if (listOfMediaItems.isNotEmpty())
            exoPlayer?.setMediaItems(listOfMediaItems)
    }

    actual fun playNextFromPlaylist() {
        if (exoPlayer?.hasNextMediaItem() == true) {
            mediaDuration.value = 0
            exoPlayer?.seekToNextMediaItem()
        }
    }

    actual fun playPreviousFromPlaylist() {
        if (exoPlayer?.hasPreviousMediaItem() == true) {
            mediaDuration.value = 0
            exoPlayer?.seekToPreviousMediaItem()
        }
    }

    private var currentPlaybackSpeed = 1f
    actual fun setPlaybackSpeed(selectedPlaybackSpeed: Float) {
        currentPlaybackSpeed = selectedPlaybackSpeed
        exoPlayer?.setPlaybackSpeed(selectedPlaybackSpeed)
    }

    actual fun getCurrentPlaybackSpeed(): Float = currentPlaybackSpeed
}