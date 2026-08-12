package com.example.brainhemorrhage.api;

import java.util.List;

public class DashboardResponse {
    private String status;
    private int total_scans;
    private int normal_scans;
    private int abnormal_scans;
    private List<ScanResponse.ScanItemDto> data;
    private long server_time;

    public String getStatus() { return status; }
    public int getTotalScans() { return total_scans; }
    public int getNormalScans() { return normal_scans; }
    public int getAbnormalScans() { return abnormal_scans; }
    public List<ScanResponse.ScanItemDto> getData() { return data; }
    public long getServerTime() { return server_time; }
}
