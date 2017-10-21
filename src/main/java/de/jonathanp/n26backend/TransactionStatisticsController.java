package de.jonathanp.n26backend;

import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionStatisticsController {

    @RequestMapping(method= RequestMethod.POST, value="/transactions")
    public ResponseEntity<?> greeting(@RequestBody Transaction input) {

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}