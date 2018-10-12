package com.github.hansonhsc.loan.quote;

import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

/**
 * Bean to represent lenders specified in the input CSV file
 */
public final class Lender {
    /**
     * the name of the lender
     */
    @CsvBindByName(column = "Lender", required = true)
    private String name;

    /**
     * the annual interest rate of any loans by this lender
     */
    @CsvBindByName(column = "Rate", required = true)
    private BigDecimal rate;

    /**
     * the total amount available for loans from this lender in pounds sterling
     */
    @CsvBindByName(column = "Available", required = true)
    private int amount;

    /**
     * Gets the name of the lender
     * @return the name of the lender
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the lender
     * @param name the name of the lender
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the annual interest rate of any loans by this lender
     * @return the annual interest rate of any loans by this lender
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Sets the annual interest rate of any loans by this lender
     * @param rate the annual interest rate of any loans by this lender
     */
    public void setRate(final BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * Gets the total amount available for loans from this lender in pounds sterling
     * @return the total amount available for loans from this lender in pounds sterling
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the total amount available for loans from this lender in pounds sterling
     * @param amount the total amount available for loans from this lender in pounds sterling
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }
}
