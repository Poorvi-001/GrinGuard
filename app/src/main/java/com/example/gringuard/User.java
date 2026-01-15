package com.example.gringuard;

public class User {
    // These MUST match your Firebase keys exactly
    public String age;
    public String firstName;
    public String gender;
    public String lastName;

    public User() {} // Required for Firebase

    public User(String firstName, String lastName, String age, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
    }
}