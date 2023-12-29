package com.github.nabin0.kmmvideoplayer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.VideoItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listOfVideoUrls = listOf(
            VideoItem(videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"),
            VideoItem(videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            VideoItem(videoUrl = "https://media.istockphoto.com/id/1172642711/video/sea-wave-on-the-beach.mp4?s=mp4-640x640-is&k=20&c=WGMko95H0KBhKVIe519DF6_xeZtu1CdhiUHmK0D7Wy4="),
            VideoItem(
                videoUrl = "https://cdn.bitmovin.com/content/assets/art-of-motion_drm/mpds/11331.mpd",
                licenseUrl = "https://cwip-shaka-proxy.appspot.com/no_auth"
            )
        )
        setContent {
            MyApplicationTheme {
                val videoPlayer = VideoPlayerControllerFactory().createVideoPlayer()
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    com.github.nabin0.kmmvideoplayer.view.VideoPlayer(
                        modifier = Modifier.fillMaxWidth(),
                        videoUrl = null,
                        videoPlayerController = videoPlayer,
                        listOfVideoUrls = listOfVideoUrls
                    )

//                    VideoPlayer()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}


object VideoPlayerProvider {
    @Composable
    fun getPlayer() = VideoPlayer()
}

