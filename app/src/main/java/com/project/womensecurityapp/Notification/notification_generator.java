package com.project.womensecurityapp.Notification;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class notification_generator {

    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "key=" + "AIzaSyDo7twRdgfEnI2OVNitEEhlHyykzA22PE0";
    private String contentType = "application/json";


       public void send_notification(String notification_title, String notification_body, Context context)
       {

        String TOPIC,NOTIFICATION_TITLE,NOTIFICATION_MESSAGE;

                    TOPIC = "/topics/hello"; //topic must match with what the receiver subscribed to
                    NOTIFICATION_TITLE = notification_title;
                    NOTIFICATION_MESSAGE = notification_body;

                    JSONObject notification = new JSONObject();
                    JSONObject notificationBody = new JSONObject();
                    try {
                        notificationBody.put("title", NOTIFICATION_TITLE);
                        notificationBody.put("message", NOTIFICATION_MESSAGE);

                        notification.put("to", TOPIC);
                        notification.put("data", notificationBody);
                    } catch (JSONException e) {
                        Log.e("123", "onCreate: " + e.getMessage());
                    }

                    if (!NOTIFICATION_TITLE.isEmpty()) {

                        if (!NOTIFICATION_MESSAGE.isEmpty()) {

                            sendNotification(notification,context);
                        }
                        else
                            Toast.makeText(context,"Message field can't be empty", Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(context,"Title field can't be empty", Toast.LENGTH_LONG).show();
                }

        private void sendNotification(JSONObject notification, final Context context) {

            Log.e("kc","kj");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,FCM_API,notification,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("123", "onResponse: " + response.toString());

                            Toast.makeText(context,"Notification successfully send, thank you", Toast.LENGTH_LONG).show();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                            Log.i("123", "onErrorResponse: Didn't work");
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization",serverKey);
                    params.put("Content-Type", contentType);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            int socketTimeout = 1000 * 60;   // 60 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);
        }
    }