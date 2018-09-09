package com.kid.brain.models;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Item implements Serializable {

    private String id;
    private String icon;
    private String name;
    private int sort;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int order) {
        this.sort = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", status='" + status + '\'' +
                '}';
    }
}
