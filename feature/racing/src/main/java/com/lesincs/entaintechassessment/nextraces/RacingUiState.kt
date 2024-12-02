package com.lesincs.entaintechassessment.nextraces

internal data class NextRacesUiState(
    val raceListState: RaceListState,
    val selectedCategoryIds: List<String>,
)

internal sealed interface RaceListState {
    data object Loading : RaceListState
    data class Success(val races: List<RaceSummaryUiItem>) : RaceListState
    data object Error : RaceListState
}

internal data class RaceSummaryUiItem(
    val raceId: String,
    val raceName: String,
    val raceNumber: String,
    val meetingName: String,
    val countdownTime: String,
    val category: Category?,
)

internal enum class Category(val title: String, val id: String) {
    HorseRacing("Horse", "4a2788f8-e825-4d36-9894-efd4baf1cfae"),
    HarnessRacing("Harness", "161d9be2-e909-4326-8c2c-35ed71fb460b"),
    GreyHoundRacing("GreyHound", "9daef0d7-bf3c-4f50-921d-8e818c60fe61")
}

internal enum class LoadingStatus {
    LOADING, SUCCESS, FAILED
}
