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
import com.example.mathgameapp.ui.theme.MathgameappTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathgameappTheme {
                MathGameApp()
            }
        }
    }
}

@Composable
fun MathGameApp() {
    // State for the current question and options
    var question by remember { mutableStateOf(generateMathQuestion()) }
    var options by remember { mutableStateOf(generateOptions(question.correctAnswer)) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }

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
                            question = generateMathQuestion()
                            options = generateOptions(question.correctAnswer)
                            selectedAnswer = null // Reset selected answer
                        } else {
                            // Optionally handle incorrect answer
                        }
                    },
                    modifier = Modifier // Correctly pass Modifier here
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
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
    val operations = listOf("+", "-", "*", "/")
    val num1 = Random.nextInt(1, 10)
    val num2 = Random.nextInt(1, 10)
    val operation = operations.random()
    val correctAnswer = when (operation) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> if (num2 != 0) num1 / num2 else num1
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
        MathGameApp()
    }
}
