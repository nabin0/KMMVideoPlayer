package com.github.nabin0.kmmvideoplayer.android

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.chromium.net.CronetEngine
import java.io.File
import java.util.concurrent.Executors

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mediaItemBuilder = MediaItem.Builder()


    val type =
        Util.inferContentType(Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"))
    when (type) {
        C.CONTENT_TYPE_SS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_SS)
        C.CONTENT_TYPE_DASH -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
        C.CONTENT_TYPE_HLS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        C.CONTENT_TYPE_OTHER -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MP4)
        else -> {}
    }

    val mediaItem1 = mediaItemBuilder
        .setUri("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8")
        .setMediaId("never-gonna-give-you-up")
        .setTag("never-gonna-give-you-up")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setDisplayTitle("Never gonna give you up")
                .build()
        )

//        .setSubtitleConfigurations(
//            listOf(
//                MediaItem.SubtitleConfiguration
//                    .Builder(Uri.parse(subtitleLink))
//                    .setMimeType(MimeTypes.APPLICATION_SUBRIP)
//                    .setLanguage("en")
//                    .setSelectionFlags(SELECTION_FLAG_DEFAULT)
//                    .build()
//            )
//        )
        .build()

    val mediaItem = MediaItem.Builder()
        .setUri("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd")
        .setMimeType(MimeTypes.APPLICATION_MPD)
        .setDrmConfiguration(
            MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri("https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test")
                .build()
        )
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle("Clear(WebM, VP9)")
                .setDescription(
                    "Tears of Steel is a short film by producer Ton Roosendaal and director Ian Hubert. " +
                            "It was made using new enhancements to the visual effects capabilities of Blender, a free and open source software application for animation. " +
                            "The film was funded by the Blender Foundation, donations from the Blender community, pre-sales of the film's DVD and commercial sponsorship."
                )
                .setArtworkUri(Uri.parse("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"))
                .build()
        )
        .build()

    var videoTitle by remember { mutableStateOf(mediaItem.mediaMetadata.displayTitle) }
    var visibleState by remember { mutableStateOf(true) }
    var videoDuration by remember { mutableStateOf<Long?>(null) }
    var currentPosition by remember { mutableStateOf<Long?>(null) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).setMediaSourceFactory(
            DefaultMediaSourceFactory(getReadOnlyDataSourceFactory(context))
        ).build().apply {
            this.setMediaItem(mediaItem)

            this.prepare()

            this.playWhenReady = true


            this.addListener(
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events,
                    ) {
                        super.onEvents(player, events)

                        // hide title only when player duration is at least 200ms
                        if (player.currentPosition >= 200) {
                            visibleState = false
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)

                        // everytime the media item changes, show the title
                        visibleState = true
                        videoTitle = mediaItem?.mediaMetadata?.displayTitle.toString()

                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        if (playbackState == ExoPlayer.STATE_READY && videoDuration == null) {
                            videoDuration = contentDuration
                        }
                    }
                }
            )
        }
    }

    // TODO: Optimize this so that this runs only when video is playing
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(300)
            currentPosition = exoPlayer.currentPosition
        }
    }

    Box(modifier = modifier) {

        AndroidView(factory = {
            PlayerView(it)
        }) {
            it.player = exoPlayer
//            it.useController = false
        }

        val trackSelector = exoPlayer.trackSelector as DefaultTrackSelector
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo?.getTrackGroups(2)

        Button(onClick = {
            if (mappedTrackInfo != null) {
                for (i in 0 until mappedTrackInfo.length) {
                    val trackGroups = mappedTrackInfo.get(i)
                    val format = trackGroups.getFormat(i)
                    if (format.language != null) {
                        Log.d("TAG", "VideoPlayer: ${format.language}")
                    }

                    Log.d("TAG", "format: ${format.toString()}, $format.")
//                    if (trackGroups.length != 0) {
//                        when (exoPlayer.getRendererType(i)) {
//                            C.TRACK_TYPE_VIDEO -> mVideoRendererIndex = i
//                            C.TRACK_TYPE_TEXT -> mTextRendererIndex = i
//                            C.TRACK_TYPE_AUDIO -> mAudioRendererIndex = i
//                        }
//                    }
                }
            }
        }) {
            Text(text = "Subtitles")
        }


        Button(onClick = {
            trackSelector?.let {
                var availableStreamingQualitiesHLS: MutableList<HLSStreamingQuality>? = ArrayList()
                val renderTrack = it.currentMappedTrackInfo
                val renderCount = renderTrack?.rendererCount ?: 0
                for (rendererIndex in 0 until renderCount) {
                    if (isSupportedFormat(renderTrack, rendererIndex)) {
                        val trackGroupType = renderTrack?.getRendererType(rendererIndex)
                        val trackGroups = renderTrack?.getTrackGroups(rendererIndex)
                        val trackGroupsCount = trackGroups?.length!!
                        if (trackGroupType == C.TRACK_TYPE_VIDEO) {
                            for (groupIndex in 0 until trackGroupsCount) {
                                val videoQualityTrackCount = trackGroups[groupIndex].length
                                for (trackIndex in 0 until videoQualityTrackCount) {
                                    val isTrackSupported = renderTrack.getTrackSupport(
                                        rendererIndex,
                                        groupIndex,
                                        trackIndex
                                    ) == C.FORMAT_HANDLED
                                    if (isTrackSupported) {
                                        val track = trackGroups[groupIndex]
                                        val trackName =
                                            "${track.getFormat(trackIndex).height} p"
                                        val resolutionKey = track.getFormat(trackIndex).height
                                        /*if (track.getFormat(trackIndex).selectionFlags == C.SELECTION_FLAG_AUTOSELECT) {
                                            trackName.plus(" (Default)")
                                        }*/
                                        availableStreamingQualitiesHLS?.add(
                                            HLSStreamingQuality(
                                                trackIndex, trackName, resolutionKey = resolutionKey
                                            )
                                        )


                                        availableStreamingQualitiesHLS?.distinctBy { it.resolutionKey }
                                            ?.sortedByDescending { it.resolutionKey }
                                            ?.toMutableList()
                                            ?.takeIf { it.isNotEmpty() }?.also {
                                                it.add(0, HLSStreamingQuality(0, "Auto", 0))
                                            }
                                        Log.d("TAG", "VideoPlayer: $availableStreamingQualitiesHLS")

                                    }
                                }
                            }
                        }
                    }
                }

            }
        }) {
            Text(text = "Resoulution")
        }
//        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
//            val (title, videoPlayer) = this.createRefs()
//
//            // video title
//            AnimatedVisibility(
//                visible = visibleState,
//                modifier = Modifier.constrainAs(title) {
//                    top.linkTo(parent.top)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                }
//            ) {
//                Text(
//                    text = videoTitle.toString(),
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                )
//            }
//
        DisposableEffect(Unit) {
            onDispose { exoPlayer.release() }
        }
