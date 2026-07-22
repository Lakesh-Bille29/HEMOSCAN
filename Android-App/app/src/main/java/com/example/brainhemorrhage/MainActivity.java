package com.example.brainhemorrhage;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    /**
     * Called before onCreate. Applies the user's saved language so that every
     * string resource, layout inflation, and fragment render uses the correct locale.
     * Without this, LocaleHelper.setLocale() calls from SettingsFragment have no
     * effect on the Activity-level context used for resource resolution.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply Dark Mode from preferences
        android.content.SharedPreferences prefs = getSharedPreferences("HemoScanPrefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                           : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        // Edge-to-edge: let content draw behind the status bar / notch.
        // Each fragment controls its own insets via fitsSystemWindows or
        // ViewCompat.setOnApplyWindowInsetsListener so buttons are never obscured.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        }
    }
}
