package com.lesincs.entaintechassessment.data

import com.lesincs.entaintechassessment.data.model.RaceSummary
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NextRacesRepositoryTest {
    private val mockLocalDataSource: NextRacesLocalDataSource = mockk()
    private val mockRemoteDataSource: NextRacesRemoteDataSource = mockk()
    private val sut: NextRacesRepository =
        NextRacesRepository(mockLocalDataSource, mockRemoteDataSource)

    @Test
    fun `should fetch the next race from remote and save it locally and return true if successful`() =
        runTest {
            val raceSummaries: List<RaceSummary> = mockk()
            coEvery { mockRemoteDataSource.getNextRaceSummaries() } returns raceSummaries
            coEvery { mockLocalDataSource.updateNextRaces(raceSummaries) } just runs

            val success = sut.getNextRaces()

            success shouldBe true
            coVerifyOrder {
                mockRemoteDataSource.getNextRaceSummaries()
                mockLocalDataSource.updateNextRaces(raceSummaries)
            }
        }

    @Test
    fun `should return false given fetch the next race from remote failed`() = runTest {
        coEvery { mockRemoteDataSource.getNextRaceSummaries() } returns null

        val success = sut.getNextRaces()

        success shouldBe false
        coVerify { mockRemoteDataSource.getNextRaceSummaries() }
        coVerify(exactly = 0) { mockLocalDataSource.updateNextRaces(any()) }
    }

    @Test
    fun `should return nextRacesFlow from local datasource`() {
        val nextRacesFlow: Flow<List<RaceSummary>> = mockk()
        every { mockLocalDataSource.getNextRacesFlow() } returns nextRacesFlow

        sut.getNextRacesFlow() shouldBe nextRacesFlow
    }
}
