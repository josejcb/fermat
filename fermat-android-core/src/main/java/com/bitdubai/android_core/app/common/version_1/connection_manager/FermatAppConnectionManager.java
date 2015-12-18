package com.bitdubai.android_core.app.common.version_1.connection_manager;

import android.app.Activity;

import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatAppConnection;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.interfaces.IntraUserLoginIdentity;
import com.bitdubai.fermat_dap_android_sub_app_asset_factory_bitdubai.app_connection.AssetFactoryFermatAppConnection;
import com.bitdubai.fermat_dap_android_sub_app_asset_issuer_community_bitdubai.app_connection.CommunityAssetIssuerFermatAppConnection;
import com.bitdubai.fermat_dap_android_sub_app_asset_issuer_identity_bitdubai.app_connection.AssetIssuerFermatAppConnection;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.app_connection.CommunityAssetUserFermatAppConnection;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_identity_bitdubai.app_connection.AssetUserFermatAppConnection;
import com.bitdubai.fermat_dap_android_sub_app_redeem_point_community_bitdubai.app_connection.CommunityRedeemPointFermatAppConnection;
import com.bitdubai.fermat_dap_android_sub_app_redeem_point_identity_bitdubai.app_connection.RedeemPointFermatAppConnection;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.app_connection.BitcoinWalletFermatAppConnection;
import com.bitdubai.reference_wallet.crypto_broker_wallet.app_connection.CryptoBrokerWalletFermatAppConnection;
import com.bitdubai.reference_wallet.crypto_customer_wallet.app_connection.CryptoCustomerWalletFermatAppConnection;
import com.bitdubai.sub_app.crypto_broker_community.app_connection.CryptoBrokerCommunityFermatAppConnection;
import com.bitdubai.sub_app.crypto_broker_identity.app_connection.CryptoBrokerIdentityFermatAppConnection;
import com.bitdubai.sub_app.crypto_customer_identity.app_connection.CryptoCustomerIdentityFermatAppConnection;
import com.bitdubai.sub_app.intra_user_community.app_connection.CryptoWalletUserCommunityFermatAppConnection;
import com.bitdubai.sub_app.intra_user_identity.app_connection.CryptoWalletUserFermatAppConnection;

/**
 * Created by Matias Furszyfer on 2015.12.09..
 */
public class FermatAppConnectionManager {


    // todo ojo macho, no se porq ponés la intrauserloginidentity acá, pero cada app usa una identidad para manejarse... esto ees de lo que hablabas?
    public static AppConnections getFermatAppConnection(String publicKey, Activity activity, IntraUserLoginIdentity intraUserLoginIdentity) {
        AppConnections fermatAppConnection = null;
        switch (publicKey) {
            case "reference_wallet":
                fermatAppConnection = new BitcoinWalletFermatAppConnection(activity,intraUserLoginIdentity);
                break;
            case "crypto_broker_wallet":
                fermatAppConnection = new CryptoBrokerWalletFermatAppConnection(activity, null);
                break;
            case "crypto_customer_wallet":
                fermatAppConnection = new CryptoCustomerWalletFermatAppConnection(activity, null);
                break;
            case "public_key_dap_asset_issuer_identity":
                fermatAppConnection = new AssetIssuerFermatAppConnection(activity);
                break;
            case "public_key_dap_asset_user_identity":
                fermatAppConnection = new AssetUserFermatAppConnection(activity);
                break;
            case "public_key_dap_redeem_point_identity":
                fermatAppConnection = new RedeemPointFermatAppConnection(activity);
                break;
            case "public_key_dap_factory":
                fermatAppConnection = new AssetFactoryFermatAppConnection(activity);
                break;
            case "public_key_dap_issuer_community":
                fermatAppConnection = new CommunityAssetIssuerFermatAppConnection(activity);
                break;
            case "public_key_dap_user_community":
                fermatAppConnection = new CommunityAssetUserFermatAppConnection(activity);
                break;
            case "public_key_dap_reedem_point_community":
                fermatAppConnection = new CommunityRedeemPointFermatAppConnection(activity);
                break;
            case "public_key_ccp_intra_user_identity":
                fermatAppConnection = new CryptoWalletUserFermatAppConnection(activity);
                break;
            case "public_key_intra_user_commmunity":
                fermatAppConnection = new CryptoWalletUserCommunityFermatAppConnection(activity);
                break;
            case "public_key_crypto_broker_community":
                fermatAppConnection = new CryptoBrokerCommunityFermatAppConnection(activity);
                break;
            case "sub_app_crypto_broker_identity":
                fermatAppConnection = new CryptoBrokerIdentityFermatAppConnection(activity);
                break;
            case "sub_app_crypto_customer_identity":
                fermatAppConnection = new CryptoCustomerIdentityFermatAppConnection(activity);
                break;
            case "public_key_pip_developer_sub_app":
                fermatAppConnection = new DeveloperFermatAppConnection(activity);
                break;
        }
        return fermatAppConnection;
    }


}