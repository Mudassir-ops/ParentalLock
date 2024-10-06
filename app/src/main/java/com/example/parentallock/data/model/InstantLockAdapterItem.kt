package com.example.parentallock.data.model

sealed class InstantLockAdapterItem {

    data class NORMAL(
        val lottieRawRes: Int,
        val soundRawRes: Int,
        var isSelected: Boolean = false
    ) :
        InstantLockAdapterItem()

    data class CALL(
        val lottieRawRes: Int,
        val soundRawRes: Int,
        val labelOne: String,
        val labelTwo: String,
        val dpSrc: Int,
        var isSelected: Boolean = false
    ) : InstantLockAdapterItem()

}