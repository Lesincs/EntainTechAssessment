package com.lesincs.entaintechassessment

data class NextRacesUiState(
    val races: List<RaceSummaryUiItem>,
    val selectedCategoryIds: List<String>,
)

data class RaceSummaryUiItem(
    val raceId: String,
    val raceName: String,
    val raceNumber: String,
    val meetingName: String,
    val countdownTime: String,
)

enum class CategoryFilter(val title: String, val id: String) {
    // TODO replace with real category id
    HorseRacing("Horse ", "1"),
    HarnessRacing("Harness", "2"),
    GreyHoundRacing("GreyHound", "3")
}
