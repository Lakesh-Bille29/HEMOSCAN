package com.example.brainhemorrhage;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.color.DynamicColors;

public class BrainHemorrhageApp extends Application {
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // Restore dark mode preference before first activity launches
        SharedPreferences prefs = getSharedPreferences("HemoScanPrefs", Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Apply Material You dynamic color to all activities
        DynamicColors.applyToActivitiesIfAvailable(this);

        // Create FCM notification channels on startup (required for Android 8+)
        createNotificationChannels();

        // Register FCM token if user is already logged in (app updated / reinstalled)
        registerFcmTokenIfLoggedIn(prefs);
    }

    /** Creates all notification channels. Safe to call multiple times — Android ignores duplicates. */
    private void createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        // Channel 1: Clinical Alerts — urgent, high priority
        NotificationChannel alertsChannel = new NotificationChannel(
                "hemoscan_alerts",
                "Clinical Alerts",
                NotificationManager.IMPORTANCE_HIGH);
        alertsChannel.setDescription("Urgent notifications for critical scan findings and system alerts.");
        alertsChannel.enableVibration(true);
        alertsChannel.setShowBadge(true);
        manager.createNotificationChannel(alertsChannel);

        // Channel 2: Scan Results — moderate priority, scan completed
        NotificationChannel resultsChannel = new NotificationChannel(
                "hemoscan_results",
                "Scan Results",
                NotificationManager.IMPORTANCE_DEFAULT);
        resultsChannel.setDescription("Notifications when an AI analysis result is ready to review.");
        resultsChannel.enableVibration(false);
        resultsChannel.setShowBadge(true);
        manager.createNotificationChannel(resultsChannel);
    }

    /**
     * If the user is already logged in (e.g. after app update), re-register their
     * FCM token with the backend. Firebase may have issued a new token since last run.
     */
    private void registerFcmTokenIfLoggedIn(SharedPreferences prefs) {
        String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        // Use Firebase Messaging to get the current token asynchronously
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {
                        if (token != null && !token.isEmpty()) {
                            prefs.edit().putString("fcm_token", token).apply();
                            HemoScanFirebaseService.sendTokenToServer(email, token);
                        }
                    })
                    .addOnFailureListener(e ->
                            android.util.Log.w("BrainHemorrhageApp",
                                    "FCM token fetch failed (google-services.json missing?): "
                                    + e.getMessage()));
        } catch (Exception e) {
            // Firebase not initialized — google-services.json not yet added
            android.util.Log.w("BrainHemorrhageApp",
                    "Firebase not available: " + e.getMessage());
        }
    }
}