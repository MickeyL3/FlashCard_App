package com.example.flashcard.screens

import android.app.AlertDialog
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.flashcard.models.FlashCard
import com.example.flashcard.viewmodels.FlashCardsViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun FlashCardListScreen(navController: NavController, flashCardsViewModel: FlashCardsViewModel) {
    flashCardsViewModel.getFlashCards()
    val flashCards: List<FlashCard> by flashCardsViewModel.flashCards.collectAsState(emptyList())

    LazyColumn {
        items(flashCards) { flashCard ->
            FlashCardItem(
                navController = navController,
                flashCard = flashCard,
                deleteFn = { id: Int -> flashCardsViewModel.deleteFlashCardById(id) }
            )
            Divider()
        }
    }
}

@Composable
fun FlashCardItem(
    navController: NavController,
    flashCard: FlashCard,
    deleteFn: (id: Int) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navController.navigate("FlashCardDetails/${flashCard.id}") },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
        ) {
            Text(
                text = flashCard.question,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Answers: ${flashCard.answers.size}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            IconButton(onClick = {
                navController.navigate("EditFlashCard/${flashCard.id}")
            }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.Blue
                )
            }
            IconButton(onClick = {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Delete flashcard \"${flashCard.question}\"?")
                    .setCancelable(false)
                    .setPositiveButton("Delete") { dialog, id ->
                        deleteFn(flashCard.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }
}