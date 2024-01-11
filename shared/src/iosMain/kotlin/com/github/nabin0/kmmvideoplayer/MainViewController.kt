package com.github.nabin0.kmmvideoplayer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.view.VideoPlayer

fun VideoViewController(videoItem: VideoItem?, listOfVideoUrls: List<VideoItem>?) = ComposeUIViewController {
     VideoPlayer(
          modifier = Modifier.fillMaxWidth(),
          videoItem = videoItem,
          videoPlayerController = VideoPlayerControllerFactory().createVideoPlayer(),
          listOfVideoUrls = listOfVideoUrls
     )
}
