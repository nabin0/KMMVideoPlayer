package com.github.nabin0.kmmvideoplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.github.nabin0.kmmvideoplayer.data.ClosedCaptionForTrackSelector
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.data.VideoQuality
import com.kmmvideoplayer.ObserverProtocol
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.coroutines.flow.MutableStateFlow
import platform.AVFoundation.AVMediaCharacteristicAudible
import platform.AVFoundation.AVMediaCharacteristicLegible
import platform.AVFoundation.AVMediaSelectionGroup
import platform.AVFoundation.AVMediaSelectionOption
import platform.AVFoundation.AVMediaTypeSubtitle
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.closedCaptionDisplayEnabled
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.isClosedCaptionDisplayEnabled
import platform.AVFoundation.isPlaybackLikelyToKeepUp
import platform.AVFoundation.mediaSelectionGroupForMediaCharacteristic
import platform.AVFoundation.mediaSelectionOptionsFromArray
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.playImmediatelyAtRate
import platform.AVFoundation.preferredPeakBitRate
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.selectMediaOption
import platform.AVFoundation.selectMediaOptionAutomaticallyInMediaSelectionGroup
import platform.AVFoundation.selectedMediaOptionInMediaSelectionGroup
import platform.AVFoundation.setDefaultRate
import platform.AVFoundation.setPreferredMaximumResolution
import platform.AVFoundation.setRate
import platform.AVFoundation.setVolume
import platform.AVFoundation.timeControlStatus
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGSize
import platform.CoreGraphics.CGSizeMake
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIInterfaceOrientationMaskLandscape
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowSceneGeometryPreferencesIOS
import platform.UIKit.accessibilityLanguage
import platform.darwin.NSEC_PER_SEC
import platform.darwin.NSObject

