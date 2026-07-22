package com.example.brainhemorrhage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SplashFragment extends Fragment {

    private View pulseRing1, pulseRing2;
    private LinearLayout splashContent;
    private View logoCard, versionBadge;
    private TextView splashTitle, splashTagline;
    private LinearLayout loadingSection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pulseRing1    = view.findViewById(R.id.pulseRing1);
        pulseRing2    = view.findViewById(R.id.pulseRing2);
        splashContent = view.findViewById(R.id.splashContent);
        logoCard      = view.findViewById(R.id.logoCard);
        splashTitle   = view.findViewById(R.id.splashTitle);
        splashTagline = view.findViewById(R.id.splashTagline);
        versionBadge  = view.findViewById(R.id.versionBadge);
        loadingSection = view.findViewById(R.id.loadingSection);

        startEntranceAnimations();
    }

    private void startEntranceAnimations() {
        // ── Step 1: logo card pops in (scale + fade) ──────────────────────────
        splashContent.setAlpha(1f);
        logoCard.setAlpha(0f);
        logoCard.setScaleX(0.4f);
        logoCard.setScaleY(0.4f);

        ObjectAnimator logoAlpha  = ObjectAnimator.ofFloat(logoCard, "alpha",  0f, 1f);
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoCard, "scaleX", 0.4f, 1.05f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoCard, "scaleY", 0.4f, 1.05f, 1f);

        AnimatorSet logoSet = new AnimatorSet();
        logoSet.playTogether(logoAlpha, logoScaleX, logoScaleY);
        logoSet.setDuration(550);
        logoSet.setInterpolator(new DecelerateInterpolator(1.5f));

        // ── Step 2: title slides up + fades in ────────────────────────────────
        splashTitle.setAlpha(0f);
        splashTitle.setTranslationY(20f);
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(splashTitle, "alpha", 0f, 1f);
        ObjectAnimator titleY     = ObjectAnimator.ofFloat(splashTitle, "translationY", 20f, 0f);
        AnimatorSet titleSet = new AnimatorSet();
        titleSet.playTogether(titleAlpha, titleY);
        titleSet.setDuration(400);

        // ── Step 3: tagline + version fade in ─────────────────────────────────
        splashTagline.setAlpha(0f);
        versionBadge.setAlpha(0f);
        ObjectAnimator tagAlpha  = ObjectAnimator.ofFloat(splashTagline, "alpha", 0f, 1f);
        ObjectAnimator badgeAlpha = ObjectAnimator.ofFloat(versionBadge, "alpha", 0f, 1f);
        AnimatorSet tagSet = new AnimatorSet();
        tagSet.playTogether(tagAlpha, badgeAlpha);
        tagSet.setDuration(350);

        // ── Step 4: loading section fades in ──────────────────────────────────
        loadingSection.setAlpha(0f);
        ObjectAnimator loadingAlpha = ObjectAnimator.ofFloat(loadingSection, "alpha", 0f, 1f);
        loadingAlpha.setDuration(300);

        // ── Chain all entrance steps ──────────────────────────────────────────
        AnimatorSet entrance = new AnimatorSet();
        entrance.play(logoSet)
                .before(titleSet);
        entrance.play(titleSet)
                .before(tagSet);
        entrance.play(tagSet)
                .before(loadingAlpha);

        entrance.setStartDelay(200);

        entrance.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startPulseRingAnimation();
                startLogoPulse();
                navigateAfterDelay();
            }
        });

        entrance.start();
    }

    /** Repeated outward expand + fade rings for heartbeat effect */
    private void startPulseRingAnimation() {
        animatePulseRing(pulseRing1, 0);
        animatePulseRing(pulseRing2, 600);
    }

    private void animatePulseRing(View ring, long startDelay) {
        if (ring == null) return;
        ring.setAlpha(0.6f);
        ring.setScaleX(0.3f);
        ring.setScaleY(0.3f);

        ObjectAnimator scaleX  = ObjectAnimator.ofFloat(ring, "scaleX", 0.3f, 1.3f);
        ObjectAnimator scaleY  = ObjectAnimator.ofFloat(ring, "scaleY", 0.3f, 1.3f);
        ObjectAnimator alpha   = ObjectAnimator.ofFloat(ring, "alpha",  0.6f, 0f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setDuration(2000);
        set.setStartDelay(startDelay);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAdded()) animatePulseRing(ring, 0);
            }
        });
        set.start();
    }

    /** Subtle heartbeat pulse on the logo itself */
    private void startLogoPulse() {
        if (logoCard == null) return;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoCard, "scaleX", 1f, 1.06f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoCard, "scaleY", 1f, 1.06f, 1f);
        scaleX.setDuration(1800);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setDuration(1800);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();
    }

    private void navigateAfterDelay() {
        View v = getView();
        if (v == null) return;
        v.postDelayed(() -> {
            if (!isAdded()) return;
            try {
                android.content.SharedPreferences prefs =
                    requireContext().getSharedPreferences("HemoScanPrefs", android.content.Context.MODE_PRIVATE);
                String email = prefs.getString("email", "");
                
                if (email.isEmpty()) {
                    Navigation.findNavController(v).navigate(R.id.action_splash_to_login);
                } else {
                    // Refresh profile details and settings from server
                    com.example.brainhemorrhage.api.BrainScanApi api = 
                            com.example.brainhemorrhage.api.RetrofitClient.getRetrofitInstance().create(com.example.brainhemorrhage.api.BrainScanApi.class);
                    api.checkUser(email).enqueue(new retrofit2.Callback<com.example.brainhemorrhage.api.LoginResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.brainhemorrhage.api.LoginResponse> call, retrofit2.Response<com.example.brainhemorrhage.api.LoginResponse> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful() && response.body() != null) {
                                com.example.brainhemorrhage.api.LoginResponse res = response.body();
                                if ("success".equals(res.getStatus()) && res.getUser() != null) {
                                    com.example.brainhemorrhage.api.LoginResponse.User user = res.getUser();
                                    android.content.SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("name", user.getName());
                                    editor.putString("mobile", user.getMobile());
                                    editor.putString("gender", user.getGender());
                                    editor.putString("specialty", user.getSpecialty() != null ? user.getSpecialty() : "");
                                    editor.putString("profile_image", user.getProfile_image() != null ? user.getProfile_image() : "");
                                    editor.putString("bio", user.getBio() != null ? user.getBio() : "");
                                    editor.putString("hospital", user.getHospital() != null ? user.getHospital() : "");
                                    editor.putString("license", user.getLicense() != null ? user.getLicense() : "");
                                    editor.putInt("years_exp", user.getYears_exp());
                                    editor.putBoolean("dark_mode", user.getDark_mode() == 1);
                                    editor.putString("language", user.getLanguage() != null ? user.getLanguage() : "English");
                                    editor.putBoolean("daily_summary", user.getDaily_summary() == 1);
                                    editor.putBoolean("sound", user.getSound() == 1);
                                    editor.putBoolean("vibration", user.getVibration() == 1);
                                    editor.putInt("theme_mode", user.getTheme_mode());
                                    // Mark sync timestamp so Dashboard/Settings skip a redundant checkUser call
                                    editor.putLong("last_server_sync_ms", System.currentTimeMillis());
                                    editor.apply();

                                    // Apply dark mode right away
                                    androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                                            user.getDark_mode() == 1 
                                                    ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES 
                                                    : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                                    );

                                    Navigation.findNavController(v).navigate(R.id.action_splash_to_dashboard);
                                } else {
                                    // Session invalid or account deleted, log out
                                    prefs.edit().clear().apply();
                                    Navigation.findNavController(v).navigate(R.id.action_splash_to_login);
                                }
                            } else {
                                // Server returned error, navigate with local cached credentials
                                Navigation.findNavController(v).navigate(R.id.action_splash_to_dashboard);
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.brainhemorrhage.api.LoginResponse> call, Throwable t) {
                            if (!isAdded()) return;
                            // Offline or connection failed, load with cached settings
                            Navigation.findNavController(v).navigate(R.id.action_splash_to_dashboard);
                        }
                    });
                }
            } catch (Exception e) {
                // Navigation already happened
            }
        }, 3200);
    }
}
