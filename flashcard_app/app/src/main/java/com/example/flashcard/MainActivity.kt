package com.example.flashcard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcard.models.FlashCard
import com.example.flashcard.screens.AnswerResult
import com.example.flashcard.screens.CreateFlashCards
import com.example.flashcard.screens.EditFlashCardScreen
import com.example.flashcard.screens.FlashCardListScreen
import com.example.flashcard.screens.PlayFlashCards
import com.example.flashcard.screens.SummaryScreen
import com.example.flashcard.screens.ViewFlashCardsScreen
import com.example.flashcard.ui.theme.FlashCardTheme
import com.example.flashcard.viewmodels.CreateFlashCardsViewModel
import com.example.flashcard.viewmodels.EditFlashCardViewModel
import com.example.flashcard.viewmodels.FlashCardsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel


class MainActivity : ComponentActivity() {
    private val flashCardViewModel: FlashCardsViewModel by koinViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashCardTheme {
                val navController = rememberNavController()
                val splashScreenOn = remember { mutableStateOf(true) }
                if (splashScreenOn.value) {
                    SplashScreen {
                        splashScreenOn.value = false
                    }
                } else {
                    MainScreen(navController = navController, flashCardViewModel)

                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(navController: NavController, flashCardsViewModel: FlashCardsViewModel) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Flash Cards App") },
                    navigationIcon = {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        if (currentRoute != "SplashScreen" && currentRoute != "Home") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF87CEFA))
                    .padding(it)

            ) {
                NavHost(
                    navController = navController as NavHostController,
                    startDestination = "Home",
                    Modifier.padding(it)
                ) {
                    composable("Home") {
                        HomeScreen(navController = navController)
                    }
                    composable("CreateFlashCards") {
                        val createFlashCardsViewModel: CreateFlashCardsViewModel = viewModel()
                        CreateFlashCards(
                            navController = navController,
                            question = createFlashCardsViewModel.question,
                            onQuestionChange = { newQuestion ->
                                createFlashCardsViewModel.updateQuestion(
                                    newQuestion
                                )
                            },
                            createFlashCardsViewModel = createFlashCardsViewModel,
                            userAnswers = createFlashCardsViewModel.answers.toMutableList(),
                            onAnswersChange = { newListAnswer ->
                                createFlashCardsViewModel.setAnswers(
                                    newListAnswer
                                )
                            },
                            onCorrectAnswerChange = { correctAnswer ->
                                createFlashCardsViewModel.updateCorrectAnswer(
                                    correctAnswer
                                )
                            },
                            createCardFn = { question, answers, correctAnswer ->
                                flashCardViewModel.createFlashCard(
                                    question = question,
                                    answers = answers,
                                    correctAnswer = correctAnswer
                                )
                            })
                    }
                    composable("ViewFlashCards") {
                        ViewFlashCardsScreen(
                            viewModel = flashCardViewModel,
                            navController = navController
                        )
                    }
                    composable("EditFlashCard/{flashCardId}") { backStackEntry ->
//                        val viewModel: FlashCardsViewModel = viewModel()
                        val editFlashCardViewModel: EditFlashCardViewModel = viewModel()
                        val flashCardIdArg = backStackEntry.arguments?.getString("flashCardId")?.toIntOrNull()
                        val flashCardId = flashCardIdArg ?: -1
                        LaunchedEffect(flashCardId) {
                            if (flashCardId != -1) {
                                flashCardsViewModel.getFlashCardById(flashCardId)
                                flashCardsViewModel.selectedFlashCard.collect { card ->
                                    if (card != null) {
                                        editFlashCardViewModel.setDefaultValues(card)
                                    }
                                }
                            } else {
                                editFlashCardViewModel.setDefaultValues(null)
                            }
                        }
                        EditFlashCardScreen(
                            navController = navController,
                            flashCardId = flashCardId,
                            viewModel = flashCardsViewModel,
                            editViewModel = editFlashCardViewModel
                        )
                    }
                    composable("PlayFlashCards") {
                        PlayFlashCards(navController, flashCardViewModel)
                    }
                    composable(
                        "SummaryScreen/{score}/{totalQuestions}/{answersResult}",
                        arguments = listOf(
                            navArgument("score") { type = NavType.IntType },
                            navArgument("totalQuestions") { type = NavType.IntType },
                            navArgument("answersResult") { type = NavType.StringType } // You may use StringType if you serialize data
                        )
                    ) { backStackEntry ->
                        val score = backStackEntry.arguments?.getInt("score") ?: 0
                        val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
                        val answersResultJson = backStackEntry.arguments?.getString("answersResult") ?: "[]"
                        val answersResult = Gson().fromJson(answersResultJson, Array<AnswerResult>::class.java).toList()

                        SummaryScreen(
                            score = score,
                            totalQuestions = totalQuestions,
                            answersResult = answersResult,
                            onBackToHome = { navController.navigate("Home") }
                        )
                    }

                    composable("FlashCardList") {
                        val listCards: List<FlashCard> by flashCardViewModel.flashCards.collectAsState(
                            emptyList()
                        )
                        Log.d(
                            "MAIN_ACTIVITY",
                            "Number of question in storage is: ${listCards.size}"
                        )
                        FlashCardListScreen(navController, flashCardViewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun SplashScreen(onSplashScreenDone: () -> Unit) {
        var splashScreenDone by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(2000)
            splashScreenDone = true
            onSplashScreenDone()
        }
        if (!splashScreenDone) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE6E6FA)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(200.dp)
                    )
                    Text(
                        text = "Flash Cards App!",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun HomeScreen(navController: NavController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Button(onClick = { navController.navigate("ViewFlashCards") }) {
                Text("View Flash Cards")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("CreateFlashCards") }) {
                Text("Create Flash Card")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("PlayFlashCards") }) {
                Text("Play Flash Cards")
            }

        }
    }
}