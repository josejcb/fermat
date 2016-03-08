package com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.DealsWithPluginIdentity;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Specialist;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.exceptions.CantConfirmTransactionException;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.exceptions.CantDeliverPendingTransactionsException;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.BroadcasterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.DealsWithLogger;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_cht_api.all_definition.agent.CHTTransactionAgent;
import com.bitdubai.fermat_cht_api.all_definition.enums.ChatStatus;
import com.bitdubai.fermat_cht_api.all_definition.enums.ContactStatus;
import com.bitdubai.fermat_cht_api.all_definition.enums.MessageStatus;
import com.bitdubai.fermat_cht_api.all_definition.enums.TypeMessage;
import com.bitdubai.fermat_cht_api.all_definition.events.enums.EventStatus;
import com.bitdubai.fermat_cht_api.all_definition.events.enums.EventType;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetChatException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetContactConnectionException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetContactException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantGetMessageException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantInitializeCHTAgent;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveChatException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveContactConnectionException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveContactException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSaveMessageException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSendChatMessageException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.ObjectNotSetException;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cht_api.all_definition.util.ObjectChecker;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Chat;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Contact;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.ContactConnection;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.Message;
import com.bitdubai.fermat_cht_api.layer.middleware.interfaces.MiddlewareChatManager;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.ChatImpl;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.ContactImpl;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.EventRecord;
import com.bitdubai.fermat_cht_api.layer.middleware.utils.MessageImpl;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.enums.ChatMessageStatus;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.enums.DistributionStatus;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.exceptions.CantSendChatMessageMetadataException;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.interfaces.ChatMetadata;
import com.bitdubai.fermat_cht_api.layer.network_service.chat.interfaces.NetworkServiceChatManager;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.ChatMiddlewarePluginRoot;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.database.ChatMiddlewareDatabaseConstants;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.database.ChatMiddlewareDatabaseDao;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.database.ChatMiddlewareDatabaseFactory;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.exceptions.CantGetPendingEventListException;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.exceptions.CantGetPendingTransactionException;
import com.bitdubai.fermat_cht_plugin.layer.middleware.chat.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.DealsWithEvents;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 08/01/16.
 */
