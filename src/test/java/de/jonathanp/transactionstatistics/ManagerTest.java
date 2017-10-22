package de.jonathanp.transactionstatistics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static de.jonathanp.transactionstatistics.Utils.checkValues;
import static de.jonathanp.transactionstatistics.Utils.checkLimits;
import org.junit.Test;

public class ManagerTest {

    private static long BASE_TIMESTAMP = 1000000;
    private static long STORAGE_SIZE = 60000;

    @Test
    public void testAddTimestamps() {
        TransactionStatisticsManager manager = new TransactionStatisticsManager();
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP), 0,0,0,0,0);

        Transaction trans = new Transaction(0, BASE_TIMESTAMP);
        assertFalse(manager.addTransaction(trans, BASE_TIMESTAMP - 1));
        assertFalse(manager.addTransaction(trans, BASE_TIMESTAMP + STORAGE_SIZE));
        assertTrue(manager.addTransaction(trans, BASE_TIMESTAMP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimeError() {
        TransactionStatisticsManager manager = new TransactionStatisticsManager();

        manager.getCumulativeStatistics(BASE_TIMESTAMP);
        manager.getCumulativeStatistics(BASE_TIMESTAMP - 1);

    }

    @Test
    public void testCumulativeStatistics()
    {
        TransactionStatisticsManager manager = new TransactionStatisticsManager();
        //Add a single transaction in the past
        manager.addTransaction(new Transaction(100, BASE_TIMESTAMP), BASE_TIMESTAMP + 100);
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + 100), 100, 100, 100, 100, 1);
        //Add another transaction at the same time in the past
        manager.addTransaction(new Transaction(200, BASE_TIMESTAMP), BASE_TIMESTAMP + 200);
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + 300), 300, 150, 200, 100, 2);

        //Add another value at a later point
        manager.addTransaction(new Transaction(51, BASE_TIMESTAMP + 100), BASE_TIMESTAMP + 300);
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + 300), 351, 117, 200, 51, 3);

        //Advance time so that the first values expire
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE), 51, 51, 51, 51, 1);
        //Advance again so all values are gone
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE + 200), 0,0,0,0,0);
    }

    @Test
    public void testFullReset() {
        TransactionStatisticsManager manager = new TransactionStatisticsManager();
        manager.addTransaction(new Transaction(100, BASE_TIMESTAMP), BASE_TIMESTAMP);
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + 100), 100, 100, 100, 100, 1);
        checkValues(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE + 100), 0,0,0,0,0);
    }

    @Test
    public void testRecalculateMinMax() {
        TransactionStatisticsManager manager = new TransactionStatisticsManager();
        manager.addTransaction(new Transaction(6000, BASE_TIMESTAMP - 100), BASE_TIMESTAMP + 500);
        manager.addTransaction(new Transaction(2000, BASE_TIMESTAMP + 200), BASE_TIMESTAMP + 500);
        manager.addTransaction(new Transaction(4000, BASE_TIMESTAMP + 300), BASE_TIMESTAMP + 500);
        manager.addTransaction(new Transaction(3000, BASE_TIMESTAMP + 301), BASE_TIMESTAMP + 500);
        checkLimits(manager.getCumulativeStatistics(BASE_TIMESTAMP + 550), 6000, 2000);

        //Recalculate the maximum
        checkLimits(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE), 4000, 2000);
        //Recalculate the minimum
        checkLimits(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE + 250), 4000, 3000);
        //Recalculate max again
        checkLimits(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE + 300), 3000, 3000);
        //Everything is gone
        checkLimits(manager.getCumulativeStatistics(BASE_TIMESTAMP + STORAGE_SIZE + 301), 0, 0);
    }
}
