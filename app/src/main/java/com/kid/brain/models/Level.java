package com.kid.brain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Level implements Serializable {

    @SerializedName("ageId")
    private int id;

    @SerializedName("icon")
    private String icon;

    @SerializedName("ageRange")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("categoryVOs")
    private List<Category> categories;


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Id:" + getId() +"\n"
                + "Name:" + getName() + "\n"
                + "Icon:" + getIcon() + "\n"
                + "Description:" + getDescription() + "\n"
                + "Status:" + getStatus();
    }
}
