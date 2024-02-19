package com.example.mathgameapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mathgameapp.ui.theme.MathgameappTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathgameappTheme {
                // Navigation Host
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "startScreen") {
                    composable("startScreen") { StartScreen(navController) }
                    composable("gameScreen") { MathGameApp(navController) }
                    composable("endScreen/{score}", arguments = listOf(navArgument("score") { type = NavType.IntType })) { backStackEntry ->
                        EndScreen(navController, backStackEntry.arguments?.getInt("score") ?: 0)
                    }
                }
            }
        }
    }
}
@Composable
fun StartScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Button(onClick = { navController.navigate("gameScreen") }) {
                Text("Play")
            }
        }
    }
}
@Composable
fun EndScreen(navController: NavController, score: Int) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Your Score: $score", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack(navController.graph.startDestinationId, inclusive = false); navController.navigate("gameScreen") }) {
                Text("Play Again")
            }
        }
    }
}


@Composable
fun MathGameApp(navController: NavController) { // Accept NavController as a parameter
    // State for the current question and options
    var question by remember { mutableStateOf(generateMathQuestion()) }
    var options by remember { mutableStateOf(generateOptions(question.correctAnswer)) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var questionCount by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = question.text, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            options.forEachIndexed { index, answer ->
                AnswerButton(
                    answerText = answer.toString(),
                    onClick = {
                        if (question.correctAnswer == answer) {
                            score += 5 // Add 5 points for correct answer
                            questionCount++ // Increment question count
                            if (questionCount >= 10) {
                                // Navigate to the end screen when 10 questions have been asked
                                navController.navigate("endScreen/$score")
                            } else {
                                question = generateMathQuestion()
                                options = generateOptions(question.correctAnswer)
                                selectedAnswer = null // Reset selected answer
                            }
                        } else {
                            score -= 1 // Subtract 1 point for incorrect answer
                            options = options.filterNot { it == answer } // Remove incorrect answer
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            // Display the score and question count
            Text("Score: $score", style = MaterialTheme.typography.titleLarge)
            Text("Question: $questionCount/10", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun AnswerButton(answerText: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Text(text = answerText)
    }
}

data class MathQuestion(val text: String, val correctAnswer: Int)

fun generateMathQuestion(): MathQuestion {
    var num1 = Random.nextInt(1, 10)
    var num2 = Random.nextInt(1, 10)
    val operations = listOf("+", "-", "*", "/").filter {
        // Filter operations to ensure whole number results and no negative answers for subtraction
        !(it == "-" && num1 < num2) && !(it == "/" && num1 % num2 != 0)
    }
    val operation = operations.random()

    if (operation == "-") {
        // Ensure num1 is always greater than num2 for subtraction
        if (num1 < num2) {
            val temp = num1
            num1 = num2
            num2 = temp
        }
    } else if (operation == "/") {
        // Adjust num1 to ensure whole number division results
        while (num1 % num2 != 0) {
            num1 = Random.nextInt(1, 10) * num2
        }
    }

    val correctAnswer = when (operation) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> num1 / num2
        else -> 0
    }
    return MathQuestion("$num1 $operation $num2", correctAnswer)
}

fun generateOptions(correctAnswer: Int): List<Int> {
    val fakeAnswers = mutableSetOf<Int>()
    while (fakeAnswers.size < 3) {
        val fakeAnswer = correctAnswer + Random.nextInt(-3, 4)
        if (fakeAnswer != correctAnswer) fakeAnswers.add(fakeAnswer)
    }
    val allAnswers = fakeAnswers.toMutableList().apply { add(correctAnswer) }.shuffled()
    return allAnswers
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MathgameappTheme {
        // Create a mock NavController for preview purposes
        val navController = rememberNavController()
        MathGameApp(navController = navController)
    }
}