actual class VideoPlayerController {
    private var avPlayer: AVPlayer? = null

    private var playWhenReady: Boolean? = null

    private val videoList: MutableList<VideoItem> = mutableListOf()
    private var currentVideoItemIndex: Int? = null
    private var currentVideoItem: AVPlayerItem? = null

    private var currentSelectedVideoQuality =
        VideoQuality(index = -1, value = "Auto", resolutionKey = -1, height = null, width = null)

    private var currentSelectedCC: ClosedCaptionForTrackSelector = ClosedCaptionForTrackSelector(
        index = -1,
        language = "Off",
        name = null
    )

    //val url = "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
    //private val avPlayerItem = AVPlayerItem(uRL = NSURL.URLWithString(url)!!)

    actual val mediaDuration: MutableStateFlow<Long> = MutableStateFlow(1L)
    actual val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    actual val isBuffering: MutableStateFlow<Boolean> = MutableStateFlow(false)
    actual val listOfVideoResolutions: MutableStateFlow<List<VideoQuality>?> =
        MutableStateFlow(null)
    actual val listOfAudioFormats: MutableStateFlow<List<String>?> = MutableStateFlow(null)
    actual val listOfCC: MutableStateFlow<List<ClosedCaptionForTrackSelector>?> =
        MutableStateFlow(null)


    private lateinit var timeObserver: Any

    @OptIn(ExperimentalForeignApi::class)
    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        isBuffering.value = avPlayer?.currentItem?.isPlaybackLikelyToKeepUp() != true
        isPlaying.value = avPlayer?.timeControlStatus == AVPlayerTimeControlStatusPlaying
        /*
        val rawTime: Float64 = CMTimeGetSeconds(time)
         val parsedTime = rawTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
          currentTime = parsedTime
          if (avPlayer?.currentItem != null) {
              val cmTime = CMTimeGetSeconds(avPlayer.currentItem!!.duration)
              duration = if (cmTime.isNaN()) 0 else cmTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
          }
         */


//        for (track in avPlayerItem.tracks) {
//            val a = track as AVPlayerItemTrack
//            val asset = a.assetTrack
//            if (asset != null) {
//                if (asset.mediaType == AVMediaTypeVideo) {
//
//                }
//            }
//        }


    }

    init {
        setVideoQualityOptions()
    }

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun BuildPlayer(onPlayerCreated: (player: Any) -> Unit) {
        avPlayer = remember { AVPlayer() }
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = avPlayer?.addPeriodicTimeObserverForInterval(interval, null, observer)!!
//        NSNotificationCenter.defaultCenter.addObserverForName(
//            name = AVPlayerItemDidPlayToEndTimeNotification,
//            `object` = avPlayer?.currentItem,
//            queue = NSOperationQueue.mainQueue,
//            usingBlock = {
//                // next()
//            }
//        )
    }


    @OptIn(ExperimentalForeignApi::class)
    fun observeTimeControlStatus(player: AVPlayer, onChange: (Int) -> Unit) {
//        val valobserverContext = nativeHeap.alloc<COpaquePointerVar>().apply {
//            this.value = nativeHeap.alloc()
//        }
//        val observerContext = nativeHeap.alloc<COpaquePointerVar>()

//        player.observe("timeControlStatus", NSKeyValueObservingOptions(NSKeyValueObservingOptionNew), observerContext) { _, _, change ->
//            val newValue = change?.get(NSKeyValueChangeNewKey) as? Int
//            if (newValue != null) {
//                onChange(newValue)
//            }
//        }

        // You can return the observerContext if you need it for further cleanup
    }

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun PlayerView(
        useDefaultController: Boolean,
        modifier: Modifier
    ) {
        val playerLayer = remember { AVPlayerLayer() }
        var avPlayerViewController = remember { AVPlayerViewController() }
        avPlayerViewController.player = avPlayer
        avPlayerViewController.showsPlaybackControls = false

        startTimeObserver()

        isPlaying.value = (avPlayer?.timeControlStatus() == AVPlayerTimeControlStatusPlaying)

        playerLayer.player = avPlayer

//        var actualOrientation by remember {
//            mutableStateOf(
//                AVCaptureVideoOrientationPortrait
//            )
//        }
//        DisposableEffect(Unit) {
//            class OrientationListener : NSObject() {
//                @Suppress("UNUSED_PARAMETER")
//                @ObjCAction
//                fun orientationDidChange(arg: NSNotification) {
//                    // val cameraConnection = avPlayerViewController.interfaceOrientation
//                    if (avPlayerViewController != null) {
//                        actualOrientation = when (UIDevice.currentDevice.orientation) {
//                            UIDeviceOrientation.UIDeviceOrientationPortrait ->
//                                AVCaptureVideoOrientationPortrait
//
//                            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft ->
//                                AVCaptureVideoOrientationLandscapeRight
//
//                            UIDeviceOrientation.UIDeviceOrientationLandscapeRight ->
//                                AVCaptureVideoOrientationLandscapeLeft
//
//                            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown ->
//                                AVCaptureVideoOrientationPortrait
//
//                            else -> avPlayerViewController.interfaceOrientation
//                        }
//                        // avPlayerViewController.supportedInterfaceOrientations =
//                            UIInterfaceOrientationMaskAll
//                    }
////                    capturePhotoOutput.connectionWithMediaType(AVMediaTypeVideo)
////                        ?.videoOrientation = actualOrientation
//                }
//            }

//            val listener = OrientationListener()
//            val notificationName = platform.UIKit.UIDeviceOrientationDidChangeNotification
//            NSNotificationCenter.defaultCenter.addObserver(
//                observer = listener,
//                selector = NSSelectorFromString(
//                    OrientationListener::orientationDidChange.name + ":"
//                ),
//                name = notificationName,
//                `object` = null
//            )
//            onDispose {
//                NSNotificationCenter.defaultCenter.removeObserver(
//                    observer = listener,
//                    name = notificationName,
//                    `object` = null
//                )
//            }
//        }


        UIKitView(
            interactive = true,
            modifier = modifier,
            factory = {
                val playerContainer = UIView()
                playerContainer.addSubview(avPlayerViewController.view)
                if (playWhenReady != null && playWhenReady == true) {
                    avPlayer?.play()
                    avPlayerViewController.player!!.play()
                }
                avPlayer?.closedCaptionDisplayEnabled = false
                isBuffering.value = true
                //playerContainer.transform = CGAffineTransformMakeRotation(90.0)

                playerContainer
            },
            onResize = { view: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                // view.transform = CGAffineTransformMakeRotation(90.0)
                view.layer.setFrame(rect)
                playerLayer.setFrame(rect)
                avPlayerViewController.view.layer.frame = rect
                CATransaction.commit()
            },
            update = { view ->

            }
        )


    }

    @Composable
    actual fun EnablePortraitScreenMode() {


    }

    @Composable
    actual fun EnableLandscapeScreenMode() {
        // val windowScreen =
        // UIDevice.currentDevice.or
//        var currentOrientation: UIInterfaceOrientation = UIApplication.sharedApplication.statusBarOrientation
//        var value = UIInterfaceOrientationLandscapeLeft
//        UIDevice.currentDevice.setValue(value, forKey: "orientation")
        val win = UIViewController().view.window?.windowScene

        win?.let {
            //if (win.interfaceOrientation == UIInterfaceOrientationPortrait) {
            win.requestGeometryUpdateWithPreferences(
                UIWindowSceneGeometryPreferencesIOS(
                    UIInterfaceOrientationMaskLandscape
                )
            ) {
                println("error: $it")
            }
            //}
        }
    }

    @Composable
    actual fun HandleActivityLifecycleStageChanges() {
    }

    actual fun setMediaItem(videoItem: VideoItem) {
        val url = videoItem.videoUrl
        val avPlayerItem = AVPlayerItem(uRL = NSURL.URLWithString(url)!!)
        currentVideoItem = avPlayerItem
        avPlayer?.replaceCurrentItemWithPlayerItem(currentVideoItem)
    }

    actual fun prepare() {
    }

    actual fun play() {
        //isPlaying.value = true
        avPlayer?.play()
    }

    actual fun pause() {
        // isPlaying.value = false
        avPlayer?.pause()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun seekTo(millis: Long) {
        isBuffering.value = true
        val millsToSeconds = (millis / 1000).toDouble()
        avPlayer?.seekToTime(CMTimeMakeWithSeconds(millsToSeconds, NSEC_PER_SEC.toInt())) {
            isBuffering.value = false
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun currentPosition(): Long {
        val defaultCMTime = cValue<CMTime>()
        val currentTime = avPlayer?.currentTime() ?: defaultCMTime
        val currentTimInMillis = CMTimeGetSeconds(currentTime) * 1000

        // TODO: optimize this
        val durationInMillis =
            CMTimeGetSeconds(avPlayer?.currentItem?.duration() ?: defaultCMTime) * 1000
        mediaDuration.value = durationInMillis.toLong()

        //print("currentTime $currentTimInMillis duration $durationInMillis \n")
        isPlaying.value = (avPlayer?.timeControlStatus() == AVPlayerTimeControlStatusPlaying)
        // println("is playing${isPlaying.value}")

        return currentTimInMillis.toLong()
    }


    actual fun releasePlayer() {
    }

    actual fun stop() {
        if (::timeObserver.isInitialized) avPlayer?.removeTimeObserver(timeObserver)
    }

    actual fun playWhenReady(boolean: Boolean) {
        playWhenReady = boolean
    }

    actual fun addPlayList(listOfVideos: List<VideoItem>) {
    }

    actual fun setPlayList(listOfVideos: List<VideoItem>) {
        videoList.removeAll(videoList)
        videoList.addAll(listOfVideos)


        // TODO: add separate fun to play media by index
        if (videoList.isNotEmpty()) {
            replacePlayerItemWithNewItem(videoList[0])
            currentVideoItemIndex = 0
        }

    }


    actual fun playNextFromPlaylist() {
        currentVideoItemIndex?.let {
            if (it < videoList.size - 1) {
                val nextVideoIndex = it + 1
                currentVideoItemIndex = nextVideoIndex
                replacePlayerItemWithNewItem(videoList[nextVideoIndex])
            }
        }

    }

    actual fun playPreviousFromPlaylist() {
        currentVideoItemIndex?.let {
            if (it > 0) {
                val nextVideoIndex = it - 1
                currentVideoItemIndex = nextVideoIndex
                replacePlayerItemWithNewItem(videoList[nextVideoIndex])
            }
        }
    }

    private fun replacePlayerItemWithNewItem(videoItem: VideoItem) {
        isBuffering.value = true
        val url = videoItem.videoUrl
        val avPlayerItem = AVPlayerItem(uRL = NSURL.URLWithString(url)!!)
        currentVideoItem = avPlayerItem
        avPlayer?.replaceCurrentItemWithPlayerItem(avPlayerItem)
        currentSelectedCC = ClosedCaptionForTrackSelector(
            index = -1,
            language = "Off",
            name = null
        )
        getAvailableSubtitleAndAudioTracks(videoItem)
    }

    private var mediaSelectionGroupCC: AVMediaSelectionGroup? = null
    private var ccOptions: List<*>? = null
    private fun getAvailableSubtitleAndAudioTracks(videoItem: VideoItem) {
        setCCEnabled(true)
        try {
            val url = videoItem.videoUrl
            val hlsAsset = AVURLAsset(uRL = NSURL.URLWithString(url)!!, options = null)
            val mediaCharacteristicCC = AVMediaCharacteristicLegible

            mediaSelectionGroupCC =
                hlsAsset.mediaSelectionGroupForMediaCharacteristic(mediaCharacteristicCC)
            ccOptions = mediaSelectionGroupCC?.options

            val ccSelectorList = mutableListOf<ClosedCaptionForTrackSelector>()
            ccSelectorList.add(
                ClosedCaptionForTrackSelector(
                    index = -1,
                    language = "Off",
                    name = null
                )
            )
            if (ccOptions != null) {
                for (i in ccOptions!!.indices) {
                    val option = ccOptions!![i] as AVMediaSelectionOption
                    if (option.mediaType == "sbtl") {
                        if (!option.extendedLanguageTag.isNullOrBlank()) {
                            ccSelectorList.add(
                                ClosedCaptionForTrackSelector(
                                    index = i,
                                    language = option.extendedLanguageTag!!,
                                    name = option.displayName
                                )
                            )
                        }
                    }
                    mediaSelectionGroupCC?.let {
                        //currentVideoItem?.selectMediaOption(option, it)
                        println("option $option ${avPlayer?.isClosedCaptionDisplayEnabled()}")
                    }
                }
                listOfCC.value = ccSelectorList
            }

            val audioSelectorList = mutableListOf<String>()

            val mediaCharacteristicAudio = AVMediaCharacteristicAudible
            val mediaSelectionGroup =
                hlsAsset.mediaSelectionGroupForMediaCharacteristic(mediaCharacteristicAudio)
            val audioTrackOptions = mediaSelectionGroup?.options

            if (audioTrackOptions != null) {
                for (i in audioTrackOptions.indices) {
                    val option = audioTrackOptions[i] as AVMediaSelectionOption
                    option.extendedLanguageTag?.let { audioSelectorList.add(it) }
                }
                listOfAudioFormats.value = audioSelectorList
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private var currentPlaybackSpeed = 1f
    actual fun setPlaybackSpeed(selectedPlaybackSpeed: Float) {
        currentPlaybackSpeed = selectedPlaybackSpeed
        avPlayer?.setDefaultRate(selectedPlaybackSpeed)
        // avPlayer?.rate = (selectedPlaybackSpeed)
        avPlayer?.setRate(selectedPlaybackSpeed)
        avPlayer?.playImmediatelyAtRate(selectedPlaybackSpeed)
    }

    actual fun getCurrentPlaybackSpeed(): Float {
        return currentPlaybackSpeed
    }

    actual fun getCurrentVideoStreamingQuality(): VideoQuality {
        return currentSelectedVideoQuality
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun setSpecificVideoQuality(videoQuality: VideoQuality) {
        //avPlayerItem.setPreferredPeakBitRate(1356000.0)
        getCzSizeFromVideoQuality(videoQuality)?.let {
            currentVideoItem?.setPreferredMaximumResolution(it)
            currentSelectedVideoQuality = videoQuality
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getCzSizeFromVideoQuality(videoQuality: VideoQuality): CValue<CGSize>? {
        return if (videoQuality.width != null && videoQuality.height != null)
            CGSizeMake(width = videoQuality.width * 1.0, videoQuality.height * 1.0)
        else {
            currentVideoItem?.preferredPeakBitRate = 0.0
            currentSelectedVideoQuality = VideoQuality(
                index = -1,
                value = "Off",
                resolutionKey = -1,
                height = null,
                width = null
            )
            null
        }
    }


    actual fun setSpecificCC(cc: ClosedCaptionForTrackSelector) {
        try {
            if (cc.index == -1) {
                setCCEnabled(false)
                currentSelectedCC = cc
                return
            }
            setCCEnabled(true)

            if (ccOptions != null) {
                for (i in ccOptions!!.indices) {
                    val option = ccOptions!![i] as AVMediaSelectionOption
                    if (option.mediaType == "sbtl") {
                        if (!option.extendedLanguageTag.isNullOrBlank()) {
                            if (option.extendedLanguageTag == cc.language) {
                                mediaSelectionGroupCC?.let {
                                    currentVideoItem?.selectMediaOption(
                                        option,
                                        it
                                    )
                                }
                                currentSelectedCC = cc
                                println("optiona1 $option")
                                getAvailableSubtitleAndAudioTracks(videoList[currentVideoItemIndex!!])
                            }
                        }
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    actual fun getCurrentCC(): ClosedCaptionForTrackSelector {
        // https://developer.apple.com/videos/play/wwdc2020/10655/
        return currentSelectedCC
    }

    actual fun setCCEnabled(enabled: Boolean) {
        avPlayer?.closedCaptionDisplayEnabled = enabled
    }

    actual fun setVolumeLevel(volumeLevel: Float) {
        avPlayer?.setVolume(volumeLevel)
    }


    private fun setVideoQualityOptions() {
        // Try- getting value of available resolution from hls manifest
        val videoQualitySelectorOptions = listOf(
            VideoQuality(
                index = -1,
                value = "Auto",
                resolutionKey = -1,
                width = null,
                height = null
            ),
            VideoQuality(
                index = 0,
                value = "144 p",
                resolutionKey = -1,
                width = 144f,
                height = 256f
            ),
            VideoQuality(
                index = 1,
                value = "240 p",
                resolutionKey = -1,
                width = 240f,
                height = 426f
            ),
            VideoQuality(
                index = 2,
                value = "360 p",
                resolutionKey = -1,
                width = 360f,
                height = 640f
            ),
            VideoQuality(
                index = 3,
                value = "480 p",
                resolutionKey = -1,
                width = 480f,
                height = 854f
            ),
            VideoQuality(
                index = 4,
                value = "720 p",
                resolutionKey = -1,
                width = 720f,
                height = 1280f
            ),
            VideoQuality(
                index = 5,
                value = "1080 p",
                resolutionKey = -1,
                width = 1080f,
                height = 1920f
            ),
        )
        listOfVideoResolutions.value = videoQualitySelectorOptions
    }

}

@OptIn(ExperimentalForeignApi::class)
class VideoObserver : ObserverProtocol, NSObject() {
    override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: COpaquePointer?
    ) {
        println("keyPath $keyPath")
        println("ofObject $ofObject")
        println("change $change")
        println("context $context")
    }
}
