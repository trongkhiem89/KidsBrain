package com.kid.brain.managers.enums;

public enum EKidCate {
    Normal(1),
    Autism(2),
    CerebralPalsy(3);

    int name;

    EKidCate(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }
}
