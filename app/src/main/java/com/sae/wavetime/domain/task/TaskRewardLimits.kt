package com.sae.wavetime.domain.task

object TaskRewardLimits {

    private val coinLimits = intArrayOf(
        10,
        20,
        40,
        80,
        160,
        320,
        640,
        1280
    )

    private val experienceLimits = intArrayOf(
        5,
        10,
        20,
        40,
        80,
        160,
        320,
        640
    )

    fun getMaxCoin(difficulty: Int): Int {
        val index = (difficulty - 1)
            .coerceIn(coinLimits.indices)

        return coinLimits[index]
    }

    fun getMaxExperience(difficulty: Int): Int {
        val index = (difficulty - 1)
            .coerceIn(experienceLimits.indices)

        return experienceLimits[index]
    }
}