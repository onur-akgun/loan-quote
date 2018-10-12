package com.github.hansonhsc.loan.quote;

public class LoanQuoteCalculatorTest {
    // test constructor for sorting
    // 0 lenders
    // 1 lender
    // 2 lender with different rates
    // 2 lender with same rate, different amount
    // 2 lender with same rate, same amount
    // 5 lenders with 2 same rate, different amount
    // 5 lenders with all same rate, but different amount

    // test getLendersForLoan
    // 0 lenders
    // 1 lender with 0 amount
    // 1 lender with not enough amount
    // 2 lenders with not enough amount
    // 1 lender with more than amount
    // 1 lender with exactly amount
    // 2 lenders with more than amount
    // 2 lenders with exactly amount
    // 5 lenders with 1 lender needed
    // 5 lenders with 3 lenders needed
    // 5 lenders with 5 lenders needed
    // 2 lenders with same rate
    // 5 lenders with same rate

    // test getQuote
    // mock getLendersForLoan
    // mock monthlyRepaymentCalculation
    // mock getApproximateAnnualInterestRate
    // test rate rounding
    // test with 1 monthly repayment
    // test with 2 monthly repayments
    // test with 5 monthly repayments
    // test monthly repayment rounding
    // test total repayment without monthly repayment fractional pennies
    // test total repayment with monthly repayment fractional pennies
    // test total repayment rounding
}