public class ChatMiddlewareMonitorAgent implements
        CHTTransactionAgent,
        DealsWithLogger,
        DealsWithEvents,
        DealsWithErrors,
        DealsWithPluginDatabaseSystem,
        DealsWithPluginIdentity {

    Database database;
    MonitorAgent monitorAgent;
    Thread agentThread;
    LogManager logManager;
    EventManager eventManager;
    ErrorManager errorManager;
    PluginDatabaseSystem pluginDatabaseSystem;
    UUID pluginId;
    NetworkServiceChatManager chatNetworkServiceManager;
    MiddlewareChatManager chatMiddlewareManager;
    private final Broadcaster broadcaster;
    private PluginFileSystem pluginFileSystem;


    public ChatMiddlewareMonitorAgent(PluginDatabaseSystem pluginDatabaseSystem,
                                    LogManager logManager,
                                    ErrorManager errorManager,
                                    EventManager eventManager,
                                    UUID pluginId,
                                    NetworkServiceChatManager chatNetworkServiceManager, MiddlewareChatManager chatMiddlewareManager, Broadcaster broadcaster, PluginFileSystem pluginFileSystem) throws CantSetObjectException {
        this.eventManager = eventManager;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.errorManager = errorManager;
        this.pluginId = pluginId;
        this.logManager=logManager;
        this.chatNetworkServiceManager=chatNetworkServiceManager;
        this.chatMiddlewareManager = chatMiddlewareManager;
        this.broadcaster=broadcaster;
        this.pluginFileSystem = pluginFileSystem;
    }

    @Override
    public void start() throws CantStartAgentException {

        //Logger LOG = Logger.getGlobal();
        //LOG.info("Open contract monitor agent starting");
        monitorAgent = new MonitorAgent();

        this.monitorAgent.setPluginDatabaseSystem(this.pluginDatabaseSystem);
        this.monitorAgent.setErrorManager(this.errorManager);

        try {
            this.monitorAgent.Initialize();
        } catch (CantInitializeCHTAgent exception) {
            errorManager.reportUnexpectedPluginException(
                    Plugins.CHAT_MIDDLEWARE,
                    UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                    exception);
        }

        this.agentThread = new Thread(monitorAgent);
        this.agentThread.start();

    }

    @Override
    public void stop() {
        this.agentThread.interrupt();
    }

    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager=errorManager;
    }

    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager=eventManager;
    }

    @Override
    public void setLogManager(LogManager logManager) {
        this.logManager=logManager;
    }

    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem=pluginDatabaseSystem;
    }

    @Override
    public void setPluginId(UUID pluginId) {
        this.pluginId=pluginId;
    }

    /**
     * Private class which implements runnable and is started by the Agent
     * Based on MonitorAgent created by Rodrigo Acosta
     */
    private class MonitorAgent implements DealsWithPluginDatabaseSystem, DealsWithErrors, Runnable{

        ErrorManager errorManager;
        PluginDatabaseSystem pluginDatabaseSystem;
        public final int SLEEP_TIME = 5000;
        public final int DISCOVER_ITERATION_LIMIT = 1;
        public final String BROADCAST_CODE="13";
        int discoverIteration = 0;
        int iterationNumber = 0;
        ChatMiddlewareDatabaseDao chatMiddlewareDatabaseDao;
        boolean threadWorking;

        @Override
        public void setErrorManager(ErrorManager errorManager) {
            this.errorManager = errorManager;
        }

        @Override
        public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
            this.pluginDatabaseSystem = pluginDatabaseSystem;
        }
        @Override
        public void run() {

            threadWorking=true;
            //logManager.log(null,
            logManager.log(ChatMiddlewarePluginRoot.getLogLevelByClass(this.getClass().getName()),
                    "Chat Middleware Agent: running...", null, null);
            while(threadWorking){
                /**
                 * Increase the iteration counter
                 */
                iterationNumber++;
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException interruptedException) {
                    return;
                }

                /**
                 * now I will check if there are pending transactions to raise the event
                 */
                try {
                    //logManager.log(null,
                    logManager.log(ChatMiddlewarePluginRoot.getLogLevelByClass(this.getClass().getName()),
                            "Iteration number " + iterationNumber, null, null);
                    doTheMainTask();
                } catch (Exception e) {
                    errorManager.reportUnexpectedPluginException(
                            Plugins.CHAT_MIDDLEWARE,
                            UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,
                            e);
                }

            }

        }
        public void Initialize() throws CantInitializeCHTAgent {
            try {

                database = this.pluginDatabaseSystem.openDatabase(pluginId,
                        ChatMiddlewareDatabaseConstants.DATABASE_NAME);
            }
            catch (DatabaseNotFoundException databaseNotFoundException) {

                //Logger LOG = Logger.getGlobal();
                //LOG.info("Database in Open Contract monitor agent doesn't exists");
                ChatMiddlewareDatabaseFactory chatMiddlewareDatabaseFactory=
                        new ChatMiddlewareDatabaseFactory(this.pluginDatabaseSystem);
                try {
                    database = chatMiddlewareDatabaseFactory.createDatabase(pluginId,
                            ChatMiddlewareDatabaseConstants.DATABASE_NAME);
                } catch (CantCreateDatabaseException cantCreateDatabaseException) {
                    errorManager.reportUnexpectedPluginException(
                            Plugins.CHAT_MIDDLEWARE,
                            UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                            cantCreateDatabaseException);
                    throw new CantInitializeCHTAgent(cantCreateDatabaseException,
                            "Initialize Monitor Agent - trying to create the plugin database",
                            "Please, check the cause");
                }
            } catch (CantOpenDatabaseException exception) {
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        exception);
                throw new CantInitializeCHTAgent(exception,
                        "Initialize Monitor Agent - trying to open the plugin database",
                        "Please, check the cause");
            } catch (Exception exception){
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        FermatException.wrapException(exception));
                throw new CantInitializeCHTAgent(
                        FermatException.wrapException(exception),
                        "Initialize Monitor Agent - trying to open the plugin database",
                        "Unexpected exception");
            }
        }

        private void doTheMainTask() throws
                DatabaseOperationException,
                CantSendChatMessageException {

            //TODO: to implement
            try{
                /**
                 * Init the plugin database dao
                 */
                chatMiddlewareDatabaseDao = new ChatMiddlewareDatabaseDao(
                        pluginDatabaseSystem,
                        pluginId,
                        database,
                        errorManager,
                        pluginFileSystem);

                /**
                 * Discover contact
                 */
                List<ContactConnection> contactList;
                if(discoverIteration==0){
                    //increase counter
                    System.out.println("Chat Middleware discovery contact process "+discoverIteration+":");
                    contactList=chatMiddlewareManager.discoverActorsRegistered();
                    if(!contactList.isEmpty()){
                        for(ContactConnection contact : contactList){
                            saveContactConnection(contact);
                        }
                    }
                }
                discoverIteration++;
                if(discoverIteration==DISCOVER_ITERATION_LIMIT){
                    discoverIteration=0;
                }
                /**
                 * Check if pending messages to submit
                 */
                List<Message> createdMessagesList=chatMiddlewareDatabaseDao.getCreatedMessages();
                for(Message createdMessage : createdMessagesList){
                    sendMessage(createdMessage);
                }

                /**
                 * Check if pending events in database
                 */
                List<EventRecord> pendingEventList = chatMiddlewareDatabaseDao.getPendingEventList();
                EventType eventType;
                UUID chatId;
                for(EventRecord eventRecord : pendingEventList){
                    eventType=eventRecord.getEventType();
                    chatId=eventRecord.getChatId();
                    switch (eventType){
                        case INCOMING_CHAT:
                            checkIncomingChat(
                                    chatId,
                                    eventRecord);
                            break;
                        case OUTGOING_CHAT:
                            //TODO: TO IMPLEMENT
                            break;
                        case INCOMING_STATUS:
                            checkIncomingStatus(
                                    chatId,
                                    eventRecord);
                        default:
                            //TODO: THROW AN EXCEPTION
                            break;
                    }

                }
            } catch (UnexpectedResultReturnedFromDatabaseException e) {
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        e);
                throw new CantSendChatMessageException(
                        e,
                        "Executing Monitor Agent",
                        "Unexpected result in database"
                );
            } catch (CantGetPendingEventListException e) {
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        e);
                throw new CantSendChatMessageException(
                        e,
                        "Executing Monitor Agent",
                        "Cannot get the Pending event list"
                );
            }  catch (CantGetMessageException e) {
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        e);
                throw new CantSendChatMessageException(
                        e,
                        "Executing Monitor Agent",
                        "Cannot get the message"
                );
            } catch (CantGetPendingTransactionException e) {
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        e);
                throw new CantSendChatMessageException(
                        e,
                        "Executing Monitor Agent",
                        "Cannot get the pending transaction from Network Service plugin"
                );
            } catch (CantGetContactConnectionException e) {
                //For now, I'm gonna handle this print the exception and continue the thread
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        e);
                e.printStackTrace();
            } catch (CantSaveContactConnectionException e) {
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        e);
                throw new CantSendChatMessageException(
                        e,
                        "Executing Monitor Agent",
                        "Cannot save a new contact"
                );
            } catch (Exception exception){
                errorManager.reportUnexpectedPluginException(
                        Plugins.CHAT_MIDDLEWARE,
                        UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                        FermatException.wrapException(exception));
                throw new DatabaseOperationException(
                        DatabaseOperationException.DEFAULT_MESSAGE,
                        FermatException.wrapException(exception),
                        "Executing Monitor Agent",
                        "Unexpected exception");
            }


        }

        /**
         * This method saves a contact in database.
         * Also it checks if the Public key is already registered in database, in this case, updates
         *
         * @param contact
         * @throws CantSaveContactException
         * @throws DatabaseOperationException
         */
        private void saveContactConnection(ContactConnection contact) throws
                CantSaveContactConnectionException,
                DatabaseOperationException {
            try{
                ObjectChecker.checkArgument(contact, "The contact connection is null");
                String actorPublicKey=contact.getRemoteActorPublicKey();
                ContactConnection contactFromDatabase=
                        chatMiddlewareDatabaseDao.getContactConnectionByLocalPublicKey( //TODO:Modificar por un metodo getContactConnectionByLocalPublicKey
                                actorPublicKey);
                if(contactFromDatabase!=null){
                    //This contact already exists, so, I don't gonna save in database.
                    //TODO: I need to study if the contact must be updated.
                    return;
                }
                chatMiddlewareDatabaseDao.saveContactConnection(contact);
            } catch (ObjectNotSetException e) {
                throw new CantSaveContactConnectionException(
                        e,
                        "Saving the remote contact connection",
                        "The contact object is null");
            } catch (CantSaveContactException e) {
                throw new CantSaveContactConnectionException(
                        e,
                        "Saving the remote contact connection",
                        "Unexpected error in database");
            }
        }
        /**
         * This method checks the incoming chat event and acts according to this.
         * @param eventChatId
         * @throws CantGetPendingTransactionException
         */
        private void checkIncomingChat(UUID eventChatId, EventRecord eventRecord)
                throws CantGetPendingTransactionException,
                UnexpectedResultReturnedFromDatabaseException {
            try{
                List<Transaction<ChatMetadata>> pendingTransactionList=
                        chatNetworkServiceManager.getPendingTransactions(
                                Specialist.UNKNOWN_SPECIALIST);
                UUID incomingTransactionChatId;
                ChatMetadata incomingChatMetadata;
                if(pendingTransactionList==null){
                    /**
                     * In this version, when the NS return a null list, I'll ignore this this issue,
                     * I'll try later.
                     */
                    //throw new CantGetPendingTransactionException("The Network Service returns a null list");
                    System.out.println("CHAT MIDDLEWARE: The Network Service returns a null list");
                    return;
                }
                for(Transaction<ChatMetadata> pendingTransaction : pendingTransactionList){
                    incomingChatMetadata=pendingTransaction.getInformation();
                    incomingTransactionChatId=incomingChatMetadata.getChatId();
                    if(eventChatId.toString().equals(incomingTransactionChatId.toString())){
                        saveChat(incomingChatMetadata);
                        //If message exists in database, this message will be update
                        saveChat(incomingChatMetadata);
                        saveMessage(incomingChatMetadata);
                        chatNetworkServiceManager.confirmReception(pendingTransaction.getTransactionID());
                        //TODO TEST NOTIFICATION TO PIP
                        broadcaster.publish(BroadcasterType.UPDATE_VIEW, BROADCAST_CODE);
                      //  chatMiddlewareManager.notificationNewIncomingMessage(chatNetworkServiceManager.getNetWorkServicePublicKey(),"New Message",incomingChatMetadata.getMessage());
                      //This happen when recive a message check first the message sent from here and then the recive message
                       //when response some wrong with this code down here
                      /*  chatNetworkServiceManager.sendChatMessageNewStatusNotification(
                                chatNetworkServiceManager.getNetWorkServicePublicKey(),
                                PlatformComponentType.NETWORK_SERVICE,
                                incomingChatMetadata.getLocalActorPublicKey(),
                                PlatformComponentType.NETWORK_SERVICE,
                                DistributionStatus.DELIVERED,
                                incomingChatMetadata.getChatId(),
                                incomingChatMetadata.getMessageId()
                        );*/
             //           break;
                    }
                }
                eventRecord.setEventStatus(EventStatus.NOTIFIED);
                chatMiddlewareDatabaseDao.updateEventRecord(eventRecord);
            } catch (CantDeliverPendingTransactionsException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot get the pending transaction from Network Service plugin"
                );
            } catch (DatabaseOperationException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Unexpected error in database operation"
                );
            } catch (CantSaveMessageException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot save message from database"
                );
            } catch (CantGetMessageException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot get the message from database"
                );
            /*}
            catch (CantSendChatMessageNewStatusNotificationException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot send the message to TX"
                );*/
            } catch (CantConfirmTransactionException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot get confirm the reception to local NS"
                );
