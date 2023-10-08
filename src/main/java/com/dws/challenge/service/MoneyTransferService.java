package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.MoneyTransaction;
import com.dws.challenge.domain.MoneyTransfer;
import com.dws.challenge.repository.MoneyTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MoneyTransferService {

    private final MoneyTransactionRepository moneyTransactionRepository;
    private final AccountsService accountsService;
    private final NotificationService notificationService;

    public MoneyTransferService(MoneyTransactionRepository moneyTransactionRepository,
                                AccountsService accountsService,
                                NotificationService notificationService) {
        this.moneyTransactionRepository = moneyTransactionRepository;
        this.accountsService = accountsService;
        this.notificationService = notificationService;
    }

    public void createMoneyTransfer(MoneyTransfer moneyTransfer) {
        MoneyTransaction transaction = new MoneyTransaction(moneyTransfer.getTransactionId(), moneyTransfer.getSourceAccountId());
        moneyTransactionRepository.saveMoneyTransaction(transaction);
        Account sourceAccount = accountsService.getAccount(moneyTransfer.getSourceAccountId());
        Account targetAccount = accountsService.getAccount(moneyTransfer.getTargetAccountId());
        updateBalances(sourceAccount, targetAccount, moneyTransfer.getAmount());
        sendNotification(sourceAccount, targetAccount);
    }

    private void updateBalances(Account sourceAccount, Account targetAccount, BigDecimal amount) {
        sourceAccount.updateBalance(amount.negate());
        targetAccount.updateBalance(amount);
    }

    private void sendNotification(Account sourceAccount, Account targetAccount) {
        notificationService.notifyAboutTransfer(sourceAccount, "Money transfer has been done from your account.");
        notificationService.notifyAboutTransfer(targetAccount, "Money transfer has been received to your account.");
    }
}
