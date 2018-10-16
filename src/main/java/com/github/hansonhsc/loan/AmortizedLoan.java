package com.github.hansonhsc.loan;

import java.math.BigDecimal;
import java.util.function.Function;

import static java.math.RoundingMode.HALF_UP;

/**
 * Utility class to provide calculations based on amortized loans
 */
public final class AmortizedLoan {
    /**
     * The initial annual interest rate to use when used for approximation in
     * <code>getApproximateAnnualInterestRate</code>. Expressed as a decimal, i.e. 10% = 0.01.
     */
    private static final double GUESSED_INTEREST_RATE = 0.10;

    /**
     * Used in <code>newtonRaphsonMethod</code> to progress towards zero
     */
    private static final double EPSILON = 0.00000001;

    /**
     * The scale used in all BigDecimal calculations
     */
    private static final int SCALE = 10;

    /**
     * Calculates an approximate annual interest rate using only the principal, term and monthly repayment
     * @param principal the initial loan amount
     * @param term number of repayment terms
     * @param monthlyPayment amount of repayment per term
     * @return an approximation of the annual interest rate in decimal format (i.e. 0.1 = 10%)
     */
    public static double getApproximateAnnualInterestRate(final double principal, final int term, final double monthlyPayment) {
        final double guessedMonthlyInterestRate = GUESSED_INTEREST_RATE / 12;

        // each month, the new amount owed is calculated by multiplying (the amount currently owed (1) + guessedMonthlyInterestRate)
        final double guessedMonthlyMultiplier = 1 + guessedMonthlyInterestRate;

        // use Newton-Raphson method to estimate the monthly multiplier
        final double estimatedMonthlyMultiplier = newtonRaphsonMethod(
                guessedMonthlyMultiplier,

                // this is the function that we want to find roots of
                m -> (principal + monthlyPayment) * Math.pow(m, term) - principal * Math.pow(m, term + 1) - monthlyPayment,

                // this is the derivative of the above
                m -> (principal + monthlyPayment) * term * Math.pow(m, term - 1) - principal * (term + 1) * Math.pow(m, term)
        );

        final double estimatedMonthlyInterestRate = estimatedMonthlyMultiplier - 1;

        // get the estimated annual interest rate
        return 12 * estimatedMonthlyInterestRate;
    }

    /**
     * Uses Newton-Raphson method to find an approximation of a root, given a function and its derivative
     * @param guess the initial guess of the root
     * @param f the function that tends to zero
     * @param fPrime the derivative function of <code>f</code>
     * @return an approximation of the root
     */
    private static double newtonRaphsonMethod(final double guess, final Function<Double, Double> f, final Function<Double, Double> fPrime) {
        double current = guess;

        while (Math.abs(f.apply(current)) > EPSILON) {
            current = current - f.apply(current) / fPrime.apply(current);
        }

        return current;
    }

    /**
     * Calculates the monthly repayment required using amortized interest
     * @param principal the initial loan amount
     * @param annualInterestRate the annual interest rate in decimal form (i.e. 0.1 = 10%)
     * @param numberOfPaymentPeriods number of repayment periods
     * @return the repayment required to repay capital and interest every month
     */
    public static BigDecimal getMonthlyRepayment(final BigDecimal principal, final BigDecimal annualInterestRate, final int numberOfPaymentPeriods) {
        final BigDecimal monthlyInterestRate = annualInterestRate.divide(new BigDecimal(12), SCALE, HALF_UP);

        // c = (P * r) / (1-(1/(1+r)^n))
        // where:
        // c = monthly repayment
        // P = principal
        // r = monthly interest rate
        // n = number of payment periods

        return principal.multiply(monthlyInterestRate)
                .divide(
                        BigDecimal.ONE.subtract(
                                BigDecimal.ONE.divide(
                                        BigDecimal.ONE.add(monthlyInterestRate).pow(numberOfPaymentPeriods),
                                        SCALE, HALF_UP
                                )
                        ),
                        SCALE, HALF_UP
                );
    }
}
