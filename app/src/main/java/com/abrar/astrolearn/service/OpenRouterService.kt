package com.abrar.astrolearn.service

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class OpenRouterService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    private val apiKey = "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"
    private val baseUrl = "https://openrouter.ai/api/v1/chat/completions"

    fun explainTopicForChild(topicName: String, callback: (String?, String?) -> Unit) {
        val json = JSONObject().apply {
            put("model", "qwen/qwen3-235b-a22b-2507:free")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", "explain $topicName simply for a learner.")
                })
            })
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(baseUrl)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "astroai.local")
            .addHeader("X-Title", "AstroAI Android App")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val choices = jsonResponse.getJSONArray("choices")
                        if (choices.length() > 0) {
                            val message = choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                            callback(message, null)
                        } else {
                            callback(null, "No response from AI")
                        }
                    } else {
                        callback(null, "API Error: ${response.code} ${response.message}")
                    }
                } catch (e: Exception) {
                    callback(null, "Error parsing response: ${e.message}")
                }
            }
        })
    }
}
