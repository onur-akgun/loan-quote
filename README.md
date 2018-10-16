![Loan Quote Calculator](images/console.gif)

[![Build Status](https://travis-ci.com/hansonhsc/loan-quote.svg?branch=master)](https://travis-ci.com/hansonhsc/loan-quote)
[![codecov](https://codecov.io/gh/hansonhsc/loan-quote/branch/master/graph/badge.svg)](https://codecov.io/gh/hansonhsc/loan-quote/branch/master)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Loan Quote Calculator

The loan quote calculator is an example application that demonstrates how to create a rate calculation
system allowing prospective borrowers to obtain a quote from a pool of lenders for 36 month loans.

The system strives to provide as low a rate to the borrower as is possible to ensure that the quotes are as competitive
as they can be against competitors'. The borrower will also be provided with the details of the monthly repayment amount
and the total repayment amount.

## Building the Loan Quote Calculator

Ensure you have Java Development Kit (JDK 8+) installed and `JAVA_HOME` environment variable is set, then:

```bash
$ git clone https://github.com/hansonhsc/loan-quote.git
$ cd loan-quote
$ ./mvnw clean install
```

The Loan Quote Calculator can then be found in `target/loan-quote-[version].jar`.

`loan-quote-[version]-sources.jar` and `loan-quote-[version]-javadoc.jar` can also be found in the `target` directory.

### Windows

Run `mvnw.cmd` instead of `mvnw`:

```bash
$ .\mvnw.cmd clean install
```

## Running the tests

### Unit tests

```bash
$ ./mvnw clean test
```

Note that [payments.csv](src/test/resources/payments.csv) is used in some of the unit tests as a lookup table
of expected values of interest rate, principal and monthly repayments. This CSV file is generated using a LibreOffice
Calc (any spreadsheet application should support this).

The formula used to generate the monthly repayment for the given interest rate and principal is:

```
=ABS(PMT([rate] / 12, 36 , [principal]))
```

See [PMT function documentation](https://wiki.openoffice.org/wiki/Documentation/How_Tos/Calc:_PMT_function) for more
details.

### Integration tests

[LoanQuoteApplicationIT](src/test/java/com/github/hansonhsc/loan/quote/LoanQuoteApplicationIT.java) consists of a few
simple tests that starts the application from the command line and verifying the application output. You can run them
by:

```bash
$ ./mvnw clean verify
```

## Usage

```bash
$ java -jar loan-quote.jar [market_file] [loan_amount]
```

* `market_file` - Path to a CSV file containing a list of all the offers being made by the lenders within the system. See
[market.csv](src/test/resources/market.csv) for exact formatting
* `loan_amount` - Borrowers are able to request a loan of any £100 increment between £1000 and £15000 inclusive. If the
market does not have sufficient offers from lenders to satisfy the loan then the system would inform the borrower that
it is not possible to provide a quote at that time.

### Example

```bash
$ java -jar target/loan-quote-0.1.0-SNAPSHOT.jar src/test/resources/market.csv 1000
Requested amount: £1000
Rate: 7.0%
Monthly repayment: £30.88
Total repayment: £1111.65
```

Repayment amounts are displayed to 2 decimal places and the rate of the loan is displayed to one decimal place.

## How is the quote calculated?

1. The list of lenders is read from the CSV file
0. Based on the requested loan amount, we find the lenders that will provide the lowest rates
0. Once the lenders are found, we calculate how much we need to borrow from each lender, which might not be the full
amount the lender can lend out
0. Once the loan amount for each lender is established, we calculate the monthly repayment for each lender based on
amortized interest, using this
formula from [Wikipedia](https://en.wikipedia.org/wiki/Compound_interest#Exact_formula_for_monthly_payment):
   <p align="center">
   <img src="images/monthly_repayment.gif">
   </p>
   where:

    - ![c](images/c.gif) = monthly payment
    - ![P](images/capital_p.gif) = principal, i.e. loan amount
    - ![r](images/r.gif) = monthly interest rate, i.e. annual interest rate / 12
    - ![n](images/n.gif) = number of payment periods, i.e. 36

0. The monthly repayment displayed is then the sum of monthly repayment to each lender. This number is rounded up/down
to the nearest penny. The rounding behaviour can be modified easily, but it does not matter too much, as long as we are
consistent.
0. The total repayment is the product of total repayment periods (36) and the total unrounded monthly repayment.
     - *Note that the total repayment amount might not be exactly 36 times of the displayed monthly repayment amount. When
issuing the amortized schedule, care needs to be taken in the final month to reconcile any overpayments or underpayments
each month due to extra or missing fractional pennies:*

        `finalMonthPayment = totalPayment - monthlyPayment * 35`
0. The displayed loan rate is then calculated from the unrounded monthly repayment amount, loan amount and number of
payment periods (36).
    - **This is NOT the weighted average of each individual lender's rate.**

### Calculating the loan rate

To calculate the loan rate, there is no readily available formula. However,
[this article](https://blog.bossylobster.com/2012/05/reverse-calculating-interest-rate) does describe a method for
doing this using Newton-Raphson method.

Note that there is a slight mistake in this article when resolving the geometric series. The following
is the algorithm used based on the method described in the article, with the mistake corrected.

#### Identifying the variables

Before we calculate the loan rate, let's establish what values we have and what we are trying to find out:

- loan amount: let's call this the **principal (![P](images/capital_p.gif))**
- number of monthly payments: let's call this the **term (![T](images/capital_t.gif))**
- monthly payment: let's call this the **payment (![R](images/capital_r.gif))**
- annual interest rate: let's call this the **interest rate (![r](images/r.gif))** - this is what we are trying to find out
    - *Note the slight different in terminology here compared to the earlier formula from Wikipedia for calculating the
    monthly repayment, where ![r](images/r.gif) represented the monthly interest rate. Here, ![r](images/r.gif)
    represents the __annual__ interest rate.*

#### What happens every month?

Every month, the borrower pays a recurring amount (![R](images/capital_r.gif)), which includes paying interest and part of the principal still owed.
At the end of the payment term, the principal owed will be 0.

If <sub>![P<sub>i</sub>](images/p_i.gif)</sub> is the principal owed currently after *i*
payments have been made, then:

- <sub>![P<sub>0</sub> = P](images/p_0_p.gif)</sub>, the principal owed before any payments is the initial loan amount or principal
- <sub>![P<sub>T</sub> = 0](images/p_t_0.gif)</sub>, nothing is owed at the end of the term

Due to compound interest, every month the interest is based on the principal owed. So the principal owed the next month
can be calculated by the monthly interest rate. The monthly interest rate can be calculated by dividing the annual interest
rate by 12.

The principal owed next month is then the principal owed this month plus the interest calculated, minus the payment made:

<p align="center">
<img src="images/monthly_principal.gif">
</p>

To simplify things a little bit later, let's introduce a variable, the **monthly multiplier (![m](images/m.gif))**:

<p align="center">
<img src="images/monthly_multiplier.gif">
</p>

This then means:

<p align="center">
<img src="images/monthly_principal_simplified.gif">
</p>

#### How the principal changes month to month

The first 3 payment periods looks like this:

<p align="center">
<img src="images/principal_sequence.gif">
</p>

Looking at how the sequence progresses from <sub>![P<sub>3</sub>](images/p_3.gif)</sub>, we can then define <sub>![P<sub>i</sub>](images/p_i.gif)</sub> as:

<p align="center">
<img src="images/principal_sum_of_m.gif">
</p>

We can calculate the principal owed in a given month by the above formula.

#### Extracting a polynomial using <sub>![P<sub>T</sub>](images/p_t.gif)</sub>

Substituting ![T](images/capital_t.gif) as ![i](images/i.gif) for the above:

<p align="center">
<img src="images/end_principal.gif">
</p>

But recall that <sub>![P<sub>T</sub> = 0](images/p_t_0.gif)</sub>, therefore we have the following polynomial:

<p align="center">
<img src="images/polynomial_with_sum.gif">
</p>

Using the [formula for the sum of the first ![n](images/hyperlink_n.gif) terms of a geometric series](https://en.wikipedia.org/wiki/Geometric_series#Formula):

<p align="center">
<img src="images/geometric_series.gif">
</p>

We can simplify our polynomial:

<p align="center">
<img src="images/simplified_polynomial.gif">
</p>

#### Solving the polynomial to find the interest rate

To solve the polynomial, we can use the [Newton-Raphson method](https://en.wikipedia.org/wiki/Newton%27s_method), where
given a function:

<p align="center">
<img src="images/function.gif">
</p>

It can find successively better approximations of ![x](images/x.gif) using the function's derivative ![f'](images/f_prime.gif):

<p align="center">
<img src="images/newton_raphson_1.gif">
</p>

As the process is repeated:

<p align="center">
<img src="images/newton_raphson_n.gif">
</p>

Using the polynomial that we got earlier, we can find its derivative:

<p align="center">
<img src="images/derivative.gif">
</p>

Then we can use Newton-Raphson to find ![m](images/m.gif) and ultimately the annual interest rate by the following pseudo-code:

```
guess = 0.10 // start by guessing the interest rate
epsilon = 0.00000001
m = 1 + guess / 12

while (abs(f(m)) > epsilon) {
    m = m - f(m) / f'(m)
}

monthlyInterestRate = m - 1
annualInterestRate = 12 * monthlyInterestRate
```

## Assumptions made

* all rates specified in CSV are annual interest rates as decimal, i.e. 0.10 = 10%
* the displayed rate in the output is annual interest rate for the entire loan amount
* any input CSV has the appropriate headers
* the column *Available* in the CSV specifies the amount available for loan in pounds sterling (£)
* all loans are amortized
* interest is compounded monthly
* it is possible to borrow only part of the amount available from a particular lender
* lenders expect monthly repayment of capital and interest
* lenders are not recycling their monthly repayments - if they are, we can potentially use low rate lenders to
pay off high rate lenders to reduce the interest every month
* it is possible to reconcile fractional pennies that were overpaid or underpaid each month in the final month
* we are not charging any fees on top of paying back lenders (quite unrealistic)

Credits
-------
- [Compound interest monthly repayment](https://en.wikipedia.org/wiki/Compound_interest#Exact_formula_for_monthly_payment)
- [Geometric series](https://en.wikipedia.org/wiki/Geometric_progression#Geometric_series)
- [Using Newton-Raphson method to find the interest rate](https://blog.bossylobster.com/2012/05/reverse-calculating-interest-rate)
- [Newton-Raphson method](https://en.wikipedia.org/wiki/Newton%27s_method)
- [Interest rate calculator](https://financialmentor.com/calculator/interest-rate-calculator)
- [Online LaTeX editor](https://www.codecogs.com/latex/eqneditor.php)
- [Convert terminal recordings to animated gifs](https://github.com/icholy/ttygif)
- [Gif editor](https://ezgif.com/)
- [Maven Wrapper](https://github.com/takari/maven-wrapper)

License
-------
MIT License - https://opensource.org/licenses/MIT
