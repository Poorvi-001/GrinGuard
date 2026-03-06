package com.example.gringuard;

public class User {

    public String firstName;
    public String lastName;
    public String age;
    public String gender;
    public String email;

    public User() {
        // Required for Firebase
    }

    public User(String firstName, String lastName,
                String age, String gender, String email) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }
}