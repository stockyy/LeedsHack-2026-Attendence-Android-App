package com.university.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.university.app.network.*
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(studentId: Int, onBack: () -> Unit) {
    var quiz by remember { mutableStateOf<Quiz?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var userAnswers by remember { mutableStateOf<MutableMap<Int, MutableSet<Int>>>(mutableMapOf()) }
    var submissionResult by remember { mutableStateOf<QuizResponse?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(studentId) {
        quiz = ApiClient.getAvailableQuiz(studentId)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (submissionResult != null) {
        QuizResultView(submissionResult!!, onBack)
    } else if (quiz == null) {
        NoQuizView(onBack)
    } else {
        QuizContentView(
            quiz = quiz!!,
            userAnswers = userAnswers,
            onAnswerChange = { questionId, optionIndex, checked ->
                val currentAnswers = userAnswers.getOrDefault(questionId, mutableSetOf()).toMutableSet()
                if (checked) currentAnswers.add(optionIndex) else currentAnswers.remove(optionIndex)
                userAnswers = userAnswers.toMutableMap().apply { put(questionId, currentAnswers) }
            },
            onSubmit = {
                val submission = QuizSubmission(
                    quizId = quiz!!.quizId,
                    studentId = studentId,
                    answers = quiz!!.questions.map { q ->
                        userAnswers[q.questionId]?.toList() ?: emptyList()
                    }
                )
                scope.launch {
                    submissionResult = ApiClient.submitQuiz(submission)
                }
            },
            onBack = onBack
        )
    }
}

@Composable
fun QuizContentView(
    quiz: Quiz,
    userAnswers: Map<Int, Set<Int>>,
    onAnswerChange: (Int, Int, Boolean) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = quiz.title, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Module: ${quiz.moduleCode}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(quiz.questions) { index, question ->
                QuestionItem(
                    index = index + 1,
                    question = question,
                    selectedOptions = userAnswers[question.questionId] ?: emptySet(),
                    onAnswerChange = { optionIndex, checked ->
                        onAnswerChange(question.questionId, optionIndex, checked)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("Back") }
            Button(onClick = onSubmit) { Text("Submit Quiz") }
        }
    }
}

@Composable
fun QuestionItem(
    index: Int,
    question: Question,
    selectedOptions: Set<Int>,
    onAnswerChange: (Int, Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Q$index: ${question.text}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            question.options.forEachIndexed { optionIndex, optionText ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedOptions.contains(optionIndex),
                        onCheckedChange = { onAnswerChange(optionIndex, it) }
                    )
                    Text(text = optionText)
                }
            }
        }
    }
}

@Composable
fun QuizResultView(result: QuizResponse, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Quiz Submitted!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Score: ${result.score} / ${result.totalQuestions}", style = MaterialTheme.typography.headlineSmall)
        Text(text = result.message)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack) { Text("Back to Home") }
    }
}

@Composable
fun NoQuizView(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No Quiz Available", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "There are no quizzes active for your modules right now. Check back after your next lecture!")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}
