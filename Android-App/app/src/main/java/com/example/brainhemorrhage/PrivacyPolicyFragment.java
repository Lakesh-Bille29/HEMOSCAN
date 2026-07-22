package com.example.brainhemorrhage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.appbar.MaterialToolbar;

public class PrivacyPolicyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back navigation
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        // Inject full privacy policy text programmatically into the content TextView
        TextView privacyContent = view.findViewById(R.id.privacyContent);
        if (privacyContent != null) {
            privacyContent.setText(getFullPrivacyPolicy());
        }
    }

    private String getFullPrivacyPolicy() {
        return "Last Updated: June 2025 · Version 1.0\n\n"

                + "1. Information We Collect\n"
                + "We collect information you provide directly, including your name, email address, medical credentials (license number, hospital affiliation, specialization), and patient scan data you upload for analysis. We also collect usage data and app interaction logs to improve application performance.\n\n"

                + "2. How We Use Your Information\n"
                + "Your data is used exclusively to:\n"
                + "• Provide AI-powered brain hemorrhage diagnostic analysis\n"
                + "• Maintain and synchronize your account across Android and Web\n"
                + "• Store and display historical scan records\n"
                + "• Improve model accuracy and platform performance\n"
                + "We do not sell, rent, or share your personal data with third parties for marketing.\n\n"

                + "3. Data Security\n"
                + "All data is encrypted in transit using TLS 1.3 and stored at rest using AES-256 encryption. Patient scan images are stored on secure servers with access restricted to authenticated users only. Passwords are hashed using bcrypt and never stored in plaintext.\n\n"

                + "4. Data Retention\n"
                + "Account data is retained while your account is active. You may request permanent deletion at any time via Settings → Danger Zone → Delete Account. Deleted data is permanently purged from our systems within 30 days.\n\n"

                + "5. HIPAA & Medical Data Compliance\n"
                + "HemoScan is designed with medical data standards in mind. We follow established practices for Protected Health Information (PHI) handling. Patient identifiers are never shared with unauthorized parties. All diagnostic results are for clinical support purposes only.\n\n"

                + "6. Cookies & Analytics\n"
                + "The web portal uses minimal session cookies required for authentication only. We do not use third-party advertising trackers or analytics SDKs that share data externally.\n\n"

                + "7. Your Rights\n"
                + "You have the right to:\n"
                + "• Access and download your personal data\n"
                + "• Correct inaccurate information\n"
                + "• Delete your account and all associated data\n"
                + "• Opt out of non-essential communications\n\n"

                + "8. Contact\n"
                + "For privacy-related concerns or data requests, contact us at:\n"
                + "Email: privacy@hemoscan.ai\n"
                + "We respond to all privacy inquiries within 72 business hours.\n\n"

                + "© 2025 HemoScan AI. All rights reserved.";
    }
}
