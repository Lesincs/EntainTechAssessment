package com.lesincs.entaintechassessment.data

import com.lesincs.entaintechassessment.data.model.NextRacesResponse
import com.lesincs.entaintechassessment.data.model.RaceSummary
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import timber.log.Timber
import javax.inject.Inject

class NextRacesRemoteDataSource @Inject constructor(
    private val nedsHttpClient: HttpClient,
) {
    suspend fun getNextRaceSummaries(): List<RaceSummary>? {
        val result: Result<NextRacesResponse> = runCatching {
            nedsHttpClient.get("racing/?method=nextraces&count=10").body()
        }

        if (result.isFailure) {
            Timber.e("Failed to get nextraces: ${result.exceptionOrNull()?.message}")
            return null
        }

        val nextRacesResponse = requireNotNull(result.getOrNull())
        val raceSummaries = nextRacesResponse.data.raceSummaries.values.map { raceSummaryDTO ->
            RaceSummary(
                raceId = raceSummaryDTO.raceId,
                raceName = raceSummaryDTO.raceName,
                raceNumber = raceSummaryDTO.raceNumber,
                meetingId = raceSummaryDTO.meetingId,
                meetingName = raceSummaryDTO.meetingName,
                categoryId = raceSummaryDTO.categoryId,
                advertisedStartSeconds = raceSummaryDTO.advertisedStart.seconds,
            )
        }
        return raceSummaries
    }
}
