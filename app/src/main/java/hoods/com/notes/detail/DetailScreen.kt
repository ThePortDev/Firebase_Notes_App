package hoods.com.notes.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hoods.com.notes.Utils
import hoods.com.notes.ui.theme.NotesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel?,
    noteId: String,
    onNavigate: () -> Unit
) {
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()

    val isFormsNotBlank = detailUiState.note.isNotBlank() &&
            detailUiState.title.isNotBlank()

    val selectedColor by animateColorAsState(targetValue = Utils.colors[detailUiState.colorIndex])

    val isNoteIdNotBlank = noteId.isNotBlank()
    val icon = if (isFormsNotBlank) Icons.Default.Refresh
        else Icons.Default.Check

    LaunchedEffect(key1 = Unit) {
        if (isNoteIdNotBlank) {
            detailViewModel?.getNote(noteId)
        } else {
            detailViewModel?.resetState()
        }
    }

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            AnimatedVisibility(visible = isFormsNotBlank) {
                FloatingActionButton(
                    onClick = {
                        if (isNoteIdNotBlank) {
                            detailViewModel?.updateNote(noteId)
                        } else {
                            detailViewModel?.addNote()
                        }
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = selectedColor)
                .padding(padding),
        ) {
            if (detailUiState.noteAddedStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Added Note Successfully")
                    detailViewModel?.resetNoteAddedStatus()
                    onNavigate.invoke()
                }
            }
            if (detailUiState.updateNoteStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Note Updated Successfully")
                    detailViewModel?.resetNoteAddedStatus()
                    onNavigate.invoke()
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 8.dp
                )
            ) {
                itemsIndexed(Utils.colors) { colorIndex,  color ->
                    ColorItem(color = color) {
                        detailViewModel?.onColorChange(colorIndex)
                    }
                }
            }

            OutlinedTextField(
                value = detailUiState.title,
                onValueChange = {
                    detailViewModel?.onTitleChange(it)
                },
                label = { Text(text = "Title")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            OutlinedTextField(
                value = detailUiState.note,
                onValueChange = { detailViewModel?.onNoteChange(it)},
                label = { Text(text = "Notes")},
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(8.dp)
            )
        }
        

    }
}

@Composable
fun ColorItem(
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .clickable {
                onClick.invoke()
            },
        border = BorderStroke(2.dp, Color.Black)
    ) {

    }
}

@Preview(showSystemUi = true)
@Composable
fun PrevDetailScreen() {
    NotesTheme {
        DetailScreen(detailViewModel = null, noteId = "") {
            
        }
    }
}
