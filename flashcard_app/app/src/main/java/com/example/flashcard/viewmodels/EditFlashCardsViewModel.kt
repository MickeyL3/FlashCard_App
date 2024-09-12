package com.example.flashcard.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.flashcard.models.FlashCard

class EditFlashCardViewModel : ViewModel() {
    var question by mutableStateOf("")
        private set

    fun updateQuestion(newQuestion: String) {
        question = newQuestion
    }

    var answers by mutableStateOf(listOf<String>())
        private set

    fun updateAnswers(newAnswers: List<String>) {
        answers = newAnswers
    }

    var correctAnswer by mutableStateOf("")
        private set

    fun updateCorrectAnswer(newCorrectAnswer: String) {
        correctAnswer = newCorrectAnswer
    }


    // Function to set the default values based on the selected flashcard
    fun setDefaultValues(selectedFlashCard: FlashCard?) {
        selectedFlashCard?.let {
            question = it.question
            answers = it.answers
            correctAnswer = it.correctAnswer
        }
    }
}