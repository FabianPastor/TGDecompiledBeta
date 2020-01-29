package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public class SecretChatHelper extends BaseController {
    public static final int CURRENT_SECRET_CHAT_LAYER = 101;
    private static volatile SecretChatHelper[] Instance = new SecretChatHelper[3];
    private SparseArray<TLRPC.EncryptedChat> acceptingChats = new SparseArray<>();
    public ArrayList<TLRPC.Update> delayedEncryptedChatUpdates = new ArrayList<>();
    private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList<>();
    private SparseArray<ArrayList<TLRPC.Update>> pendingSecretMessages = new SparseArray<>();
    private SparseArray<SparseIntArray> requestedHoles = new SparseArray<>();
    private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray<>();
    private ArrayList<Integer> sendingNotifyLayer = new ArrayList<>();
    private boolean startingSecretChat = false;

    static /* synthetic */ void lambda$declineSecretChat$19(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = NUM;
        public int date;
        public int decryptedWithVersion;
        public TLRPC.EncryptedFile file;
        public TLRPC.TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            abstractSerializedData.readInt64(z);
            this.date = abstractSerializedData.readInt32(z);
            this.layer = TLRPC.TL_decryptedMessageLayer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (abstractSerializedData.readBool(z)) {
                this.file = TLRPC.EncryptedFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.new_key_used = abstractSerializedData.readBool(z);
        }

        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt64(0);
            abstractSerializedData.writeInt32(this.date);
            this.layer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeBool(this.file != null);
            TLRPC.EncryptedFile encryptedFile = this.file;
            if (encryptedFile != null) {
                encryptedFile.serializeToStream(abstractSerializedData);
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
        super(i);
    }

    public void cleanup() {
        this.sendingNotifyLayer.clear();
        this.acceptingChats.clear();
        this.secretHolesQueue.clear();
        this.pendingSecretMessages.clear();
        this.requestedHoles.clear();
        this.delayedEncryptedChatUpdates.clear();
        this.pendingEncMessagesToDelete.clear();
        this.startingSecretChat = false;
    }

    /* access modifiers changed from: protected */
    public void processPendingEncMessages() {
        if (!this.pendingEncMessagesToDelete.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(new ArrayList(this.pendingEncMessagesToDelete)) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processPendingEncMessages$0$SecretChatHelper(this.f$1);
                }
            });
            getMessagesStorage().markMessagesAsDeletedByRandoms(new ArrayList(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
        }
    }

    public /* synthetic */ void lambda$processPendingEncMessages$0$SecretChatHelper(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = getMessagesController().dialogMessagesByRandomIds.get(((Long) arrayList.get(i)).longValue());
            if (messageObject != null) {
                messageObject.deleted = true;
            }
        }
    }

    private TLRPC.TL_messageService createServiceSecretMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.DecryptedMessageAction decryptedMessageAction) {
        TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
        tL_messageService.action = new TLRPC.TL_messageEncryptedAction();
        tL_messageService.action.encryptedAction = decryptedMessageAction;
        int newMessageId = getUserConfig().getNewMessageId();
        tL_messageService.id = newMessageId;
        tL_messageService.local_id = newMessageId;
        tL_messageService.from_id = getUserConfig().getClientUserId();
        tL_messageService.unread = true;
        tL_messageService.out = true;
        tL_messageService.flags = 256;
        tL_messageService.dialog_id = ((long) encryptedChat.id) << 32;
        tL_messageService.to_id = new TLRPC.TL_peerUser();
        tL_messageService.send_state = 1;
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            tL_messageService.to_id.user_id = encryptedChat.admin_id;
        } else {
            tL_messageService.to_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
            tL_messageService.date = getConnectionsManager().getCurrentTime();
        } else {
            tL_messageService.date = 0;
        }
        tL_messageService.random_id = getSendMessagesHelper().getNextRandomId();
        getUserConfig().saveConfig(false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_messageService);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, false, true, true, 0, false);
        return tL_messageService;
    }

    public void sendMessagesReadMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> arrayList, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionReadMessages();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.random_ids = arrayList;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    /* access modifiers changed from: protected */
    public void processUpdateEncryption(TLRPC.TL_updateEncryption tL_updateEncryption, ConcurrentHashMap<Integer, TLRPC.User> concurrentHashMap) {
        byte[] bArr;
        TLRPC.EncryptedChat encryptedChat = tL_updateEncryption.chat;
        long j = ((long) encryptedChat.id) << 32;
        TLRPC.EncryptedChat encryptedChatDB = getMessagesController().getEncryptedChatDB(encryptedChat.id, false);
        if ((encryptedChat instanceof TLRPC.TL_encryptedChatRequested) && encryptedChatDB == null) {
            int i = encryptedChat.participant_id;
            if (i == getUserConfig().getClientUserId()) {
                i = encryptedChat.admin_id;
            }
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(i));
            if (user == null) {
                user = concurrentHashMap.get(Integer.valueOf(i));
            }
            encryptedChat.user_id = i;
            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
            tL_dialog.id = j;
            tL_dialog.unread_count = 0;
            tL_dialog.top_message = 0;
            tL_dialog.last_message_date = tL_updateEncryption.date;
            getMessagesController().putEncryptedChat(encryptedChat, false);
            AndroidUtilities.runOnUIThread(new Runnable(tL_dialog) {
                private final /* synthetic */ TLRPC.Dialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processUpdateEncryption$1$SecretChatHelper(this.f$1);
                }
            });
            getMessagesStorage().putEncryptedChat(encryptedChat, user, tL_dialog);
            acceptSecretChat(encryptedChat);
        } else if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
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
            AndroidUtilities.runOnUIThread(new Runnable(encryptedChatDB, encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;
                private final /* synthetic */ TLRPC.EncryptedChat f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processUpdateEncryption$2$SecretChatHelper(this.f$1, this.f$2);
                }
            });
        } else if ((encryptedChatDB instanceof TLRPC.TL_encryptedChatWaiting) && ((bArr = encryptedChatDB.auth_key) == null || bArr.length == 1)) {
            encryptedChat.a_or_b = encryptedChatDB.a_or_b;
            encryptedChat.user_id = encryptedChatDB.user_id;
            processAcceptedSecretChat(encryptedChat);
        } else if (encryptedChatDB == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(tL_updateEncryption);
        }
    }

    public /* synthetic */ void lambda$processUpdateEncryption$1$SecretChatHelper(TLRPC.Dialog dialog) {
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateEncryption$2$SecretChatHelper(TLRPC.EncryptedChat encryptedChat, TLRPC.EncryptedChat encryptedChat2) {
        if (encryptedChat != null) {
            getMessagesController().putEncryptedChat(encryptedChat2, false);
        }
        getMessagesStorage().updateEncryptedChat(encryptedChat2);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat2);
    }

    public void sendMessagesDeleteMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> arrayList, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionDeleteMessages();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.random_ids = arrayList;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendClearHistoryMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNotifyLayerMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if ((encryptedChat instanceof TLRPC.TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionNotifyLayer();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.layer = 101;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendRequestKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionRequestKey();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.exchange_id = encryptedChat.exchange_id;
                decryptedMessageAction.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAcceptKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionAcceptKey();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.exchange_id = encryptedChat.exchange_id;
                decryptedMessageAction.key_fingerprint = encryptedChat.future_key_fingerprint;
                decryptedMessageAction.g_b = encryptedChat.g_a_or_b;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendCommitKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionCommitKey();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.exchange_id = encryptedChat.exchange_id;
                decryptedMessageAction.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAbortKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message, long j) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionAbortKey();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.exchange_id = j;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNoopMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, tL_decryptedMessageService.action);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendResendMessage(TLRPC.EncryptedChat encryptedChat, int i, int i2, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            SparseIntArray sparseIntArray = this.requestedHoles.get(encryptedChat.id);
            if (sparseIntArray == null || sparseIntArray.indexOfKey(i) < 0) {
                if (sparseIntArray == null) {
                    sparseIntArray = new SparseIntArray();
                    this.requestedHoles.put(encryptedChat.id, sparseIntArray);
                }
                sparseIntArray.put(i, i2);
                TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
                if (message != null) {
                    tL_decryptedMessageService.action = message.action.encryptedAction;
                } else {
                    tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionResend();
                    TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                    decryptedMessageAction.start_seq_no = i;
                    decryptedMessageAction.end_seq_no = i2;
                    message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
                }
                TLRPC.Message message2 = message;
                tL_decryptedMessageService.random_id = message2.random_id;
                performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
            }
        }
    }

    public void sendTTLMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionSetMessageTTL();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.ttl_seconds = encryptedChat.ttl;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
                MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
                messageObject.messageOwner.send_state = 1;
                ArrayList arrayList = new ArrayList();
                arrayList.add(messageObject);
                getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendScreenshotMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> arrayList, TLRPC.Message message) {
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService tL_decryptedMessageService = new TLRPC.TL_decryptedMessageService();
            if (message != null) {
                tL_decryptedMessageService.action = message.action.encryptedAction;
            } else {
                tL_decryptedMessageService.action = new TLRPC.TL_decryptedMessageActionScreenshotMessages();
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                decryptedMessageAction.random_ids = arrayList;
                message = createServiceSecretMessage(encryptedChat, decryptedMessageAction);
                MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
                messageObject.messageOwner.send_state = 1;
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(messageObject);
                getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList2, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            TLRPC.Message message2 = message;
            tL_decryptedMessageService.random_id = message2.random_id;
            performSendEncryptedRequest(tL_decryptedMessageService, message2, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    private void updateMediaPaths(MessageObject messageObject, TLRPC.EncryptedFile encryptedFile, TLRPC.DecryptedMessage decryptedMessage, String str) {
        TLRPC.Document document;
        TLRPC.Photo photo;
        TLRPC.Message message = messageObject.messageOwner;
        if (encryptedFile != null) {
            TLRPC.MessageMedia messageMedia = message.media;
            if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (photo = messageMedia.photo) == null) {
                TLRPC.MessageMedia messageMedia2 = message.media;
                if ((messageMedia2 instanceof TLRPC.TL_messageMediaDocument) && (document = messageMedia2.document) != null) {
                    messageMedia2.document = new TLRPC.TL_documentEncrypted();
                    TLRPC.Document document2 = message.media.document;
                    document2.id = encryptedFile.id;
                    document2.access_hash = encryptedFile.access_hash;
                    document2.date = document.date;
                    document2.attributes = document.attributes;
                    document2.mime_type = document.mime_type;
                    document2.size = encryptedFile.size;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia = decryptedMessage.media;
                    document2.key = decryptedMessageMedia.key;
                    document2.iv = decryptedMessageMedia.iv;
                    document2.thumbs = document.thumbs;
                    document2.dc_id = encryptedFile.dc_id;
                    if (document2.thumbs.isEmpty()) {
                        TLRPC.TL_photoSizeEmpty tL_photoSizeEmpty = new TLRPC.TL_photoSizeEmpty();
                        tL_photoSizeEmpty.type = "s";
                        message.media.document.thumbs.add(tL_photoSizeEmpty);
                    }
                    String str2 = message.attachPath;
                    if (str2 != null && str2.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(message.attachPath).renameTo(FileLoader.getPathToAttach(message.media.document))) {
                        messageObject.mediaExists = messageObject.attachPathExists;
                        messageObject.attachPathExists = false;
                        message.attachPath = "";
                    }
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(message);
                    getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, false, true, false, 0, false);
                    return;
                }
                return;
            }
            ArrayList<TLRPC.PhotoSize> arrayList2 = photo.sizes;
            TLRPC.PhotoSize photoSize = arrayList2.get(arrayList2.size() - 1);
            String str3 = photoSize.location.volume_id + "_" + photoSize.location.local_id;
            photoSize.location = new TLRPC.TL_fileEncryptedLocation();
            TLRPC.FileLocation fileLocation = photoSize.location;
            TLRPC.DecryptedMessageMedia decryptedMessageMedia2 = decryptedMessage.media;
            fileLocation.key = decryptedMessageMedia2.key;
            fileLocation.iv = decryptedMessageMedia2.iv;
            fileLocation.dc_id = encryptedFile.dc_id;
            fileLocation.volume_id = encryptedFile.id;
            fileLocation.secret = encryptedFile.access_hash;
            fileLocation.local_id = encryptedFile.key_fingerprint;
            new File(FileLoader.getDirectory(4), str3 + ".jpg").renameTo(FileLoader.getPathToAttach(photoSize));
            ImageLoader.getInstance().replaceImageInCache(str3, photoSize.location.volume_id + "_" + photoSize.location.local_id, ImageLocation.getForPhoto(photoSize, message.media.photo), true);
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(message);
            getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList3, false, true, false, 0, false);
        }
    }

    public static boolean isSecretVisibleMessage(TLRPC.Message message) {
        TLRPC.MessageAction messageAction = message.action;
        if (messageAction instanceof TLRPC.TL_messageEncryptedAction) {
            TLRPC.DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
            if ((decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSecretInvisibleMessage(TLRPC.Message message) {
        TLRPC.MessageAction messageAction = message.action;
        if (messageAction instanceof TLRPC.TL_messageEncryptedAction) {
            TLRPC.DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
            return !(decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) && !(decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL);
        }
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC.TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia, SendMessagesHelper.DelayedMessage delayedMessage) {
        for (int i = 0; i < tL_messages_sendEncryptedMultiMedia.files.size(); i++) {
            performSendEncryptedRequest(tL_messages_sendEncryptedMultiMedia.messages.get(i), delayedMessage.messages.get(i), delayedMessage.encryptedChat, tL_messages_sendEncryptedMultiMedia.files.get(i), delayedMessage.originalPaths.get(i), delayedMessage.messageObjects.get(i));
        }
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC.DecryptedMessage decryptedMessage, TLRPC.Message message, TLRPC.EncryptedChat encryptedChat, TLRPC.InputEncryptedFile inputEncryptedFile, String str, MessageObject messageObject) {
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        if (decryptedMessage != null && encryptedChat2.auth_key != null && !(encryptedChat2 instanceof TLRPC.TL_encryptedChatRequested) && !(encryptedChat2 instanceof TLRPC.TL_encryptedChatWaiting)) {
            getSendMessagesHelper().putToSendingMessages(message, false);
            Utilities.stageQueue.postRunnable(new Runnable(encryptedChat, decryptedMessage, message, inputEncryptedFile, messageObject, str) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;
                private final /* synthetic */ TLRPC.DecryptedMessage f$2;
                private final /* synthetic */ TLRPC.Message f$3;
                private final /* synthetic */ TLRPC.InputEncryptedFile f$4;
                private final /* synthetic */ MessageObject f$5;
                private final /* synthetic */ String f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$performSendEncryptedRequest$7$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSendEncryptedRequest$7$SecretChatHelper(org.telegram.tgnet.TLRPC.EncryptedChat r27, org.telegram.tgnet.TLRPC.DecryptedMessage r28, org.telegram.tgnet.TLRPC.Message r29, org.telegram.tgnet.TLRPC.InputEncryptedFile r30, org.telegram.messenger.MessageObject r31, java.lang.String r32) {
        /*
            r26 = this;
            r0 = r27
            r3 = r28
            r5 = r29
            r1 = r30
            org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer     // Catch:{ Exception -> 0x0263 }
            r2.<init>()     // Catch:{ Exception -> 0x0263 }
            int r4 = r0.layer     // Catch:{ Exception -> 0x0263 }
            int r4 = org.telegram.messenger.AndroidUtilities.getMyLayerVersion(r4)     // Catch:{ Exception -> 0x0263 }
            r6 = 46
            int r4 = java.lang.Math.max(r6, r4)     // Catch:{ Exception -> 0x0263 }
            int r7 = r0.layer     // Catch:{ Exception -> 0x0263 }
            int r7 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r7)     // Catch:{ Exception -> 0x0263 }
            int r6 = java.lang.Math.max(r6, r7)     // Catch:{ Exception -> 0x0263 }
            int r4 = java.lang.Math.min(r4, r6)     // Catch:{ Exception -> 0x0263 }
            r2.layer = r4     // Catch:{ Exception -> 0x0263 }
            r2.message = r3     // Catch:{ Exception -> 0x0263 }
            r4 = 15
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0263 }
            r2.random_bytes = r4     // Catch:{ Exception -> 0x0263 }
            java.security.SecureRandom r4 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0263 }
            byte[] r6 = r2.random_bytes     // Catch:{ Exception -> 0x0263 }
            r4.nextBytes(r6)     // Catch:{ Exception -> 0x0263 }
            int r4 = r0.layer     // Catch:{ Exception -> 0x0263 }
            int r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4)     // Catch:{ Exception -> 0x0263 }
            r6 = 73
            r7 = 1
            r8 = 2
            if (r4 < r6) goto L_0x0046
            r4 = 2
            goto L_0x0047
        L_0x0046:
            r4 = 1
        L_0x0047:
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0263 }
            if (r6 != 0) goto L_0x0064
            int r6 = r0.seq_out     // Catch:{ Exception -> 0x0263 }
            if (r6 != 0) goto L_0x0064
            int r6 = r0.admin_id     // Catch:{ Exception -> 0x0263 }
            org.telegram.messenger.UserConfig r9 = r26.getUserConfig()     // Catch:{ Exception -> 0x0263 }
            int r9 = r9.getClientUserId()     // Catch:{ Exception -> 0x0263 }
            if (r6 != r9) goto L_0x0061
            r0.seq_out = r7     // Catch:{ Exception -> 0x0263 }
            r6 = -2
            r0.seq_in = r6     // Catch:{ Exception -> 0x0263 }
            goto L_0x0064
        L_0x0061:
            r6 = -1
            r0.seq_in = r6     // Catch:{ Exception -> 0x0263 }
        L_0x0064:
            int r6 = r5.seq_in     // Catch:{ Exception -> 0x0263 }
            r9 = 0
            if (r6 != 0) goto L_0x00e6
            int r6 = r5.seq_out     // Catch:{ Exception -> 0x0263 }
            if (r6 != 0) goto L_0x00e6
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0263 }
            if (r6 <= 0) goto L_0x0074
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0263 }
            goto L_0x0077
        L_0x0074:
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0263 }
            int r6 = r6 + r8
        L_0x0077:
            r2.in_seq_no = r6     // Catch:{ Exception -> 0x0263 }
            int r6 = r0.seq_out     // Catch:{ Exception -> 0x0263 }
            r2.out_seq_no = r6     // Catch:{ Exception -> 0x0263 }
            int r6 = r0.seq_out     // Catch:{ Exception -> 0x0263 }
            int r6 = r6 + r8
            r0.seq_out = r6     // Catch:{ Exception -> 0x0263 }
            int r6 = r0.layer     // Catch:{ Exception -> 0x0263 }
            int r6 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r6)     // Catch:{ Exception -> 0x0263 }
            r10 = 20
            if (r6 < r10) goto L_0x00c7
            int r6 = r0.key_create_date     // Catch:{ Exception -> 0x0263 }
            if (r6 != 0) goto L_0x009a
            org.telegram.tgnet.ConnectionsManager r6 = r26.getConnectionsManager()     // Catch:{ Exception -> 0x0263 }
            int r6 = r6.getCurrentTime()     // Catch:{ Exception -> 0x0263 }
            r0.key_create_date = r6     // Catch:{ Exception -> 0x0263 }
        L_0x009a:
            short r6 = r0.key_use_count_out     // Catch:{ Exception -> 0x0263 }
            int r6 = r6 + r7
            short r6 = (short) r6     // Catch:{ Exception -> 0x0263 }
            r0.key_use_count_out = r6     // Catch:{ Exception -> 0x0263 }
            short r6 = r0.key_use_count_out     // Catch:{ Exception -> 0x0263 }
            r10 = 100
            if (r6 >= r10) goto L_0x00b6
            int r6 = r0.key_create_date     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.ConnectionsManager r10 = r26.getConnectionsManager()     // Catch:{ Exception -> 0x0263 }
            int r10 = r10.getCurrentTime()     // Catch:{ Exception -> 0x0263 }
            r11 = 604800(0x93a80, float:8.47505E-40)
            int r10 = r10 - r11
            if (r6 >= r10) goto L_0x00c7
        L_0x00b6:
            long r10 = r0.exchange_id     // Catch:{ Exception -> 0x0263 }
            r12 = 0
            int r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r6 != 0) goto L_0x00c7
            long r10 = r0.future_key_fingerprint     // Catch:{ Exception -> 0x0263 }
            int r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r6 != 0) goto L_0x00c7
            r26.requestNewSecretChatKey(r27)     // Catch:{ Exception -> 0x0263 }
        L_0x00c7:
            org.telegram.messenger.MessagesStorage r6 = r26.getMessagesStorage()     // Catch:{ Exception -> 0x0263 }
            r6.updateEncryptedChatSeq(r0, r9)     // Catch:{ Exception -> 0x0263 }
            if (r5 == 0) goto L_0x00ee
            int r6 = r2.in_seq_no     // Catch:{ Exception -> 0x0263 }
            r5.seq_in = r6     // Catch:{ Exception -> 0x0263 }
            int r6 = r2.out_seq_no     // Catch:{ Exception -> 0x0263 }
            r5.seq_out = r6     // Catch:{ Exception -> 0x0263 }
            org.telegram.messenger.MessagesStorage r6 = r26.getMessagesStorage()     // Catch:{ Exception -> 0x0263 }
            int r10 = r5.id     // Catch:{ Exception -> 0x0263 }
            int r11 = r5.seq_in     // Catch:{ Exception -> 0x0263 }
            int r12 = r5.seq_out     // Catch:{ Exception -> 0x0263 }
            r6.setMessageSeq(r10, r11, r12)     // Catch:{ Exception -> 0x0263 }
            goto L_0x00ee
        L_0x00e6:
            int r6 = r5.seq_in     // Catch:{ Exception -> 0x0263 }
            r2.in_seq_no = r6     // Catch:{ Exception -> 0x0263 }
            int r6 = r5.seq_out     // Catch:{ Exception -> 0x0263 }
            r2.out_seq_no = r6     // Catch:{ Exception -> 0x0263 }
        L_0x00ee:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0263 }
            if (r6 == 0) goto L_0x0115
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0263 }
            r6.<init>()     // Catch:{ Exception -> 0x0263 }
            r6.append(r3)     // Catch:{ Exception -> 0x0263 }
            java.lang.String r10 = " send message with in_seq = "
            r6.append(r10)     // Catch:{ Exception -> 0x0263 }
            int r10 = r2.in_seq_no     // Catch:{ Exception -> 0x0263 }
            r6.append(r10)     // Catch:{ Exception -> 0x0263 }
            java.lang.String r10 = " out_seq = "
            r6.append(r10)     // Catch:{ Exception -> 0x0263 }
            int r10 = r2.out_seq_no     // Catch:{ Exception -> 0x0263 }
            r6.append(r10)     // Catch:{ Exception -> 0x0263 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0263 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0263 }
        L_0x0115:
            int r6 = r2.getObjectSize()     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0263 }
            int r11 = r6 + 4
            r10.<init>((int) r11)     // Catch:{ Exception -> 0x0263 }
            r10.writeInt32(r6)     // Catch:{ Exception -> 0x0263 }
            r2.serializeToStream(r10)     // Catch:{ Exception -> 0x0263 }
            int r2 = r10.length()     // Catch:{ Exception -> 0x0263 }
            int r6 = r2 % 16
            r11 = 16
            if (r6 == 0) goto L_0x0135
            int r6 = r2 % 16
            int r6 = 16 - r6
            goto L_0x0136
        L_0x0135:
            r6 = 0
        L_0x0136:
            if (r4 != r8) goto L_0x0143
            java.security.SecureRandom r12 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0263 }
            r13 = 3
            int r12 = r12.nextInt(r13)     // Catch:{ Exception -> 0x0263 }
            int r12 = r12 + r8
            int r12 = r12 * 16
            int r6 = r6 + r12
        L_0x0143:
            org.telegram.tgnet.NativeByteBuffer r12 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0263 }
            int r2 = r2 + r6
            r12.<init>((int) r2)     // Catch:{ Exception -> 0x0263 }
            r10.position(r9)     // Catch:{ Exception -> 0x0263 }
            r12.writeBytes((org.telegram.tgnet.NativeByteBuffer) r10)     // Catch:{ Exception -> 0x0263 }
            if (r6 == 0) goto L_0x015b
            byte[] r2 = new byte[r6]     // Catch:{ Exception -> 0x0263 }
            java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0263 }
            r6.nextBytes(r2)     // Catch:{ Exception -> 0x0263 }
            r12.writeBytes((byte[]) r2)     // Catch:{ Exception -> 0x0263 }
        L_0x015b:
            byte[] r2 = new byte[r11]     // Catch:{ Exception -> 0x0263 }
            if (r4 != r8) goto L_0x016c
            int r6 = r0.admin_id     // Catch:{ Exception -> 0x0263 }
            org.telegram.messenger.UserConfig r13 = r26.getUserConfig()     // Catch:{ Exception -> 0x0263 }
            int r13 = r13.getClientUserId()     // Catch:{ Exception -> 0x0263 }
            if (r6 == r13) goto L_0x016c
            goto L_0x016d
        L_0x016c:
            r7 = 0
        L_0x016d:
            if (r4 != r8) goto L_0x0192
            byte[] r13 = r0.auth_key     // Catch:{ Exception -> 0x0263 }
            r8 = 88
            if (r7 == 0) goto L_0x0178
            r14 = 8
            goto L_0x0179
        L_0x0178:
            r14 = 0
        L_0x0179:
            int r14 = r14 + r8
            r15 = 32
            java.nio.ByteBuffer r8 = r12.buffer     // Catch:{ Exception -> 0x0263 }
            r17 = 0
            java.nio.ByteBuffer r6 = r12.buffer     // Catch:{ Exception -> 0x0263 }
            int r18 = r6.limit()     // Catch:{ Exception -> 0x0263 }
            r16 = r8
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA256(r13, r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x0263 }
            r8 = 8
            java.lang.System.arraycopy(r6, r8, r2, r9, r11)     // Catch:{ Exception -> 0x0263 }
            goto L_0x019d
        L_0x0192:
            java.nio.ByteBuffer r6 = r10.buffer     // Catch:{ Exception -> 0x0263 }
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA1((java.nio.ByteBuffer) r6)     // Catch:{ Exception -> 0x0263 }
            int r8 = r6.length     // Catch:{ Exception -> 0x0263 }
            int r8 = r8 - r11
            java.lang.System.arraycopy(r6, r8, r2, r9, r11)     // Catch:{ Exception -> 0x0263 }
        L_0x019d:
            r10.reuse()     // Catch:{ Exception -> 0x0263 }
            byte[] r6 = r0.auth_key     // Catch:{ Exception -> 0x0263 }
            org.telegram.messenger.MessageKeyData r4 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r6, r2, r7, r4)     // Catch:{ Exception -> 0x0263 }
            java.nio.ByteBuffer r6 = r12.buffer     // Catch:{ Exception -> 0x0263 }
            byte[] r7 = r4.aesKey     // Catch:{ Exception -> 0x0263 }
            byte[] r4 = r4.aesIv     // Catch:{ Exception -> 0x0263 }
            r22 = 1
            r23 = 0
            r24 = 0
            int r25 = r12.limit()     // Catch:{ Exception -> 0x0263 }
            r19 = r6
            r20 = r7
            r21 = r4
            org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0263 }
            int r6 = r2.length     // Catch:{ Exception -> 0x0263 }
            r7 = 8
            int r6 = r6 + r7
            int r7 = r12.length()     // Catch:{ Exception -> 0x0263 }
            int r6 = r6 + r7
            r4.<init>((int) r6)     // Catch:{ Exception -> 0x0263 }
            r12.position(r9)     // Catch:{ Exception -> 0x0263 }
            long r6 = r0.key_fingerprint     // Catch:{ Exception -> 0x0263 }
            r4.writeInt64(r6)     // Catch:{ Exception -> 0x0263 }
            r4.writeBytes((byte[]) r2)     // Catch:{ Exception -> 0x0263 }
            r4.writeBytes((org.telegram.tgnet.NativeByteBuffer) r12)     // Catch:{ Exception -> 0x0263 }
            r12.reuse()     // Catch:{ Exception -> 0x0263 }
            r4.position(r9)     // Catch:{ Exception -> 0x0263 }
            if (r1 != 0) goto L_0x0226
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageService     // Catch:{ Exception -> 0x0263 }
            if (r1 == 0) goto L_0x0206
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService     // Catch:{ Exception -> 0x0263 }
            r1.<init>()     // Catch:{ Exception -> 0x0263 }
            r1.data = r4     // Catch:{ Exception -> 0x0263 }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x0263 }
            r1.random_id = r6     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0263 }
            r2.<init>()     // Catch:{ Exception -> 0x0263 }
            r1.peer = r2     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x0263 }
            int r4 = r0.id     // Catch:{ Exception -> 0x0263 }
            r2.chat_id = r4     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x0263 }
            long r6 = r0.access_hash     // Catch:{ Exception -> 0x0263 }
            r2.access_hash = r6     // Catch:{ Exception -> 0x0263 }
            goto L_0x0224
        L_0x0206:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted     // Catch:{ Exception -> 0x0263 }
            r1.<init>()     // Catch:{ Exception -> 0x0263 }
            r1.data = r4     // Catch:{ Exception -> 0x0263 }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x0263 }
            r1.random_id = r6     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0263 }
            r2.<init>()     // Catch:{ Exception -> 0x0263 }
            r1.peer = r2     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x0263 }
            int r4 = r0.id     // Catch:{ Exception -> 0x0263 }
            r2.chat_id = r4     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = r1.peer     // Catch:{ Exception -> 0x0263 }
            long r6 = r0.access_hash     // Catch:{ Exception -> 0x0263 }
            r2.access_hash = r6     // Catch:{ Exception -> 0x0263 }
        L_0x0224:
            r8 = r1
            goto L_0x0247
        L_0x0226:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile     // Catch:{ Exception -> 0x0263 }
            r2.<init>()     // Catch:{ Exception -> 0x0263 }
            r2.data = r4     // Catch:{ Exception -> 0x0263 }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x0263 }
            r2.random_id = r6     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r4 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0263 }
            r4.<init>()     // Catch:{ Exception -> 0x0263 }
            r2.peer = r4     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r4 = r2.peer     // Catch:{ Exception -> 0x0263 }
            int r6 = r0.id     // Catch:{ Exception -> 0x0263 }
            r4.chat_id = r6     // Catch:{ Exception -> 0x0263 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r4 = r2.peer     // Catch:{ Exception -> 0x0263 }
            long r6 = r0.access_hash     // Catch:{ Exception -> 0x0263 }
            r4.access_hash = r6     // Catch:{ Exception -> 0x0263 }
            r2.file = r1     // Catch:{ Exception -> 0x0263 }
            r8 = r2
        L_0x0247:
            org.telegram.tgnet.ConnectionsManager r9 = r26.getConnectionsManager()     // Catch:{ Exception -> 0x0263 }
            org.telegram.messenger.-$$Lambda$SecretChatHelper$NeIJyTvVk2g1G3EFM6ENqFtjkw0 r10 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$NeIJyTvVk2g1G3EFM6ENqFtjkw0     // Catch:{ Exception -> 0x0263 }
            r1 = r10
            r2 = r26
            r3 = r28
            r4 = r27
            r5 = r29
            r6 = r31
            r7 = r32
            r1.<init>(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0263 }
            r0 = 64
            r9.sendRequest(r8, r10, r0)     // Catch:{ Exception -> 0x0263 }
            goto L_0x0267
        L_0x0263:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0267:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.lambda$performSendEncryptedRequest$7$SecretChatHelper(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$DecryptedMessage, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$InputEncryptedFile, org.telegram.messenger.MessageObject, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$6$SecretChatHelper(TLRPC.DecryptedMessage decryptedMessage, TLRPC.EncryptedChat encryptedChat, TLRPC.Message message, MessageObject messageObject, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i;
        if (tL_error == null && (decryptedMessage.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
            TLRPC.EncryptedChat encryptedChat2 = getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChat.id));
            if (encryptedChat2 == null) {
                encryptedChat2 = encryptedChat;
            }
            if (encryptedChat2.key_hash == null) {
                encryptedChat2.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat2.auth_key);
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 46 && encryptedChat2.key_hash.length == 16) {
                try {
                    byte[] computeSHA256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                    byte[] bArr = new byte[36];
                    System.arraycopy(encryptedChat.key_hash, 0, bArr, 0, 16);
                    System.arraycopy(computeSHA256, 0, bArr, 16, 20);
                    encryptedChat2.key_hash = bArr;
                    getMessagesStorage().updateEncryptedChat(encryptedChat2);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            this.sendingNotifyLayer.remove(Integer.valueOf(encryptedChat2.id));
            encryptedChat2.layer = AndroidUtilities.setMyLayerVersion(encryptedChat2.layer, 101);
            getMessagesStorage().updateEncryptedChatLayer(encryptedChat2);
        }
        if (message == null) {
            return;
        }
        if (tL_error == null) {
            String str2 = message.attachPath;
            TLRPC.messages_SentEncryptedMessage messages_sentencryptedmessage = (TLRPC.messages_SentEncryptedMessage) tLObject;
            if (isSecretVisibleMessage(message)) {
                message.date = messages_sentencryptedmessage.date;
            }
            if (messageObject != null) {
                TLRPC.EncryptedFile encryptedFile = messages_sentencryptedmessage.file;
                if (encryptedFile instanceof TLRPC.TL_encryptedFile) {
                    updateMediaPaths(messageObject, encryptedFile, decryptedMessage, str);
                    i = messageObject.getMediaExistanceFlags();
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(message, messages_sentencryptedmessage, i, str2) {
                        private final /* synthetic */ TLRPC.Message f$1;
                        private final /* synthetic */ TLRPC.messages_SentEncryptedMessage f$2;
                        private final /* synthetic */ int f$3;
                        private final /* synthetic */ String f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            SecretChatHelper.this.lambda$null$4$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                    return;
                }
            }
            i = 0;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(message, messages_sentencryptedmessage, i, str2) {
                private final /* synthetic */ TLRPC.Message f$1;
                private final /* synthetic */ TLRPC.messages_SentEncryptedMessage f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ String f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$4$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        getMessagesStorage().markMessageAsSendError(message, false);
        AndroidUtilities.runOnUIThread(new Runnable(message) {
            private final /* synthetic */ TLRPC.Message f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$5$SecretChatHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$SecretChatHelper(TLRPC.Message message, TLRPC.messages_SentEncryptedMessage messages_sentencryptedmessage, int i, String str) {
        if (isSecretInvisibleMessage(message)) {
            messages_sentencryptedmessage.date = 0;
        }
        getMessagesStorage().updateMessageStateAndId(message.random_id, Integer.valueOf(message.id), message.id, messages_sentencryptedmessage.date, false, 0, 0);
        AndroidUtilities.runOnUIThread(new Runnable(message, i, str) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$3$SecretChatHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$SecretChatHelper(TLRPC.Message message, int i, String str) {
        message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(message.id), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), 0L, Integer.valueOf(i), false);
        getSendMessagesHelper().processSentMessage(message.id);
        if (MessageObject.isVideoMessage(message) || MessageObject.isNewGifMessage(message) || MessageObject.isRoundVideoMessage(message)) {
            getSendMessagesHelper().stopVideoService(str);
        }
        getSendMessagesHelper().removeFromSendingMessages(message.id, false);
    }

    public /* synthetic */ void lambda$null$5$SecretChatHelper(TLRPC.Message message) {
        message.send_state = 2;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
        getSendMessagesHelper().processSentMessage(message.id);
        if (MessageObject.isVideoMessage(message) || MessageObject.isNewGifMessage(message) || MessageObject.isRoundVideoMessage(message)) {
            getSendMessagesHelper().stopVideoService(message.attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(message.id, false);
    }

    private void applyPeerLayer(TLRPC.EncryptedChat encryptedChat, int i) {
        int peerLayerVersion = AndroidUtilities.getPeerLayerVersion(encryptedChat.layer);
        if (i > peerLayerVersion) {
            if (encryptedChat.key_hash.length == 16 && peerLayerVersion >= 46) {
                try {
                    byte[] computeSHA256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                    byte[] bArr = new byte[36];
                    System.arraycopy(encryptedChat.key_hash, 0, bArr, 0, 16);
                    System.arraycopy(computeSHA256, 0, bArr, 16, 20);
                    encryptedChat.key_hash = bArr;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            encryptedChat.layer = AndroidUtilities.setPeerLayerVersion(encryptedChat.layer, i);
            getMessagesStorage().updateEncryptedChatLayer(encryptedChat);
            if (peerLayerVersion < 101) {
                sendNotifyLayerMessage(encryptedChat, (TLRPC.Message) null);
            }
            AndroidUtilities.runOnUIThread(new Runnable(encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$applyPeerLayer$8$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyPeerLayer$8$SecretChatHelper(TLRPC.EncryptedChat encryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
    }

    public TLRPC.Message processDecryptedObject(TLRPC.EncryptedChat encryptedChat, TLRPC.EncryptedFile encryptedFile, int i, TLObject tLObject, boolean z) {
        int i2;
        int i3;
        int i4;
        byte[] bArr;
        byte[] bArr2;
        TLRPC.Message message;
        byte[] bArr3;
        byte[] bArr4;
        TLRPC.PhotoSize photoSize;
        byte[] bArr5;
        TLRPC.PhotoSize photoSize2;
        byte[] bArr6;
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        TLRPC.EncryptedFile encryptedFile2 = encryptedFile;
        int i5 = i;
        TLObject tLObject2 = tLObject;
        if (tLObject2 != null) {
            int i6 = encryptedChat2.admin_id;
            if (i6 == getUserConfig().getClientUserId()) {
                i6 = encryptedChat2.participant_id;
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 20 && encryptedChat2.exchange_id == 0 && encryptedChat2.future_key_fingerprint == 0 && encryptedChat2.key_use_count_in >= 120) {
                requestNewSecretChatKey(encryptedChat);
            }
            if (encryptedChat2.exchange_id == 0 && encryptedChat2.future_key_fingerprint != 0 && !z) {
                encryptedChat2.future_auth_key = new byte[256];
                encryptedChat2.future_key_fingerprint = 0;
                getMessagesStorage().updateEncryptedChat(encryptedChat2);
            } else if (encryptedChat2.exchange_id != 0 && z) {
                encryptedChat2.key_fingerprint = encryptedChat2.future_key_fingerprint;
                encryptedChat2.auth_key = encryptedChat2.future_auth_key;
                encryptedChat2.key_create_date = getConnectionsManager().getCurrentTime();
                encryptedChat2.future_auth_key = new byte[256];
                encryptedChat2.future_key_fingerprint = 0;
                encryptedChat2.key_use_count_in = 0;
                encryptedChat2.key_use_count_out = 0;
                encryptedChat2.exchange_id = 0;
                getMessagesStorage().updateEncryptedChat(encryptedChat2);
            }
            if (tLObject2 instanceof TLRPC.TL_decryptedMessage) {
                TLRPC.TL_decryptedMessage tL_decryptedMessage = (TLRPC.TL_decryptedMessage) tLObject2;
                if (AndroidUtilities.getPeerLayerVersion(encryptedChat2.layer) >= 17) {
                    message = new TLRPC.TL_message_secret();
                    message.ttl = tL_decryptedMessage.ttl;
                    message.entities = tL_decryptedMessage.entities;
                } else {
                    message = new TLRPC.TL_message();
                    message.ttl = encryptedChat2.ttl;
                }
                message.message = tL_decryptedMessage.message;
                message.date = i5;
                int newMessageId = getUserConfig().getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                getUserConfig().saveConfig(false);
                message.from_id = i6;
                message.to_id = new TLRPC.TL_peerUser();
                message.random_id = tL_decryptedMessage.random_id;
                message.to_id.user_id = getUserConfig().getClientUserId();
                message.unread = true;
                message.flags = 768;
                String str = tL_decryptedMessage.via_bot_name;
                if (str != null && str.length() > 0) {
                    message.via_bot_name = tL_decryptedMessage.via_bot_name;
                    message.flags |= 2048;
                }
                long j = tL_decryptedMessage.grouped_id;
                if (j != 0) {
                    message.grouped_id = j;
                    message.flags |= 131072;
                }
                message.dialog_id = ((long) encryptedChat2.id) << 32;
                long j2 = tL_decryptedMessage.reply_to_random_id;
                if (j2 != 0) {
                    message.reply_to_random_id = j2;
                    message.flags |= 8;
                }
                TLRPC.DecryptedMessageMedia decryptedMessageMedia = tL_decryptedMessage.media;
                if (decryptedMessageMedia == null || (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaEmpty)) {
                    message.media = new TLRPC.TL_messageMediaEmpty();
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaWebPage) {
                    message.media = new TLRPC.TL_messageMediaWebPage();
                    message.media.webpage = new TLRPC.TL_webPageUrlPending();
                    message.media.webpage.url = tL_decryptedMessage.media.url;
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaContact) {
                    message.media = new TLRPC.TL_messageMediaContact();
                    TLRPC.MessageMedia messageMedia = message.media;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia2 = tL_decryptedMessage.media;
                    messageMedia.last_name = decryptedMessageMedia2.last_name;
                    messageMedia.first_name = decryptedMessageMedia2.first_name;
                    messageMedia.phone_number = decryptedMessageMedia2.phone_number;
                    messageMedia.user_id = decryptedMessageMedia2.user_id;
                    messageMedia.vcard = "";
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaGeoPoint) {
                    message.media = new TLRPC.TL_messageMediaGeo();
                    message.media.geo = new TLRPC.TL_geoPoint();
                    TLRPC.GeoPoint geoPoint = message.media.geo;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia3 = tL_decryptedMessage.media;
                    geoPoint.lat = decryptedMessageMedia3.lat;
                    geoPoint._long = decryptedMessageMedia3._long;
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaPhoto) {
                    byte[] bArr7 = decryptedMessageMedia.key;
                    if (bArr7 == null || bArr7.length != 32 || (bArr6 = decryptedMessageMedia.iv) == null || bArr6.length != 32) {
                        return null;
                    }
                    message.media = new TLRPC.TL_messageMediaPhoto();
                    message.media.flags |= 3;
                    String str2 = tL_decryptedMessage.media.caption;
                    if (str2 == null) {
                        str2 = "";
                    }
                    message.message = str2;
                    message.media.photo = new TLRPC.TL_photo();
                    TLRPC.Photo photo = message.media.photo;
                    photo.file_reference = new byte[0];
                    photo.date = message.date;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia4 = tL_decryptedMessage.media;
                    byte[] bArr8 = ((TLRPC.TL_decryptedMessageMediaPhoto) decryptedMessageMedia4).thumb;
                    if (bArr8 != null && bArr8.length != 0 && bArr8.length <= 6000 && decryptedMessageMedia4.thumb_w <= 100 && decryptedMessageMedia4.thumb_h <= 100) {
                        TLRPC.TL_photoCachedSize tL_photoCachedSize = new TLRPC.TL_photoCachedSize();
                        TLRPC.DecryptedMessageMedia decryptedMessageMedia5 = tL_decryptedMessage.media;
                        tL_photoCachedSize.w = decryptedMessageMedia5.thumb_w;
                        tL_photoCachedSize.h = decryptedMessageMedia5.thumb_h;
                        tL_photoCachedSize.bytes = bArr8;
                        tL_photoCachedSize.type = "s";
                        tL_photoCachedSize.location = new TLRPC.TL_fileLocationUnavailable();
                        message.media.photo.sizes.add(tL_photoCachedSize);
                    }
                    int i7 = message.ttl;
                    if (i7 != 0) {
                        TLRPC.MessageMedia messageMedia2 = message.media;
                        messageMedia2.ttl_seconds = i7;
                        messageMedia2.flags |= 4;
                    }
                    TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia6 = tL_decryptedMessage.media;
                    tL_photoSize.w = decryptedMessageMedia6.w;
                    tL_photoSize.h = decryptedMessageMedia6.h;
                    tL_photoSize.type = "x";
                    tL_photoSize.size = encryptedFile2.size;
                    tL_photoSize.location = new TLRPC.TL_fileEncryptedLocation();
                    TLRPC.FileLocation fileLocation = tL_photoSize.location;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia7 = tL_decryptedMessage.media;
                    fileLocation.key = decryptedMessageMedia7.key;
                    fileLocation.iv = decryptedMessageMedia7.iv;
                    fileLocation.dc_id = encryptedFile2.dc_id;
                    fileLocation.volume_id = encryptedFile2.id;
                    fileLocation.secret = encryptedFile2.access_hash;
                    fileLocation.local_id = encryptedFile2.key_fingerprint;
                    message.media.photo.sizes.add(tL_photoSize);
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaVideo) {
                    byte[] bArr9 = decryptedMessageMedia.key;
                    if (bArr9 == null || bArr9.length != 32 || (bArr5 = decryptedMessageMedia.iv) == null || bArr5.length != 32) {
                        return null;
                    }
                    message.media = new TLRPC.TL_messageMediaDocument();
                    TLRPC.MessageMedia messageMedia3 = message.media;
                    messageMedia3.flags |= 3;
                    messageMedia3.document = new TLRPC.TL_documentEncrypted();
                    TLRPC.Document document = message.media.document;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia8 = tL_decryptedMessage.media;
                    document.key = decryptedMessageMedia8.key;
                    document.iv = decryptedMessageMedia8.iv;
                    document.dc_id = encryptedFile2.dc_id;
                    String str3 = decryptedMessageMedia8.caption;
                    if (str3 == null) {
                        str3 = "";
                    }
                    message.message = str3;
                    TLRPC.Document document2 = message.media.document;
                    document2.date = i5;
                    document2.size = encryptedFile2.size;
                    document2.id = encryptedFile2.id;
                    document2.access_hash = encryptedFile2.access_hash;
                    document2.mime_type = tL_decryptedMessage.media.mime_type;
                    if (document2.mime_type == null) {
                        document2.mime_type = "video/mp4";
                    }
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia9 = tL_decryptedMessage.media;
                    byte[] bArr10 = ((TLRPC.TL_decryptedMessageMediaVideo) decryptedMessageMedia9).thumb;
                    if (bArr10 == null || bArr10.length == 0 || bArr10.length > 6000 || decryptedMessageMedia9.thumb_w > 100 || decryptedMessageMedia9.thumb_h > 100) {
                        photoSize2 = new TLRPC.TL_photoSizeEmpty();
                        photoSize2.type = "s";
                    } else {
                        photoSize2 = new TLRPC.TL_photoCachedSize();
                        photoSize2.bytes = bArr10;
                        TLRPC.DecryptedMessageMedia decryptedMessageMedia10 = tL_decryptedMessage.media;
                        photoSize2.w = decryptedMessageMedia10.thumb_w;
                        photoSize2.h = decryptedMessageMedia10.thumb_h;
                        photoSize2.type = "s";
                        photoSize2.location = new TLRPC.TL_fileLocationUnavailable();
                    }
                    message.media.document.thumbs.add(photoSize2);
                    message.media.document.flags |= 1;
                    TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo = new TLRPC.TL_documentAttributeVideo();
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia11 = tL_decryptedMessage.media;
                    tL_documentAttributeVideo.w = decryptedMessageMedia11.w;
                    tL_documentAttributeVideo.h = decryptedMessageMedia11.h;
                    tL_documentAttributeVideo.duration = decryptedMessageMedia11.duration;
                    tL_documentAttributeVideo.supports_streaming = false;
                    message.media.document.attributes.add(tL_documentAttributeVideo);
                    int i8 = message.ttl;
                    if (i8 != 0) {
                        TLRPC.MessageMedia messageMedia4 = message.media;
                        messageMedia4.ttl_seconds = i8;
                        messageMedia4.flags |= 4;
                    }
                    int i9 = message.ttl;
                    if (i9 != 0) {
                        message.ttl = Math.max(tL_decryptedMessage.media.duration + 1, i9);
                    }
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaDocument) {
                    byte[] bArr11 = decryptedMessageMedia.key;
                    if (bArr11 == null || bArr11.length != 32 || (bArr4 = decryptedMessageMedia.iv) == null || bArr4.length != 32) {
                        return null;
                    }
                    message.media = new TLRPC.TL_messageMediaDocument();
                    message.media.flags |= 3;
                    String str4 = tL_decryptedMessage.media.caption;
                    if (str4 == null) {
                        str4 = "";
                    }
                    message.message = str4;
                    message.media.document = new TLRPC.TL_documentEncrypted();
                    TLRPC.Document document3 = message.media.document;
                    document3.id = encryptedFile2.id;
                    document3.access_hash = encryptedFile2.access_hash;
                    document3.date = i5;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia12 = tL_decryptedMessage.media;
                    if (decryptedMessageMedia12 instanceof TLRPC.TL_decryptedMessageMediaDocument_layer8) {
                        TLRPC.TL_documentAttributeFilename tL_documentAttributeFilename = new TLRPC.TL_documentAttributeFilename();
                        tL_documentAttributeFilename.file_name = tL_decryptedMessage.media.file_name;
                        message.media.document.attributes.add(tL_documentAttributeFilename);
                    } else {
                        document3.attributes = decryptedMessageMedia12.attributes;
                    }
                    TLRPC.Document document4 = message.media.document;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia13 = tL_decryptedMessage.media;
                    document4.mime_type = decryptedMessageMedia13.mime_type;
                    int i10 = decryptedMessageMedia13.size;
                    document4.size = i10 != 0 ? Math.min(i10, encryptedFile2.size) : encryptedFile2.size;
                    TLRPC.Document document5 = message.media.document;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia14 = tL_decryptedMessage.media;
                    document5.key = decryptedMessageMedia14.key;
                    document5.iv = decryptedMessageMedia14.iv;
                    if (document5.mime_type == null) {
                        document5.mime_type = "";
                    }
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia15 = tL_decryptedMessage.media;
                    byte[] bArr12 = ((TLRPC.TL_decryptedMessageMediaDocument) decryptedMessageMedia15).thumb;
                    if (bArr12 == null || bArr12.length == 0 || bArr12.length > 6000 || decryptedMessageMedia15.thumb_w > 100 || decryptedMessageMedia15.thumb_h > 100) {
                        photoSize = new TLRPC.TL_photoSizeEmpty();
                        photoSize.type = "s";
                    } else {
                        photoSize = new TLRPC.TL_photoCachedSize();
                        photoSize.bytes = bArr12;
                        TLRPC.DecryptedMessageMedia decryptedMessageMedia16 = tL_decryptedMessage.media;
                        photoSize.w = decryptedMessageMedia16.thumb_w;
                        photoSize.h = decryptedMessageMedia16.thumb_h;
                        photoSize.type = "s";
                        photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                    }
                    message.media.document.thumbs.add(photoSize);
                    TLRPC.Document document6 = message.media.document;
                    document6.flags |= 1;
                    document6.dc_id = encryptedFile2.dc_id;
                    if (MessageObject.isVoiceMessage(message) || MessageObject.isRoundVideoMessage(message)) {
                        message.media_unread = true;
                    }
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaExternalDocument) {
                    message.media = new TLRPC.TL_messageMediaDocument();
                    TLRPC.MessageMedia messageMedia5 = message.media;
                    messageMedia5.flags |= 3;
                    message.message = "";
                    messageMedia5.document = new TLRPC.TL_document();
                    TLRPC.Document document7 = message.media.document;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia17 = tL_decryptedMessage.media;
                    document7.id = decryptedMessageMedia17.id;
                    document7.access_hash = decryptedMessageMedia17.access_hash;
                    document7.file_reference = new byte[0];
                    document7.date = decryptedMessageMedia17.date;
                    document7.attributes = decryptedMessageMedia17.attributes;
                    document7.mime_type = decryptedMessageMedia17.mime_type;
                    document7.dc_id = decryptedMessageMedia17.dc_id;
                    document7.size = decryptedMessageMedia17.size;
                    document7.thumbs.add(((TLRPC.TL_decryptedMessageMediaExternalDocument) decryptedMessageMedia17).thumb);
                    TLRPC.Document document8 = message.media.document;
                    document8.flags |= 1;
                    if (document8.mime_type == null) {
                        document8.mime_type = "";
                    }
                } else if (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaAudio) {
                    byte[] bArr13 = decryptedMessageMedia.key;
                    if (bArr13 == null || bArr13.length != 32 || (bArr3 = decryptedMessageMedia.iv) == null || bArr3.length != 32) {
                        return null;
                    }
                    message.media = new TLRPC.TL_messageMediaDocument();
                    TLRPC.MessageMedia messageMedia6 = message.media;
                    messageMedia6.flags |= 3;
                    messageMedia6.document = new TLRPC.TL_documentEncrypted();
                    TLRPC.Document document9 = message.media.document;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia18 = tL_decryptedMessage.media;
                    document9.key = decryptedMessageMedia18.key;
                    document9.iv = decryptedMessageMedia18.iv;
                    document9.id = encryptedFile2.id;
                    document9.access_hash = encryptedFile2.access_hash;
                    document9.date = i5;
                    document9.size = encryptedFile2.size;
                    document9.dc_id = encryptedFile2.dc_id;
                    document9.mime_type = decryptedMessageMedia18.mime_type;
                    String str5 = decryptedMessageMedia18.caption;
                    if (str5 == null) {
                        str5 = "";
                    }
                    message.message = str5;
                    TLRPC.Document document10 = message.media.document;
                    if (document10.mime_type == null) {
                        document10.mime_type = "audio/ogg";
                    }
                    TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
                    tL_documentAttributeAudio.duration = tL_decryptedMessage.media.duration;
                    tL_documentAttributeAudio.voice = true;
                    message.media.document.attributes.add(tL_documentAttributeAudio);
                    int i11 = message.ttl;
                    if (i11 != 0) {
                        message.ttl = Math.max(tL_decryptedMessage.media.duration + 1, i11);
                    }
                    if (message.media.document.thumbs.isEmpty()) {
                        TLRPC.TL_photoSizeEmpty tL_photoSizeEmpty = new TLRPC.TL_photoSizeEmpty();
                        tL_photoSizeEmpty.type = "s";
                        message.media.document.thumbs.add(tL_photoSizeEmpty);
                    }
                } else if (!(decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaVenue)) {
                    return null;
                } else {
                    message.media = new TLRPC.TL_messageMediaVenue();
                    message.media.geo = new TLRPC.TL_geoPoint();
                    TLRPC.MessageMedia messageMedia7 = message.media;
                    TLRPC.GeoPoint geoPoint2 = messageMedia7.geo;
                    TLRPC.DecryptedMessageMedia decryptedMessageMedia19 = tL_decryptedMessage.media;
                    geoPoint2.lat = decryptedMessageMedia19.lat;
                    geoPoint2._long = decryptedMessageMedia19._long;
                    messageMedia7.title = decryptedMessageMedia19.title;
                    messageMedia7.address = decryptedMessageMedia19.address;
                    messageMedia7.provider = decryptedMessageMedia19.provider;
                    messageMedia7.venue_id = decryptedMessageMedia19.venue_id;
                    messageMedia7.venue_type = "";
                }
                int i12 = message.ttl;
                if (i12 != 0) {
                    TLRPC.MessageMedia messageMedia8 = message.media;
                    if (messageMedia8.ttl_seconds == 0) {
                        messageMedia8.ttl_seconds = i12;
                        messageMedia8.flags |= 4;
                    }
                }
                String str6 = message.message;
                if (str6 != null) {
                    message.message = str6.replace(8238, ' ');
                }
                return message;
            } else if (tLObject2 instanceof TLRPC.TL_decryptedMessageService) {
                TLRPC.TL_decryptedMessageService tL_decryptedMessageService = (TLRPC.TL_decryptedMessageService) tLObject2;
                TLRPC.DecryptedMessageAction decryptedMessageAction = tL_decryptedMessageService.action;
                if ((decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) || (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
                    TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
                    TLRPC.DecryptedMessageAction decryptedMessageAction2 = tL_decryptedMessageService.action;
                    if (decryptedMessageAction2 instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                        tL_messageService.action = new TLRPC.TL_messageEncryptedAction();
                        int i13 = tL_decryptedMessageService.action.ttl_seconds;
                        if (i13 < 0 || i13 > 31536000) {
                            tL_decryptedMessageService.action.ttl_seconds = 31536000;
                        }
                        TLRPC.DecryptedMessageAction decryptedMessageAction3 = tL_decryptedMessageService.action;
                        encryptedChat2.ttl = decryptedMessageAction3.ttl_seconds;
                        tL_messageService.action.encryptedAction = decryptedMessageAction3;
                        getMessagesStorage().updateEncryptedChatTTL(encryptedChat2);
                    } else if (decryptedMessageAction2 instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                        tL_messageService.action = new TLRPC.TL_messageEncryptedAction();
                        tL_messageService.action.encryptedAction = tL_decryptedMessageService.action;
                    }
                    int newMessageId2 = getUserConfig().getNewMessageId();
                    tL_messageService.id = newMessageId2;
                    tL_messageService.local_id = newMessageId2;
                    getUserConfig().saveConfig(false);
                    tL_messageService.unread = true;
                    tL_messageService.flags = 256;
                    tL_messageService.date = i5;
                    tL_messageService.from_id = i6;
                    tL_messageService.to_id = new TLRPC.TL_peerUser();
                    tL_messageService.to_id.user_id = getUserConfig().getClientUserId();
                    tL_messageService.dialog_id = ((long) encryptedChat2.id) << 32;
                    return tL_messageService;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                    AndroidUtilities.runOnUIThread(new Runnable(((long) encryptedChat2.id) << 32) {
                        private final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.this.lambda$processDecryptedObject$11$SecretChatHelper(this.f$1);
                        }
                    });
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                    if (decryptedMessageAction.random_ids.isEmpty()) {
                        return null;
                    }
                    this.pendingEncMessagesToDelete.addAll(tL_decryptedMessageService.action.random_ids);
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                    if (decryptedMessageAction.random_ids.isEmpty()) {
                        return null;
                    }
                    int currentTime = getConnectionsManager().getCurrentTime();
                    getMessagesStorage().createTaskForSecretChat(encryptedChat2.id, currentTime, currentTime, 1, tL_decryptedMessageService.action.random_ids);
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                    applyPeerLayer(encryptedChat2, decryptedMessageAction.layer);
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                    long j3 = encryptedChat2.exchange_id;
                    if (j3 != 0) {
                        if (j3 <= decryptedMessageAction.exchange_id) {
                            sendAbortKeyMessage(encryptedChat2, (TLRPC.Message) null, j3);
                        } else if (!BuildVars.LOGS_ENABLED) {
                            return null;
                        } else {
                            FileLog.d("we already have request key with higher exchange_id");
                            return null;
                        }
                    }
                    byte[] bArr14 = new byte[256];
                    Utilities.random.nextBytes(bArr14);
                    BigInteger bigInteger = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                    BigInteger modPow = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr14), bigInteger);
                    BigInteger bigInteger2 = new BigInteger(1, tL_decryptedMessageService.action.g_a);
                    if (!Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
                        sendAbortKeyMessage(encryptedChat2, (TLRPC.Message) null, tL_decryptedMessageService.action.exchange_id);
                        return null;
                    }
                    byte[] byteArray = modPow.toByteArray();
                    if (byteArray.length > 256) {
                        byte[] bArr15 = new byte[256];
                        System.arraycopy(byteArray, 1, bArr15, 0, 256);
                        byteArray = bArr15;
                    }
                    byte[] byteArray2 = bigInteger2.modPow(new BigInteger(1, bArr14), bigInteger).toByteArray();
                    if (byteArray2.length > 256) {
                        bArr2 = new byte[256];
                        System.arraycopy(byteArray2, byteArray2.length - 256, bArr2, 0, 256);
                    } else if (byteArray2.length < 256) {
                        bArr2 = new byte[256];
                        System.arraycopy(byteArray2, 0, bArr2, 256 - byteArray2.length, byteArray2.length);
                        for (int i14 = 0; i14 < 256 - byteArray2.length; i14++) {
                            bArr2[i14] = 0;
                        }
                    } else {
                        bArr2 = byteArray2;
                    }
                    byte[] computeSHA1 = Utilities.computeSHA1(bArr2);
                    byte[] bArr16 = new byte[8];
                    System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr16, 0, 8);
                    encryptedChat2.exchange_id = tL_decryptedMessageService.action.exchange_id;
                    encryptedChat2.future_auth_key = bArr2;
                    encryptedChat2.future_key_fingerprint = Utilities.bytesToLong(bArr16);
                    encryptedChat2.g_a_or_b = byteArray;
                    getMessagesStorage().updateEncryptedChat(encryptedChat2);
                    sendAcceptKeyMessage(encryptedChat2, (TLRPC.Message) null);
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                    if (encryptedChat2.exchange_id == decryptedMessageAction.exchange_id) {
                        BigInteger bigInteger3 = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                        BigInteger bigInteger4 = new BigInteger(1, tL_decryptedMessageService.action.g_b);
                        if (!Utilities.isGoodGaAndGb(bigInteger4, bigInteger3)) {
                            encryptedChat2.future_auth_key = new byte[256];
                            encryptedChat2.future_key_fingerprint = 0;
                            encryptedChat2.exchange_id = 0;
                            getMessagesStorage().updateEncryptedChat(encryptedChat2);
                            sendAbortKeyMessage(encryptedChat2, (TLRPC.Message) null, tL_decryptedMessageService.action.exchange_id);
                            return null;
                        }
                        byte[] byteArray3 = bigInteger4.modPow(new BigInteger(1, encryptedChat2.a_or_b), bigInteger3).toByteArray();
                        if (byteArray3.length > 256) {
                            bArr = new byte[256];
                            System.arraycopy(byteArray3, byteArray3.length - 256, bArr, 0, 256);
                        } else if (byteArray3.length < 256) {
                            bArr = new byte[256];
                            System.arraycopy(byteArray3, 0, bArr, 256 - byteArray3.length, byteArray3.length);
                            for (int i15 = 0; i15 < 256 - byteArray3.length; i15++) {
                                bArr[i15] = 0;
                            }
                        } else {
                            bArr = byteArray3;
                        }
                        byte[] computeSHA12 = Utilities.computeSHA1(bArr);
                        byte[] bArr17 = new byte[8];
                        System.arraycopy(computeSHA12, computeSHA12.length - 8, bArr17, 0, 8);
                        long bytesToLong = Utilities.bytesToLong(bArr17);
                        if (tL_decryptedMessageService.action.key_fingerprint == bytesToLong) {
                            encryptedChat2.future_auth_key = bArr;
                            encryptedChat2.future_key_fingerprint = bytesToLong;
                            getMessagesStorage().updateEncryptedChat(encryptedChat2);
                            sendCommitKeyMessage(encryptedChat2, (TLRPC.Message) null);
                            return null;
                        }
                        encryptedChat2.future_auth_key = new byte[256];
                        encryptedChat2.future_key_fingerprint = 0;
                        encryptedChat2.exchange_id = 0;
                        getMessagesStorage().updateEncryptedChat(encryptedChat2);
                        sendAbortKeyMessage(encryptedChat2, (TLRPC.Message) null, tL_decryptedMessageService.action.exchange_id);
                        return null;
                    }
                    encryptedChat2.future_auth_key = new byte[256];
                    encryptedChat2.future_key_fingerprint = 0;
                    encryptedChat2.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat2);
                    sendAbortKeyMessage(encryptedChat2, (TLRPC.Message) null, tL_decryptedMessageService.action.exchange_id);
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                    if (encryptedChat2.exchange_id == decryptedMessageAction.exchange_id) {
                        long j4 = encryptedChat2.future_key_fingerprint;
                        if (j4 == decryptedMessageAction.key_fingerprint) {
                            long j5 = encryptedChat2.key_fingerprint;
                            byte[] bArr18 = encryptedChat2.auth_key;
                            encryptedChat2.key_fingerprint = j4;
                            encryptedChat2.auth_key = encryptedChat2.future_auth_key;
                            encryptedChat2.key_create_date = getConnectionsManager().getCurrentTime();
                            encryptedChat2.future_auth_key = bArr18;
                            encryptedChat2.future_key_fingerprint = j5;
                            encryptedChat2.key_use_count_in = 0;
                            encryptedChat2.key_use_count_out = 0;
                            encryptedChat2.exchange_id = 0;
                            getMessagesStorage().updateEncryptedChat(encryptedChat2);
                            sendNoopMessage(encryptedChat2, (TLRPC.Message) null);
                            return null;
                        }
                    }
                    encryptedChat2.future_auth_key = new byte[256];
                    encryptedChat2.future_key_fingerprint = 0;
                    encryptedChat2.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat2);
                    sendAbortKeyMessage(encryptedChat2, (TLRPC.Message) null, tL_decryptedMessageService.action.exchange_id);
                    return null;
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                    if (encryptedChat2.exchange_id != decryptedMessageAction.exchange_id) {
                        return null;
                    }
                    encryptedChat2.future_auth_key = new byte[256];
                    encryptedChat2.future_key_fingerprint = 0;
                    encryptedChat2.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat2);
                    return null;
                } else if ((decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionNoop) || !(decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionResend) || (i2 = decryptedMessageAction.end_seq_no) < (i3 = encryptedChat2.in_seq_no) || i2 < (i4 = decryptedMessageAction.start_seq_no)) {
                    return null;
                } else {
                    if (i4 < i3) {
                        decryptedMessageAction.start_seq_no = i3;
                    }
                    TLRPC.DecryptedMessageAction decryptedMessageAction4 = tL_decryptedMessageService.action;
                    resendMessages(decryptedMessageAction4.start_seq_no, decryptedMessageAction4.end_seq_no, encryptedChat2);
                    return null;
                }
            } else if (!BuildVars.LOGS_ENABLED) {
                return null;
            } else {
                FileLog.e("unknown message " + tLObject2);
                return null;
            }
        } else if (!BuildVars.LOGS_ENABLED) {
            return null;
        } else {
            FileLog.e("unknown TLObject");
            return null;
        }
    }

    public /* synthetic */ void lambda$processDecryptedObject$11$SecretChatHelper(long j) {
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(j);
        if (dialog != null) {
            dialog.unread_count = 0;
            getMessagesController().dialogMessage.remove(dialog.id);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$10$SecretChatHelper(this.f$1);
            }
        });
        getMessagesStorage().deleteDialog(j, 1);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), false);
    }

    public /* synthetic */ void lambda$null$10$SecretChatHelper(long j) {
        AndroidUtilities.runOnUIThread(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$9$SecretChatHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$SecretChatHelper(long j) {
        getNotificationsController().processReadMessages((SparseLongArray) null, j, 0, Integer.MAX_VALUE, false);
        LongSparseArray longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j, 0);
        getNotificationsController().processDialogsUpdateRead(longSparseArray);
    }

    private TLRPC.Message createDeleteMessage(int i, int i2, int i3, long j, TLRPC.EncryptedChat encryptedChat) {
        TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
        tL_messageService.action = new TLRPC.TL_messageEncryptedAction();
        tL_messageService.action.encryptedAction = new TLRPC.TL_decryptedMessageActionDeleteMessages();
        tL_messageService.action.encryptedAction.random_ids.add(Long.valueOf(j));
        tL_messageService.id = i;
        tL_messageService.local_id = i;
        tL_messageService.from_id = getUserConfig().getClientUserId();
        tL_messageService.unread = true;
        tL_messageService.out = true;
        tL_messageService.flags = 256;
        tL_messageService.dialog_id = ((long) encryptedChat.id) << 32;
        tL_messageService.to_id = new TLRPC.TL_peerUser();
        tL_messageService.send_state = 1;
        tL_messageService.seq_in = i3;
        tL_messageService.seq_out = i2;
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            tL_messageService.to_id.user_id = encryptedChat.admin_id;
        } else {
            tL_messageService.to_id.user_id = encryptedChat.participant_id;
        }
        tL_messageService.date = 0;
        tL_messageService.random_id = j;
        return tL_messageService;
    }

    private void resendMessages(int i, int i2, TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat != null && i2 - i >= 0) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, encryptedChat, i2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.EncryptedChat f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$resendMessages$14$SecretChatHelper(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$resendMessages$14$SecretChatHelper(int i, TLRPC.EncryptedChat encryptedChat, int i2) {
        long j;
        ArrayList arrayList;
        TLRPC.Message message;
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        try {
            int i3 = (encryptedChat2.admin_id == getUserConfig().getClientUserId() && i % 2 == 0) ? i + 1 : i;
            int i4 = 5;
            int i5 = 1;
            int i6 = 2;
            int i7 = 3;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(encryptedChat2.id), Integer.valueOf(i3), Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i2)}), new Object[0]);
            boolean next = queryFinalized.next();
            queryFinalized.dispose();
            if (!next) {
                long j2 = ((long) encryptedChat2.id) << 32;
                SparseArray sparseArray = new SparseArray();
                ArrayList arrayList2 = new ArrayList();
                int i8 = i2;
                for (int i9 = i3; i9 <= i8; i9 += 2) {
                    sparseArray.put(i9, (Object) null);
                }
                SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms as r ON r.mid = s.mid LEFT JOIN messages as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(j2), Integer.valueOf(i3), Integer.valueOf(i2)}), new Object[0]);
                while (queryFinalized2.next()) {
                    long longValue = queryFinalized2.longValue(i5);
                    if (longValue == 0) {
                        longValue = Utilities.random.nextLong();
                    }
                    long j3 = longValue;
                    int intValue = queryFinalized2.intValue(i6);
                    int intValue2 = queryFinalized2.intValue(i7);
                    int intValue3 = queryFinalized2.intValue(i4);
                    NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                        byteBufferValue.reuse();
                        TLdeserialize.random_id = j3;
                        TLdeserialize.dialog_id = j2;
                        TLdeserialize.seq_in = intValue;
                        TLdeserialize.seq_out = intValue2;
                        TLdeserialize.ttl = queryFinalized2.intValue(4);
                        j = j2;
                        message = TLdeserialize;
                        arrayList = arrayList2;
                    } else {
                        j = j2;
                        arrayList = arrayList2;
                        message = createDeleteMessage(intValue3, intValue2, intValue, j3, encryptedChat);
                    }
                    arrayList.add(message);
                    sparseArray.remove(intValue2);
                    int i10 = i2;
                    arrayList2 = arrayList;
                    j2 = j;
                    i4 = 5;
                    i5 = 1;
                    i6 = 2;
                    i7 = 3;
                }
                ArrayList arrayList3 = arrayList2;
                queryFinalized2.dispose();
                if (sparseArray.size() != 0) {
                    for (int i11 = 0; i11 < sparseArray.size(); i11++) {
                        int keyAt = sparseArray.keyAt(i11);
                        arrayList3.add(createDeleteMessage(getUserConfig().getNewMessageId(), keyAt, keyAt + 1, Utilities.random.nextLong(), encryptedChat));
                    }
                    getUserConfig().saveConfig(false);
                }
                Collections.sort(arrayList3, $$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc.INSTANCE);
                ArrayList arrayList4 = new ArrayList();
                arrayList4.add(encryptedChat2);
                try {
                    AndroidUtilities.runOnUIThread(new Runnable(arrayList3) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.this.lambda$null$13$SecretChatHelper(this.f$1);
                        }
                    });
                    getSendMessagesHelper().processUnsentMessages(arrayList3, (ArrayList<TLRPC.Message>) null, new ArrayList(), new ArrayList(), arrayList4);
                    getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(encryptedChat2.id), Integer.valueOf(i3), Integer.valueOf(i2)})).stepThis().dispose();
                } catch (Exception e) {
                    e = e;
                }
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$13$SecretChatHelper(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) arrayList.get(i), false);
            messageObject.resendAsIs = true;
            getSendMessagesHelper().retrySendMessage(messageObject, true);
        }
    }

    public void checkSecretHoles(TLRPC.EncryptedChat encryptedChat, ArrayList<TLRPC.Message> arrayList) {
        ArrayList arrayList2 = this.secretHolesQueue.get(encryptedChat.id);
        if (arrayList2 != null) {
            Collections.sort(arrayList2, $$Lambda$SecretChatHelper$XChl_gDRHQHDfwtxghrPUY1XhL4.INSTANCE);
            boolean z = false;
            while (arrayList2.size() > 0) {
                TL_decryptedMessageHolder tL_decryptedMessageHolder = (TL_decryptedMessageHolder) arrayList2.get(0);
                int i = tL_decryptedMessageHolder.layer.out_seq_no;
                int i2 = encryptedChat.seq_in;
                if (i != i2 && i2 != i - 2) {
                    break;
                }
                applyPeerLayer(encryptedChat, tL_decryptedMessageHolder.layer.layer);
                TLRPC.TL_decryptedMessageLayer tL_decryptedMessageLayer = tL_decryptedMessageHolder.layer;
                encryptedChat.seq_in = tL_decryptedMessageLayer.out_seq_no;
                encryptedChat.in_seq_no = tL_decryptedMessageLayer.in_seq_no;
                arrayList2.remove(0);
                if (tL_decryptedMessageHolder.decryptedWithVersion == 2) {
                    encryptedChat.mtproto_seq = Math.min(encryptedChat.mtproto_seq, encryptedChat.seq_in);
                }
                TLRPC.Message processDecryptedObject = processDecryptedObject(encryptedChat, tL_decryptedMessageHolder.file, tL_decryptedMessageHolder.date, tL_decryptedMessageHolder.layer.message, tL_decryptedMessageHolder.new_key_used);
                if (processDecryptedObject != null) {
                    arrayList.add(processDecryptedObject);
                }
                z = true;
            }
            if (arrayList2.isEmpty()) {
                this.secretHolesQueue.remove(encryptedChat.id);
            }
            if (z) {
                getMessagesStorage().updateEncryptedChatSeq(encryptedChat, true);
            }
        }
    }

    static /* synthetic */ int lambda$checkSecretHoles$15(TL_decryptedMessageHolder tL_decryptedMessageHolder, TL_decryptedMessageHolder tL_decryptedMessageHolder2) {
        int i = tL_decryptedMessageHolder.layer.out_seq_no;
        int i2 = tL_decryptedMessageHolder2.layer.out_seq_no;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    private boolean decryptWithMtProtoVersion(NativeByteBuffer nativeByteBuffer, byte[] bArr, byte[] bArr2, int i, boolean z, boolean z2) {
        NativeByteBuffer nativeByteBuffer2 = nativeByteBuffer;
        byte[] bArr3 = bArr2;
        int i2 = i;
        byte[] bArr4 = bArr;
        boolean z3 = i2 == 1 ? false : z;
        MessageKeyData generateMessageKeyData = MessageKeyData.generateMessageKeyData(bArr4, bArr3, z3, i2);
        Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, false, false, 24, nativeByteBuffer.limit() - 24);
        int readInt32 = nativeByteBuffer2.readInt32(false);
        if (i2 == 2) {
            int i3 = z3 ? 8 : 0;
            ByteBuffer byteBuffer = nativeByteBuffer2.buffer;
            if (!Utilities.arraysEquals(bArr3, 0, Utilities.computeSHA256(bArr, i3 + 88, 32, byteBuffer, 24, byteBuffer.limit()), 8)) {
                if (z2) {
                    Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, true, false, 24, nativeByteBuffer.limit() - 24);
                    nativeByteBuffer2.position(24);
                }
                return false;
            }
        } else {
            int i4 = readInt32 + 28;
            if (i4 < nativeByteBuffer2.buffer.limit() - 15 || i4 > nativeByteBuffer2.buffer.limit()) {
                i4 = nativeByteBuffer2.buffer.limit();
            }
            byte[] computeSHA1 = Utilities.computeSHA1(nativeByteBuffer2.buffer, 24, i4);
            if (!Utilities.arraysEquals(bArr3, 0, computeSHA1, computeSHA1.length - 16)) {
                if (z2) {
                    Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, true, false, 24, nativeByteBuffer.limit() - 24);
                    nativeByteBuffer2.position(24);
                }
                return false;
            }
        }
        if (readInt32 <= 0 || readInt32 > nativeByteBuffer.limit() - 28) {
            return false;
        }
        int limit = (nativeByteBuffer.limit() - 28) - readInt32;
        if ((i2 != 2 || (limit >= 12 && limit <= 1024)) && (i2 != 1 || limit <= 15)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x026e A[Catch:{ Exception -> 0x0288 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0080 A[Catch:{ Exception -> 0x0288 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082 A[Catch:{ Exception -> 0x0288 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0085 A[Catch:{ Exception -> 0x0288 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> decryptMessage(org.telegram.tgnet.TLRPC.EncryptedMessage r22) {
        /*
            r21 = this;
            r8 = r21
            r0 = r22
            java.lang.String r9 = " out_seq = "
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()
            int r2 = r0.chat_id
            r10 = 1
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r1.getEncryptedChatDB(r2, r10)
            r12 = 0
            if (r11 == 0) goto L_0x028c
            boolean r1 = r11 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r1 == 0) goto L_0x001a
            goto L_0x028c
        L_0x001a:
            boolean r1 = r11 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting     // Catch:{ Exception -> 0x0288 }
            if (r1 == 0) goto L_0x0041
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Update>> r1 = r8.pendingSecretMessages     // Catch:{ Exception -> 0x0288 }
            int r2 = r11.id     // Catch:{ Exception -> 0x0288 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0288 }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ Exception -> 0x0288 }
            if (r1 != 0) goto L_0x0036
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0288 }
            r1.<init>()     // Catch:{ Exception -> 0x0288 }
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Update>> r2 = r8.pendingSecretMessages     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.id     // Catch:{ Exception -> 0x0288 }
            r2.put(r3, r1)     // Catch:{ Exception -> 0x0288 }
        L_0x0036:
            org.telegram.tgnet.TLRPC$TL_updateNewEncryptedMessage r2 = new org.telegram.tgnet.TLRPC$TL_updateNewEncryptedMessage     // Catch:{ Exception -> 0x0288 }
            r2.<init>()     // Catch:{ Exception -> 0x0288 }
            r2.message = r0     // Catch:{ Exception -> 0x0288 }
            r1.add(r2)     // Catch:{ Exception -> 0x0288 }
            return r12
        L_0x0041:
            org.telegram.tgnet.NativeByteBuffer r13 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0288 }
            byte[] r1 = r0.bytes     // Catch:{ Exception -> 0x0288 }
            int r1 = r1.length     // Catch:{ Exception -> 0x0288 }
            r13.<init>((int) r1)     // Catch:{ Exception -> 0x0288 }
            byte[] r1 = r0.bytes     // Catch:{ Exception -> 0x0288 }
            r13.writeBytes((byte[]) r1)     // Catch:{ Exception -> 0x0288 }
            r14 = 0
            r13.position(r14)     // Catch:{ Exception -> 0x0288 }
            long r1 = r13.readInt64(r14)     // Catch:{ Exception -> 0x0288 }
            long r3 = r11.key_fingerprint     // Catch:{ Exception -> 0x0288 }
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 != 0) goto L_0x0060
            byte[] r3 = r11.auth_key     // Catch:{ Exception -> 0x0288 }
            r15 = r3
            goto L_0x0074
        L_0x0060:
            long r3 = r11.future_key_fingerprint     // Catch:{ Exception -> 0x0288 }
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0073
            long r3 = r11.future_key_fingerprint     // Catch:{ Exception -> 0x0288 }
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 != 0) goto L_0x0073
            byte[] r3 = r11.future_auth_key     // Catch:{ Exception -> 0x0288 }
            r15 = r3
            r7 = 1
            goto L_0x0075
        L_0x0073:
            r15 = r12
        L_0x0074:
            r7 = 0
        L_0x0075:
            int r3 = r11.layer     // Catch:{ Exception -> 0x0288 }
            int r3 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r3)     // Catch:{ Exception -> 0x0288 }
            r4 = 73
            r6 = 2
            if (r3 < r4) goto L_0x0082
            r5 = 2
            goto L_0x0083
        L_0x0082:
            r5 = 1
        L_0x0083:
            if (r15 == 0) goto L_0x026e
            r1 = 16
            byte[] r16 = r13.readData(r1, r14)     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.admin_id     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.UserConfig r2 = r21.getUserConfig()     // Catch:{ Exception -> 0x0288 }
            int r2 = r2.getClientUserId()     // Catch:{ Exception -> 0x0288 }
            if (r1 != r2) goto L_0x009a
            r17 = 1
            goto L_0x009c
        L_0x009a:
            r17 = 0
        L_0x009c:
            if (r5 != r6) goto L_0x00a5
            int r1 = r11.mtproto_seq     // Catch:{ Exception -> 0x0288 }
            if (r1 == 0) goto L_0x00a5
            r18 = 0
            goto L_0x00a7
        L_0x00a5:
            r18 = 1
        L_0x00a7:
            r1 = r21
            r2 = r13
            r3 = r15
            r4 = r16
            r19 = r5
            r10 = 2
            r6 = r17
            r20 = r7
            r7 = r18
            boolean r1 = r1.decryptWithMtProtoVersion(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0288 }
            r6 = r19
            if (r1 != 0) goto L_0x00e9
            if (r6 != r10) goto L_0x00d6
            if (r18 == 0) goto L_0x00d5
            r5 = 1
            r7 = 0
            r1 = r21
            r2 = r13
            r3 = r15
            r4 = r16
            r6 = r17
            boolean r1 = r1.decryptWithMtProtoVersion(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0288 }
            if (r1 != 0) goto L_0x00d3
            goto L_0x00d5
        L_0x00d3:
            r6 = 1
            goto L_0x00e9
        L_0x00d5:
            return r12
        L_0x00d6:
            r5 = 2
            r1 = r21
            r2 = r13
            r3 = r15
            r4 = r16
            r6 = r17
            r7 = r18
            boolean r1 = r1.decryptWithMtProtoVersion(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0288 }
            if (r1 != 0) goto L_0x00e8
            return r12
        L_0x00e8:
            r6 = 2
        L_0x00e9:
            org.telegram.tgnet.TLClassStore r1 = org.telegram.tgnet.TLClassStore.Instance()     // Catch:{ Exception -> 0x0288 }
            int r2 = r13.readInt32(r14)     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLObject r1 = r1.TLdeserialize(r13, r2, r14)     // Catch:{ Exception -> 0x0288 }
            r13.reuse()     // Catch:{ Exception -> 0x0288 }
            r14 = r20
            if (r14 != 0) goto L_0x010d
            int r2 = r11.layer     // Catch:{ Exception -> 0x0288 }
            int r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2)     // Catch:{ Exception -> 0x0288 }
            r3 = 20
            if (r2 < r3) goto L_0x010d
            short r2 = r11.key_use_count_in     // Catch:{ Exception -> 0x0288 }
            r3 = 1
            int r2 = r2 + r3
            short r2 = (short) r2     // Catch:{ Exception -> 0x0288 }
            r11.key_use_count_in = r2     // Catch:{ Exception -> 0x0288 }
        L_0x010d:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer) r1     // Catch:{ Exception -> 0x0288 }
            int r2 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            if (r2 != 0) goto L_0x0131
            int r2 = r11.seq_out     // Catch:{ Exception -> 0x0288 }
            if (r2 != 0) goto L_0x0131
            int r2 = r11.admin_id     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.UserConfig r3 = r21.getUserConfig()     // Catch:{ Exception -> 0x0288 }
            int r3 = r3.getClientUserId()     // Catch:{ Exception -> 0x0288 }
            if (r2 != r3) goto L_0x012e
            r2 = 1
            r11.seq_out = r2     // Catch:{ Exception -> 0x0288 }
            r2 = -2
            r11.seq_in = r2     // Catch:{ Exception -> 0x0288 }
            goto L_0x0131
        L_0x012e:
            r2 = -1
            r11.seq_in = r2     // Catch:{ Exception -> 0x0288 }
        L_0x0131:
            byte[] r2 = r1.random_bytes     // Catch:{ Exception -> 0x0288 }
            int r2 = r2.length     // Catch:{ Exception -> 0x0288 }
            r3 = 15
            if (r2 >= r3) goto L_0x0142
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0288 }
            if (r0 == 0) goto L_0x0141
            java.lang.String r0 = "got random bytes less than needed"
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0288 }
        L_0x0141:
            return r12
        L_0x0142:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x0182
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0288 }
            r2.<init>()     // Catch:{ Exception -> 0x0288 }
            java.lang.String r3 = "current chat in_seq = "
            r2.append(r3)     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            r2.append(r3)     // Catch:{ Exception -> 0x0288 }
            r2.append(r9)     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.seq_out     // Catch:{ Exception -> 0x0288 }
            r2.append(r3)     // Catch:{ Exception -> 0x0288 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0288 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0288 }
            r2.<init>()     // Catch:{ Exception -> 0x0288 }
            java.lang.String r3 = "got message with in_seq = "
            r2.append(r3)     // Catch:{ Exception -> 0x0288 }
            int r3 = r1.in_seq_no     // Catch:{ Exception -> 0x0288 }
            r2.append(r3)     // Catch:{ Exception -> 0x0288 }
            r2.append(r9)     // Catch:{ Exception -> 0x0288 }
            int r3 = r1.out_seq_no     // Catch:{ Exception -> 0x0288 }
            r2.append(r3)     // Catch:{ Exception -> 0x0288 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0288 }
        L_0x0182:
            int r2 = r1.out_seq_no     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            if (r2 > r3) goto L_0x0189
            return r12
        L_0x0189:
            r2 = 1
            if (r6 != r2) goto L_0x0197
            int r2 = r11.mtproto_seq     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x0197
            int r2 = r1.out_seq_no     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.mtproto_seq     // Catch:{ Exception -> 0x0288 }
            if (r2 < r3) goto L_0x0197
            return r12
        L_0x0197:
            int r2 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            int r3 = r1.out_seq_no     // Catch:{ Exception -> 0x0288 }
            int r3 = r3 - r10
            if (r2 == r3) goto L_0x0220
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x01a7
            java.lang.String r2 = "got hole"
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ Exception -> 0x0288 }
        L_0x01a7:
            int r2 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            int r2 = r2 + r10
            int r3 = r1.out_seq_no     // Catch:{ Exception -> 0x0288 }
            int r3 = r3 - r10
            r8.sendResendMessage(r11, r2, r3, r12)     // Catch:{ Exception -> 0x0288 }
            android.util.SparseArray<java.util.ArrayList<org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r2 = r8.secretHolesQueue     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.id     // Catch:{ Exception -> 0x0288 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0288 }
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch:{ Exception -> 0x0288 }
            if (r2 != 0) goto L_0x01c8
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0288 }
            r2.<init>()     // Catch:{ Exception -> 0x0288 }
            android.util.SparseArray<java.util.ArrayList<org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r3 = r8.secretHolesQueue     // Catch:{ Exception -> 0x0288 }
            int r4 = r11.id     // Catch:{ Exception -> 0x0288 }
            r3.put(r4, r2)     // Catch:{ Exception -> 0x0288 }
        L_0x01c8:
            int r3 = r2.size()     // Catch:{ Exception -> 0x0288 }
            r4 = 4
            if (r3 < r4) goto L_0x0209
            android.util.SparseArray<java.util.ArrayList<org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r0 = r8.secretHolesQueue     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.id     // Catch:{ Exception -> 0x0288 }
            r0.remove(r1)     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded r0 = new org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded     // Catch:{ Exception -> 0x0288 }
            r0.<init>()     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.id     // Catch:{ Exception -> 0x0288 }
            r0.id = r1     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.user_id     // Catch:{ Exception -> 0x0288 }
            r0.user_id = r1     // Catch:{ Exception -> 0x0288 }
            byte[] r1 = r11.auth_key     // Catch:{ Exception -> 0x0288 }
            r0.auth_key = r1     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.key_create_date     // Catch:{ Exception -> 0x0288 }
            r0.key_create_date = r1     // Catch:{ Exception -> 0x0288 }
            short r1 = r11.key_use_count_in     // Catch:{ Exception -> 0x0288 }
            r0.key_use_count_in = r1     // Catch:{ Exception -> 0x0288 }
            short r1 = r11.key_use_count_out     // Catch:{ Exception -> 0x0288 }
            r0.key_use_count_out = r1     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            r0.seq_in = r1     // Catch:{ Exception -> 0x0288 }
            int r1 = r11.seq_out     // Catch:{ Exception -> 0x0288 }
            r0.seq_out = r1     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.-$$Lambda$SecretChatHelper$y_-QKcAzTtKUDu-WddTe8KbVDxY r1 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$y_-QKcAzTtKUDu-WddTe8KbVDxY     // Catch:{ Exception -> 0x0288 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x0288 }
            int r0 = r11.id     // Catch:{ Exception -> 0x0288 }
            r8.declineSecretChat(r0)     // Catch:{ Exception -> 0x0288 }
            return r12
        L_0x0209:
            org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder r3 = new org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder     // Catch:{ Exception -> 0x0288 }
            r3.<init>()     // Catch:{ Exception -> 0x0288 }
            r3.layer = r1     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLRPC$EncryptedFile r1 = r0.file     // Catch:{ Exception -> 0x0288 }
            r3.file = r1     // Catch:{ Exception -> 0x0288 }
            int r0 = r0.date     // Catch:{ Exception -> 0x0288 }
            r3.date = r0     // Catch:{ Exception -> 0x0288 }
            r3.new_key_used = r14     // Catch:{ Exception -> 0x0288 }
            r3.decryptedWithVersion = r6     // Catch:{ Exception -> 0x0288 }
            r2.add(r3)     // Catch:{ Exception -> 0x0288 }
            return r12
        L_0x0220:
            if (r6 != r10) goto L_0x022c
            int r2 = r11.mtproto_seq     // Catch:{ Exception -> 0x0288 }
            int r3 = r11.seq_in     // Catch:{ Exception -> 0x0288 }
            int r2 = java.lang.Math.min(r2, r3)     // Catch:{ Exception -> 0x0288 }
            r11.mtproto_seq = r2     // Catch:{ Exception -> 0x0288 }
        L_0x022c:
            int r2 = r1.layer     // Catch:{ Exception -> 0x0288 }
            r8.applyPeerLayer(r11, r2)     // Catch:{ Exception -> 0x0288 }
            int r2 = r1.out_seq_no     // Catch:{ Exception -> 0x0288 }
            r11.seq_in = r2     // Catch:{ Exception -> 0x0288 }
            int r2 = r1.in_seq_no     // Catch:{ Exception -> 0x0288 }
            r11.in_seq_no = r2     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.MessagesStorage r2 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x0288 }
            r3 = 1
            r2.updateEncryptedChatSeq(r11, r3)     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLRPC$DecryptedMessage r1 = r1.message     // Catch:{ Exception -> 0x0288 }
            goto L_0x0252
        L_0x0244:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageService     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x026d
            r2 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageService r2 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageService) r2     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action     // Catch:{ Exception -> 0x0288 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer     // Catch:{ Exception -> 0x0288 }
            if (r2 != 0) goto L_0x0252
            goto L_0x026d
        L_0x0252:
            r5 = r1
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0288 }
            r7.<init>()     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLRPC$EncryptedFile r3 = r0.file     // Catch:{ Exception -> 0x0288 }
            int r4 = r0.date     // Catch:{ Exception -> 0x0288 }
            r1 = r21
            r2 = r11
            r6 = r14
            org.telegram.tgnet.TLRPC$Message r0 = r1.processDecryptedObject(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0288 }
            if (r0 == 0) goto L_0x0269
            r7.add(r0)     // Catch:{ Exception -> 0x0288 }
        L_0x0269:
            r8.checkSecretHoles(r11, r7)     // Catch:{ Exception -> 0x0288 }
            return r7
        L_0x026d:
            return r12
        L_0x026e:
            r13.reuse()     // Catch:{ Exception -> 0x0288 }
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0288 }
            if (r0 == 0) goto L_0x028c
            java.lang.String r0 = "fingerprint mismatch %x"
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x0288 }
            java.lang.Long r1 = java.lang.Long.valueOf(r1)     // Catch:{ Exception -> 0x0288 }
            r3[r14] = r1     // Catch:{ Exception -> 0x0288 }
            java.lang.String r0 = java.lang.String.format(r0, r3)     // Catch:{ Exception -> 0x0288 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0288 }
            goto L_0x028c
        L_0x0288:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x028c:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.decryptMessage(org.telegram.tgnet.TLRPC$EncryptedMessage):java.util.ArrayList");
    }

    public /* synthetic */ void lambda$decryptMessage$16$SecretChatHelper(TLRPC.TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
        getMessagesController().putEncryptedChat(tL_encryptedChatDiscarded, false);
        getMessagesStorage().updateEncryptedChat(tL_encryptedChatDiscarded);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tL_encryptedChatDiscarded);
    }

    public void requestNewSecretChatKey(TLRPC.EncryptedChat encryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 20) {
            byte[] bArr = new byte[256];
            Utilities.random.nextBytes(bArr);
            byte[] byteArray = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr2 = new byte[256];
                System.arraycopy(byteArray, 1, bArr2, 0, 256);
                byteArray = bArr2;
            }
            encryptedChat.exchange_id = getSendMessagesHelper().getNextRandomId();
            encryptedChat.a_or_b = bArr;
            encryptedChat.g_a = byteArray;
            getMessagesStorage().updateEncryptedChat(encryptedChat);
            sendRequestKeyMessage(encryptedChat, (TLRPC.Message) null);
        }
    }

    public void processAcceptedSecretChat(TLRPC.EncryptedChat encryptedChat) {
        byte[] bArr;
        BigInteger bigInteger = new BigInteger(1, getMessagesStorage().getSecretPBytes());
        BigInteger bigInteger2 = new BigInteger(1, encryptedChat.g_a_or_b);
        if (!Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
            declineSecretChat(encryptedChat.id);
            return;
        }
        byte[] byteArray = bigInteger2.modPow(new BigInteger(1, encryptedChat.a_or_b), bigInteger).toByteArray();
        if (byteArray.length > 256) {
            bArr = new byte[256];
            System.arraycopy(byteArray, byteArray.length - 256, bArr, 0, 256);
        } else if (byteArray.length < 256) {
            bArr = new byte[256];
            System.arraycopy(byteArray, 0, bArr, 256 - byteArray.length, byteArray.length);
            for (int i = 0; i < 256 - byteArray.length; i++) {
                bArr[i] = 0;
            }
        } else {
            bArr = byteArray;
        }
        byte[] computeSHA1 = Utilities.computeSHA1(bArr);
        byte[] bArr2 = new byte[8];
        System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr2, 0, 8);
        if (encryptedChat.key_fingerprint == Utilities.bytesToLong(bArr2)) {
            encryptedChat.auth_key = bArr;
            encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
            encryptedChat.seq_in = -2;
            encryptedChat.seq_out = 1;
            getMessagesStorage().updateEncryptedChat(encryptedChat);
            getMessagesController().putEncryptedChat(encryptedChat, false);
            ArrayList arrayList = this.pendingSecretMessages.get(encryptedChat.id);
            if (arrayList != null) {
                getMessagesController().processUpdateArray(arrayList, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
                this.pendingSecretMessages.remove(encryptedChat.id);
            }
            AndroidUtilities.runOnUIThread(new Runnable(encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$processAcceptedSecretChat$17$SecretChatHelper(this.f$1);
                }
            });
            return;
        }
        TLRPC.TL_encryptedChatDiscarded tL_encryptedChatDiscarded = new TLRPC.TL_encryptedChatDiscarded();
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
        getMessagesStorage().updateEncryptedChat(tL_encryptedChatDiscarded);
        AndroidUtilities.runOnUIThread(new Runnable(tL_encryptedChatDiscarded) {
            private final /* synthetic */ TLRPC.TL_encryptedChatDiscarded f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$processAcceptedSecretChat$18$SecretChatHelper(this.f$1);
            }
        });
        declineSecretChat(encryptedChat.id);
    }

    public /* synthetic */ void lambda$processAcceptedSecretChat$17$SecretChatHelper(TLRPC.EncryptedChat encryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
        sendNotifyLayerMessage(encryptedChat, (TLRPC.Message) null);
    }

    public /* synthetic */ void lambda$processAcceptedSecretChat$18$SecretChatHelper(TLRPC.TL_encryptedChatDiscarded tL_encryptedChatDiscarded) {
        getMessagesController().putEncryptedChat(tL_encryptedChatDiscarded, false);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tL_encryptedChatDiscarded);
    }

    public void declineSecretChat(int i) {
        TLRPC.TL_messages_discardEncryption tL_messages_discardEncryption = new TLRPC.TL_messages_discardEncryption();
        tL_messages_discardEncryption.chat_id = i;
        getConnectionsManager().sendRequest(tL_messages_discardEncryption, $$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M.INSTANCE);
    }

    public void acceptSecretChat(TLRPC.EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) == null) {
            this.acceptingChats.put(encryptedChat.id, encryptedChat);
            TLRPC.TL_messages_getDhConfig tL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
            tL_messages_getDhConfig.random_length = 256;
            tL_messages_getDhConfig.version = getMessagesStorage().getLastSecretVersion();
            getConnectionsManager().sendRequest(tL_messages_getDhConfig, new RequestDelegate(encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$acceptSecretChat$22$SecretChatHelper(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$acceptSecretChat$22$SecretChatHelper(TLRPC.EncryptedChat encryptedChat, TLObject tLObject, TLRPC.TL_error tL_error) {
        byte[] bArr;
        if (tL_error == null) {
            TLRPC.messages_DhConfig messages_dhconfig = (TLRPC.messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(messages_dhconfig.p, messages_dhconfig.g)) {
                    this.acceptingChats.remove(encryptedChat.id);
                    declineSecretChat(encryptedChat.id);
                    return;
                }
                getMessagesStorage().setSecretPBytes(messages_dhconfig.p);
                getMessagesStorage().setSecretG(messages_dhconfig.g);
                getMessagesStorage().setLastSecretVersion(messages_dhconfig.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] bArr2 = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr2[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
            }
            encryptedChat.a_or_b = bArr2;
            encryptedChat.seq_in = -1;
            encryptedChat.seq_out = 0;
            BigInteger bigInteger = new BigInteger(1, getMessagesStorage().getSecretPBytes());
            BigInteger modPow = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr2), bigInteger);
            BigInteger bigInteger2 = new BigInteger(1, encryptedChat.g_a);
            if (!Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
                this.acceptingChats.remove(encryptedChat.id);
                declineSecretChat(encryptedChat.id);
                return;
            }
            byte[] byteArray = modPow.toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr3 = new byte[256];
                System.arraycopy(byteArray, 1, bArr3, 0, 256);
                byteArray = bArr3;
            }
            byte[] byteArray2 = bigInteger2.modPow(new BigInteger(1, bArr2), bigInteger).toByteArray();
            if (byteArray2.length > 256) {
                bArr = new byte[256];
                System.arraycopy(byteArray2, byteArray2.length - 256, bArr, 0, 256);
            } else if (byteArray2.length < 256) {
                bArr = new byte[256];
                System.arraycopy(byteArray2, 0, bArr, 256 - byteArray2.length, byteArray2.length);
                for (int i2 = 0; i2 < 256 - byteArray2.length; i2++) {
                    bArr[i2] = 0;
                }
            } else {
                bArr = byteArray2;
            }
            byte[] computeSHA1 = Utilities.computeSHA1(bArr);
            byte[] bArr4 = new byte[8];
            System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr4, 0, 8);
            encryptedChat.auth_key = bArr;
            encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
            TLRPC.TL_messages_acceptEncryption tL_messages_acceptEncryption = new TLRPC.TL_messages_acceptEncryption();
            tL_messages_acceptEncryption.g_b = byteArray;
            tL_messages_acceptEncryption.peer = new TLRPC.TL_inputEncryptedChat();
            TLRPC.TL_inputEncryptedChat tL_inputEncryptedChat = tL_messages_acceptEncryption.peer;
            tL_inputEncryptedChat.chat_id = encryptedChat.id;
            tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
            tL_messages_acceptEncryption.key_fingerprint = Utilities.bytesToLong(bArr4);
            getConnectionsManager().sendRequest(tL_messages_acceptEncryption, new RequestDelegate(encryptedChat) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$null$21$SecretChatHelper(this.f$1, tLObject, tL_error);
                }
            }, 64);
            return;
        }
        this.acceptingChats.remove(encryptedChat.id);
    }

    public /* synthetic */ void lambda$null$21$SecretChatHelper(TLRPC.EncryptedChat encryptedChat, TLObject tLObject, TLRPC.TL_error tL_error) {
        this.acceptingChats.remove(encryptedChat.id);
        if (tL_error == null) {
            TLRPC.EncryptedChat encryptedChat2 = (TLRPC.EncryptedChat) tLObject;
            encryptedChat2.auth_key = encryptedChat.auth_key;
            encryptedChat2.user_id = encryptedChat.user_id;
            encryptedChat2.seq_in = encryptedChat.seq_in;
            encryptedChat2.seq_out = encryptedChat.seq_out;
            encryptedChat2.key_create_date = encryptedChat.key_create_date;
            encryptedChat2.key_use_count_in = encryptedChat.key_use_count_in;
            encryptedChat2.key_use_count_out = encryptedChat.key_use_count_out;
            getMessagesStorage().updateEncryptedChat(encryptedChat2);
            getMessagesController().putEncryptedChat(encryptedChat2, false);
            AndroidUtilities.runOnUIThread(new Runnable(encryptedChat2) {
                private final /* synthetic */ TLRPC.EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$20$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$20$SecretChatHelper(TLRPC.EncryptedChat encryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
        sendNotifyLayerMessage(encryptedChat, (TLRPC.Message) null);
    }

    public void startSecretChat(Context context, TLRPC.User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            AlertDialog alertDialog = new AlertDialog(context, 3);
            TLRPC.TL_messages_getDhConfig tL_messages_getDhConfig = new TLRPC.TL_messages_getDhConfig();
            tL_messages_getDhConfig.random_length = 256;
            tL_messages_getDhConfig.version = getMessagesStorage().getLastSecretVersion();
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(tL_messages_getDhConfig, new RequestDelegate(context, alertDialog, user) {
                private final /* synthetic */ Context f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ TLRPC.User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$startSecretChat$29$SecretChatHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }, 2)) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    SecretChatHelper.this.lambda$startSecretChat$30$SecretChatHelper(this.f$1, dialogInterface);
                }
            });
            try {
                alertDialog.show();
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$startSecretChat$29$SecretChatHelper(Context context, AlertDialog alertDialog, TLRPC.User user, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_DhConfig messages_dhconfig = (TLRPC.messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(messages_dhconfig.p, messages_dhconfig.g)) {
                    AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog) {
                        private final /* synthetic */ Context f$0;
                        private final /* synthetic */ AlertDialog f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.lambda$null$23(this.f$0, this.f$1);
                        }
                    });
                    return;
                }
                getMessagesStorage().setSecretPBytes(messages_dhconfig.p);
                getMessagesStorage().setSecretG(messages_dhconfig.g);
                getMessagesStorage().setLastSecretVersion(messages_dhconfig.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] bArr = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ messages_dhconfig.random[i]);
            }
            byte[] byteArray = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr2 = new byte[256];
                System.arraycopy(byteArray, 1, bArr2, 0, 256);
                byteArray = bArr2;
            }
            TLRPC.TL_messages_requestEncryption tL_messages_requestEncryption = new TLRPC.TL_messages_requestEncryption();
            tL_messages_requestEncryption.g_a = byteArray;
            tL_messages_requestEncryption.user_id = getMessagesController().getInputUser(user);
            tL_messages_requestEncryption.random_id = Utilities.random.nextInt();
            getConnectionsManager().sendRequest(tL_messages_requestEncryption, new RequestDelegate(context, alertDialog, bArr, user) {
                private final /* synthetic */ Context f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ byte[] f$3;
                private final /* synthetic */ TLRPC.User f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.lambda$null$27$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            }, 2);
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$28$SecretChatHelper(this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$null$23(Context context, AlertDialog alertDialog) {
        try {
            if (!((Activity) context).isFinishing()) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$27$SecretChatHelper(Context context, AlertDialog alertDialog, byte[] bArr, TLRPC.User user, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog, tLObject, bArr, user) {
                private final /* synthetic */ Context f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ byte[] f$4;
                private final /* synthetic */ TLRPC.User f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$25$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog) {
            private final /* synthetic */ Context f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$26$SecretChatHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$25$SecretChatHelper(Context context, AlertDialog alertDialog, TLObject tLObject, byte[] bArr, TLRPC.User user) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) tLObject;
        encryptedChat.user_id = encryptedChat.participant_id;
        encryptedChat.seq_in = -2;
        encryptedChat.seq_out = 1;
        encryptedChat.a_or_b = bArr;
        getMessagesController().putEncryptedChat(encryptedChat, false);
        TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
        tL_dialog.id = DialogObject.makeSecretDialogId(encryptedChat.id);
        tL_dialog.unread_count = 0;
        tL_dialog.top_message = 0;
        tL_dialog.last_message_date = getConnectionsManager().getCurrentTime();
        getMessagesController().dialogs_dict.put(tL_dialog.id, tL_dialog);
        getMessagesController().allDialogs.add(tL_dialog);
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getMessagesStorage().putEncryptedChat(encryptedChat, user, tL_dialog);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatCreated, encryptedChat);
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                SecretChatHelper.this.lambda$null$24$SecretChatHelper();
            }
        });
    }

    public /* synthetic */ void lambda$null$24$SecretChatHelper() {
        if (!this.delayedEncryptedChatUpdates.isEmpty()) {
            getMessagesController().processUpdateArray(this.delayedEncryptedChatUpdates, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
            this.delayedEncryptedChatUpdates.clear();
        }
    }

    public /* synthetic */ void lambda$null$26$SecretChatHelper(Context context, AlertDialog alertDialog) {
        if (!((Activity) context).isFinishing()) {
            this.startingSecretChat = false;
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("CreateEncryptedChatError", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    public /* synthetic */ void lambda$null$28$SecretChatHelper(Context context, AlertDialog alertDialog) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$startSecretChat$30$SecretChatHelper(int i, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
    }
}
