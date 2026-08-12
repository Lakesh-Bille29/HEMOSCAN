package com.example.brainhemorrhage.api;

public class BaseResponse {
    private String  status;
    private String  message;
    private String  patient_id;
    private String  profile_image;
    private String  ticket_number;   // returned by submit_ticket.php
    private boolean email_sent;      // returned by submit_ticket.php

    public String getStatus()  { return status; }
    public void   setStatus(String status)  { this.status = status; }

    public String getMessage() { return message; }
    public void   setMessage(String message) { this.message = message; }

    public String getPatient_id() { return patient_id; }
    public void   setPatient_id(String patient_id) { this.patient_id = patient_id; }

    public String getProfile_image() { return profile_image; }
    public void   setProfile_image(String profile_image) { this.profile_image = profile_image; }

    public String getTicketNumber() { return ticket_number; }
    public void   setTicketNumber(String ticket_number) { this.ticket_number = ticket_number; }

    public boolean isEmailSent() { return email_sent; }
    public void    setEmailSent(boolean email_sent) { this.email_sent = email_sent; }
}

