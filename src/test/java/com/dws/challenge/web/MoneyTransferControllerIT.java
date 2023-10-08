package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.repository.MoneyTransactionRepository;
import com.dws.challenge.service.MoneyTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyTransferControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private MoneyTransferService moneyTransferService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private MoneyTransactionRepository moneyTransactionRepository;

    @BeforeEach
    void beforeAll() {
        accountsRepository.clearAccounts();
        Account sourceAccount = new Account("sourceAccountId", BigDecimal.valueOf(90));
        Account targetAccount = new Account("targetAccountId", BigDecimal.valueOf(50));
        accountsRepository.createAccount(sourceAccount);
        accountsRepository.createAccount(targetAccount);

        moneyTransactionRepository.clearTransactions();
    }

    @Test
    public void createTransfer_success() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.TEN);

        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/money-transfer",
                moneyTransfer,
                Object.class
        );

        Account sourceAccount = accountsRepository.getAccount("sourceAccountId");
        Account targetAccount = accountsRepository.getAccount("targetAccountId");
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(BigDecimal.valueOf(80), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(60), targetAccount.getBalance());
    }

    @Test
    public void createTransfer_whenDuplicateTransaction_fail() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.TEN);
        moneyTransferService.createMoneyTransfer(moneyTransfer);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/money-transfer",
                moneyTransfer,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Transaction id 123 already exists!", responseEntity.getBody());
    }

    @Test
    public void createTransfer_whenInsufficientBalance_fail() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.valueOf(1000));

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/money-transfer",
                moneyTransfer,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("balance is not sufficient", responseEntity.getBody());
    }

    @Test
    public void createTransfer_whenAccountNotExist_fail() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.valueOf(1000));
        accountsRepository.clearAccounts();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/money-transfer",
                moneyTransfer,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("account with id: sourceAccountId does not exist!", responseEntity.getBody());
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    public void createTransfer_whenInvalidRequest_fail() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.valueOf(-5));

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/money-transfer",
                moneyTransfer,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    private static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of("123", "sourceAccountId", "targetAccountId", BigDecimal.valueOf(-5)),
                Arguments.of("", "sourceAccountId", "targetAccountId", BigDecimal.valueOf(20)),
                Arguments.of("123", "", "targetAccountId", BigDecimal.valueOf(20)),
                Arguments.of("123", "sourceAccountId", "", BigDecimal.valueOf(20)),
                Arguments.of("123", "sourceAccountId", "targetAccountId", null)
        );
    }
}