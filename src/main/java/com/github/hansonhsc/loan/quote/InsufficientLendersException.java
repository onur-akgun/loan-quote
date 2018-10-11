package com.github.hansonhsc.loan.quote;

public class InsufficientLendersException extends Exception {
    public InsufficientLendersException(final String message) {
        super(message);
    }
}
