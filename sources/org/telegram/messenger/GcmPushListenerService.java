package org.telegram.messenger;

import android.text.TextUtils;
import android.util.Base64;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0559C;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
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

    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        final Map data = message.getData();
        final long time = message.getSentTime();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("GCM received data: " + data + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.GcmPushListenerService$1$1 */
            class C01891 implements Runnable {
                C01891() {
                }

                public void run() {
                    Throwable e;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("GCM START PROCESSING");
                    }
                    int currentAccount;
                    try {
                        Object value = data.get(TtmlNode.TAG_P);
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
                                    Object obj;
                                    int accountUserId;
                                    int a;
                                    byte[] strBytes = new byte[nativeByteBuffer.readInt32(true)];
                                    nativeByteBuffer.readBytes(strBytes, true);
                                    JSONObject json = new JSONObject(new String(strBytes, C0559C.UTF8_NAME));
                                    JSONObject custom = json.getJSONObject("custom");
                                    if (json.has("user_id")) {
                                        obj = json.get("user_id");
                                    } else {
                                        obj = null;
                                    }
                                    if (obj == null) {
                                        accountUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                    } else if (obj instanceof Integer) {
                                        accountUserId = ((Integer) obj).intValue();
                                    } else if (obj instanceof String) {
                                        accountUserId = Utilities.parseInt((String) obj).intValue();
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
                                    int accountFinal = account;
                                    try {
                                        if (UserConfig.getInstance(currentAccount).isClientActivated()) {
                                            String loc_key;
                                            if (json.has("loc_key")) {
                                                loc_key = json.getString("loc_key");
                                            } else {
                                                loc_key = TtmlNode.ANONYMOUS_REGION_ID;
                                            }
                                            Object obj2 = data.get("google.sent_time");
                                            Object obj3 = -1;
                                            switch (loc_key.hashCode()) {
                                                case -920689527:
                                                    if (loc_key.equals("DC_UPDATE")) {
                                                        obj3 = null;
                                                        break;
                                                    }
                                                    break;
                                                case 633004703:
                                                    if (loc_key.equals("MESSAGE_ANNOUNCEMENT")) {
                                                        obj3 = 1;
                                                        break;
                                                    }
                                                    break;
                                            }
                                            switch (obj3) {
                                                case null:
                                                    int dc = custom.getInt("dc");
                                                    String[] parts = custom.getString("addr").split(":");
                                                    if (parts.length == 2) {
                                                        ConnectionsManager.getInstance(currentAccount).applyDatacenterAddress(dc, parts[0], Integer.parseInt(parts[1]));
                                                        ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                                        return;
                                                    }
                                                    return;
                                                case 1:
                                                    TL_updateServiceNotification update = new TL_updateServiceNotification();
                                                    update.popup = false;
                                                    update.flags = 2;
                                                    update.inbox_date = (int) (time / 1000);
                                                    update.message = json.getString("message");
                                                    update.type = "announcement";
                                                    update.media = new TL_messageMediaEmpty();
                                                    TL_updates updates = new TL_updates();
                                                    updates.updates.add(update);
                                                    final int i = accountFinal;
                                                    final TL_updates tL_updates = updates;
                                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                                        public void run() {
                                                            MessagesController.getInstance(i).processUpdates(tL_updates, false);
                                                        }
                                                    });
                                                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
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
                                                        int badge;
                                                        if (json.has("badge")) {
                                                            badge = json.getInt("badge");
                                                        } else {
                                                            badge = 0;
                                                        }
                                                        if (badge != 0) {
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
                                                                if (json.has("loc_args")) {
                                                                    JSONArray loc_args = json.getJSONArray("loc_args");
                                                                    args = new String[loc_args.length()];
                                                                    for (a = 0; a < args.length; a++) {
                                                                        args[a] = loc_args.getString(a);
                                                                    }
                                                                } else {
                                                                    args = null;
                                                                }
                                                                String messageText = null;
                                                                String message = null;
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
                                                                    FileLog.m0d("GCM received message notification " + loc_key + " for dialogId = " + dialog_id + " mid = " + msg_id);
                                                                }
                                                                obj3 = -1;
                                                                switch (loc_key.hashCode()) {
                                                                    case -2091498420:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                            obj3 = 28;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -2053872415:
                                                                        if (loc_key.equals("CHAT_CREATED")) {
                                                                            obj3 = 50;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -2039746363:
                                                                        if (loc_key.equals("MESSAGE_STICKER")) {
                                                                            obj3 = 9;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1979538588:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_DOC")) {
                                                                            obj3 = 25;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1979536003:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_GEO")) {
                                                                            obj3 = 29;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1979535888:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_GIF")) {
                                                                            obj3 = 31;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1969004705:
                                                                        if (loc_key.equals("CHAT_ADD_MEMBER")) {
                                                                            obj3 = 53;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1946699248:
                                                                        if (loc_key.equals("CHAT_JOINED")) {
                                                                            obj3 = 59;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1528047021:
                                                                        if (loc_key.equals("CHAT_MESSAGES")) {
                                                                            obj3 = 62;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1493579426:
                                                                        if (loc_key.equals("MESSAGE_AUDIO")) {
                                                                            obj3 = 10;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1480102982:
                                                                        if (loc_key.equals("MESSAGE_PHOTO")) {
                                                                            obj3 = 2;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1478041834:
                                                                        if (loc_key.equals("MESSAGE_ROUND")) {
                                                                            obj3 = 7;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1474543101:
                                                                        if (loc_key.equals("MESSAGE_VIDEO")) {
                                                                            obj3 = 4;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1465695932:
                                                                        if (loc_key.equals("ENCRYPTION_ACCEPT")) {
                                                                            obj3 = 82;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1374906292:
                                                                        if (loc_key.equals("ENCRYPTED_MESSAGE")) {
                                                                            obj3 = 80;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1372940586:
                                                                        if (loc_key.equals("CHAT_RETURNED")) {
                                                                            obj3 = 58;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1264245338:
                                                                        if (loc_key.equals("PINNED_INVOICE")) {
                                                                            obj3 = 75;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1236086700:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                            obj3 = 33;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1236077786:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_GAME")) {
                                                                            obj3 = 32;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1235686303:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                            obj3 = 20;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1198046100:
                                                                        if (loc_key.equals("MESSAGE_VIDEO_SECRET")) {
                                                                            obj3 = 5;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1124254527:
                                                                        if (loc_key.equals("CHAT_MESSAGE_CONTACT")) {
                                                                            obj3 = 44;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1085137927:
                                                                        if (loc_key.equals("PINNED_GAME")) {
                                                                            obj3 = 74;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -1084746444:
                                                                        if (loc_key.equals("PINNED_TEXT")) {
                                                                            obj3 = 63;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -819729482:
                                                                        if (loc_key.equals("PINNED_STICKER")) {
                                                                            obj3 = 69;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -772141857:
                                                                        if (loc_key.equals("PHONE_CALL_REQUEST")) {
                                                                            obj3 = 84;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -638310039:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                            obj3 = 26;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -589196239:
                                                                        if (loc_key.equals("PINNED_DOC")) {
                                                                            obj3 = 68;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -589193654:
                                                                        if (loc_key.equals("PINNED_GEO")) {
                                                                            obj3 = 72;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -589193539:
                                                                        if (loc_key.equals("PINNED_GIF")) {
                                                                            obj3 = 76;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -440169325:
                                                                        if (loc_key.equals("AUTH_UNKNOWN")) {
                                                                            obj3 = 78;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -412748110:
                                                                        if (loc_key.equals("CHAT_DELETE_YOU")) {
                                                                            obj3 = 56;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -228518075:
                                                                        if (loc_key.equals("MESSAGE_GEOLIVE")) {
                                                                            obj3 = 13;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -213586509:
                                                                        if (loc_key.equals("ENCRYPTION_REQUEST")) {
                                                                            obj3 = 81;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -115582002:
                                                                        if (loc_key.equals("CHAT_MESSAGE_INVOICE")) {
                                                                            obj3 = 49;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -112621464:
                                                                        if (loc_key.equals("CONTACT_JOINED")) {
                                                                            obj3 = 77;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -108522133:
                                                                        if (loc_key.equals("AUTH_REGION")) {
                                                                            obj3 = 79;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -107572034:
                                                                        if (loc_key.equals("MESSAGE_SCREENSHOT")) {
                                                                            obj3 = 6;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case -40534265:
                                                                        if (loc_key.equals("CHAT_DELETE_MEMBER")) {
                                                                            obj3 = 55;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 65254746:
                                                                        if (loc_key.equals("CHAT_ADD_YOU")) {
                                                                            obj3 = 54;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 141040782:
                                                                        if (loc_key.equals("CHAT_LEFT")) {
                                                                            obj3 = 57;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 309993049:
                                                                        if (loc_key.equals("CHAT_MESSAGE_DOC")) {
                                                                            obj3 = 41;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 309995634:
                                                                        if (loc_key.equals("CHAT_MESSAGE_GEO")) {
                                                                            obj3 = 45;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 309995749:
                                                                        if (loc_key.equals("CHAT_MESSAGE_GIF")) {
                                                                            obj3 = 47;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 320532812:
                                                                        if (loc_key.equals("MESSAGES")) {
                                                                            obj3 = 19;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 328933854:
                                                                        if (loc_key.equals("CHAT_MESSAGE_STICKER")) {
                                                                            obj3 = 42;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 331340546:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                            obj3 = 27;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 344816990:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                            obj3 = 22;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 346878138:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                            obj3 = 24;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 350376871:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                            obj3 = 23;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 615714517:
                                                                        if (loc_key.equals("MESSAGE_PHOTO_SECRET")) {
                                                                            obj3 = 3;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 715508879:
                                                                        if (loc_key.equals("PINNED_AUDIO")) {
                                                                            obj3 = 70;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 728985323:
                                                                        if (loc_key.equals("PINNED_PHOTO")) {
                                                                            obj3 = 65;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 731046471:
                                                                        if (loc_key.equals("PINNED_ROUND")) {
                                                                            obj3 = 67;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 734545204:
                                                                        if (loc_key.equals("PINNED_VIDEO")) {
                                                                            obj3 = 66;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 802032552:
                                                                        if (loc_key.equals("MESSAGE_CONTACT")) {
                                                                            obj3 = 11;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 991498806:
                                                                        if (loc_key.equals("PINNED_GEOLIVE")) {
                                                                            obj3 = 73;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1019917311:
                                                                        if (loc_key.equals("CHAT_MESSAGE_FWDS")) {
                                                                            obj3 = 60;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1019926225:
                                                                        if (loc_key.equals("CHAT_MESSAGE_GAME")) {
                                                                            obj3 = 48;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1020317708:
                                                                        if (loc_key.equals("CHAT_MESSAGE_TEXT")) {
                                                                            obj3 = 36;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1060349560:
                                                                        if (loc_key.equals("MESSAGE_FWDS")) {
                                                                            obj3 = 17;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1060358474:
                                                                        if (loc_key.equals("MESSAGE_GAME")) {
                                                                            obj3 = 15;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1060749957:
                                                                        if (loc_key.equals("MESSAGE_TEXT")) {
                                                                            obj3 = null;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1073049781:
                                                                        if (loc_key.equals("PINNED_NOTEXT")) {
                                                                            obj3 = 64;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1078101399:
                                                                        if (loc_key.equals("CHAT_TITLE_EDITED")) {
                                                                            obj3 = 51;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1110103437:
                                                                        if (loc_key.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                            obj3 = 37;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1160762272:
                                                                        if (loc_key.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                            obj3 = 61;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1172918249:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                            obj3 = 30;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1281128640:
                                                                        if (loc_key.equals("MESSAGE_DOC")) {
                                                                            obj3 = 8;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1281131225:
                                                                        if (loc_key.equals("MESSAGE_GEO")) {
                                                                            obj3 = 12;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1281131340:
                                                                        if (loc_key.equals("MESSAGE_GIF")) {
                                                                            obj3 = 14;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1310789062:
                                                                        if (loc_key.equals("MESSAGE_NOTEXT")) {
                                                                            obj3 = 1;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1361447897:
                                                                        if (loc_key.equals("MESSAGE_PHOTOS")) {
                                                                            obj3 = 18;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1498266155:
                                                                        if (loc_key.equals("PHONE_CALL_MISSED")) {
                                                                            obj3 = 85;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1547988151:
                                                                        if (loc_key.equals("CHAT_MESSAGE_AUDIO")) {
                                                                            obj3 = 43;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1561464595:
                                                                        if (loc_key.equals("CHAT_MESSAGE_PHOTO")) {
                                                                            obj3 = 38;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1563525743:
                                                                        if (loc_key.equals("CHAT_MESSAGE_ROUND")) {
                                                                            obj3 = 40;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1567024476:
                                                                        if (loc_key.equals("CHAT_MESSAGE_VIDEO")) {
                                                                            obj3 = 39;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1810705077:
                                                                        if (loc_key.equals("MESSAGE_INVOICE")) {
                                                                            obj3 = 16;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1815177512:
                                                                        if (loc_key.equals("CHANNEL_MESSAGES")) {
                                                                            obj3 = 35;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 1963241394:
                                                                        if (loc_key.equals("LOCKED_MESSAGE")) {
                                                                            obj3 = 83;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 2014789757:
                                                                        if (loc_key.equals("CHAT_PHOTO_EDITED")) {
                                                                            obj3 = 52;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 2022049433:
                                                                        if (loc_key.equals("PINNED_CONTACT")) {
                                                                            obj3 = 71;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 2048733346:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                            obj3 = 21;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 2099392181:
                                                                        if (loc_key.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                            obj3 = 34;
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 2140162142:
                                                                        if (loc_key.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                            obj3 = 46;
                                                                            break;
                                                                        }
                                                                        break;
                                                                }
                                                                switch (obj3) {
                                                                    case null:
                                                                        messageText = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, args[0], args[1]);
                                                                        message = args[1];
                                                                        break;
                                                                    case 1:
                                                                        messageText = LocaleController.formatString("NotificationMessageNoText", R.string.NotificationMessageNoText, args[0]);
                                                                        message = LocaleController.getString("Message", R.string.Message);
                                                                        break;
                                                                    case 2:
                                                                        messageText = LocaleController.formatString("NotificationMessagePhoto", R.string.NotificationMessagePhoto, args[0]);
                                                                        message = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                                                                        break;
                                                                    case 3:
                                                                        messageText = LocaleController.formatString("NotificationMessageSDPhoto", R.string.NotificationMessageSDPhoto, args[0]);
                                                                        message = LocaleController.getString("AttachDestructingPhoto", R.string.AttachDestructingPhoto);
                                                                        break;
                                                                    case 4:
                                                                        messageText = LocaleController.formatString("NotificationMessageVideo", R.string.NotificationMessageVideo, args[0]);
                                                                        message = LocaleController.getString("AttachVideo", R.string.AttachVideo);
                                                                        break;
                                                                    case 5:
                                                                        messageText = LocaleController.formatString("NotificationMessageSDVideo", R.string.NotificationMessageSDVideo, args[0]);
                                                                        message = LocaleController.getString("AttachDestructingVideo", R.string.AttachDestructingVideo);
                                                                        break;
                                                                    case 6:
                                                                        messageText = LocaleController.getString("ActionTakeScreenshoot", R.string.ActionTakeScreenshoot).replace("un1", args[0]);
                                                                        break;
                                                                    case 7:
                                                                        messageText = LocaleController.formatString("NotificationMessageRound", R.string.NotificationMessageRound, args[0]);
                                                                        message = LocaleController.getString("AttachRound", R.string.AttachRound);
                                                                        break;
                                                                    case 8:
                                                                        messageText = LocaleController.formatString("NotificationMessageDocument", R.string.NotificationMessageDocument, args[0]);
                                                                        message = LocaleController.getString("AttachDocument", R.string.AttachDocument);
                                                                        break;
                                                                    case 9:
                                                                        if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                                                                            messageText = LocaleController.formatString("NotificationMessageStickerEmoji", R.string.NotificationMessageStickerEmoji, args[0], args[1]);
                                                                            message = args[1] + " " + LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                                                            break;
                                                                        }
                                                                        messageText = LocaleController.formatString("NotificationMessageSticker", R.string.NotificationMessageSticker, args[0]);
                                                                        message = LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                                                        break;
                                                                        break;
                                                                    case 10:
                                                                        messageText = LocaleController.formatString("NotificationMessageAudio", R.string.NotificationMessageAudio, args[0]);
                                                                        message = LocaleController.getString("AttachAudio", R.string.AttachAudio);
                                                                        break;
                                                                    case 11:
                                                                        messageText = LocaleController.formatString("NotificationMessageContact", R.string.NotificationMessageContact, args[0]);
                                                                        message = LocaleController.getString("AttachContact", R.string.AttachContact);
                                                                        break;
                                                                    case 12:
                                                                        messageText = LocaleController.formatString("NotificationMessageMap", R.string.NotificationMessageMap, args[0]);
                                                                        message = LocaleController.getString("AttachLocation", R.string.AttachLocation);
                                                                        break;
                                                                    case 13:
                                                                        messageText = LocaleController.formatString("NotificationMessageLiveLocation", R.string.NotificationMessageLiveLocation, args[0]);
                                                                        message = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                                                                        break;
                                                                    case 14:
                                                                        messageText = LocaleController.formatString("NotificationMessageGif", R.string.NotificationMessageGif, args[0]);
                                                                        message = LocaleController.getString("AttachGif", R.string.AttachGif);
                                                                        break;
                                                                    case 15:
                                                                        messageText = LocaleController.formatString("NotificationMessageGame", R.string.NotificationMessageGame, args[0]);
                                                                        message = LocaleController.getString("AttachGame", R.string.AttachGame);
                                                                        break;
                                                                    case 16:
                                                                        messageText = LocaleController.formatString("NotificationMessageInvoice", R.string.NotificationMessageInvoice, args[0], args[1]);
                                                                        message = LocaleController.getString("PaymentInvoice", R.string.PaymentInvoice);
                                                                        break;
                                                                    case 17:
                                                                        messageText = LocaleController.formatString("NotificationMessageForwardFew", R.string.NotificationMessageForwardFew, args[0], LocaleController.formatPluralString("messages", Utilities.parseInt(args[1]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 18:
                                                                        messageText = LocaleController.formatString("NotificationMessageFew", R.string.NotificationMessageFew, args[0], LocaleController.formatPluralString("Photos", Utilities.parseInt(args[1]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 19:
                                                                        messageText = LocaleController.formatString("NotificationMessageFew", R.string.NotificationMessageFew, args[0], LocaleController.formatPluralString("messages", Utilities.parseInt(args[1]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 20:
                                                                        messageText = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, args[0], args[1]);
                                                                        message = args[1];
                                                                        break;
                                                                    case 21:
                                                                        messageText = LocaleController.formatString("ChannelMessageNoText", R.string.ChannelMessageNoText, args[0]);
                                                                        message = LocaleController.getString("Message", R.string.Message);
                                                                        break;
                                                                    case 22:
                                                                        messageText = LocaleController.formatString("ChannelMessagePhoto", R.string.ChannelMessagePhoto, args[0]);
                                                                        message = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                                                                        break;
                                                                    case 23:
                                                                        messageText = LocaleController.formatString("ChannelMessageVideo", R.string.ChannelMessageVideo, args[0]);
                                                                        message = LocaleController.getString("AttachVideo", R.string.AttachVideo);
                                                                        break;
                                                                    case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                                                                        messageText = LocaleController.formatString("ChannelMessageRound", R.string.ChannelMessageRound, args[0]);
                                                                        message = LocaleController.getString("AttachRound", R.string.AttachRound);
                                                                        break;
                                                                    case 25:
                                                                        messageText = LocaleController.formatString("ChannelMessageDocument", R.string.ChannelMessageDocument, args[0]);
                                                                        message = LocaleController.getString("AttachDocument", R.string.AttachDocument);
                                                                        break;
                                                                    case 26:
                                                                        if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                                                                            messageText = LocaleController.formatString("ChannelMessageStickerEmoji", R.string.ChannelMessageStickerEmoji, args[0], args[1]);
                                                                            message = args[1] + " " + LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                                                            break;
                                                                        }
                                                                        messageText = LocaleController.formatString("ChannelMessageSticker", R.string.ChannelMessageSticker, args[0]);
                                                                        message = LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                                                        break;
                                                                        break;
                                                                    case 27:
                                                                        messageText = LocaleController.formatString("ChannelMessageAudio", R.string.ChannelMessageAudio, args[0]);
                                                                        message = LocaleController.getString("AttachAudio", R.string.AttachAudio);
                                                                        break;
                                                                    case 28:
                                                                        messageText = LocaleController.formatString("ChannelMessageContact", R.string.ChannelMessageContact, args[0]);
                                                                        message = LocaleController.getString("AttachContact", R.string.AttachContact);
                                                                        break;
                                                                    case 29:
                                                                        messageText = LocaleController.formatString("ChannelMessageMap", R.string.ChannelMessageMap, args[0]);
                                                                        message = LocaleController.getString("AttachLocation", R.string.AttachLocation);
                                                                        break;
                                                                    case 30:
                                                                        messageText = LocaleController.formatString("ChannelMessageLiveLocation", R.string.ChannelMessageLiveLocation, args[0]);
                                                                        message = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                                                                        break;
                                                                    case 31:
                                                                        messageText = LocaleController.formatString("ChannelMessageGIF", R.string.ChannelMessageGIF, args[0]);
                                                                        message = LocaleController.getString("AttachGif", R.string.AttachGif);
                                                                        break;
                                                                    case 32:
                                                                        messageText = LocaleController.formatString("NotificationMessageGame", R.string.NotificationMessageGame, args[0]);
                                                                        message = LocaleController.getString("AttachGame", R.string.AttachGame);
                                                                        break;
                                                                    case 33:
                                                                        messageText = LocaleController.formatString("ChannelMessageFew", R.string.ChannelMessageFew, args[0], LocaleController.formatPluralString("ForwardedMessageCount", Utilities.parseInt(args[1]).intValue()).toLowerCase());
                                                                        localMessage = true;
                                                                        break;
                                                                    case 34:
                                                                        messageText = LocaleController.formatString("ChannelMessageFew", R.string.ChannelMessageFew, args[0], LocaleController.formatPluralString("Photos", Utilities.parseInt(args[1]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 35:
                                                                        messageText = LocaleController.formatString("ChannelMessageFew", R.string.ChannelMessageFew, args[0], LocaleController.formatPluralString("messages", Utilities.parseInt(args[1]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case TsExtractor.TS_STREAM_TYPE_H265 /*36*/:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupText", R.string.NotificationMessageGroupText, args[0], args[1], args[2]);
                                                                        message = args[2];
                                                                        break;
                                                                    case 37:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupNoText", R.string.NotificationMessageGroupNoText, args[0], args[1]);
                                                                        message = LocaleController.getString("Message", R.string.Message);
                                                                        break;
                                                                    case 38:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupPhoto", R.string.NotificationMessageGroupPhoto, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
                                                                        break;
                                                                    case 39:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupVideo", R.string.NotificationMessageGroupVideo, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachVideo", R.string.AttachVideo);
                                                                        break;
                                                                    case 40:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupRound", R.string.NotificationMessageGroupRound, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachRound", R.string.AttachRound);
                                                                        break;
                                                                    case 41:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupDocument", R.string.NotificationMessageGroupDocument, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachDocument", R.string.AttachDocument);
                                                                        break;
                                                                    case 42:
                                                                        if (args.length > 2 && !TextUtils.isEmpty(args[2])) {
                                                                            messageText = LocaleController.formatString("NotificationMessageGroupStickerEmoji", R.string.NotificationMessageGroupStickerEmoji, args[0], args[1], args[2]);
                                                                            message = args[2] + " " + LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                                                            break;
                                                                        }
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupSticker", R.string.NotificationMessageGroupSticker, args[0], args[1]);
                                                                        message = args[1] + " " + LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                                                        break;
                                                                    case 43:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupAudio", R.string.NotificationMessageGroupAudio, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachAudio", R.string.AttachAudio);
                                                                        break;
                                                                    case 44:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupContact", R.string.NotificationMessageGroupContact, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachContact", R.string.AttachContact);
                                                                        break;
                                                                    case 45:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupMap", R.string.NotificationMessageGroupMap, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachLocation", R.string.AttachLocation);
                                                                        break;
                                                                    case 46:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupLiveLocation", R.string.NotificationMessageGroupLiveLocation, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                                                                        break;
                                                                    case 47:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupGif", R.string.NotificationMessageGroupGif, args[0], args[1]);
                                                                        message = LocaleController.getString("AttachGif", R.string.AttachGif);
                                                                        break;
                                                                    case 48:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupGame", R.string.NotificationMessageGroupGame, args[0], args[1], args[2]);
                                                                        message = LocaleController.getString("AttachGame", R.string.AttachGame);
                                                                        break;
                                                                    case 49:
                                                                        messageText = LocaleController.formatString("NotificationMessageGroupInvoice", R.string.NotificationMessageGroupInvoice, args[0], args[1], args[2]);
                                                                        message = LocaleController.getString("PaymentInvoice", R.string.PaymentInvoice);
                                                                        break;
                                                                    case 50:
                                                                        messageText = LocaleController.formatString("NotificationInvitedToGroup", R.string.NotificationInvitedToGroup, args[0], args[1]);
                                                                        break;
                                                                    case 51:
                                                                        messageText = LocaleController.formatString("NotificationEditedGroupName", R.string.NotificationEditedGroupName, args[0], args[1]);
                                                                        break;
                                                                    case 52:
                                                                        messageText = LocaleController.formatString("NotificationEditedGroupPhoto", R.string.NotificationEditedGroupPhoto, args[0], args[1]);
                                                                        break;
                                                                    case 53:
                                                                        messageText = LocaleController.formatString("NotificationGroupAddMember", R.string.NotificationGroupAddMember, args[0], args[1], args[2]);
                                                                        break;
                                                                    case 54:
                                                                        messageText = LocaleController.formatString("NotificationInvitedToGroup", R.string.NotificationInvitedToGroup, args[0], args[1]);
                                                                        break;
                                                                    case 55:
                                                                        messageText = LocaleController.formatString("NotificationGroupKickMember", R.string.NotificationGroupKickMember, args[0], args[1]);
                                                                        break;
                                                                    case 56:
                                                                        messageText = LocaleController.formatString("NotificationGroupKickYou", R.string.NotificationGroupKickYou, args[0], args[1]);
                                                                        break;
                                                                    case 57:
                                                                        messageText = LocaleController.formatString("NotificationGroupLeftMember", R.string.NotificationGroupLeftMember, args[0], args[1]);
                                                                        break;
                                                                    case 58:
                                                                        messageText = LocaleController.formatString("NotificationGroupAddSelf", R.string.NotificationGroupAddSelf, args[0], args[1]);
                                                                        break;
                                                                    case 59:
                                                                        messageText = LocaleController.formatString("NotificationGroupAddSelfMega", R.string.NotificationGroupAddSelfMega, args[0], args[1]);
                                                                        break;
                                                                    case 60:
                                                                        messageText = LocaleController.formatString("NotificationGroupForwardedFew", R.string.NotificationGroupForwardedFew, args[0], args[1], LocaleController.formatPluralString("messages", Utilities.parseInt(args[2]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 61:
                                                                        messageText = LocaleController.formatString("NotificationGroupFew", R.string.NotificationGroupFew, args[0], args[1], LocaleController.formatPluralString("Photos", Utilities.parseInt(args[2]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 62:
                                                                        messageText = LocaleController.formatString("NotificationGroupFew", R.string.NotificationGroupFew, args[0], args[1], LocaleController.formatPluralString("messages", Utilities.parseInt(args[2]).intValue()));
                                                                        localMessage = true;
                                                                        break;
                                                                    case 63:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, args[0], args[1]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, args[0], args[1], args[2]);
                                                                            break;
                                                                        }
                                                                    case 64:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedNoTextChannel", R.string.NotificationActionPinnedNoTextChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedNoText", R.string.NotificationActionPinnedNoText, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case VoIPService.CALL_MIN_LAYER /*65*/:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedPhotoChannel", R.string.NotificationActionPinnedPhotoChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedPhoto", R.string.NotificationActionPinnedPhoto, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 66:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedVideoChannel", R.string.NotificationActionPinnedVideoChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedVideo", R.string.NotificationActionPinnedVideo, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 67:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedRoundChannel", R.string.NotificationActionPinnedRoundChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedRound", R.string.NotificationActionPinnedRound, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 68:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedFileChannel", R.string.NotificationActionPinnedFileChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedFile", R.string.NotificationActionPinnedFile, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 69:
                                                                        if (chat_from_id == 0) {
                                                                            if (args.length > 1 && !TextUtils.isEmpty(args[1])) {
                                                                                messageText = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", R.string.NotificationActionPinnedStickerEmojiChannel, args[0], args[1]);
                                                                                break;
                                                                            } else {
                                                                                messageText = LocaleController.formatString("NotificationActionPinnedStickerChannel", R.string.NotificationActionPinnedStickerChannel, args[0]);
                                                                                break;
                                                                            }
                                                                        } else if (args.length > 2 && !TextUtils.isEmpty(args[2])) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedStickerEmoji", R.string.NotificationActionPinnedStickerEmoji, args[0], args[1], args[2]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedSticker", R.string.NotificationActionPinnedSticker, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                        break;
                                                                    case 70:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedVoiceChannel", R.string.NotificationActionPinnedVoiceChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedVoice", R.string.NotificationActionPinnedVoice, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 71:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedContactChannel", R.string.NotificationActionPinnedContactChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedContact", R.string.NotificationActionPinnedContact, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 72:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeoChannel", R.string.NotificationActionPinnedGeoChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeo", R.string.NotificationActionPinnedGeo, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*73*/:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", R.string.NotificationActionPinnedGeoLiveChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGeoLive", R.string.NotificationActionPinnedGeoLive, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case VoIPService.CALL_MAX_LAYER /*74*/:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGameChannel", R.string.NotificationActionPinnedGameChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGame", R.string.NotificationActionPinnedGame, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 75:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedInvoiceChannel", R.string.NotificationActionPinnedInvoiceChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedInvoice", R.string.NotificationActionPinnedInvoice, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 76:
                                                                        if (chat_from_id == 0) {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGifChannel", R.string.NotificationActionPinnedGifChannel, args[0]);
                                                                            break;
                                                                        } else {
                                                                            messageText = LocaleController.formatString("NotificationActionPinnedGif", R.string.NotificationActionPinnedGif, args[0], args[1]);
                                                                            break;
                                                                        }
                                                                    case 77:
                                                                    case 78:
                                                                    case 79:
                                                                    case 81:
                                                                    case TLRPC.LAYER /*82*/:
                                                                    case 83:
                                                                    case 84:
                                                                    case 85:
                                                                        break;
                                                                    case 80:
                                                                        messageText = LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
                                                                        name = LocaleController.getString("SecretChatName", R.string.SecretChatName);
                                                                        localMessage = true;
                                                                        break;
                                                                    default:
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            FileLog.m4w("unhandled loc_key = " + loc_key);
                                                                            break;
                                                                        }
                                                                        break;
                                                                }
                                                                if (messageText != null) {
                                                                    TL_message messageOwner = new TL_message();
                                                                    messageOwner.id = msg_id;
                                                                    messageOwner.random_id = random_id;
                                                                    if (message == null) {
                                                                        message = messageText;
                                                                    }
                                                                    messageOwner.message = message;
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
                                                                    messageOwner.mentioned = mention;
                                                                    MessageObject messageObject = new MessageObject(currentAccount, messageOwner, messageText, name, userName, localMessage, channel);
                                                                    ArrayList<MessageObject> arrayList = new ArrayList();
                                                                    arrayList.add(messageObject);
                                                                    NotificationsController.getInstance(currentAccount).processNewMessages(arrayList, true, true);
                                                                }
                                                            }
                                                        } else {
                                                            int max_id = custom.getInt("max_id");
                                                            ArrayList<Update> updates2 = new ArrayList();
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                FileLog.m0d("GCM received read notification max_id = " + max_id + " for dialogId = " + dialog_id);
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
                                                        }
                                                    }
                                                    ConnectionsManager.onInternalPushReceived(currentAccount);
                                                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                                    return;
                                            }
                                        } else if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("GCM ACCOUNT NOT ACTIVATED");
                                            return;
                                        } else {
                                            return;
                                        }
                                    } catch (Throwable th) {
                                        e = th;
                                        if (currentAccount == -1) {
                                            ConnectionsManager.onInternalPushReceived(currentAccount);
                                            ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                                        } else {
                                            GcmPushListenerService.this.onDecryptError();
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m1e("error in loc_key = " + null);
                                        }
                                        FileLog.m3e(e);
                                    }
                                }
                                GcmPushListenerService.this.onDecryptError();
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d(String.format("GCM DECRYPT ERROR 3, key = %s", new Object[]{Utilities.bytesToHex(SharedConfig.pushAuthKey)}));
                                }
                                currentAccount = -1;
                                return;
                            }
                            GcmPushListenerService.this.onDecryptError();
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d(String.format(Locale.US, "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s", new Object[]{Utilities.bytesToHex(SharedConfig.pushAuthKeyId), Utilities.bytesToHex(inAuthKeyId), Utilities.bytesToHex(SharedConfig.pushAuthKey)}));
                            }
                            currentAccount = -1;
                            return;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("GCM DECRYPT ERROR 1");
                        }
                        GcmPushListenerService.this.onDecryptError();
                        currentAccount = -1;
                    } catch (Throwable th2) {
                        e = th2;
                        currentAccount = -1;
                        if (currentAccount == -1) {
                            GcmPushListenerService.this.onDecryptError();
                        } else {
                            ConnectionsManager.onInternalPushReceived(currentAccount);
                            ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m1e("error in loc_key = " + null);
                        }
                        FileLog.m3e(e);
                    }
                }
            }

            public void run() {
                ApplicationLoader.postInitApplication();
                Utilities.stageQueue.postRunnable(new C01891());
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