//            } catch (CantSendNotificationNewIncomingMessageException e) {
//                throw new CantGetPendingTransactionException(
//                        e,
//                        "Checking the incoming chat pending transactions",
//                        "Cannot send Notification to PIP"
//                );
            } catch (CantGetChatException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot get chat"
                );
            } catch (CantSaveChatException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming chat pending transactions",
                        "Cannot save chat"
                );
            }

        }

        /**
         * This method checks the incoming status event and acts according to this.
         * @param eventChatId
         * @throws CantGetPendingTransactionException
         */
        private void checkIncomingStatus(
                UUID eventChatId,
                EventRecord eventRecord) throws
                CantGetPendingTransactionException,
                UnexpectedResultReturnedFromDatabaseException {
            try{
                List<Transaction<ChatMetadata>> pendingTransactionList=
                        chatNetworkServiceManager.getPendingTransactions(
                                Specialist.UNKNOWN_SPECIALIST);
                UUID incomingTransactionChatId;
                ChatMetadata incomingChatMetadata;
                if(pendingTransactionList==null){
                    throw new CantGetPendingTransactionException("The Network Service returns a null list");
                }
                for(Transaction<ChatMetadata> pendingTransaction : pendingTransactionList){
                    incomingChatMetadata=pendingTransaction.getInformation();
                    incomingTransactionChatId=incomingChatMetadata.getChatId();
                    if(eventChatId.toString().equals(incomingTransactionChatId.toString())){
                        //Check if metadata exists in database
                        checkChatMetadata(incomingChatMetadata);
                        updateMessageStatus(incomingChatMetadata);
                        chatNetworkServiceManager.confirmReception(pendingTransaction.getTransactionID());
                        break;
                    }
                }
                eventRecord.setEventStatus(EventStatus.NOTIFIED);
                chatMiddlewareDatabaseDao.updateEventRecord(eventRecord);
            } catch (CantDeliverPendingTransactionsException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming status pending transactions",
                        "Cannot get the pending transaction from Network Service plugin"
                );
            } catch (CantGetChatException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming status pending transactions",
                        "Cannot get the chat from database"
                );
            } catch (DatabaseOperationException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming status pending transactions",
                        "Unexpected error in database operation"
                );
            }  catch (CantGetMessageException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming status pending transactions",
                        "Cannot get the message from database"
                );
            } catch (CantSaveMessageException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming status pending transactions",
                        "Cannot update message from database"
                );
            } catch (CantConfirmTransactionException e) {
                throw new CantGetPendingTransactionException(
                        e,
                        "Checking the incoming status pending transactions",
                        "Cannot get confirm the reception to local NS"
                );
            }
        }

        /**
         * This method returns true if the chat and the message exists in database.
         * This throws an exception instead return false if any element does not exists in database
         * because this is not should happen.
         * @param chatMetadata
         * @return
         * @throws CantGetChatException
         * @throws CantGetPendingTransactionException
         */
        private boolean checkChatMetadata(ChatMetadata chatMetadata) throws
                CantGetChatException,
                CantGetPendingTransactionException {
            UUID chatId=chatMetadata.getChatId();
            UUID messageId;
            if(chatMiddlewareDatabaseDao.chatIdExists(
                    chatId)){
                messageId=chatMetadata.getMessageId();
                if(chatMiddlewareDatabaseDao.messageIdExists(messageId)){
                    return true;
                }else{
                    //TODO: I need to study how can I handle this case.
                    throw new CantGetPendingTransactionException(
                            "The Message Id "+messageId+" does not exists in database");
                }
            }else{
                //This is a case that in this version I cannot handle
                throw new CantGetPendingTransactionException(
                        "The Chat Id "+chatId+" does not exists in database");
            }
        }

        /**
         * This method saves a new message in database
         * @param chatMetadata
         * @throws DatabaseOperationException
         * @throws CantSaveMessageException
         * @throws CantGetMessageException
         */
        private void saveMessage(
                ChatMetadata chatMetadata) throws
                DatabaseOperationException,
                CantSaveMessageException,
                CantGetMessageException {
            UUID messageId=chatMetadata.getMessageId();
            Message messageRecorded=chatMiddlewareDatabaseDao.getMessageByMessageId(messageId);
            if(messageRecorded==null){
                /**
                 * In this case, the message is not created in database, so, is an incoming message,
                 * I need to create a new message
                 */
                messageRecorded=getMessageFromChatMetadata(
                        chatMetadata);
                if(messageRecorded==null) return;
            }

            messageRecorded.setStatus(MessageStatus.RECEIVE);
            chatMiddlewareDatabaseDao.saveMessage(messageRecorded);
        }

        /**
         * This method saves the new chat in database
         * @param chatMetadata
         * @throws DatabaseOperationException
         */
        private void saveChat(ChatMetadata chatMetadata) throws DatabaseOperationException, CantGetChatException, CantSaveChatException {
            String remotepk="";
            String localpk="";
      //      String newmessage="gone";
/*            try {
                for (int i = 0; i < chatMiddlewareDatabaseDao.getMessages().size(); i++) {
                    if (chatMiddlewareDatabaseDao.getMessageByChatId(chatMetadata.getChatId()).get(i).getMessage().equals("troy")) {
                        newmessage = "probe";
                    }
                }
            }catch(CantGetMessageException e){
            }*/
            Chat chat = chatMiddlewareDatabaseDao.getChatByChatId(chatMetadata.getChatId());

            // change to put in the remote device in the correct place of table chat
            if(chat == null){
                chat = getChatFromChatMetadata(chatMetadata);
                remotepk=chat.getRemoteActorPublicKey();
                localpk=chat.getLocalActorPublicKey();
                chat.setLocalActorPublicKey(remotepk);
                chat.setRemoteActorPublicKey(localpk);
            }else{
                remotepk=chatMetadata.getRemoteActorPublicKey();
                if(!remotepk.equals(chat.getRemoteActorPublicKey())) {
                    chat.setLocalActorPublicKey(remotepk);
                }
            }
            chat.setLastMessageDate(new Timestamp(System.currentTimeMillis()));//updating date of last message arrived in chat
            chat.setStatus(ChatStatus.VISSIBLE);

            chatMiddlewareDatabaseDao.saveChat(chat);
        }

        /**
         * This method add a new contact to the incoming chat
         * @param chat
         * @param contact
         * @return
         */
        private Chat addContactToChat(
                Chat chat,
                Contact contact){
            List<Contact> contactList=chat.getContactAssociated();
            if(contactList==null){
                contactList=new ArrayList<>();
                contactList.add(contact);
            }else {
                int contactIndex=contactList.indexOf(contact);
                if(contactIndex==-1){
                    contactList.add(contact);
                }
                //If the contact exists in chat object, I'll pass to include in chat
            }
            chat.setContactAssociated(contactList);
            return chat;
        }

        /**
         * This method creates a new Message from incoming metadata
         * @param chatMetadata
         * @return
         */
        private Message getMessageFromChatMetadata(ChatMetadata chatMetadata)
                throws
                CantGetMessageException{
            if(chatMetadata==null){
                throw new CantGetMessageException("The chat metadata from network service is null");
            }
            try{
                UUID chatId=chatMetadata.getChatId();
                Chat chatFromDatabase=chatMiddlewareDatabaseDao.getChatByChatId(chatId);
                String contactLocalPublicKey=chatFromDatabase.getRemoteActorPublicKey();
                Contact contact=chatMiddlewareDatabaseDao.getContactByLocalPublicKey(contactLocalPublicKey);
                if(contact==null){
                    contact = createUnregisteredContact(chatMetadata);
                    if (contact==null) return null;
                }

                //I'll associated the contact, message and chat with the following method
                addContactToChat(chatFromDatabase, contact);
                UUID contactId=contact.getContactId();
                Message message=new MessageImpl(
                        chatMetadata,
                        MessageStatus.CREATED,
                        TypeMessage.INCOMMING,
                        contactId
                        );
                return message;
            } catch (DatabaseOperationException e) {
                throw new CantGetMessageException(e,
                        "Getting message from ChatMetadata",
                        "Unexpected exception in database");
            } catch (CantGetContactException e) {
                throw new CantGetMessageException(e,
                        "Getting message from ChatMetadata",
                        "Cannot get the contact");
            } catch (CantSaveContactException e) {
                throw new CantGetMessageException(e,
                        "Getting message from ChatMetadata",
                        "Cannot save the contact");

            } catch (CantGetChatException e) {
                throw new CantGetMessageException(e,
                        "Getting message from ChatMetadata",
                        "Cannot get the chat");
            }

        }

        /**
         * This method creates and saves a new contact.
         * @param chatMetadata
         * @return
         * @throws CantSaveContactException
         * @throws DatabaseOperationException
         */
        private Contact createUnregisteredContact(
                ChatMetadata chatMetadata) throws
                CantSaveContactException,
                DatabaseOperationException, CantGetContactException {
            Date date=new Date();

            //Se trae de la tabla Contact Connection para forzarlo a guardar el contacto no registrado
            ContactConnection contactConnection;
            contactConnection = chatMiddlewareDatabaseDao.getContactConnectionByLocalPublicKey(chatMetadata.getLocalActorPublicKey());
            if (contactConnection==null) return null;

                Contact contact = new ContactImpl(
                        UUID.randomUUID(),
                        contactConnection.getRemoteName(),
                        contactConnection.getAlias(),
                        contactConnection.getRemoteActorType(),
                        chatMetadata.getLocalActorPublicKey(),
                        date.getTime(),
                        contactConnection.getProfileImage(),
                        contactConnection.getContactStatus()
                );
                chatMiddlewareDatabaseDao.saveContact(contact);

            return contact;
        }

        /**
         * THis Method creates a new Chat from incoming Metadata
         * @param chatMetadata
         * @return
         */
        private Chat getChatFromChatMetadata(ChatMetadata chatMetadata){
            return new ChatImpl(
                    chatMetadata.getChatId(),
                    chatMetadata.getObjectId(),
                    chatMetadata.getLocalActorType(),
                    chatMetadata.getLocalActorPublicKey(),
                    chatMetadata.getRemoteActorType(),
                    chatMetadata.getRemoteActorPublicKey(),
                    chatMetadata.getChatName(),
                    ChatStatus.VISSIBLE,
                    chatMetadata.getDate(),
                    chatMetadata.getDate()
            );
        }
        /**
         * This method updates a message record in database.
         * @param chatMetadata
         * @throws DatabaseOperationException
         * @throws CantSaveMessageException
         * @throws CantGetMessageException
         */
        private void updateMessageStatus(
                ChatMetadata chatMetadata) throws
                DatabaseOperationException,
                CantSaveMessageException,
                CantGetMessageException {
            UUID messageId=chatMetadata.getMessageId();
            Message messageRecorded=chatMiddlewareDatabaseDao.getMessageByMessageId(messageId);
            if(messageRecorded==null){
                /**
                 * In this case, the message is not created in database, so, is an incoming message,
                 * I need to create a new message
                 */
                messageRecorded=getMessageFromChatMetadata(
                        chatMetadata);
                if(messageRecorded==null) return;
            }
            messageRecorded.setStatus(chatMetadata.getMessageStatus());
            chatMiddlewareDatabaseDao.saveMessage(messageRecorded);
        }

        /**
         * This method sends the message through the Chat Network Service
         * @param createdMessage
         * @throws CantSendChatMessageException
         */
        private void sendMessage(Message createdMessage) throws CantSendChatMessageException {
            try{
                UUID chatId=createdMessage.getChatId();
                Chat chat=chatMiddlewareDatabaseDao.getChatByChatId(chatId);
                if(chat==null){
                    return;
                }
                String localActorPublicKey=chat.getLocalActorPublicKey();
                String remoteActorPublicKey=chat.getRemoteActorPublicKey();
                ChatMetadata chatMetadata=constructChatMetadata(
                        chat,
                        createdMessage
                );
                System.out.println("ChatMetadata to send:\n" + chatMetadata);
                try{
                    chatNetworkServiceManager.sendChatMetadata(
                            localActorPublicKey,
                            remoteActorPublicKey,
                            chatMetadata
                    );
                    createdMessage.setStatus(MessageStatus.SEND);
                } catch (IllegalArgumentException e) {
                    /**
                     * In this case, any argument in chat or message was null or not properly set.
                     * I'm gonna change the status to CANNOT_SEND to avoid send this message.
                     */
                    createdMessage.setStatus(MessageStatus.CANNOT_SEND);
                }

                chatMiddlewareDatabaseDao.saveMessage(createdMessage);
            } catch (DatabaseOperationException e) {
                throw new CantSendChatMessageException(
                        e,
                        "Sending a message",
                        "Unexpected error in database"
                );
            } catch (CantGetChatException e) {
                throw new CantSendChatMessageException(
                        e,
                        "Sending a message",
                        "Cannot get the chat"
                );
            } catch (CantSendChatMessageMetadataException e) {
                throw new CantSendChatMessageException(
                        e,
                        "Sending a message",
                        "Cannot send the ChatMetadata"
                );
            } catch (CantSaveMessageException e) {
                throw new CantSendChatMessageException(
                        e,
                        "Sending a message",
                        "Cannot save the message"
                );
            }

        }

        /**
         * This method return a ChatMetadata from a Chat and Message objects.
         * @param chat
         * @param message
         * @return
         */
        private ChatMetadata constructChatMetadata(
                Chat chat,
                Message message){
            Timestamp timestamp=new Timestamp(chat.getDate().getTime());
            ChatMetadata chatMetadata=new ChatMetadataRecord(
                    chat.getChatId(),
                    chat.getObjectId(),
                    chat.getLocalActorType(),
                    chat.getLocalActorPublicKey(),
                    chat.getRemoteActorType(),
                    chat.getRemoteActorPublicKey(),
                    chat.getChatName(),
                    ChatMessageStatus.READ_CHAT,
                    MessageStatus.SEND,
                    timestamp,
                    message.getMessageId(),
                    message.getMessage(),
                    DistributionStatus.OUTGOING_MSG
            );
            return chatMetadata;
        }

    }

}

