package com.github.hansonhsc.loan.quote;

/**
 * Throw when there is an insufficient amount of lenders for the loan amount requested
 */
public final class InsufficientLendersException extends Exception {
    public InsufficientLendersException() {
    }
}
