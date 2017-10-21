package de.jonathanp.transactionstatistics;

public class Statistics implements Cloneable{

    public Statistics()
    {
    }

    public Statistics(Statistics other)
    {
        this.sum = other.sum;
        this.avg = other.avg;
        this.max = other.max;
        this.min = other.min;
        this.count = other.count;
    }

    public void reset()
    {
        sum = 0;
        avg = 0;
        max = 0;
        min = 0;
        count = 0;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public void subtract(Statistics other) {
        sum -= other.sum;
        count -= other.count;
    }

    public void add(double amount) {
        sum += amount;
        count ++;
        max = Math.max(max, amount);
        min = Math.min(min, amount);
    }

    private double sum;
    private double avg;
    private double max;
    private double min;
    private double count;
}
