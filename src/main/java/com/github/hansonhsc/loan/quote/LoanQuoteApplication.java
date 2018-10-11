package com.github.hansonhsc.loan.quote;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class LoanQuoteApplication {
    private final static int MIN_LOAN_AMOUNT = 1000;
    private final static int MAX_LOAN_AMOUNT = 15000;
    private final static int LOAN_AMOUNT_INCREMENT = 100;

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
            printError(e.getMessage());

            return;
        }

        // display the quote to the user
        printQuote(quote);
    }

    private static void printQuote(final LoanQuote quote) {
        print("Requested amount: £" + quote.getLoanAmount());
        print("Rate: " + quote.getRate() + "%");
        print("Monthly repayment: £" + quote.getMonthlyRepayment());
        print("Total repayment: £" + quote.getTotalRepayment());
    }

    private static void printError(final String message) {
        print(message);
        printUsage();
    }

    private static void printUsage() {
        print("Usage: java -jar [loan_quote_jar_file] [market_file] [loan_amount]");
    }

    // isolate usage of System.out.println, so if the application ever needs logging, we can integrate it here
    private static void print(final String message) {
        System.out.println(message);
    }
}
