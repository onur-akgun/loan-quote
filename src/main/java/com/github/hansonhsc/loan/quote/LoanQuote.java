package com.github.hansonhsc.loan.quote;

import java.math.BigDecimal;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class LoanQuote {
    private static final int REPAYMENT_MONTHS = 36;
    public static final int DECIMAL_SCALE = 10000;

    private final int loanAmount;
    private final Map<Lender, Integer> loans;

    public LoanQuote(final int loanAmount, final Map<Lender, Integer> loans) {
        this.loanAmount = loanAmount;
        this.loans = loans;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getRate() {
        return new BigDecimal(AmortizedLoan.getEstimatedAnnualInterestRate(loanAmount, REPAYMENT_MONTHS, getMonthlyRepayment().doubleValue()) * 100);
    }

    private static BigDecimal getMonthlyRepayment(final int individualLoanAmount, final BigDecimal monthlyLoanRate) {
        return new BigDecimal(individualLoanAmount).multiply(
                monthlyLoanRate.add(
                        monthlyLoanRate.divide(
                                BigDecimal.ONE.add(monthlyLoanRate).pow(REPAYMENT_MONTHS).subtract(BigDecimal.ONE),
                                DECIMAL_SCALE,
                                ROUND_HALF_UP
                        )
                )
        );
    }

    private static BigDecimal getMonthlyRepayment(final Map.Entry<Lender, Integer> individualLoan) {
        final Lender lender = individualLoan.getKey();
        final Integer individualLoanAmount = individualLoan.getValue();

        return getMonthlyRepayment(individualLoanAmount, lender.getRate().divide(new BigDecimal(12), DECIMAL_SCALE, ROUND_HALF_UP));
    }

    public BigDecimal getMonthlyRepayment() {
        return loans.entrySet().stream()
                .map(LoanQuote::getMonthlyRepayment)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalRepayment() {
        return getMonthlyRepayment().multiply(new BigDecimal(REPAYMENT_MONTHS));
    }
}
