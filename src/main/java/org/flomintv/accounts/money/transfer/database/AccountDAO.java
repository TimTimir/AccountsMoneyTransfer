package org.flomintv.accounts.money.transfer.database;

import org.flomintv.accounts.money.transfer.error.NotEnoughMoneyException;
import org.flomintv.accounts.money.transfer.model.Account;
import org.flomintv.accounts.money.transfer.model.Transfer;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.flomintv.accounts.money.transfer.database.DBUtils.*;

public class AccountDAO {

    private DataSource dataSource;

    public AccountDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Account getAccountData(Integer userId) {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            selectStatement = connection.prepareStatement("SELECT currentAmount FROM accounts where userId = ?");
            selectStatement.setInt(1, userId);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                BigDecimal currentAmount = resultSet.getBigDecimal(1);

                Account account = new Account();
                account.setUserId(userId);
                account.setCurrentAmount(currentAmount);

                return account;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeResultSet(resultSet);
            closeStatement(selectStatement);
            closeConnection(connection);
        }

        return null;
    }

    public void transferMoneyFromOneAccountToAnother(Transfer transfer) throws NotEnoughMoneyException {
        Account accountDataFrom = getAccountData(transfer.getUserIdFrom());
        if (accountDataFrom.getCurrentAmount().compareTo(transfer.getAmount()) < 0) {
            throw new NotEnoughMoneyException("Sorry, but it seems you don't have enough money on your account");
        }
        Account accountDataTo = getAccountData(transfer.getUserIdTo());
        BigDecimal amountFrom = accountDataFrom.getCurrentAmount().subtract(transfer.getAmount());
        BigDecimal amountTo = accountDataTo.getCurrentAmount().add(transfer.getAmount());
        performTransfer(transfer, amountFrom, amountTo);
    }

    private void performTransfer(Transfer transfer, BigDecimal amountFrom, BigDecimal amountTo) {
        Connection connection = null;
        PreparedStatement psUpdate = null;
        try {
            connection = dataSource.getConnection("sa", "");
            connection.setAutoCommit(false);
            psUpdate = connection.prepareStatement("update accounts set currentAmount = ? where userId = ?");

            psUpdate.setBigDecimal(1, amountFrom);
            psUpdate.setInt(2, transfer.getUserIdFrom());
            psUpdate.executeUpdate();

            psUpdate.setBigDecimal(1, amountTo);
            psUpdate.setInt(2, transfer.getUserIdTo());
            psUpdate.executeUpdate();

            connection.commit();
        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            closeStatement(psUpdate);
            closeConnection(connection);
        }
    }
}
