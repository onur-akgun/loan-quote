package com.github.hansonhsc.loan.quote;

import java.util.function.Function;

public final class AmortizedLoan {
    private static final double GUESSED_INTEREST_RATE = 0.10;
    public static final double EPSILON = 0.00000001;

    public static double getEstimatedAnnualInterestRate(final double principal, final int term, final double monthlyPayment) {
        final double guessedMonthlyInterestRate = GUESSED_INTEREST_RATE / 12.0;

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
        return 12.0 * estimatedMonthlyInterestRate;
    }

    private static double newtonRaphsonMethod(final double guess, final Function<Double, Double> f, final Function<Double, Double> fPrime) {
        double current = guess;

        while (Math.abs(f.apply(current)) > EPSILON) {
            current = current - f.apply(current) / fPrime.apply(current);
        }

        return current;
    }
}
