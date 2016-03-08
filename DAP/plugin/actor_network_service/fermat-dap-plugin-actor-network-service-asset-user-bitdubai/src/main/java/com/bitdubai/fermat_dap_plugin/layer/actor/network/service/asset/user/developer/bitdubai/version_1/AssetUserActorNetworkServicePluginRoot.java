/*
 * @#AssetTransmissionPluginRoot.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1;

import android.util.Base64;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.DiscoveryQueryParameters;
import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.developer.LogManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.all_definition.network_service.interfaces.NetworkServiceConnectionManager;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogLevel;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPMessageSubject;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPMessageType;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventType;
import com.bitdubai.fermat_dap_api.layer.all_definition.events.ActorAssetNetworkServicePendingNotificationEvent;
import com.bitdubai.fermat_dap_api.layer.all_definition.events.ActorAssetUserCompleteRegistrationNotificationEvent;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.DAPMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantGetDAPMessagesException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantSendMessageException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantUpdateMessageStatusException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.AssetUserActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantAcceptConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantAskConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantCancelConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantConfirmActorAssetNotificationException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantDenyConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantDisconnectConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_user.exceptions.CantRegisterActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_user.exceptions.CantRequestListActorAssetUserRegisteredException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_user.interfaces.AssetUserActorNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.enums.ActorAssetProtocolState;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.enums.AssetNotificationDescriptor;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantCreateActorAssetNotificationException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantGetActorAssetNotificationException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.interfaces.ActorNotification;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.agents.AssetUserActorNetworkServiceAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.communications.CommunicationNetworkServiceConnectionManager;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.communications.CommunicationRegistrationProcessNetworkServiceAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.database.communications.AssetUserNetworkServiceDatabaseConstants;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.database.communications.AssetUserNetworkServiceDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.database.communications.AssetUserNetworkServiceDeveloperDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.database.communications.IncomingNotificationDao;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.database.communications.OutgoingNotificationDao;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.ClientConnectionCloseNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.ClientConnectionLooseNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.ClientSuccessfullReconnectNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.CompleteComponentConnectionRequestNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.CompleteComponentRegistrationNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.CompleteRequestListComponentRegisteredNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.event_handlers.VPNConnectionCloseNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.exceptions.CantInitializeTemplateNetworkServiceDatabaseException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantUpdateRecordDataBaseException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.ActorAssetNetworkServiceRecord;
import com.bitdubai.fermat_p2p_api.layer.all_definition.common.network_services.abstract_classes.AbstractNetworkService;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatMessageCommunication;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.ClientConnectionCloseNotificationEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.VPNConnectionCloseNotificationEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.data_base.CommunicationNetworkServiceDatabaseConstants;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.MessagesStatus;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.WsCommunicationsCloudClientManager;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.client.CommunicationsClientConnection;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatMessage;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatMessagesStatus;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.exceptions.CantRegisterComponentException;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.exceptions.CantRequestListException;
import com.bitdubai.fermat_pip_api.layer.actor.exception.CantGetLogTool;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * The Class <code>com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.AssetUserActorNetworkServicePluginRoot</code> is
 * throw when error occurred updating new record in a table of the data base
 * <p/>
 * Created by Hendry Rodriguez - (elnegroevaristo@gmail.com) on 07/10/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class AssetUserActorNetworkServicePluginRoot extends AbstractNetworkService implements
        AssetUserActorNetworkServiceManager,
        DatabaseManagerForDevelopers,
        LogManagerForDevelopers {


    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    protected PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.LOG_MANAGER)
    private LogManager logManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    private ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;

    @NeededPluginReference(platform = Platforms.COMMUNICATION_PLATFORM, layer = Layers.COMMUNICATION, plugin = Plugins.WS_CLOUD_CLIENT)
    private WsCommunicationsCloudClientManager wsCommunicationsCloudClientManager;

    /**
     * Represent the dataBase
     */
    private Database dataBase;

    /**
     * Represent the EVENT_SOURCE
     */
    public final static EventSource EVENT_SOURCE = EventSource.NETWORK_SERVICE_ACTOR_ASSET_USER;

    protected final static String DAP_IMG_USER = "DAP_IMG_USER";

    private List<FermatEventListener> listenersAdded = new ArrayList<>();

    /**
     * DealsWithLogger interface member variable
     */
    static Map<String, LogLevel> newLoggingLevel = new HashMap<>();

    /**
     * Represent the communicationNetworkServiceConnectionManager
     */
    private CommunicationNetworkServiceConnectionManager communicationNetworkServiceConnectionManager;

    /**
     * Represent the remoteNetworkServicesRegisteredList
     */
    private List<PlatformComponentProfile> remoteNetworkServicesRegisteredList;

    /**
     * Represent the actorAssetUserPendingToRegistration
     */
    private List<PlatformComponentProfile> actorAssetUserPendingToRegistration;

    /**
     * Represent the communicationNetworkServiceDeveloperDatabaseFactory
     */
    private AssetUserNetworkServiceDeveloperDatabaseFactory communicationNetworkServiceDeveloperDatabaseFactory;

    /**
     * Represent the communicationRegistrationProcessNetworkServiceAgent
     */
    private CommunicationRegistrationProcessNetworkServiceAgent communicationRegistrationProcessNetworkServiceAgent;

    /**
     * Represent the actorAssetUserRegisteredList
     */
    private List<ActorAssetUser> actorAssetUserRegisteredList;

    private AssetUserActorNetworkServiceAgent assetUserActorNetworkServiceAgent;

    private int createactorAgentnum = 0;

    /**
     * DAO
     */
    private IncomingNotificationDao incomingNotificationsDao;
    private OutgoingNotificationDao outgoingNotificationDao;

    /**
     * Represent the flag to start only once
     */
    private AtomicBoolean flag = new AtomicBoolean(false);

    private long reprocessTimer =  300000; //five minutes

    private Timer timer = new Timer();
    /**
     * Executor
     */
    ExecutorService executorService;

    /**
     * Constructor
     */
    public AssetUserActorNetworkServicePluginRoot() {
        super(
                new PluginVersionReference(new Version()),
                PlatformComponentType.NETWORK_SERVICE,
                NetworkServiceType.ASSET_USER_ACTOR,
                "Actor Network Service Asset User",
                "ActorNetworkServiceAssetUser",
                null,
                EventSource.NETWORK_SERVICE_ACTOR_ASSET_USER
        );
        this.listenersAdded = new ArrayList<>();

        this.actorAssetUserRegisteredList = new ArrayList<>();
        this.actorAssetUserPendingToRegistration = new ArrayList<>();
    }

    /**
     * Get the IdentityPublicKey
     *
     * @return String
     */
    public String getIdentityPublicKey() {
        return this.identity.getPublicKey();
    }

    /**
     * This method initialize the communicationNetworkServiceConnectionManager.
     * IMPORTANT: Call this method only in the CommunicationRegistrationProcessNetworkServiceAgent, when execute the registration process
     * because at this moment, is create the platformComponentProfile for this component
     */
    public void initializeCommunicationNetworkServiceConnectionManager() {
        this.communicationNetworkServiceConnectionManager = new CommunicationNetworkServiceConnectionManager(
                getPlatformComponentProfilePluginRoot(),
                identity,
                wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection(),
                dataBase,
                errorManager,
                eventManager);
    }

    @Override
    public List<String> getClassesFullPath() {
        List<String> returnedClasses = new ArrayList<String>();
        returnedClasses.add("com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.AssetUserActorNetworkServicePluginRoot");
        returnedClasses.add("com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.communications.CommunicationRegistrationProcessNetworkServiceAgent");
        returnedClasses.add("com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.communications.CommunicationNetworkServiceLocal");
        returnedClasses.add("com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.communications.CommunicationNetworkServiceConnectionManager");
        returnedClasses.add("com.bitdubai.fermat_dap_plugin.layer.actor.network.service.asset.user.developer.bitdubai.version_1.communications.CommunicationNetworkServiceRemoteAgent");
        return returnedClasses;
    }

    @Override
    public void setLoggingLevelPerClass(Map<String, LogLevel> newLoggingLevel) {
        /**
         * Modify by Manuel on 25/07/2015
         * I will wrap all this method within a try, I need to catch any generic java Exception
         */
        try {
            /**
             * I will check the current values and update the LogLevel in those which is different
             */
            for (Map.Entry<String, LogLevel> pluginPair : newLoggingLevel.entrySet()) {
                /**
                 * if this path already exists in the Root.bewLoggingLevel I'll update the value, else, I will put as new
                 */
                if (AssetUserActorNetworkServicePluginRoot.newLoggingLevel.containsKey(pluginPair.getKey())) {
                    AssetUserActorNetworkServicePluginRoot.newLoggingLevel.remove(pluginPair.getKey());
                    AssetUserActorNetworkServicePluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
                } else {
                    AssetUserActorNetworkServicePluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
                }
            }
        } catch (Exception exception) {
            FermatException e = new CantGetLogTool(CantGetLogTool.DEFAULT_MESSAGE, FermatException.wrapException(exception), "setLoggingLevelPerClass: " + AssetUserActorNetworkServicePluginRoot.newLoggingLevel, "Check the cause");
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }

    @Override
    public synchronized void start() throws CantStartPluginException {

        if (!flag.getAndSet(true)) {
            if (this.serviceStatus != ServiceStatus.STARTING) {
                serviceStatus = ServiceStatus.STARTING;

                System.out.println("Start Plugin Asset User ActorNetworkService");
                logManager.log(AssetUserActorNetworkServicePluginRoot.getLogLevelByClass(this.getClass().getName()), "AssetUserActorNetworkService - Starting", "AssetUserActorNetworkServicePluginRoot - Starting", "AssetUserActorNetworkServicePluginRoot - Starting");

        /*
         * Validate required resources
         */
                validateInjectedResources();

                try {
        /*
         * Create a new key pair for this execution
         */
                    initializeClientIdentity();
           /*
            * Initialize the data base
            */
                    initializeDb();

            /*
             * Initialize Developer Database Factory
             */
                    communicationNetworkServiceDeveloperDatabaseFactory = new AssetUserNetworkServiceDeveloperDatabaseFactory(pluginDatabaseSystem, pluginId);
                    communicationNetworkServiceDeveloperDatabaseFactory.initializeDatabase();

                    //DAO
                    incomingNotificationsDao = new IncomingNotificationDao(dataBase, this.pluginFileSystem, this.pluginId);

                    outgoingNotificationDao = new OutgoingNotificationDao(dataBase, this.pluginFileSystem, this.pluginId);

                    executorService = Executors.newFixedThreadPool(3);

                    // change message state to process again first time
                    reprocessMessages();

                    //declare a schedule to process waiting request message

                    this.startTimer();
            /*
             * Initialize listeners
             */
                    initializeListener();

            /*
             * Initialize connection manager
             */
                    initializeCommunicationNetworkServiceConnectionManager();

            /*
             * Verify if the communication cloud client is active
             */
                    if (!wsCommunicationsCloudClientManager.isDisable()) {

                /*
                 * Construct my profile and register me
                 */
                        PlatformComponentProfile platformComponentProfilePluginRoot = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructPlatformComponentProfileFactory(getIdentityPublicKey(),
                                getAlias().toLowerCase(),
                                getName(),
                                getNetworkServiceType(),
                                getPlatformComponentType(),
                                getExtraData());

                        setPlatformComponentProfilePluginRoot(platformComponentProfilePluginRoot);

                /*
                 * Initialize the agent and start
                 */
                        communicationRegistrationProcessNetworkServiceAgent = new CommunicationRegistrationProcessNetworkServiceAgent(this, wsCommunicationsCloudClientManager);
                        communicationRegistrationProcessNetworkServiceAgent.start();
                    }

            /*
             * Its all ok, set the new status
            */
                    this.serviceStatus = ServiceStatus.STARTED;

                } catch (CantInitializeTemplateNetworkServiceDatabaseException exception) {

                    StringBuffer contextBuffer = new StringBuffer();
                    contextBuffer.append("Plugin ID: " + pluginId);
                    contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
                    contextBuffer.append("Database Name: " + AssetUserNetworkServiceDatabaseConstants.DATA_BASE_NAME);

                    String context = contextBuffer.toString();
                    String possibleCause = "The Asset User Actor Network Service Database triggered an unexpected problem that wasn't able to solve by itself";
                    CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, exception, context, possibleCause);

                    errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

                    throw pluginStartException;
                }
            }
        }
    }

    private void initializeClientIdentity() throws CantStartPluginException {

        System.out.println("Calling the method - initializeClientIdentity() ");

        try {

            System.out.println("Loading clientIdentity");

             /*
              * Load the file with the clientIdentity
              */
            PluginTextFile pluginTextFile = pluginFileSystem.getTextFile(pluginId, "private", "clientIdentity", FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
            String content = pluginTextFile.getContent();

            //System.out.println("content = "+content);

            identity = new ECCKeyPair(content);

        } catch (FileNotFoundException e) {

            /*
             * The file no exist may be the first time the plugin is running on this device,
             * We need to create the new clientIdentity
             */
            try {

                System.out.println("No previous clientIdentity finder - Proceed to create new one");

                /*
                 * Create the new clientIdentity
                 */
                identity = new ECCKeyPair();

                /*
                 * save into the file
                 */
                PluginTextFile pluginTextFile = pluginFileSystem.createTextFile(pluginId, "private", "clientIdentity", FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);
                pluginTextFile.setContent(identity.getPrivateKey());
                pluginTextFile.persistToMedia();

            } catch (Exception exception) {
                /*
                 * The file cannot be created. I can not handle this situation.
                 */
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, exception);
                throw new CantStartPluginException(exception.getLocalizedMessage());
            }


        } catch (CantCreateFileException cantCreateFileException) {

            /*
             * The file cannot be load. I can not handle this situation.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantCreateFileException);
            throw new CantStartPluginException(cantCreateFileException.getLocalizedMessage());

        }

    }

    @Override
    public void pause() {
        /*
         * Pause
         */
        communicationNetworkServiceConnectionManager.pause();

        /*
         * Set the new status
         */
        this.serviceStatus = ServiceStatus.PAUSED;
    }

    @Override
    public void resume() {
        /*
         * resume the managers
         */
        communicationNetworkServiceConnectionManager.resume();

        /*
         * Set the new status
         */
        this.serviceStatus = ServiceStatus.STARTED;
    }

    @Override
    public void stop() {
        //Clear all references of the event listeners registered with the event manager.
        listenersAdded.clear();

        /*
         * Stop all connection on the managers
         */
        communicationNetworkServiceConnectionManager.closeAllConnection();

        //set to not register
        register = Boolean.FALSE;

        /*
         * Set the new status
         */
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    private void initializeActorAssetUserAgent() {
        try {
            assetUserActorNetworkServiceAgent = new AssetUserActorNetworkServiceAgent(this, dataBase);
//                    this,
//                    wsCommunicationsCloudClientManager,
//                    communicationNetworkServiceConnectionManager,
//                    getPlatformComponentProfilePluginRoot(),
//                    errorManager,
//                    identity,
//                    dataBase);
            assetUserActorNetworkServiceAgent.start();

        } catch (CantStartAgentException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
        }
    }

    @Override
    public void registerActorAssetUser(ActorAssetUser actorAssetUserToRegister) throws CantRegisterActorAssetUserException {

        try {
            final CommunicationsClientConnection communicationsClientConnection = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection();
            /*
             * If register
             */
            if (true) {
                /*
                 * Construct the profile
                 */
                Gson gson = new Gson();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(DAP_IMG_USER, Base64.encodeToString(actorAssetUserToRegister.getProfileImage(), Base64.DEFAULT));
                String extraData = gson.toJson(jsonObject);

                final PlatformComponentProfile platformComponentProfileAssetUser = communicationsClientConnection.constructPlatformComponentProfileFactory(
                        actorAssetUserToRegister.getActorPublicKey(),
                        actorAssetUserToRegister.getName().toLowerCase().trim(),
                        actorAssetUserToRegister.getName(),
                        NetworkServiceType.UNDEFINED,
                        PlatformComponentType.ACTOR_ASSET_USER,
                        extraData);
                /*
                 * ask to the communication cloud client to register
                 */
                /**
                 * I need to add this in a new thread other than the main android thread
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            communicationsClientConnection.registerComponentForCommunication(getNetworkServiceType(), platformComponentProfileAssetUser);
                        } catch (CantRegisterComponentException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            } else {
                /*
                 * Construct the profile
                 */
                Gson gson = new Gson();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(DAP_IMG_USER, Base64.encodeToString(actorAssetUserToRegister.getProfileImage(), Base64.DEFAULT));
                String extraData = gson.toJson(jsonObject);

                PlatformComponentProfile platformComponentProfileAssetUser = communicationsClientConnection.constructPlatformComponentProfileFactory(
                        actorAssetUserToRegister.getActorPublicKey(),
                        actorAssetUserToRegister.getName().toLowerCase().trim(),
                        actorAssetUserToRegister.getName(),
                        NetworkServiceType.UNDEFINED,
                        PlatformComponentType.ACTOR_ASSET_USER,
                        extraData);
                /*
                 * Add to the list of pending to register
                 */
                actorAssetUserPendingToRegistration.add(platformComponentProfileAssetUser);
            }
        } catch (Exception e) {
            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Plugin was not registered";

            CantRegisterActorAssetUserException pluginStartException = new CantRegisterActorAssetUserException(CantStartPluginException.DEFAULT_MESSAGE, e, context, possibleCause);
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
    }

    @Override
    public void updateActorAssetUser(ActorAssetUser actorAssetUserToRegister) throws CantRegisterActorAssetUserException {

        try {
            final CommunicationsClientConnection communicationsClientConnection = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection();
            /*
             * If register
             */
            if (true) {
                /*
                 * Construct the profile
                 */
                Gson gson = new Gson();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(DAP_IMG_USER, Base64.encodeToString(actorAssetUserToRegister.getProfileImage(), Base64.DEFAULT));
                String extraData = gson.toJson(jsonObject);

                final PlatformComponentProfile platformComponentProfileAssetUser = communicationsClientConnection.constructPlatformComponentProfileFactory(
                        actorAssetUserToRegister.getActorPublicKey(),
                        actorAssetUserToRegister.getName().toLowerCase().trim(),
                        actorAssetUserToRegister.getName(),
                        NetworkServiceType.UNDEFINED,
                        PlatformComponentType.ACTOR_ASSET_USER,
                        extraData);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            communicationsClientConnection.updateRegisterActorProfile(getNetworkServiceType(), platformComponentProfileAssetUser);
                        } catch (CantRegisterComponentException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
//            else {
//                /*
//                 * Construct the profile
//                 */
//                Gson gson = new Gson();
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty(DAP_IMG_USER, Base64.encodeToString(actorAssetUserToRegister.getProfileImage(), Base64.DEFAULT));
//                String extraData = gson.toJson(jsonObject);
//
//                PlatformComponentProfile platformComponentProfileAssetUser = communicationsClientConnection.constructPlatformComponentProfileFactory(
//                        actorAssetUserToRegister.getActorPublicKey(),
//                        actorAssetUserToRegister.getName().toLowerCase().trim(),
//                        actorAssetUserToRegister.getName(),
//                        NetworkServiceType.UNDEFINED,
//                        PlatformComponentType.ACTOR_ASSET_USER,
//                        extraData);
//                /*
//                 * Add to the list of pending to register
//                 */
//                actorAssetUserPendingToRegistration.add(platformComponentProfileAssetUser);
//        }
        } catch (Exception e) {
            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Plugin was not registered";

            CantRegisterActorAssetUserException pluginStartException = new CantRegisterActorAssetUserException(CantStartPluginException.DEFAULT_MESSAGE, e, context, possibleCause);
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
    }

    @Override
    public List<ActorAssetUser> getListActorAssetUserRegistered() throws CantRequestListActorAssetUserRegisteredException {

        try {
            // if (true) {
            if (actorAssetUserRegisteredList != null && !actorAssetUserRegisteredList.isEmpty()) {
                actorAssetUserRegisteredList.clear();
            }
            DiscoveryQueryParameters discoveryQueryParametersAssetUser = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                    constructDiscoveryQueryParamsFactory(PlatformComponentType.ACTOR_ASSET_USER, //applicant = who made the request
                            NetworkServiceType.UNDEFINED,
                            null,                     // alias
                            null,                     // identityPublicKey
                            null,                     // location
                            null,                     // distance
                            null,                     // name
                            null,                     // extraData
                            null,                     // offset
                            null,                     // max
                            null,                     // fromOtherPlatformComponentType, when use this filter apply the identityPublicKey
                            null);

            List<PlatformComponentProfile> platformComponentProfileRegisteredListRemote = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().requestListComponentRegistered(discoveryQueryParametersAssetUser);

            if (platformComponentProfileRegisteredListRemote != null && !platformComponentProfileRegisteredListRemote.isEmpty()) {

                for (PlatformComponentProfile platformComponentProfile : platformComponentProfileRegisteredListRemote) {

                    String profileImage = "";
                    if (!platformComponentProfile.getExtraData().equals("")) {
                        try {
                            JsonParser jParser = new JsonParser();
                            JsonObject jsonObject = jParser.parse(platformComponentProfile.getExtraData()).getAsJsonObject();

                            profileImage = jsonObject.get(DAP_IMG_USER).getAsString();
                        } catch (Exception e) {
                            profileImage = platformComponentProfile.getExtraData();
                        }
                    }

                    byte[] imageByte = Base64.decode(profileImage, Base64.DEFAULT);

                    ActorAssetUser actorAssetUserNew = new AssetUserActorRecord(
                            platformComponentProfile.getIdentityPublicKey(),
                            platformComponentProfile.getName(),
                            imageByte,
                            platformComponentProfile.getLocation());

                    actorAssetUserRegisteredList.add(actorAssetUserNew);
                }
            } else {
                return actorAssetUserRegisteredList;
            }

        } catch (CantRequestListException e) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Cant Request List Actor Asset User Registered";

            CantRequestListActorAssetUserRegisteredException pluginStartException = new CantRequestListActorAssetUserRegisteredException(CantRequestListActorAssetUserRegisteredException.DEFAULT_MESSAGE, null, context, possibleCause);

            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
        return actorAssetUserRegisteredList;
    }

    @Override
    public List<ActorAssetUser> getActorAssetUserRegistered(String actorAssetUserPublicKey) throws CantRequestListActorAssetUserRegisteredException {
        try {
//            if (actorAssetUserRegisteredList != null && !actorAssetUserRegisteredList.isEmpty()) {
//                actorAssetUserRegisteredList.clear();
//            }

            DiscoveryQueryParameters discoveryQueryParametersAssetUser = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                    constructDiscoveryQueryParamsFactory(PlatformComponentType.ACTOR_ASSET_USER, //applicant = who made the request
                            NetworkServiceType.UNDEFINED,
                            null,                     // alias
                            actorAssetUserPublicKey,  // identityPublicKey
                            null,                     // location
                            null,                     // distance
                            null,                     // name
                            null,                     // extraData
                            null,                     // offset
                            null,                     // max
                            null,                     // fromOtherPlatformComponentType, when use this filter apply the identityPublicKey
                            null);

            List<PlatformComponentProfile> platformComponentProfileRegisteredListRemote = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().requestListComponentRegistered(discoveryQueryParametersAssetUser);

            if (platformComponentProfileRegisteredListRemote != null && !platformComponentProfileRegisteredListRemote.isEmpty()) {

                for (PlatformComponentProfile platformComponentProfile : platformComponentProfileRegisteredListRemote) {

                    String profileImage = "";
                    if (!platformComponentProfile.getExtraData().equals("")) {
                        try {
                            JsonParser jParser = new JsonParser();
                            JsonObject jsonObject = jParser.parse(platformComponentProfile.getExtraData()).getAsJsonObject();

                            profileImage = jsonObject.get(DAP_IMG_USER).getAsString();
                        } catch (Exception e) {
                            profileImage = platformComponentProfile.getExtraData();
                        }
                    }

                    byte[] imageByte = Base64.decode(profileImage, Base64.DEFAULT);

                    ActorAssetUser actorAssetUserNew = new AssetUserActorRecord(
                            platformComponentProfile.getIdentityPublicKey(),
                            platformComponentProfile.getName(),
                            imageByte,
                            platformComponentProfile.getLocation());

                    actorAssetUserRegisteredList.add(actorAssetUserNew);
                }
            } else {
                return actorAssetUserRegisteredList;
            }

        } catch (CantRequestListException e) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "Cant Request List Actor Asset User Registered";

            CantRequestListActorAssetUserRegisteredException pluginStartException = new CantRequestListActorAssetUserRegisteredException(CantRequestListActorAssetUserRegisteredException.DEFAULT_MESSAGE, null, context, possibleCause);

            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);

            throw pluginStartException;
        }
        return actorAssetUserRegisteredList;
    }

    @Override
    public void askConnectionActorAsset(String actorAssetLoggedInPublicKey,
                                        String actorAssetLoggedName,
                                        Actors senderType,
                                        String actorAssetToAddPublicKey,
                                        String actorAssetToAddName,
                                        Actors destinationType,
                                        byte[] profileImage,
                                        BlockchainNetworkType blockchainNetworkType) throws CantAskConnectionActorAssetException {

        try {
            UUID newNotificationID = UUID.randomUUID();
            AssetNotificationDescriptor assetNotificationDescriptor = AssetNotificationDescriptor.ASKFORCONNECTION;
            long currentTime = System.currentTimeMillis();
            ActorAssetProtocolState actorAssetProtocolState = ActorAssetProtocolState.PROCESSING_SEND;

            final ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord = outgoingNotificationDao.createNotification(
                    newNotificationID,
                    actorAssetLoggedInPublicKey,
                    senderType,
                    actorAssetToAddPublicKey,
                    actorAssetLoggedName,
//                    intraUserToAddPhrase,
                    profileImage,
                    destinationType,
                    assetNotificationDescriptor,
                    currentTime,
                    actorAssetProtocolState,
                    false,
                    1,
                    blockchainNetworkType,
                    null
            );

//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        sendNewMessage(
//                                getProfileSenderToRequestConnection(assetUserNetworkServiceRecord.getActorSenderPublicKey()),
//                                getProfileDestinationToRequestConnection(assetUserNetworkServiceRecord.getActorDestinationPublicKey()),
//                                assetUserNetworkServiceRecord.toJson());
//                    } catch (com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.exceptions.CantSendMessageException e) {
//                        reportUnexpectedError(e);
//                    }
//                }
//            });
            // Sending message to the destination
        } catch (final CantCreateActorAssetNotificationException e) {
            reportUnexpectedError(e);
            throw new CantAskConnectionActorAssetException(e, "intra actor network service", "database corrupted");
        } catch (final Exception e) {
            reportUnexpectedError(e);
            throw new CantAskConnectionActorAssetException(e, "intra actor network service", "Unhandled error.");
        }
    }

    @Override
    public void acceptConnectionActorAsset(String actorAssetLoggedInPublicKey, String ActorAssetToAddPublicKey)
            throws CantAcceptConnectionActorAssetException {

        try {
            ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord = incomingNotificationsDao.
                    changeActorAssetNotificationDescriptor(
                            ActorAssetToAddPublicKey,
                            AssetNotificationDescriptor.ACCEPTED,
                            ActorAssetProtocolState.PENDING_ACTION);

            assetUserNetworkServiceRecord.setActorSenderPublicKey(actorAssetLoggedInPublicKey);
            assetUserNetworkServiceRecord.setActorDestinationPublicKey(ActorAssetToAddPublicKey);

            assetUserNetworkServiceRecord.setActorSenderAlias(null);

            assetUserNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.ACCEPTED);

            assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_SEND);

            final ActorAssetNetworkServiceRecord messageToSend = outgoingNotificationDao.createNotification(
                    UUID.randomUUID(),
                    assetUserNetworkServiceRecord.getActorSenderPublicKey(),
                    assetUserNetworkServiceRecord.getActorSenderType(),
                    assetUserNetworkServiceRecord.getActorDestinationPublicKey(),
                    assetUserNetworkServiceRecord.getActorSenderAlias(),
//                    assetUserNetworkServiceRecord.getActorSenderPhrase(),
                    assetUserNetworkServiceRecord.getActorSenderProfileImage(),
                    assetUserNetworkServiceRecord.getActorDestinationType(),
                    assetUserNetworkServiceRecord.getAssetNotificationDescriptor(),
                    System.currentTimeMillis(),
                    assetUserNetworkServiceRecord.getActorAssetProtocolState(),
                    false,
                    1,
                    assetUserNetworkServiceRecord.getBlockchainNetworkType(),
                    assetUserNetworkServiceRecord.getResponseToNotificationId()
            );

//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        // Sending message to the destination
//                        sendNewMessage(
//                                getProfileSenderToRequestConnection(messageToSend.getActorSenderPublicKey()),
//                                getProfileDestinationToRequestConnection(messageToSend.getActorDestinationPublicKey()),
//                                messageToSend.toJson());
//                    } catch (com.bitdubai.fermat_p2p_api.layer.all_definition.communication.network_services.exceptions.CantSendMessageException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception e) {
            throw new CantAcceptConnectionActorAssetException("ERROR ACTOR ASSET USER NS WHEN ACCEPTING CONNECTION", e, "", "Generic Exception");
        }
    }

    @Override
    public void denyConnectionActorAsset(String actorAssetLoggedInPublicKey, String actorAssetToRejectPublicKey)
            throws CantDenyConnectionActorAssetException {

        try {
            final ActorAssetNetworkServiceRecord actorNetworkServiceRecord = incomingNotificationsDao.
                    changeActorAssetNotificationDescriptor(
                            actorAssetToRejectPublicKey,
                            AssetNotificationDescriptor.DENIED,
                            ActorAssetProtocolState.DONE);

            actorNetworkServiceRecord.setActorDestinationPublicKey(actorAssetToRejectPublicKey);

            actorNetworkServiceRecord.setActorSenderPublicKey(actorAssetLoggedInPublicKey);

            actorNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.DENIED);

            actorNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_SEND);

            outgoingNotificationDao.createNotification(actorNetworkServiceRecord);

//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    // Sending message to the destination
//                    try {
//                        sendNewMessage(
//                                getProfileSenderToRequestConnection(actorNetworkServiceRecord.getActorSenderPublicKey()),
//                                getProfileDestinationToRequestConnection(actorNetworkServiceRecord.getActorDestinationPublicKey()),
//                                actorNetworkServiceRecord.toJson());
//                    } catch (CantSendMessageException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception e) {
            throw new CantDenyConnectionActorAssetException("ERROR DENY CONNECTION TO ACTOR ASSET USER", e, "", "Generic Exception");
        }
    }

    @Override
    public void disconnectConnectionActorAsset(String actorAssetLoggedInPublicKey, String actorAssetToDisconnectPublicKey)
            throws CantDisconnectConnectionActorAssetException {

        try {
            //make message to actor
            UUID newNotificationID = UUID.randomUUID();
            AssetNotificationDescriptor assetNotificationDescriptor = AssetNotificationDescriptor.DISCONNECTED;
            long currentTime = System.currentTimeMillis();
            ActorAssetProtocolState actorAssetProtocolState = ActorAssetProtocolState.PROCESSING_SEND;

            final ActorAssetNetworkServiceRecord actorNetworkServiceRecord = outgoingNotificationDao.createNotification(
                    newNotificationID,
                    actorAssetLoggedInPublicKey,
                    Actors.DAP_ASSET_ISSUER,
                    actorAssetToDisconnectPublicKey,
                    "",
                    new byte[0],
                    Actors.DAP_ASSET_USER,
                    assetNotificationDescriptor,
                    currentTime,
                    actorAssetProtocolState,
                    false,
                    1,
                    BlockchainNetworkType.getDefaultBlockchainNetworkType(),
                    null
            );

//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    // Sending message to the destination
//                    try {
//                        sendNewMessage(
//                                getProfileSenderToRequestConnection(actorNetworkServiceRecord.getActorSenderPublicKey()),
//                                getProfileDestinationToRequestConnection(actorNetworkServiceRecord.getActorDestinationPublicKey()),
//                                actorNetworkServiceRecord.toJson());
//                    } catch (CantSendMessageException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception e) {
            throw new CantDisconnectConnectionActorAssetException("ERROR DISCONNECTING ACTOR ASSET USER ", e, "", "Generic Exception");
        }
    }

    @Override
    public void cancelConnectionActorAsset(String actorAssetLoggedInPublicKey, String actorAssetToCancelPublicKey)
            throws CantCancelConnectionActorAssetException {

        try {
            final ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord = incomingNotificationsDao.
                    changeActorAssetNotificationDescriptor(
                            actorAssetLoggedInPublicKey,
                            AssetNotificationDescriptor.CANCEL,
                            ActorAssetProtocolState.DONE);

            assetUserNetworkServiceRecord.setActorDestinationPublicKey(actorAssetToCancelPublicKey);

            assetUserNetworkServiceRecord.setActorSenderPublicKey(actorAssetLoggedInPublicKey);

            assetUserNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.CANCEL);

            assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_SEND);

            outgoingNotificationDao.createNotification(assetUserNetworkServiceRecord);

