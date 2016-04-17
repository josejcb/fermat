package com.bitdubai.fermat_cht_plugin.layer.sub_app_module.chat_community.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.actor_connection.common.enums.ConnectionState;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.ActorConnectionNotFoundException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantAcceptActorConnectionRequestException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantCancelActorConnectionRequestException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantDenyActorConnectionRequestException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantDisconnectFromActorException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantListActorConnectionsException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.ConnectionAlreadyRequestedException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.UnexpectedConnectionStateException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.UnsupportedActorTypeException;
import com.bitdubai.fermat_api.layer.actor_connection.common.structure_common_classes.ActorIdentityInformation;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;

import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantPersistSettingsException;

import com.bitdubai.fermat_api.layer.all_definition.settings.structure.SettingsManager;
import com.bitdubai.fermat_api.layer.modules.exceptions.ActorIdentityNotSelectedException;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_cht_api.layer.actor_connection.interfaces.ChatActorConnectionManager;
import com.bitdubai.fermat_cht_api.layer.actor_connection.interfaces.ChatActorConnectionSearch;
import com.bitdubai.fermat_cht_api.layer.actor_connection.utils.ChatActorConnection;
import com.bitdubai.fermat_cht_api.layer.actor_connection.utils.ChatLinkedActorIdentity;
import com.bitdubai.fermat_cht_api.layer.actor_network_service.exceptions.ConnectionRequestNotFoundException;
import com.bitdubai.fermat_cht_api.layer.actor_network_service.interfaces.ChatManager;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantListChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.identity.interfaces.ChatIdentity;
import com.bitdubai.fermat_cht_api.layer.identity.interfaces.ChatIdentityManager;

import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ActorChatConnectionAlreadyRequestesException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ActorChatTypeNotSupportedException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ActorConnectionRequestNotFoundException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantAcceptChatRequestException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantGetChtActorSearchResult;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantListChatActorException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantListChatIdentitiesToSelectException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantRequestActorConnectionException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantValidateActorConnectionStateException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ChatActorCancellingFailedException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ChatActorConnectionDenialFailedException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ChatActorDisconnectingFailedException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunityInformation;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunitySearch;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunitySelectableIdentity;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunitySubAppModuleManager;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.settings.ChatActorCommunitySettings;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Eleazar (eorono@protonmail.com) on 3/04/16.
 */
public class ChatActorCommunityManager implements ChatActorCommunitySubAppModuleManager, Serializable {

     private final ChatIdentityManager                   chatIdentityManager;
     private ChatActorCommunityInformation               chatActorCommunityManager             ;
     private final ChatActorConnectionManager            chatActorConnectionManager            ;
     private final ChatManager                           chatActorNetworkServiceManager        ;
     private String                                      subAppPublicKey                       ;
     private final ErrorManager                          errorManager                          ;
     private final PluginFileSystem                      pluginFileSystem                      ;
     private final UUID                                  pluginId                              ;
     private final PluginVersionReference                pluginVersionReference                ;
     private SettingsManager<ChatActorCommunitySettings> settingsManager                       ;

    public ChatActorCommunityManager(ChatIdentityManager chatIdentityManager, ChatActorConnectionManager chatActorConnectionManager, ChatManager chatActorNetworkServiceManager, ErrorManager errorManager, PluginFileSystem pluginFileSystem, UUID pluginId, PluginVersionReference pluginVersionReference) {
        this.chatIdentityManager= chatIdentityManager;
        this.chatActorConnectionManager=chatActorConnectionManager;
        this.chatActorNetworkServiceManager = chatActorNetworkServiceManager;
        this.chatActorCommunityManager = chatActorCommunityManager;
        this.errorManager = errorManager;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
        this.pluginVersionReference= pluginVersionReference;
    }

