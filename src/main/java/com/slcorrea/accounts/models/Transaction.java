package com.slcorrea.accounts.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    public enum TransactionType {
        Transfer ("transfer"),
        Exchange ("exchange"),
        Refund ("refund");

        private String value;

        public String getValue() {
            return value;
        }

        TransactionType(String value) {
            this.value = value;
        }
    }

    public enum TransactionStatus {
        Pending ("pending"),
        Completed ("completed"),
        Declined ("declined");

        private String value;

        public String getValue() {
            return value;
        }

        TransactionStatus(String value) {
            this.value = value;
        }
    }


    private UUID id;
    private TransactionType type;
    private String requestId;
    private TransactionStatus status;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private String currency;
    private String reference;

    public Transaction() {
        this.id = UUID.randomUUID();
        this.status = TransactionStatus.Pending;
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public synchronized TransactionStatus getStatus() {
        return status;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(UUID targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public synchronized void completed() {
        this.status = TransactionStatus.Completed;
    }

    public synchronized void declined() {
        this.status = TransactionStatus.Declined;
    }

}
