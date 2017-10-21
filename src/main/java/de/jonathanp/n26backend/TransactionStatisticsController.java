package de.jonathanp.n26backend;

import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class TransactionStatisticsController {

    TransactionStatisticsManager statisticsManager;

    TransactionStatisticsController()
    {
        statisticsManager = new TransactionStatisticsManager();
    }

    @RequestMapping(method= RequestMethod.POST, value="/transactions")
    public ResponseEntity<?> transactions(@RequestBody Transaction input) {

        if(statisticsManager.addTransaction(input, Instant.now().toEpochMilli())) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @RequestMapping(method= RequestMethod.GET, value="/statistics")
    public Statistics statistics() {
        return statisticsManager.getCumulativeStatistics(Instant.now().toEpochMilli());
    }
}