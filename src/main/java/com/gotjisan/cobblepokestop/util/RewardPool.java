package com.gotjisan.cobblepokestop.util;

public class RewardPool {
    private String item;
    private int count;
    private int roll;

    // Default constructor for JSON deserialization
    public RewardPool() {}

    public RewardPool(String item, int count, int roll) {
        this.item = item;
        this.count = count;
        this.roll = roll;
    }

    // Getters and setters
    public String getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public int getRoll() {
        return roll;
    }
    @Override
    public String toString() {
        return "RewardPool{" +
                "item='" + item + '\'' +
                ", count=" + count +
                ", roll=" + roll +
                '}';
    }
}