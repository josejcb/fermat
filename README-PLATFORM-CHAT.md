![alt text](https://github.com/bitDubai/media-kit/blob/master/Readme%20Image/Fermat%20Logotype/Fermat_Logo_3D.png "Fermat
Logo")

<br><br>
## Introduction: Platform Chat
  This component is a platform of text(chat) and voice messaging. 
  The exchanging messages between two or more users (or entities) work as stored supports related to a coin transaction(*) managed by Fermat.<br>
  (*) Also, it can be use as a instant messaging platform without a coin transaction reference, using its own contact list.
  
## Sup App
  TransactionList: List of transactions in process.<br>
  ChatInput: Input for text messaging.<br>
  MessagesList: List for voice and text messages access.<br>
  ContactsList: List for manage contacts.<br>
  VoiceRecordingInput: Input for voice messaging.
  
## Module
Transaction: To view transactions in process.<br>
  - Methods:<br>
      getTransactionList(): to ask for all the transactions in process of the actual entity.  <br>
      getTransactionDetail(): to ask for details of the selected transaction. <br>

SendText: To send message.<br>
  - Methods:<br>
      sendTextMessage(): to send a text message from the actual entity to the other entity related to the selected transaction in process.  <br>

Messages: To view text messages or play voice messages.<br>
  - Methods:<br>
      getMessageList(): to ask for all the messages of the actual entity related to the selected transaction.  <br>
      getMessageDetail(): to ask for details of the selected message. i.e: voice recording, images, date/time, etc.<br>

Contacts: To add, modify, delete and view contacts.<br>
  - Methods:<br>
      getContactList(): to ask for all the contacts saved in the platform due to previous transactions with the actual entity.  <br>
      getContactDetail(): to ask for details of the selected contact. i.e: previous transactions between the actual entity and the contact entity, name, etc.<br>
      addContact(): to add contact. <br>
      editContact(): to modify contact. <br>
      deleteContact(): to delete contact. <br>

SendVoice: To send voice message.<br>
  - Methods:<br>
      sendVoiceMessage(): to send a voice message from the actual entity to the other entity related to the selected transaction in process.  <br>

## Middleware

## Network Service
