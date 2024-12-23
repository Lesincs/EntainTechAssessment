package com.lesincs.entaintechassessment.nextraces

import javax.inject.Inject
import kotlin.math.abs

internal class CountdownSecondsFormatter @Inject constructor() {

    fun format(countdownSeconds: Long): String {
        val minutes = abs(countdownSeconds) / SECONDS_PER_MINUTE
        val seconds = abs(countdownSeconds) % SECONDS_PER_MINUTE

        return buildString {
            val prefix = if (countdownSeconds >= 0) "" else "-"
            append(prefix)

            if (minutes != 0L) append("${minutes}m")

            if (minutes != 0L) {
                append(" ")
                val twoDigitsSeconds = seconds.toString().padStart(2, '0')
                append("${twoDigitsSeconds}s")
            } else {
                append("${seconds}s")
            }
        }
    }

    companion object {
        private const val SECONDS_PER_MINUTE = 60
    }
}
