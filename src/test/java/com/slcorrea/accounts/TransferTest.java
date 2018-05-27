package com.slcorrea.accounts;

import com.slcorrea.accounts.models.Account;
import com.slcorrea.accounts.models.Transaction;
import com.slcorrea.accounts.models.Transfer;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferTest {

    @ClassRule
    public static final DropwizardAppRule<AppConfig> appRule = new DropwizardAppRule<>(App.class);

    private static String apiUrl;
    private static Client client;
    private static Account accountA;
    private static Account accountB;
    private static Account accountC;
    private static Logger logger = Logger.getLogger(TransferTest.class.getName());

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            super.starting(description);
            logger.info("***** Starting test: " + description.getMethodName() + " *****");
        }

        @Override
        protected void finished(Description description) {
            super.finished(description);
            logger.info("***** Test run: " + description.getMethodName() + "*****\n");
        }
    };

    @BeforeClass
    public static void setUp() {

        apiUrl = String.format("http://localhost:%d", appRule.getLocalPort());
        client = new JerseyClientBuilder(appRule.getEnvironment()).build("Accounts API");

        String url = String.format("%s/accounts", apiUrl);

        Account newAccount = new Account();
        newAccount.setName("Account A");
        newAccount.setCurrency("EUR");

        Entity<Account> entity = Entity.entity(newAccount, MediaType.APPLICATION_JSON_TYPE);
        Response response = client.target(url).request().post(entity);
        accountA = response.readEntity(Account.class);


        newAccount = new Account();
        newAccount.setName("Account B");
        newAccount.setCurrency("EUR");

        entity = Entity.entity(newAccount, MediaType.APPLICATION_JSON_TYPE);
        response = client.target(url).request().post(entity);
        accountB = response.readEntity(Account.class);


        newAccount = new Account();
        newAccount.setName("Account C");
        newAccount.setCurrency("EUR");

        entity = Entity.entity(newAccount, MediaType.APPLICATION_JSON_TYPE);
        response = client.target(url).request().post(entity);
        accountC = response.readEntity(Account.class);

        System.out.println("");
    }


    @Test
    public void listAccountsTest() {
        String url = String.format("%s/accounts", apiUrl);
        Response response = client.target(url).request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(new GenericType<List<Account>>() {}).size()).isEqualTo(3);
    }


    @Test
    public void declinedTransferTest() {
        String url = String.format("%s/transactions/transfer", apiUrl);

        Transfer newTransfer = new Transfer();
        newTransfer.setRequestId("request-001");
        newTransfer.setCurrency("EUR");
        newTransfer.setAmount(new BigDecimal(25));
        newTransfer.setReference("Transfer from Account A to Account C");
        newTransfer.setSourceAccountId(accountA.getId());
        newTransfer.setTargetAccountId(accountC.getId());

        Entity<Transfer> entity = Entity.entity(newTransfer, MediaType.APPLICATION_JSON_TYPE);
        Response response = client.target(url).request().post(entity);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(Transaction.class).getStatus()).isEqualTo(Transaction.TransactionStatus.Declined);
    }


    @Test
    public void successfulTransferTest() {

        String url = String.format("%s/accounts/%s/deposit", apiUrl, accountB.getId());

        Form form = new Form();
        form.param("amount", "100");

        Response response = client.target(url).request().post(Entity.form(form));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(Account.class).getBalance()).isEqualTo(new BigDecimal(100));


        url = String.format("%s/transactions/transfer", apiUrl);

        Transfer newTransfer = new Transfer();
        newTransfer.setRequestId("request-002");
        newTransfer.setCurrency("EUR");
        newTransfer.setAmount(new BigDecimal(25));
        newTransfer.setReference("Transfer from Account B to Account C");
        newTransfer.setSourceAccountId(accountB.getId());
        newTransfer.setTargetAccountId(accountC.getId());

        Entity<Transfer> entity = Entity.entity(newTransfer, MediaType.APPLICATION_JSON_TYPE);
        response = client.target(url).request().post(entity);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(Transaction.class).getStatus()).isEqualTo(Transaction.TransactionStatus.Completed);
    }

}
