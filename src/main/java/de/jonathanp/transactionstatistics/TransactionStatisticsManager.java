package de.jonathanp.transactionstatistics;

import java.util.ListIterator;

class TransactionStatisticsManager {

    private static final int BUFFER_SIZE = 60000; //Number of milliseconds of history to maintain

    private Statistics cumulative;
    private CircularBuffer<Statistics> dataBuffer;
    private long lastUpdate; //The last time we cleared out stale data

    public TransactionStatisticsManager() {
        resetData();
    }

    /* Adds a new transaction.
       Thread safe method.
     */
    public boolean addTransaction(Transaction transaction, long currentTime) {
        //Check if the timestamp is in our range
        if (currentTime < transaction.getTimestamp() || currentTime - transaction.getTimestamp() >= BUFFER_SIZE) {
            return false;
        }

        synchronized (this) {
            cleanUp(currentTime);

            Statistics milliStats = dataBuffer.get(timestampBucket(transaction.getTimestamp()));
            milliStats.add(transaction.getAmount());
            cumulative.add(transaction.getAmount());
        }

        return true;
    }

    /* Returns a copy of the statistics from the 60 seconds preceding the current time
       Thread safe method.
     */
    public synchronized Statistics getCumulativeStatistics(long currentTime) {

        cleanUp(currentTime);
        return new Statistics(cumulative);
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

        //Subtract and clear all the data that became stale since the last clean up
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
            milliStats.reset();
        }

        //Scan the remaining data for a new minimum and maximum, if required
        if (cumulative.getCount() == 0) {
            cumulative.setMin(0);
            cumulative.setMax(0);
        } else if (maxReset || minReset) {

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

    /* Converts a timestamp into a buffer index */
    private int timestampBucket(long timestamp) {
        return (int) timestamp % BUFFER_SIZE;
    }

}
