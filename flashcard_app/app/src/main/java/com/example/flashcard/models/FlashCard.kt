package com.example.flashcard.models

data class FlashCard (
    val id: Int,
    val question: String,
    val answers: MutableList<String>,
    val correctAnswer: String): Identifiable {
    companion object {
        fun getCards(): List<FlashCard> {
            return listOf(
                FlashCard(
                    id = 1,
                    question = "What is the capital of Vietnam?",
                    answers = mutableListOf("Paris", "Hanoi", "Rome", "Berlin"),
                    correctAnswer = "Hanoi",
                ),
                FlashCard(
                    id = 2,
                    question = "What is 2 + 9?",
                    answers = mutableListOf("3", "4", "11", "6"),
                    correctAnswer = "11",
                )
            )
        }
    }
    override fun getIdentifier(): Int {
        return id;
    }
}
