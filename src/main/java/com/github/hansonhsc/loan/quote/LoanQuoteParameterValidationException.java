package com.github.hansonhsc.loan.quote;

public class LoanQuoteParameterValidationException extends Exception {
    public LoanQuoteParameterValidationException(final String message) {
        super(message);
    }

    public LoanQuoteParameterValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
