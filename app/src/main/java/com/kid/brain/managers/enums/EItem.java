package com.kid.brain.managers.enums;

public enum EItem {
    TUTORIAL("ITEM_1"),
    SEARCH("ITEM_2"),
    PROFILE("ITEM_3"),
    KID("ITEM_4");

    String itemId;

    EItem(String itemId) {
        this.itemId = itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}
