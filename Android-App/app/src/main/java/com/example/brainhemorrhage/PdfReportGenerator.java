package com.example.brainhemorrhage;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * PdfReportGenerator — Native Android PDF report generator.
 * Creates an official HemoScan Medical Diagnostic Report PDF and saves it
 * to Downloads/HemoScan/ folder on the device.
 */
public class PdfReportGenerator {

    public static File generateAndSavePdf(Context context, ScanItem scan, Bitmap scanBitmap) {
        if (context == null || scan == null) return null;

        ScanResultParser parser = new ScanResultParser(scan.getResult());
        int pageWidth = 595;  // A4 dimensions at 72 DPI
        int pageHeight = 842;

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // ── 1. Header Background & Branding ────────────────────────────────────
        paint.setColor(Color.parseColor("#1E1B4B")); // Dark Navy
        canvas.drawRect(0, 0, pageWidth, 90, paint);

        paint.setColor(Color.parseColor("#818CF8")); // Indigo Accent
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("HemoScan AI", 30, 42, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        canvas.drawText("Intelligent Neuroimaging Diagnostic Report", 30, 62, paint);

        paint.setColor(Color.parseColor("#A5B4FC"));
        paint.setTextSize(10);
        String reportDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Report Date: " + reportDate, pageWidth - 190, 42, paint);
        canvas.drawText("Report ID: HST-" + scan.getId(), pageWidth - 190, 58, paint);

        // ── 2. Patient Information Card ───────────────────────────────────────
        paint.setColor(Color.parseColor("#F8FAFC")); // Light Slate background
        RectF patientCard = new RectF(30, 105, pageWidth - 30, 195);
        canvas.drawRoundRect(patientCard, 12, 12, paint);

        paint.setColor(Color.parseColor("#CBD5E1"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRoundRect(patientCard, 12, 12, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.parseColor("#0F172A"));
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        canvas.drawText("PATIENT DEMOGRAPHICS", 45, 128, paint);

        paint.setTextSize(11);
        paint.setFakeBoldText(false);
        paint.setColor(Color.parseColor("#475569"));
        canvas.drawText("Patient Name:", 45, 150, paint);
        canvas.drawText("Patient ID:", 45, 172, paint);

        paint.setColor(Color.parseColor("#0F172A"));
        paint.setFakeBoldText(true);
        canvas.drawText(scan.getPatientName() != null ? scan.getPatientName() : "N/A", 130, 150, paint);
        canvas.drawText(scan.getDbPatientId() != null ? scan.getDbPatientId() : (scan.getPatientId() != null ? scan.getPatientId() : "N/A"), 130, 172, paint);

        paint.setColor(Color.parseColor("#475569"));
        paint.setFakeBoldText(false);
        canvas.drawText("Age / Gender:", 320, 150, paint);
        canvas.drawText("Scan Date:", 320, 172, paint);

        paint.setColor(Color.parseColor("#0F172A"));
        paint.setFakeBoldText(true);
        String ageStr = (scan.getAge() != null && !scan.getAge().isEmpty()) ? scan.getAge() : "N/A";
        String genderStr = (scan.getGender() != null && !scan.getGender().isEmpty()) ? scan.getGender() : "N/A";
        canvas.drawText(ageStr + " / " + genderStr, 410, 150, paint);
        canvas.drawText(scan.getDate() != null ? scan.getDate() : "N/A", 410, 172, paint);

        // ── 3. AI Diagnostic Finding Section ──────────────────────────────────
        boolean isAbnormal = parser.isAbnormal();
        int statusBgColor = isAbnormal ? Color.parseColor("#FFE4E6") : Color.parseColor("#DCFCE7");
        int statusTextColor = isAbnormal ? Color.parseColor("#E11D48") : Color.parseColor("#16A34A");

        RectF findingCard = new RectF(30, 210, pageWidth - 30, 310);
        paint.setColor(statusBgColor);
        canvas.drawRoundRect(findingCard, 12, 12, paint);

        // Status Badge Pill
        RectF badgePill = new RectF(45, 225, 165, 255);
        paint.setColor(statusTextColor);
        canvas.drawRoundRect(badgePill, 15, 15, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(12);
        paint.setFakeBoldText(true);
        canvas.drawText(parser.getStatus(), 68, 244, paint);

        // Hemorrhage Subtype & Confidence
        paint.setColor(Color.parseColor("#0F172A"));
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText(parser.getHemorrhageType() + (isAbnormal ? " Hemorrhage" : ""), 180, 245, paint);

        paint.setColor(Color.parseColor("#475569"));
        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        canvas.drawText("Confidence Score: " + parser.getConfidence(), 45, 285, paint);
        canvas.drawText("AI Model: TFLite Multi-Stage Hemorrhage Ensemble (v1.0)", 260, 285, paint);

        // ── 4. Analyzed CT Scan Image Render ──────────────────────────────────
        canvas.save();
        int imageY = 325;
        int imageSize = 340;
        int imageX = (pageWidth - imageSize) / 2;

        RectF imageCard = new RectF(imageX - 5, imageY - 5, imageX + imageSize + 5, imageY + imageSize + 5);
        paint.setColor(Color.parseColor("#E2E8F0"));
        canvas.drawRoundRect(imageCard, 10, 10, paint);

        if (scanBitmap != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(scanBitmap, imageSize, imageSize, true);
            canvas.drawBitmap(scaledBitmap, imageX, imageY, null);
        } else {
            paint.setColor(Color.parseColor("#F1F5F9"));
            canvas.drawRect(imageX, imageY, imageX + imageSize, imageY + imageSize, paint);
            paint.setColor(Color.parseColor("#94A3B8"));
            paint.setTextSize(14);
            canvas.drawText("CT Scan Image Preview Unavailable", imageX + 50, imageY + 170, paint);
        }
        canvas.restore();

        // ── 5. Medical Disclaimer Footer ──────────────────────────────────────
        paint.setColor(Color.parseColor("#F1F5F9"));
        canvas.drawRect(0, pageHeight - 90, pageWidth, pageHeight, paint);

        paint.setColor(Color.parseColor("#64748B"));
        paint.setTextSize(9);
        paint.setFakeBoldText(false);
        canvas.drawText("CLINICAL DISCLAIMER:", 30, pageHeight - 65, paint);
        canvas.drawText("HemoScan AI is a clinical decision-support tool designed to assist healthcare professionals.", 30, pageHeight - 50, paint);
        canvas.drawText("All AI prediction results must be verified by a licensed radiologist or physician before clinical treatment.", 30, pageHeight - 38, paint);
        canvas.drawText("© " + new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()) + " HemoScan AI Diagnostic Systems. All rights reserved.", 30, pageHeight - 20, paint);

        pdfDocument.finishPage(page);

        // ── 6. File Saving (Downloads/HemoScan/) ──────────────────────────────
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "HemoScan_Report_" + (scan.getDbPatientId() != null ? scan.getDbPatientId() : scan.getId()) + "_" + timeStamp + ".pdf";

        File outputFile = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/HemoScan");

                Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream os = context.getContentResolver().openOutputStream(uri);
                    if (os != null) {
                        pdfDocument.writeTo(os);
                        os.close();
                    }
                }
                // Also create a local file reference for direct path verification
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File hemoscanDir = new File(downloadsDir, "HemoScan");
                if (!hemoscanDir.exists()) hemoscanDir.mkdirs();
                outputFile = new File(hemoscanDir, fileName);
            } else {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File hemoscanDir = new File(downloadsDir, "HemoScan");
                if (!hemoscanDir.exists()) hemoscanDir.mkdirs();

                outputFile = new File(hemoscanDir, fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                pdfDocument.writeTo(fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        return outputFile;
    }
}
