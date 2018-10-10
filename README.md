[![Build Status](https://travis-ci.com/hansonhsc/loan-quote.svg?branch=master)](https://travis-ci.com/hansonhsc/loan-quote)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Loan Quote Calculator

The loan quote calculator is an example application that demonstrates how to create a rate calculation
system allowing prospective borrowers to obtain a quote from a pool of lenders for 36 month loans.

The system strives to provide as low a rate to the borrower as is possible to ensure that the quotes are as competitive
as they can be against competitors'. The borrower will also be provided with the details of the monthly repayment amount
and the total repayment amount.

## Usage

```bash
> java -jar loan-quote.jar [market_file] [loan_amount]
```

* `market_file` - Path to a CSV file containing a list of all the offers being made by the lenders within the system. See
`market.csv` for exact formatting
* `loan_amount` - Borrowers are able to request a loan of any £100 increment between £1000 and £15000 inclusive. If the
market does not have sufficient offers from lenders to satisfy the loan then the system would inform the borrower that
it is not possible to provide a quote at that time.

### Example

```bash
> java -jar loan-quote.jar market.csv 1500
Requested amount: £1000
Rate: 7.0%
Monthly repayment: £30.78
Total repayment: £1108.10
```

Repayment amounts are displayed to 2 decimal places and the rate of the loan is displayed to one decimal place.

License
-------
MIT License - https://opensource.org/licenses/MIT
