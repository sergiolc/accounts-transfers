package com.slcorrea.accounts.models;

import com.slcorrea.accounts.exceptions.AccountBalanceException;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private UUID id;
    private String name;
    private String currency;
    private BigDecimal balance;

    public Account() {
        this.id = UUID.randomUUID();
        this.balance = new BigDecimal(0);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) throws AccountBalanceException {
        if (this.balance.subtract(amount).doubleValue() > 0) {
            this.balance = this.balance.subtract(amount);
        } else {
            throw new AccountBalanceException();
        }
    }


}
