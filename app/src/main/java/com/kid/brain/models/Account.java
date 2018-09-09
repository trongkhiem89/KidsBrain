package com.kid.brain.models;

import android.text.TextUtils;

import com.kid.brain.util.AppCommon;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 11/7/16.
 */
public class Account implements Serializable {

    public static int MALE = 0;
    public static int FEMALE = 1;
    public static String DEFAULT_ID = "-1";

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String username;

    @SerializedName("phone")
    private String mobile;

    @SerializedName("dob")
    private String birthDay;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("imgUrl")
    private String photo;

    @SerializedName("country")
    private String country;

    @SerializedName("sessionToken")
    private String sessionToken;

    @SerializedName("userId")
    private String userId;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("address")
    private String address;

    @SerializedName("gender")
    private int gender;

    @SerializedName("childrenVOs")
    private List<Kid> kids;

    public Account() {
        username = "";
        mobile = "";
        birthDay = "";
        photo = "";
        address = "";
        gender = MALE;
        photo = "";
    }

    public Account clone() {
        Account account = new Account();
        account.setUserId(getUserId());
        account.setEmail(getEmail());
        account.setUsername(getUsername());
        account.setLastName(getLastName());
        account.setFirstName(getFirstName());
        account.setCountry(getCountry());
        account.setMobile(getMobile());
        account.setPhoto(getPhoto());
        return account;
    }


    public String getFullName() {
        String fullName = "";
//        if (!TextUtils.isEmpty(this.getFirstName())) {
//            fullName = this.getFirstName();
//        }
//        if (!TextUtils.isEmpty(this.getLastName())) {
//            fullName = fullName + " " + this.getLastName();
//        }

        fullName = getUsername();
        return AppCommon.formatUTF8(fullName);
    }

    public String getFullNameOrEmail() {
        String fullName = this.getFullName();
        if (TextUtils.isEmpty(fullName)) {
            return getEmail();
        }
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirstName() {
        return AppCommon.formatUTF8(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return AppCommon.formatUTF8(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public List<Kid> getKids() {
        return kids;
    }

    public void setKids(List<Kid> kids) {
        this.kids = kids;
    }

    @Override
    public String toString() {
        return "Account{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", photo='" + photo + '\'' +
                ", country='" + country + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", userId='" + userId + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
