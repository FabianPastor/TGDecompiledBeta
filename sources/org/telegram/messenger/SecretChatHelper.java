package org.telegram.messenger;

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
import org.telegram.SQLite.SQLiteDatabase;
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
    class C04678 implements Comparator<TL_decryptedMessageHolder> {
        C04678() {
        }

        public int compare(TL_decryptedMessageHolder tL_decryptedMessageHolder, TL_decryptedMessageHolder tL_decryptedMessageHolder2) {
            if (tL_decryptedMessageHolder.layer.out_seq_no > tL_decryptedMessageHolder2.layer.out_seq_no) {
                return 1;
            }
            return tL_decryptedMessageHolder.layer.out_seq_no < tL_decryptedMessageHolder2.layer.out_seq_no ? -1 : null;
        }
    }

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = NUM;
        public int date;
        public int decryptedWithVersion;
        public EncryptedFile file;
        public TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            abstractSerializedData.readInt64(z);
            this.date = abstractSerializedData.readInt32(z);
            this.layer = TL_decryptedMessageLayer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (abstractSerializedData.readBool(z)) {
                this.file = EncryptedFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.new_key_used = abstractSerializedData.readBool(z);
        }

        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt64(0);
            abstractSerializedData.writeInt32(this.date);
            this.layer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeBool(this.file != null);
            if (this.file != null) {
                this.file.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeBool(this.new_key_used);
        }
    }

    public static SecretChatHelper getInstance(int i) {
        SecretChatHelper secretChatHelper = Instance[i];
        if (secretChatHelper == null) {
            synchronized (SecretChatHelper.class) {
                secretChatHelper = Instance[i];
                if (secretChatHelper == null) {
                    SecretChatHelper[] secretChatHelperArr = Instance;
                    SecretChatHelper secretChatHelper2 = new SecretChatHelper(i);
                    secretChatHelperArr[i] = secretChatHelper2;
                    secretChatHelper = secretChatHelper2;
                }
            }
        }
        return secretChatHelper;
    }

    public SecretChatHelper(int i) {
        this.currentAccount = i;
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
            final ArrayList arrayList = new ArrayList(this.pendingEncMessagesToDelete);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    for (int i = 0; i < arrayList.size(); i++) {
                        MessageObject messageObject = (MessageObject) MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessagesByRandomIds.get(((Long) arrayList.get(i)).longValue());
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

    private TL_messageService createServiceSecretMessage(EncryptedChat encryptedChat, DecryptedMessageAction decryptedMessageAction) {
        ArrayList arrayList;
        TL_messageService tL_messageService = new TL_messageService();
        tL_messageService.action = new TL_messageEncryptedAction();
        tL_messageService.action.encryptedAction = decryptedMessageAction;
        int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
        tL_messageService.id = newMessageId;
        tL_messageService.local_id = newMessageId;
        tL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        tL_messageService.unread = true;
        tL_messageService.out = true;
        tL_messageService.flags = 256;
        tL_messageService.dialog_id = ((long) encryptedChat.id) << 32;
        tL_messageService.to_id = new TL_peerUser();
        tL_messageService.send_state = 1;
        if (encryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            tL_messageService.to_id.user_id = encryptedChat.admin_id;
        } else {
            tL_messageService.to_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessageAction instanceof TL_decryptedMessageActionScreenshotMessages) == null) {
            if ((decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL) == null) {
                tL_messageService.date = 0;
                tL_messageService.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                arrayList = new ArrayList();
                arrayList.add(tL_messageService);
                MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, false, true, true, 0);
                return tL_messageService;
            }
        }
        tL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        tL_messageService.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        arrayList = new ArrayList();
        arrayList.add(tL_messageService);
        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, false, true, true, 0);
        return tL_messageService;
    }

    public void sendMessagesReadMessage(EncryptedChat encryptedChat, ArrayList<Long> arrayList, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionReadMessages();
                tL_decryptedMessageService.action.random_ids = arrayList;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    protected void processUpdateEncryption(TL_updateEncryption tL_updateEncryption, ConcurrentHashMap<Integer, User> concurrentHashMap) {
        final EncryptedChat encryptedChat = tL_updateEncryption.chat;
        long j = ((long) encryptedChat.id) << 32;
        final EncryptedChat encryptedChatDB = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(encryptedChat.id, false);
        if ((encryptedChat instanceof TL_encryptedChatRequested) && encryptedChatDB == null) {
            int i = encryptedChat.participant_id;
            if (i == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i = encryptedChat.admin_id;
            }
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (user == null) {
                user = (User) concurrentHashMap.get(Integer.valueOf(i));
            }
            encryptedChat.user_id = i;
            concurrentHashMap = new TL_dialog();
            concurrentHashMap.id = j;
            concurrentHashMap.unread_count = 0;
            concurrentHashMap.top_message = 0;
            concurrentHashMap.last_message_date = tL_updateEncryption.date;
            MessagesController.getInstance(this.currentAccount).putEncryptedChat(encryptedChat, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.put(concurrentHashMap.id, concurrentHashMap);
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs.add(concurrentHashMap);
                    MessagesController.getInstance(SecretChatHelper.this.currentAccount).sortDialogs(null);
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            });
            MessagesStorage.getInstance(this.currentAccount).putEncryptedChat(encryptedChat, user, concurrentHashMap);
            acceptSecretChat(encryptedChat);
        } else if ((encryptedChat instanceof TL_encryptedChat) == null) {
            if (encryptedChatDB != null) {
                encryptedChat.user_id = encryptedChatDB.user_id;
                encryptedChat.auth_key = encryptedChatDB.auth_key;
                encryptedChat.key_create_date = encryptedChatDB.key_create_date;
                encryptedChat.key_use_count_in = encryptedChatDB.key_use_count_in;
                encryptedChat.key_use_count_out = encryptedChatDB.key_use_count_out;
                encryptedChat.ttl = encryptedChatDB.ttl;
                encryptedChat.seq_in = encryptedChatDB.seq_in;
                encryptedChat.seq_out = encryptedChatDB.seq_out;
                encryptedChat.admin_id = encryptedChatDB.admin_id;
                encryptedChat.mtproto_seq = encryptedChatDB.mtproto_seq;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (encryptedChatDB != null) {
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(encryptedChat, false);
                    }
                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(encryptedChat);
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
                }
            });
        } else if (encryptedChatDB != null && (encryptedChatDB instanceof TL_encryptedChatWaiting) != null && (encryptedChatDB.auth_key == null || encryptedChatDB.auth_key.length == 1)) {
            encryptedChat.a_or_b = encryptedChatDB.a_or_b;
            encryptedChat.user_id = encryptedChatDB.user_id;
            processAcceptedSecretChat(encryptedChat);
        } else if (encryptedChatDB == null && this.startingSecretChat != null) {
            this.delayedEncryptedChatUpdates.add(tL_updateEncryption);
        }
    }

    public void sendMessagesDeleteMessage(EncryptedChat encryptedChat, ArrayList<Long> arrayList, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionDeleteMessages();
                tL_decryptedMessageService.action.random_ids = arrayList;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendClearHistoryMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendNotifyLayerMessage(EncryptedChat encryptedChat, Message message) {
        if ((encryptedChat instanceof TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionNotifyLayer();
                tL_decryptedMessageService.action.layer = 73;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendRequestKeyMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionRequestKey();
                tL_decryptedMessageService.action.exchange_id = encryptedChat.exchange_id;
                tL_decryptedMessageService.action.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendAcceptKeyMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionAcceptKey();
                tL_decryptedMessageService.action.exchange_id = encryptedChat.exchange_id;
                tL_decryptedMessageService.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                tL_decryptedMessageService.action.g_b = encryptedChat.g_a_or_b;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendCommitKeyMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionCommitKey();
                tL_decryptedMessageService.action.exchange_id = encryptedChat.exchange_id;
                tL_decryptedMessageService.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendAbortKeyMessage(EncryptedChat encryptedChat, Message message, long j) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionAbortKey();
                tL_decryptedMessageService.action.exchange_id = j;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendNoopMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendTTLMessage(EncryptedChat encryptedChat, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionSetMessageTTL();
                tL_decryptedMessageService.action.ttl_seconds = encryptedChat.ttl;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
                MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
                messageObject.messageOwner.send_state = 1;
                ArrayList arrayList = new ArrayList();
                arrayList.add(messageObject);
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.dialog_id, arrayList);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    public void sendScreenshotMessage(EncryptedChat encryptedChat, ArrayList<Long> arrayList, Message message) {
        if (encryptedChat instanceof TL_encryptedChat) {
            DecryptedMessage tL_decryptedMessageService = new TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TL_decryptedMessageActionScreenshotMessages();
                tL_decryptedMessageService.action.random_ids = arrayList;
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
                arrayList = new MessageObject(this.currentAccount, message, false);
                arrayList.messageOwner.send_state = 1;
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(arrayList);
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.dialog_id, arrayList2);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, null, null, null);
        }
    }

    private void updateMediaPaths(MessageObject messageObject, EncryptedFile encryptedFile, DecryptedMessage decryptedMessage, String str) {
        str = messageObject.messageOwner;
        if (encryptedFile == null) {
            return;
        }
        if ((str.media instanceof TL_messageMediaPhoto) && str.media.photo != null) {
            PhotoSize photoSize = (PhotoSize) str.media.photo.sizes.get(str.media.photo.sizes.size() - 1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(photoSize.location.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(photoSize.location.local_id);
            String stringBuilder2 = stringBuilder.toString();
            photoSize.location = new TL_fileEncryptedLocation();
            photoSize.location.key = decryptedMessage.media.key;
            photoSize.location.iv = decryptedMessage.media.iv;
            photoSize.location.dc_id = encryptedFile.dc_id;
            photoSize.location.volume_id = encryptedFile.id;
            photoSize.location.secret = encryptedFile.access_hash;
            photoSize.location.local_id = encryptedFile.key_fingerprint;
            encryptedFile = new StringBuilder();
            encryptedFile.append(photoSize.location.volume_id);
            encryptedFile.append("_");
            encryptedFile.append(photoSize.location.local_id);
            encryptedFile = encryptedFile.toString();
            File directory = FileLoader.getDirectory(4);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(stringBuilder2);
            stringBuilder3.append(".jpg");
            new File(directory, stringBuilder3.toString()).renameTo(FileLoader.getPathToAttach(photoSize));
            ImageLoader.getInstance().replaceImageInCache(stringBuilder2, encryptedFile, photoSize.location, true);
            ArrayList arrayList = new ArrayList();
            arrayList.add(str);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, false, true, false, 0);
        } else if ((str.media instanceof TL_messageMediaDocument) && str.media.document != null) {
            Document document = str.media.document;
            str.media.document = new TL_documentEncrypted();
            str.media.document.id = encryptedFile.id;
            str.media.document.access_hash = encryptedFile.access_hash;
            str.media.document.date = document.date;
            str.media.document.attributes = document.attributes;
            str.media.document.mime_type = document.mime_type;
            str.media.document.size = encryptedFile.size;
            str.media.document.key = decryptedMessage.media.key;
            str.media.document.iv = decryptedMessage.media.iv;
            str.media.document.thumb = document.thumb;
            str.media.document.dc_id = encryptedFile.dc_id;
            if (!(str.attachPath == null || str.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) == null || new File(str.attachPath).renameTo(FileLoader.getPathToAttach(str.media.document)) == null)) {
                messageObject.mediaExists = messageObject.attachPathExists;
                messageObject.attachPathExists = null;
                str.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            }
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(str);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, false, true, false, 0);
        }
    }

    public static boolean isSecretVisibleMessage(Message message) {
        return (!(message.action instanceof TL_messageEncryptedAction) || (!(message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) && (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) == null)) ? null : true;
    }

    public static boolean isSecretInvisibleMessage(Message message) {
        return ((message.action instanceof TL_messageEncryptedAction) && !(message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) && (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) == null) ? true : null;
    }

    protected void performSendEncryptedRequest(TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia, DelayedMessage delayedMessage) {
        for (int i = 0; i < tL_messages_sendEncryptedMultiMedia.files.size(); i++) {
            performSendEncryptedRequest((DecryptedMessage) tL_messages_sendEncryptedMultiMedia.messages.get(i), (Message) delayedMessage.messages.get(i), delayedMessage.encryptedChat, (InputEncryptedFile) tL_messages_sendEncryptedMultiMedia.files.get(i), (String) delayedMessage.originalPaths.get(i), (MessageObject) delayedMessage.messageObjects.get(i));
        }
    }

    protected void performSendEncryptedRequest(DecryptedMessage decryptedMessage, Message message, EncryptedChat encryptedChat, InputEncryptedFile inputEncryptedFile, String str, MessageObject messageObject) {
        final EncryptedChat encryptedChat2 = encryptedChat;
        if (!(decryptedMessage == null || encryptedChat2.auth_key == null || (encryptedChat2 instanceof TL_encryptedChatRequested))) {
            if (!(encryptedChat2 instanceof TL_encryptedChatWaiting)) {
                final Message message2 = message;
                SendMessagesHelper.getInstance(this.currentAccount).putToSendingMessages(message2);
                final DecryptedMessage decryptedMessage2 = decryptedMessage;
                final InputEncryptedFile inputEncryptedFile2 = inputEncryptedFile;
                final MessageObject messageObject2 = messageObject;
                final String str2 = str;
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.SecretChatHelper$4$1 */
                    class C18191 implements RequestDelegate {

                        /* renamed from: org.telegram.messenger.SecretChatHelper$4$1$2 */
                        class C04582 implements Runnable {
                            C04582() {
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

                        C18191() {
                        }

                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null && (decryptedMessage2.action instanceof TL_decryptedMessageActionNotifyLayer)) {
                                EncryptedChat encryptedChat = MessagesController.getInstance(SecretChatHelper.this.currentAccount).getEncryptedChat(Integer.valueOf(encryptedChat2.id));
                                if (encryptedChat == null) {
                                    encryptedChat = encryptedChat2;
                                }
                                if (encryptedChat.key_hash == null) {
                                    encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
                                }
                                if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 46 && encryptedChat.key_hash.length == 16) {
                                    try {
                                        Object computeSHA256 = Utilities.computeSHA256(encryptedChat2.auth_key, 0, encryptedChat2.auth_key.length);
                                        Object obj = new byte[36];
                                        System.arraycopy(encryptedChat2.key_hash, 0, obj, 0, 16);
                                        System.arraycopy(computeSHA256, 0, obj, 16, 20);
                                        encryptedChat.key_hash = obj;
                                        MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(encryptedChat);
                                    } catch (Throwable th) {
                                        FileLog.m3e(th);
                                    }
                                }
                                SecretChatHelper.this.sendingNotifyLayer.remove(Integer.valueOf(encryptedChat.id));
                                encryptedChat.layer = AndroidUtilities.setMyLayerVersion(encryptedChat.layer, 73);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChatLayer(encryptedChat);
                            }
                            if (message2 == null) {
                                return;
                            }
                            if (tL_error == null) {
                                tL_error = message2.attachPath;
                                final messages_SentEncryptedMessage messages_sentencryptedmessage = (messages_SentEncryptedMessage) tLObject;
                                if (SecretChatHelper.isSecretVisibleMessage(message2)) {
                                    message2.date = messages_sentencryptedmessage.date;
                                }
                                if (messageObject2 != null && (messages_sentencryptedmessage.file instanceof TL_encryptedFile)) {
                                    SecretChatHelper.this.updateMediaPaths(messageObject2, messages_sentencryptedmessage.file, decryptedMessage2, str2);
                                }
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                    /* renamed from: org.telegram.messenger.SecretChatHelper$4$1$1$1 */
                                    class C04561 implements Runnable {
                                        C04561() {
                                        }

                                        public void run() {
                                            message2.send_state = 0;
                                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(message2.id), Integer.valueOf(message2.id), message2, Long.valueOf(message2.dialog_id));
                                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processSentMessage(message2.id);
                                            if (MessageObject.isVideoMessage(message2) || MessageObject.isNewGifMessage(message2) || MessageObject.isRoundVideoMessage(message2)) {
                                                SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).stopVideoService(tL_error);
                                            }
                                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).removeFromSendingMessages(message2.id);
                                        }
                                    }

                                    public void run() {
                                        if (SecretChatHelper.isSecretInvisibleMessage(message2)) {
                                            messages_sentencryptedmessage.date = 0;
                                        }
                                        MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateMessageStateAndId(message2.random_id, Integer.valueOf(message2.id), message2.id, messages_sentencryptedmessage.date, false, 0);
                                        AndroidUtilities.runOnUIThread(new C04561());
                                    }
                                });
                                return;
                            }
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).markMessageAsSendError(message2);
                            AndroidUtilities.runOnUIThread(new C04582());
                        }
                    }

                    public void run() {
                        C04594 c04594 = this;
                        try {
                            byte[] bArr;
                            TLObject tL_decryptedMessageLayer = new TL_decryptedMessageLayer();
                            tL_decryptedMessageLayer.layer = Math.min(Math.max(46, AndroidUtilities.getMyLayerVersion(encryptedChat2.layer)), Math.max(46, AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer)));
                            tL_decryptedMessageLayer.message = decryptedMessage2;
                            tL_decryptedMessageLayer.random_bytes = new byte[15];
                            Utilities.random.nextBytes(tL_decryptedMessageLayer.random_bytes);
                            boolean z = true;
                            int i = AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 73 ? 2 : 1;
                            if (encryptedChat2.seq_in == 0 && encryptedChat2.seq_out == 0) {
                                if (encryptedChat2.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId()) {
                                    encryptedChat2.seq_out = 1;
                                    encryptedChat2.seq_in = -2;
                                } else {
                                    encryptedChat2.seq_in = -1;
                                }
                            }
                            if (message2.seq_in == 0 && message2.seq_out == 0) {
                                tL_decryptedMessageLayer.in_seq_no = encryptedChat2.seq_in > 0 ? encryptedChat2.seq_in : encryptedChat2.seq_in + 2;
                                tL_decryptedMessageLayer.out_seq_no = encryptedChat2.seq_out;
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
                                    message2.seq_in = tL_decryptedMessageLayer.in_seq_no;
                                    message2.seq_out = tL_decryptedMessageLayer.out_seq_no;
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setMessageSeq(message2.id, message2.seq_in, message2.seq_out);
                                }
                            } else {
                                tL_decryptedMessageLayer.in_seq_no = message2.seq_in;
                                tL_decryptedMessageLayer.out_seq_no = message2.seq_out;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(decryptedMessage2);
                                stringBuilder.append(" send message with in_seq = ");
                                stringBuilder.append(tL_decryptedMessageLayer.in_seq_no);
                                stringBuilder.append(" out_seq = ");
                                stringBuilder.append(tL_decryptedMessageLayer.out_seq_no);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            int objectSize = tL_decryptedMessageLayer.getObjectSize();
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(4 + objectSize);
                            nativeByteBuffer.writeInt32(objectSize);
                            tL_decryptedMessageLayer.serializeToStream(nativeByteBuffer);
                            int length = nativeByteBuffer.length();
                            objectSize = length % 16 != 0 ? 16 - (length % 16) : 0;
                            if (i == 2) {
                                objectSize += (Utilities.random.nextInt(3) + 2) * 16;
                            }
                            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(length + objectSize);
                            nativeByteBuffer.position(0);
                            nativeByteBuffer2.writeBytes(nativeByteBuffer);
                            if (objectSize != 0) {
                                bArr = new byte[objectSize];
                                Utilities.random.nextBytes(bArr);
                                nativeByteBuffer2.writeBytes(bArr);
                            }
                            bArr = new byte[16];
                            if (i != 2 || encryptedChat2.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId()) {
                                z = false;
                            }
                            if (i == 2) {
                                System.arraycopy(Utilities.computeSHA256(encryptedChat2.auth_key, (z ? 8 : 0) + 88, 32, nativeByteBuffer2.buffer, 0, nativeByteBuffer2.buffer.limit()), 8, bArr, 0, 16);
                            } else {
                                Object computeSHA1 = Utilities.computeSHA1(nativeByteBuffer.buffer);
                                System.arraycopy(computeSHA1, computeSHA1.length - 16, bArr, 0, 16);
                            }
                            nativeByteBuffer.reuse();
                            MessageKeyData generateMessageKeyData = MessageKeyData.generateMessageKeyData(encryptedChat2.auth_key, bArr, z, i);
                            Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, true, false, 0, nativeByteBuffer2.limit());
                            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer((8 + bArr.length) + nativeByteBuffer2.length());
                            nativeByteBuffer2.position(0);
                            nativeByteBuffer3.writeInt64(encryptedChat2.key_fingerprint);
                            nativeByteBuffer3.writeBytes(bArr);
                            nativeByteBuffer3.writeBytes(nativeByteBuffer2);
                            nativeByteBuffer2.reuse();
                            nativeByteBuffer3.position(0);
                            if (inputEncryptedFile2 != null) {
                                tL_decryptedMessageLayer = new TL_messages_sendEncryptedFile();
                                tL_decryptedMessageLayer.data = nativeByteBuffer3;
                                tL_decryptedMessageLayer.random_id = decryptedMessage2.random_id;
                                tL_decryptedMessageLayer.peer = new TL_inputEncryptedChat();
                                tL_decryptedMessageLayer.peer.chat_id = encryptedChat2.id;
                                tL_decryptedMessageLayer.peer.access_hash = encryptedChat2.access_hash;
                                tL_decryptedMessageLayer.file = inputEncryptedFile2;
                            } else if (decryptedMessage2 instanceof TL_decryptedMessageService) {
                                tL_decryptedMessageLayer = new TL_messages_sendEncryptedService();
                                tL_decryptedMessageLayer.data = nativeByteBuffer3;
                                tL_decryptedMessageLayer.random_id = decryptedMessage2.random_id;
                                tL_decryptedMessageLayer.peer = new TL_inputEncryptedChat();
                                tL_decryptedMessageLayer.peer.chat_id = encryptedChat2.id;
                                tL_decryptedMessageLayer.peer.access_hash = encryptedChat2.access_hash;
                            } else {
                                tL_decryptedMessageLayer = new TL_messages_sendEncrypted();
                                tL_decryptedMessageLayer.data = nativeByteBuffer3;
                                tL_decryptedMessageLayer.random_id = decryptedMessage2.random_id;
                                tL_decryptedMessageLayer.peer = new TL_inputEncryptedChat();
                                tL_decryptedMessageLayer.peer.chat_id = encryptedChat2.id;
                                tL_decryptedMessageLayer.peer.access_hash = encryptedChat2.access_hash;
                            }
                            ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(tL_decryptedMessageLayer, new C18191(), 64);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                return;
            }
        }
        SecretChatHelper secretChatHelper = this;
    }

    private void applyPeerLayer(final EncryptedChat encryptedChat, int i) {
        int peerLayerVersion = AndroidUtilities.getPeerLayerVersion(encryptedChat.layer);
        if (i > peerLayerVersion) {
            if (encryptedChat.key_hash.length == 16 && peerLayerVersion >= 46) {
                try {
                    Object computeSHA256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                    Object obj = new byte[36];
                    System.arraycopy(encryptedChat.key_hash, 0, obj, 0, 16);
                    System.arraycopy(computeSHA256, 0, obj, 16, 20);
                    encryptedChat.key_hash = obj;
                    MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(encryptedChat);
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
            encryptedChat.layer = AndroidUtilities.setPeerLayerVersion(encryptedChat.layer, i);
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatLayer(encryptedChat);
            if (peerLayerVersion < 73) {
                sendNotifyLayerMessage(encryptedChat, 0);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
                }
            });
        }
    }

    public Message processDecryptedObject(EncryptedChat encryptedChat, EncryptedFile encryptedFile, int i, TLObject tLObject, boolean z) {
        SecretChatHelper secretChatHelper = this;
        EncryptedChat encryptedChat2 = encryptedChat;
        EncryptedFile encryptedFile2 = encryptedFile;
        int i2 = i;
        TLObject tLObject2 = tLObject;
        if (tLObject2 != null) {
            int i3 = encryptedChat2.admin_id;
            if (i3 == UserConfig.getInstance(secretChatHelper.currentAccount).getClientUserId()) {
                i3 = encryptedChat2.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 20 && encryptedChat2.exchange_id == 0 && encryptedChat2.future_key_fingerprint == 0 && encryptedChat2.key_use_count_in >= (short) 120) {
                requestNewSecretChatKey(encryptedChat);
            }
            if (encryptedChat2.exchange_id == 0 && encryptedChat2.future_key_fingerprint != 0 && !z) {
                encryptedChat2.future_auth_key = new byte[256];
                encryptedChat2.future_key_fingerprint = 0;
                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
            } else if (encryptedChat2.exchange_id != 0 && z) {
                encryptedChat2.key_fingerprint = encryptedChat2.future_key_fingerprint;
                encryptedChat2.auth_key = encryptedChat2.future_auth_key;
                encryptedChat2.key_create_date = ConnectionsManager.getInstance(secretChatHelper.currentAccount).getCurrentTime();
                encryptedChat2.future_auth_key = new byte[256];
                encryptedChat2.future_key_fingerprint = 0;
                encryptedChat2.key_use_count_in = (short) 0;
                encryptedChat2.key_use_count_out = (short) 0;
                encryptedChat2.exchange_id = 0;
                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
            }
            if (tLObject2 instanceof TL_decryptedMessage) {
                Message tL_message_secret;
                MessageMedia messageMedia;
                TL_decryptedMessage tL_decryptedMessage = (TL_decryptedMessage) tLObject2;
                if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                    tL_message_secret = new TL_message_secret();
                    tL_message_secret.ttl = tL_decryptedMessage.ttl;
                    tL_message_secret.entities = tL_decryptedMessage.entities;
                } else {
                    tL_message_secret = new TL_message();
                    tL_message_secret.ttl = encryptedChat2.ttl;
                }
                tL_message_secret.message = tL_decryptedMessage.message;
                tL_message_secret.date = i2;
                int newMessageId = UserConfig.getInstance(secretChatHelper.currentAccount).getNewMessageId();
                tL_message_secret.id = newMessageId;
                tL_message_secret.local_id = newMessageId;
                UserConfig.getInstance(secretChatHelper.currentAccount).saveConfig(false);
                tL_message_secret.from_id = i3;
                tL_message_secret.to_id = new TL_peerUser();
                tL_message_secret.random_id = tL_decryptedMessage.random_id;
                tL_message_secret.to_id.user_id = UserConfig.getInstance(secretChatHelper.currentAccount).getClientUserId();
                tL_message_secret.unread = true;
                tL_message_secret.flags = 768;
                if (tL_decryptedMessage.via_bot_name != null && tL_decryptedMessage.via_bot_name.length() > 0) {
                    tL_message_secret.via_bot_name = tL_decryptedMessage.via_bot_name;
                    tL_message_secret.flags |= 2048;
                }
                if (tL_decryptedMessage.grouped_id != 0) {
                    tL_message_secret.grouped_id = tL_decryptedMessage.grouped_id;
                    tL_message_secret.flags |= 131072;
                }
                tL_message_secret.dialog_id = ((long) encryptedChat2.id) << 32;
                if (tL_decryptedMessage.reply_to_random_id != 0) {
                    tL_message_secret.reply_to_random_id = tL_decryptedMessage.reply_to_random_id;
                    tL_message_secret.flags |= 8;
                }
                if (tL_decryptedMessage.media != null) {
                    if (!(tL_decryptedMessage.media instanceof TL_decryptedMessageMediaEmpty)) {
                        if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaWebPage) {
                            tL_message_secret.media = new TL_messageMediaWebPage();
                            tL_message_secret.media.webpage = new TL_webPageUrlPending();
                            tL_message_secret.media.webpage.url = tL_decryptedMessage.media.url;
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaContact) {
                            tL_message_secret.media = new TL_messageMediaContact();
                            tL_message_secret.media.last_name = tL_decryptedMessage.media.last_name;
                            tL_message_secret.media.first_name = tL_decryptedMessage.media.first_name;
                            tL_message_secret.media.phone_number = tL_decryptedMessage.media.phone_number;
                            tL_message_secret.media.user_id = tL_decryptedMessage.media.user_id;
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaGeoPoint) {
                            tL_message_secret.media = new TL_messageMediaGeo();
                            tL_message_secret.media.geo = new TL_geoPoint();
                            tL_message_secret.media.geo.lat = tL_decryptedMessage.media.lat;
                            tL_message_secret.media.geo._long = tL_decryptedMessage.media._long;
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaPhoto) {
                            if (!(tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null)) {
                                if (tL_decryptedMessage.media.iv.length == 32) {
                                    tL_message_secret.media = new TL_messageMediaPhoto();
                                    messageMedia = tL_message_secret.media;
                                    messageMedia.flags |= 3;
                                    tL_message_secret.message = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_message_secret.media.photo = new TL_photo();
                                    tL_message_secret.media.photo.date = tL_message_secret.date;
                                    r1 = ((TL_decryptedMessageMediaPhoto) tL_decryptedMessage.media).thumb;
                                    if (r1 != null && r1.length != 0 && r1.length <= 6000 && tL_decryptedMessage.media.thumb_w <= 100 && tL_decryptedMessage.media.thumb_h <= 100) {
                                        TL_photoCachedSize tL_photoCachedSize = new TL_photoCachedSize();
                                        tL_photoCachedSize.w = tL_decryptedMessage.media.thumb_w;
                                        tL_photoCachedSize.h = tL_decryptedMessage.media.thumb_h;
                                        tL_photoCachedSize.bytes = r1;
                                        tL_photoCachedSize.type = "s";
                                        tL_photoCachedSize.location = new TL_fileLocationUnavailable();
                                        tL_message_secret.media.photo.sizes.add(tL_photoCachedSize);
                                    }
                                    if (tL_message_secret.ttl != 0) {
                                        tL_message_secret.media.ttl_seconds = tL_message_secret.ttl;
                                        messageMedia = tL_message_secret.media;
                                        messageMedia.flags |= 4;
                                    }
                                    TL_photoSize tL_photoSize = new TL_photoSize();
                                    tL_photoSize.w = tL_decryptedMessage.media.f34w;
                                    tL_photoSize.h = tL_decryptedMessage.media.f33h;
                                    tL_photoSize.type = "x";
                                    tL_photoSize.size = encryptedFile2.size;
                                    tL_photoSize.location = new TL_fileEncryptedLocation();
                                    tL_photoSize.location.key = tL_decryptedMessage.media.key;
                                    tL_photoSize.location.iv = tL_decryptedMessage.media.iv;
                                    tL_photoSize.location.dc_id = encryptedFile2.dc_id;
                                    tL_photoSize.location.volume_id = encryptedFile2.id;
                                    tL_photoSize.location.secret = encryptedFile2.access_hash;
                                    tL_photoSize.location.local_id = encryptedFile2.key_fingerprint;
                                    tL_message_secret.media.photo.sizes.add(tL_photoSize);
                                }
                            }
                            return null;
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaVideo) {
                            if (!(tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null)) {
                                if (tL_decryptedMessage.media.iv.length == 32) {
                                    tL_message_secret.media = new TL_messageMediaDocument();
                                    messageMedia = tL_message_secret.media;
                                    messageMedia.flags |= 3;
                                    tL_message_secret.media.document = new TL_documentEncrypted();
                                    tL_message_secret.media.document.key = tL_decryptedMessage.media.key;
                                    tL_message_secret.media.document.iv = tL_decryptedMessage.media.iv;
                                    tL_message_secret.media.document.dc_id = encryptedFile2.dc_id;
                                    tL_message_secret.message = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_message_secret.media.document.date = i2;
                                    tL_message_secret.media.document.size = encryptedFile2.size;
                                    tL_message_secret.media.document.id = encryptedFile2.id;
                                    tL_message_secret.media.document.access_hash = encryptedFile2.access_hash;
                                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                                    if (tL_message_secret.media.document.mime_type == null) {
                                        tL_message_secret.media.document.mime_type = MimeTypes.VIDEO_MP4;
                                    }
                                    r1 = ((TL_decryptedMessageMediaVideo) tL_decryptedMessage.media).thumb;
                                    if (r1 == null || r1.length == 0 || r1.length > 6000 || tL_decryptedMessage.media.thumb_w > 100 || tL_decryptedMessage.media.thumb_h > 100) {
                                        tL_message_secret.media.document.thumb = new TL_photoSizeEmpty();
                                        tL_message_secret.media.document.thumb.type = "s";
                                    } else {
                                        tL_message_secret.media.document.thumb = new TL_photoCachedSize();
                                        tL_message_secret.media.document.thumb.bytes = r1;
                                        tL_message_secret.media.document.thumb.f43w = tL_decryptedMessage.media.thumb_w;
                                        tL_message_secret.media.document.thumb.f42h = tL_decryptedMessage.media.thumb_h;
                                        tL_message_secret.media.document.thumb.type = "s";
                                        tL_message_secret.media.document.thumb.location = new TL_fileLocationUnavailable();
                                    }
                                    TL_documentAttributeVideo tL_documentAttributeVideo = new TL_documentAttributeVideo();
                                    tL_documentAttributeVideo.w = tL_decryptedMessage.media.f34w;
                                    tL_documentAttributeVideo.h = tL_decryptedMessage.media.f33h;
                                    tL_documentAttributeVideo.duration = tL_decryptedMessage.media.duration;
                                    tL_documentAttributeVideo.supports_streaming = false;
                                    tL_message_secret.media.document.attributes.add(tL_documentAttributeVideo);
                                    if (tL_message_secret.ttl != 0) {
                                        tL_message_secret.media.ttl_seconds = tL_message_secret.ttl;
                                        messageMedia = tL_message_secret.media;
                                        messageMedia.flags |= 4;
                                    }
                                    if (tL_message_secret.ttl != 0) {
                                        tL_message_secret.ttl = Math.max(tL_decryptedMessage.media.duration + 1, tL_message_secret.ttl);
                                    }
                                }
                            }
                            return null;
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaDocument) {
                            if (!(tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null)) {
                                if (tL_decryptedMessage.media.iv.length == 32) {
                                    tL_message_secret.media = new TL_messageMediaDocument();
                                    messageMedia = tL_message_secret.media;
                                    messageMedia.flags |= 3;
                                    tL_message_secret.message = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_message_secret.media.document = new TL_documentEncrypted();
                                    tL_message_secret.media.document.id = encryptedFile2.id;
                                    tL_message_secret.media.document.access_hash = encryptedFile2.access_hash;
                                    tL_message_secret.media.document.date = i2;
                                    if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaDocument_layer8) {
                                        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                                        tL_documentAttributeFilename.file_name = tL_decryptedMessage.media.file_name;
                                        tL_message_secret.media.document.attributes.add(tL_documentAttributeFilename);
                                    } else {
                                        tL_message_secret.media.document.attributes = tL_decryptedMessage.media.attributes;
                                    }
                                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                                    tL_message_secret.media.document.size = tL_decryptedMessage.media.size != 0 ? Math.min(tL_decryptedMessage.media.size, encryptedFile2.size) : encryptedFile2.size;
                                    tL_message_secret.media.document.key = tL_decryptedMessage.media.key;
                                    tL_message_secret.media.document.iv = tL_decryptedMessage.media.iv;
                                    if (tL_message_secret.media.document.mime_type == null) {
                                        tL_message_secret.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                    r1 = ((TL_decryptedMessageMediaDocument) tL_decryptedMessage.media).thumb;
                                    if (r1 == null || r1.length == 0 || r1.length > 6000 || tL_decryptedMessage.media.thumb_w > 100 || tL_decryptedMessage.media.thumb_h > 100) {
                                        tL_message_secret.media.document.thumb = new TL_photoSizeEmpty();
                                        tL_message_secret.media.document.thumb.type = "s";
                                    } else {
                                        tL_message_secret.media.document.thumb = new TL_photoCachedSize();
                                        tL_message_secret.media.document.thumb.bytes = r1;
                                        tL_message_secret.media.document.thumb.f43w = tL_decryptedMessage.media.thumb_w;
                                        tL_message_secret.media.document.thumb.f42h = tL_decryptedMessage.media.thumb_h;
                                        tL_message_secret.media.document.thumb.type = "s";
                                        tL_message_secret.media.document.thumb.location = new TL_fileLocationUnavailable();
                                    }
                                    tL_message_secret.media.document.dc_id = encryptedFile2.dc_id;
                                    if (MessageObject.isVoiceMessage(tL_message_secret) || MessageObject.isRoundVideoMessage(tL_message_secret)) {
                                        tL_message_secret.media_unread = true;
                                    }
                                }
                            }
                            return null;
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaExternalDocument) {
                            tL_message_secret.media = new TL_messageMediaDocument();
                            messageMedia = tL_message_secret.media;
                            messageMedia.flags |= 3;
                            tL_message_secret.message = TtmlNode.ANONYMOUS_REGION_ID;
                            tL_message_secret.media.document = new TL_document();
                            tL_message_secret.media.document.id = tL_decryptedMessage.media.id;
                            tL_message_secret.media.document.access_hash = tL_decryptedMessage.media.access_hash;
                            tL_message_secret.media.document.date = tL_decryptedMessage.media.date;
                            tL_message_secret.media.document.attributes = tL_decryptedMessage.media.attributes;
                            tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                            tL_message_secret.media.document.dc_id = tL_decryptedMessage.media.dc_id;
                            tL_message_secret.media.document.size = tL_decryptedMessage.media.size;
                            tL_message_secret.media.document.thumb = ((TL_decryptedMessageMediaExternalDocument) tL_decryptedMessage.media).thumb;
                            if (tL_message_secret.media.document.mime_type == null) {
                                tL_message_secret.media.document.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                        } else if (tL_decryptedMessage.media instanceof TL_decryptedMessageMediaAudio) {
                            if (!(tL_decryptedMessage.media.key == null || tL_decryptedMessage.media.key.length != 32 || tL_decryptedMessage.media.iv == null)) {
                                if (tL_decryptedMessage.media.iv.length == 32) {
                                    tL_message_secret.media = new TL_messageMediaDocument();
                                    messageMedia = tL_message_secret.media;
                                    messageMedia.flags |= 3;
                                    tL_message_secret.media.document = new TL_documentEncrypted();
                                    tL_message_secret.media.document.key = tL_decryptedMessage.media.key;
                                    tL_message_secret.media.document.iv = tL_decryptedMessage.media.iv;
                                    tL_message_secret.media.document.id = encryptedFile2.id;
                                    tL_message_secret.media.document.access_hash = encryptedFile2.access_hash;
                                    tL_message_secret.media.document.date = i2;
                                    tL_message_secret.media.document.size = encryptedFile2.size;
                                    tL_message_secret.media.document.dc_id = encryptedFile2.dc_id;
                                    tL_message_secret.media.document.mime_type = tL_decryptedMessage.media.mime_type;
                                    tL_message_secret.media.document.thumb = new TL_photoSizeEmpty();
                                    tL_message_secret.media.document.thumb.type = "s";
                                    tL_message_secret.message = tL_decryptedMessage.media.caption != null ? tL_decryptedMessage.media.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                    if (tL_message_secret.media.document.mime_type == null) {
                                        tL_message_secret.media.document.mime_type = "audio/ogg";
                                    }
                                    TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                                    tL_documentAttributeAudio.duration = tL_decryptedMessage.media.duration;
                                    tL_documentAttributeAudio.voice = true;
                                    tL_message_secret.media.document.attributes.add(tL_documentAttributeAudio);
                                    if (tL_message_secret.ttl != 0) {
                                        tL_message_secret.ttl = Math.max(tL_decryptedMessage.media.duration + 1, tL_message_secret.ttl);
                                    }
                                }
                            }
                            return null;
                        } else if (!(tL_decryptedMessage.media instanceof TL_decryptedMessageMediaVenue)) {
                            return null;
                        } else {
                            tL_message_secret.media = new TL_messageMediaVenue();
                            tL_message_secret.media.geo = new TL_geoPoint();
                            tL_message_secret.media.geo.lat = tL_decryptedMessage.media.lat;
                            tL_message_secret.media.geo._long = tL_decryptedMessage.media._long;
                            tL_message_secret.media.title = tL_decryptedMessage.media.title;
                            tL_message_secret.media.address = tL_decryptedMessage.media.address;
                            tL_message_secret.media.provider = tL_decryptedMessage.media.provider;
                            tL_message_secret.media.venue_id = tL_decryptedMessage.media.venue_id;
                            tL_message_secret.media.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        if (tL_message_secret.ttl != 0 && tL_message_secret.media.ttl_seconds == 0) {
                            tL_message_secret.media.ttl_seconds = tL_message_secret.ttl;
                            messageMedia = tL_message_secret.media;
                            messageMedia.flags |= 4;
                        }
                        return tL_message_secret;
                    }
                }
                tL_message_secret.media = new TL_messageMediaEmpty();
                tL_message_secret.media.ttl_seconds = tL_message_secret.ttl;
                messageMedia = tL_message_secret.media;
                messageMedia.flags |= 4;
                return tL_message_secret;
            } else if (tLObject2 instanceof TL_decryptedMessageService) {
                TL_decryptedMessageService tL_decryptedMessageService = (TL_decryptedMessageService) tLObject2;
                if (!(tL_decryptedMessageService.action instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    if (!(tL_decryptedMessageService.action instanceof TL_decryptedMessageActionScreenshotMessages)) {
                        if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionFlushHistory) {
                            final long j = ((long) encryptedChat2.id) << 32;
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.SecretChatHelper$6$1 */
                                class C04621 implements Runnable {

                                    /* renamed from: org.telegram.messenger.SecretChatHelper$6$1$1 */
                                    class C04611 implements Runnable {
                                        C04611() {
                                        }

                                        public void run() {
                                            NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processReadMessages(null, j, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
                                            LongSparseArray longSparseArray = new LongSparseArray(1);
                                            longSparseArray.put(j, Integer.valueOf(0));
                                            NotificationsController.getInstance(SecretChatHelper.this.currentAccount).processDialogsUpdateRead(longSparseArray);
                                        }
                                    }

                                    C04621() {
                                    }

                                    public void run() {
                                        AndroidUtilities.runOnUIThread(new C04611());
                                    }
                                }

                                public void run() {
                                    TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogs_dict.get(j);
                                    if (tL_dialog != null) {
                                        tL_dialog.unread_count = 0;
                                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).dialogMessage.remove(tL_dialog.id);
                                    }
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getStorageQueue().postRunnable(new C04621());
                                    MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).deleteDialog(j, 1);
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(false));
                                }
                            });
                            return null;
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionDeleteMessages) {
                            if (!tL_decryptedMessageService.action.random_ids.isEmpty()) {
                                secretChatHelper.pendingEncMessagesToDelete.addAll(tL_decryptedMessageService.action.random_ids);
                            }
                            return null;
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionReadMessages) {
                            if (!tL_decryptedMessageService.action.random_ids.isEmpty()) {
                                int currentTime = ConnectionsManager.getInstance(secretChatHelper.currentAccount).getCurrentTime();
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).createTaskForSecretChat(encryptedChat2.id, currentTime, currentTime, 1, tL_decryptedMessageService.action.random_ids);
                            }
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionNotifyLayer) {
                            applyPeerLayer(encryptedChat2, tL_decryptedMessageService.action.layer);
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionRequestKey) {
                            if (encryptedChat2.exchange_id != 0) {
                                if (encryptedChat2.exchange_id > tL_decryptedMessageService.action.exchange_id) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("we already have request key with higher exchange_id");
                                    }
                                    return null;
                                }
                                sendAbortKeyMessage(encryptedChat2, null, encryptedChat2.exchange_id);
                            }
                            byte[] bArr = new byte[256];
                            Utilities.random.nextBytes(bArr);
                            r4 = new BigInteger(1, MessagesStorage.getInstance(secretChatHelper.currentAccount).getSecretPBytes());
                            BigInteger modPow = BigInteger.valueOf((long) MessagesStorage.getInstance(secretChatHelper.currentAccount).getSecretG()).modPow(new BigInteger(1, bArr), r4);
                            BigInteger bigInteger = new BigInteger(1, tL_decryptedMessageService.action.g_a);
                            if (Utilities.isGoodGaAndGb(bigInteger, r4)) {
                                byte[] bArr2;
                                r5 = modPow.toByteArray();
                                if (r5.length > 256) {
                                    bArr2 = new byte[256];
                                    System.arraycopy(r5, 1, bArr2, 0, 256);
                                } else {
                                    bArr2 = r5;
                                }
                                r3 = bigInteger.modPow(new BigInteger(1, bArr), r4).toByteArray();
                                if (r3.length > 256) {
                                    r4 = new byte[256];
                                    r15 = 0;
                                    System.arraycopy(r3, r3.length - 256, r4, 0, 256);
                                } else {
                                    r15 = 0;
                                    if (r3.length < 256) {
                                        r4 = new byte[256];
                                        System.arraycopy(r3, 0, r4, 256 - r3.length, r3.length);
                                        for (r5 = 0; r5 < 256 - r3.length; r5++) {
                                            r3[r5] = null;
                                        }
                                    } else {
                                        r4 = r3;
                                    }
                                }
                                r3 = Utilities.computeSHA1(r4);
                                r5 = new byte[8];
                                System.arraycopy(r3, r3.length - 8, r5, r15, 8);
                                encryptedChat2.exchange_id = tL_decryptedMessageService.action.exchange_id;
                                encryptedChat2.future_auth_key = r4;
                                encryptedChat2.future_key_fingerprint = Utilities.bytesToLong(r5);
                                encryptedChat2.g_a_or_b = bArr2;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                                sendAcceptKeyMessage(encryptedChat2, null);
                            } else {
                                sendAbortKeyMessage(encryptedChat2, null, tL_decryptedMessageService.action.exchange_id);
                                return null;
                            }
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionAcceptKey) {
                            if (encryptedChat2.exchange_id == tL_decryptedMessageService.action.exchange_id) {
                                BigInteger bigInteger2 = new BigInteger(1, MessagesStorage.getInstance(secretChatHelper.currentAccount).getSecretPBytes());
                                r4 = new BigInteger(1, tL_decryptedMessageService.action.g_b);
                                if (Utilities.isGoodGaAndGb(r4, bigInteger2)) {
                                    r3 = r4.modPow(new BigInteger(1, encryptedChat2.a_or_b), bigInteger2).toByteArray();
                                    if (r3.length > 256) {
                                        r4 = new byte[256];
                                        r15 = 0;
                                        System.arraycopy(r3, r3.length - 256, r4, 0, 256);
                                    } else {
                                        r15 = 0;
                                        if (r3.length < 256) {
                                            r4 = new byte[256];
                                            System.arraycopy(r3, 0, r4, 256 - r3.length, r3.length);
                                            for (r5 = 0; r5 < 256 - r3.length; r5++) {
                                                r3[r5] = null;
                                            }
                                        } else {
                                            r4 = r3;
                                        }
                                    }
                                    r3 = Utilities.computeSHA1(r4);
                                    r5 = new byte[8];
                                    System.arraycopy(r3, r3.length - 8, r5, r15, 8);
                                    long bytesToLong = Utilities.bytesToLong(r5);
                                    if (tL_decryptedMessageService.action.key_fingerprint == bytesToLong) {
                                        encryptedChat2.future_auth_key = r4;
                                        encryptedChat2.future_key_fingerprint = bytesToLong;
                                        MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                                        sendCommitKeyMessage(encryptedChat2, null);
                                    } else {
                                        encryptedChat2.future_auth_key = new byte[256];
                                        encryptedChat2.future_key_fingerprint = 0;
                                        encryptedChat2.exchange_id = 0;
                                        MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                                        sendAbortKeyMessage(encryptedChat2, null, tL_decryptedMessageService.action.exchange_id);
                                    }
                                } else {
                                    encryptedChat2.future_auth_key = new byte[256];
                                    encryptedChat2.future_key_fingerprint = 0;
                                    encryptedChat2.exchange_id = 0;
                                    MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                                    sendAbortKeyMessage(encryptedChat2, null, tL_decryptedMessageService.action.exchange_id);
                                    return null;
                                }
                            }
                            encryptedChat2.future_auth_key = new byte[256];
                            encryptedChat2.future_key_fingerprint = 0;
                            encryptedChat2.exchange_id = 0;
                            MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                            sendAbortKeyMessage(encryptedChat2, null, tL_decryptedMessageService.action.exchange_id);
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionCommitKey) {
                            if (encryptedChat2.exchange_id == tL_decryptedMessageService.action.exchange_id && encryptedChat2.future_key_fingerprint == tL_decryptedMessageService.action.key_fingerprint) {
                                long j2 = encryptedChat2.key_fingerprint;
                                r4 = encryptedChat2.auth_key;
                                encryptedChat2.key_fingerprint = encryptedChat2.future_key_fingerprint;
                                encryptedChat2.auth_key = encryptedChat2.future_auth_key;
                                encryptedChat2.key_create_date = ConnectionsManager.getInstance(secretChatHelper.currentAccount).getCurrentTime();
                                encryptedChat2.future_auth_key = r4;
                                encryptedChat2.future_key_fingerprint = j2;
                                encryptedChat2.key_use_count_in = (short) 0;
                                encryptedChat2.key_use_count_out = (short) 0;
                                encryptedChat2.exchange_id = 0;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                                sendNoopMessage(encryptedChat2, null);
                            } else {
                                encryptedChat2.future_auth_key = new byte[256];
                                encryptedChat2.future_key_fingerprint = 0;
                                encryptedChat2.exchange_id = 0;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                                sendAbortKeyMessage(encryptedChat2, null, tL_decryptedMessageService.action.exchange_id);
                            }
                        } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionAbortKey) {
                            if (encryptedChat2.exchange_id == tL_decryptedMessageService.action.exchange_id) {
                                encryptedChat2.future_auth_key = new byte[256];
                                encryptedChat2.future_key_fingerprint = 0;
                                encryptedChat2.exchange_id = 0;
                                MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChat(encryptedChat2);
                            }
                        } else if (!(tL_decryptedMessageService.action instanceof TL_decryptedMessageActionNoop)) {
                            if ((tL_decryptedMessageService.action instanceof TL_decryptedMessageActionResend) && tL_decryptedMessageService.action.end_seq_no >= encryptedChat2.in_seq_no) {
                                if (tL_decryptedMessageService.action.end_seq_no >= tL_decryptedMessageService.action.start_seq_no) {
                                    if (tL_decryptedMessageService.action.start_seq_no < encryptedChat2.in_seq_no) {
                                        tL_decryptedMessageService.action.start_seq_no = encryptedChat2.in_seq_no;
                                    }
                                    resendMessages(tL_decryptedMessageService.action.start_seq_no, tL_decryptedMessageService.action.end_seq_no, encryptedChat2);
                                }
                            }
                            return null;
                        }
                    }
                }
                Message tL_messageService = new TL_messageService();
                if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionSetMessageTTL) {
                    tL_messageService.action = new TL_messageEncryptedAction();
                    if (tL_decryptedMessageService.action.ttl_seconds < 0 || tL_decryptedMessageService.action.ttl_seconds > 31536000) {
                        tL_decryptedMessageService.action.ttl_seconds = 31536000;
                    }
                    encryptedChat2.ttl = tL_decryptedMessageService.action.ttl_seconds;
                    tL_messageService.action.encryptedAction = tL_decryptedMessageService.action;
                    MessagesStorage.getInstance(secretChatHelper.currentAccount).updateEncryptedChatTTL(encryptedChat2);
                } else if (tL_decryptedMessageService.action instanceof TL_decryptedMessageActionScreenshotMessages) {
                    tL_messageService.action = new TL_messageEncryptedAction();
                    tL_messageService.action.encryptedAction = tL_decryptedMessageService.action;
                }
                int newMessageId2 = UserConfig.getInstance(secretChatHelper.currentAccount).getNewMessageId();
                tL_messageService.id = newMessageId2;
                tL_messageService.local_id = newMessageId2;
                UserConfig.getInstance(secretChatHelper.currentAccount).saveConfig(false);
                tL_messageService.unread = true;
                tL_messageService.flags = 256;
                tL_messageService.date = i2;
                tL_messageService.from_id = i3;
                tL_messageService.to_id = new TL_peerUser();
                tL_messageService.to_id.user_id = UserConfig.getInstance(secretChatHelper.currentAccount).getClientUserId();
                tL_messageService.dialog_id = ((long) encryptedChat2.id) << 32;
                return tL_messageService;
            } else if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unknown message ");
                stringBuilder.append(tLObject2);
                FileLog.m1e(stringBuilder.toString());
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.m1e("unknown TLObject");
        }
        return null;
    }

    private Message createDeleteMessage(int i, int i2, int i3, long j, EncryptedChat encryptedChat) {
        Message tL_messageService = new TL_messageService();
        tL_messageService.action = new TL_messageEncryptedAction();
        tL_messageService.action.encryptedAction = new TL_decryptedMessageActionDeleteMessages();
        tL_messageService.action.encryptedAction.random_ids.add(Long.valueOf(j));
        tL_messageService.id = i;
        tL_messageService.local_id = i;
        tL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        tL_messageService.unread = true;
        tL_messageService.out = true;
        tL_messageService.flags = 256;
        tL_messageService.dialog_id = ((long) encryptedChat.id) << 32;
        tL_messageService.to_id = new TL_peerUser();
        tL_messageService.send_state = 1;
        tL_messageService.seq_in = i3;
        tL_messageService.seq_out = i2;
        if (encryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            tL_messageService.to_id.user_id = encryptedChat.admin_id;
        } else {
            tL_messageService.to_id.user_id = encryptedChat.participant_id;
        }
        tL_messageService.date = 0;
        tL_messageService.random_id = j;
        return tL_messageService;
    }

    private void resendMessages(final int i, final int i2, final EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            if (i2 - i >= 0) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.SecretChatHelper$7$1 */
                    class C04641 implements Comparator<Message> {
                        C04641() {
                        }

                        public int compare(Message message, Message message2) {
                            return AndroidUtilities.compare(message.seq_out, message2.seq_out);
                        }
                    }

                    public void run() {
                        try {
                            int i = i;
                            if (encryptedChat.admin_id == UserConfig.getInstance(SecretChatHelper.this.currentAccount).getClientUserId() && i % 2 == 0) {
                                i++;
                            }
                            SQLiteDatabase database = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase();
                            r7 = new Object[5];
                            int i2 = 1;
                            r7[1] = Integer.valueOf(i);
                            int i3 = 2;
                            r7[2] = Integer.valueOf(i);
                            int i4 = 3;
                            r7[3] = Integer.valueOf(i2);
                            r7[4] = Integer.valueOf(i2);
                            SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", r7), new Object[0]);
                            boolean next = queryFinalized.next();
                            queryFinalized.dispose();
                            if (!next) {
                                int i5;
                                long j = ((long) encryptedChat.id) << 32;
                                SparseArray sparseArray = new SparseArray();
                                final ArrayList arrayList = new ArrayList();
                                for (i5 = i; i5 < i2; i5 += 2) {
                                    sparseArray.put(i5, null);
                                }
                                SQLiteCursor queryFinalized2 = MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
                                while (queryFinalized2.next()) {
                                    Object TLdeserialize;
                                    long longValue = queryFinalized2.longValue(i2);
                                    if (longValue == 0) {
                                        longValue = Utilities.random.nextLong();
                                    }
                                    i5 = queryFinalized2.intValue(i3);
                                    int intValue = queryFinalized2.intValue(i4);
                                    int intValue2 = queryFinalized2.intValue(5);
                                    AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(SecretChatHelper.this.currentAccount).clientUserId);
                                        byteBufferValue.reuse();
                                        TLdeserialize.random_id = longValue;
                                        TLdeserialize.dialog_id = j;
                                        TLdeserialize.seq_in = i5;
                                        TLdeserialize.seq_out = intValue;
                                        TLdeserialize.ttl = queryFinalized2.intValue(4);
                                    } else {
                                        TLdeserialize = SecretChatHelper.this.createDeleteMessage(intValue2, intValue, i5, longValue, encryptedChat);
                                    }
                                    arrayList.add(TLdeserialize);
                                    sparseArray.remove(intValue);
                                    i2 = 1;
                                    i3 = 2;
                                    i4 = 3;
                                }
                                queryFinalized2.dispose();
                                if (sparseArray.size() != 0) {
                                    for (int i6 = 0; i6 < sparseArray.size(); i6++) {
                                        arrayList.add(SecretChatHelper.this.createDeleteMessage(UserConfig.getInstance(SecretChatHelper.this.currentAccount).getNewMessageId(), sparseArray.keyAt(i6), 0, Utilities.random.nextLong(), encryptedChat));
                                    }
                                    UserConfig.getInstance(SecretChatHelper.this.currentAccount).saveConfig(false);
                                }
                                Collections.sort(arrayList, new C04641());
                                ArrayList arrayList2 = new ArrayList();
                                arrayList2.add(encryptedChat);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        for (int i = 0; i < arrayList.size(); i++) {
                                            MessageObject messageObject = new MessageObject(SecretChatHelper.this.currentAccount, (Message) arrayList.get(i), false);
                                            messageObject.resendAsIs = true;
                                            SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).retrySendMessage(messageObject, true);
                                        }
                                    }
                                });
                                SendMessagesHelper.getInstance(SecretChatHelper.this.currentAccount).processUnsentMessages(arrayList, new ArrayList(), new ArrayList(), arrayList2);
                                MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(encryptedChat.id), Integer.valueOf(i), Integer.valueOf(i2)})).stepThis().dispose();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void checkSecretHoles(EncryptedChat encryptedChat, ArrayList<Message> arrayList) {
        ArrayList arrayList2 = (ArrayList) this.secretHolesQueue.get(encryptedChat.id);
        if (arrayList2 != null) {
            Collections.sort(arrayList2, new C04678());
            int i = 0;
            while (arrayList2.size() > 0) {
                TL_decryptedMessageHolder tL_decryptedMessageHolder = (TL_decryptedMessageHolder) arrayList2.get(0);
                if (tL_decryptedMessageHolder.layer.out_seq_no != encryptedChat.seq_in && encryptedChat.seq_in != tL_decryptedMessageHolder.layer.out_seq_no - 2) {
                    break;
                }
                applyPeerLayer(encryptedChat, tL_decryptedMessageHolder.layer.layer);
                encryptedChat.seq_in = tL_decryptedMessageHolder.layer.out_seq_no;
                encryptedChat.in_seq_no = tL_decryptedMessageHolder.layer.in_seq_no;
                arrayList2.remove(0);
                if (tL_decryptedMessageHolder.decryptedWithVersion == 2) {
                    encryptedChat.mtproto_seq = Math.min(encryptedChat.mtproto_seq, encryptedChat.seq_in);
                }
                Message processDecryptedObject = processDecryptedObject(encryptedChat, tL_decryptedMessageHolder.file, tL_decryptedMessageHolder.date, tL_decryptedMessageHolder.layer.message, tL_decryptedMessageHolder.new_key_used);
                if (processDecryptedObject != null) {
                    arrayList.add(processDecryptedObject);
                }
                boolean z = true;
            }
            if (arrayList2.isEmpty() != null) {
                this.secretHolesQueue.remove(encryptedChat.id);
            }
            if (i != 0) {
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChatSeq(encryptedChat, true);
            }
        }
    }

    private boolean decryptWithMtProtoVersion(NativeByteBuffer nativeByteBuffer, byte[] bArr, byte[] bArr2, int i, boolean z, boolean z2) {
        byte[] bArr3;
        boolean z3;
        NativeByteBuffer nativeByteBuffer2 = nativeByteBuffer;
        byte[] bArr4 = bArr2;
        int i2 = i;
        if (i2 == 1) {
            bArr3 = bArr;
            z3 = false;
        } else {
            bArr3 = bArr;
            z3 = z;
        }
        MessageKeyData generateMessageKeyData = MessageKeyData.generateMessageKeyData(bArr3, bArr4, z3, i2);
        Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, false, false, 24, nativeByteBuffer.limit() - 24);
        int readInt32 = nativeByteBuffer2.readInt32(false);
        int i3;
        if (i2 == 2) {
            i3 = 24;
            if (!Utilities.arraysEquals(bArr4, 0, Utilities.computeSHA256(bArr3, (z3 ? 8 : 0) + 88, 32, nativeByteBuffer2.buffer, 24, nativeByteBuffer2.buffer.limit()), 8)) {
                if (z2) {
                    Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, true, false, 24, nativeByteBuffer.limit() - 24);
                    nativeByteBuffer2.position(i3);
                }
                return false;
            }
        }
        i3 = 24;
        int i4 = readInt32 + 28;
        if (i4 < nativeByteBuffer2.buffer.limit() - 15 || i4 > nativeByteBuffer2.buffer.limit()) {
            i4 = nativeByteBuffer2.buffer.limit();
        }
        byte[] computeSHA1 = Utilities.computeSHA1(nativeByteBuffer2.buffer, i3, i4);
        if (!Utilities.arraysEquals(bArr4, 0, computeSHA1, computeSHA1.length - 16)) {
            if (z2) {
                Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, true, false, 24, nativeByteBuffer.limit() - 24);
                nativeByteBuffer2.position(i3);
            }
            return false;
        }
        if (readInt32 > 0) {
            if (readInt32 <= nativeByteBuffer.limit() - 28) {
                int limit = (nativeByteBuffer.limit() - 28) - readInt32;
                if ((i2 != 2 || (limit >= 12 && limit <= 1024)) && (i2 != 1 || limit <= 15)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    protected ArrayList<Message> decryptMessage(EncryptedMessage encryptedMessage) {
        EncryptedMessage encryptedMessage2 = encryptedMessage;
        EncryptedChat encryptedChatDB = MessagesController.getInstance(this.currentAccount).getEncryptedChatDB(encryptedMessage2.chat_id, true);
        if (encryptedChatDB != null) {
            if (!(encryptedChatDB instanceof TL_encryptedChatDiscarded)) {
                try {
                    byte[] bArr;
                    boolean z;
                    int i;
                    byte[] readData;
                    boolean z2;
                    boolean z3;
                    int i2;
                    int i3;
                    boolean z4;
                    int i4;
                    TLObject TLdeserialize;
                    boolean z5;
                    TL_decryptedMessageLayer tL_decryptedMessageLayer;
                    StringBuilder stringBuilder;
                    ArrayList arrayList;
                    final TL_encryptedChatDiscarded tL_encryptedChatDiscarded;
                    TL_decryptedMessageHolder tL_decryptedMessageHolder;
                    TLObject tLObject;
                    ArrayList<Message> arrayList2;
                    Message processDecryptedObject;
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedMessage2.bytes.length);
                    nativeByteBuffer.writeBytes(encryptedMessage2.bytes);
                    nativeByteBuffer.position(0);
                    long readInt64 = nativeByteBuffer.readInt64(false);
                    if (encryptedChatDB.key_fingerprint == readInt64) {
                        bArr = encryptedChatDB.auth_key;
                    } else if (encryptedChatDB.future_key_fingerprint == 0 || encryptedChatDB.future_key_fingerprint != readInt64) {
                        bArr = null;
                    } else {
                        bArr = encryptedChatDB.future_auth_key;
                        z = true;
                        i = AndroidUtilities.getPeerLayerVersion(encryptedChatDB.layer) < 73 ? 2 : 1;
                        if (bArr == null) {
                            readData = nativeByteBuffer.readData(16, false);
                            z2 = encryptedChatDB.admin_id != UserConfig.getInstance(r8.currentAccount).getClientUserId();
                            z3 = i == 2 || encryptedChatDB.mtproto_seq == 0;
                            i2 = i;
                            i3 = 2;
                            z4 = z;
                            if (!decryptWithMtProtoVersion(nativeByteBuffer, bArr, readData, i, z2, z3)) {
                                i4 = i2;
                            } else if (i2 == i3) {
                                if (z3) {
                                    if (!decryptWithMtProtoVersion(nativeByteBuffer, bArr, readData, 1, z2, false)) {
                                        i4 = 1;
                                    }
                                }
                                return null;
                            } else if (!decryptWithMtProtoVersion(nativeByteBuffer, bArr, readData, 2, z2, z3)) {
                                return null;
                            } else {
                                i4 = i3;
                            }
                            TLdeserialize = TLClassStore.Instance().TLdeserialize(nativeByteBuffer, nativeByteBuffer.readInt32(false), false);
                            nativeByteBuffer.reuse();
                            z5 = z4;
                            if (!z5 && AndroidUtilities.getPeerLayerVersion(encryptedChatDB.layer) >= 20) {
                                encryptedChatDB.key_use_count_in = (short) (encryptedChatDB.key_use_count_in + 1);
                            }
                            if (TLdeserialize instanceof TL_decryptedMessageLayer) {
                                if (TLdeserialize instanceof TL_decryptedMessageService) {
                                    if (!(((TL_decryptedMessageService) TLdeserialize).action instanceof TL_decryptedMessageActionNotifyLayer)) {
                                    }
                                }
                                return null;
                            }
                            tL_decryptedMessageLayer = (TL_decryptedMessageLayer) TLdeserialize;
                            if (encryptedChatDB.seq_in == 0 && encryptedChatDB.seq_out == 0) {
                                if (encryptedChatDB.admin_id != UserConfig.getInstance(r8.currentAccount).getClientUserId()) {
                                    encryptedChatDB.seq_out = 1;
                                    encryptedChatDB.seq_in = -2;
                                } else {
                                    encryptedChatDB.seq_in = -1;
                                }
                            }
                            if (tL_decryptedMessageLayer.random_bytes.length >= 15) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m1e("got random bytes less than needed");
                                }
                                return null;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("current chat in_seq = ");
                                stringBuilder.append(encryptedChatDB.seq_in);
                                stringBuilder.append(" out_seq = ");
                                stringBuilder.append(encryptedChatDB.seq_out);
                                FileLog.m0d(stringBuilder.toString());
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("got message with in_seq = ");
                                stringBuilder.append(tL_decryptedMessageLayer.in_seq_no);
                                stringBuilder.append(" out_seq = ");
                                stringBuilder.append(tL_decryptedMessageLayer.out_seq_no);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            if (tL_decryptedMessageLayer.out_seq_no <= encryptedChatDB.seq_in) {
                                return null;
                            }
                            if (i4 != 1 && encryptedChatDB.mtproto_seq != 0 && tL_decryptedMessageLayer.out_seq_no >= encryptedChatDB.mtproto_seq) {
                                return null;
                            }
                            if (encryptedChatDB.seq_in == tL_decryptedMessageLayer.out_seq_no - i3) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m1e("got hole");
                                }
                                arrayList = (ArrayList) r8.secretHolesQueue.get(encryptedChatDB.id);
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    r8.secretHolesQueue.put(encryptedChatDB.id, arrayList);
                                }
                                if (arrayList.size() < 4) {
                                    r8.secretHolesQueue.remove(encryptedChatDB.id);
                                    tL_encryptedChatDiscarded = new TL_encryptedChatDiscarded();
                                    tL_encryptedChatDiscarded.id = encryptedChatDB.id;
                                    tL_encryptedChatDiscarded.user_id = encryptedChatDB.user_id;
                                    tL_encryptedChatDiscarded.auth_key = encryptedChatDB.auth_key;
                                    tL_encryptedChatDiscarded.key_create_date = encryptedChatDB.key_create_date;
                                    tL_encryptedChatDiscarded.key_use_count_in = encryptedChatDB.key_use_count_in;
                                    tL_encryptedChatDiscarded.key_use_count_out = encryptedChatDB.key_use_count_out;
                                    tL_encryptedChatDiscarded.seq_in = encryptedChatDB.seq_in;
                                    tL_encryptedChatDiscarded.seq_out = encryptedChatDB.seq_out;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(tL_encryptedChatDiscarded, false);
                                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(tL_encryptedChatDiscarded);
                                            NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, tL_encryptedChatDiscarded);
                                        }
                                    });
                                    declineSecretChat(encryptedChatDB.id);
                                    return null;
                                }
                                tL_decryptedMessageHolder = new TL_decryptedMessageHolder();
                                tL_decryptedMessageHolder.layer = tL_decryptedMessageLayer;
                                tL_decryptedMessageHolder.file = encryptedMessage2.file;
                                tL_decryptedMessageHolder.date = encryptedMessage2.date;
                                tL_decryptedMessageHolder.new_key_used = z5;
                                tL_decryptedMessageHolder.decryptedWithVersion = i4;
                                arrayList.add(tL_decryptedMessageHolder);
                                return null;
                            }
                            if (i4 == i3) {
                                encryptedChatDB.mtproto_seq = Math.min(encryptedChatDB.mtproto_seq, encryptedChatDB.seq_in);
                            }
                            applyPeerLayer(encryptedChatDB, tL_decryptedMessageLayer.layer);
                            encryptedChatDB.seq_in = tL_decryptedMessageLayer.out_seq_no;
                            encryptedChatDB.in_seq_no = tL_decryptedMessageLayer.in_seq_no;
                            MessagesStorage.getInstance(r8.currentAccount).updateEncryptedChatSeq(encryptedChatDB, true);
                            TLdeserialize = tL_decryptedMessageLayer.message;
                            tLObject = TLdeserialize;
                            arrayList2 = new ArrayList();
                            processDecryptedObject = processDecryptedObject(encryptedChatDB, encryptedMessage2.file, encryptedMessage2.date, tLObject, z5);
                            if (processDecryptedObject != null) {
                                arrayList2.add(processDecryptedObject);
                            }
                            checkSecretHoles(encryptedChatDB, arrayList2);
                            return arrayList2;
                        }
                        nativeByteBuffer.reuse();
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m1e(String.format("fingerprint mismatch %x", new Object[]{Long.valueOf(readInt64)}));
                        }
                        return null;
                    }
                    z = false;
                    if (AndroidUtilities.getPeerLayerVersion(encryptedChatDB.layer) < 73) {
                    }
                    if (bArr == null) {
                        nativeByteBuffer.reuse();
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m1e(String.format("fingerprint mismatch %x", new Object[]{Long.valueOf(readInt64)}));
                        }
                        return null;
                    }
                    readData = nativeByteBuffer.readData(16, false);
                    if (encryptedChatDB.admin_id != UserConfig.getInstance(r8.currentAccount).getClientUserId()) {
                    }
                    if (i == 2) {
                    }
                    i2 = i;
                    i3 = 2;
                    z4 = z;
                    if (!decryptWithMtProtoVersion(nativeByteBuffer, bArr, readData, i, z2, z3)) {
                        i4 = i2;
                    } else if (i2 == i3) {
                        if (z3) {
                            if (!decryptWithMtProtoVersion(nativeByteBuffer, bArr, readData, 1, z2, false)) {
                                i4 = 1;
                            }
                        }
                        return null;
                    } else if (!decryptWithMtProtoVersion(nativeByteBuffer, bArr, readData, 2, z2, z3)) {
                        return null;
                    } else {
                        i4 = i3;
                    }
                    TLdeserialize = TLClassStore.Instance().TLdeserialize(nativeByteBuffer, nativeByteBuffer.readInt32(false), false);
                    nativeByteBuffer.reuse();
                    z5 = z4;
                    encryptedChatDB.key_use_count_in = (short) (encryptedChatDB.key_use_count_in + 1);
                    if (TLdeserialize instanceof TL_decryptedMessageLayer) {
                        if (TLdeserialize instanceof TL_decryptedMessageService) {
                            if (((TL_decryptedMessageService) TLdeserialize).action instanceof TL_decryptedMessageActionNotifyLayer) {
                            }
                        }
                        return null;
                    }
                    tL_decryptedMessageLayer = (TL_decryptedMessageLayer) TLdeserialize;
                    if (encryptedChatDB.admin_id != UserConfig.getInstance(r8.currentAccount).getClientUserId()) {
                        encryptedChatDB.seq_in = -1;
                    } else {
                        encryptedChatDB.seq_out = 1;
                        encryptedChatDB.seq_in = -2;
                    }
                    if (tL_decryptedMessageLayer.random_bytes.length >= 15) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("current chat in_seq = ");
                            stringBuilder.append(encryptedChatDB.seq_in);
                            stringBuilder.append(" out_seq = ");
                            stringBuilder.append(encryptedChatDB.seq_out);
                            FileLog.m0d(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("got message with in_seq = ");
                            stringBuilder.append(tL_decryptedMessageLayer.in_seq_no);
                            stringBuilder.append(" out_seq = ");
                            stringBuilder.append(tL_decryptedMessageLayer.out_seq_no);
                            FileLog.m0d(stringBuilder.toString());
                        }
                        if (tL_decryptedMessageLayer.out_seq_no <= encryptedChatDB.seq_in) {
                            return null;
                        }
                        if (i4 != 1) {
                        }
                        if (encryptedChatDB.seq_in == tL_decryptedMessageLayer.out_seq_no - i3) {
                            if (i4 == i3) {
                                encryptedChatDB.mtproto_seq = Math.min(encryptedChatDB.mtproto_seq, encryptedChatDB.seq_in);
                            }
                            applyPeerLayer(encryptedChatDB, tL_decryptedMessageLayer.layer);
                            encryptedChatDB.seq_in = tL_decryptedMessageLayer.out_seq_no;
                            encryptedChatDB.in_seq_no = tL_decryptedMessageLayer.in_seq_no;
                            MessagesStorage.getInstance(r8.currentAccount).updateEncryptedChatSeq(encryptedChatDB, true);
                            TLdeserialize = tL_decryptedMessageLayer.message;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m1e("got hole");
                            }
                            arrayList = (ArrayList) r8.secretHolesQueue.get(encryptedChatDB.id);
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                                r8.secretHolesQueue.put(encryptedChatDB.id, arrayList);
                            }
                            if (arrayList.size() < 4) {
                                tL_decryptedMessageHolder = new TL_decryptedMessageHolder();
                                tL_decryptedMessageHolder.layer = tL_decryptedMessageLayer;
                                tL_decryptedMessageHolder.file = encryptedMessage2.file;
                                tL_decryptedMessageHolder.date = encryptedMessage2.date;
                                tL_decryptedMessageHolder.new_key_used = z5;
                                tL_decryptedMessageHolder.decryptedWithVersion = i4;
                                arrayList.add(tL_decryptedMessageHolder);
                                return null;
                            }
                            r8.secretHolesQueue.remove(encryptedChatDB.id);
                            tL_encryptedChatDiscarded = new TL_encryptedChatDiscarded();
                            tL_encryptedChatDiscarded.id = encryptedChatDB.id;
                            tL_encryptedChatDiscarded.user_id = encryptedChatDB.user_id;
                            tL_encryptedChatDiscarded.auth_key = encryptedChatDB.auth_key;
                            tL_encryptedChatDiscarded.key_create_date = encryptedChatDB.key_create_date;
                            tL_encryptedChatDiscarded.key_use_count_in = encryptedChatDB.key_use_count_in;
                            tL_encryptedChatDiscarded.key_use_count_out = encryptedChatDB.key_use_count_out;
                            tL_encryptedChatDiscarded.seq_in = encryptedChatDB.seq_in;
                            tL_encryptedChatDiscarded.seq_out = encryptedChatDB.seq_out;
                            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                            declineSecretChat(encryptedChatDB.id);
                            return null;
                        }
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m1e("got random bytes less than needed");
                    }
                    return null;
                    tLObject = TLdeserialize;
                    arrayList2 = new ArrayList();
                    processDecryptedObject = processDecryptedObject(encryptedChatDB, encryptedMessage2.file, encryptedMessage2.date, tLObject, z5);
                    if (processDecryptedObject != null) {
                        arrayList2.add(processDecryptedObject);
                    }
                    checkSecretHoles(encryptedChatDB, arrayList2);
                    return arrayList2;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
        return null;
    }

    public void requestNewSecretChatKey(EncryptedChat encryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
            byte[] bArr = new byte[256];
            Utilities.random.nextBytes(bArr);
            byte[] toByteArray = BigInteger.valueOf((long) MessagesStorage.getInstance(this.currentAccount).getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes())).toByteArray();
            if (toByteArray.length > 256) {
                Object obj = new byte[256];
                System.arraycopy(toByteArray, 1, obj, 0, 256);
                toByteArray = obj;
            }
            encryptedChat.exchange_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
            encryptedChat.a_or_b = bArr;
            encryptedChat.g_a = toByteArray;
            MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(encryptedChat);
            sendRequestKeyMessage(encryptedChat, null);
        }
    }

    public void processAcceptedSecretChat(final EncryptedChat encryptedChat) {
        BigInteger bigInteger = new BigInteger(1, MessagesStorage.getInstance(this.currentAccount).getSecretPBytes());
        BigInteger bigInteger2 = new BigInteger(1, encryptedChat.g_a_or_b);
        if (Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
            byte[] bArr;
            Object toByteArray = bigInteger2.modPow(new BigInteger(1, encryptedChat.a_or_b), bigInteger).toByteArray();
            if (toByteArray.length > 256) {
                bArr = new byte[256];
                System.arraycopy(toByteArray, toByteArray.length - 256, bArr, 0, 256);
            } else if (toByteArray.length < 256) {
                bArr = new byte[256];
                System.arraycopy(toByteArray, 0, bArr, 256 - toByteArray.length, toByteArray.length);
                for (int i = 0; i < 256 - toByteArray.length; i++) {
                    toByteArray[i] = null;
                }
            } else {
                bArr = toByteArray;
            }
            toByteArray = Utilities.computeSHA1(bArr);
            Object obj = new byte[8];
            System.arraycopy(toByteArray, toByteArray.length - 8, obj, 0, 8);
            if (encryptedChat.key_fingerprint == Utilities.bytesToLong(obj)) {
                encryptedChat.auth_key = bArr;
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
                final EncryptedChat tL_encryptedChatDiscarded = new TL_encryptedChatDiscarded();
                tL_encryptedChatDiscarded.id = encryptedChat.id;
                tL_encryptedChatDiscarded.user_id = encryptedChat.user_id;
                tL_encryptedChatDiscarded.auth_key = encryptedChat.auth_key;
                tL_encryptedChatDiscarded.key_create_date = encryptedChat.key_create_date;
                tL_encryptedChatDiscarded.key_use_count_in = encryptedChat.key_use_count_in;
                tL_encryptedChatDiscarded.key_use_count_out = encryptedChat.key_use_count_out;
                tL_encryptedChatDiscarded.seq_in = encryptedChat.seq_in;
                tL_encryptedChatDiscarded.seq_out = encryptedChat.seq_out;
                tL_encryptedChatDiscarded.admin_id = encryptedChat.admin_id;
                tL_encryptedChatDiscarded.mtproto_seq = encryptedChat.mtproto_seq;
                MessagesStorage.getInstance(this.currentAccount).updateEncryptedChat(tL_encryptedChatDiscarded);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(tL_encryptedChatDiscarded, false);
                        NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, tL_encryptedChatDiscarded);
                    }
                });
                declineSecretChat(encryptedChat.id);
            }
            return;
        }
        declineSecretChat(encryptedChat.id);
    }

    public void declineSecretChat(int i) {
        TLObject tL_messages_discardEncryption = new TL_messages_discardEncryption();
        tL_messages_discardEncryption.chat_id = i;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_discardEncryption, new RequestDelegate() {
            public void run(TLObject tLObject, TL_error tL_error) {
            }
        });
    }

    public void acceptSecretChat(final EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) == null) {
            this.acceptingChats.put(encryptedChat.id, encryptedChat);
            TLObject tL_messages_getDhConfig = new TL_messages_getDhConfig();
            tL_messages_getDhConfig.random_length = 256;
            tL_messages_getDhConfig.version = MessagesStorage.getInstance(this.currentAccount).getLastSecretVersion();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getDhConfig, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.SecretChatHelper$13$1 */
                class C18171 implements RequestDelegate {
                    C18171() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                        if (tL_error == null) {
                            final EncryptedChat encryptedChat = (EncryptedChat) tLObject;
                            encryptedChat.auth_key = encryptedChat.auth_key;
                            encryptedChat.user_id = encryptedChat.user_id;
                            encryptedChat.seq_in = encryptedChat.seq_in;
                            encryptedChat.seq_out = encryptedChat.seq_out;
                            encryptedChat.key_create_date = encryptedChat.key_create_date;
                            encryptedChat.key_use_count_in = encryptedChat.key_use_count_in;
                            encryptedChat.key_use_count_out = encryptedChat.key_use_count_out;
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).updateEncryptedChat(encryptedChat);
                            MessagesController.getInstance(SecretChatHelper.this.currentAccount).putEncryptedChat(encryptedChat, false);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(SecretChatHelper.this.currentAccount).postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
                                    SecretChatHelper.this.sendNotifyLayerMessage(encryptedChat, null);
                                }
                            });
                        }
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        messages_DhConfig messages_dhconfig = (messages_DhConfig) tLObject;
                        if ((tLObject instanceof TL_messages_dhConfig) != null) {
                            if (Utilities.isGoodPrime(messages_dhconfig.f56p, messages_dhconfig.f55g) == null) {
                                SecretChatHelper.this.acceptingChats.remove(encryptedChat.id);
                                SecretChatHelper.this.declineSecretChat(encryptedChat.id);
                                return;
                            }
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretPBytes(messages_dhconfig.f56p);
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setSecretG(messages_dhconfig.f55g);
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).setLastSecretVersion(messages_dhconfig.version);
                            MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).saveSecretParams(MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getLastSecretVersion(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG(), MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                        }
                        byte[] bArr = new byte[256];
                        for (int i = 0; i < 256; i++) {
                            bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
                        }
                        encryptedChat.a_or_b = bArr;
                        encryptedChat.seq_in = -1;
                        encryptedChat.seq_out = 0;
                        tL_error = new BigInteger(1, MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretPBytes());
                        BigInteger modPow = BigInteger.valueOf((long) MessagesStorage.getInstance(SecretChatHelper.this.currentAccount).getSecretG()).modPow(new BigInteger(1, bArr), tL_error);
                        BigInteger bigInteger = new BigInteger(1, encryptedChat.g_a);
                        if (Utilities.isGoodGaAndGb(bigInteger, tL_error)) {
                            byte[] toByteArray = modPow.toByteArray();
                            if (toByteArray.length > 256) {
                                Object obj = new byte[256];
                                System.arraycopy(toByteArray, 1, obj, 0, 256);
                                toByteArray = obj;
                            }
                            tL_error = bigInteger.modPow(new BigInteger(1, bArr), tL_error).toByteArray();
                            if (tL_error.length > 256) {
                                bArr = new byte[256];
                                System.arraycopy(tL_error, tL_error.length - 256, bArr, 0, 256);
                            } else if (tL_error.length < 256) {
                                bArr = new byte[256];
                                System.arraycopy(tL_error, 0, bArr, 256 - tL_error.length, tL_error.length);
                                for (int i2 = 0; i2 < 256 - tL_error.length; i2++) {
                                    tL_error[i2] = null;
                                }
                            } else {
                                bArr = tL_error;
                            }
                            tLObject = Utilities.computeSHA1(bArr);
                            Object obj2 = new byte[8];
                            System.arraycopy(tLObject, tLObject.length - 8, obj2, 0, 8);
                            encryptedChat.auth_key = bArr;
                            encryptedChat.key_create_date = ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).getCurrentTime();
                            tLObject = new TL_messages_acceptEncryption();
                            tLObject.g_b = toByteArray;
                            tLObject.peer = new TL_inputEncryptedChat();
                            tLObject.peer.chat_id = encryptedChat.id;
                            tLObject.peer.access_hash = encryptedChat.access_hash;
                            tLObject.key_fingerprint = Utilities.bytesToLong(obj2);
                            ConnectionsManager.getInstance(SecretChatHelper.this.currentAccount).sendRequest(tLObject, new C18171());
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

    public void startSecretChat(final android.content.Context r5, final org.telegram.tgnet.TLRPC.User r6) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r4 = this;
        if (r6 == 0) goto L_0x005b;
    L_0x0002:
        if (r5 != 0) goto L_0x0005;
    L_0x0004:
        goto L_0x005b;
    L_0x0005:
        r0 = 1;
        r4.startingSecretChat = r0;
        r1 = new org.telegram.ui.ActionBar.AlertDialog;
        r1.<init>(r5, r0);
        r0 = "Loading";
        r2 = NUM; // 0x7f0c0382 float:1.8611013E38 double:1.053097842E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);
        r1.setMessage(r0);
        r0 = 0;
        r1.setCanceledOnTouchOutside(r0);
        r1.setCancelable(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getDhConfig;
        r0.<init>();
        r2 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        r0.random_length = r2;
        r2 = r4.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2 = r2.getLastSecretVersion();
        r0.version = r2;
        r2 = r4.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = new org.telegram.messenger.SecretChatHelper$14;
        r3.<init>(r5, r1, r6);
        r5 = 2;
        r5 = r2.sendRequest(r0, r3, r5);
        r6 = -2;
        r0 = "Cancel";
        r2 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);
        r2 = new org.telegram.messenger.SecretChatHelper$15;
        r2.<init>(r5);
        r1.setButton(r6, r0, r2);
        r1.show();	 Catch:{ Exception -> 0x005a }
    L_0x005a:
        return;
    L_0x005b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.startSecretChat(android.content.Context, org.telegram.tgnet.TLRPC$User):void");
    }
}
