package com.example.gringuard;

public class User {
    public String age, firstName, gender, lastName, email;

    public User() {}

    public User(String firstName, String lastName, String age, String gender, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }
}