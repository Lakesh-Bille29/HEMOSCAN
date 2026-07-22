package com.example.brainhemorrhage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatViewHolder> {

    private List<String> labels;

    public StatAdapter(List<String> labels) {
        this.labels = labels;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stat_card, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        holder.labelText.setText(labels.get(position));
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        StatViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.labelText);
        }
    }
}
