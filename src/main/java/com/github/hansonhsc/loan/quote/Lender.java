package com.github.hansonhsc.loan.quote;

import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

public class Lender {
    @CsvBindByName(column = "Lender", required = true)
    private String name;

    @CsvBindByName(column = "Rate", required = true)
    private BigDecimal rate;

    @CsvBindByName(column = "Available", required = true)
    private int amount;

    public String getName() {
        return name;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public int getAmount() {
        return amount;
    }
}