    @Override
    public List<ChatActorCommunityInformation> listWorldChatActor(ChatActorCommunitySelectableIdentity selectableIdentity, int max, int offset) throws CantListChatActorException {
        List<ChatActorCommunityInformation> worldActorList = null;
        List<ChatActorConnection> actorConnections = null;

        try{
         worldActorList = getChatActorSearch().getResult();
        } catch (CantGetChtActorSearchResult cantGetChtActorSearchResult) {
            cantGetChtActorSearchResult.printStackTrace();
        }

        try{
           final ChatLinkedActorIdentity linkedChatActorIdentity = new ChatLinkedActorIdentity(selectableIdentity.getPublicKey(), selectableIdentity.getActorType());
           final ChatActorConnectionSearch search = chatActorConnectionManager.getSearch(linkedChatActorIdentity);

            actorConnections = search.getResult(Integer.MAX_VALUE, 0);
        } catch (CantListActorConnectionsException e) {
            e.printStackTrace();
        }

        ChatActorCommunityInformation worldActor;
        for(int i = 0; i < worldActorList.size(); i++)
        {
            worldActor = worldActorList.get(i);
            for(ChatActorConnection connectedActor : actorConnections)
            {
                if(worldActor.getPublicKey().equals(connectedActor.getPublicKey()))
                    worldActorList.set(i, new ChatActorCommunitySubAppModuleInformationImpl(worldActor.getPublicKey(), worldActor.getAlias(), worldActor.getImage(), connectedActor.getConnectionState(), connectedActor.getConnectionId()));
            }
        }

        return worldActorList;
    }

    @Override
    public List<ChatActorCommunitySelectableIdentity> listSelectableIdentities() throws CantListChatIdentitiesToSelectException {

        List<ChatActorCommunitySelectableIdentity> selectableIdentities = null;
        try {
            selectableIdentities = new ArrayList<>();

            final List<ChatIdentity> chatActorIdentity = chatIdentityManager.getIdentityChatUsersFromCurrentDeviceUser();

            for (final ChatIdentity chi : chatActorIdentity)
                selectableIdentities.add(new ChatActorCommunitySelectableIdentityImpl(chi));


        } catch (CantListChatIdentityException e) {
            e.printStackTrace();
        }

        return selectableIdentities;

    }

