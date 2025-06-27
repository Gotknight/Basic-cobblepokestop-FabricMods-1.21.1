package com.gotjisan.cobblepokestop.util;

public class RewardPool {
    private String item;
    private int count;
    private int weight;

    // Default constructor for JSON deserialization
    public RewardPool() {}

    public RewardPool(String item, int count, int weight) {
        this.item = item;
        this.count = count;
        this.weight = weight;
    }

    // Getters and setters
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "RewardPool{" +
                "item='" + item + '\'' +
                ", count=" + count +
                ", weight=" + weight +
                '}';
    }
}