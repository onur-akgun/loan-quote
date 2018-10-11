package com.github.hansonhsc.loan.quote;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanQuoteCalculator {
    private static final int REPAYMENT_MONTHS = 36;

    private final List<Lender> lenders;

    public LoanQuoteCalculator(final List<Lender> lenders) {
        // sort lender based on cheapest rate and the largest amount
        this.lenders = lenders;

        this.lenders.sort((lender1, lender2) -> {
            final int rateComparison = lender1.getRate().compareTo(lender2.getRate());

            return rateComparison != 0 ? rateComparison : lender2.getAmount() - lender1.getAmount();
        });
    }

    public Collection<Lender> getLenders() {
        return lenders;
    }

    public LoanQuote getQuote(final int loanAmount) {
        final Map<Lender, Integer> loans = getLendersForLoan(loanAmount);

        // calculate monthly repayment for each individual lender
        final BigDecimal monthlyRepayment = loans.entrySet().stream()

                // calculate total monthly repayment by calculating monthly repayment towards each individual lender
                .map(individualLoan -> {
                    final Lender lender = individualLoan.getKey();
                    final Integer individualLoanAmount = individualLoan.getValue();

                    return AmortizedLoan.getMonthlyRepayment(new BigDecimal(individualLoanAmount), lender.getRate(), REPAYMENT_MONTHS);
                })

                // add up each monthly repayment
                .reduce(BigDecimal::add)

                // there must be at least one lender, so this is impossible
                .orElseThrow(() -> new IllegalStateException("getLendersForLoan should never return empty map"));

        final BigDecimal totalRepayment = monthlyRepayment.multiply(new BigDecimal(REPAYMENT_MONTHS));

        final double rate = AmortizedLoan.getEstimatedAnnualInterestRate(loanAmount, REPAYMENT_MONTHS, monthlyRepayment.doubleValue()) * 100;

        return new LoanQuote(
                loanAmount,
                new BigDecimal(rate).setScale(1, BigDecimal.ROUND_HALF_UP), // round rate to one decimal place
                monthlyRepayment,
                totalRepayment
        );
    }

    private Map<Lender, Integer> getLendersForLoan(final int loanAmount) {
        final Map<Lender, Integer> result = new HashMap<>();

        int remainingLoanAmount = loanAmount;

        for (final Lender lender : lenders) {
            // can this lender satisfy remaining loan required?
            if (lender.getAmount() >= remainingLoanAmount) {
                result.put(lender, remainingLoanAmount);

                return result;
            }

            // use up all of lender's quota
            result.put(lender, lender.getAmount());

            remainingLoanAmount -= lender.getAmount();
        }

        throw new RuntimeException("Not enough lenders"); // TODO: choose the right exception type
    }
}
