package com.example.flashcard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.models.FlashCard
import com.example.flashcard.datastore.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlinx.coroutines.flow.collect

class FlashCardsViewModel(
    private val flashCardsStorage: Storage<FlashCard>
) : ViewModel() {

    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> get() = _flashCards

    private val _selectedFlashCard = MutableStateFlow<FlashCard?>(null)
    val selectedFlashCard: StateFlow<FlashCard?> get() = _selectedFlashCard

    fun getFlashCards() = viewModelScope.launch {
        flashCardsStorage.getAll()
            .catch { Log.e("FLASH_CARDS_VIEW_MODEL", it.toString()) }
            .collect { _flashCards.emit(it) }
    }

    fun loadDefaultFlashCardsIfNoneExist() = viewModelScope.launch {
        val currentFlashCards = flashCardsStorage.getAll().first()
        if (currentFlashCards.isEmpty()) {
            Log.d("FLASH_CARDS_VIEW_MODEL", "Inserting default flashcards...")
            flashCardsStorage.insertAll(FlashCard.getCards())
                .catch { Log.w("FLASH_CARDS_VIEW_MODEL", "Could not insert default flashcards") }
                .collect {
                    Log.d("FLASH_CARDS_VIEW_MODEL", "Default flashcards inserted successfully")
                    _flashCards.emit(FlashCard.getCards())
                }
        }
    }

    fun createFlashCard(question: String, answers: MutableList<String>, correctAnswer: String) = viewModelScope.launch {
        val flashCard = FlashCard(
            id = Random.nextInt(0, Int.MAX_VALUE),
            question = question,
            answers = answers,
            correctAnswer = correctAnswer,
            )
        flashCardsStorage.insert(flashCard)
            .catch { Log.e("FLASH_CARDS_VIEW_MODEL", "Could not create flashcard") }
            .collect()
        flashCardsStorage.getAll()
            .catch { Log.e("FLASH_CARDS_VIEW_MODEL", it.toString()) }
            .collect { _flashCards.emit(it) }
    }

    fun getFlashCardById(flashCardId: Int?) = viewModelScope.launch {
        if (flashCardId != null) {
            _selectedFlashCard.value = flashCardsStorage.get { it.getIdentifier() == flashCardId }.first()
        } else {
            _selectedFlashCard.value = null
        }
    }

    fun deleteFlashCardById(flashCardId: Int) = viewModelScope.launch {
        Log.d("FLASH_CARDS_VIEW_MODEL", "Deleting flash card: $flashCardId")

        flashCardsStorage.delete(flashCardId).catch { Log.e("FLASH_CARDS_VIEW_MODEL", "Could not delete flash card") }.collect()
        flashCardsStorage.getAll()
            .catch { Log.e("FLASH_CARDS_VIEW_MODEL", it.toString()) }
            .collect { _flashCards.emit(it) }

    }

    fun editFlashCardById(flashCardId: Int?, flashCard: FlashCard) = viewModelScope.launch {
        Log.d("FLASH_CARDS_VIEW_MODEL", "Editing flashcard: $flashCardId")
        if (flashCardId != null) {
            flashCardsStorage.edit(flashCardId, flashCard).catch { Log.e("FLASH_CARDS_VIEW_MODEL", "Could not edit flash card") }.collect()
            flashCardsStorage.getAll()
                .catch { Log.e("FLASH_CARDS_VIEW_MODEL", it.toString()) }
                .collect { _flashCards.emit(it) }
        }
    }
}