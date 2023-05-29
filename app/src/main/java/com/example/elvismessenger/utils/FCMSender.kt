package com.example.elvismessenger.utils

import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object FCMSender {
    private const val BASE_URL = "https://fcm.googleapis.com/fcm/send"
    private const val SERVER_KEY =
        "key=AAAAKoR96eA:APA91bFKGpgizQe5SWg9tYwSdAUTXvj22bfscAhLGR0PxQ0KOXJhkOZ0QostGq7gIu0vIsuv5uyseRSHyzC7_2znAL7UqRqTbHvpjT64EmnMW7AnbCka350x_e8HtQwZKOOMX3JzdkN2"

    fun pushNotification(
        context: Context?,
        token: String?,
        title: String? = "",
        message: String? = "",
        from: String = "",
        to: String = "",
        action: String,
        data_: String = ""
    ) {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val queue = Volley.newRequestQueue(context)
        try {
            val json = JSONObject()
            json.put("to", token)
            val notification = JSONObject()
            notification.put("title", title)
            notification.put("body", message)
            val data = JSONObject()
            data.put(NotificationService.ACTION_KEY, action)
            val dataMessage = if(data_ == "") {
                from + "_&&&_" + to
            } else {
                data_
            }
            data.put(NotificationService.MESSAGE_KEY, dataMessage)
            if(title != "" && message != ""){
                json.put("notification", notification)
            }
            json.put("data", data)
            val jsonObjectRequest: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, BASE_URL, json,
                    Response.Listener { response: JSONObject ->
                        System.err.println(
                            "FCM: $response"
                        )
                    }, Response.ErrorListener { error: VolleyError ->
                        System.err.println(
                            "FCM: $error"
                        )
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["Authorization"] = SERVER_KEY
                        params["Content-Type"] = "application/json"
                        return params
                    }
                }
            queue.add(jsonObjectRequest)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
    }
}