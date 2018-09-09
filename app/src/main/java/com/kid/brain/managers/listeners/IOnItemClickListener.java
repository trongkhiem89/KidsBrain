package com.kid.brain.managers.listeners;

/**
 * Created by khiemnt on 4/17/17.
 */

public interface IOnItemClickListener {
    <T> void onItemClickListener(T object);
    <T> void onItemLongClickListener(T object);
}
