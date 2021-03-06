package com.bitdubai.fermat_bnk_plugin.layer.bank_money_transaction.receive_offline_bank_transfer.developer.bitdubai.version_1.exceptions;

import com.bitdubai.fermat_api.FermatException;
/**
 * The Class <code>package com.bitdubai.fermat_bnk_plugin.layer.bank_money_transaction.receive_offline_bank_transfer.developer.bitdubai.version_1.exceptions.CantInitializeReceiveOfflineBankTransferBankMoneyTransactionDatabaseException</code>
 * is thrown when an error occurs initializing database
 * <p/>
 *
 * Created by Yordin Alayn - (y.alayn@gmail.com) on 01/10/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CantInitializeReceiveOfflineBankTransferBankMoneyTransactionDatabaseException extends FermatException {

    public static final String DEFAULT_MESSAGE = "CAN'T INTIALIZE RECEIVE OFFLINE BANK TRANSFER BANK MONEY TRANSACTION DATABASE EXCEPTION";

    public CantInitializeReceiveOfflineBankTransferBankMoneyTransactionDatabaseException(final String message, final Exception cause, final String context, final String possibleReason) {
        super(message, cause, context, possibleReason);
    }

    public CantInitializeReceiveOfflineBankTransferBankMoneyTransactionDatabaseException(final String message, final Exception cause) {
        this(message, cause, "", "");
    }

    public CantInitializeReceiveOfflineBankTransferBankMoneyTransactionDatabaseException(final String message) {
        this(message, null);
    }

    public CantInitializeReceiveOfflineBankTransferBankMoneyTransactionDatabaseException() {
        this(DEFAULT_MESSAGE);
    }
}