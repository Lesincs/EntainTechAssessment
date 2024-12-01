package com.lesincs.entaintechassessment.data.model

data class RaceSummary(
    val raceId: String,
    val raceName: String,
    val raceNumber: Int,
    val meetingId: String,
    val meetingName: String,
    val categoryId: String,
    val advertisedStartSeconds: Long,
)
