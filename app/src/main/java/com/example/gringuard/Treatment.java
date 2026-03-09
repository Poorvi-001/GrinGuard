package com.example.gringuard;

public class Treatment {
    public String diseaseName;
    public String planStatus;
    public long startDate;
    public int maxSeverity; // This matches your code

    public Treatment() {} // Required for Firebase

    public Treatment(String diseaseName) {
        this.diseaseName = diseaseName;
        this.planStatus = "Active";
        this.startDate = System.currentTimeMillis();
    }
}