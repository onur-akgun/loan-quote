package com.github.hansonhsc.loan.quote;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanQuoteCalculator {
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
        final Map<Lender, Integer> loans = new HashMap<>();

        int remainingLoanAmount = loanAmount;

        for (final Lender lender : lenders) {
            // can this lender satisfy remaining loan required?
            if (lender.getAmount() >= remainingLoanAmount) {
                loans.put(lender, remainingLoanAmount);

                return new LoanQuote(loanAmount, loans);
            }

            // use up all of lender's quota
            loans.put(lender, lender.getAmount());

            remainingLoanAmount -= lender.getAmount();
        }

        throw new RuntimeException("Not enough lenders"); // TODO: choose the right exception type
    }
}
