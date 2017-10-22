package de.jonathanp.transactionstatistics;


import static org.junit.Assert.assertEquals;

public class Utils {

    public static void checkValues(Statistics stats, double sum, double avg, double max, double min, long count)
    {
        assertEquals("Sum", sum, stats.getSum(), 0);
        assertEquals("Avg",avg, stats.getAvg(), 0);
        assertEquals("Max",max, stats.getMax(), 0);
        assertEquals("Min", min, stats.getMin(), 0);
        assertEquals("Count",count, stats.getCount());
    }

    public static void checkValues(Statistics stats, double sum, double avg, long count)
    {
        assertEquals("Sum",sum, stats.getSum(), 0);
        assertEquals("Avg",avg, stats.getAvg(), 0);
        assertEquals("Count",count, stats.getCount());
    }
}
