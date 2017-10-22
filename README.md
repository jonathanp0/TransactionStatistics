This project implements a REST API which maintains cumulative statistics(sum, mean average, maximum, minimum, count) for
transactions that have occurred over the previous 60 seconds.

To maintain fixed memory and execution times(O(1)) it uses a fixed size buffer, with one entry for each
millisecond of history, each of which stores cumulative statistics for the transactions that occurred in that millisecond.
A record of the overall cumulative statistics is also maintained.
Every time a call is made add a new transaction, or to retrieve the current statistics, the stale data is cleared from
the buffer and the overall cumulative statistics are updated to remove them, before the request is processed.
Java synchronisation is used to ensure that multiple threads can call the manager methods without causing the internal
state to be inconsistent.

Various optimisations have been made to detect cases when it is not necessary to iterate over buffer at all, although it
would be possible to go further.

Unit test are included to check the logic of the Statistics and TransactionStatisticsManager classes, which can be
executed with `mvn test`.
To run a server for testing purposes, execute `mvn spring-boot:run`.
