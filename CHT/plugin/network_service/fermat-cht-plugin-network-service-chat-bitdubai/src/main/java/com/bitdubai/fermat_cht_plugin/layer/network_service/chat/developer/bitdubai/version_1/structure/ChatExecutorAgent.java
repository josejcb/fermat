package com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.FermatAgent;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.enums.AgentStatus;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_user.exceptions.CantGetNotificationException;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_user.exceptions.NotificationNotFoundException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CHTException;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.enums.ChatProtocolState;
import com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.ChatNetworkServicePluginRoot;
import com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.database.IncomingNotificationDAO;
import com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.database.OutgoingNotificationDAO;
import com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.base.CommunicationNetworkServiceLocal;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The class <code>com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.version_1.structure.ChatExectuorAgent</code>
 * haves all the necessary business logic to execute all required actions.
 *
 * Created by Gabriel Araujo on 05/01/16.
 */
public final class ChatExecutorAgent extends FermatAgent {

    // Represent the sleep time for the cycles of receive and send in this agent, with both cycles send and receive 15000 millis.
    private static final long SLEEP_TIME = 7500;

    // Represent the receive and send cycles for this agent.
    private Thread agentThread;

    // network services registered
    private Map<String, String> poolConnectionsWaitingForResponse;

    private final ChatNetworkServicePluginRoot chatNetworkServicePluginRoot;
    private final ErrorManager                              errorManager                             ;
    private final EventManager                              eventManager                             ;
    private final IncomingNotificationDAO incomingNotificationDAO;
    private final OutgoingNotificationDAO outgoingNotificationDAO;