    @Override
    public void setSelectedActorIdentity(ChatActorCommunitySelectableIdentity identity) {

        ChatActorCommunitySettings appSettings = null;
        try {
            appSettings = this.settingsManager.loadAndGetSettings(this.subAppPublicKey);
        }catch (Exception e){ appSettings = null; }

        //If appSettings exist, save identity
        if(appSettings != null){
            if(identity.getPublicKey() != null)
                appSettings.setLastSelectedIdentityPublicKey(identity.getPublicKey());
            if(identity.getActorType() != null)
                appSettings.setLastSelectedActorType(identity.getActorType());
            try {
                this.settingsManager.persistSettings(this.subAppPublicKey, appSettings);
            }catch (CantPersistSettingsException e){
                this.errorManager.reportUnexpectedPluginException(pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            }
        }
    }

    @Override
    public ChatActorCommunitySearch getChatActorSearch() {
        return new ChatActorCommunitySubAppModuleSearch(chatActorNetworkServiceManager) {
                  };
    }


    @Override
    public ChatActorCommunitySearch searchConnectedChatActor(ChatActorCommunitySelectableIdentity selectedIdentity) {
        return null;
    }

    @Override
    public void requestConnectionToChatActor(final ChatActorCommunitySelectableIdentity selectedIdentity,
                                             final ChatActorCommunityInformation chatActorToContact) throws CantRequestActorConnectionException, ActorChatTypeNotSupportedException, ActorChatConnectionAlreadyRequestesException {
        try {

            final ActorIdentityInformation actorSending = new ActorIdentityInformation(
                    selectedIdentity.getPublicKey(),
                    selectedIdentity.getActorType(),
                    selectedIdentity.getAlias(),
                    selectedIdentity.getImage()
            );

            final ActorIdentityInformation actorReceiving = new ActorIdentityInformation(
                    chatActorToContact.getPublicKey(),
                    Actors.CHT_CHAT_ACTOR,
                    chatActorToContact.getAlias(),
                    chatActorToContact.getImage()
            );

            chatActorConnectionManager.requestConnection(
                    actorSending,
                    actorReceiving
            );
        } catch (ConnectionAlreadyRequestedException e) {
            e.printStackTrace();
        } catch (com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantRequestActorConnectionException e) {
            e.printStackTrace();
        } catch (UnsupportedActorTypeException e) {
            e.printStackTrace();
        }
    }

        public void acceptChatActor(UUID requestId) throws CantAcceptChatRequestException, ActorConnectionRequestNotFoundException {

            try {

                chatActorConnectionManager.acceptConnection(requestId);

            } catch (CantAcceptActorConnectionRequestException e) {
                e.printStackTrace();
            } catch (ActorConnectionNotFoundException e) {
                e.printStackTrace();
            } catch (UnexpectedConnectionStateException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void denyChatConnection(UUID requestId) throws ChatActorConnectionDenialFailedException,
            ActorConnectionRequestNotFoundException {

        try {

            chatActorConnectionManager.denyConnection(requestId);

        } catch (CantDenyActorConnectionRequestException e) {
            e.printStackTrace();
        } catch (ActorConnectionNotFoundException e) {
            e.printStackTrace();
        } catch (UnexpectedConnectionStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disconnectChatActor(UUID requestId) throws ChatActorDisconnectingFailedException,
            ActorConnectionRequestNotFoundException, ConnectionRequestNotFoundException, CantDisconnectFromActorException, UnexpectedConnectionStateException, ActorConnectionNotFoundException {

        try {

            chatActorConnectionManager.disconnect(requestId);

        } catch (CantDisconnectFromActorException e) {
            e.printStackTrace();
        } catch (ActorConnectionNotFoundException e) {
            e.printStackTrace();
        } catch (UnexpectedConnectionStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cancelChatActor(UUID requestId) throws ChatActorCancellingFailedException,
            ActorConnectionRequestNotFoundException, ConnectionRequestNotFoundException {

        try {

            chatActorConnectionManager.cancelConnection(requestId);

        } catch (CantCancelActorConnectionRequestException e) {
            e.printStackTrace();
        } catch (ActorConnectionNotFoundException e) {
            e.printStackTrace();
        } catch (UnexpectedConnectionStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ChatActorCommunityInformation> listAllConnectedChatActor(ChatActorCommunitySelectableIdentity selectedIdentity, int max, int offset) throws CantListChatActorException {
        List<ChatActorCommunityInformation> chatActorCommunityInformationList = null;
        try{
            final ChatLinkedActorIdentity linkedChatActor = new ChatLinkedActorIdentity(
                    selectedIdentity.getPublicKey(),
                    selectedIdentity.getActorType()
            );

            final ChatActorConnectionSearch search = chatActorConnectionManager.getSearch(linkedChatActor);

            search.addConnectionState(ConnectionState.CONNECTED);

            final List<ChatActorConnection> actorConnections = search.getResult(max, offset);

             chatActorCommunityInformationList = new ArrayList<>();

            for (ChatActorConnection cac : actorConnections)
                chatActorCommunityInformationList.add(new ChatActorCommunitySubAppModuleInformationImpl(cac));



        } catch (CantListActorConnectionsException e) {
            e.printStackTrace();
        }
        return chatActorCommunityInformationList;

      }


    @Override
    public List<ChatActorCommunityInformation> listChatActorPendingLocalAction(ChatActorCommunitySelectableIdentity selectedIdentity, int max, int offset) throws CantListChatActorException {

        List<ChatActorCommunityInformation> chatActorCommunityInformationList = null;
        try {
            final ChatLinkedActorIdentity linkedChatActor = new ChatLinkedActorIdentity(
                    selectedIdentity.getPublicKey(),
                    selectedIdentity.getActorType()
            );

            final ChatActorConnectionSearch search = chatActorConnectionManager.getSearch(linkedChatActor);

            search.addConnectionState(ConnectionState.PENDING_LOCALLY_ACCEPTANCE);

            final List<ChatActorConnection> actorConnections = search.getResult(max, offset);

            chatActorCommunityInformationList = new ArrayList<>();

            for (ChatActorConnection cac : actorConnections)
                chatActorCommunityInformationList.add(new ChatActorCommunitySubAppModuleInformationImpl(cac));
        }

        catch(CantListActorConnectionsException e){
            e.printStackTrace();
        }

        return chatActorCommunityInformationList;
    }

    @Override
    public List<ChatActorCommunityInformation> listChatActorPendingRemoteAction(ChatActorCommunitySelectableIdentity selectedIdentity, int max, int offset) throws CantListChatActorException {
        List<ChatActorCommunityInformation> chatActorCommunityInformationList = null;
        try {
            final ChatLinkedActorIdentity linkedChatActor = new ChatLinkedActorIdentity(
                    selectedIdentity.getPublicKey(),
                    selectedIdentity.getActorType()
            );

            final ChatActorConnectionSearch search = chatActorConnectionManager.getSearch(linkedChatActor);

            search.addConnectionState(ConnectionState.PENDING_REMOTELY_ACCEPTANCE);

            final List<ChatActorConnection> actorConnections = search.getResult(max, offset);

            chatActorCommunityInformationList = new ArrayList<>();

            for (ChatActorConnection cac : actorConnections)
                chatActorCommunityInformationList.add(new ChatActorCommunitySubAppModuleInformationImpl(cac));
        }

        catch(CantListActorConnectionsException e){
            e.printStackTrace();
        }

        return chatActorCommunityInformationList;
    }

    @Override
    public int getChatActorWaitingYourAcceptanceCount() { return 0;  }

    @Override
    public ConnectionState getActorConnectionState(String publicKey) throws CantValidateActorConnectionStateException {

      try{
            ChatActorCommunitySelectableIdentity selectedIdentity = getSelectedActorIdentity();
          final ChatLinkedActorIdentity linkedChatActor = new ChatLinkedActorIdentity(selectedIdentity.getPublicKey(), selectedIdentity.getActorType());
          final ChatActorConnectionSearch search = chatActorConnectionManager.getSearch(linkedChatActor);
          final List<ChatActorConnection> actorConnections = search.getResult(Integer.MAX_VALUE,0);

          for (ChatActorConnection connection : actorConnections){
              if(publicKey.equals(connection.getPublicKey()))
                  return connection.getConnectionState();
          }

      } catch (final CantListActorConnectionsException e) {
          this.errorManager.reportUnexpectedPluginException(pluginVersionReference, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
          throw new CantValidateActorConnectionStateException(e, "", "Error trying to list actor connections.");
      } catch (Exception e) {}

        return ConnectionState.DISCONNECTED_REMOTELY;
    }

    @Override
    public SettingsManager<ChatActorCommunitySettings> getSettingsManager() {
        if (this.settingsManager != null)
            return this.settingsManager;

        this.settingsManager = new SettingsManager<>(
                pluginFileSystem,
                pluginId
        );

        return this.settingsManager;
    }

    @Override
    public ChatActorCommunitySelectableIdentity getSelectedActorIdentity() throws CantGetSelectedActorIdentityException, ActorIdentityNotSelectedException {
        //Try to get appSettings
        ChatActorCommunitySettings appSettings = null;
        try {
            appSettings = this.settingsManager.loadAndGetSettings(this.subAppPublicKey);
        }catch (Exception e){ return null; }

        List<ChatIdentity> IdentitiesInDevice = new ArrayList<>();
        try{
            IdentitiesInDevice = chatIdentityManager.getIdentityChatUsersFromCurrentDeviceUser();
        } catch(CantListChatIdentityException e) { /*Do nothing*/ }


        //If appSettings exists, get its selectedActorIdentityPublicKey property
        if(appSettings != null)
        {
            String lastSelectedIdentityPublicKey = appSettings.getLastSelectedIdentityPublicKey();
            Actors lastSelectedActorType = appSettings.getLastSelectedActorType();

            if (lastSelectedIdentityPublicKey != null && lastSelectedActorType != null) {

                ChatActorCommunitySelectableIdentityImpl selectedIdentity = null;

                if(lastSelectedActorType == Actors.CHT_CHAT_ACTOR)
                {
                    for(ChatIdentity i : IdentitiesInDevice) {
                        if(i.getPublicKey().equals(lastSelectedIdentityPublicKey))
                            selectedIdentity = new ChatActorCommunitySelectableIdentityImpl(i.getPublicKey(), Actors.CHT_CHAT_ACTOR, i.getAlias(), i.getImage());
                    }
                }
               if(selectedIdentity == null)
                    throw new ActorIdentityNotSelectedException("", null, "", "");

                return selectedIdentity;
            }
            else
                throw new ActorIdentityNotSelectedException("", null, "", "");
        }

        return null;
    }

    @Override
    public void createIdentity(String name, String phrase, byte[] profile_img) throws Exception {

    }


    @Override
    public void setAppPublicKey(String publicKey) {

    }

    @Override
    public int[] getMenuNotifications() {
        return new int[0];
    }
}