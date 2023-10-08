package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.MoneyTransaction;
import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.exception.BalanceNotSufficientException;
import com.dws.challenge.exception.DuplicateTransactionException;
import com.dws.challenge.repository.MoneyTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyTransferServiceTest {

    @Mock
    private MoneyTransactionRepository moneyTransactionRepository;
    @Mock
    private AccountsService accountsService;
    @Mock
    private NotificationService notificationService;

    private MoneyTransferService moneyTransferService;

    @BeforeEach
    void setUp() {
        moneyTransferService = new MoneyTransferService(moneyTransactionRepository, accountsService, notificationService);
    }

    @Test
    void createMoneyTransfer_successTransaction() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.TEN);
        Account sourceAccount = new Account("sourceAccountId", BigDecimal.valueOf(100));
        Account targetAccount = new Account("targetAccountId", BigDecimal.valueOf(50));

        when(accountsService.getAccount("sourceAccountId")).thenReturn(sourceAccount);
        when(accountsService.getAccount("targetAccountId")).thenReturn(targetAccount);

        moneyTransferService.createMoneyTransfer(moneyTransfer);

        verify(moneyTransactionRepository, times(1)).saveMoneyTransaction(any(MoneyTransaction.class));
        verify(accountsService, times(1)).getAccount("sourceAccountId");
        verify(accountsService, times(1)).getAccount("targetAccountId");
        verify(notificationService, times(1)).notifyAboutTransfer(sourceAccount, "Money transfer has been done from your account.");
        verify(notificationService, times(1)).notifyAboutTransfer(targetAccount, "Money transfer has been received to your account.");
        assertEquals(BigDecimal.valueOf(90), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(60), targetAccount.getBalance());
    }

    @Test
    void createMoneyTransfer_whenTransactionExist_fail() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.TEN);
        doThrow(new DuplicateTransactionException("duplicate transaction")).when(moneyTransactionRepository).saveMoneyTransaction(any());

        Exception exception = assertThrows(DuplicateTransactionException.class, () -> {
            moneyTransferService.createMoneyTransfer(moneyTransfer);
        });

        verify(moneyTransactionRepository, times(1)).saveMoneyTransaction(any(MoneyTransaction.class));
        verifyNoInteractions(accountsService);
        verifyNoInteractions(accountsService);
        verifyNoInteractions(notificationService);
        assertEquals("duplicate transaction", exception.getMessage());
    }

    @Test
    void createMoneyTransfer_whenBalanceNotSufficient_fail() {
        MoneyTransfer moneyTransfer = new MoneyTransfer("123", "sourceAccountId", "targetAccountId", BigDecimal.TEN);
        Account sourceAccount = new Account("sourceAccountId", BigDecimal.valueOf(5));
        Account targetAccount = new Account("targetAccountId", BigDecimal.valueOf(50));

        when(accountsService.getAccount("sourceAccountId")).thenReturn(sourceAccount);
        when(accountsService.getAccount("targetAccountId")).thenReturn(targetAccount);

        Exception exception = assertThrows(BalanceNotSufficientException.class, () -> {
            moneyTransferService.createMoneyTransfer(moneyTransfer);
        });

        verify(moneyTransactionRepository, times(1)).saveMoneyTransaction(any(MoneyTransaction.class));
        verify(accountsService, times(1)).getAccount("sourceAccountId");
        verify(accountsService, times(1)).getAccount("targetAccountId");
        verifyNoInteractions(notificationService);
        assertEquals("balance is not sufficient", exception.getMessage());
        assertEquals(BigDecimal.valueOf(5), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(50), targetAccount.getBalance());
    }
}