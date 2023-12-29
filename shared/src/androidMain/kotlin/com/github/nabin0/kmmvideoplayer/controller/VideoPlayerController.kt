package com.github.nabin0.kmmvideoplayer.controller

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.data.VideoQuality
import kotlinx.coroutines.flow.MutableStateFlow

actual class VideoPlayerController {

    private var exoPlayer: ExoPlayer? = null
    private var currentSelectedVideoQuality = VideoQuality(-1, "Auto", -1)

    private var audioTrackGroup: Any? = null
    private var videoTrackGroup: Any? = null
    private var textTrackGroup: Any? = null


    actual fun getCurrentPlaybackSpeed(): Float = currentPlaybackSpeed
    actual val listOfVideoResolutions: MutableStateFlow<List<VideoQuality>?> =
        MutableStateFlow(null)
    actual val listOfAudioFormats: MutableStateFlow<List<VideoQuality>?> = MutableStateFlow(null)
    actual val listOfSubtitles: MutableStateFlow<List<VideoQuality>?> = MutableStateFlow(null)


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

                    override fun onTracksChanged(tracks: Tracks) {
                        super.onTracksChanged(tracks)
                        val videoResolutions: MutableList<VideoQuality> = mutableListOf()
//                        val audioFormats = mutableStateListOf<>()
//                        val subtitlesList = mutableStateListOf<VideoQuality>()

                        for (trackGroup in tracks.groups) {
                            // Group level information.
                            val trackType = trackGroup.type
                            val trackInGroupIsSelected = trackGroup.isSelected
                            val trackInGroupIsSupported = trackGroup.isSupported
                            Log.d("TAG", "track type $trackType")

                            if (trackType == C.TRACK_TYPE_VIDEO) {
                                videoTrackGroup = trackGroup
                                for (i in 0 until trackGroup.length) {
                                    val trackFormat = trackGroup.getTrackFormat(i)
                                    val trackIndex = i
                                    val trackName = "${trackFormat.height} p"
                                    val resolutionKey = trackFormat.height
                                    videoResolutions.add(
                                        VideoQuality(
                                            trackIndex,
                                            trackName,
                                            resolutionKey
                                        )
                                    )
                                }

                                if (videoResolutions.isNotEmpty()) {
                                    videoResolutions.add(0, VideoQuality(-1, "Auto", -1))
                                    videoResolutions?.distinctBy { it.resolutionKey }
                                        ?.sortedByDescending { it.resolutionKey }
                                        ?.toMutableList()
                                        ?.takeIf { it.isNotEmpty() }
                                    listOfVideoResolutions.value = videoResolutions
                                } else {
                                    listOfVideoResolutions.value = null
                                }

                            }
                            if (trackType == C.TRACK_TYPE_AUDIO) {
                                // audioTrackGroup = trackGroup
                                Log.d("TAG", "this is track type audio: ")

                            }
                            if (trackType == C.TRACK_TYPE_TEXT) {
                                Log.d("TAG", "this is track type text: ")
                                for(i in 0..trackGroup.length){
                                    val trackFormat = trackGroup.getTrackFormat(i)

                                    Log.d("TAG", " ${trackFormat.language} ")
                                    Log.d("TAG", " ${trackFormat} ")
                                }
                            }

                        }
                    }

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
        videoItem: VideoItem,
    ) {
        exoPlayer?.setMediaItem(
            buildMediaItem(videoItem)
        )
    }

    private fun buildMediaItem(
        videoItem: VideoItem,
    ): MediaItem {
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(videoItem.videoUrl)
            .setMediaId(videoItem.videoUrl.toString())
            .setTag(videoItem.title.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDisplayTitle(videoItem.title)
                    .build()
            )
        when (Util.inferContentType(Uri.parse(videoItem.videoUrl))) {
            C.CONTENT_TYPE_SS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_SS)
            C.CONTENT_TYPE_DASH -> {
                mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
                videoItem.licenseUrl?.let {
                    mediaItemBuilder.setDrmConfiguration(
                        DrmConfiguration.Builder(C.WIDEVINE_UUID)
                            .setLicenseUri(videoItem.licenseUrl).build()
                    )
                }
            }

            C.CONTENT_TYPE_HLS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
            C.CONTENT_TYPE_OTHER -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MP4)
            else -> {}
        }

        val mediaItem =
            if (videoItem.listOfExternalSubtitleLink != null) {
                val listOfSubtitleConfiguration = mutableListOf<MediaItem.SubtitleConfiguration>()

                for (subtitleLink in videoItem.listOfExternalSubtitleLink) {
                    listOfSubtitleConfiguration.add(
                        MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleLink))
                            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                            .setLanguage("en")
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()
                    )
                }
                mediaItemBuilder.setSubtitleConfigurations(listOfSubtitleConfiguration).build()
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

    actual fun addPlayList(listOfVideos: List<VideoItem>) {
        val listOfMediaItems = mutableListOf<MediaItem>()

        for (videoItem in listOfVideos) {
            listOfMediaItems.add(buildMediaItem(videoItem = videoItem))
        }

        if (listOfMediaItems.isNotEmpty())
            exoPlayer?.addMediaItems(listOfMediaItems)
    }

    actual fun setPlayList(listOfVideos: List<VideoItem>) {
        val listOfMediaItems = mutableListOf<MediaItem>()

        for (videoItem in listOfVideos) {
            listOfMediaItems.add(buildMediaItem(videoItem = videoItem))
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

    actual fun setSpecificVideoQuality(videoQuality: VideoQuality) {
        currentSelectedVideoQuality = videoQuality
        exoPlayer?.let {
            if (videoQuality.index == -1) {
                it.trackSelectionParameters = it.trackSelectionParameters.buildUpon()
                    .clearOverride((videoTrackGroup as Tracks.Group).mediaTrackGroup).build()
                return
            }
            it.trackSelectionParameters =
                it.trackSelectionParameters
                    .buildUpon()
                    .setOverrideForType(
                        TrackSelectionOverride(
                            (videoTrackGroup as Tracks.Group).mediaTrackGroup,
                            videoQuality.index
                        )
                    ).build()
        }
    }

    actual fun getCurrentVideoStreamingQuality(): VideoQuality {
        return currentSelectedVideoQuality
    }


}