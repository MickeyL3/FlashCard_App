package com.example.flashcard.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel



class CreateFlashCardsViewModel : ViewModel() {
    var question by mutableStateOf("")
        private set

    var answers = mutableListOf("", "", "", "")
        private set

    var correctAnswer by mutableStateOf("")
        private set

    fun updateQuestion(newQuestion: String) {
        question = newQuestion
    }

    fun updateAnswer(index: Int, newAnswer: String) {
        if (index < answers.size) {
            answers[index] = newAnswer
        }
    }
    fun updateCorrectAnswer(newAnswer: String) {
        correctAnswer = newAnswer
    }
    fun setAnswers(newAnswers: MutableList<String> ) {
        answers.clear()
        answers.addAll(newAnswers)
    }
}