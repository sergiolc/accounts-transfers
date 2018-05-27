package com.slcorrea.accounts.persistence;

import com.slcorrea.accounts.models.Account;

import java.util.*;

public class AccountDAO {

    private static Map<UUID, Account> accounts = new HashMap<UUID, Account>();


    public Account getById(UUID id) {
        return accounts.get(id);
    }

    public List<Account> getAll() {
        List<Account> result = new ArrayList<Account>();
        for (UUID key : accounts.keySet()) {
            result.add(accounts.get(key));
        }

        return result;
    }

    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

}
