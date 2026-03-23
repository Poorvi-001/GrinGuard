package com.example.gringuard;
public class User {
    public String fName, lName, age, gender, email;
    // Add this to prevent null pointer exceptions if you download the whole user object
    public java.util.Map<String, Object> CurrentTreatment;

    public User() {}

    public User(String firstName, String lastName, String age, String gender, String email) {
        this.fName = firstName;
        this.lName = lastName;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }
}