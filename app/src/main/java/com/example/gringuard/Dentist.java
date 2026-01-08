package com.example.gringuard; // Ensure this matches your package name at the top of other files

public class Dentist {
    private String name;
    private String state;
    private String phone;

    // Constructor to initialize the dentist details
    public Dentist(String name, String state, String phone) {
        this.name = name;
        this.state = state;
        this.phone = phone;
    }

    // This method allows the filter and adapter to see the Name
    public String getName() {
        return name;
    }

    // This method allows the filter and adapter to see the State
    public String getState() {
        return state;
    }

    // This method allows the adapter to get the Phone number for the call intent
    public String getPhone() {
        return phone;
    }
}