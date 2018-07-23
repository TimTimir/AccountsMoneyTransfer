package org.flomintv.accounts.money.transfer;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.main.Main;
import org.flomintv.accounts.money.transfer.database.AccountDAO;
import org.flomintv.accounts.money.transfer.database.DatabaseConfiguration;
import org.flomintv.accounts.money.transfer.database.TransferDAO;
import org.flomintv.accounts.money.transfer.database.UserDAO;
import org.flomintv.accounts.money.transfer.processors.*;
import org.flomintv.accounts.money.transfer.route.TransferRoute;

import javax.sql.DataSource;

public class TransferMain {

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        JndiRegistry registry = configureJndiRegistry();
        TransferRoute transferRoute = new TransferRoute();

        DefaultCamelContext context = new DefaultCamelContext(registry);
        context.addRoutes(transferRoute);
        main.getCamelContexts().add(context);

        main.run();
    }

    private static JndiRegistry configureJndiRegistry() {
        JndiRegistry registry = new JndiRegistry();

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        DataSource dataSource = databaseConfiguration.init();
        databaseConfiguration.populateTablesWithInitialData();

        AccountDAO accountDAO = new AccountDAO(dataSource);

        registry.bind("jsonProvider", configureJsonProvider());
        registry.bind("transferService", configureTransferService(dataSource, accountDAO));
        registry.bind("userService", configureUserService(dataSource));
        registry.bind("accountService", configureAccountService(accountDAO));
        return registry;
    }

    private static JacksonJsonProvider configureJsonProvider() {
        return new JacksonJsonProvider();
    }

    private static TransferService configureTransferService(DataSource dataSource, AccountDAO accountDAO) {
        TransferDAO transferDAO = new TransferDAO(dataSource);

        TransferService transferService = new TransferService();
        transferService.setTransferDAO(transferDAO);
        transferService.setAccountDAO(accountDAO);
        return transferService;
    }

    private static UserService configureUserService(DataSource dataSource) {
        UserDAO userDAO = new UserDAO(dataSource);

        UserService userService = new UserService();
        userService.setUserDAO(userDAO);
        return userService;
    }

    private static AccountService configureAccountService(AccountDAO accountDAO) {
        AccountService accountService = new AccountService();
        accountService.setAccountDAO(accountDAO);
        return accountService;
    }

}
