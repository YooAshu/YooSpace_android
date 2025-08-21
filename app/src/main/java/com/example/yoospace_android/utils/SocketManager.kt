package com.example.yoospace_android.utils


import io.socket.client.IO
import io.socket.client.Socket

object SocketManager {
    private var socket: Socket? = null

    fun init(baseUrl: String, accessToken: String) {
        if (socket == null) {
            // Prepare headers
            val headers = mutableMapOf<String, List<String>>()
            headers["Cookie"] = listOf("accessToken=$accessToken")

            val options = IO.Options().apply {
                reconnection = true
                extraHeaders = headers   // ✅ send cookies manually
            }

            socket = IO.socket(baseUrl, options)
            socket?.connect()
        }
    }

    fun getSocket(): Socket? = socket

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}

