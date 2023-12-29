package com.github.nabin0.kmmvideoplayer.data

data class VideoItem(
    val videoUrl: String,
    val title: String? = "Unknown",
    val videoDescription: String? = "Unknown",
    val licenseUrl: String? = null,
    val listOfClosedCaptions: List<ClosedCaption>? = null,
)
