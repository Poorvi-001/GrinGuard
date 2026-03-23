package com.example.gringuard;

public class Treatment {
    // These must be public for Firebase to read them
    public String disease;
    public String severity;
    public long startDate;

    // Firebase needs an empty constructor
    public Treatment() {}

    public Treatment(String disease, String severity, long startDate) {
        this.disease = disease;
        this.severity = severity;
        this.startDate = startDate;
    }
}