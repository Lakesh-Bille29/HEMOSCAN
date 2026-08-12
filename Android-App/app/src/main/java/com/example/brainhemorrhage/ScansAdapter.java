package com.example.brainhemorrhage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.brainhemorrhage.api.RetrofitClient;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class ScansAdapter extends RecyclerView.Adapter<ScansAdapter.ViewHolder> {

    private List<ScanItem> scans;
    private OnScanClickListener listener;

    public interface OnScanClickListener {
        void onScanClick(ScanItem scan);
    }

    public ScansAdapter(List<ScanItem> scans, OnScanClickListener listener) {
        this.scans = scans;
        this.listener = listener;
    }

    public void updateData(List<ScanItem> newScans) {
        this.scans = newScans;
        notifyDataSetChanged();
    }

    public List<ScanItem> getData() {
        return scans;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < scans.size()) {
            scans.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_scan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanItem scan = scans.get(position);
        Context context = holder.itemView.getContext();

        // 1. Patient Name
        holder.patientName.setText(scan.getPatientName() != null ? scan.getPatientName() : "Patient #" + scan.getId());

        // 2. Scan Date & Time
        holder.scanDate.setText(scan.getDate() != null ? scan.getDate() : "Date N/A");

        // 3. Result Parsing via ScanResultParser
        ScanResultParser parser = new ScanResultParser(scan.getResult());
        boolean isAbnormal = parser.isAbnormal();

        holder.resultStatus.setText(parser.getStatus());
        if (holder.hemorrhageDetails != null) {
            holder.hemorrhageDetails.setText(parser.getFormattedDetails());
        }

        // 4. Status Badge Pill Styling
        if (isAbnormal) {
            holder.resultStatus.setBackgroundResource(R.drawable.badge_danger);
            holder.resultStatus.setTextColor(ContextCompat.getColor(context, R.color.error));
        } else {
            holder.resultStatus.setBackgroundResource(R.drawable.badge_success);
            holder.resultStatus.setTextColor(ContextCompat.getColor(context, R.color.success));
        }

        // 5. Load CT Scan Thumbnail via Glide
        if (scan.getImagePath() != null && !scan.getImagePath().isEmpty()) {
            String imageUrl = scan.getImagePath();
            if (!imageUrl.startsWith("http") && !imageUrl.startsWith("file") && !imageUrl.startsWith("content")) {
                imageUrl = RetrofitClient.getBaseUrl() + imageUrl;
            }

            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .centerCrop()
                .into(holder.scanThumbnail);
        } else {
            holder.scanThumbnail.setImageResource(R.drawable.logo);
        }

        // 6. Download PDF Report Button Click Listener
        if (holder.downloadPdfBtn != null) {
            holder.downloadPdfBtn.setOnClickListener(v -> {
                Bitmap bitmap = null;
                if (holder.scanThumbnail.getDrawable() instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) holder.scanThumbnail.getDrawable()).getBitmap();
                }
                File pdfFile = PdfReportGenerator.generateAndSavePdf(context, scan, bitmap);
                if (pdfFile != null) {
                    CustomToast.showSuccess(context, "PDF Report saved to Downloads/HemoScan/");
                } else {
                    CustomToast.showError(context, "Failed to save PDF Report.");
                }
            });
        }

        // 7. Full Card Click Listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onScanClick(scan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scans != null ? scans.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, scanDate, resultStatus, hemorrhageDetails;
        ImageView scanThumbnail;
        View downloadPdfBtn;

        ViewHolder(View view) {
            super(view);
            patientName = view.findViewById(R.id.patientName);
            scanDate = view.findViewById(R.id.scanDate);
            resultStatus = view.findViewById(R.id.resultStatus);
            hemorrhageDetails = view.findViewById(R.id.hemorrhageDetails);
            scanThumbnail = view.findViewById(R.id.scanThumbnail);
            downloadPdfBtn = view.findViewById(R.id.downloadPdfBtn);
        }
    }
}
