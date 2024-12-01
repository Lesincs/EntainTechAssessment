package com.lesincs.entaintechassessment.data

import com.lesincs.entaintechassessment.data.model.RaceSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NextRacesLocalDataSource @Inject constructor() {
    private val nextRaceSummariesStateFlow: MutableStateFlow<List<RaceSummary>> = MutableStateFlow(
        emptyList()
    )

    fun updateNextRaces(raceSummaries: List<RaceSummary>) {
        nextRaceSummariesStateFlow.value = raceSummaries
    }

    fun getNextRacesFlow(): Flow<List<RaceSummary>> {
        return nextRaceSummariesStateFlow
    }
}
