package com.kid.brain.managers.listeners;

/**
 * Created by khiemnt on 4/17/17.
 */

public interface IActivityCommunicatorListener {
    <T> void passDataToActivity(T object);
}
