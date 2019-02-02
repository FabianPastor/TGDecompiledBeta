package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLClassStore;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.DecryptedMessageAction;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedFile;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaAudio;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument_layer8;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_encryptedFile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_acceptEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_discardEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC.TL_messages_requestEncryption;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncrypted;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedService;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_DhConfig;
import org.telegram.tgnet.TLRPC.messages_SentEncryptedMessage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

public class SecretChatHelper {
    public static final int CURRENT_SECRET_CHAT_LAYER = 73;
    private static volatile SecretChatHelper[] Instance = new SecretChatHelper[3];
    private SparseArray<EncryptedChat> acceptingChats = new SparseArray();
    private int currentAccount;
    public ArrayList<Update> delayedEncryptedChatUpdates = new ArrayList();
    private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList();
    private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray();
    private ArrayList<Integer> sendingNotifyLayer = new ArrayList();
    private boolean startingSecretChat = false;

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = NUM;
        public int date;
        public int decryptedWithVersion;
        public EncryptedFile file;
        public TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.layer = TL_decryptedMessageLayer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (stream.readBool(exception)) {
                this.file = EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.new_key_used = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(0);
            stream.writeInt32(this.date);
            this.layer.serializeToStream(stream);
            stream.writeBool(this.file != null);
            if (this.file != null) {
                this.file.serializeToStream(stream);
            }
            stream.writeBool(this.new_key_used);
        }
    }

    public static SecretChatHelper getInstance(int num) {
        Throwable th;
        SecretChatHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (SecretChatHelper.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        SecretChatHelper[] secretChatHelperArr = Instance;
                        SecretChatHelper localInstance2 = new SecretChatHelper(num);
                        try {
                            secretChatHelperArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public SecretChatHelper(int instance) {
        this.currentAccount = instance;
    }

    public void cleanup() {
        this.sendingNotifyLayer.clear();
        this.acceptingChats.clear();
        this.secretHolesQueue.clear();
        this.delayedEncryptedChatUpdates.clear();
        this.pendingEncMessagesToDelete.clear();
        this.startingSecretChat = false;
    }

    protected void processPendingEncMessages() {
        if (!this.pendingEncMessagesToDelete.isEmpty()) {
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$0(this, new ArrayList(this.pendingEncMessagesToDelete)));
            MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeletedByRandoms(new ArrayList(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
        }
    }

    final /* synthetic */ void lambda$processPendingEncMessages$0$SecretChatHelper(ArrayList pendingEncMessagesToDeleteCopy) {
        for (int a = 0; a < pendingEncMessagesToDeleteCopy.size(); a++) {
            MessageObject messageObject = (MessageObject) MessagesController.getInstance(this.currentAccount).dialogMessagesByRandomIds.get(((Long) pendingEncMessagesToDeleteCopy.get(a)).longValue());
            if (messageObject != null) {
                messageObject.deleted = true;
            }
        }
    }

    private TL_messageService createServiceSecretMessage(EncryptedChat encryptedChat, DecryptedMessageAction decryptedMessage) {
        TL_messageService newMsg = new TL_messageService();
        newMsg.action = new TL_messageEncryptedAction();
        newMsg.action.encryptedAction = decryptedMessage;
        int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
        newMsg.id = newMessageId;
        newMsg.local_id = newMessageId;
        newMsg.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = ((long) encryptedChat.id) << 32;
        newMsg.to_id = new TL_peerUser();
        newMsg.send_state = 1;
        if (encryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            newMsg.to_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.to_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessage instanceof TL_decryptedMessageActionScreenshotMessages) || (decryptedMessage instanceof TL_decryptedMessageActionSetMessageTTL)) {
            newMsg.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        } else {
            newMsg.date = 0;
        }
        newMsg.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        ArrayList arr = new ArrayList();
        arr.add(newMsg);
        MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, true, 0);
        return newMsg;
    }

    public void sendMessagesReadMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionReadMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    protected void processUpdateEncryption(TL_updateEncryption update, ConcurrentHashMap<Integer, User> usersDict) {
        EncryptedChat newChat = update.chat;
        long dialog_id = ((long) newChat.id) << 32;
        EncryptedChat existingChat = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(newChat.id, false);
        if ((newChat instanceof TL_encryptedChatRequested) && existingChat == null) {
            int user_id = newChat.participant_id;
            if (user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                user_id = newChat.admin_id;
            }
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(user_id));
            if (user == null) {
                user = (User) usersDict.get(Integer.valueOf(user_id));
            }
            newChat.user_id = user_id;
            TL_dialog dialog = new TL_dialog();
            dialog.id = dialog_id;
            dialog.unread_count = 0;
            dialog.top_message = 0;
            dialog.last_message_date = update.date;
            MessagesController.getInstance(this.currentAccount).putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$1(this, dialog));
            MessagesStorage.getInstance(this.currentAccount).putEncryptedChat(newChat, user, dialog);
            acceptSecretChat(newChat);
        } else if (!(newChat instanceof TL_encryptedChat)) {
            EncryptedChat exist = existingChat;
            if (exist != null) {
                newChat.user_id = exist.user_id;
                newChat.auth_key = exist.auth_key;
                newChat.key_create_date = exist.key_create_date;
                newChat.key_use_count_in = exist.key_use_count_in;
                newChat.key_use_count_out = exist.key_use_count_out;
                newChat.ttl = exist.ttl;
                newChat.seq_in = exist.seq_in;
                newChat.seq_out = exist.seq_out;
                newChat.admin_id = exist.admin_id;
                newChat.mtproto_seq = exist.mtproto_seq;
            }
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$2(this, exist, newChat));
        } else if (existingChat != null && (existingChat instanceof TL_encryptedChatWaiting) && (existingChat.auth_key == null || existingChat.auth_key.length == 1)) {
            newChat.a_or_b = existingChat.a_or_b;
            newChat.user_id = existingChat.user_id;
            processAcceptedSecretChat(newChat);
        } else if (existingChat == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(update);
        }
    }

    final /* synthetic */ void lambda$processUpdateEncryption$1$SecretChatHelper(TL_dialog dialog) {
        MessagesController.getInstance(this.currentAccount).dialogs_dict.put(dialog.id, dialog);
        MessagesController.getInstance(this.currentAccount).dialogs.add(dialog);
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    final /* synthetic */ void lambda$processUpdateEncryption$2$SecretChatHelper(EncryptedChat exist, EncryptedChat newChat) {
        if (exist != null) {
            MessagesController.getInstance(this.currentAccount).putEncryptedChat(newChat, false);
        }
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(newChat);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void sendMessagesDeleteMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionDeleteMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendClearHistoryMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendNotifyLayerMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if ((encryptedChat instanceof TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            Message message;
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionNotifyLayer();
                reqSend.action.layer = 73;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendRequestKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionRequestKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendAcceptKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionAcceptKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                reqSend.action.g_b = encryptedChat.g_a_or_b;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendCommitKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionCommitKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendAbortKeyMessage(EncryptedChat encryptedChat, Message resendMessage, long excange_id) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionAbortKey();
                reqSend.action.exchange_id = excange_id;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendNoopMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendTTLMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionSetMessageTTL();
                reqSend.action.ttl_seconds = encryptedChat.ttl;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(this.currentAccount, message, false);
                newMsgObj.messageOwner.send_state = 1;
                ArrayList<MessageObject> objArr = new ArrayList();
                objArr.add(newMsgObj);
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.dialog_id, objArr);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    public void sendScreenshotMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            TL_decryptedMessageService reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionScreenshotMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(this.currentAccount, message, false);
                newMsgObj.messageOwner.send_state = 1;
                ArrayList<MessageObject> objArr = new ArrayList();
                objArr.add(newMsgObj);
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.dialog_id, objArr);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
        }
    }

    private void updateMediaPaths(MessageObject newMsgObj, EncryptedFile file, DecryptedMessage decryptedMessage, String originalPath) {
        Message newMsg = newMsgObj.messageOwner;
        if (file == null) {
            return;
        }
        ArrayList arr;
        if ((newMsg.media instanceof TL_messageMediaPhoto) && newMsg.media.photo != null) {
            PhotoSize size = (PhotoSize) newMsg.media.photo.sizes.get(newMsg.media.photo.sizes.size() - 1);
            String fileName = size.location.volume_id + "_" + size.location.local_id;
            size.location = new TL_fileEncryptedLocation();
            size.location.key = decryptedMessage.media.key;
            size.location.iv = decryptedMessage.media.iv;
            size.location.dc_id = file.dc_id;
            size.location.volume_id = file.id;
            size.location.secret = file.access_hash;
            size.location.local_id = file.key_fingerprint;
            String fileName2 = size.location.volume_id + "_" + size.location.local_id;
            new File(FileLoader.getDirectory(4), fileName + ".jpg").renameTo(FileLoader.getPathToAttach(size));
            ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, true);
            arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
        } else if ((newMsg.media instanceof TL_messageMediaDocument) && newMsg.media.document != null) {
            Document document = newMsg.media.document;
            newMsg.media.document = new TL_documentEncrypted();
            newMsg.media.document.id = file.id;
            newMsg.media.document.access_hash = file.access_hash;
            newMsg.media.document.date = document.date;
            newMsg.media.document.attributes = document.attributes;
            newMsg.media.document.mime_type = document.mime_type;
            newMsg.media.document.size = file.size;
            newMsg.media.document.key = decryptedMessage.media.key;
            newMsg.media.document.iv = decryptedMessage.media.iv;
            newMsg.media.document.thumbs = document.thumbs;
            newMsg.media.document.dc_id = file.dc_id;
            if (newMsg.media.document.thumbs.isEmpty()) {
                PhotoSize thumb = new TL_photoSizeEmpty();
                thumb.type = "s";
                newMsg.media.document.thumbs.add(thumb);
            }
            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(newMsg.attachPath).renameTo(FileLoader.getPathToAttach(newMsg.media.document))) {
                newMsgObj.mediaExists = newMsgObj.attachPathExists;
                newMsgObj.attachPathExists = false;
                newMsg.attachPath = "";
            }
            arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
        }
    }

    public static boolean isSecretVisibleMessage(Message message) {
        return (message.action instanceof TL_messageEncryptedAction) && ((message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL));
    }

    public static boolean isSecretInvisibleMessage(Message message) {
        return (!(message.action instanceof TL_messageEncryptedAction) || (message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) ? false : true;
    }

    protected void performSendEncryptedRequest(TL_messages_sendEncryptedMultiMedia req, DelayedMessage message) {
        for (int a = 0; a < req.files.size(); a++) {
            performSendEncryptedRequest((DecryptedMessage) req.messages.get(a), (Message) message.messages.get(a), message.encryptedChat, (InputEncryptedFile) req.files.get(a), (String) message.originalPaths.get(a), (MessageObject) message.messageObjects.get(a));
        }
    }

    protected void performSendEncryptedRequest(DecryptedMessage req, Message newMsgObj, EncryptedChat chat, InputEncryptedFile encryptedFile, String originalPath, MessageObject newMsg) {
        if (req != null && chat.auth_key != null && !(chat instanceof TL_encryptedChatRequested) && !(chat instanceof TL_encryptedChatWaiting)) {
            SendMessagesHelper.getInstance(this.currentAccount).putToSendingMessages(newMsgObj);
            Utilities.stageQueue.postRunnable(new SecretChatHelper$$Lambda$3(this, chat, req, newMsgObj, encryptedFile, newMsg, originalPath));
        }
    }

    final /* synthetic */ void lambda$performSendEncryptedRequest$7$SecretChatHelper(EncryptedChat chat, DecryptedMessage req, Message newMsgObj, InputEncryptedFile encryptedFile, MessageObject newMsg, String originalPath) {
        try {
            TLObject reqToSend;
            TLObject layer = new TL_decryptedMessageLayer();
            layer.layer = Math.min(Math.max(46, AndroidUtilities.getMyLayerVersion(chat.layer)), Math.max(46, AndroidUtilities.getPeerLayerVersion(chat.layer)));
            layer.message = req;
            layer.random_bytes = new byte[15];
            Utilities.random.nextBytes(layer.random_bytes);
            TLObject toEncryptObject = layer;
            int mtprotoVersion = AndroidUtilities.getPeerLayerVersion(chat.layer) >= 73 ? 2 : 1;
            if (chat.seq_in == 0 && chat.seq_out == 0) {
                if (chat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    chat.seq_out = 1;
                    chat.seq_in = -2;
                } else {
                    chat.seq_in = -1;
                }
            }
            if (newMsgObj.seq_in == 0 && newMsgObj.seq_out == 0) {
                int i;
                if (chat.seq_in > 0) {
                    i = chat.seq_in;
                } else {
                    i = chat.seq_in + 2;
                }
                layer.in_seq_no = i;
                layer.out_seq_no = chat.seq_out;
                chat.seq_out += 2;
                if (AndroidUtilities.getPeerLayerVersion(chat.layer) >= 20) {
                    if (chat.key_create_date == 0) {
                        chat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    }
                    chat.key_use_count_out = (short) (chat.key_use_count_out + 1);
                    if ((chat.key_use_count_out >= (short) 100 || chat.key_create_date < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - 604800) && chat.exchange_id == 0 && chat.future_key_fingerprint == 0) {
                        requestNewSecretChatKey(chat);
                    }
                }
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatSeq(chat, false);
                if (newMsgObj != null) {
                    newMsgObj.seq_in = layer.in_seq_no;
                    newMsgObj.seq_out = layer.out_seq_no;
                    MessagesStorage.getInstance(this.currentAccount).setMessageSeq(newMsgObj.id, newMsgObj.seq_in, newMsgObj.seq_out);
                }
            } else {
                layer.in_seq_no = newMsgObj.seq_in;
                layer.out_seq_no = newMsgObj.seq_out;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d(req + " send message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
            }
            int len = toEncryptObject.getObjectSize();
            AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(len + 4);
            nativeByteBuffer.writeInt32(len);
            toEncryptObject.serializeToStream(nativeByteBuffer);
            len = nativeByteBuffer.length();
            int extraLen = len % 16 != 0 ? 16 - (len % 16) : 0;
            if (mtprotoVersion == 2) {
                extraLen += (Utilities.random.nextInt(3) + 2) * 16;
            }
            NativeByteBuffer dataForEncryption = new NativeByteBuffer(len + extraLen);
            nativeByteBuffer.position(0);
            dataForEncryption.writeBytes((NativeByteBuffer) nativeByteBuffer);
            if (extraLen != 0) {
                byte[] b = new byte[extraLen];
                Utilities.random.nextBytes(b);
                dataForEncryption.writeBytes(b);
            }
            Object messageKey = new byte[16];
            boolean incoming = mtprotoVersion == 2 && chat.admin_id != UserConfig.getInstance(this.currentAccount).getClientUserId();
            if (mtprotoVersion == 2) {
                System.arraycopy(Utilities.computeSHA256(chat.auth_key, (incoming ? 8 : 0) + 88, 32, dataForEncryption.buffer, 0, dataForEncryption.buffer.limit()), 8, messageKey, 0, 16);
            } else {
                Object messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer);
                System.arraycopy(messageKeyFull, messageKeyFull.length - 16, messageKey, 0, 16);
            }
            nativeByteBuffer.reuse();
            MessageKeyData keyData = MessageKeyData.generateMessageKeyData(chat.auth_key, messageKey, incoming, mtprotoVersion);
            Utilities.aesIgeEncryption(dataForEncryption.buffer, keyData.aesKey, keyData.aesIv, true, false, 0, dataForEncryption.limit());
            NativeByteBuffer data = new NativeByteBuffer((messageKey.length + 8) + dataForEncryption.length());
            dataForEncryption.position(0);
            data.writeInt64(chat.key_fingerprint);
            data.writeBytes((byte[]) messageKey);
            data.writeBytes(dataForEncryption);
            dataForEncryption.reuse();
            data.position(0);
            TLObject req2;
            if (encryptedFile != null) {
                req2 = new TL_messages_sendEncryptedFile();
                req2.data = data;
                req2.random_id = req.random_id;
                req2.peer = new TL_inputEncryptedChat();
                req2.peer.chat_id = chat.id;
                req2.peer.access_hash = chat.access_hash;
                req2.file = encryptedFile;
                reqToSend = req2;
            } else if (req instanceof TL_decryptedMessageService) {
                req2 = new TL_messages_sendEncryptedService();
                req2.data = data;
                req2.random_id = req.random_id;
                req2.peer = new TL_inputEncryptedChat();
                req2.peer.chat_id = chat.id;
                req2.peer.access_hash = chat.access_hash;
                reqToSend = req2;
            } else {
                req2 = new TL_messages_sendEncrypted();
                req2.data = data;
                req2.random_id = req.random_id;
                req2.peer = new TL_inputEncryptedChat();
                req2.peer.chat_id = chat.id;
                req2.peer.access_hash = chat.access_hash;
                reqToSend = req2;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(reqToSend, new SecretChatHelper$$Lambda$27(this, req, chat, newMsgObj, newMsg, originalPath), 64);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$null$6$SecretChatHelper(DecryptedMessage req, EncryptedChat chat, Message newMsgObj, MessageObject newMsg, String originalPath, TLObject response, TL_error error) {
        if (error == null && (req.action instanceof TL_decryptedMessageActionNotifyLayer)) {
            EncryptedChat currentChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(chat.id));
            if (currentChat == null) {
                currentChat = chat;
            }
            if (currentChat.key_hash == null) {
                currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
            }
            if (AndroidUtilities.getPeerLayerVersion(currentChat.layer) >= 46 && currentChat.key_hash.length == 16) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    currentChat.key_hash = key_hash;
                    MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(currentChat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            this.sendingNotifyLayer.remove(Integer.valueOf(currentChat.id));
            currentChat.layer = AndroidUtilities.setMyLayerVersion(currentChat.layer, 73);
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatLayer(currentChat);
        }
        if (newMsgObj == null) {
            return;
        }
        if (error == null) {
            int existFlags;
            String attachPath = newMsgObj.attachPath;
            messages_SentEncryptedMessage res = (messages_SentEncryptedMessage) response;
            if (isSecretVisibleMessage(newMsgObj)) {
                newMsgObj.date = res.date;
            }
            if (newMsg == null || !(res.file instanceof TL_encryptedFile)) {
                existFlags = 0;
            } else {
                updateMediaPaths(newMsg, res.file, req, originalPath);
                existFlags = newMsg.getMediaExistanceFlags();
            }
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SecretChatHelper$$Lambda$28(this, newMsgObj, res, existFlags, attachPath));
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsgObj);
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$29(this, newMsgObj));
    }

    final /* synthetic */ void lambda$null$4$SecretChatHelper(Message newMsgObj, messages_SentEncryptedMessage res, int existFlags, String attachPath) {
        if (isSecretInvisibleMessage(newMsgObj)) {
            res.date = 0;
        }
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(newMsgObj.id), newMsgObj.id, res.date, false, 0);
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$30(this, newMsgObj, existFlags, attachPath));
    }

    final /* synthetic */ void lambda$null$3$SecretChatHelper(Message newMsgObj, int existFlags, String attachPath) {
        newMsgObj.send_state = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(newMsgObj.id), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(0), Integer.valueOf(existFlags));
        SendMessagesHelper.getInstance(this.currentAccount).processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            SendMessagesHelper.getInstance(this.currentAccount).stopVideoService(attachPath);
        }
        SendMessagesHelper.getInstance(this.currentAccount).removeFromSendingMessages(newMsgObj.id);
    }

    final /* synthetic */ void lambda$null$5$SecretChatHelper(Message newMsgObj) {
        newMsgObj.send_state = 2;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
        SendMessagesHelper.getInstance(this.currentAccount).processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            SendMessagesHelper.getInstance(this.currentAccount).stopVideoService(newMsgObj.attachPath);
        }
        SendMessagesHelper.getInstance(this.currentAccount).removeFromSendingMessages(newMsgObj.id);
    }

    private void applyPeerLayer(EncryptedChat chat, int newPeerLayer) {
        int currentPeerLayer = AndroidUtilities.getPeerLayerVersion(chat.layer);
        if (newPeerLayer > currentPeerLayer) {
            if (chat.key_hash.length == 16 && currentPeerLayer >= 46) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    chat.key_hash = key_hash;
                    MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            chat.layer = AndroidUtilities.setPeerLayerVersion(chat.layer, newPeerLayer);
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatLayer(chat);
            if (currentPeerLayer < 73) {
                sendNotifyLayerMessage(chat, null);
            }
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$4(this, chat));
        }
    }

    final /* synthetic */ void lambda$applyPeerLayer$8$SecretChatHelper(EncryptedChat chat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, chat);
    }

    public Message processDecryptedObject(EncryptedChat chat, EncryptedFile file, int date, TLObject object, boolean new_key_used) {
        if (object != null) {
            int from_id = chat.admin_id;
            if (from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                from_id = chat.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(chat.layer) >= 20 && chat.exchange_id == 0 && chat.future_key_fingerprint == 0 && chat.key_use_count_in >= (short) 120) {
                requestNewSecretChatKey(chat);
            }
            if (chat.exchange_id == 0 && chat.future_key_fingerprint != 0 && !new_key_used) {
                chat.future_auth_key = new byte[256];
                chat.future_key_fingerprint = 0;
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
            } else if (chat.exchange_id != 0 && new_key_used) {
                chat.key_fingerprint = chat.future_key_fingerprint;
                chat.auth_key = chat.future_auth_key;
                chat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                chat.future_auth_key = new byte[256];
                chat.future_key_fingerprint = 0;
                chat.key_use_count_in = (short) 0;
                chat.key_use_count_out = (short) 0;
                chat.exchange_id = 0;
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
            }
            int newMessageId;
            if (object instanceof TL_decryptedMessage) {
                TL_message newMessage;
                MessageMedia messageMedia;
                TL_decryptedMessage decryptedMessage = (TL_decryptedMessage) object;
                if (AndroidUtilities.getPeerLayerVersion(chat.layer) >= 17) {
                    newMessage = new TL_message_secret();
                    newMessage.ttl = decryptedMessage.ttl;
                    newMessage.entities = decryptedMessage.entities;
                } else {
                    newMessage = new TL_message();
                    newMessage.ttl = chat.ttl;
                }
                newMessage.message = decryptedMessage.message;
                newMessage.date = date;
                newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                newMessage.id = newMessageId;
                newMessage.local_id = newMessageId;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                newMessage.from_id = from_id;
                newMessage.to_id = new TL_peerUser();
                newMessage.random_id = decryptedMessage.random_id;
                newMessage.to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                newMessage.unread = true;
                newMessage.flags = 768;
                if (decryptedMessage.via_bot_name != null && decryptedMessage.via_bot_name.length() > 0) {
                    newMessage.via_bot_name = decryptedMessage.via_bot_name;
                    newMessage.flags |= 2048;
                }
                if (decryptedMessage.grouped_id != 0) {
                    newMessage.grouped_id = decryptedMessage.grouped_id;
                    newMessage.flags |= 131072;
                }
                newMessage.dialog_id = ((long) chat.id) << 32;
                if (decryptedMessage.reply_to_random_id != 0) {
                    newMessage.reply_to_random_id = decryptedMessage.reply_to_random_id;
                    newMessage.flags |= 8;
                }
                byte[] thumb;
                PhotoSize photoSize;
                Document document;
                if (decryptedMessage.media == null || (decryptedMessage.media instanceof TL_decryptedMessageMediaEmpty)) {
                    newMessage.media = new TL_messageMediaEmpty();
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaWebPage) {
                    newMessage.media = new TL_messageMediaWebPage();
                    newMessage.media.webpage = new TL_webPageUrlPending();
                    newMessage.media.webpage.url = decryptedMessage.media.url;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaContact) {
                    newMessage.media = new TL_messageMediaContact();
                    newMessage.media.last_name = decryptedMessage.media.last_name;
                    newMessage.media.first_name = decryptedMessage.media.first_name;
                    newMessage.media.phone_number = decryptedMessage.media.phone_number;
                    newMessage.media.user_id = decryptedMessage.media.user_id;
                    newMessage.media.vcard = "";
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaGeoPoint) {
                    newMessage.media = new TL_messageMediaGeo();
                    newMessage.media.geo = new TL_geoPoint();
                    newMessage.media.geo.lat = decryptedMessage.media.lat;
                    newMessage.media.geo._long = decryptedMessage.media._long;
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaPhoto) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaPhoto();
                    messageMedia = newMessage.media;
                    messageMedia.flags |= 3;
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : "";
                    newMessage.media.photo = new TL_photo();
                    newMessage.media.photo.file_reference = new byte[0];
                    newMessage.media.photo.date = newMessage.date;
                    thumb = ((TL_decryptedMessageMediaPhoto) decryptedMessage.media).thumb;
                    if (thumb != null && thumb.length != 0 && thumb.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                        TL_photoCachedSize small = new TL_photoCachedSize();
                        small.w = decryptedMessage.media.thumb_w;
                        small.h = decryptedMessage.media.thumb_h;
                        small.bytes = thumb;
                        small.type = "s";
                        small.location = new TL_fileLocationUnavailable();
                        newMessage.media.photo.sizes.add(small);
                    }
                    if (newMessage.ttl != 0) {
                        newMessage.media.ttl_seconds = newMessage.ttl;
                        messageMedia = newMessage.media;
                        messageMedia.flags |= 4;
                    }
                    TL_photoSize big = new TL_photoSize();
                    big.w = decryptedMessage.media.w;
                    big.h = decryptedMessage.media.h;
                    big.type = "x";
                    big.size = file.size;
                    big.location = new TL_fileEncryptedLocation();
                    big.location.key = decryptedMessage.media.key;
                    big.location.iv = decryptedMessage.media.iv;
                    big.location.dc_id = file.dc_id;
                    big.location.volume_id = file.id;
                    big.location.secret = file.access_hash;
                    big.location.local_id = file.key_fingerprint;
                    newMessage.media.photo.sizes.add(big);
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaVideo) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaDocument();
                    messageMedia = newMessage.media;
                    messageMedia.flags |= 3;
                    newMessage.media.document = new TL_documentEncrypted();
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    newMessage.media.document.dc_id = file.dc_id;
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : "";
                    newMessage.media.document.date = date;
                    newMessage.media.document.size = file.size;
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = "video/mp4";
                    }
                    thumb = ((TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                    if (thumb == null || thumb.length == 0 || thumb.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                        photoSize = new TL_photoSizeEmpty();
                        photoSize.type = "s";
                    } else {
                        photoSize = new TL_photoCachedSize();
                        photoSize.bytes = thumb;
                        photoSize.w = decryptedMessage.media.thumb_w;
                        photoSize.h = decryptedMessage.media.thumb_h;
                        photoSize.type = "s";
                        photoSize.location = new TL_fileLocationUnavailable();
                    }
                    newMessage.media.document.thumbs.add(photoSize);
                    document = newMessage.media.document;
                    document.flags |= 1;
                    TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                    attributeVideo.w = decryptedMessage.media.w;
                    attributeVideo.h = decryptedMessage.media.h;
                    attributeVideo.duration = decryptedMessage.media.duration;
                    attributeVideo.supports_streaming = false;
                    newMessage.media.document.attributes.add(attributeVideo);
                    if (newMessage.ttl != 0) {
                        newMessage.media.ttl_seconds = newMessage.ttl;
                        messageMedia = newMessage.media;
                        messageMedia.flags |= 4;
                    }
                    if (newMessage.ttl != 0) {
                        newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                    }
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaDocument();
                    messageMedia = newMessage.media;
                    messageMedia.flags |= 3;
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : "";
                    newMessage.media.document = new TL_documentEncrypted();
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.date = date;
                    if (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument_layer8) {
                        TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                        fileName.file_name = decryptedMessage.media.file_name;
                        newMessage.media.document.attributes.add(fileName);
                    } else {
                        newMessage.media.document.attributes = decryptedMessage.media.attributes;
                    }
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    Document document2 = newMessage.media.document;
                    if (decryptedMessage.media.size != 0) {
                        newMessageId = Math.min(decryptedMessage.media.size, file.size);
                    } else {
                        newMessageId = file.size;
                    }
                    document2.size = newMessageId;
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = "";
                    }
                    thumb = ((TL_decryptedMessageMediaDocument) decryptedMessage.media).thumb;
                    if (thumb == null || thumb.length == 0 || thumb.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                        photoSize = new TL_photoSizeEmpty();
                        photoSize.type = "s";
                    } else {
                        photoSize = new TL_photoCachedSize();
                        photoSize.bytes = thumb;
                        photoSize.w = decryptedMessage.media.thumb_w;
                        photoSize.h = decryptedMessage.media.thumb_h;
                        photoSize.type = "s";
                        photoSize.location = new TL_fileLocationUnavailable();
                    }
                    newMessage.media.document.thumbs.add(photoSize);
                    document = newMessage.media.document;
                    document.flags |= 1;
                    newMessage.media.document.dc_id = file.dc_id;
                    if (MessageObject.isVoiceMessage(newMessage) || MessageObject.isRoundVideoMessage(newMessage)) {
                        newMessage.media_unread = true;
                    }
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaExternalDocument) {
                    newMessage.media = new TL_messageMediaDocument();
                    messageMedia = newMessage.media;
                    messageMedia.flags |= 3;
                    newMessage.message = "";
                    newMessage.media.document = new TL_document();
                    newMessage.media.document.id = decryptedMessage.media.id;
                    newMessage.media.document.access_hash = decryptedMessage.media.access_hash;
                    newMessage.media.document.file_reference = new byte[0];
                    newMessage.media.document.date = decryptedMessage.media.date;
                    newMessage.media.document.attributes = decryptedMessage.media.attributes;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    newMessage.media.document.dc_id = decryptedMessage.media.dc_id;
                    newMessage.media.document.size = decryptedMessage.media.size;
                    newMessage.media.document.thumbs.add(((TL_decryptedMessageMediaExternalDocument) decryptedMessage.media).thumb);
                    document = newMessage.media.document;
                    document.flags |= 1;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = "";
                    }
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaAudio) {
                    if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    }
                    newMessage.media = new TL_messageMediaDocument();
                    messageMedia = newMessage.media;
                    messageMedia.flags |= 3;
                    newMessage.media.document = new TL_documentEncrypted();
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.date = date;
                    newMessage.media.document.size = file.size;
                    newMessage.media.document.dc_id = file.dc_id;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : "";
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = "audio/ogg";
                    }
                    TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                    attributeAudio.duration = decryptedMessage.media.duration;
                    attributeAudio.voice = true;
                    newMessage.media.document.attributes.add(attributeAudio);
                    if (newMessage.ttl != 0) {
                        newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                    }
                    if (newMessage.media.document.thumbs.isEmpty()) {
                        PhotoSize thumb2 = new TL_photoSizeEmpty();
                        thumb2.type = "s";
                        newMessage.media.document.thumbs.add(thumb2);
                    }
                } else if (!(decryptedMessage.media instanceof TL_decryptedMessageMediaVenue)) {
                    return null;
                } else {
                    newMessage.media = new TL_messageMediaVenue();
                    newMessage.media.geo = new TL_geoPoint();
                    newMessage.media.geo.lat = decryptedMessage.media.lat;
                    newMessage.media.geo._long = decryptedMessage.media._long;
                    newMessage.media.title = decryptedMessage.media.title;
                    newMessage.media.address = decryptedMessage.media.address;
                    newMessage.media.provider = decryptedMessage.media.provider;
                    newMessage.media.venue_id = decryptedMessage.media.venue_id;
                    newMessage.media.venue_type = "";
                }
                if (newMessage.ttl == 0 || newMessage.media.ttl_seconds != 0) {
                    return newMessage;
                }
                newMessage.media.ttl_seconds = newMessage.ttl;
                messageMedia = newMessage.media;
                messageMedia.flags |= 4;
                return newMessage;
            } else if (object instanceof TL_decryptedMessageService) {
                TL_decryptedMessageService serviceMessage = (TL_decryptedMessageService) object;
                BigInteger bigInteger;
                Object correctedAuth;
                byte[] authKey;
                int a;
                Object authKey2;
                byte[] authKeyHash;
                byte[] authKeyId;
                if ((serviceMessage.action instanceof TL_decryptedMessageActionSetMessageTTL) || (serviceMessage.action instanceof TL_decryptedMessageActionScreenshotMessages)) {
                    Message newMessage2 = new TL_messageService();
                    if (serviceMessage.action instanceof TL_decryptedMessageActionSetMessageTTL) {
                        newMessage2.action = new TL_messageEncryptedAction();
                        if (serviceMessage.action.ttl_seconds < 0 || serviceMessage.action.ttl_seconds > 31536000) {
                            serviceMessage.action.ttl_seconds = 31536000;
                        }
                        chat.ttl = serviceMessage.action.ttl_seconds;
                        newMessage2.action.encryptedAction = serviceMessage.action;
                        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatTTL(chat);
                    } else if (serviceMessage.action instanceof TL_decryptedMessageActionScreenshotMessages) {
                        newMessage2.action = new TL_messageEncryptedAction();
                        newMessage2.action.encryptedAction = serviceMessage.action;
                    }
                    newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                    newMessage2.id = newMessageId;
                    newMessage2.local_id = newMessageId;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    newMessage2.unread = true;
                    newMessage2.flags = 256;
                    newMessage2.date = date;
                    newMessage2.from_id = from_id;
                    newMessage2.to_id = new TL_peerUser();
                    newMessage2.to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    newMessage2.dialog_id = ((long) chat.id) << 32;
                    return newMessage2;
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionFlushHistory) {
                    AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$5(this, ((long) chat.id) << 32));
                    return null;
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionDeleteMessages) {
                    if (!serviceMessage.action.random_ids.isEmpty()) {
                        this.pendingEncMessagesToDelete.addAll(serviceMessage.action.random_ids);
                    }
                    return null;
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionReadMessages) {
                    if (!serviceMessage.action.random_ids.isEmpty()) {
                        int time = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                        MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(chat.id, time, time, 1, serviceMessage.action.random_ids);
                    }
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionNotifyLayer) {
                    applyPeerLayer(chat, serviceMessage.action.layer);
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionRequestKey) {
                    if (chat.exchange_id != 0) {
                        if (chat.exchange_id > serviceMessage.action.exchange_id) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("we already have request key with higher exchange_id");
                            }
                            return null;
                        }
                        sendAbortKeyMessage(chat, null, chat.exchange_id);
                    }
                    byte[] salt = new byte[256];
                    Utilities.random.nextBytes(salt);
                    bigInteger = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                    BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), bigInteger);
                    bigInteger = new BigInteger(1, serviceMessage.action.g_a);
                    if (Utilities.isGoodGaAndGb(bigInteger, bigInteger)) {
                        byte[] g_b_bytes = g_b.toByteArray();
                        if (g_b_bytes.length > 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                            g_b_bytes = correctedAuth;
                        }
                        authKey2 = bigInteger.modPow(new BigInteger(1, salt), bigInteger).toByteArray();
                        if (authKey2.length > 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(authKey2, authKey2.length - 256, correctedAuth, 0, 256);
                            authKey2 = correctedAuth;
                        } else if (authKey2.length < 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(authKey2, 0, correctedAuth, 256 - authKey2.length, authKey2.length);
                            for (a = 0; a < 256 - authKey2.length; a++) {
                                correctedAuth[a] = (byte) 0;
                            }
                            authKey2 = correctedAuth;
                        }
                        authKeyHash = Utilities.computeSHA1(authKey2);
                        authKeyId = new byte[8];
                        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                        chat.exchange_id = serviceMessage.action.exchange_id;
                        chat.future_auth_key = authKey2;
                        chat.future_key_fingerprint = Utilities.bytesToLong(authKeyId);
                        chat.g_a_or_b = g_b_bytes;
                        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                        sendAcceptKeyMessage(chat, null);
                    } else {
                        sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                        return null;
                    }
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionAcceptKey) {
                    if (chat.exchange_id == serviceMessage.action.exchange_id) {
                        bigInteger = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                        bigInteger = new BigInteger(1, serviceMessage.action.g_b);
                        if (Utilities.isGoodGaAndGb(bigInteger, bigInteger)) {
                            authKey2 = bigInteger.modPow(new BigInteger(1, chat.a_or_b), bigInteger).toByteArray();
                            if (authKey2.length > 256) {
                                correctedAuth = new byte[256];
                                System.arraycopy(authKey2, authKey2.length - 256, correctedAuth, 0, 256);
                                authKey2 = correctedAuth;
                            } else if (authKey2.length < 256) {
                                correctedAuth = new byte[256];
                                System.arraycopy(authKey2, 0, correctedAuth, 256 - authKey2.length, authKey2.length);
                                for (a = 0; a < 256 - authKey2.length; a++) {
                                    correctedAuth[a] = (byte) 0;
                                }
                                authKey2 = correctedAuth;
                            }
                            authKeyHash = Utilities.computeSHA1(authKey2);
                            authKeyId = new byte[8];
                            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                            long fingerprint = Utilities.bytesToLong(authKeyId);
                            if (serviceMessage.action.key_fingerprint == fingerprint) {
                                chat.future_auth_key = authKey2;
                                chat.future_key_fingerprint = fingerprint;
                                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                                sendCommitKeyMessage(chat, null);
                            } else {
                                chat.future_auth_key = new byte[256];
                                chat.future_key_fingerprint = 0;
                                chat.exchange_id = 0;
                                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                                sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                            }
                        } else {
                            chat.future_auth_key = new byte[256];
                            chat.future_key_fingerprint = 0;
                            chat.exchange_id = 0;
                            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                            sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                            return null;
                        }
                    }
                    chat.future_auth_key = new byte[256];
                    chat.future_key_fingerprint = 0;
                    chat.exchange_id = 0;
                    MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                    sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionCommitKey) {
                    if (chat.exchange_id == serviceMessage.action.exchange_id && chat.future_key_fingerprint == serviceMessage.action.key_fingerprint) {
                        long old_fingerpring = chat.key_fingerprint;
                        byte[] old_key = chat.auth_key;
                        chat.key_fingerprint = chat.future_key_fingerprint;
                        chat.auth_key = chat.future_auth_key;
                        chat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                        chat.future_auth_key = old_key;
                        chat.future_key_fingerprint = old_fingerpring;
                        chat.key_use_count_in = (short) 0;
                        chat.key_use_count_out = (short) 0;
                        chat.exchange_id = 0;
                        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                        sendNoopMessage(chat, null);
                    } else {
                        chat.future_auth_key = new byte[256];
                        chat.future_key_fingerprint = 0;
                        chat.exchange_id = 0;
                        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                        sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                    }
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionAbortKey) {
                    if (chat.exchange_id == serviceMessage.action.exchange_id) {
                        chat.future_auth_key = new byte[256];
                        chat.future_key_fingerprint = 0;
                        chat.exchange_id = 0;
                        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(chat);
                    }
                } else if (!(serviceMessage.action instanceof TL_decryptedMessageActionNoop)) {
                    if (!(serviceMessage.action instanceof TL_decryptedMessageActionResend)) {
                        return null;
                    }
                    if (serviceMessage.action.end_seq_no < chat.in_seq_no || serviceMessage.action.end_seq_no < serviceMessage.action.start_seq_no) {
                        return null;
                    }
                    if (serviceMessage.action.start_seq_no < chat.in_seq_no) {
                        serviceMessage.action.start_seq_no = chat.in_seq_no;
                    }
                    resendMessages(serviceMessage.action.start_seq_no, serviceMessage.action.end_seq_no, chat);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e("unknown message " + object);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.e("unknown TLObject");
        }
        return null;
    }

    final /* synthetic */ void lambda$processDecryptedObject$11$SecretChatHelper(long did) {
        TL_dialog dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
        if (dialog != null) {
            dialog.unread_count = 0;
            MessagesController.getInstance(this.currentAccount).dialogMessage.remove(dialog.id);
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SecretChatHelper$$Lambda$25(this, did));
        MessagesStorage.getInstance(this.currentAccount).deleteDialog(did, 1);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), Boolean.valueOf(false));
    }

    final /* synthetic */ void lambda$null$10$SecretChatHelper(long did) {
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$26(this, did));
    }

    final /* synthetic */ void lambda$null$9$SecretChatHelper(long did) {
        NotificationsController.getInstance(this.currentAccount).processReadMessages(null, did, 0, Integer.MAX_VALUE, false);
        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
        dialogsToUpdate.put(did, Integer.valueOf(0));
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
    }

    private Message createDeleteMessage(int mid, int seq_out, int seq_in, long random_id, EncryptedChat encryptedChat) {
        TL_messageService newMsg = new TL_messageService();
        newMsg.action = new TL_messageEncryptedAction();
        newMsg.action.encryptedAction = new TL_decryptedMessageActionDeleteMessages();
        newMsg.action.encryptedAction.random_ids.add(Long.valueOf(random_id));
        newMsg.id = mid;
        newMsg.local_id = mid;
        newMsg.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = ((long) encryptedChat.id) << 32;
        newMsg.to_id = new TL_peerUser();
        newMsg.send_state = 1;
        newMsg.seq_in = seq_in;
        newMsg.seq_out = seq_out;
        if (encryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            newMsg.to_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.to_id.user_id = encryptedChat.participant_id;
        }
        newMsg.date = 0;
        newMsg.random_id = random_id;
        return newMsg;
    }

    private void resendMessages(int startSeq, int endSeq, EncryptedChat encryptedChat) {
        if (encryptedChat != null && endSeq - startSeq >= 0) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SecretChatHelper$$Lambda$6(this, startSeq, encryptedChat, endSeq));
        }
    }

    final /* synthetic */ void lambda$resendMessages$14$SecretChatHelper(int startSeq, EncryptedChat encryptedChat, int endSeq) {
        int sSeq = startSeq;
        try {
            if (encryptedChat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId() && sSeq % 2 == 0) {
                sSeq++;
            }
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(encryptedChat.id), Integer.valueOf(sSeq), Integer.valueOf(sSeq), Integer.valueOf(endSeq), Integer.valueOf(endSeq)}), new Object[0]);
            boolean exists = cursor.next();
            cursor.dispose();
            if (!exists) {
                int a;
                long dialog_id = ((long) encryptedChat.id) << 32;
                SparseArray<Message> messagesToResend = new SparseArray();
                ArrayList<Message> messages = new ArrayList();
                for (a = sSeq; a < endSeq; a += 2) {
                    messagesToResend.put(a, null);
                }
                cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)}), new Object[0]);
                while (cursor.next()) {
                    Message message;
                    long random_id = cursor.longValue(1);
                    if (random_id == 0) {
                        random_id = Utilities.random.nextLong();
                    }
                    int seq_in = cursor.intValue(2);
                    int seq_out = cursor.intValue(3);
                    int mid = cursor.intValue(5);
                    AbstractSerializedData data = cursor.byteBufferValue(0);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        message.random_id = random_id;
                        message.dialog_id = dialog_id;
                        message.seq_in = seq_in;
                        message.seq_out = seq_out;
                        message.ttl = cursor.intValue(4);
                    } else {
                        message = createDeleteMessage(mid, seq_out, seq_in, random_id, encryptedChat);
                    }
                    messages.add(message);
                    messagesToResend.remove(seq_out);
                }
                cursor.dispose();
                if (messagesToResend.size() != 0) {
                    for (a = 0; a < messagesToResend.size(); a++) {
                        ArrayList<Message> arrayList = messages;
                        arrayList.add(createDeleteMessage(UserConfig.getInstance(this.currentAccount).getNewMessageId(), messagesToResend.keyAt(a), 0, Utilities.random.nextLong(), encryptedChat));
                    }
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                }
                Collections.sort(messages, SecretChatHelper$$Lambda$23.$instance);
                ArrayList<EncryptedChat> encryptedChats = new ArrayList();
                encryptedChats.add(encryptedChat);
                AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$24(this, messages));
                SendMessagesHelper.getInstance(this.currentAccount).processUnsentMessages(messages, new ArrayList(), new ArrayList(), encryptedChats);
                MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(encryptedChat.id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)})).stepThis().dispose();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$null$13$SecretChatHelper(ArrayList messages) {
        for (int a = 0; a < messages.size(); a++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (Message) messages.get(a), false);
            messageObject.resendAsIs = true;
            SendMessagesHelper.getInstance(this.currentAccount).retrySendMessage(messageObject, true);
        }
    }

    public void checkSecretHoles(EncryptedChat chat, ArrayList<Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = (ArrayList) this.secretHolesQueue.get(chat.id);
        if (holes != null) {
            Collections.sort(holes, SecretChatHelper$$Lambda$7.$instance);
            boolean update = false;
            int a = 0;
            while (holes.size() > 0) {
                TL_decryptedMessageHolder holder = (TL_decryptedMessageHolder) holes.get(a);
                if (holder.layer.out_seq_no != chat.seq_in && chat.seq_in != holder.layer.out_seq_no - 2) {
                    break;
                }
                applyPeerLayer(chat, holder.layer.layer);
                chat.seq_in = holder.layer.out_seq_no;
                chat.in_seq_no = holder.layer.in_seq_no;
                holes.remove(a);
                a--;
                update = true;
                if (holder.decryptedWithVersion == 2) {
                    chat.mtproto_seq = Math.min(chat.mtproto_seq, chat.seq_in);
                }
                Message message = processDecryptedObject(chat, holder.file, holder.date, holder.layer.message, holder.new_key_used);
                if (message != null) {
                    messages.add(message);
                }
                a++;
            }
            if (holes.isEmpty()) {
                this.secretHolesQueue.remove(chat.id);
            }
            if (update) {
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatSeq(chat, true);
            }
        }
    }

    static final /* synthetic */ int lambda$checkSecretHoles$15$SecretChatHelper(TL_decryptedMessageHolder lhs, TL_decryptedMessageHolder rhs) {
        if (lhs.layer.out_seq_no > rhs.layer.out_seq_no) {
            return 1;
        }
        if (lhs.layer.out_seq_no < rhs.layer.out_seq_no) {
            return -1;
        }
        return 0;
    }

    private boolean decryptWithMtProtoVersion(NativeByteBuffer is, byte[] keyToDecrypt, byte[] messageKey, int version, boolean incoming, boolean encryptOnError) {
        if (version == 1) {
            incoming = false;
        }
        MessageKeyData keyData = MessageKeyData.generateMessageKeyData(keyToDecrypt, messageKey, incoming, version);
        Utilities.aesIgeEncryption(is.buffer, keyData.aesKey, keyData.aesIv, false, false, 24, is.limit() - 24);
        int len = is.readInt32(false);
        if (version == 2) {
            if (!Utilities.arraysEquals(messageKey, 0, Utilities.computeSHA256(keyToDecrypt, (incoming ? 8 : 0) + 88, 32, is.buffer, 24, is.buffer.limit()), 8)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(is.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    is.position(24);
                }
                return false;
            }
        }
        int l = len + 28;
        if (l < is.buffer.limit() - 15 || l > is.buffer.limit()) {
            l = is.buffer.limit();
        }
        byte[] messageKeyFull = Utilities.computeSHA1(is.buffer, 24, l);
        if (!Utilities.arraysEquals(messageKey, 0, messageKeyFull, messageKeyFull.length - 16)) {
            if (encryptOnError) {
                Utilities.aesIgeEncryption(is.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                is.position(24);
            }
            return false;
        }
        if (len <= 0 || len > is.limit() - 28) {
            return false;
        }
        int padding = (is.limit() - 28) - len;
        if ((version != 2 || (padding >= 12 && padding <= 1024)) && (version != 1 || padding <= 15)) {
            return true;
        }
        return false;
    }

    protected ArrayList<Message> decryptMessage(EncryptedMessage message) {
        EncryptedChat chat = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(message.chat_id, true);
        if (chat == null || (chat instanceof TL_encryptedChatDiscarded)) {
            return null;
        }
        try {
            NativeByteBuffer is = new NativeByteBuffer(message.bytes.length);
            is.writeBytes(message.bytes);
            is.position(0);
            long fingerprint = is.readInt64(false);
            byte[] keyToDecrypt = null;
            boolean new_key_used = false;
            if (chat.key_fingerprint == fingerprint) {
                keyToDecrypt = chat.auth_key;
            } else if (chat.future_key_fingerprint != 0 && chat.future_key_fingerprint == fingerprint) {
                keyToDecrypt = chat.future_auth_key;
                new_key_used = true;
            }
            int mtprotoVersion = AndroidUtilities.getPeerLayerVersion(chat.layer) >= 73 ? 2 : 1;
            int decryptedWithVersion = mtprotoVersion;
            if (keyToDecrypt != null) {
                byte[] messageKey = is.readData(16, false);
                boolean incoming = chat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId();
                boolean tryAnotherDecrypt = true;
                if (decryptedWithVersion == 2 && chat.mtproto_seq != 0) {
                    tryAnotherDecrypt = false;
                }
                if (!decryptWithMtProtoVersion(is, keyToDecrypt, messageKey, mtprotoVersion, incoming, tryAnotherDecrypt)) {
                    if (mtprotoVersion == 2) {
                        decryptedWithVersion = 1;
                        if (!(tryAnotherDecrypt && decryptWithMtProtoVersion(is, keyToDecrypt, messageKey, 1, incoming, false))) {
                            return null;
                        }
                    }
                    decryptedWithVersion = 2;
                    if (!decryptWithMtProtoVersion(is, keyToDecrypt, messageKey, 2, incoming, tryAnotherDecrypt)) {
                        return null;
                    }
                }
                TLObject object = TLClassStore.Instance().TLdeserialize(is, is.readInt32(false), false);
                is.reuse();
                if (!new_key_used && AndroidUtilities.getPeerLayerVersion(chat.layer) >= 20) {
                    chat.key_use_count_in = (short) (chat.key_use_count_in + 1);
                }
                if (object instanceof TL_decryptedMessageLayer) {
                    TL_decryptedMessageLayer layer = (TL_decryptedMessageLayer) object;
                    if (chat.seq_in == 0 && chat.seq_out == 0) {
                        if (chat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            chat.seq_out = 1;
                            chat.seq_in = -2;
                        } else {
                            chat.seq_in = -1;
                        }
                    }
                    if (layer.random_bytes.length < 15) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("got random bytes less than needed");
                        }
                        return null;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("current chat in_seq = " + chat.seq_in + " out_seq = " + chat.seq_out);
                        FileLog.d("got message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
                    }
                    if (layer.out_seq_no <= chat.seq_in) {
                        return null;
                    }
                    if (decryptedWithVersion == 1 && chat.mtproto_seq != 0 && layer.out_seq_no >= chat.mtproto_seq) {
                        return null;
                    }
                    if (chat.seq_in != layer.out_seq_no - 2) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("got hole");
                        }
                        ArrayList<TL_decryptedMessageHolder> arr = (ArrayList) this.secretHolesQueue.get(chat.id);
                        if (arr == null) {
                            arr = new ArrayList();
                            this.secretHolesQueue.put(chat.id, arr);
                        }
                        if (arr.size() >= 4) {
                            this.secretHolesQueue.remove(chat.id);
                            TL_encryptedChatDiscarded newChat = new TL_encryptedChatDiscarded();
                            newChat.id = chat.id;
                            newChat.user_id = chat.user_id;
                            newChat.auth_key = chat.auth_key;
                            newChat.key_create_date = chat.key_create_date;
                            newChat.key_use_count_in = chat.key_use_count_in;
                            newChat.key_use_count_out = chat.key_use_count_out;
                            newChat.seq_in = chat.seq_in;
                            newChat.seq_out = chat.seq_out;
                            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$8(this, newChat));
                            declineSecretChat(chat.id);
                            return null;
                        }
                        TL_decryptedMessageHolder holder = new TL_decryptedMessageHolder();
                        holder.layer = layer;
                        holder.file = message.file;
                        holder.date = message.date;
                        holder.new_key_used = new_key_used;
                        holder.decryptedWithVersion = decryptedWithVersion;
                        arr.add(holder);
                        return null;
                    }
                    if (decryptedWithVersion == 2) {
                        chat.mtproto_seq = Math.min(chat.mtproto_seq, chat.seq_in);
                    }
                    applyPeerLayer(chat, layer.layer);
                    chat.seq_in = layer.out_seq_no;
                    chat.in_seq_no = layer.in_seq_no;
                    MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatSeq(chat, true);
                    object = layer.message;
                } else if (!((object instanceof TL_decryptedMessageService) && (((TL_decryptedMessageService) object).action instanceof TL_decryptedMessageActionNotifyLayer))) {
                    return null;
                }
                ArrayList<Message> messages = new ArrayList();
                Message decryptedMessage = processDecryptedObject(chat, message.file, message.date, object, new_key_used);
                if (decryptedMessage != null) {
                    messages.add(decryptedMessage);
                }
                checkSecretHoles(chat, messages);
                return messages;
            }
            is.reuse();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(String.format("fingerprint mismatch %x", new Object[]{Long.valueOf(fingerprint)}));
            }
            return null;
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$decryptMessage$16$SecretChatHelper(TL_encryptedChatDiscarded newChat) {
        MessagesController.getInstance(this.currentAccount).putEncryptedChat(newChat, false);
        MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(newChat);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void requestNewSecretChatKey(EncryptedChat encryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
            byte[] salt = new byte[256];
            Utilities.random.nextBytes(salt);
            byte[] g_a = BigInteger.valueOf((long) MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes())).toByteArray();
            if (g_a.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                g_a = correctedAuth;
            }
            encryptedChat.exchange_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
            encryptedChat.a_or_b = salt;
            encryptedChat.g_a = g_a;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(encryptedChat);
            sendRequestKeyMessage(encryptedChat, null);
        }
    }

    public void processAcceptedSecretChat(EncryptedChat encryptedChat) {
        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, encryptedChat.g_a_or_b);
        if (Utilities.isGoodGaAndGb(i_authKey, p)) {
            byte[] authKey = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), p).toByteArray();
            byte[] correctedAuth;
            if (authKey.length > 256) {
                correctedAuth = new byte[256];
                System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
                authKey = correctedAuth;
            } else if (authKey.length < 256) {
                correctedAuth = new byte[256];
                System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                for (int a = 0; a < 256 - authKey.length; a++) {
                    correctedAuth[a] = (byte) 0;
                }
                authKey = correctedAuth;
            }
            byte[] authKeyHash = Utilities.computeSHA1(authKey);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            if (encryptedChat.key_fingerprint == Utilities.bytesToLong(authKeyId)) {
                encryptedChat.auth_key = authKey;
                encryptedChat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                encryptedChat.seq_in = -2;
                encryptedChat.seq_out = 1;
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(encryptedChat);
                MessagesController.getInstance(this.currentAccount).putEncryptedChat(encryptedChat, false);
                AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$9(this, encryptedChat));
                return;
            }
            TL_encryptedChatDiscarded newChat = new TL_encryptedChatDiscarded();
            newChat.id = encryptedChat.id;
            newChat.user_id = encryptedChat.user_id;
            newChat.auth_key = encryptedChat.auth_key;
            newChat.key_create_date = encryptedChat.key_create_date;
            newChat.key_use_count_in = encryptedChat.key_use_count_in;
            newChat.key_use_count_out = encryptedChat.key_use_count_out;
            newChat.seq_in = encryptedChat.seq_in;
            newChat.seq_out = encryptedChat.seq_out;
            newChat.admin_id = encryptedChat.admin_id;
            newChat.mtproto_seq = encryptedChat.mtproto_seq;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(newChat);
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$10(this, newChat));
            declineSecretChat(encryptedChat.id);
            return;
        }
        declineSecretChat(encryptedChat.id);
    }

    final /* synthetic */ void lambda$processAcceptedSecretChat$17$SecretChatHelper(EncryptedChat encryptedChat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
        sendNotifyLayerMessage(encryptedChat, null);
    }

    final /* synthetic */ void lambda$processAcceptedSecretChat$18$SecretChatHelper(TL_encryptedChatDiscarded newChat) {
        MessagesController.getInstance(this.currentAccount).putEncryptedChat(newChat, false);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void declineSecretChat(int chat_id) {
        TL_messages_discardEncryption req = new TL_messages_discardEncryption();
        req.chat_id = chat_id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, SecretChatHelper$$Lambda$11.$instance);
    }

    static final /* synthetic */ void lambda$declineSecretChat$19$SecretChatHelper(TLObject response, TL_error error) {
    }

    public void acceptSecretChat(EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) == null) {
            this.acceptingChats.put(encryptedChat.id, encryptedChat);
            TL_messages_getDhConfig req = new TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SecretChatHelper$$Lambda$12(this, encryptedChat));
        }
    }

    final /* synthetic */ void lambda$acceptSecretChat$22$SecretChatHelper(EncryptedChat encryptedChat, TLObject response, TL_error error) {
        if (error == null) {
            int a;
            messages_DhConfig res = (messages_DhConfig) response;
            if (response instanceof TL_messages_dhConfig) {
                if (Utilities.isGoodPrime(res.p, res.g)) {
                    MessagesStorage.getInstance(this.currentAccount).setSecretPBytes(res.p);
                    MessagesStorage.getInstance(this.currentAccount).setSecretG(res.g);
                    MessagesStorage.getInstance(this.currentAccount).setLastSecretVersion(res.version);
                    MessagesStorage.getInstance(this.currentAccount).saveSecretParams(MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(this.currentAccount).getSecretG(), MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                } else {
                    this.acceptingChats.remove(encryptedChat.id);
                    declineSecretChat(encryptedChat.id);
                    return;
                }
            }
            byte[] salt = new byte[256];
            for (a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            encryptedChat.a_or_b = salt;
            encryptedChat.seq_in = -1;
            encryptedChat.seq_out = 0;
            BigInteger p = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
            BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), p);
            BigInteger g_a = new BigInteger(1, encryptedChat.g_a);
            if (Utilities.isGoodGaAndGb(g_a, p)) {
                byte[] correctedAuth;
                byte[] g_b_bytes = g_b.toByteArray();
                if (g_b_bytes.length > 256) {
                    correctedAuth = new byte[256];
                    System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                    g_b_bytes = correctedAuth;
                }
                byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
                if (authKey.length > 256) {
                    correctedAuth = new byte[256];
                    System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
                    authKey = correctedAuth;
                } else if (authKey.length < 256) {
                    correctedAuth = new byte[256];
                    System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                    for (a = 0; a < 256 - authKey.length; a++) {
                        correctedAuth[a] = (byte) 0;
                    }
                    authKey = correctedAuth;
                }
                byte[] authKeyHash = Utilities.computeSHA1(authKey);
                byte[] authKeyId = new byte[8];
                System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                encryptedChat.auth_key = authKey;
                encryptedChat.key_create_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                TL_messages_acceptEncryption req2 = new TL_messages_acceptEncryption();
                req2.g_b = g_b_bytes;
                req2.peer = new TL_inputEncryptedChat();
                req2.peer.chat_id = encryptedChat.id;
                req2.peer.access_hash = encryptedChat.access_hash;
                req2.key_fingerprint = Utilities.bytesToLong(authKeyId);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new SecretChatHelper$$Lambda$21(this, encryptedChat));
                return;
            }
            this.acceptingChats.remove(encryptedChat.id);
            declineSecretChat(encryptedChat.id);
            return;
        }
        this.acceptingChats.remove(encryptedChat.id);
    }

    final /* synthetic */ void lambda$null$21$SecretChatHelper(EncryptedChat encryptedChat, TLObject response1, TL_error error1) {
        this.acceptingChats.remove(encryptedChat.id);
        if (error1 == null) {
            EncryptedChat newChat = (EncryptedChat) response1;
            newChat.auth_key = encryptedChat.auth_key;
            newChat.user_id = encryptedChat.user_id;
            newChat.seq_in = encryptedChat.seq_in;
            newChat.seq_out = encryptedChat.seq_out;
            newChat.key_create_date = encryptedChat.key_create_date;
            newChat.key_use_count_in = encryptedChat.key_use_count_in;
            newChat.key_use_count_out = encryptedChat.key_use_count_out;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(newChat);
            MessagesController.getInstance(this.currentAccount).putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$22(this, newChat));
        }
    }

    final /* synthetic */ void lambda$null$20$SecretChatHelper(EncryptedChat newChat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
        sendNotifyLayerMessage(newChat, null);
    }

    public void startSecretChat(Context context, User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            AlertDialog progressDialog = new AlertDialog(context, 3);
            TL_messages_getDhConfig req = new TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
            progressDialog.setOnCancelListener(new SecretChatHelper$$Lambda$14(this, ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SecretChatHelper$$Lambda$13(this, context, progressDialog, user), 2)));
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    final /* synthetic */ void lambda$startSecretChat$29$SecretChatHelper(Context context, AlertDialog progressDialog, User user, TLObject response, TL_error error) {
        if (error == null) {
            messages_DhConfig res = (messages_DhConfig) response;
            if (response instanceof TL_messages_dhConfig) {
                if (Utilities.isGoodPrime(res.p, res.g)) {
                    MessagesStorage.getInstance(this.currentAccount).setSecretPBytes(res.p);
                    MessagesStorage.getInstance(this.currentAccount).setSecretG(res.g);
                    MessagesStorage.getInstance(this.currentAccount).setLastSecretVersion(res.version);
                    MessagesStorage.getInstance(this.currentAccount).saveSecretParams(MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(this.currentAccount).getSecretG(), MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                } else {
                    AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$15(context, progressDialog));
                    return;
                }
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            byte[] g_a = BigInteger.valueOf((long) MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes())).toByteArray();
            if (g_a.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                g_a = correctedAuth;
            }
            TL_messages_requestEncryption req2 = new TL_messages_requestEncryption();
            req2.g_a = g_a;
            req2.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            req2.random_id = Utilities.random.nextInt();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new SecretChatHelper$$Lambda$16(this, context, progressDialog, salt, user), 2);
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$17(this, context, progressDialog));
    }

    static final /* synthetic */ void lambda$null$23$SecretChatHelper(Context context, AlertDialog progressDialog) {
        try {
            if (!((Activity) context).isFinishing()) {
                progressDialog.dismiss();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$null$27$SecretChatHelper(Context context, AlertDialog progressDialog, byte[] salt, User user, TLObject response1, TL_error error1) {
        if (error1 == null) {
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$18(this, context, progressDialog, response1, salt, user));
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$Lambda$19(this, context, progressDialog));
    }

    final /* synthetic */ void lambda$null$25$SecretChatHelper(Context context, AlertDialog progressDialog, TLObject response1, byte[] salt, User user) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        EncryptedChat chat = (EncryptedChat) response1;
        chat.user_id = chat.participant_id;
        chat.seq_in = -2;
        chat.seq_out = 1;
        chat.a_or_b = salt;
        MessagesController.getInstance(this.currentAccount).putEncryptedChat(chat, false);
        TL_dialog dialog = new TL_dialog();
        dialog.id = ((long) chat.id) << 32;
        dialog.unread_count = 0;
        dialog.top_message = 0;
        dialog.last_message_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        MessagesController.getInstance(this.currentAccount).dialogs_dict.put(dialog.id, dialog);
        MessagesController.getInstance(this.currentAccount).dialogs.add(dialog);
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        MessagesStorage.getInstance(this.currentAccount).putEncryptedChat(chat, user, dialog);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.encryptedChatCreated, chat);
        Utilities.stageQueue.postRunnable(new SecretChatHelper$$Lambda$20(this));
    }

    final /* synthetic */ void lambda$null$24$SecretChatHelper() {
        if (!this.delayedEncryptedChatUpdates.isEmpty()) {
            MessagesController.getInstance(this.currentAccount).processUpdateArray(this.delayedEncryptedChatUpdates, null, null, false);
            this.delayedEncryptedChatUpdates.clear();
        }
    }

    final /* synthetic */ void lambda$null$26$SecretChatHelper(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            this.startingSecretChat = false;
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            Builder builder = new Builder(context);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("CreateEncryptedChatError", R.string.CreateEncryptedChatError));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    final /* synthetic */ void lambda$null$28$SecretChatHelper(Context context, AlertDialog progressDialog) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    final /* synthetic */ void lambda$startSecretChat$30$SecretChatHelper(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }
}
