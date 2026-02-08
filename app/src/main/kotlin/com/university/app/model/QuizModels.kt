package com.university.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val answerIndex: Int? = null // Optional, might be null if server hides it
)

@Serializable
data class QuizSubmit(
    val studentId: Int,
    val sessionId: Int,
    val score: Int,
    val total: Int
)