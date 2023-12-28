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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listOfVideoUrls = listOf(
            "https://media.istockphoto.com/id/1134268569/video/cloud-computing-technology-tech-data-storage.mp4?s=mp4-640x640-is&k=20&c=-8suq36WekEolzTTZn6FsLXdCOTNlcXB8OfMdcFWUCA=",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://media.istockphoto.com/id/1172642711/video/sea-wave-on-the-beach.mp4?s=mp4-640x640-is&k=20&c=WGMko95H0KBhKVIe519DF6_xeZtu1CdhiUHmK0D7Wy4=")
        setContent {
            MyApplicationTheme {
                val videoPlayer = VideoPlayerControllerFactory().createVideoPlayer()
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
//                    com.github.nabin0.kmmvideoplayer.view.VideoPlayer(
//                        modifier = Modifier.fillMaxWidth(),
//                        videoUrl = null,
//                        videoPlayerController = videoPlayer,
//                        listOfVideoUrls = listOfVideoUrls
//                    )

                    VideoPlayer()
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

