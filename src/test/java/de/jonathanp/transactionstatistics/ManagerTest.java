package de.jonathanp.transactionstatistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static de.jonathanp.transactionstatistics.Utils.checkValues;
import org.junit.Test;

public class ManagerTest {

    static long BASE_TIMESTAMP = 1000000;
    static long STORAGE_SIZE = 60000;

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
     
}
