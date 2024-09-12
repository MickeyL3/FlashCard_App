package com.example.flashcard.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.flashcard.models.FlashCard
import com.example.flashcard.viewmodels.EditFlashCardViewModel
import com.example.flashcard.viewmodels.FlashCardsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nz.ac.canterbury.seng303.lab1.shared.preferences")

@FlowPreview
val dataAccessModule = module {
    single<Storage<FlashCard>> {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<FlashCard>>(){}.type,
            preferenceKey = stringPreferencesKey("notes"),
            dataStore = androidContext().dataStore

        )
    }

    single { Gson() }

    viewModel {
        FlashCardsViewModel(
            flashCardsStorage = get()
        )
    }
    viewModel{
        EditFlashCardViewModel()
    }
}