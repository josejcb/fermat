package com.bitdubai.fermat_api.layer.dmp_basic_wallet.bitcoin_wallet.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.dmp_basic_wallet.bitcoin_wallet.enums.TransactionState;
import com.bitdubai.fermat_api.layer.dmp_basic_wallet.bitcoin_wallet.enums.TransactionType;

import java.util.UUID;

/**
 * Created by eze on 2015.06.17..
 */
public interface BitcoinWalletTransactionRecord {

    /*
     * TODO: Natalia - Agregar un UUID a esta interfaz. Va a ser necesario para
     *       registrar las transacciones.
     */

    public CryptoAddress getAddressFrom();

    public void setAddressFrom(CryptoAddress addressFrom);

    public CryptoAddress getAddressTo();

    public void setAddressTo(CryptoAddress addressTo);

    public long getAmount();

    public void setAmount(long amount);

    public TransactionType getType();

    public void setType(TransactionType type);

    /*
     * TODO: Natalia - borrar los métodos getState y setState de esta interfaz
     *       (el enum TransactionState queda como está)
     */
    public TransactionState getState();

    public void setState(TransactionState state);

    public long getTimestamp();

    public void setTimestamp(long timestamp);

    public String getMemo();

    public void setMemo(String memo);

    public String getTramsactionHash();

    public void setTramsactionHash(String tramsactionHash);
}
