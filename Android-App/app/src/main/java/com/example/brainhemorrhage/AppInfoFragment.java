package com.example.brainhemorrhage;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;

public class AppInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ─── Toolbar ────────────────────────────────────────────────────
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // ─── Dynamic version info ────────────────────────────────────────
        String versionName = "1.0.0";
        int    versionCode = 1;
        try {
            PackageInfo info = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            versionName = info.versionName;
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {}

        final String finalVersionName = versionName;

        // Inject into the "Technical Details" TextViews via string composition
        // The layout already has static strings but we override version/build rows
        TextView buildText = view.findViewById(R.id.buildText);
        if (buildText != null) {
            buildText.setText("Build: " + versionName + " (" + versionCode + ")");
        }

        // Dynamic copyright year
        TextView copyrightText = view.findViewById(R.id.copyrightText);
        if (copyrightText != null) {
            copyrightText.setText("© " + Calendar.getInstance().get(Calendar.YEAR) + " HemoScan AI. All rights reserved.");
        }

        // ─── Contact developer button ────────────────────────────────────
        MaterialButton contactBtn = view.findViewById(R.id.contactDeveloperButton);
        if (contactBtn != null) {
            contactBtn.setOnClickListener(v -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@hemoscan.ai"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[HemoScan] Developer Inquiry");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "App: HemoScan AI\nVersion: " + finalVersionName + "\nDevice: " + android.os.Build.MODEL + "\n\n---\n\nMessage:\n");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                } catch (android.content.ActivityNotFoundException e) {
                    android.widget.Toast.makeText(requireContext(), "No email client found", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
