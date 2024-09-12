package com.example.flashcard.screens


import android.app.SearchManager
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcard.models.FlashCard
import com.example.flashcard.viewmodels.FlashCardsViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color

@Composable
fun ViewFlashCardsScreen(
    viewModel: FlashCardsViewModel = viewModel(),
    navController: NavController
) {
    val flashCards by viewModel.flashCards.collectAsState(emptyList())
    var showDeleteDialog by rememberSaveable { mutableStateOf<Pair<FlashCard?, Boolean>>(Pair(null, false)) }

    if (flashCards.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "There are no cards created.\nPlease create some cards.",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            )
        }
    } else {
        LazyColumn {
            items(flashCards) { flashCard ->
                FlashCardItem(
                    flashCard = flashCard,
                    onEdit = { navController.navigate("EditFlashCard/${flashCard.id}") },
                    onDelete = {
                        showDeleteDialog = Pair(flashCard, true)
                    },
                    onSearch = { searchQuery ->
                        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(SearchManager.QUERY, searchQuery)
                        }
                        LocalContext.current.startActivity(searchIntent)
                    }
                )
                Divider()
            }

        }
    }
    showDeleteDialog.first?.let { flashCard ->
        if (showDeleteDialog.second) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = Pair(null, false) },
                title = { Text("Delete flashcard") },
                text = { Text("Delete flashcard \"${flashCard.question}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteFlashCardById(flashCard.id)
                            showDeleteDialog = Pair(null, false)
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog = Pair(null, false) }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FlashCardItem(
    flashCard: FlashCard,
    onSearch: @Composable (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = flashCard.question,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    val searchQuery = flashCard.question
                    val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                        putExtra(SearchManager.QUERY, searchQuery)
                    }
                    context.startActivity(searchIntent)
                    Log.d("FLASH CARD LIST", "Clickable search button searches for ${flashCard.question}")
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = { onEdit() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = { onDelete() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}