//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    // Sending message to the destination
//                    try {
//                        sendNewMessage(
//                                getProfileSenderToRequestConnection(assetUserNetworkServiceRecord.getActorSenderPublicKey()),
//                                getProfileDestinationToRequestConnection(assetUserNetworkServiceRecord.getActorDestinationPublicKey()),
//                                assetUserNetworkServiceRecord.toJson());
//                    } catch (CantSendMessageException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception e) {
            throw new CantCancelConnectionActorAssetException("ERROR CANCEL CONNECTION TO ACTOR ASSET USER ", e, "", "Generic Exception");
        }
    }

    @Override
    public void confirmActorAssetNotification(UUID notificationID) throws CantConfirmActorAssetNotificationException {
        try {
            incomingNotificationsDao.markNotificationAsRead(notificationID);
        } catch (final Exception e) {
            throw new CantConfirmActorAssetNotificationException(e, "notificationID: " + notificationID, "Unhandled error.");
        }
    }

//    @Override
//    public void requestCryptoAddress(ActorAssetIssuer actorAssetIssuerSender, ActorAssetUser actorAssetUserDestination) throws CantRequestCryptoAddressException {
//
//        try {
//
//            if (true) {
//
//                CommunicationNetworkServiceLocal communicationNetworkServiceLocal = communicationNetworkServiceConnectionManager.getNetworkServiceLocalInstance(actorAssetUserDestination.getActorPublicKey());
//
//                Gson gson = new Gson();
//                JsonObject packetContent = new JsonObject();
//                packetContent.addProperty(JsonAssetUserANSAttNamesConstants.ISSUER, gson.toJson(actorAssetIssuerSender));
//                packetContent.addProperty(JsonAssetUserANSAttNamesConstants.USER, gson.toJson(actorAssetUserDestination));
//
//
//                String messageContentIntoJson = gson.toJson(packetContent);
//
//                if (communicationNetworkServiceLocal != null) {
//
//                    //Send the message
//                    communicationNetworkServiceLocal.sendMessage(actorAssetIssuerSender.getActorPublicKey(), actorAssetUserDestination.getActorPublicKey(), messageContentIntoJson);
//
//                } else {
//
//                    /*
//                     * Created the message
//                     */
//                    FermatMessage fermatMessage = FermatMessageCommunicationFactory.constructFermatMessage(actorAssetIssuerSender.getActorPublicKey(),//Sender
//                            actorAssetUserDestination.getActorPublicKey(), //Receiver
//                            messageContentIntoJson,                //Message Content
//                            FermatMessageContentType.TEXT);//Type
//
//                    /*
//                     * Configure the correct status
//                     */
//                    ((FermatMessageCommunication) fermatMessage).setFermatMessagesStatus(FermatMessagesStatus.PENDING_TO_SEND);
//
//                    /*
//                     * Save to the data base table
//                     */
//                    OutgoingMessageDao outgoingMessageDao = communicationNetworkServiceConnectionManager.getOutgoingMessageDao();
//                    outgoingMessageDao.create(fermatMessage);
//
//                    /*
//                     * Create the sender basic profile
//                     */
//                    PlatformComponentProfile sender = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructBasicPlatformComponentProfileFactory(actorAssetIssuerSender.getActorPublicKey(), NetworkServiceType.UNDEFINED, PlatformComponentType.ACTOR_ASSET_ISSUER);
//
//                    /*
//                     * Create the receiver basic profile
//                     */
//                    PlatformComponentProfile receiver = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructBasicPlatformComponentProfileFactory(actorAssetUserDestination.getActorPublicKey(), NetworkServiceType.UNDEFINED, PlatformComponentType.ACTOR_ASSET_USER);
//
//                    /*
//                     * Ask the client to connect
//                     */
//                    communicationNetworkServiceConnectionManager.connectTo(sender, getPlatformComponentProfilePluginRoot(), receiver);
//
//                }
//
//
//            } else {
//
//                StringBuffer contextBuffer = new StringBuffer();
//                contextBuffer.append("Plugin ID: " + pluginId);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("errorManager: " + errorManager);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("eventManager: " + eventManager);
//
//                String context = contextBuffer.toString();
//                String possibleCause = "Asset User Actor Network Service Not Registered";
//
//                CantRequestCryptoAddressException pluginStartException = new CantRequestCryptoAddressException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);
//
//                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
//
//                throw pluginStartException;
//
//            }
//
//        } catch (Exception e) {
//
//            StringBuffer contextBuffer = new StringBuffer();
//            contextBuffer.append("Plugin ID: " + pluginId);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("errorManager: " + errorManager);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("eventManager: " + eventManager);
//
//            String context = contextBuffer.toString();
//            String possibleCause = "Cant Request Crypto Address";
//
//            CantRequestCryptoAddressException pluginStartException = new CantRequestCryptoAddressException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);
//
//            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
//
//            throw pluginStartException;
//        }
//
//    }
//
//    @Override
//    public void sendCryptoAddress(ActorAssetUser actorAssetUserSender, ActorAssetIssuer actorAssetIssuerDestination, CryptoAddress cryptoAddress) throws CantSendCryptoAddressException {
//
//        try {
//
//
//            if (true) {
//                CommunicationNetworkServiceLocal communicationNetworkServiceLocal = communicationNetworkServiceConnectionManager.getNetworkServiceLocalInstance(actorAssetIssuerDestination.getActorPublicKey());
//
//                Gson gson = new Gson();
//                JsonObject packetContent = new JsonObject();
//                packetContent.addProperty(JsonAssetUserANSAttNamesConstants.ISSUER, gson.toJson(actorAssetIssuerDestination));
//                packetContent.addProperty(JsonAssetUserANSAttNamesConstants.USER, gson.toJson(actorAssetUserSender));
//                packetContent.addProperty(JsonAssetUserANSAttNamesConstants.CRYPTOADDRES, gson.toJson(cryptoAddress));
//
//                String messageContentIntoJson = gson.toJson(packetContent);
//
//                if (communicationNetworkServiceLocal != null) {
//
//                    //Send the message
//                    communicationNetworkServiceLocal.sendMessage(actorAssetUserSender.getActorPublicKey(), actorAssetIssuerDestination.getActorPublicKey(), messageContentIntoJson);
//
//                } else {
//
//                    /*
//                     * Created the message
//                     */
//                    FermatMessage fermatMessage = FermatMessageCommunicationFactory.constructFermatMessage(actorAssetUserSender.getActorPublicKey(),//Sender
//                            actorAssetIssuerDestination.getActorPublicKey(), //Receiver
//                            messageContentIntoJson,                //Message Content
//                            FermatMessageContentType.TEXT);//Type
//
//                    /*
//                     * Configure the correct status
//                     */
//                    ((FermatMessageCommunication) fermatMessage).setFermatMessagesStatus(FermatMessagesStatus.PENDING_TO_SEND);
//
//                    /*
//                     * Save to the data base table
//                     */
//                    OutgoingMessageDao outgoingMessageDao = communicationNetworkServiceConnectionManager.getOutgoingMessageDao();
//                    outgoingMessageDao.create(fermatMessage);
//
//                    /*
//                     * Create the sender basic profile
//                     */
//                    PlatformComponentProfile sender = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructBasicPlatformComponentProfileFactory(actorAssetUserSender.getActorPublicKey(), NetworkServiceType.UNDEFINED, PlatformComponentType.ACTOR_ASSET_USER);
//
//                    /*
//                     * Create the receiver basic profile
//                     */
//                    PlatformComponentProfile receiver = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructBasicPlatformComponentProfileFactory(actorAssetIssuerDestination.getActorPublicKey(), NetworkServiceType.UNDEFINED, PlatformComponentType.ACTOR_ASSET_ISSUER);
//
//                    /*
//                     * Ask the client to connect
//                     */
//                    communicationNetworkServiceConnectionManager.connectTo(sender, getPlatformComponentProfilePluginRoot(), receiver);
//
//                }
//
//            } else {
//
//
//                StringBuffer contextBuffer = new StringBuffer();
//                contextBuffer.append("Plugin ID: " + pluginId);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("errorManager: " + errorManager);
//                contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//                contextBuffer.append("eventManager: " + eventManager);
//
//                String context = contextBuffer.toString();
//                String possibleCause = "Cant Request Crypto Address";
//
//                CantSendCryptoAddressException pluginStartException = new CantSendCryptoAddressException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);
//
//                //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
//
//                throw pluginStartException;
//
//            }
//
//
//        } catch (Exception e) {
//
//            StringBuffer contextBuffer = new StringBuffer();
//            contextBuffer.append("Plugin ID: " + pluginId);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("errorManager: " + errorManager);
//            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
//            contextBuffer.append("eventManager: " + eventManager);
//
//            String context = contextBuffer.toString();
//            String possibleCause = "Cant Send Message";
//
//            CantSendCryptoAddressException pluginStartException = new CantSendCryptoAddressException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);
//
//            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
//
//            throw pluginStartException;
//        }
//    }

    @Override
    public List<PlatformComponentProfile> getRemoteNetworkServicesRegisteredList() {
        return remoteNetworkServicesRegisteredList;
    }

    @Override
    public void requestRemoteNetworkServicesRegisteredList(DiscoveryQueryParameters discoveryQueryParameters) {

        System.out.println("Asset User Actor NetworkServicePluginRoot - requestRemoteNetworkServicesRegisteredList");

        /*
         * Request the list of component registers
         */
        try {
            wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().requestListComponentRegistered(getPlatformComponentProfilePluginRoot(), discoveryQueryParameters);
        } catch (CantRequestListException e) {
            e.printStackTrace();
        }

    }

    @Override
    public NetworkServiceConnectionManager getNetworkServiceConnectionManager() {
        return communicationNetworkServiceConnectionManager;
    }

    @Override
    public DiscoveryQueryParameters constructDiscoveryQueryParamsFactory(PlatformComponentType platformComponentType, NetworkServiceType networkServiceType, String alias, String identityPublicKey, Location location, Double distance, String name, String extraData, Integer firstRecord, Integer numRegister, PlatformComponentType fromOtherPlatformComponentType, NetworkServiceType fromOtherNetworkServiceType) {
        return wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructDiscoveryQueryParamsFactory(platformComponentType,
                networkServiceType, alias, identityPublicKey, location, distance, name, extraData, firstRecord, numRegister, fromOtherPlatformComponentType, fromOtherNetworkServiceType);
    }

    @Override
    public void handleCompleteComponentRegistrationNotificationEvent(PlatformComponentProfile platformComponentProfileRegistered) {
        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.COMMUNICATION_CLOUD_CLIENT && this.register) {
            if (communicationRegistrationProcessNetworkServiceAgent.isAlive()) {
                communicationRegistrationProcessNetworkServiceAgent.interrupt();
                communicationRegistrationProcessNetworkServiceAgent = null;
            }
            /*
             * Construct my profile and register me
             */
            PlatformComponentProfile platformComponentProfileToReconnect = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().constructPlatformComponentProfileFactory(this.getIdentityPublicKey(),
                    this.getAlias().toLowerCase(),
                    this.getName(),
                    this.getNetworkServiceType(),
                    this.getPlatformComponentType(),
                    this.getExtraData());

            try {
                    /*
                     * Register me
                     */
                wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().registerComponentForCommunication(this.getNetworkServiceType(), platformComponentProfileToReconnect);

            } catch (CantRegisterComponentException e) {
                e.printStackTrace();
            }

        }

        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.ACTOR_ASSET_USER) {
            System.out.print("ACTOR ASSET USER REGISTRADO!! -----------------------\n" +
                    "-----------------------\n USER: " + platformComponentProfileRegistered.getAlias());
        }

        /*
         * If the component registered have my profile and my identity public key
         */
        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.NETWORK_SERVICE &&
                platformComponentProfileRegistered.getNetworkServiceType() == NetworkServiceType.ASSET_USER_ACTOR &&
                platformComponentProfileRegistered.getIdentityPublicKey().equals(identity.getPublicKey())) {

            /*
             * Mark as register
             */
            this.register = Boolean.TRUE;

//            initializeActorAssetUserAgent();

            /*
             * If exist actor asset user pending to registration
             */
            if (actorAssetUserPendingToRegistration != null && !actorAssetUserPendingToRegistration.isEmpty()) {

                CommunicationsClientConnection communicationsClientConnection = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection();

                for (PlatformComponentProfile platformComponentProfileAssetUser : actorAssetUserPendingToRegistration) {
                     /*
                     * ask to the communication cloud client to register
                     */
                    try {
                        communicationsClientConnection.registerComponentForCommunication(
                                this.getNetworkServiceType(),
                                platformComponentProfileAssetUser);
                    } catch (CantRegisterComponentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /*
         * If is a actor registered
         */
        if (platformComponentProfileRegistered.getPlatformComponentType() == PlatformComponentType.ACTOR_ASSET_USER &&
                platformComponentProfileRegistered.getNetworkServiceType() == NetworkServiceType.UNDEFINED) {

            Location loca = null;

            ActorAssetUser actorAssetUserNewRegistered = new AssetUserActorRecord(
                    platformComponentProfileRegistered.getIdentityPublicKey(),
                    platformComponentProfileRegistered.getName(),
                    convertoByteArrayfromString(platformComponentProfileRegistered.getExtraData()),
                    loca);

            if (communicationNetworkServiceConnectionManager == null) {

                this.communicationNetworkServiceConnectionManager = new CommunicationNetworkServiceConnectionManager(
                        getPlatformComponentProfilePluginRoot(),
                        identity,
                        wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection(),
                        dataBase,
                        errorManager,
                        eventManager);
            }
//            if (createactorAgentnum == 0) {
//
//                assetUserActorNetworkServiceAgent = new AssetUserActorNetworkServiceAgent(
//                        this,
//                        wsCommunicationsCloudClientManager,
//                        communicationNetworkServiceConnectionManager,
//                        getPlatformComponentProfilePluginRoot(),
//                        errorManager,
//                        identity,
//                        dataBase);
//
//                // start main threads
//                assetUserActorNetworkServiceAgent.start();
//
//                createactorAgentnum = 1;
//            }

            System.out.println("Actor Asset User REGISTRADO en A.N.S - Enviando Evento de Notificacion");

            FermatEvent event = eventManager.getNewEvent(EventType.COMPLETE_ASSET_USER_REGISTRATION_NOTIFICATION);
            event.setSource(EventSource.ACTOR_ASSET_USER);

            ((ActorAssetUserCompleteRegistrationNotificationEvent) event).setActorAssetUser(actorAssetUserNewRegistered);

            eventManager.raiseEvent(event);
        }
    }

    @Override
    public void handleFailureComponentRegistrationNotificationEvent(PlatformComponentProfile networkServiceApplicant, PlatformComponentProfile remoteParticipant) {
        System.out.println(" Failure ConnectoTo() with " + remoteParticipant.getIdentityPublicKey());
//        assetUserActorNetworkServiceAgent.connectionFailure(remoteParticipant.getIdentityPublicKey());
    }

    @Override
    public void handleCompleteRequestListComponentRegisteredNotificationEvent(List<PlatformComponentProfile> platformComponentProfileRegisteredList) {

        System.out.println(" CommunicationNetworkServiceConnectionManager - Starting method handleCompleteComponentRegistrationNotificationEvent");
        /*
         * if have result create a ActorAssetUser
         */
        if (platformComponentProfileRegisteredList != null && !platformComponentProfileRegisteredList.isEmpty()) {
            /*
             * Get a remote network service registered from the list requested
             */
            PlatformComponentProfile remoteNetworkServiceToConnect = platformComponentProfileRegisteredList.get(0);

            /*
             * tell to the manager to connect to this remote network service
             */
            if (remoteNetworkServiceToConnect.getNetworkServiceType() == NetworkServiceType.UNDEFINED && remoteNetworkServiceToConnect.getPlatformComponentType() == PlatformComponentType.ACTOR_ASSET_USER) {
                for (PlatformComponentProfile p : platformComponentProfileRegisteredList) {
                    Location loca = null;
                    ActorAssetUser actorAssetUserNew = new AssetUserActorRecord(p.getIdentityPublicKey(), p.getName(), convertoByteArrayfromString(p.getExtraData()), loca);

                    actorAssetUserRegisteredList.add(actorAssetUserNew);
                }

                FermatEvent event = eventManager.getNewEvent(EventType.COMPLETE_REQUEST_LIST_ASSET_USER_REGISTERED_NOTIFICATION);
                event.setSource(EventSource.ACTOR_ASSET_USER);

                //((AssetUserActorRequestListRegisteredNetworkServiceNotificationEvent) event).setActorAssetUserList(actorAssetUserRegisteredList);
                eventManager.raiseEvent(event);
            } else if (remoteNetworkServiceToConnect.getNetworkServiceType() == NetworkServiceType.ASSET_USER_ACTOR && remoteNetworkServiceToConnect.getPlatformComponentType() == PlatformComponentType.NETWORK_SERVICE) {
                /*
                 * save into the cache
                 */
                remoteNetworkServicesRegisteredList = platformComponentProfileRegisteredList;
            }
        }
    }

    @Override
    public void handleCompleteComponentConnectionRequestNotificationEvent(PlatformComponentProfile applicantComponentProfile, PlatformComponentProfile remoteComponentProfile) {

        System.out.println(" AssetUserActorNetworkServiceRoot - Starting method handleCompleteComponentConnectionRequestNotificationEvent");

        /*
         * Tell the manager to handler the new connection stablished
         */
        communicationNetworkServiceConnectionManager.handleEstablishedRequestedNetworkServiceConnection(remoteComponentProfile);
    }


    /**
     * Handles the events VPNConnectionCloseNotificationEvent
     *
     * @param fermatEvent
     */
    @Override
    public void handleVpnConnectionCloseNotificationEvent(FermatEvent fermatEvent) {

        if (fermatEvent instanceof VPNConnectionCloseNotificationEvent) {

            VPNConnectionCloseNotificationEvent vpnConnectionCloseNotificationEvent = (VPNConnectionCloseNotificationEvent) fermatEvent;

            if (vpnConnectionCloseNotificationEvent.getNetworkServiceApplicant() == getNetworkServiceType()) {
                String remotePublicKey = vpnConnectionCloseNotificationEvent.getRemoteParticipant().getIdentityPublicKey();

                if (communicationNetworkServiceConnectionManager != null)
                    communicationNetworkServiceConnectionManager.closeConnection(remotePublicKey);

//                if (assetUserActorNetworkServiceAgent != null)
//                    assetUserActorNetworkServiceAgent.getPoolConnectionsWaitingForResponse().remove(remotePublicKey);

            }
        }
    }

    /**
     * Handles the events ClientConnectionCloseNotificationEvent
     *
     * @param fermatEvent
     */
    @Override
    public void handleClientConnectionCloseNotificationEvent(FermatEvent fermatEvent) {

        if (fermatEvent instanceof ClientConnectionCloseNotificationEvent) {
            this.register = false;

            if (communicationNetworkServiceConnectionManager != null)
                communicationNetworkServiceConnectionManager.closeAllConnection();
        }

    }

    /*
     * Handles the events ClientConnectionLooseNotificationEvent
     */
    @Override
    public void handleClientConnectionLooseNotificationEvent(FermatEvent fermatEvent) {

        if (communicationNetworkServiceConnectionManager != null)
            communicationNetworkServiceConnectionManager.stop();

    }

    /*
     * Handles the events ClientSuccessfullReconnectNotificationEvent
     */
    @Override
    public void handleClientSuccessfullReconnectNotificationEvent(FermatEvent fermatEvent) {

        if (communicationNetworkServiceConnectionManager != null) {
            communicationNetworkServiceConnectionManager.restart();
        }

        if (communicationRegistrationProcessNetworkServiceAgent != null && !this.register) {

            if (communicationRegistrationProcessNetworkServiceAgent.isAlive()) {
                communicationRegistrationProcessNetworkServiceAgent.interrupt();
                communicationRegistrationProcessNetworkServiceAgent = null;
            }

                   /*
                 * Construct my profile and register me
                 */
            PlatformComponentProfile platformComponentProfileToReconnect = wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().
                    constructPlatformComponentProfileFactory(this.getIdentityPublicKey(),
                            this.getAlias().toLowerCase(),
                            this.getName(),
                            this.getNetworkServiceType(),
                            this.getPlatformComponentType(),
                            this.getExtraData());

            try {
                    /*
                     * Register me
                     */
                wsCommunicationsCloudClientManager.getCommunicationsCloudClientConnection().registerComponentForCommunication(
                        this.getNetworkServiceType(),
                        platformComponentProfileToReconnect);

            } catch (CantRegisterComponentException e) {
                e.printStackTrace();
            }
                /*
                 * Configure my new profile
                 */
            this.setPlatformComponentProfilePluginRoot(platformComponentProfileToReconnect);

                /*
                 * Initialize the connection manager
                 */
            this.initializeCommunicationNetworkServiceConnectionManager();
        }

        /*
         * Mark as register
         */
        this.register = Boolean.TRUE;

//        if (assetUserActorNetworkServiceAgent != null) {
//            try {
//                assetUserActorNetworkServiceAgent.start();
//            } catch (CantStartAgentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            initializeActorAssetUserAgent();
//        }
    }

    private void startTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // change message state to process retry later
                reprocessMessages();
            }
        },0, reprocessTimer);
    }

    /**
     * Static method to get the logging level from any class under root.
     *
     * @param className
     * @return
     */
    public static LogLevel getLogLevelByClass(String className) {
        try {
            /**
             * sometimes the classname may be passed dinamically with an $moretext
             * I need to ignore whats after this.
             */
            String[] correctedClass = className.split((Pattern.quote("$")));
            return AssetUserActorNetworkServicePluginRoot.newLoggingLevel.get(correctedClass[0]);
        } catch (Exception e) {
            /**
             * If I couldn't get the correct loggin level, then I will set it to minimal.
             */
            return DEFAULT_LOG_LEVEL;
        }
    }

    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        return communicationNetworkServiceDeveloperDatabaseFactory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        return communicationNetworkServiceDeveloperDatabaseFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        return communicationNetworkServiceDeveloperDatabaseFactory.getDatabaseTableContent(developerObjectFactory, developerDatabase, developerDatabaseTable);
    }

    /**
     * This method initialize the database
     *
     * @throws CantInitializeTemplateNetworkServiceDatabaseException
     */
    private void initializeDb() throws CantInitializeTemplateNetworkServiceDatabaseException {

        try {
            /*
             * Open new database connection
             */
            this.dataBase = this.pluginDatabaseSystem.openDatabase(pluginId, AssetUserNetworkServiceDatabaseConstants.DATA_BASE_NAME);

        } catch (CantOpenDatabaseException cantOpenDatabaseException) {

            /*
             * The database exists but cannot be open. I can not handle this situation.
             */
            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantOpenDatabaseException);
            throw new CantInitializeTemplateNetworkServiceDatabaseException(cantOpenDatabaseException.getLocalizedMessage());

        } catch (DatabaseNotFoundException e) {

            /*
             * The database no exist may be the first time the plugin is running on this device,
             * We need to create the new database
             */
            AssetUserNetworkServiceDatabaseFactory communicationNetworkServiceDatabaseFactory = new AssetUserNetworkServiceDatabaseFactory(pluginDatabaseSystem);

            try {

                /*
                 * We create the new database
                 */
                this.dataBase = communicationNetworkServiceDatabaseFactory.createDatabase(pluginId, AssetUserNetworkServiceDatabaseConstants.DATA_BASE_NAME);

            } catch (CantCreateDatabaseException cantOpenDatabaseException) {

                /*
                 * The database cannot be created. I can not handle this situation.
                 */
                //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, cantOpenDatabaseException);
                throw new CantInitializeTemplateNetworkServiceDatabaseException(cantOpenDatabaseException.getLocalizedMessage());

            }
        }
    }

    /*
     * Convert a string to array bytes
     */
    private static byte[] convertoByteArrayfromString(String arrengebytes) {

        if (arrengebytes != null) {
            try {
                String[] byteValues = arrengebytes.substring(1, arrengebytes.length() - 1).split(",");
                byte[] bytes = new byte[byteValues.length];
                if (bytes.length > 0) {
                    for (int i = 0, len = bytes.length; i < len; i++) {
                        bytes[i] = Byte.parseByte(byteValues[i].trim());
                    }
                    return bytes;
                } else {
                    return new byte[]{};
                }
            } catch (Exception e) {
                return new byte[]{};
            }
        } else {
            return new byte[]{};
        }
    }

    /**
     * Get the New Received Message List
     *
     * @return List<FermatMessage>
     */
    public List<FermatMessage> getNewReceivedMessageList() throws CantReadRecordDataBaseException {

        Map<String, Object> filters = new HashMap<>();
        filters.put(CommunicationNetworkServiceDatabaseConstants.INCOMING_MESSAGES_FIRST_KEY_COLUMN, MessagesStatus.NEW_RECEIVED.getCode());

        return communicationNetworkServiceConnectionManager.getIncomingMessageDao().findAll(filters);
    }

    /**
     * Mark the message as read
     *
     * @param fermatMessage
     */
    public void markAsRead(FermatMessage fermatMessage) throws CantUpdateRecordDataBaseException {
        try {
            ((FermatMessageCommunication) fermatMessage).setFermatMessagesStatus(FermatMessagesStatus.READ);
            communicationNetworkServiceConnectionManager.getIncomingMessageDao().update(fermatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method validate is all required resource are injected into
     * the plugin root by the platform
     *
     * @throws CantStartPluginException
     */
    private void validateInjectedResources() throws CantStartPluginException {

         /*
         * If all resources are inject
         */
        if (wsCommunicationsCloudClientManager == null ||
                pluginDatabaseSystem == null ||
                errorManager == null ||
                eventManager == null) {

            StringBuffer contextBuffer = new StringBuffer();
            contextBuffer.append("Plugin ID: " + pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("wsCommunicationsCloudClientManager: " + wsCommunicationsCloudClientManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: " + pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: " + errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: " + eventManager);

            String context = contextBuffer.toString();
            String possibleCause = "No all required resource are injected";
            CantStartPluginException pluginStartException = new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, null, context, possibleCause);

            //errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, pluginStartException);
            throw pluginStartException;
        }
    }

    /**
     * Initialize the event listener and configure
     */
    private void initializeListener() {

         /*
         * Listen and handle Complete Component Registration Notification Event
         */
        FermatEventListener fermatEventListener = eventManager.getNewListener(P2pEventType.COMPLETE_COMPONENT_REGISTRATION_NOTIFICATION);
        fermatEventListener.setEventHandler(new CompleteComponentRegistrationNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

         /*
         * Listen and handle Complete Request List Component Registered Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.COMPLETE_REQUEST_LIST_COMPONENT_REGISTERED_NOTIFICATION);
        fermatEventListener.setEventHandler(new CompleteRequestListComponentRegisteredNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle Complete Request List Component Registered Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.COMPLETE_COMPONENT_CONNECTION_REQUEST_NOTIFICATION);
        fermatEventListener.setEventHandler(new CompleteComponentConnectionRequestNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

        /*
         * Listen and handle VPN Connection Close Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.VPN_CONNECTION_CLOSE);
        fermatEventListener.setEventHandler(new VPNConnectionCloseNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

              /*
         * Listen and handle Client Connection Close Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.CLIENT_CONNECTION_CLOSE);
        fermatEventListener.setEventHandler(new ClientConnectionCloseNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

//         /*
//         * Listen and handle New Message Receive Notification Event
//         */
//        fermatEventListener = eventManager.getNewListener(P2pEventType.NEW_NETWORK_SERVICE_MESSAGE_RECEIVE_NOTIFICATION);
//        fermatEventListener.setEventHandler(new NewReceiveMessagesNotificationEventHandler(this, eventManager));
//        eventManager.addListener(fermatEventListener);
//        listenersAdded.add(fermatEventListener);

          /*
         * Listen and handle Client Connection Loose Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.CLIENT_CONNECTION_LOOSE);
        fermatEventListener.setEventHandler(new ClientConnectionLooseNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);


        /*
         * Listen and handle Client Connection Success Reconnect Notification Event
         */
        fermatEventListener = eventManager.getNewListener(P2pEventType.CLIENT_SUCCESS_RECONNECT);
        fermatEventListener.setEventHandler(new ClientSuccessfullReconnectNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

    }

    @Override
    public void handleNewMessages(FermatMessage fermatMessage) {
        try {
            System.out.println("ACTOR ASSET MENSAJE ENTRANTE A GSON: " + fermatMessage.toJson());

            ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord = ActorAssetNetworkServiceRecord.fronJson(fermatMessage.getContent());

            switch (assetUserNetworkServiceRecord.getAssetNotificationDescriptor()) {
                case ASKFORCONNECTION:
                    System.out.println("ACTOR ASSET MENSAJE LLEGO: " + assetUserNetworkServiceRecord.getActorSenderAlias());
                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_RECEIVE);
                    System.out.println("ACTOR ASSET REGISTRANDO EN INCOMING NOTIFICATION DAO");
                    assetUserNetworkServiceRecord.setFlagRead(false);
                    incomingNotificationsDao.createNotification(assetUserNetworkServiceRecord);

                    //NOTIFICATION LAUNCH
//                launchIncomingRequestConnectionNotificationEvent(actorNetworkServiceRecord);
                    launchNotificationActorAsset();
                    respondReceiveAndDoneCommunication(assetUserNetworkServiceRecord);
                    break;

                case ACCEPTED:
                    //TODO: ver si me conviene guardarlo en el outogoing DAO o usar el incoming para las que llegan directamente
                    assetUserNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.ACCEPTED);
                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.DONE);
                    outgoingNotificationDao.update(assetUserNetworkServiceRecord);

                    //create incoming notification
                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_RECEIVE);
                    assetUserNetworkServiceRecord.setFlagRead(false);
                    incomingNotificationsDao.createNotification(assetUserNetworkServiceRecord);
                    System.out.println("ACTOR ASSET MENSAJE ACCEPTED LLEGÓ: " + assetUserNetworkServiceRecord.getActorSenderAlias());
                    respondReceiveAndDoneCommunication(assetUserNetworkServiceRecord);
                    break;

                case RECEIVED:
                    //launchIncomingRequestConnectionNotificationEvent(actorNetworkServiceRecord);
                    System.out.println("ACTOR ASSET THE RECORD WAS CHANGE TO THE STATE OF DELIVERY: " + assetUserNetworkServiceRecord.getActorSenderAlias());
                    //TODO: ver porqué no encuentra el id para cambiarlo
                    if (assetUserNetworkServiceRecord.getResponseToNotificationId() != null)
                        outgoingNotificationDao.changeProtocolState(assetUserNetworkServiceRecord.getResponseToNotificationId(), ActorAssetProtocolState.DONE);

                    // close connection, sender is the destination
                    System.out.println("ACTOR ASSET THE CONNECTION WAS CHANGE TO DONE" + assetUserNetworkServiceRecord.getActorSenderAlias());

                    communicationNetworkServiceConnectionManager.closeConnection(assetUserNetworkServiceRecord.getActorSenderPublicKey());
//                    assetUserActorNetworkServiceAgent.getPoolConnectionsWaitingForResponse().remove(assetUserNetworkServiceRecord.getActorSenderPublicKey());
                    System.out.println("ACTOR ASSET THE CONNECTION WAS CLOSED AND THE AWAITING POOL CLEARED." + assetUserNetworkServiceRecord.getActorSenderAlias());
                    break;

                case DENIED:
                    assetUserNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.DENIED);
                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.DONE);
                    outgoingNotificationDao.update(assetUserNetworkServiceRecord);

                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_RECEIVE);
                    assetUserNetworkServiceRecord.setFlagRead(false);
                    incomingNotificationsDao.createNotification(assetUserNetworkServiceRecord);
                    System.out.println("ACTOR ASSET MENSAJE DENIED LLEGÓ: " + assetUserNetworkServiceRecord.getActorDestinationPublicKey());

                    respondReceiveAndDoneCommunication(assetUserNetworkServiceRecord);
                    break;

                case DISCONNECTED:
                    assetUserNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.DISCONNECTED);
                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.DONE);
                    outgoingNotificationDao.update(assetUserNetworkServiceRecord);

                    assetUserNetworkServiceRecord.changeState(ActorAssetProtocolState.PROCESSING_RECEIVE);
                    assetUserNetworkServiceRecord.setFlagRead(false);
                    incomingNotificationsDao.createNotification(assetUserNetworkServiceRecord);
                    System.out.println("ACTOR ASSET MENSAJE DISCONNECTED LLEGÓ: " + assetUserNetworkServiceRecord.getActorSenderAlias());

                    respondReceiveAndDoneCommunication(assetUserNetworkServiceRecord);
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            //quiere decir que no estoy reciviendo metadata si no una respuesta
            e.printStackTrace();

        }

        System.out.println("Actor Asset Llegaron mensajes!!!!");
    }

    @Override
    public void handleNewSentMessageNotificationEvent(FermatMessage fermatMessage) {
        try {
            ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord = ActorAssetNetworkServiceRecord.fronJson(fermatMessage.getContent());

            if (assetUserNetworkServiceRecord.getActorAssetProtocolState() == ActorAssetProtocolState.DONE) {
                // close connection, sender is the destination
                System.out.println("ACTOR ASSET CERRANDO LA CONEXION DEL HANDLE NEW SENT MESSAGE NOTIFICATION");
                //   communicationNetworkServiceConnectionManager.closeConnection(actorNetworkServiceRecord.getActorDestinationPublicKey());
//                assetUserActorNetworkServiceAgent.getPoolConnectionsWaitingForResponse().remove(assetUserNetworkServiceRecord.getActorDestinationPublicKey());
            }

            //done message type receive
            if(assetUserNetworkServiceRecord.getAssetNotificationDescriptor() == AssetNotificationDescriptor.RECEIVED) {
                assetUserNetworkServiceRecord.setActorAssetProtocolState(ActorAssetProtocolState.DONE);
                outgoingNotificationDao.update(assetUserNetworkServiceRecord);
//                assetUserActorNetworkServiceAgent.getPoolConnectionsWaitingForResponse().remove(assetUserNetworkServiceRecord.getActorDestinationPublicKey());
            }
            System.out.println("SALIENDO DEL HANDLE NEW SENT MESSAGE NOTIFICATION");

        } catch (Exception e) {
            //quiere decir que no estoy reciviendo metadata si no una respuesta
            System.out.println("EXCEPCION DENTRO DEL PROCCESS EVENT");
            e.printStackTrace();

        }
    }

