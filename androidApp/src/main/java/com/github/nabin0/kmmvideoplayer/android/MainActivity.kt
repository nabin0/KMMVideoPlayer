package com.github.nabin0.kmmvideoplayer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.ClosedCaption
import com.github.nabin0.kmmvideoplayer.data.VideoItem

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
            VideoItem(videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            VideoItem(videoUrl = "https://media.istockphoto.com/id/1172642711/video/sea-wave-on-the-beach.mp4?s=mp4-640x640-is&k=20&c=WGMko95H0KBhKVIe519DF6_xeZtu1CdhiUHmK0D7Wy4="),
            VideoItem(
                videoUrl = "https://cdn.bitmovin.com/content/assets/art-of-motion_drm/mpds/11331.mpd",
                licenseUrl = "https://cwip-shaka-proxy.appspot.com/no_auth"
            ),
            VideoItem(
                videoUrl = "https://hoichoihlsns.akamaized.net/vhoichoiindia2/Renditions/20190314/1552534964025_astey_ladies_ep_01_bengali/hls/1552534964025_astey_ladies_ep_01_bengali.m3u8?hdnts=exp=1703839751~acl=/vhoichoiindia2/Renditions/20190314/1552534964025_astey_ladies_ep_01_bengali/hls/*~hmac=1ca22a1d758267ca6e69d9d58500c4e662f14f85db0b1c0c950df894addc2d1f",
                listOfClosedCaptions = listOf(
                    ClosedCaption(
                        subtitleLink = "https://cchoichoi.viewlift.com/2019/03/1552555747726_astey_ladies_ep_01_subs_final.srt",
                        language = "en"
                    )
                )
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
                        videoItem = null,
                        videoPlayerController = videoPlayer,
                        listOfVideoUrls = listOfVideoUrls
                    )

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

