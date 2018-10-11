package com.github.hansonhsc.loan;

import java.math.BigDecimal;
import java.util.function.Function;

import static java.math.BigDecimal.ROUND_UP;
import static java.math.RoundingMode.HALF_UP;

public final class AmortizedLoan {
    private static final double GUESSED_INTEREST_RATE = 0.10;
    private static final double EPSILON = 0.00000001;
    public static final int SCALE = 10;

    public static double getEstimatedAnnualInterestRate(final double principal, final int term, final double monthlyPayment) {
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

    private static double newtonRaphsonMethod(final double guess, final Function<Double, Double> f, final Function<Double, Double> fPrime) {
        double current = guess;

        while (Math.abs(f.apply(current)) > EPSILON) {
            current = current - f.apply(current) / fPrime.apply(current);
        }

        return current;
    }

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
                )
                // round up to nearest penny, rounding up to ensure we don't lose fractional pennies every month
                // better for the customer to pay a few pennies too much than us having a shortfall over many lenders
                .setScale(2, ROUND_UP);
    }
}
