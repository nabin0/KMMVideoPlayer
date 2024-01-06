package com.github.nabin0.kmmvideoplayer.controller

import androidx.compose.foundation.layout.fillMaxSize
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
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.timeControlStatus
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGRect
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import platform.darwin.Float64
import platform.darwin.NSEC_PER_SEC
import platform.darwin.NSObject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

actual class VideoPlayerController {
    private var avPlayer: AVPlayer? = null

    actual val mediaDuration: MutableStateFlow<Long> = MutableStateFlow(0L)
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
        println("something observed")
        // isBuffering = avPlayer?.currentItem?.isPlaybackLikelyToKeepUp() != true
        isPlaying.value = avPlayer?.timeControlStatus == AVPlayerTimeControlStatusPlaying
        val rawTime: Float64 = CMTimeGetSeconds(time)
        val parsedTime = rawTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
        //currentTime = parsedTime
        if (avPlayer?.currentItem != null) {
            // val cmTime = CMTimeGetSeconds(avPlayer.currentItem!!.duration)
            // duration = if (cmTime.isNaN()) 0 else cmTime.toDuration(DurationUnit.SECONDS).inWholeSeconds
        }
    }


    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun BuildPlayer(onPlayerCreated: (player: Any) -> Unit) {
        val url =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
         avPlayer = remember { AVPlayer(uRL = NSURL.URLWithString(url)!!) }

       // avPlayer = AVPlayer()



        if (avPlayer != null) {
            println("avplayer is not null")
        }


    }


    @OptIn(ExperimentalForeignApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = avPlayer?.addPeriodicTimeObserverForInterval(interval, null, observer)!!
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = avPlayer?.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                // next()
            }
        )
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
        val avPlayerViewController = remember { AVPlayerViewController() }
        avPlayerViewController.player = avPlayer
        avPlayerViewController.showsPlaybackControls = false

         startTimeObserver()

        isPlaying.value = (avPlayer?.timeControlStatus() == AVPlayerTimeControlStatusPlaying)

        playerLayer.player = avPlayer
        // Use a UIKitView to integrate with your existing UIKit views
        UIKitView(
            factory = {
                // Create a UIView to hold the AVPlayerLayer
                val playerContainer = UIView()
                playerContainer.addSubview(avPlayerViewController.view)
                // Return the playerContainer as the root UIView
                playerContainer
            },
            onResize = { view: UIView, rect: CValue<CGRect> ->
                CATransaction.begin()
                CATransaction.setValue(true, kCATransactionDisableActions)
                view.layer.setFrame(rect)
                playerLayer.setFrame(rect)
                avPlayerViewController.view.layer.frame = rect
                CATransaction.commit()
            },
            update = { view ->
                avPlayer?.play()
                avPlayerViewController.player!!.play()
            },
            modifier = Modifier.fillMaxSize()
        )

    }

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    fun playerView() {
//        val url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
//        val url = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
//        val plr = AVPlayer(NSURL(string = url))
//        val ctrl = AVPlayerViewController()
//        ctrl.player = plr
//        plr.play()

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
        //isPlaying.value = true
        avPlayer?.play()
    }

    actual fun pause() {
        // isPlaying.value = false
        avPlayer?.pause()
    }

    actual fun seekTo(millis: Long) {
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
        return 1F
    }

    actual fun getCurrentVideoStreamingQuality(): VideoQuality {
        return VideoQuality(0, "144", 1, null)
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
