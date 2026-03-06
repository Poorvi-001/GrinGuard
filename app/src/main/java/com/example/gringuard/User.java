package com.example.gringuard;

public class User {
    // These must be public or have public getters
    public String fName, lName, age, gender, email;

    // Empty constructor is REQUIRED for Firebase
    public User() {}

    public User(String fName, String lName, String age, String gender, String email) {
        this.fName = fName;
        this.lName = lName;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }
}