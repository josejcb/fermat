package com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ReferenceWallet;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.enums.VaultType;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_bch_api.layer.crypto_module.crypto_address_book.exceptions.CantRegisterCryptoAddressBookRecordException;
import com.bitdubai.fermat_bch_api.layer.crypto_module.crypto_address_book.interfaces.CryptoAddressBookManager;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.exceptions.CantGetExtendedPublicKeyException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.interfaces.AssetVaultManager;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.ExtendedPublicKey;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.interfaces.WatchOnlyVaultManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DAPConnectionState;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventType;
import com.bitdubai.fermat_dap_api.layer.all_definition.events.NewReceiveExtendedNotificationEvent;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetUserDeveloperIdentitiesException;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.DAPMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.content_message.AssetExtendedPublicKeyContentMessage;
import com.bitdubai.fermat_dap_api.layer.all_definition.network_service_message.exceptions.CantSendMessageException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.AssetIssuerActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantAssetIssuerActorNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantCreateActorAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantGetAssetIssuerActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantUpdateActorAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuerManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.exceptions.CantConnectToActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.exceptions.CantConnectToActorAssetRedeemPointException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPoint;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.exceptions.CantRegisterActorAssetIssuerException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.asset_issuer.interfaces.AssetIssuerActorNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantAcceptActorAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantAskConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantCancelConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantCreateActorAssetReceiveException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantDenyConnectionActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantGetActorAssetNotificationException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantGetActorAssetWaitingException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.exceptions.CantRequestAlreadySendActorAssetException;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.interfaces.ActorNotification;
import com.bitdubai.fermat_dap_api.layer.dap_actor_network_service.redeem_point.interfaces.AssetRedeemPointActorNetworkServiceManager;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.agent.ActorAssetIssuerMonitorAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.agent.RedeemerAddressesMonitorAgent;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.developerUtils.AssetIssuerActorDeveloperDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.event_handlers.ActorAssetIssuerCompleteRegistrationNotificationEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.event_handlers.ActorAssetIssuerNewNotificationsEventHandler;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.exceptions.CantAddPendingAssetIssuerException;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.exceptions.CantGetAssetIssuersListException;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.exceptions.CantInitializeAssetIssuerActorDatabaseException;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.exceptions.CantUpdateAssetIssuerException;
import com.bitdubai.fermat_dap_plugin.layer.actor.asset.issuer.developer.bitdubai.version_1.structure.AssetIssuerActorDao;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.bitdubai.fermat_pip_api.layer.user.device_user.exceptions.CantGetLoggedInDeviceUserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Nerio on 09/09/15.
 */
