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

public class TermsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back navigation
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        // Inject full terms text programmatically
        TextView termsContent = view.findViewById(R.id.termsContent);
        if (termsContent != null) {
            termsContent.setText(getFullTerms());
        }
    }

    private String getFullTerms() {
        return "Effective: June 2025 · Version 1.0\n\n"

                + "1. Acceptance of Terms\n"
                + "By creating an account and using HemoScan AI (\"the App\"), you confirm that you have read, understood, and agree to be bound by these Terms & Conditions. If you do not agree, you must not use the App.\n\n"

                + "2. Medical Disclaimer\n"
                + "HemoScan AI is a clinical decision-support tool designed to assist qualified medical professionals. It is NOT a replacement for professional medical diagnosis, clinical judgment, or physician review.\n"
                + "• All AI-generated results MUST be reviewed by a licensed radiologist or physician before clinical action is taken.\n"
                + "• The App does not provide definitive medical diagnoses.\n"
                + "• HemoScan AI assumes no liability for clinical decisions made based solely on App output.\n\n"

                + "3. User Account & Eligibility\n"
                + "• You must be a licensed medical professional, authorized researcher, or institutional user to access diagnostic features.\n"
                + "• You are solely responsible for maintaining the confidentiality and security of your login credentials.\n"
                + "• Do not share your account with others. Each account is for individual use only.\n"
                + "• You must be at least 18 years of age to create an account.\n\n"

                + "4. Permitted Use\n"
                + "HemoScan may only be used for:\n"
                + "• Legitimate clinical diagnostic support\n"
                + "• Medical education and training\n"
                + "• Authorized research with appropriate patient consent\n\n"
                + "The following are strictly prohibited:\n"
                + "• Unauthorized reproduction or redistribution of AI models\n"
                + "• Reverse engineering or decompiling App components\n"
                + "• Using the App for non-medical commercial purposes\n"
                + "• Uploading patient data without proper consent\n\n"

                + "5. Patient Data & Consent\n"
                + "Patient scan data you upload remains your property and your institution's responsibility. You represent that you have obtained all required consents to process and analyze any patient data through this App. HemoScan processes data solely to provide diagnostic analysis.\n\n"

                + "6. Intellectual Property\n"
                + "All AI models, software, designs, and content within HemoScan are the intellectual property of HemoScan AI and its licensors. No rights are granted to reproduce, modify, or distribute any App components.\n\n"

                + "7. Service Availability\n"
                + "We aim for 99.9% uptime but do not guarantee uninterrupted service. Scheduled maintenance will be announced in advance where possible. We are not liable for losses resulting from service outages or interruptions.\n\n"

                + "8. Limitation of Liability\n"
                + "To the maximum extent permitted by law, HemoScan AI shall not be liable for:\n"
                + "• Clinical decisions made based on App results\n"
                + "• Data loss due to technical failures\n"
                + "• Indirect, incidental, or consequential damages\n\n"

                + "9. Termination\n"
                + "We reserve the right to suspend or terminate accounts that violate these Terms without prior notice. You may terminate your account at any time through Settings → Delete Account.\n\n"

                + "10. Governing Law\n"
                + "These Terms are governed by applicable Indian law. Any disputes shall be subject to the jurisdiction of competent courts in the relevant jurisdiction.\n\n"

                + "11. Changes to Terms\n"
                + "We may update these Terms periodically. Continued use of the App after changes constitutes acceptance of the updated Terms. We will notify users of significant changes via in-app notification.\n\n"

                + "12. Contact\n"
                + "For questions about these Terms, contact us at:\n"
                + "Email: legal@hemoscan.ai\n\n"

                + "© 2025 HemoScan AI. All rights reserved.";
    }
}
