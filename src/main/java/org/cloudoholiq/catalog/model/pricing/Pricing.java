package org.cloudoholiq.catalog.model.pricing;

import java.math.BigDecimal;

public class Pricing {
    private BigDecimal price;
    private CurrencyType currencyType;
    private RecurrencePeriodType recurrencePeriodType;
    public Pricing() {
    }

    public Pricing(BigDecimal price, CurrencyType currencyType, RecurrencePeriodType recurrencePeriodType) {
        this.price = price;
        this.currencyType = currencyType;
        this.recurrencePeriodType = recurrencePeriodType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public RecurrencePeriodType getRecurrencePeriodType() {
        return recurrencePeriodType;
    }

    public void setRecurrencePeriodType(RecurrencePeriodType recurrencePeriodType) {
        this.recurrencePeriodType = recurrencePeriodType;
    }
}
