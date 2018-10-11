package com.github.hansonhsc.loan.quote;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class LoanQuoteApplication {
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
        final File marketFile = new File(marketFilePath);

        if (!marketFile.isFile()) {
            printError("Invalid market file: " + marketFilePath);

            return;
        }

        // parse the market.csv
        final List<Lender> lenders;

        try {
            //noinspection unchecked
            lenders = new CsvToBeanBuilder(new FileReader(marketFile))
                    .withType(Lender.class)
                    .build()
                    .parse();
        } catch (FileNotFoundException e) {
            printError("Invalid market file: " + marketFilePath);

            return;
        }

        // create a loan quote calculator that can get as many quotes as we like
        final LoanQuoteCalculator loanQuoteCalculator = new LoanQuoteCalculator(lenders);

        // TODO: validate loanAmountAsString and loanAmount min max interval
        final int loanAmount = Integer.parseInt(loanAmountAsString);

        // get the single quote we want
        final LoanQuote quote = loanQuoteCalculator.getQuote(loanAmount);

        // display the quote to the user
        printQuote(quote);
    }

    private static void printQuote(final LoanQuote quote) {
        print("Requested amount: £" + quote.getLoanAmount());
        print("Rate: " + quote.getRate().setScale(1, ROUND_HALF_UP) + "%");
        print("Monthly repayment: £" + quote.getMonthlyRepayment().setScale(2, ROUND_HALF_UP));
        print("Total repayment: £" + quote.getTotalRepayment().setScale(2, ROUND_HALF_UP));
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