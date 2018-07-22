package org.flomintv.accounts.money.transfer;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.gson.Gson;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.flomintv.accounts.money.transfer.database.DatabaseConfiguration;
import org.flomintv.accounts.money.transfer.database.UserDAO;
import org.flomintv.accounts.money.transfer.model.User;
import org.flomintv.accounts.money.transfer.processors.*;
import org.flomintv.accounts.money.transfer.route.TransferRoute;
import org.junit.Test;

import javax.sql.DataSource;

public class RestUserServiceTest extends CamelTestSupport {

    private UserService userService = new UserService();
    private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("jsonProvider", new JacksonJsonProvider());
        configureMockTransferService();
        jndi.bind("transferService", new TransferService());
        jndi.bind("userService", userService);
        jndi.bind("accountService", new AccountService());
        return jndi;
    }

    private void configureMockTransferService() {
        databaseConfiguration = new DatabaseConfiguration();
        DataSource dataSource = databaseConfiguration.init();
        databaseConfiguration.populateTablesWithInitialData();

        UserDAO userDAO = new UserDAO(dataSource);

        userService.setUserDAO(userDAO);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new TransferRoute();
    }

    @Test
    public void testGetUserJson() {
        String response = template.requestBodyAndHeader("restlet:http://localhost:8080/user/1?restletMethod=GET", null, "Accept", "application/json", String.class);
        log.info("Response: {}", response);
        Gson gson = new Gson();
        User user = gson.fromJson(response, User.class);
        assertEquals("Ivan", user.getFirstName());
        assertEquals("Ivanovich", user.getMiddleName());
        assertEquals("Ivanov", user.getLastName());
    }

}
