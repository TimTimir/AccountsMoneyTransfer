package org.flomintv.accounts.money.transfer.processors;

import org.flomintv.accounts.money.transfer.database.AccountDAO;
import org.flomintv.accounts.money.transfer.database.TransferDAO;
import org.flomintv.accounts.money.transfer.error.NotEnoughMoneyException;
import org.flomintv.accounts.money.transfer.model.Transfer;

import java.util.concurrent.atomic.AtomicInteger;

public class TransferService {

    private TransferDAO transferDAO;

    private AccountDAO accountDAO;

    private final AtomicInteger idGen = new AtomicInteger();

    public Transfer getTransfer(int transferId) {
        return transferDAO.getTransfer(transferId);
    }

    public String createTransfer(Transfer transfer) throws NotEnoughMoneyException {
        int transferId = idGen.incrementAndGet();
        accountDAO.transferMoneyFromOneAccountToAnother(transfer);
        transferDAO.insertNewTransfer(transfer, transferId);
        return Integer.toString(transferId);
    }

    public void cancelTransfer(int transferId) {
        //TODO
    }

    public void setTransferDAO(TransferDAO transferDAO) {
        this.transferDAO = transferDAO;
    }

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }
}
