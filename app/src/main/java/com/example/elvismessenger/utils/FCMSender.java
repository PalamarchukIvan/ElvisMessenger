package com.example.elvismessenger.utils;


import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSender {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAAKoR96eA:APA91bFKGpgizQe5SWg9tYwSdAUTXvj22bfscAhLGR0PxQ0KOXJhkOZ0QostGq7gIu0vIsuv5uyseRSHyzC7_2znAL7UqRqTbHvpjT64EmnMW7AnbCka350x_e8HtQwZKOOMX3JzdkN2";

        public static void pushNotification(Context context, String token, String title, String message, String from, String to) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            RequestQueue queue = Volley.newRequestQueue(context);
            try {
                JSONObject json = new JSONObject();
                json.put("to", token);
                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", message);
                JSONObject data = new JSONObject();
                data.put(NotificationService.ACTION_KEY, NotificationService.ACTION_NOTIFICATION);
                String dataMessage = from + "_" + to;
                data.put(NotificationService.MESSAGE_KEY, dataMessage);
                json.put("notification", notification);
                json.put("data", data);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                        response -> {
                            System.err.println("FCM: " + response.toString());
                        }, error -> {
                             System.err.println("FCM: " + error.toString());
                        }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("Authorization", SERVER_KEY);
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };

                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
}