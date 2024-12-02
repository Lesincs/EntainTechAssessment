package com.lesincs.entaintechassessment.nextraces

import javax.inject.Inject

class CurrentTimeSecondsProvider @Inject constructor() {

    fun provide(): Long {
        return System.currentTimeMillis() / MILLISECONDS_PER_SECOND
    }

    private companion object {
        private const val MILLISECONDS_PER_SECOND = 1_000
    }
}
