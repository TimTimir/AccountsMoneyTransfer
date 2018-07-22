package org.flomintv.accounts.money.transfer.processors;

import org.flomintv.accounts.money.transfer.database.AccountDAO;
import org.flomintv.accounts.money.transfer.model.Account;

public class AccountService {

    private AccountDAO accountDAO;

    public Account getAccount(Integer userId) {
        return accountDAO.getAccountData(userId);
    }

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

}