//    @Override
    public PlatformComponentProfile getProfileDestinationToRequestConnection(String identityPublicKeyDestination) {
        return getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection()
                .constructPlatformComponentProfileFactory(identityPublicKeyDestination,
                        "destination_alias",
                        "destionation_name",
                        NetworkServiceType.UNDEFINED,
                        PlatformComponentType.ACTOR_ASSET_USER,
                        "");
    }

//    @Override
    public PlatformComponentProfile getProfileSenderToRequestConnection(String identityPublicKeySender) {
        return getWsCommunicationsCloudClientManager().getCommunicationsCloudClientConnection()
                .constructPlatformComponentProfileFactory(identityPublicKeySender,
                        "sender_alias",
                        "sender_name",
                        NetworkServiceType.UNDEFINED,
                        PlatformComponentType.ACTOR_ASSET_ISSUER,
                        "");
    }

//    @Override
    protected void reprocessMessages() {
        try {
            outgoingNotificationDao.changeStatusNotSentMessage();
        }
        catch(CantGetActorAssetNotificationException e)
        {
            System.out.println("INTRA USER NS EXCEPCION REPROCESANDO MESSAGEs");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("INTRA USER NS EXCEPCION REPROCESANDO MESSAGEs");
            e.printStackTrace();
        }
    }

