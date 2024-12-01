package com.lesincs.entaintechassessment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEach

@Composable
fun RacingRoute(modifier: Modifier = Modifier) {
    NextRacesScreen(
        racesUiState = NextRacesUiState(emptyList(), emptyList()),
        onSelectedCategoryIdsApply = {},
        modifier = modifier
    )
}

@Composable
internal fun NextRacesScreen(
    racesUiState: NextRacesUiState,
    onSelectedCategoryIdsApply: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            NextRaceAppBar(
                selectedCategoryIds = racesUiState.selectedCategoryIds,
                onFilterClick = { showFilterDialog = true }
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            HorizontalDivider()
            racesUiState.races.fastForEach { race ->
                RaceSummaryItem(race)
                HorizontalDivider()
            }
        }
    }

    if (showFilterDialog) {
        CategoryFilterDialog(
            selectedCategoryIds = racesUiState.selectedCategoryIds,
            onSelectedCategoryIdsApply = onSelectedCategoryIdsApply,
            onDismissDialog = { showFilterDialog = false },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NextRaceAppBar(
    selectedCategoryIds: List<String>,
    onFilterClick: () -> Unit,
) {
    LargeTopAppBar(
        title = {
            Text(text = stringResource(R.string.title_next_races))
        },
        actions = {
            val imageVector = if (selectedCategoryIds.isEmpty()) {
                Icons.Filled.FilterAltOff
            } else {
                Icons.Filled.FilterAlt
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun RaceSummaryItem(race: RaceSummaryUiItem) {
    ListItem(
        leadingContent = { Text(race.raceNumber) },
        headlineContent = { Text(race.meetingName) },
        trailingContent = { Text(race.countdownTime) },
        supportingContent = { Text(race.raceName) },
    )
}

@Preview
@Composable
private fun NextRacesScreenPreview() {
    MaterialTheme {
        NextRacesScreen(
            racesUiState = NextRacesUiState(
                races = listOf(
                    RaceSummaryUiItem(
                        raceId = "e91170b7-ae24-46c1-9b8e-f1d001ecd567",
                        raceNumber = "R2",
                        meetingName = "Cromwell",
                        countdownTime = "50s",
                        raceName = "Happy Hire Cromwell Cup"
                    ),
                    RaceSummaryUiItem(
                        raceId = "fbadd808-430d-4e5b-9734-da07665cc0f6",
                        raceNumber = "R6",
                        meetingName = "Woodbine Mohawk Park",
                        countdownTime = "1m 20s",
                        raceName = "Race 6 - 1609M"
                    )
                ),
                selectedCategoryIds = emptyList(),
            ),
            onSelectedCategoryIdsApply = {}
        )
    }
}
