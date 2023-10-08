package com.dws.challenge.web;

import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.exception.AccountNotExist;
import com.dws.challenge.exception.BalanceNotSufficientException;
import com.dws.challenge.exception.DuplicateTransactionException;
import com.dws.challenge.service.MoneyTransferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/v1/money-transfer")
@RestController
public class MoneyTransferController {

    private final MoneyTransferService moneyTransferService;

    public MoneyTransferController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createTransfer(@RequestBody @Valid MoneyTransfer moneyTransfer) {
        log.info("Money transfer request from account id {} to account id {}", moneyTransfer.getSourceAccountId(), moneyTransfer.getTargetAccountId());
        moneyTransferService.createMoneyTransfer(moneyTransfer);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler({DuplicateTransactionException.class})
    public ResponseEntity<String> handleException(DuplicateTransactionException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({BalanceNotSufficientException.class})
    public ResponseEntity<String> handleException(BalanceNotSufficientException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({AccountNotExist.class})
    public ResponseEntity<String> handleException(AccountNotExist exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
