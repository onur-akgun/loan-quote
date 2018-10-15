package com.github.hansonhsc.loan;

import org.junit.jupiter.api.Test;

import static com.github.hansonhsc.loan.AmortizedLoan.getApproximateAnnualInterestRate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmortizedLoanApproximateAnnualInterestRateTest {
    @Test
    void testAnnualInterestRate() {
//        assertEquals(7.0, getApproximateAnnualInterestRate(1000, 36, 30.78));
    }
    // double getApproximateAnnualInterestRate(final double principal, final int term, final double monthlyPayment)

    // test negative principal
    // test 0 principal
    // test 1 principal
    // test negative term
    // test 0 term
    // test 1 term
    // test negative payment
    // test 0 payment
    // test payment = principal - 1
    // test payment = principal
    // test payment = principal + 1
    // test payment = principal * 2

    // test normal term=36
    // test normal term=12
    // test normal term=24
}