//
//            // Player view
//            AndroidView(
//                modifier = Modifier
//                    .testTag("VideoPlayer")
//                    .constrainAs(videoPlayer) {
//                        top.linkTo(parent.top)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                        bottom.linkTo(parent.bottom)
//                    },
//                factory = {
//                    PlayerView(context).apply {
//                        player = exoPlayer
////                        useController = false
//                        subtitleView?.setStyle(
//                            CaptionStyleCompat(
//                                android.graphics.Color.WHITE,
//                                android.graphics.Color.RED,
//                                android.graphics.Color.BLUE,
//                                CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
//                                android.graphics.Color.TRANSPARENT,
//                                null
//                            )
//                        )
//                        subtitleView?.setBottomPaddingFraction(0.8f)
////                        subtitleView?.setLeftTopRightBottom(20, 20, 0, 0)
//                        subtitleView
//                        layoutParams = FrameLayout
//                            .LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                            )
//                    }
//                }
//            )
//        }

        VideoPlayerOverlay(
            totalDuration = videoDuration ?: 0,
            currentPosition = currentPosition ?: 0,
            exoPlayer = exoPlayer,
        )
    }
}

@Composable
fun VideoPlayerOverlay(
    totalDuration: Long,
    currentPosition: Long,
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        PlayPauseButtons(
            isPaused = !exoPlayer.isPlaying,
            pause = { exoPlayer.pause() },
            resume = { exoPlayer.play() },
            seekForward = { exoPlayer.seekTo(currentPosition + it) },
            seekBackward = { exoPlayer.seekTo(currentPosition - it) },
        )

        VideoOptions()

        ProgressBar(
            totalDuration = totalDuration,
            currentPosition = currentPosition,
            exoPlayer = exoPlayer,
            modifier = Modifier,
        )
        DurationBox(
            totalDuration = totalDuration,
            currentPosition = currentPosition,
            modifier = Modifier,
        )
    }
}


