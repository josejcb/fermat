package com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.listeners;

import com.bitdubai.fermat_api.layer.all_definition.events.common.GenericEventListener;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventMonitor;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.enums.EventType;

/**
 * Created by rodrigo on 11/19/15.
 */
public class IncomingAssetOnBlockchainWaitingTransferenceRedeemPointEventListener extends GenericEventListener {
    public IncomingAssetOnBlockchainWaitingTransferenceRedeemPointEventListener(FermatEventMonitor fermatEventMonitor) {
        super(EventType.INCOMING_ASSET_ON_BLOCKCHAIN_WAITING_TRANSFERENCE_REDEEM_POINT, fermatEventMonitor);
    }
}
