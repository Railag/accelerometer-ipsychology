package com.inri.sopsop;

public interface AccelerometerListener {
    void onLeft();

    void onRight();

    void onMinThreshold();

    void onUpdate(double x, double y, double z);
}