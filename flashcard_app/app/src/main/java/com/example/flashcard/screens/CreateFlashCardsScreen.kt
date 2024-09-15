package com.example.flashcard.screens
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashCards(
    navController: NavController,
    question: String,
    onQuestionChange: (String) -> Unit,
    userAnswers: MutableList<String>,
    onAnswersChange: (MutableList<String>) -> Unit,
    onCorrectAnswerChange: (String) -> Unit,
    createCardFn: (String, MutableList<String>, String) -> Unit,
    createFlashCardsViewModel: CreateFlashCardsViewModel
    ) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }
    var dialogTitle by rememberSaveable { mutableStateOf("") }
    var correctAnswer by rememberSaveable { mutableStateOf(" ") }
    var answers by rememberSaveable {
        mutableStateOf(
            listOf
                (
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf(""),
                mutableStateOf("")
            )
        )
    }
    var answerChecked by rememberSaveable {
        mutableStateOf(
            listOf
                (
                mutableStateOf(false),
                mutableStateOf(false),
                mutableStateOf(false),
                mutableStateOf(false)
            )
        )
    }
    var checkedIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (index in 0..answers.size - 1) {
            if (answers[index].value == correctAnswer) {
                answerChecked[index].value = true
            } else {
                answerChecked[index].value = false
            }
        }
        item {
            Text(
                text = "Add a new flash card",
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
                onValueChange = { onQuestionChange(it) },
                placeholder = { Text(text = "Input question here") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            color = Color.LightGray,
                            cornerRadius = CornerRadius(8.dp.toPx()),
                        )
                    }
            )
            Log.d("Flash Card Screen", "Question: $question")
        }
        items(answers.size) { index ->
            Log.d(
                "Flash Card Screen",
                "Answer ${answers.size}, user answers size ${userAnswers.size}, each answer is ${userAnswers}"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Checkbox(
                        checked = checkedIndex == index,
                        onCheckedChange = { isChecked ->
                            checkedIndex = if (isChecked) index else null
                            // Update the correct answer
                            correctAnswer = if (isChecked) answers[index].value else ""
                            onCorrectAnswerChange(correctAnswer)
                        }
                    )
                    Log.d("Flash Card Screen", "Correct answer: $correctAnswer")
                    OutlinedTextField(
                        value = answers[index].value,
                        placeholder = { Text(text = "") },
                        onValueChange = { answer ->
                            answers[index].value = answer
                            userAnswers[index] = answer
                            Log.d("Flash Card Screen", "User answers: $userAnswers")
                            createFlashCardsViewModel.updateAnswer(index, answer)
                            Log.d(
                                "Flash Card Screen",
                                "User answers after calling update: $userAnswers"
                            )
                            onAnswersChange(userAnswers)
                        },
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .drawBehind {
                                drawRoundRect(
                                    color = Color.LightGray,
                                    cornerRadius = CornerRadius(8.dp.toPx()),
                                )
                            }
                    )
                    Log.e("Create", "answer changed $answers")
                }
            }
        }
        item {
            Button(
                onClick = {
                    if (answers.size < 6) {
                        answers = answers.toMutableList().apply { add(mutableStateOf("")) }
                        answerChecked = answerChecked.toMutableList().apply {
                            add(mutableStateOf(false))
                        }
                        userAnswers.add("")
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

                        userAnswers.filter { it.isNotBlank() }.size < 2 -> {
                            dialogMessage = "A flash card must have at least 2 answers"
                            showDialog = true
                        }

                        correctAnswer.isBlank() -> {
                            dialogMessage = "A flash card must have 1 correct answer"
                            showDialog = true
                        }

                        else -> {
                            dialogTitle = "Flashcard Created:"
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
                                    createCardFn(question, userAnswers, correctAnswer)
                                    onQuestionChange("")
                                    onCorrectAnswerChange("")
                                    onAnswersChange(mutableListOf("", "", "", ""))
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