package de.jonathanp.transactionstatistics;

import java.util.ListIterator;

public class TransactionStatisticsManager {

    public TransactionStatisticsManager()
    {
        resetData();
    }

    public boolean addTransaction(Transaction transaction, long currentTime)
    {
        System.out.println(transaction.getTimestamp());
        //Check if the timestamp is in out range
        if (currentTime < transaction.getTimestamp() || currentTime - transaction.getTimestamp() > BUFFER_SIZE)
        {
            return false;
        }

        synchronized (this)
        {
            cleanUp(currentTime);

            Statistics milliStats = dataBuffer.get(timestampBucket(transaction.getTimestamp()));
            milliStats.add(transaction.getAmount());
            cumulative.add(transaction.getAmount());
        }

        return true;
    }

    public Statistics getCumulativeStatistics(long currentTime)
    {
        Statistics statsCopy;

        synchronized (this)
        {
            cleanUp(currentTime);
            statsCopy = new Statistics(cumulative);
        }

        return statsCopy;
    }

    /*
    Iterates over all the expired buckets, erases them and updates the cumulative statistics
     */
    private void cleanUp(long currentTime)
    {
        //No time advance, no need to do anything
        if(currentTime == lastUpdate)
            return;

        if (currentTime < lastUpdate)
            throw new IllegalArgumentException("Time cannot go backwards");

        //If more than 60 seconds has gone by, erase everything
        long timeDifference = currentTime - lastUpdate;
        if(timeDifference >= BUFFER_SIZE) {
            resetData();
            lastUpdate = currentTime;
            maxReset = false;
            minReset = false;
            return;
        }

        System.out.println("Cleaning from " + timestampBucket(lastUpdate + 1) + " " +  timestampBucket(currentTime));

        ListIterator<Statistics> iterator = dataBuffer.iterator(timestampBucket(lastUpdate + 1), timestampBucket(currentTime));

        while(iterator.hasNext())
        {
            System.out.println("Processing " + (iterator.nextIndex()));
            Statistics milliStats = iterator.next();
            cumulative.subtract(milliStats);
            if (milliStats.getMax() == cumulative.getMax())
                maxReset = true;
            if (milliStats.getMin() == cumulative.getMin())
                minReset = true;
            iterator.set(new Statistics());
        }

        System.out.println("Cleanup complete");

        lastUpdate = currentTime;

        if(maxReset || minReset){
            iterator = dataBuffer.iterator(timestampBucket(currentTime + 1), timestampBucket(lastUpdate));
        }
    }

    private void resetData()
    {
        dataBuffer = new CircularBuffer<>(Statistics.class, BUFFER_SIZE);
        cumulative = new Statistics();
    }

    private int timestampBucket(long timestamp)
    {
        return (int) timestamp % BUFFER_SIZE;
    }

    private Statistics cumulative;
    private CircularBuffer<Statistics> dataBuffer;
    private boolean maxReset;
    private boolean minReset;
    private long lastUpdate;

    static final int BUFFER_SIZE = 60000;

}
