package com.dws.challenge.repository;

import com.dws.challenge.domain.MoneyTransaction;

public interface MoneyTransactionRepository {
    void saveMoneyTransaction(MoneyTransaction moneyTransaction);
    void clearTransactions();
}
