package com.example.brainhemorrhage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.example.brainhemorrhage.api.LoginResponse;
import com.example.brainhemorrhage.api.BrainScanApi;
import com.example.brainhemorrhage.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class LoginFragment extends Fragment {

    private TextInputEditText emailInput, passwordInput;
    private TextView forgotPasswordText, signUpText;
    private Button signInButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        forgotPasswordText = view.findViewById(R.id.forgotPasswordText);
        signUpText = view.findViewById(R.id.signupText);
        signInButton = view.findViewById(R.id.loginButton);


        // Set click listeners
        signInButton.setOnClickListener(v -> handleLogin());
        forgotPasswordText.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_login_to_forgotPassword));
        signUpText.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_login_to_signup));

        // Connect tactile animations
        AnimationHelper.applyBouncePress(signInButton);
        AnimationHelper.applyBouncePress(forgotPasswordText);
        AnimationHelper.applyBouncePress(signUpText);

        // Slide/fade entrance sequence
        AnimationHelper.animateViewsInSequence(
            view.findViewById(R.id.logoCard),
            view.findViewById(R.id.loginCardContainer)
        );

        // Gentle floating animation on form container
        float density = getResources().getDisplayMetrics().density;
        AnimationHelper.applyFloatingEffect(view.findViewById(R.id.loginCardContainer), 6f * density, 4000, 200);

        return view;
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            CustomToast.show(getView(), "Please enter valid credentials", false);
            return;
        }


        // Disable button while loading
        signInButton.setEnabled(false);
        signInButton.setText("Logging in...");

        BrainScanApi api = RetrofitClient.getRetrofitInstance().create(BrainScanApi.class);
        api.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                signInButton.setEnabled(true);
                signInButton.setText("Sign In");
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if ("success".equals(loginResponse.getStatus())) {
                        SharedPreferences prefs = requireActivity().getSharedPreferences("HemoScanPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", email);
                        
                        if (loginResponse.getUser() != null) {
                            LoginResponse.User user = loginResponse.getUser();
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

                            // Apply dark mode right away
                            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                                    user.getDark_mode() == 1 
                                            ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES 
                                            : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                            );
                        }
                        editor.apply();
                        
                        // errorText.setVisibility(View.GONE);

                        View fragmentView = getView();
                        if (fragmentView != null) {
                            Navigation.findNavController(fragmentView).navigate(R.id.action_login_to_welcome);
                        }
                    } else {
                        ErrorDialogHelper.show(getContext(), "Sign In Failed", loginResponse.getMessage());
                    }

                } else {
                    ErrorDialogHelper.show(getContext(), "Server Error", "Server returned an error (HTTP " + response.code() + ").");
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                signInButton.setEnabled(true);
                signInButton.setText("Sign In");
                String msg;
                if (t instanceof ConnectException) {
                    msg = "Cannot connect to server. Please make sure the XAMPP backend is running on your network.";
                } else if (t instanceof SocketTimeoutException) {
                    msg = "Connection timed out. Please check your Wi-Fi connectivity and verify the server's IP address.";
                } else {
                    msg = "Network error: " + t.getLocalizedMessage();
                }
                ErrorDialogHelper.show(getContext(), "Connection Failed", msg);
            }

        });
    }
}
