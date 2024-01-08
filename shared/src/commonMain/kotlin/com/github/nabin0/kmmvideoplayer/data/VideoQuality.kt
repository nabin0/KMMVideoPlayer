package com.github.nabin0.kmmvideoplayer.data

data class VideoQuality(
    val index: Int,
    val value: String,
    val resolutionKey: Int,
    val bitrate: Double? = null,
    val dataConsumption: String? = null,
)
