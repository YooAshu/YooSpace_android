package com.example.yoospace_android.utils


import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.yoospace_android.data.local.TokenManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

object SocketManager {
    private var socket: Socket? = null
    private const val TAG = "SocketManager"
    fun init(baseUrl: String, accessToken: String) {
        if (socket == null) {
            val headers = mutableMapOf<String, List<String>>()
            headers["Cookie"] = listOf("accessToken=$accessToken")

            val options = IO.Options().apply {
                reconnection = true
                extraHeaders = headers
            }

            socket = IO.socket(baseUrl, options)

            //  Add connection listener to confirm successful connection
            socket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "✅ Socket connected successfully!")
                socket?.emit("joinNotifications", TokenManager.getUserId())
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(
                    TAG,
                    "Socket connection error: ${if (args.isNotEmpty()) args[0] else "Unknown error"}"
                )
            }

            //  FIXED: Better notification listener
            socket?.on("receive_notification") { args ->
                if (args.isNotEmpty()) {
                    try {
                        val rawData = args[0]
                        val notif = when (rawData) {
                            is JSONObject -> rawData
                            is String -> JSONObject(rawData)
                            else -> JSONObject(rawData.toString())
                        }

                        val type = notif.optString("type", "")
                        val message = notif.optString("message", "You have a new notification")

                        // 🔔 Show system notification

                        showNotification(type, message)

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing notification: ${e.message}")
                        e.printStackTrace()
                    }
                } else {
                    Log.w(TAG, "Received empty notification event")
                }
            }

            socket?.connect()
        }
    }

    fun getSocket(): Socket? = socket

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    // ✅ Test method to verify notification reception
    fun testNotification() {
        Log.d(TAG, "🧪 Requesting test notification...")
        socket?.emit("test_notification")
    }
}