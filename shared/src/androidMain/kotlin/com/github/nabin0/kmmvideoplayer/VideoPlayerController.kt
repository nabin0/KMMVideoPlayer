package com.github.nabin0.kmmvideoplayer

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

actual class VideoPlayerController {

    private var exoPlayer: ExoPlayer? = null

    actual val mediaDuration: MutableStateFlow<Long> = MutableStateFlow(0L)
    actual val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(exoPlayer?.isPlaying ?: false)

    @Composable
    actual fun buildPlayer(onPlayerCreated: (player: Any) -> Unit) {
        val context = LocalContext.current
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            this.addListener(
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events,
                    ) {
                        super.onEvents(player, events)

                        // hide title only when player duration is at least 200ms
//                        if (player.currentPosition >= 200) {
//                            visibleState = false
//                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)

                        // everytime the media item changes, show the title
//                        visibleState = true
//                        videoTitle = mediaItem?.mediaMetadata?.displayTitle.toString()

                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == ExoPlayer.STATE_READY && mediaDuration.value == null) {
                            mediaDuration.value = contentDuration
                        }
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
                        MediaItem.SubtitleConfiguration
                            .Builder(Uri.parse(subtitleLink))
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
    actual fun PlayerView(modifier: Modifier) {
        AndroidView(modifier = modifier, factory = {
            androidx.media3.ui.PlayerView(it)
        }) {
            it.player = exoPlayer
            it.useController = false
        }
    }


    actual fun seekTo(millis: Long) {
        exoPlayer?.seekTo(millis)
    }


    actual fun currentPosition(): Long = exoPlayer?.currentPosition ?: 0L


}