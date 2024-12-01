package com.lesincs.entaintechassessment.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NextRacesResponse(
    val status: Int,
    val data: RaceDataDTO,
    val message: String
)

@Serializable
internal data class RaceDataDTO(
    @SerialName("race_summaries") val raceSummaries: Map<String, RaceSummaryDTO>
)

@Serializable
internal data class RaceSummaryDTO(
    @SerialName("race_id") val raceId: String,
    @SerialName("race_name") val raceName: String,
    @SerialName("race_number") val raceNumber: Int,
    @SerialName("meeting_id") val meetingId: String,
    @SerialName("meeting_name") val meetingName: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("advertised_start") val advertisedStart: AdvertisedStartDTO,
)

@Serializable
internal data class AdvertisedStartDTO(
    val seconds: Long
)
