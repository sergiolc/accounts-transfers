package com.slcorrea.accounts.exceptions;


public class AccountBalanceException extends Exception {

    public AccountBalanceException() {
        super("Invalid balance");
    }
}
