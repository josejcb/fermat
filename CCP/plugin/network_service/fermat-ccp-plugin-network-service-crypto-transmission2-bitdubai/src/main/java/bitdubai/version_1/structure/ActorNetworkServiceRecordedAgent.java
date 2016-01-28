package bitdubai.version_1.structure;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStopAgentException;
import com.bitdubai.fermat_api.FermatAgent;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.AgentStatus;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.all_definition.network_service.interfaces.NetworkServiceLocal;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_ccp_api.layer.identity.intra_user.exceptions.CantListIntraWalletUsersException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_addresses.exceptions.PendingRequestNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_payment_request.exceptions.RequestNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_transmission.enums.CryptoTransmissionMetadataState;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_transmission.enums.CryptoTransmissionProtocolState;
import com.bitdubai.fermat_ccp_api.layer.network_service.crypto_transmission.interfaces.structure.CryptoTransmissionMetadataType;

import com.bitdubai.fermat_ccp_api.layer.network_service.intra_actor.events.ActorNetworkServicePendingsNotificationEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.common.network_services.template.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.enums.EventType;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.events.IncomingCryptoMetadataEvent;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import bitdubai.version_1.CryptoTransmissionNetworkServicePluginRoot;
import bitdubai.version_1.database.communications.CryptoTransmissionNetworkServiceDatabaseConstants;
import bitdubai.version_1.exceptions.CantSaveCryptoTransmissionMetadatatException;
import bitdubai.version_1.exceptions.CantUpdateRecordDataBaseException;
import bitdubai.version_1.structure.structure.CryptoTransmissionMessage;
import bitdubai.version_1.structure.structure.CryptoTransmissionMessageType;
import bitdubai.version_1.structure.structure.CryptoTransmissionMetadataRecord;
import bitdubai.version_1.structure.structure.CryptoTransmissionResponseMessage;

/**
 * Created by mati on 2015.10.15..
 */
public class ActorNetworkServiceRecordedAgent extends FermatAgent{


    private static final long SEND_SLEEP_TIME    = 15000;
    private static final long RECEIVE_SLEEP_TIME = 15000;
    private final ExecutorService threadPoolExecutor;

    private Runnable toSend   ;
    private Runnable toReceive;

    // network services registered
    private Map<String, CryptoTransmissionMessage> poolConnectionsWaitingForResponse;

    private List<Future<?>> futures= new ArrayList<>();

    private final CryptoTransmissionNetworkServicePluginRoot cryptoTransmissionNetworkServicePluginRoot;
    private final ErrorManager errorManager                                ;
    private final EventManager eventManager                                ;

    public ActorNetworkServiceRecordedAgent( final CryptoTransmissionNetworkServicePluginRoot cryptoTransmissionNetworkServicePluginRoot,
                                             final ErrorManager                                 errorManager                                ,
                                             final EventManager                                 eventManager
                                             ) {

        this.errorManager                                 = errorManager                                ;
        this.eventManager                                 = eventManager                                ;
        this.status                                       = AgentStatus.CREATED                         ;
        this.cryptoTransmissionNetworkServicePluginRoot = cryptoTransmissionNetworkServicePluginRoot;

        poolConnectionsWaitingForResponse = new HashMap<>();

        threadPoolExecutor = Executors.newFixedThreadPool(2);
        //Create a thread to send the messages
        this.toSend = new Runnable() {
            @Override
            public void run() {
                while (isRunning())
                    sendCycle();
            }
        };

        //Create a thread to receive the messages
        this.toReceive = new Runnable() {
            @Override
            public void run() {
                while (isRunning())
                    receiveCycle();
            }
        };
    }


