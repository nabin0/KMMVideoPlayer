package com.github.nabin0.kmmvideoplayer.android

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.ClosedCaption
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.view.VideoPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listOfVideoUrls = listOf(
            VideoItem(
                videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
                listOfClosedCaptions = listOf(
                    ClosedCaption(
                        subtitleLink = "https://cchoichoi.viewlift.com/2019/03/1552555747726_astey_ladies_ep_01_subs_final.srt",
                        language = "english"
                    )
                )
            ),
            VideoItem(videoUrl = "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
            VideoItem(videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            VideoItem(videoUrl = "https://media.istockphoto.com/id/1172642711/video/sea-wave-on-the-beach.mp4?s=mp4-640x640-is&k=20&c=WGMko95H0KBhKVIe519DF6_xeZtu1CdhiUHmK0D7Wy4="),
            VideoItem(
                videoUrl = "https://cdn.bitmovin.com/content/assets/art-of-motion_drm/mpds/11331.mpd",
                licenseUrl = "https://cwip-shaka-proxy.appspot.com/no_auth"
            ),
            VideoItem(
                videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
            )
        )
        setContent {
            MyApplicationTheme {

                Surface {
                    val videoPlayer =
                        remember { VideoPlayerControllerFactory().createVideoPlayer() }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        VideoPlayer(
                            modifier = Modifier.fillMaxWidth(),
                            videoItem = null,
                            videoPlayerController = videoPlayer,
                            listOfVideoUrls = listOfVideoUrls
                        )

                        Text(
                            text = "Control Video From App Level Code",
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            style = TextStyle(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.padding(top = 25.dp)
                        )

                        Row {
                            Button(onClick = {
                                videoPlayer.play()
                            }) {
                                Text(text = "play")
                            }
                            Button(onClick = {
                                videoPlayer.pause()
                            }) {
                                Text(text = "pause")
                            }

                        }

                    }
                }
            }
        }
    }
}
