package com.bitdubai.fermat_wrd_api.layer.api.tokenly.interfaces;

import java.sql.Date;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 04/03/16.
 */
public interface Bot {

    /**
     * This method returns the bot id.
     * @return
     */
    String getId();

    /**
     * This method return the bot name
     * @return
     */
    String getName();

    /**
     * This method returns the address
     * @return
     */
    String getAddress();

    /**
     * This method returns the user name
     * @return
     */
    String getUserName();

    /**
     * This method returns the bot description
     * @return
     */
    String getDescription();

    /**
     * This method returns the html bot description.
     * @return
     */
    String getDescriptionHtml();

    /**
     * This method the background image details
     * @return
     */
    ImageDetails getBackgroundImageDetails();

    /**
     * This method returns the logo image details
     * @return
     */
    ImageDetails getLogoImageDetails();

    /**
     * This method returns the background overlay settings
     * @return
     */
    String[] getBackgroundOverlaySettings();

    /**
     * This method returns the swap bots
     * @return
     */
    Swap[] getSwaps();

    /**
     * This method returns the swap bot balances
     * @return
     */
    TokenlyBalance[] getBalances();

    /**
     * This method returns the swap bot balances by type
     * @return
     */
    TokenlyBalance[] getAllBalancesByType();

    /**
     * This method return the return fee.
     * @return
     */
    float getReturnFee();

    /**
     * This method returns the swap bot state.
     * @return
     */
    String getState();

    /**
     * This method returns the confirmations required
     * @return
     */
    long getConfirmationsRequired();

    /**
     * Thi method returns the refund after blocks
     * @return
     */
    long getRefundAfterBlocks();

    /**
     * This method returns the creation date
     * @return
     */
    Date getCreatedAt();

    /**
     * This method return the bot hash
     * @return
     */
    String getHash();

}
