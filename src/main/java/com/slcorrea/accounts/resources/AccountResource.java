package com.slcorrea.accounts.resources;

import com.google.inject.Inject;
import com.slcorrea.accounts.models.Account;
import com.slcorrea.accounts.persistence.AccountDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountDAO accountDAO;

    @Inject
    public AccountResource(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @GET
    public List<Account> list() {
        return accountDAO.getAll();
    }

    @POST
    public Account create(Account account) {
        Account newAccount = new Account();
        newAccount.setName(account.getName());
        newAccount.setCurrency(account.getCurrency());

        return accountDAO.save(newAccount);
    }

    @GET
    @Path("/{id}")
    public Account get(@PathParam("id") UUID id) {
        return accountDAO.getById(id);
    }

    @POST
    @Path("/{id}/deposit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Account deposit(@PathParam("id") UUID id, @FormParam("amount") BigDecimal amount) {
        Account account = accountDAO.getById(id);
        account.deposit(amount);

        return accountDAO.save(account);
    }

}
