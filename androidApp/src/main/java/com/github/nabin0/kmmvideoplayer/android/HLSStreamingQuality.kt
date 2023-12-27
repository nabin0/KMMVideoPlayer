package com.github.nabin0.kmmvideoplayer.android

data class HLSStreamingQuality(
    val index: Int,
    val value: String,
    val resolutionKey: Int,
    val dataConsumption: String? = null
)