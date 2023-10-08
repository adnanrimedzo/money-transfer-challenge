package com.dws.challenge.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyTransfer  {
    @NotNull
    @NotEmpty
    String transactionId;
    @NotNull
    @NotEmpty
    String sourceAccountId;
    @NotNull
    @NotEmpty
    String targetAccountId;
    @NotNull
    @DecimalMin(value = "0.01", message = "transfer amount must be positive.")
    BigDecimal amount;

    public MoneyTransfer(String transactionId,
                         String sourceAccountId,
                         String targetAccountId,
                         BigDecimal amount
    ) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
    }
}
