package com.example.brainhemorrhage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;
import com.example.brainhemorrhage.api.BaseResponse;
import com.example.brainhemorrhage.api.BrainScanApi;
import com.example.brainhemorrhage.api.RetrofitClient;
import com.example.brainhemorrhage.api.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SettingsFragment extends Fragment {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,}$"
    );

    private static final String PREFS_NAME = "HemoScanPrefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_DAILY_SUMMARY = "daily_summary";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_VIBRATION = "vibration";

    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private com.google.android.material.floatingactionbutton.FloatingActionButton changePhotoButton;
    private ImageView themeIcon;
    private TextView languageText, doctorNameText, doctorDetailsText;


    private LinearLayout darkModeLayout, languageLayout;
    private LinearLayout changePasswordLayout;
    private SwitchCompat darkModeSwitch;
    private Button editProfileButton;
    private View logoutButton;
    private TextView deleteAccountButton;

    // New functional elements
    private View privacyPolicyLayout, termsLayout, appInfoLayout, faqsLayout, supportLayout, aboutUsLayout, helpCenterLayout;
    private View shareAppLayout, rateAppLayout, notificationsLayout, dataStorageLayout, themeSettingsLayout;
    private TextView currentThemeText;

    private SharedPreferences prefs;

    private final ActivityResultLauncher<Intent> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        profileImage.setImageURI(uri);
                        prefs.edit().putString("profile_image", uri.toString()).apply();
                        showToast("Uploading photo to server...", true);
                        uploadProfileToServer(doctorNameText.getText().toString(), doctorDetailsText.getText().toString(), uri);
                    }
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initViews(view);
        loadSettings();
        setupListeners();

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        return view;
    }

    private void initViews(View view) {
        profileImage           = view.findViewById(R.id.profileImage);
        changePhotoButton      = view.findViewById(R.id.changePhotoButton);
        doctorNameText         = view.findViewById(R.id.doctorNameText);
        doctorDetailsText      = view.findViewById(R.id.doctorDetailsText);
        editProfileButton      = view.findViewById(R.id.editProfileButton);

        
        themeIcon              = view.findViewById(R.id.themeIcon);

        darkModeLayout         = view.findViewById(R.id.darkModeLayout);
        darkModeSwitch         = view.findViewById(R.id.darkModeSwitch);
        languageLayout         = view.findViewById(R.id.languageLayout);
        languageText           = view.findViewById(R.id.languageText);

        
        changePasswordLayout   = view.findViewById(R.id.changePasswordLayout);
        logoutButton           = view.findViewById(R.id.logoutButton);
        deleteAccountButton    = view.findViewById(R.id.deleteAccountButton);

        privacyPolicyLayout    = view.findViewById(R.id.privacyPolicyLayout);
        termsLayout            = view.findViewById(R.id.termsLayout);
        appInfoLayout          = view.findViewById(R.id.appInfoLayout);
        faqsLayout             = view.findViewById(R.id.faqsLayout);
        supportLayout          = view.findViewById(R.id.supportLayout);
        aboutUsLayout          = view.findViewById(R.id.aboutUsLayout);
        helpCenterLayout       = view.findViewById(R.id.helpCenterLayout);

        themeSettingsLayout    = view.findViewById(R.id.themeSettingsLayout);
        currentThemeText       = view.findViewById(R.id.currentThemeText);
        notificationsLayout    = view.findViewById(R.id.notificationsLayout);
        dataStorageLayout      = view.findViewById(R.id.dataStorageLayout);

        shareAppLayout         = view.findViewById(R.id.shareAppLayout);
        rateAppLayout          = view.findViewById(R.id.rateAppLayout);
    }


    private void loadSettings() {
        boolean isDark      = prefs.getBoolean(KEY_DARK_MODE, false);
        String  language    = prefs.getString(KEY_LANGUAGE, "English (India)");
        
        darkModeSwitch.setChecked(isDark);
        updateThemeUI(isDark);
        languageText.setText(language);

        // Load profile data
        doctorNameText.setText(prefs.getString("name", "Dr. Sarah Johnson"));
        doctorDetailsText.setText(prefs.getString("specialty", "Senior Radiologist"));


        String photoUri = prefs.getString("profile_image", null);
        if (photoUri != null && !photoUri.isEmpty()) {
            String imageUrl = photoUri;
            if (!imageUrl.startsWith("http") && !imageUrl.startsWith("file") && !imageUrl.startsWith("content")) {
                imageUrl = RetrofitClient.getBaseUrl() + imageUrl;
            }
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.logo);
        }
    }


    private void setupListeners() {
        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerLauncher.launch(intent);
        });

        editProfileButton.setOnClickListener(v -> showEditProfileDialog());

        darkModeLayout.setOnClickListener(v -> darkModeSwitch.toggle());
        darkModeSwitch.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, checked).apply();
            updateThemeUI(checked);
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
            syncSettingsToServer();
        });

        languageLayout.setOnClickListener(v -> showLanguageDialog());

        changePasswordLayout.setOnClickListener(v -> showChangePasswordDialog());
        
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());

        // New Navigation listeners
        privacyPolicyLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_privacyPolicy));
        termsLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_terms));
        appInfoLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_appInfo));
        faqsLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_faq));
        supportLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_supportDashboard));
        aboutUsLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_aboutUs));
        helpCenterLayout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_helpCenter));

        // themeSettingsLayout is hidden (gone) — no listener needed
        notificationsLayout.setOnClickListener(v -> showNotificationsDialog());
        dataStorageLayout.setOnClickListener(v -> showDataStorageDialog());

        shareAppLayout.setOnClickListener(v -> shareApp());
        rateAppLayout.setOnClickListener(v -> rateApp());
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "HemoScan AI");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share_app) + "\nDownload now: https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName());
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + requireActivity().getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
        }
    }

    // ── Theme Settings dialog removed — Dark Mode toggle (above) is sufficient ──

    private void showNotificationsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notifications, null);
        SwitchCompat dailySum = dialogView.findViewById(R.id.dailySummarySwitch);
        SwitchCompat sound = dialogView.findViewById(R.id.soundSwitch);
        SwitchCompat vibration = dialogView.findViewById(R.id.vibrationSwitch);

        dailySum.setChecked(prefs.getBoolean(KEY_DAILY_SUMMARY, true));
        sound.setChecked(prefs.getBoolean(KEY_SOUND, true));
        vibration.setChecked(prefs.getBoolean(KEY_VIBRATION, true));

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_notifications)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    prefs.edit()
                            .putBoolean(KEY_DAILY_SUMMARY, dailySum.isChecked())
                            .putBoolean(KEY_SOUND, sound.isChecked())
                            .putBoolean(KEY_VIBRATION, vibration.isChecked())
                            .apply();
                    showToast("Notification preferences saved", true);
                    syncSettingsToServer();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDataStorageDialog() {
        long cacheSize = getCacheSize();
        String sizeStr = android.text.format.Formatter.formatFileSize(requireContext(), cacheSize);
        String currentServerUrl = RetrofitClient.getBaseUrl();

        String[] options = {
            "Clear Temporary Cache (" + sizeStr + ")",
            "Configure Server API URL"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_storage)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        clearCache();
                        showToast("Cache cleared successfully", true);
                    } else if (which == 1) {
                        showServerUrlDialog(currentServerUrl);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showServerUrlDialog(String currentUrl) {
        final TextInputEditText input = new TextInputEditText(requireContext());
        input.setText(currentUrl);
        input.setHint("e.g. http://192.168.1.6/brainscan_api/");
        input.setPadding(32, 32, 32, 32);

        new AlertDialog.Builder(requireContext())
                .setTitle("Server API Endpoint")
                .setMessage("Enter backend API base URL (must end with /):")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String newUrl = input.getText() != null ? input.getText().toString().trim() : "";
                    if (!newUrl.isEmpty()) {
                        RetrofitClient.saveServerUrl(requireContext(), newUrl);
                        showToast("Server URL saved: " + RetrofitClient.getBaseUrl(), true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private long getCacheSize() {
        long size = 0;
        File[] files = requireContext().getCacheDir().listFiles();
        if (files != null) {
            for (File f : files) size += f.length();
        }
        return size;
    }

    private void clearCache() {
        File[] files = requireContext().getCacheDir().listFiles();
        if (files != null) {
            for (File f : files) f.delete();
        }
    }

    private void showEditProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        final TextInputEditText nameInput = dialogView.findViewById(R.id.nameInput);
        final TextInputEditText specialtyInput = dialogView.findViewById(R.id.specialtyInput);
        final TextInputEditText bioInput = dialogView.findViewById(R.id.bioInput);
        final TextInputEditText hospitalInput = dialogView.findViewById(R.id.hospitalInput);
        final TextInputEditText licenseInput = dialogView.findViewById(R.id.licenseInput);
        final TextInputEditText yearsExpInput = dialogView.findViewById(R.id.yearsExpInput);

        nameInput.setText(doctorNameText.getText());
        specialtyInput.setText(doctorDetailsText.getText());
        bioInput.setText(prefs.getString("bio", ""));
        hospitalInput.setText(prefs.getString("hospital", ""));
        licenseInput.setText(prefs.getString("license", ""));
        yearsExpInput.setText(String.valueOf(prefs.getInt("years_exp", 0)));

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
                    String spec = specialtyInput.getText() != null ? specialtyInput.getText().toString().trim() : "";
                    String bio = bioInput.getText() != null ? bioInput.getText().toString().trim() : "";
                    String hosp = hospitalInput.getText() != null ? hospitalInput.getText().toString().trim() : "";
                    String lic = licenseInput.getText() != null ? licenseInput.getText().toString().trim() : "";
                    String yearsStr = yearsExpInput.getText() != null ? yearsExpInput.getText().toString().trim() : "0";
                    int years = yearsStr.isEmpty() ? 0 : Integer.parseInt(yearsStr);

                    if (name.isEmpty()) {
                        showToast("Name cannot be empty", false);
                        return;
                    }

                    if (name.length() < 2) {
                        showToast("Please enter a valid name", false);
                        return;
                    }

                    doctorNameText.setText(name);
                    doctorDetailsText.setText(spec);

                    prefs.edit()
                            .putString("name", name)
                            .putString("specialty", spec)
                            .putString("bio", bio)
                            .putString("hospital", hosp)
                            .putString("license", lic)
                            .putInt("years_exp", years)
                            .apply();

                    uploadProfileFieldsToServer(name, spec, bio, hosp, lic, years);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private File compressImage(Uri uri) {
        try {
            InputStream input = requireContext().getContentResolver().openInputStream(uri);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(input);
            if (input != null) input.close();
            
            if (bitmap == null) return null;
            
            int maxWidth = 1024;
            int maxHeight = 1024;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            
            if (width > maxWidth || height > maxHeight) {
                float ratio = (float) width / (float) height;
                if (ratio > 1) {
                    width = maxWidth;
                    height = (int) (maxWidth / ratio);
                } else {
                    height = maxHeight;
                    width = (int) (maxHeight * ratio);
                }
                bitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            
            File compressedFile = new File(requireContext().getCacheDir(), "prof_compressed_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(compressedFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            return compressedFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadProfileToServer(String name, String specialty, Uri photoUri) {
        String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        final android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage(photoUri != null ? "Uploading profile photo..." : "Saving profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);

        if (photoUri == null) {
            // ── Text-only update (no photo) ───────────────────────────────────────
            api.updateProfileTextOnly(email, name != null ? name : "", specialty != null ? specialty : "")
               .enqueue(new Callback<BaseResponse>() {
                   @Override
                   public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                       progressDialog.dismiss();
                       if (response.isSuccessful() && response.body() != null
                               && "success".equals(response.body().getStatus())) {
                           if (getContext() != null) {
                               showToast("Profile updated successfully", true);
                           }
                       } else if (getContext() != null) {
                           String msg = response.body() != null ? response.body().getMessage() : "Update failed";
                           showToast(msg, false);
                       }
                   }
                   @Override
                   public void onFailure(Call<BaseResponse> call, Throwable t) {
                       progressDialog.dismiss();
                       if (getContext() != null) {
                           showToast("Network error: " + t.getMessage(), false);
                       }
                   }
               });
            return;
        }

        // ── Multipart update (with new photo) ─────────────────────────────────
        RequestBody emailPart   = RequestBody.create(MultipartBody.FORM, email);
        RequestBody namePart    = RequestBody.create(MultipartBody.FORM, name != null ? name : "");
        RequestBody specPart    = RequestBody.create(MultipartBody.FORM, specialty != null ? specialty : "");

        MultipartBody.Part imagePart = null;
        try {
            File file = compressImage(photoUri);
            if (file != null && file.exists()) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            if (getContext() != null)
                showToast("Could not read selected image", false);
            return;
        }

        if (imagePart == null) {
            progressDialog.dismiss();
            if (getContext() != null)
                showToast("Failed to compress image", false);
            return;
        }

        api.updateProfile(emailPart, namePart, specPart, imagePart).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null
                        && "success".equals(response.body().getStatus())) {
                    if (response.body().getProfile_image() != null
                            && !response.body().getProfile_image().isEmpty()) {
                        prefs.edit().putString("profile_image", response.body().getProfile_image()).apply();
                    }
                    if (getContext() != null) {
                        showToast("Profile photo updated", true);
                    }
                } else if (getContext() != null) {
                    String msg = response.body() != null ? response.body().getMessage() : "Upload failed";
                    showToast(msg, false);
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                if (getContext() != null)
                    showToast("Network error: " + t.getMessage(), false);
            }
        });
    }



    private void updateThemeUI(boolean isDark) {
        themeIcon.setImageResource(isDark ? R.drawable.ic_moon : R.drawable.ic_sun);
    }


    private void showLanguageDialog() {
        // India-only: English + 7 major Indian languages
        String[] languages = {
                "English",
                "Hindi (हिन्दी)",
                "Bengali (বাংলা)",
                "Tamil (தமிழ்)",
                "Telugu (తెలుగు)",
                "Marathi (मराठी)",
                "Kannada (ಕನ್ನಡ)",
                "Gujarati (ગુજરાતી)"
        };
        String current = prefs.getString(KEY_LANGUAGE, "English");
        int sel = 0;
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(current)) { sel = i; break; }
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, sel, (dialog, which) -> {
                    prefs.edit().putString(KEY_LANGUAGE, languages[which]).apply();
                    languageText.setText(languages[which]);
                    showToast("Language: " + languages[which], true);
                    syncSettingsToServer();
                    dialog.dismiss();
                    // Apply locale immediately and recreate the Activity so all string
                    // resources reload in the new language without requiring an app restart.
                    LocaleHelper.applyLocale(requireContext());
                    requireActivity().recreate();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText currentPwdInput  = dialogView.findViewById(R.id.currentPasswordInput);
        TextInputEditText newPwdInput      = dialogView.findViewById(R.id.newPasswordInput);
        TextInputEditText confirmPwdInput  = dialogView.findViewById(R.id.confirmPasswordInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String current = currentPwdInput.getText() != null
                    ? currentPwdInput.getText().toString().trim() : "";
            String newPwd  = newPwdInput.getText() != null
                    ? newPwdInput.getText().toString() : "";
            String confirm = confirmPwdInput.getText() != null
                    ? confirmPwdInput.getText().toString() : "";

            if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
                ErrorDialogHelper.show(getContext(), "Missing Fields", "Please fill in all fields.");
                return;
            }
            if (!PASSWORD_PATTERN.matcher(newPwd).matches()) {
                ErrorDialogHelper.show(getContext(), "Password Requirements",
                        "Password must be at least 6 characters and include an uppercase letter, a number, and a symbol.");
                return;
            }
            if (!newPwd.equals(confirm)) {
                ErrorDialogHelper.show(getContext(), "Mismatch", "Passwords do not match.");
                return;
            }

            dialog.dismiss();
            requestPasswordChangeOtp(newPwd);
        });
    }

    private void requestPasswordChangeOtp(final String newPwd) {
        final String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        showToast("Sending verification code...", true);

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
        api.sendOtp(email, "update_pwd").enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    showToast(response.body().getMessage(), true);
                    showPasswordChangeOtpDialog(email, newPwd);
                } else {
                    String msg = (response.body() != null && response.body().getMessage() != null)
                            ? response.body().getMessage() : "Failed to send OTP verification code";
                    showToast(msg, false);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage(), false);
            }
        });
    }

    private void showPasswordChangeOtpDialog(final String email, final String newPwd) {
        new OtpInputDialog(requireContext(), email, "update_pwd",
                verifiedCode -> updatePasswordOnBackend(email, verifiedCode, newPwd))
                .show();
    }


    private void updatePasswordOnBackend(final String email, final String code, final String newPwd) {
        showToast("Updating password...", true);

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
        api.resetPassword(email, code, newPwd).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    showToast("Password updated successfully", true);
                } else {
                    String msg = (response.body() != null && response.body().getMessage() != null)
                            ? response.body().getMessage() : "Failed to update password";
                    showToast(msg, false);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage(), false);
            }
        });
    }

    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    prefs.edit().clear().apply();
                    showToast("Logged out successfully", true);
                    View v = getView();
                    if (v != null) {
                        try {
                            Navigation.findNavController(v).navigate(R.id.action_settings_to_login);
                        } catch (Exception e) {
                            requireActivity().finish();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        // Ask for password confirmation before deletion
        View pwdView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText pwdInput = pwdView.findViewById(R.id.currentPasswordInput);
        // Hide the new/confirm fields — we only need current password here
        pwdView.findViewById(R.id.newPasswordInput).setVisibility(View.GONE);
        pwdView.findViewById(R.id.confirmPasswordInput).setVisibility(View.GONE);

        AlertDialog confirmDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("This is permanent and cannot be undone.\n\nEnter your password to confirm:")
                .setView(pwdView)
                .setPositiveButton("Delete Permanently", null)
                .setNegativeButton("Cancel", null)
                .create();

        confirmDialog.show();

        confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String pwd = pwdInput.getText() != null ? pwdInput.getText().toString().trim() : "";
            if (pwd.isEmpty()) {
                showToast("Password is required to delete account", false);
                return;
            }
            confirmDialog.dismiss();
            String userEmail = prefs.getString("email", "");
            BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
            api.deleteAccount(userEmail, pwd).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if ("success".equals(response.body().getStatus())) {
                            prefs.edit().clear().apply();
                            showToast("Account deleted successfully", true);
                            View fv = getView();
                            if (fv != null) {
                                try {
                                    Navigation.findNavController(fv).navigate(R.id.action_settings_to_login);
                                } catch (Exception e) {
                                    requireActivity().finish();
                                }
                            }
                        } else {
                            showToast(response.body().getMessage(), false);
                        }
                    } else {
                        showToast("Server error", false);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    showToast("Network error: " + t.getMessage(), false);
                }
            });
        });
    }

    private void showToast(String msg, boolean success) {
        CustomToast.show(getView(), msg, success);
    }


    private void syncSettingsToServer() {
        String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
        
        String name = prefs.getString("name", "");
        String mobile = prefs.getString("mobile", "");
        String gender = prefs.getString("gender", "");
        String specialty = prefs.getString("specialty", "");
        String bio = prefs.getString("bio", "");
        String hospital = prefs.getString("hospital", "");
        String license = prefs.getString("license", "");
        int yearsExp = prefs.getInt("years_exp", 0);
        int darkMode = prefs.getBoolean(KEY_DARK_MODE, false) ? 1 : 0;
        String language = prefs.getString(KEY_LANGUAGE, "English");
        int dailySummary = prefs.getBoolean(KEY_DAILY_SUMMARY, true) ? 1 : 0;
        int sound = prefs.getBoolean(KEY_SOUND, true) ? 1 : 0;
        int vibration = prefs.getBoolean(KEY_VIBRATION, true) ? 1 : 0;
        int themeMode = prefs.getInt("theme_mode", 0);

        api.updateProfileFull(
                email, name, mobile, gender, specialty, bio, hospital, license, yearsExp,
                darkMode, language, dailySummary, sound, vibration, themeMode
        ).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {}
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {}
        });
    }

    private void uploadProfileFieldsToServer(String name, String specialty, String bio, String hospital, String license, int yearsExp) {
        String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        final android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Saving profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
        
        String mobile = prefs.getString("mobile", "");
        String gender = prefs.getString("gender", "");
        int darkMode = prefs.getBoolean(KEY_DARK_MODE, false) ? 1 : 0;
        String language = prefs.getString(KEY_LANGUAGE, "English");
        int dailySummary = prefs.getBoolean(KEY_DAILY_SUMMARY, true) ? 1 : 0;
        int sound = prefs.getBoolean(KEY_SOUND, true) ? 1 : 0;
        int vibration = prefs.getBoolean(KEY_VIBRATION, true) ? 1 : 0;
        int themeMode = prefs.getInt("theme_mode", 0);

        api.updateProfileFull(
                email, name, mobile, gender, specialty, bio, hospital, license, yearsExp,
                darkMode, language, dailySummary, sound, vibration, themeMode
        ).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null
                        && "success".equals(response.body().getStatus())) {
                    showToast("Profile updated successfully", true);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Update failed";
                    showToast(msg, false);
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                showToast("Network error: " + t.getMessage(), false);
            }
        });
    }

    private void refreshSettingsFromServer() {
        String email = prefs.getString("email", "");
        if (email.isEmpty()) return;

        // Debounce: skip if last sync was within 30 seconds to avoid triple-calling
        // across Splash → Dashboard → Settings navigation chain
        long lastSync = prefs.getLong("last_server_sync_ms", 0L);
        if (System.currentTimeMillis() - lastSync < 30_000L) return;
        prefs.edit().putLong("last_server_sync_ms", System.currentTimeMillis()).apply();

        BrainScanApi api = RetrofitClient.getRetrofitInstance(requireContext()).create(BrainScanApi.class);
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
                    editor.putBoolean(KEY_DARK_MODE, user.getDark_mode() == 1);
                    editor.putString(KEY_LANGUAGE, user.getLanguage() != null ? user.getLanguage() : "English");
                    editor.putBoolean(KEY_DAILY_SUMMARY, user.getDaily_summary() == 1);
                    editor.putBoolean(KEY_SOUND, user.getSound() == 1);
                    editor.putBoolean(KEY_VIBRATION, user.getVibration() == 1);
                    editor.putInt("theme_mode", user.getTheme_mode());
                    editor.apply();

                    // Reload Settings UI
                    loadSettings();
                    
                    // Apply dark-mode preference in case it changed on another device
                    boolean isDarkFromServer = user.getDark_mode() == 1;
                    updateThemeUI(isDarkFromServer);
                    AppCompatDelegate.setDefaultNightMode(
                            isDarkFromServer ? AppCompatDelegate.MODE_NIGHT_YES
                                            : AppCompatDelegate.MODE_NIGHT_NO);
                } else if (response.body() != null && "error".equals(response.body().getStatus())) {
                    // Account was deleted, logout
                    prefs.edit().clear().apply();
                    View v = getView();
                    if (v != null) {
                        try {
                            Navigation.findNavController(v).navigate(R.id.action_settings_to_login);
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
        refreshSettingsFromServer();
    }

}
