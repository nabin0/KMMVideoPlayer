package com.github.nabin0.kmmvideoplayer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nabin0.kmmvideoplayer.VideoPlayerControllerFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val videoPlayer = VideoPlayerControllerFactory().createVideoPlayer()
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    com.github.nabin0.kmmvideoplayer.VideoPlayer(
                        modifier = Modifier.fillMaxWidth(),
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        videoPlayerController = videoPlayer
                    )
                    Text(text = "VideoPlayer View", fontSize = 25.sp, textAlign = TextAlign.Center, color = Color.White)

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

