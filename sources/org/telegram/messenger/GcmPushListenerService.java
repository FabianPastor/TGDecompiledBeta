package org.telegram.messenger;

import android.os.SystemClock;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.ConnectionsManager;

public class GcmPushListenerService extends FirebaseMessagingService {
    public static final int NOTIFICATION_ID = 1;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void onMessageReceived(RemoteMessage remoteMessage) {
        StringBuilder stringBuilder;
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();
        long sentTime = remoteMessage.getSentTime();
        long uptimeMillis = SystemClock.uptimeMillis();
        if (BuildVars.LOGS_ENABLED) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("GCM received data: ");
            stringBuilder.append(data);
            stringBuilder.append(" from: ");
            stringBuilder.append(from);
            FileLog.d(stringBuilder.toString());
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$nHUi11ZwPF6EFepQ1zn-R3cweVE(this, data, sentTime));
        try {
            this.countDownLatch.await();
        } catch (Throwable unused) {
        }
        if (BuildVars.DEBUG_VERSION) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("finished GCM service, time = ");
            stringBuilder.append(SystemClock.uptimeMillis() - uptimeMillis);
            FileLog.d(stringBuilder.toString());
        }
    }

    public /* synthetic */ void lambda$onMessageReceived$4$GcmPushListenerService(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$mgGQQF3kpbOfBT2rVoACkZ_x-2M(this, map, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x18ba  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x18ca  */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x18d1  */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:825:0x17e3, B:834:0x17fc] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:115:0x01dd, code skipped:
            if (r2 == null) goto L_0x1856;
     */
    /* JADX WARNING: Missing block: B:116:0x01df, code skipped:
            if (r2 == 1) goto L_0x180d;
     */
    /* JADX WARNING: Missing block: B:117:0x01e1, code skipped:
            if (r2 == 2) goto L_0x17f9;
     */
    /* JADX WARNING: Missing block: B:118:0x01e3, code skipped:
            if (r2 == 3) goto L_0x17d5;
     */
    /* JADX WARNING: Missing block: B:122:0x01ed, code skipped:
            if (r11.has("channel_id") == false) goto L_0x01f8;
     */
    /* JADX WARNING: Missing block: B:124:?, code skipped:
            r2 = r11.getInt("channel_id");
     */
    /* JADX WARNING: Missing block: B:125:0x01f5, code skipped:
            r3 = (long) (-r2);
     */
    /* JADX WARNING: Missing block: B:126:0x01f8, code skipped:
            r3 = 0;
            r2 = 0;
     */
    /* JADX WARNING: Missing block: B:129:0x0201, code skipped:
            if (r11.has("from_id") == false) goto L_0x0215;
     */
    /* JADX WARNING: Missing block: B:131:?, code skipped:
            r3 = r11.getInt("from_id");
     */
    /* JADX WARNING: Missing block: B:132:0x0209, code skipped:
            r14 = r7;
            r6 = r3;
            r3 = (long) r3;
     */
    /* JADX WARNING: Missing block: B:133:0x0211, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:134:0x0212, code skipped:
            r14 = r7;
     */
    /* JADX WARNING: Missing block: B:135:0x0213, code skipped:
            r3 = r1;
     */
    /* JADX WARNING: Missing block: B:136:0x0215, code skipped:
            r14 = r7;
            r6 = 0;
     */
    /* JADX WARNING: Missing block: B:139:0x021d, code skipped:
            if (r11.has("chat_id") == false) goto L_0x022a;
     */
    /* JADX WARNING: Missing block: B:141:?, code skipped:
            r3 = r11.getInt("chat_id");
     */
    /* JADX WARNING: Missing block: B:142:0x0225, code skipped:
            r12 = (long) (-r3);
     */
    /* JADX WARNING: Missing block: B:143:0x0228, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:144:0x022a, code skipped:
            r12 = r3;
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:147:0x0232, code skipped:
            if (r11.has("encryption_id") == false) goto L_0x023e;
     */
    /* JADX WARNING: Missing block: B:150:0x023a, code skipped:
            r12 = ((long) r11.getInt("encryption_id")) << 32;
     */
    /* JADX WARNING: Missing block: B:153:0x0244, code skipped:
            if (r11.has("schedule") == false) goto L_0x0250;
     */
    /* JADX WARNING: Missing block: B:156:0x024c, code skipped:
            if (r11.getInt("schedule") != 1) goto L_0x0250;
     */
    /* JADX WARNING: Missing block: B:157:0x024e, code skipped:
            r4 = true;
     */
    /* JADX WARNING: Missing block: B:158:0x0250, code skipped:
            r4 = false;
     */
    /* JADX WARNING: Missing block: B:160:0x0253, code skipped:
            if (r12 != 0) goto L_0x0262;
     */
    /* JADX WARNING: Missing block: B:162:0x025b, code skipped:
            if ("ENCRYPTED_MESSAGE".equals(r9) == false) goto L_0x0262;
     */
    /* JADX WARNING: Missing block: B:163:0x025d, code skipped:
            r12 = -4294967296L;
     */
    /* JADX WARNING: Missing block: B:165:0x0264, code skipped:
            if (r12 == 0) goto L_0x17a2;
     */
    /* JADX WARNING: Missing block: B:168:0x026c, code skipped:
            r10 = " for dialogId = ";
     */
    /* JADX WARNING: Missing block: B:169:0x026e, code skipped:
            if ("READ_HISTORY".equals(r9) == false) goto L_0x02de;
     */
    /* JADX WARNING: Missing block: B:171:?, code skipped:
            r4 = r11.getInt("max_id");
            r5 = new java.util.ArrayList();
     */
    /* JADX WARNING: Missing block: B:172:0x027d, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0299;
     */
    /* JADX WARNING: Missing block: B:173:0x027f, code skipped:
            r7 = new java.lang.StringBuilder();
            r7.append("GCM received read notification max_id = ");
            r7.append(r4);
            r7.append(r10);
            r7.append(r12);
            org.telegram.messenger.FileLog.d(r7.toString());
     */
    /* JADX WARNING: Missing block: B:174:0x0299, code skipped:
            if (r2 == 0) goto L_0x02a8;
     */
    /* JADX WARNING: Missing block: B:175:0x029b, code skipped:
            r3 = new org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox();
            r3.channel_id = r2;
            r3.max_id = r4;
            r5.add(r3);
     */
    /* JADX WARNING: Missing block: B:176:0x02a8, code skipped:
            r2 = new org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Missing block: B:177:0x02ad, code skipped:
            if (r6 == 0) goto L_0x02bb;
     */
    /* JADX WARNING: Missing block: B:178:0x02af, code skipped:
            r2.peer = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r2.peer.user_id = r6;
     */
    /* JADX WARNING: Missing block: B:179:0x02bb, code skipped:
            r2.peer = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r2.peer.chat_id = r3;
     */
    /* JADX WARNING: Missing block: B:180:0x02c6, code skipped:
            r2.max_id = r4;
            r5.add(r2);
     */
    /* JADX WARNING: Missing block: B:181:0x02cb, code skipped:
            org.telegram.messenger.MessagesController.getInstance(r15).processUpdateArray(r5, null, null, false, 0);
     */
    /* JADX WARNING: Missing block: B:184:0x02e4, code skipped:
            r8 = "messages";
     */
    /* JADX WARNING: Missing block: B:185:0x02e6, code skipped:
            if ("MESSAGE_DELETED".equals(r9) == false) goto L_0x034b;
     */
    /* JADX WARNING: Missing block: B:187:?, code skipped:
            r3 = r11.getString(r8).split(",");
            r4 = new android.util.SparseArray();
            r5 = new java.util.ArrayList();
            r6 = 0;
     */
    /* JADX WARNING: Missing block: B:189:0x02fe, code skipped:
            if (r6 >= r3.length) goto L_0x030c;
     */
    /* JADX WARNING: Missing block: B:190:0x0300, code skipped:
            r5.add(org.telegram.messenger.Utilities.parseInt(r3[r6]));
            r6 = r6 + 1;
     */
    /* JADX WARNING: Missing block: B:191:0x030c, code skipped:
            r4.put(r2, r5);
            org.telegram.messenger.NotificationsController.getInstance(r15).removeDeletedMessagesFromNotifications(r4);
            org.telegram.messenger.MessagesController.getInstance(r15).deleteMessagesByPush(r12, r5, r2);
     */
    /* JADX WARNING: Missing block: B:192:0x031f, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x17a2;
     */
    /* JADX WARNING: Missing block: B:193:0x0321, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("GCM received ");
            r2.append(r9);
            r2.append(r10);
            r2.append(r12);
            r2.append(" mids = ");
            r2.append(android.text.TextUtils.join(",", r5));
            org.telegram.messenger.FileLog.d(r2.toString());
     */
    /* JADX WARNING: Missing block: B:196:0x034f, code skipped:
            if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x17a2;
     */
    /* JADX WARNING: Missing block: B:198:0x0357, code skipped:
            if (r11.has("msg_id") == false) goto L_0x0362;
     */
    /* JADX WARNING: Missing block: B:200:?, code skipped:
            r7 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Missing block: B:201:0x035f, code skipped:
            r29 = r14;
     */
    /* JADX WARNING: Missing block: B:202:0x0362, code skipped:
            r29 = r14;
            r7 = 0;
     */
    /* JADX WARNING: Missing block: B:205:0x036b, code skipped:
            if (r11.has("random_id") == false) goto L_0x0389;
     */
    /* JADX WARNING: Missing block: B:208:0x037b, code skipped:
            r14 = r4;
            r22 = r3;
            r3 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Missing block: B:209:0x0383, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:210:0x0384, code skipped:
            r3 = r1;
            r14 = r29;
     */
    /* JADX WARNING: Missing block: B:211:0x0389, code skipped:
            r22 = r3;
            r14 = r4;
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:212:0x038e, code skipped:
            if (r7 == 0) goto L_0x03d3;
     */
    /* JADX WARNING: Missing block: B:213:0x0390, code skipped:
            r23 = r14;
     */
    /* JADX WARNING: Missing block: B:215:?, code skipped:
            r1 = (java.lang.Integer) org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r12));
     */
    /* JADX WARNING: Missing block: B:216:0x03a2, code skipped:
            if (r1 != null) goto L_0x03c1;
     */
    /* JADX WARNING: Missing block: B:217:0x03a4, code skipped:
            r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r15).getDialogReadMax(false, r12));
            r24 = r6;
            org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r12), r1);
     */
    /* JADX WARNING: Missing block: B:218:0x03c1, code skipped:
            r24 = r6;
     */
    /* JADX WARNING: Missing block: B:220:0x03c7, code skipped:
            if (r7 <= r1.intValue()) goto L_0x03e7;
     */
    /* JADX WARNING: Missing block: B:222:0x03ca, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:223:0x03cb, code skipped:
            r2 = -1;
            r3 = r35;
            r1 = r0;
            r14 = r29;
     */
    /* JADX WARNING: Missing block: B:224:0x03d3, code skipped:
            r24 = r6;
            r23 = r14;
     */
    /* JADX WARNING: Missing block: B:225:0x03d9, code skipped:
            if (r3 == 0) goto L_0x03e7;
     */
    /* JADX WARNING: Missing block: B:227:0x03e3, code skipped:
            if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r3) != false) goto L_0x03e7;
     */
    /* JADX WARNING: Missing block: B:228:0x03e5, code skipped:
            r1 = 1;
     */
    /* JADX WARNING: Missing block: B:229:0x03e7, code skipped:
            r1 = null;
     */
    /* JADX WARNING: Missing block: B:230:0x03e8, code skipped:
            if (r1 == null) goto L_0x179c;
     */
    /* JADX WARNING: Missing block: B:233:0x03f0, code skipped:
            if (r11.has("chat_from_id") == false) goto L_0x03f9;
     */
    /* JADX WARNING: Missing block: B:235:?, code skipped:
            r1 = r11.getInt("chat_from_id");
     */
    /* JADX WARNING: Missing block: B:236:0x03f9, code skipped:
            r1 = 0;
     */
    /* JADX WARNING: Missing block: B:239:0x0400, code skipped:
            if (r11.has("mention") == false) goto L_0x040c;
     */
    /* JADX WARNING: Missing block: B:242:0x0408, code skipped:
            if (r11.getInt("mention") == 0) goto L_0x040c;
     */
    /* JADX WARNING: Missing block: B:243:0x040a, code skipped:
            r6 = 1;
     */
    /* JADX WARNING: Missing block: B:244:0x040c, code skipped:
            r6 = null;
     */
    /* JADX WARNING: Missing block: B:247:0x0413, code skipped:
            if (r11.has("silent") == false) goto L_0x0421;
     */
    /* JADX WARNING: Missing block: B:250:0x041b, code skipped:
            if (r11.getInt("silent") == 0) goto L_0x0421;
     */
    /* JADX WARNING: Missing block: B:251:0x041d, code skipped:
            r30 = r15;
            r14 = true;
     */
    /* JADX WARNING: Missing block: B:252:0x0421, code skipped:
            r30 = r15;
            r14 = false;
     */
    /* JADX WARNING: Missing block: B:255:0x042a, code skipped:
            if (r5.has("loc_args") == false) goto L_0x0456;
     */
    /* JADX WARNING: Missing block: B:257:?, code skipped:
            r5 = r5.getJSONArray("loc_args");
            r15 = new java.lang.String[r5.length()];
            r20 = r6;
            r19 = r14;
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:259:0x043e, code skipped:
            if (r14 >= r15.length) goto L_0x0449;
     */
    /* JADX WARNING: Missing block: B:260:0x0440, code skipped:
            r15[r14] = r5.getString(r14);
     */
    /* JADX WARNING: Missing block: B:261:0x0446, code skipped:
            r14 = r14 + 1;
     */
    /* JADX WARNING: Missing block: B:262:0x0449, code skipped:
            r5 = 0;
     */
    /* JADX WARNING: Missing block: B:263:0x044b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:264:0x044c, code skipped:
            r2 = -1;
            r3 = r35;
            r1 = r0;
            r14 = r29;
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:265:0x0456, code skipped:
            r20 = r6;
            r19 = r14;
            r5 = 0;
            r15 = null;
     */
    /* JADX WARNING: Missing block: B:267:?, code skipped:
            r6 = r15[r5];
            r27 = r11.has("edit_date");
     */
    /* JADX WARNING: Missing block: B:268:0x046a, code skipped:
            if (r9.startsWith("CHAT_") == false) goto L_0x047a;
     */
    /* JADX WARNING: Missing block: B:269:0x046c, code skipped:
            if (r2 == 0) goto L_0x0470;
     */
    /* JADX WARNING: Missing block: B:270:0x046e, code skipped:
            r5 = 1;
     */
    /* JADX WARNING: Missing block: B:271:0x0470, code skipped:
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:274:?, code skipped:
            r14 = r15[1];
     */
    /* JADX WARNING: Missing block: B:275:0x0474, code skipped:
            r11 = r6;
            r26 = false;
            r6 = r5;
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:278:0x0480, code skipped:
            if (r9.startsWith("PINNED_") == false) goto L_0x048e;
     */
    /* JADX WARNING: Missing block: B:279:0x0482, code skipped:
            if (r1 == 0) goto L_0x0486;
     */
    /* JADX WARNING: Missing block: B:280:0x0484, code skipped:
            r5 = 1;
     */
    /* JADX WARNING: Missing block: B:281:0x0486, code skipped:
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:282:0x0487, code skipped:
            r14 = r6;
            r11 = null;
            r26 = false;
            r6 = r5;
            r5 = 1;
     */
    /* JADX WARNING: Missing block: B:283:0x048e, code skipped:
            r14 = r6;
     */
    /* JADX WARNING: Missing block: B:284:0x0495, code skipped:
            if (r9.startsWith("CHANNEL_") == false) goto L_0x049d;
     */
    /* JADX WARNING: Missing block: B:285:0x0497, code skipped:
            r5 = null;
            r6 = null;
            r11 = null;
            r26 = true;
     */
    /* JADX WARNING: Missing block: B:286:0x049d, code skipped:
            r5 = null;
            r6 = null;
            r11 = null;
            r26 = false;
     */
    /* JADX WARNING: Missing block: B:288:0x04a4, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04cd;
     */
    /* JADX WARNING: Missing block: B:289:0x04a6, code skipped:
            r25 = r14;
     */
    /* JADX WARNING: Missing block: B:291:?, code skipped:
            r14 = new java.lang.StringBuilder();
            r31 = r11;
            r14.append("GCM received message notification ");
            r14.append(r9);
            r14.append(r10);
            r14.append(r12);
            r14.append(" mid = ");
            r14.append(r7);
            org.telegram.messenger.FileLog.d(r14.toString());
     */
    /* JADX WARNING: Missing block: B:292:0x04cd, code skipped:
            r31 = r11;
            r25 = r14;
     */
    /* JADX WARNING: Missing block: B:295:0x04d5, code skipped:
            switch(r9.hashCode()) {
                case -2100047043: goto L_0x0956;
                case -2091498420: goto L_0x094b;
                case -2053872415: goto L_0x0940;
                case -2039746363: goto L_0x0935;
                case -2023218804: goto L_0x092a;
                case -1979538588: goto L_0x091f;
                case -1979536003: goto L_0x0914;
                case -1979535888: goto L_0x0909;
                case -1969004705: goto L_0x08fe;
                case -1946699248: goto L_0x08f2;
                case -1528047021: goto L_0x08e6;
                case -1493579426: goto L_0x08da;
                case -1482481933: goto L_0x08ce;
                case -1480102982: goto L_0x08c3;
                case -1478041834: goto L_0x08b7;
                case -1474543101: goto L_0x08ac;
                case -1465695932: goto L_0x08a0;
                case -1374906292: goto L_0x0894;
                case -1372940586: goto L_0x0888;
                case -1264245338: goto L_0x087c;
                case -1236086700: goto L_0x0870;
                case -1236077786: goto L_0x0864;
                case -1235796237: goto L_0x0858;
                case -1235686303: goto L_0x084d;
                case -1198046100: goto L_0x0842;
                case -1124254527: goto L_0x0836;
                case -1085137927: goto L_0x082a;
                case -1084856378: goto L_0x081e;
                case -1084746444: goto L_0x0812;
                case -819729482: goto L_0x0806;
                case -772141857: goto L_0x07fa;
                case -638310039: goto L_0x07ee;
                case -590403924: goto L_0x07e2;
                case -589196239: goto L_0x07d6;
                case -589193654: goto L_0x07ca;
                case -589193539: goto L_0x07be;
                case -440169325: goto L_0x07b2;
                case -412748110: goto L_0x07a6;
                case -228518075: goto L_0x079a;
                case -213586509: goto L_0x078e;
                case -115582002: goto L_0x0782;
                case -112621464: goto L_0x0776;
                case -108522133: goto L_0x076a;
                case -107572034: goto L_0x075f;
                case -40534265: goto L_0x0753;
                case 65254746: goto L_0x0747;
                case 141040782: goto L_0x073b;
                case 309993049: goto L_0x072f;
                case 309995634: goto L_0x0723;
                case 309995749: goto L_0x0717;
                case 320532812: goto L_0x070b;
                case 328933854: goto L_0x06ff;
                case 331340546: goto L_0x06f3;
                case 344816990: goto L_0x06e7;
                case 346878138: goto L_0x06db;
                case 350376871: goto L_0x06cf;
                case 615714517: goto L_0x06c4;
                case 715508879: goto L_0x06b8;
                case 728985323: goto L_0x06ac;
                case 731046471: goto L_0x06a0;
                case 734545204: goto L_0x0694;
                case 802032552: goto L_0x0688;
                case 991498806: goto L_0x067c;
                case 1007364121: goto L_0x0670;
                case 1019917311: goto L_0x0664;
                case 1019926225: goto L_0x0658;
                case 1020207774: goto L_0x064c;
                case 1020317708: goto L_0x0640;
                case 1060349560: goto L_0x0634;
                case 1060358474: goto L_0x0628;
                case 1060640023: goto L_0x061c;
                case 1060749957: goto L_0x0611;
                case 1073049781: goto L_0x0605;
                case 1078101399: goto L_0x05f9;
                case 1110103437: goto L_0x05ed;
                case 1160762272: goto L_0x05e1;
                case 1172918249: goto L_0x05d5;
                case 1234591620: goto L_0x05c9;
                case 1281128640: goto L_0x05bd;
                case 1281131225: goto L_0x05b1;
                case 1281131340: goto L_0x05a5;
                case 1310789062: goto L_0x059a;
                case 1333118583: goto L_0x058e;
                case 1361447897: goto L_0x0582;
                case 1498266155: goto L_0x0576;
                case 1533804208: goto L_0x056a;
                case 1547988151: goto L_0x055e;
                case 1561464595: goto L_0x0552;
                case 1563525743: goto L_0x0546;
                case 1567024476: goto L_0x053a;
                case 1810705077: goto L_0x052e;
                case 1815177512: goto L_0x0522;
                case 1963241394: goto L_0x0516;
                case 2014789757: goto L_0x050a;
                case 2022049433: goto L_0x04fe;
                case 2048733346: goto L_0x04f2;
                case 2099392181: goto L_0x04e6;
                case 2140162142: goto L_0x04da;
                default: goto L_0x04d8;
            };
     */
    /* JADX WARNING: Missing block: B:298:0x04e0, code skipped:
            if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:299:0x04e2, code skipped:
            r10 = 53;
     */
    /* JADX WARNING: Missing block: B:301:0x04ec, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:302:0x04ee, code skipped:
            r10 = 39;
     */
    /* JADX WARNING: Missing block: B:304:0x04f8, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:305:0x04fa, code skipped:
            r10 = 25;
     */
    /* JADX WARNING: Missing block: B:307:0x0504, code skipped:
            if (r9.equals("PINNED_CONTACT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:308:0x0506, code skipped:
            r10 = 80;
     */
    /* JADX WARNING: Missing block: B:310:0x0510, code skipped:
            if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:311:0x0512, code skipped:
            r10 = 61;
     */
    /* JADX WARNING: Missing block: B:313:0x051c, code skipped:
            if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:314:0x051e, code skipped:
            r10 = 92;
     */
    /* JADX WARNING: Missing block: B:316:0x0528, code skipped:
            if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:317:0x052a, code skipped:
            r10 = 41;
     */
    /* JADX WARNING: Missing block: B:319:0x0534, code skipped:
            if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:320:0x0536, code skipped:
            r10 = 20;
     */
    /* JADX WARNING: Missing block: B:322:0x0540, code skipped:
            if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:323:0x0542, code skipped:
            r10 = 45;
     */
    /* JADX WARNING: Missing block: B:325:0x054c, code skipped:
            if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:326:0x054e, code skipped:
            r10 = 46;
     */
    /* JADX WARNING: Missing block: B:328:0x0558, code skipped:
            if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:329:0x055a, code skipped:
            r10 = 44;
     */
    /* JADX WARNING: Missing block: B:331:0x0564, code skipped:
            if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:332:0x0566, code skipped:
            r10 = 49;
     */
    /* JADX WARNING: Missing block: B:334:0x0570, code skipped:
            if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:335:0x0572, code skipped:
            r10 = 23;
     */
    /* JADX WARNING: Missing block: B:337:0x057c, code skipped:
            if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:338:0x057e, code skipped:
            r10 = 97;
     */
    /* JADX WARNING: Missing block: B:340:0x0588, code skipped:
            if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:341:0x058a, code skipped:
            r10 = 22;
     */
    /* JADX WARNING: Missing block: B:343:0x0594, code skipped:
            if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:344:0x0596, code skipped:
            r10 = 70;
     */
    /* JADX WARNING: Missing block: B:346:0x05a0, code skipped:
            if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:347:0x05a2, code skipped:
            r10 = 2;
     */
    /* JADX WARNING: Missing block: B:349:0x05ab, code skipped:
            if (r9.equals("MESSAGE_GIF") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:350:0x05ad, code skipped:
            r10 = 16;
     */
    /* JADX WARNING: Missing block: B:352:0x05b7, code skipped:
            if (r9.equals("MESSAGE_GEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:353:0x05b9, code skipped:
            r10 = 14;
     */
    /* JADX WARNING: Missing block: B:355:0x05c3, code skipped:
            if (r9.equals("MESSAGE_DOC") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:356:0x05c5, code skipped:
            r10 = 9;
     */
    /* JADX WARNING: Missing block: B:358:0x05cf, code skipped:
            if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:359:0x05d1, code skipped:
            r10 = 56;
     */
    /* JADX WARNING: Missing block: B:361:0x05db, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:362:0x05dd, code skipped:
            r10 = 35;
     */
    /* JADX WARNING: Missing block: B:364:0x05e7, code skipped:
            if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:365:0x05e9, code skipped:
            r10 = 69;
     */
    /* JADX WARNING: Missing block: B:367:0x05f3, code skipped:
            if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:368:0x05f5, code skipped:
            r10 = 43;
     */
    /* JADX WARNING: Missing block: B:370:0x05ff, code skipped:
            if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:371:0x0601, code skipped:
            r10 = 60;
     */
    /* JADX WARNING: Missing block: B:373:0x060b, code skipped:
            if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:374:0x060d, code skipped:
            r10 = 73;
     */
    /* JADX WARNING: Missing block: B:376:0x0617, code skipped:
            if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:377:0x0619, code skipped:
            r10 = null;
     */
    /* JADX WARNING: Missing block: B:379:0x0622, code skipped:
            if (r9.equals("MESSAGE_POLL") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:380:0x0624, code skipped:
            r10 = 13;
     */
    /* JADX WARNING: Missing block: B:382:0x062e, code skipped:
            if (r9.equals("MESSAGE_GAME") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:383:0x0630, code skipped:
            r10 = 17;
     */
    /* JADX WARNING: Missing block: B:385:0x063a, code skipped:
            if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:386:0x063c, code skipped:
            r10 = 21;
     */
    /* JADX WARNING: Missing block: B:388:0x0646, code skipped:
            if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:389:0x0648, code skipped:
            r10 = 42;
     */
    /* JADX WARNING: Missing block: B:391:0x0652, code skipped:
            if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:392:0x0654, code skipped:
            r10 = 51;
     */
    /* JADX WARNING: Missing block: B:394:0x065e, code skipped:
            if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:395:0x0660, code skipped:
            r10 = 55;
     */
    /* JADX WARNING: Missing block: B:397:0x066a, code skipped:
            if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:398:0x066c, code skipped:
            r10 = 68;
     */
    /* JADX WARNING: Missing block: B:400:0x0676, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:401:0x0678, code skipped:
            r10 = 19;
     */
    /* JADX WARNING: Missing block: B:403:0x0682, code skipped:
            if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:404:0x0684, code skipped:
            r10 = 83;
     */
    /* JADX WARNING: Missing block: B:406:0x068e, code skipped:
            if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:407:0x0690, code skipped:
            r10 = 12;
     */
    /* JADX WARNING: Missing block: B:409:0x069a, code skipped:
            if (r9.equals("PINNED_VIDEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:410:0x069c, code skipped:
            r10 = 75;
     */
    /* JADX WARNING: Missing block: B:412:0x06a6, code skipped:
            if (r9.equals("PINNED_ROUND") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:413:0x06a8, code skipped:
            r10 = 76;
     */
    /* JADX WARNING: Missing block: B:415:0x06b2, code skipped:
            if (r9.equals("PINNED_PHOTO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:416:0x06b4, code skipped:
            r10 = 74;
     */
    /* JADX WARNING: Missing block: B:418:0x06be, code skipped:
            if (r9.equals("PINNED_AUDIO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:419:0x06c0, code skipped:
            r10 = 79;
     */
    /* JADX WARNING: Missing block: B:421:0x06ca, code skipped:
            if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:422:0x06cc, code skipped:
            r10 = 4;
     */
    /* JADX WARNING: Missing block: B:424:0x06d5, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:425:0x06d7, code skipped:
            r10 = 27;
     */
    /* JADX WARNING: Missing block: B:427:0x06e1, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:428:0x06e3, code skipped:
            r10 = 28;
     */
    /* JADX WARNING: Missing block: B:430:0x06ed, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:431:0x06ef, code skipped:
            r10 = 26;
     */
    /* JADX WARNING: Missing block: B:433:0x06f9, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:434:0x06fb, code skipped:
            r10 = 31;
     */
    /* JADX WARNING: Missing block: B:436:0x0705, code skipped:
            if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:437:0x0707, code skipped:
            r10 = 48;
     */
    /* JADX WARNING: Missing block: B:439:0x0711, code skipped:
            if (r9.equals("MESSAGES") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:440:0x0713, code skipped:
            r10 = 24;
     */
    /* JADX WARNING: Missing block: B:442:0x071d, code skipped:
            if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:443:0x071f, code skipped:
            r10 = 54;
     */
    /* JADX WARNING: Missing block: B:445:0x0729, code skipped:
            if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:446:0x072b, code skipped:
            r10 = 52;
     */
    /* JADX WARNING: Missing block: B:448:0x0735, code skipped:
            if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:449:0x0737, code skipped:
            r10 = 47;
     */
    /* JADX WARNING: Missing block: B:451:0x0741, code skipped:
            if (r9.equals("CHAT_LEFT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:452:0x0743, code skipped:
            r10 = 65;
     */
    /* JADX WARNING: Missing block: B:454:0x074d, code skipped:
            if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:455:0x074f, code skipped:
            r10 = 59;
     */
    /* JADX WARNING: Missing block: B:457:0x0759, code skipped:
            if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:458:0x075b, code skipped:
            r10 = 63;
     */
    /* JADX WARNING: Missing block: B:460:0x0765, code skipped:
            if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:461:0x0767, code skipped:
            r10 = 7;
     */
    /* JADX WARNING: Missing block: B:463:0x0770, code skipped:
            if (r9.equals("AUTH_REGION") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:464:0x0772, code skipped:
            r10 = 91;
     */
    /* JADX WARNING: Missing block: B:466:0x077c, code skipped:
            if (r9.equals("CONTACT_JOINED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:467:0x077e, code skipped:
            r10 = 89;
     */
    /* JADX WARNING: Missing block: B:469:0x0788, code skipped:
            if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:470:0x078a, code skipped:
            r10 = 57;
     */
    /* JADX WARNING: Missing block: B:472:0x0794, code skipped:
            if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:473:0x0796, code skipped:
            r10 = 93;
     */
    /* JADX WARNING: Missing block: B:475:0x07a0, code skipped:
            if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:476:0x07a2, code skipped:
            r10 = 15;
     */
    /* JADX WARNING: Missing block: B:478:0x07ac, code skipped:
            if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:479:0x07ae, code skipped:
            r10 = 64;
     */
    /* JADX WARNING: Missing block: B:481:0x07b8, code skipped:
            if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:482:0x07ba, code skipped:
            r10 = 90;
     */
    /* JADX WARNING: Missing block: B:484:0x07c4, code skipped:
            if (r9.equals("PINNED_GIF") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:485:0x07c6, code skipped:
            r10 = 87;
     */
    /* JADX WARNING: Missing block: B:487:0x07d0, code skipped:
            if (r9.equals("PINNED_GEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:488:0x07d2, code skipped:
            r10 = 82;
     */
    /* JADX WARNING: Missing block: B:490:0x07dc, code skipped:
            if (r9.equals("PINNED_DOC") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:491:0x07de, code skipped:
            r10 = 77;
     */
    /* JADX WARNING: Missing block: B:493:0x07e8, code skipped:
            if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:494:0x07ea, code skipped:
            r10 = 85;
     */
    /* JADX WARNING: Missing block: B:496:0x07f4, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:497:0x07f6, code skipped:
            r10 = 30;
     */
    /* JADX WARNING: Missing block: B:499:0x0800, code skipped:
            if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:500:0x0802, code skipped:
            r10 = 95;
     */
    /* JADX WARNING: Missing block: B:502:0x080c, code skipped:
            if (r9.equals("PINNED_STICKER") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:503:0x080e, code skipped:
            r10 = 78;
     */
    /* JADX WARNING: Missing block: B:505:0x0818, code skipped:
            if (r9.equals("PINNED_TEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:506:0x081a, code skipped:
            r10 = 72;
     */
    /* JADX WARNING: Missing block: B:508:0x0824, code skipped:
            if (r9.equals("PINNED_POLL") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:509:0x0826, code skipped:
            r10 = 81;
     */
    /* JADX WARNING: Missing block: B:511:0x0830, code skipped:
            if (r9.equals("PINNED_GAME") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:512:0x0832, code skipped:
            r10 = 84;
     */
    /* JADX WARNING: Missing block: B:514:0x083c, code skipped:
            if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:515:0x083e, code skipped:
            r10 = 50;
     */
    /* JADX WARNING: Missing block: B:517:0x0848, code skipped:
            if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:518:0x084a, code skipped:
            r10 = 6;
     */
    /* JADX WARNING: Missing block: B:520:0x0853, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:521:0x0855, code skipped:
            r10 = 1;
     */
    /* JADX WARNING: Missing block: B:523:0x085e, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:524:0x0860, code skipped:
            r10 = 33;
     */
    /* JADX WARNING: Missing block: B:526:0x086a, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:527:0x086c, code skipped:
            r10 = 37;
     */
    /* JADX WARNING: Missing block: B:529:0x0876, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:530:0x0878, code skipped:
            r10 = 38;
     */
    /* JADX WARNING: Missing block: B:532:0x0882, code skipped:
            if (r9.equals("PINNED_INVOICE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:533:0x0884, code skipped:
            r10 = 86;
     */
    /* JADX WARNING: Missing block: B:535:0x088e, code skipped:
            if (r9.equals("CHAT_RETURNED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:536:0x0890, code skipped:
            r10 = 66;
     */
    /* JADX WARNING: Missing block: B:538:0x089a, code skipped:
            if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:539:0x089c, code skipped:
            r10 = 88;
     */
    /* JADX WARNING: Missing block: B:541:0x08a6, code skipped:
            if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:542:0x08a8, code skipped:
            r10 = 94;
     */
    /* JADX WARNING: Missing block: B:544:0x08b2, code skipped:
            if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:545:0x08b4, code skipped:
            r10 = 5;
     */
    /* JADX WARNING: Missing block: B:547:0x08bd, code skipped:
            if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:548:0x08bf, code skipped:
            r10 = 8;
     */
    /* JADX WARNING: Missing block: B:550:0x08c9, code skipped:
            if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:551:0x08cb, code skipped:
            r10 = 3;
     */
    /* JADX WARNING: Missing block: B:553:0x08d4, code skipped:
            if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:554:0x08d6, code skipped:
            r10 = 96;
     */
    /* JADX WARNING: Missing block: B:556:0x08e0, code skipped:
            if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:557:0x08e2, code skipped:
            r10 = 11;
     */
    /* JADX WARNING: Missing block: B:559:0x08ec, code skipped:
            if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:560:0x08ee, code skipped:
            r10 = 71;
     */
    /* JADX WARNING: Missing block: B:562:0x08f8, code skipped:
            if (r9.equals("CHAT_JOINED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:563:0x08fa, code skipped:
            r10 = 67;
     */
    /* JADX WARNING: Missing block: B:565:0x0904, code skipped:
            if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:566:0x0906, code skipped:
            r10 = 62;
     */
    /* JADX WARNING: Missing block: B:568:0x090f, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:569:0x0911, code skipped:
            r10 = 36;
     */
    /* JADX WARNING: Missing block: B:571:0x091a, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:572:0x091c, code skipped:
            r10 = 34;
     */
    /* JADX WARNING: Missing block: B:574:0x0925, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:575:0x0927, code skipped:
            r10 = 29;
     */
    /* JADX WARNING: Missing block: B:577:0x0930, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:578:0x0932, code skipped:
            r10 = 40;
     */
    /* JADX WARNING: Missing block: B:580:0x093b, code skipped:
            if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:581:0x093d, code skipped:
            r10 = 10;
     */
    /* JADX WARNING: Missing block: B:583:0x0946, code skipped:
            if (r9.equals("CHAT_CREATED") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:584:0x0948, code skipped:
            r10 = 58;
     */
    /* JADX WARNING: Missing block: B:586:0x0951, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:587:0x0953, code skipped:
            r10 = 32;
     */
    /* JADX WARNING: Missing block: B:589:0x095c, code skipped:
            if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0961;
     */
    /* JADX WARNING: Missing block: B:590:0x095e, code skipped:
            r10 = 18;
     */
    /* JADX WARNING: Missing block: B:591:0x0961, code skipped:
            r10 = -1;
     */
    /* JADX WARNING: Missing block: B:592:0x0962, code skipped:
            r14 = "ChannelMessageFew";
            r11 = " ";
            r32 = r7;
            r7 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:593:0x096a, code skipped:
            switch(r10) {
                case 0: goto L_0x16bb;
                case 1: goto L_0x16bb;
                case 2: goto L_0x1698;
                case 3: goto L_0x1679;
                case 4: goto L_0x165a;
                case 5: goto L_0x163b;
                case 6: goto L_0x161b;
                case 7: goto L_0x1601;
                case 8: goto L_0x15e1;
                case 9: goto L_0x15c1;
                case 10: goto L_0x1562;
                case 11: goto L_0x1542;
                case 12: goto L_0x151d;
                case 13: goto L_0x14f8;
                case 14: goto L_0x14d8;
                case 15: goto L_0x14b8;
                case 16: goto L_0x1498;
                case 17: goto L_0x1473;
                case 18: goto L_0x1452;
                case 19: goto L_0x1452;
                case 20: goto L_0x142d;
                case 21: goto L_0x1406;
                case 22: goto L_0x13dd;
                case 23: goto L_0x13b4;
                case 24: goto L_0x139c;
                case 25: goto L_0x137c;
                case 26: goto L_0x135c;
                case 27: goto L_0x133c;
                case 28: goto L_0x131c;
                case 29: goto L_0x12fc;
                case 30: goto L_0x129d;
                case 31: goto L_0x127d;
                case 32: goto L_0x1258;
                case 33: goto L_0x1233;
                case 34: goto L_0x1213;
                case 35: goto L_0x11f3;
                case 36: goto L_0x11d3;
                case 37: goto L_0x11b3;
                case 38: goto L_0x1187;
                case 39: goto L_0x115f;
                case 40: goto L_0x1137;
                case 41: goto L_0x1120;
                case 42: goto L_0x10fd;
                case 43: goto L_0x10d8;
                case 44: goto L_0x10b3;
                case 45: goto L_0x108e;
                case 46: goto L_0x1069;
                case 47: goto L_0x1044;
                case 48: goto L_0x0fc2;
                case 49: goto L_0x0f9b;
                case 50: goto L_0x0var_;
                case 51: goto L_0x0f4d;
                case 52: goto L_0x0f2b;
                case 53: goto L_0x0var_;
                case 54: goto L_0x0ee5;
                case 55: goto L_0x0ebd;
                case 56: goto L_0x0e96;
                case 57: goto L_0x0e6e;
                case 58: goto L_0x0e54;
                case 59: goto L_0x0e54;
                case 60: goto L_0x0e3a;
                case 61: goto L_0x0e20;
                case 62: goto L_0x0e01;
                case 63: goto L_0x0de7;
                case 64: goto L_0x0dcd;
                case 65: goto L_0x0db3;
                case 66: goto L_0x0d99;
                case 67: goto L_0x0d7f;
                case 68: goto L_0x0d51;
                case 69: goto L_0x0d24;
                case 70: goto L_0x0cf7;
                case 71: goto L_0x0cdb;
                case 72: goto L_0x0ca4;
                case 73: goto L_0x0CLASSNAME;
                case 74: goto L_0x0CLASSNAME;
                case 75: goto L_0x0c1a;
                case 76: goto L_0x0beb;
                case 77: goto L_0x0bbc;
                case 78: goto L_0x0b40;
                case 79: goto L_0x0b11;
                case 80: goto L_0x0ad8;
                case 81: goto L_0x0aa3;
                case 82: goto L_0x0a73;
                case 83: goto L_0x0a48;
                case 84: goto L_0x0a1d;
                case 85: goto L_0x09f0;
                case 86: goto L_0x09c3;
                case 87: goto L_0x0996;
                case 88: goto L_0x097b;
                case 89: goto L_0x0975;
                case 90: goto L_0x0975;
                case 91: goto L_0x0975;
                case 92: goto L_0x0975;
                case 93: goto L_0x0975;
                case 94: goto L_0x0975;
                case 95: goto L_0x0975;
                case 96: goto L_0x0975;
                case 97: goto L_0x0975;
                default: goto L_0x096d;
            };
     */
    /* JADX WARNING: Missing block: B:594:0x096d, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:597:0x0975, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:599:?, code skipped:
            r7 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r21 = r1;
            r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:600:0x0993, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:601:0x0996, code skipped:
            if (r1 == 0) goto L_0x09b0;
     */
    /* JADX WARNING: Missing block: B:602:0x0998, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:603:0x09b0, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:604:0x09c3, code skipped:
            if (r1 == 0) goto L_0x09dd;
     */
    /* JADX WARNING: Missing block: B:605:0x09c5, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:606:0x09dd, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:607:0x09f0, code skipped:
            if (r1 == 0) goto L_0x0a0a;
     */
    /* JADX WARNING: Missing block: B:608:0x09f2, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:609:0x0a0a, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:610:0x0a1d, code skipped:
            if (r1 == 0) goto L_0x0a36;
     */
    /* JADX WARNING: Missing block: B:611:0x0a1f, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:612:0x0a36, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:613:0x0a48, code skipped:
            if (r1 == 0) goto L_0x0a61;
     */
    /* JADX WARNING: Missing block: B:614:0x0a4a, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:615:0x0a61, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:616:0x0a73, code skipped:
            if (r1 == 0) goto L_0x0a8c;
     */
    /* JADX WARNING: Missing block: B:617:0x0a75, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:618:0x0a8c, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:619:0x0a9d, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:620:0x0aa3, code skipped:
            if (r1 == 0) goto L_0x0ac1;
     */
    /* JADX WARNING: Missing block: B:621:0x0aa5, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:622:0x0ac1, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:623:0x0ad8, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:624:0x0ada, code skipped:
            if (r1 == 0) goto L_0x0af9;
     */
    /* JADX WARNING: Missing block: B:625:0x0adc, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:626:0x0af9, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:627:0x0b11, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:628:0x0b13, code skipped:
            if (r1 == 0) goto L_0x0b2d;
     */
    /* JADX WARNING: Missing block: B:629:0x0b15, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:630:0x0b2d, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:631:0x0b40, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:632:0x0b42, code skipped:
            if (r1 == 0) goto L_0x0b85;
     */
    /* JADX WARNING: Missing block: B:634:0x0b46, code skipped:
            if (r15.length <= 2) goto L_0x0b6d;
     */
    /* JADX WARNING: Missing block: B:636:0x0b4e, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0b6d;
     */
    /* JADX WARNING: Missing block: B:637:0x0b50, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:638:0x0b6d, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:640:0x0b87, code skipped:
            if (r15.length <= 1) goto L_0x0ba9;
     */
    /* JADX WARNING: Missing block: B:642:0x0b8f, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0ba9;
     */
    /* JADX WARNING: Missing block: B:643:0x0b91, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:644:0x0ba9, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:645:0x0bbc, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:646:0x0bbe, code skipped:
            if (r1 == 0) goto L_0x0bd8;
     */
    /* JADX WARNING: Missing block: B:647:0x0bc0, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:648:0x0bd8, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:649:0x0beb, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:650:0x0bed, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:651:0x0bef, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:652:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:653:0x0c1a, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:654:0x0c1c, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:655:0x0c1e, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:656:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:657:0x0CLASSNAME, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:658:0x0CLASSNAME, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:659:0x0c4b, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:660:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:661:0x0CLASSNAME, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:662:0x0CLASSNAME, code skipped:
            if (r1 == 0) goto L_0x0c8f;
     */
    /* JADX WARNING: Missing block: B:663:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:664:0x0c8f, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:665:0x0ca0, code skipped:
            r21 = r1;
     */
    /* JADX WARNING: Missing block: B:666:0x0ca4, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:667:0x0ca6, code skipped:
            if (r1 == 0) goto L_0x0cc4;
     */
    /* JADX WARNING: Missing block: B:668:0x0ca8, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:669:0x0cc4, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:670:0x0cdb, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:671:0x0cf3, code skipped:
            r21 = r1;
     */
    /* JADX WARNING: Missing block: B:672:0x0cf7, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:673:0x0d24, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:674:0x0d51, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r8, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:675:0x0d7f, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:676:0x0d99, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:677:0x0db3, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:678:0x0dcd, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:679:0x0de7, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:680:0x0e01, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:681:0x0e20, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:682:0x0e3a, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:683:0x0e54, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:684:0x0e6e, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:685:0x0e96, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:686:0x0ebd, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:687:0x0ee5, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:688:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:689:0x0f2b, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:690:0x0f4d, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:691:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:692:0x0f9b, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:693:0x0fbc, code skipped:
            r21 = r1;
            r16 = r8;
     */
    /* JADX WARNING: Missing block: B:694:0x0fc2, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:695:0x0fc6, code skipped:
            if (r15.length <= 2) goto L_0x100d;
     */
    /* JADX WARNING: Missing block: B:697:0x0fce, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x100d;
     */
    /* JADX WARNING: Missing block: B:698:0x0fd0, code skipped:
            r21 = r1;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[2]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:699:0x100d, code skipped:
            r21 = r1;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:700:0x1044, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:701:0x1069, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:702:0x108e, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:703:0x10b3, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:704:0x10d8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:705:0x10fd, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r7 = r15[2];
     */
    /* JADX WARNING: Missing block: B:706:0x1120, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:707:0x1137, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:708:0x115f, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:709:0x1187, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:710:0x11b3, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:711:0x11d3, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:712:0x11f3, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:713:0x1213, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:714:0x1233, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:715:0x1258, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:716:0x127d, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:717:0x129d, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:718:0x12a3, code skipped:
            if (r15.length <= 1) goto L_0x12e2;
     */
    /* JADX WARNING: Missing block: B:720:0x12ab, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x12e2;
     */
    /* JADX WARNING: Missing block: B:721:0x12ad, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:722:0x12e2, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString(r7, NUM);
     */
    /* JADX WARNING: Missing block: B:723:0x12fc, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:724:0x131c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:725:0x133c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:726:0x135c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:727:0x137c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:728:0x139c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:729:0x13b1, code skipped:
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:730:0x13b4, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:731:0x13dd, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:732:0x1406, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r8, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:733:0x142d, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:734:0x1452, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:735:0x1473, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:736:0x1498, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:737:0x14b8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:738:0x14d8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:739:0x14f8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:740:0x151d, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:741:0x1542, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:742:0x1562, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:743:0x1568, code skipped:
            if (r15.length <= 1) goto L_0x15a7;
     */
    /* JADX WARNING: Missing block: B:745:0x1570, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x15a7;
     */
    /* JADX WARNING: Missing block: B:746:0x1572, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:747:0x15a7, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString(r7, NUM);
     */
    /* JADX WARNING: Missing block: B:748:0x15c1, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:749:0x15e1, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:750:0x1601, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:751:0x1617, code skipped:
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:752:0x1618, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:753:0x161b, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:754:0x163b, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:755:0x165a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:756:0x1679, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:757:0x1698, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:758:0x16b6, code skipped:
            r16 = r7;
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:759:0x16b9, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:760:0x16bb, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r7 = r15[1];
     */
    /* JADX WARNING: Missing block: B:761:0x16d8, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x16ee;
     */
    /* JADX WARNING: Missing block: B:762:0x16da, code skipped:
            r1 = new java.lang.StringBuilder();
            r1.append("unhandled loc_key = ");
            r1.append(r9);
            org.telegram.messenger.FileLog.w(r1.toString());
     */
    /* JADX WARNING: Missing block: B:763:0x16ee, code skipped:
            r1 = false;
            r7 = null;
     */
    /* JADX WARNING: Missing block: B:764:0x16f0, code skipped:
            r16 = null;
     */
    /* JADX WARNING: Missing block: B:765:0x16f2, code skipped:
            if (r7 == null) goto L_0x1791;
     */
    /* JADX WARNING: Missing block: B:767:?, code skipped:
            r8 = new org.telegram.tgnet.TLRPC.TL_message();
            r8.id = r10;
            r8.random_id = r3;
     */
    /* JADX WARNING: Missing block: B:768:0x16fd, code skipped:
            if (r16 == null) goto L_0x1702;
     */
    /* JADX WARNING: Missing block: B:769:0x16ff, code skipped:
            r3 = r16;
     */
    /* JADX WARNING: Missing block: B:770:0x1702, code skipped:
            r3 = r7;
     */
    /* JADX WARNING: Missing block: B:771:0x1703, code skipped:
            r8.message = r3;
            r8.date = (int) (r37 / 1000);
     */
    /* JADX WARNING: Missing block: B:772:0x170c, code skipped:
            if (r5 == null) goto L_0x1715;
     */
    /* JADX WARNING: Missing block: B:774:?, code skipped:
            r8.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:775:0x1715, code skipped:
            if (r6 == null) goto L_0x171e;
     */
    /* JADX WARNING: Missing block: B:776:0x1717, code skipped:
            r8.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:778:?, code skipped:
            r8.dialog_id = r12;
     */
    /* JADX WARNING: Missing block: B:779:0x1720, code skipped:
            if (r2 == 0) goto L_0x172e;
     */
    /* JADX WARNING: Missing block: B:781:?, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r8.to_id.channel_id = r2;
     */
    /* JADX WARNING: Missing block: B:782:0x172e, code skipped:
            if (r22 == 0) goto L_0x173e;
     */
    /* JADX WARNING: Missing block: B:783:0x1730, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r8.to_id.chat_id = r22;
     */
    /* JADX WARNING: Missing block: B:785:?, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r8.to_id.user_id = r24;
     */
    /* JADX WARNING: Missing block: B:786:0x174b, code skipped:
            r8.flags |= 256;
            r8.from_id = r21;
     */
    /* JADX WARNING: Missing block: B:787:0x1755, code skipped:
            if (r20 != null) goto L_0x175c;
     */
    /* JADX WARNING: Missing block: B:788:0x1757, code skipped:
            if (r5 == null) goto L_0x175a;
     */
    /* JADX WARNING: Missing block: B:790:0x175a, code skipped:
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:791:0x175c, code skipped:
            r2 = true;
     */
    /* JADX WARNING: Missing block: B:792:0x175d, code skipped:
            r8.mentioned = r2;
            r8.silent = r19;
            r8.from_scheduled = r23;
            r19 = new org.telegram.messenger.MessageObject(r30, r8, r7, r25, r31, r1, r26, r27);
            r1 = new java.util.ArrayList();
            r1.add(r19);
     */
    /* JADX WARNING: Missing block: B:793:0x1786, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:795:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r30).processNewMessages(r1, true, true, r3.countDownLatch);
            r28 = null;
     */
    /* JADX WARNING: Missing block: B:796:0x1791, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:797:0x1794, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:798:0x1795, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:799:0x1798, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:800:0x1799, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:801:0x179c, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:802:0x179f, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:803:0x17a0, code skipped:
            r3 = r1;
     */
    /* JADX WARNING: Missing block: B:804:0x17a2, code skipped:
            r3 = r1;
            r29 = r14;
     */
    /* JADX WARNING: Missing block: B:805:0x17a5, code skipped:
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:806:0x17a7, code skipped:
            r28 = 1;
     */
    /* JADX WARNING: Missing block: B:807:0x17a9, code skipped:
            if (r28 == null) goto L_0x17b0;
     */
    /* JADX WARNING: Missing block: B:808:0x17ab, code skipped:
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:809:0x17b0, code skipped:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);
            org.telegram.tgnet.ConnectionsManager.getInstance(r30).resumeNetworkMaybe();
     */
    /* JADX WARNING: Missing block: B:810:0x17bc, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:812:0x17c4, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:813:0x17c5, code skipped:
            r3 = r1;
            r29 = r14;
            r30 = r15;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:814:0x17cd, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:815:0x17ce, code skipped:
            r3 = r1;
            r29 = r7;
     */
    /* JADX WARNING: Missing block: B:816:0x17d1, code skipped:
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:817:0x17d5, code skipped:
            r3 = r1;
            r29 = r7;
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:822:0x17de, code skipped:
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:826:?, code skipped:
            org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4(r15));
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:827:0x17eb, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:828:0x17ec, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:829:0x17ef, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:830:0x17f0, code skipped:
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:831:0x17f4, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:832:0x17f5, code skipped:
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:833:0x17f9, code skipped:
            r3 = r1;
            r29 = r7;
     */
    /* JADX WARNING: Missing block: B:837:?, code skipped:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ(r15));
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:838:0x1809, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:839:0x180a, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:841:0x180d, code skipped:
            r3 = r1;
            r29 = r7;
            r1 = new org.telegram.tgnet.TLRPC.TL_updateServiceNotification();
            r1.popup = false;
            r1.flags = 2;
            r1.inbox_date = (int) (r37 / 1000);
            r1.message = r5.getString("message");
            r1.type = "announcement";
            r1.media = new org.telegram.tgnet.TLRPC.TL_messageMediaEmpty();
            r2 = new org.telegram.tgnet.TLRPC.TL_updates();
            r2.updates.add(r1);
     */
    /* JADX WARNING: Missing block: B:845:?, code skipped:
            org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo(r15, r2));
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:846:0x1855, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:847:0x1856, code skipped:
            r3 = r1;
            r29 = r7;
            r1 = r11.getInt("dc");
            r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Missing block: B:848:0x186d, code skipped:
            if (r2.length == 2) goto L_0x1875;
     */
    /* JADX WARNING: Missing block: B:849:0x186f, code skipped:
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:850:0x1874, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:851:0x1875, code skipped:
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:852:0x1892, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:853:0x1893, code skipped:
            r0 = th;
     */
    public /* synthetic */ void lambda$null$3$GcmPushListenerService(java.util.Map r36, long r37) {
        /*
        r35 = this;
        r1 = r35;
        r2 = r36;
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x000d;
    L_0x0008:
        r3 = "GCM START PROCESSING";
        org.telegram.messenger.FileLog.d(r3);
    L_0x000d:
        r5 = "p";
        r5 = r2.get(r5);	 Catch:{ all -> 0x18b1 }
        r6 = r5 instanceof java.lang.String;	 Catch:{ all -> 0x18b1 }
        if (r6 != 0) goto L_0x002d;
    L_0x0017:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0024 }
        if (r2 == 0) goto L_0x0020;
    L_0x001b:
        r2 = "GCM DECRYPT ERROR 1";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0024 }
    L_0x0020:
        r35.onDecryptError();	 Catch:{ all -> 0x0024 }
        return;
    L_0x0024:
        r0 = move-exception;
        r3 = r1;
        r2 = -1;
        r9 = 0;
        r14 = 0;
    L_0x0029:
        r15 = -1;
    L_0x002a:
        r1 = r0;
        goto L_0x18b8;
    L_0x002d:
        r5 = (java.lang.String) r5;	 Catch:{ all -> 0x18b1 }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ all -> 0x18b1 }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x18b1 }
        r8 = r5.length;	 Catch:{ all -> 0x18b1 }
        r7.<init>(r8);	 Catch:{ all -> 0x18b1 }
        r7.writeBytes(r5);	 Catch:{ all -> 0x18b1 }
        r8 = 0;
        r7.position(r8);	 Catch:{ all -> 0x18b1 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x18b1 }
        if (r9 != 0) goto L_0x0057;
    L_0x0046:
        r9 = new byte[r6];	 Catch:{ all -> 0x0024 }
        org.telegram.messenger.SharedConfig.pushAuthKeyId = r9;	 Catch:{ all -> 0x0024 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x0024 }
        r9 = org.telegram.messenger.Utilities.computeSHA1(r9);	 Catch:{ all -> 0x0024 }
        r10 = r9.length;	 Catch:{ all -> 0x0024 }
        r10 = r10 - r6;
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x0024 }
        java.lang.System.arraycopy(r9, r10, r11, r8, r6);	 Catch:{ all -> 0x0024 }
    L_0x0057:
        r9 = new byte[r6];	 Catch:{ all -> 0x18b1 }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x18b1 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x18b1 }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ all -> 0x18b1 }
        r12 = 3;
        r13 = 2;
        if (r11 != 0) goto L_0x0092;
    L_0x0067:
        r35.onDecryptError();	 Catch:{ all -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0024 }
        if (r2 == 0) goto L_0x0091;
    L_0x006e:
        r2 = java.util.Locale.US;	 Catch:{ all -> 0x0024 }
        r5 = "GCM DECRYPT ERROR 2 k1=%s k2=%s, key=%s";
        r6 = new java.lang.Object[r12];	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r7);	 Catch:{ all -> 0x0024 }
        r6[r8] = r7;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r9);	 Catch:{ all -> 0x0024 }
        r6[r10] = r7;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x0024 }
        r7 = org.telegram.messenger.Utilities.bytesToHex(r7);	 Catch:{ all -> 0x0024 }
        r6[r13] = r7;	 Catch:{ all -> 0x0024 }
        r2 = java.lang.String.format(r2, r5, r6);	 Catch:{ all -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0024 }
    L_0x0091:
        return;
    L_0x0092:
        r9 = 16;
        r9 = new byte[r9];	 Catch:{ all -> 0x18b1 }
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x18b1 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x18b1 }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ all -> 0x18b1 }
        r14 = r7.buffer;	 Catch:{ all -> 0x18b1 }
        r15 = r11.aesKey;	 Catch:{ all -> 0x18b1 }
        r11 = r11.aesIv;	 Catch:{ all -> 0x18b1 }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ all -> 0x18b1 }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ all -> 0x18b1 }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x18b1 }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ all -> 0x18b1 }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ all -> 0x18b1 }
        r26 = r11.limit();	 Catch:{ all -> 0x18b1 }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ all -> 0x18b1 }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ all -> 0x18b1 }
        if (r5 != 0) goto L_0x00ea;
    L_0x00cf:
        r35.onDecryptError();	 Catch:{ all -> 0x0024 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0024 }
        if (r2 == 0) goto L_0x00e9;
    L_0x00d6:
        r2 = "GCM DECRYPT ERROR 3, key = %s";
        r5 = new java.lang.Object[r10];	 Catch:{ all -> 0x0024 }
        r6 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x0024 }
        r6 = org.telegram.messenger.Utilities.bytesToHex(r6);	 Catch:{ all -> 0x0024 }
        r5[r8] = r6;	 Catch:{ all -> 0x0024 }
        r2 = java.lang.String.format(r2, r5);	 Catch:{ all -> 0x0024 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0024 }
    L_0x00e9:
        return;
    L_0x00ea:
        r5 = r7.readInt32(r10);	 Catch:{ all -> 0x18b1 }
        r5 = new byte[r5];	 Catch:{ all -> 0x18b1 }
        r7.readBytes(r5, r10);	 Catch:{ all -> 0x18b1 }
        r7 = new java.lang.String;	 Catch:{ all -> 0x18b1 }
        r7.<init>(r5);	 Catch:{ all -> 0x18b1 }
        r5 = new org.json.JSONObject;	 Catch:{ all -> 0x18a7 }
        r5.<init>(r7);	 Catch:{ all -> 0x18a7 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ all -> 0x18a7 }
        if (r9 == 0) goto L_0x0113;
    L_0x0105:
        r9 = "loc_key";
        r9 = r5.getString(r9);	 Catch:{ all -> 0x010c }
        goto L_0x0115;
    L_0x010c:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
        r2 = -1;
        r9 = 0;
        goto L_0x0029;
    L_0x0113:
        r9 = "";
    L_0x0115:
        r11 = "custom";
        r11 = r5.get(r11);	 Catch:{ all -> 0x189e }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ all -> 0x189e }
        if (r11 == 0) goto L_0x012c;
    L_0x011f:
        r11 = "custom";
        r11 = r5.getJSONObject(r11);	 Catch:{ all -> 0x0126 }
        goto L_0x0131;
    L_0x0126:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
        r2 = -1;
        goto L_0x0029;
    L_0x012c:
        r11 = new org.json.JSONObject;	 Catch:{ all -> 0x189e }
        r11.<init>();	 Catch:{ all -> 0x189e }
    L_0x0131:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ all -> 0x189e }
        if (r14 == 0) goto L_0x0140;
    L_0x0139:
        r14 = "user_id";
        r14 = r5.get(r14);	 Catch:{ all -> 0x0126 }
        goto L_0x0141;
    L_0x0140:
        r14 = 0;
    L_0x0141:
        if (r14 != 0) goto L_0x014e;
    L_0x0143:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x014e:
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ all -> 0x189e }
        if (r15 == 0) goto L_0x0159;
    L_0x0152:
        r14 = (java.lang.Integer) r14;	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x0159:
        r15 = r14 instanceof java.lang.String;	 Catch:{ all -> 0x189e }
        if (r15 == 0) goto L_0x0168;
    L_0x015d:
        r14 = (java.lang.String) r14;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0172;
    L_0x0168:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x189e }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x189e }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x189e }
    L_0x0172:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x189e }
        r4 = 0;
    L_0x0175:
        if (r4 >= r12) goto L_0x0188;
    L_0x0177:
        r17 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ all -> 0x0126 }
        r6 = r17.getClientUserId();	 Catch:{ all -> 0x0126 }
        if (r6 != r14) goto L_0x0183;
    L_0x0181:
        r15 = r4;
        goto L_0x0188;
    L_0x0183:
        r4 = r4 + 1;
        r6 = 8;
        goto L_0x0175;
    L_0x0188:
        r4 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ all -> 0x1895 }
        r4 = r4.isClientActivated();	 Catch:{ all -> 0x1895 }
        if (r4 != 0) goto L_0x01a7;
    L_0x0192:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x019b;
    L_0x0196:
        r2 = "GCM ACCOUNT NOT ACTIVATED";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x01a1 }
    L_0x019b:
        r2 = r1.countDownLatch;	 Catch:{ all -> 0x01a1 }
        r2.countDown();	 Catch:{ all -> 0x01a1 }
        return;
    L_0x01a1:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
    L_0x01a4:
        r2 = -1;
        goto L_0x002a;
    L_0x01a7:
        r4 = "google.sent_time";
        r2.get(r4);	 Catch:{ all -> 0x1895 }
        r2 = r9.hashCode();	 Catch:{ all -> 0x1895 }
        switch(r2) {
            case -1963663249: goto L_0x01d2;
            case -920689527: goto L_0x01c8;
            case 633004703: goto L_0x01be;
            case 1365673842: goto L_0x01b4;
            default: goto L_0x01b3;
        };
    L_0x01b3:
        goto L_0x01dc;
    L_0x01b4:
        r2 = "GEO_LIVE_PENDING";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x01dc;
    L_0x01bc:
        r2 = 3;
        goto L_0x01dd;
    L_0x01be:
        r2 = "MESSAGE_ANNOUNCEMENT";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x01dc;
    L_0x01c6:
        r2 = 1;
        goto L_0x01dd;
    L_0x01c8:
        r2 = "DC_UPDATE";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x01dc;
    L_0x01d0:
        r2 = 0;
        goto L_0x01dd;
    L_0x01d2:
        r2 = "SESSION_REVOKE";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a1 }
        if (r2 == 0) goto L_0x01dc;
    L_0x01da:
        r2 = 2;
        goto L_0x01dd;
    L_0x01dc:
        r2 = -1;
    L_0x01dd:
        if (r2 == 0) goto L_0x1856;
    L_0x01df:
        if (r2 == r10) goto L_0x180d;
    L_0x01e1:
        if (r2 == r13) goto L_0x17f9;
    L_0x01e3:
        if (r2 == r12) goto L_0x17d5;
    L_0x01e5:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ all -> 0x17cd }
        r19 = 0;
        if (r2 == 0) goto L_0x01f8;
    L_0x01ef:
        r2 = "channel_id";
        r2 = r11.getInt(r2);	 Catch:{ all -> 0x01a1 }
        r4 = -r2;
        r3 = (long) r4;
        goto L_0x01fb;
    L_0x01f8:
        r3 = r19;
        r2 = 0;
    L_0x01fb:
        r14 = "from_id";
        r14 = r11.has(r14);	 Catch:{ all -> 0x17cd }
        if (r14 == 0) goto L_0x0215;
    L_0x0203:
        r3 = "from_id";
        r3 = r11.getInt(r3);	 Catch:{ all -> 0x0211 }
        r14 = r7;
        r6 = (long) r3;
        r33 = r6;
        r6 = r3;
        r3 = r33;
        goto L_0x0217;
    L_0x0211:
        r0 = move-exception;
        r14 = r7;
    L_0x0213:
        r3 = r1;
        goto L_0x01a4;
    L_0x0215:
        r14 = r7;
        r6 = 0;
    L_0x0217:
        r7 = "chat_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x17c4 }
        if (r7 == 0) goto L_0x022a;
    L_0x021f:
        r3 = "chat_id";
        r3 = r11.getInt(r3);	 Catch:{ all -> 0x0228 }
        r4 = -r3;
        r12 = (long) r4;
        goto L_0x022c;
    L_0x0228:
        r0 = move-exception;
        goto L_0x0213;
    L_0x022a:
        r12 = r3;
        r3 = 0;
    L_0x022c:
        r4 = "encryption_id";
        r4 = r11.has(r4);	 Catch:{ all -> 0x17c4 }
        if (r4 == 0) goto L_0x023e;
    L_0x0234:
        r4 = "encryption_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        r12 = (long) r4;
        r4 = 32;
        r12 = r12 << r4;
    L_0x023e:
        r4 = "schedule";
        r4 = r11.has(r4);	 Catch:{ all -> 0x17c4 }
        if (r4 == 0) goto L_0x0250;
    L_0x0246:
        r4 = "schedule";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        if (r4 != r10) goto L_0x0250;
    L_0x024e:
        r4 = 1;
        goto L_0x0251;
    L_0x0250:
        r4 = 0;
    L_0x0251:
        r21 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r21 != 0) goto L_0x0262;
    L_0x0255:
        r7 = "ENCRYPTED_MESSAGE";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x0228 }
        if (r7 == 0) goto L_0x0262;
    L_0x025d:
        r12 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
    L_0x0262:
        r7 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r7 == 0) goto L_0x17a2;
    L_0x0266:
        r7 = "READ_HISTORY";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x17c4 }
        r10 = " for dialogId = ";
        if (r7 == 0) goto L_0x02de;
    L_0x0270:
        r4 = "max_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0228 }
        r5.<init>();	 Catch:{ all -> 0x0228 }
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0228 }
        if (r7 == 0) goto L_0x0299;
    L_0x027f:
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0228 }
        r7.<init>();	 Catch:{ all -> 0x0228 }
        r8 = "GCM received read notification max_id = ";
        r7.append(r8);	 Catch:{ all -> 0x0228 }
        r7.append(r4);	 Catch:{ all -> 0x0228 }
        r7.append(r10);	 Catch:{ all -> 0x0228 }
        r7.append(r12);	 Catch:{ all -> 0x0228 }
        r7 = r7.toString();	 Catch:{ all -> 0x0228 }
        org.telegram.messenger.FileLog.d(r7);	 Catch:{ all -> 0x0228 }
    L_0x0299:
        if (r2 == 0) goto L_0x02a8;
    L_0x029b:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ all -> 0x0228 }
        r3.<init>();	 Catch:{ all -> 0x0228 }
        r3.channel_id = r2;	 Catch:{ all -> 0x0228 }
        r3.max_id = r4;	 Catch:{ all -> 0x0228 }
        r5.add(r3);	 Catch:{ all -> 0x0228 }
        goto L_0x02cb;
    L_0x02a8:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ all -> 0x0228 }
        r2.<init>();	 Catch:{ all -> 0x0228 }
        if (r6 == 0) goto L_0x02bb;
    L_0x02af:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x0228 }
        r3.<init>();	 Catch:{ all -> 0x0228 }
        r2.peer = r3;	 Catch:{ all -> 0x0228 }
        r3 = r2.peer;	 Catch:{ all -> 0x0228 }
        r3.user_id = r6;	 Catch:{ all -> 0x0228 }
        goto L_0x02c6;
    L_0x02bb:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x0228 }
        r6.<init>();	 Catch:{ all -> 0x0228 }
        r2.peer = r6;	 Catch:{ all -> 0x0228 }
        r6 = r2.peer;	 Catch:{ all -> 0x0228 }
        r6.chat_id = r3;	 Catch:{ all -> 0x0228 }
    L_0x02c6:
        r2.max_id = r4;	 Catch:{ all -> 0x0228 }
        r5.add(r2);	 Catch:{ all -> 0x0228 }
    L_0x02cb:
        r16 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r18 = 0;
        r19 = 0;
        r20 = 0;
        r21 = 0;
        r17 = r5;
        r16.processUpdateArray(r17, r18, r19, r20, r21);	 Catch:{ all -> 0x0228 }
        goto L_0x17a2;
    L_0x02de:
        r7 = "MESSAGE_DELETED";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x17c4 }
        r8 = "messages";
        if (r7 == 0) goto L_0x034b;
    L_0x02e8:
        r3 = r11.getString(r8);	 Catch:{ all -> 0x0228 }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ all -> 0x0228 }
        r4 = new android.util.SparseArray;	 Catch:{ all -> 0x0228 }
        r4.<init>();	 Catch:{ all -> 0x0228 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0228 }
        r5.<init>();	 Catch:{ all -> 0x0228 }
        r6 = 0;
    L_0x02fd:
        r7 = r3.length;	 Catch:{ all -> 0x0228 }
        if (r6 >= r7) goto L_0x030c;
    L_0x0300:
        r7 = r3[r6];	 Catch:{ all -> 0x0228 }
        r7 = org.telegram.messenger.Utilities.parseInt(r7);	 Catch:{ all -> 0x0228 }
        r5.add(r7);	 Catch:{ all -> 0x0228 }
        r6 = r6 + 1;
        goto L_0x02fd;
    L_0x030c:
        r4.put(r2, r5);	 Catch:{ all -> 0x0228 }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ all -> 0x0228 }
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x0228 }
        r3.deleteMessagesByPush(r12, r5, r2);	 Catch:{ all -> 0x0228 }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x0228 }
        if (r2 == 0) goto L_0x17a2;
    L_0x0321:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0228 }
        r2.<init>();	 Catch:{ all -> 0x0228 }
        r3 = "GCM received ";
        r2.append(r3);	 Catch:{ all -> 0x0228 }
        r2.append(r9);	 Catch:{ all -> 0x0228 }
        r2.append(r10);	 Catch:{ all -> 0x0228 }
        r2.append(r12);	 Catch:{ all -> 0x0228 }
        r3 = " mids = ";
        r2.append(r3);	 Catch:{ all -> 0x0228 }
        r3 = ",";
        r3 = android.text.TextUtils.join(r3, r5);	 Catch:{ all -> 0x0228 }
        r2.append(r3);	 Catch:{ all -> 0x0228 }
        r2 = r2.toString();	 Catch:{ all -> 0x0228 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x0228 }
        goto L_0x17a2;
    L_0x034b:
        r7 = android.text.TextUtils.isEmpty(r9);	 Catch:{ all -> 0x17c4 }
        if (r7 != 0) goto L_0x17a2;
    L_0x0351:
        r7 = "msg_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x17c4 }
        if (r7 == 0) goto L_0x0362;
    L_0x0359:
        r7 = "msg_id";
        r7 = r11.getInt(r7);	 Catch:{ all -> 0x0228 }
        r29 = r14;
        goto L_0x0365;
    L_0x0362:
        r29 = r14;
        r7 = 0;
    L_0x0365:
        r14 = "random_id";
        r14 = r11.has(r14);	 Catch:{ all -> 0x179f }
        if (r14 == 0) goto L_0x0389;
    L_0x036d:
        r14 = "random_id";
        r14 = r11.getString(r14);	 Catch:{ all -> 0x0383 }
        r14 = org.telegram.messenger.Utilities.parseLong(r14);	 Catch:{ all -> 0x0383 }
        r22 = r14.longValue();	 Catch:{ all -> 0x0383 }
        r14 = r4;
        r33 = r22;
        r22 = r3;
        r3 = r33;
        goto L_0x038e;
    L_0x0383:
        r0 = move-exception;
        r3 = r1;
        r14 = r29;
        goto L_0x01a4;
    L_0x0389:
        r22 = r3;
        r14 = r4;
        r3 = r19;
    L_0x038e:
        if (r7 == 0) goto L_0x03d3;
    L_0x0390:
        r23 = r14;
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x03ca }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x03ca }
        r1 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x03ca }
        r1 = r14.get(r1);	 Catch:{ all -> 0x03ca }
        r1 = (java.lang.Integer) r1;	 Catch:{ all -> 0x03ca }
        if (r1 != 0) goto L_0x03c1;
    L_0x03a4:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x03ca }
        r14 = 0;
        r1 = r1.getDialogReadMax(r14, r12);	 Catch:{ all -> 0x03ca }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x03ca }
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x03ca }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x03ca }
        r24 = r6;
        r6 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x03ca }
        r14.put(r6, r1);	 Catch:{ all -> 0x03ca }
        goto L_0x03c3;
    L_0x03c1:
        r24 = r6;
    L_0x03c3:
        r1 = r1.intValue();	 Catch:{ all -> 0x03ca }
        if (r7 <= r1) goto L_0x03e7;
    L_0x03c9:
        goto L_0x03e5;
    L_0x03ca:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r29;
        goto L_0x18b8;
    L_0x03d3:
        r24 = r6;
        r23 = r14;
        r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1));
        if (r1 == 0) goto L_0x03e7;
    L_0x03db:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x03ca }
        r1 = r1.checkMessageByRandomId(r3);	 Catch:{ all -> 0x03ca }
        if (r1 != 0) goto L_0x03e7;
    L_0x03e5:
        r1 = 1;
        goto L_0x03e8;
    L_0x03e7:
        r1 = 0;
    L_0x03e8:
        if (r1 == 0) goto L_0x179c;
    L_0x03ea:
        r1 = "chat_from_id";
        r1 = r11.has(r1);	 Catch:{ all -> 0x1798 }
        if (r1 == 0) goto L_0x03f9;
    L_0x03f2:
        r1 = "chat_from_id";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x03ca }
        goto L_0x03fa;
    L_0x03f9:
        r1 = 0;
    L_0x03fa:
        r6 = "mention";
        r6 = r11.has(r6);	 Catch:{ all -> 0x1798 }
        if (r6 == 0) goto L_0x040c;
    L_0x0402:
        r6 = "mention";
        r6 = r11.getInt(r6);	 Catch:{ all -> 0x03ca }
        if (r6 == 0) goto L_0x040c;
    L_0x040a:
        r6 = 1;
        goto L_0x040d;
    L_0x040c:
        r6 = 0;
    L_0x040d:
        r14 = "silent";
        r14 = r11.has(r14);	 Catch:{ all -> 0x1798 }
        if (r14 == 0) goto L_0x0421;
    L_0x0415:
        r14 = "silent";
        r14 = r11.getInt(r14);	 Catch:{ all -> 0x03ca }
        if (r14 == 0) goto L_0x0421;
    L_0x041d:
        r30 = r15;
        r14 = 1;
        goto L_0x0424;
    L_0x0421:
        r30 = r15;
        r14 = 0;
    L_0x0424:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ all -> 0x1794 }
        if (r15 == 0) goto L_0x0456;
    L_0x042c:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ all -> 0x044b }
        r15 = r5.length();	 Catch:{ all -> 0x044b }
        r15 = new java.lang.String[r15];	 Catch:{ all -> 0x044b }
        r20 = r6;
        r19 = r14;
        r14 = 0;
    L_0x043d:
        r6 = r15.length;	 Catch:{ all -> 0x044b }
        if (r14 >= r6) goto L_0x0449;
    L_0x0440:
        r6 = r5.getString(r14);	 Catch:{ all -> 0x044b }
        r15[r14] = r6;	 Catch:{ all -> 0x044b }
        r14 = r14 + 1;
        goto L_0x043d;
    L_0x0449:
        r5 = 0;
        goto L_0x045c;
    L_0x044b:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r29;
        r15 = r30;
        goto L_0x18b8;
    L_0x0456:
        r20 = r6;
        r19 = r14;
        r5 = 0;
        r15 = 0;
    L_0x045c:
        r6 = r15[r5];	 Catch:{ all -> 0x1794 }
        r5 = "edit_date";
        r27 = r11.has(r5);	 Catch:{ all -> 0x1794 }
        r5 = "CHAT_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x1794 }
        if (r5 == 0) goto L_0x047a;
    L_0x046c:
        if (r2 == 0) goto L_0x0470;
    L_0x046e:
        r5 = 1;
        goto L_0x0471;
    L_0x0470:
        r5 = 0;
    L_0x0471:
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r11 = r6;
        r26 = 0;
        r6 = r5;
        r5 = 0;
        goto L_0x04a2;
    L_0x047a:
        r5 = "PINNED_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x1794 }
        if (r5 == 0) goto L_0x048e;
    L_0x0482:
        if (r1 == 0) goto L_0x0486;
    L_0x0484:
        r5 = 1;
        goto L_0x0487;
    L_0x0486:
        r5 = 0;
    L_0x0487:
        r14 = r6;
        r11 = 0;
        r26 = 0;
        r6 = r5;
        r5 = 1;
        goto L_0x04a2;
    L_0x048e:
        r5 = "CHANNEL_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x1794 }
        r14 = r6;
        if (r5 == 0) goto L_0x049d;
    L_0x0497:
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r26 = 1;
        goto L_0x04a2;
    L_0x049d:
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r26 = 0;
    L_0x04a2:
        r25 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x1794 }
        if (r25 == 0) goto L_0x04cd;
    L_0x04a6:
        r25 = r14;
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044b }
        r14.<init>();	 Catch:{ all -> 0x044b }
        r31 = r11;
        r11 = "GCM received message notification ";
        r14.append(r11);	 Catch:{ all -> 0x044b }
        r14.append(r9);	 Catch:{ all -> 0x044b }
        r14.append(r10);	 Catch:{ all -> 0x044b }
        r14.append(r12);	 Catch:{ all -> 0x044b }
        r10 = " mid = ";
        r14.append(r10);	 Catch:{ all -> 0x044b }
        r14.append(r7);	 Catch:{ all -> 0x044b }
        r10 = r14.toString();	 Catch:{ all -> 0x044b }
        org.telegram.messenger.FileLog.d(r10);	 Catch:{ all -> 0x044b }
        goto L_0x04d1;
    L_0x04cd:
        r31 = r11;
        r25 = r14;
    L_0x04d1:
        r10 = r9.hashCode();	 Catch:{ all -> 0x1794 }
        switch(r10) {
            case -2100047043: goto L_0x0956;
            case -2091498420: goto L_0x094b;
            case -2053872415: goto L_0x0940;
            case -2039746363: goto L_0x0935;
            case -2023218804: goto L_0x092a;
            case -1979538588: goto L_0x091f;
            case -1979536003: goto L_0x0914;
            case -1979535888: goto L_0x0909;
            case -1969004705: goto L_0x08fe;
            case -1946699248: goto L_0x08f2;
            case -1528047021: goto L_0x08e6;
            case -1493579426: goto L_0x08da;
            case -1482481933: goto L_0x08ce;
            case -1480102982: goto L_0x08c3;
            case -1478041834: goto L_0x08b7;
            case -1474543101: goto L_0x08ac;
            case -1465695932: goto L_0x08a0;
            case -1374906292: goto L_0x0894;
            case -1372940586: goto L_0x0888;
            case -1264245338: goto L_0x087c;
            case -1236086700: goto L_0x0870;
            case -1236077786: goto L_0x0864;
            case -1235796237: goto L_0x0858;
            case -1235686303: goto L_0x084d;
            case -1198046100: goto L_0x0842;
            case -1124254527: goto L_0x0836;
            case -1085137927: goto L_0x082a;
            case -1084856378: goto L_0x081e;
            case -1084746444: goto L_0x0812;
            case -819729482: goto L_0x0806;
            case -772141857: goto L_0x07fa;
            case -638310039: goto L_0x07ee;
            case -590403924: goto L_0x07e2;
            case -589196239: goto L_0x07d6;
            case -589193654: goto L_0x07ca;
            case -589193539: goto L_0x07be;
            case -440169325: goto L_0x07b2;
            case -412748110: goto L_0x07a6;
            case -228518075: goto L_0x079a;
            case -213586509: goto L_0x078e;
            case -115582002: goto L_0x0782;
            case -112621464: goto L_0x0776;
            case -108522133: goto L_0x076a;
            case -107572034: goto L_0x075f;
            case -40534265: goto L_0x0753;
            case 65254746: goto L_0x0747;
            case 141040782: goto L_0x073b;
            case 309993049: goto L_0x072f;
            case 309995634: goto L_0x0723;
            case 309995749: goto L_0x0717;
            case 320532812: goto L_0x070b;
            case 328933854: goto L_0x06ff;
            case 331340546: goto L_0x06f3;
            case 344816990: goto L_0x06e7;
            case 346878138: goto L_0x06db;
            case 350376871: goto L_0x06cf;
            case 615714517: goto L_0x06c4;
            case 715508879: goto L_0x06b8;
            case 728985323: goto L_0x06ac;
            case 731046471: goto L_0x06a0;
            case 734545204: goto L_0x0694;
            case 802032552: goto L_0x0688;
            case 991498806: goto L_0x067c;
            case 1007364121: goto L_0x0670;
            case 1019917311: goto L_0x0664;
            case 1019926225: goto L_0x0658;
            case 1020207774: goto L_0x064c;
            case 1020317708: goto L_0x0640;
            case 1060349560: goto L_0x0634;
            case 1060358474: goto L_0x0628;
            case 1060640023: goto L_0x061c;
            case 1060749957: goto L_0x0611;
            case 1073049781: goto L_0x0605;
            case 1078101399: goto L_0x05f9;
            case 1110103437: goto L_0x05ed;
            case 1160762272: goto L_0x05e1;
            case 1172918249: goto L_0x05d5;
            case 1234591620: goto L_0x05c9;
            case 1281128640: goto L_0x05bd;
            case 1281131225: goto L_0x05b1;
            case 1281131340: goto L_0x05a5;
            case 1310789062: goto L_0x059a;
            case 1333118583: goto L_0x058e;
            case 1361447897: goto L_0x0582;
            case 1498266155: goto L_0x0576;
            case 1533804208: goto L_0x056a;
            case 1547988151: goto L_0x055e;
            case 1561464595: goto L_0x0552;
            case 1563525743: goto L_0x0546;
            case 1567024476: goto L_0x053a;
            case 1810705077: goto L_0x052e;
            case 1815177512: goto L_0x0522;
            case 1963241394: goto L_0x0516;
            case 2014789757: goto L_0x050a;
            case 2022049433: goto L_0x04fe;
            case 2048733346: goto L_0x04f2;
            case 2099392181: goto L_0x04e6;
            case 2140162142: goto L_0x04da;
            default: goto L_0x04d8;
        };
    L_0x04d8:
        goto L_0x0961;
    L_0x04da:
        r10 = "CHAT_MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x04e2:
        r10 = 53;
        goto L_0x0962;
    L_0x04e6:
        r10 = "CHANNEL_MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x04ee:
        r10 = 39;
        goto L_0x0962;
    L_0x04f2:
        r10 = "CHANNEL_MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x04fa:
        r10 = 25;
        goto L_0x0962;
    L_0x04fe:
        r10 = "PINNED_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0506:
        r10 = 80;
        goto L_0x0962;
    L_0x050a:
        r10 = "CHAT_PHOTO_EDITED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0512:
        r10 = 61;
        goto L_0x0962;
    L_0x0516:
        r10 = "LOCKED_MESSAGE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x051e:
        r10 = 92;
        goto L_0x0962;
    L_0x0522:
        r10 = "CHANNEL_MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x052a:
        r10 = 41;
        goto L_0x0962;
    L_0x052e:
        r10 = "MESSAGE_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0536:
        r10 = 20;
        goto L_0x0962;
    L_0x053a:
        r10 = "CHAT_MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0542:
        r10 = 45;
        goto L_0x0962;
    L_0x0546:
        r10 = "CHAT_MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x054e:
        r10 = 46;
        goto L_0x0962;
    L_0x0552:
        r10 = "CHAT_MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x055a:
        r10 = 44;
        goto L_0x0962;
    L_0x055e:
        r10 = "CHAT_MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0566:
        r10 = 49;
        goto L_0x0962;
    L_0x056a:
        r10 = "MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0572:
        r10 = 23;
        goto L_0x0962;
    L_0x0576:
        r10 = "PHONE_CALL_MISSED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x057e:
        r10 = 97;
        goto L_0x0962;
    L_0x0582:
        r10 = "MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x058a:
        r10 = 22;
        goto L_0x0962;
    L_0x058e:
        r10 = "CHAT_MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0596:
        r10 = 70;
        goto L_0x0962;
    L_0x059a:
        r10 = "MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05a2:
        r10 = 2;
        goto L_0x0962;
    L_0x05a5:
        r10 = "MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05ad:
        r10 = 16;
        goto L_0x0962;
    L_0x05b1:
        r10 = "MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05b9:
        r10 = 14;
        goto L_0x0962;
    L_0x05bd:
        r10 = "MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05c5:
        r10 = 9;
        goto L_0x0962;
    L_0x05c9:
        r10 = "CHAT_MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05d1:
        r10 = 56;
        goto L_0x0962;
    L_0x05d5:
        r10 = "CHANNEL_MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05dd:
        r10 = 35;
        goto L_0x0962;
    L_0x05e1:
        r10 = "CHAT_MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05e9:
        r10 = 69;
        goto L_0x0962;
    L_0x05ed:
        r10 = "CHAT_MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x05f5:
        r10 = 43;
        goto L_0x0962;
    L_0x05f9:
        r10 = "CHAT_TITLE_EDITED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0601:
        r10 = 60;
        goto L_0x0962;
    L_0x0605:
        r10 = "PINNED_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x060d:
        r10 = 73;
        goto L_0x0962;
    L_0x0611:
        r10 = "MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0619:
        r10 = 0;
        goto L_0x0962;
    L_0x061c:
        r10 = "MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0624:
        r10 = 13;
        goto L_0x0962;
    L_0x0628:
        r10 = "MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0630:
        r10 = 17;
        goto L_0x0962;
    L_0x0634:
        r10 = "MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x063c:
        r10 = 21;
        goto L_0x0962;
    L_0x0640:
        r10 = "CHAT_MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0648:
        r10 = 42;
        goto L_0x0962;
    L_0x064c:
        r10 = "CHAT_MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0654:
        r10 = 51;
        goto L_0x0962;
    L_0x0658:
        r10 = "CHAT_MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0660:
        r10 = 55;
        goto L_0x0962;
    L_0x0664:
        r10 = "CHAT_MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x066c:
        r10 = 68;
        goto L_0x0962;
    L_0x0670:
        r10 = "CHANNEL_MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0678:
        r10 = 19;
        goto L_0x0962;
    L_0x067c:
        r10 = "PINNED_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0684:
        r10 = 83;
        goto L_0x0962;
    L_0x0688:
        r10 = "MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0690:
        r10 = 12;
        goto L_0x0962;
    L_0x0694:
        r10 = "PINNED_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x069c:
        r10 = 75;
        goto L_0x0962;
    L_0x06a0:
        r10 = "PINNED_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06a8:
        r10 = 76;
        goto L_0x0962;
    L_0x06ac:
        r10 = "PINNED_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06b4:
        r10 = 74;
        goto L_0x0962;
    L_0x06b8:
        r10 = "PINNED_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06c0:
        r10 = 79;
        goto L_0x0962;
    L_0x06c4:
        r10 = "MESSAGE_PHOTO_SECRET";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06cc:
        r10 = 4;
        goto L_0x0962;
    L_0x06cf:
        r10 = "CHANNEL_MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06d7:
        r10 = 27;
        goto L_0x0962;
    L_0x06db:
        r10 = "CHANNEL_MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06e3:
        r10 = 28;
        goto L_0x0962;
    L_0x06e7:
        r10 = "CHANNEL_MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06ef:
        r10 = 26;
        goto L_0x0962;
    L_0x06f3:
        r10 = "CHANNEL_MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x06fb:
        r10 = 31;
        goto L_0x0962;
    L_0x06ff:
        r10 = "CHAT_MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0707:
        r10 = 48;
        goto L_0x0962;
    L_0x070b:
        r10 = "MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0713:
        r10 = 24;
        goto L_0x0962;
    L_0x0717:
        r10 = "CHAT_MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x071f:
        r10 = 54;
        goto L_0x0962;
    L_0x0723:
        r10 = "CHAT_MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x072b:
        r10 = 52;
        goto L_0x0962;
    L_0x072f:
        r10 = "CHAT_MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0737:
        r10 = 47;
        goto L_0x0962;
    L_0x073b:
        r10 = "CHAT_LEFT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0743:
        r10 = 65;
        goto L_0x0962;
    L_0x0747:
        r10 = "CHAT_ADD_YOU";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x074f:
        r10 = 59;
        goto L_0x0962;
    L_0x0753:
        r10 = "CHAT_DELETE_MEMBER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x075b:
        r10 = 63;
        goto L_0x0962;
    L_0x075f:
        r10 = "MESSAGE_SCREENSHOT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0767:
        r10 = 7;
        goto L_0x0962;
    L_0x076a:
        r10 = "AUTH_REGION";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0772:
        r10 = 91;
        goto L_0x0962;
    L_0x0776:
        r10 = "CONTACT_JOINED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x077e:
        r10 = 89;
        goto L_0x0962;
    L_0x0782:
        r10 = "CHAT_MESSAGE_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x078a:
        r10 = 57;
        goto L_0x0962;
    L_0x078e:
        r10 = "ENCRYPTION_REQUEST";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0796:
        r10 = 93;
        goto L_0x0962;
    L_0x079a:
        r10 = "MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07a2:
        r10 = 15;
        goto L_0x0962;
    L_0x07a6:
        r10 = "CHAT_DELETE_YOU";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07ae:
        r10 = 64;
        goto L_0x0962;
    L_0x07b2:
        r10 = "AUTH_UNKNOWN";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07ba:
        r10 = 90;
        goto L_0x0962;
    L_0x07be:
        r10 = "PINNED_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07c6:
        r10 = 87;
        goto L_0x0962;
    L_0x07ca:
        r10 = "PINNED_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07d2:
        r10 = 82;
        goto L_0x0962;
    L_0x07d6:
        r10 = "PINNED_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07de:
        r10 = 77;
        goto L_0x0962;
    L_0x07e2:
        r10 = "PINNED_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07ea:
        r10 = 85;
        goto L_0x0962;
    L_0x07ee:
        r10 = "CHANNEL_MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x07f6:
        r10 = 30;
        goto L_0x0962;
    L_0x07fa:
        r10 = "PHONE_CALL_REQUEST";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0802:
        r10 = 95;
        goto L_0x0962;
    L_0x0806:
        r10 = "PINNED_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x080e:
        r10 = 78;
        goto L_0x0962;
    L_0x0812:
        r10 = "PINNED_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x081a:
        r10 = 72;
        goto L_0x0962;
    L_0x081e:
        r10 = "PINNED_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0826:
        r10 = 81;
        goto L_0x0962;
    L_0x082a:
        r10 = "PINNED_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0832:
        r10 = 84;
        goto L_0x0962;
    L_0x0836:
        r10 = "CHAT_MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x083e:
        r10 = 50;
        goto L_0x0962;
    L_0x0842:
        r10 = "MESSAGE_VIDEO_SECRET";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x084a:
        r10 = 6;
        goto L_0x0962;
    L_0x084d:
        r10 = "CHANNEL_MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0855:
        r10 = 1;
        goto L_0x0962;
    L_0x0858:
        r10 = "CHANNEL_MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0860:
        r10 = 33;
        goto L_0x0962;
    L_0x0864:
        r10 = "CHANNEL_MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x086c:
        r10 = 37;
        goto L_0x0962;
    L_0x0870:
        r10 = "CHANNEL_MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0878:
        r10 = 38;
        goto L_0x0962;
    L_0x087c:
        r10 = "PINNED_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0884:
        r10 = 86;
        goto L_0x0962;
    L_0x0888:
        r10 = "CHAT_RETURNED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0890:
        r10 = 66;
        goto L_0x0962;
    L_0x0894:
        r10 = "ENCRYPTED_MESSAGE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x089c:
        r10 = 88;
        goto L_0x0962;
    L_0x08a0:
        r10 = "ENCRYPTION_ACCEPT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08a8:
        r10 = 94;
        goto L_0x0962;
    L_0x08ac:
        r10 = "MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08b4:
        r10 = 5;
        goto L_0x0962;
    L_0x08b7:
        r10 = "MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08bf:
        r10 = 8;
        goto L_0x0962;
    L_0x08c3:
        r10 = "MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08cb:
        r10 = 3;
        goto L_0x0962;
    L_0x08ce:
        r10 = "MESSAGE_MUTED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08d6:
        r10 = 96;
        goto L_0x0962;
    L_0x08da:
        r10 = "MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08e2:
        r10 = 11;
        goto L_0x0962;
    L_0x08e6:
        r10 = "CHAT_MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08ee:
        r10 = 71;
        goto L_0x0962;
    L_0x08f2:
        r10 = "CHAT_JOINED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x08fa:
        r10 = 67;
        goto L_0x0962;
    L_0x08fe:
        r10 = "CHAT_ADD_MEMBER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0906:
        r10 = 62;
        goto L_0x0962;
    L_0x0909:
        r10 = "CHANNEL_MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0911:
        r10 = 36;
        goto L_0x0962;
    L_0x0914:
        r10 = "CHANNEL_MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x091c:
        r10 = 34;
        goto L_0x0962;
    L_0x091f:
        r10 = "CHANNEL_MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0927:
        r10 = 29;
        goto L_0x0962;
    L_0x092a:
        r10 = "CHANNEL_MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0932:
        r10 = 40;
        goto L_0x0962;
    L_0x0935:
        r10 = "MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x093d:
        r10 = 10;
        goto L_0x0962;
    L_0x0940:
        r10 = "CHAT_CREATED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0948:
        r10 = 58;
        goto L_0x0962;
    L_0x094b:
        r10 = "CHANNEL_MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x0953:
        r10 = 32;
        goto L_0x0962;
    L_0x0956:
        r10 = "MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044b }
        if (r10 == 0) goto L_0x0961;
    L_0x095e:
        r10 = 18;
        goto L_0x0962;
    L_0x0961:
        r10 = -1;
    L_0x0962:
        r14 = "ChannelMessageFew";
        r11 = " ";
        r32 = r7;
        r7 = "AttachSticker";
        switch(r10) {
            case 0: goto L_0x16bb;
            case 1: goto L_0x16bb;
            case 2: goto L_0x1698;
            case 3: goto L_0x1679;
            case 4: goto L_0x165a;
            case 5: goto L_0x163b;
            case 6: goto L_0x161b;
            case 7: goto L_0x1601;
            case 8: goto L_0x15e1;
            case 9: goto L_0x15c1;
            case 10: goto L_0x1562;
            case 11: goto L_0x1542;
            case 12: goto L_0x151d;
            case 13: goto L_0x14f8;
            case 14: goto L_0x14d8;
            case 15: goto L_0x14b8;
            case 16: goto L_0x1498;
            case 17: goto L_0x1473;
            case 18: goto L_0x1452;
            case 19: goto L_0x1452;
            case 20: goto L_0x142d;
            case 21: goto L_0x1406;
            case 22: goto L_0x13dd;
            case 23: goto L_0x13b4;
            case 24: goto L_0x139c;
            case 25: goto L_0x137c;
            case 26: goto L_0x135c;
            case 27: goto L_0x133c;
            case 28: goto L_0x131c;
            case 29: goto L_0x12fc;
            case 30: goto L_0x129d;
            case 31: goto L_0x127d;
            case 32: goto L_0x1258;
            case 33: goto L_0x1233;
            case 34: goto L_0x1213;
            case 35: goto L_0x11f3;
            case 36: goto L_0x11d3;
            case 37: goto L_0x11b3;
            case 38: goto L_0x1187;
            case 39: goto L_0x115f;
            case 40: goto L_0x1137;
            case 41: goto L_0x1120;
            case 42: goto L_0x10fd;
            case 43: goto L_0x10d8;
            case 44: goto L_0x10b3;
            case 45: goto L_0x108e;
            case 46: goto L_0x1069;
            case 47: goto L_0x1044;
            case 48: goto L_0x0fc2;
            case 49: goto L_0x0f9b;
            case 50: goto L_0x0var_;
            case 51: goto L_0x0f4d;
            case 52: goto L_0x0f2b;
            case 53: goto L_0x0var_;
            case 54: goto L_0x0ee5;
            case 55: goto L_0x0ebd;
            case 56: goto L_0x0e96;
            case 57: goto L_0x0e6e;
            case 58: goto L_0x0e54;
            case 59: goto L_0x0e54;
            case 60: goto L_0x0e3a;
            case 61: goto L_0x0e20;
            case 62: goto L_0x0e01;
            case 63: goto L_0x0de7;
            case 64: goto L_0x0dcd;
            case 65: goto L_0x0db3;
            case 66: goto L_0x0d99;
            case 67: goto L_0x0d7f;
            case 68: goto L_0x0d51;
            case 69: goto L_0x0d24;
            case 70: goto L_0x0cf7;
            case 71: goto L_0x0cdb;
            case 72: goto L_0x0ca4;
            case 73: goto L_0x0CLASSNAME;
            case 74: goto L_0x0CLASSNAME;
            case 75: goto L_0x0c1a;
            case 76: goto L_0x0beb;
            case 77: goto L_0x0bbc;
            case 78: goto L_0x0b40;
            case 79: goto L_0x0b11;
            case 80: goto L_0x0ad8;
            case 81: goto L_0x0aa3;
            case 82: goto L_0x0a73;
            case 83: goto L_0x0a48;
            case 84: goto L_0x0a1d;
            case 85: goto L_0x09f0;
            case 86: goto L_0x09c3;
            case 87: goto L_0x0996;
            case 88: goto L_0x097b;
            case 89: goto L_0x0975;
            case 90: goto L_0x0975;
            case 91: goto L_0x0975;
            case 92: goto L_0x0975;
            case 93: goto L_0x0975;
            case 94: goto L_0x0975;
            case 95: goto L_0x0975;
            case 96: goto L_0x0975;
            case 97: goto L_0x0975;
            default: goto L_0x096d;
        };
    L_0x096d:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x1794 }
        goto L_0x16d8;
    L_0x0975:
        r21 = r1;
        r10 = r32;
        goto L_0x16ee;
    L_0x097b:
        r7 = "YouHaveNewMessage";
        r8 = NUM; // 0x7f0e0ca1 float:1.8881595E38 double:1.053163754E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        r8 = "SecretChatName";
        r10 = NUM; // 0x7f0e09c6 float:1.8880112E38 double:1.053163393E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);	 Catch:{ all -> 0x044b }
        r21 = r1;
        r25 = r8;
        r10 = r32;
    L_0x0993:
        r1 = 1;
        goto L_0x16f0;
    L_0x0996:
        if (r1 == 0) goto L_0x09b0;
    L_0x0998:
        r7 = "NotificationActionPinnedGif";
        r8 = NUM; // 0x7f0e06d6 float:1.8878587E38 double:1.053163021E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x09b0:
        r7 = "NotificationActionPinnedGifChannel";
        r8 = NUM; // 0x7f0e06d7 float:1.887859E38 double:1.0531630217E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044b }
        r11[r10] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x09c3:
        if (r1 == 0) goto L_0x09dd;
    L_0x09c5:
        r7 = "NotificationActionPinnedInvoice";
        r8 = NUM; // 0x7f0e06d8 float:1.8878591E38 double:1.053163022E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x09dd:
        r7 = "NotificationActionPinnedInvoiceChannel";
        r8 = NUM; // 0x7f0e06d9 float:1.8878593E38 double:1.0531630227E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044b }
        r11[r10] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x09f0:
        if (r1 == 0) goto L_0x0a0a;
    L_0x09f2:
        r7 = "NotificationActionPinnedGameScore";
        r8 = NUM; // 0x7f0e06d0 float:1.8878575E38 double:1.0531630183E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a0a:
        r7 = "NotificationActionPinnedGameScoreChannel";
        r8 = NUM; // 0x7f0e06d1 float:1.8878577E38 double:1.053163019E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044b }
        r11[r10] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a1d:
        if (r1 == 0) goto L_0x0a36;
    L_0x0a1f:
        r7 = "NotificationActionPinnedGame";
        r8 = NUM; // 0x7f0e06ce float:1.887857E38 double:1.0531630173E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a36:
        r7 = "NotificationActionPinnedGameChannel";
        r8 = NUM; // 0x7f0e06cf float:1.8878573E38 double:1.053163018E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044b }
        r11[r10] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a48:
        if (r1 == 0) goto L_0x0a61;
    L_0x0a4a:
        r7 = "NotificationActionPinnedGeoLive";
        r8 = NUM; // 0x7f0e06d4 float:1.8878583E38 double:1.0531630203E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a61:
        r7 = "NotificationActionPinnedGeoLiveChannel";
        r8 = NUM; // 0x7f0e06d5 float:1.8878585E38 double:1.0531630208E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044b }
        r11[r10] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a73:
        if (r1 == 0) goto L_0x0a8c;
    L_0x0a75:
        r7 = "NotificationActionPinnedGeo";
        r8 = NUM; // 0x7f0e06d2 float:1.8878579E38 double:1.0531630193E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0a8c:
        r7 = "NotificationActionPinnedGeoChannel";
        r8 = NUM; // 0x7f0e06d3 float:1.887858E38 double:1.05316302E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044b }
        r11[r10] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
    L_0x0a9d:
        r21 = r1;
        r10 = r32;
        goto L_0x1618;
    L_0x0aa3:
        if (r1 == 0) goto L_0x0ac1;
    L_0x0aa5:
        r7 = "NotificationActionPinnedPoll2";
        r8 = NUM; // 0x7f0e06e0 float:1.8878607E38 double:1.053163026E-314;
        r10 = 3;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0ac1:
        r7 = "NotificationActionPinnedPollChannel2";
        r8 = NUM; // 0x7f0e06e1 float:1.887861E38 double:1.0531630267E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r10[r11] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044b }
        goto L_0x0a9d;
    L_0x0ad8:
        r10 = r32;
        if (r1 == 0) goto L_0x0af9;
    L_0x0adc:
        r8 = "NotificationActionPinnedContact2";
        r11 = NUM; // 0x7f0e06ca float:1.8878563E38 double:1.0531630153E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0af9:
        r7 = "NotificationActionPinnedContactChannel2";
        r8 = NUM; // 0x7f0e06cb float:1.8878565E38 double:1.053163016E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0b11:
        r10 = r32;
        if (r1 == 0) goto L_0x0b2d;
    L_0x0b15:
        r7 = "NotificationActionPinnedVoice";
        r8 = NUM; // 0x7f0e06ec float:1.8878632E38 double:1.053163032E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0b2d:
        r7 = "NotificationActionPinnedVoiceChannel";
        r8 = NUM; // 0x7f0e06ed float:1.8878634E38 double:1.0531630326E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0b40:
        r10 = r32;
        if (r1 == 0) goto L_0x0b85;
    L_0x0b44:
        r8 = r15.length;	 Catch:{ all -> 0x044b }
        r11 = 2;
        if (r8 <= r11) goto L_0x0b6d;
    L_0x0b48:
        r8 = r15[r11];	 Catch:{ all -> 0x044b }
        r8 = android.text.TextUtils.isEmpty(r8);	 Catch:{ all -> 0x044b }
        if (r8 != 0) goto L_0x0b6d;
    L_0x0b50:
        r8 = "NotificationActionPinnedStickerEmoji";
        r11 = NUM; // 0x7f0e06e6 float:1.887862E38 double:1.053163029E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0b6d:
        r7 = "NotificationActionPinnedSticker";
        r8 = NUM; // 0x7f0e06e4 float:1.8878615E38 double:1.053163028E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0b85:
        r7 = r15.length;	 Catch:{ all -> 0x044b }
        r8 = 1;
        if (r7 <= r8) goto L_0x0ba9;
    L_0x0b89:
        r7 = r15[r8];	 Catch:{ all -> 0x044b }
        r7 = android.text.TextUtils.isEmpty(r7);	 Catch:{ all -> 0x044b }
        if (r7 != 0) goto L_0x0ba9;
    L_0x0b91:
        r7 = "NotificationActionPinnedStickerEmojiChannel";
        r8 = NUM; // 0x7f0e06e7 float:1.8878621E38 double:1.0531630296E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0ba9:
        r7 = "NotificationActionPinnedStickerChannel";
        r8 = NUM; // 0x7f0e06e5 float:1.8878617E38 double:1.0531630287E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0bbc:
        r10 = r32;
        if (r1 == 0) goto L_0x0bd8;
    L_0x0bc0:
        r7 = "NotificationActionPinnedFile";
        r8 = NUM; // 0x7f0e06cc float:1.8878567E38 double:1.0531630163E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0bd8:
        r7 = "NotificationActionPinnedFileChannel";
        r8 = NUM; // 0x7f0e06cd float:1.8878569E38 double:1.053163017E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0beb:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bef:
        r7 = "NotificationActionPinnedRound";
        r8 = NUM; // 0x7f0e06e2 float:1.8878611E38 double:1.053163027E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedRoundChannel";
        r8 = NUM; // 0x7f0e06e3 float:1.8878613E38 double:1.0531630277E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0c1a:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c1e:
        r7 = "NotificationActionPinnedVideo";
        r8 = NUM; // 0x7f0e06ea float:1.8878628E38 double:1.053163031E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedVideoChannel";
        r8 = NUM; // 0x7f0e06eb float:1.887863E38 double:1.0531630316E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c4b:
        r7 = "NotificationActionPinnedPhoto";
        r8 = NUM; // 0x7f0e06de float:1.8878603E38 double:1.053163025E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedPhotoChannel";
        r8 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0CLASSNAME:
        r10 = r32;
        if (r1 == 0) goto L_0x0c8f;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedNoText";
        r8 = NUM; // 0x7f0e06dc float:1.88786E38 double:1.053163024E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0c8f:
        r7 = "NotificationActionPinnedNoTextChannel";
        r8 = NUM; // 0x7f0e06dd float:1.8878601E38 double:1.0531630247E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044b }
    L_0x0ca0:
        r21 = r1;
        goto L_0x1618;
    L_0x0ca4:
        r10 = r32;
        if (r1 == 0) goto L_0x0cc4;
    L_0x0ca8:
        r8 = "NotificationActionPinnedText";
        r11 = NUM; // 0x7f0e06e8 float:1.8878623E38 double:1.05316303E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0cc4:
        r7 = "NotificationActionPinnedTextChannel";
        r8 = NUM; // 0x7f0e06e9 float:1.8878625E38 double:1.0531630306E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0cdb:
        r10 = r32;
        r7 = "NotificationGroupAlbum";
        r8 = NUM; // 0x7f0e06f5 float:1.887865E38 double:1.0531630366E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
    L_0x0cf3:
        r21 = r1;
        goto L_0x0993;
    L_0x0cf7:
        r10 = r32;
        r8 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = "Videos";
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x044b }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044b }
        r15 = r15.intValue();	 Catch:{ all -> 0x044b }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x044b }
        r7[r17] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0cf3;
    L_0x0d24:
        r10 = r32;
        r8 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = "Photos";
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x044b }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044b }
        r15 = r15.intValue();	 Catch:{ all -> 0x044b }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x044b }
        r7[r17] = r14;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0cf3;
    L_0x0d51:
        r10 = r32;
        r11 = "NotificationGroupForwardedFew";
        r14 = NUM; // 0x7f0e06f7 float:1.8878654E38 double:1.0531630375E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x044b }
        r7[r18] = r21;	 Catch:{ all -> 0x044b }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x044b }
        r7[r18] = r21;	 Catch:{ all -> 0x044b }
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x044b }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044b }
        r15 = r15.intValue();	 Catch:{ all -> 0x044b }
        r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15);	 Catch:{ all -> 0x044b }
        r7[r17] = r8;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r11, r14, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0cf3;
    L_0x0d7f:
        r10 = r32;
        r7 = "NotificationGroupAddSelfMega";
        r8 = NUM; // 0x7f0e06f4 float:1.8878648E38 double:1.053163036E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0d99:
        r10 = r32;
        r7 = "NotificationGroupAddSelf";
        r8 = NUM; // 0x7f0e06f3 float:1.8878646E38 double:1.0531630356E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0db3:
        r10 = r32;
        r7 = "NotificationGroupLeftMember";
        r8 = NUM; // 0x7f0e06fa float:1.887866E38 double:1.053163039E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0dcd:
        r10 = r32;
        r7 = "NotificationGroupKickYou";
        r8 = NUM; // 0x7f0e06f9 float:1.8878658E38 double:1.0531630385E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0de7:
        r10 = r32;
        r7 = "NotificationGroupKickMember";
        r8 = NUM; // 0x7f0e06f8 float:1.8878656E38 double:1.053163038E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0e01:
        r10 = r32;
        r8 = "NotificationGroupAddMember";
        r11 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r18;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0e20:
        r10 = r32;
        r7 = "NotificationEditedGroupPhoto";
        r8 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0e3a:
        r10 = r32;
        r7 = "NotificationEditedGroupName";
        r8 = NUM; // 0x7f0e06f0 float:1.887864E38 double:1.053163034E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0e54:
        r10 = r32;
        r7 = "NotificationInvitedToGroup";
        r8 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0e6e:
        r10 = r32;
        r8 = "NotificationMessageGroupInvoice";
        r11 = NUM; // 0x7f0e0710 float:1.8878705E38 double:1.05316305E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        r8 = "PaymentInvoice";
        r11 = NUM; // 0x7f0e0875 float:1.8879429E38 double:1.0531632263E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0e96:
        r10 = r32;
        r8 = "NotificationMessageGroupGameScored";
        r11 = NUM; // 0x7f0e070e float:1.88787E38 double:1.053163049E-314;
        r14 = 4;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044b }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x044b }
        r14[r18] = r21;	 Catch:{ all -> 0x044b }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x044b }
        r14[r18] = r21;	 Catch:{ all -> 0x044b }
        r17 = 2;
        r18 = r15[r17];	 Catch:{ all -> 0x044b }
        r14[r17] = r18;	 Catch:{ all -> 0x044b }
        r7 = 3;
        r15 = r15[r7];	 Catch:{ all -> 0x044b }
        r14[r7] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14);	 Catch:{ all -> 0x044b }
        goto L_0x0ca0;
    L_0x0ebd:
        r10 = r32;
        r8 = "NotificationMessageGroupGame";
        r11 = NUM; // 0x7f0e070d float:1.8878699E38 double:1.0531630484E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        r8 = "AttachGame";
        r11 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0ee5:
        r10 = r32;
        r7 = "NotificationMessageGroupGif";
        r8 = NUM; // 0x7f0e070f float:1.8878703E38 double:1.0531630494E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        r8 = "AttachGif";
        r11 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0var_:
        r10 = r32;
        r7 = "NotificationMessageGroupLiveLocation";
        r8 = NUM; // 0x7f0e0711 float:1.8878707E38 double:1.0531630504E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        r8 = "AttachLiveLocation";
        r11 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0f2b:
        r10 = r32;
        r7 = "NotificationMessageGroupMap";
        r8 = NUM; // 0x7f0e0712 float:1.8878709E38 double:1.053163051E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        r8 = "AttachLocation";
        r11 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0f4d:
        r10 = r32;
        r8 = "NotificationMessageGroupPoll2";
        r11 = NUM; // 0x7f0e0716 float:1.8878717E38 double:1.053163053E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        r8 = "Poll";
        r11 = NUM; // 0x7f0e08e3 float:1.8879652E38 double:1.0531632806E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0var_:
        r10 = r32;
        r8 = "NotificationMessageGroupContact2";
        r11 = NUM; // 0x7f0e070b float:1.8878694E38 double:1.0531630474E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r7[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044b }
        r8 = "AttachContact";
        r11 = NUM; // 0x7f0e014a float:1.8875707E38 double:1.0531623197E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
        goto L_0x0fbc;
    L_0x0f9b:
        r10 = r32;
        r7 = "NotificationMessageGroupAudio";
        r8 = NUM; // 0x7f0e070a float:1.8878692E38 double:1.053163047E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r16;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r15;	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044b }
        r8 = "AttachAudio";
        r11 = NUM; // 0x7f0e0148 float:1.8875703E38 double:1.0531623187E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044b }
    L_0x0fbc:
        r21 = r1;
        r16 = r8;
        goto L_0x16b9;
    L_0x0fc2:
        r10 = r32;
        r14 = r15.length;	 Catch:{ all -> 0x044b }
        r8 = 2;
        if (r14 <= r8) goto L_0x100d;
    L_0x0fc8:
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r8 = android.text.TextUtils.isEmpty(r14);	 Catch:{ all -> 0x044b }
        if (r8 != 0) goto L_0x100d;
    L_0x0fd0:
        r8 = "NotificationMessageGroupStickerEmoji";
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044b }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x044b }
        r14[r18] = r21;	 Catch:{ all -> 0x044b }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x044b }
        r14[r18] = r21;	 Catch:{ all -> 0x044b }
        r17 = 2;
        r18 = r15[r17];	 Catch:{ all -> 0x044b }
        r14[r17] = r18;	 Catch:{ all -> 0x044b }
        r21 = r1;
        r1 = NUM; // 0x7f0e0719 float:1.8878723E38 double:1.0531630543E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r1, r14);	 Catch:{ all -> 0x044b }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044b }
        r8.<init>();	 Catch:{ all -> 0x044b }
        r14 = r15[r17];	 Catch:{ all -> 0x044b }
        r8.append(r14);	 Catch:{ all -> 0x044b }
        r8.append(r11);	 Catch:{ all -> 0x044b }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044b }
        r8.append(r7);	 Catch:{ all -> 0x044b }
        r7 = r8.toString();	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x100d:
        r21 = r1;
        r1 = "NotificationMessageGroupSticker";
        r8 = NUM; // 0x7f0e0718 float:1.887872E38 double:1.053163054E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044b }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x044b }
        r14[r16] = r17;	 Catch:{ all -> 0x044b }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x044b }
        r14[r16] = r17;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044b }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044b }
        r8.<init>();	 Catch:{ all -> 0x044b }
        r14 = r15[r16];	 Catch:{ all -> 0x044b }
        r8.append(r14);	 Catch:{ all -> 0x044b }
        r8.append(r11);	 Catch:{ all -> 0x044b }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044b }
        r8.append(r7);	 Catch:{ all -> 0x044b }
        r7 = r8.toString();	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1044:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupDocument";
        r7 = NUM; // 0x7f0e070c float:1.8878696E38 double:1.053163048E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1069:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupRound";
        r7 = NUM; // 0x7f0e0717 float:1.8878719E38 double:1.0531630534E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x108e:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupVideo";
        r7 = NUM; // 0x7f0e071b float:1.8878727E38 double:1.0531630553E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0160 float:1.8875751E38 double:1.0531623305E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x10b3:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupPhoto";
        r7 = NUM; // 0x7f0e0715 float:1.8878715E38 double:1.0531630524E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e015a float:1.887574E38 double:1.0531623276E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x10d8:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupNoText";
        r7 = NUM; // 0x7f0e0714 float:1.8878713E38 double:1.053163052E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x10fd:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupText";
        r8 = NUM; // 0x7f0e071a float:1.8878725E38 double:1.053163055E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r7[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r7[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r7[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7);	 Catch:{ all -> 0x044b }
        r7 = r15[r11];	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1120:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageAlbum";
        r7 = NUM; // 0x7f0e025e float:1.8876267E38 double:1.053162456E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x1137:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x044b }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x044b }
        r1[r7] = r8;	 Catch:{ all -> 0x044b }
        r7 = "Videos";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x044b }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x044b }
        r11 = r11.intValue();	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x044b }
        r1[r8] = r7;	 Catch:{ all -> 0x044b }
        r7 = NUM; // 0x7f0e0262 float:1.8876275E38 double:1.053162458E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x115f:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x044b }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x044b }
        r1[r7] = r8;	 Catch:{ all -> 0x044b }
        r7 = "Photos";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x044b }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x044b }
        r11 = r11.intValue();	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x044b }
        r1[r8] = r7;	 Catch:{ all -> 0x044b }
        r7 = NUM; // 0x7f0e0262 float:1.8876275E38 double:1.053162458E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x1187:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x044b }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x044b }
        r1[r7] = r8;	 Catch:{ all -> 0x044b }
        r7 = "ForwardedMessageCount";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x044b }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x044b }
        r11 = r11.intValue();	 Catch:{ all -> 0x044b }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x044b }
        r7 = r7.toLowerCase();	 Catch:{ all -> 0x044b }
        r1[r8] = r7;	 Catch:{ all -> 0x044b }
        r7 = NUM; // 0x7f0e0262 float:1.8876275E38 double:1.053162458E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x11b3:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGame";
        r7 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachGame";
        r8 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x11d3:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageGIF";
        r7 = NUM; // 0x7f0e0263 float:1.8876277E38 double:1.0531624585E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachGif";
        r8 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x11f3:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageLiveLocation";
        r7 = NUM; // 0x7f0e0264 float:1.8876279E38 double:1.053162459E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachLiveLocation";
        r8 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1213:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageMap";
        r7 = NUM; // 0x7f0e0265 float:1.887628E38 double:1.0531624595E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachLocation";
        r8 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1233:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessagePoll2";
        r7 = NUM; // 0x7f0e0269 float:1.8876289E38 double:1.0531624615E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "Poll";
        r8 = NUM; // 0x7f0e08e3 float:1.8879652E38 double:1.0531632806E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1258:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageContact2";
        r7 = NUM; // 0x7f0e0260 float:1.887627E38 double:1.053162457E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachContact";
        r8 = NUM; // 0x7f0e014a float:1.8875707E38 double:1.0531623197E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x127d:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageAudio";
        r7 = NUM; // 0x7f0e025f float:1.8876269E38 double:1.0531624565E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachAudio";
        r8 = NUM; // 0x7f0e0148 float:1.8875703E38 double:1.0531623187E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x129d:
        r21 = r1;
        r10 = r32;
        r1 = r15.length;	 Catch:{ all -> 0x044b }
        r8 = 1;
        if (r1 <= r8) goto L_0x12e2;
    L_0x12a5:
        r1 = r15[r8];	 Catch:{ all -> 0x044b }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x044b }
        if (r1 != 0) goto L_0x12e2;
    L_0x12ad:
        r1 = "ChannelMessageStickerEmoji";
        r8 = NUM; // 0x7f0e026c float:1.8876295E38 double:1.053162463E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044b }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x044b }
        r14[r16] = r17;	 Catch:{ all -> 0x044b }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x044b }
        r14[r16] = r17;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044b }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044b }
        r8.<init>();	 Catch:{ all -> 0x044b }
        r14 = r15[r16];	 Catch:{ all -> 0x044b }
        r8.append(r14);	 Catch:{ all -> 0x044b }
        r8.append(r11);	 Catch:{ all -> 0x044b }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044b }
        r8.append(r7);	 Catch:{ all -> 0x044b }
        r7 = r8.toString();	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x12e2:
        r1 = "ChannelMessageSticker";
        r8 = NUM; // 0x7f0e026b float:1.8876293E38 double:1.0531624625E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044b }
        r8 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x12fc:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageDocument";
        r7 = NUM; // 0x7f0e0261 float:1.8876273E38 double:1.0531624575E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x131c:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageRound";
        r7 = NUM; // 0x7f0e026a float:1.887629E38 double:1.053162462E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x133c:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageVideo";
        r7 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0160 float:1.8875751E38 double:1.0531623305E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x135c:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessagePhoto";
        r7 = NUM; // 0x7f0e0268 float:1.8876287E38 double:1.053162461E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e015a float:1.887574E38 double:1.0531623276E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x137c:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageNoText";
        r7 = NUM; // 0x7f0e0267 float:1.8876285E38 double:1.0531624605E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x139c:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageAlbum";
        r7 = NUM; // 0x7f0e0701 float:1.8878674E38 double:1.0531630425E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
    L_0x13b1:
        r7 = r1;
        goto L_0x0993;
    L_0x13b4:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageFew";
        r7 = NUM; // 0x7f0e0705 float:1.8878682E38 double:1.0531630445E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = "Videos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044b }
        r15 = r15.intValue();	 Catch:{ all -> 0x044b }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ all -> 0x044b }
        r8[r14] = r11;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x13dd:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageFew";
        r7 = NUM; // 0x7f0e0705 float:1.8878682E38 double:1.0531630445E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = "Photos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044b }
        r15 = r15.intValue();	 Catch:{ all -> 0x044b }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ all -> 0x044b }
        r8[r14] = r11;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x1406:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageForwardFew";
        r7 = NUM; // 0x7f0e0706 float:1.8878684E38 double:1.053163045E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044b }
        r11[r14] = r17;	 Catch:{ all -> 0x044b }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044b }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044b }
        r15 = r15.intValue();	 Catch:{ all -> 0x044b }
        r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15);	 Catch:{ all -> 0x044b }
        r11[r14] = r8;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        goto L_0x13b1;
    L_0x142d:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageInvoice";
        r7 = NUM; // 0x7f0e071c float:1.8878729E38 double:1.053163056E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "PaymentInvoice";
        r8 = NUM; // 0x7f0e0875 float:1.8879429E38 double:1.0531632263E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1452:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGameScored";
        r8 = NUM; // 0x7f0e0708 float:1.8878688E38 double:1.053163046E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r7[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r7[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r7[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7);	 Catch:{ all -> 0x044b }
        goto L_0x1617;
    L_0x1473:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGame";
        r7 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachGame";
        r8 = NUM; // 0x7f0e014e float:1.8875715E38 double:1.0531623216E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1498:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGif";
        r7 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachGif";
        r8 = NUM; // 0x7f0e014f float:1.8875717E38 double:1.053162322E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x14b8:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageLiveLocation";
        r7 = NUM; // 0x7f0e071d float:1.887873E38 double:1.0531630563E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachLiveLocation";
        r8 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x14d8:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageMap";
        r7 = NUM; // 0x7f0e071e float:1.8878733E38 double:1.053163057E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachLocation";
        r8 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x14f8:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessagePoll2";
        r7 = NUM; // 0x7f0e0722 float:1.8878741E38 double:1.053163059E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "Poll";
        r8 = NUM; // 0x7f0e08e3 float:1.8879652E38 double:1.0531632806E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x151d:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageContact2";
        r7 = NUM; // 0x7f0e0703 float:1.8878678E38 double:1.0531630435E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = "AttachContact";
        r8 = NUM; // 0x7f0e014a float:1.8875707E38 double:1.0531623197E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1542:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageAudio";
        r7 = NUM; // 0x7f0e0702 float:1.8878676E38 double:1.053163043E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachAudio";
        r8 = NUM; // 0x7f0e0148 float:1.8875703E38 double:1.0531623187E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1562:
        r21 = r1;
        r10 = r32;
        r1 = r15.length;	 Catch:{ all -> 0x044b }
        r8 = 1;
        if (r1 <= r8) goto L_0x15a7;
    L_0x156a:
        r1 = r15[r8];	 Catch:{ all -> 0x044b }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x044b }
        if (r1 != 0) goto L_0x15a7;
    L_0x1572:
        r1 = "NotificationMessageStickerEmoji";
        r8 = NUM; // 0x7f0e0729 float:1.8878755E38 double:1.0531630623E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044b }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x044b }
        r14[r16] = r17;	 Catch:{ all -> 0x044b }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x044b }
        r14[r16] = r17;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044b }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044b }
        r8.<init>();	 Catch:{ all -> 0x044b }
        r14 = r15[r16];	 Catch:{ all -> 0x044b }
        r8.append(r14);	 Catch:{ all -> 0x044b }
        r8.append(r11);	 Catch:{ all -> 0x044b }
        r11 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044b }
        r8.append(r7);	 Catch:{ all -> 0x044b }
        r7 = r8.toString();	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x15a7:
        r1 = "NotificationMessageSticker";
        r8 = NUM; // 0x7f0e0728 float:1.8878753E38 double:1.053163062E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044b }
        r14[r11] = r15;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044b }
        r8 = NUM; // 0x7f0e015d float:1.8875745E38 double:1.053162329E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x15c1:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageDocument";
        r7 = NUM; // 0x7f0e0704 float:1.887868E38 double:1.053163044E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e014d float:1.8875713E38 double:1.053162321E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x15e1:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageRound";
        r7 = NUM; // 0x7f0e0723 float:1.8878743E38 double:1.0531630593E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1601:
        r21 = r1;
        r10 = r32;
        r1 = "ActionTakeScreenshoot";
        r7 = NUM; // 0x7f0e0093 float:1.8875336E38 double:1.0531622293E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r7);	 Catch:{ all -> 0x044b }
        r7 = "un1";
        r8 = 0;
        r11 = r15[r8];	 Catch:{ all -> 0x044b }
        r1 = r1.replace(r7, r11);	 Catch:{ all -> 0x044b }
    L_0x1617:
        r7 = r1;
    L_0x1618:
        r1 = 0;
        goto L_0x16f0;
    L_0x161b:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageSDVideo";
        r7 = NUM; // 0x7f0e0725 float:1.8878747E38 double:1.0531630603E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachDestructingVideo";
        r8 = NUM; // 0x7f0e014c float:1.887571E38 double:1.0531623207E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x163b:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageVideo";
        r7 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0160 float:1.8875751E38 double:1.0531623305E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x165a:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageSDPhoto";
        r7 = NUM; // 0x7f0e0724 float:1.8878745E38 double:1.05316306E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachDestructingPhoto";
        r8 = NUM; // 0x7f0e014b float:1.8875709E38 double:1.05316232E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1679:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessagePhoto";
        r7 = NUM; // 0x7f0e0721 float:1.887874E38 double:1.0531630583E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e015a float:1.887574E38 double:1.0531623276E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x1698:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageNoText";
        r7 = NUM; // 0x7f0e0720 float:1.8878737E38 double:1.053163058E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044b }
        r11[r8] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044b }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044b }
    L_0x16b6:
        r16 = r7;
        r7 = r1;
    L_0x16b9:
        r1 = 0;
        goto L_0x16f2;
    L_0x16bb:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageText";
        r7 = NUM; // 0x7f0e072a float:1.8878757E38 double:1.0531630627E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044b }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044b }
        r8[r11] = r14;	 Catch:{ all -> 0x044b }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044b }
        r7 = r15[r11];	 Catch:{ all -> 0x044b }
        goto L_0x16b6;
    L_0x16d8:
        if (r1 == 0) goto L_0x16ee;
    L_0x16da:
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044b }
        r1.<init>();	 Catch:{ all -> 0x044b }
        r7 = "unhandled loc_key = ";
        r1.append(r7);	 Catch:{ all -> 0x044b }
        r1.append(r9);	 Catch:{ all -> 0x044b }
        r1 = r1.toString();	 Catch:{ all -> 0x044b }
        org.telegram.messenger.FileLog.w(r1);	 Catch:{ all -> 0x044b }
    L_0x16ee:
        r1 = 0;
        r7 = 0;
    L_0x16f0:
        r16 = 0;
    L_0x16f2:
        if (r7 == 0) goto L_0x1791;
    L_0x16f4:
        r8 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ all -> 0x1794 }
        r8.<init>();	 Catch:{ all -> 0x1794 }
        r8.id = r10;	 Catch:{ all -> 0x1794 }
        r8.random_id = r3;	 Catch:{ all -> 0x1794 }
        if (r16 == 0) goto L_0x1702;
    L_0x16ff:
        r3 = r16;
        goto L_0x1703;
    L_0x1702:
        r3 = r7;
    L_0x1703:
        r8.message = r3;	 Catch:{ all -> 0x1794 }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r37 / r3;
        r4 = (int) r3;	 Catch:{ all -> 0x1794 }
        r8.date = r4;	 Catch:{ all -> 0x1794 }
        if (r5 == 0) goto L_0x1715;
    L_0x170e:
        r3 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ all -> 0x044b }
        r3.<init>();	 Catch:{ all -> 0x044b }
        r8.action = r3;	 Catch:{ all -> 0x044b }
    L_0x1715:
        if (r6 == 0) goto L_0x171e;
    L_0x1717:
        r3 = r8.flags;	 Catch:{ all -> 0x044b }
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r4;
        r8.flags = r3;	 Catch:{ all -> 0x044b }
    L_0x171e:
        r8.dialog_id = r12;	 Catch:{ all -> 0x1794 }
        if (r2 == 0) goto L_0x172e;
    L_0x1722:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ all -> 0x044b }
        r3.<init>();	 Catch:{ all -> 0x044b }
        r8.to_id = r3;	 Catch:{ all -> 0x044b }
        r3 = r8.to_id;	 Catch:{ all -> 0x044b }
        r3.channel_id = r2;	 Catch:{ all -> 0x044b }
        goto L_0x174b;
    L_0x172e:
        if (r22 == 0) goto L_0x173e;
    L_0x1730:
        r2 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x044b }
        r2.<init>();	 Catch:{ all -> 0x044b }
        r8.to_id = r2;	 Catch:{ all -> 0x044b }
        r2 = r8.to_id;	 Catch:{ all -> 0x044b }
        r3 = r22;
        r2.chat_id = r3;	 Catch:{ all -> 0x044b }
        goto L_0x174b;
    L_0x173e:
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x1794 }
        r2.<init>();	 Catch:{ all -> 0x1794 }
        r8.to_id = r2;	 Catch:{ all -> 0x1794 }
        r2 = r8.to_id;	 Catch:{ all -> 0x1794 }
        r3 = r24;
        r2.user_id = r3;	 Catch:{ all -> 0x1794 }
    L_0x174b:
        r2 = r8.flags;	 Catch:{ all -> 0x1794 }
        r2 = r2 | 256;
        r8.flags = r2;	 Catch:{ all -> 0x1794 }
        r2 = r21;
        r8.from_id = r2;	 Catch:{ all -> 0x1794 }
        if (r20 != 0) goto L_0x175c;
    L_0x1757:
        if (r5 == 0) goto L_0x175a;
    L_0x1759:
        goto L_0x175c;
    L_0x175a:
        r2 = 0;
        goto L_0x175d;
    L_0x175c:
        r2 = 1;
    L_0x175d:
        r8.mentioned = r2;	 Catch:{ all -> 0x1794 }
        r2 = r19;
        r8.silent = r2;	 Catch:{ all -> 0x1794 }
        r4 = r23;
        r8.from_scheduled = r4;	 Catch:{ all -> 0x1794 }
        r2 = new org.telegram.messenger.MessageObject;	 Catch:{ all -> 0x1794 }
        r19 = r2;
        r20 = r30;
        r21 = r8;
        r22 = r7;
        r23 = r25;
        r24 = r31;
        r25 = r1;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ all -> 0x1794 }
        r1 = new java.util.ArrayList;	 Catch:{ all -> 0x1794 }
        r1.<init>();	 Catch:{ all -> 0x1794 }
        r1.add(r2);	 Catch:{ all -> 0x1794 }
        r2 = org.telegram.messenger.NotificationsController.getInstance(r30);	 Catch:{ all -> 0x1794 }
        r3 = r35;
        r4 = r3.countDownLatch;	 Catch:{ all -> 0x17bc }
        r5 = 1;
        r2.processNewMessages(r1, r5, r5, r4);	 Catch:{ all -> 0x17bc }
        r28 = 0;
        goto L_0x17a9;
    L_0x1791:
        r3 = r35;
        goto L_0x17a7;
    L_0x1794:
        r0 = move-exception;
        r3 = r35;
        goto L_0x17bd;
    L_0x1798:
        r0 = move-exception;
        r3 = r35;
        goto L_0x17d1;
    L_0x179c:
        r3 = r35;
        goto L_0x17a5;
    L_0x179f:
        r0 = move-exception;
        r3 = r1;
        goto L_0x17d1;
    L_0x17a2:
        r3 = r1;
        r29 = r14;
    L_0x17a5:
        r30 = r15;
    L_0x17a7:
        r28 = 1;
    L_0x17a9:
        if (r28 == 0) goto L_0x17b0;
    L_0x17ab:
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x17bc }
        r1.countDown();	 Catch:{ all -> 0x17bc }
    L_0x17b0:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);	 Catch:{ all -> 0x17bc }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r30);	 Catch:{ all -> 0x17bc }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x17bc }
        goto L_0x18f0;
    L_0x17bc:
        r0 = move-exception;
    L_0x17bd:
        r1 = r0;
        r14 = r29;
        r15 = r30;
        goto L_0x189c;
    L_0x17c4:
        r0 = move-exception;
        r3 = r1;
        r29 = r14;
        r30 = r15;
        r1 = r0;
        goto L_0x189c;
    L_0x17cd:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
    L_0x17d1:
        r30 = r15;
        goto L_0x1899;
    L_0x17d5:
        r3 = r1;
        r29 = r7;
        r30 = r15;
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ all -> 0x17f4 }
        r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4;	 Catch:{ all -> 0x17ef }
        r15 = r30;
        r2.<init>(r15);	 Catch:{ all -> 0x17ec }
        r1.postRunnable(r2);	 Catch:{ all -> 0x1893 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1893 }
        r1.countDown();	 Catch:{ all -> 0x1893 }
        return;
    L_0x17ec:
        r0 = move-exception;
        goto L_0x1899;
    L_0x17ef:
        r0 = move-exception;
        r15 = r30;
        goto L_0x1899;
    L_0x17f4:
        r0 = move-exception;
        r15 = r30;
        goto L_0x1899;
    L_0x17f9:
        r3 = r1;
        r29 = r7;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ all -> 0x180a }
        r1.<init>(r15);	 Catch:{ all -> 0x180a }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ all -> 0x1893 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1893 }
        r1.countDown();	 Catch:{ all -> 0x1893 }
        return;
    L_0x180a:
        r0 = move-exception;
        goto L_0x1899;
    L_0x180d:
        r3 = r1;
        r29 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ all -> 0x1893 }
        r1.<init>();	 Catch:{ all -> 0x1893 }
        r2 = 0;
        r1.popup = r2;	 Catch:{ all -> 0x1893 }
        r2 = 2;
        r1.flags = r2;	 Catch:{ all -> 0x1893 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r37 / r6;
        r2 = (int) r6;	 Catch:{ all -> 0x1893 }
        r1.inbox_date = r2;	 Catch:{ all -> 0x1893 }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ all -> 0x1893 }
        r1.message = r2;	 Catch:{ all -> 0x1893 }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ all -> 0x1893 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ all -> 0x1893 }
        r2.<init>();	 Catch:{ all -> 0x1893 }
        r1.media = r2;	 Catch:{ all -> 0x1893 }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ all -> 0x1893 }
        r2.<init>();	 Catch:{ all -> 0x1893 }
        r4 = r2.updates;	 Catch:{ all -> 0x1893 }
        r4.add(r1);	 Catch:{ all -> 0x1893 }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ all -> 0x1893 }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ all -> 0x180a }
        r4.<init>(r15, r2);	 Catch:{ all -> 0x180a }
        r1.postRunnable(r4);	 Catch:{ all -> 0x1893 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x1893 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x1893 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1893 }
        r1.countDown();	 Catch:{ all -> 0x1893 }
        return;
    L_0x1856:
        r3 = r1;
        r29 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x1893 }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ all -> 0x1893 }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ all -> 0x1893 }
        r4 = r2.length;	 Catch:{ all -> 0x1893 }
        r5 = 2;
        if (r4 == r5) goto L_0x1875;
    L_0x186f:
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1893 }
        r1.countDown();	 Catch:{ all -> 0x1893 }
        return;
    L_0x1875:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ all -> 0x1893 }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ all -> 0x1893 }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ all -> 0x1893 }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x1893 }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ all -> 0x1893 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x1893 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x1893 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1893 }
        r1.countDown();	 Catch:{ all -> 0x1893 }
        return;
    L_0x1893:
        r0 = move-exception;
        goto L_0x1899;
    L_0x1895:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
    L_0x1899:
        r1 = r0;
        r14 = r29;
    L_0x189c:
        r2 = -1;
        goto L_0x18b8;
    L_0x189e:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
        r1 = r0;
        r14 = r29;
        r2 = -1;
        goto L_0x18b7;
    L_0x18a7:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
        r1 = r0;
        r14 = r29;
        r2 = -1;
        r9 = 0;
        goto L_0x18b7;
    L_0x18b1:
        r0 = move-exception;
        r3 = r1;
        r1 = r0;
        r2 = -1;
        r9 = 0;
        r14 = 0;
    L_0x18b7:
        r15 = -1;
    L_0x18b8:
        if (r15 == r2) goto L_0x18ca;
    L_0x18ba:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2.resumeNetworkMaybe();
        r2 = r3.countDownLatch;
        r2.countDown();
        goto L_0x18cd;
    L_0x18ca:
        r35.onDecryptError();
    L_0x18cd:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x18ed;
    L_0x18d1:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "error in loc_key = ";
        r2.append(r4);
        r2.append(r9);
        r4 = " json ";
        r2.append(r4);
        r2.append(r14);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.e(r2);
    L_0x18ed:
        org.telegram.messenger.FileLog.e(r1);
    L_0x18f0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.GcmPushListenerService.lambda$null$3$GcmPushListenerService(java.util.Map, long):void");
    }

    static /* synthetic */ void lambda$null$1(int i) {
        if (UserConfig.getInstance(i).getClientUserId() != 0) {
            UserConfig.getInstance(i).clearConfig();
            MessagesController.getInstance(i).performLogout(0);
        }
    }

    private void onDecryptError() {
        for (int i = 0; i < 3; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                ConnectionsManager.onInternalPushReceived(i);
                ConnectionsManager.getInstance(i).resumeNetworkMaybe();
            }
        }
        this.countDownLatch.countDown();
    }

    public void onNewToken(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$yAX-L4AmtPgnfoqai_KIyMdrl9c(str));
    }

    static /* synthetic */ void lambda$onNewToken$5(String str) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Refreshed token: ");
            stringBuilder.append(str);
            FileLog.d(stringBuilder.toString());
        }
        ApplicationLoader.postInitApplication();
        sendRegistrationToServer(str);
    }

    public static void sendRegistrationToServer(String str) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$oMpUElUKeVcspCfJjKdmR5Ds1PU(str));
    }

    static /* synthetic */ void lambda$sendRegistrationToServer$7(String str) {
        ConnectionsManager.setRegId(str, SharedConfig.pushStringStatus);
        if (str != null) {
            SharedConfig.pushString = str;
            for (int i = 0; i < 3; i++) {
                UserConfig instance = UserConfig.getInstance(i);
                instance.registeredForPush = false;
                instance.saveConfig(false);
                if (instance.getClientUserId() != 0) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$GcmPushListenerService$YWXR3JZImuShQ4_sLgJ0wneqQVQ(i, str));
                }
            }
        }
    }
}
