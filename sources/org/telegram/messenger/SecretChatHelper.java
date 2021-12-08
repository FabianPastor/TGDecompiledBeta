package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public class SecretChatHelper extends BaseController {
    public static int CURRENT_SECRET_CHAT_LAYER = 101;
    private static volatile SecretChatHelper[] Instance = new SecretChatHelper[3];
    private SparseArray<TLRPC.EncryptedChat> acceptingChats = new SparseArray<>();
    public ArrayList<TLRPC.Update> delayedEncryptedChatUpdates = new ArrayList<>();
    private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList<>();
    private SparseArray<ArrayList<TLRPC.Update>> pendingSecretMessages = new SparseArray<>();
    private SparseArray<SparseIntArray> requestedHoles = new SparseArray<>();
    private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray<>();
    private ArrayList<Integer> sendingNotifyLayer = new ArrayList<>();
    private boolean startingSecretChat = false;

    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = NUM;
        public int date;
        public int decryptedWithVersion;
        public TLRPC.EncryptedFile file;
        public TLRPC.TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        public void readParams(AbstractSerializedData stream, boolean exception) {
            stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.layer = TLRPC.TL_decryptedMessageLayer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (stream.readBool(exception)) {
                this.file = TLRPC.EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.new_key_used = stream.readBool(exception);
        }

        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(0);
            stream.writeInt32(this.date);
            this.layer.serializeToStream(stream);
            stream.writeBool(this.file != null);
            TLRPC.EncryptedFile encryptedFile = this.file;
            if (encryptedFile != null) {
                encryptedFile.serializeToStream(stream);
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
        super(instance);
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
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda2(this, new ArrayList<>(this.pendingEncMessagesToDelete)));
            getMessagesStorage().markMessagesAsDeletedByRandoms(new ArrayList<>(this.pendingEncMessagesToDelete));
            this.pendingEncMessagesToDelete.clear();
        }
    }

    /* renamed from: lambda$processPendingEncMessages$0$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1144xc3b53c7c(ArrayList pendingEncMessagesToDeleteCopy) {
        for (int a = 0; a < pendingEncMessagesToDeleteCopy.size(); a++) {
            MessageObject messageObject = getMessagesController().dialogMessagesByRandomIds.get(((Long) pendingEncMessagesToDeleteCopy.get(a)).longValue());
            if (messageObject != null) {
                messageObject.deleted = true;
            }
        }
    }

    private TLRPC.TL_messageService createServiceSecretMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.DecryptedMessageAction decryptedMessage) {
        TLRPC.TL_messageService newMsg = new TLRPC.TL_messageService();
        newMsg.action = new TLRPC.TL_messageEncryptedAction();
        newMsg.action.encryptedAction = decryptedMessage;
        int newMessageId = getUserConfig().getNewMessageId();
        newMsg.id = newMessageId;
        newMsg.local_id = newMessageId;
        newMsg.from_id = new TLRPC.TL_peerUser();
        newMsg.from_id.user_id = getUserConfig().getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = DialogObject.makeEncryptedDialogId((long) encryptedChat.id);
        newMsg.peer_id = new TLRPC.TL_peerUser();
        newMsg.send_state = 1;
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            newMsg.peer_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.peer_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessage instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (decryptedMessage instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
            newMsg.date = getConnectionsManager().getCurrentTime();
        } else {
            newMsg.date = 0;
        }
        newMsg.random_id = getSendMessagesHelper().getNextRandomId();
        getUserConfig().saveConfig(false);
        ArrayList<TLRPC.Message> arr = new ArrayList<>();
        arr.add(newMsg);
        getMessagesStorage().putMessages(arr, false, true, true, 0, false);
        return newMsg;
    }

    public void sendMessagesReadMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionReadMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    /* access modifiers changed from: protected */
    public void processUpdateEncryption(TLRPC.TL_updateEncryption update, ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap) {
        TLRPC.EncryptedChat newChat = update.chat;
        long dialog_id = DialogObject.makeEncryptedDialogId((long) newChat.id);
        TLRPC.EncryptedChat existingChat = getMessagesController().getEncryptedChatDB(newChat.id, false);
        if ((newChat instanceof TLRPC.TL_encryptedChatRequested) && existingChat == null) {
            long userId = newChat.participant_id;
            if (userId == getUserConfig().getClientUserId()) {
                userId = newChat.admin_id;
            }
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(userId));
            if (user == null) {
                user = concurrentHashMap.get(Long.valueOf(userId));
            }
            newChat.user_id = userId;
            TLRPC.Dialog dialog = new TLRPC.TL_dialog();
            dialog.id = dialog_id;
            dialog.folder_id = newChat.folder_id;
            dialog.unread_count = 0;
            dialog.top_message = 0;
            dialog.last_message_date = update.date;
            getMessagesController().putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda4(this, dialog, dialog_id));
            getMessagesStorage().putEncryptedChat(newChat, user, dialog);
            acceptSecretChat(newChat);
        } else if (!(newChat instanceof TLRPC.TL_encryptedChat)) {
            TLRPC.EncryptedChat exist = existingChat;
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
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda9(this, exist, newChat));
        } else if ((existingChat instanceof TLRPC.TL_encryptedChatWaiting) && (existingChat.auth_key == null || existingChat.auth_key.length == 1)) {
            newChat.a_or_b = existingChat.a_or_b;
            newChat.user_id = existingChat.user_id;
            processAcceptedSecretChat(newChat);
        } else if (existingChat == null && this.startingSecretChat) {
            this.delayedEncryptedChatUpdates.add(update);
        }
        if ((newChat instanceof TLRPC.TL_encryptedChatDiscarded) && newChat.history_deleted) {
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda29(this, dialog_id));
        }
    }

    /* renamed from: lambda$processUpdateEncryption$1$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1145xe82e9eba(TLRPC.Dialog dialog, long dialog_id) {
        if (dialog.folder_id == 1) {
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            editor.putBoolean("dialog_bar_archived" + dialog_id, true);
            editor.commit();
        }
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs((LongSparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* renamed from: lambda$processUpdateEncryption$2$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1146x74cec9bb(TLRPC.EncryptedChat exist, TLRPC.EncryptedChat newChat) {
        if (exist != null) {
            getMessagesController().putEncryptedChat(newChat, false);
        }
        getMessagesStorage().updateEncryptedChat(newChat);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    /* renamed from: lambda$processUpdateEncryption$3$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1147x16ef4bc(long dialog_id) {
        getMessagesController().deleteDialog(dialog_id, 0);
    }

    public void sendMessagesDeleteMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionDeleteMessages();
                reqSend.action.random_ids = random_ids;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendClearHistoryMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionFlushHistory();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNotifyLayerMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if ((encryptedChat instanceof TLRPC.TL_encryptedChat) && !this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionNotifyLayer();
                reqSend.action.layer = CURRENT_SECRET_CHAT_LAYER;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendRequestKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionRequestKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.g_a = encryptedChat.g_a;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAcceptKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionAcceptKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                reqSend.action.g_b = encryptedChat.g_a_or_b;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendCommitKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionCommitKey();
                reqSend.action.exchange_id = encryptedChat.exchange_id;
                reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendAbortKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage, long excange_id) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionAbortKey();
                reqSend.action.exchange_id = excange_id;
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendNoopMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionNoop();
                message = createServiceSecretMessage(encryptedChat, reqSend.action);
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendResendMessage(TLRPC.EncryptedChat encryptedChat, int start, int end, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            SparseIntArray array = this.requestedHoles.get(encryptedChat.id);
            if (array == null || array.indexOfKey(start) < 0) {
                if (array == null) {
                    array = new SparseIntArray();
                    this.requestedHoles.put(encryptedChat.id, array);
                }
                array.put(start, end);
                TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
                if (resendMessage != null) {
                    TLRPC.Message message2 = resendMessage;
                    reqSend.action = message2.action.encryptedAction;
                    message = message2;
                } else {
                    reqSend.action = new TLRPC.TL_decryptedMessageActionResend();
                    reqSend.action.start_seq_no = start;
                    reqSend.action.end_seq_no = end;
                    message = createServiceSecretMessage(encryptedChat, reqSend.action);
                }
                reqSend.random_id = message.random_id;
                performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
            }
        }
    }

    public void sendTTLMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionSetMessageTTL();
                reqSend.action.ttl_seconds = encryptedChat.ttl;
                TLRPC.Message createServiceSecretMessage = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(this.currentAccount, createServiceSecretMessage, false, false);
                newMsgObj.messageOwner.send_state = 1;
                newMsgObj.wasJustSent = true;
                ArrayList<MessageObject> objArr = new ArrayList<>();
                objArr.add(newMsgObj);
                getMessagesController().updateInterfaceWithMessages(createServiceSecretMessage.dialog_id, objArr, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                message = createServiceSecretMessage;
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    public void sendScreenshotMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
            TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
            if (resendMessage != null) {
                TLRPC.Message message2 = resendMessage;
                reqSend.action = message2.action.encryptedAction;
                message = message2;
            } else {
                reqSend.action = new TLRPC.TL_decryptedMessageActionScreenshotMessages();
                reqSend.action.random_ids = random_ids;
                TLRPC.Message createServiceSecretMessage = createServiceSecretMessage(encryptedChat, reqSend.action);
                MessageObject newMsgObj = new MessageObject(this.currentAccount, createServiceSecretMessage, false, false);
                newMsgObj.messageOwner.send_state = 1;
                newMsgObj.wasJustSent = true;
                ArrayList<MessageObject> objArr = new ArrayList<>();
                objArr.add(newMsgObj);
                getMessagesController().updateInterfaceWithMessages(createServiceSecretMessage.dialog_id, objArr, false);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                message = createServiceSecretMessage;
            }
            reqSend.random_id = message.random_id;
            performSendEncryptedRequest(reqSend, message, encryptedChat, (TLRPC.InputEncryptedFile) null, (String) null, (MessageObject) null);
        }
    }

    private void updateMediaPaths(MessageObject newMsgObj, TLRPC.EncryptedFile file, TLRPC.DecryptedMessage decryptedMessage, String originalPath) {
        MessageObject messageObject = newMsgObj;
        TLRPC.EncryptedFile encryptedFile = file;
        TLRPC.DecryptedMessage decryptedMessage2 = decryptedMessage;
        TLRPC.Message newMsg = messageObject.messageOwner;
        if (encryptedFile == null) {
            return;
        }
        if ((newMsg.media instanceof TLRPC.TL_messageMediaPhoto) && newMsg.media.photo != null) {
            TLRPC.PhotoSize size = newMsg.media.photo.sizes.get(newMsg.media.photo.sizes.size() - 1);
            String fileName = size.location.volume_id + "_" + size.location.local_id;
            size.location = new TLRPC.TL_fileEncryptedLocation();
            size.location.key = decryptedMessage2.media.key;
            size.location.iv = decryptedMessage2.media.iv;
            size.location.dc_id = encryptedFile.dc_id;
            size.location.volume_id = encryptedFile.id;
            size.location.secret = encryptedFile.access_hash;
            size.location.local_id = encryptedFile.key_fingerprint;
            new File(FileLoader.getDirectory(4), fileName + ".jpg").renameTo(FileLoader.getPathToAttach(size));
            ImageLoader.getInstance().replaceImageInCache(fileName, size.location.volume_id + "_" + size.location.local_id, ImageLocation.getForPhoto(size, newMsg.media.photo), true);
            ArrayList<TLRPC.Message> arr = new ArrayList<>();
            arr.add(newMsg);
            getMessagesStorage().putMessages(arr, false, true, false, 0, false);
        } else if ((newMsg.media instanceof TLRPC.TL_messageMediaDocument) && newMsg.media.document != null) {
            TLRPC.Document document = newMsg.media.document;
            newMsg.media.document = new TLRPC.TL_documentEncrypted();
            newMsg.media.document.id = encryptedFile.id;
            newMsg.media.document.access_hash = encryptedFile.access_hash;
            newMsg.media.document.date = document.date;
            newMsg.media.document.attributes = document.attributes;
            newMsg.media.document.mime_type = document.mime_type;
            newMsg.media.document.size = encryptedFile.size;
            newMsg.media.document.key = decryptedMessage2.media.key;
            newMsg.media.document.iv = decryptedMessage2.media.iv;
            newMsg.media.document.thumbs = document.thumbs;
            newMsg.media.document.dc_id = encryptedFile.dc_id;
            if (newMsg.media.document.thumbs.isEmpty()) {
                TLRPC.PhotoSize thumb = new TLRPC.TL_photoSizeEmpty();
                thumb.type = "s";
                newMsg.media.document.thumbs.add(thumb);
            }
            if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath()) && new File(newMsg.attachPath).renameTo(FileLoader.getPathToAttach(newMsg.media.document))) {
                messageObject.mediaExists = messageObject.attachPathExists;
                messageObject.attachPathExists = false;
                newMsg.attachPath = "";
            }
            ArrayList<TLRPC.Message> arr2 = new ArrayList<>();
            arr2.add(newMsg);
            getMessagesStorage().putMessages(arr2, false, true, false, 0, false);
        }
    }

    public static boolean isSecretVisibleMessage(TLRPC.Message message) {
        return (message.action instanceof TLRPC.TL_messageEncryptedAction) && ((message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL));
    }

    public static boolean isSecretInvisibleMessage(TLRPC.Message message) {
        return (message.action instanceof TLRPC.TL_messageEncryptedAction) && !(message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) && !(message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL);
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC.TL_messages_sendEncryptedMultiMedia req, SendMessagesHelper.DelayedMessage message) {
        for (int a = 0; a < req.files.size(); a++) {
            performSendEncryptedRequest(req.messages.get(a), message.messages.get(a), message.encryptedChat, req.files.get(a), message.originalPaths.get(a), message.messageObjects.get(a));
        }
    }

    /* access modifiers changed from: protected */
    public void performSendEncryptedRequest(TLRPC.DecryptedMessage req, TLRPC.Message newMsgObj, TLRPC.EncryptedChat chat, TLRPC.InputEncryptedFile encryptedFile, String originalPath, MessageObject newMsg) {
        TLRPC.EncryptedChat encryptedChat = chat;
        if (req == null || encryptedChat.auth_key == null || (encryptedChat instanceof TLRPC.TL_encryptedChatRequested)) {
            TLRPC.Message message = newMsgObj;
        } else if (encryptedChat instanceof TLRPC.TL_encryptedChatWaiting) {
            TLRPC.Message message2 = newMsgObj;
        } else {
            TLRPC.Message message3 = newMsgObj;
            getSendMessagesHelper().putToSendingMessages(newMsgObj, false);
            Utilities.stageQueue.postRunnable(new SecretChatHelper$$ExternalSyntheticLambda8(this, chat, req, newMsgObj, encryptedFile, newMsg, originalPath));
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$performSendEncryptedRequest$8$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1138x85cvar_a(org.telegram.tgnet.TLRPC.EncryptedChat r28, org.telegram.tgnet.TLRPC.DecryptedMessage r29, org.telegram.tgnet.TLRPC.Message r30, org.telegram.tgnet.TLRPC.InputEncryptedFile r31, org.telegram.messenger.MessageObject r32, java.lang.String r33) {
        /*
            r27 = this;
            r8 = r28
            r9 = r29
            r10 = r30
            r11 = r31
            org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer r0 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer     // Catch:{ Exception -> 0x0278 }
            r0.<init>()     // Catch:{ Exception -> 0x0278 }
            int r1 = r8.layer     // Catch:{ Exception -> 0x0278 }
            int r1 = org.telegram.messenger.AndroidUtilities.getMyLayerVersion(r1)     // Catch:{ Exception -> 0x0278 }
            r2 = 46
            int r1 = java.lang.Math.max(r2, r1)     // Catch:{ Exception -> 0x0278 }
            r12 = r1
            int r1 = r8.layer     // Catch:{ Exception -> 0x0278 }
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)     // Catch:{ Exception -> 0x0278 }
            int r1 = java.lang.Math.max(r2, r1)     // Catch:{ Exception -> 0x0278 }
            int r1 = java.lang.Math.min(r12, r1)     // Catch:{ Exception -> 0x0278 }
            r0.layer = r1     // Catch:{ Exception -> 0x0278 }
            r0.message = r9     // Catch:{ Exception -> 0x0278 }
            r1 = 15
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x0278 }
            r0.random_bytes = r1     // Catch:{ Exception -> 0x0278 }
            java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0278 }
            byte[] r2 = r0.random_bytes     // Catch:{ Exception -> 0x0278 }
            r1.nextBytes(r2)     // Catch:{ Exception -> 0x0278 }
            r13 = r0
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x0278 }
            r2 = 1
            if (r1 != 0) goto L_0x005a
            int r1 = r8.seq_out     // Catch:{ Exception -> 0x0278 }
            if (r1 != 0) goto L_0x005a
            long r3 = r8.admin_id     // Catch:{ Exception -> 0x0278 }
            org.telegram.messenger.UserConfig r1 = r27.getUserConfig()     // Catch:{ Exception -> 0x0278 }
            long r5 = r1.getClientUserId()     // Catch:{ Exception -> 0x0278 }
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x0057
            r8.seq_out = r2     // Catch:{ Exception -> 0x0278 }
            r1 = -2
            r8.seq_in = r1     // Catch:{ Exception -> 0x0278 }
            goto L_0x005a
        L_0x0057:
            r1 = -1
            r8.seq_in = r1     // Catch:{ Exception -> 0x0278 }
        L_0x005a:
            int r1 = r10.seq_in     // Catch:{ Exception -> 0x0278 }
            r3 = 2
            r4 = 0
            if (r1 != 0) goto L_0x00d1
            int r1 = r10.seq_out     // Catch:{ Exception -> 0x0278 }
            if (r1 != 0) goto L_0x00d1
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x0278 }
            if (r1 <= 0) goto L_0x006b
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x0278 }
            goto L_0x006e
        L_0x006b:
            int r1 = r8.seq_in     // Catch:{ Exception -> 0x0278 }
            int r1 = r1 + r3
        L_0x006e:
            r0.in_seq_no = r1     // Catch:{ Exception -> 0x0278 }
            int r1 = r8.seq_out     // Catch:{ Exception -> 0x0278 }
            r0.out_seq_no = r1     // Catch:{ Exception -> 0x0278 }
            int r1 = r8.seq_out     // Catch:{ Exception -> 0x0278 }
            int r1 = r1 + r3
            r8.seq_out = r1     // Catch:{ Exception -> 0x0278 }
            int r1 = r8.key_create_date     // Catch:{ Exception -> 0x0278 }
            if (r1 != 0) goto L_0x0087
            org.telegram.tgnet.ConnectionsManager r1 = r27.getConnectionsManager()     // Catch:{ Exception -> 0x0278 }
            int r1 = r1.getCurrentTime()     // Catch:{ Exception -> 0x0278 }
            r8.key_create_date = r1     // Catch:{ Exception -> 0x0278 }
        L_0x0087:
            short r1 = r8.key_use_count_out     // Catch:{ Exception -> 0x0278 }
            int r1 = r1 + r2
            short r1 = (short) r1     // Catch:{ Exception -> 0x0278 }
            r8.key_use_count_out = r1     // Catch:{ Exception -> 0x0278 }
            short r1 = r8.key_use_count_out     // Catch:{ Exception -> 0x0278 }
            r5 = 100
            if (r1 >= r5) goto L_0x00a3
            int r1 = r8.key_create_date     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.ConnectionsManager r5 = r27.getConnectionsManager()     // Catch:{ Exception -> 0x0278 }
            int r5 = r5.getCurrentTime()     // Catch:{ Exception -> 0x0278 }
            r6 = 604800(0x93a80, float:8.47505E-40)
            int r5 = r5 - r6
            if (r1 >= r5) goto L_0x00b4
        L_0x00a3:
            long r5 = r8.exchange_id     // Catch:{ Exception -> 0x0278 }
            r14 = 0
            int r1 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x00b4
            long r5 = r8.future_key_fingerprint     // Catch:{ Exception -> 0x0278 }
            int r1 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x00b4
            r27.requestNewSecretChatKey(r28)     // Catch:{ Exception -> 0x0278 }
        L_0x00b4:
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0278 }
            r1.updateEncryptedChatSeq(r8, r4)     // Catch:{ Exception -> 0x0278 }
            int r1 = r0.in_seq_no     // Catch:{ Exception -> 0x0278 }
            r10.seq_in = r1     // Catch:{ Exception -> 0x0278 }
            int r1 = r0.out_seq_no     // Catch:{ Exception -> 0x0278 }
            r10.seq_out = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0278 }
            int r5 = r10.id     // Catch:{ Exception -> 0x0278 }
            int r6 = r10.seq_in     // Catch:{ Exception -> 0x0278 }
            int r7 = r10.seq_out     // Catch:{ Exception -> 0x0278 }
            r1.setMessageSeq(r5, r6, r7)     // Catch:{ Exception -> 0x0278 }
            goto L_0x00d9
        L_0x00d1:
            int r1 = r10.seq_in     // Catch:{ Exception -> 0x0278 }
            r0.in_seq_no = r1     // Catch:{ Exception -> 0x0278 }
            int r1 = r10.seq_out     // Catch:{ Exception -> 0x0278 }
            r0.out_seq_no = r1     // Catch:{ Exception -> 0x0278 }
        L_0x00d9:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0278 }
            if (r1 == 0) goto L_0x0100
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0278 }
            r1.<init>()     // Catch:{ Exception -> 0x0278 }
            r1.append(r9)     // Catch:{ Exception -> 0x0278 }
            java.lang.String r5 = " send message with in_seq = "
            r1.append(r5)     // Catch:{ Exception -> 0x0278 }
            int r5 = r0.in_seq_no     // Catch:{ Exception -> 0x0278 }
            r1.append(r5)     // Catch:{ Exception -> 0x0278 }
            java.lang.String r5 = " out_seq = "
            r1.append(r5)     // Catch:{ Exception -> 0x0278 }
            int r5 = r0.out_seq_no     // Catch:{ Exception -> 0x0278 }
            r1.append(r5)     // Catch:{ Exception -> 0x0278 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0278 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0278 }
        L_0x0100:
            int r1 = r13.getObjectSize()     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.NativeByteBuffer r5 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0278 }
            int r6 = r1 + 4
            r5.<init>((int) r6)     // Catch:{ Exception -> 0x0278 }
            r14 = r5
            r14.writeInt32(r1)     // Catch:{ Exception -> 0x0278 }
            r13.serializeToStream(r14)     // Catch:{ Exception -> 0x0278 }
            int r5 = r14.length()     // Catch:{ Exception -> 0x0278 }
            r15 = r5
            int r1 = r15 % 16
            r5 = 16
            if (r1 == 0) goto L_0x0122
            int r1 = r15 % 16
            int r1 = 16 - r1
            goto L_0x0123
        L_0x0122:
            r1 = 0
        L_0x0123:
            java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0278 }
            r7 = 3
            int r6 = r6.nextInt(r7)     // Catch:{ Exception -> 0x0278 }
            int r6 = r6 + r3
            int r6 = r6 * 16
            int r7 = r1 + r6
            org.telegram.tgnet.NativeByteBuffer r1 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0278 }
            int r6 = r15 + r7
            r1.<init>((int) r6)     // Catch:{ Exception -> 0x0278 }
            r6 = r1
            r14.position(r4)     // Catch:{ Exception -> 0x0278 }
            r6.writeBytes((org.telegram.tgnet.NativeByteBuffer) r14)     // Catch:{ Exception -> 0x0278 }
            if (r7 == 0) goto L_0x0149
            byte[] r1 = new byte[r7]     // Catch:{ Exception -> 0x0278 }
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0278 }
            r2.nextBytes(r1)     // Catch:{ Exception -> 0x0278 }
            r6.writeBytes((byte[]) r1)     // Catch:{ Exception -> 0x0278 }
        L_0x0149:
            byte[] r1 = new byte[r5]     // Catch:{ Exception -> 0x0278 }
            r2 = r1
            long r3 = r8.admin_id     // Catch:{ Exception -> 0x0278 }
            org.telegram.messenger.UserConfig r18 = r27.getUserConfig()     // Catch:{ Exception -> 0x0278 }
            long r18 = r18.getClientUserId()     // Catch:{ Exception -> 0x0278 }
            int r20 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r20 == 0) goto L_0x015d
            r16 = 1
            goto L_0x015f
        L_0x015d:
            r16 = 0
        L_0x015f:
            r4 = r16
            byte[] r3 = r8.auth_key     // Catch:{ Exception -> 0x0278 }
            r16 = 88
            if (r4 == 0) goto L_0x016a
            r18 = 8
            goto L_0x016c
        L_0x016a:
            r18 = 0
        L_0x016c:
            int r19 = r16 + r18
            r20 = 32
            java.nio.ByteBuffer r1 = r6.buffer     // Catch:{ Exception -> 0x0278 }
            r22 = 0
            java.nio.ByteBuffer r5 = r6.buffer     // Catch:{ Exception -> 0x0278 }
            int r23 = r5.limit()     // Catch:{ Exception -> 0x0278 }
            r18 = r3
            r21 = r1
            byte[] r1 = org.telegram.messenger.Utilities.computeSHA256(r18, r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x0278 }
            r5 = r1
            r18 = r0
            r0 = 0
            r1 = 8
            r3 = 16
            java.lang.System.arraycopy(r5, r1, r2, r0, r3)     // Catch:{ Exception -> 0x0278 }
            r14.reuse()     // Catch:{ Exception -> 0x0278 }
            byte[] r0 = r8.auth_key     // Catch:{ Exception -> 0x0278 }
            r1 = 2
            org.telegram.messenger.MessageKeyData r0 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r0, r2, r4, r1)     // Catch:{ Exception -> 0x0278 }
            java.nio.ByteBuffer r1 = r6.buffer     // Catch:{ Exception -> 0x0278 }
            byte[] r3 = r0.aesKey     // Catch:{ Exception -> 0x0278 }
            r26 = r4
            byte[] r4 = r0.aesIv     // Catch:{ Exception -> 0x0278 }
            r22 = 1
            r23 = 0
            r24 = 0
            int r25 = r6.limit()     // Catch:{ Exception -> 0x0278 }
            r19 = r1
            r20 = r3
            r21 = r4
            org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.NativeByteBuffer r1 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0278 }
            int r3 = r2.length     // Catch:{ Exception -> 0x0278 }
            r4 = 8
            int r3 = r3 + r4
            int r4 = r6.length()     // Catch:{ Exception -> 0x0278 }
            int r3 = r3 + r4
            r1.<init>((int) r3)     // Catch:{ Exception -> 0x0278 }
            r4 = r1
            r1 = 0
            r6.position(r1)     // Catch:{ Exception -> 0x0278 }
            r16 = r0
            long r0 = r8.key_fingerprint     // Catch:{ Exception -> 0x0278 }
            r4.writeInt64(r0)     // Catch:{ Exception -> 0x0278 }
            r4.writeBytes((byte[]) r2)     // Catch:{ Exception -> 0x0278 }
            r4.writeBytes((org.telegram.tgnet.NativeByteBuffer) r6)     // Catch:{ Exception -> 0x0278 }
            r6.reuse()     // Catch:{ Exception -> 0x0278 }
            r0 = 0
            r4.position(r0)     // Catch:{ Exception -> 0x0278 }
            if (r11 != 0) goto L_0x0228
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageService     // Catch:{ Exception -> 0x0278 }
            if (r0 == 0) goto L_0x0202
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService r0 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedService     // Catch:{ Exception -> 0x0278 }
            r0.<init>()     // Catch:{ Exception -> 0x0278 }
            r0.data = r4     // Catch:{ Exception -> 0x0278 }
            r3 = r2
            long r1 = r9.random_id     // Catch:{ Exception -> 0x0278 }
            r0.random_id = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0278 }
            r1.<init>()     // Catch:{ Exception -> 0x0278 }
            r0.peer = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = r0.peer     // Catch:{ Exception -> 0x0278 }
            int r2 = r8.id     // Catch:{ Exception -> 0x0278 }
            r1.chat_id = r2     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = r0.peer     // Catch:{ Exception -> 0x0278 }
            r17 = r3
            long r2 = r8.access_hash     // Catch:{ Exception -> 0x0278 }
            r1.access_hash = r2     // Catch:{ Exception -> 0x0278 }
            goto L_0x024f
        L_0x0202:
            r17 = r2
            org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted r0 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncrypted     // Catch:{ Exception -> 0x0278 }
            r0.<init>()     // Catch:{ Exception -> 0x0278 }
            boolean r1 = r10.silent     // Catch:{ Exception -> 0x0278 }
            r0.silent = r1     // Catch:{ Exception -> 0x0278 }
            r0.data = r4     // Catch:{ Exception -> 0x0278 }
            long r1 = r9.random_id     // Catch:{ Exception -> 0x0278 }
            r0.random_id = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0278 }
            r1.<init>()     // Catch:{ Exception -> 0x0278 }
            r0.peer = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = r0.peer     // Catch:{ Exception -> 0x0278 }
            int r2 = r8.id     // Catch:{ Exception -> 0x0278 }
            r1.chat_id = r2     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = r0.peer     // Catch:{ Exception -> 0x0278 }
            long r2 = r8.access_hash     // Catch:{ Exception -> 0x0278 }
            r1.access_hash = r2     // Catch:{ Exception -> 0x0278 }
            goto L_0x024f
        L_0x0228:
            r17 = r2
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile r0 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedFile     // Catch:{ Exception -> 0x0278 }
            r0.<init>()     // Catch:{ Exception -> 0x0278 }
            boolean r1 = r10.silent     // Catch:{ Exception -> 0x0278 }
            r0.silent = r1     // Catch:{ Exception -> 0x0278 }
            r0.data = r4     // Catch:{ Exception -> 0x0278 }
            long r1 = r9.random_id     // Catch:{ Exception -> 0x0278 }
            r0.random_id = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedChat     // Catch:{ Exception -> 0x0278 }
            r1.<init>()     // Catch:{ Exception -> 0x0278 }
            r0.peer = r1     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = r0.peer     // Catch:{ Exception -> 0x0278 }
            int r2 = r8.id     // Catch:{ Exception -> 0x0278 }
            r1.chat_id = r2     // Catch:{ Exception -> 0x0278 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedChat r1 = r0.peer     // Catch:{ Exception -> 0x0278 }
            long r2 = r8.access_hash     // Catch:{ Exception -> 0x0278 }
            r1.access_hash = r2     // Catch:{ Exception -> 0x0278 }
            r0.file = r11     // Catch:{ Exception -> 0x0278 }
            r1 = r0
        L_0x024f:
            org.telegram.tgnet.ConnectionsManager r3 = r27.getConnectionsManager()     // Catch:{ Exception -> 0x0278 }
            org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda21 r2 = new org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda21     // Catch:{ Exception -> 0x0278 }
            r1 = r2
            r8 = r2
            r2 = r27
            r9 = r3
            r3 = r29
            r20 = r4
            r19 = r26
            r4 = r28
            r21 = r5
            r5 = r30
            r22 = r6
            r6 = r32
            r23 = r7
            r7 = r33
            r1.<init>(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0278 }
            r1 = 64
            r9.sendRequest(r0, r8, r1)     // Catch:{ Exception -> 0x0278 }
            goto L_0x027c
        L_0x0278:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x027c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.m1138x85cvar_a(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$DecryptedMessage, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$InputEncryptedFile, org.telegram.messenger.MessageObject, java.lang.String):void");
    }

    /* renamed from: lambda$performSendEncryptedRequest$7$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1137xvar_ed719(TLRPC.DecryptedMessage req, TLRPC.EncryptedChat chat, TLRPC.Message newMsgObj, MessageObject newMsg, String originalPath, TLObject response, TLRPC.TL_error error) {
        int existFlags;
        TLRPC.EncryptedChat currentChat;
        TLRPC.DecryptedMessage decryptedMessage = req;
        TLRPC.EncryptedChat encryptedChat = chat;
        TLRPC.Message message = newMsgObj;
        MessageObject messageObject = newMsg;
        if (error == null && (decryptedMessage.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
            TLRPC.EncryptedChat currentChat2 = getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChat.id));
            if (currentChat2 == null) {
                currentChat = chat;
            } else {
                currentChat = currentChat2;
            }
            if (currentChat.key_hash == null) {
                currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
            }
            if (currentChat.key_hash.length == 16) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(encryptedChat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    currentChat.key_hash = key_hash;
                    getMessagesStorage().updateEncryptedChat(currentChat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            this.sendingNotifyLayer.remove(Integer.valueOf(currentChat.id));
            currentChat.layer = AndroidUtilities.setMyLayerVersion(currentChat.layer, CURRENT_SECRET_CHAT_LAYER);
            getMessagesStorage().updateEncryptedChatLayer(currentChat);
        }
        if (error == null) {
            String attachPath = message.attachPath;
            TLRPC.messages_SentEncryptedMessage res = (TLRPC.messages_SentEncryptedMessage) response;
            if (isSecretVisibleMessage(newMsgObj)) {
                message.date = res.date;
            }
            if (messageObject == null || !(res.file instanceof TLRPC.TL_encryptedFile)) {
                String str = originalPath;
                existFlags = 0;
            } else {
                updateMediaPaths(messageObject, res.file, decryptedMessage, originalPath);
                existFlags = newMsg.getMediaExistanceFlags();
            }
            DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
            SecretChatHelper$$ExternalSyntheticLambda13 secretChatHelper$$ExternalSyntheticLambda13 = r1;
            SecretChatHelper$$ExternalSyntheticLambda13 secretChatHelper$$ExternalSyntheticLambda132 = new SecretChatHelper$$ExternalSyntheticLambda13(this, newMsgObj, res, existFlags, attachPath);
            storageQueue.postRunnable(secretChatHelper$$ExternalSyntheticLambda13);
            return;
        }
        String str2 = originalPath;
        getMessagesStorage().markMessageAsSendError(message, false);
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda10(this, message));
    }

    /* renamed from: lambda$performSendEncryptedRequest$5$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1135xdfee8117(TLRPC.Message newMsgObj, TLRPC.messages_SentEncryptedMessage res, int existFlags, String attachPath) {
        if (isSecretInvisibleMessage(newMsgObj)) {
            res.date = 0;
        }
        getMessagesStorage().updateMessageStateAndId(newMsgObj.random_id, 0, Integer.valueOf(newMsgObj.id), newMsgObj.id, res.date, false, 0);
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda12(this, newMsgObj, existFlags, attachPath));
    }

    /* renamed from: lambda$performSendEncryptedRequest$4$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1134x534e5616(TLRPC.Message newMsgObj, int existFlags, String attachPath) {
        newMsgObj.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(newMsgObj.id), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), 0L, Integer.valueOf(existFlags), false);
        getSendMessagesHelper().processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            getSendMessagesHelper().stopVideoService(attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(newMsgObj.id, false);
    }

    /* renamed from: lambda$performSendEncryptedRequest$6$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1136x6c8eaCLASSNAME(TLRPC.Message newMsgObj) {
        newMsgObj.send_state = 2;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
        getSendMessagesHelper().processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            getSendMessagesHelper().stopVideoService(newMsgObj.attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(newMsgObj.id, false);
    }

    private void applyPeerLayer(TLRPC.EncryptedChat chat, int newPeerLayer) {
        int currentPeerLayer = AndroidUtilities.getPeerLayerVersion(chat.layer);
        if (newPeerLayer > currentPeerLayer) {
            if (chat.key_hash.length == 16) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    chat.key_hash = key_hash;
                    getMessagesStorage().updateEncryptedChat(chat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            chat.layer = AndroidUtilities.setPeerLayerVersion(chat.layer, newPeerLayer);
            getMessagesStorage().updateEncryptedChatLayer(chat);
            if (currentPeerLayer < CURRENT_SECRET_CHAT_LAYER) {
                sendNotifyLayerMessage(chat, (TLRPC.Message) null);
            }
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda6(this, chat));
        }
    }

    /* renamed from: lambda$applyPeerLayer$9$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1131lambda$applyPeerLayer$9$orgtelegrammessengerSecretChatHelper(TLRPC.EncryptedChat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, chat);
    }

    public TLRPC.Message processDecryptedObject(TLRPC.EncryptedChat chat, TLRPC.EncryptedFile file, int date, TLObject object, boolean new_key_used) {
        TLRPC.PhotoSize photoSize;
        TLRPC.DocumentAttribute attribute;
        TLRPC.PhotoSize photoSize2;
        TLRPC.EncryptedChat encryptedChat = chat;
        TLRPC.EncryptedFile encryptedFile = file;
        int i = date;
        TLObject tLObject = object;
        if (tLObject != null) {
            long from_id = encryptedChat.admin_id;
            if (from_id == getUserConfig().getClientUserId()) {
                from_id = encryptedChat.participant_id;
            }
            if (encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint == 0 && encryptedChat.key_use_count_in >= 120) {
                requestNewSecretChatKey(chat);
            }
            if (encryptedChat.exchange_id == 0 && encryptedChat.future_key_fingerprint != 0 && !new_key_used) {
                encryptedChat.future_auth_key = new byte[256];
                encryptedChat.future_key_fingerprint = 0;
                getMessagesStorage().updateEncryptedChat(encryptedChat);
            } else if (encryptedChat.exchange_id != 0 && new_key_used) {
                encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                encryptedChat.auth_key = encryptedChat.future_auth_key;
                encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
                encryptedChat.future_auth_key = new byte[256];
                encryptedChat.future_key_fingerprint = 0;
                encryptedChat.key_use_count_in = 0;
                encryptedChat.key_use_count_out = 0;
                encryptedChat.exchange_id = 0;
                getMessagesStorage().updateEncryptedChat(encryptedChat);
            }
            if (tLObject instanceof TLRPC.TL_decryptedMessage) {
                TLRPC.TL_decryptedMessage decryptedMessage = (TLRPC.TL_decryptedMessage) tLObject;
                TLRPC.TL_message newMessage = new TLRPC.TL_message_secret();
                newMessage.ttl = decryptedMessage.ttl;
                newMessage.entities = decryptedMessage.entities;
                newMessage.message = decryptedMessage.message;
                newMessage.date = i;
                int newMessageId = getUserConfig().getNewMessageId();
                newMessage.id = newMessageId;
                newMessage.local_id = newMessageId;
                newMessage.silent = decryptedMessage.silent;
                getUserConfig().saveConfig(false);
                newMessage.from_id = new TLRPC.TL_peerUser();
                newMessage.from_id.user_id = from_id;
                newMessage.peer_id = new TLRPC.TL_peerUser();
                long j = from_id;
                newMessage.peer_id.user_id = getUserConfig().getClientUserId();
                newMessage.random_id = decryptedMessage.random_id;
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
                newMessage.dialog_id = DialogObject.makeEncryptedDialogId((long) encryptedChat.id);
                if (decryptedMessage.reply_to_random_id != 0) {
                    newMessage.reply_to = new TLRPC.TL_messageReplyHeader();
                    newMessage.reply_to.reply_to_random_id = decryptedMessage.reply_to_random_id;
                    newMessage.flags |= 8;
                }
                if (decryptedMessage.media == null || (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaEmpty)) {
                    newMessage.media = new TLRPC.TL_messageMediaEmpty();
                } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaWebPage) {
                    newMessage.media = new TLRPC.TL_messageMediaWebPage();
                    newMessage.media.webpage = new TLRPC.TL_webPageUrlPending();
                    newMessage.media.webpage.url = decryptedMessage.media.url;
                } else {
                    String str = "";
                    if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaContact) {
                        newMessage.media = new TLRPC.TL_messageMediaContact();
                        newMessage.media.last_name = decryptedMessage.media.last_name;
                        newMessage.media.first_name = decryptedMessage.media.first_name;
                        newMessage.media.phone_number = decryptedMessage.media.phone_number;
                        newMessage.media.user_id = decryptedMessage.media.user_id;
                        newMessage.media.vcard = str;
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaGeoPoint) {
                        newMessage.media = new TLRPC.TL_messageMediaGeo();
                        newMessage.media.geo = new TLRPC.TL_geoPoint();
                        newMessage.media.geo.lat = decryptedMessage.media.lat;
                        newMessage.media.geo._long = decryptedMessage.media._long;
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaPhoto) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaPhoto();
                        newMessage.media.flags |= 3;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            if (decryptedMessage.media.caption != null) {
                                str = decryptedMessage.media.caption;
                            }
                            newMessage.message = str;
                        }
                        newMessage.media.photo = new TLRPC.TL_photo();
                        newMessage.media.photo.file_reference = new byte[0];
                        newMessage.media.photo.date = newMessage.date;
                        byte[] thumb = ((TLRPC.TL_decryptedMessageMediaPhoto) decryptedMessage.media).thumb;
                        if (thumb != null && thumb.length != 0 && thumb.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                            TLRPC.TL_photoCachedSize small = new TLRPC.TL_photoCachedSize();
                            small.w = decryptedMessage.media.thumb_w;
                            small.h = decryptedMessage.media.thumb_h;
                            small.bytes = thumb;
                            small.type = "s";
                            small.location = new TLRPC.TL_fileLocationUnavailable();
                            newMessage.media.photo.sizes.add(small);
                        }
                        if (newMessage.ttl != 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            newMessage.media.flags |= 4;
                        }
                        TLRPC.TL_photoSize big = new TLRPC.TL_photoSize_layer127();
                        big.w = decryptedMessage.media.w;
                        big.h = decryptedMessage.media.h;
                        big.type = "x";
                        big.size = encryptedFile.size;
                        big.location = new TLRPC.TL_fileEncryptedLocation();
                        big.location.key = decryptedMessage.media.key;
                        big.location.iv = decryptedMessage.media.iv;
                        big.location.dc_id = encryptedFile.dc_id;
                        big.location.volume_id = encryptedFile.id;
                        big.location.secret = encryptedFile.access_hash;
                        big.location.local_id = encryptedFile.key_fingerprint;
                        newMessage.media.photo.sizes.add(big);
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVideo) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        newMessage.media.document.dc_id = encryptedFile.dc_id;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            if (decryptedMessage.media.caption != null) {
                                str = decryptedMessage.media.caption;
                            }
                            newMessage.message = str;
                        }
                        newMessage.media.document.date = i;
                        newMessage.media.document.size = encryptedFile.size;
                        newMessage.media.document.id = encryptedFile.id;
                        newMessage.media.document.access_hash = encryptedFile.access_hash;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = "video/mp4";
                        }
                        byte[] thumb2 = ((TLRPC.TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                        if (thumb2 == null || thumb2.length == 0 || thumb2.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                            photoSize2 = new TLRPC.TL_photoSizeEmpty();
                            photoSize2.type = "s";
                        } else {
                            photoSize2 = new TLRPC.TL_photoCachedSize();
                            photoSize2.bytes = thumb2;
                            photoSize2.w = decryptedMessage.media.thumb_w;
                            photoSize2.h = decryptedMessage.media.thumb_h;
                            photoSize2.type = "s";
                            photoSize2.location = new TLRPC.TL_fileLocationUnavailable();
                        }
                        newMessage.media.document.thumbs.add(photoSize2);
                        newMessage.media.document.flags |= 1;
                        TLRPC.TL_documentAttributeVideo attributeVideo = new TLRPC.TL_documentAttributeVideo();
                        attributeVideo.w = decryptedMessage.media.w;
                        attributeVideo.h = decryptedMessage.media.h;
                        attributeVideo.duration = decryptedMessage.media.duration;
                        attributeVideo.supports_streaming = false;
                        newMessage.media.document.attributes.add(attributeVideo);
                        if (newMessage.ttl != 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            newMessage.media.flags |= 4;
                        }
                        if (newMessage.ttl != 0) {
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                    } else if (!(decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument)) {
                        String str2 = "s";
                        if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaExternalDocument) {
                            newMessage.media = new TLRPC.TL_messageMediaDocument();
                            newMessage.media.flags |= 3;
                            newMessage.message = str;
                            newMessage.media.document = new TLRPC.TL_document();
                            newMessage.media.document.id = decryptedMessage.media.id;
                            newMessage.media.document.access_hash = decryptedMessage.media.access_hash;
                            newMessage.media.document.file_reference = new byte[0];
                            newMessage.media.document.date = decryptedMessage.media.date;
                            newMessage.media.document.attributes = decryptedMessage.media.attributes;
                            newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                            newMessage.media.document.dc_id = decryptedMessage.media.dc_id;
                            newMessage.media.document.size = decryptedMessage.media.size;
                            newMessage.media.document.thumbs.add(((TLRPC.TL_decryptedMessageMediaExternalDocument) decryptedMessage.media).thumb);
                            newMessage.media.document.flags |= 1;
                            if (newMessage.media.document.mime_type == null) {
                                newMessage.media.document.mime_type = str;
                            }
                            if (MessageObject.isAnimatedStickerMessage(newMessage)) {
                                newMessage.stickerVerified = 0;
                                getMediaDataController().verifyAnimatedStickerMessage(newMessage, true);
                            }
                        } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaAudio) {
                            if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                                return null;
                            }
                            newMessage.media = new TLRPC.TL_messageMediaDocument();
                            newMessage.media.flags |= 3;
                            newMessage.media.document = new TLRPC.TL_documentEncrypted();
                            newMessage.media.document.key = decryptedMessage.media.key;
                            newMessage.media.document.iv = decryptedMessage.media.iv;
                            newMessage.media.document.id = encryptedFile.id;
                            newMessage.media.document.access_hash = encryptedFile.access_hash;
                            newMessage.media.document.date = i;
                            newMessage.media.document.size = encryptedFile.size;
                            newMessage.media.document.dc_id = encryptedFile.dc_id;
                            newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                            if (TextUtils.isEmpty(newMessage.message)) {
                                if (decryptedMessage.media.caption != null) {
                                    str = decryptedMessage.media.caption;
                                }
                                newMessage.message = str;
                            }
                            if (newMessage.media.document.mime_type == null) {
                                newMessage.media.document.mime_type = "audio/ogg";
                            }
                            TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                            attributeAudio.duration = decryptedMessage.media.duration;
                            attributeAudio.voice = true;
                            newMessage.media.document.attributes.add(attributeAudio);
                            if (newMessage.ttl != 0) {
                                newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                            }
                            if (newMessage.media.document.thumbs.isEmpty()) {
                                TLRPC.PhotoSize thumb3 = new TLRPC.TL_photoSizeEmpty();
                                thumb3.type = str2;
                                newMessage.media.document.thumbs.add(thumb3);
                            }
                        } else if (!(decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVenue)) {
                            return null;
                        } else {
                            newMessage.media = new TLRPC.TL_messageMediaVenue();
                            newMessage.media.geo = new TLRPC.TL_geoPoint();
                            newMessage.media.geo.lat = decryptedMessage.media.lat;
                            newMessage.media.geo._long = decryptedMessage.media._long;
                            newMessage.media.title = decryptedMessage.media.title;
                            newMessage.media.address = decryptedMessage.media.address;
                            newMessage.media.provider = decryptedMessage.media.provider;
                            newMessage.media.venue_id = decryptedMessage.media.venue_id;
                            newMessage.media.venue_type = str;
                        }
                    } else if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                        return null;
                    } else {
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : str;
                        }
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        String str3 = "s";
                        newMessage.media.document.id = encryptedFile.id;
                        newMessage.media.document.access_hash = encryptedFile.access_hash;
                        newMessage.media.document.date = i;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument_layer8) {
                            TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                            fileName.file_name = decryptedMessage.media.file_name;
                            newMessage.media.document.attributes.add(fileName);
                        } else {
                            newMessage.media.document.attributes = decryptedMessage.media.attributes;
                        }
                        if (newMessage.ttl > 0) {
                            int a = 0;
                            int N = newMessage.media.document.attributes.size();
                            while (true) {
                                if (a >= N) {
                                    break;
                                }
                                attribute = newMessage.media.document.attributes.get(a);
                                if ((attribute instanceof TLRPC.TL_documentAttributeAudio) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                    newMessage.ttl = Math.max(attribute.duration + 1, newMessage.ttl);
                                } else {
                                    a++;
                                }
                            }
                            newMessage.ttl = Math.max(attribute.duration + 1, newMessage.ttl);
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                        newMessage.media.document.size = decryptedMessage.media.size != 0 ? Math.min(decryptedMessage.media.size, encryptedFile.size) : encryptedFile.size;
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = str;
                        } else if ("application/x-tgsticker".equals(newMessage.media.document.mime_type) || "application/x-tgsdice".equals(newMessage.media.document.mime_type)) {
                            newMessage.media.document.mime_type = "application/x-bad_tgsticker";
                        }
                        byte[] thumb4 = ((TLRPC.TL_decryptedMessageMediaDocument) decryptedMessage.media).thumb;
                        if (thumb4 == null || thumb4.length == 0 || thumb4.length > 6000 || decryptedMessage.media.thumb_w > 100 || decryptedMessage.media.thumb_h > 100) {
                            photoSize = new TLRPC.TL_photoSizeEmpty();
                            photoSize.type = str3;
                        } else {
                            photoSize = new TLRPC.TL_photoCachedSize();
                            photoSize.bytes = thumb4;
                            photoSize.w = decryptedMessage.media.thumb_w;
                            photoSize.h = decryptedMessage.media.thumb_h;
                            photoSize.type = str3;
                            photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                        }
                        newMessage.media.document.thumbs.add(photoSize);
                        newMessage.media.document.flags |= 1;
                        newMessage.media.document.dc_id = encryptedFile.dc_id;
                        if (MessageObject.isVoiceMessage(newMessage) || MessageObject.isRoundVideoMessage(newMessage)) {
                            newMessage.media_unread = true;
                        }
                    }
                }
                if (newMessage.ttl != 0 && newMessage.media.ttl_seconds == 0) {
                    newMessage.media.ttl_seconds = newMessage.ttl;
                    newMessage.media.flags |= 4;
                }
                if (newMessage.message != null) {
                    newMessage.message = newMessage.message.replace(8238, ' ');
                }
                return newMessage;
            }
            long from_id2 = from_id;
            if (tLObject instanceof TLRPC.TL_decryptedMessageService) {
                TLRPC.TL_decryptedMessageService serviceMessage = (TLRPC.TL_decryptedMessageService) tLObject;
                if ((serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) || (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
                    TLRPC.TL_messageService newMessage2 = new TLRPC.TL_messageService();
                    if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                        newMessage2.action = new TLRPC.TL_messageEncryptedAction();
                        if (serviceMessage.action.ttl_seconds < 0 || serviceMessage.action.ttl_seconds > 31536000) {
                            serviceMessage.action.ttl_seconds = 31536000;
                        }
                        encryptedChat.ttl = serviceMessage.action.ttl_seconds;
                        newMessage2.action.encryptedAction = serviceMessage.action;
                        getMessagesStorage().updateEncryptedChatTTL(encryptedChat);
                    } else {
                        newMessage2.action = new TLRPC.TL_messageEncryptedAction();
                        newMessage2.action.encryptedAction = serviceMessage.action;
                    }
                    int newMessageId2 = getUserConfig().getNewMessageId();
                    newMessage2.id = newMessageId2;
                    newMessage2.local_id = newMessageId2;
                    getUserConfig().saveConfig(false);
                    newMessage2.unread = true;
                    newMessage2.flags = 256;
                    newMessage2.date = i;
                    newMessage2.from_id = new TLRPC.TL_peerUser();
                    newMessage2.from_id.user_id = from_id2;
                    newMessage2.peer_id = new TLRPC.TL_peerUser();
                    newMessage2.peer_id.user_id = getUserConfig().getClientUserId();
                    newMessage2.dialog_id = DialogObject.makeEncryptedDialogId((long) encryptedChat.id);
                    return newMessage2;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                    AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda28(this, DialogObject.makeEncryptedDialogId((long) encryptedChat.id)));
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                    if (serviceMessage.action.random_ids.isEmpty()) {
                        return null;
                    }
                    this.pendingEncMessagesToDelete.addAll(serviceMessage.action.random_ids);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                    if (serviceMessage.action.random_ids.isEmpty()) {
                        return null;
                    }
                    int time = getConnectionsManager().getCurrentTime();
                    getMessagesStorage().createTaskForSecretChat(encryptedChat.id, time, time, 1, serviceMessage.action.random_ids);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                    applyPeerLayer(encryptedChat, serviceMessage.action.layer);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                    if (encryptedChat.exchange_id != 0) {
                        if (encryptedChat.exchange_id <= serviceMessage.action.exchange_id) {
                            sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, encryptedChat.exchange_id);
                        } else if (!BuildVars.LOGS_ENABLED) {
                            return null;
                        } else {
                            FileLog.d("we already have request key with higher exchange_id");
                            return null;
                        }
                    }
                    byte[] salt = new byte[256];
                    Utilities.random.nextBytes(salt);
                    BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                    BigInteger g_b = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), p);
                    BigInteger g_a = new BigInteger(1, serviceMessage.action.g_a);
                    if (!Utilities.isGoodGaAndGb(g_a, p)) {
                        sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                        return null;
                    }
                    byte[] g_b_bytes = g_b.toByteArray();
                    if (g_b_bytes.length > 256) {
                        byte[] correctedAuth = new byte[256];
                        System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                        g_b_bytes = correctedAuth;
                    }
                    byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
                    if (authKey.length > 256) {
                        byte[] correctedAuth2 = new byte[256];
                        System.arraycopy(authKey, authKey.length - 256, correctedAuth2, 0, 256);
                        authKey = correctedAuth2;
                    } else if (authKey.length < 256) {
                        byte[] correctedAuth3 = new byte[256];
                        System.arraycopy(authKey, 0, correctedAuth3, 256 - authKey.length, authKey.length);
                        for (int a2 = 0; a2 < 256 - authKey.length; a2++) {
                            correctedAuth3[a2] = 0;
                        }
                        authKey = correctedAuth3;
                    }
                    byte[] authKeyHash = Utilities.computeSHA1(authKey);
                    byte[] authKeyId = new byte[8];
                    System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                    byte[] bArr = salt;
                    BigInteger bigInteger = p;
                    encryptedChat.exchange_id = serviceMessage.action.exchange_id;
                    encryptedChat.future_auth_key = authKey;
                    encryptedChat.future_key_fingerprint = Utilities.bytesToLong(authKeyId);
                    encryptedChat.g_a_or_b = g_b_bytes;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    sendAcceptKeyMessage(encryptedChat, (TLRPC.Message) null);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                    if (encryptedChat.exchange_id == serviceMessage.action.exchange_id) {
                        BigInteger p2 = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                        BigInteger i_authKey = new BigInteger(1, serviceMessage.action.g_b);
                        if (!Utilities.isGoodGaAndGb(i_authKey, p2)) {
                            encryptedChat.future_auth_key = new byte[256];
                            encryptedChat.future_key_fingerprint = 0;
                            encryptedChat.exchange_id = 0;
                            getMessagesStorage().updateEncryptedChat(encryptedChat);
                            sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                            return null;
                        }
                        byte[] authKey2 = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), p2).toByteArray();
                        if (authKey2.length > 256) {
                            byte[] correctedAuth4 = new byte[256];
                            System.arraycopy(authKey2, authKey2.length - 256, correctedAuth4, 0, 256);
                            authKey2 = correctedAuth4;
                        } else if (authKey2.length < 256) {
                            byte[] correctedAuth5 = new byte[256];
                            byte b = 0;
                            System.arraycopy(authKey2, 0, correctedAuth5, 256 - authKey2.length, authKey2.length);
                            int a3 = 0;
                            while (a3 < 256 - authKey2.length) {
                                correctedAuth5[a3] = b;
                                a3++;
                                b = 0;
                            }
                            authKey2 = correctedAuth5;
                        }
                        byte[] authKeyHash2 = Utilities.computeSHA1(authKey2);
                        byte[] authKeyId2 = new byte[8];
                        System.arraycopy(authKeyHash2, authKeyHash2.length - 8, authKeyId2, 0, 8);
                        long fingerprint = Utilities.bytesToLong(authKeyId2);
                        if (serviceMessage.action.key_fingerprint == fingerprint) {
                            encryptedChat.future_auth_key = authKey2;
                            encryptedChat.future_key_fingerprint = fingerprint;
                            getMessagesStorage().updateEncryptedChat(encryptedChat);
                            sendCommitKeyMessage(encryptedChat, (TLRPC.Message) null);
                            return null;
                        }
                        encryptedChat.future_auth_key = new byte[256];
                        encryptedChat.future_key_fingerprint = 0;
                        encryptedChat.exchange_id = 0;
                        getMessagesStorage().updateEncryptedChat(encryptedChat);
                        sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                        return null;
                    }
                    encryptedChat.future_auth_key = new byte[256];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                    if (encryptedChat.exchange_id == serviceMessage.action.exchange_id && encryptedChat.future_key_fingerprint == serviceMessage.action.key_fingerprint) {
                        long old_fingerprint = encryptedChat.key_fingerprint;
                        byte[] old_key = encryptedChat.auth_key;
                        encryptedChat.key_fingerprint = encryptedChat.future_key_fingerprint;
                        encryptedChat.auth_key = encryptedChat.future_auth_key;
                        encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
                        encryptedChat.future_auth_key = old_key;
                        encryptedChat.future_key_fingerprint = old_fingerprint;
                        encryptedChat.key_use_count_in = 0;
                        encryptedChat.key_use_count_out = 0;
                        encryptedChat.exchange_id = 0;
                        getMessagesStorage().updateEncryptedChat(encryptedChat);
                        sendNoopMessage(encryptedChat, (TLRPC.Message) null);
                        return null;
                    }
                    encryptedChat.future_auth_key = new byte[256];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    sendAbortKeyMessage(encryptedChat, (TLRPC.Message) null, serviceMessage.action.exchange_id);
                    return null;
                } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                    if (encryptedChat.exchange_id != serviceMessage.action.exchange_id) {
                        return null;
                    }
                    encryptedChat.future_auth_key = new byte[256];
                    encryptedChat.future_key_fingerprint = 0;
                    encryptedChat.exchange_id = 0;
                    getMessagesStorage().updateEncryptedChat(encryptedChat);
                    return null;
                } else if ((serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionNoop) || !(serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionResend) || serviceMessage.action.end_seq_no < encryptedChat.in_seq_no || serviceMessage.action.end_seq_no < serviceMessage.action.start_seq_no) {
                    return null;
                } else {
                    if (serviceMessage.action.start_seq_no < encryptedChat.in_seq_no) {
                        serviceMessage.action.start_seq_no = encryptedChat.in_seq_no;
                    }
                    resendMessages(serviceMessage.action.start_seq_no, serviceMessage.action.end_seq_no, encryptedChat);
                    return null;
                }
            } else {
                if (!BuildVars.LOGS_ENABLED) {
                    return null;
                }
                FileLog.e("unknown message " + tLObject);
                return null;
            }
        } else if (!BuildVars.LOGS_ENABLED) {
            return null;
        } else {
            FileLog.e("unknown TLObject");
            return null;
        }
    }

    /* renamed from: lambda$processDecryptedObject$12$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1143x53e0a31b(long did) {
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
        if (dialog != null) {
            dialog.unread_count = 0;
            getMessagesController().dialogMessage.remove(dialog.id);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new SecretChatHelper$$ExternalSyntheticLambda27(this, did));
        getMessagesStorage().deleteDialog(did, 1);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), false, null);
    }

    /* renamed from: lambda$processDecryptedObject$11$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1142xCLASSNAMEa(long did) {
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda26(this, did));
    }

    /* renamed from: lambda$processDecryptedObject$10$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1141x3aa04d19(long did) {
        getNotificationsController().processReadMessages((LongSparseIntArray) null, did, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray dialogsToUpdate = new LongSparseIntArray(1);
        dialogsToUpdate.put(did, 0);
        getNotificationsController().processDialogsUpdateRead(dialogsToUpdate);
    }

    private TLRPC.Message createDeleteMessage(int mid, int seq_out, int seq_in, long random_id, TLRPC.EncryptedChat encryptedChat) {
        TLRPC.TL_messageService newMsg = new TLRPC.TL_messageService();
        newMsg.action = new TLRPC.TL_messageEncryptedAction();
        newMsg.action.encryptedAction = new TLRPC.TL_decryptedMessageActionDeleteMessages();
        newMsg.action.encryptedAction.random_ids.add(Long.valueOf(random_id));
        newMsg.id = mid;
        newMsg.local_id = mid;
        newMsg.from_id = new TLRPC.TL_peerUser();
        newMsg.from_id.user_id = getUserConfig().getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = DialogObject.makeEncryptedDialogId((long) encryptedChat.id);
        newMsg.send_state = 1;
        newMsg.seq_in = seq_in;
        newMsg.seq_out = seq_out;
        newMsg.peer_id = new TLRPC.TL_peerUser();
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            newMsg.peer_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.peer_id.user_id = encryptedChat.participant_id;
        }
        newMsg.date = 0;
        newMsg.random_id = random_id;
        return newMsg;
    }

    private void resendMessages(int startSeq, int endSeq, TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat != null && endSeq - startSeq >= 0) {
            getMessagesStorage().getStorageQueue().postRunnable(new SecretChatHelper$$ExternalSyntheticLambda25(this, startSeq, encryptedChat, endSeq));
        }
    }

    /* renamed from: lambda$resendMessages$15$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1149lambda$resendMessages$15$orgtelegrammessengerSecretChatHelper(int startSeq, TLRPC.EncryptedChat encryptedChat, int endSeq) {
        long dialog_id;
        SparseArray<TLRPC.Message> messagesToResend;
        ArrayList<TLRPC.Message> messages;
        int seq_out;
        TLRPC.Message message;
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        int sSeq = startSeq;
        try {
            if (encryptedChat2.admin_id == getUserConfig().getClientUserId() && sSeq % 2 == 0) {
                sSeq++;
            }
            boolean z = false;
            int i = 1;
            int i2 = 3;
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", new Object[]{Integer.valueOf(encryptedChat2.id), Integer.valueOf(sSeq), Integer.valueOf(sSeq), Integer.valueOf(endSeq), Integer.valueOf(endSeq)}), new Object[0]);
            boolean exists = cursor.next();
            cursor.dispose();
            if (!exists) {
                long dialog_id2 = DialogObject.makeEncryptedDialogId((long) encryptedChat2.id);
                SparseArray<TLRPC.Message> messagesToResend2 = new SparseArray<>();
                ArrayList<TLRPC.Message> messages2 = new ArrayList<>();
                for (int a = sSeq; a <= endSeq; a += 2) {
                    messagesToResend2.put(a, (Object) null);
                }
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms_v2 as r ON r.mid = s.mid LEFT JOIN messages_v2 as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", new Object[]{Long.valueOf(dialog_id2), Integer.valueOf(sSeq), Integer.valueOf(endSeq)}), new Object[0]);
                while (cursor2.next()) {
                    long random_id = cursor2.longValue(i);
                    if (random_id == 0) {
                        random_id = Utilities.random.nextLong();
                    }
                    int seq_in = cursor2.intValue(2);
                    int seq_out2 = cursor2.intValue(i2);
                    long random_id2 = random_id;
                    int mid = cursor2.intValue(5);
                    NativeByteBuffer data = cursor2.byteBufferValue(z ? 1 : 0);
                    if (data != null) {
                        TLRPC.Message message2 = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                        message2.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message2.random_id = random_id2;
                        message2.dialog_id = dialog_id2;
                        message2.seq_in = seq_in;
                        seq_out = seq_out2;
                        message2.seq_out = seq_out;
                        message2.ttl = cursor2.intValue(4);
                        NativeByteBuffer nativeByteBuffer = data;
                        dialog_id = dialog_id2;
                        message = message2;
                        messages = messages2;
                        int i3 = seq_in;
                        messagesToResend = messagesToResend2;
                    } else {
                        seq_out = seq_out2;
                        NativeByteBuffer nativeByteBuffer2 = data;
                        messages = messages2;
                        dialog_id = dialog_id2;
                        int i4 = seq_in;
                        messagesToResend = messagesToResend2;
                        message = createDeleteMessage(mid, seq_out, seq_in, random_id2, encryptedChat);
                    }
                    messages.add(message);
                    messagesToResend.remove(seq_out);
                    int i5 = endSeq;
                    messages2 = messages;
                    messagesToResend2 = messagesToResend;
                    dialog_id2 = dialog_id;
                    z = false;
                    i = 1;
                    i2 = 3;
                }
                ArrayList<TLRPC.Message> messages3 = messages2;
                long j = dialog_id2;
                SparseArray<TLRPC.Message> messagesToResend3 = messagesToResend2;
                cursor2.dispose();
                if (messagesToResend3.size() != 0) {
                    for (int a2 = 0; a2 < messagesToResend3.size(); a2++) {
                        int seq = messagesToResend3.keyAt(a2);
                        messages3.add(createDeleteMessage(getUserConfig().getNewMessageId(), seq, seq + 1, Utilities.random.nextLong(), encryptedChat));
                    }
                    getUserConfig().saveConfig(false);
                }
                Collections.sort(messages3, SecretChatHelper$$ExternalSyntheticLambda17.INSTANCE);
                ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
                encryptedChats.add(encryptedChat2);
                try {
                    AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda3(this, messages3));
                    getSendMessagesHelper().processUnsentMessages(messages3, (ArrayList<TLRPC.Message>) null, new ArrayList(), new ArrayList(), encryptedChats);
                    getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", new Object[]{Integer.valueOf(encryptedChat2.id), Integer.valueOf(sSeq), Integer.valueOf(endSeq)})).stepThis().dispose();
                } catch (Exception e) {
                    e = e;
                }
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$resendMessages$14$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1148lambda$resendMessages$14$orgtelegrammessengerSecretChatHelper(ArrayList messages) {
        for (int a = 0; a < messages.size(); a++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) messages.get(a), false, true);
            messageObject.resendAsIs = true;
            getSendMessagesHelper().retrySendMessage(messageObject, true);
        }
    }

    public void checkSecretHoles(TLRPC.EncryptedChat chat, ArrayList<TLRPC.Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = this.secretHolesQueue.get(chat.id);
        if (holes != null) {
            Collections.sort(holes, SecretChatHelper$$ExternalSyntheticLambda16.INSTANCE);
            boolean update = false;
            int a = 0;
            while (a < holes.size()) {
                TL_decryptedMessageHolder holder = holes.get(a);
                if (holder.layer.out_seq_no != chat.seq_in && chat.seq_in != holder.layer.out_seq_no - 2) {
                    break;
                }
                applyPeerLayer(chat, holder.layer.layer);
                chat.seq_in = holder.layer.out_seq_no;
                chat.in_seq_no = holder.layer.in_seq_no;
                holes.remove(a);
                int a2 = a - 1;
                update = true;
                if (holder.decryptedWithVersion == 2) {
                    chat.mtproto_seq = Math.min(chat.mtproto_seq, chat.seq_in);
                }
                TLRPC.Message message = processDecryptedObject(chat, holder.file, holder.date, holder.layer.message, holder.new_key_used);
                if (message != null) {
                    messages.add(message);
                }
                a = a2 + 1;
            }
            if (holes.isEmpty() != 0) {
                this.secretHolesQueue.remove(chat.id);
            }
            if (update) {
                getMessagesStorage().updateEncryptedChatSeq(chat, true);
            }
        }
    }

    static /* synthetic */ int lambda$checkSecretHoles$16(TL_decryptedMessageHolder lhs, TL_decryptedMessageHolder rhs) {
        if (lhs.layer.out_seq_no > rhs.layer.out_seq_no) {
            return 1;
        }
        if (lhs.layer.out_seq_no < rhs.layer.out_seq_no) {
            return -1;
        }
        return 0;
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
        MessageKeyData keyData = MessageKeyData.generateMessageKeyData(keyToDecrypt, bArr, incoming2, i);
        Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, false, false, 24, is.limit() - 24);
        int error = 0;
        int len = nativeByteBuffer.readInt32(false);
        if (i == 2) {
            if (!Utilities.arraysEquals(bArr, 0, Utilities.computeSHA256(keyToDecrypt, (incoming2 ? 8 : 0) + 88, 32, nativeByteBuffer.buffer, 24, nativeByteBuffer.buffer.limit()), 8)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    nativeByteBuffer.position(24);
                }
                error = 0 | 1;
            }
        } else {
            int l = len + 28;
            if (l < nativeByteBuffer.buffer.limit() - 15 || l > nativeByteBuffer.buffer.limit()) {
                l = nativeByteBuffer.buffer.limit();
            }
            byte[] messageKeyFull = Utilities.computeSHA1(nativeByteBuffer.buffer, 24, l);
            if (!Utilities.arraysEquals(bArr, 0, messageKeyFull, messageKeyFull.length - 16)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    nativeByteBuffer.position(24);
                }
                error = 0 | 1;
                byte[] bArr2 = messageKeyFull;
            } else {
                byte[] bArr3 = messageKeyFull;
            }
        }
        if (len <= 0) {
            error |= 1;
        }
        if (len > is.limit() - 28) {
            error |= 1;
        }
        int padding = (is.limit() - 28) - len;
        if (i == 2) {
            if (padding < 12) {
                error |= 1;
            }
            if (padding > 1024) {
                error |= 1;
            }
        } else if (padding > 15) {
            error |= 1;
        }
        return error ^ 1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.tgnet.NativeByteBuffer} */
    /* JADX WARNING: type inference failed for: r2v10 */
    /* JADX WARNING: type inference failed for: r2v12, types: [org.telegram.tgnet.TLRPC$Message, java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>] */
    /* JADX WARNING: type inference failed for: r2v13 */
    /* JADX WARNING: type inference failed for: r2v14 */
    /* JADX WARNING: type inference failed for: r2v20 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0274 A[Catch:{ Exception -> 0x02cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x029b A[Catch:{ Exception -> 0x02cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0114 A[Catch:{ Exception -> 0x02cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0132 A[Catch:{ Exception -> 0x02cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x013c A[Catch:{ Exception -> 0x02cd }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> decryptMessage(org.telegram.tgnet.TLRPC.EncryptedMessage r26) {
        /*
            r25 = this;
            r14 = r25
            r15 = r26
            java.lang.String r0 = " out_seq = "
            org.telegram.messenger.MessagesController r1 = r25.getMessagesController()
            int r2 = r15.chat_id
            r13 = 1
            org.telegram.tgnet.TLRPC$EncryptedChat r12 = r1.getEncryptedChatDB(r2, r13)
            r11 = 0
            if (r12 == 0) goto L_0x02d6
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r1 == 0) goto L_0x001c
            r2 = r11
            r1 = r12
            goto L_0x02d8
        L_0x001c:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting     // Catch:{ Exception -> 0x02cf }
            if (r1 == 0) goto L_0x0049
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Update>> r0 = r14.pendingSecretMessages     // Catch:{ Exception -> 0x0044 }
            int r1 = r12.id     // Catch:{ Exception -> 0x0044 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x0044 }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ Exception -> 0x0044 }
            if (r0 != 0) goto L_0x0039
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0044 }
            r1.<init>()     // Catch:{ Exception -> 0x0044 }
            r0 = r1
            android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Update>> r1 = r14.pendingSecretMessages     // Catch:{ Exception -> 0x0044 }
            int r2 = r12.id     // Catch:{ Exception -> 0x0044 }
            r1.put(r2, r0)     // Catch:{ Exception -> 0x0044 }
        L_0x0039:
            org.telegram.tgnet.TLRPC$TL_updateNewEncryptedMessage r1 = new org.telegram.tgnet.TLRPC$TL_updateNewEncryptedMessage     // Catch:{ Exception -> 0x0044 }
            r1.<init>()     // Catch:{ Exception -> 0x0044 }
            r1.message = r15     // Catch:{ Exception -> 0x0044 }
            r0.add(r1)     // Catch:{ Exception -> 0x0044 }
            return r11
        L_0x0044:
            r0 = move-exception
            r2 = r11
            r1 = r12
            goto L_0x02d2
        L_0x0049:
            org.telegram.tgnet.NativeByteBuffer r1 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02cf }
            byte[] r2 = r15.bytes     // Catch:{ Exception -> 0x02cf }
            int r2 = r2.length     // Catch:{ Exception -> 0x02cf }
            r1.<init>((int) r2)     // Catch:{ Exception -> 0x02cf }
            r10 = r1
            byte[] r1 = r15.bytes     // Catch:{ Exception -> 0x02cf }
            r10.writeBytes((byte[]) r1)     // Catch:{ Exception -> 0x02cf }
            r9 = 0
            r10.position(r9)     // Catch:{ Exception -> 0x02cf }
            long r1 = r10.readInt64(r9)     // Catch:{ Exception -> 0x02cf }
            r16 = r1
            r1 = 0
            r2 = 0
            long r3 = r12.key_fingerprint     // Catch:{ Exception -> 0x02cf }
            int r5 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r5 != 0) goto L_0x0070
            byte[] r3 = r12.auth_key     // Catch:{ Exception -> 0x0044 }
            r1 = r3
            r18 = r1
            r8 = r2
            goto L_0x0089
        L_0x0070:
            long r3 = r12.future_key_fingerprint     // Catch:{ Exception -> 0x02cf }
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0086
            long r3 = r12.future_key_fingerprint     // Catch:{ Exception -> 0x0044 }
            int r5 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r5 != 0) goto L_0x0086
            byte[] r3 = r12.future_auth_key     // Catch:{ Exception -> 0x0044 }
            r1 = r3
            r2 = 1
            r18 = r1
            r8 = r2
            goto L_0x0089
        L_0x0086:
            r18 = r1
            r8 = r2
        L_0x0089:
            r7 = 2
            r5 = 2
            if (r18 == 0) goto L_0x02a9
            r1 = 16
            byte[] r4 = r10.readData(r1, r9)     // Catch:{ Exception -> 0x02cf }
            long r1 = r12.admin_id     // Catch:{ Exception -> 0x02cf }
            org.telegram.messenger.UserConfig r3 = r25.getUserConfig()     // Catch:{ Exception -> 0x02cf }
            long r19 = r3.getClientUserId()     // Catch:{ Exception -> 0x02cf }
            int r3 = (r1 > r19 ? 1 : (r1 == r19 ? 0 : -1))
            if (r3 != 0) goto L_0x00a3
            r6 = 1
            goto L_0x00a4
        L_0x00a3:
            r6 = 0
        L_0x00a4:
            r1 = 1
            r3 = 2
            if (r5 != r3) goto L_0x00b0
            int r2 = r12.mtproto_seq     // Catch:{ Exception -> 0x0044 }
            if (r2 == 0) goto L_0x00b0
            r1 = 0
            r19 = r1
            goto L_0x00b2
        L_0x00b0:
            r19 = r1
        L_0x00b2:
            r1 = r25
            r2 = r10
            r13 = 2
            r3 = r18
            r21 = r5
            r5 = r7
            r11 = r7
            r7 = r19
            boolean r1 = r1.decryptWithMtProtoVersion(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x02a5 }
            if (r1 != 0) goto L_0x0114
            if (r11 != r13) goto L_0x00f6
            r5 = 1
            if (r19 == 0) goto L_0x00ec
            r1 = 1
            r2 = 0
            r7 = r25
            r3 = r8
            r8 = r10
            r9 = r18
            r24 = r10
            r10 = r4
            r22 = r11
            r11 = r1
            r1 = r12
            r12 = r6
            r21 = r5
            r5 = 1
            r13 = r2
            boolean r2 = r7.decryptWithMtProtoVersion(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x00e8 }
            if (r2 != 0) goto L_0x00e4
            goto L_0x00f4
        L_0x00e4:
            r13 = r21
            r2 = 0
            goto L_0x011e
        L_0x00e8:
            r0 = move-exception
            r2 = 0
            goto L_0x02d2
        L_0x00ec:
            r21 = r5
            r3 = r8
            r24 = r10
            r22 = r11
            r1 = r12
        L_0x00f4:
            r2 = 0
            return r2
        L_0x00f6:
            r3 = r8
            r24 = r10
            r22 = r11
            r1 = r12
            r2 = 0
            r5 = 1
            r21 = 2
            r11 = 2
            r13 = 1
            r7 = r25
            r8 = r24
            r9 = r18
            r10 = r4
            r12 = r6
            boolean r7 = r7.decryptWithMtProtoVersion(r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x02cd }
            if (r7 != 0) goto L_0x0111
            return r2
        L_0x0111:
            r13 = r21
            goto L_0x011e
        L_0x0114:
            r3 = r8
            r24 = r10
            r22 = r11
            r1 = r12
            r2 = 0
            r5 = 1
            r13 = r21
        L_0x011e:
            org.telegram.tgnet.TLClassStore r7 = org.telegram.tgnet.TLClassStore.Instance()     // Catch:{ Exception -> 0x02cd }
            r12 = r24
            r8 = 0
            int r9 = r12.readInt32(r8)     // Catch:{ Exception -> 0x02cd }
            org.telegram.tgnet.TLObject r7 = r7.TLdeserialize(r12, r9, r8)     // Catch:{ Exception -> 0x02cd }
            r12.reuse()     // Catch:{ Exception -> 0x02cd }
            if (r3 != 0) goto L_0x0138
            short r9 = r1.key_use_count_in     // Catch:{ Exception -> 0x02cd }
            int r9 = r9 + r5
            short r9 = (short) r9     // Catch:{ Exception -> 0x02cd }
            r1.key_use_count_in = r9     // Catch:{ Exception -> 0x02cd }
        L_0x0138:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer     // Catch:{ Exception -> 0x02cd }
            if (r9 == 0) goto L_0x0274
            r9 = r7
            org.telegram.tgnet.TLRPC$TL_decryptedMessageLayer r9 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer) r9     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            if (r10 != 0) goto L_0x015e
            int r10 = r1.seq_out     // Catch:{ Exception -> 0x02cd }
            if (r10 != 0) goto L_0x015e
            long r10 = r1.admin_id     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.UserConfig r21 = r25.getUserConfig()     // Catch:{ Exception -> 0x02cd }
            long r23 = r21.getClientUserId()     // Catch:{ Exception -> 0x02cd }
            int r21 = (r10 > r23 ? 1 : (r10 == r23 ? 0 : -1))
            if (r21 != 0) goto L_0x015b
            r1.seq_out = r5     // Catch:{ Exception -> 0x02cd }
            r10 = -2
            r1.seq_in = r10     // Catch:{ Exception -> 0x02cd }
            goto L_0x015e
        L_0x015b:
            r10 = -1
            r1.seq_in = r10     // Catch:{ Exception -> 0x02cd }
        L_0x015e:
            byte[] r10 = r9.random_bytes     // Catch:{ Exception -> 0x02cd }
            int r10 = r10.length     // Catch:{ Exception -> 0x02cd }
            r11 = 15
            if (r10 >= r11) goto L_0x016f
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02cd }
            if (r0 == 0) goto L_0x016e
            java.lang.String r0 = "got random bytes less than needed"
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x02cd }
        L_0x016e:
            return r2
        L_0x016f:
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02cd }
            if (r10 == 0) goto L_0x01af
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02cd }
            r10.<init>()     // Catch:{ Exception -> 0x02cd }
            java.lang.String r11 = "current chat in_seq = "
            r10.append(r11)     // Catch:{ Exception -> 0x02cd }
            int r11 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            r10.append(r11)     // Catch:{ Exception -> 0x02cd }
            r10.append(r0)     // Catch:{ Exception -> 0x02cd }
            int r11 = r1.seq_out     // Catch:{ Exception -> 0x02cd }
            r10.append(r11)     // Catch:{ Exception -> 0x02cd }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ Exception -> 0x02cd }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02cd }
            r10.<init>()     // Catch:{ Exception -> 0x02cd }
            java.lang.String r11 = "got message with in_seq = "
            r10.append(r11)     // Catch:{ Exception -> 0x02cd }
            int r11 = r9.in_seq_no     // Catch:{ Exception -> 0x02cd }
            r10.append(r11)     // Catch:{ Exception -> 0x02cd }
            r10.append(r0)     // Catch:{ Exception -> 0x02cd }
            int r0 = r9.out_seq_no     // Catch:{ Exception -> 0x02cd }
            r10.append(r0)     // Catch:{ Exception -> 0x02cd }
            java.lang.String r0 = r10.toString()     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x02cd }
        L_0x01af:
            int r0 = r9.out_seq_no     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            if (r0 > r10) goto L_0x01b6
            return r2
        L_0x01b6:
            if (r13 != r5) goto L_0x01c3
            int r0 = r1.mtproto_seq     // Catch:{ Exception -> 0x02cd }
            if (r0 == 0) goto L_0x01c3
            int r0 = r9.out_seq_no     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.mtproto_seq     // Catch:{ Exception -> 0x02cd }
            if (r0 < r10) goto L_0x01c3
            return r2
        L_0x01c3:
            int r0 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            int r10 = r9.out_seq_no     // Catch:{ Exception -> 0x02cd }
            r11 = 2
            int r10 = r10 - r11
            if (r0 == r10) goto L_0x024f
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02cd }
            if (r0 == 0) goto L_0x01d4
            java.lang.String r0 = "got hole"
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x02cd }
        L_0x01d4:
            int r0 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            r5 = 2
            int r0 = r0 + r5
            int r10 = r9.out_seq_no     // Catch:{ Exception -> 0x02cd }
            int r10 = r10 - r5
            r14.sendResendMessage(r1, r0, r10, r2)     // Catch:{ Exception -> 0x02cd }
            android.util.SparseArray<java.util.ArrayList<org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r0 = r14.secretHolesQueue     // Catch:{ Exception -> 0x02cd }
            int r5 = r1.id     // Catch:{ Exception -> 0x02cd }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ Exception -> 0x02cd }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ Exception -> 0x02cd }
            if (r0 != 0) goto L_0x01f7
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x02cd }
            r5.<init>()     // Catch:{ Exception -> 0x02cd }
            r0 = r5
            android.util.SparseArray<java.util.ArrayList<org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r5 = r14.secretHolesQueue     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.id     // Catch:{ Exception -> 0x02cd }
            r5.put(r10, r0)     // Catch:{ Exception -> 0x02cd }
        L_0x01f7:
            int r5 = r0.size()     // Catch:{ Exception -> 0x02cd }
            r10 = 4
            if (r5 < r10) goto L_0x0238
            android.util.SparseArray<java.util.ArrayList<org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder>> r5 = r14.secretHolesQueue     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.id     // Catch:{ Exception -> 0x02cd }
            r5.remove(r10)     // Catch:{ Exception -> 0x02cd }
            org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded r5 = new org.telegram.tgnet.TLRPC$TL_encryptedChatDiscarded     // Catch:{ Exception -> 0x02cd }
            r5.<init>()     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.id     // Catch:{ Exception -> 0x02cd }
            r5.id = r10     // Catch:{ Exception -> 0x02cd }
            long r10 = r1.user_id     // Catch:{ Exception -> 0x02cd }
            r5.user_id = r10     // Catch:{ Exception -> 0x02cd }
            byte[] r10 = r1.auth_key     // Catch:{ Exception -> 0x02cd }
            r5.auth_key = r10     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.key_create_date     // Catch:{ Exception -> 0x02cd }
            r5.key_create_date = r10     // Catch:{ Exception -> 0x02cd }
            short r10 = r1.key_use_count_in     // Catch:{ Exception -> 0x02cd }
            r5.key_use_count_in = r10     // Catch:{ Exception -> 0x02cd }
            short r10 = r1.key_use_count_out     // Catch:{ Exception -> 0x02cd }
            r5.key_use_count_out = r10     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            r5.seq_in = r10     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.seq_out     // Catch:{ Exception -> 0x02cd }
            r5.seq_out = r10     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda14 r10 = new org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda14     // Catch:{ Exception -> 0x02cd }
            r10.<init>(r14, r5)     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10)     // Catch:{ Exception -> 0x02cd }
            int r10 = r1.id     // Catch:{ Exception -> 0x02cd }
            r14.declineSecretChat(r10, r8)     // Catch:{ Exception -> 0x02cd }
            return r2
        L_0x0238:
            org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder r5 = new org.telegram.messenger.SecretChatHelper$TL_decryptedMessageHolder     // Catch:{ Exception -> 0x02cd }
            r5.<init>()     // Catch:{ Exception -> 0x02cd }
            r5.layer = r9     // Catch:{ Exception -> 0x02cd }
            org.telegram.tgnet.TLRPC$EncryptedFile r8 = r15.file     // Catch:{ Exception -> 0x02cd }
            r5.file = r8     // Catch:{ Exception -> 0x02cd }
            int r8 = r15.date     // Catch:{ Exception -> 0x02cd }
            r5.date = r8     // Catch:{ Exception -> 0x02cd }
            r5.new_key_used = r3     // Catch:{ Exception -> 0x02cd }
            r5.decryptedWithVersion = r13     // Catch:{ Exception -> 0x02cd }
            r0.add(r5)     // Catch:{ Exception -> 0x02cd }
            return r2
        L_0x024f:
            r0 = 2
            if (r13 != r0) goto L_0x025c
            int r0 = r1.mtproto_seq     // Catch:{ Exception -> 0x02cd }
            int r8 = r1.seq_in     // Catch:{ Exception -> 0x02cd }
            int r0 = java.lang.Math.min(r0, r8)     // Catch:{ Exception -> 0x02cd }
            r1.mtproto_seq = r0     // Catch:{ Exception -> 0x02cd }
        L_0x025c:
            int r0 = r9.layer     // Catch:{ Exception -> 0x02cd }
            r14.applyPeerLayer(r1, r0)     // Catch:{ Exception -> 0x02cd }
            int r0 = r9.out_seq_no     // Catch:{ Exception -> 0x02cd }
            r1.seq_in = r0     // Catch:{ Exception -> 0x02cd }
            int r0 = r9.in_seq_no     // Catch:{ Exception -> 0x02cd }
            r1.in_seq_no = r0     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x02cd }
            r0.updateEncryptedChatSeq(r1, r5)     // Catch:{ Exception -> 0x02cd }
            org.telegram.tgnet.TLRPC$DecryptedMessage r0 = r9.message     // Catch:{ Exception -> 0x02cd }
            r7 = r0
            goto L_0x0285
        L_0x0274:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageService     // Catch:{ Exception -> 0x02cd }
            if (r0 == 0) goto L_0x02a2
            r0 = r7
            org.telegram.tgnet.TLRPC$TL_decryptedMessageService r0 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageService) r0     // Catch:{ Exception -> 0x02cd }
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r0 = r0.action     // Catch:{ Exception -> 0x02cd }
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer     // Catch:{ Exception -> 0x02cd }
            if (r0 != 0) goto L_0x0284
            r20 = r12
            goto L_0x02a4
        L_0x0284:
            r0 = r7
        L_0x0285:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x02cd }
            r5.<init>()     // Catch:{ Exception -> 0x02cd }
            org.telegram.tgnet.TLRPC$EncryptedFile r9 = r15.file     // Catch:{ Exception -> 0x02cd }
            int r10 = r15.date     // Catch:{ Exception -> 0x02cd }
            r7 = r25
            r8 = r1
            r11 = r0
            r20 = r12
            r12 = r3
            org.telegram.tgnet.TLRPC$Message r7 = r7.processDecryptedObject(r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x02cd }
            if (r7 == 0) goto L_0x029e
            r5.add(r7)     // Catch:{ Exception -> 0x02cd }
        L_0x029e:
            r14.checkSecretHoles(r1, r5)     // Catch:{ Exception -> 0x02cd }
            return r5
        L_0x02a2:
            r20 = r12
        L_0x02a4:
            return r2
        L_0x02a5:
            r0 = move-exception
            r1 = r12
            r2 = 0
            goto L_0x02d2
        L_0x02a9:
            r21 = r5
            r22 = r7
            r3 = r8
            r20 = r10
            r2 = r11
            r1 = r12
            r5 = 1
            r8 = 0
            r20.reuse()     // Catch:{ Exception -> 0x02cd }
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02cd }
            if (r0 == 0) goto L_0x02cc
            java.lang.String r0 = "fingerprint mismatch %x"
            java.lang.Object[] r4 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x02cd }
            java.lang.Long r5 = java.lang.Long.valueOf(r16)     // Catch:{ Exception -> 0x02cd }
            r4[r8] = r5     // Catch:{ Exception -> 0x02cd }
            java.lang.String r0 = java.lang.String.format(r0, r4)     // Catch:{ Exception -> 0x02cd }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x02cd }
        L_0x02cc:
            goto L_0x02d5
        L_0x02cd:
            r0 = move-exception
            goto L_0x02d2
        L_0x02cf:
            r0 = move-exception
            r2 = r11
            r1 = r12
        L_0x02d2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02d5:
            return r2
        L_0x02d6:
            r2 = r11
            r1 = r12
        L_0x02d8:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SecretChatHelper.decryptMessage(org.telegram.tgnet.TLRPC$EncryptedMessage):java.util.ArrayList");
    }

    /* renamed from: lambda$decryptMessage$17$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1133lambda$decryptMessage$17$orgtelegrammessengerSecretChatHelper(TLRPC.TL_encryptedChatDiscarded newChat) {
        getMessagesController().putEncryptedChat(newChat, false);
        getMessagesStorage().updateEncryptedChat(newChat);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void requestNewSecretChatKey(TLRPC.EncryptedChat encryptedChat) {
        byte[] salt = new byte[256];
        Utilities.random.nextBytes(salt);
        byte[] g_a = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
        if (g_a.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(g_a, 1, correctedAuth, 0, 256);
            g_a = correctedAuth;
        }
        encryptedChat.exchange_id = getSendMessagesHelper().getNextRandomId();
        encryptedChat.a_or_b = salt;
        encryptedChat.g_a = g_a;
        getMessagesStorage().updateEncryptedChat(encryptedChat);
        sendRequestKeyMessage(encryptedChat, (TLRPC.Message) null);
    }

    public void processAcceptedSecretChat(TLRPC.EncryptedChat encryptedChat) {
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, encryptedChat2.g_a_or_b);
        if (!Utilities.isGoodGaAndGb(i_authKey, p)) {
            declineSecretChat(encryptedChat2.id, false);
            return;
        }
        byte[] authKey = i_authKey.modPow(new BigInteger(1, encryptedChat2.a_or_b), p).toByteArray();
        if (authKey.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
            authKey = correctedAuth;
        } else if (authKey.length < 256) {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(authKey, 0, correctedAuth2, 256 - authKey.length, authKey.length);
            for (int a = 0; a < 256 - authKey.length; a++) {
                correctedAuth2[a] = 0;
            }
            authKey = correctedAuth2;
        }
        byte[] authKeyHash = Utilities.computeSHA1(authKey);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        if (encryptedChat2.key_fingerprint == Utilities.bytesToLong(authKeyId)) {
            encryptedChat2.auth_key = authKey;
            encryptedChat2.key_create_date = getConnectionsManager().getCurrentTime();
            encryptedChat2.seq_in = -2;
            encryptedChat2.seq_out = 1;
            getMessagesStorage().updateEncryptedChat(encryptedChat2);
            getMessagesController().putEncryptedChat(encryptedChat2, false);
            ArrayList<TLRPC.Update> pendingUpdates = this.pendingSecretMessages.get(encryptedChat2.id);
            if (pendingUpdates != null) {
                getMessagesController().processUpdateArray(pendingUpdates, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
                this.pendingSecretMessages.remove(encryptedChat2.id);
            }
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda7(this, encryptedChat2));
            return;
        }
        TLRPC.TL_encryptedChatDiscarded newChat = new TLRPC.TL_encryptedChatDiscarded();
        newChat.id = encryptedChat2.id;
        newChat.user_id = encryptedChat2.user_id;
        newChat.auth_key = encryptedChat2.auth_key;
        newChat.key_create_date = encryptedChat2.key_create_date;
        newChat.key_use_count_in = encryptedChat2.key_use_count_in;
        newChat.key_use_count_out = encryptedChat2.key_use_count_out;
        newChat.seq_in = encryptedChat2.seq_in;
        newChat.seq_out = encryptedChat2.seq_out;
        newChat.admin_id = encryptedChat2.admin_id;
        newChat.mtproto_seq = encryptedChat2.mtproto_seq;
        getMessagesStorage().updateEncryptedChat(newChat);
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda15(this, newChat));
        declineSecretChat(encryptedChat2.id, false);
    }

    /* renamed from: lambda$processAcceptedSecretChat$18$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1139x790a9119(TLRPC.EncryptedChat encryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
        sendNotifyLayerMessage(encryptedChat, (TLRPC.Message) null);
    }

    /* renamed from: lambda$processAcceptedSecretChat$19$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1140x5aabc1a(TLRPC.TL_encryptedChatDiscarded newChat) {
        getMessagesController().putEncryptedChat(newChat, false);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void declineSecretChat(int chat_id, boolean revoke) {
        declineSecretChat(chat_id, revoke, 0);
    }

    public void declineSecretChat(int chat_id, boolean revoke, long taskId) {
        long newTaskId;
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(12);
                data.writeInt32(100);
                data.writeInt32(chat_id);
                data.writeBool(revoke);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            newTaskId = getMessagesStorage().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        TLRPC.TL_messages_discardEncryption req = new TLRPC.TL_messages_discardEncryption();
        req.chat_id = chat_id;
        req.delete_history = revoke;
        getConnectionsManager().sendRequest(req, new SecretChatHelper$$ExternalSyntheticLambda18(this, newTaskId));
    }

    /* renamed from: lambda$declineSecretChat$20$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1132xbee5b370(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void acceptSecretChat(TLRPC.EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) == null) {
            this.acceptingChats.put(encryptedChat.id, encryptedChat);
            TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = getMessagesStorage().getLastSecretVersion();
            getConnectionsManager().sendRequest(req, new SecretChatHelper$$ExternalSyntheticLambda24(this, encryptedChat));
        }
    }

    /* renamed from: lambda$acceptSecretChat$23$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1130xb735687(TLRPC.EncryptedChat encryptedChat, TLObject response, TLRPC.TL_error error) {
        TLRPC.EncryptedChat encryptedChat2 = encryptedChat;
        TLObject tLObject = response;
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    this.acceptingChats.remove(encryptedChat2.id);
                    declineSecretChat(encryptedChat2.id, false);
                    return;
                }
                getMessagesStorage().setSecretPBytes(res.p);
                getMessagesStorage().setSecretG(res.g);
                getMessagesStorage().setLastSecretVersion(res.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            encryptedChat2.a_or_b = salt;
            encryptedChat2.seq_in = -1;
            encryptedChat2.seq_out = 0;
            BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
            BigInteger g_b = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), p);
            BigInteger g_a = new BigInteger(1, encryptedChat2.g_a);
            if (!Utilities.isGoodGaAndGb(g_a, p)) {
                this.acceptingChats.remove(encryptedChat2.id);
                declineSecretChat(encryptedChat2.id, false);
                return;
            }
            byte[] g_b_bytes = g_b.toByteArray();
            if (g_b_bytes.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                g_b_bytes = correctedAuth;
            }
            byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
            if (authKey.length > 256) {
                byte[] correctedAuth2 = new byte[256];
                System.arraycopy(authKey, authKey.length - 256, correctedAuth2, 0, 256);
                authKey = correctedAuth2;
            } else if (authKey.length < 256) {
                byte[] correctedAuth3 = new byte[256];
                System.arraycopy(authKey, 0, correctedAuth3, 256 - authKey.length, authKey.length);
                for (int a2 = 0; a2 < 256 - authKey.length; a2++) {
                    correctedAuth3[a2] = 0;
                }
                authKey = correctedAuth3;
            }
            byte[] authKeyHash = Utilities.computeSHA1(authKey);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            encryptedChat2.auth_key = authKey;
            encryptedChat2.key_create_date = getConnectionsManager().getCurrentTime();
            TLRPC.TL_messages_acceptEncryption req2 = new TLRPC.TL_messages_acceptEncryption();
            req2.g_b = g_b_bytes;
            req2.peer = new TLRPC.TL_inputEncryptedChat();
            req2.peer.chat_id = encryptedChat2.id;
            req2.peer.access_hash = encryptedChat2.access_hash;
            req2.key_fingerprint = Utilities.bytesToLong(authKeyId);
            getConnectionsManager().sendRequest(req2, new SecretChatHelper$$ExternalSyntheticLambda23(this, encryptedChat2), 64);
            return;
        }
        this.acceptingChats.remove(encryptedChat2.id);
    }

    /* renamed from: lambda$acceptSecretChat$22$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1129x7ed32b86(TLRPC.EncryptedChat encryptedChat, TLObject response1, TLRPC.TL_error error1) {
        this.acceptingChats.remove(encryptedChat.id);
        if (error1 == null) {
            TLRPC.EncryptedChat newChat = (TLRPC.EncryptedChat) response1;
            newChat.auth_key = encryptedChat.auth_key;
            newChat.user_id = encryptedChat.user_id;
            newChat.seq_in = encryptedChat.seq_in;
            newChat.seq_out = encryptedChat.seq_out;
            newChat.key_create_date = encryptedChat.key_create_date;
            newChat.key_use_count_in = encryptedChat.key_use_count_in;
            newChat.key_use_count_out = encryptedChat.key_use_count_out;
            getMessagesStorage().updateEncryptedChat(newChat);
            getMessagesController().putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda5(this, newChat));
        }
    }

    /* renamed from: lambda$acceptSecretChat$21$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1128xvar_(TLRPC.EncryptedChat newChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
        sendNotifyLayerMessage(newChat, (TLRPC.Message) null);
    }

    public void startSecretChat(Context context, TLRPC.User user) {
        if (user != null && context != null) {
            this.startingSecretChat = true;
            AlertDialog progressDialog = new AlertDialog(context, 3);
            TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
            req.random_length = 256;
            req.version = getMessagesStorage().getLastSecretVersion();
            progressDialog.setOnCancelListener(new SecretChatHelper$$ExternalSyntheticLambda0(this, getConnectionsManager().sendRequest(req, new SecretChatHelper$$ExternalSyntheticLambda19(this, context, progressDialog, user), 2)));
            try {
                progressDialog.show();
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$startSecretChat$30$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1155x98b25de3(Context context, AlertDialog progressDialog, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        byte[] g_a;
        Context context2 = context;
        AlertDialog alertDialog = progressDialog;
        TLObject tLObject = response;
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) tLObject;
            if (tLObject instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda11(context2, alertDialog));
                    return;
                }
                getMessagesStorage().setSecretPBytes(res.p);
                getMessagesStorage().setSecretG(res.g);
                getMessagesStorage().setLastSecretVersion(res.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) ((int) (Utilities.random.nextDouble() * 256.0d))) ^ res.random[a]);
            }
            byte[] g_a2 = BigInteger.valueOf((long) getMessagesStorage().getSecretG()).modPow(new BigInteger(1, salt), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
            if (g_a2.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_a2, 1, correctedAuth, 0, 256);
                g_a = correctedAuth;
            } else {
                g_a = g_a2;
            }
            TLRPC.TL_messages_requestEncryption req2 = new TLRPC.TL_messages_requestEncryption();
            req2.g_a = g_a;
            req2.user_id = getMessagesController().getInputUser(user);
            req2.random_id = Utilities.random.nextInt();
            SecretChatHelper$$ExternalSyntheticLambda20 secretChatHelper$$ExternalSyntheticLambda20 = r0;
            TLRPC.messages_DhConfig messages_dhconfig = res;
            ConnectionsManager connectionsManager = getConnectionsManager();
            SecretChatHelper$$ExternalSyntheticLambda20 secretChatHelper$$ExternalSyntheticLambda202 = new SecretChatHelper$$ExternalSyntheticLambda20(this, context, progressDialog, salt, user);
            connectionsManager.sendRequest(req2, secretChatHelper$$ExternalSyntheticLambda20, 2);
            return;
        }
        TLRPC.User user2 = user;
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda31(this, context2, alertDialog));
    }

    static /* synthetic */ void lambda$startSecretChat$24(Context context, AlertDialog progressDialog) {
        try {
            if (!((Activity) context).isFinishing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$startSecretChat$28$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1153xvar_e80cc(Context context, AlertDialog progressDialog, byte[] salt, TLRPC.User user, TLObject response1, TLRPC.TL_error error1) {
        if (error1 == null) {
            AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda1(this, context, progressDialog, response1, salt, user));
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new SecretChatHelper$$ExternalSyntheticLambda30(this, context, progressDialog));
    }

    /* renamed from: lambda$startSecretChat$26$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1151xdd0e2aca(Context context, AlertDialog progressDialog, TLObject response1, byte[] salt, TLRPC.User user) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        TLRPC.EncryptedChat chat = (TLRPC.EncryptedChat) response1;
        chat.user_id = chat.participant_id;
        chat.seq_in = -2;
        chat.seq_out = 1;
        chat.a_or_b = salt;
        getMessagesController().putEncryptedChat(chat, false);
        TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        dialog.id = DialogObject.makeEncryptedDialogId((long) chat.id);
        dialog.unread_count = 0;
        dialog.top_message = 0;
        dialog.last_message_date = getConnectionsManager().getCurrentTime();
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs((LongSparseArray<TLRPC.Chat>) null);
        getMessagesStorage().putEncryptedChat(chat, user, dialog);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatCreated, chat);
        Utilities.stageQueue.postRunnable(new SecretChatHelper$$ExternalSyntheticLambda22(this));
    }

    /* renamed from: lambda$startSecretChat$25$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1150x506dffc9() {
        if (!this.delayedEncryptedChatUpdates.isEmpty()) {
            getMessagesController().processUpdateArray(this.delayedEncryptedChatUpdates, (ArrayList<TLRPC.User>) null, (ArrayList<TLRPC.Chat>) null, false, 0);
            this.delayedEncryptedChatUpdates.clear();
        }
    }

    /* renamed from: lambda$startSecretChat$27$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1152x69ae55cb(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            this.startingSecretChat = false;
            try {
                progressDialog.dismiss();
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

    /* renamed from: lambda$startSecretChat$29$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1154x82eeabcd(Context context, AlertDialog progressDialog) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$startSecretChat$31$org-telegram-messenger-SecretChatHelper  reason: not valid java name */
    public /* synthetic */ void m1156x255288e4(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }
}
