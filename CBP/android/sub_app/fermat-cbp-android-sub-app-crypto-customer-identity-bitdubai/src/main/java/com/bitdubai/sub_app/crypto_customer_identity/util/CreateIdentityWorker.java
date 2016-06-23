package com.bitdubai.sub_app.crypto_customer_identity.util;

import android.content.Context;

import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_cbp_api.all_definition.enums.Frecuency;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_customer_identity.interfaces.CryptoCustomerIdentityModuleManager;


/**
 * Created by nelsonalfo on 18/06/16.
 */
public class CreateIdentityWorker extends FermatWorker {
    private static final int SUCCESS = 1;

    private final String alias;
    private Frecuency frecuency;
    private int accuracy;
    private byte[] imgByteArray;
    private CryptoCustomerIdentityModuleManager moduleManager;


    public CreateIdentityWorker(Context context, CryptoCustomerIdentityModuleManager moduleManager, FermatWorkerCallBack callBack,
                                String alias, byte[] imgByteArray, int accuracy, Frecuency frecuency) {
        super(context, callBack);

        this.moduleManager = moduleManager;
        this.alias = alias;
        this.frecuency = frecuency;
        this.accuracy = accuracy;
        this.imgByteArray = imgByteArray;
    }

    @Override
    protected Object doInBackground() throws Exception {
        moduleManager.createCryptoCustomerIdentity(alias, imgByteArray, accuracy, frecuency);
        return SUCCESS;
    }
}