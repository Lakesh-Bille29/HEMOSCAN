package com.example.brainhemorrhage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bumptech.glide.Glide;
import com.example.brainhemorrhage.api.BrainScanApi;
import com.example.brainhemorrhage.api.RetrofitClient;
import com.example.brainhemorrhage.api.ScanResponse;
import com.example.brainhemorrhage.api.DashboardResponse;
import com.example.brainhemorrhage.api.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final long POLL_INTERVAL_MS = 30_000; // 30 seconds
    private static final String TAG = "DashboardFragment";

    private TextView doctorNameText, totalCountText, normalCountText, abnormalCountText;
    private ImageView toolbarProfileImage;
    private RecyclerView recentScansRecyclerView;
    private BottomNavigationView bottomNavigation;
    private SharedPreferences prefs;

    private ScansAdapter scansAdapter;

    // Delta-sync baseline: stores server_time returned by the last successful full fetch
    private long lastSyncServerTime = 0L;

    // Handler for 30-second background polling
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private Runnable pollRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        prefs = requireContext().getSharedPreferences("HemoScanPrefs", Context.MODE_PRIVATE);

        initViews(view);
        loadProfile();
        setupRecentScans();
        setupClickListeners(view);
        setupBottomNavigation();

        // Apply premium touch/click bounce animations
        AnimationHelper.applyBouncePress(view.findViewById(R.id.newScanCard));
        AnimationHelper.applyBouncePress(view.findViewById(R.id.totalCard));
        AnimationHelper.applyBouncePress(view.findViewById(R.id.normalCard));
        AnimationHelper.applyBouncePress(view.findViewById(R.id.abnormalCard));
        AnimationHelper.applyBouncePress(view.findViewById(R.id.profileCard));

        // Staggered sequence slide-in entry animation
        AnimationHelper.animateViewsInSequence(
            view.findViewById(R.id.doctorNameText),
            view.findViewById(R.id.subtitleText),
            view.findViewById(R.id.newScanCard),
            view.findViewById(R.id.totalCard),
            view.findViewById(R.id.normalCard),
            view.findViewById(R.id.abnormalCard),
            view.findViewById(R.id.historyTitle),
            recentScansRecyclerView
        );

        // Continuous staggered floating animation on main cards
        float density = getResources().getDisplayMetrics().density;
        AnimationHelper.applyFloatingEffect(view.findViewById(R.id.newScanCard), 4f * density, 3000, 0);
        AnimationHelper.applyFloatingEffect(view.findViewById(R.id.totalCard), 4f * density, 3200, 200);
        AnimationHelper.applyFloatingEffect(view.findViewById(R.id.normalCard), 4f * density, 3400, 400);
        AnimationHelper.applyFloatingEffect(view.findViewById(R.id.abnormalCard), 4f * density, 3600, 600);

        return view;
    }

    private void initViews(View view) {
        doctorNameText          = view.findViewById(R.id.doctorNameText);
        toolbarProfileImage     = view.findViewById(R.id.toolbarProfileImage);
        recentScansRecyclerView = view.findViewById(R.id.recentScansRecyclerView);
        totalCountText          = view.findViewById(R.id.totalCountText);
        normalCountText         = view.findViewById(R.id.normalCountText);
        abnormalCountText       = view.findViewById(R.id.abnormalCountText);
        bottomNavigation        = view.findViewById(R.id.bottomNavigation);

        recentScansRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadProfile() {
        String name = prefs.getString("name", "");
        if (name.isEmpty()) {
            doctorNameText.setText("Hello!");
        } else {
            doctorNameText.setText("Hello, " + name);
        }
        
        String specialty = prefs.getString("specialty", "");
        TextView subtitleText = getView() != null ? getView().findViewById(R.id.subtitleText) : null;
        if (subtitleText != null) {
            if (specialty.isEmpty()) {
                subtitleText.setText("Good to see you back!");
            } else {
                subtitleText.setText(specialty);
            }
        }
        
        String photoUri = prefs.getString("profile_image", null);
        if (photoUri != null && !photoUri.isEmpty() && toolbarProfileImage != null) {
            String imageUrl = photoUri;
            if (!imageUrl.startsWith("http") && !imageUrl.startsWith("file") && !imageUrl.startsWith("content")) {
                imageUrl = RetrofitClient.getBaseUrl() + imageUrl;
            }
            toolbarProfileImage.setImageTintList(null); // Clear the white XML tint
            toolbarProfileImage.setPadding(0, 0, 0, 0); // Clear padding so photo fills the circle
            toolbarProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(toolbarProfileImage);
        } else if (toolbarProfileImage != null) {
            toolbarProfileImage.setImageResource(R.drawable.ic_person);
            toolbarProfileImage.setImageTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), android.R.color.white));
            float density = getResources().getDisplayMetrics().density;
            int padding = (int) (8 * density);
            toolbarProfileImage.setPadding(padding, padding, padding, padding);
            toolbarProfileImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    private void setupRecentScans() {
        scansAdapter = new ScansAdapter(new ArrayList<>(), scan -> {
            Bundle args = new Bundle();
            args.putString("patientId", scan.getDbPatientId());
            args.putString("patientName", scan.getPatientName());
            args.putString("patientAge", scan.getAge());
            args.putString("patientGender", scan.getGender());
            Navigation.findNavController(requireView()).navigate(R.id.action_dashboard_to_patientDetail, args);
        });

        recentScansRecyclerView.setAdapter(scansAdapter);
        fetchRecentScans();
    }

    private void fetchRecentScans() {
        String doctorEmail = prefs.getString("email", "");
        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
        api.getDashboard(doctorEmail).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    DashboardResponse dash = response.body();
                    int total = dash.getTotalScans();
                    int normal = dash.getNormalScans();
                    int abnormal = dash.getAbnormalScans();

                    if (totalCountText != null) totalCountText.setText(String.valueOf(total));
                    if (normalCountText != null) normalCountText.setText(String.valueOf(normal));
                    if (abnormalCountText != null) abnormalCountText.setText(String.valueOf(abnormal));

                    List<ScanResponse.ScanItemDto> dtos = dash.getData();
                    List<ScanItem> scans = new ArrayList<>();
                    if (dtos != null) {
                        for (int i = 0; i < dtos.size(); i++) {
                            ScanResponse.ScanItemDto dto = dtos.get(i);
                            ScanItem item = new ScanItem(
                                Integer.parseInt(dto.getId()),
                                dto.getDoctor_email(),
                                dto.getPatient_name(),
                                dto.getResult(),
                                dto.getDate_added(),
                                dto.getImage_path()
                            );
                            item.setAge(dto.getPatient_age());
                            item.setGender(dto.getPatient_gender());
                            item.setDbPatientId(dto.getPatient_id());
                            scans.add(item);
                        }
                    }

                    if (scansAdapter != null) {
                        scansAdapter.updateData(scans);
                    }

                    if (dash.getServerTime() > 0) {
                        lastSyncServerTime = dash.getServerTime();
                    }

                    startPolling();
                } else {
                    if (totalCountText != null) totalCountText.setText("0");
                    if (normalCountText != null) normalCountText.setText("0");
                    if (abnormalCountText != null) abnormalCountText.setText("0");
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                try {
                    List<ScanItem> scans = DatabaseHelper.getInstance(requireContext()).getAllLocalPatients();
                    int total = scans.size();
                    int normal = 0;
                    int abnormal = 0;

                    List<ScanItem> recentScans = new ArrayList<>();
                    for (int i = 0; i < scans.size(); i++) {
                        ScanItem item = scans.get(i);
                        String result = item.getResult();
                        if (result != null && result.toLowerCase().contains("abnormal")) {
                            abnormal++;
                        } else {
                            normal++;
                        }
                        if (i < 5) {
                            recentScans.add(item);
                        }
                    }

                    if (totalCountText != null) totalCountText.setText(String.valueOf(total));
                    if (normalCountText != null) normalCountText.setText(String.valueOf(normal));
                    if (abnormalCountText != null) abnormalCountText.setText(String.valueOf(abnormal));

                    if (scansAdapter != null) {
                        scansAdapter.updateData(recentScans);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ── 30-second delta sync polling ───────────────────────────────────────

    private void startPolling() {
        stopPolling(); // clear any existing runnable
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || getContext() == null) return;
                doDeltaSync();
                pollHandler.postDelayed(this, POLL_INTERVAL_MS);
            }
        };
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
    }

    private void stopPolling() {
        if (pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
            pollRunnable = null;
        }
    }

    /**
     * Fetches only scans created after lastSyncServerTime.
     * Merges new items into the existing adapter list without clearing it.
     */
    private void doDeltaSync() {
        String doctorEmail = prefs.getString("email", "");
        if (doctorEmail.isEmpty() || lastSyncServerTime == 0L) return;

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
        api.getPatientScansDelta(doctorEmail, lastSyncServerTime).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null
                        && "success".equals(response.body().getStatus())) {
                    List<ScanResponse.ScanItemDto> dtos = response.body().getData();
                    if (dtos == null || dtos.isEmpty()) return;

                    // Build ScanItem list from delta (newest first, from server)
                    List<ScanItem> newItems = new ArrayList<>();
                    for (ScanResponse.ScanItemDto dto : dtos) {
                        ScanItem item = new ScanItem(
                            Integer.parseInt(dto.getId()),
                            dto.getDoctor_email(),
                            dto.getPatient_name(),
                            dto.getResult(),
                            dto.getDate_added(),
                            dto.getImage_path()
                        );
                        item.setAge(dto.getPatient_age());
                        item.setGender(dto.getPatient_gender());
                        item.setDbPatientId(dto.getPatient_id());
                        newItems.add(item);
                    }

                    // Prepend new items to existing adapter data
                    if (scansAdapter != null && !newItems.isEmpty()) {
                        List<ScanItem> current = scansAdapter.getData();
                        newItems.addAll(current);
                        // Keep only first 5 in the recent list
                        scansAdapter.updateData(newItems.size() > 5 ? newItems.subList(0, 5) : newItems);

                        // Update stat counters
                        int total = newItems.size();
                        int normal = 0, abnormal = 0;
                        for (ScanItem s : newItems) {
                            if (s.getResult() != null && s.getResult().toLowerCase().contains("abnormal")) abnormal++;
                            else normal++;
                        }
                        if (totalCountText != null) totalCountText.setText(String.valueOf(total));
                        if (normalCountText != null) normalCountText.setText(String.valueOf(normal));
                        if (abnormalCountText != null) abnormalCountText.setText(String.valueOf(abnormal));
                    }

                    // Advance the baseline
                    if (response.body().getServerTime() > 0) {
                        lastSyncServerTime = response.body().getServerTime();
                    }
                }
            }

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                // Silent fail — retry on next interval
            }
        });
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.newScanCard).setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_patientDetails));
        
        view.findViewById(R.id.totalCard).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("filterType", "total");
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_history, args);
        });
            
        view.findViewById(R.id.normalCard).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("filterType", "normal");
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_history, args);
        });
            
        view.findViewById(R.id.abnormalCard).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("filterType", "abnormal");
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_history, args);
        });

        view.findViewById(R.id.viewAllText).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("filterType", "total");
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_history, args);
        });
            
        view.findViewById(R.id.profileCard).setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_settings));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) return true;

            androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.dashboardFragment, false)
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build();

            if (id == R.id.nav_scan) {
                Navigation.findNavController(requireView()).navigate(R.id.action_dashboard_to_patientDetails, null, navOptions);
                return true;
            }
            if (id == R.id.nav_history) {
                Navigation.findNavController(requireView()).navigate(R.id.action_dashboard_to_history, null, navOptions);
                return true;
            }
            if (id == R.id.nav_settings) {
                Navigation.findNavController(requireView()).navigate(R.id.action_dashboard_to_settings, null, navOptions);
                return true;
            }
            return false;
        });
    }

    private void refreshProfileFromServer() {
        String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        // Debounce: skip if SplashFragment already synced within the last 30 seconds
        long lastSync = prefs.getLong("last_server_sync_ms", 0L);
        if (System.currentTimeMillis() - lastSync < 30_000L) return;
        prefs.edit().putLong("last_server_sync_ms", System.currentTimeMillis()).apply();

        BrainScanApi api = RetrofitClient.getRetrofitInstance().create(BrainScanApi.class);
        api.checkUser(email).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (getContext() == null || !isAdded()) return;
                if (response.isSuccessful() && response.body() != null 
                        && "success".equals(response.body().getStatus()) && response.body().getUser() != null) {
                    LoginResponse.User user = response.body().getUser();
                    SharedPreferences.Editor editor = prefs.edit();
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
                    editor.apply();

                    // Reload UI
                    loadProfile();

                    // Apply theme preference in case it changed
                    androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                            user.getDark_mode() == 1 
                                    ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES 
                                    : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                    );
                } else if (response.body() != null && "error".equals(response.body().getStatus())) {
                    // Account was deleted, logout
                    prefs.edit().clear().apply();
                    View v = getView();
                    if (v != null) {
                        try {
                            Navigation.findNavController(v).navigate(R.id.loginFragment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Fail silently, use cached profile
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        }
        refreshProfileFromServer();
        fetchRecentScans();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling(); // stop polling to avoid memory leaks when fragment is removed
    }
}
