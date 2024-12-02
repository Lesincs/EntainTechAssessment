package com.lesincs.entaintechassessment.nextraces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lesincs.entaintechassessment.data.NextRacesRepository
import com.lesincs.entaintechassessment.data.model.RaceSummary
import com.lesincs.entaintechassessment.di.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class NextRacesVieModel @Inject constructor(
    private val nextRacesRepository: NextRacesRepository,
    private val currentTimeSecondsProvider: CurrentTimeSecondsProvider,
    private val countdownSecondsFormatter: CountdownSecondsFormatter,
    @MainDispatcher private val syncCurrentTimeSecondsDispatcher: CoroutineDispatcher,
    @MainDispatcher private val updateNextRacesDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val nextRacesLoadingStatusStateFlow: MutableStateFlow<LoadingStatus> =
        MutableStateFlow(LoadingStatus.LOADING)
    private val currentTimeSecondsStateFlow: MutableStateFlow<Long> =
        MutableStateFlow(currentTimeSecondsProvider.provide())
    private val selectedCategoryIdsStateFlow: MutableStateFlow<List<String>> = MutableStateFlow(
        emptyList()
    )

    init {
        loadNextRaces()
        syncCurrentTimeSecondsRegularly()
        refreshNextRacesAutomatically()
    }

    fun loadNextRaces() {
        nextRacesLoadingStatusStateFlow.value = LoadingStatus.LOADING

        viewModelScope.launch {
            val succeed = nextRacesRepository.getNextRaces()
            nextRacesLoadingStatusStateFlow.value =
                if (succeed) LoadingStatus.SUCCESS else LoadingStatus.FAILED
        }
    }

    private fun syncCurrentTimeSecondsRegularly() {
        viewModelScope.launch(syncCurrentTimeSecondsDispatcher) {
            while (isActive) {
                delay(CURRENT_TIME_SYNC_DELAY_MILLIS)
                currentTimeSecondsStateFlow.value = currentTimeSecondsProvider.provide()
            }
        }
    }

    private fun refreshNextRacesAutomatically() {
        viewModelScope.launch(updateNextRacesDispatcher) {
            // Update next races only if loaded and a minute has passed.
            nextRacesUiStateFlow
                .map { it.raceListState is RaceListState.Success }
                .distinctUntilChanged()
                .collectLatest { loadedRacesSucceed ->
                    if (loadedRacesSucceed) {
                        Timber.d("Races will be automatically refreshed after $NEXT_RACES_REFRESH_DELAY_MILLIS")
                        delay(NEXT_RACES_REFRESH_DELAY_MILLIS)
                        loadNextRaces()
                    }
                }
        }
    }

    val nextRacesUiStateFlow: StateFlow<NextRacesUiState> = combine(
        nextRacesRepository.getNextRacesFlow(),
        nextRacesLoadingStatusStateFlow,
        currentTimeSecondsStateFlow,
        selectedCategoryIdsStateFlow
    ) { raceSummaries, loadingStatus, currentSeconds, selectedCategoryIds ->
        val racesListState = getRacesListState(
            loadingStatus = loadingStatus,
            raceSummaries = raceSummaries,
            selectedCategoryIds = selectedCategoryIds,
            currentSeconds = currentSeconds
        )
        NextRacesUiState(
            selectedCategoryIds = selectedCategoryIds,
            raceListState = racesListState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = NextRacesUiState(
            raceListState = RaceListState.Loading,
            selectedCategoryIds = emptyList(),
        )
    )

    private fun getRacesListState(
        loadingStatus: LoadingStatus,
        raceSummaries: List<RaceSummary>,
        selectedCategoryIds: List<String>,
        currentSeconds: Long
    ) = when (loadingStatus) {
        LoadingStatus.LOADING -> RaceListState.Loading
        LoadingStatus.FAILED -> RaceListState.Error
        LoadingStatus.SUCCESS -> RaceListState.Success(
            getRaceSummaryUiItems(
                raceSummaries = raceSummaries,
                selectedCategoryIds = selectedCategoryIds,
                currentTimeSeconds = currentSeconds
            )
        )
    }

    fun selectCategories(categoryIds: List<String>) {
        selectedCategoryIdsStateFlow.value = categoryIds
    }

    private fun getRaceSummaryUiItems(
        raceSummaries: List<RaceSummary>,
        selectedCategoryIds: List<String>,
        currentTimeSeconds: Long
    ): List<RaceSummaryUiItem> = raceSummaries
        .asSequence()
        .filterNot { raceSummary ->
            val racePastSeconds = currentTimeSeconds - raceSummary.advertisedStartSeconds
            racePastSeconds > RACE_OBSOLETE_SECONDS
        }
        .filter { raceSummary ->
            selectedCategoryIds.isEmpty() || raceSummary.categoryId in selectedCategoryIds
        }
        .sortedBy { raceSummary -> raceSummary.advertisedStartSeconds }
        .take(MAXIMUM_RACE_DISPLAY_COUNT)
        .map { raceSummary ->
            val countdownTimeSeconds = raceSummary.advertisedStartSeconds - currentTimeSeconds
            val countdownTime = countdownSecondsFormatter.format(countdownTimeSeconds)
            RaceSummaryUiItem(
                raceId = raceSummary.raceId,
                raceName = raceSummary.raceName,
                raceNumber = "R${raceSummary.raceNumber}",
                meetingName = raceSummary.meetingName,
                countdownTime = countdownTime,
                category = Category.entries.firstOrNull { it.id == raceSummary.categoryId }
            )
        }.toList()

    companion object {
        private const val NEXT_RACES_REFRESH_DELAY_MILLIS = 60_000L
        private const val CURRENT_TIME_SYNC_DELAY_MILLIS = 1_000L
        private const val MAXIMUM_RACE_DISPLAY_COUNT = 5
        private const val RACE_OBSOLETE_SECONDS = 60
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
