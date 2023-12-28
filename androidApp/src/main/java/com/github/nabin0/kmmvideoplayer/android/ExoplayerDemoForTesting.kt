package com.github.nabin0.kmmvideoplayer.android

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mediaItemBuilder = MediaItem.Builder()


//    val type =
//        Util.inferContentType(Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"))
//    when (type) {
//        C.CONTENT_TYPE_SS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_SS)
//        C.CONTENT_TYPE_DASH -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
//        C.CONTENT_TYPE_HLS -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
//        C.CONTENT_TYPE_OTHER -> mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MP4)
//        else -> {}
//    }


    val drmMediaItem = MediaItem.Builder()
        .setUri("https://cdn.bitmovin.com/content/assets/art-of-motion_drm/mpds/11331.mpd")
        .setMimeType(MimeTypes.APPLICATION_MPD).setDrmConfiguration(
            MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri("https://cwip-shaka-proxy.appspot.com/no_auth").build()
        ).setMediaMetadata(
            MediaMetadata.Builder().setTitle("Clear(WebM, VP9)").setDescription(
                "Tears of Steel is a short film by producer Ton Roosendaal and director Ian Hubert. " + "It was made using new enhancements to the visual effects capabilities of Blender, a free and open source software application for animation. " + "The film was funded by the Blender Foundation, donations from the Blender community, pre-sales of the film's DVD and commercial sponsorship."
            )
                .setArtworkUri(Uri.parse("https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"))
                .build()
        ).setTag("tag").build()


    val listofurls = listOf(
        "http://playertest.longtailvideo.com/adaptive/wowzaid3/playlist.m3u8",
        "https://cdn.bitmovin.com/content/assets/art-of-motion_drm/m3u8s/11331.m3u8",
        "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
    )

    val hlsMediaItem = MediaItem.Builder()
        .setUri(listofurls[2])
        .setMimeType(MimeTypes.APPLICATION_M3U8).setMediaMetadata(
            MediaMetadata.Builder().setTitle("Animation Movie").setDescription(
                "Sintel is an independently produced short film, initiated by the Blender Foundation as a means to further improve and validate the free,open source 3D creation suite Blender. " + "With initial funding provided by 1000s of donations via the internet community, it has again proven to be a viable development model for both open 3D technology as for independent animation film."
            )
                .setArtworkUri(Uri.parse("https://images.unsplash.com/photo-1563089145-599997674d42?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"))
                .build()
        ).setTag("tagg").build()


    var audioTrackGroup = Any()
    var videoTrackGroup = Any()

    val listOfVideoQualtites = remember { mutableStateListOf<Format>() }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            setMediaItem(hlsMediaItem)
            prepare()
            this.addListener(object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    Log.d("TAG", "onTracksChanged: called $tracks")

                    for (trackGroup in tracks.groups) {
                        // Group level information.
                        val trackType = trackGroup.type
                        val trackInGroupIsSelected = trackGroup.isSelected
                        val trackInGroupIsSupported = trackGroup.isSupported
                        Log.d("TAG", "track type $trackType")

                        if (trackType == C.TRACK_TYPE_VIDEO) {
                            videoTrackGroup = trackGroup
                            listOfVideoQualtites.removeAll(listOfVideoQualtites)
                            for (i in 0 until trackGroup.length) {
                                val trackFormat = trackGroup.getTrackFormat(i)
                                listOfVideoQualtites.add(trackFormat)
                            }

                            Log.d("TAG", "this is track type video: ")
                        }
                        if (trackType == C.TRACK_TYPE_AUDIO) {
                            audioTrackGroup = trackGroup
                            Log.d("TAG", "this is track type audio: ")
                        }
                        if (trackType == C.TRACK_TYPE_TEXT) {
                            Log.d("TAG", "this is track type text: ")
                        }

                        for (i in 0 until trackGroup.length) {
                            // Individual track information.
                            val isSupported = trackGroup.isTrackSupported(i)
                            val isSelected = trackGroup.isTrackSelected(i)
                            val trackFormat = trackGroup.getTrackFormat(i)
                            Log.d(
                                "TAG",
                                "track format: $trackFormat/n issuppprted $isSupported, isselecte = $isSelected"
                            )
                        }
                    }
                }
            })
        }
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            AndroidView(factory = {
                PlayerView(it).apply {
                    player = exoPlayer
//                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
//                setShowSubtitleButton(true)
//                controllerAutoShow = false
                }
            }, modifier = Modifier.height(300.dp)) {}
        }
        item {
            Button(onClick = {
                Log.d("TAG", "VideoPlayer: ${listOfVideoQualtites.size}")
            }) {
                Text(text = "djaljl")
            }
        }

        itemsIndexed(items = listOfVideoQualtites) { index: Int, item: Format ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
            ) {

                Text(text = "width ${item.width} x height ${item.height} ",
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            exoPlayer.trackSelectionParameters =
                                exoPlayer.trackSelectionParameters
                                    .buildUpon()
                                    .setOverrideForType(
                                        TrackSelectionOverride(
                                            (videoTrackGroup as Tracks.Group).mediaTrackGroup,
                                            index
                                        )
                                    )
                                    .build()
                        })
            }
        }
    }

}
//}