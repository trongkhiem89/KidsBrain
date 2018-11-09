package com.kid.brain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Category implements Serializable {

    @SerializedName("categoryId")
    private int id;

    @SerializedName("ageId")
    private int levelId;

    @SerializedName("icon")
    private String icon;

    @SerializedName("categoryName")
    private String name;

    @SerializedName("linkImgCategory")
    private String avatar;

    @SerializedName("description")
    private String description;

    @SerializedName("status")
    private String status;

    @SerializedName("order")
    private int order;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

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

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Id:" + getId() +"\n"
                + "Name:" + getName() + "\n"
                + "Avatar:" + getAvatar() + "\n"
                + "Icon:" + getIcon() + "\n"
                + "Description:" + getDescription() + "\n"
                + "Status:" + getStatus();
    }
}
