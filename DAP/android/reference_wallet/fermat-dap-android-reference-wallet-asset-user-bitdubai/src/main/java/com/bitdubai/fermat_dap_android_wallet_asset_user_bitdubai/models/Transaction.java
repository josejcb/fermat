package com.bitdubai.fermat_dap_android_wallet_asset_user_bitdubai.models;

import com.bitdubai.fermat_dap_api.layer.dap_actor.DAPActor;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.TransactionType;
import com.bitdubai.fermat_dap_api.layer.all_definition.util.DAPStandardFormats;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWalletTransaction;

import java.sql.Timestamp;

/**
 * Created by Frank Contreras (contrerasfrank@gmail.com) on 2/11/16.
 */
public class Transaction {
    private byte[] actorImage;
    private String actorName;
    private TransactionType transactionType;
    private BalanceType balanceType;
    private Timestamp date;

    private AssetUserWalletTransaction assetUserWalletTransaction;

    public Transaction(AssetUserWalletTransaction assetUserWalletTransaction, DAPActor dapActor) {
        setAssetUserWalletTransaction(assetUserWalletTransaction);
        setActorName(dapActor.getName());
        setAmount(assetUserWalletTransaction.getAmount());
        setDate(new Timestamp(assetUserWalletTransaction.getTimestamp()));
        setActorImage(dapActor.getProfileImage());
        setTransactionType(assetUserWalletTransaction.getTransactionType());
        setBalanceType(assetUserWalletTransaction.getBalanceType());
    }

    public Transaction() {
    }

    public byte[] getActorImage() {
        return actorImage;
    }

    public void setActorImage(byte[] actorImage) {
        this.actorImage = actorImage;
    }

    private double amount;

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getFormattedDate() {
        if (date == null) return "No transaction date";
        return DAPStandardFormats.SIMPLE_DATETIME_FORMAT.format(date);
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    public AssetUserWalletTransaction getAssetUserWalletTransaction() {
        return assetUserWalletTransaction;
    }

    public void setAssetUserWalletTransaction(AssetUserWalletTransaction assetUserWalletTransaction) {
        this.assetUserWalletTransaction = assetUserWalletTransaction;
    }
}