fun isSupportedFormat(
    mappedTrackInfo: MappingTrackSelector.MappedTrackInfo?,
    rendererIndex: Int,
): Boolean {
    val trackGroupArray = mappedTrackInfo?.getTrackGroups(rendererIndex)
    return if (trackGroupArray?.length == 0) {
        false
    } else mappedTrackInfo?.getRendererType(rendererIndex) == C.TRACK_TYPE_VIDEO || mappedTrackInfo?.getRendererType(
        rendererIndex
    ) == C.TRACK_TYPE_AUDIO || mappedTrackInfo?.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT
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
    val seekAmount = 5000L

    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.align(Alignment.Center)
    ) {
        val myIcon: @Composable (imageVector: ImageVector, size: Dp) -> Unit =
            @Composable { imageVector, size ->
                Icon(
                    imageVector = imageVector,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp),
                    contentDescription = "",
                )
            }
        val modifier = @Composable {
            Modifier
                .outlineOnFocus(
                    outlineColor = Color.White,
                    outlineShape = CircleShape,
                    outlineWidth = 2.dp,
                    outlineOffset = 4.dp,
                )
                .focusable()
        }
        IconButton(
            onClick = { seekBackward(seekAmount) },
            modifier = modifier(),
        ) {
            myIcon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                size = 60.dp,
            )
        }
        IconButton(
            onClick = {
                if (isPaused) {
                    resume()
                } else {
                    pause()
                }
            },
            modifier = modifier(),
        ) {
            myIcon(
                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                size = 50.dp,
            )
        }
        IconButton(
            onClick = { seekForward(seekAmount) },
            modifier = modifier(),
        ) {
            myIcon(
                imageVector = Icons.Default.KeyboardArrowRight,
                size = 60.dp,
            )
        }
    }
}

@Composable
fun VideoOptions(modifier: Modifier = Modifier) {
    var showMenu by remember { mutableStateOf(false) }
    var menuHeading by remember { mutableStateOf("Heading") }
    var menuOptions by remember {
        mutableStateOf(
            listOf<String>(
                "Option 1",
                "Option 2",
                "Option 3"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onFocusChanged { showMenu = it.hasFocus }
    ) {
        if (showMenu) {
            // Option Menu
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 175.dp)
                    .background(Color.Gray)
            ) {
                Column(modifier = Modifier) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = menuHeading,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    }
                    Divider()
                    menuOptions.forEach { option ->
                        var isFocused by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isFocused) Color.White else Color.Transparent)
                                .onFocusChanged { isFocused = it.isFocused }
                                .focusable()
                        ) {
                            Text(
                                text = option,
                                textAlign = TextAlign.Center,
                                color = if (isFocused) Color.Black else Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 110.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            val option: @Composable (imageVector: ImageVector, heading: String, options: List<String>) -> Unit =
                @Composable { imageVector, heading, options ->
                    var isFocused by remember { mutableStateOf(false) }

                    LaunchedEffect(isFocused) {
                        if (isFocused) {
                            menuHeading = heading
                            menuOptions = options
                        }
                    }

                    IconButton(
                        onClick = {

                        },
                        modifier = Modifier
                            .outlineOnFocus(
                                outlineWidth = 2.dp,
                                outlineColor = Color.White,
                                outlineShape = CircleShape,
                                outlineOffset = 4.dp,
                            )
                            .background(
                                color = if (isFocused) Color.White else Color.Gray,
                                shape = CircleShape,
                            )
                            .onFocusChanged { isFocused = it.isFocused }
                    ) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = "",
                            tint = if (isFocused) Color.Gray else Color.White,
                        )
                    }
                }

            option(
                imageVector = Icons.Default.ClosedCaption,
                heading = "Subtitles",
                options = listOf("Disabled", "English", "French"),
            )
            option(
                imageVector = Icons.Default.MusicNote,
                heading = "Audio",
                options = listOf("English", "French", "German"),
            )
            option(
                imageVector = Icons.Default.Settings,
                heading = "Settings",
                options = listOf("Quality", "Speed", "Closed Captions Style"),
            )
        }
    }
}

