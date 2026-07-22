package com.example.brainhemorrhage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.brainhemorrhage.api.BaseResponse;
import com.example.brainhemorrhage.api.BrainScanApi;
import com.example.brainhemorrhage.api.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.content.SharedPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactSupportFragment extends Fragment {

    private AutoCompleteTextView categoryDropdown;
    private TextInputEditText    messageInput;
    private TextInputLayout      messageLayout;
    private MaterialButton       submitButton;
    private String               doctorEmail = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── Toolbar ──────────────────────────────────────────────────────────────
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // ── Load doctor email from prefs ──────────────────────────────────────────
        SharedPreferences prefs = requireContext().getSharedPreferences("HemoScanPrefs", Context.MODE_PRIVATE);
        doctorEmail = prefs.getString("email", "");

        // ── Category dropdown ─────────────────────────────────────────────────────
        categoryDropdown = view.findViewById(R.id.categoryDropdown);
        String[] categories = {
                "Technical Issue", "Account Issue", "Scan Processing Issue",
                "AI Model Concern", "Privacy / Data Request",
                "Feature Request", "Billing", "Bug Report", "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        categoryDropdown.setAdapter(adapter);
        categoryDropdown.setText(categories[0], false);

        // Pre-select category if navigated from SupportDashboard
        if (getArguments() != null) {
            String category = getArguments().getString("category");
            if (category != null) categoryDropdown.setText(category, false);
        }

        // ── Message input ─────────────────────────────────────────────────────────
        messageInput  = view.findViewById(R.id.messageInput);
        messageLayout = view.findViewById(R.id.messageLayout);

        // ── Attachment info (informational only) ──────────────────────────────────
        View attachButton = view.findViewById(R.id.attachButton);
        if (attachButton != null) {
            attachButton.setOnClickListener(v ->
                Snackbar.make(view,
                    "For screenshots, describe them in your message or email us at: "
                            + getString(R.string.support_email),
                    Snackbar.LENGTH_LONG).show()
            );
        }

        // ── Submit button ─────────────────────────────────────────────────────────
        submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> submitSupportRequest(view));
    }

    private void submitSupportRequest(View rootView) {
        String category = categoryDropdown.getText().toString().trim();
        String message  = (messageInput != null && messageInput.getText() != null)
                ? messageInput.getText().toString().trim() : "";

        // ── Validation ──────────────────────────────────────────────────────────
        if (TextUtils.isEmpty(category)) {
            Snackbar.make(rootView, "Please select an issue category.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (message.length() < 20) {
            if (messageLayout != null) {
                messageLayout.setError("Please describe your issue (at least 20 characters).");
            }
            Snackbar.make(rootView, "Message too short. Please provide more detail.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (messageLayout != null) messageLayout.setError(null);

        // ── Disable button to prevent double-submission ──────────────────────────
        if (submitButton != null) {
            submitButton.setEnabled(false);
            submitButton.setText("Submitting…");
        }

        // ── Build device info string ─────────────────────────────────────────────
        String deviceInfo = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
                + " (Android " + android.os.Build.VERSION.RELEASE + ")";

        // ── Call API ─────────────────────────────────────────────────────────────
        BrainScanApi api = RetrofitClient.getRetrofitInstance().create(BrainScanApi.class);
        api.submitTicket(doctorEmail, category, message, "android", deviceInfo)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BaseResponse> call,
                                           @NonNull Response<BaseResponse> response) {
                        if (submitButton != null) {
                            submitButton.setEnabled(true);
                            submitButton.setText("Submit");
                        }
                        if (response.isSuccessful() && response.body() != null
                                && "success".equals(response.body().getStatus())) {

                            String ticketNum = response.body().getTicketNumber();
                            // Navigate to success screen, pass ticket number
                            Bundle args = new Bundle();
                            args.putString("ticket_number", ticketNum != null ? ticketNum : "N/A");
                            args.putString("category", category);
                            View fragmentView = getView();
                            if (fragmentView != null) {
                                Navigation.findNavController(fragmentView)
                                        .navigate(R.id.action_contactSupport_to_success, args);
                            }
                        } else {
                            String msg = (response.body() != null && response.body().getMessage() != null)
                                    ? response.body().getMessage()
                                    : "Submission failed. Please try again.";
                            Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                        if (submitButton != null) {
                            submitButton.setEnabled(true);
                            submitButton.setText("Submit");
                        }
                        Snackbar.make(rootView,
                                "Network error. Please check your connection and try again.",
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
