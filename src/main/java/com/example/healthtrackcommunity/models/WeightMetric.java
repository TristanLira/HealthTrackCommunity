package com.example.healthtrackcommunity.models;


public class WeightMetric extends Metric {

    private int weight;
    private int height;

    public WeightMetric(int height, int weight) {
        super();
        this.weight = weight;
        this.height = height;
    }

    public WeightMetric() {}

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

}
