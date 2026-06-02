package com.example.healthtrackcommunity.models;

public class OxygenMetric extends Metric {

    private int oxygen;

    public OxygenMetric(String userId, int oxygen) {
        super();
        this.userId = userId;
        this.oxygen = oxygen;
    }

    public OxygenMetric() {}

    public int getOxygen() {
        return oxygen;
    }

    public void setOxygen(int oxygen) {
        this.oxygen = oxygen;
    }

    public String toString() {
        return super.toString() + " (oxígeno)";
    }
}
