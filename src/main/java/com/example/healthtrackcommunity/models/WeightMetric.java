package com.example.healthtrackcommunity.models;


public class WeightMetric extends Metric {

    private int weight;
    private int height;
    private double bmi;

    public WeightMetric(String userId, int height, int weight) {
        super();
        this.userId = userId;
        this.weight = weight;
        this.height = height;
        calculateBmi();
    }

    public WeightMetric() {}

    private void calculateBmi() {
        double mHeight = ((double) height) * 0.01;
        bmi = ((double) weight) / Math.pow(mHeight, 2);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }
}
