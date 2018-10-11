package com.github.hansonhsc.loan.quote;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

/**
 * A command line application that reads a CSV file of lenders and provides a quote for the requested loan amount
 * using the lenders with the lowest rates
 */
public class LoanQuoteApplication {
    /**
     * Minimum loan amount in pounds sterling that is allowed to be requested by the user
     */
    private final static int MIN_LOAN_AMOUNT = 1000;

    /**
     * Maximum loan amount in pounds sterling that is allowed to be requested by the user
     */
    private final static int MAX_LOAN_AMOUNT = 15000;

    /**
     * Loan amount increments that the user is allowed to request in
     */
    private final static int LOAN_AMOUNT_INCREMENT = 100;

    /**
     * Entry point for the application
     * @param args array of strings representing the user input. Must be of length 2, where <code>args[0]</code>
     *             is a file path to the input CSV file containing lender information; and <code>arges[1]</code>
     *             is an integer specifying the loan amount that is between <code>MIN_LOAN_AMOUNT</code> and
     *             <code>MAX_LOAN_AMOUNT</code> inclusive and in increments of <code>LOAN_AMOUNT_INCREMENT</code>.
     */
    public static void main(final String[] args) {
        // validate number of arguments
        if (args.length != 2) {
            printError("Invalid number of arguments: " + args.length + ". Expected: 2");

            return;
        }

        // read the arguments as string
        final String marketFilePath = args[0];
        final String loanAmountAsString = args[1];

        // first argument is market.csv, ensure that it is a file
        final FileReader marketFileReader;

        try {
            marketFileReader = new FileReader(marketFilePath);
        } catch (FileNotFoundException e) {
            printError("Invalid market file: " + marketFilePath);

            return;
        }

        // parse the market.csv

        //noinspection unchecked
        final List<Lender> lenders = new CsvToBeanBuilder(marketFileReader)
                .withType(Lender.class)
                .build()
                .parse();

        // TODO: catch malformed CSV

        // create a loan quote calculator that can get as many quotes as we like
        final LoanQuoteCalculator loanQuoteCalculator = new LoanQuoteCalculator(lenders);

        // validate loan amount
        final int loanAmount;

        try {
            loanAmount = Integer.parseInt(loanAmountAsString);
        } catch (NumberFormatException e) {
            printError("Invalid loan amount format, must be a number: " + loanAmountAsString);

            return;
        }

        if (loanAmount < MIN_LOAN_AMOUNT || MAX_LOAN_AMOUNT < loanAmount || loanAmount % LOAN_AMOUNT_INCREMENT != 0) {
            printError("Invalid loan amount, must be any 100 increment between 1000-15000 inclusive: " + loanAmount);

            return;
        }

        // get the single quote we want
        final LoanQuote quote;

        try {
            quote = loanQuoteCalculator.getQuote(loanAmount);
        } catch (InsufficientLendersException e) {
            printError("Insufficient offers from lenders to satisfy the loan. Try a smaller loan amount.");

            return;
        }

        // display the quote to the user
        printQuote(quote);
    }

    /**
     * Prints the quote information to the standard output
     * @param quote the <code>LoanQuote</code> object containing the quote information
     */
    private static void printQuote(final LoanQuote quote) {
        print("Requested amount: £" + quote.getLoanAmount());
        print("Rate: " + quote.getRate() + "%");
        print("Monthly repayment: £" + quote.getMonthlyRepayment());
        print("Total repayment: £" + quote.getTotalRepayment());
    }

    /**
     * Prints the specified error message to standard output as well as application usage information
     * @param message the error message to print to standard output
     */
    private static void printError(final String message) {
        print(message);
        printUsage();
    }

    /**
     * Prints a single line to the standard output to explain how to use the application
     */
    private static void printUsage() {
        print("Usage: java -jar [loan_quote_jar_file] [market_file] [loan_amount]");
    }

    /**
     * Prints the specified message to standard output. Isolated here, so that if the application ever needs logging,
     * we can integrate it here
     * @param message the message to print to standard output
     */
    private static void print(final String message) {
        System.out.println(message);
    }
}
