package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$DecryptedMessage;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$DecryptedMessageMedia;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$EncryptedFile;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageService;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_documentEncrypted;
import org.telegram.tgnet.TLRPC$TL_encryptedChat;
import org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC$TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC$TL_encryptedFile;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messages_acceptEncryption;
import org.telegram.tgnet.TLRPC$TL_messages_dhConfig;
import org.telegram.tgnet.TLRPC$TL_messages_discardEncryption;
import org.telegram.tgnet.TLRPC$TL_messages_getDhConfig;
import org.telegram.tgnet.TLRPC$TL_messages_requestEncryption;
import org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_DhConfig;
import org.telegram.tgnet.TLRPC$messages_SentEncryptedMessage;
import org.telegram.ui.ActionBar.AlertDialog;

public class SecretChatHelper extends BaseController {
    public static final int CURRENT_SECRET_CHAT_LAYER = 101;
    private static volatile SecretChatHelper[] Instance = new SecretChatHelper[3];
    private SparseArray<TLRPC$EncryptedChat> acceptingChats = new SparseArray<>();
    public ArrayList<TLRPC$Update> delayedEncryptedChatUpdates = new ArrayList<>();
    private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList<>();
    private SparseArray<ArrayList<TLRPC$Update>> pendingSecretMessages = new SparseArray<>();
    private SparseArray<SparseIntArray> requestedHoles = new SparseArray<>();
    private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray<>();
    private ArrayList<Integer> sendingNotifyLayer = new ArrayList<>();
    private boolean startingSecretChat = false;

