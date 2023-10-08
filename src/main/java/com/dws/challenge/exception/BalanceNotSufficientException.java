package com.dws.challenge.exception;

public class BalanceNotSufficientException extends RuntimeException {

  public BalanceNotSufficientException(String message) {
    super(message);
  }
}
