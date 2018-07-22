package org.flomintv.accounts.money.transfer;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.gson.Gson;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.restlet.RestletOperationException;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.flomintv.accounts.money.transfer.database.AccountDAO;
import org.flomintv.accounts.money.transfer.database.DatabaseConfiguration;
import org.flomintv.accounts.money.transfer.database.TransferDAO;
import org.flomintv.accounts.money.transfer.model.Account;
import org.flomintv.accounts.money.transfer.processors.*;
import org.flomintv.accounts.money.transfer.model.Transfer;
import org.flomintv.accounts.money.transfer.route.TransferRoute;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RestTransferServiceTest extends CamelTestSupport {

    private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    private TransferService transferService = new TransferService();

    private AccountService accountService = new AccountService();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("jsonProvider", new JacksonJsonProvider());
        configureMockTransferService();
        jndi.bind("transferService", transferService);
        jndi.bind("userService", new UserService());
        jndi.bind("accountService", accountService);
        return jndi;
    }

    private void configureMockTransferService() {
        databaseConfiguration = new DatabaseConfiguration();
        DataSource dataSource = databaseConfiguration.init();
        databaseConfiguration.populateTablesWithInitialData();

        TransferDAO transferDAO = new TransferDAO(dataSource);
        AccountDAO accountDAO = new AccountDAO(dataSource);

        transferService.setTransferDAO(transferDAO);
        transferService.setAccountDAO(accountDAO);

        accountService.setAccountDAO(accountDAO);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new TransferRoute();
    }

    @Test
    public void testCreateTransfer() {
        String json = "{\"userIdFrom\":1,\"userIdTo\":2,\"amount\":2500,\"comment\":\"Thanks for the beers, mate!\"}";

        log.info("Sending transfer using json payload: {}", json);

        // Send request to transfer money between accounts
        Map headers = new HashMap();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        String id = template.requestBodyAndHeaders("restlet:http://localhost:8080/transfer?restletMethod=POST", json, headers, String.class);
        assertNotNull(id);

        log.info("Created new transfer with id " + id);

        assertEquals("1", id);

        // Send request to get information about newly created transfer
        String response = template.requestBodyAndHeader("restlet:http://localhost:8080/transfer/1?restletMethod=GET", null, "Accept", "application/json", String.class);
        log.info("Response: {}", response);
        Gson gson = new Gson();
        Transfer transfer = gson.fromJson(response, Transfer.class);
        assertTrue(1L == transfer.getUserIdFrom());
        assertTrue(2L == transfer.getUserIdTo());
        assertTrue(BigDecimal.valueOf(2500).compareTo(transfer.getAmount()) == 0);
        assertEquals("Thanks for the beers, mate!", transfer.getComment());

        // Send request to get first account information after transfer
        response = template.requestBodyAndHeader("restlet:http://localhost:8080/account/1?restletMethod=GET", null, "Accept", "application/json", String.class);
        log.info("Response: {}", response);
        gson = new Gson();
        Account account = gson.fromJson(response, Account.class);
        assertTrue(BigDecimal.valueOf(97500).compareTo(account.getCurrentAmount()) == 0);

        // Send request to get second account information after transfer
        response = template.requestBodyAndHeader("restlet:http://localhost:8080/account/2?restletMethod=GET", null, "Accept", "application/json", String.class);
        log.info("Response: {}", response);
        gson = new Gson();
        account = gson.fromJson(response, Account.class);
        assertTrue(BigDecimal.valueOf(152700.59).compareTo(account.getCurrentAmount()) == 0);

        databaseConfiguration.clearTables();
    }

    @Test
    public void testCreateTransferInsufficientFunds() {
        String json = "{\"userIdFrom\":1,\"userIdTo\":2,\"amount\":250000,\"comment\":\"Thanks for the beers, mate!\"}";

        log.info("Sending transfer using json payload: {}", json);

        // Send request to transfer money between accounts
        Map headers = new HashMap();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        try {
            String id = template.requestBodyAndHeaders("restlet:http://localhost:8080/transfer?restletMethod=POST", json, headers, String.class);
        } catch (CamelExecutionException e) {
            assertEquals(400, ((RestletOperationException) e.getCause()).getStatusCode());
            assertEquals("Sorry, but it seems you don't have enough money on your account", ((RestletOperationException) e.getCause()).getResponseBody());
        }
    }

}
