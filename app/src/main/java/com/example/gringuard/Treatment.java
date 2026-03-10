package com.example.gringuard;

public class Treatment {
    public String disease;
    public String severity;
    public long startDate;

    public Treatment() {} // Required for Firebase

    public Treatment(String disease, String severity, long startDate) {
        this.disease = disease;
        this.severity = severity;
        this.startDate = startDate;
    }
}