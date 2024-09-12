package com.example.flashcard.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.flashcard.viewmodels.FlashCardsViewModel
import com.google.gson.Gson
@Composable
fun PlayFlashCards(
    navController: NavController,
    viewModel: FlashCardsViewModel
) {
    val flashCards by viewModel.flashCards.collectAsState(emptyList())
    val currentQuestionIndex = remember { mutableStateOf(0) }
    val selectedAnswer = remember { mutableStateOf<String?>(null) }
    val score = remember { mutableStateOf(0) }
    val answersResult = remember { mutableStateListOf<AnswerResult>() }
    val context = LocalContext.current

    if (flashCards.isNotEmpty() && currentQuestionIndex.value < flashCards.size) {
        val flashCard = flashCards[currentQuestionIndex.value]

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Play flash cards",
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
                Text(
                    text = flashCard.question,
                    style = MaterialTheme.typography.headlineSmall,
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
            }

            items(flashCard.answers) { answer ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedAnswer.value = answer }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedAnswer.value == answer,
                        onClick = { selectedAnswer.value = answer }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = answer)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedAnswer.value == null) {
                            Toast.makeText(context, "Please select an answer", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val isCorrect = selectedAnswer.value == flashCard.correctAnswer
                        if (isCorrect) {
                            score.value++
                        }

                        answersResult.add(
                            AnswerResult(
                                question = flashCard.question,
                                userAnswer = selectedAnswer.value!!,
                                correctAnswer = flashCard.correctAnswer,
                                isCorrect = isCorrect
                            )
                        )

                        selectedAnswer.value = null

                        if (currentQuestionIndex.value < flashCards.size - 1) {
                            currentQuestionIndex.value++
                        } else {
                            navController.navigate("SummaryScreen/${score.value}/${flashCards.size}/${Gson().toJson(answersResult)}") {
                                popUpTo("PlayFlashCards") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Submit")
                }

                Text(
                    text = "Question ${currentQuestionIndex.value + 1} of ${flashCards.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "There are no cards to play.\nPlease create some cards.",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}