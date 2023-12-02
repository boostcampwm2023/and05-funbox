package com.rpg.funbox.presentation.map


data class ApplyGameToServerData(
    val opponentId: Int
)

data class ApplyGameFromServerData(
    val userId: String,
    val roomId: String
)

data class GameApplyAnswerToServerData(
    val roomId: String,
    val answer: String,
)

data class GameApplyAnswerFromServerData(
    val answer: String,
)

data class QuizFromServer(
    val roomId: String,
    val quiz: String,
    val target: Int
)

data class QuizAnswerToServer(
    val answer: String,
    val roomId: String
)

data class QuizAnswerFromServer(
    val answer: String,
    val roomId: String
)

data class VerifyAnswerToServer(
    val isCorrect: Boolean,
    val roomId: String
)

class ScoreFromServer : ArrayList<ScoreFromServerItem>()

data class ScoreFromServerItem(
    val id: Int,
    val score: Int,
    val username: String
)