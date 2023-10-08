package com.dws.challenge.exception;

public class DuplicateTransactionException extends RuntimeException {

  public DuplicateTransactionException(String message) {
    super(message);
  }
}
