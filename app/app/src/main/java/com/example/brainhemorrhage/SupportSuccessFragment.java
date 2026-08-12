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

public class SupportSuccessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Display the ticket number passed from ContactSupportFragment
        if (getArguments() != null) {
            String ticketNumber = getArguments().getString("ticket_number", "N/A");
            TextView ticketText = view.findViewById(R.id.ticketNumberText);
            if (ticketText != null) {
                ticketText.setText(ticketNumber);
            }
        }

        // Navigate to dashboard on done
        view.findViewById(R.id.doneButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_success_to_dashboard));
    }
}
