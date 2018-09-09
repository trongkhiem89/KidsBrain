package com.kid.brain.managers.enums;

public enum EGender {
    MALE("Nam"),
    FEMALE("Nữ");

    String name;

    EGender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
