package com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.ProtocolState;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.enums.RequestType;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerExtraData;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.utils.CryptoBrokerQuote;

import java.util.List;
import java.util.UUID;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.structure.CryptoBrokerActorNetworkServiceQuotesRequest</code>
 * bla bla bla.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 06/02/2016.
 */
public class CryptoBrokerActorNetworkServiceQuotesRequest implements CryptoBrokerExtraData<CryptoBrokerQuote> {

    private final UUID                    requestId            ;
    private final String                  requesterPublicKey   ;
    private final Actors                  requesterActorType   ;
    private final String                  cryptoBrokerPublicKey;
    private final long                    updateTime           ;
    private final List<CryptoBrokerQuote> quotes               ;
    private final RequestType             type                 ;
    private final ProtocolState           state                ;

    public CryptoBrokerActorNetworkServiceQuotesRequest(final UUID                    requestId            ,
                                                        final String                  requesterPublicKey   ,
                                                        final Actors                  requesterActorType   ,
                                                        final String                  cryptoBrokerPublicKey,
                                                        final long                    updateTime           ,
                                                        final List<CryptoBrokerQuote> quotes               ,
                                                        final RequestType             type                 ,
                                                        final ProtocolState           state                ) {

        this.requestId             = requestId            ;
        this.requesterPublicKey    = requesterPublicKey   ;
        this.requesterActorType    = requesterActorType   ;
        this.cryptoBrokerPublicKey = cryptoBrokerPublicKey;
        this.updateTime            = updateTime           ;
        this.quotes                = quotes               ;
        this.type                  = type                 ;
        this.state                 = state                ;
    }

    @Override
    public UUID getRequestId() {
        return requestId;
    }

    @Override
    public String getRequesterPublicKey() {
        return requesterPublicKey;
    }

    @Override
    public Actors getRequesterActorType() {
        return requesterActorType;
    }

    @Override
    public String getCryptoBrokerPublicKey() {
        return cryptoBrokerPublicKey;
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public List<CryptoBrokerQuote> listInformation() {
        return quotes;
    }

    public RequestType getType() {
        return type;
    }

    public ProtocolState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "CryptoBrokerActorNetworkServiceQuotesRequest{" +
                "requestId=" + requestId +
                ", requesterPublicKey='" + requesterPublicKey + '\'' +
                ", requesterActorType=" + requesterActorType +
                ", cryptoBrokerPublicKey='" + cryptoBrokerPublicKey + '\'' +
                ", updateTime=" + updateTime +
                ", quotes=" + quotes +
                ", type=" + type +
                ", state=" + state +
                '}';
    }
}
