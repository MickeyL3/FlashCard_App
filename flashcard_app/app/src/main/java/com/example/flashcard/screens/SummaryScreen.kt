package com.example.flashcard.screens
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.flashcard.models.FlashCard
import com.example.flashcard.viewmodels.FlashCardsViewModel

@Composable
fun SummaryScreen(
    score: Int,
    totalQuestions: Int,
    answersResult: List<AnswerResult>,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Summary",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Score: $score / $totalQuestions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn {
            items(answersResult) { result ->
                QuestionResultItem(result)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackToHome,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Back to Home")
        }
    }
}

@Composable
fun QuestionResultItem(result: AnswerResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Question: ${result.question}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Your Answer: ${result.userAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (result.isCorrect) Color(0xFF030303) else Color(0xFFF50535)
            )
            Text(
                text = "Correct Answer: ${result.correctAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF008121)
            )
        }
    }
}

data class AnswerResult(
    val question: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)