    public void start() throws CantStartAgentException {

        try {
            futures.add(threadPoolExecutor.submit(toSend));
            futures.add(threadPoolExecutor.submit(toReceive));

            this.status = AgentStatus.STARTED;

        } catch (Exception exception) {

            throw new CantStartAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    public void resume() throws CantStartAgentException {
        try {
            futures.add(threadPoolExecutor.submit(toSend));
            futures.add(threadPoolExecutor.submit(toReceive));

            this.status = AgentStatus.STARTED;

        } catch (Exception exception) {

            throw new CantStartAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    public void pause() throws CantStopAgentException {
        try {

            Iterator<Future<?>> it = futures.iterator();

            while (it.hasNext()){
                it.next().cancel(true);
            }

            this.status = AgentStatus.PAUSED;

        } catch (Exception exception) {

            throw new CantStopAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    public void stop() throws CantStopAgentException {
        try {

            Iterator<Future<?>> it = futures.iterator();

            while (it.hasNext()){
                it.next().cancel(true);
            }

            this.status = AgentStatus.PAUSED;

        } catch (Exception exception) {

            throw new CantStopAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
        }
    }

    // TODO MANAGE PAUSE, STOP AND RESUME METHODS.

    public void sendCycle() {

        try {

            if(cryptoTransmissionNetworkServicePluginRoot.isRegister() && cryptoTransmissionNetworkServicePluginRoot.isStarted()) {

                // function to process and send the rigth message to the counterparts.
                processSend();

            }

            //Sleep for a time
            TimeUnit.SECONDS.sleep(2);

        } catch (InterruptedException e) {
            status = AgentStatus.STOPPED;
            reportUnexpectedError(FermatException.wrapException(e));
        } catch(Exception e) {
            reportUnexpectedError(FermatException.wrapException(e));
        }

    }

    private void processSend() {
        try {

            Map<String, Object> filters = new HashMap<>();
            filters.put(CryptoTransmissionNetworkServiceDatabaseConstants.CRYPTO_TRANSMISSION_METADATA_STATUS_COLUMN_NAME, CryptoTransmissionProtocolState.PRE_PROCESSING_SEND.getCode());
            List<CryptoTransmissionMetadataRecord> lstActorRecord = cryptoTransmissionNetworkServicePluginRoot.getOutgoingMetadataDao().findAll(
                    filters
            );





            for (CryptoTransmissionMessage cpr : lstActorRecord) {
                switch (cpr.getCryptoTransmissionMessageType()) {

                    case METADATA:
                        sendMessageToActor(
                                cpr
                        );
                        break;
                    case RESPONSE:
                        CryptoTransmissionResponseMessage cryptoTransmissionResponseMessage = null;
                        switch (cpr.getCryptoTransmissionMetadataState()){
                            case SEEN_BY_DESTINATION_NETWORK_SERVICE:
                                cryptoTransmissionResponseMessage = new CryptoTransmissionResponseMessage(
                                        cpr.getTransactionId(),
                                        CryptoTransmissionMessageType.RESPONSE,
                                        CryptoTransmissionProtocolState.SENT,
                                        CryptoTransmissionMetadataType.METADATA_SEND,
                                        CryptoTransmissionMetadataState.SEEN_BY_DESTINATION_NETWORK_SERVICE,
                                        cpr.getSenderPublicKey(),
                                        cpr.getDestinationPublicKey(),
                                        false,
                                        0);
                                break;

                        }
                        sendMessageToActor(
                                cryptoTransmissionResponseMessage
                        );
                        break;

                }

            }
//        } catch (CantExecuteDatabaseOperationException e) {
//            e.printStackTrace();
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void receiveCycle() {

        try {

            if(cryptoTransmissionNetworkServicePluginRoot.isRegister() && cryptoTransmissionNetworkServicePluginRoot.isStarted()) {

                // function to process and send the right message to the counterparts.
                processReceive();
            }

            //Sleep for a time
            Thread.sleep(RECEIVE_SLEEP_TIME);

        } catch (InterruptedException e) {
            status = AgentStatus.STOPPED;
            reportUnexpectedError(FermatException.wrapException(e));
        } catch(Exception e) {

            reportUnexpectedError(FermatException.wrapException(e));
        }

    }

    public void processReceive(){
       try {
           Map<String, Object> filters = new HashMap<>();
           filters.put(CryptoTransmissionNetworkServiceDatabaseConstants.CRYPTO_TRANSMISSION_METADATA_PENDING_FLAG_COLUMN_NAME, "false");
             /*
             * Read all pending CryptoTransmissionMetadata from database
             */
           List<CryptoTransmissionMetadataRecord> lstCryptoTransmissionMetadata = cryptoTransmissionNetworkServicePluginRoot.getIncomingNotificationsDao().findAllNotDone(filters);


           for(CryptoTransmissionMetadataRecord cryptoTransmissionMetadata : lstCryptoTransmissionMetadata) {
               switch (cryptoTransmissionMetadata.getCryptoTransmissionMetadataStates()) {
                   case SEEN_BY_DESTINATION_NETWORK_SERVICE:
                       //guardo estado
                       // deberia ver si tengo que lanzar un evento acá
                       cryptoTransmissionNetworkServicePluginRoot.getIncomingNotificationsDao().changeCryptoTransmissionProtocolState(
                               cryptoTransmissionMetadata.getTransactionId(),
                               CryptoTransmissionProtocolState.RECEIVED);
                       //TODO: for test reason
                       lauchNotification();
                       System.out.print("-----------------------\n" +
                               "ACA DEBERIA LANZAR EVENTO NO CREO  -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionMetadataStates());
                       System.out.print("CryptoTransmission SEEN_BY_DESTINATION_NETWORK_SERVICE event");
                       break;

                   case SEEN_BY_DESTINATION_VAULT:
                       // deberia ver si tengo que lanzar un evento acá
                       System.out.print("-----------------------\n" +
                               "ACA DEBERIA LANZAR EVENTO NO CREO -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionMetadataStates());
                       System.out.print("CryptoTransmission SEEN_BY_DESTINATION_VAULT event");

                       cryptoTransmissionMetadata.changeCryptoTransmissionProtocolState(CryptoTransmissionProtocolState.RECEIVED);
                       cryptoTransmissionMetadata.changeMetadataState(CryptoTransmissionMetadataState.SEEN_BY_DESTINATION_VAULT);
                       cryptoTransmissionMetadata.setTypeMetadata(CryptoTransmissionMetadataType.METADATA_RECEIVE);
                       cryptoTransmissionNetworkServicePluginRoot.getOutgoingMetadataDao().update(cryptoTransmissionMetadata);

                       System.out.print("-----------------------\n" +
                               "RECIVIENDO CRYPTO METADATA!!!!! -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionMetadataStates());

                       lauchNotification();
                       break;

                   case CREDITED_IN_DESTINATION_WALLET:
                       // Guardo estado
                       System.out.print("-----------------------\n" +
                               "RECIBIENDO CRYPTO METADATA!!!!! -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionMetadataStates());

                       //update message in DONE and Close connection with another device - End message
                       cryptoTransmissionNetworkServicePluginRoot.getOutgoingMetadataDao().doneTransaction(cryptoTransmissionMetadata.getTransactionId());


                       System.out.print("CryptoTransmission Close Connection - End Message");
                       this.poolConnectionsWaitingForResponse.remove(cryptoTransmissionMetadata.getDestinationPublicKey());
                       break;
                   // si el mensaje viene con un estado de SENT es porque es la primera vez que llega, por lo que tengo que guardarlo en la bd y responder
                   case SEEN_BY_OWN_NETWORK_SERVICE_WAITING_FOR_RESPONSE:

//                                incomingCryptoTransmissionMetadataDAO.changeTransactionStateAndProtocolState(
//                                        cryptoTransmissionMetadata.getTransactionId(),
//                                        CryptoTransmissionMetadataState.SEEN_BY_DESTINATION_NETWORK_SERVICE,
//                                        CryptoTransmissionProtocolState.RECEIVED);

                       System.out.print("-----------------------\n" +
                               "RECIBIENDO CRYPTO METADATA!!!!! -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionProtocolState());

                       lauchNotification();

                       // El destination soy yo porque me lo estan enviando
                       // El sender es el otro y es a quien le voy a responder
                           // Notifico recepcion de metadata
                       CryptoTransmissionMetadataRecord cryptoTransmissionMetadataRecord = new CryptoTransmissionMetadataRecord(
                                   cryptoTransmissionMetadata.getTransactionId(),
                                   CryptoTransmissionMessageType.RESPONSE,
                                   cryptoTransmissionMetadata.getRequestId(),
                                   cryptoTransmissionMetadata.getCryptoCurrency(),
                                   cryptoTransmissionMetadata.getCryptoAmount(),
                                   cryptoTransmissionMetadata.getDestinationPublicKey(),
                                   cryptoTransmissionMetadata.getSenderPublicKey(),
                                   cryptoTransmissionMetadata.getAssociatedCryptoTransactionHash(),
                                   cryptoTransmissionMetadata.getPaymentDescription(),
                                   CryptoTransmissionProtocolState.SENT,
                                   CryptoTransmissionMetadataType.METADATA_SEND,
                                   cryptoTransmissionMetadata.getTimestamp(),
                                   false,
                                   0,
                                   CryptoTransmissionMetadataState.SEEN_BY_DESTINATION_NETWORK_SERVICE
                           );
                       cryptoTransmissionNetworkServicePluginRoot.getOutgoingMetadataDao().saveCryptoTransmissionMetadata(cryptoTransmissionMetadataRecord);


                           System.out.print("-----------------------\n" +
                                   "GUARDANDO RESPUESTA CRYPTO METADATA PARA ENVIAR, LO VI Y AHORA LE DIGO QUE YA TENGO LA METADATA!!!!! -----------------------\n" +
                                   "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionProtocolState());

                       break;
                   case CREDITED_IN_OWN_WALLET:
                       System.out.print("-----------------------\n" +
                               "CREDITED IN WALLET, TENES QUE VER QUE SE HACE ACÁ !!!!! -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionProtocolState());
                       break;
                   default:
                       System.out.print("-----------------------\n" +
                               "TE ESTAS YENDO POR EL DEFAULT !!!!! -----------------------\n" +
                               "-----------------------\n STATE: " + cryptoTransmissionMetadata.getCryptoTransmissionProtocolState());
                       break;
               }
            }



       } catch (CantReadRecordDataBaseException e) {
           e.printStackTrace();
       } catch (CantUpdateRecordDataBaseException e) {
           e.printStackTrace();
       } catch (PendingRequestNotFoundException e) {
           e.printStackTrace();
       } catch (CantSaveCryptoTransmissionMetadatatException e) {
           e.printStackTrace();
       }
    }



    private void sendMessageToActor(CryptoTransmissionMessage actorNetworkServiceRecord) {
        try {
            String actorDestinationPublicKey = actorNetworkServiceRecord.getDestinationPublicKey();
            if (!poolConnectionsWaitingForResponse.containsKey(actorDestinationPublicKey)) {
                if (cryptoTransmissionNetworkServicePluginRoot.getNetworkServiceConnectionManager().getNetworkServiceLocalInstance(actorDestinationPublicKey) == null) {
                    if (cryptoTransmissionNetworkServicePluginRoot.getWsCommunicationsCloudClientManager() != null) {
                        if (cryptoTransmissionNetworkServicePluginRoot.getPlatformComponentProfilePluginRoot() != null) {

                            PlatformComponentProfile applicantParticipant = cryptoTransmissionNetworkServicePluginRoot.getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection()
                                    .constructPlatformComponentProfileFactory(
                                            actorNetworkServiceRecord.getSenderPublicKey(),
                                            "sender_alias",//actorNetworkServiceRecord.getActorSenderAlias(),
                                            "sender_alias",//actorNetworkServiceRecord.getActorSenderAlias(),
                                            NetworkServiceType.UNDEFINED,
                                            PlatformComponentType.ACTOR_INTRA_USER,"");
                            PlatformComponentProfile remoteParticipant = cryptoTransmissionNetworkServicePluginRoot.getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection()
                                    .constructPlatformComponentProfileFactory(
                                            actorDestinationPublicKey,
                                            "destination_alias",
                                            "destination_alias",
                                            NetworkServiceType.UNDEFINED,
                                            PlatformComponentType.ACTOR_INTRA_USER,"");

                            cryptoTransmissionNetworkServicePluginRoot.getNetworkServiceConnectionManager().connectTo(
                                    applicantParticipant,
                                    cryptoTransmissionNetworkServicePluginRoot.getPlatformComponentProfilePluginRoot(),
                                    remoteParticipant
                            );

                            // I put the actor in the pool of connections waiting for response-
                            poolConnectionsWaitingForResponse.put(actorDestinationPublicKey, actorNetworkServiceRecord);
                        }

                    }
                }else{
                    NetworkServiceLocal communicationNetworkServiceLocal = cryptoTransmissionNetworkServicePluginRoot.getNetworkServiceConnectionManager().getNetworkServiceLocalInstance(actorNetworkServiceRecord.getDestinationPublicKey());

                    System.out.println("----------------------------\n" +
                            "ENVIANDO MENSAJE: " + actorDestinationPublicKey
                            + "\n-------------------------------------------------");


                    communicationNetworkServiceLocal.sendMessage(
                            actorNetworkServiceRecord.getSenderPublicKey(),
                            actorNetworkServiceRecord.getDestinationPublicKey(),
                           actorNetworkServiceRecord.toJson()
                    );

                    cryptoTransmissionNetworkServicePluginRoot.getOutgoingMetadataDao().changeCryptoTransmissionProtocolState(
                            actorNetworkServiceRecord.getTransactionId(),
                            CryptoTransmissionProtocolState.SENT_TO_COMMUNICATION_TEMPLATE);

                    poolConnectionsWaitingForResponse.put(actorNetworkServiceRecord.getDestinationPublicKey(), actorNetworkServiceRecord);
                }
            } else {

                NetworkServiceLocal communicationNetworkServiceLocal = cryptoTransmissionNetworkServicePluginRoot.getNetworkServiceConnectionManager().getNetworkServiceLocalInstance(actorNetworkServiceRecord.getDestinationPublicKey());

                if (communicationNetworkServiceLocal != null) {

                    try {

                        System.out.println("----------------------------\n" +
                                "ENVIANDO MENSAJE: " + actorNetworkServiceRecord.getDestinationPublicKey()
                                + "\n-------------------------------------------------");


                        communicationNetworkServiceLocal.sendMessage(
                                actorNetworkServiceRecord.getSenderPublicKey(),
                                actorNetworkServiceRecord.getDestinationPublicKey(),
                                actorNetworkServiceRecord.toJson()
                        );

                        cryptoTransmissionNetworkServicePluginRoot.getOutgoingMetadataDao().changeCryptoTransmissionProtocolState(
                                actorNetworkServiceRecord.getTransactionId(),
                                CryptoTransmissionProtocolState.SENT_TO_COMMUNICATION_TEMPLATE);

                    } catch (Exception e) {

                        reportUnexpectedError(FermatException.wrapException(e));
                    }
                }
            }
        } catch (Exception z) {
            reportUnexpectedError(FermatException.wrapException(z));
        }
    }

    private PlatformComponentType platformComponentTypeSelectorByActorType(Actors type) throws InvalidParameterException {

        switch (type) {

            case INTRA_USER  : return PlatformComponentType.ACTOR_INTRA_USER  ;
            case DAP_ASSET_ISSUER: return PlatformComponentType.ACTOR_ASSET_ISSUER;
            case DAP_ASSET_USER  : return PlatformComponentType.ACTOR_ASSET_USER  ;

            default: throw new InvalidParameterException(
                    " actor type: "+type.name()+"  type-code: "+type.getCode(),
                    " type of actor not expected."
            );
        }
    }



    private void raiseEvent(final EventType eventType,
                            final UUID      requestId) {

        FermatEvent eventToRaise = eventManager.getNewEvent(eventType);
        //((CryptoPaymentRequestEvent) eventToRaise).setRequestId(requestId);
        //eventToRaise.setSource(CryptoPaymentRequestNetworkServicePluginRoot.EVENT_SOURCE);
        eventManager.raiseEvent(eventToRaise);
    }

    private void reportUnexpectedError(FermatException e) {
        errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INTRAUSER_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
    }

    public void connectionFailure(String identityPublicKey){
        this.poolConnectionsWaitingForResponse.remove(identityPublicKey);
    }


    public Map<String, CryptoTransmissionMessage> getPoolConnectionsWaitingForResponse() {
        return poolConnectionsWaitingForResponse;
    }

    private void lauchNotification(){
        FermatEvent fermatEvent = eventManager.getNewEvent(EventType.INCOMING_CRYPTO_METADATA);
        IncomingCryptoMetadataEvent incomingCryptoMetadataReceive = (IncomingCryptoMetadataEvent) fermatEvent;
        incomingCryptoMetadataReceive.setSource(EventSource.NETWORK_SERVICE_CRYPTO_TRANSMISSION);
        eventManager.raiseEvent(incomingCryptoMetadataReceive);
    }

}
