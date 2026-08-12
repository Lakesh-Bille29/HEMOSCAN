package com.example.brainhemorrhage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class AboutUsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar back navigation
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        // "Visit Website" button — opens the HemoScan web portal (local or remote)
        MaterialButton visitButton = view.findViewById(R.id.visitWebsiteButton);
        if (visitButton != null) {
            visitButton.setOnClickListener(v -> {
                String url = getString(R.string.web_portal_url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(),
                        "Could not open browser. Visit: " + url,
                        Toast.LENGTH_LONG).show();
                }
            });
        }

        // "Contact Us" button — opens email client pre-filled
        MaterialButton contactButton = view.findViewById(R.id.contactUsButton);
        if (contactButton != null) {
            contactButton.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + getString(R.string.support_email)));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[HemoScan] Inquiry");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(requireContext(),
                        "No email app found. Email us at: " + getString(R.string.support_email),
                        Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
