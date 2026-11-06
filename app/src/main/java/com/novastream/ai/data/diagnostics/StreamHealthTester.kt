package com.novastream.ai.data.diagnostics

import com.novastream.ai.data.model.StreamHealth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object StreamHealthTester {

    suspend fun testStream(urlStr: String, timeoutMs: Int = 8000): StreamHealth = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                instanceFollowRedirects = true
                requestMethod = "GET"
                setRequestProperty("User-Agent", "NovaStreamAI/1.0 (Android; ExoPlayer)")
                setRequestProperty("Range", "bytes=0-2048")
                setRequestProperty("Accept", "*/*")
            }

            connection.connect()
            val latency = System.currentTimeMillis() - startTime
            val responseCode = connection.responseCode
            val isReachable = responseCode in 200..399
            val contentType = connection.contentType
            val serverHeader = connection.getHeaderField("Server")

            StreamHealth(
                url = urlStr,
                isReachable = isReachable,
                httpStatus = responseCode,
                responseTimeMs = latency,
                contentType = contentType,
                serverHeader = serverHeader,
                errorDetails = if (!isReachable) "HTTP $responseCode ${connection.responseMessage}" else null
            )
        } catch (e: Exception) {
            val latency = System.currentTimeMillis() - startTime
            StreamHealth(
                url = urlStr,
                isReachable = false,
                httpStatus = 0,
                responseTimeMs = latency,
                contentType = null,
                serverHeader = null,
                errorDetails = e.message ?: "Connection failed"
            )
        } finally {
            connection?.disconnect()
        }
    }
}