    public ChatExecutorAgent(final ChatNetworkServicePluginRoot chatNetworkServicePluginRoot,
                             final ErrorManager errorManager,
                             final EventManager eventManager,
                             final IncomingNotificationDAO incomingNotificationDAO,
                             final OutgoingNotificationDAO outgoingNotificationDAO) {

        this.chatNetworkServicePluginRoot = chatNetworkServicePluginRoot;
        this.errorManager                              = errorManager                             ;
        this.eventManager                              = eventManager                             ;
        this.incomingNotificationDAO = incomingNotificationDAO;
        this.outgoingNotificationDAO = outgoingNotificationDAO;

        this.status                                    = AgentStatus.CREATED                      ;

        this.poolConnectionsWaitingForResponse = new HashMap<>();

//        Create a thread to send the messages
        this.agentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    sendCycle();
                   // receiveCycle();
                }
            }
        });
    }

    public final void start() throws CantStartAgentException {

        if (!isRunning()) {
            try {

                this.status = AgentStatus.STARTED;

                agentThread.start();

            } catch (Exception exception) {

                throw new CantStartAgentException(FermatException.wrapException(exception), null, "You should inspect the cause.");
            }
        }
    }

    @Override
    public void pause() {
        agentThread.interrupt();
        status = AgentStatus.PAUSED;
    }

    @Override
    public void resume() {
        agentThread.start();
        status = AgentStatus.STARTED;
    }

    @Override
    public void stop() {
        agentThread.interrupt();
        //TODO: fijense esto
        status = AgentStatus.STOPPED;

    }

    private void sendCycle() {

        try {

            if (chatNetworkServicePluginRoot.getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection() != null) {

                if (chatNetworkServicePluginRoot.getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection().isConnected()) {

                    processSend();

                    //Sleep for a time
                    Thread.sleep(SLEEP_TIME);
                }
            }

        } catch (InterruptedException e) {

            reportUnexpectedError(FermatException.wrapException(e));
        } catch(Exception e) {
            status = AgentStatus.STOPPED;
            reportUnexpectedError(e);
        }

    }

    private void processSend() {

        try {

            List<ChatMetadataRecord> chatMetadataRecords = outgoingNotificationDAO.listRequestsByChatProtocolState(
                    ChatProtocolState.PROCESSING_SEND
            );

            for(ChatMetadataRecord chatMetadataRecord : chatMetadataRecords) {

                if (sendMessageToRemote(
                        EncodeMsjContent.encodeMSjContentChatMetadataTransmit(chatMetadataRecord, chatMetadataRecord.getLocalActorType(), chatMetadataRecord.getRemoteActorType()),
                        chatMetadataRecord.getLocalActorPublicKey(),
                        chatMetadataRecord.getLocalActorType(),
                        chatMetadataRecord.getRemoteActorPublicKey(),
                        chatMetadataRecord.getRemoteActorType()
                )) {
                    changeDoneState(chatMetadataRecord.getTransactionId(), ChatProtocolState.DONE);
                }

            }

        } catch(CantReadRecordDataBaseException |
                CantLoadTableToMemoryException
                e) {

            reportUnexpectedError(e);
        } catch (NotificationNotFoundException e) {
            reportUnexpectedError(e);
        } catch (CHTException e) {
            reportUnexpectedError(e);
        } catch (CantGetNotificationException e) {
            reportUnexpectedError(e);
        }
    }

    private boolean sendMessageToRemote(final String jsonMessage,
                                        final String senderPublicKey,
                                        final PlatformComponentType senderType,
                                        final String remotePublicKey,
                                        final PlatformComponentType remoteType) {

        try {

            if (!poolConnectionsWaitingForResponse.containsKey(remotePublicKey)) {

                if (chatNetworkServicePluginRoot.getCommunicationNetworkServiceConnectionManager().getNetworkServiceLocalInstance(remotePublicKey) == null) {


                    if (chatNetworkServicePluginRoot.getNetworkServiceProfile() != null) {

                        PlatformComponentProfile applicantParticipant = chatNetworkServicePluginRoot.getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection()
                                .constructBasicPlatformComponentProfileFactory(
                                        senderPublicKey,
                                        NetworkServiceType.CHAT,
                                        senderType);
                        PlatformComponentProfile remoteParticipant = chatNetworkServicePluginRoot.getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection()
                                .constructBasicPlatformComponentProfileFactory(
                                        remotePublicKey,
                                        NetworkServiceType.CHAT,
                                        remoteType);

                        chatNetworkServicePluginRoot.getCommunicationNetworkServiceConnectionManager().connectTo(
                                applicantParticipant,
                                chatNetworkServicePluginRoot.getNetworkServiceProfile(),
                                remoteParticipant
                        );

                        // i put the actor in the pool of connections waiting for response-
                        poolConnectionsWaitingForResponse.put(remotePublicKey, remotePublicKey);
                    }

                    return false;

                } else {

                    return sendMessage(senderPublicKey, remotePublicKey, jsonMessage);

                }
            } else {

                return sendMessage(senderPublicKey, remotePublicKey, jsonMessage);
            }


        } catch (Exception z) {

            reportUnexpectedError(FermatException.wrapException(z));
            return false;
        }
    }
    private boolean sendMessage(final String senderPublicKey,
                                final String remotePublicKey   ,
                                final String jsonMessage      ) {

        CommunicationNetworkServiceLocal communicationNetworkServiceLocal = chatNetworkServicePluginRoot.getCommunicationNetworkServiceConnectionManager().getNetworkServiceLocalInstance(remotePublicKey);

        if (communicationNetworkServiceLocal != null) {

            communicationNetworkServiceLocal.sendMessage(
                    senderPublicKey,
                    jsonMessage
            );

            poolConnectionsWaitingForResponse.remove(remotePublicKey);

            return true;
        }
        poolConnectionsWaitingForResponse.remove(remotePublicKey);
        return false;
    }

    private void changeDoneState(final UUID transactionID, ChatProtocolState chatProtocolState) throws NotificationNotFoundException, CHTException, CantGetNotificationException {

        outgoingNotificationDAO.changeChatProtocolState(transactionID, chatProtocolState);
    }

    private void reportUnexpectedError(final Exception e) {
        errorManager.reportUnexpectedPluginException(chatNetworkServicePluginRoot.getPluginVersionReference(), UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
    }

}