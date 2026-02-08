package com.university.app.network

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val quizId: Int,
    val moduleCode: String,
    val title: String,
    val questions: List<Question>
)

@Serializable
data class Question(
    val questionId: Int,
    val text: String,
    val options: List<String>,
    val correctOptions: List<Int> = emptyList()
)

@Serializable
data class QuizSubmission(
    val quizId: Int,
    val studentId: Int,
    val answers: List<List<Int>>
)

@Serializable
data class QuizResponse(
    val score: Int,
    val totalQuestions: Int,
    val message: String
)

@Serializable
data class ModuleStats(
    val moduleCode: String,
    val averageScore: Double,
    val totalQuizzes: Int,
    val attendancePercentage: Double // Added for attendance tracking
)

@Serializable
data class StudentStats(
    val overallAverage: Double,
    val totalPoints: Int,
    val overallAttendance: Double, // Added overall attendance
    val moduleStats: List<ModuleStats>
)
