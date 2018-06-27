package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
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

    /* renamed from: org.telegram.messenger.SecretChatHelper$8 */
    class C04768 implements Comparator<TL_decryptedMessageHolder> {
        C04768() {
        }

        public int compare(TL_decryptedMessageHolder lhs, TL_decryptedMessageHolder rhs) {
            if (lhs.layer.out_seq_no > rhs.layer.out_seq_no) {
                return 1;
            }
            if (lhs.layer.out_seq_no < rhs.layer.out_seq_no) {
                return -1;
            }
            return 0;
        }
    }

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
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
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
            final ArrayList<Long> pendingEncMessagesToDeleteCopy = new ArrayList(this.pendingEncMessagesToDelete);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    for (int a = 0; a < pendingEncMessagesToDeleteCopy.size(); a++) {
                        MessageObject messageObject = (MessageObject) MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessagesByRandomIds.get(((Long) pendingEncMessagesToDeleteCopy.get(a)).longValue());
                        if (messageObject != null) {
                            messageObject.deleted = true;
                        }
                    }
                }
            });
            MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeletedByRandoms(new ArrayList(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
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
        final EncryptedChat newChat = update.chat;
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
            final TL_dialog dialog = new TL_dialog();
            dialog.id = dialog_id;
            dialog.unread_count = 0;
            dialog.top_message = 0;
            dialog.last_message_date = update.date;
            MessagesController.getInstance(this.currentAccount).putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.put(dialog.id, dialog);
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs.add(dialog);
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).sortDialogs(null);
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            });
            MessagesStorage.getInstance(this.currentAccount).putEncryptedChat(newChat, user, dialog);
            acceptSecretChat(newChat);
        } else if (!(newChat instanceof TL_encryptedChat)) {
            final EncryptedChat exist = existingChat;
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (exist != null) {
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(newChat, false);
                    }
                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(newChat);
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
                }
            });
        } else if (existingChat != null && (existingChat instanceof TL_encryptedChatWaiting) && (existingChat.auth_key == null || existingChat.auth_key.length == 1)) {
            newChat.a_or_b = existingChat.a_or_b;
            newChat.user_id = existingChat.user_id;
            processAcceptedSecretChat(newChat);
        } else if (existingChat == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(update);
        }
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
            newMsg.media.document.thumb = document.thumb;
            newMsg.media.document.dc_id = file.dc_id;
            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(newMsg.attachPath).renameTo(FileLoader.getPathToAttach(newMsg.media.document))) {
                newMsgObj.mediaExists = newMsgObj.attachPathExists;
                newMsgObj.attachPathExists = false;
                newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
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
            final EncryptedChat encryptedChat = chat;
            final DecryptedMessage decryptedMessage = req;
            final Message message = newMsgObj;
            final InputEncryptedFile inputEncryptedFile = encryptedFile;
            final MessageObject messageObject = newMsg;
            final String str = originalPath;
            Utilities.stageQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.SecretChatHelper$4$1 */
                class C19571 implements RequestDelegate {

                    /* renamed from: org.telegram.messenger.SecretChatHelper$4$1$2 */
                    class C04672 implements Runnable {
                        C04672() {
                        }

                        public void run() {
                            message.send_state = 2;
                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processSentMessage(message.id);
                            if (MessageObject.isVideoMessage(message) || MessageObject.isNewGifMessage(message) || MessageObject.isRoundVideoMessage(message)) {
                                SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).stopVideoService(message.attachPath);
                            }
                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).removeFromSendingMessages(message.id);
                        }
                    }

                    C19571() {
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null && (decryptedMessage.action instanceof TL_decryptedMessageActionNotifyLayer)) {
                            EncryptedChat currentChat = MessagesController.getInstance(SecretChatHelper.this.currentAccount).getEncryptedChat(Integer.valueOf(encryptedChat.id));
                            if (currentChat == null) {
                                currentChat = encryptedChat;
                            }
                            if (currentChat.key_hash == null) {
                                currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
                            }
                            if (AndroidUtilities.getPeerLayerVersion(currentChat.layer) >= 46 && currentChat.key_hash.length == 16) {
                                try {
                                    byte[] sha256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                                    byte[] key_hash = new byte[36];
                                    System.arraycopy(encryptedChat.key_hash, 0, key_hash, 0, 16);
                                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                                    currentChat.key_hash = key_hash;
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(currentChat);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                            SecretChatHelper.this.sendingNotifyLayer.remove(Integer.valueOf(currentChat.id));
                            currentChat.layer = AndroidUtilities.setMyLayerVersion(currentChat.layer, 73);
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChatLayer(currentChat);
                        }
                        if (message == null) {
                            return;
                        }
                        if (error == null) {
                            final String attachPath = message.attachPath;
                            final messages_SentEncryptedMessage res = (messages_SentEncryptedMessage) response;
                            if (SecretChatHelper.isSecretVisibleMessage(message)) {
                                message.date = res.date;
                            }
                            if (messageObject != null && (res.file instanceof TL_encryptedFile)) {
                                SecretChatHelper.this.updateMediaPaths(messageObject, res.file, decryptedMessage, str);
                            }
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.SecretChatHelper$4$1$1$1 */
                                class C04651 implements Runnable {
                                    C04651() {
                                    }

                                    public void run() {
                                        message.send_state = 0;
                                        NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(message.id), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(0));
                                        SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processSentMessage(message.id);
                                        if (MessageObject.isVideoMessage(message) || MessageObject.isNewGifMessage(message) || MessageObject.isRoundVideoMessage(message)) {
                                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).stopVideoService(attachPath);
                                        }
                                        SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).removeFromSendingMessages(message.id);
                                    }
                                }

                                public void run() {
                                    if (SecretChatHelper.isSecretInvisibleMessage(message)) {
                                        res.date = 0;
                                    }
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateMessageStateAndId(message.random_id, Integer.valueOf(message.id), message.id, res.date, false, 0);
                                    AndroidUtilities.runOnUIThread(new C04651());
                                }
                            });
                            return;
                        }
                        MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).markMessageAsSendError(message);
                        AndroidUtilities.runOnUIThread(new C04672());
                    }
                }

                public void run() {
                    try {
                        TLObject reqToSend;
                        TLObject layer = new TL_decryptedMessageLayer();
                        layer.layer = Math.min(Math.max(46, AndroidUtilities.getMyLayerVersion(encryptedChat.layer)), Math.max(46, AndroidUtilities.getPeerLayerVersion(encryptedChat.layer)));
                        layer.message = decryptedMessage;
                        layer.random_bytes = new byte[15];
                        Utilities.random.nextBytes(layer.random_bytes);
                        TLObject toEncryptObject = layer;
                        int mtprotoVersion = AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 73 ? 2 : 1;
                        if (encryptedChat.seq_in == 0 && encryptedChat.seq_out == 0) {
                            if (encryptedChat.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId()) {
                                encryptedChat.seq_out = 1;
                                encryptedChat.seq_in = -2;
                            } else {
                                encryptedChat.seq_in = -1;
                            }
                        }
                        if (message.seq_in == 0 && message.seq_out == 0) {
                            int i;
                            if (encryptedChat.seq_in > 0) {
                                i = encryptedChat.seq_in;
                            } else {
                                i = encryptedChat.seq_in + 2;
                            }
                            layer.in_seq_no = i;
                            layer.out_seq_no = encryptedChat.seq_out;
                            EncryptedChat encryptedChat = encryptedChat;
                            encryptedChat.seq_out += 2;
                            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
                                if (encryptedChat.key_create_date == 0) {
                                    encryptedChat.key_create_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                                }
                                encryptedChat = encryptedChat;
                                encryptedChat.key_use_count_out = (short) (encryptedChat.key_use_count_out + 1);
                                if ((encryptedChat.key_use_count_out >= (short) 100 || encryptedChat.key_create_date < ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime() - 604800) && encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint == 0) {
                                    SecretChatHelper.this.requestNewSecretChatKey(encryptedChat);
                                }
                            }
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChatSeq(encryptedChat, false);
                            if (message != null) {
                                message.seq_in = layer.in_seq_no;
                                message.seq_out = layer.out_seq_no;
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setMessageSeq(message.id, message.seq_in, message.seq_out);
                            }
                        } else {
                            layer.in_seq_no = message.seq_in;
                            layer.out_seq_no = message.seq_out;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d(decryptedMessage + " send message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
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
                        boolean incoming = mtprotoVersion == 2 && encryptedChat.admin_id != UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId();
                        if (mtprotoVersion == 2) {
                            System.arraycopy(Utilities.computeSHA256(encryptedChat.auth_key, (incoming ? 8 : 0) + 88, 32, dataForEncryption.buffer, 0, dataForEncryption.buffer.limit()), 8, messageKey, 0, 16);
                        } else {
                            Object messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer);
                            System.arraycopy(messageKeyFull, messageKeyFull.length - 16, messageKey, 0, 16);
                        }
                        nativeByteBuffer.reuse();
                        MessageKeyData keyData = MessageKeyData.generateMessageKeyData(encryptedChat.auth_key, messageKey, incoming, mtprotoVersion);
                        Utilities.aesIgeEncryption(dataForEncryption.buffer, keyData.aesKey, keyData.aesIv, true, false, 0, dataForEncryption.limit());
                        NativeByteBuffer data = new NativeByteBuffer((messageKey.length + 8) + dataForEncryption.length());
                        dataForEncryption.position(0);
                        data.writeInt64(encryptedChat.key_fingerprint);
                        data.writeBytes((byte[]) messageKey);
                        data.writeBytes(dataForEncryption);
                        dataForEncryption.reuse();
                        data.position(0);
                        TLObject req2;
                        if (inputEncryptedFile != null) {
                            req2 = new TL_messages_sendEncryptedFile();
                            req2.data = data;
                            req2.random_id = decryptedMessage.random_id;
                            req2.peer = new TL_inputEncryptedChat();
                            req2.peer.chat_id = encryptedChat.id;
                            req2.peer.access_hash = encryptedChat.access_hash;
                            req2.file = inputEncryptedFile;
                            reqToSend = req2;
                        } else if (decryptedMessage instanceof TL_decryptedMessageService) {
                            req2 = new TL_messages_sendEncryptedService();
                            req2.data = data;
                            req2.random_id = decryptedMessage.random_id;
                            req2.peer = new TL_inputEncryptedChat();
                            req2.peer.chat_id = encryptedChat.id;
                            req2.peer.access_hash = encryptedChat.access_hash;
                            reqToSend = req2;
                        } else {
                            req2 = new TL_messages_sendEncrypted();
                            req2.data = data;
                            req2.random_id = decryptedMessage.random_id;
                            req2.peer = new TL_inputEncryptedChat();
                            req2.peer.chat_id = encryptedChat.id;
                            req2.peer.access_hash = encryptedChat.access_hash;
                            reqToSend = req2;
                        }
                        ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(reqToSend, new C19571(), 64);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private void applyPeerLayer(final EncryptedChat chat, int newPeerLayer) {
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
                    FileLog.m3e(e);
                }
            }
            chat.layer = AndroidUtilities.setPeerLayerVersion(chat.layer, newPeerLayer);
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatLayer(chat);
            if (currentPeerLayer < 73) {
                sendNotifyLayerMessage(chat, null);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, chat);
                }
            });
        }
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
                    newMessage.media.vcard = TtmlNode.ANONYMOUS_REGION_ID;
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
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.photo = new TL_photo();
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
                    big.w = decryptedMessage.media.f36w;
                    big.h = decryptedMessage.media.f35h;
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
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.document.date = date;
                    newMessage.media.document.size = file.size;
                    newMessage.media.document.id = file.id;
                    newMessage.media.document.access_hash = file.access_hash;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = MimeTypes.VIDEO_MP4;
                    }
                    thumb = ((TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                    if (thumb == null || thumb.length == 0 || thumb.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                        newMessage.media.document.thumb = new TL_photoSizeEmpty();
                        newMessage.media.document.thumb.type = "s";
                    } else {
                        newMessage.media.document.thumb = new TL_photoCachedSize();
                        newMessage.media.document.thumb.bytes = thumb;
                        newMessage.media.document.thumb.f45w = decryptedMessage.media.thumb_w;
                        newMessage.media.document.thumb.f44h = decryptedMessage.media.thumb_h;
                        newMessage.media.document.thumb.type = "s";
                        newMessage.media.document.thumb.location = new TL_fileLocationUnavailable();
                    }
                    TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                    attributeVideo.w = decryptedMessage.media.f36w;
                    attributeVideo.h = decryptedMessage.media.f35h;
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
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
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
                    Document document = newMessage.media.document;
                    if (decryptedMessage.media.size != 0) {
                        newMessageId = Math.min(decryptedMessage.media.size, file.size);
                    } else {
                        newMessageId = file.size;
                    }
                    document.size = newMessageId;
                    newMessage.media.document.key = decryptedMessage.media.key;
                    newMessage.media.document.iv = decryptedMessage.media.iv;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    thumb = ((TL_decryptedMessageMediaDocument) decryptedMessage.media).thumb;
                    if (thumb == null || thumb.length == 0 || thumb.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                        newMessage.media.document.thumb = new TL_photoSizeEmpty();
                        newMessage.media.document.thumb.type = "s";
                    } else {
                        newMessage.media.document.thumb = new TL_photoCachedSize();
                        newMessage.media.document.thumb.bytes = thumb;
                        newMessage.media.document.thumb.f45w = decryptedMessage.media.thumb_w;
                        newMessage.media.document.thumb.f44h = decryptedMessage.media.thumb_h;
                        newMessage.media.document.thumb.type = "s";
                        newMessage.media.document.thumb.location = new TL_fileLocationUnavailable();
                    }
                    newMessage.media.document.dc_id = file.dc_id;
                    if (MessageObject.isVoiceMessage(newMessage) || MessageObject.isRoundVideoMessage(newMessage)) {
                        newMessage.media_unread = true;
                    }
                } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaExternalDocument) {
                    newMessage.media = new TL_messageMediaDocument();
                    messageMedia = newMessage.media;
                    messageMedia.flags |= 3;
                    newMessage.message = TtmlNode.ANONYMOUS_REGION_ID;
                    newMessage.media.document = new TL_document();
                    newMessage.media.document.id = decryptedMessage.media.id;
                    newMessage.media.document.access_hash = decryptedMessage.media.access_hash;
                    newMessage.media.document.date = decryptedMessage.media.date;
                    newMessage.media.document.attributes = decryptedMessage.media.attributes;
                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                    newMessage.media.document.dc_id = decryptedMessage.media.dc_id;
                    newMessage.media.document.size = decryptedMessage.media.size;
                    newMessage.media.document.thumb = ((TL_decryptedMessageMediaExternalDocument) decryptedMessage.media).thumb;
                    if (newMessage.media.document.mime_type == null) {
                        newMessage.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
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
                    newMessage.media.document.thumb = new TL_photoSizeEmpty();
                    newMessage.media.document.thumb.type = "s";
                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
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
                    newMessage.media.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
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
                    final long j = ((long) chat.id) << 32;
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.messenger.SecretChatHelper$6$1 */
                        class C04711 implements Runnable {

                            /* renamed from: org.telegram.messenger.SecretChatHelper$6$1$1 */
                            class C04701 implements Runnable {
                                C04701() {
                                }

                                public void run() {
                                    NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processReadMessages(null, j, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
                                    LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
                                    dialogsToUpdate.put(j, Integer.valueOf(0));
                                    NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                                }
                            }

                            C04711() {
                            }

                            public void run() {
                                AndroidUtilities.runOnUIThread(new C04701());
                            }
                        }

                        public void run() {
                            TL_dialog dialog = (TL_dialog) MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.get(j);
                            if (dialog != null) {
                                dialog.unread_count = 0;
                                MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessage.remove(dialog.id);
                            }
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new C04711());
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).deleteDialog(j, 1);
                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(false));
                        }
                    });
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
                                FileLog.m0d("we already have request key with higher exchange_id");
                            }
                            return null;
                        }
                        sendAbortKeyMessage(chat, null, chat.exchange_id);
                    }
                    byte[] salt = new byte[256];
                    Utilities.random.nextBytes(salt);
                    r0 = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                    BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), r0);
                    r0 = new BigInteger(1, serviceMessage.action.g_a);
                    if (Utilities.isGoodGaAndGb(r0, r0)) {
                        byte[] g_b_bytes = g_b.toByteArray();
                        if (g_b_bytes.length > 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                            g_b_bytes = correctedAuth;
                        }
                        authKey = r0.modPow(new BigInteger(1, salt), r0).toByteArray();
                        if (authKey.length > 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
                            authKey = correctedAuth;
                        } else if (authKey.length < 256) {
                            correctedAuth = new byte[256];
                            System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                            for (a = 0; a < 256 - authKey.length; a++) {
                                authKey[a] = (byte) 0;
                            }
                            authKey = correctedAuth;
                        }
                        authKeyHash = Utilities.computeSHA1(authKey);
                        authKeyId = new byte[8];
                        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                        chat.exchange_id = serviceMessage.action.exchange_id;
                        chat.future_auth_key = authKey;
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
                        r0 = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
                        r0 = new BigInteger(1, serviceMessage.action.g_b);
                        if (Utilities.isGoodGaAndGb(r0, r0)) {
                            authKey = r0.modPow(new BigInteger(1, chat.a_or_b), r0).toByteArray();
                            if (authKey.length > 256) {
                                correctedAuth = new byte[256];
                                System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
                                authKey = correctedAuth;
                            } else if (authKey.length < 256) {
                                correctedAuth = new byte[256];
                                System.arraycopy(authKey, 0, correctedAuth, 256 - authKey.length, authKey.length);
                                for (a = 0; a < 256 - authKey.length; a++) {
                                    authKey[a] = (byte) 0;
                                }
                                authKey = correctedAuth;
                            }
                            authKeyHash = Utilities.computeSHA1(authKey);
                            authKeyId = new byte[8];
                            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                            long fingerprint = Utilities.bytesToLong(authKeyId);
                            if (serviceMessage.action.key_fingerprint == fingerprint) {
                                chat.future_auth_key = authKey;
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
                FileLog.m1e("unknown message " + object);
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.m1e("unknown TLObject");
        }
        return null;
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

    private void resendMessages(final int startSeq, final int endSeq, final EncryptedChat encryptedChat) {
        if (encryptedChat != null && endSeq - startSeq >= 0) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.SecretChatHelper$7$1 */
                class C04731 implements Comparator<Message> {
                    C04731() {
                    }

                    public int compare(Message lhs, Message rhs) {
                        return AndroidUtilities.compare(lhs.seq_out, rhs.seq_out);
                    }
                }

                public void run() {
                    try {
                        int sSeq = startSeq;
                        if (encryptedChat.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId() && sSeq % 2 == 0) {
                            sSeq++;
                        }
                        SQLiteCursor cursor = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(encryptedChat.id), Integer.valueOf(sSeq), Integer.valueOf(sSeq), Integer.valueOf(endSeq), Integer.valueOf(endSeq)}), new Object[0]);
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
                            cursor = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)}), new Object[0]);
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
                                    message.readAttachPath(data, UserConfig.getInstance(SecretChatHelper.this.currentAccount).clientUserId);
                                    data.reuse();
                                    message.random_id = random_id;
                                    message.dialog_id = dialog_id;
                                    message.seq_in = seq_in;
                                    message.seq_out = seq_out;
                                    message.ttl = cursor.intValue(4);
                                } else {
                                    message = SecretChatHelper.this.createDeleteMessage(mid, seq_out, seq_in, random_id, encryptedChat);
                                }
                                messages.add(message);
                                messagesToResend.remove(seq_out);
                            }
                            cursor.dispose();
                            if (messagesToResend.size() != 0) {
                                for (a = 0; a < messagesToResend.size(); a++) {
                                    messages.add(SecretChatHelper.this.createDeleteMessage(UserConfig.getInstance(SecretChatHelper.this.currentAccount).getNewMessageId(), messagesToResend.keyAt(a), 0, Utilities.random.nextLong(), encryptedChat));
                                }
                                UserConfig.getInstance(SecretChatHelper.this.currentAccount).saveConfig(false);
                            }
                            Collections.sort(messages, new C04731());
                            ArrayList<EncryptedChat> encryptedChats = new ArrayList();
                            encryptedChats.add(encryptedChat);
                            final ArrayList<Message> arrayList = messages;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    for (int a = 0; a < arrayList.size(); a++) {
                                        MessageObject messageObject = new MessageObject(SecretChatHelper.this.currentAccount, (Message) arrayList.get(a), false);
                                        messageObject.resendAsIs = true;
                                        SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).retrySendMessage(messageObject, true);
                                    }
                                }
                            });
                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processUnsentMessages(messages, new ArrayList(), new ArrayList(), encryptedChats);
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(encryptedChat.id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)})).stepThis().dispose();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public void checkSecretHoles(EncryptedChat chat, ArrayList<Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = (ArrayList) this.secretHolesQueue.get(chat.id);
        if (holes != null) {
            Collections.sort(holes, new C04768());
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
                            FileLog.m1e("got random bytes less than needed");
                        }
                        return null;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("current chat in_seq = " + chat.seq_in + " out_seq = " + chat.seq_out);
                        FileLog.m0d("got message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
                    }
                    if (layer.out_seq_no <= chat.seq_in) {
                        return null;
                    }
                    if (decryptedWithVersion == 1 && chat.mtproto_seq != 0 && layer.out_seq_no >= chat.mtproto_seq) {
                        return null;
                    }
                    if (chat.seq_in != layer.out_seq_no - 2) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m1e("got hole");
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
                            final TL_encryptedChatDiscarded tL_encryptedChatDiscarded = newChat;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(tL_encryptedChatDiscarded, false);
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(tL_encryptedChatDiscarded);
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, tL_encryptedChatDiscarded);
                                }
                            });
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
                FileLog.m1e(String.format("fingerprint mismatch %x", new Object[]{Long.valueOf(fingerprint)}));
            }
            return null;
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
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

    public void processAcceptedSecretChat(final EncryptedChat encryptedChat) {
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
                    authKey[a] = (byte) 0;
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
                        SecretChatHelper.this.sendNotifyLayerMessage(encryptedChat, null);
                    }
                });
                return;
            }
            final TL_encryptedChatDiscarded newChat = new TL_encryptedChatDiscarded();
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(newChat, false);
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
                }
            });
            declineSecretChat(encryptedChat.id);
            return;
        }
        declineSecretChat(encryptedChat.id);
    }

    public void declineSecretChat(int chat_id) {
        TL_messages_discardEncryption req = new TL_messages_discardEncryption();
        req.chat_id = chat_id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void acceptSecretChat(final EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) == null) {
            this.acceptingChats.put(encryptedChat.id, encryptedChat);
            TL_messages_getDhConfig req = new TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.SecretChatHelper$13$1 */
                class C19551 implements RequestDelegate {
                    C19551() {
                    }

                    public void run(TLObject response, TL_error error) {
                        SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                        if (error == null) {
                            final EncryptedChat newChat = (EncryptedChat) response;
                            newChat.auth_key = encryptedChat.auth_key;
                            newChat.user_id = encryptedChat.user_id;
                            newChat.seq_in = encryptedChat.seq_in;
                            newChat.seq_out = encryptedChat.seq_out;
                            newChat.key_create_date = encryptedChat.key_create_date;
                            newChat.key_use_count_in = encryptedChat.key_use_count_in;
                            newChat.key_use_count_out = encryptedChat.key_use_count_out;
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(newChat);
                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(newChat, false);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
                                    SecretChatHelper.this.sendNotifyLayerMessage(newChat, null);
                                }
                            });
                        }
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        int a;
                        messages_DhConfig res = (messages_DhConfig) response;
                        if (response instanceof TL_messages_dhConfig) {
                            if (Utilities.isGoodPrime(res.f58p, res.f57g)) {
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(res.f58p);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(res.f57g);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(res.version);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                            } else {
                                SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                                SecretChatHelper.this.declineSecretChat(encryptedChat.id);
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
                        BigInteger p = new BigInteger(1, MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                        BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), p);
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
                                    authKey[a] = (byte) 0;
                                }
                                authKey = correctedAuth;
                            }
                            byte[] authKeyHash = Utilities.computeSHA1(authKey);
                            byte[] authKeyId = new byte[8];
                            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                            encryptedChat.auth_key = authKey;
                            encryptedChat.key_create_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                            TL_messages_acceptEncryption req2 = new TL_messages_acceptEncryption();
                            req2.g_b = g_b_bytes;
                            req2.peer = new TL_inputEncryptedChat();
                            req2.peer.chat_id = encryptedChat.id;
                            req2.peer.access_hash = encryptedChat.access_hash;
                            req2.key_fingerprint = Utilities.bytesToLong(authKeyId);
                            ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(req2, new C19551());
                            return;
                        }
                        SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                        SecretChatHelper.this.declineSecretChat(encryptedChat.id);
                        return;
                    }
                    SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                }
            });
        }
    }

    public void startSecretChat(final Context context, final User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            final AlertDialog progressDialog = new AlertDialog(context, 1);
            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            TL_messages_getDhConfig req = new TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
            final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.SecretChatHelper$14$1 */
                class C04571 implements Runnable {
                    C04571() {
                    }

                    public void run() {
                        try {
                            if (!((Activity) context).isFinishing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }

                /* renamed from: org.telegram.messenger.SecretChatHelper$14$3 */
                class C04613 implements Runnable {
                    C04613() {
                    }

                    public void run() {
                        SecretChatHelper.this.startingSecretChat = false;
                        if (!((Activity) context).isFinishing()) {
                            try {
                                progressDialog.dismiss();
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        messages_DhConfig res = (messages_DhConfig) response;
                        if (response instanceof TL_messages_dhConfig) {
                            if (Utilities.isGoodPrime(res.f58p, res.f57g)) {
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(res.f58p);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(res.f57g);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(res.version);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                            } else {
                                AndroidUtilities.runOnUIThread(new C04571());
                                return;
                            }
                        }
                        final byte[] salt = new byte[256];
                        for (int a = 0; a < 256; a++) {
                            salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
                        }
                        byte[] g_a = BigInteger.valueOf((long) MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes())).toByteArray();
                        if (g_a.length > 256) {
                            byte[] correctedAuth = new byte[256];
                            System.arraycopy(g_a, 1, correctedAuth, 0, 256);
                            g_a = correctedAuth;
                        }
                        TL_messages_requestEncryption req2 = new TL_messages_requestEncryption();
                        req2.g_a = g_a;
                        req2.user_id = MessagesController.getInstance(SecretChatHelper.this.currentAccount).getInputUser(user);
                        req2.random_id = Utilities.random.nextInt();
                        ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(req2, new RequestDelegate() {

                            /* renamed from: org.telegram.messenger.SecretChatHelper$14$2$2 */
                            class C04602 implements Runnable {
                                C04602() {
                                }

                                public void run() {
                                    if (!((Activity) context).isFinishing()) {
                                        SecretChatHelper.this.startingSecretChat = false;
                                        try {
                                            progressDialog.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                        Builder builder = new Builder(context);
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        builder.setMessage(LocaleController.getString("CreateEncryptedChatError", R.string.CreateEncryptedChatError));
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                        builder.show().setCanceledOnTouchOutside(true);
                                    }
                                }
                            }

                            public void run(final TLObject response, TL_error error) {
                                if (error == null) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {

                                        /* renamed from: org.telegram.messenger.SecretChatHelper$14$2$1$1 */
                                        class C04581 implements Runnable {
                                            C04581() {
                                            }

                                            public void run() {
                                                if (!SecretChatHelper.this.delayedEncryptedChatUpdates.isEmpty()) {
                                                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).processUpdateArray(SecretChatHelper.this.delayedEncryptedChatUpdates, null, null, false);
                                                    SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                                                }
                                            }
                                        }

                                        public void run() {
                                            SecretChatHelper.this.startingSecretChat = false;
                                            if (!((Activity) context).isFinishing()) {
                                                try {
                                                    progressDialog.dismiss();
                                                } catch (Throwable e) {
                                                    FileLog.m3e(e);
                                                }
                                            }
                                            EncryptedChat chat = response;
                                            chat.user_id = chat.participant_id;
                                            chat.seq_in = -2;
                                            chat.seq_out = 1;
                                            chat.a_or_b = salt;
                                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(chat, false);
                                            TL_dialog dialog = new TL_dialog();
                                            dialog.id = ((long) chat.id) << 32;
                                            dialog.unread_count = 0;
                                            dialog.top_message = 0;
                                            dialog.last_message_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.put(dialog.id, dialog);
                                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs.add(dialog);
                                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).sortDialogs(null);
                                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(chat, user, dialog);
                                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatCreated, chat);
                                            Utilities.stageQueue.postRunnable(new C04581());
                                        }
                                    });
                                    return;
                                }
                                SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                                AndroidUtilities.runOnUIThread(new C04602());
                            }
                        }, 2);
                        return;
                    }
                    SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                    AndroidUtilities.runOnUIThread(new C04613());
                }
            }, 2);
            progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).cancelRequest(reqId, true);
                    try {
                        dialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }
}
