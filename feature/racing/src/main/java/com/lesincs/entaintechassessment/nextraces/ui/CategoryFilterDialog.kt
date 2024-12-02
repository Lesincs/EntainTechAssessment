package com.lesincs.entaintechassessment.nextraces.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lesincs.entaintechassessment.R
import com.lesincs.entaintechassessment.nextraces.CategoryFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CategoryFilterDialog(
    selectedCategoryIds: List<String>,
    onSelectedCategoryIdsApply: (List<String>) -> Unit,
    onDismissDialog: () -> Unit,
) {
    val selectedCategoryIdsInDialog = remember(selectedCategoryIds) {
        mutableStateListOf<String>().apply {
            addAll(selectedCategoryIds)
        }
    }
    Dialog(
        onDismissRequest = onDismissDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(12.dp)) {
            Column {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.category_filter_title),
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CategoryFilter.entries.fastForEach { categoryFilter ->
                        FilterChip(
                            modifier = Modifier.padding(4.dp),
                            label = {
                                Text(categoryFilter.title)
                            },
                            selected = selectedCategoryIdsInDialog.contains(categoryFilter.id),
                            onClick = {
                                if (selectedCategoryIdsInDialog.contains(categoryFilter.id)) {
                                    selectedCategoryIdsInDialog.remove(categoryFilter.id)
                                } else {
                                    selectedCategoryIdsInDialog.add(categoryFilter.id)
                                }
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onSelectedCategoryIdsApply(selectedCategoryIdsInDialog.toList())
                        onDismissDialog()
                    }) {
                        Text(stringResource(R.string.category_filter_button_text_apply))
                    }
                    TextButton(onClick = { onDismissDialog() }) {
                        Text(stringResource(R.string.category_filter_button_text_cancel))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryFilterDialogPreview() {
    MaterialTheme {
        CategoryFilterDialog(
            selectedCategoryIds = emptyList(),
            onSelectedCategoryIdsApply = {},
            onDismissDialog = {}
        )
    }
}
