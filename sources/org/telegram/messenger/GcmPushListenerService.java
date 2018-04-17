package org.telegram.messenger;

import android.util.Base64;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.Update;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;

    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        final Map data = message.getData();
        final long time = message.getSentTime();
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("GCM received data: ");
            stringBuilder.append(data);
            stringBuilder.append(" from: ");
            stringBuilder.append(from);
            FileLog.m0d(stringBuilder.toString());
        }
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.GcmPushListenerService$1$1 */
            class C01761 implements Runnable {
                C01761() {
                }

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    Throwable e;
                    Throwable th;
                    int currentAccount;
                    StringBuilder stringBuilder;
                    String str;
                    int currentAccount2 = -1;
                    String loc_key = null;
                    String loc_key2;
                    try {
                        Object value = data.get(TtmlNode.TAG_P);
                        if (value instanceof String) {
                            byte[] authKeyHash;
                            byte[] bytes = Base64.decode((String) value, 8);
                            NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                            buffer.writeBytes(bytes);
                            buffer.position(0);
                            if (SharedConfig.pushAuthKeyId == null) {
                                SharedConfig.pushAuthKeyId = new byte[8];
                                authKeyHash = Utilities.computeSHA1(SharedConfig.pushAuthKey);
                                System.arraycopy(authKeyHash, authKeyHash.length - 8, SharedConfig.pushAuthKeyId, 0, 8);
                            }
                            authKeyHash = new byte[8];
                            buffer.readBytes(authKeyHash, true);
                            if (Arrays.equals(SharedConfig.pushAuthKeyId, authKeyHash)) {
                                byte[] messageKey = new byte[16];
                                buffer.readBytes(messageKey, true);
                                MessageKeyData messageKeyData = MessageKeyData.generateMessageKeyData(SharedConfig.pushAuthKey, messageKey, true, 2);
                                Utilities.aesIgeEncryption(buffer.buffer, messageKeyData.aesKey, messageKeyData.aesIv, false, false, 24, bytes.length - 24);
                                byte[] messageKeyFull = Utilities.computeSHA256(SharedConfig.pushAuthKey, 96, 32, buffer.buffer, 24, buffer.buffer.limit());
                                if (Utilities.arraysEquals(messageKey, 0, messageKeyFull, 8)) {
                                    int a;
                                    int a2;
                                    String loc_key3;
                                    Object obj;
                                    int hashCode;
                                    long dialog_id;
                                    String addr;
                                    String[] parts;
                                    NativeByteBuffer nativeByteBuffer;
                                    TL_updateServiceNotification update;
                                    byte[] bArr;
                                    final TL_updates updates;
                                    int channel_id;
                                    int user_id;
                                    long dialog_id2;
                                    int chat_id;
                                    int chat_id2;
                                    int chat_id3;
                                    int badge;
                                    Integer currentReadValue;
                                    Integer num;
                                    MessageKeyData messageKeyData2;
                                    int accountFinal;
                                    JSONObject jSONObject;
                                    byte[] bArr2;
                                    String[] args;
                                    JSONArray loc_args;
                                    String[] args2;
                                    String userName;
                                    boolean localMessage;
                                    boolean supergroup;
                                    boolean pinned;
                                    boolean channel;
                                    String messageText;
                                    String userName2;
                                    String message;
                                    StringBuilder stringBuilder2;
                                    Object obj2;
                                    long dialog_id3;
                                    int i;
                                    int i2;
                                    byte[] bArr3;
                                    ArrayList<Update> updates2;
                                    StringBuilder stringBuilder3;
                                    TL_updateReadChannelInbox update2;
                                    TL_updateReadHistoryInbox update3;
                                    int len = buffer.readInt32(true);
                                    byte[] strBytes = new byte[len];
                                    buffer.readBytes(strBytes, true);
                                    String jsonString = new String(strBytes, C0539C.UTF8_NAME);
                                    JSONObject json = new JSONObject(jsonString);
                                    JSONObject custom = json.getJSONObject("custom");
                                    int currentAccount3 = -1;
                                    try {
                                        Object userIdObject;
                                        int account;
                                        Object userIdObject2;
                                        if (json.has("user_id")) {
                                            try {
                                                userIdObject = json.get("user_id");
                                            } catch (Throwable th2) {
                                                e = th2;
                                                currentAccount = currentAccount3;
                                                if (currentAccount == -1) {
                                                    GcmPushListenerService.this.onDecryptError();
                                                } else {
                                                    ConnectionsManager.onInternalPushReceived(currentAccount);
                                                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("error in loc_key = ");
                                                    stringBuilder.append(loc_key);
                                                    FileLog.m1e(stringBuilder.toString());
                                                }
                                                FileLog.m3e(e);
                                                str = loc_key;
                                            }
                                        }
                                        userIdObject = null;
                                        if (userIdObject != null) {
                                            if (!(userIdObject instanceof Integer)) {
                                                if (!(userIdObject instanceof String)) {
                                                    currentAccount = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                                    account = UserConfig.selectedAccount;
                                                    a = 0;
                                                    while (true) {
                                                        userIdObject2 = userIdObject;
                                                        loc_key2 = loc_key;
                                                        a2 = a;
                                                        if (a2 >= 3) {
                                                            break;
                                                        }
                                                        try {
                                                            if (UserConfig.getInstance(a2).getClientUserId() == currentAccount) {
                                                                break;
                                                            }
                                                            a = a2 + 1;
                                                            userIdObject = userIdObject2;
                                                            loc_key = loc_key2;
                                                        } catch (Throwable th22) {
                                                            e = th22;
                                                            currentAccount = currentAccount3;
                                                            loc_key = loc_key2;
                                                        }
                                                    }
                                                    account = a2;
                                                    currentAccount2 = account;
                                                    a2 = account;
                                                    if (!UserConfig.getInstance(currentAccount2).isClientActivated()) {
                                                        if (json.has("loc_key")) {
                                                            loc_key3 = TtmlNode.ANONYMOUS_REGION_ID;
                                                        } else {
                                                            loc_key3 = json.getString("loc_key");
                                                        }
                                                        obj = data.get("google.sent_time");
                                                        hashCode = loc_key3.hashCode();
                                                        if (hashCode == -920689527) {
                                                            if (hashCode != 633004703) {
                                                                try {
                                                                    if (loc_key3.equals("MESSAGE_ANNOUNCEMENT")) {
                                                                        obj = 1;
                                                                        switch (obj) {
                                                                            case null:
                                                                                dialog_id = custom.getInt("dc");
                                                                                addr = custom.getString("addr");
                                                                                parts = addr.split(":");
                                                                                nativeByteBuffer = buffer;
                                                                                if (parts.length != 2) {
                                                                                    ConnectionsManager.getInstance(currentAccount2).applyDatacenterAddress(dialog_id, parts[null], Integer.parseInt(parts[1]));
                                                                                    ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                                    return;
                                                                                }
                                                                                return;
                                                                            case 1:
                                                                                update = new TL_updateServiceNotification();
                                                                                update.popup = false;
                                                                                update.flags = 2;
                                                                                bArr = bytes;
                                                                                update.inbox_date = (int) (time / 1000);
                                                                                update.message = json.getString("message");
                                                                                update.type = "announcement";
                                                                                update.media = new TL_messageMediaEmpty();
                                                                                updates = new TL_updates();
                                                                                updates.updates.add(update);
                                                                                Utilities.stageQueue.postRunnable(new Runnable() {
                                                                                    public void run() {
                                                                                        MessagesController.getInstance(a2).processUpdates(updates, false);
                                                                                    }
                                                                                });
                                                                                ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                                return;
                                                                            default:
                                                                                dialog_id = 0;
                                                                                if (custom.has("channel_id")) {
                                                                                    channel_id = 0;
                                                                                } else {
                                                                                    channel_id = custom.getInt("channel_id");
                                                                                    dialog_id = (long) (-channel_id);
                                                                                }
                                                                                try {
                                                                                    if (custom.has("from_id")) {
                                                                                        user_id = 0;
                                                                                    } else {
                                                                                        user_id = custom.getInt("from_id");
                                                                                        dialog_id = (long) user_id;
                                                                                    }
                                                                                    dialog_id2 = dialog_id;
                                                                                    if (custom.has("chat_id")) {
                                                                                        dialog_id = dialog_id2;
                                                                                        chat_id = 0;
                                                                                    } else {
                                                                                        chat_id2 = custom.getInt("chat_id");
                                                                                        chat_id = chat_id2;
                                                                                        dialog_id = (long) (-chat_id2);
                                                                                    }
                                                                                    chat_id3 = chat_id;
                                                                                    if (dialog_id == 0) {
                                                                                        if (json.has("badge")) {
                                                                                            badge = 0;
                                                                                        } else {
                                                                                            badge = json.getInt("badge");
                                                                                        }
                                                                                        if (badge == 0) {
                                                                                            badge = custom.getInt("msg_id");
                                                                                            currentReadValue = (Integer) MessagesController.getInstance(currentAccount2).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                                                            if (currentReadValue != null) {
                                                                                                currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount2).getDialogReadMax(null, dialog_id));
                                                                                                MessagesController.getInstance(a2).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                                                            } else {
                                                                                                num = currentReadValue;
                                                                                                messageKeyData2 = messageKeyData;
                                                                                            }
                                                                                            if (badge <= currentReadValue.intValue()) {
                                                                                                if (custom.has("chat_from_id")) {
                                                                                                    messageKey = null;
                                                                                                } else {
                                                                                                    messageKey = custom.getInt("chat_from_id");
                                                                                                }
                                                                                                if (custom.has("mention")) {
                                                                                                    if (custom.getInt("mention") != 0) {
                                                                                                        messageKeyData = true;
                                                                                                        if (json.has("loc_args")) {
                                                                                                            accountFinal = a2;
                                                                                                            jSONObject = json;
                                                                                                            bArr2 = strBytes;
                                                                                                            args = null;
                                                                                                        } else {
                                                                                                            loc_args = json.getJSONArray("loc_args");
                                                                                                            json = new String[loc_args.length()];
                                                                                                            a = 0;
                                                                                                            while (true) {
                                                                                                                bArr2 = strBytes;
                                                                                                                accountFinal = a2;
                                                                                                                a2 = a;
                                                                                                                if (a2 >= json.length) {
                                                                                                                    json[a2] = loc_args.getString(a2);
                                                                                                                    a = a2 + 1;
                                                                                                                    strBytes = bArr2;
                                                                                                                    a2 = accountFinal;
                                                                                                                } else {
                                                                                                                    args = json;
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                        args2 = args;
                                                                                                        strBytes = args2[0];
                                                                                                        userName = null;
                                                                                                        localMessage = false;
                                                                                                        supergroup = false;
                                                                                                        pinned = false;
                                                                                                        channel = false;
                                                                                                        messageText = null;
                                                                                                        if (!loc_key3.startsWith("CHAT_")) {
                                                                                                            supergroup = channel_id == 0;
                                                                                                            userName2 = strBytes;
                                                                                                            strBytes = args2[1];
                                                                                                            userName = userName2;
                                                                                                        } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                                            supergroup = messageKey == null;
                                                                                                            pinned = true;
                                                                                                        } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                                            channel = true;
                                                                                                        }
                                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                                            message = null;
                                                                                                        } else {
                                                                                                            stringBuilder2 = new StringBuilder();
                                                                                                            message = null;
                                                                                                            stringBuilder2.append("GCM received message notification ");
                                                                                                            stringBuilder2.append(loc_key3);
                                                                                                            stringBuilder2.append(" for dialogId = ");
                                                                                                            stringBuilder2.append(dialog_id);
                                                                                                            stringBuilder2.append(" mid = ");
                                                                                                            stringBuilder2.append(badge);
                                                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                                                        }
                                                                                                        switch (loc_key3.hashCode()) {
                                                                                                            case -2091498420:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                                                    obj2 = 28;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -2053872415:
                                                                                                                if (loc_key3.equals("CHAT_CREATED")) {
                                                                                                                    obj2 = 50;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -2039746363:
                                                                                                                if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                                                    obj2 = 9;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1979538588:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                                                    obj2 = 25;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1979536003:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                                                    obj2 = 29;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1979535888:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                                                    obj2 = 31;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1969004705:
                                                                                                                if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                                                    obj2 = 53;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1946699248:
                                                                                                                if (loc_key3.equals("CHAT_JOINED")) {
                                                                                                                    obj2 = 59;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1528047021:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                                                    obj2 = 62;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1493579426:
                                                                                                                if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                                                    obj2 = 10;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1480102982:
                                                                                                                if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                                                    obj2 = 2;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1478041834:
                                                                                                                if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                                                    obj2 = 7;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1474543101:
                                                                                                                if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                                                    obj2 = 4;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1465695932:
                                                                                                                if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                                                    obj2 = 81;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1374906292:
                                                                                                                if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                                                    obj2 = 82;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1372940586:
                                                                                                                if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                                                    obj2 = 58;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1264245338:
                                                                                                                if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                                                    obj2 = 75;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1236086700:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                                                    obj2 = 33;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1236077786:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                                                    obj2 = 32;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1235686303:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                                                    obj2 = 20;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1198046100:
                                                                                                                if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                                                    obj2 = 5;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1124254527:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                                                    obj2 = 44;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1085137927:
                                                                                                                if (loc_key3.equals("PINNED_GAME")) {
                                                                                                                    obj2 = 74;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -1084746444:
                                                                                                                if (loc_key3.equals("PINNED_TEXT")) {
                                                                                                                    obj2 = 63;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -819729482:
                                                                                                                if (loc_key3.equals("PINNED_STICKER")) {
                                                                                                                    obj2 = 69;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -772141857:
                                                                                                                if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                                                    obj2 = 84;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -638310039:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                                                    obj2 = 26;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -589196239:
                                                                                                                if (loc_key3.equals("PINNED_DOC")) {
                                                                                                                    obj2 = 68;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -589193654:
                                                                                                                if (loc_key3.equals("PINNED_GEO")) {
                                                                                                                    obj2 = 72;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -589193539:
                                                                                                                if (loc_key3.equals("PINNED_GIF")) {
                                                                                                                    obj2 = 76;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -440169325:
                                                                                                                if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                                                    obj2 = 78;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -412748110:
                                                                                                                if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                                                    obj2 = 56;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -228518075:
                                                                                                                if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                                                    obj2 = 13;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -213586509:
                                                                                                                if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                                                    obj2 = 80;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -115582002:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                                                    obj2 = 49;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -112621464:
                                                                                                                if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                                                    obj2 = 77;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -108522133:
                                                                                                                if (loc_key3.equals("AUTH_REGION")) {
                                                                                                                    obj2 = 79;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -107572034:
                                                                                                                if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                                                    obj2 = 6;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case -40534265:
                                                                                                                if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                                                    obj2 = 55;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 65254746:
                                                                                                                if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                                                    obj2 = 54;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 141040782:
                                                                                                                if (loc_key3.equals("CHAT_LEFT")) {
                                                                                                                    obj2 = 57;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 309993049:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                                                    obj2 = 41;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 309995634:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                                                    obj2 = 45;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 309995749:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                                                    obj2 = 47;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 320532812:
                                                                                                                if (loc_key3.equals("MESSAGES")) {
                                                                                                                    obj2 = 19;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 328933854:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                                                    obj2 = 42;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 331340546:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                                                    obj2 = 27;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 344816990:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                                                    obj2 = 22;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 346878138:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                                                    obj2 = 24;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 350376871:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                                                    obj2 = 23;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 615714517:
                                                                                                                if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                                                    obj2 = 3;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 715508879:
                                                                                                                if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                                                    obj2 = 70;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 728985323:
                                                                                                                if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                                                    obj2 = 65;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 731046471:
                                                                                                                if (loc_key3.equals("PINNED_ROUND")) {
                                                                                                                    obj2 = 67;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 734545204:
                                                                                                                if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                                                    obj2 = 66;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 802032552:
                                                                                                                if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                                                    obj2 = 11;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 991498806:
                                                                                                                if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                                                    obj2 = 73;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1019917311:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                                                    obj2 = 60;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1019926225:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                                                    obj2 = 48;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1020317708:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                                                    obj2 = 36;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1060349560:
                                                                                                                if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                                                    obj2 = 17;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1060358474:
                                                                                                                if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                                                    obj2 = 15;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1060749957:
                                                                                                                if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                                                    obj2 = null;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1073049781:
                                                                                                                if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                                                    obj2 = 64;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1078101399:
                                                                                                                if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                                                    obj2 = 51;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1110103437:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                                                    obj2 = 37;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1160762272:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                                                    obj2 = 61;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1172918249:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                                                    obj2 = 30;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1281128640:
                                                                                                                if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                                                    obj2 = 8;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1281131225:
                                                                                                                if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                                                    obj2 = 12;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1281131340:
                                                                                                                if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                                                    obj2 = 14;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1310789062:
                                                                                                                if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                                                    obj2 = 1;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1361447897:
                                                                                                                if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                                                    obj2 = 18;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1498266155:
                                                                                                                if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                                                    obj2 = 85;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1547988151:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                                                    obj2 = 43;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1561464595:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                                                    obj2 = 38;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1563525743:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                                                    obj2 = 40;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1567024476:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                                                    obj2 = 39;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1810705077:
                                                                                                                if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                                                    obj2 = 16;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1815177512:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                                                    obj2 = 35;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 1963241394:
                                                                                                                if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                                                    obj2 = 83;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 2014789757:
                                                                                                                if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                                                    obj2 = 52;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 2022049433:
                                                                                                                if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                                                    obj2 = 71;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 2048733346:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                                                    obj2 = 21;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 2099392181:
                                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                                                    obj2 = 34;
                                                                                                                    break;
                                                                                                                }
                                                                                                            case 2140162142:
                                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                                                    obj2 = 46;
                                                                                                                    break;
                                                                                                                }
                                                                                                                obj2 = -1;
                                                                                                                break;
                                                                                                            default:
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                messageKeyData = null;
                                                                                                if (json.has("loc_args")) {
                                                                                                    accountFinal = a2;
                                                                                                    jSONObject = json;
                                                                                                    bArr2 = strBytes;
                                                                                                    args = null;
                                                                                                } else {
                                                                                                    loc_args = json.getJSONArray("loc_args");
                                                                                                    json = new String[loc_args.length()];
                                                                                                    a = 0;
                                                                                                    while (true) {
                                                                                                        bArr2 = strBytes;
                                                                                                        accountFinal = a2;
                                                                                                        a2 = a;
                                                                                                        if (a2 >= json.length) {
                                                                                                            args = json;
                                                                                                        } else {
                                                                                                            json[a2] = loc_args.getString(a2);
                                                                                                            a = a2 + 1;
                                                                                                            strBytes = bArr2;
                                                                                                            a2 = accountFinal;
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                args2 = args;
                                                                                                strBytes = args2[0];
                                                                                                userName = null;
                                                                                                localMessage = false;
                                                                                                supergroup = false;
                                                                                                pinned = false;
                                                                                                channel = false;
                                                                                                messageText = null;
                                                                                                if (!loc_key3.startsWith("CHAT_")) {
                                                                                                    if (channel_id == 0) {
                                                                                                    }
                                                                                                    supergroup = channel_id == 0;
                                                                                                    userName2 = strBytes;
                                                                                                    strBytes = args2[1];
                                                                                                    userName = userName2;
                                                                                                } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                                    if (messageKey == null) {
                                                                                                    }
                                                                                                    supergroup = messageKey == null;
                                                                                                    pinned = true;
                                                                                                } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                                    channel = true;
                                                                                                }
                                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                                    message = null;
                                                                                                } else {
                                                                                                    stringBuilder2 = new StringBuilder();
                                                                                                    message = null;
                                                                                                    stringBuilder2.append("GCM received message notification ");
                                                                                                    stringBuilder2.append(loc_key3);
                                                                                                    stringBuilder2.append(" for dialogId = ");
                                                                                                    stringBuilder2.append(dialog_id);
                                                                                                    stringBuilder2.append(" mid = ");
                                                                                                    stringBuilder2.append(badge);
                                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                                }
                                                                                                switch (loc_key3.hashCode()) {
                                                                                                    case -2091498420:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                                            obj2 = 28;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -2053872415:
                                                                                                        if (loc_key3.equals("CHAT_CREATED")) {
                                                                                                            obj2 = 50;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -2039746363:
                                                                                                        if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                                            obj2 = 9;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1979538588:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                                            obj2 = 25;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1979536003:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                                            obj2 = 29;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1979535888:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                                            obj2 = 31;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1969004705:
                                                                                                        if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                                            obj2 = 53;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1946699248:
                                                                                                        if (loc_key3.equals("CHAT_JOINED")) {
                                                                                                            obj2 = 59;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1528047021:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                                            obj2 = 62;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1493579426:
                                                                                                        if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                                            obj2 = 10;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1480102982:
                                                                                                        if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                                            obj2 = 2;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1478041834:
                                                                                                        if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                                            obj2 = 7;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1474543101:
                                                                                                        if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                                            obj2 = 4;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1465695932:
                                                                                                        if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                                            obj2 = 81;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1374906292:
                                                                                                        if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                                            obj2 = 82;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1372940586:
                                                                                                        if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                                            obj2 = 58;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1264245338:
                                                                                                        if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                                            obj2 = 75;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1236086700:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                                            obj2 = 33;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1236077786:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                                            obj2 = 32;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1235686303:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                                            obj2 = 20;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1198046100:
                                                                                                        if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                                            obj2 = 5;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1124254527:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                                            obj2 = 44;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1085137927:
                                                                                                        if (loc_key3.equals("PINNED_GAME")) {
                                                                                                            obj2 = 74;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -1084746444:
                                                                                                        if (loc_key3.equals("PINNED_TEXT")) {
                                                                                                            obj2 = 63;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -819729482:
                                                                                                        if (loc_key3.equals("PINNED_STICKER")) {
                                                                                                            obj2 = 69;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -772141857:
                                                                                                        if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                                            obj2 = 84;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -638310039:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                                            obj2 = 26;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -589196239:
                                                                                                        if (loc_key3.equals("PINNED_DOC")) {
                                                                                                            obj2 = 68;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -589193654:
                                                                                                        if (loc_key3.equals("PINNED_GEO")) {
                                                                                                            obj2 = 72;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -589193539:
                                                                                                        if (loc_key3.equals("PINNED_GIF")) {
                                                                                                            obj2 = 76;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -440169325:
                                                                                                        if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                                            obj2 = 78;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -412748110:
                                                                                                        if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                                            obj2 = 56;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -228518075:
                                                                                                        if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                                            obj2 = 13;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -213586509:
                                                                                                        if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                                            obj2 = 80;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -115582002:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                                            obj2 = 49;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -112621464:
                                                                                                        if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                                            obj2 = 77;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -108522133:
                                                                                                        if (loc_key3.equals("AUTH_REGION")) {
                                                                                                            obj2 = 79;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -107572034:
                                                                                                        if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                                            obj2 = 6;
                                                                                                            break;
                                                                                                        }
                                                                                                    case -40534265:
                                                                                                        if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                                            obj2 = 55;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 65254746:
                                                                                                        if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                                            obj2 = 54;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 141040782:
                                                                                                        if (loc_key3.equals("CHAT_LEFT")) {
                                                                                                            obj2 = 57;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 309993049:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                                            obj2 = 41;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 309995634:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                                            obj2 = 45;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 309995749:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                                            obj2 = 47;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 320532812:
                                                                                                        if (loc_key3.equals("MESSAGES")) {
                                                                                                            obj2 = 19;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 328933854:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                                            obj2 = 42;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 331340546:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                                            obj2 = 27;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 344816990:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                                            obj2 = 22;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 346878138:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                                            obj2 = 24;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 350376871:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                                            obj2 = 23;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 615714517:
                                                                                                        if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                                            obj2 = 3;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 715508879:
                                                                                                        if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                                            obj2 = 70;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 728985323:
                                                                                                        if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                                            obj2 = 65;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 731046471:
                                                                                                        if (loc_key3.equals("PINNED_ROUND")) {
                                                                                                            obj2 = 67;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 734545204:
                                                                                                        if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                                            obj2 = 66;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 802032552:
                                                                                                        if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                                            obj2 = 11;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 991498806:
                                                                                                        if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                                            obj2 = 73;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1019917311:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                                            obj2 = 60;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1019926225:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                                            obj2 = 48;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1020317708:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                                            obj2 = 36;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1060349560:
                                                                                                        if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                                            obj2 = 17;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1060358474:
                                                                                                        if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                                            obj2 = 15;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1060749957:
                                                                                                        if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                                            obj2 = null;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1073049781:
                                                                                                        if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                                            obj2 = 64;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1078101399:
                                                                                                        if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                                            obj2 = 51;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1110103437:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                                            obj2 = 37;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1160762272:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                                            obj2 = 61;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1172918249:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                                            obj2 = 30;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1281128640:
                                                                                                        if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                                            obj2 = 8;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1281131225:
                                                                                                        if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                                            obj2 = 12;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1281131340:
                                                                                                        if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                                            obj2 = 14;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1310789062:
                                                                                                        if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                                            obj2 = 1;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1361447897:
                                                                                                        if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                                            obj2 = 18;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1498266155:
                                                                                                        if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                                            obj2 = 85;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1547988151:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                                            obj2 = 43;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1561464595:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                                            obj2 = 38;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1563525743:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                                            obj2 = 40;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1567024476:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                                            obj2 = 39;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1810705077:
                                                                                                        if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                                            obj2 = 16;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1815177512:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                                            obj2 = 35;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 1963241394:
                                                                                                        if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                                            obj2 = 83;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 2014789757:
                                                                                                        if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                                            obj2 = 52;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 2022049433:
                                                                                                        if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                                            obj2 = 71;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 2048733346:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                                            obj2 = 21;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 2099392181:
                                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                                            obj2 = 34;
                                                                                                            break;
                                                                                                        }
                                                                                                    case 2140162142:
                                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                                            obj2 = 46;
                                                                                                            break;
                                                                                                        }
                                                                                                        obj2 = -1;
                                                                                                        break;
                                                                                                    default:
                                                                                                }
                                                                                            } else {
                                                                                                return;
                                                                                            }
                                                                                        }
                                                                                        accountFinal = a2;
                                                                                        dialog_id3 = dialog_id;
                                                                                        jSONObject = json;
                                                                                        i = badge;
                                                                                        i2 = len;
                                                                                        bArr3 = messageKey;
                                                                                        messageKeyData2 = messageKeyData;
                                                                                        bArr2 = strBytes;
                                                                                        currentAccount = chat_id3;
                                                                                        a2 = custom.getInt("max_id");
                                                                                        updates2 = new ArrayList();
                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                        } else {
                                                                                            stringBuilder3 = new StringBuilder();
                                                                                            stringBuilder3.append("GCM received read notification max_id = ");
                                                                                            stringBuilder3.append(a2);
                                                                                            stringBuilder3.append(" for dialogId = ");
                                                                                            stringBuilder3.append(dialog_id3);
                                                                                            FileLog.m0d(stringBuilder3.toString());
                                                                                        }
                                                                                        if (channel_id == 0) {
                                                                                            update2 = new TL_updateReadChannelInbox();
                                                                                            update2.channel_id = channel_id;
                                                                                            update2.max_id = a2;
                                                                                            updates2.add(update2);
                                                                                        } else {
                                                                                            update3 = new TL_updateReadHistoryInbox();
                                                                                            if (user_id == 0) {
                                                                                                update3.peer = new TL_peerUser();
                                                                                                update3.peer.user_id = user_id;
                                                                                            } else {
                                                                                                update3.peer = new TL_peerChat();
                                                                                                update3.peer.chat_id = currentAccount;
                                                                                            }
                                                                                            update3.max_id = a2;
                                                                                            updates2.add(update3);
                                                                                        }
                                                                                        MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                                                                        ConnectionsManager.onInternalPushReceived(currentAccount2);
                                                                                        ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                                    } else {
                                                                                        return;
                                                                                    }
                                                                                } catch (Throwable th222) {
                                                                                    str = loc_key3;
                                                                                    currentAccount = currentAccount2;
                                                                                    loc_key = str;
                                                                                    e = th222;
                                                                                    if (currentAccount == -1) {
                                                                                        GcmPushListenerService.this.onDecryptError();
                                                                                    } else {
                                                                                        ConnectionsManager.onInternalPushReceived(currentAccount);
                                                                                        ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                                                                    }
                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                        stringBuilder = new StringBuilder();
                                                                                        stringBuilder.append("error in loc_key = ");
                                                                                        stringBuilder.append(loc_key);
                                                                                        FileLog.m1e(stringBuilder.toString());
                                                                                    }
                                                                                    FileLog.m3e(e);
                                                                                    str = loc_key;
                                                                                }
                                                                        }
                                                                    }
                                                                } catch (Throwable th3) {
                                                                    th222 = th3;
                                                                    loc_key = loc_key3;
                                                                    currentAccount = currentAccount2;
                                                                    e = th222;
                                                                    if (currentAccount == -1) {
                                                                        ConnectionsManager.onInternalPushReceived(currentAccount);
                                                                        ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                                                    } else {
                                                                        GcmPushListenerService.this.onDecryptError();
                                                                    }
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        stringBuilder = new StringBuilder();
                                                                        stringBuilder.append("error in loc_key = ");
                                                                        stringBuilder.append(loc_key);
                                                                        FileLog.m1e(stringBuilder.toString());
                                                                    }
                                                                    FileLog.m3e(e);
                                                                    str = loc_key;
                                                                }
                                                            }
                                                        } else if (loc_key3.equals("DC_UPDATE")) {
                                                            obj = null;
                                                            switch (obj) {
                                                                case null:
                                                                    dialog_id = custom.getInt("dc");
                                                                    addr = custom.getString("addr");
                                                                    parts = addr.split(":");
                                                                    nativeByteBuffer = buffer;
                                                                    if (parts.length != 2) {
                                                                        ConnectionsManager.getInstance(currentAccount2).applyDatacenterAddress(dialog_id, parts[null], Integer.parseInt(parts[1]));
                                                                        ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                        return;
                                                                    }
                                                                    return;
                                                                case 1:
                                                                    update = new TL_updateServiceNotification();
                                                                    update.popup = false;
                                                                    update.flags = 2;
                                                                    bArr = bytes;
                                                                    update.inbox_date = (int) (time / 1000);
                                                                    update.message = json.getString("message");
                                                                    update.type = "announcement";
                                                                    update.media = new TL_messageMediaEmpty();
                                                                    updates = new TL_updates();
                                                                    updates.updates.add(update);
                                                                    Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                                    ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                    return;
                                                                default:
                                                                    dialog_id = 0;
                                                                    if (custom.has("channel_id")) {
                                                                        channel_id = 0;
                                                                    } else {
                                                                        channel_id = custom.getInt("channel_id");
                                                                        dialog_id = (long) (-channel_id);
                                                                    }
                                                                    if (custom.has("from_id")) {
                                                                        user_id = 0;
                                                                    } else {
                                                                        user_id = custom.getInt("from_id");
                                                                        dialog_id = (long) user_id;
                                                                    }
                                                                    dialog_id2 = dialog_id;
                                                                    if (custom.has("chat_id")) {
                                                                        dialog_id = dialog_id2;
                                                                        chat_id = 0;
                                                                    } else {
                                                                        chat_id2 = custom.getInt("chat_id");
                                                                        chat_id = chat_id2;
                                                                        dialog_id = (long) (-chat_id2);
                                                                    }
                                                                    chat_id3 = chat_id;
                                                                    if (dialog_id == 0) {
                                                                        if (json.has("badge")) {
                                                                            badge = 0;
                                                                        } else {
                                                                            badge = json.getInt("badge");
                                                                        }
                                                                        if (badge == 0) {
                                                                            accountFinal = a2;
                                                                            dialog_id3 = dialog_id;
                                                                            jSONObject = json;
                                                                            i = badge;
                                                                            i2 = len;
                                                                            bArr3 = messageKey;
                                                                            messageKeyData2 = messageKeyData;
                                                                            bArr2 = strBytes;
                                                                            currentAccount = chat_id3;
                                                                            a2 = custom.getInt("max_id");
                                                                            updates2 = new ArrayList();
                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                            } else {
                                                                                stringBuilder3 = new StringBuilder();
                                                                                stringBuilder3.append("GCM received read notification max_id = ");
                                                                                stringBuilder3.append(a2);
                                                                                stringBuilder3.append(" for dialogId = ");
                                                                                stringBuilder3.append(dialog_id3);
                                                                                FileLog.m0d(stringBuilder3.toString());
                                                                            }
                                                                            if (channel_id == 0) {
                                                                                update3 = new TL_updateReadHistoryInbox();
                                                                                if (user_id == 0) {
                                                                                    update3.peer = new TL_peerChat();
                                                                                    update3.peer.chat_id = currentAccount;
                                                                                } else {
                                                                                    update3.peer = new TL_peerUser();
                                                                                    update3.peer.user_id = user_id;
                                                                                }
                                                                                update3.max_id = a2;
                                                                                updates2.add(update3);
                                                                            } else {
                                                                                update2 = new TL_updateReadChannelInbox();
                                                                                update2.channel_id = channel_id;
                                                                                update2.max_id = a2;
                                                                                updates2.add(update2);
                                                                            }
                                                                            MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                                                        } else {
                                                                            badge = custom.getInt("msg_id");
                                                                            currentReadValue = (Integer) MessagesController.getInstance(currentAccount2).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                                            if (currentReadValue != null) {
                                                                                num = currentReadValue;
                                                                                messageKeyData2 = messageKeyData;
                                                                            } else {
                                                                                currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount2).getDialogReadMax(null, dialog_id));
                                                                                MessagesController.getInstance(a2).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                                            }
                                                                            if (badge <= currentReadValue.intValue()) {
                                                                                if (custom.has("chat_from_id")) {
                                                                                    messageKey = null;
                                                                                } else {
                                                                                    messageKey = custom.getInt("chat_from_id");
                                                                                }
                                                                                if (custom.has("mention")) {
                                                                                    if (custom.getInt("mention") != 0) {
                                                                                        messageKeyData = true;
                                                                                        if (json.has("loc_args")) {
                                                                                            loc_args = json.getJSONArray("loc_args");
                                                                                            json = new String[loc_args.length()];
                                                                                            a = 0;
                                                                                            while (true) {
                                                                                                bArr2 = strBytes;
                                                                                                accountFinal = a2;
                                                                                                a2 = a;
                                                                                                if (a2 >= json.length) {
                                                                                                    json[a2] = loc_args.getString(a2);
                                                                                                    a = a2 + 1;
                                                                                                    strBytes = bArr2;
                                                                                                    a2 = accountFinal;
                                                                                                } else {
                                                                                                    args = json;
                                                                                                }
                                                                                            }
                                                                                        } else {
                                                                                            accountFinal = a2;
                                                                                            jSONObject = json;
                                                                                            bArr2 = strBytes;
                                                                                            args = null;
                                                                                        }
                                                                                        args2 = args;
                                                                                        strBytes = args2[0];
                                                                                        userName = null;
                                                                                        localMessage = false;
                                                                                        supergroup = false;
                                                                                        pinned = false;
                                                                                        channel = false;
                                                                                        messageText = null;
                                                                                        if (!loc_key3.startsWith("CHAT_")) {
                                                                                            if (channel_id == 0) {
                                                                                            }
                                                                                            supergroup = channel_id == 0;
                                                                                            userName2 = strBytes;
                                                                                            strBytes = args2[1];
                                                                                            userName = userName2;
                                                                                        } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                            if (messageKey == null) {
                                                                                            }
                                                                                            supergroup = messageKey == null;
                                                                                            pinned = true;
                                                                                        } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                            channel = true;
                                                                                        }
                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                            stringBuilder2 = new StringBuilder();
                                                                                            message = null;
                                                                                            stringBuilder2.append("GCM received message notification ");
                                                                                            stringBuilder2.append(loc_key3);
                                                                                            stringBuilder2.append(" for dialogId = ");
                                                                                            stringBuilder2.append(dialog_id);
                                                                                            stringBuilder2.append(" mid = ");
                                                                                            stringBuilder2.append(badge);
                                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                                        } else {
                                                                                            message = null;
                                                                                        }
                                                                                        switch (loc_key3.hashCode()) {
                                                                                            case -2091498420:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                                    obj2 = 28;
                                                                                                    break;
                                                                                                }
                                                                                            case -2053872415:
                                                                                                if (loc_key3.equals("CHAT_CREATED")) {
                                                                                                    obj2 = 50;
                                                                                                    break;
                                                                                                }
                                                                                            case -2039746363:
                                                                                                if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                                    obj2 = 9;
                                                                                                    break;
                                                                                                }
                                                                                            case -1979538588:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                                    obj2 = 25;
                                                                                                    break;
                                                                                                }
                                                                                            case -1979536003:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                                    obj2 = 29;
                                                                                                    break;
                                                                                                }
                                                                                            case -1979535888:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                                    obj2 = 31;
                                                                                                    break;
                                                                                                }
                                                                                            case -1969004705:
                                                                                                if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                                    obj2 = 53;
                                                                                                    break;
                                                                                                }
                                                                                            case -1946699248:
                                                                                                if (loc_key3.equals("CHAT_JOINED")) {
                                                                                                    obj2 = 59;
                                                                                                    break;
                                                                                                }
                                                                                            case -1528047021:
                                                                                                if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                                    obj2 = 62;
                                                                                                    break;
                                                                                                }
                                                                                            case -1493579426:
                                                                                                if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                                    obj2 = 10;
                                                                                                    break;
                                                                                                }
                                                                                            case -1480102982:
                                                                                                if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                                    obj2 = 2;
                                                                                                    break;
                                                                                                }
                                                                                            case -1478041834:
                                                                                                if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                                    obj2 = 7;
                                                                                                    break;
                                                                                                }
                                                                                            case -1474543101:
                                                                                                if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                                    obj2 = 4;
                                                                                                    break;
                                                                                                }
                                                                                            case -1465695932:
                                                                                                if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                                    obj2 = 81;
                                                                                                    break;
                                                                                                }
                                                                                            case -1374906292:
                                                                                                if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                                    obj2 = 82;
                                                                                                    break;
                                                                                                }
                                                                                            case -1372940586:
                                                                                                if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                                    obj2 = 58;
                                                                                                    break;
                                                                                                }
                                                                                            case -1264245338:
                                                                                                if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                                    obj2 = 75;
                                                                                                    break;
                                                                                                }
                                                                                            case -1236086700:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                                    obj2 = 33;
                                                                                                    break;
                                                                                                }
                                                                                            case -1236077786:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                                    obj2 = 32;
                                                                                                    break;
                                                                                                }
                                                                                            case -1235686303:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                                    obj2 = 20;
                                                                                                    break;
                                                                                                }
                                                                                            case -1198046100:
                                                                                                if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                                    obj2 = 5;
                                                                                                    break;
                                                                                                }
                                                                                            case -1124254527:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                                    obj2 = 44;
                                                                                                    break;
                                                                                                }
                                                                                            case -1085137927:
                                                                                                if (loc_key3.equals("PINNED_GAME")) {
                                                                                                    obj2 = 74;
                                                                                                    break;
                                                                                                }
                                                                                            case -1084746444:
                                                                                                if (loc_key3.equals("PINNED_TEXT")) {
                                                                                                    obj2 = 63;
                                                                                                    break;
                                                                                                }
                                                                                            case -819729482:
                                                                                                if (loc_key3.equals("PINNED_STICKER")) {
                                                                                                    obj2 = 69;
                                                                                                    break;
                                                                                                }
                                                                                            case -772141857:
                                                                                                if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                                    obj2 = 84;
                                                                                                    break;
                                                                                                }
                                                                                            case -638310039:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                                    obj2 = 26;
                                                                                                    break;
                                                                                                }
                                                                                            case -589196239:
                                                                                                if (loc_key3.equals("PINNED_DOC")) {
                                                                                                    obj2 = 68;
                                                                                                    break;
                                                                                                }
                                                                                            case -589193654:
                                                                                                if (loc_key3.equals("PINNED_GEO")) {
                                                                                                    obj2 = 72;
                                                                                                    break;
                                                                                                }
                                                                                            case -589193539:
                                                                                                if (loc_key3.equals("PINNED_GIF")) {
                                                                                                    obj2 = 76;
                                                                                                    break;
                                                                                                }
                                                                                            case -440169325:
                                                                                                if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                                    obj2 = 78;
                                                                                                    break;
                                                                                                }
                                                                                            case -412748110:
                                                                                                if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                                    obj2 = 56;
                                                                                                    break;
                                                                                                }
                                                                                            case -228518075:
                                                                                                if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                                    obj2 = 13;
                                                                                                    break;
                                                                                                }
                                                                                            case -213586509:
                                                                                                if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                                    obj2 = 80;
                                                                                                    break;
                                                                                                }
                                                                                            case -115582002:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                                    obj2 = 49;
                                                                                                    break;
                                                                                                }
                                                                                            case -112621464:
                                                                                                if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                                    obj2 = 77;
                                                                                                    break;
                                                                                                }
                                                                                            case -108522133:
                                                                                                if (loc_key3.equals("AUTH_REGION")) {
                                                                                                    obj2 = 79;
                                                                                                    break;
                                                                                                }
                                                                                            case -107572034:
                                                                                                if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                                    obj2 = 6;
                                                                                                    break;
                                                                                                }
                                                                                            case -40534265:
                                                                                                if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                                    obj2 = 55;
                                                                                                    break;
                                                                                                }
                                                                                            case 65254746:
                                                                                                if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                                    obj2 = 54;
                                                                                                    break;
                                                                                                }
                                                                                            case 141040782:
                                                                                                if (loc_key3.equals("CHAT_LEFT")) {
                                                                                                    obj2 = 57;
                                                                                                    break;
                                                                                                }
                                                                                            case 309993049:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                                    obj2 = 41;
                                                                                                    break;
                                                                                                }
                                                                                            case 309995634:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                                    obj2 = 45;
                                                                                                    break;
                                                                                                }
                                                                                            case 309995749:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                                    obj2 = 47;
                                                                                                    break;
                                                                                                }
                                                                                            case 320532812:
                                                                                                if (loc_key3.equals("MESSAGES")) {
                                                                                                    obj2 = 19;
                                                                                                    break;
                                                                                                }
                                                                                            case 328933854:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                                    obj2 = 42;
                                                                                                    break;
                                                                                                }
                                                                                            case 331340546:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                                    obj2 = 27;
                                                                                                    break;
                                                                                                }
                                                                                            case 344816990:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                                    obj2 = 22;
                                                                                                    break;
                                                                                                }
                                                                                            case 346878138:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                                    obj2 = 24;
                                                                                                    break;
                                                                                                }
                                                                                            case 350376871:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                                    obj2 = 23;
                                                                                                    break;
                                                                                                }
                                                                                            case 615714517:
                                                                                                if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                                    obj2 = 3;
                                                                                                    break;
                                                                                                }
                                                                                            case 715508879:
                                                                                                if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                                    obj2 = 70;
                                                                                                    break;
                                                                                                }
                                                                                            case 728985323:
                                                                                                if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                                    obj2 = 65;
                                                                                                    break;
                                                                                                }
                                                                                            case 731046471:
                                                                                                if (loc_key3.equals("PINNED_ROUND")) {
                                                                                                    obj2 = 67;
                                                                                                    break;
                                                                                                }
                                                                                            case 734545204:
                                                                                                if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                                    obj2 = 66;
                                                                                                    break;
                                                                                                }
                                                                                            case 802032552:
                                                                                                if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                                    obj2 = 11;
                                                                                                    break;
                                                                                                }
                                                                                            case 991498806:
                                                                                                if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                                    obj2 = 73;
                                                                                                    break;
                                                                                                }
                                                                                            case 1019917311:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                                    obj2 = 60;
                                                                                                    break;
                                                                                                }
                                                                                            case 1019926225:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                                    obj2 = 48;
                                                                                                    break;
                                                                                                }
                                                                                            case 1020317708:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                                    obj2 = 36;
                                                                                                    break;
                                                                                                }
                                                                                            case 1060349560:
                                                                                                if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                                    obj2 = 17;
                                                                                                    break;
                                                                                                }
                                                                                            case 1060358474:
                                                                                                if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                                    obj2 = 15;
                                                                                                    break;
                                                                                                }
                                                                                            case 1060749957:
                                                                                                if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                                    obj2 = null;
                                                                                                    break;
                                                                                                }
                                                                                            case 1073049781:
                                                                                                if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                                    obj2 = 64;
                                                                                                    break;
                                                                                                }
                                                                                            case 1078101399:
                                                                                                if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                                    obj2 = 51;
                                                                                                    break;
                                                                                                }
                                                                                            case 1110103437:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                                    obj2 = 37;
                                                                                                    break;
                                                                                                }
                                                                                            case 1160762272:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                                    obj2 = 61;
                                                                                                    break;
                                                                                                }
                                                                                            case 1172918249:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                                    obj2 = 30;
                                                                                                    break;
                                                                                                }
                                                                                            case 1281128640:
                                                                                                if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                                    obj2 = 8;
                                                                                                    break;
                                                                                                }
                                                                                            case 1281131225:
                                                                                                if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                                    obj2 = 12;
                                                                                                    break;
                                                                                                }
                                                                                            case 1281131340:
                                                                                                if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                                    obj2 = 14;
                                                                                                    break;
                                                                                                }
                                                                                            case 1310789062:
                                                                                                if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                                    obj2 = 1;
                                                                                                    break;
                                                                                                }
                                                                                            case 1361447897:
                                                                                                if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                                    obj2 = 18;
                                                                                                    break;
                                                                                                }
                                                                                            case 1498266155:
                                                                                                if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                                    obj2 = 85;
                                                                                                    break;
                                                                                                }
                                                                                            case 1547988151:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                                    obj2 = 43;
                                                                                                    break;
                                                                                                }
                                                                                            case 1561464595:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                                    obj2 = 38;
                                                                                                    break;
                                                                                                }
                                                                                            case 1563525743:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                                    obj2 = 40;
                                                                                                    break;
                                                                                                }
                                                                                            case 1567024476:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                                    obj2 = 39;
                                                                                                    break;
                                                                                                }
                                                                                            case 1810705077:
                                                                                                if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                                    obj2 = 16;
                                                                                                    break;
                                                                                                }
                                                                                            case 1815177512:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                                    obj2 = 35;
                                                                                                    break;
                                                                                                }
                                                                                            case 1963241394:
                                                                                                if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                                    obj2 = 83;
                                                                                                    break;
                                                                                                }
                                                                                            case 2014789757:
                                                                                                if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                                    obj2 = 52;
                                                                                                    break;
                                                                                                }
                                                                                            case 2022049433:
                                                                                                if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                                    obj2 = 71;
                                                                                                    break;
                                                                                                }
                                                                                            case 2048733346:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                                    obj2 = 21;
                                                                                                    break;
                                                                                                }
                                                                                            case 2099392181:
                                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                                    obj2 = 34;
                                                                                                    break;
                                                                                                }
                                                                                            case 2140162142:
                                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                                    obj2 = 46;
                                                                                                    break;
                                                                                                }
                                                                                                obj2 = -1;
                                                                                                break;
                                                                                            default:
                                                                                        }
                                                                                    }
                                                                                }
                                                                                messageKeyData = null;
                                                                                if (json.has("loc_args")) {
                                                                                    accountFinal = a2;
                                                                                    jSONObject = json;
                                                                                    bArr2 = strBytes;
                                                                                    args = null;
                                                                                } else {
                                                                                    loc_args = json.getJSONArray("loc_args");
                                                                                    json = new String[loc_args.length()];
                                                                                    a = 0;
                                                                                    while (true) {
                                                                                        bArr2 = strBytes;
                                                                                        accountFinal = a2;
                                                                                        a2 = a;
                                                                                        if (a2 >= json.length) {
                                                                                            args = json;
                                                                                        } else {
                                                                                            json[a2] = loc_args.getString(a2);
                                                                                            a = a2 + 1;
                                                                                            strBytes = bArr2;
                                                                                            a2 = accountFinal;
                                                                                        }
                                                                                    }
                                                                                }
                                                                                args2 = args;
                                                                                strBytes = args2[0];
                                                                                userName = null;
                                                                                localMessage = false;
                                                                                supergroup = false;
                                                                                pinned = false;
                                                                                channel = false;
                                                                                messageText = null;
                                                                                if (!loc_key3.startsWith("CHAT_")) {
                                                                                    if (channel_id == 0) {
                                                                                    }
                                                                                    supergroup = channel_id == 0;
                                                                                    userName2 = strBytes;
                                                                                    strBytes = args2[1];
                                                                                    userName = userName2;
                                                                                } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                    if (messageKey == null) {
                                                                                    }
                                                                                    supergroup = messageKey == null;
                                                                                    pinned = true;
                                                                                } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                    channel = true;
                                                                                }
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    message = null;
                                                                                } else {
                                                                                    stringBuilder2 = new StringBuilder();
                                                                                    message = null;
                                                                                    stringBuilder2.append("GCM received message notification ");
                                                                                    stringBuilder2.append(loc_key3);
                                                                                    stringBuilder2.append(" for dialogId = ");
                                                                                    stringBuilder2.append(dialog_id);
                                                                                    stringBuilder2.append(" mid = ");
                                                                                    stringBuilder2.append(badge);
                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                }
                                                                                switch (loc_key3.hashCode()) {
                                                                                    case -2091498420:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                            obj2 = 28;
                                                                                            break;
                                                                                        }
                                                                                    case -2053872415:
                                                                                        if (loc_key3.equals("CHAT_CREATED")) {
                                                                                            obj2 = 50;
                                                                                            break;
                                                                                        }
                                                                                    case -2039746363:
                                                                                        if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                            obj2 = 9;
                                                                                            break;
                                                                                        }
                                                                                    case -1979538588:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                            obj2 = 25;
                                                                                            break;
                                                                                        }
                                                                                    case -1979536003:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                            obj2 = 29;
                                                                                            break;
                                                                                        }
                                                                                    case -1979535888:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                            obj2 = 31;
                                                                                            break;
                                                                                        }
                                                                                    case -1969004705:
                                                                                        if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                            obj2 = 53;
                                                                                            break;
                                                                                        }
                                                                                    case -1946699248:
                                                                                        if (loc_key3.equals("CHAT_JOINED")) {
                                                                                            obj2 = 59;
                                                                                            break;
                                                                                        }
                                                                                    case -1528047021:
                                                                                        if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                            obj2 = 62;
                                                                                            break;
                                                                                        }
                                                                                    case -1493579426:
                                                                                        if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                            obj2 = 10;
                                                                                            break;
                                                                                        }
                                                                                    case -1480102982:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                            obj2 = 2;
                                                                                            break;
                                                                                        }
                                                                                    case -1478041834:
                                                                                        if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                            obj2 = 7;
                                                                                            break;
                                                                                        }
                                                                                    case -1474543101:
                                                                                        if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                            obj2 = 4;
                                                                                            break;
                                                                                        }
                                                                                    case -1465695932:
                                                                                        if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                            obj2 = 81;
                                                                                            break;
                                                                                        }
                                                                                    case -1374906292:
                                                                                        if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                            obj2 = 82;
                                                                                            break;
                                                                                        }
                                                                                    case -1372940586:
                                                                                        if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                            obj2 = 58;
                                                                                            break;
                                                                                        }
                                                                                    case -1264245338:
                                                                                        if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                            obj2 = 75;
                                                                                            break;
                                                                                        }
                                                                                    case -1236086700:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                            obj2 = 33;
                                                                                            break;
                                                                                        }
                                                                                    case -1236077786:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                            obj2 = 32;
                                                                                            break;
                                                                                        }
                                                                                    case -1235686303:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                            obj2 = 20;
                                                                                            break;
                                                                                        }
                                                                                    case -1198046100:
                                                                                        if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                            obj2 = 5;
                                                                                            break;
                                                                                        }
                                                                                    case -1124254527:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                            obj2 = 44;
                                                                                            break;
                                                                                        }
                                                                                    case -1085137927:
                                                                                        if (loc_key3.equals("PINNED_GAME")) {
                                                                                            obj2 = 74;
                                                                                            break;
                                                                                        }
                                                                                    case -1084746444:
                                                                                        if (loc_key3.equals("PINNED_TEXT")) {
                                                                                            obj2 = 63;
                                                                                            break;
                                                                                        }
                                                                                    case -819729482:
                                                                                        if (loc_key3.equals("PINNED_STICKER")) {
                                                                                            obj2 = 69;
                                                                                            break;
                                                                                        }
                                                                                    case -772141857:
                                                                                        if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                            obj2 = 84;
                                                                                            break;
                                                                                        }
                                                                                    case -638310039:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                            obj2 = 26;
                                                                                            break;
                                                                                        }
                                                                                    case -589196239:
                                                                                        if (loc_key3.equals("PINNED_DOC")) {
                                                                                            obj2 = 68;
                                                                                            break;
                                                                                        }
                                                                                    case -589193654:
                                                                                        if (loc_key3.equals("PINNED_GEO")) {
                                                                                            obj2 = 72;
                                                                                            break;
                                                                                        }
                                                                                    case -589193539:
                                                                                        if (loc_key3.equals("PINNED_GIF")) {
                                                                                            obj2 = 76;
                                                                                            break;
                                                                                        }
                                                                                    case -440169325:
                                                                                        if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                            obj2 = 78;
                                                                                            break;
                                                                                        }
                                                                                    case -412748110:
                                                                                        if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                            obj2 = 56;
                                                                                            break;
                                                                                        }
                                                                                    case -228518075:
                                                                                        if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 13;
                                                                                            break;
                                                                                        }
                                                                                    case -213586509:
                                                                                        if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                            obj2 = 80;
                                                                                            break;
                                                                                        }
                                                                                    case -115582002:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                            obj2 = 49;
                                                                                            break;
                                                                                        }
                                                                                    case -112621464:
                                                                                        if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                            obj2 = 77;
                                                                                            break;
                                                                                        }
                                                                                    case -108522133:
                                                                                        if (loc_key3.equals("AUTH_REGION")) {
                                                                                            obj2 = 79;
                                                                                            break;
                                                                                        }
                                                                                    case -107572034:
                                                                                        if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                            obj2 = 6;
                                                                                            break;
                                                                                        }
                                                                                    case -40534265:
                                                                                        if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                            obj2 = 55;
                                                                                            break;
                                                                                        }
                                                                                    case 65254746:
                                                                                        if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                            obj2 = 54;
                                                                                            break;
                                                                                        }
                                                                                    case 141040782:
                                                                                        if (loc_key3.equals("CHAT_LEFT")) {
                                                                                            obj2 = 57;
                                                                                            break;
                                                                                        }
                                                                                    case 309993049:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                            obj2 = 41;
                                                                                            break;
                                                                                        }
                                                                                    case 309995634:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                            obj2 = 45;
                                                                                            break;
                                                                                        }
                                                                                    case 309995749:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                            obj2 = 47;
                                                                                            break;
                                                                                        }
                                                                                    case 320532812:
                                                                                        if (loc_key3.equals("MESSAGES")) {
                                                                                            obj2 = 19;
                                                                                            break;
                                                                                        }
                                                                                    case 328933854:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                            obj2 = 42;
                                                                                            break;
                                                                                        }
                                                                                    case 331340546:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                            obj2 = 27;
                                                                                            break;
                                                                                        }
                                                                                    case 344816990:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                            obj2 = 22;
                                                                                            break;
                                                                                        }
                                                                                    case 346878138:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                            obj2 = 24;
                                                                                            break;
                                                                                        }
                                                                                    case 350376871:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                            obj2 = 23;
                                                                                            break;
                                                                                        }
                                                                                    case 615714517:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                            obj2 = 3;
                                                                                            break;
                                                                                        }
                                                                                    case 715508879:
                                                                                        if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                            obj2 = 70;
                                                                                            break;
                                                                                        }
                                                                                    case 728985323:
                                                                                        if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                            obj2 = 65;
                                                                                            break;
                                                                                        }
                                                                                    case 731046471:
                                                                                        if (loc_key3.equals("PINNED_ROUND")) {
                                                                                            obj2 = 67;
                                                                                            break;
                                                                                        }
                                                                                    case 734545204:
                                                                                        if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                            obj2 = 66;
                                                                                            break;
                                                                                        }
                                                                                    case 802032552:
                                                                                        if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                            obj2 = 11;
                                                                                            break;
                                                                                        }
                                                                                    case 991498806:
                                                                                        if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                            obj2 = 73;
                                                                                            break;
                                                                                        }
                                                                                    case 1019917311:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                            obj2 = 60;
                                                                                            break;
                                                                                        }
                                                                                    case 1019926225:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                            obj2 = 48;
                                                                                            break;
                                                                                        }
                                                                                    case 1020317708:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                            obj2 = 36;
                                                                                            break;
                                                                                        }
                                                                                    case 1060349560:
                                                                                        if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                            obj2 = 17;
                                                                                            break;
                                                                                        }
                                                                                    case 1060358474:
                                                                                        if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                            obj2 = 15;
                                                                                            break;
                                                                                        }
                                                                                    case 1060749957:
                                                                                        if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                            obj2 = null;
                                                                                            break;
                                                                                        }
                                                                                    case 1073049781:
                                                                                        if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                            obj2 = 64;
                                                                                            break;
                                                                                        }
                                                                                    case 1078101399:
                                                                                        if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                            obj2 = 51;
                                                                                            break;
                                                                                        }
                                                                                    case 1110103437:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                            obj2 = 37;
                                                                                            break;
                                                                                        }
                                                                                    case 1160762272:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                            obj2 = 61;
                                                                                            break;
                                                                                        }
                                                                                    case 1172918249:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 30;
                                                                                            break;
                                                                                        }
                                                                                    case 1281128640:
                                                                                        if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                            obj2 = 8;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131225:
                                                                                        if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                            obj2 = 12;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131340:
                                                                                        if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                            obj2 = 14;
                                                                                            break;
                                                                                        }
                                                                                    case 1310789062:
                                                                                        if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                            obj2 = 1;
                                                                                            break;
                                                                                        }
                                                                                    case 1361447897:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                            obj2 = 18;
                                                                                            break;
                                                                                        }
                                                                                    case 1498266155:
                                                                                        if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                            obj2 = 85;
                                                                                            break;
                                                                                        }
                                                                                    case 1547988151:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                            obj2 = 43;
                                                                                            break;
                                                                                        }
                                                                                    case 1561464595:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                            obj2 = 38;
                                                                                            break;
                                                                                        }
                                                                                    case 1563525743:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                            obj2 = 40;
                                                                                            break;
                                                                                        }
                                                                                    case 1567024476:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                            obj2 = 39;
                                                                                            break;
                                                                                        }
                                                                                    case 1810705077:
                                                                                        if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                            obj2 = 16;
                                                                                            break;
                                                                                        }
                                                                                    case 1815177512:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                            obj2 = 35;
                                                                                            break;
                                                                                        }
                                                                                    case 1963241394:
                                                                                        if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                            obj2 = 83;
                                                                                            break;
                                                                                        }
                                                                                    case 2014789757:
                                                                                        if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                            obj2 = 52;
                                                                                            break;
                                                                                        }
                                                                                    case 2022049433:
                                                                                        if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                            obj2 = 71;
                                                                                            break;
                                                                                        }
                                                                                    case 2048733346:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                            obj2 = 21;
                                                                                            break;
                                                                                        }
                                                                                    case 2099392181:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                            obj2 = 34;
                                                                                            break;
                                                                                        }
                                                                                    case 2140162142:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 46;
                                                                                            break;
                                                                                        }
                                                                                        obj2 = -1;
                                                                                        break;
                                                                                    default:
                                                                                }
                                                                            } else {
                                                                                return;
                                                                            }
                                                                        }
                                                                        ConnectionsManager.onInternalPushReceived(currentAccount2);
                                                                        ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                        break;
                                                                    }
                                                                    return;
                                                            }
                                                        }
                                                        obj = -1;
                                                        switch (obj) {
                                                            case null:
                                                                dialog_id = custom.getInt("dc");
                                                                addr = custom.getString("addr");
                                                                parts = addr.split(":");
                                                                nativeByteBuffer = buffer;
                                                                if (parts.length != 2) {
                                                                    ConnectionsManager.getInstance(currentAccount2).applyDatacenterAddress(dialog_id, parts[null], Integer.parseInt(parts[1]));
                                                                    ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                    return;
                                                                }
                                                                return;
                                                            case 1:
                                                                update = new TL_updateServiceNotification();
                                                                update.popup = false;
                                                                update.flags = 2;
                                                                bArr = bytes;
                                                                update.inbox_date = (int) (time / 1000);
                                                                update.message = json.getString("message");
                                                                update.type = "announcement";
                                                                update.media = new TL_messageMediaEmpty();
                                                                updates = new TL_updates();
                                                                updates.updates.add(update);
                                                                Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                                ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                return;
                                                            default:
                                                                dialog_id = 0;
                                                                if (custom.has("channel_id")) {
                                                                    channel_id = custom.getInt("channel_id");
                                                                    dialog_id = (long) (-channel_id);
                                                                } else {
                                                                    channel_id = 0;
                                                                }
                                                                if (custom.has("from_id")) {
                                                                    user_id = custom.getInt("from_id");
                                                                    dialog_id = (long) user_id;
                                                                } else {
                                                                    user_id = 0;
                                                                }
                                                                dialog_id2 = dialog_id;
                                                                if (custom.has("chat_id")) {
                                                                    chat_id2 = custom.getInt("chat_id");
                                                                    chat_id = chat_id2;
                                                                    dialog_id = (long) (-chat_id2);
                                                                } else {
                                                                    dialog_id = dialog_id2;
                                                                    chat_id = 0;
                                                                }
                                                                chat_id3 = chat_id;
                                                                if (dialog_id == 0) {
                                                                    if (json.has("badge")) {
                                                                        badge = json.getInt("badge");
                                                                    } else {
                                                                        badge = 0;
                                                                    }
                                                                    if (badge == 0) {
                                                                        badge = custom.getInt("msg_id");
                                                                        currentReadValue = (Integer) MessagesController.getInstance(currentAccount2).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                                        if (currentReadValue != null) {
                                                                            currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount2).getDialogReadMax(null, dialog_id));
                                                                            MessagesController.getInstance(a2).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                                        } else {
                                                                            num = currentReadValue;
                                                                            messageKeyData2 = messageKeyData;
                                                                        }
                                                                        if (badge <= currentReadValue.intValue()) {
                                                                            if (custom.has("chat_from_id")) {
                                                                                messageKey = custom.getInt("chat_from_id");
                                                                            } else {
                                                                                messageKey = null;
                                                                            }
                                                                            if (custom.has("mention")) {
                                                                                if (custom.getInt("mention") != 0) {
                                                                                    messageKeyData = true;
                                                                                    if (json.has("loc_args")) {
                                                                                        loc_args = json.getJSONArray("loc_args");
                                                                                        json = new String[loc_args.length()];
                                                                                        a = 0;
                                                                                        while (true) {
                                                                                            bArr2 = strBytes;
                                                                                            accountFinal = a2;
                                                                                            a2 = a;
                                                                                            if (a2 >= json.length) {
                                                                                                json[a2] = loc_args.getString(a2);
                                                                                                a = a2 + 1;
                                                                                                strBytes = bArr2;
                                                                                                a2 = accountFinal;
                                                                                            } else {
                                                                                                args = json;
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        accountFinal = a2;
                                                                                        jSONObject = json;
                                                                                        bArr2 = strBytes;
                                                                                        args = null;
                                                                                    }
                                                                                    args2 = args;
                                                                                    strBytes = args2[0];
                                                                                    userName = null;
                                                                                    localMessage = false;
                                                                                    supergroup = false;
                                                                                    pinned = false;
                                                                                    channel = false;
                                                                                    messageText = null;
                                                                                    if (!loc_key3.startsWith("CHAT_")) {
                                                                                        if (channel_id == 0) {
                                                                                        }
                                                                                        supergroup = channel_id == 0;
                                                                                        userName2 = strBytes;
                                                                                        strBytes = args2[1];
                                                                                        userName = userName2;
                                                                                    } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                        if (messageKey == null) {
                                                                                        }
                                                                                        supergroup = messageKey == null;
                                                                                        pinned = true;
                                                                                    } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                        channel = true;
                                                                                    }
                                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                                        stringBuilder2 = new StringBuilder();
                                                                                        message = null;
                                                                                        stringBuilder2.append("GCM received message notification ");
                                                                                        stringBuilder2.append(loc_key3);
                                                                                        stringBuilder2.append(" for dialogId = ");
                                                                                        stringBuilder2.append(dialog_id);
                                                                                        stringBuilder2.append(" mid = ");
                                                                                        stringBuilder2.append(badge);
                                                                                        FileLog.m0d(stringBuilder2.toString());
                                                                                    } else {
                                                                                        message = null;
                                                                                    }
                                                                                    switch (loc_key3.hashCode()) {
                                                                                        case -2091498420:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                                obj2 = 28;
                                                                                                break;
                                                                                            }
                                                                                        case -2053872415:
                                                                                            if (loc_key3.equals("CHAT_CREATED")) {
                                                                                                obj2 = 50;
                                                                                                break;
                                                                                            }
                                                                                        case -2039746363:
                                                                                            if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                                obj2 = 9;
                                                                                                break;
                                                                                            }
                                                                                        case -1979538588:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                                obj2 = 25;
                                                                                                break;
                                                                                            }
                                                                                        case -1979536003:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                                obj2 = 29;
                                                                                                break;
                                                                                            }
                                                                                        case -1979535888:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                                obj2 = 31;
                                                                                                break;
                                                                                            }
                                                                                        case -1969004705:
                                                                                            if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                                obj2 = 53;
                                                                                                break;
                                                                                            }
                                                                                        case -1946699248:
                                                                                            if (loc_key3.equals("CHAT_JOINED")) {
                                                                                                obj2 = 59;
                                                                                                break;
                                                                                            }
                                                                                        case -1528047021:
                                                                                            if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                                obj2 = 62;
                                                                                                break;
                                                                                            }
                                                                                        case -1493579426:
                                                                                            if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                                obj2 = 10;
                                                                                                break;
                                                                                            }
                                                                                        case -1480102982:
                                                                                            if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                                obj2 = 2;
                                                                                                break;
                                                                                            }
                                                                                        case -1478041834:
                                                                                            if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                                obj2 = 7;
                                                                                                break;
                                                                                            }
                                                                                        case -1474543101:
                                                                                            if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                                obj2 = 4;
                                                                                                break;
                                                                                            }
                                                                                        case -1465695932:
                                                                                            if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                                obj2 = 81;
                                                                                                break;
                                                                                            }
                                                                                        case -1374906292:
                                                                                            if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                                obj2 = 82;
                                                                                                break;
                                                                                            }
                                                                                        case -1372940586:
                                                                                            if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                                obj2 = 58;
                                                                                                break;
                                                                                            }
                                                                                        case -1264245338:
                                                                                            if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                                obj2 = 75;
                                                                                                break;
                                                                                            }
                                                                                        case -1236086700:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                                obj2 = 33;
                                                                                                break;
                                                                                            }
                                                                                        case -1236077786:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                                obj2 = 32;
                                                                                                break;
                                                                                            }
                                                                                        case -1235686303:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                                obj2 = 20;
                                                                                                break;
                                                                                            }
                                                                                        case -1198046100:
                                                                                            if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                                obj2 = 5;
                                                                                                break;
                                                                                            }
                                                                                        case -1124254527:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                                obj2 = 44;
                                                                                                break;
                                                                                            }
                                                                                        case -1085137927:
                                                                                            if (loc_key3.equals("PINNED_GAME")) {
                                                                                                obj2 = 74;
                                                                                                break;
                                                                                            }
                                                                                        case -1084746444:
                                                                                            if (loc_key3.equals("PINNED_TEXT")) {
                                                                                                obj2 = 63;
                                                                                                break;
                                                                                            }
                                                                                        case -819729482:
                                                                                            if (loc_key3.equals("PINNED_STICKER")) {
                                                                                                obj2 = 69;
                                                                                                break;
                                                                                            }
                                                                                        case -772141857:
                                                                                            if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                                obj2 = 84;
                                                                                                break;
                                                                                            }
                                                                                        case -638310039:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                                obj2 = 26;
                                                                                                break;
                                                                                            }
                                                                                        case -589196239:
                                                                                            if (loc_key3.equals("PINNED_DOC")) {
                                                                                                obj2 = 68;
                                                                                                break;
                                                                                            }
                                                                                        case -589193654:
                                                                                            if (loc_key3.equals("PINNED_GEO")) {
                                                                                                obj2 = 72;
                                                                                                break;
                                                                                            }
                                                                                        case -589193539:
                                                                                            if (loc_key3.equals("PINNED_GIF")) {
                                                                                                obj2 = 76;
                                                                                                break;
                                                                                            }
                                                                                        case -440169325:
                                                                                            if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                                obj2 = 78;
                                                                                                break;
                                                                                            }
                                                                                        case -412748110:
                                                                                            if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                                obj2 = 56;
                                                                                                break;
                                                                                            }
                                                                                        case -228518075:
                                                                                            if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                                obj2 = 13;
                                                                                                break;
                                                                                            }
                                                                                        case -213586509:
                                                                                            if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                                obj2 = 80;
                                                                                                break;
                                                                                            }
                                                                                        case -115582002:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                                obj2 = 49;
                                                                                                break;
                                                                                            }
                                                                                        case -112621464:
                                                                                            if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                                obj2 = 77;
                                                                                                break;
                                                                                            }
                                                                                        case -108522133:
                                                                                            if (loc_key3.equals("AUTH_REGION")) {
                                                                                                obj2 = 79;
                                                                                                break;
                                                                                            }
                                                                                        case -107572034:
                                                                                            if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                                obj2 = 6;
                                                                                                break;
                                                                                            }
                                                                                        case -40534265:
                                                                                            if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                                obj2 = 55;
                                                                                                break;
                                                                                            }
                                                                                        case 65254746:
                                                                                            if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                                obj2 = 54;
                                                                                                break;
                                                                                            }
                                                                                        case 141040782:
                                                                                            if (loc_key3.equals("CHAT_LEFT")) {
                                                                                                obj2 = 57;
                                                                                                break;
                                                                                            }
                                                                                        case 309993049:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                                obj2 = 41;
                                                                                                break;
                                                                                            }
                                                                                        case 309995634:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                                obj2 = 45;
                                                                                                break;
                                                                                            }
                                                                                        case 309995749:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                                obj2 = 47;
                                                                                                break;
                                                                                            }
                                                                                        case 320532812:
                                                                                            if (loc_key3.equals("MESSAGES")) {
                                                                                                obj2 = 19;
                                                                                                break;
                                                                                            }
                                                                                        case 328933854:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                                obj2 = 42;
                                                                                                break;
                                                                                            }
                                                                                        case 331340546:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                                obj2 = 27;
                                                                                                break;
                                                                                            }
                                                                                        case 344816990:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                                obj2 = 22;
                                                                                                break;
                                                                                            }
                                                                                        case 346878138:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                                obj2 = 24;
                                                                                                break;
                                                                                            }
                                                                                        case 350376871:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                                obj2 = 23;
                                                                                                break;
                                                                                            }
                                                                                        case 615714517:
                                                                                            if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                                obj2 = 3;
                                                                                                break;
                                                                                            }
                                                                                        case 715508879:
                                                                                            if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                                obj2 = 70;
                                                                                                break;
                                                                                            }
                                                                                        case 728985323:
                                                                                            if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                                obj2 = 65;
                                                                                                break;
                                                                                            }
                                                                                        case 731046471:
                                                                                            if (loc_key3.equals("PINNED_ROUND")) {
                                                                                                obj2 = 67;
                                                                                                break;
                                                                                            }
                                                                                        case 734545204:
                                                                                            if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                                obj2 = 66;
                                                                                                break;
                                                                                            }
                                                                                        case 802032552:
                                                                                            if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                                obj2 = 11;
                                                                                                break;
                                                                                            }
                                                                                        case 991498806:
                                                                                            if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                                obj2 = 73;
                                                                                                break;
                                                                                            }
                                                                                        case 1019917311:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                                obj2 = 60;
                                                                                                break;
                                                                                            }
                                                                                        case 1019926225:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                                obj2 = 48;
                                                                                                break;
                                                                                            }
                                                                                        case 1020317708:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                                obj2 = 36;
                                                                                                break;
                                                                                            }
                                                                                        case 1060349560:
                                                                                            if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                                obj2 = 17;
                                                                                                break;
                                                                                            }
                                                                                        case 1060358474:
                                                                                            if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                                obj2 = 15;
                                                                                                break;
                                                                                            }
                                                                                        case 1060749957:
                                                                                            if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                                obj2 = null;
                                                                                                break;
                                                                                            }
                                                                                        case 1073049781:
                                                                                            if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                                obj2 = 64;
                                                                                                break;
                                                                                            }
                                                                                        case 1078101399:
                                                                                            if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                                obj2 = 51;
                                                                                                break;
                                                                                            }
                                                                                        case 1110103437:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                                obj2 = 37;
                                                                                                break;
                                                                                            }
                                                                                        case 1160762272:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                                obj2 = 61;
                                                                                                break;
                                                                                            }
                                                                                        case 1172918249:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                                obj2 = 30;
                                                                                                break;
                                                                                            }
                                                                                        case 1281128640:
                                                                                            if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                                obj2 = 8;
                                                                                                break;
                                                                                            }
                                                                                        case 1281131225:
                                                                                            if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                                obj2 = 12;
                                                                                                break;
                                                                                            }
                                                                                        case 1281131340:
                                                                                            if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                                obj2 = 14;
                                                                                                break;
                                                                                            }
                                                                                        case 1310789062:
                                                                                            if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                                obj2 = 1;
                                                                                                break;
                                                                                            }
                                                                                        case 1361447897:
                                                                                            if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                                obj2 = 18;
                                                                                                break;
                                                                                            }
                                                                                        case 1498266155:
                                                                                            if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                                obj2 = 85;
                                                                                                break;
                                                                                            }
                                                                                        case 1547988151:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                                obj2 = 43;
                                                                                                break;
                                                                                            }
                                                                                        case 1561464595:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                                obj2 = 38;
                                                                                                break;
                                                                                            }
                                                                                        case 1563525743:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                                obj2 = 40;
                                                                                                break;
                                                                                            }
                                                                                        case 1567024476:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                                obj2 = 39;
                                                                                                break;
                                                                                            }
                                                                                        case 1810705077:
                                                                                            if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                                obj2 = 16;
                                                                                                break;
                                                                                            }
                                                                                        case 1815177512:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                                obj2 = 35;
                                                                                                break;
                                                                                            }
                                                                                        case 1963241394:
                                                                                            if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                                obj2 = 83;
                                                                                                break;
                                                                                            }
                                                                                        case 2014789757:
                                                                                            if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                                obj2 = 52;
                                                                                                break;
                                                                                            }
                                                                                        case 2022049433:
                                                                                            if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                                obj2 = 71;
                                                                                                break;
                                                                                            }
                                                                                        case 2048733346:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                                obj2 = 21;
                                                                                                break;
                                                                                            }
                                                                                        case 2099392181:
                                                                                            if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                                obj2 = 34;
                                                                                                break;
                                                                                            }
                                                                                        case 2140162142:
                                                                                            if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                                obj2 = 46;
                                                                                                break;
                                                                                            }
                                                                                            obj2 = -1;
                                                                                            break;
                                                                                        default:
                                                                                    }
                                                                                }
                                                                            }
                                                                            messageKeyData = null;
                                                                            if (json.has("loc_args")) {
                                                                                accountFinal = a2;
                                                                                jSONObject = json;
                                                                                bArr2 = strBytes;
                                                                                args = null;
                                                                            } else {
                                                                                loc_args = json.getJSONArray("loc_args");
                                                                                json = new String[loc_args.length()];
                                                                                a = 0;
                                                                                while (true) {
                                                                                    bArr2 = strBytes;
                                                                                    accountFinal = a2;
                                                                                    a2 = a;
                                                                                    if (a2 >= json.length) {
                                                                                        args = json;
                                                                                    } else {
                                                                                        json[a2] = loc_args.getString(a2);
                                                                                        a = a2 + 1;
                                                                                        strBytes = bArr2;
                                                                                        a2 = accountFinal;
                                                                                    }
                                                                                }
                                                                            }
                                                                            args2 = args;
                                                                            strBytes = args2[0];
                                                                            userName = null;
                                                                            localMessage = false;
                                                                            supergroup = false;
                                                                            pinned = false;
                                                                            channel = false;
                                                                            messageText = null;
                                                                            if (!loc_key3.startsWith("CHAT_")) {
                                                                                if (channel_id == 0) {
                                                                                }
                                                                                supergroup = channel_id == 0;
                                                                                userName2 = strBytes;
                                                                                strBytes = args2[1];
                                                                                userName = userName2;
                                                                            } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                if (messageKey == null) {
                                                                                }
                                                                                supergroup = messageKey == null;
                                                                                pinned = true;
                                                                            } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                channel = true;
                                                                            }
                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                                message = null;
                                                                            } else {
                                                                                stringBuilder2 = new StringBuilder();
                                                                                message = null;
                                                                                stringBuilder2.append("GCM received message notification ");
                                                                                stringBuilder2.append(loc_key3);
                                                                                stringBuilder2.append(" for dialogId = ");
                                                                                stringBuilder2.append(dialog_id);
                                                                                stringBuilder2.append(" mid = ");
                                                                                stringBuilder2.append(badge);
                                                                                FileLog.m0d(stringBuilder2.toString());
                                                                            }
                                                                            switch (loc_key3.hashCode()) {
                                                                                case -2091498420:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                        obj2 = 28;
                                                                                        break;
                                                                                    }
                                                                                case -2053872415:
                                                                                    if (loc_key3.equals("CHAT_CREATED")) {
                                                                                        obj2 = 50;
                                                                                        break;
                                                                                    }
                                                                                case -2039746363:
                                                                                    if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                        obj2 = 9;
                                                                                        break;
                                                                                    }
                                                                                case -1979538588:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                        obj2 = 25;
                                                                                        break;
                                                                                    }
                                                                                case -1979536003:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                        obj2 = 29;
                                                                                        break;
                                                                                    }
                                                                                case -1979535888:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                        obj2 = 31;
                                                                                        break;
                                                                                    }
                                                                                case -1969004705:
                                                                                    if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                        obj2 = 53;
                                                                                        break;
                                                                                    }
                                                                                case -1946699248:
                                                                                    if (loc_key3.equals("CHAT_JOINED")) {
                                                                                        obj2 = 59;
                                                                                        break;
                                                                                    }
                                                                                case -1528047021:
                                                                                    if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                        obj2 = 62;
                                                                                        break;
                                                                                    }
                                                                                case -1493579426:
                                                                                    if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                        obj2 = 10;
                                                                                        break;
                                                                                    }
                                                                                case -1480102982:
                                                                                    if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                        obj2 = 2;
                                                                                        break;
                                                                                    }
                                                                                case -1478041834:
                                                                                    if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                        obj2 = 7;
                                                                                        break;
                                                                                    }
                                                                                case -1474543101:
                                                                                    if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                        obj2 = 4;
                                                                                        break;
                                                                                    }
                                                                                case -1465695932:
                                                                                    if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                        obj2 = 81;
                                                                                        break;
                                                                                    }
                                                                                case -1374906292:
                                                                                    if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                        obj2 = 82;
                                                                                        break;
                                                                                    }
                                                                                case -1372940586:
                                                                                    if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                        obj2 = 58;
                                                                                        break;
                                                                                    }
                                                                                case -1264245338:
                                                                                    if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                        obj2 = 75;
                                                                                        break;
                                                                                    }
                                                                                case -1236086700:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                        obj2 = 33;
                                                                                        break;
                                                                                    }
                                                                                case -1236077786:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                        obj2 = 32;
                                                                                        break;
                                                                                    }
                                                                                case -1235686303:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                        obj2 = 20;
                                                                                        break;
                                                                                    }
                                                                                case -1198046100:
                                                                                    if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                        obj2 = 5;
                                                                                        break;
                                                                                    }
                                                                                case -1124254527:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                        obj2 = 44;
                                                                                        break;
                                                                                    }
                                                                                case -1085137927:
                                                                                    if (loc_key3.equals("PINNED_GAME")) {
                                                                                        obj2 = 74;
                                                                                        break;
                                                                                    }
                                                                                case -1084746444:
                                                                                    if (loc_key3.equals("PINNED_TEXT")) {
                                                                                        obj2 = 63;
                                                                                        break;
                                                                                    }
                                                                                case -819729482:
                                                                                    if (loc_key3.equals("PINNED_STICKER")) {
                                                                                        obj2 = 69;
                                                                                        break;
                                                                                    }
                                                                                case -772141857:
                                                                                    if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                        obj2 = 84;
                                                                                        break;
                                                                                    }
                                                                                case -638310039:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                        obj2 = 26;
                                                                                        break;
                                                                                    }
                                                                                case -589196239:
                                                                                    if (loc_key3.equals("PINNED_DOC")) {
                                                                                        obj2 = 68;
                                                                                        break;
                                                                                    }
                                                                                case -589193654:
                                                                                    if (loc_key3.equals("PINNED_GEO")) {
                                                                                        obj2 = 72;
                                                                                        break;
                                                                                    }
                                                                                case -589193539:
                                                                                    if (loc_key3.equals("PINNED_GIF")) {
                                                                                        obj2 = 76;
                                                                                        break;
                                                                                    }
                                                                                case -440169325:
                                                                                    if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                        obj2 = 78;
                                                                                        break;
                                                                                    }
                                                                                case -412748110:
                                                                                    if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                        obj2 = 56;
                                                                                        break;
                                                                                    }
                                                                                case -228518075:
                                                                                    if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                        obj2 = 13;
                                                                                        break;
                                                                                    }
                                                                                case -213586509:
                                                                                    if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                        obj2 = 80;
                                                                                        break;
                                                                                    }
                                                                                case -115582002:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                        obj2 = 49;
                                                                                        break;
                                                                                    }
                                                                                case -112621464:
                                                                                    if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                        obj2 = 77;
                                                                                        break;
                                                                                    }
                                                                                case -108522133:
                                                                                    if (loc_key3.equals("AUTH_REGION")) {
                                                                                        obj2 = 79;
                                                                                        break;
                                                                                    }
                                                                                case -107572034:
                                                                                    if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                        obj2 = 6;
                                                                                        break;
                                                                                    }
                                                                                case -40534265:
                                                                                    if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                        obj2 = 55;
                                                                                        break;
                                                                                    }
                                                                                case 65254746:
                                                                                    if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                        obj2 = 54;
                                                                                        break;
                                                                                    }
                                                                                case 141040782:
                                                                                    if (loc_key3.equals("CHAT_LEFT")) {
                                                                                        obj2 = 57;
                                                                                        break;
                                                                                    }
                                                                                case 309993049:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                        obj2 = 41;
                                                                                        break;
                                                                                    }
                                                                                case 309995634:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                        obj2 = 45;
                                                                                        break;
                                                                                    }
                                                                                case 309995749:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                        obj2 = 47;
                                                                                        break;
                                                                                    }
                                                                                case 320532812:
                                                                                    if (loc_key3.equals("MESSAGES")) {
                                                                                        obj2 = 19;
                                                                                        break;
                                                                                    }
                                                                                case 328933854:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                        obj2 = 42;
                                                                                        break;
                                                                                    }
                                                                                case 331340546:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                        obj2 = 27;
                                                                                        break;
                                                                                    }
                                                                                case 344816990:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                        obj2 = 22;
                                                                                        break;
                                                                                    }
                                                                                case 346878138:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                        obj2 = 24;
                                                                                        break;
                                                                                    }
                                                                                case 350376871:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                        obj2 = 23;
                                                                                        break;
                                                                                    }
                                                                                case 615714517:
                                                                                    if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                        obj2 = 3;
                                                                                        break;
                                                                                    }
                                                                                case 715508879:
                                                                                    if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                        obj2 = 70;
                                                                                        break;
                                                                                    }
                                                                                case 728985323:
                                                                                    if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                        obj2 = 65;
                                                                                        break;
                                                                                    }
                                                                                case 731046471:
                                                                                    if (loc_key3.equals("PINNED_ROUND")) {
                                                                                        obj2 = 67;
                                                                                        break;
                                                                                    }
                                                                                case 734545204:
                                                                                    if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                        obj2 = 66;
                                                                                        break;
                                                                                    }
                                                                                case 802032552:
                                                                                    if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                        obj2 = 11;
                                                                                        break;
                                                                                    }
                                                                                case 991498806:
                                                                                    if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                        obj2 = 73;
                                                                                        break;
                                                                                    }
                                                                                case 1019917311:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                        obj2 = 60;
                                                                                        break;
                                                                                    }
                                                                                case 1019926225:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                        obj2 = 48;
                                                                                        break;
                                                                                    }
                                                                                case 1020317708:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                        obj2 = 36;
                                                                                        break;
                                                                                    }
                                                                                case 1060349560:
                                                                                    if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                        obj2 = 17;
                                                                                        break;
                                                                                    }
                                                                                case 1060358474:
                                                                                    if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                        obj2 = 15;
                                                                                        break;
                                                                                    }
                                                                                case 1060749957:
                                                                                    if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                        obj2 = null;
                                                                                        break;
                                                                                    }
                                                                                case 1073049781:
                                                                                    if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                        obj2 = 64;
                                                                                        break;
                                                                                    }
                                                                                case 1078101399:
                                                                                    if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                        obj2 = 51;
                                                                                        break;
                                                                                    }
                                                                                case 1110103437:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                        obj2 = 37;
                                                                                        break;
                                                                                    }
                                                                                case 1160762272:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                        obj2 = 61;
                                                                                        break;
                                                                                    }
                                                                                case 1172918249:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                        obj2 = 30;
                                                                                        break;
                                                                                    }
                                                                                case 1281128640:
                                                                                    if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                        obj2 = 8;
                                                                                        break;
                                                                                    }
                                                                                case 1281131225:
                                                                                    if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                        obj2 = 12;
                                                                                        break;
                                                                                    }
                                                                                case 1281131340:
                                                                                    if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                        obj2 = 14;
                                                                                        break;
                                                                                    }
                                                                                case 1310789062:
                                                                                    if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                        obj2 = 1;
                                                                                        break;
                                                                                    }
                                                                                case 1361447897:
                                                                                    if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                        obj2 = 18;
                                                                                        break;
                                                                                    }
                                                                                case 1498266155:
                                                                                    if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                        obj2 = 85;
                                                                                        break;
                                                                                    }
                                                                                case 1547988151:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                        obj2 = 43;
                                                                                        break;
                                                                                    }
                                                                                case 1561464595:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                        obj2 = 38;
                                                                                        break;
                                                                                    }
                                                                                case 1563525743:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                        obj2 = 40;
                                                                                        break;
                                                                                    }
                                                                                case 1567024476:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                        obj2 = 39;
                                                                                        break;
                                                                                    }
                                                                                case 1810705077:
                                                                                    if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                        obj2 = 16;
                                                                                        break;
                                                                                    }
                                                                                case 1815177512:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                        obj2 = 35;
                                                                                        break;
                                                                                    }
                                                                                case 1963241394:
                                                                                    if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                        obj2 = 83;
                                                                                        break;
                                                                                    }
                                                                                case 2014789757:
                                                                                    if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                        obj2 = 52;
                                                                                        break;
                                                                                    }
                                                                                case 2022049433:
                                                                                    if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                        obj2 = 71;
                                                                                        break;
                                                                                    }
                                                                                case 2048733346:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                        obj2 = 21;
                                                                                        break;
                                                                                    }
                                                                                case 2099392181:
                                                                                    if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                        obj2 = 34;
                                                                                        break;
                                                                                    }
                                                                                case 2140162142:
                                                                                    if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                        obj2 = 46;
                                                                                        break;
                                                                                    }
                                                                                    obj2 = -1;
                                                                                    break;
                                                                                default:
                                                                            }
                                                                        } else {
                                                                            return;
                                                                        }
                                                                    }
                                                                    accountFinal = a2;
                                                                    dialog_id3 = dialog_id;
                                                                    jSONObject = json;
                                                                    i = badge;
                                                                    i2 = len;
                                                                    bArr3 = messageKey;
                                                                    messageKeyData2 = messageKeyData;
                                                                    bArr2 = strBytes;
                                                                    currentAccount = chat_id3;
                                                                    a2 = custom.getInt("max_id");
                                                                    updates2 = new ArrayList();
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        stringBuilder3 = new StringBuilder();
                                                                        stringBuilder3.append("GCM received read notification max_id = ");
                                                                        stringBuilder3.append(a2);
                                                                        stringBuilder3.append(" for dialogId = ");
                                                                        stringBuilder3.append(dialog_id3);
                                                                        FileLog.m0d(stringBuilder3.toString());
                                                                    }
                                                                    if (channel_id == 0) {
                                                                        update2 = new TL_updateReadChannelInbox();
                                                                        update2.channel_id = channel_id;
                                                                        update2.max_id = a2;
                                                                        updates2.add(update2);
                                                                    } else {
                                                                        update3 = new TL_updateReadHistoryInbox();
                                                                        if (user_id == 0) {
                                                                            update3.peer = new TL_peerUser();
                                                                            update3.peer.user_id = user_id;
                                                                        } else {
                                                                            update3.peer = new TL_peerChat();
                                                                            update3.peer.chat_id = currentAccount;
                                                                        }
                                                                        update3.max_id = a2;
                                                                        updates2.add(update3);
                                                                    }
                                                                    MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                                                    ConnectionsManager.onInternalPushReceived(currentAccount2);
                                                                    ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                    break;
                                                                }
                                                                return;
                                                        }
                                                    }
                                                    return;
                                                }
                                                currentAccount = Utilities.parseInt((String) userIdObject).intValue();
                                            } else {
                                                currentAccount = ((Integer) userIdObject).intValue();
                                            }
                                        } else {
                                            currentAccount = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                        }
                                        account = UserConfig.selectedAccount;
                                        a = 0;
                                        while (true) {
                                            userIdObject2 = userIdObject;
                                            loc_key2 = loc_key;
                                            a2 = a;
                                            if (a2 >= 3) {
                                                if (UserConfig.getInstance(a2).getClientUserId() == currentAccount) {
                                                    break;
                                                }
                                                a = a2 + 1;
                                                userIdObject = userIdObject2;
                                                loc_key = loc_key2;
                                            } else {
                                                break;
                                            }
                                        }
                                        account = a2;
                                        currentAccount2 = account;
                                        a2 = account;
                                    } catch (Throwable th2222) {
                                        loc_key2 = loc_key;
                                        e = th2222;
                                        currentAccount = currentAccount3;
                                        if (currentAccount == -1) {
                                            ConnectionsManager.onInternalPushReceived(currentAccount);
                                            ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                        } else {
                                            GcmPushListenerService.this.onDecryptError();
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("error in loc_key = ");
                                            stringBuilder.append(loc_key);
                                            FileLog.m1e(stringBuilder.toString());
                                        }
                                        FileLog.m3e(e);
                                        str = loc_key;
                                    }
                                    try {
                                        if (!UserConfig.getInstance(currentAccount2).isClientActivated()) {
                                            if (json.has("loc_key")) {
                                                loc_key3 = TtmlNode.ANONYMOUS_REGION_ID;
                                            } else {
                                                loc_key3 = json.getString("loc_key");
                                            }
                                            obj = data.get("google.sent_time");
                                            hashCode = loc_key3.hashCode();
                                            if (hashCode == -920689527) {
                                                if (loc_key3.equals("DC_UPDATE")) {
                                                    obj = null;
                                                    switch (obj) {
                                                        case null:
                                                            dialog_id = custom.getInt("dc");
                                                            addr = custom.getString("addr");
                                                            parts = addr.split(":");
                                                            nativeByteBuffer = buffer;
                                                            if (parts.length != 2) {
                                                                ConnectionsManager.getInstance(currentAccount2).applyDatacenterAddress(dialog_id, parts[null], Integer.parseInt(parts[1]));
                                                                ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                return;
                                                            }
                                                            return;
                                                        case 1:
                                                            update = new TL_updateServiceNotification();
                                                            update.popup = false;
                                                            update.flags = 2;
                                                            bArr = bytes;
                                                            update.inbox_date = (int) (time / 1000);
                                                            update.message = json.getString("message");
                                                            update.type = "announcement";
                                                            update.media = new TL_messageMediaEmpty();
                                                            updates = new TL_updates();
                                                            updates.updates.add(update);
                                                            Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                            ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                            return;
                                                        default:
                                                            dialog_id = 0;
                                                            if (custom.has("channel_id")) {
                                                                channel_id = 0;
                                                            } else {
                                                                channel_id = custom.getInt("channel_id");
                                                                dialog_id = (long) (-channel_id);
                                                            }
                                                            if (custom.has("from_id")) {
                                                                user_id = 0;
                                                            } else {
                                                                user_id = custom.getInt("from_id");
                                                                dialog_id = (long) user_id;
                                                            }
                                                            dialog_id2 = dialog_id;
                                                            if (custom.has("chat_id")) {
                                                                dialog_id = dialog_id2;
                                                                chat_id = 0;
                                                            } else {
                                                                chat_id2 = custom.getInt("chat_id");
                                                                chat_id = chat_id2;
                                                                dialog_id = (long) (-chat_id2);
                                                            }
                                                            chat_id3 = chat_id;
                                                            if (dialog_id == 0) {
                                                                if (json.has("badge")) {
                                                                    badge = 0;
                                                                } else {
                                                                    badge = json.getInt("badge");
                                                                }
                                                                if (badge == 0) {
                                                                    accountFinal = a2;
                                                                    dialog_id3 = dialog_id;
                                                                    jSONObject = json;
                                                                    i = badge;
                                                                    i2 = len;
                                                                    bArr3 = messageKey;
                                                                    messageKeyData2 = messageKeyData;
                                                                    bArr2 = strBytes;
                                                                    currentAccount = chat_id3;
                                                                    a2 = custom.getInt("max_id");
                                                                    updates2 = new ArrayList();
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                    } else {
                                                                        stringBuilder3 = new StringBuilder();
                                                                        stringBuilder3.append("GCM received read notification max_id = ");
                                                                        stringBuilder3.append(a2);
                                                                        stringBuilder3.append(" for dialogId = ");
                                                                        stringBuilder3.append(dialog_id3);
                                                                        FileLog.m0d(stringBuilder3.toString());
                                                                    }
                                                                    if (channel_id == 0) {
                                                                        update3 = new TL_updateReadHistoryInbox();
                                                                        if (user_id == 0) {
                                                                            update3.peer = new TL_peerChat();
                                                                            update3.peer.chat_id = currentAccount;
                                                                        } else {
                                                                            update3.peer = new TL_peerUser();
                                                                            update3.peer.user_id = user_id;
                                                                        }
                                                                        update3.max_id = a2;
                                                                        updates2.add(update3);
                                                                    } else {
                                                                        update2 = new TL_updateReadChannelInbox();
                                                                        update2.channel_id = channel_id;
                                                                        update2.max_id = a2;
                                                                        updates2.add(update2);
                                                                    }
                                                                    MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                                                } else {
                                                                    badge = custom.getInt("msg_id");
                                                                    currentReadValue = (Integer) MessagesController.getInstance(currentAccount2).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                                    if (currentReadValue != null) {
                                                                        num = currentReadValue;
                                                                        messageKeyData2 = messageKeyData;
                                                                    } else {
                                                                        currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount2).getDialogReadMax(null, dialog_id));
                                                                        MessagesController.getInstance(a2).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                                    }
                                                                    if (badge <= currentReadValue.intValue()) {
                                                                        if (custom.has("chat_from_id")) {
                                                                            messageKey = null;
                                                                        } else {
                                                                            messageKey = custom.getInt("chat_from_id");
                                                                        }
                                                                        if (custom.has("mention")) {
                                                                            if (custom.getInt("mention") != 0) {
                                                                                messageKeyData = true;
                                                                                if (json.has("loc_args")) {
                                                                                    loc_args = json.getJSONArray("loc_args");
                                                                                    json = new String[loc_args.length()];
                                                                                    a = 0;
                                                                                    while (true) {
                                                                                        bArr2 = strBytes;
                                                                                        accountFinal = a2;
                                                                                        a2 = a;
                                                                                        if (a2 >= json.length) {
                                                                                            json[a2] = loc_args.getString(a2);
                                                                                            a = a2 + 1;
                                                                                            strBytes = bArr2;
                                                                                            a2 = accountFinal;
                                                                                        } else {
                                                                                            args = json;
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    accountFinal = a2;
                                                                                    jSONObject = json;
                                                                                    bArr2 = strBytes;
                                                                                    args = null;
                                                                                }
                                                                                args2 = args;
                                                                                strBytes = args2[0];
                                                                                userName = null;
                                                                                localMessage = false;
                                                                                supergroup = false;
                                                                                pinned = false;
                                                                                channel = false;
                                                                                messageText = null;
                                                                                if (!loc_key3.startsWith("CHAT_")) {
                                                                                    if (channel_id == 0) {
                                                                                    }
                                                                                    supergroup = channel_id == 0;
                                                                                    userName2 = strBytes;
                                                                                    strBytes = args2[1];
                                                                                    userName = userName2;
                                                                                } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                    if (messageKey == null) {
                                                                                    }
                                                                                    supergroup = messageKey == null;
                                                                                    pinned = true;
                                                                                } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                    channel = true;
                                                                                }
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    stringBuilder2 = new StringBuilder();
                                                                                    message = null;
                                                                                    stringBuilder2.append("GCM received message notification ");
                                                                                    stringBuilder2.append(loc_key3);
                                                                                    stringBuilder2.append(" for dialogId = ");
                                                                                    stringBuilder2.append(dialog_id);
                                                                                    stringBuilder2.append(" mid = ");
                                                                                    stringBuilder2.append(badge);
                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                } else {
                                                                                    message = null;
                                                                                }
                                                                                switch (loc_key3.hashCode()) {
                                                                                    case -2091498420:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                            obj2 = 28;
                                                                                            break;
                                                                                        }
                                                                                    case -2053872415:
                                                                                        if (loc_key3.equals("CHAT_CREATED")) {
                                                                                            obj2 = 50;
                                                                                            break;
                                                                                        }
                                                                                    case -2039746363:
                                                                                        if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                            obj2 = 9;
                                                                                            break;
                                                                                        }
                                                                                    case -1979538588:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                            obj2 = 25;
                                                                                            break;
                                                                                        }
                                                                                    case -1979536003:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                            obj2 = 29;
                                                                                            break;
                                                                                        }
                                                                                    case -1979535888:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                            obj2 = 31;
                                                                                            break;
                                                                                        }
                                                                                    case -1969004705:
                                                                                        if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                            obj2 = 53;
                                                                                            break;
                                                                                        }
                                                                                    case -1946699248:
                                                                                        if (loc_key3.equals("CHAT_JOINED")) {
                                                                                            obj2 = 59;
                                                                                            break;
                                                                                        }
                                                                                    case -1528047021:
                                                                                        if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                            obj2 = 62;
                                                                                            break;
                                                                                        }
                                                                                    case -1493579426:
                                                                                        if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                            obj2 = 10;
                                                                                            break;
                                                                                        }
                                                                                    case -1480102982:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                            obj2 = 2;
                                                                                            break;
                                                                                        }
                                                                                    case -1478041834:
                                                                                        if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                            obj2 = 7;
                                                                                            break;
                                                                                        }
                                                                                    case -1474543101:
                                                                                        if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                            obj2 = 4;
                                                                                            break;
                                                                                        }
                                                                                    case -1465695932:
                                                                                        if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                            obj2 = 81;
                                                                                            break;
                                                                                        }
                                                                                    case -1374906292:
                                                                                        if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                            obj2 = 82;
                                                                                            break;
                                                                                        }
                                                                                    case -1372940586:
                                                                                        if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                            obj2 = 58;
                                                                                            break;
                                                                                        }
                                                                                    case -1264245338:
                                                                                        if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                            obj2 = 75;
                                                                                            break;
                                                                                        }
                                                                                    case -1236086700:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                            obj2 = 33;
                                                                                            break;
                                                                                        }
                                                                                    case -1236077786:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                            obj2 = 32;
                                                                                            break;
                                                                                        }
                                                                                    case -1235686303:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                            obj2 = 20;
                                                                                            break;
                                                                                        }
                                                                                    case -1198046100:
                                                                                        if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                            obj2 = 5;
                                                                                            break;
                                                                                        }
                                                                                    case -1124254527:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                            obj2 = 44;
                                                                                            break;
                                                                                        }
                                                                                    case -1085137927:
                                                                                        if (loc_key3.equals("PINNED_GAME")) {
                                                                                            obj2 = 74;
                                                                                            break;
                                                                                        }
                                                                                    case -1084746444:
                                                                                        if (loc_key3.equals("PINNED_TEXT")) {
                                                                                            obj2 = 63;
                                                                                            break;
                                                                                        }
                                                                                    case -819729482:
                                                                                        if (loc_key3.equals("PINNED_STICKER")) {
                                                                                            obj2 = 69;
                                                                                            break;
                                                                                        }
                                                                                    case -772141857:
                                                                                        if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                            obj2 = 84;
                                                                                            break;
                                                                                        }
                                                                                    case -638310039:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                            obj2 = 26;
                                                                                            break;
                                                                                        }
                                                                                    case -589196239:
                                                                                        if (loc_key3.equals("PINNED_DOC")) {
                                                                                            obj2 = 68;
                                                                                            break;
                                                                                        }
                                                                                    case -589193654:
                                                                                        if (loc_key3.equals("PINNED_GEO")) {
                                                                                            obj2 = 72;
                                                                                            break;
                                                                                        }
                                                                                    case -589193539:
                                                                                        if (loc_key3.equals("PINNED_GIF")) {
                                                                                            obj2 = 76;
                                                                                            break;
                                                                                        }
                                                                                    case -440169325:
                                                                                        if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                            obj2 = 78;
                                                                                            break;
                                                                                        }
                                                                                    case -412748110:
                                                                                        if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                            obj2 = 56;
                                                                                            break;
                                                                                        }
                                                                                    case -228518075:
                                                                                        if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 13;
                                                                                            break;
                                                                                        }
                                                                                    case -213586509:
                                                                                        if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                            obj2 = 80;
                                                                                            break;
                                                                                        }
                                                                                    case -115582002:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                            obj2 = 49;
                                                                                            break;
                                                                                        }
                                                                                    case -112621464:
                                                                                        if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                            obj2 = 77;
                                                                                            break;
                                                                                        }
                                                                                    case -108522133:
                                                                                        if (loc_key3.equals("AUTH_REGION")) {
                                                                                            obj2 = 79;
                                                                                            break;
                                                                                        }
                                                                                    case -107572034:
                                                                                        if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                            obj2 = 6;
                                                                                            break;
                                                                                        }
                                                                                    case -40534265:
                                                                                        if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                            obj2 = 55;
                                                                                            break;
                                                                                        }
                                                                                    case 65254746:
                                                                                        if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                            obj2 = 54;
                                                                                            break;
                                                                                        }
                                                                                    case 141040782:
                                                                                        if (loc_key3.equals("CHAT_LEFT")) {
                                                                                            obj2 = 57;
                                                                                            break;
                                                                                        }
                                                                                    case 309993049:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                            obj2 = 41;
                                                                                            break;
                                                                                        }
                                                                                    case 309995634:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                            obj2 = 45;
                                                                                            break;
                                                                                        }
                                                                                    case 309995749:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                            obj2 = 47;
                                                                                            break;
                                                                                        }
                                                                                    case 320532812:
                                                                                        if (loc_key3.equals("MESSAGES")) {
                                                                                            obj2 = 19;
                                                                                            break;
                                                                                        }
                                                                                    case 328933854:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                            obj2 = 42;
                                                                                            break;
                                                                                        }
                                                                                    case 331340546:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                            obj2 = 27;
                                                                                            break;
                                                                                        }
                                                                                    case 344816990:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                            obj2 = 22;
                                                                                            break;
                                                                                        }
                                                                                    case 346878138:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                            obj2 = 24;
                                                                                            break;
                                                                                        }
                                                                                    case 350376871:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                            obj2 = 23;
                                                                                            break;
                                                                                        }
                                                                                    case 615714517:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                            obj2 = 3;
                                                                                            break;
                                                                                        }
                                                                                    case 715508879:
                                                                                        if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                            obj2 = 70;
                                                                                            break;
                                                                                        }
                                                                                    case 728985323:
                                                                                        if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                            obj2 = 65;
                                                                                            break;
                                                                                        }
                                                                                    case 731046471:
                                                                                        if (loc_key3.equals("PINNED_ROUND")) {
                                                                                            obj2 = 67;
                                                                                            break;
                                                                                        }
                                                                                    case 734545204:
                                                                                        if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                            obj2 = 66;
                                                                                            break;
                                                                                        }
                                                                                    case 802032552:
                                                                                        if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                            obj2 = 11;
                                                                                            break;
                                                                                        }
                                                                                    case 991498806:
                                                                                        if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                            obj2 = 73;
                                                                                            break;
                                                                                        }
                                                                                    case 1019917311:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                            obj2 = 60;
                                                                                            break;
                                                                                        }
                                                                                    case 1019926225:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                            obj2 = 48;
                                                                                            break;
                                                                                        }
                                                                                    case 1020317708:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                            obj2 = 36;
                                                                                            break;
                                                                                        }
                                                                                    case 1060349560:
                                                                                        if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                            obj2 = 17;
                                                                                            break;
                                                                                        }
                                                                                    case 1060358474:
                                                                                        if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                            obj2 = 15;
                                                                                            break;
                                                                                        }
                                                                                    case 1060749957:
                                                                                        if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                            obj2 = null;
                                                                                            break;
                                                                                        }
                                                                                    case 1073049781:
                                                                                        if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                            obj2 = 64;
                                                                                            break;
                                                                                        }
                                                                                    case 1078101399:
                                                                                        if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                            obj2 = 51;
                                                                                            break;
                                                                                        }
                                                                                    case 1110103437:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                            obj2 = 37;
                                                                                            break;
                                                                                        }
                                                                                    case 1160762272:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                            obj2 = 61;
                                                                                            break;
                                                                                        }
                                                                                    case 1172918249:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 30;
                                                                                            break;
                                                                                        }
                                                                                    case 1281128640:
                                                                                        if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                            obj2 = 8;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131225:
                                                                                        if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                            obj2 = 12;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131340:
                                                                                        if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                            obj2 = 14;
                                                                                            break;
                                                                                        }
                                                                                    case 1310789062:
                                                                                        if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                            obj2 = 1;
                                                                                            break;
                                                                                        }
                                                                                    case 1361447897:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                            obj2 = 18;
                                                                                            break;
                                                                                        }
                                                                                    case 1498266155:
                                                                                        if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                            obj2 = 85;
                                                                                            break;
                                                                                        }
                                                                                    case 1547988151:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                            obj2 = 43;
                                                                                            break;
                                                                                        }
                                                                                    case 1561464595:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                            obj2 = 38;
                                                                                            break;
                                                                                        }
                                                                                    case 1563525743:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                            obj2 = 40;
                                                                                            break;
                                                                                        }
                                                                                    case 1567024476:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                            obj2 = 39;
                                                                                            break;
                                                                                        }
                                                                                    case 1810705077:
                                                                                        if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                            obj2 = 16;
                                                                                            break;
                                                                                        }
                                                                                    case 1815177512:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                            obj2 = 35;
                                                                                            break;
                                                                                        }
                                                                                    case 1963241394:
                                                                                        if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                            obj2 = 83;
                                                                                            break;
                                                                                        }
                                                                                    case 2014789757:
                                                                                        if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                            obj2 = 52;
                                                                                            break;
                                                                                        }
                                                                                    case 2022049433:
                                                                                        if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                            obj2 = 71;
                                                                                            break;
                                                                                        }
                                                                                    case 2048733346:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                            obj2 = 21;
                                                                                            break;
                                                                                        }
                                                                                    case 2099392181:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                            obj2 = 34;
                                                                                            break;
                                                                                        }
                                                                                    case 2140162142:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 46;
                                                                                            break;
                                                                                        }
                                                                                        obj2 = -1;
                                                                                        break;
                                                                                    default:
                                                                                }
                                                                            }
                                                                        }
                                                                        messageKeyData = null;
                                                                        if (json.has("loc_args")) {
                                                                            accountFinal = a2;
                                                                            jSONObject = json;
                                                                            bArr2 = strBytes;
                                                                            args = null;
                                                                        } else {
                                                                            loc_args = json.getJSONArray("loc_args");
                                                                            json = new String[loc_args.length()];
                                                                            a = 0;
                                                                            while (true) {
                                                                                bArr2 = strBytes;
                                                                                accountFinal = a2;
                                                                                a2 = a;
                                                                                if (a2 >= json.length) {
                                                                                    args = json;
                                                                                } else {
                                                                                    json[a2] = loc_args.getString(a2);
                                                                                    a = a2 + 1;
                                                                                    strBytes = bArr2;
                                                                                    a2 = accountFinal;
                                                                                }
                                                                            }
                                                                        }
                                                                        args2 = args;
                                                                        strBytes = args2[0];
                                                                        userName = null;
                                                                        localMessage = false;
                                                                        supergroup = false;
                                                                        pinned = false;
                                                                        channel = false;
                                                                        messageText = null;
                                                                        if (!loc_key3.startsWith("CHAT_")) {
                                                                            if (channel_id == 0) {
                                                                            }
                                                                            supergroup = channel_id == 0;
                                                                            userName2 = strBytes;
                                                                            strBytes = args2[1];
                                                                            userName = userName2;
                                                                        } else if (!loc_key3.startsWith("PINNED_")) {
                                                                            if (messageKey == null) {
                                                                            }
                                                                            supergroup = messageKey == null;
                                                                            pinned = true;
                                                                        } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                            channel = true;
                                                                        }
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            message = null;
                                                                        } else {
                                                                            stringBuilder2 = new StringBuilder();
                                                                            message = null;
                                                                            stringBuilder2.append("GCM received message notification ");
                                                                            stringBuilder2.append(loc_key3);
                                                                            stringBuilder2.append(" for dialogId = ");
                                                                            stringBuilder2.append(dialog_id);
                                                                            stringBuilder2.append(" mid = ");
                                                                            stringBuilder2.append(badge);
                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                        }
                                                                        switch (loc_key3.hashCode()) {
                                                                            case -2091498420:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                    obj2 = 28;
                                                                                    break;
                                                                                }
                                                                            case -2053872415:
                                                                                if (loc_key3.equals("CHAT_CREATED")) {
                                                                                    obj2 = 50;
                                                                                    break;
                                                                                }
                                                                            case -2039746363:
                                                                                if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                    obj2 = 9;
                                                                                    break;
                                                                                }
                                                                            case -1979538588:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                    obj2 = 25;
                                                                                    break;
                                                                                }
                                                                            case -1979536003:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                    obj2 = 29;
                                                                                    break;
                                                                                }
                                                                            case -1979535888:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                    obj2 = 31;
                                                                                    break;
                                                                                }
                                                                            case -1969004705:
                                                                                if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                    obj2 = 53;
                                                                                    break;
                                                                                }
                                                                            case -1946699248:
                                                                                if (loc_key3.equals("CHAT_JOINED")) {
                                                                                    obj2 = 59;
                                                                                    break;
                                                                                }
                                                                            case -1528047021:
                                                                                if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                    obj2 = 62;
                                                                                    break;
                                                                                }
                                                                            case -1493579426:
                                                                                if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                    obj2 = 10;
                                                                                    break;
                                                                                }
                                                                            case -1480102982:
                                                                                if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                    obj2 = 2;
                                                                                    break;
                                                                                }
                                                                            case -1478041834:
                                                                                if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                    obj2 = 7;
                                                                                    break;
                                                                                }
                                                                            case -1474543101:
                                                                                if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                    obj2 = 4;
                                                                                    break;
                                                                                }
                                                                            case -1465695932:
                                                                                if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                    obj2 = 81;
                                                                                    break;
                                                                                }
                                                                            case -1374906292:
                                                                                if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                    obj2 = 82;
                                                                                    break;
                                                                                }
                                                                            case -1372940586:
                                                                                if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                    obj2 = 58;
                                                                                    break;
                                                                                }
                                                                            case -1264245338:
                                                                                if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                    obj2 = 75;
                                                                                    break;
                                                                                }
                                                                            case -1236086700:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                    obj2 = 33;
                                                                                    break;
                                                                                }
                                                                            case -1236077786:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                    obj2 = 32;
                                                                                    break;
                                                                                }
                                                                            case -1235686303:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                    obj2 = 20;
                                                                                    break;
                                                                                }
                                                                            case -1198046100:
                                                                                if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                    obj2 = 5;
                                                                                    break;
                                                                                }
                                                                            case -1124254527:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                    obj2 = 44;
                                                                                    break;
                                                                                }
                                                                            case -1085137927:
                                                                                if (loc_key3.equals("PINNED_GAME")) {
                                                                                    obj2 = 74;
                                                                                    break;
                                                                                }
                                                                            case -1084746444:
                                                                                if (loc_key3.equals("PINNED_TEXT")) {
                                                                                    obj2 = 63;
                                                                                    break;
                                                                                }
                                                                            case -819729482:
                                                                                if (loc_key3.equals("PINNED_STICKER")) {
                                                                                    obj2 = 69;
                                                                                    break;
                                                                                }
                                                                            case -772141857:
                                                                                if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                    obj2 = 84;
                                                                                    break;
                                                                                }
                                                                            case -638310039:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                    obj2 = 26;
                                                                                    break;
                                                                                }
                                                                            case -589196239:
                                                                                if (loc_key3.equals("PINNED_DOC")) {
                                                                                    obj2 = 68;
                                                                                    break;
                                                                                }
                                                                            case -589193654:
                                                                                if (loc_key3.equals("PINNED_GEO")) {
                                                                                    obj2 = 72;
                                                                                    break;
                                                                                }
                                                                            case -589193539:
                                                                                if (loc_key3.equals("PINNED_GIF")) {
                                                                                    obj2 = 76;
                                                                                    break;
                                                                                }
                                                                            case -440169325:
                                                                                if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                    obj2 = 78;
                                                                                    break;
                                                                                }
                                                                            case -412748110:
                                                                                if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                    obj2 = 56;
                                                                                    break;
                                                                                }
                                                                            case -228518075:
                                                                                if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 13;
                                                                                    break;
                                                                                }
                                                                            case -213586509:
                                                                                if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                    obj2 = 80;
                                                                                    break;
                                                                                }
                                                                            case -115582002:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                    obj2 = 49;
                                                                                    break;
                                                                                }
                                                                            case -112621464:
                                                                                if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                    obj2 = 77;
                                                                                    break;
                                                                                }
                                                                            case -108522133:
                                                                                if (loc_key3.equals("AUTH_REGION")) {
                                                                                    obj2 = 79;
                                                                                    break;
                                                                                }
                                                                            case -107572034:
                                                                                if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                    obj2 = 6;
                                                                                    break;
                                                                                }
                                                                            case -40534265:
                                                                                if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                    obj2 = 55;
                                                                                    break;
                                                                                }
                                                                            case 65254746:
                                                                                if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                    obj2 = 54;
                                                                                    break;
                                                                                }
                                                                            case 141040782:
                                                                                if (loc_key3.equals("CHAT_LEFT")) {
                                                                                    obj2 = 57;
                                                                                    break;
                                                                                }
                                                                            case 309993049:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                    obj2 = 41;
                                                                                    break;
                                                                                }
                                                                            case 309995634:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                    obj2 = 45;
                                                                                    break;
                                                                                }
                                                                            case 309995749:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                    obj2 = 47;
                                                                                    break;
                                                                                }
                                                                            case 320532812:
                                                                                if (loc_key3.equals("MESSAGES")) {
                                                                                    obj2 = 19;
                                                                                    break;
                                                                                }
                                                                            case 328933854:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                    obj2 = 42;
                                                                                    break;
                                                                                }
                                                                            case 331340546:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                    obj2 = 27;
                                                                                    break;
                                                                                }
                                                                            case 344816990:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                    obj2 = 22;
                                                                                    break;
                                                                                }
                                                                            case 346878138:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                    obj2 = 24;
                                                                                    break;
                                                                                }
                                                                            case 350376871:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                    obj2 = 23;
                                                                                    break;
                                                                                }
                                                                            case 615714517:
                                                                                if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                    obj2 = 3;
                                                                                    break;
                                                                                }
                                                                            case 715508879:
                                                                                if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                    obj2 = 70;
                                                                                    break;
                                                                                }
                                                                            case 728985323:
                                                                                if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                    obj2 = 65;
                                                                                    break;
                                                                                }
                                                                            case 731046471:
                                                                                if (loc_key3.equals("PINNED_ROUND")) {
                                                                                    obj2 = 67;
                                                                                    break;
                                                                                }
                                                                            case 734545204:
                                                                                if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                    obj2 = 66;
                                                                                    break;
                                                                                }
                                                                            case 802032552:
                                                                                if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                    obj2 = 11;
                                                                                    break;
                                                                                }
                                                                            case 991498806:
                                                                                if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                    obj2 = 73;
                                                                                    break;
                                                                                }
                                                                            case 1019917311:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                    obj2 = 60;
                                                                                    break;
                                                                                }
                                                                            case 1019926225:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                    obj2 = 48;
                                                                                    break;
                                                                                }
                                                                            case 1020317708:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                    obj2 = 36;
                                                                                    break;
                                                                                }
                                                                            case 1060349560:
                                                                                if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                    obj2 = 17;
                                                                                    break;
                                                                                }
                                                                            case 1060358474:
                                                                                if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                    obj2 = 15;
                                                                                    break;
                                                                                }
                                                                            case 1060749957:
                                                                                if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                    obj2 = null;
                                                                                    break;
                                                                                }
                                                                            case 1073049781:
                                                                                if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                    obj2 = 64;
                                                                                    break;
                                                                                }
                                                                            case 1078101399:
                                                                                if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                    obj2 = 51;
                                                                                    break;
                                                                                }
                                                                            case 1110103437:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                    obj2 = 37;
                                                                                    break;
                                                                                }
                                                                            case 1160762272:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                    obj2 = 61;
                                                                                    break;
                                                                                }
                                                                            case 1172918249:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 30;
                                                                                    break;
                                                                                }
                                                                            case 1281128640:
                                                                                if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                    obj2 = 8;
                                                                                    break;
                                                                                }
                                                                            case 1281131225:
                                                                                if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                    obj2 = 12;
                                                                                    break;
                                                                                }
                                                                            case 1281131340:
                                                                                if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                    obj2 = 14;
                                                                                    break;
                                                                                }
                                                                            case 1310789062:
                                                                                if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                    obj2 = 1;
                                                                                    break;
                                                                                }
                                                                            case 1361447897:
                                                                                if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                    obj2 = 18;
                                                                                    break;
                                                                                }
                                                                            case 1498266155:
                                                                                if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                    obj2 = 85;
                                                                                    break;
                                                                                }
                                                                            case 1547988151:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                    obj2 = 43;
                                                                                    break;
                                                                                }
                                                                            case 1561464595:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                    obj2 = 38;
                                                                                    break;
                                                                                }
                                                                            case 1563525743:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                    obj2 = 40;
                                                                                    break;
                                                                                }
                                                                            case 1567024476:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                    obj2 = 39;
                                                                                    break;
                                                                                }
                                                                            case 1810705077:
                                                                                if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                    obj2 = 16;
                                                                                    break;
                                                                                }
                                                                            case 1815177512:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                    obj2 = 35;
                                                                                    break;
                                                                                }
                                                                            case 1963241394:
                                                                                if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                    obj2 = 83;
                                                                                    break;
                                                                                }
                                                                            case 2014789757:
                                                                                if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                    obj2 = 52;
                                                                                    break;
                                                                                }
                                                                            case 2022049433:
                                                                                if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                    obj2 = 71;
                                                                                    break;
                                                                                }
                                                                            case 2048733346:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                    obj2 = 21;
                                                                                    break;
                                                                                }
                                                                            case 2099392181:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                    obj2 = 34;
                                                                                    break;
                                                                                }
                                                                            case 2140162142:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 46;
                                                                                    break;
                                                                                }
                                                                                obj2 = -1;
                                                                                break;
                                                                            default:
                                                                        }
                                                                    } else {
                                                                        return;
                                                                    }
                                                                }
                                                                ConnectionsManager.onInternalPushReceived(currentAccount2);
                                                                ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                break;
                                                            }
                                                            return;
                                                    }
                                                }
                                            } else if (hashCode != 633004703) {
                                                if (loc_key3.equals("MESSAGE_ANNOUNCEMENT")) {
                                                    obj = 1;
                                                    switch (obj) {
                                                        case null:
                                                            dialog_id = custom.getInt("dc");
                                                            addr = custom.getString("addr");
                                                            parts = addr.split(":");
                                                            nativeByteBuffer = buffer;
                                                            if (parts.length != 2) {
                                                                ConnectionsManager.getInstance(currentAccount2).applyDatacenterAddress(dialog_id, parts[null], Integer.parseInt(parts[1]));
                                                                ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                return;
                                                            }
                                                            return;
                                                        case 1:
                                                            update = new TL_updateServiceNotification();
                                                            update.popup = false;
                                                            update.flags = 2;
                                                            bArr = bytes;
                                                            update.inbox_date = (int) (time / 1000);
                                                            update.message = json.getString("message");
                                                            update.type = "announcement";
                                                            update.media = new TL_messageMediaEmpty();
                                                            updates = new TL_updates();
                                                            updates.updates.add(update);
                                                            Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                            ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                            return;
                                                        default:
                                                            dialog_id = 0;
                                                            if (custom.has("channel_id")) {
                                                                channel_id = custom.getInt("channel_id");
                                                                dialog_id = (long) (-channel_id);
                                                            } else {
                                                                channel_id = 0;
                                                            }
                                                            if (custom.has("from_id")) {
                                                                user_id = custom.getInt("from_id");
                                                                dialog_id = (long) user_id;
                                                            } else {
                                                                user_id = 0;
                                                            }
                                                            dialog_id2 = dialog_id;
                                                            if (custom.has("chat_id")) {
                                                                chat_id2 = custom.getInt("chat_id");
                                                                chat_id = chat_id2;
                                                                dialog_id = (long) (-chat_id2);
                                                            } else {
                                                                dialog_id = dialog_id2;
                                                                chat_id = 0;
                                                            }
                                                            chat_id3 = chat_id;
                                                            if (dialog_id == 0) {
                                                                if (json.has("badge")) {
                                                                    badge = json.getInt("badge");
                                                                } else {
                                                                    badge = 0;
                                                                }
                                                                if (badge == 0) {
                                                                    badge = custom.getInt("msg_id");
                                                                    currentReadValue = (Integer) MessagesController.getInstance(currentAccount2).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                                    if (currentReadValue != null) {
                                                                        currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount2).getDialogReadMax(null, dialog_id));
                                                                        MessagesController.getInstance(a2).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                                    } else {
                                                                        num = currentReadValue;
                                                                        messageKeyData2 = messageKeyData;
                                                                    }
                                                                    if (badge <= currentReadValue.intValue()) {
                                                                        if (custom.has("chat_from_id")) {
                                                                            messageKey = custom.getInt("chat_from_id");
                                                                        } else {
                                                                            messageKey = null;
                                                                        }
                                                                        if (custom.has("mention")) {
                                                                            if (custom.getInt("mention") != 0) {
                                                                                messageKeyData = true;
                                                                                if (json.has("loc_args")) {
                                                                                    loc_args = json.getJSONArray("loc_args");
                                                                                    json = new String[loc_args.length()];
                                                                                    a = 0;
                                                                                    while (true) {
                                                                                        bArr2 = strBytes;
                                                                                        accountFinal = a2;
                                                                                        a2 = a;
                                                                                        if (a2 >= json.length) {
                                                                                            json[a2] = loc_args.getString(a2);
                                                                                            a = a2 + 1;
                                                                                            strBytes = bArr2;
                                                                                            a2 = accountFinal;
                                                                                        } else {
                                                                                            args = json;
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    accountFinal = a2;
                                                                                    jSONObject = json;
                                                                                    bArr2 = strBytes;
                                                                                    args = null;
                                                                                }
                                                                                args2 = args;
                                                                                strBytes = args2[0];
                                                                                userName = null;
                                                                                localMessage = false;
                                                                                supergroup = false;
                                                                                pinned = false;
                                                                                channel = false;
                                                                                messageText = null;
                                                                                if (!loc_key3.startsWith("CHAT_")) {
                                                                                    if (channel_id == 0) {
                                                                                    }
                                                                                    supergroup = channel_id == 0;
                                                                                    userName2 = strBytes;
                                                                                    strBytes = args2[1];
                                                                                    userName = userName2;
                                                                                } else if (!loc_key3.startsWith("PINNED_")) {
                                                                                    if (messageKey == null) {
                                                                                    }
                                                                                    supergroup = messageKey == null;
                                                                                    pinned = true;
                                                                                } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                                    channel = true;
                                                                                }
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    stringBuilder2 = new StringBuilder();
                                                                                    message = null;
                                                                                    stringBuilder2.append("GCM received message notification ");
                                                                                    stringBuilder2.append(loc_key3);
                                                                                    stringBuilder2.append(" for dialogId = ");
                                                                                    stringBuilder2.append(dialog_id);
                                                                                    stringBuilder2.append(" mid = ");
                                                                                    stringBuilder2.append(badge);
                                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                                } else {
                                                                                    message = null;
                                                                                }
                                                                                switch (loc_key3.hashCode()) {
                                                                                    case -2091498420:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                            obj2 = 28;
                                                                                            break;
                                                                                        }
                                                                                    case -2053872415:
                                                                                        if (loc_key3.equals("CHAT_CREATED")) {
                                                                                            obj2 = 50;
                                                                                            break;
                                                                                        }
                                                                                    case -2039746363:
                                                                                        if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                            obj2 = 9;
                                                                                            break;
                                                                                        }
                                                                                    case -1979538588:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                            obj2 = 25;
                                                                                            break;
                                                                                        }
                                                                                    case -1979536003:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                            obj2 = 29;
                                                                                            break;
                                                                                        }
                                                                                    case -1979535888:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                            obj2 = 31;
                                                                                            break;
                                                                                        }
                                                                                    case -1969004705:
                                                                                        if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                            obj2 = 53;
                                                                                            break;
                                                                                        }
                                                                                    case -1946699248:
                                                                                        if (loc_key3.equals("CHAT_JOINED")) {
                                                                                            obj2 = 59;
                                                                                            break;
                                                                                        }
                                                                                    case -1528047021:
                                                                                        if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                            obj2 = 62;
                                                                                            break;
                                                                                        }
                                                                                    case -1493579426:
                                                                                        if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                            obj2 = 10;
                                                                                            break;
                                                                                        }
                                                                                    case -1480102982:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                            obj2 = 2;
                                                                                            break;
                                                                                        }
                                                                                    case -1478041834:
                                                                                        if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                            obj2 = 7;
                                                                                            break;
                                                                                        }
                                                                                    case -1474543101:
                                                                                        if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                            obj2 = 4;
                                                                                            break;
                                                                                        }
                                                                                    case -1465695932:
                                                                                        if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                            obj2 = 81;
                                                                                            break;
                                                                                        }
                                                                                    case -1374906292:
                                                                                        if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                            obj2 = 82;
                                                                                            break;
                                                                                        }
                                                                                    case -1372940586:
                                                                                        if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                            obj2 = 58;
                                                                                            break;
                                                                                        }
                                                                                    case -1264245338:
                                                                                        if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                            obj2 = 75;
                                                                                            break;
                                                                                        }
                                                                                    case -1236086700:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                            obj2 = 33;
                                                                                            break;
                                                                                        }
                                                                                    case -1236077786:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                            obj2 = 32;
                                                                                            break;
                                                                                        }
                                                                                    case -1235686303:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                            obj2 = 20;
                                                                                            break;
                                                                                        }
                                                                                    case -1198046100:
                                                                                        if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                            obj2 = 5;
                                                                                            break;
                                                                                        }
                                                                                    case -1124254527:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                            obj2 = 44;
                                                                                            break;
                                                                                        }
                                                                                    case -1085137927:
                                                                                        if (loc_key3.equals("PINNED_GAME")) {
                                                                                            obj2 = 74;
                                                                                            break;
                                                                                        }
                                                                                    case -1084746444:
                                                                                        if (loc_key3.equals("PINNED_TEXT")) {
                                                                                            obj2 = 63;
                                                                                            break;
                                                                                        }
                                                                                    case -819729482:
                                                                                        if (loc_key3.equals("PINNED_STICKER")) {
                                                                                            obj2 = 69;
                                                                                            break;
                                                                                        }
                                                                                    case -772141857:
                                                                                        if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                            obj2 = 84;
                                                                                            break;
                                                                                        }
                                                                                    case -638310039:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                            obj2 = 26;
                                                                                            break;
                                                                                        }
                                                                                    case -589196239:
                                                                                        if (loc_key3.equals("PINNED_DOC")) {
                                                                                            obj2 = 68;
                                                                                            break;
                                                                                        }
                                                                                    case -589193654:
                                                                                        if (loc_key3.equals("PINNED_GEO")) {
                                                                                            obj2 = 72;
                                                                                            break;
                                                                                        }
                                                                                    case -589193539:
                                                                                        if (loc_key3.equals("PINNED_GIF")) {
                                                                                            obj2 = 76;
                                                                                            break;
                                                                                        }
                                                                                    case -440169325:
                                                                                        if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                            obj2 = 78;
                                                                                            break;
                                                                                        }
                                                                                    case -412748110:
                                                                                        if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                            obj2 = 56;
                                                                                            break;
                                                                                        }
                                                                                    case -228518075:
                                                                                        if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 13;
                                                                                            break;
                                                                                        }
                                                                                    case -213586509:
                                                                                        if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                            obj2 = 80;
                                                                                            break;
                                                                                        }
                                                                                    case -115582002:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                            obj2 = 49;
                                                                                            break;
                                                                                        }
                                                                                    case -112621464:
                                                                                        if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                            obj2 = 77;
                                                                                            break;
                                                                                        }
                                                                                    case -108522133:
                                                                                        if (loc_key3.equals("AUTH_REGION")) {
                                                                                            obj2 = 79;
                                                                                            break;
                                                                                        }
                                                                                    case -107572034:
                                                                                        if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                            obj2 = 6;
                                                                                            break;
                                                                                        }
                                                                                    case -40534265:
                                                                                        if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                            obj2 = 55;
                                                                                            break;
                                                                                        }
                                                                                    case 65254746:
                                                                                        if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                            obj2 = 54;
                                                                                            break;
                                                                                        }
                                                                                    case 141040782:
                                                                                        if (loc_key3.equals("CHAT_LEFT")) {
                                                                                            obj2 = 57;
                                                                                            break;
                                                                                        }
                                                                                    case 309993049:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                            obj2 = 41;
                                                                                            break;
                                                                                        }
                                                                                    case 309995634:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                            obj2 = 45;
                                                                                            break;
                                                                                        }
                                                                                    case 309995749:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                            obj2 = 47;
                                                                                            break;
                                                                                        }
                                                                                    case 320532812:
                                                                                        if (loc_key3.equals("MESSAGES")) {
                                                                                            obj2 = 19;
                                                                                            break;
                                                                                        }
                                                                                    case 328933854:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                            obj2 = 42;
                                                                                            break;
                                                                                        }
                                                                                    case 331340546:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                            obj2 = 27;
                                                                                            break;
                                                                                        }
                                                                                    case 344816990:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                            obj2 = 22;
                                                                                            break;
                                                                                        }
                                                                                    case 346878138:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                            obj2 = 24;
                                                                                            break;
                                                                                        }
                                                                                    case 350376871:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                            obj2 = 23;
                                                                                            break;
                                                                                        }
                                                                                    case 615714517:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                            obj2 = 3;
                                                                                            break;
                                                                                        }
                                                                                    case 715508879:
                                                                                        if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                            obj2 = 70;
                                                                                            break;
                                                                                        }
                                                                                    case 728985323:
                                                                                        if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                            obj2 = 65;
                                                                                            break;
                                                                                        }
                                                                                    case 731046471:
                                                                                        if (loc_key3.equals("PINNED_ROUND")) {
                                                                                            obj2 = 67;
                                                                                            break;
                                                                                        }
                                                                                    case 734545204:
                                                                                        if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                            obj2 = 66;
                                                                                            break;
                                                                                        }
                                                                                    case 802032552:
                                                                                        if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                            obj2 = 11;
                                                                                            break;
                                                                                        }
                                                                                    case 991498806:
                                                                                        if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                            obj2 = 73;
                                                                                            break;
                                                                                        }
                                                                                    case 1019917311:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                            obj2 = 60;
                                                                                            break;
                                                                                        }
                                                                                    case 1019926225:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                            obj2 = 48;
                                                                                            break;
                                                                                        }
                                                                                    case 1020317708:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                            obj2 = 36;
                                                                                            break;
                                                                                        }
                                                                                    case 1060349560:
                                                                                        if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                            obj2 = 17;
                                                                                            break;
                                                                                        }
                                                                                    case 1060358474:
                                                                                        if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                            obj2 = 15;
                                                                                            break;
                                                                                        }
                                                                                    case 1060749957:
                                                                                        if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                            obj2 = null;
                                                                                            break;
                                                                                        }
                                                                                    case 1073049781:
                                                                                        if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                            obj2 = 64;
                                                                                            break;
                                                                                        }
                                                                                    case 1078101399:
                                                                                        if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                            obj2 = 51;
                                                                                            break;
                                                                                        }
                                                                                    case 1110103437:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                            obj2 = 37;
                                                                                            break;
                                                                                        }
                                                                                    case 1160762272:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                            obj2 = 61;
                                                                                            break;
                                                                                        }
                                                                                    case 1172918249:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 30;
                                                                                            break;
                                                                                        }
                                                                                    case 1281128640:
                                                                                        if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                            obj2 = 8;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131225:
                                                                                        if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                            obj2 = 12;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131340:
                                                                                        if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                            obj2 = 14;
                                                                                            break;
                                                                                        }
                                                                                    case 1310789062:
                                                                                        if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                            obj2 = 1;
                                                                                            break;
                                                                                        }
                                                                                    case 1361447897:
                                                                                        if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                            obj2 = 18;
                                                                                            break;
                                                                                        }
                                                                                    case 1498266155:
                                                                                        if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                            obj2 = 85;
                                                                                            break;
                                                                                        }
                                                                                    case 1547988151:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                            obj2 = 43;
                                                                                            break;
                                                                                        }
                                                                                    case 1561464595:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                            obj2 = 38;
                                                                                            break;
                                                                                        }
                                                                                    case 1563525743:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                            obj2 = 40;
                                                                                            break;
                                                                                        }
                                                                                    case 1567024476:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                            obj2 = 39;
                                                                                            break;
                                                                                        }
                                                                                    case 1810705077:
                                                                                        if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                            obj2 = 16;
                                                                                            break;
                                                                                        }
                                                                                    case 1815177512:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                            obj2 = 35;
                                                                                            break;
                                                                                        }
                                                                                    case 1963241394:
                                                                                        if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                            obj2 = 83;
                                                                                            break;
                                                                                        }
                                                                                    case 2014789757:
                                                                                        if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                            obj2 = 52;
                                                                                            break;
                                                                                        }
                                                                                    case 2022049433:
                                                                                        if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                            obj2 = 71;
                                                                                            break;
                                                                                        }
                                                                                    case 2048733346:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                            obj2 = 21;
                                                                                            break;
                                                                                        }
                                                                                    case 2099392181:
                                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                            obj2 = 34;
                                                                                            break;
                                                                                        }
                                                                                    case 2140162142:
                                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                            obj2 = 46;
                                                                                            break;
                                                                                        }
                                                                                        obj2 = -1;
                                                                                        break;
                                                                                    default:
                                                                                }
                                                                            }
                                                                        }
                                                                        messageKeyData = null;
                                                                        if (json.has("loc_args")) {
                                                                            accountFinal = a2;
                                                                            jSONObject = json;
                                                                            bArr2 = strBytes;
                                                                            args = null;
                                                                        } else {
                                                                            loc_args = json.getJSONArray("loc_args");
                                                                            json = new String[loc_args.length()];
                                                                            a = 0;
                                                                            while (true) {
                                                                                bArr2 = strBytes;
                                                                                accountFinal = a2;
                                                                                a2 = a;
                                                                                if (a2 >= json.length) {
                                                                                    args = json;
                                                                                } else {
                                                                                    json[a2] = loc_args.getString(a2);
                                                                                    a = a2 + 1;
                                                                                    strBytes = bArr2;
                                                                                    a2 = accountFinal;
                                                                                }
                                                                            }
                                                                        }
                                                                        args2 = args;
                                                                        strBytes = args2[0];
                                                                        userName = null;
                                                                        localMessage = false;
                                                                        supergroup = false;
                                                                        pinned = false;
                                                                        channel = false;
                                                                        messageText = null;
                                                                        if (!loc_key3.startsWith("CHAT_")) {
                                                                            if (channel_id == 0) {
                                                                            }
                                                                            supergroup = channel_id == 0;
                                                                            userName2 = strBytes;
                                                                            strBytes = args2[1];
                                                                            userName = userName2;
                                                                        } else if (!loc_key3.startsWith("PINNED_")) {
                                                                            if (messageKey == null) {
                                                                            }
                                                                            supergroup = messageKey == null;
                                                                            pinned = true;
                                                                        } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                            channel = true;
                                                                        }
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            message = null;
                                                                        } else {
                                                                            stringBuilder2 = new StringBuilder();
                                                                            message = null;
                                                                            stringBuilder2.append("GCM received message notification ");
                                                                            stringBuilder2.append(loc_key3);
                                                                            stringBuilder2.append(" for dialogId = ");
                                                                            stringBuilder2.append(dialog_id);
                                                                            stringBuilder2.append(" mid = ");
                                                                            stringBuilder2.append(badge);
                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                        }
                                                                        switch (loc_key3.hashCode()) {
                                                                            case -2091498420:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                    obj2 = 28;
                                                                                    break;
                                                                                }
                                                                            case -2053872415:
                                                                                if (loc_key3.equals("CHAT_CREATED")) {
                                                                                    obj2 = 50;
                                                                                    break;
                                                                                }
                                                                            case -2039746363:
                                                                                if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                    obj2 = 9;
                                                                                    break;
                                                                                }
                                                                            case -1979538588:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                    obj2 = 25;
                                                                                    break;
                                                                                }
                                                                            case -1979536003:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                    obj2 = 29;
                                                                                    break;
                                                                                }
                                                                            case -1979535888:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                    obj2 = 31;
                                                                                    break;
                                                                                }
                                                                            case -1969004705:
                                                                                if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                    obj2 = 53;
                                                                                    break;
                                                                                }
                                                                            case -1946699248:
                                                                                if (loc_key3.equals("CHAT_JOINED")) {
                                                                                    obj2 = 59;
                                                                                    break;
                                                                                }
                                                                            case -1528047021:
                                                                                if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                    obj2 = 62;
                                                                                    break;
                                                                                }
                                                                            case -1493579426:
                                                                                if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                    obj2 = 10;
                                                                                    break;
                                                                                }
                                                                            case -1480102982:
                                                                                if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                    obj2 = 2;
                                                                                    break;
                                                                                }
                                                                            case -1478041834:
                                                                                if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                    obj2 = 7;
                                                                                    break;
                                                                                }
                                                                            case -1474543101:
                                                                                if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                    obj2 = 4;
                                                                                    break;
                                                                                }
                                                                            case -1465695932:
                                                                                if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                    obj2 = 81;
                                                                                    break;
                                                                                }
                                                                            case -1374906292:
                                                                                if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                    obj2 = 82;
                                                                                    break;
                                                                                }
                                                                            case -1372940586:
                                                                                if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                    obj2 = 58;
                                                                                    break;
                                                                                }
                                                                            case -1264245338:
                                                                                if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                    obj2 = 75;
                                                                                    break;
                                                                                }
                                                                            case -1236086700:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                    obj2 = 33;
                                                                                    break;
                                                                                }
                                                                            case -1236077786:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                    obj2 = 32;
                                                                                    break;
                                                                                }
                                                                            case -1235686303:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                    obj2 = 20;
                                                                                    break;
                                                                                }
                                                                            case -1198046100:
                                                                                if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                    obj2 = 5;
                                                                                    break;
                                                                                }
                                                                            case -1124254527:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                    obj2 = 44;
                                                                                    break;
                                                                                }
                                                                            case -1085137927:
                                                                                if (loc_key3.equals("PINNED_GAME")) {
                                                                                    obj2 = 74;
                                                                                    break;
                                                                                }
                                                                            case -1084746444:
                                                                                if (loc_key3.equals("PINNED_TEXT")) {
                                                                                    obj2 = 63;
                                                                                    break;
                                                                                }
                                                                            case -819729482:
                                                                                if (loc_key3.equals("PINNED_STICKER")) {
                                                                                    obj2 = 69;
                                                                                    break;
                                                                                }
                                                                            case -772141857:
                                                                                if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                    obj2 = 84;
                                                                                    break;
                                                                                }
                                                                            case -638310039:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                    obj2 = 26;
                                                                                    break;
                                                                                }
                                                                            case -589196239:
                                                                                if (loc_key3.equals("PINNED_DOC")) {
                                                                                    obj2 = 68;
                                                                                    break;
                                                                                }
                                                                            case -589193654:
                                                                                if (loc_key3.equals("PINNED_GEO")) {
                                                                                    obj2 = 72;
                                                                                    break;
                                                                                }
                                                                            case -589193539:
                                                                                if (loc_key3.equals("PINNED_GIF")) {
                                                                                    obj2 = 76;
                                                                                    break;
                                                                                }
                                                                            case -440169325:
                                                                                if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                    obj2 = 78;
                                                                                    break;
                                                                                }
                                                                            case -412748110:
                                                                                if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                    obj2 = 56;
                                                                                    break;
                                                                                }
                                                                            case -228518075:
                                                                                if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 13;
                                                                                    break;
                                                                                }
                                                                            case -213586509:
                                                                                if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                    obj2 = 80;
                                                                                    break;
                                                                                }
                                                                            case -115582002:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                    obj2 = 49;
                                                                                    break;
                                                                                }
                                                                            case -112621464:
                                                                                if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                    obj2 = 77;
                                                                                    break;
                                                                                }
                                                                            case -108522133:
                                                                                if (loc_key3.equals("AUTH_REGION")) {
                                                                                    obj2 = 79;
                                                                                    break;
                                                                                }
                                                                            case -107572034:
                                                                                if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                    obj2 = 6;
                                                                                    break;
                                                                                }
                                                                            case -40534265:
                                                                                if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                    obj2 = 55;
                                                                                    break;
                                                                                }
                                                                            case 65254746:
                                                                                if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                    obj2 = 54;
                                                                                    break;
                                                                                }
                                                                            case 141040782:
                                                                                if (loc_key3.equals("CHAT_LEFT")) {
                                                                                    obj2 = 57;
                                                                                    break;
                                                                                }
                                                                            case 309993049:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                    obj2 = 41;
                                                                                    break;
                                                                                }
                                                                            case 309995634:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                    obj2 = 45;
                                                                                    break;
                                                                                }
                                                                            case 309995749:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                    obj2 = 47;
                                                                                    break;
                                                                                }
                                                                            case 320532812:
                                                                                if (loc_key3.equals("MESSAGES")) {
                                                                                    obj2 = 19;
                                                                                    break;
                                                                                }
                                                                            case 328933854:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                    obj2 = 42;
                                                                                    break;
                                                                                }
                                                                            case 331340546:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                    obj2 = 27;
                                                                                    break;
                                                                                }
                                                                            case 344816990:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                    obj2 = 22;
                                                                                    break;
                                                                                }
                                                                            case 346878138:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                    obj2 = 24;
                                                                                    break;
                                                                                }
                                                                            case 350376871:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                    obj2 = 23;
                                                                                    break;
                                                                                }
                                                                            case 615714517:
                                                                                if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                    obj2 = 3;
                                                                                    break;
                                                                                }
                                                                            case 715508879:
                                                                                if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                    obj2 = 70;
                                                                                    break;
                                                                                }
                                                                            case 728985323:
                                                                                if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                    obj2 = 65;
                                                                                    break;
                                                                                }
                                                                            case 731046471:
                                                                                if (loc_key3.equals("PINNED_ROUND")) {
                                                                                    obj2 = 67;
                                                                                    break;
                                                                                }
                                                                            case 734545204:
                                                                                if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                    obj2 = 66;
                                                                                    break;
                                                                                }
                                                                            case 802032552:
                                                                                if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                    obj2 = 11;
                                                                                    break;
                                                                                }
                                                                            case 991498806:
                                                                                if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                    obj2 = 73;
                                                                                    break;
                                                                                }
                                                                            case 1019917311:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                    obj2 = 60;
                                                                                    break;
                                                                                }
                                                                            case 1019926225:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                    obj2 = 48;
                                                                                    break;
                                                                                }
                                                                            case 1020317708:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                    obj2 = 36;
                                                                                    break;
                                                                                }
                                                                            case 1060349560:
                                                                                if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                    obj2 = 17;
                                                                                    break;
                                                                                }
                                                                            case 1060358474:
                                                                                if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                    obj2 = 15;
                                                                                    break;
                                                                                }
                                                                            case 1060749957:
                                                                                if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                    obj2 = null;
                                                                                    break;
                                                                                }
                                                                            case 1073049781:
                                                                                if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                    obj2 = 64;
                                                                                    break;
                                                                                }
                                                                            case 1078101399:
                                                                                if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                    obj2 = 51;
                                                                                    break;
                                                                                }
                                                                            case 1110103437:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                    obj2 = 37;
                                                                                    break;
                                                                                }
                                                                            case 1160762272:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                    obj2 = 61;
                                                                                    break;
                                                                                }
                                                                            case 1172918249:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 30;
                                                                                    break;
                                                                                }
                                                                            case 1281128640:
                                                                                if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                    obj2 = 8;
                                                                                    break;
                                                                                }
                                                                            case 1281131225:
                                                                                if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                    obj2 = 12;
                                                                                    break;
                                                                                }
                                                                            case 1281131340:
                                                                                if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                    obj2 = 14;
                                                                                    break;
                                                                                }
                                                                            case 1310789062:
                                                                                if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                    obj2 = 1;
                                                                                    break;
                                                                                }
                                                                            case 1361447897:
                                                                                if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                    obj2 = 18;
                                                                                    break;
                                                                                }
                                                                            case 1498266155:
                                                                                if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                    obj2 = 85;
                                                                                    break;
                                                                                }
                                                                            case 1547988151:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                    obj2 = 43;
                                                                                    break;
                                                                                }
                                                                            case 1561464595:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                    obj2 = 38;
                                                                                    break;
                                                                                }
                                                                            case 1563525743:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                    obj2 = 40;
                                                                                    break;
                                                                                }
                                                                            case 1567024476:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                    obj2 = 39;
                                                                                    break;
                                                                                }
                                                                            case 1810705077:
                                                                                if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                    obj2 = 16;
                                                                                    break;
                                                                                }
                                                                            case 1815177512:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                    obj2 = 35;
                                                                                    break;
                                                                                }
                                                                            case 1963241394:
                                                                                if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                    obj2 = 83;
                                                                                    break;
                                                                                }
                                                                            case 2014789757:
                                                                                if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                    obj2 = 52;
                                                                                    break;
                                                                                }
                                                                            case 2022049433:
                                                                                if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                    obj2 = 71;
                                                                                    break;
                                                                                }
                                                                            case 2048733346:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                    obj2 = 21;
                                                                                    break;
                                                                                }
                                                                            case 2099392181:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                    obj2 = 34;
                                                                                    break;
                                                                                }
                                                                            case 2140162142:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 46;
                                                                                    break;
                                                                                }
                                                                                obj2 = -1;
                                                                                break;
                                                                            default:
                                                                        }
                                                                    } else {
                                                                        return;
                                                                    }
                                                                }
                                                                accountFinal = a2;
                                                                dialog_id3 = dialog_id;
                                                                jSONObject = json;
                                                                i = badge;
                                                                i2 = len;
                                                                bArr3 = messageKey;
                                                                messageKeyData2 = messageKeyData;
                                                                bArr2 = strBytes;
                                                                currentAccount = chat_id3;
                                                                a2 = custom.getInt("max_id");
                                                                updates2 = new ArrayList();
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    stringBuilder3 = new StringBuilder();
                                                                    stringBuilder3.append("GCM received read notification max_id = ");
                                                                    stringBuilder3.append(a2);
                                                                    stringBuilder3.append(" for dialogId = ");
                                                                    stringBuilder3.append(dialog_id3);
                                                                    FileLog.m0d(stringBuilder3.toString());
                                                                }
                                                                if (channel_id == 0) {
                                                                    update2 = new TL_updateReadChannelInbox();
                                                                    update2.channel_id = channel_id;
                                                                    update2.max_id = a2;
                                                                    updates2.add(update2);
                                                                } else {
                                                                    update3 = new TL_updateReadHistoryInbox();
                                                                    if (user_id == 0) {
                                                                        update3.peer = new TL_peerUser();
                                                                        update3.peer.user_id = user_id;
                                                                    } else {
                                                                        update3.peer = new TL_peerChat();
                                                                        update3.peer.chat_id = currentAccount;
                                                                    }
                                                                    update3.max_id = a2;
                                                                    updates2.add(update3);
                                                                }
                                                                MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                                                ConnectionsManager.onInternalPushReceived(currentAccount2);
                                                                ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                                break;
                                                            }
                                                            return;
                                                    }
                                                }
                                            }
                                            obj = -1;
                                            switch (obj) {
                                                case null:
                                                    dialog_id = custom.getInt("dc");
                                                    addr = custom.getString("addr");
                                                    parts = addr.split(":");
                                                    nativeByteBuffer = buffer;
                                                    if (parts.length != 2) {
                                                        ConnectionsManager.getInstance(currentAccount2).applyDatacenterAddress(dialog_id, parts[null], Integer.parseInt(parts[1]));
                                                        ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                        return;
                                                    }
                                                    return;
                                                case 1:
                                                    update = new TL_updateServiceNotification();
                                                    update.popup = false;
                                                    update.flags = 2;
                                                    bArr = bytes;
                                                    update.inbox_date = (int) (time / 1000);
                                                    update.message = json.getString("message");
                                                    update.type = "announcement";
                                                    update.media = new TL_messageMediaEmpty();
                                                    updates = new TL_updates();
                                                    updates.updates.add(update);
                                                    Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                    ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                    return;
                                                default:
                                                    dialog_id = 0;
                                                    if (custom.has("channel_id")) {
                                                        channel_id = 0;
                                                    } else {
                                                        channel_id = custom.getInt("channel_id");
                                                        dialog_id = (long) (-channel_id);
                                                    }
                                                    if (custom.has("from_id")) {
                                                        user_id = 0;
                                                    } else {
                                                        user_id = custom.getInt("from_id");
                                                        dialog_id = (long) user_id;
                                                    }
                                                    dialog_id2 = dialog_id;
                                                    if (custom.has("chat_id")) {
                                                        dialog_id = dialog_id2;
                                                        chat_id = 0;
                                                    } else {
                                                        chat_id2 = custom.getInt("chat_id");
                                                        chat_id = chat_id2;
                                                        dialog_id = (long) (-chat_id2);
                                                    }
                                                    chat_id3 = chat_id;
                                                    if (dialog_id == 0) {
                                                        if (json.has("badge")) {
                                                            badge = 0;
                                                        } else {
                                                            badge = json.getInt("badge");
                                                        }
                                                        if (badge == 0) {
                                                            accountFinal = a2;
                                                            dialog_id3 = dialog_id;
                                                            jSONObject = json;
                                                            i = badge;
                                                            i2 = len;
                                                            bArr3 = messageKey;
                                                            messageKeyData2 = messageKeyData;
                                                            bArr2 = strBytes;
                                                            currentAccount = chat_id3;
                                                            a2 = custom.getInt("max_id");
                                                            updates2 = new ArrayList();
                                                            if (BuildVars.LOGS_ENABLED) {
                                                            } else {
                                                                stringBuilder3 = new StringBuilder();
                                                                stringBuilder3.append("GCM received read notification max_id = ");
                                                                stringBuilder3.append(a2);
                                                                stringBuilder3.append(" for dialogId = ");
                                                                stringBuilder3.append(dialog_id3);
                                                                FileLog.m0d(stringBuilder3.toString());
                                                            }
                                                            if (channel_id == 0) {
                                                                update3 = new TL_updateReadHistoryInbox();
                                                                if (user_id == 0) {
                                                                    update3.peer = new TL_peerChat();
                                                                    update3.peer.chat_id = currentAccount;
                                                                } else {
                                                                    update3.peer = new TL_peerUser();
                                                                    update3.peer.user_id = user_id;
                                                                }
                                                                update3.max_id = a2;
                                                                updates2.add(update3);
                                                            } else {
                                                                update2 = new TL_updateReadChannelInbox();
                                                                update2.channel_id = channel_id;
                                                                update2.max_id = a2;
                                                                updates2.add(update2);
                                                            }
                                                            MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                                        } else {
                                                            badge = custom.getInt("msg_id");
                                                            currentReadValue = (Integer) MessagesController.getInstance(currentAccount2).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                            if (currentReadValue != null) {
                                                                num = currentReadValue;
                                                                messageKeyData2 = messageKeyData;
                                                            } else {
                                                                currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount2).getDialogReadMax(null, dialog_id));
                                                                MessagesController.getInstance(a2).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                            }
                                                            if (badge <= currentReadValue.intValue()) {
                                                                if (custom.has("chat_from_id")) {
                                                                    messageKey = null;
                                                                } else {
                                                                    messageKey = custom.getInt("chat_from_id");
                                                                }
                                                                if (custom.has("mention")) {
                                                                    if (custom.getInt("mention") != 0) {
                                                                        messageKeyData = true;
                                                                        if (json.has("loc_args")) {
                                                                            loc_args = json.getJSONArray("loc_args");
                                                                            json = new String[loc_args.length()];
                                                                            a = 0;
                                                                            while (true) {
                                                                                bArr2 = strBytes;
                                                                                accountFinal = a2;
                                                                                a2 = a;
                                                                                if (a2 >= json.length) {
                                                                                    json[a2] = loc_args.getString(a2);
                                                                                    a = a2 + 1;
                                                                                    strBytes = bArr2;
                                                                                    a2 = accountFinal;
                                                                                } else {
                                                                                    args = json;
                                                                                }
                                                                            }
                                                                        } else {
                                                                            accountFinal = a2;
                                                                            jSONObject = json;
                                                                            bArr2 = strBytes;
                                                                            args = null;
                                                                        }
                                                                        args2 = args;
                                                                        strBytes = args2[0];
                                                                        userName = null;
                                                                        localMessage = false;
                                                                        supergroup = false;
                                                                        pinned = false;
                                                                        channel = false;
                                                                        messageText = null;
                                                                        if (!loc_key3.startsWith("CHAT_")) {
                                                                            if (channel_id == 0) {
                                                                            }
                                                                            supergroup = channel_id == 0;
                                                                            userName2 = strBytes;
                                                                            strBytes = args2[1];
                                                                            userName = userName2;
                                                                        } else if (!loc_key3.startsWith("PINNED_")) {
                                                                            if (messageKey == null) {
                                                                            }
                                                                            supergroup = messageKey == null;
                                                                            pinned = true;
                                                                        } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                            channel = true;
                                                                        }
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            stringBuilder2 = new StringBuilder();
                                                                            message = null;
                                                                            stringBuilder2.append("GCM received message notification ");
                                                                            stringBuilder2.append(loc_key3);
                                                                            stringBuilder2.append(" for dialogId = ");
                                                                            stringBuilder2.append(dialog_id);
                                                                            stringBuilder2.append(" mid = ");
                                                                            stringBuilder2.append(badge);
                                                                            FileLog.m0d(stringBuilder2.toString());
                                                                        } else {
                                                                            message = null;
                                                                        }
                                                                        switch (loc_key3.hashCode()) {
                                                                            case -2091498420:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                    obj2 = 28;
                                                                                    break;
                                                                                }
                                                                            case -2053872415:
                                                                                if (loc_key3.equals("CHAT_CREATED")) {
                                                                                    obj2 = 50;
                                                                                    break;
                                                                                }
                                                                            case -2039746363:
                                                                                if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                                    obj2 = 9;
                                                                                    break;
                                                                                }
                                                                            case -1979538588:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                    obj2 = 25;
                                                                                    break;
                                                                                }
                                                                            case -1979536003:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                    obj2 = 29;
                                                                                    break;
                                                                                }
                                                                            case -1979535888:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                    obj2 = 31;
                                                                                    break;
                                                                                }
                                                                            case -1969004705:
                                                                                if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                                    obj2 = 53;
                                                                                    break;
                                                                                }
                                                                            case -1946699248:
                                                                                if (loc_key3.equals("CHAT_JOINED")) {
                                                                                    obj2 = 59;
                                                                                    break;
                                                                                }
                                                                            case -1528047021:
                                                                                if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                                    obj2 = 62;
                                                                                    break;
                                                                                }
                                                                            case -1493579426:
                                                                                if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                                    obj2 = 10;
                                                                                    break;
                                                                                }
                                                                            case -1480102982:
                                                                                if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                                    obj2 = 2;
                                                                                    break;
                                                                                }
                                                                            case -1478041834:
                                                                                if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                                    obj2 = 7;
                                                                                    break;
                                                                                }
                                                                            case -1474543101:
                                                                                if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                                    obj2 = 4;
                                                                                    break;
                                                                                }
                                                                            case -1465695932:
                                                                                if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                                    obj2 = 81;
                                                                                    break;
                                                                                }
                                                                            case -1374906292:
                                                                                if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                                    obj2 = 82;
                                                                                    break;
                                                                                }
                                                                            case -1372940586:
                                                                                if (loc_key3.equals("CHAT_RETURNED")) {
                                                                                    obj2 = 58;
                                                                                    break;
                                                                                }
                                                                            case -1264245338:
                                                                                if (loc_key3.equals("PINNED_INVOICE")) {
                                                                                    obj2 = 75;
                                                                                    break;
                                                                                }
                                                                            case -1236086700:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                    obj2 = 33;
                                                                                    break;
                                                                                }
                                                                            case -1236077786:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                    obj2 = 32;
                                                                                    break;
                                                                                }
                                                                            case -1235686303:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                    obj2 = 20;
                                                                                    break;
                                                                                }
                                                                            case -1198046100:
                                                                                if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                    obj2 = 5;
                                                                                    break;
                                                                                }
                                                                            case -1124254527:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                    obj2 = 44;
                                                                                    break;
                                                                                }
                                                                            case -1085137927:
                                                                                if (loc_key3.equals("PINNED_GAME")) {
                                                                                    obj2 = 74;
                                                                                    break;
                                                                                }
                                                                            case -1084746444:
                                                                                if (loc_key3.equals("PINNED_TEXT")) {
                                                                                    obj2 = 63;
                                                                                    break;
                                                                                }
                                                                            case -819729482:
                                                                                if (loc_key3.equals("PINNED_STICKER")) {
                                                                                    obj2 = 69;
                                                                                    break;
                                                                                }
                                                                            case -772141857:
                                                                                if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                                    obj2 = 84;
                                                                                    break;
                                                                                }
                                                                            case -638310039:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                    obj2 = 26;
                                                                                    break;
                                                                                }
                                                                            case -589196239:
                                                                                if (loc_key3.equals("PINNED_DOC")) {
                                                                                    obj2 = 68;
                                                                                    break;
                                                                                }
                                                                            case -589193654:
                                                                                if (loc_key3.equals("PINNED_GEO")) {
                                                                                    obj2 = 72;
                                                                                    break;
                                                                                }
                                                                            case -589193539:
                                                                                if (loc_key3.equals("PINNED_GIF")) {
                                                                                    obj2 = 76;
                                                                                    break;
                                                                                }
                                                                            case -440169325:
                                                                                if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                                    obj2 = 78;
                                                                                    break;
                                                                                }
                                                                            case -412748110:
                                                                                if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                                    obj2 = 56;
                                                                                    break;
                                                                                }
                                                                            case -228518075:
                                                                                if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 13;
                                                                                    break;
                                                                                }
                                                                            case -213586509:
                                                                                if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                                    obj2 = 80;
                                                                                    break;
                                                                                }
                                                                            case -115582002:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                    obj2 = 49;
                                                                                    break;
                                                                                }
                                                                            case -112621464:
                                                                                if (loc_key3.equals("CONTACT_JOINED")) {
                                                                                    obj2 = 77;
                                                                                    break;
                                                                                }
                                                                            case -108522133:
                                                                                if (loc_key3.equals("AUTH_REGION")) {
                                                                                    obj2 = 79;
                                                                                    break;
                                                                                }
                                                                            case -107572034:
                                                                                if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                                    obj2 = 6;
                                                                                    break;
                                                                                }
                                                                            case -40534265:
                                                                                if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                                    obj2 = 55;
                                                                                    break;
                                                                                }
                                                                            case 65254746:
                                                                                if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                                    obj2 = 54;
                                                                                    break;
                                                                                }
                                                                            case 141040782:
                                                                                if (loc_key3.equals("CHAT_LEFT")) {
                                                                                    obj2 = 57;
                                                                                    break;
                                                                                }
                                                                            case 309993049:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                                    obj2 = 41;
                                                                                    break;
                                                                                }
                                                                            case 309995634:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                                    obj2 = 45;
                                                                                    break;
                                                                                }
                                                                            case 309995749:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                                    obj2 = 47;
                                                                                    break;
                                                                                }
                                                                            case 320532812:
                                                                                if (loc_key3.equals("MESSAGES")) {
                                                                                    obj2 = 19;
                                                                                    break;
                                                                                }
                                                                            case 328933854:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                                    obj2 = 42;
                                                                                    break;
                                                                                }
                                                                            case 331340546:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                    obj2 = 27;
                                                                                    break;
                                                                                }
                                                                            case 344816990:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                    obj2 = 22;
                                                                                    break;
                                                                                }
                                                                            case 346878138:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                    obj2 = 24;
                                                                                    break;
                                                                                }
                                                                            case 350376871:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                    obj2 = 23;
                                                                                    break;
                                                                                }
                                                                            case 615714517:
                                                                                if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                    obj2 = 3;
                                                                                    break;
                                                                                }
                                                                            case 715508879:
                                                                                if (loc_key3.equals("PINNED_AUDIO")) {
                                                                                    obj2 = 70;
                                                                                    break;
                                                                                }
                                                                            case 728985323:
                                                                                if (loc_key3.equals("PINNED_PHOTO")) {
                                                                                    obj2 = 65;
                                                                                    break;
                                                                                }
                                                                            case 731046471:
                                                                                if (loc_key3.equals("PINNED_ROUND")) {
                                                                                    obj2 = 67;
                                                                                    break;
                                                                                }
                                                                            case 734545204:
                                                                                if (loc_key3.equals("PINNED_VIDEO")) {
                                                                                    obj2 = 66;
                                                                                    break;
                                                                                }
                                                                            case 802032552:
                                                                                if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                                    obj2 = 11;
                                                                                    break;
                                                                                }
                                                                            case 991498806:
                                                                                if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                                    obj2 = 73;
                                                                                    break;
                                                                                }
                                                                            case 1019917311:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                                    obj2 = 60;
                                                                                    break;
                                                                                }
                                                                            case 1019926225:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                                    obj2 = 48;
                                                                                    break;
                                                                                }
                                                                            case 1020317708:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                                    obj2 = 36;
                                                                                    break;
                                                                                }
                                                                            case 1060349560:
                                                                                if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                                    obj2 = 17;
                                                                                    break;
                                                                                }
                                                                            case 1060358474:
                                                                                if (loc_key3.equals("MESSAGE_GAME")) {
                                                                                    obj2 = 15;
                                                                                    break;
                                                                                }
                                                                            case 1060749957:
                                                                                if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                                    obj2 = null;
                                                                                    break;
                                                                                }
                                                                            case 1073049781:
                                                                                if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                                    obj2 = 64;
                                                                                    break;
                                                                                }
                                                                            case 1078101399:
                                                                                if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                                    obj2 = 51;
                                                                                    break;
                                                                                }
                                                                            case 1110103437:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                    obj2 = 37;
                                                                                    break;
                                                                                }
                                                                            case 1160762272:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                    obj2 = 61;
                                                                                    break;
                                                                                }
                                                                            case 1172918249:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 30;
                                                                                    break;
                                                                                }
                                                                            case 1281128640:
                                                                                if (loc_key3.equals("MESSAGE_DOC")) {
                                                                                    obj2 = 8;
                                                                                    break;
                                                                                }
                                                                            case 1281131225:
                                                                                if (loc_key3.equals("MESSAGE_GEO")) {
                                                                                    obj2 = 12;
                                                                                    break;
                                                                                }
                                                                            case 1281131340:
                                                                                if (loc_key3.equals("MESSAGE_GIF")) {
                                                                                    obj2 = 14;
                                                                                    break;
                                                                                }
                                                                            case 1310789062:
                                                                                if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                                    obj2 = 1;
                                                                                    break;
                                                                                }
                                                                            case 1361447897:
                                                                                if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                                    obj2 = 18;
                                                                                    break;
                                                                                }
                                                                            case 1498266155:
                                                                                if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                                    obj2 = 85;
                                                                                    break;
                                                                                }
                                                                            case 1547988151:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                    obj2 = 43;
                                                                                    break;
                                                                                }
                                                                            case 1561464595:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                    obj2 = 38;
                                                                                    break;
                                                                                }
                                                                            case 1563525743:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                                    obj2 = 40;
                                                                                    break;
                                                                                }
                                                                            case 1567024476:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                    obj2 = 39;
                                                                                    break;
                                                                                }
                                                                            case 1810705077:
                                                                                if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                                    obj2 = 16;
                                                                                    break;
                                                                                }
                                                                            case 1815177512:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                                    obj2 = 35;
                                                                                    break;
                                                                                }
                                                                            case 1963241394:
                                                                                if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                                    obj2 = 83;
                                                                                    break;
                                                                                }
                                                                            case 2014789757:
                                                                                if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                                    obj2 = 52;
                                                                                    break;
                                                                                }
                                                                            case 2022049433:
                                                                                if (loc_key3.equals("PINNED_CONTACT")) {
                                                                                    obj2 = 71;
                                                                                    break;
                                                                                }
                                                                            case 2048733346:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                    obj2 = 21;
                                                                                    break;
                                                                                }
                                                                            case 2099392181:
                                                                                if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                    obj2 = 34;
                                                                                    break;
                                                                                }
                                                                            case 2140162142:
                                                                                if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                    obj2 = 46;
                                                                                    break;
                                                                                }
                                                                                obj2 = -1;
                                                                                break;
                                                                            default:
                                                                        }
                                                                    }
                                                                }
                                                                messageKeyData = null;
                                                                if (json.has("loc_args")) {
                                                                    accountFinal = a2;
                                                                    jSONObject = json;
                                                                    bArr2 = strBytes;
                                                                    args = null;
                                                                } else {
                                                                    loc_args = json.getJSONArray("loc_args");
                                                                    json = new String[loc_args.length()];
                                                                    a = 0;
                                                                    while (true) {
                                                                        bArr2 = strBytes;
                                                                        accountFinal = a2;
                                                                        a2 = a;
                                                                        if (a2 >= json.length) {
                                                                            args = json;
                                                                        } else {
                                                                            json[a2] = loc_args.getString(a2);
                                                                            a = a2 + 1;
                                                                            strBytes = bArr2;
                                                                            a2 = accountFinal;
                                                                        }
                                                                    }
                                                                }
                                                                args2 = args;
                                                                strBytes = args2[0];
                                                                userName = null;
                                                                localMessage = false;
                                                                supergroup = false;
                                                                pinned = false;
                                                                channel = false;
                                                                messageText = null;
                                                                if (!loc_key3.startsWith("CHAT_")) {
                                                                    if (channel_id == 0) {
                                                                    }
                                                                    supergroup = channel_id == 0;
                                                                    userName2 = strBytes;
                                                                    strBytes = args2[1];
                                                                    userName = userName2;
                                                                } else if (!loc_key3.startsWith("PINNED_")) {
                                                                    if (messageKey == null) {
                                                                    }
                                                                    supergroup = messageKey == null;
                                                                    pinned = true;
                                                                } else if (loc_key3.startsWith("CHANNEL_")) {
                                                                    channel = true;
                                                                }
                                                                if (BuildVars.LOGS_ENABLED) {
                                                                    message = null;
                                                                } else {
                                                                    stringBuilder2 = new StringBuilder();
                                                                    message = null;
                                                                    stringBuilder2.append("GCM received message notification ");
                                                                    stringBuilder2.append(loc_key3);
                                                                    stringBuilder2.append(" for dialogId = ");
                                                                    stringBuilder2.append(dialog_id);
                                                                    stringBuilder2.append(" mid = ");
                                                                    stringBuilder2.append(badge);
                                                                    FileLog.m0d(stringBuilder2.toString());
                                                                }
                                                                switch (loc_key3.hashCode()) {
                                                                    case -2091498420:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                            obj2 = 28;
                                                                            break;
                                                                        }
                                                                    case -2053872415:
                                                                        if (loc_key3.equals("CHAT_CREATED")) {
                                                                            obj2 = 50;
                                                                            break;
                                                                        }
                                                                    case -2039746363:
                                                                        if (loc_key3.equals("MESSAGE_STICKER")) {
                                                                            obj2 = 9;
                                                                            break;
                                                                        }
                                                                    case -1979538588:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_DOC")) {
                                                                            obj2 = 25;
                                                                            break;
                                                                        }
                                                                    case -1979536003:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEO")) {
                                                                            obj2 = 29;
                                                                            break;
                                                                        }
                                                                    case -1979535888:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GIF")) {
                                                                            obj2 = 31;
                                                                            break;
                                                                        }
                                                                    case -1969004705:
                                                                        if (loc_key3.equals("CHAT_ADD_MEMBER")) {
                                                                            obj2 = 53;
                                                                            break;
                                                                        }
                                                                    case -1946699248:
                                                                        if (loc_key3.equals("CHAT_JOINED")) {
                                                                            obj2 = 59;
                                                                            break;
                                                                        }
                                                                    case -1528047021:
                                                                        if (loc_key3.equals("CHAT_MESSAGES")) {
                                                                            obj2 = 62;
                                                                            break;
                                                                        }
                                                                    case -1493579426:
                                                                        if (loc_key3.equals("MESSAGE_AUDIO")) {
                                                                            obj2 = 10;
                                                                            break;
                                                                        }
                                                                    case -1480102982:
                                                                        if (loc_key3.equals("MESSAGE_PHOTO")) {
                                                                            obj2 = 2;
                                                                            break;
                                                                        }
                                                                    case -1478041834:
                                                                        if (loc_key3.equals("MESSAGE_ROUND")) {
                                                                            obj2 = 7;
                                                                            break;
                                                                        }
                                                                    case -1474543101:
                                                                        if (loc_key3.equals("MESSAGE_VIDEO")) {
                                                                            obj2 = 4;
                                                                            break;
                                                                        }
                                                                    case -1465695932:
                                                                        if (loc_key3.equals("ENCRYPTION_ACCEPT")) {
                                                                            obj2 = 81;
                                                                            break;
                                                                        }
                                                                    case -1374906292:
                                                                        if (loc_key3.equals("ENCRYPTED_MESSAGE")) {
                                                                            obj2 = 82;
                                                                            break;
                                                                        }
                                                                    case -1372940586:
                                                                        if (loc_key3.equals("CHAT_RETURNED")) {
                                                                            obj2 = 58;
                                                                            break;
                                                                        }
                                                                    case -1264245338:
                                                                        if (loc_key3.equals("PINNED_INVOICE")) {
                                                                            obj2 = 75;
                                                                            break;
                                                                        }
                                                                    case -1236086700:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                            obj2 = 33;
                                                                            break;
                                                                        }
                                                                    case -1236077786:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GAME")) {
                                                                            obj2 = 32;
                                                                            break;
                                                                        }
                                                                    case -1235686303:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                            obj2 = 20;
                                                                            break;
                                                                        }
                                                                    case -1198046100:
                                                                        if (loc_key3.equals("MESSAGE_VIDEO_SECRET")) {
                                                                            obj2 = 5;
                                                                            break;
                                                                        }
                                                                    case -1124254527:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_CONTACT")) {
                                                                            obj2 = 44;
                                                                            break;
                                                                        }
                                                                    case -1085137927:
                                                                        if (loc_key3.equals("PINNED_GAME")) {
                                                                            obj2 = 74;
                                                                            break;
                                                                        }
                                                                    case -1084746444:
                                                                        if (loc_key3.equals("PINNED_TEXT")) {
                                                                            obj2 = 63;
                                                                            break;
                                                                        }
                                                                    case -819729482:
                                                                        if (loc_key3.equals("PINNED_STICKER")) {
                                                                            obj2 = 69;
                                                                            break;
                                                                        }
                                                                    case -772141857:
                                                                        if (loc_key3.equals("PHONE_CALL_REQUEST")) {
                                                                            obj2 = 84;
                                                                            break;
                                                                        }
                                                                    case -638310039:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                            obj2 = 26;
                                                                            break;
                                                                        }
                                                                    case -589196239:
                                                                        if (loc_key3.equals("PINNED_DOC")) {
                                                                            obj2 = 68;
                                                                            break;
                                                                        }
                                                                    case -589193654:
                                                                        if (loc_key3.equals("PINNED_GEO")) {
                                                                            obj2 = 72;
                                                                            break;
                                                                        }
                                                                    case -589193539:
                                                                        if (loc_key3.equals("PINNED_GIF")) {
                                                                            obj2 = 76;
                                                                            break;
                                                                        }
                                                                    case -440169325:
                                                                        if (loc_key3.equals("AUTH_UNKNOWN")) {
                                                                            obj2 = 78;
                                                                            break;
                                                                        }
                                                                    case -412748110:
                                                                        if (loc_key3.equals("CHAT_DELETE_YOU")) {
                                                                            obj2 = 56;
                                                                            break;
                                                                        }
                                                                    case -228518075:
                                                                        if (loc_key3.equals("MESSAGE_GEOLIVE")) {
                                                                            obj2 = 13;
                                                                            break;
                                                                        }
                                                                    case -213586509:
                                                                        if (loc_key3.equals("ENCRYPTION_REQUEST")) {
                                                                            obj2 = 80;
                                                                            break;
                                                                        }
                                                                    case -115582002:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_INVOICE")) {
                                                                            obj2 = 49;
                                                                            break;
                                                                        }
                                                                    case -112621464:
                                                                        if (loc_key3.equals("CONTACT_JOINED")) {
                                                                            obj2 = 77;
                                                                            break;
                                                                        }
                                                                    case -108522133:
                                                                        if (loc_key3.equals("AUTH_REGION")) {
                                                                            obj2 = 79;
                                                                            break;
                                                                        }
                                                                    case -107572034:
                                                                        if (loc_key3.equals("MESSAGE_SCREENSHOT")) {
                                                                            obj2 = 6;
                                                                            break;
                                                                        }
                                                                    case -40534265:
                                                                        if (loc_key3.equals("CHAT_DELETE_MEMBER")) {
                                                                            obj2 = 55;
                                                                            break;
                                                                        }
                                                                    case 65254746:
                                                                        if (loc_key3.equals("CHAT_ADD_YOU")) {
                                                                            obj2 = 54;
                                                                            break;
                                                                        }
                                                                    case 141040782:
                                                                        if (loc_key3.equals("CHAT_LEFT")) {
                                                                            obj2 = 57;
                                                                            break;
                                                                        }
                                                                    case 309993049:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_DOC")) {
                                                                            obj2 = 41;
                                                                            break;
                                                                        }
                                                                    case 309995634:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEO")) {
                                                                            obj2 = 45;
                                                                            break;
                                                                        }
                                                                    case 309995749:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_GIF")) {
                                                                            obj2 = 47;
                                                                            break;
                                                                        }
                                                                    case 320532812:
                                                                        if (loc_key3.equals("MESSAGES")) {
                                                                            obj2 = 19;
                                                                            break;
                                                                        }
                                                                    case 328933854:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_STICKER")) {
                                                                            obj2 = 42;
                                                                            break;
                                                                        }
                                                                    case 331340546:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                            obj2 = 27;
                                                                            break;
                                                                        }
                                                                    case 344816990:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                            obj2 = 22;
                                                                            break;
                                                                        }
                                                                    case 346878138:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                            obj2 = 24;
                                                                            break;
                                                                        }
                                                                    case 350376871:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                            obj2 = 23;
                                                                            break;
                                                                        }
                                                                    case 615714517:
                                                                        if (loc_key3.equals("MESSAGE_PHOTO_SECRET")) {
                                                                            obj2 = 3;
                                                                            break;
                                                                        }
                                                                    case 715508879:
                                                                        if (loc_key3.equals("PINNED_AUDIO")) {
                                                                            obj2 = 70;
                                                                            break;
                                                                        }
                                                                    case 728985323:
                                                                        if (loc_key3.equals("PINNED_PHOTO")) {
                                                                            obj2 = 65;
                                                                            break;
                                                                        }
                                                                    case 731046471:
                                                                        if (loc_key3.equals("PINNED_ROUND")) {
                                                                            obj2 = 67;
                                                                            break;
                                                                        }
                                                                    case 734545204:
                                                                        if (loc_key3.equals("PINNED_VIDEO")) {
                                                                            obj2 = 66;
                                                                            break;
                                                                        }
                                                                    case 802032552:
                                                                        if (loc_key3.equals("MESSAGE_CONTACT")) {
                                                                            obj2 = 11;
                                                                            break;
                                                                        }
                                                                    case 991498806:
                                                                        if (loc_key3.equals("PINNED_GEOLIVE")) {
                                                                            obj2 = 73;
                                                                            break;
                                                                        }
                                                                    case 1019917311:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_FWDS")) {
                                                                            obj2 = 60;
                                                                            break;
                                                                        }
                                                                    case 1019926225:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_GAME")) {
                                                                            obj2 = 48;
                                                                            break;
                                                                        }
                                                                    case 1020317708:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_TEXT")) {
                                                                            obj2 = 36;
                                                                            break;
                                                                        }
                                                                    case 1060349560:
                                                                        if (loc_key3.equals("MESSAGE_FWDS")) {
                                                                            obj2 = 17;
                                                                            break;
                                                                        }
                                                                    case 1060358474:
                                                                        if (loc_key3.equals("MESSAGE_GAME")) {
                                                                            obj2 = 15;
                                                                            break;
                                                                        }
                                                                    case 1060749957:
                                                                        if (loc_key3.equals("MESSAGE_TEXT")) {
                                                                            obj2 = null;
                                                                            break;
                                                                        }
                                                                    case 1073049781:
                                                                        if (loc_key3.equals("PINNED_NOTEXT")) {
                                                                            obj2 = 64;
                                                                            break;
                                                                        }
                                                                    case 1078101399:
                                                                        if (loc_key3.equals("CHAT_TITLE_EDITED")) {
                                                                            obj2 = 51;
                                                                            break;
                                                                        }
                                                                    case 1110103437:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                            obj2 = 37;
                                                                            break;
                                                                        }
                                                                    case 1160762272:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                            obj2 = 61;
                                                                            break;
                                                                        }
                                                                    case 1172918249:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                            obj2 = 30;
                                                                            break;
                                                                        }
                                                                    case 1281128640:
                                                                        if (loc_key3.equals("MESSAGE_DOC")) {
                                                                            obj2 = 8;
                                                                            break;
                                                                        }
                                                                    case 1281131225:
                                                                        if (loc_key3.equals("MESSAGE_GEO")) {
                                                                            obj2 = 12;
                                                                            break;
                                                                        }
                                                                    case 1281131340:
                                                                        if (loc_key3.equals("MESSAGE_GIF")) {
                                                                            obj2 = 14;
                                                                            break;
                                                                        }
                                                                    case 1310789062:
                                                                        if (loc_key3.equals("MESSAGE_NOTEXT")) {
                                                                            obj2 = 1;
                                                                            break;
                                                                        }
                                                                    case 1361447897:
                                                                        if (loc_key3.equals("MESSAGE_PHOTOS")) {
                                                                            obj2 = 18;
                                                                            break;
                                                                        }
                                                                    case 1498266155:
                                                                        if (loc_key3.equals("PHONE_CALL_MISSED")) {
                                                                            obj2 = 85;
                                                                            break;
                                                                        }
                                                                    case 1547988151:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_AUDIO")) {
                                                                            obj2 = 43;
                                                                            break;
                                                                        }
                                                                    case 1561464595:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_PHOTO")) {
                                                                            obj2 = 38;
                                                                            break;
                                                                        }
                                                                    case 1563525743:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_ROUND")) {
                                                                            obj2 = 40;
                                                                            break;
                                                                        }
                                                                    case 1567024476:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_VIDEO")) {
                                                                            obj2 = 39;
                                                                            break;
                                                                        }
                                                                    case 1810705077:
                                                                        if (loc_key3.equals("MESSAGE_INVOICE")) {
                                                                            obj2 = 16;
                                                                            break;
                                                                        }
                                                                    case 1815177512:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGES")) {
                                                                            obj2 = 35;
                                                                            break;
                                                                        }
                                                                    case 1963241394:
                                                                        if (loc_key3.equals("LOCKED_MESSAGE")) {
                                                                            obj2 = 83;
                                                                            break;
                                                                        }
                                                                    case 2014789757:
                                                                        if (loc_key3.equals("CHAT_PHOTO_EDITED")) {
                                                                            obj2 = 52;
                                                                            break;
                                                                        }
                                                                    case 2022049433:
                                                                        if (loc_key3.equals("PINNED_CONTACT")) {
                                                                            obj2 = 71;
                                                                            break;
                                                                        }
                                                                    case 2048733346:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                            obj2 = 21;
                                                                            break;
                                                                        }
                                                                    case 2099392181:
                                                                        if (loc_key3.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                            obj2 = 34;
                                                                            break;
                                                                        }
                                                                    case 2140162142:
                                                                        if (loc_key3.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                            obj2 = 46;
                                                                            break;
                                                                        }
                                                                        obj2 = -1;
                                                                        break;
                                                                    default:
                                                                }
                                                            } else {
                                                                return;
                                                            }
                                                        }
                                                        ConnectionsManager.onInternalPushReceived(currentAccount2);
                                                        ConnectionsManager.getInstance(currentAccount2).resumeNetworkMaybe();
                                                        break;
                                                    }
                                                    return;
                                            }
                                        }
                                        return;
                                    } catch (Throwable th4) {
                                        th2222 = th4;
                                        currentAccount = currentAccount2;
                                        loc_key = loc_key2;
                                        e = th2222;
                                        if (currentAccount == -1) {
                                            ConnectionsManager.onInternalPushReceived(currentAccount);
                                            ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                        } else {
                                            GcmPushListenerService.this.onDecryptError();
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("error in loc_key = ");
                                            stringBuilder.append(loc_key);
                                            FileLog.m1e(stringBuilder.toString());
                                        }
                                        FileLog.m3e(e);
                                        str = loc_key;
                                    }
                                }
                                GcmPushListenerService.this.onDecryptError();
                                return;
                            }
                            GcmPushListenerService.this.onDecryptError();
                            return;
                        }
                        try {
                            GcmPushListenerService.this.onDecryptError();
                        } catch (Throwable th5) {
                            th2222 = th5;
                            currentAccount = currentAccount2;
                            e = th2222;
                            if (currentAccount == -1) {
                                GcmPushListenerService.this.onDecryptError();
                            } else {
                                ConnectionsManager.onInternalPushReceived(currentAccount);
                                ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("error in loc_key = ");
                                stringBuilder.append(loc_key);
                                FileLog.m1e(stringBuilder.toString());
                            }
                            FileLog.m3e(e);
                            str = loc_key;
                        }
                    } catch (Throwable th22222) {
                        loc_key2 = loc_key;
                        e = th22222;
                        currentAccount = -1;
                        if (currentAccount == -1) {
                            ConnectionsManager.onInternalPushReceived(currentAccount);
                            ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                        } else {
                            GcmPushListenerService.this.onDecryptError();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("error in loc_key = ");
                            stringBuilder.append(loc_key);
                            FileLog.m1e(stringBuilder.toString());
                        }
                        FileLog.m3e(e);
                        str = loc_key;
                    }
                }
            }

            public void run() {
                ApplicationLoader.postInitApplication();
                Utilities.stageQueue.postRunnable(new C01761());
            }
        });
    }

    private void onDecryptError() {
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(a);
                ConnectionsManager.getInstance(a).resumeNetworkMaybe();
            }
        }
    }
}
