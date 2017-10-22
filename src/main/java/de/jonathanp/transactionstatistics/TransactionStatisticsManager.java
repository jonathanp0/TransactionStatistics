package de.jonathanp.transactionstatistics;

import java.util.ListIterator;

class TransactionStatisticsManager {

    public TransactionStatisticsManager() {
        resetData();
    }

    public boolean addTransaction(Transaction transaction, long currentTime) {
        //Check if the timestamp is in out range
        if (currentTime < transaction.getTimestamp() || currentTime - transaction.getTimestamp() >= BUFFER_SIZE) {
            return false;
        }
        System.out.println("Adding transaction at " + timestampBucket(transaction.getTimestamp()));
        synchronized (this) {
            cleanUp(currentTime);

            Statistics milliStats = dataBuffer.get(timestampBucket(transaction.getTimestamp()));
            milliStats.add(transaction.getAmount());
            cumulative.add(transaction.getAmount());
        }

        return true;
    }

    public Statistics getCumulativeStatistics(long currentTime) {
        Statistics statsCopy;

        synchronized (this) {
            cleanUp(currentTime);
            statsCopy = new Statistics(cumulative);
        }

        return statsCopy;
    }

    /*
    Iterates over all the expired buckets, erases them and updates the cumulative statistics
     */
    private void cleanUp(long currentTime) {
        //No time advance, no need to do anything
        if (currentTime == lastUpdate)
            return;

        if (currentTime < lastUpdate)
            throw new IllegalArgumentException("Time cannot go backwards");

        //If more than 60 seconds has gone by, erase everything
        long timeDifference = currentTime - lastUpdate;
        if (timeDifference >= BUFFER_SIZE) {
            resetData();
            lastUpdate = currentTime;
            return;
        }

        System.out.println("Cleaning from " + timestampBucket(lastUpdate + 1) + " to " + timestampBucket(currentTime));

        boolean maxReset = false;
        boolean minReset = false;

        ListIterator<Statistics> iterator = dataBuffer.iterator(timestampBucket(lastUpdate + 1), timestampBucket(currentTime));

        while (iterator.hasNext()) {
            Statistics milliStats = iterator.next();
            cumulative.subtract(milliStats);
            if (milliStats.getMax() == cumulative.getMax())
                maxReset = true;
            if (milliStats.getMin() == cumulative.getMin())
                minReset = true;
            iterator.set(new Statistics());
        }

        //Scan the remaining data for a new minimum and maximum, if required
        if (cumulative.getCount() == 0) {
            cumulative.setMin(0);
            cumulative.setMax(0);
        } else if (maxReset || minReset) {
            System.out.println("Scanning for min/max " + timestampBucket(currentTime + 1) + " " + timestampBucket(lastUpdate));
            iterator = dataBuffer.iterator(timestampBucket(currentTime + 1), timestampBucket(lastUpdate));

            double newMax = 0;
            double newMin = 0;

            while (iterator.hasNext()) {
                Statistics milliStats = iterator.next();
                if (maxReset) {
                    newMax = Math.max(newMax, milliStats.getMax());
                }

                if (minReset && milliStats.getCount() > 0) {
                    if (newMin == 0) {
                        newMin = milliStats.getMin();
                    } else {
                        newMin = Math.min(newMin, milliStats.getMin());
                    }
                }
            }
            if (maxReset) {
                cumulative.setMax(newMax);
            }
            if (minReset) {
                cumulative.setMin(newMin);
            }

        }

        lastUpdate = currentTime;
    }

    private void resetData() {
        dataBuffer = new CircularBuffer<>(Statistics.class, BUFFER_SIZE);
        cumulative = new Statistics();
    }

    private int timestampBucket(long timestamp) {
        return (int) timestamp % BUFFER_SIZE;
    }

    private Statistics cumulative;
    private CircularBuffer<Statistics> dataBuffer;
    private long lastUpdate;

    private static final int BUFFER_SIZE = 60000;

}
