package com.example.brainhemorrhage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.brainhemorrhage.api.BaseResponse;
import com.example.brainhemorrhage.api.BrainScanApi;
import com.example.brainhemorrhage.api.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HemoScanFirebaseService — handles two events:
 *
 *  1. onNewToken()         — called when FCM assigns a new registration token.
 *                            We save it to SharedPreferences and sync it to the backend.
 *
 *  2. onMessageReceived()  — called when a data or notification message arrives
 *                            while the app is in the FOREGROUND. Background/killed
 *                            state notifications are handled by the FCM SDK automatically
 *                            using the <meta-data> in AndroidManifest.xml.
 */
public class HemoScanFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "HemoScanFCM";
    private static final String CHANNEL_ALERTS    = "hemoscan_alerts";
    private static final String CHANNEL_RESULTS   = "hemoscan_results";

    // ── Token Management ──────────────────────────────────────────────────────────

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM token refreshed: " + token.substring(0, 20) + "…");

        // Persist locally
        SharedPreferences prefs = getSharedPreferences("HemoScanPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("fcm_token", token).apply();

        // Sync to backend (best-effort — silent fail if offline)
        String email = prefs.getString("email", "");
        if (!email.isEmpty()) {
            sendTokenToServer(email, token);
        }
    }

    /** Saves or updates the FCM token on the PHP backend. */
    public static void sendTokenToServer(String email, String token) {
        BrainScanApi api = RetrofitClient.getRetrofitInstance().create(BrainScanApi.class);
        api.saveFcmToken(email, token, "android").enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call,
                                   @NonNull Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Token saved: " + response.body().getStatus());
                }
            }
            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "Token save failed (offline?): " + t.getMessage());
            }
        });
    }

    // ── Message Handling ──────────────────────────────────────────────────────────

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "FCM message received from: " + remoteMessage.getFrom());

        String title = "HemoScan";
        String body  = "You have a new notification.";

        // Prefer notification payload, fall back to data payload
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                title = remoteMessage.getNotification().getTitle();
            if (remoteMessage.getNotification().getBody() != null)
                body = remoteMessage.getNotification().getBody();
        } else if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsKey("title"))
                title = remoteMessage.getData().get("title");
            if (remoteMessage.getData().containsKey("body"))
                body = remoteMessage.getData().get("body");
        }

        // Determine channel based on message type
        String type    = remoteMessage.getData().getOrDefault("type", "alert");
        String channel = "results".equals(type) ? CHANNEL_RESULTS : CHANNEL_ALERTS;

        showNotification(title, body, channel, remoteMessage.getData());
    }

    // ── Notification Builder ──────────────────────────────────────────────────────

    private void showNotification(String title, String body, String channelId,
                                   java.util.Map<String, String> data) {
        // Build deep-link intent — opens MainActivity which restores last nav state
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Pass any deep-link extras from the data payload
        if (data.containsKey("scan_id")) {
            intent.putExtra("scan_id", data.get("scan_id"));
        }
        if (data.containsKey("navigate_to")) {
            intent.putExtra("navigate_to", data.get("navigate_to"));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Ensure the channel exists (safe to call repeatedly — Android ignores duplicates)
        ensureChannel(manager, channelId);

        // Use unique ID so multiple notifications don't replace each other
        int notifId = (int) System.currentTimeMillis();
        manager.notify(notifId, notifBuilder.build());
    }

    private void ensureChannel(NotificationManager manager, String channelId) {
        if (manager.getNotificationChannel(channelId) != null) return;

        String name;
        String description;
        int importance;

        if (CHANNEL_RESULTS.equals(channelId)) {
            name        = "Scan Results";
            description = "Notifications when an AI scan result is ready";
            importance  = NotificationManager.IMPORTANCE_HIGH;
        } else {
            name        = "Clinical Alerts";
            description = "Critical and urgent clinical notifications";
            importance  = NotificationManager.IMPORTANCE_HIGH;
        }

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);
    }
}
