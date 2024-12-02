package com.lesincs.entaintechassessment

import app.cash.turbine.test
import com.lesincs.entaintechassessment.data.NextRacesRepository
import com.lesincs.entaintechassessment.data.model.RaceSummary
import com.lesincs.entaintechassessment.nextraces.CountdownSecondsFormatter
import com.lesincs.entaintechassessment.nextraces.CurrentTimeSecondsProvider
import com.lesincs.entaintechassessment.nextraces.NextRacesVieModel
import com.lesincs.entaintechassessment.nextraces.RaceSummaryUiItem
import com.lesincs.entaintechassessment.nextraces.RacesListState
import com.lesincs.entaintechassessment.util.MainDispatcherRule
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NextRacesVieModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockCurrentTimeSecondsProvider: CurrentTimeSecondsProvider = mockk()
    private val mockNextRacesRepository: NextRacesRepository = mockk()
    private val mockCountdownSecondsFormatter: CountdownSecondsFormatter = mockk()
    private val syncCurrentTimeSecondsDispatcher: TestDispatcher = StandardTestDispatcher()
    private val updateNextRacesDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var sut: NextRacesVieModel

    @Test
    fun `should map races to ui state properly`() =
        runTest {
            coEvery {
                mockNextRacesRepository.getNextRacesFlow()
            } returns flowOf(
                listOf(
                    RaceSummary(
                        raceId = "e91170b7-ae24-46c1-9b8e-f1d001ecd567",
                        raceName = "Happy Hire Cromwell Cup",
                        raceNumber = 6,
                        meetingId = "044319ac-f363-4f5e-8dd3-3683102fb707",
                        meetingName = "Cromwell",
                        categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
                        advertisedStartSeconds = 1,
                    ),
                    RaceSummary(
                        raceId = "fbadd808-430d-4e5b-9734-da07665cc0f6",
                        raceName = "Race 6 - 1609M",
                        raceNumber = 9,
                        meetingId = "e8de4f94-9495-4cfa-ab73-0f720044d2fa",
                        meetingName = "Woodbine Mohawk Park",
                        categoryId = "161d9be2-e909-4326-8c2c-35ed71fb460b",
                        advertisedStartSeconds = 50,
                    ),
                )
            )
            coEvery { mockNextRacesRepository.getNextRaces() } returns true
            every {
                mockCurrentTimeSecondsProvider.provide()
            } returns 25
            every { mockCountdownSecondsFormatter.format(-24) } returns "-24s"
            every { mockCountdownSecondsFormatter.format(25) } returns "25s"

            constructSut()

            sut.nextRacesUiStateFlow.test {
                val racesListState = awaitItem().racesListState
                racesListState.shouldBeInstanceOf<RacesListState.Success>()
                racesListState.races shouldBe listOf(
                    RaceSummaryUiItem(
                        raceId = "e91170b7-ae24-46c1-9b8e-f1d001ecd567",
                        raceName = "Happy Hire Cromwell Cup",
                        raceNumber = "R6",
                        meetingName = "Cromwell",
                        countdownTime = "-24s"
                    ),
                    RaceSummaryUiItem(
                        raceId = "fbadd808-430d-4e5b-9734-da07665cc0f6",
                        raceName = "Race 6 - 1609M",
                        raceNumber = "R9",
                        meetingName = "Woodbine Mohawk Park",
                        countdownTime = "25s"
                    )
                )
            }
        }

    @Test
    fun `should races be time-ordered by advertised-start ascending`() =
        runTest {
            coEvery {
                mockNextRacesRepository.getNextRacesFlow()
            } returns flowOf(
                listOf(
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019302, raceId = "004"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019301, raceId = "003"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019301, raceId = "002"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019300, raceId = "001"),
                )
            )
            coEvery { mockNextRacesRepository.getNextRaces() } returns true
            every { mockCurrentTimeSecondsProvider.provide() } returns 1000
            every { mockCountdownSecondsFormatter.format(any()) } answers { firstArg<Long>().toString() }

            constructSut()

            sut.nextRacesUiStateFlow.test {
                val racesListState = awaitItem().racesListState
                racesListState.shouldBeInstanceOf<RacesListState.Success>()
                racesListState.races.map { it.raceId } shouldBe listOf("001", "003", "002", "004")
            }
        }

    @Test
    fun `should only show next 5 races`() =
        runTest {
            coEvery {
                mockNextRacesRepository.getNextRacesFlow()
            } returns flowOf(
                listOf(
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019305, raceId = "006"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019306, raceId = "007"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019307, raceId = "008"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019308, raceId = "009"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019309, raceId = "010"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019300, raceId = "001"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019301, raceId = "002"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019302, raceId = "003"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019303, raceId = "004"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 1733019304, raceId = "005"),
                )
            )
            coEvery { mockNextRacesRepository.getNextRaces() } returns true
            every { mockCurrentTimeSecondsProvider.provide() } returns 1000
            every { mockCountdownSecondsFormatter.format(any()) } answers { firstArg<Long>().toString() }

            constructSut()

            sut.nextRacesUiStateFlow.test {
                val racesListState = awaitItem().racesListState
                racesListState.shouldBeInstanceOf<RacesListState.Success>()
                racesListState.races.map(RaceSummaryUiItem::raceId) shouldBe listOf(
                    "001",
                    "002",
                    "003",
                    "004",
                    "005"
                )
            }
        }

    @Test
    fun `should not show races that are one minute past the advertised start`() =
        runTest {
            coEvery {
                mockNextRacesRepository.getNextRacesFlow()
            } returns flowOf(
                listOf(
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 100, raceId = "006"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 120, raceId = "007"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 140, raceId = "008"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 160, raceId = "009"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 180, raceId = "010"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 20, raceId = "001"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 30, raceId = "002"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 40, raceId = "003"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 50, raceId = "004"),
                    BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 60, raceId = "005"),
                )
            )
            coEvery { mockNextRacesRepository.getNextRaces() } returns true
            every { mockCurrentTimeSecondsProvider.provide() } returns 100
            every { mockCountdownSecondsFormatter.format(any()) } answers { firstArg<Long>().toString() }

            constructSut()

            sut.nextRacesUiStateFlow.test {
                val racesListState = awaitItem().racesListState
                racesListState.shouldBeInstanceOf<RacesListState.Success>()
                racesListState.races.map { it.raceId } shouldBe listOf(
                    "003",
                    "004",
                    "005",
                    "006",
                    "007"
                )
            }
        }

    @Test
    @Suppress("LongMethod")
    fun `should display races belong to categories that user selects`() =
        runTest {
            coEvery {
                mockNextRacesRepository.getNextRacesFlow()
            } returns flowOf(
                listOf(
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 20,
                        raceId = "001", categoryId = "id-Horse"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 30,
                        raceId = "002", categoryId = "other"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 40,
                        raceId = "003", categoryId = "id-Harness"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 50,
                        raceId = "004", categoryId = "other"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 60,
                        raceId = "005",
                        categoryId = "other"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 100,
                        raceId = "006",
                        categoryId = "id-GreyHound"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 120,
                        raceId = "007",
                        categoryId = "id-Horse"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 140,
                        raceId = "008",
                        categoryId = "id-Horse"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 160,
                        raceId = "009",
                        categoryId = "id-Horse"
                    ),
                    BASE_RACE_SUMMARY.copy(
                        advertisedStartSeconds = 180,
                        raceId = "010",
                        categoryId = "id-Horse"
                    ),
                )
            )
            coEvery { mockNextRacesRepository.getNextRaces() } returns true
            every { mockCurrentTimeSecondsProvider.provide() } returns 100
            every { mockCountdownSecondsFormatter.format(any()) } answers { firstArg<Long>().toString() }
            constructSut()

            sut.selectCategories(listOf("id-Horse", "id-GreyHound", "id-Harness"))

            sut.nextRacesUiStateFlow.test {
                val racesListState = awaitItem().racesListState
                racesListState.shouldBeInstanceOf<RacesListState.Success>()
                racesListState.races.map { it.raceId } shouldBe listOf(
                    "003",
                    "006",
                    "007",
                    "008",
                    "009",
                    "010"
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should update current time seconds every second`() = runTest {
        coEvery {
            mockNextRacesRepository.getNextRacesFlow()
        } returns flowOf(
            listOf(BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 100))
        )
        coEvery { mockNextRacesRepository.getNextRaces() } returns true
        every {
            mockCurrentTimeSecondsProvider.provide()
        } returnsMany listOf(120, 121)
        every { mockCountdownSecondsFormatter.format(-20) } returns "-20s"
        every { mockCountdownSecondsFormatter.format(-21) } returns "-21s"
        constructSut()

        syncCurrentTimeSecondsDispatcher.scheduler.advanceTimeBy(1001)

        sut.nextRacesUiStateFlow.test {
            val racesListState = awaitItem().racesListState
            racesListState.shouldBeInstanceOf<RacesListState.Success>()
            racesListState.races.first().countdownTime shouldBe "-21s"
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should update next races after 1 minutes since races has been loaded`() = runTest {
        coEvery {
            mockNextRacesRepository.getNextRacesFlow()
        } returns flowOf(
            listOf(BASE_RACE_SUMMARY.copy(advertisedStartSeconds = 100))
        )
        coEvery { mockNextRacesRepository.getNextRaces() } returns true
        every {
            mockCurrentTimeSecondsProvider.provide()
        } returnsMany listOf(120, 121)
        every { mockCountdownSecondsFormatter.format(-20) } returns "-20s"
        every { mockCountdownSecondsFormatter.format(-21) } returns "-21s"
        constructSut()
        coVerify(exactly = 1) { mockNextRacesRepository.getNextRaces() }

        updateNextRacesDispatcher.scheduler.advanceTimeBy(60_001)
        coVerify(exactly = 2) { mockNextRacesRepository.getNextRaces() }
    }

    private fun constructSut() {
        sut = NextRacesVieModel(
            nextRacesRepository = mockNextRacesRepository,
            currentTimeSecondsProvider = mockCurrentTimeSecondsProvider,
            countdownSecondsFormatter = mockCountdownSecondsFormatter,
            syncCurrentTimeSecondsDispatcher = syncCurrentTimeSecondsDispatcher,
            updateNextRacesDispatcher = updateNextRacesDispatcher,
        )
    }

    companion object {
        private val BASE_RACE_SUMMARY = RaceSummary(
            raceId = "e91170b7-ae24-46c1-9b8e-f1d001ecd567",
            raceName = "Happy Hire Cromwell Cup",
            raceNumber = 6,
            meetingId = "044319ac-f363-4f5e-8dd3-3683102fb707",
            meetingName = "Cromwell",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStartSeconds = 1733019301,
        )
    }
}
