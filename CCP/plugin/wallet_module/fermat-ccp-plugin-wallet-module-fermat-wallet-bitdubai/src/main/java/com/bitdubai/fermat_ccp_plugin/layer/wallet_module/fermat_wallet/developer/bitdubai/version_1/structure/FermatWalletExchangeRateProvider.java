package com.bitdubai.fermat_ccp_plugin.layer.wallet_module.fermat_wallet.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_ccp_api.layer.wallet_module.loss_protected_wallet.interfaces.ExchangeRateProvider;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Daniel González on 16/06/16.
 */
public class FermatWalletExchangeRateProvider implements ExchangeRateProvider, Serializable {

    private UUID providerId;
    private String providerName;
    public FermatWalletExchangeRateProvider (UUID providerId,String providerName)
    {
        this.providerId = providerId;
        this.providerName  = providerName;
    }
    @Override
    public UUID getProviderId() {
        return this.providerId;
    }

    @Override
    public String getProviderName() {
        return this.providerName;
    }
}