    static /* synthetic */ void lambda$declineSecretChat$19(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = NUM;
        public int date;
        public int decryptedWithVersion;
        public TLRPC$EncryptedFile file;
        public TLRPC$TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            abstractSerializedData.readInt64(z);
            this.date = abstractSerializedData.readInt32(z);
            this.layer = TLRPC$TL_decryptedMessageLayer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (abstractSerializedData.readBool(z)) {
                this.file = TLRPC$EncryptedFile.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.new_key_used = abstractSerializedData.readBool(z);
        }

        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt64(0);
            abstractSerializedData.writeInt32(this.date);
            this.layer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeBool(this.file != null);
            TLRPC$EncryptedFile tLRPC$EncryptedFile = this.file;
            if (tLRPC$EncryptedFile != null) {
                tLRPC$EncryptedFile.serializeToStream(abstractSerializedData);
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
                public final /* synthetic */ ArrayList f$1;

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

    private TLRPC$TL_messageService createServiceSecretMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction) {
        TLRPC$TL_messageService tLRPC$TL_messageService = new TLRPC$TL_messageService();
        TLRPC$TL_messageEncryptedAction tLRPC$TL_messageEncryptedAction = new TLRPC$TL_messageEncryptedAction();
        tLRPC$TL_messageService.action = tLRPC$TL_messageEncryptedAction;
        tLRPC$TL_messageEncryptedAction.encryptedAction = tLRPC$DecryptedMessageAction;
        int newMessageId = getUserConfig().getNewMessageId();
        tLRPC$TL_messageService.id = newMessageId;
        tLRPC$TL_messageService.local_id = newMessageId;
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        tLRPC$TL_messageService.from_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
        tLRPC$TL_messageService.unread = true;
        tLRPC$TL_messageService.out = true;
        tLRPC$TL_messageService.flags = 256;
        tLRPC$TL_messageService.dialog_id = ((long) tLRPC$EncryptedChat.id) << 32;
        tLRPC$TL_messageService.peer_id = new TLRPC$TL_peerUser();
        tLRPC$TL_messageService.send_state = 1;
        if (tLRPC$EncryptedChat.participant_id == getUserConfig().getClientUserId()) {
            tLRPC$TL_messageService.peer_id.user_id = tLRPC$EncryptedChat.admin_id;
        } else {
            tLRPC$TL_messageService.peer_id.user_id = tLRPC$EncryptedChat.participant_id;
        }
        if ((tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) || (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL)) {
            tLRPC$TL_messageService.date = getConnectionsManager().getCurrentTime();
        } else {
            tLRPC$TL_messageService.date = 0;
        }
        tLRPC$TL_messageService.random_id = getSendMessagesHelper().getNextRandomId();
        getUserConfig().saveConfig(false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(tLRPC$TL_messageService);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, false, true, true, 0, false);
        return tLRPC$TL_messageService;
    }

    public void sendMessagesReadMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, ArrayList<Long> arrayList, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionReadMessages tLRPC$TL_decryptedMessageActionReadMessages = new TLRPC$TL_decryptedMessageActionReadMessages();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionReadMessages;
                tLRPC$TL_decryptedMessageActionReadMessages.random_ids = arrayList;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionReadMessages);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processUpdateEncryption(org.telegram.tgnet.TLRPC$TL_updateEncryption r8, j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User> r9) {
        /*
            r7 = this;
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.chat
            int r1 = r0.id
            long r1 = (long) r1
            r3 = 32
            long r1 = r1 << r3
            org.telegram.messenger.MessagesController r3 = r7.getMessagesController()
            int r4 = r0.id
            r5 = 0
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChatDB(r4, r5)
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatRequested
            if (r4 == 0) goto L_0x006f
            if (r3 != 0) goto L_0x006f
            int r3 = r0.participant_id
            org.telegram.messenger.UserConfig r4 = r7.getUserConfig()
            int r4 = r4.getClientUserId()
            if (r3 != r4) goto L_0x0027
            int r3 = r0.admin_id
        L_0x0027:
            org.telegram.messenger.MessagesController r4 = r7.getMessagesController()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r6)
            if (r4 != 0) goto L_0x0040
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            java.lang.Object r9 = r9.get(r4)
            r4 = r9
            org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC$User) r4
        L_0x0040:
            r0.user_id = r3
            org.telegram.tgnet.TLRPC$TL_dialog r9 = new org.telegram.tgnet.TLRPC$TL_dialog
            r9.<init>()
            r9.id = r1
            int r3 = r0.folder_id
            r9.folder_id = r3
            r9.unread_count = r5
            r9.top_message = r5
            int r8 = r8.date
            r9.last_message_date = r8
            org.telegram.messenger.MessagesController r8 = r7.getMessagesController()
            r8.putEncryptedChat(r0, r5)
            org.telegram.messenger.-$$Lambda$SecretChatHelper$eDp1ow-71SJ45XI53001XP-mRTQ r8 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$eDp1ow-71SJ45XI53001XP-mRTQ
            r8.<init>(r9, r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
            org.telegram.messenger.MessagesStorage r8 = r7.getMessagesStorage()
            r8.putEncryptedChat(r0, r4, r9)
            r7.acceptSecretChat(r0)
            goto L_0x00c9
        L_0x006f:
            boolean r9 = r0 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r9 == 0) goto L_0x0097
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting
            if (r9 == 0) goto L_0x008b
            byte[] r9 = r3.auth_key
            if (r9 == 0) goto L_0x007f
            int r9 = r9.length
            r1 = 1
            if (r9 != r1) goto L_0x008b
        L_0x007f:
            byte[] r8 = r3.a_or_b
            r0.a_or_b = r8
            int r8 = r3.user_id
            r0.user_id = r8
            r7.processAcceptedSecretChat(r0)
            goto L_0x00c9
        L_0x008b:
            if (r3 != 0) goto L_0x00c9
            boolean r9 = r7.startingSecretChat
            if (r9 == 0) goto L_0x00c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r9 = r7.delayedEncryptedChatUpdates
            r9.add(r8)
            goto L_0x00c9
        L_0x0097:
            if (r3 == 0) goto L_0x00c1
            int r8 = r3.user_id
            r0.user_id = r8
            byte[] r8 = r3.auth_key
            r0.auth_key = r8
            int r8 = r3.key_create_date
            r0.key_create_date = r8
            short r8 = r3.key_use_count_in
            r0.key_use_count_in = r8
            short r8 = r3.key_use_count_out
            r0.key_use_count_out = r8
            int r8 = r3.ttl
            r0.ttl = r8
            int r8 = r3.seq_in
            r0.seq_in = r8
            int r8 = r3.seq_out
            r0.seq_out = r8
            int r8 = r3.admin_id
            r0.admin_id = r8
            int r8 = r3.mtproto_seq
            r0.mtproto_seq = r8
        L_0x00c1:
            org.telegram.messenger.-$$Lambda$SecretChatHelper$G9V6FvkI-PnA0UumuoB_kxH2lOM r8 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$G9V6FvkI-PnA0UumuoB_kxH2lOM
            r8.<init>(r3, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
        L_0x00c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.processUpdateEncryption(org.telegram.tgnet.TLRPC$TL_updateEncryption, j$.util.concurrent.ConcurrentHashMap):void");
    }

    public /* synthetic */ void lambda$processUpdateEncryption$1$SecretChatHelper(TLRPC$Dialog tLRPC$Dialog, long j) {
        if (tLRPC$Dialog.folder_id == 1) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("dialog_bar_archived" + j, true);
            edit.commit();
        }
        getMessagesController().dialogs_dict.put(tLRPC$Dialog.id, tLRPC$Dialog);
        getMessagesController().allDialogs.add(tLRPC$Dialog);
        getMessagesController().sortDialogs((SparseArray<TLRPC$Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public /* synthetic */ void lambda$processUpdateEncryption$2$SecretChatHelper(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$EncryptedChat tLRPC$EncryptedChat2) {
        if (tLRPC$EncryptedChat != null) {
            getMessagesController().putEncryptedChat(tLRPC$EncryptedChat2, false);
        }
        getMessagesStorage().updateEncryptedChat(tLRPC$EncryptedChat2);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tLRPC$EncryptedChat2);
    }

    public void sendMessagesDeleteMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, ArrayList<Long> arrayList, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionDeleteMessages tLRPC$TL_decryptedMessageActionDeleteMessages = new TLRPC$TL_decryptedMessageActionDeleteMessages();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionDeleteMessages;
                tLRPC$TL_decryptedMessageActionDeleteMessages.random_ids = arrayList;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionDeleteMessages);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendClearHistoryMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionFlushHistory tLRPC$TL_decryptedMessageActionFlushHistory = new TLRPC$TL_decryptedMessageActionFlushHistory();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionFlushHistory;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionFlushHistory);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNotifyLayerMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if ((tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(tLRPC$EncryptedChat.id))) {
            this.sendingNotifyLayer.add(Integer.valueOf(tLRPC$EncryptedChat.id));
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionNotifyLayer tLRPC$TL_decryptedMessageActionNotifyLayer = new TLRPC$TL_decryptedMessageActionNotifyLayer();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionNotifyLayer;
                tLRPC$TL_decryptedMessageActionNotifyLayer.layer = 101;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionNotifyLayer);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendRequestKeyMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionRequestKey tLRPC$TL_decryptedMessageActionRequestKey = new TLRPC$TL_decryptedMessageActionRequestKey();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionRequestKey;
                tLRPC$TL_decryptedMessageActionRequestKey.exchange_id = tLRPC$EncryptedChat.exchange_id;
                tLRPC$TL_decryptedMessageActionRequestKey.g_a = tLRPC$EncryptedChat.g_a;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionRequestKey);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAcceptKeyMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionAcceptKey tLRPC$TL_decryptedMessageActionAcceptKey = new TLRPC$TL_decryptedMessageActionAcceptKey();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionAcceptKey;
                tLRPC$TL_decryptedMessageActionAcceptKey.exchange_id = tLRPC$EncryptedChat.exchange_id;
                tLRPC$TL_decryptedMessageActionAcceptKey.key_fingerprint = tLRPC$EncryptedChat.future_key_fingerprint;
                tLRPC$TL_decryptedMessageActionAcceptKey.g_b = tLRPC$EncryptedChat.g_a_or_b;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionAcceptKey);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendCommitKeyMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionCommitKey tLRPC$TL_decryptedMessageActionCommitKey = new TLRPC$TL_decryptedMessageActionCommitKey();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionCommitKey;
                tLRPC$TL_decryptedMessageActionCommitKey.exchange_id = tLRPC$EncryptedChat.exchange_id;
                tLRPC$TL_decryptedMessageActionCommitKey.key_fingerprint = tLRPC$EncryptedChat.future_key_fingerprint;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionCommitKey);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAbortKeyMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message, long j) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionAbortKey tLRPC$TL_decryptedMessageActionAbortKey = new TLRPC$TL_decryptedMessageActionAbortKey();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionAbortKey;
                tLRPC$TL_decryptedMessageActionAbortKey.exchange_id = j;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionAbortKey);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNoopMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionNoop tLRPC$TL_decryptedMessageActionNoop = new TLRPC$TL_decryptedMessageActionNoop();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionNoop;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionNoop);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendResendMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, int i, int i2, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            SparseIntArray sparseIntArray = this.requestedHoles.get(tLRPC$EncryptedChat.id);
            if (sparseIntArray == null || sparseIntArray.indexOfKey(i) < 0) {
                if (sparseIntArray == null) {
                    sparseIntArray = new SparseIntArray();
                    this.requestedHoles.put(tLRPC$EncryptedChat.id, sparseIntArray);
                }
                sparseIntArray.put(i, i2);
                TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
                if (tLRPC$Message != null) {
                    tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
                } else {
                    TLRPC$TL_decryptedMessageActionResend tLRPC$TL_decryptedMessageActionResend = new TLRPC$TL_decryptedMessageActionResend();
                    tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionResend;
                    tLRPC$TL_decryptedMessageActionResend.start_seq_no = i;
                    tLRPC$TL_decryptedMessageActionResend.end_seq_no = i2;
                    tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionResend);
                }
                TLRPC$Message tLRPC$Message2 = tLRPC$Message;
                tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
                performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
            }
        }
    }

    public void sendTTLMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionSetMessageTTL tLRPC$TL_decryptedMessageActionSetMessageTTL = new TLRPC$TL_decryptedMessageActionSetMessageTTL();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionSetMessageTTL;
                tLRPC$TL_decryptedMessageActionSetMessageTTL.ttl_seconds = tLRPC$EncryptedChat.ttl;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionSetMessageTTL);
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, false, false);
                messageObject.messageOwner.send_state = 1;
                messageObject.wasJustSent = true;
                ArrayList arrayList = new ArrayList();
                arrayList.add(messageObject);
                getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendScreenshotMessage(TLRPC$EncryptedChat tLRPC$EncryptedChat, ArrayList<Long> arrayList, TLRPC$Message tLRPC$Message) {
        if (tLRPC$EncryptedChat instanceof TLRPC$TL_encryptedChat) {
            TLRPC$TL_decryptedMessageService tLRPC$TL_decryptedMessageService = new TLRPC$TL_decryptedMessageService();
            if (tLRPC$Message != null) {
                tLRPC$TL_decryptedMessageService.action = tLRPC$Message.action.encryptedAction;
            } else {
                TLRPC$TL_decryptedMessageActionScreenshotMessages tLRPC$TL_decryptedMessageActionScreenshotMessages = new TLRPC$TL_decryptedMessageActionScreenshotMessages();
                tLRPC$TL_decryptedMessageService.action = tLRPC$TL_decryptedMessageActionScreenshotMessages;
                tLRPC$TL_decryptedMessageActionScreenshotMessages.random_ids = arrayList;
                tLRPC$Message = createServiceSecretMessage(tLRPC$EncryptedChat, tLRPC$TL_decryptedMessageActionScreenshotMessages);
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, false, false);
                messageObject.messageOwner.send_state = 1;
                messageObject.wasJustSent = true;
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(messageObject);
                getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList2, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            TLRPC$Message tLRPC$Message2 = tLRPC$Message;
            tLRPC$TL_decryptedMessageService.random_id = tLRPC$Message2.random_id;
            performSendEncryptedRequest(tLRPC$TL_decryptedMessageService, tLRPC$Message2, tLRPC$EncryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    private void updateMediaPaths(MessageObject messageObject, TLRPC$EncryptedFile tLRPC$EncryptedFile, TLRPC$DecryptedMessage tLRPC$DecryptedMessage, String str) {
        TLRPC$Document tLRPC$Document;
        TLRPC$Photo tLRPC$Photo;
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        if (tLRPC$EncryptedFile != null) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$Photo = tLRPC$MessageMedia.photo) == null) {
                TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
                if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaDocument) && (tLRPC$Document = tLRPC$MessageMedia2.document) != null) {
                    tLRPC$MessageMedia2.document = new TLRPC$TL_documentEncrypted();
                    TLRPC$Document tLRPC$Document2 = tLRPC$Message.media.document;
                    tLRPC$Document2.id = tLRPC$EncryptedFile.id;
                    tLRPC$Document2.access_hash = tLRPC$EncryptedFile.access_hash;
                    tLRPC$Document2.date = tLRPC$Document.date;
                    tLRPC$Document2.attributes = tLRPC$Document.attributes;
                    tLRPC$Document2.mime_type = tLRPC$Document.mime_type;
                    tLRPC$Document2.size = tLRPC$EncryptedFile.size;
                    TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia = tLRPC$DecryptedMessage.media;
                    tLRPC$Document2.key = tLRPC$DecryptedMessageMedia.key;
                    tLRPC$Document2.iv = tLRPC$DecryptedMessageMedia.iv;
                    ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$Document.thumbs;
                    tLRPC$Document2.thumbs = arrayList;
                    tLRPC$Document2.dc_id = tLRPC$EncryptedFile.dc_id;
                    if (arrayList.isEmpty()) {
                        TLRPC$TL_photoSizeEmpty tLRPC$TL_photoSizeEmpty = new TLRPC$TL_photoSizeEmpty();
                        tLRPC$TL_photoSizeEmpty.type = "s";
                        tLRPC$Message.media.document.thumbs.add(tLRPC$TL_photoSizeEmpty);
                    }
                    String str2 = tLRPC$Message.attachPath;
                    if (str2 != null && str2.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(tLRPC$Message.attachPath).renameTo(FileLoader.getPathToAttach(tLRPC$Message.media.document))) {
                        messageObject.mediaExists = messageObject.attachPathExists;
                        messageObject.attachPathExists = false;
                        tLRPC$Message.attachPath = "";
                    }
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(tLRPC$Message);
                    getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList2, false, true, false, 0, false);
                    return;
                }
                return;
            }
            ArrayList<TLRPC$PhotoSize> arrayList3 = tLRPC$Photo.sizes;
            TLRPC$PhotoSize tLRPC$PhotoSize = arrayList3.get(arrayList3.size() - 1);
            String str3 = tLRPC$PhotoSize.location.volume_id + "_" + tLRPC$PhotoSize.location.local_id;
            TLRPC$TL_fileEncryptedLocation tLRPC$TL_fileEncryptedLocation = new TLRPC$TL_fileEncryptedLocation();
            tLRPC$PhotoSize.location = tLRPC$TL_fileEncryptedLocation;
            TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia2 = tLRPC$DecryptedMessage.media;
            tLRPC$TL_fileEncryptedLocation.key = tLRPC$DecryptedMessageMedia2.key;
            tLRPC$TL_fileEncryptedLocation.iv = tLRPC$DecryptedMessageMedia2.iv;
            tLRPC$TL_fileEncryptedLocation.dc_id = tLRPC$EncryptedFile.dc_id;
            tLRPC$TL_fileEncryptedLocation.volume_id = tLRPC$EncryptedFile.id;
            tLRPC$TL_fileEncryptedLocation.secret = tLRPC$EncryptedFile.access_hash;
            tLRPC$TL_fileEncryptedLocation.local_id = tLRPC$EncryptedFile.key_fingerprint;
            new File(FileLoader.getDirectory(4), str3 + ".jpg").renameTo(FileLoader.getPathToAttach(tLRPC$PhotoSize));
            ImageLoader.getInstance().replaceImageInCache(str3, tLRPC$PhotoSize.location.volume_id + "_" + tLRPC$PhotoSize.location.local_id, ImageLocation.getForPhoto(tLRPC$PhotoSize, tLRPC$Message.media.photo), true);
            ArrayList arrayList4 = new ArrayList();
            arrayList4.add(tLRPC$Message);
            getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList4, false, true, false, 0, false);
        }
    }

    public static boolean isSecretVisibleMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
            TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
            if ((tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) || (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSecretInvisibleMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
            TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
            return !(tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) && !(tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL);
        }
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia, SendMessagesHelper.DelayedMessage delayedMessage) {
        for (int i = 0; i < tLRPC$TL_messages_sendEncryptedMultiMedia.files.size(); i++) {
            performSendEncryptedRequest(tLRPC$TL_messages_sendEncryptedMultiMedia.messages.get(i), delayedMessage.messages.get(i), delayedMessage.encryptedChat, tLRPC$TL_messages_sendEncryptedMultiMedia.files.get(i), delayedMessage.originalPaths.get(i), delayedMessage.messageObjects.get(i));
        }
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC$DecryptedMessage tLRPC$DecryptedMessage, TLRPC$Message tLRPC$Message, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, String str, MessageObject messageObject) {
        TLRPC$EncryptedChat tLRPC$EncryptedChat2 = tLRPC$EncryptedChat;
        if (tLRPC$DecryptedMessage != null && tLRPC$EncryptedChat2.auth_key != null && !(tLRPC$EncryptedChat2 instanceof TLRPC$TL_encryptedChatRequested) && !(tLRPC$EncryptedChat2 instanceof TLRPC$TL_encryptedChatWaiting)) {
            getSendMessagesHelper().putToSendingMessages(tLRPC$Message, false);
            Utilities.stageQueue.postRunnable(new Runnable(tLRPC$EncryptedChat, tLRPC$DecryptedMessage, tLRPC$Message, tLRPC$InputEncryptedFile, messageObject, str) {
                public final /* synthetic */ TLRPC$EncryptedChat f$1;
                public final /* synthetic */ TLRPC$DecryptedMessage f$2;
                public final /* synthetic */ TLRPC$Message f$3;
                public final /* synthetic */ TLRPC$InputEncryptedFile f$4;
                public final /* synthetic */ MessageObject f$5;
                public final /* synthetic */ String f$6;

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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSendEncryptedRequest$7$SecretChatHelper(org.telegram.tgnet.TLRPC$EncryptedChat r21, org.telegram.tgnet.TLRPC$DecryptedMessage r22, org.telegram.tgnet.TLRPC$Message r23, org.telegram.tgnet.TLRPC$InputEncryptedFile r24, org.telegram.messenger.MessageObject r25, java.lang.String r26) {
        /*
            r20 = this;
            r0 = r21
            r3 = r22
            r5 = r23
            r1 = r24
            org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer     // Catch:{ Exception -> 0x0253 }
            r2.<init>()     // Catch:{ Exception -> 0x0253 }
            int r4 = r0.layer     // Catch:{ Exception -> 0x0253 }
            int r4 = org.telegram.messenger.AndroidUtilities.getMyLayerVersion(r4)     // Catch:{ Exception -> 0x0253 }
            r6 = 46
            int r4 = java.lang.Math.max(r6, r4)     // Catch:{ Exception -> 0x0253 }
            int r7 = r0.layer     // Catch:{ Exception -> 0x0253 }
            int r7 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r7)     // Catch:{ Exception -> 0x0253 }
            int r6 = java.lang.Math.max(r6, r7)     // Catch:{ Exception -> 0x0253 }
            int r4 = java.lang.Math.min(r4, r6)     // Catch:{ Exception -> 0x0253 }
            r2.layer = r4     // Catch:{ Exception -> 0x0253 }
            r2.message = r3     // Catch:{ Exception -> 0x0253 }
            r4 = 15
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0253 }
            r2.random_bytes = r4     // Catch:{ Exception -> 0x0253 }
            java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0253 }
            r6.nextBytes(r4)     // Catch:{ Exception -> 0x0253 }
            int r4 = r0.layer     // Catch:{ Exception -> 0x0253 }
            int r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4)     // Catch:{ Exception -> 0x0253 }
            r6 = 73
            r7 = 1
            r8 = 2
            if (r4 < r6) goto L_0x0044
            r4 = 2
            goto L_0x0045
        L_0x0044:
            r4 = 1
        L_0x0045:
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0253 }
            if (r6 != 0) goto L_0x0062
            int r6 = r0.seq_out     // Catch:{ Exception -> 0x0253 }
            if (r6 != 0) goto L_0x0062
            int r6 = r0.admin_id     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.UserConfig r9 = r20.getUserConfig()     // Catch:{ Exception -> 0x0253 }
            int r9 = r9.getClientUserId()     // Catch:{ Exception -> 0x0253 }
            if (r6 != r9) goto L_0x005f
            r0.seq_out = r7     // Catch:{ Exception -> 0x0253 }
            r6 = -2
            r0.seq_in = r6     // Catch:{ Exception -> 0x0253 }
            goto L_0x0062
        L_0x005f:
            r6 = -1
            r0.seq_in = r6     // Catch:{ Exception -> 0x0253 }
        L_0x0062:
            int r6 = r5.seq_in     // Catch:{ Exception -> 0x0253 }
            r9 = 0
            if (r6 != 0) goto L_0x00e2
            int r6 = r5.seq_out     // Catch:{ Exception -> 0x0253 }
            if (r6 != 0) goto L_0x00e2
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0253 }
            if (r6 <= 0) goto L_0x0072
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0253 }
            goto L_0x0075
        L_0x0072:
            int r6 = r0.seq_in     // Catch:{ Exception -> 0x0253 }
            int r6 = r6 + r8
        L_0x0075:
            r2.in_seq_no = r6     // Catch:{ Exception -> 0x0253 }
            int r6 = r0.seq_out     // Catch:{ Exception -> 0x0253 }
            r2.out_seq_no = r6     // Catch:{ Exception -> 0x0253 }
            int r6 = r0.seq_out     // Catch:{ Exception -> 0x0253 }
            int r6 = r6 + r8
            r0.seq_out = r6     // Catch:{ Exception -> 0x0253 }
            int r6 = r0.layer     // Catch:{ Exception -> 0x0253 }
            int r6 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r6)     // Catch:{ Exception -> 0x0253 }
            r10 = 20
            if (r6 < r10) goto L_0x00c3
            int r6 = r0.key_create_date     // Catch:{ Exception -> 0x0253 }
            if (r6 != 0) goto L_0x0098
            org.telegram.tgnet.ConnectionsManager r6 = r20.getConnectionsManager()     // Catch:{ Exception -> 0x0253 }
            int r6 = r6.getCurrentTime()     // Catch:{ Exception -> 0x0253 }
            r0.key_create_date = r6     // Catch:{ Exception -> 0x0253 }
        L_0x0098:
            short r6 = r0.key_use_count_out     // Catch:{ Exception -> 0x0253 }
            int r6 = r6 + r7
            short r6 = (short) r6     // Catch:{ Exception -> 0x0253 }
            r0.key_use_count_out = r6     // Catch:{ Exception -> 0x0253 }
            r10 = 100
            if (r6 >= r10) goto L_0x00b2
            int r6 = r0.key_create_date     // Catch:{ Exception -> 0x0253 }
            org.telegram.tgnet.ConnectionsManager r10 = r20.getConnectionsManager()     // Catch:{ Exception -> 0x0253 }
            int r10 = r10.getCurrentTime()     // Catch:{ Exception -> 0x0253 }
            r11 = 604800(0x93a80, float:8.47505E-40)
            int r10 = r10 - r11
            if (r6 >= r10) goto L_0x00c3
        L_0x00b2:
            long r10 = r0.exchange_id     // Catch:{ Exception -> 0x0253 }
            r12 = 0
            int r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r6 != 0) goto L_0x00c3
            long r10 = r0.future_key_fingerprint     // Catch:{ Exception -> 0x0253 }
            int r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r6 != 0) goto L_0x00c3
            r20.requestNewSecretChatKey(r21)     // Catch:{ Exception -> 0x0253 }
        L_0x00c3:
            org.telegram.messenger.MessagesStorage r6 = r20.getMessagesStorage()     // Catch:{ Exception -> 0x0253 }
            r6.updateEncryptedChatSeq(r0, r9)     // Catch:{ Exception -> 0x0253 }
            if (r5 == 0) goto L_0x00ea
            int r6 = r2.in_seq_no     // Catch:{ Exception -> 0x0253 }
            r5.seq_in = r6     // Catch:{ Exception -> 0x0253 }
            int r6 = r2.out_seq_no     // Catch:{ Exception -> 0x0253 }
            r5.seq_out = r6     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.MessagesStorage r6 = r20.getMessagesStorage()     // Catch:{ Exception -> 0x0253 }
            int r10 = r5.id     // Catch:{ Exception -> 0x0253 }
            int r11 = r5.seq_in     // Catch:{ Exception -> 0x0253 }
            int r12 = r5.seq_out     // Catch:{ Exception -> 0x0253 }
            r6.setMessageSeq(r10, r11, r12)     // Catch:{ Exception -> 0x0253 }
            goto L_0x00ea
        L_0x00e2:
            int r6 = r5.seq_in     // Catch:{ Exception -> 0x0253 }
            r2.in_seq_no = r6     // Catch:{ Exception -> 0x0253 }
            int r6 = r5.seq_out     // Catch:{ Exception -> 0x0253 }
            r2.out_seq_no = r6     // Catch:{ Exception -> 0x0253 }
        L_0x00ea:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0253 }
            if (r6 == 0) goto L_0x0111
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0253 }
            r6.<init>()     // Catch:{ Exception -> 0x0253 }
            r6.append(r3)     // Catch:{ Exception -> 0x0253 }
            java.lang.String r10 = " send message with in_seq = "
            r6.append(r10)     // Catch:{ Exception -> 0x0253 }
            int r10 = r2.in_seq_no     // Catch:{ Exception -> 0x0253 }
            r6.append(r10)     // Catch:{ Exception -> 0x0253 }
            java.lang.String r10 = " out_seq = "
            r6.append(r10)     // Catch:{ Exception -> 0x0253 }
            int r10 = r2.out_seq_no     // Catch:{ Exception -> 0x0253 }
            r6.append(r10)     // Catch:{ Exception -> 0x0253 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0253 }
        L_0x0111:
            int r6 = r2.getObjectSize()     // Catch:{ Exception -> 0x0253 }
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0253 }
            int r11 = r6 + 4
            r10.<init>((int) r11)     // Catch:{ Exception -> 0x0253 }
            r10.writeInt32(r6)     // Catch:{ Exception -> 0x0253 }
            r2.serializeToStream(r10)     // Catch:{ Exception -> 0x0253 }
            int r2 = r10.length()     // Catch:{ Exception -> 0x0253 }
            int r6 = r2 % 16
            r11 = 16
            if (r6 == 0) goto L_0x0131
            int r6 = r2 % 16
            int r6 = 16 - r6
            goto L_0x0132
        L_0x0131:
            r6 = 0
        L_0x0132:
            if (r4 != r8) goto L_0x013f
            java.security.SecureRandom r12 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0253 }
            r13 = 3
            int r12 = r12.nextInt(r13)     // Catch:{ Exception -> 0x0253 }
            int r12 = r12 + r8
            int r12 = r12 * 16
            int r6 = r6 + r12
        L_0x013f:
            org.telegram.tgnet.NativeByteBuffer r12 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0253 }
            int r2 = r2 + r6
            r12.<init>((int) r2)     // Catch:{ Exception -> 0x0253 }
            r10.position(r9)     // Catch:{ Exception -> 0x0253 }
            r12.writeBytes((org.telegram.tgnet.NativeByteBuffer) r10)     // Catch:{ Exception -> 0x0253 }
            if (r6 == 0) goto L_0x0157
            byte[] r2 = new byte[r6]     // Catch:{ Exception -> 0x0253 }
            java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0253 }
            r6.nextBytes(r2)     // Catch:{ Exception -> 0x0253 }
            r12.writeBytes((byte[]) r2)     // Catch:{ Exception -> 0x0253 }
        L_0x0157:
            byte[] r2 = new byte[r11]     // Catch:{ Exception -> 0x0253 }
            if (r4 != r8) goto L_0x0168
            int r6 = r0.admin_id     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.UserConfig r13 = r20.getUserConfig()     // Catch:{ Exception -> 0x0253 }
            int r13 = r13.getClientUserId()     // Catch:{ Exception -> 0x0253 }
            if (r6 == r13) goto L_0x0168
            goto L_0x0169
        L_0x0168:
            r7 = 0
        L_0x0169:
            if (r4 != r8) goto L_0x018e
            byte[] r13 = r0.auth_key     // Catch:{ Exception -> 0x0253 }
            r6 = 88
            if (r7 == 0) goto L_0x0174
            r14 = 8
            goto L_0x0175
        L_0x0174:
            r14 = 0
        L_0x0175:
            int r14 = r14 + r6
            r15 = 32
            java.nio.ByteBuffer r6 = r12.buffer     // Catch:{ Exception -> 0x0253 }
            r17 = 0
            java.nio.ByteBuffer r8 = r12.buffer     // Catch:{ Exception -> 0x0253 }
            int r18 = r8.limit()     // Catch:{ Exception -> 0x0253 }
            r16 = r6
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA256(r13, r14, r15, r16, r17, r18)     // Catch:{ Exception -> 0x0253 }
            r8 = 8
            java.lang.System.arraycopy(r6, r8, r2, r9, r11)     // Catch:{ Exception -> 0x0253 }
            goto L_0x0199
        L_0x018e:
            java.nio.ByteBuffer r6 = r10.buffer     // Catch:{ Exception -> 0x0253 }
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA1((java.nio.ByteBuffer) r6)     // Catch:{ Exception -> 0x0253 }
            int r8 = r6.length     // Catch:{ Exception -> 0x0253 }
            int r8 = r8 - r11
            java.lang.System.arraycopy(r6, r8, r2, r9, r11)     // Catch:{ Exception -> 0x0253 }
        L_0x0199:
            r10.reuse()     // Catch:{ Exception -> 0x0253 }
            byte[] r6 = r0.auth_key     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.MessageKeyData r4 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r6, r2, r7, r4)     // Catch:{ Exception -> 0x0253 }
            java.nio.ByteBuffer r13 = r12.buffer     // Catch:{ Exception -> 0x0253 }
            byte[] r14 = r4.aesKey     // Catch:{ Exception -> 0x0253 }
            byte[] r15 = r4.aesIv     // Catch:{ Exception -> 0x0253 }
            r16 = 1
            r17 = 0
            r18 = 0
            int r19 = r12.limit()     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.Utilities.aesIgeEncryption(r13, r14, r15, r16, r17, r18, r19)     // Catch:{ Exception -> 0x0253 }
            org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0253 }
            r6 = 24
            int r7 = r12.length()     // Catch:{ Exception -> 0x0253 }
            int r6 = r6 + r7
            r4.<init>((int) r6)     // Catch:{ Exception -> 0x0253 }
            r12.position(r9)     // Catch:{ Exception -> 0x0253 }
            long r6 = r0.key_fingerprint     // Catch:{ Exception -> 0x0253 }
            r4.writeInt64(r6)     // Catch:{ Exception -> 0x0253 }
            r4.writeBytes((byte[]) r2)     // Catch:{ Exception -> 0x0253 }
            r4.writeBytes((org.telegram.tgnet.NativeByteBuffer) r12)     // Catch:{ Exception -> 0x0253 }
            r12.reuse()     // Catch:{ Exception -> 0x0253 }
            r4.position(r9)     // Catch:{ Exception -> 0x0253 }
            if (r1 != 0) goto L_0x0216
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageService     // Catch:{ Exception -> 0x0253 }
            if (r1 == 0) goto L_0x01f7
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService     // Catch:{ Exception -> 0x0253 }
            r1.<init>()     // Catch:{ Exception -> 0x0253 }
            r1.data = r4     // Catch:{ Exception -> 0x0253 }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x0253 }
            r1.random_id = r6     // Catch:{ Exception -> 0x0253 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0253 }
            r2.<init>()     // Catch:{ Exception -> 0x0253 }
            r1.peer = r2     // Catch:{ Exception -> 0x0253 }
            int r4 = r0.id     // Catch:{ Exception -> 0x0253 }
            r2.chat_id = r4     // Catch:{ Exception -> 0x0253 }
            long r6 = r0.access_hash     // Catch:{ Exception -> 0x0253 }
            r2.access_hash = r6     // Catch:{ Exception -> 0x0253 }
        L_0x01f5:
            r8 = r1
            goto L_0x0237
        L_0x01f7:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted     // Catch:{ Exception -> 0x0253 }
            r1.<init>()     // Catch:{ Exception -> 0x0253 }
            boolean r2 = r5.silent     // Catch:{ Exception -> 0x0253 }
            r1.silent = r2     // Catch:{ Exception -> 0x0253 }
            r1.data = r4     // Catch:{ Exception -> 0x0253 }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x0253 }
            r1.random_id = r6     // Catch:{ Exception -> 0x0253 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0253 }
            r2.<init>()     // Catch:{ Exception -> 0x0253 }
            r1.peer = r2     // Catch:{ Exception -> 0x0253 }
            int r4 = r0.id     // Catch:{ Exception -> 0x0253 }
            r2.chat_id = r4     // Catch:{ Exception -> 0x0253 }
            long r6 = r0.access_hash     // Catch:{ Exception -> 0x0253 }
            r2.access_hash = r6     // Catch:{ Exception -> 0x0253 }
            goto L_0x01f5
        L_0x0216:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile     // Catch:{ Exception -> 0x0253 }
            r2.<init>()     // Catch:{ Exception -> 0x0253 }
            boolean r6 = r5.silent     // Catch:{ Exception -> 0x0253 }
            r2.silent = r6     // Catch:{ Exception -> 0x0253 }
            r2.data = r4     // Catch:{ Exception -> 0x0253 }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x0253 }
            r2.random_id = r6     // Catch:{ Exception -> 0x0253 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r4 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0253 }
            r4.<init>()     // Catch:{ Exception -> 0x0253 }
            r2.peer = r4     // Catch:{ Exception -> 0x0253 }
            int r6 = r0.id     // Catch:{ Exception -> 0x0253 }
            r4.chat_id = r6     // Catch:{ Exception -> 0x0253 }
            long r6 = r0.access_hash     // Catch:{ Exception -> 0x0253 }
            r4.access_hash = r6     // Catch:{ Exception -> 0x0253 }
            r2.file = r1     // Catch:{ Exception -> 0x0253 }
            r8 = r2
        L_0x0237:
            org.telegram.tgnet.ConnectionsManager r9 = r20.getConnectionsManager()     // Catch:{ Exception -> 0x0253 }
            org.telegram.messenger.-$$Lambda$SecretChatHelper$NeIJyTvVk2g1G3EFM6ENqFtjkw0 r10 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$NeIJyTvVk2g1G3EFM6ENqFtjkw0     // Catch:{ Exception -> 0x0253 }
            r1 = r10
            r2 = r20
            r3 = r22
            r4 = r21
            r5 = r23
            r6 = r25
            r7 = r26
            r1.<init>(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0253 }
            r0 = 64
            r9.sendRequest(r8, r10, r0)     // Catch:{ Exception -> 0x0253 }
            goto L_0x0257
        L_0x0253:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0257:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.lambda$performSendEncryptedRequest$7$SecretChatHelper(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$DecryptedMessage, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$InputEncryptedFile, org.telegram.messenger.MessageObject, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$6$SecretChatHelper(TLRPC$DecryptedMessage tLRPC$DecryptedMessage, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$Message tLRPC$Message, MessageObject messageObject, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int i;
        if (tLRPC$TL_error == null && (tLRPC$DecryptedMessage.action instanceof TLRPC$TL_decryptedMessageActionNotifyLayer)) {
            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(tLRPC$EncryptedChat.id));
            if (encryptedChat == null) {
                encryptedChat = tLRPC$EncryptedChat;
            }
            if (encryptedChat.key_hash == null) {
                encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
            }
            if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 46 && encryptedChat.key_hash.length == 16) {
                try {
                    byte[] computeSHA256 = Utilities.computeSHA256(tLRPC$EncryptedChat.auth_key, 0, tLRPC$EncryptedChat.auth_key.length);
                    byte[] bArr = new byte[36];
                    System.arraycopy(tLRPC$EncryptedChat.key_hash, 0, bArr, 0, 16);
                    System.arraycopy(computeSHA256, 0, bArr, 16, 20);
                    encryptedChat.key_hash = bArr;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            this.sendingNotifyLayer.remove(Integer.valueOf(encryptedChat.id));
            encryptedChat.layer = AndroidUtilities.setMyLayerVersion(encryptedChat.layer, 101);
            getMessagesStorage().updateEncryptedChatLayer(encryptedChat);
        }
        if (tLRPC$Message == null) {
            return;
        }
        if (tLRPC$TL_error == null) {
            String str2 = tLRPC$Message.attachPath;
            TLRPC$messages_SentEncryptedMessage tLRPC$messages_SentEncryptedMessage = (TLRPC$messages_SentEncryptedMessage) tLObject;
            if (isSecretVisibleMessage(tLRPC$Message)) {
                tLRPC$Message.date = tLRPC$messages_SentEncryptedMessage.date;
            }
            if (messageObject != null) {
                TLRPC$EncryptedFile tLRPC$EncryptedFile = tLRPC$messages_SentEncryptedMessage.file;
                if (tLRPC$EncryptedFile instanceof TLRPC$TL_encryptedFile) {
                    updateMediaPaths(messageObject, tLRPC$EncryptedFile, tLRPC$DecryptedMessage, str);
                    i = messageObject.getMediaExistanceFlags();
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(tLRPC$Message, tLRPC$messages_SentEncryptedMessage, i, str2) {
                        public final /* synthetic */ TLRPC$Message f$1;
                        public final /* synthetic */ TLRPC$messages_SentEncryptedMessage f$2;
                        public final /* synthetic */ int f$3;
                        public final /* synthetic */ String f$4;

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
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(tLRPC$Message, tLRPC$messages_SentEncryptedMessage, i, str2) {
                public final /* synthetic */ TLRPC$Message f$1;
                public final /* synthetic */ TLRPC$messages_SentEncryptedMessage f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ String f$4;

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
        getMessagesStorage().markMessageAsSendError(tLRPC$Message, false);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message) {
            public final /* synthetic */ TLRPC$Message f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$5$SecretChatHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$SecretChatHelper(TLRPC$Message tLRPC$Message, TLRPC$messages_SentEncryptedMessage tLRPC$messages_SentEncryptedMessage, int i, String str) {
        if (isSecretInvisibleMessage(tLRPC$Message)) {
            tLRPC$messages_SentEncryptedMessage.date = 0;
        }
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message.random_id, Integer.valueOf(tLRPC$Message.id), tLRPC$Message.id, tLRPC$messages_SentEncryptedMessage.date, false, 0, 0);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, i, str) {
            public final /* synthetic */ TLRPC$Message f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ String f$3;

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

    public /* synthetic */ void lambda$null$3$SecretChatHelper(TLRPC$Message tLRPC$Message, int i, String str) {
        tLRPC$Message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(tLRPC$Message.id), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), 0L, Integer.valueOf(i), Boolean.FALSE);
        getSendMessagesHelper().processSentMessage(tLRPC$Message.id);
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message)) {
            getSendMessagesHelper().stopVideoService(str);
        }
        getSendMessagesHelper().removeFromSendingMessages(tLRPC$Message.id, false);
    }

    public /* synthetic */ void lambda$null$5$SecretChatHelper(TLRPC$Message tLRPC$Message) {
        tLRPC$Message.send_state = 2;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message.id));
        getSendMessagesHelper().processSentMessage(tLRPC$Message.id);
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message)) {
            getSendMessagesHelper().stopVideoService(tLRPC$Message.attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(tLRPC$Message.id, false);
    }

    private void applyPeerLayer(TLRPC$EncryptedChat tLRPC$EncryptedChat, int i) {
        int peerLayerVersion = AndroidUtilities.getPeerLayerVersion(tLRPC$EncryptedChat.layer);
        if (i > peerLayerVersion) {
            if (tLRPC$EncryptedChat.key_hash.length == 16 && peerLayerVersion >= 46) {
                try {
                    byte[] computeSHA256 = Utilities.computeSHA256(tLRPC$EncryptedChat.auth_key, 0, tLRPC$EncryptedChat.auth_key.length);
                    byte[] bArr = new byte[36];
                    System.arraycopy(tLRPC$EncryptedChat.key_hash, 0, bArr, 0, 16);
                    System.arraycopy(computeSHA256, 0, bArr, 16, 20);
                    tLRPC$EncryptedChat.key_hash = bArr;
                    getMessagesStorage().updateEncryptedChat(tLRPC$EncryptedChat);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            tLRPC$EncryptedChat.layer = AndroidUtilities.setPeerLayerVersion(tLRPC$EncryptedChat.layer, i);
            getMessagesStorage().updateEncryptedChatLayer(tLRPC$EncryptedChat);
            if (peerLayerVersion < 101) {
                sendNotifyLayerMessage(tLRPC$EncryptedChat, (TLRPC$Message) null);
            }
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$EncryptedChat) {
                public final /* synthetic */ TLRPC$EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$applyPeerLayer$8$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$applyPeerLayer$8$SecretChatHelper(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tLRPC$EncryptedChat);
    }

    /* JADX WARNING: Removed duplicated region for block: B:300:0x07f8  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0809  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.tgnet.TLRPC$Message processDecryptedObject(org.telegram.tgnet.TLRPC$EncryptedChat r16, org.telegram.tgnet.TLRPC$EncryptedFile r17, int r18, org.telegram.tgnet.TLObject r19, boolean r20) {
        /*
            r15 = this;
            r0 = r15
            r1 = r16
            r2 = r17
            r3 = r18
            r4 = r19
            if (r4 == 0) goto L_0x096f
            int r6 = r1.admin_id
            org.telegram.messenger.UserConfig r7 = r15.getUserConfig()
            int r7 = r7.getClientUserId()
            if (r6 != r7) goto L_0x0019
            int r6 = r1.participant_id
        L_0x0019:
            int r7 = r1.layer
            int r7 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r7)
            r8 = 20
            r9 = 0
            if (r7 < r8) goto L_0x003a
            long r7 = r1.exchange_id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x003a
            long r7 = r1.future_key_fingerprint
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x003a
            short r7 = r1.key_use_count_in
            r8 = 120(0x78, float:1.68E-43)
            if (r7 < r8) goto L_0x003a
            r15.requestNewSecretChatKey(r16)
        L_0x003a:
            long r7 = r1.exchange_id
            r11 = 0
            r12 = 256(0x100, float:3.59E-43)
            int r13 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r13 != 0) goto L_0x0059
            long r7 = r1.future_key_fingerprint
            int r13 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r13 == 0) goto L_0x0059
            if (r20 != 0) goto L_0x0059
            byte[] r7 = new byte[r12]
            r1.future_auth_key = r7
            r1.future_key_fingerprint = r9
            org.telegram.messenger.MessagesStorage r7 = r15.getMessagesStorage()
            r7.updateEncryptedChat(r1)
            goto L_0x0086
        L_0x0059:
            long r7 = r1.exchange_id
            int r13 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r13 == 0) goto L_0x0086
            if (r20 == 0) goto L_0x0086
            long r7 = r1.future_key_fingerprint
            r1.key_fingerprint = r7
            byte[] r7 = r1.future_auth_key
            r1.auth_key = r7
            org.telegram.tgnet.ConnectionsManager r7 = r15.getConnectionsManager()
            int r7 = r7.getCurrentTime()
            r1.key_create_date = r7
            byte[] r7 = new byte[r12]
            r1.future_auth_key = r7
            r1.future_key_fingerprint = r9
            r1.key_use_count_in = r11
            r1.key_use_count_out = r11
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r7 = r15.getMessagesStorage()
            r7.updateEncryptedChat(r1)
        L_0x0086:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessage
            r8 = 8
            r13 = 32
            r14 = 1
            if (r7 == 0) goto L_0x0637
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r4 = (org.telegram.tgnet.TLRPC$TL_decryptedMessage) r4
            int r7 = r1.layer
            int r7 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r7)
            r12 = 17
            if (r7 < r12) goto L_0x00a9
            org.telegram.tgnet.TLRPC$TL_message_secret r7 = new org.telegram.tgnet.TLRPC$TL_message_secret
            r7.<init>()
            int r12 = r4.ttl
            r7.ttl = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r12 = r4.entities
            r7.entities = r12
            goto L_0x00b2
        L_0x00a9:
            org.telegram.tgnet.TLRPC$TL_message r7 = new org.telegram.tgnet.TLRPC$TL_message
            r7.<init>()
            int r12 = r1.ttl
            r7.ttl = r12
        L_0x00b2:
            java.lang.String r12 = r4.message
            r7.message = r12
            r7.date = r3
            org.telegram.messenger.UserConfig r12 = r15.getUserConfig()
            int r12 = r12.getNewMessageId()
            r7.id = r12
            r7.local_id = r12
            boolean r12 = r4.silent
            r7.silent = r12
            org.telegram.messenger.UserConfig r12 = r15.getUserConfig()
            r12.saveConfig(r11)
            org.telegram.tgnet.TLRPC$TL_peerUser r12 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r12.<init>()
            r7.from_id = r12
            r12.user_id = r6
            org.telegram.tgnet.TLRPC$TL_peerUser r6 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r6.<init>()
            r7.peer_id = r6
            org.telegram.messenger.UserConfig r12 = r15.getUserConfig()
            int r12 = r12.getClientUserId()
            r6.user_id = r12
            long r5 = r4.random_id
            r7.random_id = r5
            r7.unread = r14
            r5 = 768(0x300, float:1.076E-42)
            r7.flags = r5
            java.lang.String r5 = r4.via_bot_name
            if (r5 == 0) goto L_0x0107
            int r5 = r5.length()
            if (r5 <= 0) goto L_0x0107
            java.lang.String r5 = r4.via_bot_name
            r7.via_bot_name = r5
            int r5 = r7.flags
            r5 = r5 | 2048(0x800, float:2.87E-42)
            r7.flags = r5
        L_0x0107:
            long r5 = r4.grouped_id
            int r12 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r12 == 0) goto L_0x0116
            r7.grouped_id = r5
            int r5 = r7.flags
            r6 = 131072(0x20000, float:1.83671E-40)
            r5 = r5 | r6
            r7.flags = r5
        L_0x0116:
            int r1 = r1.id
            long r5 = (long) r1
            long r5 = r5 << r13
            r7.dialog_id = r5
            long r5 = r4.reply_to_random_id
            int r1 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r1 == 0) goto L_0x0132
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r1 = new org.telegram.tgnet.TLRPC$TL_messageReplyHeader
            r1.<init>()
            r7.reply_to = r1
            long r5 = r4.reply_to_random_id
            r1.reply_to_random_id = r5
            int r1 = r7.flags
            r1 = r1 | r8
            r7.flags = r1
        L_0x0132:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            if (r1 == 0) goto L_0x0611
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty
            if (r5 == 0) goto L_0x013c
            goto L_0x0611
        L_0x013c:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage
            if (r5 == 0) goto L_0x015a
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1.<init>()
            r7.media = r1
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r2 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending
            r2.<init>()
            r1.webpage = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            java.lang.String r2 = r2.url
            r1.url = r2
            goto L_0x0618
        L_0x015a:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact
            java.lang.String r6 = ""
            if (r5 == 0) goto L_0x017d
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact
            r1.<init>()
            r7.media = r1
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            java.lang.String r3 = r2.last_name
            r1.last_name = r3
            java.lang.String r3 = r2.first_name
            r1.first_name = r3
            java.lang.String r3 = r2.phone_number
            r1.phone_number = r3
            int r2 = r2.user_id
            r1.user_id = r2
            r1.vcard = r6
            goto L_0x0618
        L_0x017d:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint
            if (r5 == 0) goto L_0x019f
            org.telegram.tgnet.TLRPC$TL_messageMediaGeo r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            r1.<init>()
            r7.media = r1
            org.telegram.tgnet.TLRPC$TL_geoPoint r2 = new org.telegram.tgnet.TLRPC$TL_geoPoint
            r2.<init>()
            r1.geo = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            double r3 = r2.lat
            r1.lat = r3
            double r2 = r2._long
            r1._long = r2
            goto L_0x0618
        L_0x019f:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto
            r8 = 6000(0x1770, float:8.408E-42)
            r9 = 100
            java.lang.String r10 = "s"
            if (r5 == 0) goto L_0x027a
            byte[] r3 = r1.key
            if (r3 == 0) goto L_0x0278
            int r3 = r3.length
            if (r3 != r13) goto L_0x0278
            byte[] r1 = r1.iv
            if (r1 == 0) goto L_0x0278
            int r1 = r1.length
            if (r1 == r13) goto L_0x01b9
            goto L_0x0278
        L_0x01b9:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            r1.<init>()
            r7.media = r1
            int r3 = r1.flags
            r3 = r3 | 3
            r1.flags = r3
            java.lang.String r1 = r7.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x01d7
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            java.lang.String r1 = r1.caption
            if (r1 == 0) goto L_0x01d5
            r6 = r1
        L_0x01d5:
            r7.message = r6
        L_0x01d7:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$TL_photo r3 = new org.telegram.tgnet.TLRPC$TL_photo
            r3.<init>()
            r1.photo = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            byte[] r3 = new byte[r11]
            r1.file_reference = r3
            int r3 = r7.date
            r1.date = r3
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            r3 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r3
            byte[] r3 = r3.thumb
            if (r3 == 0) goto L_0x0226
            int r5 = r3.length
            if (r5 == 0) goto L_0x0226
            int r5 = r3.length
            if (r5 > r8) goto L_0x0226
            int r5 = r1.thumb_w
            if (r5 > r9) goto L_0x0226
            int r1 = r1.thumb_h
            if (r1 > r9) goto L_0x0226
            org.telegram.tgnet.TLRPC$TL_photoCachedSize r1 = new org.telegram.tgnet.TLRPC$TL_photoCachedSize
            r1.<init>()
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media
            int r6 = r5.thumb_w
            r1.w = r6
            int r5 = r5.thumb_h
            r1.h = r5
            r1.bytes = r3
            r1.type = r10
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r3.<init>()
            r1.location = r3
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            r3.add(r1)
        L_0x0226:
            int r1 = r7.ttl
            if (r1 == 0) goto L_0x0234
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            r3.ttl_seconds = r1
            int r1 = r3.flags
            r1 = r1 | 4
            r3.flags = r1
        L_0x0234:
            org.telegram.tgnet.TLRPC$TL_photoSize r1 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r1.<init>()
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            int r5 = r3.w
            r1.w = r5
            int r3 = r3.h
            r1.h = r3
            java.lang.String r3 = "x"
            r1.type = r3
            int r3 = r2.size
            r1.size = r3
            org.telegram.tgnet.TLRPC$TL_fileEncryptedLocation r3 = new org.telegram.tgnet.TLRPC$TL_fileEncryptedLocation
            r3.<init>()
            r1.location = r3
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r4.media
            byte[] r5 = r4.key
            r3.key = r5
            byte[] r4 = r4.iv
            r3.iv = r4
            int r4 = r2.dc_id
            r3.dc_id = r4
            long r4 = r2.id
            r3.volume_id = r4
            long r4 = r2.access_hash
            r3.secret = r4
            int r2 = r2.key_fingerprint
            r3.local_id = r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            r2.add(r1)
            goto L_0x0618
        L_0x0278:
            r1 = 0
            return r1
        L_0x027a:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo
            if (r5 == 0) goto L_0x036d
            byte[] r5 = r1.key
            if (r5 == 0) goto L_0x036b
            int r5 = r5.length
            if (r5 != r13) goto L_0x036b
            byte[] r1 = r1.iv
            if (r1 == 0) goto L_0x036b
            int r1 = r1.length
            if (r1 == r13) goto L_0x028e
            goto L_0x036b
        L_0x028e:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            r1.<init>()
            r7.media = r1
            int r5 = r1.flags
            r5 = r5 | 3
            r1.flags = r5
            org.telegram.tgnet.TLRPC$TL_documentEncrypted r5 = new org.telegram.tgnet.TLRPC$TL_documentEncrypted
            r5.<init>()
            r1.document = r5
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media
            byte[] r12 = r5.key
            r1.key = r12
            byte[] r5 = r5.iv
            r1.iv = r5
            int r5 = r2.dc_id
            r1.dc_id = r5
            java.lang.String r1 = r7.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            java.lang.String r1 = r1.caption
            if (r1 == 0) goto L_0x02c3
            r6 = r1
        L_0x02c3:
            r7.message = r6
        L_0x02c5:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r1.date = r3
            int r3 = r2.size
            r1.size = r3
            long r5 = r2.id
            r1.id = r5
            long r2 = r2.access_hash
            r1.access_hash = r2
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            java.lang.String r2 = r2.mime_type
            r1.mime_type = r2
            if (r2 != 0) goto L_0x02e3
            java.lang.String r2 = "video/mp4"
            r1.mime_type = r2
        L_0x02e3:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            r2 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r2
            byte[] r2 = r2.thumb
            if (r2 == 0) goto L_0x0315
            int r3 = r2.length
            if (r3 == 0) goto L_0x0315
            int r3 = r2.length
            if (r3 > r8) goto L_0x0315
            int r3 = r1.thumb_w
            if (r3 > r9) goto L_0x0315
            int r1 = r1.thumb_h
            if (r1 > r9) goto L_0x0315
            org.telegram.tgnet.TLRPC$TL_photoCachedSize r1 = new org.telegram.tgnet.TLRPC$TL_photoCachedSize
            r1.<init>()
            r1.bytes = r2
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            int r3 = r2.thumb_w
            r1.w = r3
            int r2 = r2.thumb_h
            r1.h = r2
            r1.type = r10
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r1.location = r2
            goto L_0x031c
        L_0x0315:
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r1 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            r1.<init>()
            r1.type = r10
        L_0x031c:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r2.add(r1)
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            int r2 = r1.flags
            r2 = r2 | r14
            r1.flags = r2
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            int r3 = r2.w
            r1.w = r3
            int r3 = r2.h
            r1.h = r3
            int r2 = r2.duration
            r1.duration = r2
            r1.supports_streaming = r11
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r2.add(r1)
            int r1 = r7.ttl
            if (r1 == 0) goto L_0x035a
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            r2.ttl_seconds = r1
            int r1 = r2.flags
            r1 = r1 | 4
            r2.flags = r1
        L_0x035a:
            int r1 = r7.ttl
            if (r1 == 0) goto L_0x0618
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            int r2 = r2.duration
            int r2 = r2 + r14
            int r1 = java.lang.Math.max(r2, r1)
            r7.ttl = r1
            goto L_0x0618
        L_0x036b:
            r1 = 0
            return r1
        L_0x036d:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument
            if (r5 == 0) goto L_0x04c4
            byte[] r5 = r1.key
            if (r5 == 0) goto L_0x04c2
            int r5 = r5.length
            if (r5 != r13) goto L_0x04c2
            byte[] r1 = r1.iv
            if (r1 == 0) goto L_0x04c2
            int r1 = r1.length
            if (r1 == r13) goto L_0x0381
            goto L_0x04c2
        L_0x0381:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            r1.<init>()
            r7.media = r1
            int r5 = r1.flags
            r5 = r5 | 3
            r1.flags = r5
            java.lang.String r1 = r7.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x03a0
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            java.lang.String r1 = r1.caption
            if (r1 == 0) goto L_0x039d
            goto L_0x039e
        L_0x039d:
            r1 = r6
        L_0x039e:
            r7.message = r1
        L_0x03a0:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$TL_documentEncrypted r5 = new org.telegram.tgnet.TLRPC$TL_documentEncrypted
            r5.<init>()
            r1.document = r5
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            long r11 = r2.id
            r1.id = r11
            long r11 = r2.access_hash
            r1.access_hash = r11
            r1.date = r3
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            java.lang.String r11 = r3.mime_type
            r1.mime_type = r11
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument_layer8
            if (r11 == 0) goto L_0x03d6
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r1.<init>()
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            java.lang.String r3 = r3.file_name
            r1.file_name = r3
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            r3.add(r1)
            goto L_0x03da
        L_0x03d6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            r1.attributes = r3
        L_0x03da:
            int r1 = r7.ttl
            if (r1 <= 0) goto L_0x041b
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r1.attributes
            int r1 = r1.size()
            r11 = 0
        L_0x03e9:
            if (r11 >= r1) goto L_0x040e
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            java.lang.Object r3 = r3.get(r11)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r5 != 0) goto L_0x0403
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            if (r5 == 0) goto L_0x0400
            goto L_0x0403
        L_0x0400:
            int r11 = r11 + 1
            goto L_0x03e9
        L_0x0403:
            int r1 = r3.duration
            int r1 = r1 + r14
            int r3 = r7.ttl
            int r1 = java.lang.Math.max(r1, r3)
            r7.ttl = r1
        L_0x040e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            int r1 = r1.duration
            int r1 = r1 + r14
            int r3 = r7.ttl
            int r1 = java.lang.Math.max(r1, r3)
            r7.ttl = r1
        L_0x041b:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            int r3 = r3.size
            if (r3 == 0) goto L_0x042c
            int r5 = r2.size
            int r3 = java.lang.Math.min(r3, r5)
            goto L_0x042e
        L_0x042c:
            int r3 = r2.size
        L_0x042e:
            r1.size = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            byte[] r5 = r3.key
            r1.key = r5
            byte[] r3 = r3.iv
            r1.iv = r3
            java.lang.String r3 = r1.mime_type
            if (r3 != 0) goto L_0x0445
            r1.mime_type = r6
            goto L_0x0463
        L_0x0445:
            java.lang.String r1 = "application/x-tgsticker"
            boolean r1 = r1.equals(r3)
            if (r1 != 0) goto L_0x045b
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r1 = r1.mime_type
            java.lang.String r3 = "application/x-tgsdice"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0463
        L_0x045b:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r3 = "application/x-bad_tgsticker"
            r1.mime_type = r3
        L_0x0463:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            r3 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r3
            byte[] r3 = r3.thumb
            if (r3 == 0) goto L_0x0495
            int r5 = r3.length
            if (r5 == 0) goto L_0x0495
            int r5 = r3.length
            if (r5 > r8) goto L_0x0495
            int r5 = r1.thumb_w
            if (r5 > r9) goto L_0x0495
            int r1 = r1.thumb_h
            if (r1 > r9) goto L_0x0495
            org.telegram.tgnet.TLRPC$TL_photoCachedSize r1 = new org.telegram.tgnet.TLRPC$TL_photoCachedSize
            r1.<init>()
            r1.bytes = r3
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            int r4 = r3.thumb_w
            r1.w = r4
            int r3 = r3.thumb_h
            r1.h = r3
            r1.type = r10
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r3.<init>()
            r1.location = r3
            goto L_0x049c
        L_0x0495:
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r1 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            r1.<init>()
            r1.type = r10
        L_0x049c:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            r3.add(r1)
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            int r3 = r1.flags
            r3 = r3 | r14
            r1.flags = r3
            int r2 = r2.dc_id
            r1.dc_id = r2
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceMessage(r7)
            if (r1 != 0) goto L_0x04be
            boolean r1 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r7)
            if (r1 == 0) goto L_0x0618
        L_0x04be:
            r7.media_unread = r14
            goto L_0x0618
        L_0x04c2:
            r1 = 0
            return r1
        L_0x04c4:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument
            if (r8 == 0) goto L_0x052f
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            r1.<init>()
            r7.media = r1
            int r2 = r1.flags
            r2 = r2 | 3
            r1.flags = r2
            r7.message = r6
            org.telegram.tgnet.TLRPC$TL_document r2 = new org.telegram.tgnet.TLRPC$TL_document
            r2.<init>()
            r1.document = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            long r3 = r2.id
            r1.id = r3
            long r3 = r2.access_hash
            r1.access_hash = r3
            r3 = 0
            byte[] r4 = new byte[r3]
            r1.file_reference = r4
            int r3 = r2.date
            r1.date = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes
            r1.attributes = r3
            java.lang.String r3 = r2.mime_type
            r1.mime_type = r3
            int r3 = r2.dc_id
            r1.dc_id = r3
            int r3 = r2.size
            r1.size = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.thumbs
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r2
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r2.thumb
            r1.add(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            int r2 = r1.flags
            r2 = r2 | r14
            r1.flags = r2
            java.lang.String r2 = r1.mime_type
            if (r2 != 0) goto L_0x051d
            r1.mime_type = r6
        L_0x051d:
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerMessage(r7)
            if (r1 == 0) goto L_0x0618
            r1 = 0
            r7.stickerVerified = r1
            org.telegram.messenger.MediaDataController r1 = r15.getMediaDataController()
            r1.verifyAnimatedStickerMessage(r7, r14)
            goto L_0x0618
        L_0x052f:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaAudio
            if (r5 == 0) goto L_0x05dc
            byte[] r5 = r1.key
            if (r5 == 0) goto L_0x05da
            int r5 = r5.length
            if (r5 != r13) goto L_0x05da
            byte[] r1 = r1.iv
            if (r1 == 0) goto L_0x05da
            int r1 = r1.length
            if (r1 == r13) goto L_0x0543
            goto L_0x05da
        L_0x0543:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            r1.<init>()
            r7.media = r1
            int r5 = r1.flags
            r5 = r5 | 3
            r1.flags = r5
            org.telegram.tgnet.TLRPC$TL_documentEncrypted r5 = new org.telegram.tgnet.TLRPC$TL_documentEncrypted
            r5.<init>()
            r1.document = r5
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media
            byte[] r8 = r5.key
            r1.key = r8
            byte[] r8 = r5.iv
            r1.iv = r8
            long r8 = r2.id
            r1.id = r8
            long r8 = r2.access_hash
            r1.access_hash = r8
            r1.date = r3
            int r3 = r2.size
            r1.size = r3
            int r2 = r2.dc_id
            r1.dc_id = r2
            java.lang.String r2 = r5.mime_type
            r1.mime_type = r2
            java.lang.String r1 = r7.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x058c
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media
            java.lang.String r1 = r1.caption
            if (r1 == 0) goto L_0x058a
            r6 = r1
        L_0x058a:
            r7.message = r6
        L_0x058c:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.lang.String r2 = r1.mime_type
            if (r2 != 0) goto L_0x0598
            java.lang.String r2 = "audio/ogg"
            r1.mime_type = r2
        L_0x0598:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r1.<init>()
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            int r2 = r2.duration
            r1.duration = r2
            r1.voice = r14
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r2.add(r1)
            int r1 = r7.ttl
            if (r1 == 0) goto L_0x05bd
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media
            int r2 = r2.duration
            int r2 = r2 + r14
            int r1 = java.lang.Math.max(r2, r1)
            r7.ttl = r1
        L_0x05bd:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.thumbs
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0618
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r1 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            r1.<init>()
            r1.type = r10
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r2.add(r1)
            goto L_0x0618
        L_0x05da:
            r1 = 0
            return r1
        L_0x05dc:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue
            if (r1 == 0) goto L_0x060f
            org.telegram.tgnet.TLRPC$TL_messageMediaVenue r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            r1.<init>()
            r7.media = r1
            org.telegram.tgnet.TLRPC$TL_geoPoint r2 = new org.telegram.tgnet.TLRPC$TL_geoPoint
            r2.<init>()
            r1.geo = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r1.geo
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media
            double r4 = r3.lat
            r2.lat = r4
            double r4 = r3._long
            r2._long = r4
            java.lang.String r2 = r3.title
            r1.title = r2
            java.lang.String r2 = r3.address
            r1.address = r2
            java.lang.String r2 = r3.provider
            r1.provider = r2
            java.lang.String r2 = r3.venue_id
            r1.venue_id = r2
            r1.venue_type = r6
            goto L_0x0618
        L_0x060f:
            r1 = 0
            return r1
        L_0x0611:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r7.media = r1
        L_0x0618:
            int r1 = r7.ttl
            if (r1 == 0) goto L_0x062a
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            int r3 = r2.ttl_seconds
            if (r3 != 0) goto L_0x062a
            r2.ttl_seconds = r1
            int r1 = r2.flags
            r1 = r1 | 4
            r2.flags = r1
        L_0x062a:
            java.lang.String r1 = r7.message
            if (r1 == 0) goto L_0x0636
            r2 = 8238(0x202e, float:1.1544E-41)
            java.lang.String r1 = r1.replace(r2, r13)
            r7.message = r1
        L_0x0636:
            return r7
        L_0x0637:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageService
            if (r2 == 0) goto L_0x0956
            r2 = r4
            org.telegram.tgnet.TLRPC$TL_decryptedMessageService r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageService) r2
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r4 = r2.action
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL
            if (r7 != 0) goto L_0x08d9
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages
            if (r7 == 0) goto L_0x064a
            goto L_0x08d9
        L_0x064a:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionFlushHistory
            if (r3 == 0) goto L_0x065c
            int r1 = r1.id
            long r1 = (long) r1
            long r1 = r1 << r13
            org.telegram.messenger.-$$Lambda$SecretChatHelper$OJ7QWMtzGqvNXSkDUc1v2NRta0c r3 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$OJ7QWMtzGqvNXSkDUc1v2NRta0c
            r3.<init>(r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            r1 = 0
            return r1
        L_0x065c:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionDeleteMessages
            if (r3 == 0) goto L_0x0673
            java.util.ArrayList<java.lang.Long> r1 = r4.random_ids
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0671
            java.util.ArrayList<java.lang.Long> r1 = r0.pendingEncMessagesToDelete
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.random_ids
            r1.addAll(r2)
        L_0x0671:
            r1 = 0
            return r1
        L_0x0673:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionReadMessages
            if (r3 == 0) goto L_0x0698
            java.util.ArrayList<java.lang.Long> r3 = r4.random_ids
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0978
            org.telegram.tgnet.ConnectionsManager r3 = r15.getConnectionsManager()
            int r7 = r3.getCurrentTime()
            org.telegram.messenger.MessagesStorage r4 = r15.getMessagesStorage()
            int r5 = r1.id
            r8 = 1
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r1 = r2.action
            java.util.ArrayList<java.lang.Long> r9 = r1.random_ids
            r6 = r7
            r4.createTaskForSecretChat(r5, r6, r7, r8, r9)
            goto L_0x0978
        L_0x0698:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNotifyLayer
            if (r3 == 0) goto L_0x06a3
            int r2 = r4.layer
            r15.applyPeerLayer(r1, r2)
            goto L_0x0978
        L_0x06a3:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionRequestKey
            if (r3 == 0) goto L_0x076d
            long r6 = r1.exchange_id
            int r3 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x06c3
            long r3 = r4.exchange_id
            int r9 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r9 <= 0) goto L_0x06bf
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x06bd
            java.lang.String r1 = "we already have request key with higher exchange_id"
            org.telegram.messenger.FileLog.d(r1)
        L_0x06bd:
            r3 = 0
            return r3
        L_0x06bf:
            r3 = 0
            r15.sendAbortKeyMessage(r1, r3, r6)
        L_0x06c3:
            byte[] r3 = new byte[r12]
            java.security.SecureRandom r4 = org.telegram.messenger.Utilities.random
            r4.nextBytes(r3)
            java.math.BigInteger r4 = new java.math.BigInteger
            org.telegram.messenger.MessagesStorage r6 = r15.getMessagesStorage()
            byte[] r6 = r6.getSecretPBytes()
            r4.<init>(r14, r6)
            org.telegram.messenger.MessagesStorage r6 = r15.getMessagesStorage()
            int r6 = r6.getSecretG()
            long r6 = (long) r6
            java.math.BigInteger r6 = java.math.BigInteger.valueOf(r6)
            java.math.BigInteger r7 = new java.math.BigInteger
            r7.<init>(r14, r3)
            java.math.BigInteger r6 = r6.modPow(r7, r4)
            java.math.BigInteger r7 = new java.math.BigInteger
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r9 = r2.action
            byte[] r9 = r9.g_a
            r7.<init>(r14, r9)
            boolean r9 = org.telegram.messenger.Utilities.isGoodGaAndGb(r7, r4)
            if (r9 != 0) goto L_0x0705
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            long r2 = r2.exchange_id
            r4 = 0
            r15.sendAbortKeyMessage(r1, r4, r2)
            return r4
        L_0x0705:
            byte[] r6 = r6.toByteArray()
            int r9 = r6.length
            if (r9 <= r12) goto L_0x0713
            byte[] r9 = new byte[r12]
            r5 = 0
            java.lang.System.arraycopy(r6, r14, r9, r5, r12)
            r6 = r9
        L_0x0713:
            java.math.BigInteger r9 = new java.math.BigInteger
            r9.<init>(r14, r3)
            java.math.BigInteger r3 = r7.modPow(r9, r4)
            byte[] r3 = r3.toByteArray()
            int r4 = r3.length
            if (r4 <= r12) goto L_0x072d
            byte[] r4 = new byte[r12]
            int r7 = r3.length
            int r7 = r7 - r12
            r5 = 0
            java.lang.System.arraycopy(r3, r7, r4, r5, r12)
        L_0x072b:
            r3 = r4
            goto L_0x0745
        L_0x072d:
            r5 = 0
            int r4 = r3.length
            if (r4 >= r12) goto L_0x0745
            byte[] r4 = new byte[r12]
            int r7 = r3.length
            int r7 = 256 - r7
            int r9 = r3.length
            java.lang.System.arraycopy(r3, r5, r4, r7, r9)
            r7 = 0
        L_0x073b:
            int r9 = r3.length
            int r9 = 256 - r9
            if (r7 >= r9) goto L_0x072b
            r4[r7] = r5
            int r7 = r7 + 1
            goto L_0x073b
        L_0x0745:
            byte[] r4 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r3)
            byte[] r7 = new byte[r8]
            int r9 = r4.length
            int r9 = r9 - r8
            java.lang.System.arraycopy(r4, r9, r7, r5, r8)
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            long r4 = r2.exchange_id
            r1.exchange_id = r4
            r1.future_auth_key = r3
            long r2 = org.telegram.messenger.Utilities.bytesToLong(r7)
            r1.future_key_fingerprint = r2
            r1.g_a_or_b = r6
            org.telegram.messenger.MessagesStorage r2 = r15.getMessagesStorage()
            r2.updateEncryptedChat(r1)
            r2 = 0
            r15.sendAcceptKeyMessage(r1, r2)
            goto L_0x0978
        L_0x076d:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAcceptKey
            if (r3 == 0) goto L_0x083b
            long r6 = r1.exchange_id
            long r3 = r4.exchange_id
            int r11 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x0822
            java.math.BigInteger r3 = new java.math.BigInteger
            org.telegram.messenger.MessagesStorage r4 = r15.getMessagesStorage()
            byte[] r4 = r4.getSecretPBytes()
            r3.<init>(r14, r4)
            java.math.BigInteger r4 = new java.math.BigInteger
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r6 = r2.action
            byte[] r6 = r6.g_b
            r4.<init>(r14, r6)
            boolean r6 = org.telegram.messenger.Utilities.isGoodGaAndGb(r4, r3)
            if (r6 != 0) goto L_0x07ad
            byte[] r3 = new byte[r12]
            r1.future_auth_key = r3
            r1.future_key_fingerprint = r9
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r3 = r15.getMessagesStorage()
            r3.updateEncryptedChat(r1)
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            long r2 = r2.exchange_id
            r4 = 0
            r15.sendAbortKeyMessage(r1, r4, r2)
            return r4
        L_0x07ad:
            java.math.BigInteger r6 = new java.math.BigInteger
            byte[] r7 = r1.a_or_b
            r6.<init>(r14, r7)
            java.math.BigInteger r3 = r4.modPow(r6, r3)
            byte[] r3 = r3.toByteArray()
            int r4 = r3.length
            if (r4 <= r12) goto L_0x07c9
            byte[] r4 = new byte[r12]
            int r6 = r3.length
            int r6 = r6 - r12
            r5 = 0
            java.lang.System.arraycopy(r3, r6, r4, r5, r12)
        L_0x07c7:
            r3 = r4
            goto L_0x07e1
        L_0x07c9:
            r5 = 0
            int r4 = r3.length
            if (r4 >= r12) goto L_0x07e1
            byte[] r4 = new byte[r12]
            int r6 = r3.length
            int r6 = 256 - r6
            int r7 = r3.length
            java.lang.System.arraycopy(r3, r5, r4, r6, r7)
            r6 = 0
        L_0x07d7:
            int r7 = r3.length
            int r7 = 256 - r7
            if (r6 >= r7) goto L_0x07c7
            r4[r6] = r5
            int r6 = r6 + 1
            goto L_0x07d7
        L_0x07e1:
            byte[] r4 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r3)
            byte[] r6 = new byte[r8]
            int r7 = r4.length
            int r7 = r7 - r8
            java.lang.System.arraycopy(r4, r7, r6, r5, r8)
            long r4 = org.telegram.messenger.Utilities.bytesToLong(r6)
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r6 = r2.action
            long r6 = r6.key_fingerprint
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 != 0) goto L_0x0809
            r1.future_auth_key = r3
            r1.future_key_fingerprint = r4
            org.telegram.messenger.MessagesStorage r2 = r15.getMessagesStorage()
            r2.updateEncryptedChat(r1)
            r3 = 0
            r15.sendCommitKeyMessage(r1, r3)
            goto L_0x0978
        L_0x0809:
            r3 = 0
            byte[] r4 = new byte[r12]
            r1.future_auth_key = r4
            r1.future_key_fingerprint = r9
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r4 = r15.getMessagesStorage()
            r4.updateEncryptedChat(r1)
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            long r4 = r2.exchange_id
            r15.sendAbortKeyMessage(r1, r3, r4)
            goto L_0x0978
        L_0x0822:
            r3 = 0
            byte[] r4 = new byte[r12]
            r1.future_auth_key = r4
            r1.future_key_fingerprint = r9
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r4 = r15.getMessagesStorage()
            r4.updateEncryptedChat(r1)
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            long r4 = r2.exchange_id
            r15.sendAbortKeyMessage(r1, r3, r4)
            goto L_0x0978
        L_0x083b:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionCommitKey
            if (r3 == 0) goto L_0x0894
            long r6 = r1.exchange_id
            long r13 = r4.exchange_id
            int r3 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r3 != 0) goto L_0x087b
            long r6 = r1.future_key_fingerprint
            long r3 = r4.key_fingerprint
            int r8 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r8 != 0) goto L_0x087b
            long r2 = r1.key_fingerprint
            byte[] r4 = r1.auth_key
            r1.key_fingerprint = r6
            byte[] r6 = r1.future_auth_key
            r1.auth_key = r6
            org.telegram.tgnet.ConnectionsManager r6 = r15.getConnectionsManager()
            int r6 = r6.getCurrentTime()
            r1.key_create_date = r6
            r1.future_auth_key = r4
            r1.future_key_fingerprint = r2
            r2 = 0
            r1.key_use_count_in = r2
            r1.key_use_count_out = r2
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r2 = r15.getMessagesStorage()
            r2.updateEncryptedChat(r1)
            r2 = 0
            r15.sendNoopMessage(r1, r2)
            goto L_0x0978
        L_0x087b:
            byte[] r3 = new byte[r12]
            r1.future_auth_key = r3
            r1.future_key_fingerprint = r9
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r3 = r15.getMessagesStorage()
            r3.updateEncryptedChat(r1)
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            long r2 = r2.exchange_id
            r4 = 0
            r15.sendAbortKeyMessage(r1, r4, r2)
            goto L_0x0978
        L_0x0894:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAbortKey
            if (r3 == 0) goto L_0x08b1
            long r2 = r1.exchange_id
            long r4 = r4.exchange_id
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0978
            byte[] r2 = new byte[r12]
            r1.future_auth_key = r2
            r1.future_key_fingerprint = r9
            r1.exchange_id = r9
            org.telegram.messenger.MessagesStorage r2 = r15.getMessagesStorage()
            r2.updateEncryptedChat(r1)
            goto L_0x0978
        L_0x08b1:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNoop
            if (r3 == 0) goto L_0x08b7
            goto L_0x0978
        L_0x08b7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionResend
            if (r3 == 0) goto L_0x08d7
            int r3 = r4.end_seq_no
            int r5 = r1.in_seq_no
            if (r3 < r5) goto L_0x08d5
            int r6 = r4.start_seq_no
            if (r3 >= r6) goto L_0x08c6
            goto L_0x08d5
        L_0x08c6:
            if (r6 >= r5) goto L_0x08ca
            r4.start_seq_no = r5
        L_0x08ca:
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            int r3 = r2.start_seq_no
            int r2 = r2.end_seq_no
            r15.resendMessages(r3, r2, r1)
            goto L_0x0978
        L_0x08d5:
            r1 = 0
            return r1
        L_0x08d7:
            r1 = 0
            return r1
        L_0x08d9:
            org.telegram.tgnet.TLRPC$TL_messageService r4 = new org.telegram.tgnet.TLRPC$TL_messageService
            r4.<init>()
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r7 = r2.action
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL
            if (r8 == 0) goto L_0x090c
            org.telegram.tgnet.TLRPC$TL_messageEncryptedAction r7 = new org.telegram.tgnet.TLRPC$TL_messageEncryptedAction
            r7.<init>()
            r4.action = r7
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r7 = r2.action
            int r7 = r7.ttl_seconds
            r8 = 31536000(0x1e13380, float:8.2725845E-38)
            if (r7 < 0) goto L_0x08f6
            if (r7 <= r8) goto L_0x08fa
        L_0x08f6:
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r7 = r2.action
            r7.ttl_seconds = r8
        L_0x08fa:
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            int r7 = r2.ttl_seconds
            r1.ttl = r7
            org.telegram.tgnet.TLRPC$MessageAction r7 = r4.action
            r7.encryptedAction = r2
            org.telegram.messenger.MessagesStorage r2 = r15.getMessagesStorage()
            r2.updateEncryptedChatTTL(r1)
            goto L_0x091b
        L_0x090c:
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages
            if (r7 == 0) goto L_0x091b
            org.telegram.tgnet.TLRPC$TL_messageEncryptedAction r7 = new org.telegram.tgnet.TLRPC$TL_messageEncryptedAction
            r7.<init>()
            r4.action = r7
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action
            r7.encryptedAction = r2
        L_0x091b:
            org.telegram.messenger.UserConfig r2 = r15.getUserConfig()
            int r2 = r2.getNewMessageId()
            r4.id = r2
            r4.local_id = r2
            org.telegram.messenger.UserConfig r2 = r15.getUserConfig()
            r5 = 0
            r2.saveConfig(r5)
            r4.unread = r14
            r4.flags = r12
            r4.date = r3
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r4.from_id = r2
            r2.user_id = r6
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r4.peer_id = r2
            org.telegram.messenger.UserConfig r3 = r15.getUserConfig()
            int r3 = r3.getClientUserId()
            r2.user_id = r3
            int r1 = r1.id
            long r1 = (long) r1
            long r1 = r1 << r13
            r4.dialog_id = r1
            return r4
        L_0x0956:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0978
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "unknown message "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r1)
            goto L_0x0978
        L_0x096f:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0978
            java.lang.String r1 = "unknown TLObject"
            org.telegram.messenger.FileLog.e((java.lang.String) r1)
        L_0x0978:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.processDecryptedObject(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$EncryptedFile, int, org.telegram.tgnet.TLObject, boolean):org.telegram.tgnet.TLRPC$Message");
    }

    public /* synthetic */ void lambda$processDecryptedObject$11$SecretChatHelper(long j) {
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.unread_count = 0;
            getMessagesController().dialogMessage.remove(tLRPC$Dialog.id);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j) {
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$10$SecretChatHelper(this.f$1);
            }
        });
        getMessagesStorage().deleteDialog(j, 1);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.FALSE);
    }

    public /* synthetic */ void lambda$null$10$SecretChatHelper(long j) {
        AndroidUtilities.runOnUIThread(new Runnable(j) {
            public final /* synthetic */ long f$1;

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

    private TLRPC$Message createDeleteMessage(int i, int i2, int i3, long j, TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        TLRPC$TL_messageService tLRPC$TL_messageService = new TLRPC$TL_messageService();
        TLRPC$TL_messageEncryptedAction tLRPC$TL_messageEncryptedAction = new TLRPC$TL_messageEncryptedAction();
        tLRPC$TL_messageService.action = tLRPC$TL_messageEncryptedAction;
        tLRPC$TL_messageEncryptedAction.encryptedAction = new TLRPC$TL_decryptedMessageActionDeleteMessages();
        tLRPC$TL_messageService.action.encryptedAction.random_ids.add(Long.valueOf(j));
        tLRPC$TL_messageService.id = i;
        tLRPC$TL_messageService.local_id = i;
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        tLRPC$TL_messageService.from_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
        tLRPC$TL_messageService.unread = true;
        tLRPC$TL_messageService.out = true;
        tLRPC$TL_messageService.flags = 256;
        tLRPC$TL_messageService.dialog_id = ((long) tLRPC$EncryptedChat.id) << 32;
        tLRPC$TL_messageService.send_state = 1;
        tLRPC$TL_messageService.seq_in = i3;
        tLRPC$TL_messageService.seq_out = i2;
        tLRPC$TL_messageService.peer_id = new TLRPC$TL_peerUser();
        if (tLRPC$EncryptedChat.participant_id == getUserConfig().getClientUserId()) {
            tLRPC$TL_messageService.peer_id.user_id = tLRPC$EncryptedChat.admin_id;
        } else {
            tLRPC$TL_messageService.peer_id.user_id = tLRPC$EncryptedChat.participant_id;
        }
        tLRPC$TL_messageService.date = 0;
        tLRPC$TL_messageService.random_id = j;
        return tLRPC$TL_messageService;
    }

    private void resendMessages(int i, int i2, TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat != null && i2 - i >= 0) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, tLRPC$EncryptedChat, i2) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ TLRPC$EncryptedChat f$2;
                public final /* synthetic */ int f$3;

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

    public /* synthetic */ void lambda$resendMessages$14$SecretChatHelper(int i, TLRPC$EncryptedChat tLRPC$EncryptedChat, int i2) {
        long j;
        ArrayList arrayList;
        TLRPC$Message tLRPC$Message;
        TLRPC$EncryptedChat tLRPC$EncryptedChat2 = tLRPC$EncryptedChat;
        try {
            int i3 = (tLRPC$EncryptedChat2.admin_id == getUserConfig().getClientUserId() && i % 2 == 0) ? i + 1 : i;
            int i4 = 5;
            int i5 = 1;
            int i6 = 2;
            int i7 = 3;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(tLRPC$EncryptedChat2.id), Integer.valueOf(i3), Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i2)}), new Object[0]);
            boolean next = queryFinalized.next();
            queryFinalized.dispose();
            if (!next) {
                long j2 = ((long) tLRPC$EncryptedChat2.id) << 32;
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
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                        byteBufferValue.reuse();
                        TLdeserialize.random_id = j3;
                        TLdeserialize.dialog_id = j2;
                        TLdeserialize.seq_in = intValue;
                        TLdeserialize.seq_out = intValue2;
                        TLdeserialize.ttl = queryFinalized2.intValue(4);
                        j = j2;
                        tLRPC$Message = TLdeserialize;
                        arrayList = arrayList2;
                    } else {
                        j = j2;
                        arrayList = arrayList2;
                        tLRPC$Message = createDeleteMessage(intValue3, intValue2, intValue, j3, tLRPC$EncryptedChat);
                    }
                    arrayList.add(tLRPC$Message);
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
                        arrayList3.add(createDeleteMessage(getUserConfig().getNewMessageId(), keyAt, keyAt + 1, Utilities.random.nextLong(), tLRPC$EncryptedChat));
                    }
                    getUserConfig().saveConfig(false);
                }
                Collections.sort(arrayList3, $$Lambda$SecretChatHelper$O20rGLXtuSs3IvLeQHrDUrNritc.INSTANCE);
                ArrayList arrayList4 = new ArrayList();
                arrayList4.add(tLRPC$EncryptedChat2);
                try {
                    AndroidUtilities.runOnUIThread(new Runnable(arrayList3) {
                        public final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SecretChatHelper.this.lambda$null$13$SecretChatHelper(this.f$1);
                        }
                    });
                    getSendMessagesHelper().processUnsentMessages(arrayList3, (ArrayList<TLRPC$Message>) null, new ArrayList(), new ArrayList(), arrayList4);
                    getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(tLRPC$EncryptedChat2.id), Integer.valueOf(i3), Integer.valueOf(i2)})).stepThis().dispose();
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
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList.get(i), false, true);
            messageObject.resendAsIs = true;
            getSendMessagesHelper().retrySendMessage(messageObject, true);
        }
    }

    public void checkSecretHoles(TLRPC$EncryptedChat tLRPC$EncryptedChat, ArrayList<TLRPC$Message> arrayList) {
        ArrayList arrayList2 = this.secretHolesQueue.get(tLRPC$EncryptedChat.id);
        if (arrayList2 != null) {
            Collections.sort(arrayList2, $$Lambda$SecretChatHelper$XChl_gDRHQHDfwtxghrPUY1XhL4.INSTANCE);
            boolean z = false;
            while (arrayList2.size() > 0) {
                TL_decryptedMessageHolder tL_decryptedMessageHolder = (TL_decryptedMessageHolder) arrayList2.get(0);
                int i = tL_decryptedMessageHolder.layer.out_seq_no;
                int i2 = tLRPC$EncryptedChat.seq_in;
                if (i != i2 && i2 != i - 2) {
                    break;
                }
                applyPeerLayer(tLRPC$EncryptedChat, tL_decryptedMessageHolder.layer.layer);
                TLRPC$TL_decryptedMessageLayer tLRPC$TL_decryptedMessageLayer = tL_decryptedMessageHolder.layer;
                tLRPC$EncryptedChat.seq_in = tLRPC$TL_decryptedMessageLayer.out_seq_no;
                tLRPC$EncryptedChat.in_seq_no = tLRPC$TL_decryptedMessageLayer.in_seq_no;
                arrayList2.remove(0);
                if (tL_decryptedMessageHolder.decryptedWithVersion == 2) {
                    tLRPC$EncryptedChat.mtproto_seq = Math.min(tLRPC$EncryptedChat.mtproto_seq, tLRPC$EncryptedChat.seq_in);
                }
                TLRPC$Message processDecryptedObject = processDecryptedObject(tLRPC$EncryptedChat, tL_decryptedMessageHolder.file, tL_decryptedMessageHolder.date, tL_decryptedMessageHolder.layer.message, tL_decryptedMessageHolder.new_key_used);
                if (processDecryptedObject != null) {
                    arrayList.add(processDecryptedObject);
                }
                z = true;
            }
            if (arrayList2.isEmpty()) {
                this.secretHolesQueue.remove(tLRPC$EncryptedChat.id);
            }
            if (z) {
                getMessagesStorage().updateEncryptedChatSeq(tLRPC$EncryptedChat, true);
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
    public java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> decryptMessage(org.telegram.tgnet.TLRPC$EncryptedMessage r22) {
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
            boolean r1 = r11 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            if (r1 == 0) goto L_0x001a
            goto L_0x028c
        L_0x001a:
            boolean r1 = r11 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChatWaiting     // Catch:{ Exception -> 0x0288 }
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
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer) r1     // Catch:{ Exception -> 0x0288 }
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
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageService     // Catch:{ Exception -> 0x0288 }
            if (r2 == 0) goto L_0x026d
            r2 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageService r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageService) r2     // Catch:{ Exception -> 0x0288 }
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r2 = r2.action     // Catch:{ Exception -> 0x0288 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNotifyLayer     // Catch:{ Exception -> 0x0288 }
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

    public /* synthetic */ void lambda$decryptMessage$16$SecretChatHelper(TLRPC$TL_encryptedChatDiscarded tLRPC$TL_encryptedChatDiscarded) {
        getMessagesController().putEncryptedChat(tLRPC$TL_encryptedChatDiscarded, false);
        getMessagesStorage().updateEncryptedChat(tLRPC$TL_encryptedChatDiscarded);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tLRPC$TL_encryptedChatDiscarded);
    }

    public void requestNewSecretChatKey(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (AndroidUtilities.getPeerLayerVersion(tLRPC$EncryptedChat.layer) >= 20) {
            byte[] bArr = new byte[256];
            Utilities.random.nextBytes(bArr);
            byte[] byteArray = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr2 = new byte[256];
                System.arraycopy(byteArray, 1, bArr2, 0, 256);
                byteArray = bArr2;
            }
            tLRPC$EncryptedChat.exchange_id = getSendMessagesHelper().getNextRandomId();
            tLRPC$EncryptedChat.a_or_b = bArr;
            tLRPC$EncryptedChat.g_a = byteArray;
            getMessagesStorage().updateEncryptedChat(tLRPC$EncryptedChat);
            sendRequestKeyMessage(tLRPC$EncryptedChat, (TLRPC$Message) null);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00b4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processAcceptedSecretChat(org.telegram.tgnet.TLRPC$EncryptedChat r9) {
        /*
            r8 = this;
            java.math.BigInteger r0 = new java.math.BigInteger
            org.telegram.messenger.MessagesStorage r1 = r8.getMessagesStorage()
            byte[] r1 = r1.getSecretPBytes()
            r2 = 1
            r0.<init>(r2, r1)
            java.math.BigInteger r1 = new java.math.BigInteger
            byte[] r3 = r9.g_a_or_b
            r1.<init>(r2, r3)
            boolean r3 = org.telegram.messenger.Utilities.isGoodGaAndGb(r1, r0)
            if (r3 != 0) goto L_0x0021
            int r9 = r9.id
            r8.declineSecretChat(r9)
            return
        L_0x0021:
            java.math.BigInteger r3 = new java.math.BigInteger
            byte[] r4 = r9.a_or_b
            r3.<init>(r2, r4)
            java.math.BigInteger r0 = r1.modPow(r3, r0)
            byte[] r0 = r0.toByteArray()
            int r1 = r0.length
            r3 = 0
            r4 = 256(0x100, float:3.59E-43)
            if (r1 <= r4) goto L_0x003f
            byte[] r1 = new byte[r4]
            int r5 = r0.length
            int r5 = r5 - r4
            java.lang.System.arraycopy(r0, r5, r1, r3, r4)
        L_0x003d:
            r0 = r1
            goto L_0x0056
        L_0x003f:
            int r1 = r0.length
            if (r1 >= r4) goto L_0x0056
            byte[] r1 = new byte[r4]
            int r5 = r0.length
            int r5 = 256 - r5
            int r6 = r0.length
            java.lang.System.arraycopy(r0, r3, r1, r5, r6)
            r5 = 0
        L_0x004c:
            int r6 = r0.length
            int r6 = 256 - r6
            if (r5 >= r6) goto L_0x003d
            r1[r5] = r3
            int r5 = r5 + 1
            goto L_0x004c
        L_0x0056:
            byte[] r1 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r0)
            r4 = 8
            byte[] r5 = new byte[r4]
            int r6 = r1.length
            int r6 = r6 - r4
            java.lang.System.arraycopy(r1, r6, r5, r3, r4)
            long r4 = org.telegram.messenger.Utilities.bytesToLong(r5)
            long r6 = r9.key_fingerprint
            int r1 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r1 != 0) goto L_0x00b4
            r9.auth_key = r0
            org.telegram.tgnet.ConnectionsManager r0 = r8.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r9.key_create_date = r0
            r0 = -2
            r9.seq_in = r0
            r9.seq_out = r2
            org.telegram.messenger.MessagesStorage r0 = r8.getMessagesStorage()
            r0.updateEncryptedChat(r9)
            org.telegram.messenger.MessagesController r0 = r8.getMessagesController()
            r0.putEncryptedChat(r9, r3)
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Update>> r0 = r8.pendingSecretMessages
            int r1 = r9.id
            java.lang.Object r0 = r0.get(r1)
            r2 = r0
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 == 0) goto L_0x00ab
            org.telegram.messenger.MessagesController r1 = r8.getMessagesController()
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r1.processUpdateArray(r2, r3, r4, r5, r6)
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Update>> r0 = r8.pendingSecretMessages
            int r1 = r9.id
            r0.remove(r1)
        L_0x00ab:
            org.telegram.messenger.-$$Lambda$SecretChatHelper$tDKre2aQQBiVO0S8VAHIlXCNFCM r0 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$tDKre2aQQBiVO0S8VAHIlXCNFCM
            r0.<init>(r9)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x00f5
        L_0x00b4:
            org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded r0 = new org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded
            r0.<init>()
            int r1 = r9.id
            r0.id = r1
            int r1 = r9.user_id
            r0.user_id = r1
            byte[] r1 = r9.auth_key
            r0.auth_key = r1
            int r1 = r9.key_create_date
            r0.key_create_date = r1
            short r1 = r9.key_use_count_in
            r0.key_use_count_in = r1
            short r1 = r9.key_use_count_out
            r0.key_use_count_out = r1
            int r1 = r9.seq_in
            r0.seq_in = r1
            int r1 = r9.seq_out
            r0.seq_out = r1
            int r1 = r9.admin_id
            r0.admin_id = r1
            int r1 = r9.mtproto_seq
            r0.mtproto_seq = r1
            org.telegram.messenger.MessagesStorage r1 = r8.getMessagesStorage()
            r1.updateEncryptedChat(r0)
            org.telegram.messenger.-$$Lambda$SecretChatHelper$D9qtWTcc8U_wHAaEgu6hZuZwoaE r1 = new org.telegram.messenger.-$$Lambda$SecretChatHelper$D9qtWTcc8U_wHAaEgu6hZuZwoaE
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            int r9 = r9.id
            r8.declineSecretChat(r9)
        L_0x00f5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.processAcceptedSecretChat(org.telegram.tgnet.TLRPC$EncryptedChat):void");
    }

    public /* synthetic */ void lambda$processAcceptedSecretChat$17$SecretChatHelper(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tLRPC$EncryptedChat);
        sendNotifyLayerMessage(tLRPC$EncryptedChat, (TLRPC$Message) null);
    }

    public /* synthetic */ void lambda$processAcceptedSecretChat$18$SecretChatHelper(TLRPC$TL_encryptedChatDiscarded tLRPC$TL_encryptedChatDiscarded) {
        getMessagesController().putEncryptedChat(tLRPC$TL_encryptedChatDiscarded, false);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tLRPC$TL_encryptedChatDiscarded);
    }

    public void declineSecretChat(int i) {
        TLRPC$TL_messages_discardEncryption tLRPC$TL_messages_discardEncryption = new TLRPC$TL_messages_discardEncryption();
        tLRPC$TL_messages_discardEncryption.chat_id = i;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_discardEncryption, $$Lambda$SecretChatHelper$mfQ9CBiHkuJ2sHYijldk5vxoP4M.INSTANCE);
    }

    public void acceptSecretChat(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (this.acceptingChats.get(tLRPC$EncryptedChat.id) == null) {
            this.acceptingChats.put(tLRPC$EncryptedChat.id, tLRPC$EncryptedChat);
            TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
            tLRPC$TL_messages_getDhConfig.random_length = 256;
            tLRPC$TL_messages_getDhConfig.version = getMessagesStorage().getLastSecretVersion();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate(tLRPC$EncryptedChat) {
                public final /* synthetic */ TLRPC$EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SecretChatHelper.this.lambda$acceptSecretChat$22$SecretChatHelper(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$acceptSecretChat$22$SecretChatHelper(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        byte[] bArr;
        if (tLRPC$TL_error == null) {
            TLRPC$messages_DhConfig tLRPC$messages_DhConfig = (TLRPC$messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC$TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(tLRPC$messages_DhConfig.p, tLRPC$messages_DhConfig.g)) {
                    this.acceptingChats.remove(tLRPC$EncryptedChat.id);
                    declineSecretChat(tLRPC$EncryptedChat.id);
                    return;
                }
                getMessagesStorage().setSecretPBytes(tLRPC$messages_DhConfig.p);
                getMessagesStorage().setSecretG(tLRPC$messages_DhConfig.g);
                getMessagesStorage().setLastSecretVersion(tLRPC$messages_DhConfig.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] bArr2 = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr2[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ tLRPC$messages_DhConfig.random[i]);
            }
            tLRPC$EncryptedChat.a_or_b = bArr2;
            tLRPC$EncryptedChat.seq_in = -1;
            tLRPC$EncryptedChat.seq_out = 0;
            BigInteger bigInteger = new BigInteger(1, getMessagesStorage().getSecretPBytes());
            BigInteger modPow = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr2), bigInteger);
            BigInteger bigInteger2 = new BigInteger(1, tLRPC$EncryptedChat.g_a);
            if (!Utilities.isGoodGaAndGb(bigInteger2, bigInteger)) {
                this.acceptingChats.remove(tLRPC$EncryptedChat.id);
                declineSecretChat(tLRPC$EncryptedChat.id);
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
            } else {
                if (byteArray2.length < 256) {
                    bArr = new byte[256];
                    System.arraycopy(byteArray2, 0, bArr, 256 - byteArray2.length, byteArray2.length);
                    for (int i2 = 0; i2 < 256 - byteArray2.length; i2++) {
                        bArr[i2] = 0;
                    }
                }
                byte[] computeSHA1 = Utilities.computeSHA1(byteArray2);
                byte[] bArr4 = new byte[8];
                System.arraycopy(computeSHA1, computeSHA1.length - 8, bArr4, 0, 8);
                tLRPC$EncryptedChat.auth_key = byteArray2;
                tLRPC$EncryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
                TLRPC$TL_messages_acceptEncryption tLRPC$TL_messages_acceptEncryption = new TLRPC$TL_messages_acceptEncryption();
                tLRPC$TL_messages_acceptEncryption.g_b = byteArray;
                TLRPC$TL_inputEncryptedChat tLRPC$TL_inputEncryptedChat = new TLRPC$TL_inputEncryptedChat();
                tLRPC$TL_messages_acceptEncryption.peer = tLRPC$TL_inputEncryptedChat;
                tLRPC$TL_inputEncryptedChat.chat_id = tLRPC$EncryptedChat.id;
                tLRPC$TL_inputEncryptedChat.access_hash = tLRPC$EncryptedChat.access_hash;
                tLRPC$TL_messages_acceptEncryption.key_fingerprint = Utilities.bytesToLong(bArr4);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_acceptEncryption, new RequestDelegate(tLRPC$EncryptedChat) {
                    public final /* synthetic */ TLRPC$EncryptedChat f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        SecretChatHelper.this.lambda$null$21$SecretChatHelper(this.f$1, tLObject, tLRPC$TL_error);
                    }
                }, 64);
                return;
            }
            byteArray2 = bArr;
            byte[] computeSHA12 = Utilities.computeSHA1(byteArray2);
            byte[] bArr42 = new byte[8];
            System.arraycopy(computeSHA12, computeSHA12.length - 8, bArr42, 0, 8);
            tLRPC$EncryptedChat.auth_key = byteArray2;
            tLRPC$EncryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
            TLRPC$TL_messages_acceptEncryption tLRPC$TL_messages_acceptEncryption2 = new TLRPC$TL_messages_acceptEncryption();
            tLRPC$TL_messages_acceptEncryption2.g_b = byteArray;
            TLRPC$TL_inputEncryptedChat tLRPC$TL_inputEncryptedChat2 = new TLRPC$TL_inputEncryptedChat();
            tLRPC$TL_messages_acceptEncryption2.peer = tLRPC$TL_inputEncryptedChat2;
            tLRPC$TL_inputEncryptedChat2.chat_id = tLRPC$EncryptedChat.id;
            tLRPC$TL_inputEncryptedChat2.access_hash = tLRPC$EncryptedChat.access_hash;
            tLRPC$TL_messages_acceptEncryption2.key_fingerprint = Utilities.bytesToLong(bArr42);
            getConnectionsManager().sendRequest(tLRPC$TL_messages_acceptEncryption2, new RequestDelegate(tLRPC$EncryptedChat) {
                public final /* synthetic */ TLRPC$EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SecretChatHelper.this.lambda$null$21$SecretChatHelper(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 64);
            return;
        }
        this.acceptingChats.remove(tLRPC$EncryptedChat.id);
    }

    public /* synthetic */ void lambda$null$21$SecretChatHelper(TLRPC$EncryptedChat tLRPC$EncryptedChat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.acceptingChats.remove(tLRPC$EncryptedChat.id);
        if (tLRPC$TL_error == null) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat2 = (TLRPC$EncryptedChat) tLObject;
            tLRPC$EncryptedChat2.auth_key = tLRPC$EncryptedChat.auth_key;
            tLRPC$EncryptedChat2.user_id = tLRPC$EncryptedChat.user_id;
            tLRPC$EncryptedChat2.seq_in = tLRPC$EncryptedChat.seq_in;
            tLRPC$EncryptedChat2.seq_out = tLRPC$EncryptedChat.seq_out;
            tLRPC$EncryptedChat2.key_create_date = tLRPC$EncryptedChat.key_create_date;
            tLRPC$EncryptedChat2.key_use_count_in = tLRPC$EncryptedChat.key_use_count_in;
            tLRPC$EncryptedChat2.key_use_count_out = tLRPC$EncryptedChat.key_use_count_out;
            getMessagesStorage().updateEncryptedChat(tLRPC$EncryptedChat2);
            getMessagesController().putEncryptedChat(tLRPC$EncryptedChat2, false);
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$EncryptedChat2) {
                public final /* synthetic */ TLRPC$EncryptedChat f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SecretChatHelper.this.lambda$null$20$SecretChatHelper(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$20$SecretChatHelper(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, tLRPC$EncryptedChat);
        sendNotifyLayerMessage(tLRPC$EncryptedChat, (TLRPC$Message) null);
    }

    public void startSecretChat(Context context, TLRPC$User tLRPC$User) {
        if (tLRPC$User != null && context != null) {
            this.startingSecretChat = true;
            AlertDialog alertDialog = new AlertDialog(context, 3);
            TLRPC$TL_messages_getDhConfig tLRPC$TL_messages_getDhConfig = new TLRPC$TL_messages_getDhConfig();
            tLRPC$TL_messages_getDhConfig.random_length = 256;
            tLRPC$TL_messages_getDhConfig.version = getMessagesStorage().getLastSecretVersion();
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(tLRPC$TL_messages_getDhConfig, new RequestDelegate(context, alertDialog, tLRPC$User) {
                public final /* synthetic */ Context f$1;
                public final /* synthetic */ AlertDialog f$2;
                public final /* synthetic */ TLRPC$User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SecretChatHelper.this.lambda$startSecretChat$29$SecretChatHelper(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            }, 2)) {
                public final /* synthetic */ int f$1;

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

    public /* synthetic */ void lambda$startSecretChat$29$SecretChatHelper(Context context, AlertDialog alertDialog, TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_DhConfig tLRPC$messages_DhConfig = (TLRPC$messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC$TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(tLRPC$messages_DhConfig.p, tLRPC$messages_DhConfig.g)) {
                    AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog) {
                        public final /* synthetic */ Context f$0;
                        public final /* synthetic */ AlertDialog f$1;

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
                getMessagesStorage().setSecretPBytes(tLRPC$messages_DhConfig.p);
                getMessagesStorage().setSecretG(tLRPC$messages_DhConfig.g);
                getMessagesStorage().setLastSecretVersion(tLRPC$messages_DhConfig.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] bArr = new byte[256];
            for (int i = 0; i < 256; i++) {
                bArr[i] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ tLRPC$messages_DhConfig.random[i]);
            }
            byte[] byteArray = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, bArr), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (byteArray.length > 256) {
                byte[] bArr2 = new byte[256];
                System.arraycopy(byteArray, 1, bArr2, 0, 256);
                byteArray = bArr2;
            }
            TLRPC$TL_messages_requestEncryption tLRPC$TL_messages_requestEncryption = new TLRPC$TL_messages_requestEncryption();
            tLRPC$TL_messages_requestEncryption.g_a = byteArray;
            tLRPC$TL_messages_requestEncryption.user_id = getMessagesController().getInputUser(tLRPC$User);
            tLRPC$TL_messages_requestEncryption.random_id = Utilities.random.nextInt();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_requestEncryption, new RequestDelegate(context, alertDialog, bArr, tLRPC$User) {
                public final /* synthetic */ Context f$1;
                public final /* synthetic */ AlertDialog f$2;
                public final /* synthetic */ byte[] f$3;
                public final /* synthetic */ TLRPC$User f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SecretChatHelper.this.lambda$null$27$SecretChatHelper(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            }, 2);
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog) {
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ AlertDialog f$2;

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

    public /* synthetic */ void lambda$null$27$SecretChatHelper(Context context, AlertDialog alertDialog, byte[] bArr, TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(context, alertDialog, tLObject, bArr, tLRPC$User) {
                public final /* synthetic */ Context f$1;
                public final /* synthetic */ AlertDialog f$2;
                public final /* synthetic */ TLObject f$3;
                public final /* synthetic */ byte[] f$4;
                public final /* synthetic */ TLRPC$User f$5;

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
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SecretChatHelper.this.lambda$null$26$SecretChatHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$25$SecretChatHelper(Context context, AlertDialog alertDialog, TLObject tLObject, byte[] bArr, TLRPC$User tLRPC$User) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        TLRPC$EncryptedChat tLRPC$EncryptedChat = (TLRPC$EncryptedChat) tLObject;
        tLRPC$EncryptedChat.user_id = tLRPC$EncryptedChat.participant_id;
        tLRPC$EncryptedChat.seq_in = -2;
        tLRPC$EncryptedChat.seq_out = 1;
        tLRPC$EncryptedChat.a_or_b = bArr;
        getMessagesController().putEncryptedChat(tLRPC$EncryptedChat, false);
        TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
        tLRPC$TL_dialog.id = DialogObject.makeSecretDialogId(tLRPC$EncryptedChat.id);
        tLRPC$TL_dialog.unread_count = 0;
        tLRPC$TL_dialog.top_message = 0;
        tLRPC$TL_dialog.last_message_date = getConnectionsManager().getCurrentTime();
        getMessagesController().dialogs_dict.put(tLRPC$TL_dialog.id, tLRPC$TL_dialog);
        getMessagesController().allDialogs.add(tLRPC$TL_dialog);
        getMessagesController().sortDialogs((SparseArray<TLRPC$Chat>) null);
        getMessagesStorage().putEncryptedChat(tLRPC$EncryptedChat, tLRPC$User, tLRPC$TL_dialog);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatCreated, tLRPC$EncryptedChat);
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                SecretChatHelper.this.lambda$null$24$SecretChatHelper();
            }
        });
    }

    public /* synthetic */ void lambda$null$24$SecretChatHelper() {
        if (!this.delayedEncryptedChatUpdates.isEmpty()) {
            getMessagesController().processUpdateArray(this.delayedEncryptedChatUpdates, (ArrayList<TLRPC$User>) null, (ArrayList<TLRPC$Chat>) null, false, 0);
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
