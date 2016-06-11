package com.nanodegree.gaby.bakerylovers.backend.db;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/** The Objectify object model for users we are persisting */
@Entity
public class UserRecord {

    @Id
    Long id;

    @Index
    private String email;
    private String password;
    private String fullName;
    private String address;
    private String phoneNumber;
    @Index private String loginToken;
    private boolean isGoogleAccount;

    public UserRecord() {}

    public UserRecord(long userId) {
        id = userId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isGoogleAccount() {
        return isGoogleAccount;
    }

    public void setGoogleAccount(boolean googleAccount) {
        isGoogleAccount = googleAccount;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}