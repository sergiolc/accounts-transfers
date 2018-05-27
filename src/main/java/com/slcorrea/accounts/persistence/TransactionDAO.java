package com.slcorrea.accounts.persistence;

import com.slcorrea.accounts.models.Transaction;

import java.util.*;

public class TransactionDAO {

    private static Map<UUID, Transaction> transactions = new HashMap<UUID, Transaction>();


    public Transaction getById(UUID id) {
        return transactions.get(id);
    }

    public Transaction getByRequestId(String requestId) {

        for (Transaction transaction : transactions.values()) {
            if (transaction.getRequestId().equals(requestId)) {
                return transaction;
            }
        }

        return null;
    }

    public List<Transaction> getAll() {
        List<Transaction> result = new ArrayList<Transaction>();

        for (UUID key : transactions.keySet()) {
            result.add(transactions.get(key));
        }

        return result;
    }

    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

}
