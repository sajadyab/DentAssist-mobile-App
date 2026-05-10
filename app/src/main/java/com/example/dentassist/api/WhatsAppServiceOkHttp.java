package com.example.dentassist.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.dentassist.app.network.NetworkConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WhatsAppServiceOkHttp {

    private static final String WHATSAPP_SERVER_URL = NetworkConfig.WHATSAPP_SERVER_URL;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static WhatsAppServiceOkHttp instance;
    private final OkHttpClient client;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private WhatsAppServiceOkHttp() {
        // Create client with longer timeout and cleartext allowed
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized WhatsAppServiceOkHttp getInstance() {
        if (instance == null) {
            instance = new WhatsAppServiceOkHttp();
        }
        return instance;
    }

    // ==================== SEND WHATSAPP MESSAGE ====================

    public interface WhatsAppCallback {
        void onSuccess();
        void onError(String error);
    }

    public void sendWhatsAppMessage(String phoneNumber, String message, WhatsAppCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("phone", phoneNumber);
            json.put("message", message);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(WHATSAPP_SERVER_URL + "/send")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("WhatsAppService", "Failed to send message", e);
                    mainHandler.post(() -> callback.onError(e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        mainHandler.post(() -> callback.onSuccess());
                        Log.d("WhatsAppService", "Message sent to " + phoneNumber);
                    } else {
                        String error = "Server returned: " + response.code();
                        mainHandler.post(() -> callback.onError(error));
                    }
                    response.close();
                }
            });
        } catch (Exception e) {
            mainHandler.post(() -> callback.onError(e.getMessage()));
        }
    }

    // ==================== CHECK SERVER HEALTH ====================

    public interface HealthCallback {
        void onHealthCheck(boolean isReady, int queueSize);
        void onError(String error);
    }

    public void checkServerHealth(HealthCallback callback) {
        Request request = new Request.Builder()
                .url(WHATSAPP_SERVER_URL + "/health")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        boolean ready = json.optBoolean("ready", false);
                        int queue = json.optInt("queue", 0);
                        mainHandler.post(() -> callback.onHealthCheck(ready, queue));
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Invalid JSON response"));
                    }
                } else {
                    mainHandler.post(() -> callback.onError("Server returned: " + response.code()));
                }
                response.close();
            }
        });
    }

    // ==================== TEST CONNECTION ====================

    public interface TestCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public void testConnection(TestCallback callback) {
        Request request = new Request.Builder()
                .url(WHATSAPP_SERVER_URL + "/health")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Cannot reach server at " + WHATSAPP_SERVER_URL +
                        "\nError: " + e.getMessage() +
                        "\n\nMake sure:\n- Phone and PC on same WiFi\n- Node.js server is running\n- IP address is correct"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess("✅ Server is reachable! Response: " + response.code()));
                } else {
                    mainHandler.post(() -> callback.onError("Server returned: " + response.code()));
                }
                response.close();
            }
        });
    }
}