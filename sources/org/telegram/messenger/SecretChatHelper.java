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
    class C04648 implements Comparator<TL_decryptedMessageHolder> {
        C04648() {
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
                localInstance = Instance[num];
                if (localInstance == null) {
                    SecretChatHelper[] secretChatHelperArr = Instance;
                    SecretChatHelper secretChatHelper = new SecretChatHelper(num);
                    localInstance = secretChatHelper;
                    secretChatHelperArr[num] = secretChatHelper;
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
        ArrayList<Message> arr;
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
        if (!(decryptedMessage instanceof TL_decryptedMessageActionScreenshotMessages)) {
            if (!(decryptedMessage instanceof TL_decryptedMessageActionSetMessageTTL)) {
                newMsg.date = 0;
                newMsg.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                arr = new ArrayList();
                arr.add(newMsg);
                MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList) arr, false, true, true, 0);
                return newMsg;
            }
        }
        newMsg.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        newMsg.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        arr = new ArrayList();
        arr.add(newMsg);
        MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList) arr, false, true, true, 0);
        return newMsg;
    }

    public void sendMessagesReadMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionReadMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
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
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionDeleteMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendClearHistoryMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendNotifyLayerMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if ((encryptedChat instanceof TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            Message message;
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionNotifyLayer();
                reqSend.action.layer = 73;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendRequestKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionRequestKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendAcceptKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
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
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendCommitKeyMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionCommitKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendAbortKeyMessage(EncryptedChat encryptedChat, Message resendMessage, long excange_id) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionAbortKey();
                reqSend.action.exchange_id = excange_id;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendNoopMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
            if (resendMessage != null) {
                message = resendMessage;
                reqSend.action = message.action.encryptedAction;
            } else {
                reqSend.action = new TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendTTLMessage(EncryptedChat encryptedChat, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
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
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    public void sendScreenshotMessage(EncryptedChat encryptedChat, ArrayList<Long> random_ids, Message resendMessage) {
        if (encryptedChat instanceof TL_encryptedChat) {
            Message message;
            DecryptedMessage reqSend = new TL_decryptedMessageService();
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
            Message message2 = message;
            reqSend.random_id = message2.random_id;
            performSendEncryptedRequest(reqSend, message2, encryptedChat, null, null, null);
        }
    }

    private void updateMediaPaths(MessageObject newMsgObj, EncryptedFile file, DecryptedMessage decryptedMessage, String originalPath) {
        SecretChatHelper secretChatHelper = this;
        MessageObject messageObject = newMsgObj;
        EncryptedFile encryptedFile = file;
        DecryptedMessage decryptedMessage2 = decryptedMessage;
        Message newMsg = messageObject.messageOwner;
        if (encryptedFile == null) {
            return;
        }
        if ((newMsg.media instanceof TL_messageMediaPhoto) && newMsg.media.photo != null) {
            PhotoSize size = (PhotoSize) newMsg.media.photo.sizes.get(newMsg.media.photo.sizes.size() - 1);
            String fileName = new StringBuilder();
            fileName.append(size.location.volume_id);
            fileName.append("_");
            fileName.append(size.location.local_id);
            fileName = fileName.toString();
            size.location = new TL_fileEncryptedLocation();
            size.location.key = decryptedMessage2.media.key;
            size.location.iv = decryptedMessage2.media.iv;
            size.location.dc_id = encryptedFile.dc_id;
            size.location.volume_id = encryptedFile.id;
            size.location.secret = encryptedFile.access_hash;
            size.location.local_id = encryptedFile.key_fingerprint;
            String fileName2 = new StringBuilder();
            fileName2.append(size.location.volume_id);
            fileName2.append("_");
            fileName2.append(size.location.local_id);
            fileName2 = fileName2.toString();
            File directory = FileLoader.getDirectory(4);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(fileName);
            stringBuilder.append(".jpg");
            new File(directory, stringBuilder.toString()).renameTo(FileLoader.getPathToAttach(size));
            ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, true);
            ArrayList<Message> arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance(secretChatHelper.currentAccount).putMessages((ArrayList) arr, false, true, false, 0);
        } else if ((newMsg.media instanceof TL_messageMediaDocument) && newMsg.media.document != null) {
            Document document = newMsg.media.document;
            newMsg.media.document = new TL_documentEncrypted();
            newMsg.media.document.id = encryptedFile.id;
            newMsg.media.document.access_hash = encryptedFile.access_hash;
            newMsg.media.document.date = document.date;
            newMsg.media.document.attributes = document.attributes;
            newMsg.media.document.mime_type = document.mime_type;
            newMsg.media.document.size = encryptedFile.size;
            newMsg.media.document.key = decryptedMessage2.media.key;
            newMsg.media.document.iv = decryptedMessage2.media.iv;
            newMsg.media.document.thumb = document.thumb;
            newMsg.media.document.dc_id = encryptedFile.dc_id;
            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(newMsg.attachPath).renameTo(FileLoader.getPathToAttach(newMsg.media.document))) {
                messageObject.mediaExists = messageObject.attachPathExists;
                messageObject.attachPathExists = false;
                newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            }
            ArrayList<Message> arr2 = new ArrayList();
            arr2.add(newMsg);
            MessagesStorage.getInstance(secretChatHelper.currentAccount).putMessages((ArrayList) arr2, false, true, false, 0);
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
        Message message;
        EncryptedChat encryptedChat = chat;
        if (!(req == null || encryptedChat.auth_key == null || (encryptedChat instanceof TL_encryptedChatRequested))) {
            if (!(encryptedChat instanceof TL_encryptedChatWaiting)) {
                message = newMsgObj;
                SendMessagesHelper.getInstance(this.currentAccount).putToSendingMessages(message);
                final EncryptedChat encryptedChat2 = encryptedChat;
                final DecryptedMessage decryptedMessage = req;
                final Message message2 = message;
                final InputEncryptedFile inputEncryptedFile = encryptedFile;
                final MessageObject messageObject = newMsg;
                final String str = originalPath;
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.SecretChatHelper$4$1 */
                    class C18131 implements RequestDelegate {

                        /* renamed from: org.telegram.messenger.SecretChatHelper$4$1$2 */
                        class C04552 implements Runnable {
                            C04552() {
                            }

                            public void run() {
                                message2.send_state = 2;
                                NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message2.id));
                                SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processSentMessage(message2.id);
                                if (MessageObject.isVideoMessage(message2) || MessageObject.isNewGifMessage(message2) || MessageObject.isRoundVideoMessage(message2)) {
                                    SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).stopVideoService(message2.attachPath);
                                }
                                SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).removeFromSendingMessages(message2.id);
                            }
                        }

                        C18131() {
                        }

                        public void run(TLObject response, TL_error error) {
                            if (error == null && (decryptedMessage.action instanceof TL_decryptedMessageActionNotifyLayer)) {
                                EncryptedChat currentChat = MessagesController.getInstance(SecretChatHelper.this.currentAccount).getEncryptedChat(Integer.valueOf(encryptedChat2.id));
                                if (currentChat == null) {
                                    currentChat = encryptedChat2;
                                }
                                if (currentChat.key_hash == null) {
                                    currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
                                }
                                if (AndroidUtilities.getPeerLayerVersion(currentChat.layer) >= 46 && currentChat.key_hash.length == 16) {
                                    try {
                                        byte[] sha256 = Utilities.computeSHA256(encryptedChat2.auth_key, 0, encryptedChat2.auth_key.length);
                                        byte[] key_hash = new byte[36];
                                        System.arraycopy(encryptedChat2.key_hash, 0, key_hash, 0, 16);
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
                            if (message2 == null) {
                                return;
                            }
                            if (error == null) {
                                final String attachPath = message2.attachPath;
                                final messages_SentEncryptedMessage res = (messages_SentEncryptedMessage) response;
                                if (SecretChatHelper.isSecretVisibleMessage(message2)) {
                                    message2.date = res.date;
                                }
                                if (messageObject != null && (res.file instanceof TL_encryptedFile)) {
                                    SecretChatHelper.this.updateMediaPaths(messageObject, res.file, decryptedMessage, str);
                                }
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                    /* renamed from: org.telegram.messenger.SecretChatHelper$4$1$1$1 */
                                    class C04531 implements Runnable {
                                        C04531() {
                                        }

                                        public void run() {
                                            message2.send_state = 0;
                                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(message2.id), Integer.valueOf(message2.id), message2, Long.valueOf(message2.dialog_id));
                                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processSentMessage(message2.id);
                                            if (MessageObject.isVideoMessage(message2) || MessageObject.isNewGifMessage(message2) || MessageObject.isRoundVideoMessage(message2)) {
                                                SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).stopVideoService(attachPath);
                                            }
                                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).removeFromSendingMessages(message2.id);
                                        }
                                    }

                                    public void run() {
                                        if (SecretChatHelper.isSecretInvisibleMessage(message2)) {
                                            res.date = 0;
                                        }
                                        MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateMessageStateAndId(message2.random_id, Integer.valueOf(message2.id), message2.id, res.date, false, 0);
                                        AndroidUtilities.runOnUIThread(new C04531());
                                    }
                                });
                                return;
                            }
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).markMessageAsSendError(message2);
                            AndroidUtilities.runOnUIThread(new C04552());
                        }
                    }

                    public void run() {
                        C04564 c04564 = this;
                        try {
                            byte[] b;
                            TLObject reqToSend;
                            TLObject layer = new TL_decryptedMessageLayer();
                            int myLayer = Math.max(46, AndroidUtilities.getMyLayerVersion(encryptedChat2.layer));
                            layer.layer = Math.min(myLayer, Math.max(46, AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer)));
                            layer.message = decryptedMessage;
                            layer.random_bytes = new byte[15];
                            Utilities.random.nextBytes(layer.random_bytes);
                            TLObject toEncryptObject = layer;
                            int mtprotoVersion = AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 73 ? 2 : 1;
                            if (encryptedChat2.seq_in == 0 && encryptedChat2.seq_out == 0) {
                                if (encryptedChat2.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId()) {
                                    encryptedChat2.seq_out = 1;
                                    encryptedChat2.seq_in = -2;
                                } else {
                                    encryptedChat2.seq_in = -1;
                                }
                            }
                            if (message2.seq_in == 0 && message2.seq_out == 0) {
                                layer.in_seq_no = encryptedChat2.seq_in > 0 ? encryptedChat2.seq_in : encryptedChat2.seq_in + 2;
                                layer.out_seq_no = encryptedChat2.seq_out;
                                EncryptedChat encryptedChat = encryptedChat2;
                                encryptedChat.seq_out += 2;
                                if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 20) {
                                    if (encryptedChat2.key_create_date == 0) {
                                        encryptedChat2.key_create_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                                    }
                                    encryptedChat = encryptedChat2;
                                    encryptedChat.key_use_count_out = (short) (encryptedChat.key_use_count_out + 1);
                                    if ((encryptedChat2.key_use_count_out >= (short) 100 || encryptedChat2.key_create_date < ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime() - 604800) && encryptedChat2.exchange_id == 0 && encryptedChat2.future_key_fingerprint == 0) {
                                        SecretChatHelper.this.requestNewSecretChatKey(encryptedChat2);
                                    }
                                }
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChatSeq(encryptedChat2, false);
                                if (message2 != null) {
                                    message2.seq_in = layer.in_seq_no;
                                    message2.seq_out = layer.out_seq_no;
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setMessageSeq(message2.id, message2.seq_in, message2.seq_out);
                                }
                            } else {
                                layer.in_seq_no = message2.seq_in;
                                layer.out_seq_no = message2.seq_out;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(decryptedMessage);
                                stringBuilder.append(" send message with in_seq = ");
                                stringBuilder.append(layer.in_seq_no);
                                stringBuilder.append(" out_seq = ");
                                stringBuilder.append(layer.out_seq_no);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            int len = toEncryptObject.getObjectSize();
                            NativeByteBuffer toEncrypt = new NativeByteBuffer(4 + len);
                            toEncrypt.writeInt32(len);
                            toEncryptObject.serializeToStream(toEncrypt);
                            len = toEncrypt.length();
                            int extraLen = len % 16 != 0 ? 16 - (len % 16) : 0;
                            if (mtprotoVersion == 2) {
                                extraLen += (Utilities.random.nextInt(3) + 2) * 16;
                            }
                            NativeByteBuffer dataForEncryption = new NativeByteBuffer(len + extraLen);
                            toEncrypt.position(0);
                            dataForEncryption.writeBytes(toEncrypt);
                            if (extraLen != 0) {
                                b = new byte[extraLen];
                                Utilities.random.nextBytes(b);
                                dataForEncryption.writeBytes(b);
                            }
                            b = new byte[16];
                            boolean z = mtprotoVersion == 2 && encryptedChat2.admin_id != UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId();
                            boolean incoming = z;
                            if (mtprotoVersion == 2) {
                                System.arraycopy(Utilities.computeSHA256(encryptedChat2.auth_key, 88 + (incoming ? 8 : 0), 32, dataForEncryption.buffer, 0, dataForEncryption.buffer.limit()), 8, b, 0, 16);
                            } else {
                                byte[] messageKeyFull = Utilities.computeSHA1(toEncrypt.buffer);
                                System.arraycopy(messageKeyFull, messageKeyFull.length - 16, b, 0, 16);
                            }
                            toEncrypt.reuse();
                            MessageKeyData keyData = MessageKeyData.generateMessageKeyData(encryptedChat2.auth_key, b, incoming, mtprotoVersion);
                            Utilities.aesIgeEncryption(dataForEncryption.buffer, keyData.aesKey, keyData.aesIv, true, false, 0, dataForEncryption.limit());
                            NativeByteBuffer data = new NativeByteBuffer((8 + b.length) + dataForEncryption.length());
                            dataForEncryption.position(0);
                            data.writeInt64(encryptedChat2.key_fingerprint);
                            data.writeBytes(b);
                            data.writeBytes(dataForEncryption);
                            dataForEncryption.reuse();
                            data.position(0);
                            int i;
                            if (inputEncryptedFile != null) {
                                i = len;
                                reqToSend = new TL_messages_sendEncryptedFile();
                                reqToSend.data = data;
                                reqToSend.random_id = decryptedMessage.random_id;
                                reqToSend.peer = new TL_inputEncryptedChat();
                                reqToSend.peer.chat_id = encryptedChat2.id;
                                reqToSend.peer.access_hash = encryptedChat2.access_hash;
                                reqToSend.file = inputEncryptedFile;
                            } else if (decryptedMessage instanceof TL_decryptedMessageService) {
                                reqToSend = new TL_messages_sendEncryptedService();
                                reqToSend.data = data;
                                reqToSend.random_id = decryptedMessage.random_id;
                                reqToSend.peer = new TL_inputEncryptedChat();
                                reqToSend.peer.chat_id = encryptedChat2.id;
                                reqToSend.peer.access_hash = encryptedChat2.access_hash;
                            } else {
                                i = len;
                                reqToSend = new TL_messages_sendEncrypted();
                                reqToSend.data = data;
                                reqToSend.random_id = decryptedMessage.random_id;
                                reqToSend.peer = new TL_inputEncryptedChat();
                                reqToSend.peer.chat_id = encryptedChat2.id;
                                reqToSend.peer.access_hash = encryptedChat2.access_hash;
                            }
                            ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(reqToSend, new C18131(), 64);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                return;
            }
        }
        SecretChatHelper secretChatHelper = this;
        message = newMsgObj;
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
        SecretChatHelper secretChatHelper = this;
        EncryptedChat encryptedChat = chat;
        EncryptedFile encryptedFile = file;
        int i = date;
        TLObject tLObject = object;
        if (tLObject != null) {
            int from_id = encryptedChat.admin_id;
            if (from_id == UserConfig.getInstance(secretChatHelper.currentAccount).getClientUserId()) {
                from_id = encryptedChat.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20 && encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint == 0 && encryptedChat.key_use_count_in >= (short) 120) {
                requestNewSecretChatKey(chat);
            }
            if (encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint != 0 && !new_key_used) {
                encryptedChat.future_auth_key = new byte[256];
                encryptedChat.future_key_fingerprint = 0;
                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
            } else if (encryptedChat.exchange_id != 0 && new_key_used) {
                encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                encryptedChat.auth_key = encryptedChat.future_auth_key;
                encryptedChat.key_create_date = ConnectionsManager.getInstance(secretChatHelper.currentAccount).getCurrentTime();
                encryptedChat.future_auth_key = new byte[256];
                encryptedChat.future_key_fingerprint = 0;
                encryptedChat.key_use_count_in = (short) 0;
                encryptedChat.key_use_count_out = (short) 0;
                encryptedChat.exchange_id = 0;
                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
            }
            byte[] thumb;
            byte[] thumb2;
            if (tLObject instanceof TL_decryptedMessage) {
                TL_message newMessage;
                MessageMedia messageMedia;
                TL_decryptedMessage decryptedMessage = (TL_decryptedMessage) tLObject;
                if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 17) {
                    newMessage = new TL_message_secret();
                    newMessage.ttl = decryptedMessage.ttl;
                    newMessage.entities = decryptedMessage.entities;
                } else {
                    newMessage = new TL_message();
                    newMessage.ttl = encryptedChat.ttl;
                }
                newMessage.message = decryptedMessage.message;
                newMessage.date = i;
                int newMessageId = UserConfig.getInstance(secretChatHelper.currentAccount).getNewMessageId();
                newMessage.id = newMessageId;
                newMessage.local_id = newMessageId;
                UserConfig.getInstance(secretChatHelper.currentAccount).saveConfig(false);
                newMessage.from_id = from_id;
                newMessage.to_id = new TL_peerUser();
                newMessage.random_id = decryptedMessage.random_id;
                newMessage.to_id.user_id = UserConfig.getInstance(secretChatHelper.currentAccount).getClientUserId();
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
                newMessage.dialog_id = ((long) encryptedChat.id) << 32;
                if (decryptedMessage.reply_to_random_id != 0) {
                    newMessage.reply_to_random_id = decryptedMessage.reply_to_random_id;
                    newMessage.flags |= 8;
                }
                if (decryptedMessage.media != null) {
                    if (!(decryptedMessage.media instanceof TL_decryptedMessageMediaEmpty)) {
                        if (decryptedMessage.media instanceof TL_decryptedMessageMediaWebPage) {
                            newMessage.media = new TL_messageMediaWebPage();
                            newMessage.media.webpage = new TL_webPageUrlPending();
                            newMessage.media.webpage.url = decryptedMessage.media.url;
                        } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaContact) {
                            newMessage.media = new TL_messageMediaContact();
                            newMessage.media.last_name = decryptedMessage.media.last_name;
                            newMessage.media.first_name = decryptedMessage.media.first_name;
                            newMessage.media.phone_number = decryptedMessage.media.phone_number;
                            newMessage.media.user_id = decryptedMessage.media.user_id;
                        } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaGeoPoint) {
                            newMessage.media = new TL_messageMediaGeo();
                            newMessage.media.geo = new TL_geoPoint();
                            newMessage.media.geo.lat = decryptedMessage.media.lat;
                            newMessage.media.geo._long = decryptedMessage.media._long;
                        } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaPhoto) {
                            if (!(decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null)) {
                                if (decryptedMessage.media.iv.length == 32) {
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
                                        r10 = newMessage.media;
                                        r10.flags |= 4;
                                    }
                                    TL_photoSize big = new TL_photoSize();
                                    big.w = decryptedMessage.media.f34w;
                                    big.h = decryptedMessage.media.f33h;
                                    big.type = "x";
                                    big.size = encryptedFile.size;
                                    big.location = new TL_fileEncryptedLocation();
                                    big.location.key = decryptedMessage.media.key;
                                    big.location.iv = decryptedMessage.media.iv;
                                    big.location.dc_id = encryptedFile.dc_id;
                                    big.location.volume_id = encryptedFile.id;
                                    big.location.secret = encryptedFile.access_hash;
                                    big.location.local_id = encryptedFile.key_fingerprint;
                                    newMessage.media.photo.sizes.add(big);
                                }
                            }
                            return null;
                        } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaVideo) {
                            if (!(decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null)) {
                                if (decryptedMessage.media.iv.length == 32) {
                                    newMessage.media = new TL_messageMediaDocument();
                                    r10 = newMessage.media;
                                    r10.flags |= 3;
                                    newMessage.media.document = new TL_documentEncrypted();
                                    newMessage.media.document.key = decryptedMessage.media.key;
                                    newMessage.media.document.iv = decryptedMessage.media.iv;
                                    newMessage.media.document.dc_id = encryptedFile.dc_id;
                                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    newMessage.media.document.date = i;
                                    newMessage.media.document.size = encryptedFile.size;
                                    newMessage.media.document.id = encryptedFile.id;
                                    newMessage.media.document.access_hash = encryptedFile.access_hash;
                                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                                    if (newMessage.media.document.mime_type == null) {
                                        newMessage.media.document.mime_type = MimeTypes.VIDEO_MP4;
                                    }
                                    thumb2 = ((TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                                    if (thumb2 == null || thumb2.length == 0 || thumb2.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                                        newMessage.media.document.thumb = new TL_photoSizeEmpty();
                                        newMessage.media.document.thumb.type = "s";
                                    } else {
                                        newMessage.media.document.thumb = new TL_photoCachedSize();
                                        newMessage.media.document.thumb.bytes = thumb2;
                                        newMessage.media.document.thumb.f43w = decryptedMessage.media.thumb_w;
                                        newMessage.media.document.thumb.f42h = decryptedMessage.media.thumb_h;
                                        newMessage.media.document.thumb.type = "s";
                                        newMessage.media.document.thumb.location = new TL_fileLocationUnavailable();
                                    }
                                    TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                                    attributeVideo.w = decryptedMessage.media.f34w;
                                    attributeVideo.h = decryptedMessage.media.f33h;
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
                                }
                            }
                            return null;
                        } else if (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument) {
                            if (!(decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null)) {
                                if (decryptedMessage.media.iv.length == 32) {
                                    newMessage.media = new TL_messageMediaDocument();
                                    messageMedia = newMessage.media;
                                    messageMedia.flags |= 3;
                                    newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    newMessage.media.document = new TL_documentEncrypted();
                                    newMessage.media.document.id = encryptedFile.id;
                                    newMessage.media.document.access_hash = encryptedFile.access_hash;
                                    newMessage.media.document.date = i;
                                    if (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument_layer8) {
                                        TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                                        fileName.file_name = decryptedMessage.media.file_name;
                                        newMessage.media.document.attributes.add(fileName);
                                    } else {
                                        newMessage.media.document.attributes = decryptedMessage.media.attributes;
                                    }
                                    newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                                    newMessage.media.document.size = decryptedMessage.media.size != 0 ? Math.min(decryptedMessage.media.size, encryptedFile.size) : encryptedFile.size;
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
                                        newMessage.media.document.thumb.f43w = decryptedMessage.media.thumb_w;
                                        newMessage.media.document.thumb.f42h = decryptedMessage.media.thumb_h;
                                        newMessage.media.document.thumb.type = "s";
                                        newMessage.media.document.thumb.location = new TL_fileLocationUnavailable();
                                    }
                                    newMessage.media.document.dc_id = encryptedFile.dc_id;
                                    if (MessageObject.isVoiceMessage(newMessage) || MessageObject.isRoundVideoMessage(newMessage)) {
                                        newMessage.media_unread = true;
                                    }
                                }
                            }
                            return null;
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
                            if (!(decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null)) {
                                if (decryptedMessage.media.iv.length == 32) {
                                    newMessage.media = new TL_messageMediaDocument();
                                    messageMedia = newMessage.media;
                                    messageMedia.flags |= 3;
                                    newMessage.media.document = new TL_documentEncrypted();
                                    newMessage.media.document.key = decryptedMessage.media.key;
                                    newMessage.media.document.iv = decryptedMessage.media.iv;
                                    newMessage.media.document.id = encryptedFile.id;
                                    newMessage.media.document.access_hash = encryptedFile.access_hash;
                                    newMessage.media.document.date = i;
                                    newMessage.media.document.size = encryptedFile.size;
                                    newMessage.media.document.dc_id = encryptedFile.dc_id;
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
                                }
                            }
                            return null;
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
                        if (newMessage.ttl != 0 && newMessage.media.ttl_seconds == 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            messageMedia = newMessage.media;
                            messageMedia.flags |= 4;
                        }
                        return newMessage;
                    }
                }
                newMessage.media = new TL_messageMediaEmpty();
                newMessage.media.ttl_seconds = newMessage.ttl;
                messageMedia = newMessage.media;
                messageMedia.flags |= 4;
                return newMessage;
            } else if (tLObject instanceof TL_decryptedMessageService) {
                TL_decryptedMessageService serviceMessage = (TL_decryptedMessageService) tLObject;
                if (!(serviceMessage.action instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    if (!(serviceMessage.action instanceof TL_decryptedMessageActionScreenshotMessages)) {
                        final long did;
                        if (serviceMessage.action instanceof TL_decryptedMessageActionFlushHistory) {
                            did = ((long) encryptedChat.id) << 32;
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.SecretChatHelper$6$1 */
                                class C04591 implements Runnable {

                                    /* renamed from: org.telegram.messenger.SecretChatHelper$6$1$1 */
                                    class C04581 implements Runnable {
                                        C04581() {
                                        }

                                        public void run() {
                                            NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processReadMessages(null, did, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
                                            LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
                                            dialogsToUpdate.put(did, Integer.valueOf(0));
                                            NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                                        }
                                    }

                                    C04591() {
                                    }

                                    public void run() {
                                        AndroidUtilities.runOnUIThread(new C04581());
                                    }
                                }

                                public void run() {
                                    TL_dialog dialog = (TL_dialog) MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.get(did);
                                    if (dialog != null) {
                                        dialog.unread_count = 0;
                                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessage.remove(dialog.id);
                                    }
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new C04591());
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).deleteDialog(did, 1);
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), Boolean.valueOf(false));
                                }
                            });
                            return null;
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionDeleteMessages) {
                            if (!serviceMessage.action.random_ids.isEmpty()) {
                                secretChatHelper.pendingEncMessagesToDelete.addAll(serviceMessage.action.random_ids);
                            }
                            return null;
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionReadMessages) {
                            if (!serviceMessage.action.random_ids.isEmpty()) {
                                int time = ConnectionsManager.getInstance(secretChatHelper.currentAccount).getCurrentTime();
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).createTaskForSecretChat(encryptedChat.id, time, time, 1, serviceMessage.action.random_ids);
                            }
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionNotifyLayer) {
                            applyPeerLayer(encryptedChat, serviceMessage.action.layer);
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionRequestKey) {
                            if (encryptedChat.exchange_id != 0) {
                                if (encryptedChat.exchange_id > serviceMessage.action.exchange_id) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("we already have request key with higher exchange_id");
                                    }
                                    return null;
                                }
                                sendAbortKeyMessage(encryptedChat, null, encryptedChat.exchange_id);
                            }
                            thumb2 = new byte[256];
                            Utilities.random.nextBytes(thumb2);
                            BigInteger p = new BigInteger(1, MessagesStorage.getInstance(secretChatHelper.currentAccount).getSecretPBytes());
                            BigInteger g_b = BigInteger.valueOf((long) MessagesStorage.getInstance(secretChatHelper.currentAccount).getSecretG()).modPow(new BigInteger(1, thumb2), p);
                            BigInteger g_a = new BigInteger(1, serviceMessage.action.g_a);
                            if (Utilities.isGoodGaAndGb(g_a, p)) {
                                byte[] g_b_bytes = g_b.toByteArray();
                                if (g_b_bytes.length > 256) {
                                    thumb = new byte[256];
                                    System.arraycopy(g_b_bytes, 1, thumb, 0, 256);
                                    g_b_bytes = thumb;
                                }
                                g_a = g_a.modPow(new BigInteger(1, thumb2), p);
                                thumb = g_a.toByteArray();
                                if (thumb.length > 256) {
                                    correctedAuth = new byte[256];
                                    System.arraycopy(thumb, thumb.length - 256, correctedAuth, null, 256);
                                    thumb = correctedAuth;
                                } else {
                                    if (thumb.length < 256) {
                                        correctedAuth = new byte[256];
                                        System.arraycopy(thumb, 0, correctedAuth, 256 - thumb.length, thumb.length);
                                        for (a = 0; a < 256 - thumb.length; a++) {
                                            thumb[a] = (byte) 0;
                                        }
                                        thumb = correctedAuth;
                                    }
                                }
                                correctedAuth = Utilities.computeSHA1(thumb);
                                correctedAuth = new byte[8];
                                System.arraycopy(correctedAuth, correctedAuth.length - 8, correctedAuth, 0, 8);
                                encryptedChat.exchange_id = serviceMessage.action.exchange_id;
                                encryptedChat.future_auth_key = thumb;
                                encryptedChat.future_key_fingerprint = Utilities.bytesToLong(correctedAuth);
                                encryptedChat.g_a_or_b = g_b_bytes;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                                sendAcceptKeyMessage(encryptedChat, null);
                            } else {
                                sendAbortKeyMessage(encryptedChat, null, serviceMessage.action.exchange_id);
                                return null;
                            }
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionAcceptKey) {
                            if (encryptedChat.exchange_id == serviceMessage.action.exchange_id) {
                                g_a = new BigInteger(1, MessagesStorage.getInstance(secretChatHelper.currentAccount).getSecretPBytes());
                                BigInteger i_authKey = new BigInteger(1, serviceMessage.action.g_b);
                                if (Utilities.isGoodGaAndGb(i_authKey, g_a)) {
                                    byte[] correctedAuth;
                                    thumb = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), g_a).toByteArray();
                                    if (thumb.length > 256) {
                                        correctedAuth = new byte[256];
                                        System.arraycopy(thumb, thumb.length - 256, correctedAuth, 0, 256);
                                        thumb = correctedAuth;
                                    } else if (thumb.length < 256) {
                                        correctedAuth = new byte[256];
                                        byte b = (byte) 0;
                                        System.arraycopy(thumb, 0, correctedAuth, 256 - thumb.length, thumb.length);
                                        a = 0;
                                        while (a < 256 - thumb.length) {
                                            thumb[a] = b;
                                            a++;
                                            b = (byte) 0;
                                        }
                                        thumb = correctedAuth;
                                    }
                                    correctedAuth = Utilities.computeSHA1(thumb);
                                    correctedAuth = new byte[8];
                                    System.arraycopy(correctedAuth, correctedAuth.length - 8, correctedAuth, 0, 8);
                                    long fingerprint = Utilities.bytesToLong(correctedAuth);
                                    if (serviceMessage.action.key_fingerprint == fingerprint) {
                                        encryptedChat.future_auth_key = thumb;
                                        encryptedChat.future_key_fingerprint = fingerprint;
                                        MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                                        sendCommitKeyMessage(encryptedChat, null);
                                    } else {
                                        encryptedChat.future_auth_key = new byte[256];
                                        encryptedChat.future_key_fingerprint = 0;
                                        encryptedChat.exchange_id = 0;
                                        MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                                        sendAbortKeyMessage(encryptedChat, null, serviceMessage.action.exchange_id);
                                    }
                                } else {
                                    encryptedChat.future_auth_key = new byte[256];
                                    encryptedChat.future_key_fingerprint = 0;
                                    encryptedChat.exchange_id = 0;
                                    MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                                    sendAbortKeyMessage(encryptedChat, null, serviceMessage.action.exchange_id);
                                    return null;
                                }
                            }
                            encryptedChat.future_auth_key = new byte[256];
                            encryptedChat.future_key_fingerprint = 0;
                            encryptedChat.exchange_id = 0;
                            MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                            sendAbortKeyMessage(encryptedChat, null, serviceMessage.action.exchange_id);
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionCommitKey) {
                            if (encryptedChat.exchange_id == serviceMessage.action.exchange_id && encryptedChat.future_key_fingerprint == serviceMessage.action.key_fingerprint) {
                                did = encryptedChat.key_fingerprint;
                                correctedAuth = encryptedChat.auth_key;
                                encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                                encryptedChat.auth_key = encryptedChat.future_auth_key;
                                encryptedChat.key_create_date = ConnectionsManager.getInstance(secretChatHelper.currentAccount).getCurrentTime();
                                encryptedChat.future_auth_key = correctedAuth;
                                encryptedChat.future_key_fingerprint = did;
                                encryptedChat.key_use_count_in = (short) 0;
                                encryptedChat.key_use_count_out = (short) 0;
                                encryptedChat.exchange_id = 0;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                                sendNoopMessage(encryptedChat, null);
                            } else {
                                encryptedChat.future_auth_key = new byte[256];
                                encryptedChat.future_key_fingerprint = 0;
                                encryptedChat.exchange_id = 0;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                                sendAbortKeyMessage(encryptedChat, null, serviceMessage.action.exchange_id);
                            }
                        } else if (serviceMessage.action instanceof TL_decryptedMessageActionAbortKey) {
                            if (encryptedChat.exchange_id == serviceMessage.action.exchange_id) {
                                encryptedChat.future_auth_key = new byte[256];
                                encryptedChat.future_key_fingerprint = 0;
                                encryptedChat.exchange_id = 0;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat);
                            }
                        } else if (!(serviceMessage.action instanceof TL_decryptedMessageActionNoop)) {
                            if (!(serviceMessage.action instanceof TL_decryptedMessageActionResend)) {
                                return null;
                            }
                            if (serviceMessage.action.end_seq_no >= encryptedChat.in_seq_no) {
                                if (serviceMessage.action.end_seq_no >= serviceMessage.action.start_seq_no) {
                                    if (serviceMessage.action.start_seq_no < encryptedChat.in_seq_no) {
                                        serviceMessage.action.start_seq_no = encryptedChat.in_seq_no;
                                    }
                                    resendMessages(serviceMessage.action.start_seq_no, serviceMessage.action.end_seq_no, encryptedChat);
                                }
                            }
                            return null;
                        }
                    }
                }
                TL_messageService newMessage2 = new TL_messageService();
                if (serviceMessage.action instanceof TL_decryptedMessageActionSetMessageTTL) {
                    newMessage2.action = new TL_messageEncryptedAction();
                    if (serviceMessage.action.ttl_seconds < 0 || serviceMessage.action.ttl_seconds > 31536000) {
                        serviceMessage.action.ttl_seconds = 31536000;
                    }
                    encryptedChat.ttl = serviceMessage.action.ttl_seconds;
                    newMessage2.action.encryptedAction = serviceMessage.action;
                    MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChatTTL(encryptedChat);
                } else if (serviceMessage.action instanceof TL_decryptedMessageActionScreenshotMessages) {
                    newMessage2.action = new TL_messageEncryptedAction();
                    newMessage2.action.encryptedAction = serviceMessage.action;
                }
                int newMessageId2 = UserConfig.getInstance(secretChatHelper.currentAccount).getNewMessageId();
                newMessage2.id = newMessageId2;
                newMessage2.local_id = newMessageId2;
                UserConfig.getInstance(secretChatHelper.currentAccount).saveConfig(false);
                newMessage2.unread = true;
                newMessage2.flags = 256;
                newMessage2.date = i;
                newMessage2.from_id = from_id;
                newMessage2.to_id = new TL_peerUser();
                newMessage2.to_id.user_id = UserConfig.getInstance(secretChatHelper.currentAccount).getClientUserId();
                newMessage2.dialog_id = ((long) encryptedChat.id) << 32;
                return newMessage2;
            } else if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unknown message ");
                stringBuilder.append(tLObject);
                FileLog.m1e(stringBuilder.toString());
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
        if (encryptedChat != null) {
            if (endSeq - startSeq >= 0) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.SecretChatHelper$7$1 */
                    class C04611 implements Comparator<Message> {
                        C04611() {
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
                            SQLiteCursor cursor = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase();
                            r7 = new Object[5];
                            boolean z = false;
                            r7[0] = Integer.valueOf(encryptedChat.id);
                            int i = 1;
                            r7[1] = Integer.valueOf(sSeq);
                            r7[2] = Integer.valueOf(sSeq);
                            r7[3] = Integer.valueOf(endSeq);
                            r7[4] = Integer.valueOf(endSeq);
                            cursor = cursor.queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", r7), new Object[0]);
                            boolean exists = cursor.next();
                            cursor.dispose();
                            if (!exists) {
                                long dialog_id = ((long) encryptedChat.id) << 32;
                                SparseArray<Message> messagesToResend = new SparseArray();
                                final ArrayList<Message> messages = new ArrayList();
                                for (int a = sSeq; a < endSeq; a += 2) {
                                    messagesToResend.put(a, null);
                                }
                                cursor = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)}), new Object[0]);
                                while (cursor.next()) {
                                    Message message;
                                    boolean exists2;
                                    long random_id = cursor.longValue(i);
                                    if (random_id == 0) {
                                        random_id = Utilities.random.nextLong();
                                    }
                                    int seq_in = cursor.intValue(2);
                                    int seq_out = cursor.intValue(3);
                                    int mid = cursor.intValue(5);
                                    NativeByteBuffer data = cursor.byteBufferValue(z);
                                    if (data != null) {
                                        message = Message.TLdeserialize(data, data.readInt32(z), z);
                                        message.readAttachPath(data, UserConfig.getInstance(SecretChatHelper.this.currentAccount).clientUserId);
                                        data.reuse();
                                        message.random_id = random_id;
                                        message.dialog_id = dialog_id;
                                        message.seq_in = seq_in;
                                        message.seq_out = seq_out;
                                        exists2 = exists;
                                        message.ttl = cursor.intValue(4);
                                    } else {
                                        exists2 = exists;
                                        message = SecretChatHelper.this.createDeleteMessage(mid, seq_out, seq_in, random_id, encryptedChat);
                                    }
                                    messages.add(message);
                                    messagesToResend.remove(seq_out);
                                    exists = exists2;
                                    z = false;
                                    i = 1;
                                }
                                cursor.dispose();
                                if (messagesToResend.size() != 0) {
                                    for (int a2 = 0; a2 < messagesToResend.size(); a2++) {
                                        messages.add(SecretChatHelper.this.createDeleteMessage(UserConfig.getInstance(SecretChatHelper.this.currentAccount).getNewMessageId(), messagesToResend.keyAt(a2), 0, Utilities.random.nextLong(), encryptedChat));
                                    }
                                    UserConfig.getInstance(SecretChatHelper.this.currentAccount).saveConfig(false);
                                }
                                Collections.sort(messages, new C04611());
                                ArrayList<EncryptedChat> encryptedChats = new ArrayList();
                                encryptedChats.add(encryptedChat);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        for (int a = 0; a < messages.size(); a++) {
                                            MessageObject messageObject = new MessageObject(SecretChatHelper.this.currentAccount, (Message) messages.get(a), false);
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
    }

    public void checkSecretHoles(EncryptedChat chat, ArrayList<Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = (ArrayList) this.secretHolesQueue.get(chat.id);
        if (holes != null) {
            Collections.sort(holes, new C04648());
            boolean update = false;
            int a = 0;
            while (a < holes.size()) {
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
        boolean incoming2;
        NativeByteBuffer nativeByteBuffer = is;
        byte[] bArr = messageKey;
        int i = version;
        if (i == 1) {
            incoming2 = false;
        } else {
            incoming2 = incoming;
        }
        byte[] bArr2 = keyToDecrypt;
        MessageKeyData keyData = MessageKeyData.generateMessageKeyData(bArr2, bArr, incoming2, i);
        Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, false, false, 24, is.limit() - 24);
        int len = nativeByteBuffer.readInt32(false);
        if (i == 2) {
            if (Utilities.arraysEquals(bArr, 0, Utilities.computeSHA256(bArr2, (incoming2 ? 8 : 0) + 88, 32, nativeByteBuffer.buffer, 24, nativeByteBuffer.buffer.limit()), 8)) {
                int i2 = 15;
            } else {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    nativeByteBuffer.position(24);
                }
                return false;
            }
        }
        int i3 = 24;
        int l = len + 28;
        i2 = 15;
        if (l < nativeByteBuffer.buffer.limit() - 15 || l > nativeByteBuffer.buffer.limit()) {
            l = nativeByteBuffer.buffer.limit();
        }
        byte[] messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer, i3, l);
        if (!Utilities.arraysEquals(bArr, 0, messageKeyFull, messageKeyFull.length - 16)) {
            if (encryptOnError) {
                Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                nativeByteBuffer.position(i3);
            }
            return false;
        }
        if (len > 0) {
            if (len <= is.limit() - 28) {
                l = (is.limit() - 28) - len;
                if ((i != 2 || (l >= 12 && l <= 1024)) && (i != 1 || l <= r6)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    protected ArrayList<Message> decryptMessage(EncryptedMessage message) {
        Throwable e;
        EncryptedMessage encryptedMessage = message;
        EncryptedChat chat = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(encryptedMessage.chat_id, true);
        if (chat == null) {
        } else if (chat instanceof TL_encryptedChatDiscarded) {
            r3 = chat;
        } else {
            try {
                NativeByteBuffer is = new NativeByteBuffer(encryptedMessage.bytes.length);
                is.writeBytes(encryptedMessage.bytes);
                is.position(0);
                long fingerprint = is.readInt64(false);
                byte[] keyToDecrypt = null;
                boolean new_key_used = false;
                if (chat.key_fingerprint == fingerprint) {
                    try {
                        keyToDecrypt = chat.auth_key;
                    } catch (Throwable e2) {
                        e = e2;
                        r3 = chat;
                        FileLog.m3e(e);
                        return null;
                    }
                } else if (chat.future_key_fingerprint != 0) {
                    if (chat.future_key_fingerprint == fingerprint) {
                        keyToDecrypt = chat.future_auth_key;
                        new_key_used = true;
                    }
                }
                byte[] keyToDecrypt2 = keyToDecrypt;
                boolean new_key_used2 = new_key_used;
                boolean mtprotoVersion = AndroidUtilities.getPeerLayerVersion(chat.layer) >= 73 ? true : true;
                new_key_used = mtprotoVersion;
                boolean decryptedWithVersion;
                boolean z;
                long j;
                boolean new_key_used3;
                if (keyToDecrypt2 != null) {
                    NativeByteBuffer is2;
                    int mtprotoVersion2;
                    TLObject object;
                    byte[] messageKey = is.readData(16, false);
                    boolean incoming = chat.admin_id == UserConfig.getInstance(r14.currentAccount).getClientUserId();
                    boolean tryAnotherDecrypt = true;
                    if (new_key_used) {
                        if (chat.mtproto_seq != 0) {
                            tryAnotherDecrypt = false;
                        }
                    }
                    boolean tryAnotherDecrypt2 = tryAnotherDecrypt;
                    decryptedWithVersion = new_key_used;
                    boolean z2 = mtprotoVersion;
                    boolean mtprotoVersion3 = z2;
                    boolean new_key_used4 = new_key_used2;
                    if (decryptWithMtProtoVersion(is, keyToDecrypt2, messageKey, z2, incoming, tryAnotherDecrypt2)) {
                        is2 = is;
                        mtprotoVersion2 = chat;
                        z = mtprotoVersion3;
                        tryAnotherDecrypt = true;
                        new_key_used = decryptedWithVersion;
                    } else {
                        tryAnotherDecrypt = mtprotoVersion3;
                        if (tryAnotherDecrypt) {
                            new_key_used = true;
                            if (tryAnotherDecrypt2) {
                                is2 = is;
                                mtprotoVersion2 = chat;
                                tryAnotherDecrypt = true;
                                try {
                                    if (!decryptWithMtProtoVersion(is, keyToDecrypt2, messageKey, 1, incoming, false)) {
                                    }
                                } catch (Throwable e22) {
                                    e = e22;
                                    FileLog.m3e(e);
                                    return null;
                                }
                            }
                            j = fingerprint;
                            is2 = is;
                            r3 = chat;
                            return null;
                        }
                        j = fingerprint;
                        is2 = is;
                        mtprotoVersion2 = chat;
                        tryAnotherDecrypt = true;
                        new_key_used = true;
                        if (!decryptWithMtProtoVersion(is2, keyToDecrypt2, messageKey, 2, incoming, tryAnotherDecrypt2)) {
                            return null;
                        }
                    }
                    NativeByteBuffer is3 = is2;
                    TLObject object2 = TLClassStore.Instance().TLdeserialize(is3, is3.readInt32(false), false);
                    is3.reuse();
                    chat = new_key_used4;
                    if (chat == null && AndroidUtilities.getPeerLayerVersion(mtprotoVersion2.layer) >= 20) {
                        mtprotoVersion2.key_use_count_in = (short) (mtprotoVersion2.key_use_count_in + tryAnotherDecrypt);
                    }
                    if (object2 instanceof TL_decryptedMessageLayer) {
                        TL_decryptedMessageLayer layer = (TL_decryptedMessageLayer) object2;
                        if (mtprotoVersion2.seq_in == 0 && mtprotoVersion2.seq_out == 0) {
                            if (mtprotoVersion2.admin_id == UserConfig.getInstance(r14.currentAccount).getClientUserId()) {
                                mtprotoVersion2.seq_out = tryAnotherDecrypt;
                                mtprotoVersion2.seq_in = -2;
                            } else {
                                mtprotoVersion2.seq_in = -1;
                            }
                        }
                        if (layer.random_bytes.length < 15) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m1e("got random bytes less than needed");
                            }
                            return null;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("current chat in_seq = ");
                            stringBuilder.append(mtprotoVersion2.seq_in);
                            stringBuilder.append(" out_seq = ");
                            stringBuilder.append(mtprotoVersion2.seq_out);
                            FileLog.m0d(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("got message with in_seq = ");
                            stringBuilder.append(layer.in_seq_no);
                            stringBuilder.append(" out_seq = ");
                            stringBuilder.append(layer.out_seq_no);
                            FileLog.m0d(stringBuilder.toString());
                        }
                        if (layer.out_seq_no <= mtprotoVersion2.seq_in) {
                            return null;
                        }
                        if (new_key_used == tryAnotherDecrypt && mtprotoVersion2.mtproto_seq != 0 && layer.out_seq_no >= mtprotoVersion2.mtproto_seq) {
                            return null;
                        }
                        if (mtprotoVersion2.seq_in != layer.out_seq_no - 2) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m1e("got hole");
                            }
                            ArrayList<TL_decryptedMessageHolder> arr = (ArrayList) r14.secretHolesQueue.get(mtprotoVersion2.id);
                            if (arr == null) {
                                arr = new ArrayList();
                                r14.secretHolesQueue.put(mtprotoVersion2.id, arr);
                            }
                            if (arr.size() >= 4) {
                                r14.secretHolesQueue.remove(mtprotoVersion2.id);
                                final TL_encryptedChatDiscarded newChat = new TL_encryptedChatDiscarded();
                                newChat.id = mtprotoVersion2.id;
                                newChat.user_id = mtprotoVersion2.user_id;
                                newChat.auth_key = mtprotoVersion2.auth_key;
                                newChat.key_create_date = mtprotoVersion2.key_create_date;
                                newChat.key_use_count_in = mtprotoVersion2.key_use_count_in;
                                newChat.key_use_count_out = mtprotoVersion2.key_use_count_out;
                                newChat.seq_in = mtprotoVersion2.seq_in;
                                newChat.seq_out = mtprotoVersion2.seq_out;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(newChat, false);
                                        MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(newChat);
                                        NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
                                    }
                                });
                                declineSecretChat(mtprotoVersion2.id);
                                return null;
                            }
                            TL_decryptedMessageHolder holder = new TL_decryptedMessageHolder();
                            holder.layer = layer;
                            holder.file = encryptedMessage.file;
                            holder.date = encryptedMessage.date;
                            holder.new_key_used = chat;
                            holder.decryptedWithVersion = new_key_used;
                            arr.add(holder);
                            return null;
                        }
                        if (new_key_used) {
                            mtprotoVersion2.mtproto_seq = Math.min(mtprotoVersion2.mtproto_seq, mtprotoVersion2.seq_in);
                        }
                        applyPeerLayer(mtprotoVersion2, layer.layer);
                        mtprotoVersion2.seq_in = layer.out_seq_no;
                        mtprotoVersion2.in_seq_no = layer.in_seq_no;
                        MessagesStorage.getInstance(r14.currentAccount).updateEncryptedChatSeq(mtprotoVersion2, tryAnotherDecrypt);
                        object = layer.message;
                    } else {
                        if (!(object2 instanceof TL_decryptedMessageService)) {
                            new_key_used3 = chat;
                        } else if (((TL_decryptedMessageService) object2).action instanceof TL_decryptedMessageActionNotifyLayer) {
                            object = object2;
                        } else {
                            EncryptedChat encryptedChat = chat;
                        }
                        return null;
                    }
                    object2 = new ArrayList();
                    new_key_used3 = chat;
                    new_key_used2 = processDecryptedObject(mtprotoVersion2, encryptedMessage.file, encryptedMessage.date, object, chat);
                    if (new_key_used2) {
                        object2.add(new_key_used2);
                    }
                    checkSecretHoles(mtprotoVersion2, object2);
                    return object2;
                }
                decryptedWithVersion = new_key_used;
                z = mtprotoVersion;
                new_key_used3 = new_key_used2;
                j = fingerprint;
                int i = 0;
                int i2 = 1;
                is.reuse();
                if (BuildVars.LOGS_ENABLED) {
                    Object[] objArr = new Object[i2];
                    objArr[i] = Long.valueOf(j);
                    FileLog.m1e(String.format("fingerprint mismatch %x", objArr));
                }
                return null;
            } catch (Throwable e222) {
                r3 = chat;
                e = e222;
                FileLog.m3e(e);
                return null;
            }
        }
        return null;
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
            } else {
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
            }
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
                class C18111 implements RequestDelegate {
                    C18111() {
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
                        messages_DhConfig res = (messages_DhConfig) response;
                        if (response instanceof TL_messages_dhConfig) {
                            if (Utilities.isGoodPrime(res.f56p, res.f55g)) {
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(res.f56p);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(res.f55g);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(res.version);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                            } else {
                                SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                                SecretChatHelper.this.declineSecretChat(encryptedChat.id);
                                return;
                            }
                        }
                        byte[] salt = new byte[256];
                        for (int a = 0; a < 256; a++) {
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
                                for (int a2 = 0; a2 < 256 - authKey.length; a2++) {
                                    authKey[a2] = (byte) 0;
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
                            ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(req2, new C18111());
                        } else {
                            SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                            SecretChatHelper.this.declineSecretChat(encryptedChat.id);
                            return;
                        }
                    }
                    SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                }
            });
        }
    }

    public void startSecretChat(final Context context, final User user) {
        if (user != null) {
            if (context != null) {
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
                    class C04451 implements Runnable {
                        C04451() {
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
                    class C04493 implements Runnable {
                        C04493() {
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
                                if (Utilities.isGoodPrime(res.f56p, res.f55g)) {
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(res.f56p);
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(res.f55g);
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(res.version);
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                                } else {
                                    AndroidUtilities.runOnUIThread(new C04451());
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
                                class C04482 implements Runnable {
                                    C04482() {
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
                                            class C04461 implements Runnable {
                                                C04461() {
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
                                                Utilities.stageQueue.postRunnable(new C04461());
                                            }
                                        });
                                        return;
                                    }
                                    SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                                    AndroidUtilities.runOnUIThread(new C04482());
                                }
                            }, 2);
                        } else {
                            SecretChatHelper.this.delayedEncryptedChatUpdates.clear();
                            AndroidUtilities.runOnUIThread(new C04493());
                        }
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
}
