package com.github.nabin0.kmmvideoplayer.listeners

interface PlayerEventListener {
    fun onPlayNextMediaItemFromList(index: Int)

    fun onPlayPreviousMediaItemFromList(index: Int)
}