package com.university.app

// 👇 THESE IMPORTS FIX THE "UNRESOLVED REFERENCE" ERRORS
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// 👇 This imports the classes we made in the other file
import com.university.app.model.Question
import com.university.app.network.ApiClient

@Composable
fun QuizScreen(
    sessionId: Int,
    studentId: Int,
    onQuizFinished: () -> Unit
) {
    // STATE
    // We use 'Question' here, not 'QuestionData'
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var quizComplete by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // LOAD DATA ON START
    LaunchedEffect(sessionId) {
        questions = ApiClient.getQuiz(sessionId)
        isLoading = false
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Generating Quiz from Lecture Notes...")
        }
        else if (questions.isEmpty()) {
            Text("No quiz available for this session yet.")
            Button(onClick = onQuizFinished) { Text("Go Back") }
        }
        else if (quizComplete) {
            // RESULT SCREEN
            Text(text = "Quiz Complete!", style = MaterialTheme.typography.headlineLarge)
            Text(text = "Score: $score / ${questions.size}", style = MaterialTheme.typography.displayMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                scope.launch {
                    // Send score to backend
                    ApiClient.submitQuiz(studentId, sessionId, score, questions.size)
                    onQuizFinished()
                }
            }) {
                Text("Submit & Finish")
            }
        }
        else {
            // QUESTION SCREEN
            val question = questions[currentIndex]

            Text(text = "Question ${currentIndex + 1} of ${questions.size}", style = MaterialTheme.typography.labelLarge)
            LinearProgressIndicator(
                progress = (currentIndex + 1) / questions.size.toFloat(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = question.question, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(24.dp))

            question.options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        // HACKATHON SHORTCUT:
                        // We are assuming the backend sends the correct answer index.
                        val correctIndex = question.answerIndex ?: 0

                        if (index == correctIndex) {
                            score++
                        }

                        if (currentIndex < questions.size - 1) {
                            currentIndex++
                        } else {
                            quizComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(option)
                }
            }
        }
    }
}