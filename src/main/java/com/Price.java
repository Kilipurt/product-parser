package com;

public class Price {

    private int withTax;
    private int withoutTax;
    private String currency;

    public int getWithTax() {
        return withTax;
    }

    public void setWithTax(int withTax) {
        this.withTax = withTax;
    }

    public int getWithoutTax() {
        return withoutTax;
    }

    public void setWithoutTax(int withoutTax) {
        this.withoutTax = withoutTax;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
