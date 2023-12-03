package com.rpg.funbox.presentation

import com.google.gson.Gson
import com.rpg.funbox.presentation.map.QuizAnswerToServer
import com.rpg.funbox.presentation.map.SocketApplication
import com.rpg.funbox.presentation.map.VerifyAnswerToServer
import org.json.JSONObject

object MapSocket {
    val mSocket = SocketApplication.get()

    fun verifyAnswer(roomId: String, isCorrect: Boolean) {
        val json = Gson().toJson(VerifyAnswerToServer(isCorrect, roomId))
        mSocket.emit("verifyAnswer", JSONObject(json))
    }

    fun sendQuizAnswer(roomId: String, answer: String) {
        val json = Gson().toJson(QuizAnswerToServer(answer, roomId))
        mSocket.emit("quizAnswer", JSONObject(json))
    }
}