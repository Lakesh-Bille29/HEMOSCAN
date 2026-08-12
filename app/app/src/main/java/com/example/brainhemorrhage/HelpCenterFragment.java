package com.example.brainhemorrhage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class HelpCenterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        }

        // "Browse FAQs" button — navigates to FAQ screen
        MaterialButton faqButton = view.findViewById(R.id.browseFaqsButton);
        if (faqButton != null) {
            faqButton.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_helpCenter_to_faq));
        }

        // "Contact Support" button — navigates to Contact Support screen
        MaterialButton supportButton = view.findViewById(R.id.contactSupportButton);
        if (supportButton != null) {
            supportButton.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_helpCenter_to_contactSupport));
        }
    }
}