public class AssetIssuerActorPluginRoot extends AbstractPlugin implements
        ActorAssetIssuerManager,
        DatabaseManagerForDevelopers {

    public AssetIssuerActorPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    private ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_BROADCASTER_SYSTEM)
    private Broadcaster broadcaster;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.ASSET_ISSUER)
    private AssetIssuerActorNetworkServiceManager assetIssuerActorNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin = Plugins.REDEEM_POINT)
    private AssetRedeemPointActorNetworkServiceManager assetRedeemPointActorNetworkServiceManager;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS, layer = Layers.CRYPTO_VAULT, plugin = Plugins.BITCOIN_ASSET_VAULT)
    private AssetVaultManager assetVaultManager;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS, layer = Layers.CRYPTO_VAULT, plugin = Plugins.BITCOIN_WATCH_ONLY_VAULT)
    private WatchOnlyVaultManager watchOnlyVaultManager;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS, layer = Layers.CRYPTO_MODULE, plugin = Plugins.CRYPTO_ADDRESS_BOOK)
    private CryptoAddressBookManager cryptoAddressBookManager;

    private AssetIssuerActorDao assetIssuerActorDao;

    private ActorAssetIssuerMonitorAgent actorAssetIssuerMonitorAgent;

    private ActorAssetIssuer actorAssetIssuer;

    List<FermatEventListener> listenersAdded = new ArrayList<>();

    @Override
    public void start() throws CantStartPluginException {
        try {
            /**
             * Created instance of AssetUserActorDao and initialize Database
             */
            assetIssuerActorDao = new AssetIssuerActorDao(this.pluginDatabaseSystem, this.pluginFileSystem, this.pluginId);

            initializeListener();

            /**
             * Will load (if any) the local asset issuer
             */
            actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();


            /**
             * Agent for Search Actor Asset User REGISTERED in Actor Network Service User
             */
//            startMonitorAgent();

            this.serviceStatus = ServiceStatus.STARTED;

            /**
             * Test to comment when not needed.
             */
            //testGenerateAndInitializeWatchOnlyVault();

        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_ISSUER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_DAP_ASSET_ISSUER_ACTOR);
        }
    }

    @Override
    public void stop() {
        this.actorAssetIssuerMonitorAgent.stop();
        this.serviceStatus = ServiceStatus.STOPPED;
    }

    @Override
    public ActorAssetIssuer getActorByPublicKey(String actorPublicKey) throws CantGetAssetIssuerActorsException,
            CantAssetIssuerActorNotFoundException {
        try {
            ActorAssetIssuer currentIssuer = getActorAssetIssuer();
            if (currentIssuer != null && currentIssuer.getActorPublicKey().equals(actorPublicKey)) {
                return currentIssuer;
            } else {
                return assetIssuerActorDao.getActorByPublicKey(actorPublicKey);
            }
        } catch (CantGetAssetIssuerActorsException e) {
            throw new CantGetAssetIssuerActorsException("", FermatException.wrapException(e), "Cant Get Actor Asset Issuer from Data Base", null);
        }
    }

    @Override
    public void createActorAssetIssuerFactory(String assetIssuerActorPublicKey, String assetIssuerActorName, byte[] assetIssuerActorProfileImage) throws CantCreateActorAssetIssuerException {
        try {
            ActorAssetIssuer actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();

            if (actorAssetIssuer == null) {

                Double locationLatitude = new Random().nextDouble();
                Double locationLongitude = new Random().nextDouble();
                String description = "Asset Issuer Skynet Test";

                AssetIssuerActorRecord record = new AssetIssuerActorRecord(
                        assetIssuerActorPublicKey,
                        assetIssuerActorName,
                        DAPConnectionState.REGISTERED_LOCALLY,
                        locationLatitude,
                        locationLongitude,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        Actors.DAP_ASSET_ISSUER,
                        description,
                        "-",
                        assetIssuerActorProfileImage);

                assetIssuerActorDao.createNewAssetIssuer(record);

                actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();

                assetIssuerActorNetworkServiceManager.registerActorAssetIssuer(actorAssetIssuer);
            } else {

                actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();

                Double locationLatitude = new Random().nextDouble();
                Double locationLongitude = new Random().nextDouble();
                String description = "Asset Issuer Skynet Test";

                AssetIssuerActorRecord record = new AssetIssuerActorRecord(
                        actorAssetIssuer.getActorPublicKey(),
                        assetIssuerActorName,
                        actorAssetIssuer.getDapConnectionState(),
                        locationLatitude,
                        locationLongitude,
                        actorAssetIssuer.getRegistrationDate(),
                        System.currentTimeMillis(),
                        actorAssetIssuer.getType(),
                        description,
                        actorAssetIssuer.getExtendedPublicKey(),
                        assetIssuerActorProfileImage);

                assetIssuerActorDao.updateAssetIssuer(record);

                actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();

                assetIssuerActorNetworkServiceManager.updateActorAssetIssuer(actorAssetIssuer);
            }

            if (actorAssetIssuer != null) {
                System.out.println("*********************Actor Asset Issuer************************");
                System.out.println("Actor Asset PublicKey: " + actorAssetIssuer.getActorPublicKey());
                System.out.println("Actor Asset Name: " + actorAssetIssuer.getName());
                System.out.println("Actor Asset Description: " + actorAssetIssuer.getDescription());
                System.out.println("***************************************************************");
            }
        } catch (CantAddPendingAssetIssuerException e) {
            throw new CantCreateActorAssetIssuerException("CAN'T ADD NEW ACTOR ASSET ISSUER", e, "", "");
        } catch (CantGetAssetIssuerActorsException e) {
            throw new CantCreateActorAssetIssuerException("CAN'T GET ACTOR ASSET ISSUER", e, "", "");
        } catch (Exception e) {
            throw new CantCreateActorAssetIssuerException("CAN'T ADD NEW ACTOR ASSET ISSUER unknow Cause", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public void createActorAssetIssuerRegisterInNetworkService(List<ActorAssetIssuer> actorAssetIssuers) throws CantCreateActorAssetIssuerException {
        try {
            assetIssuerActorDao.createNewAssetIssuerRegisterInNetworkServiceByList(actorAssetIssuers);
        } catch (CantAddPendingAssetIssuerException e) {
            throw new CantCreateActorAssetIssuerException("CAN'T ADD NEW ACTOR ASSET ISSUER REGISTERED", e, "", "");
        }
    }

    @Override
    public void createActorAssetIssuerRegisterInNetworkService(ActorAssetIssuer actorAssetIssuer) throws CantCreateActorAssetIssuerException {
        List<ActorAssetIssuer> list = new ArrayList<>();
        list.add(actorAssetIssuer);
        createActorAssetIssuerRegisterInNetworkService(list);
    }

    @Override
    public ActorAssetIssuer getActorAssetIssuer() throws CantGetAssetIssuerActorsException {

        ActorAssetIssuer actorAssetIssuer;
        try {
            actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();
        } catch (Exception e) {
            throw new CantGetAssetIssuerActorsException("", FermatException.wrapException(e), "There is a problem I can't identify.", null);
        }

        return actorAssetIssuer;
    }

    @Override
    public DAPConnectionState getActorIssuerRegisteredDAPConnectionState(String actorAssetPublicKey) throws CantGetAssetIssuerActorsException {
        try {
            ActorAssetIssuer actorAssetIssuer;
//
            actorAssetIssuer = this.assetIssuerActorDao.getActorByPublicKey(actorAssetPublicKey);
//
            if (actorAssetIssuer != null)
                return actorAssetIssuer.getDapConnectionState();
            else
                return DAPConnectionState.ERROR_UNKNOWN;
        } catch (CantGetAssetIssuerActorsException e) {
            throw new CantGetAssetIssuerActorsException("CAN'T GET ACTOR ASSET ISSUER STATE", e, "Error get database info", "");
        } catch (Exception e) {
            throw new CantGetAssetIssuerActorsException("CAN'T GET ACTOR ASSET ISSUER STATE", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public List<ActorAssetIssuer> getAllAssetIssuerActorInTableRegistered() throws CantGetAssetIssuerActorsException {
        List<ActorAssetIssuer> list;

        try {
            list = this.assetIssuerActorDao.getAllAssetIssuerActorRegistered();
        } catch (CantGetAssetIssuersListException e) {
            throw new CantGetAssetIssuerActorsException("CAN'T GET ASSET ISSUER REGISTERED ACTOR", e, "", "");
        }

        return list;
    }

    @Override
    public List<ActorAssetIssuer> getAllAssetIssuerActorConnected() throws CantGetAssetIssuerActorsException {
        List<ActorAssetIssuer> list; // Asset Issuer Actor list.
        try {
            list = this.assetIssuerActorDao.getAllAssetIssuerActorConnected();
        } catch (CantGetAssetIssuersListException e) {
            throw new CantGetAssetIssuerActorsException("CAN'T GET ASSET USER ACTORS CONNECTED WITH CRYPTOADDRESS ", e, "", "");
        }

        return list;
    }


    @Override
    public List<ActorAssetIssuer> getAllAssetIssuerActorConnectedWithExtendedPublicKey() throws CantGetAssetIssuerActorsException {
        List<ActorAssetIssuer> list; // Asset Issuer Actor list.
        try {
            list = this.assetIssuerActorDao.getAllAssetIssuerActorConnectedWithExtendedpk();
        } catch (CantGetAssetIssuersListException e) {
            throw new CantGetAssetIssuerActorsException("CAN'T GET ASSET USER ACTORS CONNECTED WITH CRYPTOADDRESS ", e, "", "");
        }

        return list;
    }

    @Override
    public void sendMessage(ActorAssetIssuer requester, List<ActorAssetRedeemPoint> actorAssetRedeemPoints) throws CantConnectToActorAssetRedeemPointException {
        for (ActorAssetRedeemPoint actorAssetRedeemPoint : actorAssetRedeemPoints) {
            try {
                AssetExtendedPublicKeyContentMessage assetExtendedPublickKeyContentMessage = new AssetExtendedPublicKeyContentMessage();
                DAPMessage dapMessage = new DAPMessage(
                        assetExtendedPublickKeyContentMessage,
                        requester,
                        actorAssetRedeemPoint);
                assetRedeemPointActorNetworkServiceManager.sendMessage(dapMessage);
            } catch (CantSendMessageException e) {
                throw new CantConnectToActorAssetRedeemPointException("CAN'T SEND MESSAGE TO ACTOR ASSET REDEEM POINT", e, "", "");
            } catch (CantSetObjectException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(ActorNotification actorNotification) throws CantConnectToActorAssetException {
        assetIssuerActorNetworkServiceManager.buildSendMessage(actorNotification);
    }

    @Override
    public void updateExtendedPublicKey(String issuerPublicKey, String extendedPublicKey) throws CantUpdateActorAssetIssuerException {
        String context = "Issuer PK: " + issuerPublicKey + " - Extended PK: " + extendedPublicKey;
        try {
            assetIssuerActorDao.updateExtendedPublicKey(issuerPublicKey, extendedPublicKey);
        } catch (CantGetUserDeveloperIdentitiesException | CantLoadTableToMemoryException | CantUpdateRecordException e) {
            throw new CantUpdateActorAssetIssuerException(e, context, "Probably this issuer pk wasn't registered...");
        }
    }

    @Override
    public void updateIssuerRegisteredDAPConnectionState(String issuerPublicKey, DAPConnectionState connectionState) throws CantUpdateActorAssetIssuerException {
        String context = "Issuer PK: " + issuerPublicKey + " - Connection State: " + connectionState.getCode();
        try {
            assetIssuerActorDao.updateAssetIssuerDAPConnectionStateActorNetworkService(assetIssuerActorDao.getActorByPublicKey(issuerPublicKey), connectionState);
        } catch (CantUpdateAssetIssuerException | CantGetAssetIssuerActorsException e) {
            throw new CantUpdateActorAssetIssuerException(e, context, "Cant update Connection State of Issuer");
        }
    }

    @Override
    public void askActorAssetIssuerForConnection(String actorAssetIssuerToLinkPublicKey,
                                                 String actorAssetIssuerToAddName,
                                                 String actorAssetIssuerToAddPublicKey,
                                                 byte[] profileImage) throws CantAskConnectionActorAssetException, CantRequestAlreadySendActorAssetException {

        try {
            if (assetIssuerActorDao.actorAssetRegisteredRequestExists(actorAssetIssuerToAddPublicKey, DAPConnectionState.CONNECTING)) {
                throw new CantRequestAlreadySendActorAssetException("CAN'T INSERT ACTOR ASSET USER", null, "", "The request already sent to actor.");
            } else if (assetIssuerActorDao.actorAssetRegisteredRequestExists(actorAssetIssuerToAddPublicKey, DAPConnectionState.PENDING_LOCALLY)) {
                this.assetIssuerActorDao.updateRegisteredConnectionState(
                        actorAssetIssuerToLinkPublicKey,
                        actorAssetIssuerToAddPublicKey,
                        DAPConnectionState.REGISTERED_ONLINE);
            } else {
                //TODO ANALIZAR PROBLEMAS QUE PUEDA OCASIONAR USAR CONNECTING O PENDING_REMOTELY
//                this.assetIssuerActorDao.createNewAssetIssuerRequestRegistered(
//                        actorAssetIssuerToLinkPublicKey,
//                        actorAssetIssuerToAddPublicKey,
//                        actorAssetIssuerToAddName,
//                        profileImage,
//                        DAPConnectionState.CONNECTING);
                this.updateIssuerRegisteredDAPConnectionState(actorAssetIssuerToAddPublicKey, DAPConnectionState.CONNECTING);
            }

        } catch (CantUpdateActorAssetIssuerException e) {
            e.printStackTrace();
        } catch (CantGetAssetUserActorsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void acceptActorAssetIssuer(String actorAssetIssuerInPublicKey, String actorAssetIssuerToAddPublicKey) throws CantAcceptActorAssetUserException {
        try {//TODO Probar el estado REGISTERED_LOCALLY
            this.assetIssuerActorDao.updateRegisteredConnectionState(actorAssetIssuerInPublicKey, actorAssetIssuerToAddPublicKey, DAPConnectionState.REGISTERED_ONLINE);
        } catch (CantUpdateActorAssetIssuerException e) {
            throw new CantAcceptActorAssetUserException("CAN'T ACCEPT ACTOR ASSET USER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantAcceptActorAssetUserException("CAN'T ACCEPT ACTOR ASSET USER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public void denyConnectionActorAssetIssuer(String actorAssetIssuerLoggedInPublicKey, String actorAssetIssuerToRejectPublicKey) throws CantDenyConnectionActorAssetException {
        try {
            this.assetIssuerActorDao.updateRegisteredConnectionState(actorAssetIssuerLoggedInPublicKey, actorAssetIssuerToRejectPublicKey, DAPConnectionState.DENIED_LOCALLY);
        } catch (CantUpdateActorAssetIssuerException e) {
            throw new CantDenyConnectionActorAssetException("CAN'T DENY ACTOR ASSET ISSUER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantDenyConnectionActorAssetException("CAN'T DENY ACTOR ASSET ISSUER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public void receivingActorAssetIssuerRequestConnection(String actorAssetIssuerLoggedInPublicKey,
                                                           String actorAssetIssuerToAddName,
                                                           String actorAssetIssuerToAddPublicKey,
                                                           byte[] profileImage) throws CantCreateActorAssetReceiveException {

        try {
            if (assetIssuerActorDao.actorAssetRegisteredRequestExists(actorAssetIssuerToAddPublicKey, DAPConnectionState.PENDING_REMOTELY)) {

                this.assetIssuerActorDao.updateRegisteredConnectionState(actorAssetIssuerLoggedInPublicKey, actorAssetIssuerToAddPublicKey, DAPConnectionState.REGISTERED_REMOTELY);
//                this.assetIssuerActorNetworkServiceManager.acceptConnectionActorAsset(actorAssetIssuerLoggedInPublicKey, actorAssetIssuerToAddPublicKey);
            } else {
                if (!assetIssuerActorDao.actorAssetRegisteredRequestExists(actorAssetIssuerToAddPublicKey, DAPConnectionState.REGISTERED_LOCALLY) ||
                        !assetIssuerActorDao.actorAssetRegisteredRequestExists(actorAssetIssuerToAddPublicKey, DAPConnectionState.PENDING_LOCALLY))
//                    this.assetIssuerActorDao.updateConnectionState(
//                            actorAssetUserLoggedInPublicKey,
//                            actorAssetUserToAddPublicKey,
//                            DAPConnectionState.PENDING_LOCALLY);

                    this.assetIssuerActorDao.createNewAssetIssuerRequestRegistered(
                            actorAssetIssuerLoggedInPublicKey,
                            actorAssetIssuerToAddPublicKey,
                            actorAssetIssuerToAddName,
                            profileImage,
                            DAPConnectionState.PENDING_LOCALLY);

            }
        } catch (CantUpdateActorAssetIssuerException e) {
            throw new CantCreateActorAssetReceiveException("CAN'T ADD NEW ACTOR ASSET USER REQUEST CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantCreateActorAssetReceiveException("CAN'T ADD NEW ACTOR ASSET USER REQUEST CONNECTION", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public void cancelActorAssetIssuer(String actorAssetUserLoggedInPublicKey, String actorAssetUserToCancelPublicKey) throws CantCancelConnectionActorAssetException {
        try {
            this.assetIssuerActorDao.updateRegisteredConnectionState(actorAssetUserLoggedInPublicKey, actorAssetUserToCancelPublicKey, DAPConnectionState.CANCELLED_LOCALLY);
        } catch (CantUpdateActorAssetIssuerException e) {
            throw new CantCancelConnectionActorAssetException("CAN'T CANCEL ACTOR ASSET ISSUER CONNECTION", e, "", "");
        } catch (Exception e) {
            throw new CantCancelConnectionActorAssetException("CAN'T CANCEL ACTOR ASSET ISSUER CONNECTION", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public List<ActorAssetIssuer> getWaitingYourConnectionActorAssetIssuer(String actorAssetIssuerLoggedPublicKey, int max, int offset) throws CantGetActorAssetWaitingException {
        try {
            return this.assetIssuerActorDao.getAllWaitingActorAssetIssuer(actorAssetIssuerLoggedPublicKey, DAPConnectionState.PENDING_LOCALLY, max, offset);
        } catch (CantGetAssetUserActorsException e) {
            throw new CantGetActorAssetWaitingException("CAN'T LIST ACTOR ASSET ISSUER ACCEPTED CONNECTIONS", e, "", "");
        } catch (Exception e) {
            throw new CantGetActorAssetWaitingException("CAN'T LIST ACTOR ASSET ISSUER ACCEPTED CONNECTIONS", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public List<ActorAssetIssuer> getWaitingTheirConnectionActorAssetIssuer(String actorAssetIssuerLoggedPublicKey, int max, int offset) throws CantGetActorAssetWaitingException {
        try {
            return this.assetIssuerActorDao.getAllWaitingActorAssetIssuer(actorAssetIssuerLoggedPublicKey, DAPConnectionState.PENDING_REMOTELY, max, offset);
        } catch (CantGetAssetUserActorsException e) {
            throw new CantGetActorAssetWaitingException("CAN'T LIST ACTOR ASSET ISSUER PENDING_HIS_ACCEPTANCE CONNECTIONS", e, "", "");
        } catch (Exception e) {
            throw new CantGetActorAssetWaitingException("CAN'T LIST ACTOR ASSET ISSUER PENDING_HIS_ACCEPTANCE CONNECTIONS", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public ActorAssetIssuer getLastNotificationActorAssetIssuer(String actorAssetIssuerPublicKey) throws CantGetActorAssetNotificationException {
        try {
            return assetIssuerActorDao.getLastNotification(actorAssetIssuerPublicKey);
        } catch (CantGetAssetUserActorsException e) {
            throw new CantGetActorAssetNotificationException("CAN'T GET ACTOR ASSET ISSUER LAST NOTIFICATION", e, "Error get database info", "");
        } catch (Exception e) {
            throw new CantGetActorAssetNotificationException("CAN'T GET ACTOR ASSET ISSUER LAST NOTIFICATION", FermatException.wrapException(e), "", "");
        }
    }

    public void registerActorInActorNetworkService() throws CantRegisterActorAssetIssuerException {
        try {
            /*
             * Send the Actor Asset Issuer Local for Register in Actor Network Service
             */
            ActorAssetIssuer actorAssetIssuer = this.assetIssuerActorDao.getActorAssetIssuer();

            if (actorAssetIssuer != null)
                assetIssuerActorNetworkServiceManager.registerActorAssetIssuer(actorAssetIssuer);

        } catch (CantRegisterActorAssetIssuerException e) {
            throw new CantRegisterActorAssetIssuerException("CAN'T Register Actor Asset Issuer in Actor Network Service", e, "", "");
        } catch (CantGetAssetIssuerActorsException e) {
            throw new CantRegisterActorAssetIssuerException("CAN'T GET ACTOR ASSET ISSUER", e, "", "");
        }
    }

    public void handleCompleteAssetIssuerActorRegistrationNotificationEvent(ActorAssetIssuer actorAssetIssuer) {
        System.out.println("***************************************************************");
        System.out.println("Actor Asset Issuer se Registro: " + actorAssetIssuer.getName());
        try {
            //TODO Cambiar luego por la publicKey Linked proveniente de Identity
            this.assetIssuerActorDao.updateAssetIssuerDAPConnectionState(
                    actorAssetIssuer.getActorPublicKey(),
                    actorAssetIssuer.getActorPublicKey(),
                    DAPConnectionState.REGISTERED_ONLINE);
        } catch (CantUpdateAssetIssuerException e) {
            e.printStackTrace();
        }
        System.out.println("***************************************************************");
    }

//    public void handleNewReceiveMessageActorNotificationEvent(DAPMessage dapMessage) {
//        switch (dapMessage.getMessageContent().messageType()) {
//            case EXTENDED_PUBLIC_KEY:
//                receiveNewRequestExtendedPublicKey(dapMessage);
//                break;
//            case ASSET_APPROPRIATION:
//                receiveNewAssetAppropriated(dapMessage);
//                break;
//        }
//    }

    private void receiveNewAssetAppropriated(DAPMessage dapMessage) {

    }

    private void newRequestExtendedPublicKey(ActorNotification actorNotification) {
        System.out.println("*****Actor Asset Redeem Point Solicita*****");
        System.out.println("Actor Asset Redeem Point Key: " + actorNotification.getActorSenderPublicKey());
        System.out.println("Actor Asset Redeem Point name: " + actorNotification.getActorSenderAlias());

        /**
         * I will request a new ExtendedPublicKey from the Asset Vault.
         * The asset vault will create a new extendedPublic Key for this redeem point.
         */
        ExtendedPublicKey extendedPublicKey = null;
        try {
            extendedPublicKey = assetVaultManager.getRedeemPointExtendedPublicKey(actorNotification.getActorSenderPublicKey());

        } catch (CantGetExtendedPublicKeyException e) {
            /**
             * if there was an error and we coulnd't get the ExtendedPublicKey, then we will send a null public Key
             * and this will be handle by the Redeem Point.
             * We might need to find a better way to handle this.
             */
            e.printStackTrace();
        }

        /**
         * I will create a new Message with the extended public Key.
         */
        if (extendedPublicKey != null) {
            RedeemerAddressesMonitorAgent monitorAgent = new RedeemerAddressesMonitorAgent(cryptoAddressBookManager, assetVaultManager, actorNotification.getActorDestinationPublicKey());
            try {
                monitorAgent.start();
            } catch (CantStartAgentException e) {
                /**
                 * If there was a problem, I will continue.
                 */
                e.printStackTrace();
            }

            System.out.println("*****Actor Asset Issuer ****: extended Public key generated");
            /**
             * and send it using the redeem point network service.
             */
            try {
                System.out.println("*****Actor Asset Issuer ****: enviando Event a Redeem Point");
                assetIssuerActorNetworkServiceManager.responseExtended(actorNotification, extendedPublicKey);
//            } catch (CantSetObjectException e) {
//                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveExtendedPublicKey(ActorNotification actorNotification) {
        FermatEvent event = eventManager.getNewEvent(EventType.NEW_RECEIVE_EXTENDED_KEY_ACTOR);
        event.setSource(EventSource.ACTOR_ASSET_ISSUER);
        ((NewReceiveExtendedNotificationEvent) event).setActorNotification(actorNotification);
        eventManager.raiseEvent(event);
    }

    /**
     * I will register the passed addresses in the address book
     *
     * @param cryptoAddresses
     */
    private void registerRedeemPointAddresses(List<CryptoAddress> cryptoAddresses, String issuerPublicKey, String reddemPointPublicKey) {
        for (CryptoAddress cryptoAddress : cryptoAddresses) {
            try {
                cryptoAddressBookManager.registerCryptoAddress(cryptoAddress, issuerPublicKey, Actors.DAP_ASSET_ISSUER, reddemPointPublicKey, Actors.DAP_ASSET_REDEEM_POINT, Platforms.DIGITAL_ASSET_PLATFORM, VaultType.WATCH_ONLY_VAULT, "WatchOnlyVault", "", ReferenceWallet.BASIC_WALLET_BITCOIN_WALLET);
            } catch (CantRegisterCryptoAddressBookRecordException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        AssetIssuerActorDeveloperDatabaseFactory dbFactory = new AssetIssuerActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return dbFactory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        AssetIssuerActorDeveloperDatabaseFactory dbFactory = new AssetIssuerActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return dbFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        try {
            AssetIssuerActorDeveloperDatabaseFactory dbFactory = new AssetIssuerActorDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
            dbFactory.initializeDatabase();
            return dbFactory.getDatabaseTableContent(developerObjectFactory, developerDatabaseTable);
        } catch (CantInitializeAssetIssuerActorDatabaseException e) {
            /**
             * The database exists but cannot be open. I can not handle this situation.
             */
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (Exception e) {
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_DAP_ASSET_USER_ACTOR, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
        // If we are here the database could not be opened, so we return an empry list
        return Collections.EMPTY_LIST;
    }

    /**
     * Private methods
     */
    private void startMonitorAgent() throws CantGetLoggedInDeviceUserException, CantStartAgentException {
        if (this.actorAssetIssuerMonitorAgent == null) {
//            String userPublicKey = this.deviceUserManager.getLoggedInDeviceUser().getActorPublicKey();
            this.actorAssetIssuerMonitorAgent = new ActorAssetIssuerMonitorAgent(this.eventManager,
                    this.pluginDatabaseSystem,
                    this.errorManager,
                    this.pluginId,
                    this.assetIssuerActorNetworkServiceManager,
                    this.assetIssuerActorDao,
                    this);
//            this.assetUserActorMonitorAgent.setLogManager(this.logManager);
            this.actorAssetIssuerMonitorAgent.start();
        } else {
            this.actorAssetIssuerMonitorAgent.start();
        }
    }

    private void initializeListener() {
        /**
         * I will initialize the handling of com.bitdubai.platform events.
         */
        FermatEventListener fermatEventListener;
        FermatEventHandler fermatEventHandler;

        /**
         * Listener Accepted connection event
         */
        fermatEventListener = eventManager.getNewListener(EventType.COMPLETE_ASSET_ISSUER_REGISTRATION_NOTIFICATION);
        fermatEventListener.setEventHandler(new ActorAssetIssuerCompleteRegistrationNotificationEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);

//        fermatEventListener = eventManager.getNewListener(EventType.NEW_RECEIVE_MESSAGE_ACTOR);
//        fermatEventListener.setEventHandler(new NewReceiveMessageActorIssuerNotificationEventHandler(this));
//        eventManager.addListener(fermatEventListener);
//        listenersAdded.add(fermatEventListener);

//        fermatEventListener = eventManager.getNewListener(EventType.NEW_RECEIVE_MESSAGE_ACTOR);
//        fermatEventListener.setEventHandler(new NewReceiveMessageEventHandler(this, actorAssetRedeemPointManager));
//        eventManager.addListener(fermatEventListener);
//        listenersAdded.add(fermatEventListener);

        fermatEventListener = eventManager.getNewListener(EventType.ACTOR_ASSET_NETWORK_SERVICE_NEW_NOTIFICATIONS);
        fermatEventListener.setEventHandler(new ActorAssetIssuerNewNotificationsEventHandler(this));
        eventManager.addListener(fermatEventListener);
        listenersAdded.add(fermatEventListener);
    }


    /**
     * Procces the list o f notifications from Intra User Network Services
     * And update intra user actor contact state
     *
     * @throws CantGetActorAssetNotificationException
     */
    public void processNotifications() throws CantGetActorAssetNotificationException {

        try {

            System.out.println("PROCESSING NOTIFICATIONS IN ACTOR ASSET ISSUER ");
            List<ActorNotification> actorNotifications = assetIssuerActorNetworkServiceManager.getPendingNotifications();


            for (ActorNotification notification : actorNotifications) {

                String intraUserSendingPublicKey = notification.getActorSenderPublicKey();

                String intraUserToConnectPublicKey = notification.getActorDestinationPublicKey();

                switch (notification.getAssetNotificationDescriptor()) {
                    case ASKFORCONNECTION:
                        this.receivingActorAssetIssuerRequestConnection(
                                intraUserToConnectPublicKey,
                                notification.getActorSenderAlias(),
//                                notification.getActorSenderPhrase(),
                                intraUserSendingPublicKey,
                                notification.getActorSenderProfileImage());
                        break;
                    case CANCEL:
                        this.cancelActorAssetIssuer(intraUserToConnectPublicKey, intraUserSendingPublicKey);
                        break;
                    case ACCEPTED:
                        this.acceptActorAssetIssuer(intraUserToConnectPublicKey, intraUserSendingPublicKey);
                        this.sendMessage(notification);
                        break;
//                    case DISCONNECTED:
////                        this.disconnectActorAssetUser(intraUserToConnectPublicKey, intraUserSendingPublicKey);
//                        this.disconnectToActorAssetUser(intraUserSendingPublicKey, notification.getBlockchainNetworkType());
//
//                        break;
                    case RECEIVED:
                        /**
                         * fire event "INTRA_USER_CONNECTION_REQUEST_RECEIVED_NOTIFICATION"
                         */
                        //eventManager.raiseEvent(eventManager.getNewEvent(EventType.INTRA_USER_CONNECTION_REQUEST_RECEIVED_NOTIFICATION));
                        break;
                    case DENIED:
                        this.denyConnectionActorAssetIssuer(intraUserToConnectPublicKey, intraUserSendingPublicKey);
                        break;
                    case ACTOR_ASSET_NOT_FOUND:
                        this.assetIssuerActorDao.updateRegisteredConnectionState(intraUserToConnectPublicKey, intraUserSendingPublicKey, DAPConnectionState.ERROR_UNKNOWN);
                        break;
                    case EXTENDED_KEY:
                        if (notification.getMessageXML() == null)
                            this.newRequestExtendedPublicKey(notification);
                        else
                            this.receiveExtendedPublicKey(notification);
                        break;
                    default:
                        break;
                }
                /**
                 * I confirm the application in the Network Service
                 */
                //TODO: VER PORQUE TIRA ERROR
                assetIssuerActorNetworkServiceManager.confirmActorAssetNotification(notification.getId());
            }
        } catch (CantAcceptActorAssetUserException e) {
            throw new CantGetActorAssetNotificationException("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", e, "", "Error Update Contact State to Accepted");
        } catch (CantDenyConnectionActorAssetException e) {
            throw new CantGetActorAssetNotificationException("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", e, "", "Error Update Contact State to Denied");
        } catch (Exception e) {
            throw new CantGetActorAssetNotificationException("CAN'T PROCESS NETWORK SERVICE NOTIFICATIONS", FermatException.wrapException(e), "", "");
        }
    }
}
