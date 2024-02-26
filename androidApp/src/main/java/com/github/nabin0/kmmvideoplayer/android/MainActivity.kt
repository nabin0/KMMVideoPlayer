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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.nabin0.kmmvideoplayer.android.Utils.listOfVideoUrls
import com.github.nabin0.kmmvideoplayer.controller.VideoPlayerControllerFactory
import com.github.nabin0.kmmvideoplayer.data.ClosedCaption
import com.github.nabin0.kmmvideoplayer.data.VideoItem
import com.github.nabin0.kmmvideoplayer.view.VideoPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyApplicationTheme {

                Surface {

                    val a = rememberNavController()
                    NavHost(navController = a, startDestination = "home"){
                        composable(route = "home"){
                            MainScreen()
//                            Button(onClick = {
//                                a.navigate("page1")
//                            }) {
//                                Text(text = "click me")
//                            }
                        }

                        composable(route = "page1"){
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
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
            currentVideoItemIndexInList = 1
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
