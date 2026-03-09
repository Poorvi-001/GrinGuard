package com.example.gringuard;

public class User {

    public String fName;
    public String lName;
    public String age;
    public String gender;
    public String email;

    public User() {
        // Required for Firebase
    }

    public User(String fName, String lName,
                String age, String gender, String email) {

        this.fName = fName;
        this.lName = lName;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }
}