package com.example.gringuard;

public class Treatment {
    // public for Firebase to read them
    public String disease;
    public String severity;
    public long startDate;

    // empty constructor for firebase
    public Treatment() {}

    public Treatment(String disease, String severity, long startDate) {
        this.disease = disease;
        this.severity = severity;
        this.startDate = startDate;
    }
}