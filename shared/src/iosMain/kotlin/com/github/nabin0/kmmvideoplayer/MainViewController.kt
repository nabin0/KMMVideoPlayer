package com.github.nabin0.kmmvideoplayer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerController
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.ClosedCaption
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.view.VideoPlayer
import com.github.nabin0.kmmvideoplayer.view.test

fun MainViewController() = ComposeUIViewController {
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
               videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",

          )
     )

     VideoPlayer(
          modifier = Modifier.fillMaxWidth(),
          videoItem = null,
          videoPlayerController = VideoPlayerControllerFactory().createVideoPlayer(),
          listOfVideoUrls = listOfVideoUrls
     )
}
