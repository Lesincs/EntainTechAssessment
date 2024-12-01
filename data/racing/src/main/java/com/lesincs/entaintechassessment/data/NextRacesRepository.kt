package com.lesincs.entaintechassessment.data

import com.lesincs.entaintechassessment.data.model.RaceSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NextRacesRepository @Inject constructor(
    private val localDataSource: NextRacesLocalDataSource,
    private val remoteDataSource: NextRacesRemoteDataSource
) {

    suspend fun getNextRaces(): Boolean {
        val raceSummaries = remoteDataSource.getNextRaceSummaries() ?: return false
        localDataSource.updateNextRaces(raceSummaries)
        return true
    }

    fun getNextRacesFlow(): Flow<List<RaceSummary>> {
        return localDataSource.getNextRacesFlow()
    }
}
