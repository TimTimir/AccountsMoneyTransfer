package org.flomintv.accounts.money.transfer.database;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

import static org.flomintv.accounts.money.transfer.database.DBUtils.*;

public class DatabaseConfiguration {

    Logger log = Logger.getLogger(DatabaseConfiguration.class);

    public static final BigDecimal INITIAL_FIRST_ACCOUNT_AMOUNT = BigDecimal.valueOf(100000.00);
    public static final BigDecimal INITIAL_SECOND_ACCOUNT_AMOUNT = BigDecimal.valueOf(150200.59);

    private DataSource dataSource;

    public DataSource init() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("org.apache.derby.jdbc.EmbeddedDriver");
        dataSource.setUrl("jdbc:derby:memory:transfer;create=true");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        initDB(dataSource);
        this.dataSource = dataSource;
        return dataSource;
    }

    private void initDB(PooledDataSource dataSource) {
        createTables(dataSource);
        checkInitialData(dataSource);
    }

    private void createTables(DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.execute("create table accounts(userId int, currentAmount decimal(10,2))");
            statement.execute("create table users(userId int, firstName varchar(50), middleName varchar(50), lastName varchar(50))");
            statement.execute("create table transfers(transferId int, userIdFrom int, userIdTo int, amount decimal(10,2), comment varchar(100))");
            connection.commit();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    public void populateTablesWithInitialData() {
        Connection connection = null;
        PreparedStatement psInsert = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);

            psInsert = populateAccountsTableWithInitialData(connection);
            psInsert = populateUsersTableWithInitialData(connection);
            connection.commit();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeStatement(psInsert);
            closeConnection(connection);
        }
    }

    private PreparedStatement populateAccountsTableWithInitialData(Connection connection) throws SQLException {
        PreparedStatement psInsert = connection.prepareStatement("insert into accounts values (?, ?)");

        psInsert.setInt(1, 1);
        psInsert.setBigDecimal(2, INITIAL_FIRST_ACCOUNT_AMOUNT);
        psInsert.executeUpdate();

        psInsert.setInt(1, 2);
        psInsert.setBigDecimal(2, INITIAL_SECOND_ACCOUNT_AMOUNT);
        psInsert.executeUpdate();

        return psInsert;
    }

    private PreparedStatement populateUsersTableWithInitialData(Connection connection) throws SQLException {
        PreparedStatement psInsert = connection.prepareStatement("insert into users values (?, ?, ?, ?)");

        psInsert.setInt(1, 1);
        psInsert.setString(2, "Ivan");
        psInsert.setString(3, "Ivanovich");
        psInsert.setString(4, "Ivanov");
        psInsert.executeUpdate();

        psInsert.setInt(1, 2);
        psInsert.setString(2, "Petr");
        psInsert.setString(3, "Petrovich");
        psInsert.setString(4, "Petrov");
        psInsert.executeUpdate();

        return psInsert;
    }

    private void checkInitialData(DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            resultSet = checkAccountsInitialData(statement);
            resultSet = checkUsersInitialData(statement);
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    private ResultSet checkAccountsInitialData(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT userId, currentAmount FROM accounts ORDER BY userId");

        boolean failure = false;
        int count = 0;
        while (resultSet.next()) {
            int userId = resultSet.getInt(1);
            BigDecimal currentAmount = resultSet.getBigDecimal(2);

            if (0 == count && (userId != 1 || INITIAL_FIRST_ACCOUNT_AMOUNT.compareTo(currentAmount) != 0)) {
                failure = true;
            } else if (1 == count && (userId != 2 || INITIAL_SECOND_ACCOUNT_AMOUNT.compareTo(currentAmount) != 0)) {
                failure = true;
            }
            count++;
        }

        if (!failure) {
            log.info("Verified accounts rows");
        } else {
            log.info("Accounts rows verification failed");
        }
        return resultSet;
    }

    private ResultSet checkUsersInitialData(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT userId, firstName, middleName, lastName FROM users ORDER BY userId");

        boolean failure = false;
        int count = 0;
        while (resultSet.next()) {
            int userId = resultSet.getInt(1);
            String firstName = resultSet.getString(2);
            String middleName = resultSet.getString(3);
            String lastName = resultSet.getString(4);

            if (0 == count && (userId != 1 || !"Ivan".equals(firstName) || !"Ivanovich".equals(middleName) || !"Ivanov".equals(lastName))) {
                failure = true;
            } else if (1 == count && (userId != 2 || !"Petr".equals(firstName) || !"Petrovich".equals(middleName) || !"Petrov".equals(lastName))) {
                failure = true;
            }
            count++;
        }

        if (!failure) {
            log.info("Verified users rows");
        } else {
            log.info("Users rows verification failed");
        }
        return resultSet;
    }

    public void clearTables() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.execute("delete from transfers");
            statement.execute("delete from users");
            statement.execute("delete from accounts");
            connection.commit();
            log.info("All tables have been cleared");
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }
    }

}
