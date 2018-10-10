package com.github.hansonhsc.loan.quote;

import java.util.function.Function;

public final class AmortizedLoan {
    public static double getAnnualInterestRate(final double principal, final int term, final double monthlyPayment) {
        final Function<Double, Double> f = generateF(principal, term, monthlyPayment);
        final Function<Double, Double> fPrime = generateFPrime(principal, term, monthlyPayment);

        final double guessM = m(0.10);

        return mInverse(newtonRaphsonMethod(guessM, f, fPrime));
    }

    private static Function<Double, Double> generateF(final double principal, final int term, final double monthlyPayment) {
        return m -> principal * Math.pow(m, term + 1) - (principal + monthlyPayment) * Math.pow(m, term) + monthlyPayment;
    }

    private static Function<Double, Double> generateFPrime(final double principal, final int term, final double monthlyPayment) {
        return m -> principal * (term + 1) * Math.pow(m, term) - (principal + monthlyPayment) * term * Math.pow(m, term - 1);
    }

    private static double m(final double r) {
        return 1 + r / 12.0;
    }

    private static double mInverse(final double mValue) {
        return 12.0 * (mValue - 1);
    }

    private static double newtonRaphsonMethod(final double guess, final Function<Double, Double> f, final Function<Double, Double> fPrime) {
        double current = guess;

        while (Math.abs(f.apply(current)) > 0.00000001) {
            current = current - f.apply(current) * 1.0 / fPrime.apply(current);
        }

        return current;
    }
}
