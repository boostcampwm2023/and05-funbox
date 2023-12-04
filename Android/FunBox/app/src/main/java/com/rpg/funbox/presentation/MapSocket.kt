package com.rpg.funbox.presentation

import com.google.gson.Gson
import com.rpg.funbox.presentation.map.QuizAnswerToServer
import com.rpg.funbox.presentation.map.SocketApplication
import com.rpg.funbox.presentation.map.VerifyAnswerToServer
import org.json.JSONObject
import timber.log.Timber

object MapSocket {
    val mSocket = SocketApplication.get()

    fun verifyAnswer(roomId: String, isCorrect: Boolean) {
        val json = Gson().toJson(VerifyAnswerToServer(isCorrect, roomId))
        mSocket.emit("verifyAnswer", JSONObject(json))
        Timber.d(roomId)
    }

    fun sendQuizAnswer(roomId: String, answer: String) {
        val json = Gson().toJson(QuizAnswerToServer(answer, roomId))
        mSocket.emit("quizAnswer", JSONObject(json))
        Timber.d("Room: $roomId, Answer: $answer")
    }
}