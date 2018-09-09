package com.kid.brain.provider.request.model;

import com.kid.brain.models.Account;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class EditProfileParams implements Serializable {

    private String email;
    private String phone;
    private String fullName;
    private int gender;
    private String address;
    private String imgBase64;
    private String dob;

    public EditProfileParams(Account account) throws NullPointerException{
        this.email = account.getEmail();
        this.phone = account.getMobile();
        this.fullName = account.getFullNameOrEmail();
        this.gender = account.getGender();
        this.address = account.getAddress();
        this.dob = account.getBirthDay();
        this.imgBase64 = account.getPhoto();
    }
}
