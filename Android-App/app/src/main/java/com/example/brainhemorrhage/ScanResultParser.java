package com.example.brainhemorrhage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ScanResultParser — Robust parser for backend diagnostic result strings.
 * Converts raw strings like "Abnormal (Hemorrhage detected: Epidural (6.3%))"
 * into clean, structured UI data fields.
 */
public class ScanResultParser {

    private final String status;         // "ABNORMAL" or "NORMAL"
    private final String hemorrhageType; // e.g. "Epidural", "Subdural", "No Hemorrhage Detected"
    private final String confidence;     // e.g. "6.3%" or "99.8%"

    public ScanResultParser(String rawResult) {
        if (rawResult == null || rawResult.trim().isEmpty()) {
            this.status = "NORMAL";
            this.hemorrhageType = "No Hemorrhage Detected";
            this.confidence = "N/A";
            return;
        }

        String trimmed = rawResult.trim();
        boolean isAbnormal = trimmed.toLowerCase().contains("abnormal") || 
                             (trimmed.toLowerCase().contains("hemorrhage") && !trimmed.toLowerCase().contains("no hemorrhage"));

        this.status = isAbnormal ? "ABNORMAL" : "NORMAL";

        // Extract confidence percentage if present (e.g. 98.5% or 6.3%)
        String extractedConf = "N/A";
        Pattern percentPattern = Pattern.compile("(\\d+(?:\\.\\d+)?%)");
        Matcher percentMatcher = percentPattern.matcher(trimmed);
        if (percentMatcher.find()) {
            extractedConf = percentMatcher.group(1);
        }
        this.confidence = extractedConf;

        // Extract hemorrhage subtype if present
        if (!isAbnormal) {
            this.hemorrhageType = "No Hemorrhage Detected";
        } else {
            String type = "Hemorrhage Detected";
            String lower = trimmed.toLowerCase();
            if (lower.contains("epidural")) {
                type = "Epidural";
            } else if (lower.contains("subdural")) {
                type = "Subdural";
            } else if (lower.contains("subarachnoid")) {
                type = "Subarachnoid";
            } else if (lower.contains("intraparenchymal")) {
                type = "Intraparenchymal";
            } else if (lower.contains("intraventricular")) {
                type = "Intraventricular";
            } else {
                // Regex fallback for "Hemorrhage detected: Subtype (..."
                Pattern typePattern = Pattern.compile("detected:\\s*([A-Za-z]+)", Pattern.CASE_INSENSITIVE);
                Matcher typeMatcher = typePattern.matcher(trimmed);
                if (typeMatcher.find()) {
                    type = typeMatcher.group(1);
                }
            }
            this.hemorrhageType = type;
        }
    }

    public String getStatus() { return status; }
    public boolean isAbnormal() { return "ABNORMAL".equalsIgnoreCase(status); }
    public String getHemorrhageType() { return hemorrhageType; }
    public String getConfidence() { return confidence; }

    public String getFormattedDetails() {
        if (!isAbnormal()) {
            return "No Hemorrhage Detected" + ("N/A".equals(confidence) ? "" : " · " + confidence);
        }
        return hemorrhageType + " Hemorrhage" + ("N/A".equals(confidence) ? "" : " (" + confidence + ")");
    }
}
