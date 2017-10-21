package de.jonathanp.n26backend;

import java.util.ListIterator;

public class TransactionStatisticsManager {

    public TransactionStatisticsManager()
    {
        dataBuffer = new CircularBuffer<>(BUFFER_SIZE);
        cumulative = new Statistics();
    }

    public boolean addTransaction(Transaction transaction, long currentTime)
    {
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
        if (currentTime < lastUpdate)
            throw new IllegalArgumentException("Time cannot go backwards");

        if(currentTime == lastUpdate)
            return;

        ListIterator<Statistics> iterator = dataBuffer.iterator(timestampBucket(lastUpdate), timestampBucket(currentTime));

        while(iterator.hasNext())
        {
            Statistics milliStats = iterator.next();
            cumulative.subtract(milliStats);
            if (milliStats.getMax() == cumulative.getMax())
                maxReset = true;
            if (milliStats.getMin() == cumulative.getMin())
                minReset = true;
            iterator.set(new Statistics());
        }
    }

    private int timestampBucket(long timestamp)
    {
        return (int) timestamp % BUFFER_SIZE;
    }

    Statistics cumulative;
    CircularBuffer<Statistics> dataBuffer;
    boolean maxReset;
    boolean minReset;
    private long lastUpdate;

    static final int BUFFER_SIZE = 60000;

}
