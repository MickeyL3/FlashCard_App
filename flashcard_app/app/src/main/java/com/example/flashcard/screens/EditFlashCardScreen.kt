package com.example.flashcard.screens
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcard.viewmodels.CreateFlashCardsViewModel
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcard.models.FlashCard
import com.example.flashcard.viewmodels.EditFlashCardViewModel
import com.example.flashcard.viewmodels.FlashCardsViewModel
import kotlin.random.Random
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashCardScreen(
    navController: NavController,
    flashCardId: Int,
    viewModel: FlashCardsViewModel,
    editViewModel: EditFlashCardViewModel,
) {
    val flashCard by viewModel.selectedFlashCard.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(flashCardId) {
        if (flashCardId != null) {
            viewModel.getFlashCardById(flashCardId)
            viewModel.selectedFlashCard.collect { card ->
                editViewModel.setDefaultValues(card)
            }
        } else {
            editViewModel.setDefaultValues(flashCardId)
        }
    }

    val question = editViewModel.question
    val answers = editViewModel.answers
    val correctAnswer = editViewModel.correctAnswer
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }
    var dialogTitle by rememberSaveable { mutableStateOf("") }
    var checkedIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 80.dp)
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFF87CEFA),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Edit Flash Card",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
        item {
            OutlinedTextField(
                value = question,
                onValueChange = { editViewModel.updateQuestion(it) },
                placeholder = { Text(text = "Input question here") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            color = Color.LightGray,
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )
                    }
            )
        }
        items(answers.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedIndex == index,
                    onCheckedChange = { isChecked ->
                        checkedIndex = if (isChecked) index else null
                        editViewModel.updateCorrectAnswer(if (isChecked) answers[index] else "")
                    }
                )
                OutlinedTextField(
                    value = answers[index],
                    placeholder = { Text(text = "") },
                    onValueChange = { answer ->
                        val updatedAnswers = answers.toMutableList().apply { set(index, answer) }
                        editViewModel.updateAnswers(updatedAnswers)
                    },
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = Color.LightGray,
                                cornerRadius = CornerRadius(8.dp.toPx())
                            )
                        }
                )
            }
        }
        item {
            Button(
                onClick = {
                    if (answers.size < 6) {
                        val updatedAnswers = answers.toMutableList().apply { add("") }
                        editViewModel.updateAnswers(updatedAnswers)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }

        item {
            Button(
                modifier = Modifier
                    .padding(8.dp),
                onClick = {
                    when {
                        question.isBlank() -> {
                            dialogMessage = "A flash card must have a question"
                            showDialog = true
                        }

                        answers.filter { it.isNotBlank() }.size < 2 -> {
                            dialogMessage = "A flash card must have at least 2 answers"
                            showDialog = true
                        }

                        correctAnswer.isBlank() -> {
                            dialogMessage = "A flash card must have 1 correct answer"
                            showDialog = true
                        }

                        else -> {
                            dialogTitle = "Flashcard Updated:"
                            dialogMessage = " $question"
                            isSuccessDialog = true
                            showDialog = true
                        }
                    }
                }
            ) {
                Text(text = "Save and return")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = dialogTitle) },
                    text = { Text(text = dialogMessage) },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (isSuccessDialog) {
                                    viewModel.editFlashCardById(flashCardId, FlashCard(
                                        id = flashCardId,
                                        question = question,
                                        answers = editViewModel.answers.toMutableList(), // Convert to MutableList
                                        correctAnswer = correctAnswer
                                    ))
                                    navController.navigate("Home")
                                }
                                showDialog = false
                            }
                        ) {
                            Text("Close")
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}