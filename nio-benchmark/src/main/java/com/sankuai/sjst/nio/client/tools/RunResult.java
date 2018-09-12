package com.sankuai.sjst.nio.client.tools;

public class RunResult {

    private int threads;

    private long totalCost;

    private long average;

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public long getAverage() {
        return average;
    }


    public void setAverage(long average) {
        this.average = average;
    }

    @Override
    public String toString() {
        return "RunResult{" +
                "threads=" + threads +
                ", totalCost=" + totalCost +
                ", average=" + average +
                '}';
    }
}
