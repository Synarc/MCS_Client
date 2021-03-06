package com.mcssoftware.app.mcsclient;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    String firstName;
    String lastName;
    String fullName;
    String phNum;

    String gender;

    public ClientInfo(String firstName, String lastName, String fullName, String phNum, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.phNum = phNum;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhNum() {
        return phNum;
    }

    public void setPhNum(String phNum) {
        this.phNum = phNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