@Composable
fun BoxScope.ProgressBar(
    totalDuration: Long,
    currentPosition: Long,
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier,
) {
    if (totalDuration == 0L) {
        return
    }

    val lineHeight = 4.dp
    val seekAmount = 5000L
    var size by remember { mutableStateOf(Size.Zero) }
    val progressFraction = currentPosition.toFloat() / totalDuration.toFloat()
    var isHandleFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(bottom = 60.dp)
            .align(Alignment.BottomStart)
            .onGloballyPositioned {
                size = it.size.toSize()
            }
            .fillMaxWidth()
            .heightIn(30.dp)
    ) {
        // Full width line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineHeight)
                .background(Color.Gray.copy(alpha = 0.8f))
                .align(Alignment.Center)
        )

        // Progress line
        Box(
            modifier = Modifier
                .fillMaxWidth(progressFraction)
                .height(lineHeight)
                .background(Color.White)
                .align(Alignment.CenterStart)
        )

        // Handle
        LocalDensity.current.apply {
            val generateXOffsetWithProgressFraction: (progressFraction: Float) -> Dp =
                { progressFraction ->
                    (size.width * progressFraction).toDp() - 4.dp
                }

            val actualXOffset = (size.width * progressFraction).toDp() - 4.dp
            var focusedXOffset by remember { mutableStateOf(actualXOffset) }

            val xOffset = if (isHandleFocused) focusedXOffset else actualXOffset
            var focusedProgressFraction by remember { mutableStateOf(progressFraction) }
            var focusedCurrentPosition by remember { mutableStateOf(currentPosition) }

            LaunchedEffect(isHandleFocused) {
                focusedXOffset = actualXOffset
                focusedCurrentPosition = currentPosition
            }

            val generateProgressFractionWithPositionOffset: (offset: Long) -> Float = { offset ->
                (focusedCurrentPosition + offset).toFloat() / totalDuration.toFloat()
            }

            // Preview
            if (isHandleFocused) {
                Box(
                    modifier = Modifier
                        .size(120.dp, 80.dp)
                        .offset(xOffset - 50.dp, (-55).dp)
                        .background(Color.Red)
                )
            }

//            Box(
//                modifier = Modifier
//                    .offset(xOffset, 0.dp)
//                    .outlineOnFocus(
//                        outlineColor = Color.White,
//                        outlineShape = CircleShape,
//                        outlineWidth = 2.dp,
//                        outlineOffset = 4.dp,
//                    )
//                    .onKeyEvent {
//                        if (it.isTypeKeyDown()
//                                .not()
//                        ) {
//                            return@onKeyEvent KeyEventPropagation.ContinuePropagation
//                        }
//                        if (it.isDPadCenterPress()) {
//                            exoPlayer.seekTo(focusedCurrentPosition)
//                        }
//                        if (it.isLeftPress()) {
//                            focusedCurrentPosition -= seekAmount
//                            focusedProgressFraction =
//                                generateProgressFractionWithPositionOffset(-seekAmount)
//                            focusedXOffset =
//                                generateXOffsetWithProgressFraction(focusedProgressFraction)
//                            return@onKeyEvent KeyEventPropagation.StopPropagation
//                        }
//                        if (it.isRightPress()) {
//                            focusedCurrentPosition += seekAmount
//                            focusedProgressFraction =
//                                generateProgressFractionWithPositionOffset(seekAmount)
//                            focusedXOffset =
//                                generateXOffsetWithProgressFraction(focusedProgressFraction)
//                            return@onKeyEvent KeyEventPropagation.StopPropagation
//                        }
//                        KeyEventPropagation.ContinuePropagation
//                    }
//                    .size(if (isHandleFocused) 15.dp else 12.dp)
//                    .background(Color.White, CircleShape)
//                    .onFocusChanged { isHandleFocused = it.isFocused }
//                    .align(Alignment.CenterStart)
//                    .focusable()
//            )
        }

    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.outlineOnFocus(
    outlineWidth: Dp,
    outlineColor: Color,
    outlineOffset: Dp,
    outlineShape: RoundedCornerShape,
): Modifier {
    var isFocused by remember { mutableStateOf(false) }
    return border(
        width = if (isFocused) outlineWidth else 0.dp,
        color = if (isFocused) outlineColor else Color.Transparent,
        shape = outlineShape
    )
        .padding(outlineOffset)
        .onFocusChanged { isFocused = it.isFocused }
}

