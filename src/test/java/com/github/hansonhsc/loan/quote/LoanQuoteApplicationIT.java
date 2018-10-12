package com.github.hansonhsc.loan.quote;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoanQuoteApplicationIT {
    public static final String MARKET_CSV = "src/test/resources/market.csv";

    private static File findJarFile() {
        final File[] files = new File("target").listFiles(
                file -> file.getName().startsWith("loan-quote-")
                        && file.getName().endsWith(".jar")
                        && !file.getName().endsWith("-sources.jar")
        );

        assertNotNull(files, "Jar file found");
        assertEquals(1, files.length, "There's only 1 jar file");

        final File result = files[0];

        assertTrue(result.isFile(), result.getAbsolutePath() + " is a valid file");

        return result;
    }

    private static List<String> runApplication(final String... args) {
        try {
            final List<String> command = new ArrayList<>(Arrays.asList(
                    "java",
                    "-jar",
                    "target/" + findJarFile().getName()
            ));

            command.addAll(Arrays.asList(args));

            final Process process = Runtime.getRuntime().exec(command.toArray(new String[]{}));

            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            final List<String> output = new ArrayList<>();

            String line;

            while ((line = stdInput.readLine()) != null) {
                output.add(line);
            }

            final List<String> error = new ArrayList<>();

            while ((line = stdError.readLine()) != null) {
                error.add(line);
            }

            process.waitFor();

            assertEquals(Collections.emptyList(), error, "Error stream is empty");

            assertEquals(0, process.exitValue(), "Process exited with 0");

            return output;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertApplicationOutput(final int requestedAmount, final String rate, final String monthlyRepayment, final String totalRepayment) {
        final List<String> output = runApplication(MARKET_CSV, Integer.toString(requestedAmount));

        assertEquals(
                Arrays.asList(
                        "Requested amount: £" + requestedAmount,
                        "Rate: " + rate + "%",
                        "Monthly repayment: £" + monthlyRepayment,
                        "Total repayment: £" + totalRepayment
                ),
                output,
                "Output has expected lines"
        );
    }

    private void assertErrorMessage(final String errorMessage, final String... args) {
        final List<String> output = runApplication(args);

        assertEquals(
                Arrays.asList(errorMessage, "Usage: java -jar [loan_quote_jar_file] [market_file] [loan_amount]"),
                output,
                "Running application with no args should give error message"
        );
    }

    @Test
    void testNoArgs() {
        assertErrorMessage("Invalid number of arguments: 0. Expected: 2");
    }

    @Test
    void test1Arg() {
        assertErrorMessage("Invalid number of arguments: 1. Expected: 2", MARKET_CSV);
    }

    @Test
    void testInvalidCsvPath() {
        assertErrorMessage("Invalid market file: invalid", "invalid", "1000");
    }

    @Test
    void testInvalidCsvFile() {
        assertErrorMessage("Unable to parse invalid market file: Error capturing CSV header!", "README.md", "1000");
    }

    @Test
    void test1000Amount() {
        assertApplicationOutput(1000, "7.0", "30.88", "1111.65");
    }

    // test amount as string
    // test negative amount
    // test 0 amount
    // test 999 amount
    // test 1000 amount
    // test 1050 amount
    // test 15000 amount
}
