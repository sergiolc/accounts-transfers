package com.slcorrea.accounts;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.slcorrea.accounts.models.Account;
import com.slcorrea.accounts.models.Transaction;
import com.slcorrea.accounts.models.Transfer;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(ConcurrentTestRunner.class)
public class ConcurrentTransferTest {

    @ClassRule
    public static final DropwizardAppRule<AppConfig> appRule = new DropwizardAppRule<>(App.class);

    private static String apiUrl;
    private static Client client;
    private static Account accountA;
    private static Account accountB;
    private static int transactionsCompleted = 0;
    private static Logger logger = Logger.getLogger(ConcurrentTransferTest.class.getName());


    /*
    Account A starts with a balance of 90 EUR and Account B with 0 EUR
    After performing 50 transfers concurrently, only 3 are completed successfully
    At the end of the test it prints Accounts A and B balances
     */
    @BeforeClass
    public static void setUp() {

        apiUrl = String.format("http://localhost:%d", appRule.getLocalPort());
        client = new JerseyClientBuilder(appRule.getEnvironment())
                .build("Accounts API");

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

        url = String.format("%s/accounts/%s/deposit", apiUrl, accountA.getId());
        Form form = new Form();
        form.param("amount", "90");

        response = client.target(url).request().post(Entity.form(form));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(Account.class).getBalance()).isEqualTo(new BigDecimal(90));


        System.out.println("");
    }

    @Test
    @ThreadCount(50)
    public void successfulTransferTest() {

        String url = String.format("%s/transactions/transfer", apiUrl);

        Transfer newTransfer = new Transfer();
        newTransfer.setRequestId(UUID.randomUUID().toString());
        newTransfer.setCurrency("EUR");
        newTransfer.setAmount(new BigDecimal(25));
        newTransfer.setReference("Transfer from Account A to Account B");
        newTransfer.setSourceAccountId(accountA.getId());
        newTransfer.setTargetAccountId(accountB.getId());

        Entity<Transfer> entity = Entity.entity(newTransfer, MediaType.APPLICATION_JSON_TYPE);
        Response response = client.target(url).request().post(entity);

        assertThat(response.getStatus()).isEqualTo(200);

        Transaction transaction = response.readEntity(Transaction.class);

        if (transaction.getStatus().equals(Transaction.TransactionStatus.Completed)) {
            transactionsCompleted++;
        }
        logger.info("Status: " + transaction.getStatus());
        assertThat(transactionsCompleted).isLessThan(4);
    }

    @AfterClass
    public static void checkBalance() {

        String url = String.format("%s/accounts/%s", apiUrl, accountA.getId());
        Response response = client.target(url).request().get();

        System.out.println("");
        logger.info("***** Final Balance - Account A: " + response.readEntity(Account.class).getBalance() + " *****\n");

        url = String.format("%s/accounts/%s", apiUrl, accountB.getId());
        response = client.target(url).request().get();
        logger.info("***** Final Balance - Account B: " + response.readEntity(Account.class).getBalance() + " *****\n");
    }

}
