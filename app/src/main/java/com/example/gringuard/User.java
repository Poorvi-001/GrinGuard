package com.example.gringuard;

public class User {

    public String age;
    public String firstName;
    public String gender;
    public String lastName;

    public User() {}

    public User(String firstName, String lastName, String age, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
    }
}