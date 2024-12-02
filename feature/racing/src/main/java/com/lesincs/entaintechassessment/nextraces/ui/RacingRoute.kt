package com.lesincs.entaintechassessment.nextraces.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lesincs.entaintechassessment.R
import com.lesincs.entaintechassessment.nextraces.Category
import com.lesincs.entaintechassessment.nextraces.NextRacesUiState
import com.lesincs.entaintechassessment.nextraces.NextRacesVieModel
import com.lesincs.entaintechassessment.nextraces.RaceListState
import com.lesincs.entaintechassessment.nextraces.RaceSummaryUiItem

@Composable
fun RacingRoute(modifier: Modifier = Modifier) {
    val nextRacesVieModel: NextRacesVieModel = viewModel<NextRacesVieModel>()
    val nextRacesUiState =
        nextRacesVieModel.nextRacesUiStateFlow.collectAsStateWithLifecycle().value
    NextRacesScreen(
        nextRacesUiState = nextRacesUiState,
        onSelectedCategoryIdsApply = nextRacesVieModel::selectCategories,
        reloadRaces = nextRacesVieModel::loadNextRaces,
        modifier = modifier
    )
}

@Composable
internal fun NextRacesScreen(
    nextRacesUiState: NextRacesUiState,
    onSelectedCategoryIdsApply: (List<String>) -> Unit,
    reloadRaces: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            NextRaceAppBar(
                selectedCategoryIds = nextRacesUiState.selectedCategoryIds,
                onFilterClick = { showFilterDialog = true },
                reloadRaces = reloadRaces,
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            HorizontalDivider()
            when (val racesListState = nextRacesUiState.raceListState) {
                RaceListState.Error -> Error(retry = reloadRaces)
                RaceListState.Loading -> Loading()
                is RaceListState.Success -> {
                    if (racesListState.races.isEmpty()) {
                        Empty()
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            racesListState.races.fastForEach { race ->
                                RaceSummaryItem(race)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        CategoryFilterDialog(
            selectedCategoryIds = nextRacesUiState.selectedCategoryIds,
            onSelectedCategoryIdsApply = onSelectedCategoryIdsApply,
            onDismissDialog = { showFilterDialog = false },
        )
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    )
}

@Composable
private fun Error(modifier: Modifier = Modifier, retry: () -> Unit) {
    TextButton(
        onClick = retry,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Text(stringResource(R.string.load_failed_click_to_retry))
    }
}

@Composable
private fun Empty(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.no_races_available_text),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .wrapContentSize()
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NextRaceAppBar(
    selectedCategoryIds: List<String>,
    onFilterClick: () -> Unit,
    reloadRaces: () -> Unit,
) {
    LargeTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.title_next_races))
                IconButton(onClick = reloadRaces) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            val filterApplied = selectedCategoryIds.isNotEmpty()
            val imageVector = if (filterApplied) {
                Icons.Filled.FilterAlt
            } else {
                Icons.Filled.FilterAltOff
            }
            IconButton(onClick = onFilterClick) {
                Icon(
                    modifier = Modifier.redDotBadge(filterApplied),
                    imageVector = imageVector,
                    contentDescription = null
                )
            }
        }
    )
}

private fun Modifier.redDotBadge(enabled: Boolean) = this.drawWithContent {
    drawContent()
    if (enabled) {
        val radius = 15f
        drawCircle(
            color = Color.Red,
            radius = radius,
            center = Offset(this.size.width - radius, radius),
        )
    }
}

@Composable
private fun RaceSummaryItem(race: RaceSummaryUiItem) {
    ListItem(
        leadingContent = { Text(text = race.raceNumber, modifier = Modifier.widthIn(min = 48.dp)) },
        headlineContent = { Text(race.meetingName) },
        trailingContent = { Text(text = race.countdownTime, color = Color(color = 0xFFC02A2B)) },
        supportingContent = { Text(race.raceName) },
        overlineContent = {
            race.category?.let {
                Text(
                    text = it.title,
                    modifier = Modifier.border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp)
                )
            }
        }
    )
}

@Preview
@Composable
private fun NextRacesScreenPreview_SUCCESS() {
    MaterialTheme {
        NextRacesScreen(
            nextRacesUiState = NextRacesUiState(
                raceListState = RaceListState.Success(
                    listOf(
                        RaceSummaryUiItem(
                            raceId = "e91170b7-ae24-46c1-9b8e-f1d001ecd567",
                            raceNumber = "R2",
                            meetingName = "Cromwell",
                            countdownTime = "50s",
                            raceName = "Happy Hire Cromwell Cup",
                            category = Category.HarnessRacing,
                        ),
                        RaceSummaryUiItem(
                            raceId = "fbadd808-430d-4e5b-9734-da07665cc0f6",
                            raceNumber = "R11",
                            meetingName = "Woodbine Mohawk Park",
                            countdownTime = "1m 20s",
                            raceName = "Race 6 - 1609M",
                            category = Category.HorseRacing
                        )
                    )
                ),
                selectedCategoryIds = emptyList(),
            ),
            onSelectedCategoryIdsApply = {},
            reloadRaces = {}
        )
    }
}

@Preview
@Composable
private fun NextRacesScreenPreview_LOADING() {
    MaterialTheme {
        NextRacesScreen(
            nextRacesUiState = NextRacesUiState(
                raceListState = RaceListState.Loading,
                selectedCategoryIds = emptyList(),
            ),
            onSelectedCategoryIdsApply = {},
            reloadRaces = {}
        )
    }
}

@Preview
@Composable
private fun NextRacesScreenPreview_ERROR() {
    MaterialTheme {
        NextRacesScreen(
            nextRacesUiState = NextRacesUiState(
                raceListState = RaceListState.Error,
                selectedCategoryIds = emptyList(),
            ),
            onSelectedCategoryIdsApply = {},
            reloadRaces = {}
        )
    }
}
