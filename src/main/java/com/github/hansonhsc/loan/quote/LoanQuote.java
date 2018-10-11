package com.github.hansonhsc.loan.quote;

import java.math.BigDecimal;

public class LoanQuote {
    private final int loanAmount;
    private final BigDecimal rate;
    private final BigDecimal monthlyRepayment;
    private final BigDecimal totalRepayment;

    public LoanQuote(final int loanAmount, final BigDecimal rate, final BigDecimal monthlyRepayment, final BigDecimal totalRepayment) {
        this.loanAmount = loanAmount;
        this.rate = rate;
        this.monthlyRepayment = monthlyRepayment;
        this.totalRepayment = totalRepayment;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getMonthlyRepayment() {
        return monthlyRepayment;
    }

    public BigDecimal getTotalRepayment() {
        return totalRepayment;
    }
}
