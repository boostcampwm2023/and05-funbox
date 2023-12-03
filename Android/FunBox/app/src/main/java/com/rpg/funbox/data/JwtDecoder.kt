package com.rpg.funbox.data

import android.util.Base64
import com.google.gson.JsonParser
import com.rpg.funbox.data.dto.UserAuthDto
import java.nio.charset.StandardCharsets

object JwtDecoder {

    //private val gson = Gson()
    //private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun getUser(accessToken: String): UserAuthDto {
        val payloadJwt = accessToken.split(".")[1]
        val decodedPayload =
            String(Base64.decode(payloadJwt, Base64.DEFAULT), StandardCharsets.UTF_8)
        val jsonObject = JsonParser.parseString(decodedPayload).asJsonObject

        return UserAuthDto(
            jsonObject.get("id").asInt,
            jsonObject.get("iat").asInt,
            jsonObject.get("exp").asInt
        )

        /*
            val json = gson.toJson(jsonObject, JwtDecoder::class.java)

            val moshiAdapter: JsonAdapter<UserAuthDto> = moshi.adapter(UserAuthDto::class.java)
            return moshiAdapter.fromJsonValue(decodedPayload)
        */
    }
}