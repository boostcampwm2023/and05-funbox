package com.rpg.funbox.data

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.rpg.funbox.data.dto.UserAuthDto
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.nio.charset.StandardCharsets

object JwtDecoder {

    private val gson = Gson()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun getUser(accessToken: String): UserAuthDto? {
        val payloadJwt = accessToken.split(".")[1]
        val decodedPayload =
            String(Base64.decode(payloadJwt, Base64.DEFAULT), StandardCharsets.UTF_8)
        val jsonObject = JsonParser.parseString(decodedPayload).asJsonObject
        val json = gson.toJson(jsonObject, JwtDecoder::class.java)

        val moshiAdapter: JsonAdapter<UserAuthDto> = moshi.adapter(UserAuthDto::class.java)
        return moshiAdapter.fromJson(json)
    }
}