package org.telegram.messenger;

import android.util.Base64;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updates;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;

    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        final Map data = remoteMessage.getData();
        final long sentTime = remoteMessage.getSentTime();
        if (BuildVars.LOGS_ENABLED != null) {
            remoteMessage = new StringBuilder();
            remoteMessage.append("GCM received data: ");
            remoteMessage.append(data);
            remoteMessage.append(" from: ");
            remoteMessage.append(from);
            FileLog.m0d(remoteMessage.toString());
        }
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.GcmPushListenerService$1$1 */
            class C01821 implements Runnable {
                C01821() {
                }

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    String string;
                    Throwable th;
                    Throwable th2;
                    int i;
                    StringBuilder stringBuilder;
                    int i2;
                    int i3;
                    try {
                        Object obj = data.get(TtmlNode.TAG_P);
                        if (obj instanceof String) {
                            Object computeSHA1;
                            byte[] decode = Base64.decode((String) obj, 8);
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(decode.length);
                            nativeByteBuffer.writeBytes(decode);
                            nativeByteBuffer.position(0);
                            if (SharedConfig.pushAuthKeyId == null) {
                                SharedConfig.pushAuthKeyId = new byte[8];
                                computeSHA1 = Utilities.computeSHA1(SharedConfig.pushAuthKey);
                                System.arraycopy(computeSHA1, computeSHA1.length - 8, SharedConfig.pushAuthKeyId, 0, 8);
                            }
                            byte[] bArr = new byte[8];
                            nativeByteBuffer.readBytes(bArr, true);
                            if (Arrays.equals(SharedConfig.pushAuthKeyId, bArr)) {
                                bArr = new byte[16];
                                nativeByteBuffer.readBytes(bArr, true);
                                MessageKeyData generateMessageKeyData = MessageKeyData.generateMessageKeyData(SharedConfig.pushAuthKey, bArr, true, 2);
                                Utilities.aesIgeEncryption(nativeByteBuffer.buffer, generateMessageKeyData.aesKey, generateMessageKeyData.aesIv, false, false, 24, decode.length - 24);
                                if (Utilities.arraysEquals(bArr, 0, Utilities.computeSHA256(SharedConfig.pushAuthKey, 96, 32, nativeByteBuffer.buffer, 24, nativeByteBuffer.buffer.limit()), 8)) {
                                    int clientUserId;
                                    int i4;
                                    decode = new byte[nativeByteBuffer.readInt32(true)];
                                    nativeByteBuffer.readBytes(decode, true);
                                    JSONObject jSONObject = new JSONObject(new String(decode, C0542C.UTF8_NAME));
                                    JSONObject jSONObject2 = jSONObject.getJSONObject("custom");
                                    computeSHA1 = jSONObject.has("user_id") ? jSONObject.get("user_id") : null;
                                    if (computeSHA1 == null) {
                                        clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                    } else if (computeSHA1 instanceof Integer) {
                                        clientUserId = ((Integer) computeSHA1).intValue();
                                    } else if (computeSHA1 instanceof String) {
                                        clientUserId = Utilities.parseInt((String) computeSHA1).intValue();
                                    } else {
                                        clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                                    }
                                    i3 = UserConfig.selectedAccount;
                                    for (i4 = 0; i4 < 3; i4++) {
                                        if (UserConfig.getInstance(i4).getClientUserId() == clientUserId) {
                                            i3 = i4;
                                            break;
                                        }
                                    }
                                    try {
                                        if (UserConfig.getInstance(i3).isClientActivated()) {
                                            if (jSONObject.has("loc_key")) {
                                                try {
                                                    string = jSONObject.getString("loc_key");
                                                } catch (Throwable th22) {
                                                    th = th22;
                                                    i = -1;
                                                    string = null;
                                                    if (i3 != i) {
                                                        GcmPushListenerService.this.onDecryptError();
                                                    } else {
                                                        ConnectionsManager.onInternalPushReceived(i3);
                                                        ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                    }
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("error in loc_key = ");
                                                        stringBuilder.append(string);
                                                        FileLog.m1e(stringBuilder.toString());
                                                    }
                                                    FileLog.m3e(th);
                                                }
                                            }
                                            string = TtmlNode.ANONYMOUS_REGION_ID;
                                            try {
                                                boolean z;
                                                String[] split;
                                                TL_updateServiceNotification tL_updateServiceNotification;
                                                final TL_updates tL_updates;
                                                long j;
                                                int i5;
                                                int i6;
                                                Integer num;
                                                int i7;
                                                boolean z2;
                                                JSONArray jSONArray;
                                                String[] strArr;
                                                int i8;
                                                int i9;
                                                String str;
                                                String str2;
                                                String str3;
                                                boolean z3;
                                                Object obj2;
                                                StringBuilder stringBuilder2;
                                                Object obj3;
                                                ArrayList arrayList;
                                                StringBuilder stringBuilder3;
                                                TL_updateReadChannelInbox tL_updateReadChannelInbox;
                                                TL_updateReadHistoryInbox tL_updateReadHistoryInbox;
                                                data.get("google.sent_time");
                                                i4 = string.hashCode();
                                                if (i4 != -920689527) {
                                                    if (i4 == 633004703) {
                                                        if (string.equals("MESSAGE_ANNOUNCEMENT")) {
                                                            z = true;
                                                            switch (z) {
                                                                case false:
                                                                    i = jSONObject2.getInt("dc");
                                                                    split = jSONObject2.getString("addr").split(":");
                                                                    if (split.length != 2) {
                                                                        ConnectionsManager.getInstance(i3).applyDatacenterAddress(i, split[0], Integer.parseInt(split[1]));
                                                                        ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                        return;
                                                                    }
                                                                    return;
                                                                case true:
                                                                    tL_updateServiceNotification = new TL_updateServiceNotification();
                                                                    tL_updateServiceNotification.popup = false;
                                                                    tL_updateServiceNotification.flags = 2;
                                                                    tL_updateServiceNotification.inbox_date = (int) (sentTime / 1000);
                                                                    tL_updateServiceNotification.message = jSONObject.getString("message");
                                                                    tL_updateServiceNotification.type = "announcement";
                                                                    tL_updateServiceNotification.media = new TL_messageMediaEmpty();
                                                                    tL_updates = new TL_updates();
                                                                    tL_updates.updates.add(tL_updateServiceNotification);
                                                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                                                        public void run() {
                                                                            MessagesController.getInstance(i3).processUpdates(tL_updates, false);
                                                                        }
                                                                    });
                                                                    ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                    return;
                                                                default:
                                                                    j = 0;
                                                                    if (jSONObject2.has("channel_id")) {
                                                                        i4 = jSONObject2.getInt("channel_id");
                                                                        j = (long) (-i4);
                                                                    } else {
                                                                        i4 = 0;
                                                                    }
                                                                    if (jSONObject2.has("from_id")) {
                                                                        i5 = jSONObject2.getInt("from_id");
                                                                        j = (long) i5;
                                                                    } else {
                                                                        i5 = 0;
                                                                    }
                                                                    if (jSONObject2.has("chat_id")) {
                                                                        i6 = jSONObject2.getInt("chat_id");
                                                                        j = (long) (-i6);
                                                                    } else {
                                                                        i6 = 0;
                                                                    }
                                                                    if (j == 0) {
                                                                        if ((jSONObject.has("badge") ? jSONObject.getInt("badge") : 0) != 0) {
                                                                            i = jSONObject2.getInt("msg_id");
                                                                            num = (Integer) MessagesController.getInstance(i3).dialogs_read_inbox_max.get(Long.valueOf(j));
                                                                            if (num == null) {
                                                                                num = Integer.valueOf(MessagesStorage.getInstance(i3).getDialogReadMax(false, j));
                                                                                MessagesController.getInstance(i3).dialogs_read_inbox_max.put(Long.valueOf(j), num);
                                                                            }
                                                                            if (i <= num.intValue()) {
                                                                                i7 = jSONObject2.has("chat_from_id") ? jSONObject2.getInt("chat_from_id") : 0;
                                                                                z2 = jSONObject2.has("mention") && jSONObject2.getInt("mention") != 0;
                                                                                if (jSONObject.has("loc_args")) {
                                                                                    jSONArray = jSONObject.getJSONArray("loc_args");
                                                                                    strArr = new String[jSONArray.length()];
                                                                                    for (i8 = 0; i8 < strArr.length; i8++) {
                                                                                        strArr[i8] = jSONArray.getString(i8);
                                                                                    }
                                                                                    i9 = 0;
                                                                                } else {
                                                                                    i9 = 0;
                                                                                    strArr = null;
                                                                                }
                                                                                str = strArr[i9];
                                                                                if (!string.startsWith("CHAT_")) {
                                                                                    obj = i4 != 0 ? 1 : null;
                                                                                    str2 = str;
                                                                                    str3 = strArr[1];
                                                                                    z3 = false;
                                                                                    obj2 = obj;
                                                                                    obj = null;
                                                                                } else if (!string.startsWith("PINNED_")) {
                                                                                    str3 = str;
                                                                                    str2 = null;
                                                                                    z3 = false;
                                                                                    obj2 = i7 != 0 ? 1 : null;
                                                                                    obj = 1;
                                                                                } else if (string.startsWith("CHANNEL_")) {
                                                                                    str3 = str;
                                                                                    obj = null;
                                                                                    obj2 = null;
                                                                                    str2 = null;
                                                                                    z3 = true;
                                                                                } else {
                                                                                    str3 = str;
                                                                                    obj = null;
                                                                                    obj2 = null;
                                                                                    str2 = null;
                                                                                    z3 = false;
                                                                                }
                                                                                if (BuildVars.LOGS_ENABLED) {
                                                                                    try {
                                                                                        stringBuilder2 = new StringBuilder();
                                                                                        i2 = i3;
                                                                                        try {
                                                                                            stringBuilder2.append("GCM received message notification ");
                                                                                            stringBuilder2.append(string);
                                                                                            stringBuilder2.append(" for dialogId = ");
                                                                                            stringBuilder2.append(j);
                                                                                            stringBuilder2.append(" mid = ");
                                                                                            stringBuilder2.append(i);
                                                                                            i3 = stringBuilder2.toString();
                                                                                            FileLog.m0d(i3);
                                                                                        } catch (Throwable th222) {
                                                                                            th = th222;
                                                                                            i3 = i2;
                                                                                            i = -1;
                                                                                            if (i3 != i) {
                                                                                                ConnectionsManager.onInternalPushReceived(i3);
                                                                                                ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                                            } else {
                                                                                                GcmPushListenerService.this.onDecryptError();
                                                                                            }
                                                                                            if (BuildVars.LOGS_ENABLED) {
                                                                                                stringBuilder = new StringBuilder();
                                                                                                stringBuilder.append("error in loc_key = ");
                                                                                                stringBuilder.append(string);
                                                                                                FileLog.m1e(stringBuilder.toString());
                                                                                            }
                                                                                            FileLog.m3e(th);
                                                                                        }
                                                                                    } catch (Throwable th3) {
                                                                                        th222 = th3;
                                                                                        i2 = i3;
                                                                                        th = th222;
                                                                                        i = -1;
                                                                                        if (i3 != i) {
                                                                                            ConnectionsManager.onInternalPushReceived(i3);
                                                                                            ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                                        } else {
                                                                                            GcmPushListenerService.this.onDecryptError();
                                                                                        }
                                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                                            stringBuilder = new StringBuilder();
                                                                                            stringBuilder.append("error in loc_key = ");
                                                                                            stringBuilder.append(string);
                                                                                            FileLog.m1e(stringBuilder.toString());
                                                                                        }
                                                                                        FileLog.m3e(th);
                                                                                    }
                                                                                }
                                                                                i2 = i3;
                                                                                switch (string.hashCode()) {
                                                                                    case -2091498420:
                                                                                        if (string.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                            obj3 = 28;
                                                                                            break;
                                                                                        }
                                                                                    case -2053872415:
                                                                                        if (string.equals("CHAT_CREATED")) {
                                                                                            obj3 = 50;
                                                                                            break;
                                                                                        }
                                                                                    case -2039746363:
                                                                                        if (string.equals("MESSAGE_STICKER")) {
                                                                                            obj3 = 9;
                                                                                            break;
                                                                                        }
                                                                                    case -1979538588:
                                                                                        if (string.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                            obj3 = 25;
                                                                                            break;
                                                                                        }
                                                                                    case -1979536003:
                                                                                        if (string.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                            obj3 = 29;
                                                                                            break;
                                                                                        }
                                                                                    case -1979535888:
                                                                                        if (string.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                            obj3 = 31;
                                                                                            break;
                                                                                        }
                                                                                    case -1969004705:
                                                                                        if (string.equals("CHAT_ADD_MEMBER")) {
                                                                                            obj3 = 53;
                                                                                            break;
                                                                                        }
                                                                                    case -1946699248:
                                                                                        if (string.equals("CHAT_JOINED")) {
                                                                                            obj3 = 59;
                                                                                            break;
                                                                                        }
                                                                                    case -1528047021:
                                                                                        if (string.equals("CHAT_MESSAGES")) {
                                                                                            obj3 = 62;
                                                                                            break;
                                                                                        }
                                                                                    case -1493579426:
                                                                                        if (string.equals("MESSAGE_AUDIO")) {
                                                                                            obj3 = 10;
                                                                                            break;
                                                                                        }
                                                                                    case -1480102982:
                                                                                        if (string.equals("MESSAGE_PHOTO")) {
                                                                                            obj3 = 2;
                                                                                            break;
                                                                                        }
                                                                                    case -1478041834:
                                                                                        if (string.equals("MESSAGE_ROUND")) {
                                                                                            obj3 = 7;
                                                                                            break;
                                                                                        }
                                                                                    case -1474543101:
                                                                                        if (string.equals("MESSAGE_VIDEO")) {
                                                                                            obj3 = 4;
                                                                                            break;
                                                                                        }
                                                                                    case -1465695932:
                                                                                        if (string.equals("ENCRYPTION_ACCEPT")) {
                                                                                            obj3 = 81;
                                                                                            break;
                                                                                        }
                                                                                    case -1374906292:
                                                                                        if (string.equals("ENCRYPTED_MESSAGE")) {
                                                                                            obj3 = 82;
                                                                                            break;
                                                                                        }
                                                                                    case -1372940586:
                                                                                        if (string.equals("CHAT_RETURNED")) {
                                                                                            obj3 = 58;
                                                                                            break;
                                                                                        }
                                                                                    case -1264245338:
                                                                                        if (string.equals("PINNED_INVOICE")) {
                                                                                            obj3 = 75;
                                                                                            break;
                                                                                        }
                                                                                    case -1236086700:
                                                                                        if (string.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                            obj3 = 33;
                                                                                            break;
                                                                                        }
                                                                                    case -1236077786:
                                                                                        if (string.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                            obj3 = 32;
                                                                                            break;
                                                                                        }
                                                                                    case -1235686303:
                                                                                        if (string.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                            obj3 = 20;
                                                                                            break;
                                                                                        }
                                                                                    case -1198046100:
                                                                                        if (string.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                            obj3 = 5;
                                                                                            break;
                                                                                        }
                                                                                    case -1124254527:
                                                                                        if (string.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                            obj3 = 44;
                                                                                            break;
                                                                                        }
                                                                                    case -1085137927:
                                                                                        if (string.equals("PINNED_GAME")) {
                                                                                            obj3 = 74;
                                                                                            break;
                                                                                        }
                                                                                    case -1084746444:
                                                                                        if (string.equals("PINNED_TEXT")) {
                                                                                            obj3 = 63;
                                                                                            break;
                                                                                        }
                                                                                    case -819729482:
                                                                                        if (string.equals("PINNED_STICKER")) {
                                                                                            obj3 = 69;
                                                                                            break;
                                                                                        }
                                                                                    case -772141857:
                                                                                        if (string.equals("PHONE_CALL_REQUEST")) {
                                                                                            obj3 = 84;
                                                                                            break;
                                                                                        }
                                                                                    case -638310039:
                                                                                        if (string.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                            obj3 = 26;
                                                                                            break;
                                                                                        }
                                                                                    case -589196239:
                                                                                        if (string.equals("PINNED_DOC")) {
                                                                                            obj3 = 68;
                                                                                            break;
                                                                                        }
                                                                                    case -589193654:
                                                                                        if (string.equals("PINNED_GEO")) {
                                                                                            obj3 = 72;
                                                                                            break;
                                                                                        }
                                                                                    case -589193539:
                                                                                        if (string.equals("PINNED_GIF")) {
                                                                                            obj3 = 76;
                                                                                            break;
                                                                                        }
                                                                                    case -440169325:
                                                                                        if (string.equals("AUTH_UNKNOWN")) {
                                                                                            obj3 = 78;
                                                                                            break;
                                                                                        }
                                                                                    case -412748110:
                                                                                        if (string.equals("CHAT_DELETE_YOU")) {
                                                                                            obj3 = 56;
                                                                                            break;
                                                                                        }
                                                                                    case -228518075:
                                                                                        if (string.equals("MESSAGE_GEOLIVE")) {
                                                                                            obj3 = 13;
                                                                                            break;
                                                                                        }
                                                                                    case -213586509:
                                                                                        if (string.equals("ENCRYPTION_REQUEST")) {
                                                                                            obj3 = 80;
                                                                                            break;
                                                                                        }
                                                                                    case -115582002:
                                                                                        if (string.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                            obj3 = 49;
                                                                                            break;
                                                                                        }
                                                                                    case -112621464:
                                                                                        if (string.equals("CONTACT_JOINED")) {
                                                                                            obj3 = 77;
                                                                                            break;
                                                                                        }
                                                                                    case -108522133:
                                                                                        if (string.equals("AUTH_REGION")) {
                                                                                            obj3 = 79;
                                                                                            break;
                                                                                        }
                                                                                    case -107572034:
                                                                                        if (string.equals("MESSAGE_SCREENSHOT")) {
                                                                                            obj3 = 6;
                                                                                            break;
                                                                                        }
                                                                                    case -40534265:
                                                                                        if (string.equals("CHAT_DELETE_MEMBER")) {
                                                                                            obj3 = 55;
                                                                                            break;
                                                                                        }
                                                                                    case 65254746:
                                                                                        if (string.equals("CHAT_ADD_YOU")) {
                                                                                            obj3 = 54;
                                                                                            break;
                                                                                        }
                                                                                    case 141040782:
                                                                                        if (string.equals("CHAT_LEFT")) {
                                                                                            obj3 = 57;
                                                                                            break;
                                                                                        }
                                                                                    case 309993049:
                                                                                        if (string.equals("CHAT_MESSAGE_DOC")) {
                                                                                            obj3 = 41;
                                                                                            break;
                                                                                        }
                                                                                    case 309995634:
                                                                                        if (string.equals("CHAT_MESSAGE_GEO")) {
                                                                                            obj3 = 45;
                                                                                            break;
                                                                                        }
                                                                                    case 309995749:
                                                                                        if (string.equals("CHAT_MESSAGE_GIF")) {
                                                                                            obj3 = 47;
                                                                                            break;
                                                                                        }
                                                                                    case 320532812:
                                                                                        if (string.equals("MESSAGES")) {
                                                                                            obj3 = 19;
                                                                                            break;
                                                                                        }
                                                                                    case 328933854:
                                                                                        if (string.equals("CHAT_MESSAGE_STICKER")) {
                                                                                            obj3 = 42;
                                                                                            break;
                                                                                        }
                                                                                    case 331340546:
                                                                                        if (string.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                            obj3 = 27;
                                                                                            break;
                                                                                        }
                                                                                    case 344816990:
                                                                                        if (string.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                            obj3 = 22;
                                                                                            break;
                                                                                        }
                                                                                    case 346878138:
                                                                                        if (string.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                            obj3 = 24;
                                                                                            break;
                                                                                        }
                                                                                    case 350376871:
                                                                                        if (string.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                            obj3 = 23;
                                                                                            break;
                                                                                        }
                                                                                    case 615714517:
                                                                                        if (string.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                            obj3 = 3;
                                                                                            break;
                                                                                        }
                                                                                    case 715508879:
                                                                                        if (string.equals("PINNED_AUDIO")) {
                                                                                            obj3 = 70;
                                                                                            break;
                                                                                        }
                                                                                    case 728985323:
                                                                                        if (string.equals("PINNED_PHOTO")) {
                                                                                            obj3 = 65;
                                                                                            break;
                                                                                        }
                                                                                    case 731046471:
                                                                                        if (string.equals("PINNED_ROUND")) {
                                                                                            obj3 = 67;
                                                                                            break;
                                                                                        }
                                                                                    case 734545204:
                                                                                        if (string.equals("PINNED_VIDEO")) {
                                                                                            obj3 = 66;
                                                                                            break;
                                                                                        }
                                                                                    case 802032552:
                                                                                        if (string.equals("MESSAGE_CONTACT")) {
                                                                                            obj3 = 11;
                                                                                            break;
                                                                                        }
                                                                                    case 991498806:
                                                                                        if (string.equals("PINNED_GEOLIVE")) {
                                                                                            obj3 = 73;
                                                                                            break;
                                                                                        }
                                                                                    case 1019917311:
                                                                                        if (string.equals("CHAT_MESSAGE_FWDS")) {
                                                                                            obj3 = 60;
                                                                                            break;
                                                                                        }
                                                                                    case 1019926225:
                                                                                        if (string.equals("CHAT_MESSAGE_GAME")) {
                                                                                            obj3 = 48;
                                                                                            break;
                                                                                        }
                                                                                    case 1020317708:
                                                                                        if (string.equals("CHAT_MESSAGE_TEXT")) {
                                                                                            obj3 = 36;
                                                                                            break;
                                                                                        }
                                                                                    case 1060349560:
                                                                                        if (string.equals("MESSAGE_FWDS")) {
                                                                                            obj3 = 17;
                                                                                            break;
                                                                                        }
                                                                                    case 1060358474:
                                                                                        if (string.equals("MESSAGE_GAME")) {
                                                                                            obj3 = 15;
                                                                                            break;
                                                                                        }
                                                                                    case 1060749957:
                                                                                        if (string.equals("MESSAGE_TEXT")) {
                                                                                            obj3 = null;
                                                                                            break;
                                                                                        }
                                                                                    case 1073049781:
                                                                                        if (string.equals("PINNED_NOTEXT")) {
                                                                                            obj3 = 64;
                                                                                            break;
                                                                                        }
                                                                                    case 1078101399:
                                                                                        if (string.equals("CHAT_TITLE_EDITED")) {
                                                                                            obj3 = 51;
                                                                                            break;
                                                                                        }
                                                                                    case 1110103437:
                                                                                        if (string.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                            obj3 = 37;
                                                                                            break;
                                                                                        }
                                                                                    case 1160762272:
                                                                                        if (string.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                            obj3 = 61;
                                                                                            break;
                                                                                        }
                                                                                    case 1172918249:
                                                                                        if (string.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                            obj3 = 30;
                                                                                            break;
                                                                                        }
                                                                                    case 1281128640:
                                                                                        if (string.equals("MESSAGE_DOC")) {
                                                                                            obj3 = 8;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131225:
                                                                                        if (string.equals("MESSAGE_GEO")) {
                                                                                            obj3 = 12;
                                                                                            break;
                                                                                        }
                                                                                    case 1281131340:
                                                                                        if (string.equals("MESSAGE_GIF")) {
                                                                                            obj3 = 14;
                                                                                            break;
                                                                                        }
                                                                                    case 1310789062:
                                                                                        if (string.equals("MESSAGE_NOTEXT")) {
                                                                                            obj3 = 1;
                                                                                            break;
                                                                                        }
                                                                                    case 1361447897:
                                                                                        if (string.equals("MESSAGE_PHOTOS")) {
                                                                                            obj3 = 18;
                                                                                            break;
                                                                                        }
                                                                                    case 1498266155:
                                                                                        if (string.equals("PHONE_CALL_MISSED")) {
                                                                                            obj3 = 85;
                                                                                            break;
                                                                                        }
                                                                                    case 1547988151:
                                                                                        if (string.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                            obj3 = 43;
                                                                                            break;
                                                                                        }
                                                                                    case 1561464595:
                                                                                        if (string.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                            obj3 = 38;
                                                                                            break;
                                                                                        }
                                                                                    case 1563525743:
                                                                                        if (string.equals("CHAT_MESSAGE_ROUND")) {
                                                                                            obj3 = 40;
                                                                                            break;
                                                                                        }
                                                                                    case 1567024476:
                                                                                        if (string.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                            obj3 = 39;
                                                                                            break;
                                                                                        }
                                                                                    case 1810705077:
                                                                                        if (string.equals("MESSAGE_INVOICE")) {
                                                                                            obj3 = 16;
                                                                                            break;
                                                                                        }
                                                                                    case 1815177512:
                                                                                        if (string.equals("CHANNEL_MESSAGES")) {
                                                                                            obj3 = 35;
                                                                                            break;
                                                                                        }
                                                                                    case 1963241394:
                                                                                        if (string.equals("LOCKED_MESSAGE")) {
                                                                                            obj3 = 83;
                                                                                            break;
                                                                                        }
                                                                                    case 2014789757:
                                                                                        if (string.equals("CHAT_PHOTO_EDITED")) {
                                                                                            obj3 = 52;
                                                                                            break;
                                                                                        }
                                                                                    case 2022049433:
                                                                                        if (string.equals("PINNED_CONTACT")) {
                                                                                            obj3 = 71;
                                                                                            break;
                                                                                        }
                                                                                    case 2048733346:
                                                                                        if (string.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                            obj3 = 21;
                                                                                            break;
                                                                                        }
                                                                                    case 2099392181:
                                                                                        if (string.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                            obj3 = 34;
                                                                                            break;
                                                                                        }
                                                                                    case 2140162142:
                                                                                        if (string.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                            obj3 = 46;
                                                                                            break;
                                                                                        }
                                                                                        obj3 = -1;
                                                                                        break;
                                                                                    default:
                                                                                }
                                                                            } else {
                                                                                return;
                                                                            }
                                                                        }
                                                                        i = jSONObject2.getInt("max_id");
                                                                        arrayList = new ArrayList();
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            stringBuilder3 = new StringBuilder();
                                                                            stringBuilder3.append("GCM received read notification max_id = ");
                                                                            stringBuilder3.append(i);
                                                                            stringBuilder3.append(" for dialogId = ");
                                                                            stringBuilder3.append(j);
                                                                            FileLog.m0d(stringBuilder3.toString());
                                                                        }
                                                                        if (i4 != 0) {
                                                                            tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                                                                            tL_updateReadChannelInbox.channel_id = i4;
                                                                            tL_updateReadChannelInbox.max_id = i;
                                                                            arrayList.add(tL_updateReadChannelInbox);
                                                                        } else {
                                                                            tL_updateReadHistoryInbox = new TL_updateReadHistoryInbox();
                                                                            if (i5 != 0) {
                                                                                tL_updateReadHistoryInbox.peer = new TL_peerUser();
                                                                                tL_updateReadHistoryInbox.peer.user_id = i5;
                                                                            } else {
                                                                                tL_updateReadHistoryInbox.peer = new TL_peerChat();
                                                                                tL_updateReadHistoryInbox.peer.chat_id = i6;
                                                                            }
                                                                            tL_updateReadHistoryInbox.max_id = i;
                                                                            arrayList.add(tL_updateReadHistoryInbox);
                                                                        }
                                                                        MessagesController.getInstance(i3).processUpdateArray(arrayList, null, null, false);
                                                                        ConnectionsManager.onInternalPushReceived(i3);
                                                                        ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                    } else {
                                                                        return;
                                                                    }
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                } else if (string.equals("DC_UPDATE")) {
                                                    z = false;
                                                    switch (z) {
                                                        case false:
                                                            i = jSONObject2.getInt("dc");
                                                            split = jSONObject2.getString("addr").split(":");
                                                            if (split.length != 2) {
                                                                ConnectionsManager.getInstance(i3).applyDatacenterAddress(i, split[0], Integer.parseInt(split[1]));
                                                                ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                return;
                                                            }
                                                            return;
                                                        case true:
                                                            tL_updateServiceNotification = new TL_updateServiceNotification();
                                                            tL_updateServiceNotification.popup = false;
                                                            tL_updateServiceNotification.flags = 2;
                                                            tL_updateServiceNotification.inbox_date = (int) (sentTime / 1000);
                                                            tL_updateServiceNotification.message = jSONObject.getString("message");
                                                            tL_updateServiceNotification.type = "announcement";
                                                            tL_updateServiceNotification.media = new TL_messageMediaEmpty();
                                                            tL_updates = new TL_updates();
                                                            tL_updates.updates.add(tL_updateServiceNotification);
                                                            Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                            ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                            return;
                                                        default:
                                                            j = 0;
                                                            if (jSONObject2.has("channel_id")) {
                                                                i4 = 0;
                                                            } else {
                                                                i4 = jSONObject2.getInt("channel_id");
                                                                j = (long) (-i4);
                                                            }
                                                            if (jSONObject2.has("from_id")) {
                                                                i5 = 0;
                                                            } else {
                                                                i5 = jSONObject2.getInt("from_id");
                                                                j = (long) i5;
                                                            }
                                                            if (jSONObject2.has("chat_id")) {
                                                                i6 = 0;
                                                            } else {
                                                                i6 = jSONObject2.getInt("chat_id");
                                                                j = (long) (-i6);
                                                            }
                                                            if (j == 0) {
                                                                if (jSONObject.has("badge")) {
                                                                }
                                                                if ((jSONObject.has("badge") ? jSONObject.getInt("badge") : 0) != 0) {
                                                                    i = jSONObject2.getInt("max_id");
                                                                    arrayList = new ArrayList();
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        stringBuilder3 = new StringBuilder();
                                                                        stringBuilder3.append("GCM received read notification max_id = ");
                                                                        stringBuilder3.append(i);
                                                                        stringBuilder3.append(" for dialogId = ");
                                                                        stringBuilder3.append(j);
                                                                        FileLog.m0d(stringBuilder3.toString());
                                                                    }
                                                                    if (i4 != 0) {
                                                                        tL_updateReadHistoryInbox = new TL_updateReadHistoryInbox();
                                                                        if (i5 != 0) {
                                                                            tL_updateReadHistoryInbox.peer = new TL_peerChat();
                                                                            tL_updateReadHistoryInbox.peer.chat_id = i6;
                                                                        } else {
                                                                            tL_updateReadHistoryInbox.peer = new TL_peerUser();
                                                                            tL_updateReadHistoryInbox.peer.user_id = i5;
                                                                        }
                                                                        tL_updateReadHistoryInbox.max_id = i;
                                                                        arrayList.add(tL_updateReadHistoryInbox);
                                                                    } else {
                                                                        tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                                                                        tL_updateReadChannelInbox.channel_id = i4;
                                                                        tL_updateReadChannelInbox.max_id = i;
                                                                        arrayList.add(tL_updateReadChannelInbox);
                                                                    }
                                                                    MessagesController.getInstance(i3).processUpdateArray(arrayList, null, null, false);
                                                                } else {
                                                                    i = jSONObject2.getInt("msg_id");
                                                                    num = (Integer) MessagesController.getInstance(i3).dialogs_read_inbox_max.get(Long.valueOf(j));
                                                                    if (num == null) {
                                                                        num = Integer.valueOf(MessagesStorage.getInstance(i3).getDialogReadMax(false, j));
                                                                        MessagesController.getInstance(i3).dialogs_read_inbox_max.put(Long.valueOf(j), num);
                                                                    }
                                                                    if (i <= num.intValue()) {
                                                                        if (jSONObject2.has("chat_from_id")) {
                                                                        }
                                                                        if (!jSONObject2.has("mention")) {
                                                                            break;
                                                                        }
                                                                        if (jSONObject.has("loc_args")) {
                                                                            i9 = 0;
                                                                            strArr = null;
                                                                        } else {
                                                                            jSONArray = jSONObject.getJSONArray("loc_args");
                                                                            strArr = new String[jSONArray.length()];
                                                                            for (i8 = 0; i8 < strArr.length; i8++) {
                                                                                strArr[i8] = jSONArray.getString(i8);
                                                                            }
                                                                            i9 = 0;
                                                                        }
                                                                        str = strArr[i9];
                                                                        if (!string.startsWith("CHAT_")) {
                                                                            if (i4 != 0) {
                                                                            }
                                                                            str2 = str;
                                                                            str3 = strArr[1];
                                                                            z3 = false;
                                                                            obj2 = obj;
                                                                            obj = null;
                                                                        } else if (!string.startsWith("PINNED_")) {
                                                                            if (i7 != 0) {
                                                                            }
                                                                            str3 = str;
                                                                            str2 = null;
                                                                            z3 = false;
                                                                            obj2 = i7 != 0 ? 1 : null;
                                                                            obj = 1;
                                                                        } else if (string.startsWith("CHANNEL_")) {
                                                                            str3 = str;
                                                                            obj = null;
                                                                            obj2 = null;
                                                                            str2 = null;
                                                                            z3 = false;
                                                                        } else {
                                                                            str3 = str;
                                                                            obj = null;
                                                                            obj2 = null;
                                                                            str2 = null;
                                                                            z3 = true;
                                                                        }
                                                                        if (BuildVars.LOGS_ENABLED) {
                                                                            i2 = i3;
                                                                        } else {
                                                                            stringBuilder2 = new StringBuilder();
                                                                            i2 = i3;
                                                                            stringBuilder2.append("GCM received message notification ");
                                                                            stringBuilder2.append(string);
                                                                            stringBuilder2.append(" for dialogId = ");
                                                                            stringBuilder2.append(j);
                                                                            stringBuilder2.append(" mid = ");
                                                                            stringBuilder2.append(i);
                                                                            i3 = stringBuilder2.toString();
                                                                            FileLog.m0d(i3);
                                                                        }
                                                                        switch (string.hashCode()) {
                                                                            case -2091498420:
                                                                                if (string.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                    obj3 = 28;
                                                                                    break;
                                                                                }
                                                                            case -2053872415:
                                                                                if (string.equals("CHAT_CREATED")) {
                                                                                    obj3 = 50;
                                                                                    break;
                                                                                }
                                                                            case -2039746363:
                                                                                if (string.equals("MESSAGE_STICKER")) {
                                                                                    obj3 = 9;
                                                                                    break;
                                                                                }
                                                                            case -1979538588:
                                                                                if (string.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                    obj3 = 25;
                                                                                    break;
                                                                                }
                                                                            case -1979536003:
                                                                                if (string.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                    obj3 = 29;
                                                                                    break;
                                                                                }
                                                                            case -1979535888:
                                                                                if (string.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                    obj3 = 31;
                                                                                    break;
                                                                                }
                                                                            case -1969004705:
                                                                                if (string.equals("CHAT_ADD_MEMBER")) {
                                                                                    obj3 = 53;
                                                                                    break;
                                                                                }
                                                                            case -1946699248:
                                                                                if (string.equals("CHAT_JOINED")) {
                                                                                    obj3 = 59;
                                                                                    break;
                                                                                }
                                                                            case -1528047021:
                                                                                if (string.equals("CHAT_MESSAGES")) {
                                                                                    obj3 = 62;
                                                                                    break;
                                                                                }
                                                                            case -1493579426:
                                                                                if (string.equals("MESSAGE_AUDIO")) {
                                                                                    obj3 = 10;
                                                                                    break;
                                                                                }
                                                                            case -1480102982:
                                                                                if (string.equals("MESSAGE_PHOTO")) {
                                                                                    obj3 = 2;
                                                                                    break;
                                                                                }
                                                                            case -1478041834:
                                                                                if (string.equals("MESSAGE_ROUND")) {
                                                                                    obj3 = 7;
                                                                                    break;
                                                                                }
                                                                            case -1474543101:
                                                                                if (string.equals("MESSAGE_VIDEO")) {
                                                                                    obj3 = 4;
                                                                                    break;
                                                                                }
                                                                            case -1465695932:
                                                                                if (string.equals("ENCRYPTION_ACCEPT")) {
                                                                                    obj3 = 81;
                                                                                    break;
                                                                                }
                                                                            case -1374906292:
                                                                                if (string.equals("ENCRYPTED_MESSAGE")) {
                                                                                    obj3 = 82;
                                                                                    break;
                                                                                }
                                                                            case -1372940586:
                                                                                if (string.equals("CHAT_RETURNED")) {
                                                                                    obj3 = 58;
                                                                                    break;
                                                                                }
                                                                            case -1264245338:
                                                                                if (string.equals("PINNED_INVOICE")) {
                                                                                    obj3 = 75;
                                                                                    break;
                                                                                }
                                                                            case -1236086700:
                                                                                if (string.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                    obj3 = 33;
                                                                                    break;
                                                                                }
                                                                            case -1236077786:
                                                                                if (string.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                    obj3 = 32;
                                                                                    break;
                                                                                }
                                                                            case -1235686303:
                                                                                if (string.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                    obj3 = 20;
                                                                                    break;
                                                                                }
                                                                            case -1198046100:
                                                                                if (string.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                    obj3 = 5;
                                                                                    break;
                                                                                }
                                                                            case -1124254527:
                                                                                if (string.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                    obj3 = 44;
                                                                                    break;
                                                                                }
                                                                            case -1085137927:
                                                                                if (string.equals("PINNED_GAME")) {
                                                                                    obj3 = 74;
                                                                                    break;
                                                                                }
                                                                            case -1084746444:
                                                                                if (string.equals("PINNED_TEXT")) {
                                                                                    obj3 = 63;
                                                                                    break;
                                                                                }
                                                                            case -819729482:
                                                                                if (string.equals("PINNED_STICKER")) {
                                                                                    obj3 = 69;
                                                                                    break;
                                                                                }
                                                                            case -772141857:
                                                                                if (string.equals("PHONE_CALL_REQUEST")) {
                                                                                    obj3 = 84;
                                                                                    break;
                                                                                }
                                                                            case -638310039:
                                                                                if (string.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                    obj3 = 26;
                                                                                    break;
                                                                                }
                                                                            case -589196239:
                                                                                if (string.equals("PINNED_DOC")) {
                                                                                    obj3 = 68;
                                                                                    break;
                                                                                }
                                                                            case -589193654:
                                                                                if (string.equals("PINNED_GEO")) {
                                                                                    obj3 = 72;
                                                                                    break;
                                                                                }
                                                                            case -589193539:
                                                                                if (string.equals("PINNED_GIF")) {
                                                                                    obj3 = 76;
                                                                                    break;
                                                                                }
                                                                            case -440169325:
                                                                                if (string.equals("AUTH_UNKNOWN")) {
                                                                                    obj3 = 78;
                                                                                    break;
                                                                                }
                                                                            case -412748110:
                                                                                if (string.equals("CHAT_DELETE_YOU")) {
                                                                                    obj3 = 56;
                                                                                    break;
                                                                                }
                                                                            case -228518075:
                                                                                if (string.equals("MESSAGE_GEOLIVE")) {
                                                                                    obj3 = 13;
                                                                                    break;
                                                                                }
                                                                            case -213586509:
                                                                                if (string.equals("ENCRYPTION_REQUEST")) {
                                                                                    obj3 = 80;
                                                                                    break;
                                                                                }
                                                                            case -115582002:
                                                                                if (string.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                    obj3 = 49;
                                                                                    break;
                                                                                }
                                                                            case -112621464:
                                                                                if (string.equals("CONTACT_JOINED")) {
                                                                                    obj3 = 77;
                                                                                    break;
                                                                                }
                                                                            case -108522133:
                                                                                if (string.equals("AUTH_REGION")) {
                                                                                    obj3 = 79;
                                                                                    break;
                                                                                }
                                                                            case -107572034:
                                                                                if (string.equals("MESSAGE_SCREENSHOT")) {
                                                                                    obj3 = 6;
                                                                                    break;
                                                                                }
                                                                            case -40534265:
                                                                                if (string.equals("CHAT_DELETE_MEMBER")) {
                                                                                    obj3 = 55;
                                                                                    break;
                                                                                }
                                                                            case 65254746:
                                                                                if (string.equals("CHAT_ADD_YOU")) {
                                                                                    obj3 = 54;
                                                                                    break;
                                                                                }
                                                                            case 141040782:
                                                                                if (string.equals("CHAT_LEFT")) {
                                                                                    obj3 = 57;
                                                                                    break;
                                                                                }
                                                                            case 309993049:
                                                                                if (string.equals("CHAT_MESSAGE_DOC")) {
                                                                                    obj3 = 41;
                                                                                    break;
                                                                                }
                                                                            case 309995634:
                                                                                if (string.equals("CHAT_MESSAGE_GEO")) {
                                                                                    obj3 = 45;
                                                                                    break;
                                                                                }
                                                                            case 309995749:
                                                                                if (string.equals("CHAT_MESSAGE_GIF")) {
                                                                                    obj3 = 47;
                                                                                    break;
                                                                                }
                                                                            case 320532812:
                                                                                if (string.equals("MESSAGES")) {
                                                                                    obj3 = 19;
                                                                                    break;
                                                                                }
                                                                            case 328933854:
                                                                                if (string.equals("CHAT_MESSAGE_STICKER")) {
                                                                                    obj3 = 42;
                                                                                    break;
                                                                                }
                                                                            case 331340546:
                                                                                if (string.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                    obj3 = 27;
                                                                                    break;
                                                                                }
                                                                            case 344816990:
                                                                                if (string.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                    obj3 = 22;
                                                                                    break;
                                                                                }
                                                                            case 346878138:
                                                                                if (string.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                    obj3 = 24;
                                                                                    break;
                                                                                }
                                                                            case 350376871:
                                                                                if (string.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                    obj3 = 23;
                                                                                    break;
                                                                                }
                                                                            case 615714517:
                                                                                if (string.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                    obj3 = 3;
                                                                                    break;
                                                                                }
                                                                            case 715508879:
                                                                                if (string.equals("PINNED_AUDIO")) {
                                                                                    obj3 = 70;
                                                                                    break;
                                                                                }
                                                                            case 728985323:
                                                                                if (string.equals("PINNED_PHOTO")) {
                                                                                    obj3 = 65;
                                                                                    break;
                                                                                }
                                                                            case 731046471:
                                                                                if (string.equals("PINNED_ROUND")) {
                                                                                    obj3 = 67;
                                                                                    break;
                                                                                }
                                                                            case 734545204:
                                                                                if (string.equals("PINNED_VIDEO")) {
                                                                                    obj3 = 66;
                                                                                    break;
                                                                                }
                                                                            case 802032552:
                                                                                if (string.equals("MESSAGE_CONTACT")) {
                                                                                    obj3 = 11;
                                                                                    break;
                                                                                }
                                                                            case 991498806:
                                                                                if (string.equals("PINNED_GEOLIVE")) {
                                                                                    obj3 = 73;
                                                                                    break;
                                                                                }
                                                                            case 1019917311:
                                                                                if (string.equals("CHAT_MESSAGE_FWDS")) {
                                                                                    obj3 = 60;
                                                                                    break;
                                                                                }
                                                                            case 1019926225:
                                                                                if (string.equals("CHAT_MESSAGE_GAME")) {
                                                                                    obj3 = 48;
                                                                                    break;
                                                                                }
                                                                            case 1020317708:
                                                                                if (string.equals("CHAT_MESSAGE_TEXT")) {
                                                                                    obj3 = 36;
                                                                                    break;
                                                                                }
                                                                            case 1060349560:
                                                                                if (string.equals("MESSAGE_FWDS")) {
                                                                                    obj3 = 17;
                                                                                    break;
                                                                                }
                                                                            case 1060358474:
                                                                                if (string.equals("MESSAGE_GAME")) {
                                                                                    obj3 = 15;
                                                                                    break;
                                                                                }
                                                                            case 1060749957:
                                                                                if (string.equals("MESSAGE_TEXT")) {
                                                                                    obj3 = null;
                                                                                    break;
                                                                                }
                                                                            case 1073049781:
                                                                                if (string.equals("PINNED_NOTEXT")) {
                                                                                    obj3 = 64;
                                                                                    break;
                                                                                }
                                                                            case 1078101399:
                                                                                if (string.equals("CHAT_TITLE_EDITED")) {
                                                                                    obj3 = 51;
                                                                                    break;
                                                                                }
                                                                            case 1110103437:
                                                                                if (string.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                    obj3 = 37;
                                                                                    break;
                                                                                }
                                                                            case 1160762272:
                                                                                if (string.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                    obj3 = 61;
                                                                                    break;
                                                                                }
                                                                            case 1172918249:
                                                                                if (string.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                    obj3 = 30;
                                                                                    break;
                                                                                }
                                                                            case 1281128640:
                                                                                if (string.equals("MESSAGE_DOC")) {
                                                                                    obj3 = 8;
                                                                                    break;
                                                                                }
                                                                            case 1281131225:
                                                                                if (string.equals("MESSAGE_GEO")) {
                                                                                    obj3 = 12;
                                                                                    break;
                                                                                }
                                                                            case 1281131340:
                                                                                if (string.equals("MESSAGE_GIF")) {
                                                                                    obj3 = 14;
                                                                                    break;
                                                                                }
                                                                            case 1310789062:
                                                                                if (string.equals("MESSAGE_NOTEXT")) {
                                                                                    obj3 = 1;
                                                                                    break;
                                                                                }
                                                                            case 1361447897:
                                                                                if (string.equals("MESSAGE_PHOTOS")) {
                                                                                    obj3 = 18;
                                                                                    break;
                                                                                }
                                                                            case 1498266155:
                                                                                if (string.equals("PHONE_CALL_MISSED")) {
                                                                                    obj3 = 85;
                                                                                    break;
                                                                                }
                                                                            case 1547988151:
                                                                                if (string.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                    obj3 = 43;
                                                                                    break;
                                                                                }
                                                                            case 1561464595:
                                                                                if (string.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                    obj3 = 38;
                                                                                    break;
                                                                                }
                                                                            case 1563525743:
                                                                                if (string.equals("CHAT_MESSAGE_ROUND")) {
                                                                                    obj3 = 40;
                                                                                    break;
                                                                                }
                                                                            case 1567024476:
                                                                                if (string.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                    obj3 = 39;
                                                                                    break;
                                                                                }
                                                                            case 1810705077:
                                                                                if (string.equals("MESSAGE_INVOICE")) {
                                                                                    obj3 = 16;
                                                                                    break;
                                                                                }
                                                                            case 1815177512:
                                                                                if (string.equals("CHANNEL_MESSAGES")) {
                                                                                    obj3 = 35;
                                                                                    break;
                                                                                }
                                                                            case 1963241394:
                                                                                if (string.equals("LOCKED_MESSAGE")) {
                                                                                    obj3 = 83;
                                                                                    break;
                                                                                }
                                                                            case 2014789757:
                                                                                if (string.equals("CHAT_PHOTO_EDITED")) {
                                                                                    obj3 = 52;
                                                                                    break;
                                                                                }
                                                                            case 2022049433:
                                                                                if (string.equals("PINNED_CONTACT")) {
                                                                                    obj3 = 71;
                                                                                    break;
                                                                                }
                                                                            case 2048733346:
                                                                                if (string.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                    obj3 = 21;
                                                                                    break;
                                                                                }
                                                                            case 2099392181:
                                                                                if (string.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                    obj3 = 34;
                                                                                    break;
                                                                                }
                                                                            case 2140162142:
                                                                                if (string.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                    obj3 = 46;
                                                                                    break;
                                                                                }
                                                                                obj3 = -1;
                                                                                break;
                                                                            default:
                                                                        }
                                                                    } else {
                                                                        return;
                                                                    }
                                                                }
                                                                ConnectionsManager.onInternalPushReceived(i3);
                                                                ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                                break;
                                                            }
                                                            return;
                                                    }
                                                }
                                                z = true;
                                                switch (z) {
                                                    case false:
                                                        i = jSONObject2.getInt("dc");
                                                        split = jSONObject2.getString("addr").split(":");
                                                        if (split.length != 2) {
                                                            ConnectionsManager.getInstance(i3).applyDatacenterAddress(i, split[0], Integer.parseInt(split[1]));
                                                            ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                            return;
                                                        }
                                                        return;
                                                    case true:
                                                        tL_updateServiceNotification = new TL_updateServiceNotification();
                                                        tL_updateServiceNotification.popup = false;
                                                        tL_updateServiceNotification.flags = 2;
                                                        tL_updateServiceNotification.inbox_date = (int) (sentTime / 1000);
                                                        tL_updateServiceNotification.message = jSONObject.getString("message");
                                                        tL_updateServiceNotification.type = "announcement";
                                                        tL_updateServiceNotification.media = new TL_messageMediaEmpty();
                                                        tL_updates = new TL_updates();
                                                        tL_updates.updates.add(tL_updateServiceNotification);
                                                        Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                                                        ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                        return;
                                                    default:
                                                        j = 0;
                                                        if (jSONObject2.has("channel_id")) {
                                                            i4 = jSONObject2.getInt("channel_id");
                                                            j = (long) (-i4);
                                                        } else {
                                                            i4 = 0;
                                                        }
                                                        if (jSONObject2.has("from_id")) {
                                                            i5 = jSONObject2.getInt("from_id");
                                                            j = (long) i5;
                                                        } else {
                                                            i5 = 0;
                                                        }
                                                        if (jSONObject2.has("chat_id")) {
                                                            i6 = jSONObject2.getInt("chat_id");
                                                            j = (long) (-i6);
                                                        } else {
                                                            i6 = 0;
                                                        }
                                                        if (j == 0) {
                                                            if (jSONObject.has("badge")) {
                                                            }
                                                            if ((jSONObject.has("badge") ? jSONObject.getInt("badge") : 0) != 0) {
                                                                i = jSONObject2.getInt("msg_id");
                                                                num = (Integer) MessagesController.getInstance(i3).dialogs_read_inbox_max.get(Long.valueOf(j));
                                                                if (num == null) {
                                                                    num = Integer.valueOf(MessagesStorage.getInstance(i3).getDialogReadMax(false, j));
                                                                    MessagesController.getInstance(i3).dialogs_read_inbox_max.put(Long.valueOf(j), num);
                                                                }
                                                                if (i <= num.intValue()) {
                                                                    if (jSONObject2.has("chat_from_id")) {
                                                                    }
                                                                    if (jSONObject2.has("mention")) {
                                                                        break;
                                                                    }
                                                                    if (jSONObject.has("loc_args")) {
                                                                        jSONArray = jSONObject.getJSONArray("loc_args");
                                                                        strArr = new String[jSONArray.length()];
                                                                        for (i8 = 0; i8 < strArr.length; i8++) {
                                                                            strArr[i8] = jSONArray.getString(i8);
                                                                        }
                                                                        i9 = 0;
                                                                    } else {
                                                                        i9 = 0;
                                                                        strArr = null;
                                                                    }
                                                                    str = strArr[i9];
                                                                    if (!string.startsWith("CHAT_")) {
                                                                        if (i4 != 0) {
                                                                        }
                                                                        str2 = str;
                                                                        str3 = strArr[1];
                                                                        z3 = false;
                                                                        obj2 = obj;
                                                                        obj = null;
                                                                    } else if (!string.startsWith("PINNED_")) {
                                                                        if (i7 != 0) {
                                                                        }
                                                                        str3 = str;
                                                                        str2 = null;
                                                                        z3 = false;
                                                                        obj2 = i7 != 0 ? 1 : null;
                                                                        obj = 1;
                                                                    } else if (string.startsWith("CHANNEL_")) {
                                                                        str3 = str;
                                                                        obj = null;
                                                                        obj2 = null;
                                                                        str2 = null;
                                                                        z3 = true;
                                                                    } else {
                                                                        str3 = str;
                                                                        obj = null;
                                                                        obj2 = null;
                                                                        str2 = null;
                                                                        z3 = false;
                                                                    }
                                                                    if (BuildVars.LOGS_ENABLED) {
                                                                        stringBuilder2 = new StringBuilder();
                                                                        i2 = i3;
                                                                        stringBuilder2.append("GCM received message notification ");
                                                                        stringBuilder2.append(string);
                                                                        stringBuilder2.append(" for dialogId = ");
                                                                        stringBuilder2.append(j);
                                                                        stringBuilder2.append(" mid = ");
                                                                        stringBuilder2.append(i);
                                                                        i3 = stringBuilder2.toString();
                                                                        FileLog.m0d(i3);
                                                                    } else {
                                                                        i2 = i3;
                                                                    }
                                                                    switch (string.hashCode()) {
                                                                        case -2091498420:
                                                                            if (string.equals("CHANNEL_MESSAGE_CONTACT")) {
                                                                                obj3 = 28;
                                                                                break;
                                                                            }
                                                                        case -2053872415:
                                                                            if (string.equals("CHAT_CREATED")) {
                                                                                obj3 = 50;
                                                                                break;
                                                                            }
                                                                        case -2039746363:
                                                                            if (string.equals("MESSAGE_STICKER")) {
                                                                                obj3 = 9;
                                                                                break;
                                                                            }
                                                                        case -1979538588:
                                                                            if (string.equals("CHANNEL_MESSAGE_DOC")) {
                                                                                obj3 = 25;
                                                                                break;
                                                                            }
                                                                        case -1979536003:
                                                                            if (string.equals("CHANNEL_MESSAGE_GEO")) {
                                                                                obj3 = 29;
                                                                                break;
                                                                            }
                                                                        case -1979535888:
                                                                            if (string.equals("CHANNEL_MESSAGE_GIF")) {
                                                                                obj3 = 31;
                                                                                break;
                                                                            }
                                                                        case -1969004705:
                                                                            if (string.equals("CHAT_ADD_MEMBER")) {
                                                                                obj3 = 53;
                                                                                break;
                                                                            }
                                                                        case -1946699248:
                                                                            if (string.equals("CHAT_JOINED")) {
                                                                                obj3 = 59;
                                                                                break;
                                                                            }
                                                                        case -1528047021:
                                                                            if (string.equals("CHAT_MESSAGES")) {
                                                                                obj3 = 62;
                                                                                break;
                                                                            }
                                                                        case -1493579426:
                                                                            if (string.equals("MESSAGE_AUDIO")) {
                                                                                obj3 = 10;
                                                                                break;
                                                                            }
                                                                        case -1480102982:
                                                                            if (string.equals("MESSAGE_PHOTO")) {
                                                                                obj3 = 2;
                                                                                break;
                                                                            }
                                                                        case -1478041834:
                                                                            if (string.equals("MESSAGE_ROUND")) {
                                                                                obj3 = 7;
                                                                                break;
                                                                            }
                                                                        case -1474543101:
                                                                            if (string.equals("MESSAGE_VIDEO")) {
                                                                                obj3 = 4;
                                                                                break;
                                                                            }
                                                                        case -1465695932:
                                                                            if (string.equals("ENCRYPTION_ACCEPT")) {
                                                                                obj3 = 81;
                                                                                break;
                                                                            }
                                                                        case -1374906292:
                                                                            if (string.equals("ENCRYPTED_MESSAGE")) {
                                                                                obj3 = 82;
                                                                                break;
                                                                            }
                                                                        case -1372940586:
                                                                            if (string.equals("CHAT_RETURNED")) {
                                                                                obj3 = 58;
                                                                                break;
                                                                            }
                                                                        case -1264245338:
                                                                            if (string.equals("PINNED_INVOICE")) {
                                                                                obj3 = 75;
                                                                                break;
                                                                            }
                                                                        case -1236086700:
                                                                            if (string.equals("CHANNEL_MESSAGE_FWDS")) {
                                                                                obj3 = 33;
                                                                                break;
                                                                            }
                                                                        case -1236077786:
                                                                            if (string.equals("CHANNEL_MESSAGE_GAME")) {
                                                                                obj3 = 32;
                                                                                break;
                                                                            }
                                                                        case -1235686303:
                                                                            if (string.equals("CHANNEL_MESSAGE_TEXT")) {
                                                                                obj3 = 20;
                                                                                break;
                                                                            }
                                                                        case -1198046100:
                                                                            if (string.equals("MESSAGE_VIDEO_SECRET")) {
                                                                                obj3 = 5;
                                                                                break;
                                                                            }
                                                                        case -1124254527:
                                                                            if (string.equals("CHAT_MESSAGE_CONTACT")) {
                                                                                obj3 = 44;
                                                                                break;
                                                                            }
                                                                        case -1085137927:
                                                                            if (string.equals("PINNED_GAME")) {
                                                                                obj3 = 74;
                                                                                break;
                                                                            }
                                                                        case -1084746444:
                                                                            if (string.equals("PINNED_TEXT")) {
                                                                                obj3 = 63;
                                                                                break;
                                                                            }
                                                                        case -819729482:
                                                                            if (string.equals("PINNED_STICKER")) {
                                                                                obj3 = 69;
                                                                                break;
                                                                            }
                                                                        case -772141857:
                                                                            if (string.equals("PHONE_CALL_REQUEST")) {
                                                                                obj3 = 84;
                                                                                break;
                                                                            }
                                                                        case -638310039:
                                                                            if (string.equals("CHANNEL_MESSAGE_STICKER")) {
                                                                                obj3 = 26;
                                                                                break;
                                                                            }
                                                                        case -589196239:
                                                                            if (string.equals("PINNED_DOC")) {
                                                                                obj3 = 68;
                                                                                break;
                                                                            }
                                                                        case -589193654:
                                                                            if (string.equals("PINNED_GEO")) {
                                                                                obj3 = 72;
                                                                                break;
                                                                            }
                                                                        case -589193539:
                                                                            if (string.equals("PINNED_GIF")) {
                                                                                obj3 = 76;
                                                                                break;
                                                                            }
                                                                        case -440169325:
                                                                            if (string.equals("AUTH_UNKNOWN")) {
                                                                                obj3 = 78;
                                                                                break;
                                                                            }
                                                                        case -412748110:
                                                                            if (string.equals("CHAT_DELETE_YOU")) {
                                                                                obj3 = 56;
                                                                                break;
                                                                            }
                                                                        case -228518075:
                                                                            if (string.equals("MESSAGE_GEOLIVE")) {
                                                                                obj3 = 13;
                                                                                break;
                                                                            }
                                                                        case -213586509:
                                                                            if (string.equals("ENCRYPTION_REQUEST")) {
                                                                                obj3 = 80;
                                                                                break;
                                                                            }
                                                                        case -115582002:
                                                                            if (string.equals("CHAT_MESSAGE_INVOICE")) {
                                                                                obj3 = 49;
                                                                                break;
                                                                            }
                                                                        case -112621464:
                                                                            if (string.equals("CONTACT_JOINED")) {
                                                                                obj3 = 77;
                                                                                break;
                                                                            }
                                                                        case -108522133:
                                                                            if (string.equals("AUTH_REGION")) {
                                                                                obj3 = 79;
                                                                                break;
                                                                            }
                                                                        case -107572034:
                                                                            if (string.equals("MESSAGE_SCREENSHOT")) {
                                                                                obj3 = 6;
                                                                                break;
                                                                            }
                                                                        case -40534265:
                                                                            if (string.equals("CHAT_DELETE_MEMBER")) {
                                                                                obj3 = 55;
                                                                                break;
                                                                            }
                                                                        case 65254746:
                                                                            if (string.equals("CHAT_ADD_YOU")) {
                                                                                obj3 = 54;
                                                                                break;
                                                                            }
                                                                        case 141040782:
                                                                            if (string.equals("CHAT_LEFT")) {
                                                                                obj3 = 57;
                                                                                break;
                                                                            }
                                                                        case 309993049:
                                                                            if (string.equals("CHAT_MESSAGE_DOC")) {
                                                                                obj3 = 41;
                                                                                break;
                                                                            }
                                                                        case 309995634:
                                                                            if (string.equals("CHAT_MESSAGE_GEO")) {
                                                                                obj3 = 45;
                                                                                break;
                                                                            }
                                                                        case 309995749:
                                                                            if (string.equals("CHAT_MESSAGE_GIF")) {
                                                                                obj3 = 47;
                                                                                break;
                                                                            }
                                                                        case 320532812:
                                                                            if (string.equals("MESSAGES")) {
                                                                                obj3 = 19;
                                                                                break;
                                                                            }
                                                                        case 328933854:
                                                                            if (string.equals("CHAT_MESSAGE_STICKER")) {
                                                                                obj3 = 42;
                                                                                break;
                                                                            }
                                                                        case 331340546:
                                                                            if (string.equals("CHANNEL_MESSAGE_AUDIO")) {
                                                                                obj3 = 27;
                                                                                break;
                                                                            }
                                                                        case 344816990:
                                                                            if (string.equals("CHANNEL_MESSAGE_PHOTO")) {
                                                                                obj3 = 22;
                                                                                break;
                                                                            }
                                                                        case 346878138:
                                                                            if (string.equals("CHANNEL_MESSAGE_ROUND")) {
                                                                                obj3 = 24;
                                                                                break;
                                                                            }
                                                                        case 350376871:
                                                                            if (string.equals("CHANNEL_MESSAGE_VIDEO")) {
                                                                                obj3 = 23;
                                                                                break;
                                                                            }
                                                                        case 615714517:
                                                                            if (string.equals("MESSAGE_PHOTO_SECRET")) {
                                                                                obj3 = 3;
                                                                                break;
                                                                            }
                                                                        case 715508879:
                                                                            if (string.equals("PINNED_AUDIO")) {
                                                                                obj3 = 70;
                                                                                break;
                                                                            }
                                                                        case 728985323:
                                                                            if (string.equals("PINNED_PHOTO")) {
                                                                                obj3 = 65;
                                                                                break;
                                                                            }
                                                                        case 731046471:
                                                                            if (string.equals("PINNED_ROUND")) {
                                                                                obj3 = 67;
                                                                                break;
                                                                            }
                                                                        case 734545204:
                                                                            if (string.equals("PINNED_VIDEO")) {
                                                                                obj3 = 66;
                                                                                break;
                                                                            }
                                                                        case 802032552:
                                                                            if (string.equals("MESSAGE_CONTACT")) {
                                                                                obj3 = 11;
                                                                                break;
                                                                            }
                                                                        case 991498806:
                                                                            if (string.equals("PINNED_GEOLIVE")) {
                                                                                obj3 = 73;
                                                                                break;
                                                                            }
                                                                        case 1019917311:
                                                                            if (string.equals("CHAT_MESSAGE_FWDS")) {
                                                                                obj3 = 60;
                                                                                break;
                                                                            }
                                                                        case 1019926225:
                                                                            if (string.equals("CHAT_MESSAGE_GAME")) {
                                                                                obj3 = 48;
                                                                                break;
                                                                            }
                                                                        case 1020317708:
                                                                            if (string.equals("CHAT_MESSAGE_TEXT")) {
                                                                                obj3 = 36;
                                                                                break;
                                                                            }
                                                                        case 1060349560:
                                                                            if (string.equals("MESSAGE_FWDS")) {
                                                                                obj3 = 17;
                                                                                break;
                                                                            }
                                                                        case 1060358474:
                                                                            if (string.equals("MESSAGE_GAME")) {
                                                                                obj3 = 15;
                                                                                break;
                                                                            }
                                                                        case 1060749957:
                                                                            if (string.equals("MESSAGE_TEXT")) {
                                                                                obj3 = null;
                                                                                break;
                                                                            }
                                                                        case 1073049781:
                                                                            if (string.equals("PINNED_NOTEXT")) {
                                                                                obj3 = 64;
                                                                                break;
                                                                            }
                                                                        case 1078101399:
                                                                            if (string.equals("CHAT_TITLE_EDITED")) {
                                                                                obj3 = 51;
                                                                                break;
                                                                            }
                                                                        case 1110103437:
                                                                            if (string.equals("CHAT_MESSAGE_NOTEXT")) {
                                                                                obj3 = 37;
                                                                                break;
                                                                            }
                                                                        case 1160762272:
                                                                            if (string.equals("CHAT_MESSAGE_PHOTOS")) {
                                                                                obj3 = 61;
                                                                                break;
                                                                            }
                                                                        case 1172918249:
                                                                            if (string.equals("CHANNEL_MESSAGE_GEOLIVE")) {
                                                                                obj3 = 30;
                                                                                break;
                                                                            }
                                                                        case 1281128640:
                                                                            if (string.equals("MESSAGE_DOC")) {
                                                                                obj3 = 8;
                                                                                break;
                                                                            }
                                                                        case 1281131225:
                                                                            if (string.equals("MESSAGE_GEO")) {
                                                                                obj3 = 12;
                                                                                break;
                                                                            }
                                                                        case 1281131340:
                                                                            if (string.equals("MESSAGE_GIF")) {
                                                                                obj3 = 14;
                                                                                break;
                                                                            }
                                                                        case 1310789062:
                                                                            if (string.equals("MESSAGE_NOTEXT")) {
                                                                                obj3 = 1;
                                                                                break;
                                                                            }
                                                                        case 1361447897:
                                                                            if (string.equals("MESSAGE_PHOTOS")) {
                                                                                obj3 = 18;
                                                                                break;
                                                                            }
                                                                        case 1498266155:
                                                                            if (string.equals("PHONE_CALL_MISSED")) {
                                                                                obj3 = 85;
                                                                                break;
                                                                            }
                                                                        case 1547988151:
                                                                            if (string.equals("CHAT_MESSAGE_AUDIO")) {
                                                                                obj3 = 43;
                                                                                break;
                                                                            }
                                                                        case 1561464595:
                                                                            if (string.equals("CHAT_MESSAGE_PHOTO")) {
                                                                                obj3 = 38;
                                                                                break;
                                                                            }
                                                                        case 1563525743:
                                                                            if (string.equals("CHAT_MESSAGE_ROUND")) {
                                                                                obj3 = 40;
                                                                                break;
                                                                            }
                                                                        case 1567024476:
                                                                            if (string.equals("CHAT_MESSAGE_VIDEO")) {
                                                                                obj3 = 39;
                                                                                break;
                                                                            }
                                                                        case 1810705077:
                                                                            if (string.equals("MESSAGE_INVOICE")) {
                                                                                obj3 = 16;
                                                                                break;
                                                                            }
                                                                        case 1815177512:
                                                                            if (string.equals("CHANNEL_MESSAGES")) {
                                                                                obj3 = 35;
                                                                                break;
                                                                            }
                                                                        case 1963241394:
                                                                            if (string.equals("LOCKED_MESSAGE")) {
                                                                                obj3 = 83;
                                                                                break;
                                                                            }
                                                                        case 2014789757:
                                                                            if (string.equals("CHAT_PHOTO_EDITED")) {
                                                                                obj3 = 52;
                                                                                break;
                                                                            }
                                                                        case 2022049433:
                                                                            if (string.equals("PINNED_CONTACT")) {
                                                                                obj3 = 71;
                                                                                break;
                                                                            }
                                                                        case 2048733346:
                                                                            if (string.equals("CHANNEL_MESSAGE_NOTEXT")) {
                                                                                obj3 = 21;
                                                                                break;
                                                                            }
                                                                        case 2099392181:
                                                                            if (string.equals("CHANNEL_MESSAGE_PHOTOS")) {
                                                                                obj3 = 34;
                                                                                break;
                                                                            }
                                                                        case 2140162142:
                                                                            if (string.equals("CHAT_MESSAGE_GEOLIVE")) {
                                                                                obj3 = 46;
                                                                                break;
                                                                            }
                                                                            obj3 = -1;
                                                                            break;
                                                                        default:
                                                                    }
                                                                } else {
                                                                    return;
                                                                }
                                                            }
                                                            i = jSONObject2.getInt("max_id");
                                                            arrayList = new ArrayList();
                                                            if (BuildVars.LOGS_ENABLED) {
                                                                stringBuilder3 = new StringBuilder();
                                                                stringBuilder3.append("GCM received read notification max_id = ");
                                                                stringBuilder3.append(i);
                                                                stringBuilder3.append(" for dialogId = ");
                                                                stringBuilder3.append(j);
                                                                FileLog.m0d(stringBuilder3.toString());
                                                            }
                                                            if (i4 != 0) {
                                                                tL_updateReadChannelInbox = new TL_updateReadChannelInbox();
                                                                tL_updateReadChannelInbox.channel_id = i4;
                                                                tL_updateReadChannelInbox.max_id = i;
                                                                arrayList.add(tL_updateReadChannelInbox);
                                                            } else {
                                                                tL_updateReadHistoryInbox = new TL_updateReadHistoryInbox();
                                                                if (i5 != 0) {
                                                                    tL_updateReadHistoryInbox.peer = new TL_peerUser();
                                                                    tL_updateReadHistoryInbox.peer.user_id = i5;
                                                                } else {
                                                                    tL_updateReadHistoryInbox.peer = new TL_peerChat();
                                                                    tL_updateReadHistoryInbox.peer.chat_id = i6;
                                                                }
                                                                tL_updateReadHistoryInbox.max_id = i;
                                                                arrayList.add(tL_updateReadHistoryInbox);
                                                            }
                                                            MessagesController.getInstance(i3).processUpdateArray(arrayList, null, null, false);
                                                            ConnectionsManager.onInternalPushReceived(i3);
                                                            ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                            break;
                                                        }
                                                        return;
                                                }
                                            } catch (Throwable th4) {
                                                th222 = th4;
                                                th = th222;
                                                i = -1;
                                                if (i3 != i) {
                                                    GcmPushListenerService.this.onDecryptError();
                                                } else {
                                                    ConnectionsManager.onInternalPushReceived(i3);
                                                    ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                                }
                                                if (BuildVars.LOGS_ENABLED) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("error in loc_key = ");
                                                    stringBuilder.append(string);
                                                    FileLog.m1e(stringBuilder.toString());
                                                }
                                                FileLog.m3e(th);
                                            }
                                        }
                                        return;
                                    } catch (Throwable th2222) {
                                        th = th2222;
                                        string = null;
                                        i = -1;
                                        if (i3 != i) {
                                            GcmPushListenerService.this.onDecryptError();
                                        } else {
                                            ConnectionsManager.onInternalPushReceived(i3);
                                            ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("error in loc_key = ");
                                            stringBuilder.append(string);
                                            FileLog.m1e(stringBuilder.toString());
                                        }
                                        FileLog.m3e(th);
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
                        } catch (Throwable th22222) {
                            th = th22222;
                            i = -1;
                            string = null;
                        }
                    } catch (Throwable th222222) {
                        th = th222222;
                        string = null;
                        i = -1;
                        i3 = -1;
                        if (i3 != i) {
                            ConnectionsManager.onInternalPushReceived(i3);
                            ConnectionsManager.getInstance(i3).resumeNetworkMaybe();
                        } else {
                            GcmPushListenerService.this.onDecryptError();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("error in loc_key = ");
                            stringBuilder.append(string);
                            FileLog.m1e(stringBuilder.toString());
                        }
                        FileLog.m3e(th);
                    }
                }
            }

            public void run() {
                ApplicationLoader.postInitApplication();
                Utilities.stageQueue.postRunnable(new C01821());
            }
        });
    }

    private void onDecryptError() {
        for (int i = 0; i < 3; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(i);
                ConnectionsManager.getInstance(i).resumeNetworkMaybe();
            }
        }
    }
}
