package com.kid.brain.models;

/**
 * Created by manhptit123 on 6/8/2016.
 */
public class Permission {

    public Permission(String packageName, String displayName) {
        this.packageName = packageName;
        this.displayName = displayName;
    }

    public Permission(String packageName, String displayName, boolean isGranted) {
        this.packageName = packageName;
        this.displayName = displayName;
        this.isGranted = isGranted;
    }
    public String packageName;
    public String displayName;
    public boolean isGranted;
}
