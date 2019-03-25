package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.Update;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();
        long time = message.getSentTime();
        long receiveTime = SystemClock.uptimeMillis();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$Lambda$0(this, data, time));
        try {
            this.countDownLatch.await();
        } catch (Throwable ignore) {
            FileLog.e(ignore);
        }
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("finished GCM service, time = " + (SystemClock.uptimeMillis() - receiveTime));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onMessageReceived$2$GcmPushListenerService(Map data, long time) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$Lambda$4(this, data, time));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$GcmPushListenerService(Map data, long time) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM START PROCESSING");
        }
        String loc_key = null;
        String jsonString = null;
        int currentAccount;
        Throwable e;
        try {
            Object value = data.get("p");
            if (value instanceof String) {
                byte[] bytes = Base64.decode((String) value, 8);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(bytes.length);
                nativeByteBuffer.writeBytes(bytes);
                nativeByteBuffer.position(0);
                if (SharedConfig.pushAuthKeyId == null) {
                    SharedConfig.pushAuthKeyId = new byte[8];
                    Object authKeyHash = Utilities.computeSHA1(SharedConfig.pushAuthKey);
                    System.arraycopy(authKeyHash, authKeyHash.length - 8, SharedConfig.pushAuthKeyId, 0, 8);
                }
                byte[] inAuthKeyId = new byte[8];
                nativeByteBuffer.readBytes(inAuthKeyId, true);
                if (Arrays.equals(SharedConfig.pushAuthKeyId, inAuthKeyId)) {
                    byte[] messageKey = new byte[16];
                    nativeByteBuffer.readBytes(messageKey, true);
                    MessageKeyData messageKeyData = MessageKeyData.generateMessageKeyData(SharedConfig.pushAuthKey, messageKey, true, 2);
                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, messageKeyData.aesKey, messageKeyData.aesIv, false, false, 24, bytes.length - 24);
                    if (Utilities.arraysEquals(messageKey, 0, Utilities.computeSHA256(SharedConfig.pushAuthKey, 96, 32, nativeByteBuffer.buffer, 24, nativeByteBuffer.buffer.limit()), 8)) {
                        JSONObject jSONObject;
                        JSONObject custom;
                        int a;
                        int accountFinal;
                        byte[] strBytes = new byte[nativeByteBuffer.readInt32(true)];
                        nativeByteBuffer.readBytes(strBytes, true);
                        String str = new String(strBytes, "UTF-8");
                        try {
                            Object userIdObject;
                            int accountUserId;
                            jSONObject = new JSONObject(str);
                            if (jSONObject.has("loc_key")) {
                                loc_key = jSONObject.getString("loc_key");
                            } else {
                                loc_key = "";
                            }
                            if (jSONObject.get("custom") instanceof JSONObject) {
                                custom = jSONObject.getJSONObject("custom");
                            } else {
                                custom = new JSONObject();
                            }
                            if (jSONObject.has("user_id")) {
                                userIdObject = jSONObject.get("user_id");
                            } else {
                                userIdObject = null;
                            }
                            if (userIdObject == null) {
                                accountUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                            } else if (userIdObject instanceof Integer) {
                                accountUserId = ((Integer) userIdObject).intValue();
                            } else if (userIdObject instanceof String) {
                                accountUserId = Utilities.parseInt((String) userIdObject).intValue();
                            } else {
                                accountUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                            }
                            int account = UserConfig.selectedAccount;
                            for (a = 0; a < 3; a++) {
                                if (UserConfig.getInstance(a).getClientUserId() == accountUserId) {
                                    account = a;
                                    break;
                                }
                            }
                            currentAccount = account;
                            accountFinal = account;
                            try {
                            } catch (Throwable th) {
                                e = th;
                                jsonString = str;
                            }
                        } catch (Throwable th2) {
                            e = th2;
                            jsonString = str;
                            currentAccount = -1;
                        }
                        if (UserConfig.getInstance(currentAccount).isClientActivated()) {
                            Object obj = data.get("google.sent_time");
                            Object obj2 = -1;
                            switch (loc_key.hashCode()) {
                                case -920689527:
                                    if (loc_key.equals("DC_UPDATE")) {
                                        obj2 = null;
                                        break;
                                    }
                                    break;
                                case 633004703:
                                    if (loc_key.equals("MESSAGE_ANNOUNCEMENT")) {
                                        obj2 = 1;
                                        break;
                                    }
                                    break;
                            }
                            switch (obj2) {
                                case null:
                                    int dc = custom.getInt("dc");
                                    String[] parts = custom.getString("addr").split(":");
                                    if (parts.length != 2) {
                                        this.countDownLatch.countDown();
                                        jsonString = str;
                                        return;
                                    }
                                    ConnectionsManager.getInstance(currentAccount).applyDatacenterAddress(dc, parts[0], Integer.parseInt(parts[1]));
                                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                    this.countDownLatch.countDown();
                                    jsonString = str;
                                    return;
                                case 1:
                                    TL_updateServiceNotification update = new TL_updateServiceNotification();
                                    update.popup = false;
                                    update.flags = 2;
                                    update.inbox_date = (int) (time / 1000);
                                    update.message = jSONObject.getString("message");
                                    update.type = "announcement";
                                    update.media = new TL_messageMediaEmpty();
                                    TL_updates updates = new TL_updates();
                                    updates.updates.add(update);
                                    Utilities.stageQueue.postRunnable(new GcmPushListenerService$$Lambda$5(accountFinal, updates));
                                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                    this.countDownLatch.countDown();
                                    jsonString = str;
                                    return;
                                default:
                                    int channel_id;
                                    int user_id;
                                    int chat_id;
                                    long dialog_id = 0;
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
                                    if (custom.has("chat_id")) {
                                        chat_id = custom.getInt("chat_id");
                                        dialog_id = (long) (-chat_id);
                                    } else {
                                        chat_id = 0;
                                    }
                                    if (custom.has("encryption_id")) {
                                        dialog_id = ((long) custom.getInt("encryption_id")) << 32;
                                    }
                                    if (dialog_id == 0 && "ENCRYPTED_MESSAGE".equals(loc_key)) {
                                        dialog_id = -4294967296L;
                                    }
                                    if (dialog_id != 0) {
                                        if (TextUtils.isEmpty(loc_key)) {
                                            int max_id = custom.getInt("max_id");
                                            ArrayList<Update> updates2 = new ArrayList();
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.d("GCM received read notification max_id = " + max_id + " for dialogId = " + dialog_id);
                                            }
                                            if (channel_id != 0) {
                                                TL_updateReadChannelInbox update2 = new TL_updateReadChannelInbox();
                                                update2.channel_id = channel_id;
                                                update2.max_id = max_id;
                                                updates2.add(update2);
                                            } else {
                                                TL_updateReadHistoryInbox update3 = new TL_updateReadHistoryInbox();
                                                if (user_id != 0) {
                                                    update3.peer = new TL_peerUser();
                                                    update3.peer.user_id = user_id;
                                                } else {
                                                    update3.peer = new TL_peerChat();
                                                    update3.peer.chat_id = chat_id;
                                                }
                                                update3.max_id = max_id;
                                                updates2.add(update3);
                                            }
                                            MessagesController.getInstance(accountFinal).processUpdateArray(updates2, null, null, false);
                                            this.countDownLatch.countDown();
                                        } else {
                                            int msg_id;
                                            long random_id;
                                            if (custom.has("msg_id")) {
                                                msg_id = custom.getInt("msg_id");
                                            } else {
                                                msg_id = 0;
                                            }
                                            if (custom.has("random_id")) {
                                                random_id = Utilities.parseLong(custom.getString("random_id")).longValue();
                                            } else {
                                                random_id = 0;
                                            }
                                            boolean processNotification = false;
                                            if (msg_id != 0) {
                                                Integer currentReadValue = (Integer) MessagesController.getInstance(currentAccount).dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                if (currentReadValue == null) {
                                                    currentReadValue = Integer.valueOf(MessagesStorage.getInstance(currentAccount).getDialogReadMax(false, dialog_id));
                                                    MessagesController.getInstance(accountFinal).dialogs_read_inbox_max.put(Long.valueOf(dialog_id), currentReadValue);
                                                }
                                                if (msg_id > currentReadValue.intValue()) {
                                                    processNotification = true;
                                                }
                                            } else if (!(random_id == 0 || MessagesStorage.getInstance(account).checkMessageByRandomId(random_id))) {
                                                processNotification = true;
                                            }
                                            if (processNotification) {
                                                int chat_from_id;
                                                String[] args;
                                                if (custom.has("chat_from_id")) {
                                                    chat_from_id = custom.getInt("chat_from_id");
                                                } else {
                                                    chat_from_id = 0;
                                                }
                                                boolean mention = custom.has("mention") && custom.getInt("mention") != 0;
                                                if (jSONObject.has("loc_args")) {
                                                    JSONArray loc_args = jSONObject.getJSONArray("loc_args");
                                                    args = new String[loc_args.length()];
                                                    for (a = 0; a < args.length; a++) {
                                                        args[a] = loc_args.getString(a);
                                                    }
                                                } else {
                                                    args = null;
                                                }
                                                String messageText = null;
                                                String message1 = null;
                                                String name = args[0];
                                                String userName = null;
                                                boolean localMessage = false;
                                                boolean supergroup = false;
                                                boolean pinned = false;
                                                boolean channel = false;
                                                if (loc_key.startsWith("CHAT_")) {
                                                    supergroup = channel_id != 0;
                                                    userName = name;
                                                    name = args[1];
                                                } else if (loc_key.startsWith("PINNED_")) {
                                                    supergroup = chat_from_id != 0;
                                                    pinned = true;
                                                } else if (loc_key.startsWith("CHANNEL_")) {
                                                    channel = true;
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.d("GCM received message notification " + loc_key + " for dialogId = " + dialog_id + " mid = " + msg_id);
                                                }
                                                obj2 = -1;
                                                switch (loc_key.hashCode()) {
                                                    case -2091498420:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                            obj2 = 29;
                                                            break;
                                                        }
                                                        break;
                                                    case -2053872415:
                                                        if (loc_key.equals("CHAT_CREATED")) {
                                                            obj2 = 53;
                                                            break;
                                                        }
                                                        break;
                                                    case -2039746363:
                                                        if (loc_key.equals("MESSAGE_STICKER")) {
                                                            obj2 = 9;
                                                            break;
                                                        }
                                                        break;
                                                    case -1979538588:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_DOC")) {
                                                            obj2 = 26;
                                                            break;
                                                        }
                                                        break;
                                                    case -1979536003:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_GEO")) {
                                                            obj2 = 31;
                                                            break;
                                                        }
                                                        break;
                                                    case -1979535888:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_GIF")) {
                                                            obj2 = 33;
                                                            break;
                                                        }
                                                        break;
                                                    case -1969004705:
                                                        if (loc_key.equals("CHAT_ADD_MEMBER")) {
                                                            obj2 = 56;
                                                            break;
                                                        }
                                                        break;
                                                    case -1946699248:
                                                        if (loc_key.equals("CHAT_JOINED")) {
                                                            obj2 = 62;
                                                            break;
                                                        }
                                                        break;
                                                    case -1528047021:
                                                        if (loc_key.equals("CHAT_MESSAGES")) {
                                                            obj2 = 65;
                                                            break;
                                                        }
                                                        break;
                                                    case -1493579426:
                                                        if (loc_key.equals("MESSAGE_AUDIO")) {
                                                            obj2 = 10;
                                                            break;
                                                        }
                                                        break;
                                                    case -1480102982:
                                                        if (loc_key.equals("MESSAGE_PHOTO")) {
                                                            obj2 = 2;
                                                            break;
                                                        }
                                                        break;
                                                    case -1478041834:
                                                        if (loc_key.equals("MESSAGE_ROUND")) {
                                                            obj2 = 7;
                                                            break;
                                                        }
                                                        break;
                                                    case -1474543101:
                                                        if (loc_key.equals("MESSAGE_VIDEO")) {
                                                            obj2 = 4;
                                                            break;
                                                        }
                                                        break;
                                                    case -1465695932:
                                                        if (loc_key.equals("ENCRYPTION_ACCEPT")) {
                                                            obj2 = 86;
                                                            break;
                                                        }
                                                        break;
                                                    case -1374906292:
                                                        if (loc_key.equals("ENCRYPTED_MESSAGE")) {
                                                            obj2 = 84;
                                                            break;
                                                        }
                                                        break;
                                                    case -1372940586:
                                                        if (loc_key.equals("CHAT_RETURNED")) {
                                                            obj2 = 61;
                                                            break;
                                                        }
                                                        break;
                                                    case -1264245338:
                                                        if (loc_key.equals("PINNED_INVOICE")) {
                                                            obj2 = 79;
                                                            break;
                                                        }
                                                        break;
                                                    case -1236086700:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_FWDS")) {
                                                            obj2 = 35;
                                                            break;
                                                        }
                                                        break;
                                                    case -1236077786:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_GAME")) {
                                                            obj2 = 34;
                                                            break;
                                                        }
                                                        break;
                                                    case -1235796237:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_POLL")) {
                                                            obj2 = 30;
                                                            break;
                                                        }
                                                        break;
                                                    case -1235686303:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_TEXT")) {
                                                            obj2 = 21;
                                                            break;
                                                        }
                                                        break;
                                                    case -1198046100:
                                                        if (loc_key.equals("MESSAGE_VIDEO_SECRET")) {
                                                            obj2 = 5;
                                                            break;
                                                        }
                                                        break;
                                                    case -1124254527:
                                                        if (loc_key.equals("CHAT_MESSAGE_CONTACT")) {
                                                            obj2 = 46;
                                                            break;
                                                        }
                                                        break;
                                                    case -1085137927:
                                                        if (loc_key.equals("PINNED_GAME")) {
                                                            obj2 = 78;
                                                            break;
                                                        }
                                                        break;
                                                    case -1084856378:
                                                        if (loc_key.equals("PINNED_POLL")) {
                                                            obj2 = 75;
                                                            break;
                                                        }
                                                        break;
                                                    case -1084746444:
                                                        if (loc_key.equals("PINNED_TEXT")) {
                                                            obj2 = 66;
                                                            break;
                                                        }
                                                        break;
                                                    case -819729482:
                                                        if (loc_key.equals("PINNED_STICKER")) {
                                                            obj2 = 72;
                                                            break;
                                                        }
                                                        break;
                                                    case -772141857:
                                                        if (loc_key.equals("PHONE_CALL_REQUEST")) {
                                                            obj2 = 88;
                                                            break;
                                                        }
                                                        break;
                                                    case -638310039:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_STICKER")) {
                                                            obj2 = 27;
                                                            break;
                                                        }
                                                        break;
                                                    case -589196239:
                                                        if (loc_key.equals("PINNED_DOC")) {
                                                            obj2 = 71;
                                                            break;
                                                        }
                                                        break;
                                                    case -589193654:
                                                        if (loc_key.equals("PINNED_GEO")) {
                                                            obj2 = 76;
                                                            break;
                                                        }
                                                        break;
                                                    case -589193539:
                                                        if (loc_key.equals("PINNED_GIF")) {
                                                            obj2 = 80;
                                                            break;
                                                        }
                                                        break;
                                                    case -440169325:
                                                        if (loc_key.equals("AUTH_UNKNOWN")) {
                                                            obj2 = 82;
                                                            break;
                                                        }
                                                        break;
                                                    case -412748110:
                                                        if (loc_key.equals("CHAT_DELETE_YOU")) {
                                                            obj2 = 59;
                                                            break;
                                                        }
                                                        break;
                                                    case -228518075:
                                                        if (loc_key.equals("MESSAGE_GEOLIVE")) {
                                                            obj2 = 14;
                                                            break;
                                                        }
                                                        break;
                                                    case -213586509:
                                                        if (loc_key.equals("ENCRYPTION_REQUEST")) {
                                                            obj2 = 85;
                                                            break;
                                                        }
                                                        break;
                                                    case -115582002:
                                                        if (loc_key.equals("CHAT_MESSAGE_INVOICE")) {
                                                            obj2 = 52;
                                                            break;
                                                        }
                                                        break;
                                                    case -112621464:
                                                        if (loc_key.equals("CONTACT_JOINED")) {
                                                            obj2 = 81;
                                                            break;
                                                        }
                                                        break;
                                                    case -108522133:
                                                        if (loc_key.equals("AUTH_REGION")) {
                                                            obj2 = 83;
                                                            break;
                                                        }
                                                        break;
                                                    case -107572034:
                                                        if (loc_key.equals("MESSAGE_SCREENSHOT")) {
                                                            obj2 = 6;
                                                            break;
                                                        }
                                                        break;
                                                    case -40534265:
                                                        if (loc_key.equals("CHAT_DELETE_MEMBER")) {
                                                            obj2 = 58;
                                                            break;
                                                        }
                                                        break;
                                                    case 65254746:
                                                        if (loc_key.equals("CHAT_ADD_YOU")) {
                                                            obj2 = 57;
                                                            break;
                                                        }
                                                        break;
                                                    case 141040782:
                                                        if (loc_key.equals("CHAT_LEFT")) {
                                                            obj2 = 60;
                                                            break;
                                                        }
                                                        break;
                                                    case 309993049:
                                                        if (loc_key.equals("CHAT_MESSAGE_DOC")) {
                                                            obj2 = 43;
                                                            break;
                                                        }
                                                        break;
                                                    case 309995634:
                                                        if (loc_key.equals("CHAT_MESSAGE_GEO")) {
                                                            obj2 = 48;
                                                            break;
                                                        }
                                                        break;
                                                    case 309995749:
                                                        if (loc_key.equals("CHAT_MESSAGE_GIF")) {
                                                            obj2 = 50;
                                                            break;
                                                        }
                                                        break;
                                                    case 320532812:
                                                        if (loc_key.equals("MESSAGES")) {
                                                            obj2 = 20;
                                                            break;
                                                        }
                                                        break;
                                                    case 328933854:
                                                        if (loc_key.equals("CHAT_MESSAGE_STICKER")) {
                                                            obj2 = 44;
                                                            break;
                                                        }
                                                        break;
                                                    case 331340546:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                            obj2 = 28;
                                                            break;
                                                        }
                                                        break;
                                                    case 344816990:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                            obj2 = 23;
                                                            break;
                                                        }
                                                        break;
                                                    case 346878138:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_ROUND")) {
                                                            obj2 = 25;
                                                            break;
                                                        }
                                                        break;
                                                    case 350376871:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                            obj2 = 24;
                                                            break;
                                                        }
                                                        break;
                                                    case 615714517:
                                                        if (loc_key.equals("MESSAGE_PHOTO_SECRET")) {
                                                            obj2 = 3;
                                                            break;
                                                        }
                                                        break;
                                                    case 715508879:
                                                        if (loc_key.equals("PINNED_AUDIO")) {
                                                            obj2 = 73;
                                                            break;
                                                        }
                                                        break;
                                                    case 728985323:
                                                        if (loc_key.equals("PINNED_PHOTO")) {
                                                            obj2 = 68;
                                                            break;
                                                        }
                                                        break;
                                                    case 731046471:
                                                        if (loc_key.equals("PINNED_ROUND")) {
                                                            obj2 = 70;
                                                            break;
                                                        }
                                                        break;
                                                    case 734545204:
                                                        if (loc_key.equals("PINNED_VIDEO")) {
                                                            obj2 = 69;
                                                            break;
                                                        }
                                                        break;
                                                    case 802032552:
                                                        if (loc_key.equals("MESSAGE_CONTACT")) {
                                                            obj2 = 11;
                                                            break;
                                                        }
                                                        break;
                                                    case 991498806:
                                                        if (loc_key.equals("PINNED_GEOLIVE")) {
                                                            obj2 = 77;
                                                            break;
                                                        }
                                                        break;
                                                    case 1019917311:
                                                        if (loc_key.equals("CHAT_MESSAGE_FWDS")) {
                                                            obj2 = 63;
                                                            break;
                                                        }
                                                        break;
                                                    case 1019926225:
                                                        if (loc_key.equals("CHAT_MESSAGE_GAME")) {
                                                            obj2 = 51;
                                                            break;
                                                        }
                                                        break;
                                                    case 1020207774:
                                                        if (loc_key.equals("CHAT_MESSAGE_POLL")) {
                                                            obj2 = 47;
                                                            break;
                                                        }
                                                        break;
                                                    case 1020317708:
                                                        if (loc_key.equals("CHAT_MESSAGE_TEXT")) {
                                                            obj2 = 38;
                                                            break;
                                                        }
                                                        break;
                                                    case 1060349560:
                                                        if (loc_key.equals("MESSAGE_FWDS")) {
                                                            obj2 = 18;
                                                            break;
                                                        }
                                                        break;
                                                    case 1060358474:
                                                        if (loc_key.equals("MESSAGE_GAME")) {
                                                            obj2 = 16;
                                                            break;
                                                        }
                                                        break;
                                                    case 1060640023:
                                                        if (loc_key.equals("MESSAGE_POLL")) {
                                                            obj2 = 12;
                                                            break;
                                                        }
                                                        break;
                                                    case 1060749957:
                                                        if (loc_key.equals("MESSAGE_TEXT")) {
                                                            obj2 = null;
                                                            break;
                                                        }
                                                        break;
                                                    case 1073049781:
                                                        if (loc_key.equals("PINNED_NOTEXT")) {
                                                            obj2 = 67;
                                                            break;
                                                        }
                                                        break;
                                                    case 1078101399:
                                                        if (loc_key.equals("CHAT_TITLE_EDITED")) {
                                                            obj2 = 54;
                                                            break;
                                                        }
                                                        break;
                                                    case 1110103437:
                                                        if (loc_key.equals("CHAT_MESSAGE_NOTEXT")) {
                                                            obj2 = 39;
                                                            break;
                                                        }
                                                        break;
                                                    case 1160762272:
                                                        if (loc_key.equals("CHAT_MESSAGE_PHOTOS")) {
                                                            obj2 = 64;
                                                            break;
                                                        }
                                                        break;
                                                    case 1172918249:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                            obj2 = 32;
                                                            break;
                                                        }
                                                        break;
                                                    case 1281128640:
                                                        if (loc_key.equals("MESSAGE_DOC")) {
                                                            obj2 = 8;
                                                            break;
                                                        }
                                                        break;
                                                    case 1281131225:
                                                        if (loc_key.equals("MESSAGE_GEO")) {
                                                            obj2 = 13;
                                                            break;
                                                        }
                                                        break;
                                                    case 1281131340:
                                                        if (loc_key.equals("MESSAGE_GIF")) {
                                                            obj2 = 15;
                                                            break;
                                                        }
                                                        break;
                                                    case 1310789062:
                                                        if (loc_key.equals("MESSAGE_NOTEXT")) {
                                                            obj2 = 1;
                                                            break;
                                                        }
                                                        break;
                                                    case 1361447897:
                                                        if (loc_key.equals("MESSAGE_PHOTOS")) {
                                                            obj2 = 19;
                                                            break;
                                                        }
                                                        break;
                                                    case 1498266155:
                                                        if (loc_key.equals("PHONE_CALL_MISSED")) {
                                                            obj2 = 89;
                                                            break;
                                                        }
                                                        break;
                                                    case 1547988151:
                                                        if (loc_key.equals("CHAT_MESSAGE_AUDIO")) {
                                                            obj2 = 45;
                                                            break;
                                                        }
                                                        break;
                                                    case 1561464595:
                                                        if (loc_key.equals("CHAT_MESSAGE_PHOTO")) {
                                                            obj2 = 40;
                                                            break;
                                                        }
                                                        break;
                                                    case 1563525743:
                                                        if (loc_key.equals("CHAT_MESSAGE_ROUND")) {
                                                            obj2 = 42;
                                                            break;
                                                        }
                                                        break;
                                                    case 1567024476:
                                                        if (loc_key.equals("CHAT_MESSAGE_VIDEO")) {
                                                            obj2 = 41;
                                                            break;
                                                        }
                                                        break;
                                                    case 1810705077:
                                                        if (loc_key.equals("MESSAGE_INVOICE")) {
                                                            obj2 = 17;
                                                            break;
                                                        }
                                                        break;
                                                    case 1815177512:
                                                        if (loc_key.equals("CHANNEL_MESSAGES")) {
                                                            obj2 = 37;
                                                            break;
                                                        }
                                                        break;
                                                    case 1963241394:
                                                        if (loc_key.equals("LOCKED_MESSAGE")) {
                                                            obj2 = 87;
                                                            break;
                                                        }
                                                        break;
                                                    case 2014789757:
                                                        if (loc_key.equals("CHAT_PHOTO_EDITED")) {
                                                            obj2 = 55;
                                                            break;
                                                        }
                                                        break;
                                                    case 2022049433:
                                                        if (loc_key.equals("PINNED_CONTACT")) {
                                                            obj2 = 74;
                                                            break;
                                                        }
                                                        break;
                                                    case 2048733346:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                            obj2 = 22;
                                                            break;
                                                        }
                                                        break;
                                                    case 2099392181:
                                                        if (loc_key.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                            obj2 = 36;
                                                            break;
                                                        }
                                                        break;
                                                    case 2140162142:
                                                        if (loc_key.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                            obj2 = 49;
                                                            break;
                                                        }
                                                        break;
                                                }
                                                switch (obj2) {
                                                    case null:
                                                        messageText = LocaleController.formatString("NotificationMessageText", NUM, args[0], args[1]);
                                                        message1 = args[1];
                                                        break;
                                                    case 1:
                                                        messageText = LocaleController.formatString("NotificationMessageNoText", NUM, args[0]);
                                                        message1 = LocaleController.getString("Message", NUM);
                                                        break;
                                                    case 2:
                                                        messageText = LocaleController.formatString("NotificationMessagePhoto", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachPhoto", NUM);
                                                        break;
                                                    case 3:
                                                        messageText = LocaleController.formatString("NotificationMessageSDPhoto", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachDestructingPhoto", NUM);
                                                        break;
                                                    case 4:
                                                        messageText = LocaleController.formatString("NotificationMessageVideo", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachVideo", NUM);
                                                        break;
                                                    case 5:
                                                        messageText = LocaleController.formatString("NotificationMessageSDVideo", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachDestructingVideo", NUM);
                                                        break;
                                                    case 6:
                                                        messageText = LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", args[0]);
                                                        break;
                                                    case 7:
                                                        messageText = LocaleController.formatString("NotificationMessageRound", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachRound", NUM);
                                                        break;
                                                    case 8:
                                                        messageText = LocaleController.formatString("NotificationMessageDocument", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachDocument", NUM);
                                                        break;
                                                    case 9:
                                                        if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                                                            messageText = LocaleController.formatString("NotificationMessageStickerEmoji", NUM, args[0], args[1]);
                                                            message1 = args[1] + " " + LocaleController.getString("AttachSticker", NUM);
                                                            break;
                                                        }
                                                        messageText = LocaleController.formatString("NotificationMessageSticker", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachSticker", NUM);
                                                        break;
                                                    case 10:
                                                        messageText = LocaleController.formatString("NotificationMessageAudio", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachAudio", NUM);
                                                        break;
                                                    case 11:
                                                        messageText = LocaleController.formatString("NotificationMessageContact", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachContact", NUM);
                                                        break;
                                                    case 12:
                                                        messageText = LocaleController.formatString("NotificationMessagePoll", NUM, args[0]);
                                                        message1 = LocaleController.getString("Poll", NUM);
                                                        break;
                                                    case 13:
                                                        messageText = LocaleController.formatString("NotificationMessageMap", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachLocation", NUM);
                                                        break;
                                                    case 14:
                                                        messageText = LocaleController.formatString("NotificationMessageLiveLocation", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachLiveLocation", NUM);
                                                        break;
                                                    case 15:
                                                        messageText = LocaleController.formatString("NotificationMessageGif", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachGif", NUM);
                                                        break;
                                                    case 16:
                                                        messageText = LocaleController.formatString("NotificationMessageGame", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachGame", NUM);
                                                        break;
                                                    case 17:
                                                        messageText = LocaleController.formatString("NotificationMessageInvoice", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("PaymentInvoice", NUM);
                                                        break;
                                                    case 18:
                                                        messageText = LocaleController.formatString("NotificationMessageForwardFew", NUM, args[0], LocaleController.formatPluralString("messages", Utilities.parseInt(args[1]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 19:
                                                        messageText = LocaleController.formatString("NotificationMessageFew", NUM, args[0], LocaleController.formatPluralString("Photos", Utilities.parseInt(args[1]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 20:
                                                        messageText = LocaleController.formatString("NotificationMessageFew", NUM, args[0], LocaleController.formatPluralString("messages", Utilities.parseInt(args[1]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 21:
                                                        messageText = LocaleController.formatString("NotificationMessageText", NUM, args[0], args[1]);
                                                        message1 = args[1];
                                                        break;
                                                    case 22:
                                                        messageText = LocaleController.formatString("ChannelMessageNoText", NUM, args[0]);
                                                        message1 = LocaleController.getString("Message", NUM);
                                                        break;
                                                    case 23:
                                                        messageText = LocaleController.formatString("ChannelMessagePhoto", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachPhoto", NUM);
                                                        break;
                                                    case 24:
                                                        messageText = LocaleController.formatString("ChannelMessageVideo", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachVideo", NUM);
                                                        break;
                                                    case 25:
                                                        messageText = LocaleController.formatString("ChannelMessageRound", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachRound", NUM);
                                                        break;
                                                    case 26:
                                                        messageText = LocaleController.formatString("ChannelMessageDocument", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachDocument", NUM);
                                                        break;
                                                    case 27:
                                                        if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                                                            messageText = LocaleController.formatString("ChannelMessageStickerEmoji", NUM, args[0], args[1]);
                                                            message1 = args[1] + " " + LocaleController.getString("AttachSticker", NUM);
                                                            break;
                                                        }
                                                        messageText = LocaleController.formatString("ChannelMessageSticker", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachSticker", NUM);
                                                        break;
                                                    case 28:
                                                        messageText = LocaleController.formatString("ChannelMessageAudio", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachAudio", NUM);
                                                        break;
                                                    case 29:
                                                        messageText = LocaleController.formatString("ChannelMessageContact", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachContact", NUM);
                                                        break;
                                                    case 30:
                                                        messageText = LocaleController.formatString("ChannelMessagePoll", NUM, args[0]);
                                                        message1 = LocaleController.getString("Poll", NUM);
                                                        break;
                                                    case 31:
                                                        messageText = LocaleController.formatString("ChannelMessageMap", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachLocation", NUM);
                                                        break;
                                                    case 32:
                                                        messageText = LocaleController.formatString("ChannelMessageLiveLocation", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachLiveLocation", NUM);
                                                        break;
                                                    case 33:
                                                        messageText = LocaleController.formatString("ChannelMessageGIF", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachGif", NUM);
                                                        break;
                                                    case 34:
                                                        messageText = LocaleController.formatString("NotificationMessageGame", NUM, args[0]);
                                                        message1 = LocaleController.getString("AttachGame", NUM);
                                                        break;
                                                    case 35:
                                                        messageText = LocaleController.formatString("ChannelMessageFew", NUM, args[0], LocaleController.formatPluralString("ForwardedMessageCount", Utilities.parseInt(args[1]).intValue()).toLowerCase());
                                                        localMessage = true;
                                                        break;
                                                    case 36:
                                                        messageText = LocaleController.formatString("ChannelMessageFew", NUM, args[0], LocaleController.formatPluralString("Photos", Utilities.parseInt(args[1]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 37:
                                                        messageText = LocaleController.formatString("ChannelMessageFew", NUM, args[0], LocaleController.formatPluralString("messages", Utilities.parseInt(args[1]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 38:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupText", NUM, args[0], args[1], args[2]);
                                                        message1 = args[2];
                                                        break;
                                                    case 39:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupNoText", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("Message", NUM);
                                                        break;
                                                    case 40:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupPhoto", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachPhoto", NUM);
                                                        break;
                                                    case 41:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupVideo", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachVideo", NUM);
                                                        break;
                                                    case 42:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupRound", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachRound", NUM);
                                                        break;
                                                    case 43:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupDocument", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachDocument", NUM);
                                                        break;
                                                    case 44:
                                                        if (args.length > 2 && !TextUtils.isEmpty(args[2])) {
                                                            messageText = LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, args[0], args[1], args[2]);
                                                            message1 = args[2] + " " + LocaleController.getString("AttachSticker", NUM);
                                                            break;
                                                        }
                                                        messageText = LocaleController.formatString("NotificationMessageGroupSticker", NUM, args[0], args[1]);
                                                        message1 = args[1] + " " + LocaleController.getString("AttachSticker", NUM);
                                                        break;
                                                        break;
                                                    case 45:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupAudio", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachAudio", NUM);
                                                        break;
                                                    case 46:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupContact", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachContact", NUM);
                                                        break;
                                                    case 47:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupPoll", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("Poll", NUM);
                                                        break;
                                                    case 48:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupMap", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachLocation", NUM);
                                                        break;
                                                    case 49:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachLiveLocation", NUM);
                                                        break;
                                                    case 50:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupGif", NUM, args[0], args[1]);
                                                        message1 = LocaleController.getString("AttachGif", NUM);
                                                        break;
                                                    case 51:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupGame", NUM, args[0], args[1], args[2]);
                                                        message1 = LocaleController.getString("AttachGame", NUM);
                                                        break;
                                                    case 52:
                                                        messageText = LocaleController.formatString("NotificationMessageGroupInvoice", NUM, args[0], args[1], args[2]);
                                                        message1 = LocaleController.getString("PaymentInvoice", NUM);
                                                        break;
                                                    case 53:
                                                        messageText = LocaleController.formatString("NotificationInvitedToGroup", NUM, args[0], args[1]);
                                                        break;
                                                    case 54:
                                                        messageText = LocaleController.formatString("NotificationEditedGroupName", NUM, args[0], args[1]);
                                                        break;
                                                    case 55:
                                                        messageText = LocaleController.formatString("NotificationEditedGroupPhoto", NUM, args[0], args[1]);
                                                        break;
                                                    case 56:
                                                        messageText = LocaleController.formatString("NotificationGroupAddMember", NUM, args[0], args[1], args[2]);
                                                        break;
                                                    case 57:
                                                        messageText = LocaleController.formatString("NotificationInvitedToGroup", NUM, args[0], args[1]);
                                                        break;
                                                    case 58:
                                                        messageText = LocaleController.formatString("NotificationGroupKickMember", NUM, args[0], args[1]);
                                                        break;
                                                    case 59:
                                                        messageText = LocaleController.formatString("NotificationGroupKickYou", NUM, args[0], args[1]);
                                                        break;
                                                    case 60:
                                                        messageText = LocaleController.formatString("NotificationGroupLeftMember", NUM, args[0], args[1]);
                                                        break;
                                                    case 61:
                                                        messageText = LocaleController.formatString("NotificationGroupAddSelf", NUM, args[0], args[1]);
                                                        break;
                                                    case 62:
                                                        messageText = LocaleController.formatString("NotificationGroupAddSelfMega", NUM, args[0], args[1]);
                                                        break;
                                                    case 63:
                                                        messageText = LocaleController.formatString("NotificationGroupForwardedFew", NUM, args[0], args[1], LocaleController.formatPluralString("messages", Utilities.parseInt(args[2]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 64:
                                                        messageText = LocaleController.formatString("NotificationGroupFew", NUM, args[0], args[1], LocaleController.formatPluralString("Photos", Utilities.parseInt(args[2]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 65:
                                                        messageText = LocaleController.formatString("NotificationGroupFew", NUM, args[0], args[1], LocaleController.formatPluralString("messages", Utilities.parseInt(args[2]).intValue()));
                                                        localMessage = true;
                                                        break;
                                                    case 66:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, args[0], args[1]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedText", NUM, args[0], args[1], args[2]);
                                                            break;
                                                        }
                                                    case 67:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedNoText", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 68:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedPhoto", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 69:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedVideo", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 70:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedRound", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 71:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedFile", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 72:
                                                        if (chat_from_id == 0) {
                                                            if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                                                                messageText = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, args[0], args[1]);
                                                                break;
                                                            } else {
                                                                messageText = LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, args[0]);
                                                                break;
                                                            }
                                                        } else if (args.length > 2 && !TextUtils.isEmpty(args[2])) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, args[0], args[1], args[2]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedSticker", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                        break;
                                                    case 73:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedVoice", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 74:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedContactChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedContact", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 75:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedPollChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedPoll", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 76:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeo", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 77:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 78:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGame", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 79:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedInvoice", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 80:
                                                        if (chat_from_id == 0) {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, args[0]);
                                                            break;
                                                        } else {
                                                            messageText = LocaleController.formatString("NotificationActionPinnedGif", NUM, args[0], args[1]);
                                                            break;
                                                        }
                                                    case 81:
                                                    case 82:
                                                    case 83:
                                                    case 85:
                                                    case 86:
                                                    case 87:
                                                    case 88:
                                                    case 89:
                                                        break;
                                                    case 84:
                                                        messageText = LocaleController.getString("YouHaveNewMessage", NUM);
                                                        name = LocaleController.getString("SecretChatName", NUM);
                                                        localMessage = true;
                                                        break;
                                                    default:
                                                        if (BuildVars.LOGS_ENABLED) {
                                                            FileLog.w("unhandled loc_key = " + loc_key);
                                                            break;
                                                        }
                                                        break;
                                                }
                                                if (messageText != null) {
                                                    TL_message messageOwner = new TL_message();
                                                    messageOwner.id = msg_id;
                                                    messageOwner.random_id = random_id;
                                                    if (message1 == null) {
                                                        message1 = messageText;
                                                    }
                                                    messageOwner.message = message1;
                                                    messageOwner.date = (int) (time / 1000);
                                                    if (pinned) {
                                                        messageOwner.action = new TL_messageActionPinMessage();
                                                    }
                                                    if (supergroup) {
                                                        messageOwner.flags |= Integer.MIN_VALUE;
                                                    }
                                                    messageOwner.dialog_id = dialog_id;
                                                    if (channel_id != 0) {
                                                        messageOwner.to_id = new TL_peerChannel();
                                                        messageOwner.to_id.channel_id = channel_id;
                                                    } else if (chat_id != 0) {
                                                        messageOwner.to_id = new TL_peerChat();
                                                        messageOwner.to_id.chat_id = chat_id;
                                                    } else {
                                                        messageOwner.to_id = new TL_peerUser();
                                                        messageOwner.to_id.user_id = user_id;
                                                    }
                                                    messageOwner.from_id = chat_from_id;
                                                    boolean z = mention || pinned;
                                                    messageOwner.mentioned = z;
                                                    MessageObject messageObject = new MessageObject(currentAccount, messageOwner, messageText, name, userName, localMessage, channel);
                                                    ArrayList<MessageObject> arrayList = new ArrayList();
                                                    arrayList.add(messageObject);
                                                    NotificationsController.getInstance(currentAccount).processNewMessages(arrayList, true, true, this.countDownLatch);
                                                } else {
                                                    this.countDownLatch.countDown();
                                                }
                                            } else {
                                                this.countDownLatch.countDown();
                                            }
                                        }
                                    }
                                    ConnectionsManager.onInternalPushReceived(currentAccount);
                                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                    jsonString = str;
                                    return;
                            }
                            e = th;
                            jsonString = str;
                            if (currentAccount != -1) {
                                ConnectionsManager.onInternalPushReceived(currentAccount);
                                ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                this.countDownLatch.countDown();
                            } else {
                                onDecryptError();
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("error in loc_key = " + loc_key + " json " + jsonString);
                            }
                            FileLog.e(e);
                            return;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("GCM ACCOUNT NOT ACTIVATED");
                        }
                        this.countDownLatch.countDown();
                        jsonString = str;
                        return;
                    }
                    onDecryptError();
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(String.format("GCM DECRYPT ERROR 3, key = %s", new Object[]{Utilities.bytesToHex(SharedConfig.pushAuthKey)}));
                    }
                    currentAccount = -1;
                    return;
                }
                onDecryptError();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(String.format(Locale.US, "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s", new Object[]{Utilities.bytesToHex(SharedConfig.pushAuthKeyId), Utilities.bytesToHex(inAuthKeyId), Utilities.bytesToHex(SharedConfig.pushAuthKey)}));
                }
                currentAccount = -1;
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("GCM DECRYPT ERROR 1");
            }
            onDecryptError();
            currentAccount = -1;
        } catch (Throwable th3) {
            e = th3;
            currentAccount = -1;
        }
    }

    private void onDecryptError() {
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(a);
                ConnectionsManager.getInstance(a).resumeNetworkMaybe();
            }
        }
        this.countDownLatch.countDown();
    }

    public void onNewToken(String token) {
        AndroidUtilities.runOnUIThread(new GcmPushListenerService$$Lambda$1(token));
    }

    static final /* synthetic */ void lambda$onNewToken$3$GcmPushListenerService(String token) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Refreshed token: " + token);
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(token);
    }

    public static void sendRegistrationToServer(String token) {
        Utilities.stageQueue.postRunnable(new GcmPushListenerService$$Lambda$2(token));
    }

    static final /* synthetic */ void lambda$sendRegistrationToServer$5$GcmPushListenerService(String token) {
        SharedConfig.pushString = token;
        for (int a = 0; a < 3; a++) {
            UserConfig userConfig = UserConfig.getInstance(a);
            userConfig.registeredForPush = false;
            userConfig.saveConfig(false);
            if (userConfig.getClientUserId() != 0) {
                AndroidUtilities.runOnUIThread(new GcmPushListenerService$$Lambda$3(a, token));
            }
        }
    }
}
