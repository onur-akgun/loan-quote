package com.github.hansonhsc.loan;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmortizedLoanMonthlyRepaymentTest {
    private String getMonthlyRepayment(final int principal, final double annualInterestRatePercent, final int numberOfPaymentPeriods) {
        return AmortizedLoan.getMonthlyRepayment(
                new BigDecimal(principal),
                new BigDecimal(annualInterestRatePercent).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP),
                numberOfPaymentPeriods
        ).setScale(2, ROUND_HALF_UP).toString();
    }

    @Test
    void testInvalidInput() {
        // principal=0
        // principal=-1
        // negative interest rate
        // 0 interest rate
        // 0 payment periods
        // -1 payment periods
        // Integer.MAX payment periods
    }

    @Test
    void testMonthlyRepayment() {
        assertEquals("32.27", getMonthlyRepayment(1_000, 10, 36));
        assertEquals("32267.19", getMonthlyRepayment(1_000_000, 10, 36));
        assertEquals("0.32", getMonthlyRepayment(10, 10, 36));
        assertEquals("88.28", getMonthlyRepayment(1_000, 100, 36));
        assertEquals("833.33", getMonthlyRepayment(1_000, 1000, 36));
        assertEquals("28.21", getMonthlyRepayment(1_000, 1, 36));
        assertEquals("0.03", getMonthlyRepayment(1, 10, 36));
    }

    // >1 interest rate
    // principal=Integer.MAX
}