//    @Override
    protected void reprocessMessages(String identityPublicKey) {
        try {
            outgoingNotificationDao.changeStatusNotSentMessage(identityPublicKey);
        }
        catch(CantGetActorAssetNotificationException  e)
        {
            System.out.println("INTRA USER NS EXCEPCION REPROCESANDO MESSAGEs");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("INTRA USER NS EXCEPCION REPROCESANDO MESSAGEs");
            e.printStackTrace();
        }
    }

//    @Override
    public List<ActorNotification> getPendingNotifications() throws CantGetActorAssetNotificationException {

        try {
            return incomingNotificationsDao.listUnreadNotifications();
        } catch (CantGetActorAssetNotificationException e) {
            reportUnexpectedError(e);
            throw new CantGetActorAssetNotificationException(e, "ACTOR ASSET USER network service", "database corrupted");
        } catch (Exception e) {
            reportUnexpectedError(e);
            throw new CantGetActorAssetNotificationException(e, "ACTOR ASSET USER network service", "Unhandled error.");
        }
    }

    private void reportUnexpectedError(final Exception e) {
        errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR_NETWORK_SERVICE, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
    }

    //TODO HANDLE DAP MESSAGES!!!

    /**
     * @param dapMessage the message to be sent, this message has to contain both the actor
     *                   that sent the message and the actor that will receive the message.
     * @throws CantSendMessageException
     */
    @Override
    public void sendMessage(DAPMessage dapMessage) throws CantSendMessageException {

    }

    /**
     * This method retrieves the list of new incoming and unread DAP Messages for a specific type.
     *
     * @param type The {@link DAPMessageType} of message to search for.
     * @return {@link List} instance filled with all the {@link DAPMessage} that were found.
     * @throws CantGetDAPMessagesException If there was an error while querying for the list.
     */
    @Override
    public List<DAPMessage> getUnreadDAPMessagesByType(DAPMessageType type) throws CantGetDAPMessagesException {
        return Collections.EMPTY_LIST;
    }

    /**
     * This method returns the list of new unread DAPMessages for a specific subject, these messages can be
     * from the same or different types.
     *
     * @param subject
     * @return
     * @throws CantGetDAPMessagesException
     */
    @Override
    public List<DAPMessage> getUnreadDAPMessageBySubject(DAPMessageSubject subject) throws CantGetDAPMessagesException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void confirmReception(DAPMessage message) throws CantUpdateMessageStatusException {

    }

    public WsCommunicationsCloudClientManager getWsCommunicationsCloudClientManager() {
        return wsCommunicationsCloudClientManager;
    }

    public CommunicationNetworkServiceConnectionManager getCommunicationNetworkServiceConnectionManager() {
        return communicationNetworkServiceConnectionManager;
    }

    public IncomingNotificationDao getIncomingNotificationsDao() {
        return incomingNotificationsDao;
    }

    public OutgoingNotificationDao getOutgoingNotificationDao() {
        return outgoingNotificationDao;
    }


    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    private void launchNotificationActorAsset() {
        FermatEvent fermatEvent = this.getEventManager().getNewEvent(EventType.ACTOR_ASSET_NETWORK_SERVICE_NEW_NOTIFICATIONS);
        ActorAssetNetworkServicePendingNotificationEvent actorAssetRequestConnectionEvent = (ActorAssetNetworkServicePendingNotificationEvent) fermatEvent;
        this.getEventManager().raiseEvent(actorAssetRequestConnectionEvent);
    }

    // respond receive and done notification
    private void respondReceiveAndDoneCommunication(ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord) {

        assetUserNetworkServiceRecord = swapActor(assetUserNetworkServiceRecord);
        try {
            UUID newNotificationID = UUID.randomUUID();
            long currentTime = System.currentTimeMillis();
            ActorAssetProtocolState actorAssetProtocolState = ActorAssetProtocolState.PROCESSING_SEND;
            assetUserNetworkServiceRecord.changeDescriptor(AssetNotificationDescriptor.RECEIVED);
            outgoingNotificationDao.createNotification(
                    newNotificationID,
                    assetUserNetworkServiceRecord.getActorSenderPublicKey(),
                    assetUserNetworkServiceRecord.getActorSenderType(),
                    assetUserNetworkServiceRecord.getActorDestinationPublicKey(),
                    assetUserNetworkServiceRecord.getActorSenderAlias(),
                    assetUserNetworkServiceRecord.getActorSenderProfileImage(),
                    assetUserNetworkServiceRecord.getActorDestinationType(),
                    assetUserNetworkServiceRecord.getAssetNotificationDescriptor(),
                    currentTime,
                    actorAssetProtocolState,
                    false,
                    1,
                    assetUserNetworkServiceRecord.getBlockchainNetworkType(),
                    assetUserNetworkServiceRecord.getId()
            );
        } catch (CantCreateActorAssetNotificationException e) {
            e.printStackTrace();
        }

    }

    private ActorAssetNetworkServiceRecord swapActor(ActorAssetNetworkServiceRecord assetUserNetworkServiceRecord) {
        // swap actor
        String actorDestination = assetUserNetworkServiceRecord.getActorDestinationPublicKey();
        assetUserNetworkServiceRecord.setActorDestinationPublicKey(assetUserNetworkServiceRecord.getActorSenderPublicKey());
        assetUserNetworkServiceRecord.setActorSenderPublicKey(actorDestination);
        return assetUserNetworkServiceRecord;
    }
}