@Composable
fun BoxScope.DurationBox(
    totalDuration: Long,
    currentPosition: Long,
    modifier: Modifier = Modifier,
) {
    val currentTime = convertMillisToReadableTime(currentPosition)
    val totalTime = convertMillisToReadableTime(totalDuration)

    Box(
        modifier = modifier
            .align(Alignment.BottomStart)
            .padding(20.dp)
    ) {
        Text(
            text = "$currentTime · $totalTime",
            color = Color.White,
            style = TextStyle(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(5.0f, 10.0f),
                    blurRadius = 3f,
                )
            )
        )
    }
}

fun convertMillisToReadableTime(millis: Long): ReadableTime {
    var availableSeconds = millis / 1000;
    val seconds = availableSeconds % 60
    availableSeconds -= seconds
    val minutes = availableSeconds / 60
    availableSeconds -= minutes * 60
    val hours = availableSeconds / (60 * 60)

    return ReadableTime(
        seconds = seconds,
        minutes = minutes,
        hours = hours
    )
}

class ReadableTime(val seconds: Long, val minutes: Long, val hours: Long) {
    override fun toString(): String {
        val time = listOf(hours, minutes, seconds)
        return time
            .joinToString(":") { it.toString().padStart(2, '0') }
    }
}

private lateinit var dataSourceFactory: DataSource.Factory

@Synchronized
fun getReadOnlyDataSourceFactory(context: Context): DataSource.Factory {
    val appContext = context.applicationContext
    val upstreamFactory = DefaultDataSource.Factory(
        appContext,
        getHttpDataSourceFactory(appContext)
    )
    dataSourceFactory = CacheDataSource.Factory()
        .setUpstreamDataSourceFactory(upstreamFactory)
        .setCacheWriteDataSinkFactory(null)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    return dataSourceFactory
}

@Synchronized
fun getHttpDataSourceFactory(context: Context): HttpDataSource.Factory {
        val httpDataSourceFactory = CronetDataSource.Factory(
            CronetEngine.Builder(context).build(),
            Executors.newSingleThreadExecutor()
        )
    return httpDataSourceFactory
}

@Synchronized
fun getDownloadDirectory(context: Context): File {
        val downloadDirectory = context.getExternalFilesDir(null) ?: context.filesDir
    return downloadDirectory
}