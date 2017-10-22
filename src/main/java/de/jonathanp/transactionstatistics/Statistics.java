package de.jonathanp.transactionstatistics;

public class Statistics implements Cloneable {

    public Statistics() {
    }

    public Statistics(Statistics other) {
        this.sum = other.sum;
        this.avg = other.avg;
        this.max = other.max;
        this.min = other.min;
        this.count = other.count;
    }

    public void reset() {
        sum = 0;
        avg = 0;
        max = 0;
        min = 0;
        count = 0;
    }

    public double getSum() {
        return sum;
    }

    public double getAvg() {
        return avg;
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

    public long getCount() {
        return count;
    }

    /* Subtract another Statistics object from our totals.
       Warning: Invalidates the min and max values.
     */
    public void subtract(Statistics other) {

        if (count == other.count) {
            avg = 0;
        } else {
            avg = ((avg * count) - other.sum) / (count - other.count);
        }

        count -= other.count;
        sum -= other.sum;
    }

    public void add(double amount) {
        count++;
        avg = avg + (amount - avg) / count;
        sum += amount;
        if (count == 1) {
            max = amount;
            min = amount;
        } else {
            max = Math.max(max, amount);
            min = Math.min(min, amount);
        }
    }

    private double sum;
    private double avg;
    private double max;
    private double min;
    private long count;
}
