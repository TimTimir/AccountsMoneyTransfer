package org.flomintv.accounts.money.transfer;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.gson.Gson;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.flomintv.accounts.money.transfer.database.AccountDAO;
import org.flomintv.accounts.money.transfer.database.DatabaseConfiguration;
import org.flomintv.accounts.money.transfer.model.Account;
import org.flomintv.accounts.money.transfer.processors.*;
import org.flomintv.accounts.money.transfer.route.TransferRoute;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;

public class RestAccountServiceTest extends CamelTestSupport {

    private AccountService accountService = new AccountService();
    private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("jsonProvider", new JacksonJsonProvider());
        configureMockTransferService();
        jndi.bind("transferService", new TransferService());
        jndi.bind("userService", new UserService());
        jndi.bind("accountService", accountService);
        return jndi;
    }

    private void configureMockTransferService() {
        databaseConfiguration = new DatabaseConfiguration();
        DataSource dataSource = databaseConfiguration.init();
        databaseConfiguration.populateTablesWithInitialData();

        AccountDAO accountDAO = new AccountDAO(dataSource);

        accountService.setAccountDAO(accountDAO);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new TransferRoute();
    }

    @Test
    public void testGetAccountJson() {
        String response = template.requestBodyAndHeader("restlet:http://localhost:8080/account/1?restletMethod=GET", null, "Accept", "application/json", String.class);
        log.info("Response: {}", response);
        Gson gson = new Gson();
        Account account = gson.fromJson(response, Account.class);
        assertTrue(BigDecimal.valueOf(100000.00).compareTo(account.getCurrentAmount()) == 0);
    }

}
