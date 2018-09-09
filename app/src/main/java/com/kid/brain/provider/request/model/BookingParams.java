package com.kid.brain.provider.request.model;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class BookingParams implements Serializable {

    private String phone;
    private String email;
    private String fullName;
    private String content;

    public BookingParams(String phone, String email, String fullName, String content) {
        this.phone = phone;
        this.email = email;
        this.content = content;
        this.fullName = fullName;
    }
}
