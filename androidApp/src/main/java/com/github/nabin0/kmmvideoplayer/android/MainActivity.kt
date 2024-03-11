package com.github.nabin0.kmmvideoplayer.android

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.nabin0.kmmvideoplayer.android.Utils.listOfVideoUrls
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerController
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.ClosedCaption
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.listeners.PlayerEventListener
import com.github.nabin0.kmmvideoplayer.view.VideoPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyApplicationTheme {

                Surface {

                    val a = rememberNavController()
                    NavHost(navController = a, startDestination = "home") {
                        composable(route = "home") {
                            TestRecomposition()
                             MainScreen()
//                            Button(onClick = {
//                                a.navigate("page1")
//                            }) {
//                                Text(text = "click me")
//                            }
                        }

                        composable(route = "page1") {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TestRecomposition() {
    Log.d("TAG", "TestRecomposition: called")

    val player = U.playerControlelr

    Log.d("TAG", "Player : $player ")

    val currentIndex by player.currentPlayingMediaIndex.collectAsState()
//    LaunchedEffect(key1 = currentIndex, block = {
//        Log.d("TAG", "Launcheffect $currentIndex")
//    })

    val listOfVideos= remember {
        listOfVideoUrls
    }


    comp1(listOfVideos)
    Button(onClick = { player.playNextFromPlaylist() }) {
        Text(text = "clickme")
    }

}

@Composable
fun comp1(listOfVideos: List<VideoItem>) {
    Log.d("TAG", "comp1: called ${listOfVideos}")
    Text(text = "hello compose")
    VideoPlayer(
        videoItem = null,
        videoPlayerController = U.playerControlelr,
        listOfVideoUrls = listOfVideos,
        currentVideoItemIndexInList = 0,
        playerEventListener = object : PlayerEventListener {
            override fun onPlayNextMediaItemFromList(index: Int) {

            }

            override fun onPlayPreviousMediaItemFromList(index: Int) {

            }


        }
    )
}

object U {
    @Composable
    fun GetPlayer(listOfVideos: List<VideoItem>, videoPlayerController: VideoPlayerController) {
        Log.d("TAG", "GetPlayer: called")
        VideoPlayer(
            videoItem = null,
            videoPlayerController = videoPlayerController,
            listOfVideoUrls = listOfVideos,
            currentVideoItemIndexInList = 0,
            playerEventListener = object : PlayerEventListener {
                override fun onPlayNextMediaItemFromList(index: Int) {

                }

                override fun onPlayPreviousMediaItemFromList(index: Int) {

                }


            }
        )

    }

    val playerControlelr = VideoPlayerControllerFactory().createVideoPlayer()

}

@Composable
fun MainScreen() {
    val videoPlayer =
        remember { VideoPlayerControllerFactory().createVideoPlayer() }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        VideoPlayer(
            modifier = Modifier.fillMaxWidth(),
            videoItem = null,
            videoPlayerController = videoPlayer,
            listOfVideoUrls = listOfVideoUrls,
            startPlayMuted = false,
            setCCEnabled = true,
            currentVideoItemIndexInList = 1,
            playerEventListener = null
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
