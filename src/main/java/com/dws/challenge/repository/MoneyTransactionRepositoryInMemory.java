package com.dws.challenge.repository;

import com.dws.challenge.domain.MoneyTransaction;
import com.dws.challenge.exception.DuplicateTransactionException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MoneyTransactionRepositoryInMemory implements MoneyTransactionRepository {
    private final Map<String, MoneyTransaction> transactions = new ConcurrentHashMap<>();

    @Override
    public void saveMoneyTransaction(MoneyTransaction moneyTransaction) {
        MoneyTransaction transaction = transactions.putIfAbsent(moneyTransaction.transactionId(), moneyTransaction);
        if (transaction != null) {
            throw new DuplicateTransactionException(
                    "Transaction id " + moneyTransaction.transactionId() + " already exists!");
        }
    }

    @Override
    public void clearTransactions() {
        transactions.clear();
    }
}
