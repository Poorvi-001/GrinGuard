package com.example.gringuard;

public class User {

    public String firstName;
    public String lastName;
    public String age;
    public String gender;
    public String email;

    // Required empty constructor for Firebase
    public User() {
    }

    // Constructor with 5 parameters
    public User(String ftName, String lName, String age,
                String gender, String email) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }
}