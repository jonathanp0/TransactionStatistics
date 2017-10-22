package de.jonathanp.transactionstatistics;

import static org.junit.Assert.assertEquals;

import de.jonathanp.transactionstatistics.Statistics;
import org.junit.Test;

public class StatisticsTest {
    @Test
    public void addAndReset() {
        Statistics stats = new Statistics();
        checkValues(stats, 0, 0,0,0,0);

        stats.add(100);
        checkValues(stats, 100, 100, 100, 100, 1);
        stats.add(200);
        checkValues(stats, 300, 150, 200, 100, 2);
        stats.add(51);
        checkValues(stats, 351, 117, 200, 51, 3);
        stats.add(150);
        checkValues(stats, 501, 125.25, 200, 51, 4);
        stats.reset();
        checkValues(stats, 0, 0,0,0,0);
    }

    @Test
    public void subtractTest() {
        Statistics bucketOne = new Statistics();
        bucketOne.add(100);
        bucketOne.add(200);

        Statistics bucketTwo = new Statistics();
        bucketTwo.add(300);
        bucketTwo.add(400);

        Statistics cumulative = new Statistics(bucketOne);
        cumulative.add(300);
        cumulative.add(400);

        cumulative.subtract(bucketOne);
        //Now values should be equal to bucket 2
        checkValues(cumulative, 700, 350, 2);

        cumulative.subtract(bucketTwo);
        checkValues(cumulative, 0, 0, 0);
    }

    void checkValues(Statistics stats, double sum, double avg, double max, double min, long count)
    {
        assertEquals(sum, stats.getSum(), 0);
        assertEquals(avg, stats.getAvg(), 0);
        assertEquals(max, stats.getMax(), 0);
        assertEquals(min, stats.getMin(), 0);
        assertEquals(count, stats.getCount());
    }

    void checkValues(Statistics stats, double sum, double avg, long count)
    {
        assertEquals(sum, stats.getSum(), 0);
        assertEquals(avg, stats.getAvg(), 0);
        assertEquals(count, stats.getCount());
    }
}