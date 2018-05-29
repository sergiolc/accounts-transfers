package com.slcorrea.accounts.resources;

import com.google.inject.Inject;
import com.slcorrea.accounts.exceptions.AccountBalanceException;
import com.slcorrea.accounts.models.Account;
import com.slcorrea.accounts.models.Transaction;
import com.slcorrea.accounts.models.Transfer;
import com.slcorrea.accounts.persistence.AccountDAO;
import com.slcorrea.accounts.persistence.TransactionDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;

    @Inject
    public TransactionResource(TransactionDAO transactionDAO, AccountDAO accountDAO) {
        this.transactionDAO = transactionDAO;
        this.accountDAO = accountDAO;
    }

    @GET
    public List<Transaction> list() {
        return transactionDAO.getAll();
    }


    @GET
    @Path("/{id}")
    public Transaction get(@PathParam("id") UUID id) {
        return transactionDAO.getById(id);
    }


    @POST
    @Path("/transfer")
    public Response transfer(Transfer transfer) {

        Account sourceAccount = accountDAO.getById(transfer.getSourceAccountId());
        Account targetAccount = accountDAO.getById(transfer.getTargetAccountId());

        Transaction transaction = transactionDAO.getByRequestId(transfer.getRequestId());

        if (transaction != null) {

            if (transaction.getStatus().equals(Transaction.TransactionStatus.Completed)) {
                return Response.status(Response.Status.CONFLICT).entity(transaction).build();
            }

        } else {

            if (!sourceAccount.getCurrency().equals(transfer.getCurrency()) || !targetAccount.getCurrency().equals(transfer.getCurrency())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            Transaction newTransaction = new Transaction();
            newTransaction.setRequestId(transfer.getRequestId());
            newTransaction.setType(Transaction.TransactionType.Transfer);
            newTransaction.setSourceAccountId(transfer.getSourceAccountId());
            newTransaction.setTargetAccountId(transfer.getTargetAccountId());
            newTransaction.setAmount(transfer.getAmount());
            newTransaction.setCurrency(transfer.getCurrency());
            newTransaction.setReference(transfer.getReference());

            transaction = transactionDAO.save(newTransaction);
        }


        try {
            sourceAccount.withdraw(transaction.getAmount());
            targetAccount.deposit(transaction.getAmount());
            transaction.completed();
        } catch (AccountBalanceException e) {
            transaction.declined();
            System.out.println(e.getMessage() + "-" + sourceAccount.getBalance());
        }

        return Response.status(Response.Status.OK).entity(transaction).build();
    }

}
