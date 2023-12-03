package com.rpg.funbox.presentation.map
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

class SocketApplication {
    companion object {
        private lateinit var socket : Socket
        fun get(): Socket {
            try {
                val options = IO.Options()
                options.extraHeaders = singletonMap("Authorization",singletonList("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MzUsImlhdCI6MTcwMTM5Mjg0MiwiZXhwIjoxNzAxNjUyMDQyfQ.VMGe66yPyxcu1rpJs4EoSyHxXF8sTTsQVKmzU1FG8Js"))
                socket = IO.socket("http://175.45.193.191:3000/socket",options)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return socket
        }
    }
}