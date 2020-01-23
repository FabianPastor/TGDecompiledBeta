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
        long elapsedRealtime = SystemClock.elapsedRealtime();
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
            stringBuilder.append(SystemClock.elapsedRealtime() - elapsedRealtime);
            FileLog.d(stringBuilder.toString());
        }
    }

    public /* synthetic */ void lambda$onMessageReceived$4$GcmPushListenerService(Map map, long j) {
        ApplicationLoader.postInitApplication();
        Utilities.stageQueue.postRunnable(new -$$Lambda$GcmPushListenerService$mgGQQF3kpbOfBT2rVoACkZ_x-2M(this, map, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x199b  */
    /* JADX WARNING: Removed duplicated region for block: B:886:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:889:0x19b2  */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:844:0x18c4, B:853:0x18dd] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:115:0x01df, code skipped:
            if (r2 == null) goto L_0x1937;
     */
    /* JADX WARNING: Missing block: B:116:0x01e1, code skipped:
            if (r2 == 1) goto L_0x18ee;
     */
    /* JADX WARNING: Missing block: B:117:0x01e3, code skipped:
            if (r2 == 2) goto L_0x18da;
     */
    /* JADX WARNING: Missing block: B:118:0x01e5, code skipped:
            if (r2 == 3) goto L_0x18b6;
     */
    /* JADX WARNING: Missing block: B:122:0x01ef, code skipped:
            if (r11.has("channel_id") == false) goto L_0x01fa;
     */
    /* JADX WARNING: Missing block: B:124:?, code skipped:
            r2 = r11.getInt("channel_id");
     */
    /* JADX WARNING: Missing block: B:125:0x01f7, code skipped:
            r3 = (long) (-r2);
     */
    /* JADX WARNING: Missing block: B:126:0x01fa, code skipped:
            r3 = 0;
            r2 = 0;
     */
    /* JADX WARNING: Missing block: B:129:0x0203, code skipped:
            if (r11.has("from_id") == false) goto L_0x0217;
     */
    /* JADX WARNING: Missing block: B:131:?, code skipped:
            r3 = r11.getInt("from_id");
     */
    /* JADX WARNING: Missing block: B:132:0x020b, code skipped:
            r14 = r7;
            r6 = r3;
            r3 = (long) r3;
     */
    /* JADX WARNING: Missing block: B:133:0x0213, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:134:0x0214, code skipped:
            r14 = r7;
     */
    /* JADX WARNING: Missing block: B:135:0x0215, code skipped:
            r3 = r1;
     */
    /* JADX WARNING: Missing block: B:136:0x0217, code skipped:
            r14 = r7;
            r6 = 0;
     */
    /* JADX WARNING: Missing block: B:139:0x021f, code skipped:
            if (r11.has("chat_id") == false) goto L_0x022c;
     */
    /* JADX WARNING: Missing block: B:141:?, code skipped:
            r3 = r11.getInt("chat_id");
     */
    /* JADX WARNING: Missing block: B:142:0x0227, code skipped:
            r12 = (long) (-r3);
     */
    /* JADX WARNING: Missing block: B:143:0x022a, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:144:0x022c, code skipped:
            r12 = r3;
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:147:0x0234, code skipped:
            if (r11.has("encryption_id") == false) goto L_0x0240;
     */
    /* JADX WARNING: Missing block: B:150:0x023c, code skipped:
            r12 = ((long) r11.getInt("encryption_id")) << 32;
     */
    /* JADX WARNING: Missing block: B:153:0x0246, code skipped:
            if (r11.has("schedule") == false) goto L_0x0252;
     */
    /* JADX WARNING: Missing block: B:156:0x024e, code skipped:
            if (r11.getInt("schedule") != 1) goto L_0x0252;
     */
    /* JADX WARNING: Missing block: B:157:0x0250, code skipped:
            r4 = true;
     */
    /* JADX WARNING: Missing block: B:158:0x0252, code skipped:
            r4 = false;
     */
    /* JADX WARNING: Missing block: B:160:0x0255, code skipped:
            if (r12 != 0) goto L_0x0264;
     */
    /* JADX WARNING: Missing block: B:162:0x025d, code skipped:
            if ("ENCRYPTED_MESSAGE".equals(r9) == false) goto L_0x0264;
     */
    /* JADX WARNING: Missing block: B:163:0x025f, code skipped:
            r12 = -4294967296L;
     */
    /* JADX WARNING: Missing block: B:165:0x0266, code skipped:
            if (r12 == 0) goto L_0x1883;
     */
    /* JADX WARNING: Missing block: B:168:0x026e, code skipped:
            r10 = " for dialogId = ";
     */
    /* JADX WARNING: Missing block: B:169:0x0270, code skipped:
            if ("READ_HISTORY".equals(r9) == false) goto L_0x02e0;
     */
    /* JADX WARNING: Missing block: B:171:?, code skipped:
            r4 = r11.getInt("max_id");
            r5 = new java.util.ArrayList();
     */
    /* JADX WARNING: Missing block: B:172:0x027f, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x029b;
     */
    /* JADX WARNING: Missing block: B:173:0x0281, code skipped:
            r7 = new java.lang.StringBuilder();
            r7.append("GCM received read notification max_id = ");
            r7.append(r4);
            r7.append(r10);
            r7.append(r12);
            org.telegram.messenger.FileLog.d(r7.toString());
     */
    /* JADX WARNING: Missing block: B:174:0x029b, code skipped:
            if (r2 == 0) goto L_0x02aa;
     */
    /* JADX WARNING: Missing block: B:175:0x029d, code skipped:
            r3 = new org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox();
            r3.channel_id = r2;
            r3.max_id = r4;
            r5.add(r3);
     */
    /* JADX WARNING: Missing block: B:176:0x02aa, code skipped:
            r2 = new org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox();
     */
    /* JADX WARNING: Missing block: B:177:0x02af, code skipped:
            if (r6 == 0) goto L_0x02bd;
     */
    /* JADX WARNING: Missing block: B:178:0x02b1, code skipped:
            r2.peer = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r2.peer.user_id = r6;
     */
    /* JADX WARNING: Missing block: B:179:0x02bd, code skipped:
            r2.peer = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r2.peer.chat_id = r3;
     */
    /* JADX WARNING: Missing block: B:180:0x02c8, code skipped:
            r2.max_id = r4;
            r5.add(r2);
     */
    /* JADX WARNING: Missing block: B:181:0x02cd, code skipped:
            org.telegram.messenger.MessagesController.getInstance(r15).processUpdateArray(r5, null, null, false, 0);
     */
    /* JADX WARNING: Missing block: B:184:0x02e6, code skipped:
            r8 = "messages";
     */
    /* JADX WARNING: Missing block: B:185:0x02e8, code skipped:
            if ("MESSAGE_DELETED".equals(r9) == false) goto L_0x034d;
     */
    /* JADX WARNING: Missing block: B:187:?, code skipped:
            r3 = r11.getString(r8).split(",");
            r4 = new android.util.SparseArray();
            r5 = new java.util.ArrayList();
            r6 = 0;
     */
    /* JADX WARNING: Missing block: B:189:0x0300, code skipped:
            if (r6 >= r3.length) goto L_0x030e;
     */
    /* JADX WARNING: Missing block: B:190:0x0302, code skipped:
            r5.add(org.telegram.messenger.Utilities.parseInt(r3[r6]));
            r6 = r6 + 1;
     */
    /* JADX WARNING: Missing block: B:191:0x030e, code skipped:
            r4.put(r2, r5);
            org.telegram.messenger.NotificationsController.getInstance(r15).removeDeletedMessagesFromNotifications(r4);
            org.telegram.messenger.MessagesController.getInstance(r15).deleteMessagesByPush(r12, r5, r2);
     */
    /* JADX WARNING: Missing block: B:192:0x0321, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1883;
     */
    /* JADX WARNING: Missing block: B:193:0x0323, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("GCM received ");
            r2.append(r9);
            r2.append(r10);
            r2.append(r12);
            r2.append(" mids = ");
            r2.append(android.text.TextUtils.join(",", r5));
            org.telegram.messenger.FileLog.d(r2.toString());
     */
    /* JADX WARNING: Missing block: B:196:0x0351, code skipped:
            if (android.text.TextUtils.isEmpty(r9) != false) goto L_0x1883;
     */
    /* JADX WARNING: Missing block: B:198:0x0359, code skipped:
            if (r11.has("msg_id") == false) goto L_0x0364;
     */
    /* JADX WARNING: Missing block: B:200:?, code skipped:
            r7 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Missing block: B:201:0x0361, code skipped:
            r29 = r14;
     */
    /* JADX WARNING: Missing block: B:202:0x0364, code skipped:
            r29 = r14;
            r7 = 0;
     */
    /* JADX WARNING: Missing block: B:205:0x036d, code skipped:
            if (r11.has("random_id") == false) goto L_0x038b;
     */
    /* JADX WARNING: Missing block: B:208:0x037d, code skipped:
            r14 = r4;
            r22 = r3;
            r3 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
     */
    /* JADX WARNING: Missing block: B:209:0x0385, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:210:0x0386, code skipped:
            r3 = r1;
            r14 = r29;
     */
    /* JADX WARNING: Missing block: B:211:0x038b, code skipped:
            r22 = r3;
            r14 = r4;
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:212:0x0390, code skipped:
            if (r7 == 0) goto L_0x03d5;
     */
    /* JADX WARNING: Missing block: B:213:0x0392, code skipped:
            r23 = r14;
     */
    /* JADX WARNING: Missing block: B:215:?, code skipped:
            r1 = (java.lang.Integer) org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r12));
     */
    /* JADX WARNING: Missing block: B:216:0x03a4, code skipped:
            if (r1 != null) goto L_0x03c3;
     */
    /* JADX WARNING: Missing block: B:217:0x03a6, code skipped:
            r1 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r15).getDialogReadMax(false, r12));
            r24 = r6;
            org.telegram.messenger.MessagesController.getInstance(r15).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r12), r1);
     */
    /* JADX WARNING: Missing block: B:218:0x03c3, code skipped:
            r24 = r6;
     */
    /* JADX WARNING: Missing block: B:220:0x03c9, code skipped:
            if (r7 <= r1.intValue()) goto L_0x03e9;
     */
    /* JADX WARNING: Missing block: B:222:0x03cc, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:223:0x03cd, code skipped:
            r2 = -1;
            r3 = r35;
            r1 = r0;
            r14 = r29;
     */
    /* JADX WARNING: Missing block: B:224:0x03d5, code skipped:
            r24 = r6;
            r23 = r14;
     */
    /* JADX WARNING: Missing block: B:225:0x03db, code skipped:
            if (r3 == 0) goto L_0x03e9;
     */
    /* JADX WARNING: Missing block: B:227:0x03e5, code skipped:
            if (org.telegram.messenger.MessagesStorage.getInstance(r15).checkMessageByRandomId(r3) != false) goto L_0x03e9;
     */
    /* JADX WARNING: Missing block: B:228:0x03e7, code skipped:
            r1 = 1;
     */
    /* JADX WARNING: Missing block: B:229:0x03e9, code skipped:
            r1 = null;
     */
    /* JADX WARNING: Missing block: B:230:0x03ea, code skipped:
            if (r1 == null) goto L_0x187d;
     */
    /* JADX WARNING: Missing block: B:233:0x03f2, code skipped:
            if (r11.has("chat_from_id") == false) goto L_0x03fb;
     */
    /* JADX WARNING: Missing block: B:235:?, code skipped:
            r1 = r11.getInt("chat_from_id");
     */
    /* JADX WARNING: Missing block: B:236:0x03fb, code skipped:
            r1 = 0;
     */
    /* JADX WARNING: Missing block: B:239:0x0402, code skipped:
            if (r11.has("mention") == false) goto L_0x040e;
     */
    /* JADX WARNING: Missing block: B:242:0x040a, code skipped:
            if (r11.getInt("mention") == 0) goto L_0x040e;
     */
    /* JADX WARNING: Missing block: B:243:0x040c, code skipped:
            r6 = 1;
     */
    /* JADX WARNING: Missing block: B:244:0x040e, code skipped:
            r6 = null;
     */
    /* JADX WARNING: Missing block: B:247:0x0415, code skipped:
            if (r11.has("silent") == false) goto L_0x0423;
     */
    /* JADX WARNING: Missing block: B:250:0x041d, code skipped:
            if (r11.getInt("silent") == 0) goto L_0x0423;
     */
    /* JADX WARNING: Missing block: B:251:0x041f, code skipped:
            r30 = r15;
            r14 = true;
     */
    /* JADX WARNING: Missing block: B:252:0x0423, code skipped:
            r30 = r15;
            r14 = false;
     */
    /* JADX WARNING: Missing block: B:255:0x042c, code skipped:
            if (r5.has("loc_args") == false) goto L_0x0458;
     */
    /* JADX WARNING: Missing block: B:257:?, code skipped:
            r5 = r5.getJSONArray("loc_args");
            r15 = new java.lang.String[r5.length()];
            r20 = r6;
            r19 = r14;
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:259:0x0440, code skipped:
            if (r14 >= r15.length) goto L_0x044b;
     */
    /* JADX WARNING: Missing block: B:260:0x0442, code skipped:
            r15[r14] = r5.getString(r14);
     */
    /* JADX WARNING: Missing block: B:261:0x0448, code skipped:
            r14 = r14 + 1;
     */
    /* JADX WARNING: Missing block: B:262:0x044b, code skipped:
            r5 = 0;
     */
    /* JADX WARNING: Missing block: B:263:0x044d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:264:0x044e, code skipped:
            r2 = -1;
            r3 = r35;
            r1 = r0;
            r14 = r29;
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:265:0x0458, code skipped:
            r20 = r6;
            r19 = r14;
            r5 = 0;
            r15 = null;
     */
    /* JADX WARNING: Missing block: B:267:?, code skipped:
            r6 = r15[r5];
            r27 = r11.has("edit_date");
     */
    /* JADX WARNING: Missing block: B:268:0x046c, code skipped:
            if (r9.startsWith("CHAT_") == false) goto L_0x047c;
     */
    /* JADX WARNING: Missing block: B:269:0x046e, code skipped:
            if (r2 == 0) goto L_0x0472;
     */
    /* JADX WARNING: Missing block: B:270:0x0470, code skipped:
            r5 = 1;
     */
    /* JADX WARNING: Missing block: B:271:0x0472, code skipped:
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:274:?, code skipped:
            r14 = r15[1];
     */
    /* JADX WARNING: Missing block: B:275:0x0476, code skipped:
            r11 = r6;
            r26 = false;
            r6 = r5;
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:278:0x0482, code skipped:
            if (r9.startsWith("PINNED_") == false) goto L_0x0490;
     */
    /* JADX WARNING: Missing block: B:279:0x0484, code skipped:
            if (r1 == 0) goto L_0x0488;
     */
    /* JADX WARNING: Missing block: B:280:0x0486, code skipped:
            r5 = 1;
     */
    /* JADX WARNING: Missing block: B:281:0x0488, code skipped:
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:282:0x0489, code skipped:
            r14 = r6;
            r11 = null;
            r26 = false;
            r6 = r5;
            r5 = 1;
     */
    /* JADX WARNING: Missing block: B:283:0x0490, code skipped:
            r14 = r6;
     */
    /* JADX WARNING: Missing block: B:284:0x0497, code skipped:
            if (r9.startsWith("CHANNEL_") == false) goto L_0x049f;
     */
    /* JADX WARNING: Missing block: B:285:0x0499, code skipped:
            r5 = null;
            r6 = null;
            r11 = null;
            r26 = true;
     */
    /* JADX WARNING: Missing block: B:286:0x049f, code skipped:
            r5 = null;
            r6 = null;
            r11 = null;
            r26 = false;
     */
    /* JADX WARNING: Missing block: B:288:0x04a6, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x04cf;
     */
    /* JADX WARNING: Missing block: B:289:0x04a8, code skipped:
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
    /* JADX WARNING: Missing block: B:292:0x04cf, code skipped:
            r31 = r11;
            r25 = r14;
     */
    /* JADX WARNING: Missing block: B:295:0x04d7, code skipped:
            switch(r9.hashCode()) {
                case -2100047043: goto L_0x0988;
                case -2091498420: goto L_0x097d;
                case -2053872415: goto L_0x0972;
                case -2039746363: goto L_0x0967;
                case -2023218804: goto L_0x095c;
                case -1979538588: goto L_0x0951;
                case -1979536003: goto L_0x0946;
                case -1979535888: goto L_0x093b;
                case -1969004705: goto L_0x0930;
                case -1946699248: goto L_0x0924;
                case -1528047021: goto L_0x0918;
                case -1493579426: goto L_0x090c;
                case -1482481933: goto L_0x0900;
                case -1480102982: goto L_0x08f5;
                case -1478041834: goto L_0x08e9;
                case -1474543101: goto L_0x08de;
                case -1465695932: goto L_0x08d2;
                case -1374906292: goto L_0x08c6;
                case -1372940586: goto L_0x08ba;
                case -1264245338: goto L_0x08ae;
                case -1236086700: goto L_0x08a2;
                case -1236077786: goto L_0x0896;
                case -1235796237: goto L_0x088a;
                case -1235760759: goto L_0x087e;
                case -1235686303: goto L_0x0873;
                case -1198046100: goto L_0x0868;
                case -1124254527: goto L_0x085c;
                case -1085137927: goto L_0x0850;
                case -1084856378: goto L_0x0844;
                case -1084820900: goto L_0x0838;
                case -1084746444: goto L_0x082c;
                case -819729482: goto L_0x0820;
                case -772141857: goto L_0x0814;
                case -638310039: goto L_0x0808;
                case -590403924: goto L_0x07fc;
                case -589196239: goto L_0x07f0;
                case -589193654: goto L_0x07e4;
                case -589193539: goto L_0x07d8;
                case -440169325: goto L_0x07cc;
                case -412748110: goto L_0x07c0;
                case -228518075: goto L_0x07b4;
                case -213586509: goto L_0x07a8;
                case -115582002: goto L_0x079c;
                case -112621464: goto L_0x0790;
                case -108522133: goto L_0x0784;
                case -107572034: goto L_0x0779;
                case -40534265: goto L_0x076d;
                case 65254746: goto L_0x0761;
                case 141040782: goto L_0x0755;
                case 309993049: goto L_0x0749;
                case 309995634: goto L_0x073d;
                case 309995749: goto L_0x0731;
                case 320532812: goto L_0x0725;
                case 328933854: goto L_0x0719;
                case 331340546: goto L_0x070d;
                case 344816990: goto L_0x0701;
                case 346878138: goto L_0x06f5;
                case 350376871: goto L_0x06e9;
                case 615714517: goto L_0x06de;
                case 715508879: goto L_0x06d2;
                case 728985323: goto L_0x06c6;
                case 731046471: goto L_0x06ba;
                case 734545204: goto L_0x06ae;
                case 802032552: goto L_0x06a2;
                case 991498806: goto L_0x0696;
                case 1007364121: goto L_0x068a;
                case 1019917311: goto L_0x067e;
                case 1019926225: goto L_0x0672;
                case 1020207774: goto L_0x0666;
                case 1020243252: goto L_0x065a;
                case 1020317708: goto L_0x064e;
                case 1060349560: goto L_0x0642;
                case 1060358474: goto L_0x0636;
                case 1060640023: goto L_0x062a;
                case 1060675501: goto L_0x061e;
                case 1060749957: goto L_0x0613;
                case 1073049781: goto L_0x0607;
                case 1078101399: goto L_0x05fb;
                case 1110103437: goto L_0x05ef;
                case 1160762272: goto L_0x05e3;
                case 1172918249: goto L_0x05d7;
                case 1234591620: goto L_0x05cb;
                case 1281128640: goto L_0x05bf;
                case 1281131225: goto L_0x05b3;
                case 1281131340: goto L_0x05a7;
                case 1310789062: goto L_0x059c;
                case 1333118583: goto L_0x0590;
                case 1361447897: goto L_0x0584;
                case 1498266155: goto L_0x0578;
                case 1533804208: goto L_0x056c;
                case 1547988151: goto L_0x0560;
                case 1561464595: goto L_0x0554;
                case 1563525743: goto L_0x0548;
                case 1567024476: goto L_0x053c;
                case 1810705077: goto L_0x0530;
                case 1815177512: goto L_0x0524;
                case 1963241394: goto L_0x0518;
                case 2014789757: goto L_0x050c;
                case 2022049433: goto L_0x0500;
                case 2048733346: goto L_0x04f4;
                case 2099392181: goto L_0x04e8;
                case 2140162142: goto L_0x04dc;
                default: goto L_0x04da;
            };
     */
    /* JADX WARNING: Missing block: B:298:0x04e2, code skipped:
            if (r9.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:299:0x04e4, code skipped:
            r10 = 56;
     */
    /* JADX WARNING: Missing block: B:301:0x04ee, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:302:0x04f0, code skipped:
            r10 = 41;
     */
    /* JADX WARNING: Missing block: B:304:0x04fa, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:305:0x04fc, code skipped:
            r10 = 26;
     */
    /* JADX WARNING: Missing block: B:307:0x0506, code skipped:
            if (r9.equals("PINNED_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:308:0x0508, code skipped:
            r10 = 83;
     */
    /* JADX WARNING: Missing block: B:310:0x0512, code skipped:
            if (r9.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:311:0x0514, code skipped:
            r10 = 64;
     */
    /* JADX WARNING: Missing block: B:313:0x051e, code skipped:
            if (r9.equals("LOCKED_MESSAGE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:314:0x0520, code skipped:
            r10 = 96;
     */
    /* JADX WARNING: Missing block: B:316:0x052a, code skipped:
            if (r9.equals("CHANNEL_MESSAGES") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:317:0x052c, code skipped:
            r10 = 43;
     */
    /* JADX WARNING: Missing block: B:319:0x0536, code skipped:
            if (r9.equals("MESSAGE_INVOICE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:320:0x0538, code skipped:
            r10 = 21;
     */
    /* JADX WARNING: Missing block: B:322:0x0542, code skipped:
            if (r9.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:323:0x0544, code skipped:
            r10 = 47;
     */
    /* JADX WARNING: Missing block: B:325:0x054e, code skipped:
            if (r9.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:326:0x0550, code skipped:
            r10 = 48;
     */
    /* JADX WARNING: Missing block: B:328:0x055a, code skipped:
            if (r9.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:329:0x055c, code skipped:
            r10 = 46;
     */
    /* JADX WARNING: Missing block: B:331:0x0566, code skipped:
            if (r9.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:332:0x0568, code skipped:
            r10 = 51;
     */
    /* JADX WARNING: Missing block: B:334:0x0572, code skipped:
            if (r9.equals("MESSAGE_VIDEOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:335:0x0574, code skipped:
            r10 = 24;
     */
    /* JADX WARNING: Missing block: B:337:0x057e, code skipped:
            if (r9.equals("PHONE_CALL_MISSED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:338:0x0580, code skipped:
            r10 = 101;
     */
    /* JADX WARNING: Missing block: B:340:0x058a, code skipped:
            if (r9.equals("MESSAGE_PHOTOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:341:0x058c, code skipped:
            r10 = 23;
     */
    /* JADX WARNING: Missing block: B:343:0x0596, code skipped:
            if (r9.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:344:0x0598, code skipped:
            r10 = 73;
     */
    /* JADX WARNING: Missing block: B:346:0x05a2, code skipped:
            if (r9.equals("MESSAGE_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:347:0x05a4, code skipped:
            r10 = 2;
     */
    /* JADX WARNING: Missing block: B:349:0x05ad, code skipped:
            if (r9.equals("MESSAGE_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:350:0x05af, code skipped:
            r10 = 17;
     */
    /* JADX WARNING: Missing block: B:352:0x05b9, code skipped:
            if (r9.equals("MESSAGE_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:353:0x05bb, code skipped:
            r10 = 15;
     */
    /* JADX WARNING: Missing block: B:355:0x05c5, code skipped:
            if (r9.equals("MESSAGE_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:356:0x05c7, code skipped:
            r10 = 9;
     */
    /* JADX WARNING: Missing block: B:358:0x05d1, code skipped:
            if (r9.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:359:0x05d3, code skipped:
            r10 = 59;
     */
    /* JADX WARNING: Missing block: B:361:0x05dd, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:362:0x05df, code skipped:
            r10 = 37;
     */
    /* JADX WARNING: Missing block: B:364:0x05e9, code skipped:
            if (r9.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:365:0x05eb, code skipped:
            r10 = 72;
     */
    /* JADX WARNING: Missing block: B:367:0x05f5, code skipped:
            if (r9.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:368:0x05f7, code skipped:
            r10 = 45;
     */
    /* JADX WARNING: Missing block: B:370:0x0601, code skipped:
            if (r9.equals("CHAT_TITLE_EDITED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:371:0x0603, code skipped:
            r10 = 63;
     */
    /* JADX WARNING: Missing block: B:373:0x060d, code skipped:
            if (r9.equals("PINNED_NOTEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:374:0x060f, code skipped:
            r10 = 76;
     */
    /* JADX WARNING: Missing block: B:376:0x0619, code skipped:
            if (r9.equals("MESSAGE_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:377:0x061b, code skipped:
            r10 = null;
     */
    /* JADX WARNING: Missing block: B:379:0x0624, code skipped:
            if (r9.equals("MESSAGE_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:380:0x0626, code skipped:
            r10 = 13;
     */
    /* JADX WARNING: Missing block: B:382:0x0630, code skipped:
            if (r9.equals("MESSAGE_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:383:0x0632, code skipped:
            r10 = 14;
     */
    /* JADX WARNING: Missing block: B:385:0x063c, code skipped:
            if (r9.equals("MESSAGE_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:386:0x063e, code skipped:
            r10 = 18;
     */
    /* JADX WARNING: Missing block: B:388:0x0648, code skipped:
            if (r9.equals("MESSAGE_FWDS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:389:0x064a, code skipped:
            r10 = 22;
     */
    /* JADX WARNING: Missing block: B:391:0x0654, code skipped:
            if (r9.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:392:0x0656, code skipped:
            r10 = 44;
     */
    /* JADX WARNING: Missing block: B:394:0x0660, code skipped:
            if (r9.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:395:0x0662, code skipped:
            r10 = 53;
     */
    /* JADX WARNING: Missing block: B:397:0x066c, code skipped:
            if (r9.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:398:0x066e, code skipped:
            r10 = 54;
     */
    /* JADX WARNING: Missing block: B:400:0x0678, code skipped:
            if (r9.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:401:0x067a, code skipped:
            r10 = 58;
     */
    /* JADX WARNING: Missing block: B:403:0x0684, code skipped:
            if (r9.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:404:0x0686, code skipped:
            r10 = 71;
     */
    /* JADX WARNING: Missing block: B:406:0x0690, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:407:0x0692, code skipped:
            r10 = 20;
     */
    /* JADX WARNING: Missing block: B:409:0x069c, code skipped:
            if (r9.equals("PINNED_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:410:0x069e, code skipped:
            r10 = 87;
     */
    /* JADX WARNING: Missing block: B:412:0x06a8, code skipped:
            if (r9.equals("MESSAGE_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:413:0x06aa, code skipped:
            r10 = 12;
     */
    /* JADX WARNING: Missing block: B:415:0x06b4, code skipped:
            if (r9.equals("PINNED_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:416:0x06b6, code skipped:
            r10 = 78;
     */
    /* JADX WARNING: Missing block: B:418:0x06c0, code skipped:
            if (r9.equals("PINNED_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:419:0x06c2, code skipped:
            r10 = 79;
     */
    /* JADX WARNING: Missing block: B:421:0x06cc, code skipped:
            if (r9.equals("PINNED_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:422:0x06ce, code skipped:
            r10 = 77;
     */
    /* JADX WARNING: Missing block: B:424:0x06d8, code skipped:
            if (r9.equals("PINNED_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:425:0x06da, code skipped:
            r10 = 82;
     */
    /* JADX WARNING: Missing block: B:427:0x06e4, code skipped:
            if (r9.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:428:0x06e6, code skipped:
            r10 = 4;
     */
    /* JADX WARNING: Missing block: B:430:0x06ef, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:431:0x06f1, code skipped:
            r10 = 28;
     */
    /* JADX WARNING: Missing block: B:433:0x06fb, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:434:0x06fd, code skipped:
            r10 = 29;
     */
    /* JADX WARNING: Missing block: B:436:0x0707, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:437:0x0709, code skipped:
            r10 = 27;
     */
    /* JADX WARNING: Missing block: B:439:0x0713, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:440:0x0715, code skipped:
            r10 = 32;
     */
    /* JADX WARNING: Missing block: B:442:0x071f, code skipped:
            if (r9.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:443:0x0721, code skipped:
            r10 = 50;
     */
    /* JADX WARNING: Missing block: B:445:0x072b, code skipped:
            if (r9.equals("MESSAGES") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:446:0x072d, code skipped:
            r10 = 25;
     */
    /* JADX WARNING: Missing block: B:448:0x0737, code skipped:
            if (r9.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:449:0x0739, code skipped:
            r10 = 57;
     */
    /* JADX WARNING: Missing block: B:451:0x0743, code skipped:
            if (r9.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:452:0x0745, code skipped:
            r10 = 55;
     */
    /* JADX WARNING: Missing block: B:454:0x074f, code skipped:
            if (r9.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:455:0x0751, code skipped:
            r10 = 49;
     */
    /* JADX WARNING: Missing block: B:457:0x075b, code skipped:
            if (r9.equals("CHAT_LEFT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:458:0x075d, code skipped:
            r10 = 68;
     */
    /* JADX WARNING: Missing block: B:460:0x0767, code skipped:
            if (r9.equals("CHAT_ADD_YOU") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:461:0x0769, code skipped:
            r10 = 62;
     */
    /* JADX WARNING: Missing block: B:463:0x0773, code skipped:
            if (r9.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:464:0x0775, code skipped:
            r10 = 66;
     */
    /* JADX WARNING: Missing block: B:466:0x077f, code skipped:
            if (r9.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:467:0x0781, code skipped:
            r10 = 7;
     */
    /* JADX WARNING: Missing block: B:469:0x078a, code skipped:
            if (r9.equals("AUTH_REGION") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:470:0x078c, code skipped:
            r10 = 95;
     */
    /* JADX WARNING: Missing block: B:472:0x0796, code skipped:
            if (r9.equals("CONTACT_JOINED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:473:0x0798, code skipped:
            r10 = 93;
     */
    /* JADX WARNING: Missing block: B:475:0x07a2, code skipped:
            if (r9.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:476:0x07a4, code skipped:
            r10 = 60;
     */
    /* JADX WARNING: Missing block: B:478:0x07ae, code skipped:
            if (r9.equals("ENCRYPTION_REQUEST") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:479:0x07b0, code skipped:
            r10 = 97;
     */
    /* JADX WARNING: Missing block: B:481:0x07ba, code skipped:
            if (r9.equals("MESSAGE_GEOLIVE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:482:0x07bc, code skipped:
            r10 = 16;
     */
    /* JADX WARNING: Missing block: B:484:0x07c6, code skipped:
            if (r9.equals("CHAT_DELETE_YOU") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:485:0x07c8, code skipped:
            r10 = 67;
     */
    /* JADX WARNING: Missing block: B:487:0x07d2, code skipped:
            if (r9.equals("AUTH_UNKNOWN") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:488:0x07d4, code skipped:
            r10 = 94;
     */
    /* JADX WARNING: Missing block: B:490:0x07de, code skipped:
            if (r9.equals("PINNED_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:491:0x07e0, code skipped:
            r10 = 91;
     */
    /* JADX WARNING: Missing block: B:493:0x07ea, code skipped:
            if (r9.equals("PINNED_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:494:0x07ec, code skipped:
            r10 = 86;
     */
    /* JADX WARNING: Missing block: B:496:0x07f6, code skipped:
            if (r9.equals("PINNED_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:497:0x07f8, code skipped:
            r10 = 80;
     */
    /* JADX WARNING: Missing block: B:499:0x0802, code skipped:
            if (r9.equals("PINNED_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:500:0x0804, code skipped:
            r10 = 89;
     */
    /* JADX WARNING: Missing block: B:502:0x080e, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:503:0x0810, code skipped:
            r10 = 31;
     */
    /* JADX WARNING: Missing block: B:505:0x081a, code skipped:
            if (r9.equals("PHONE_CALL_REQUEST") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:506:0x081c, code skipped:
            r10 = 99;
     */
    /* JADX WARNING: Missing block: B:508:0x0826, code skipped:
            if (r9.equals("PINNED_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:509:0x0828, code skipped:
            r10 = 81;
     */
    /* JADX WARNING: Missing block: B:511:0x0832, code skipped:
            if (r9.equals("PINNED_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:512:0x0834, code skipped:
            r10 = 75;
     */
    /* JADX WARNING: Missing block: B:514:0x083e, code skipped:
            if (r9.equals("PINNED_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:515:0x0840, code skipped:
            r10 = 84;
     */
    /* JADX WARNING: Missing block: B:517:0x084a, code skipped:
            if (r9.equals("PINNED_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:518:0x084c, code skipped:
            r10 = 85;
     */
    /* JADX WARNING: Missing block: B:520:0x0856, code skipped:
            if (r9.equals("PINNED_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:521:0x0858, code skipped:
            r10 = 88;
     */
    /* JADX WARNING: Missing block: B:523:0x0862, code skipped:
            if (r9.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:524:0x0864, code skipped:
            r10 = 52;
     */
    /* JADX WARNING: Missing block: B:526:0x086e, code skipped:
            if (r9.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:527:0x0870, code skipped:
            r10 = 6;
     */
    /* JADX WARNING: Missing block: B:529:0x0879, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:530:0x087b, code skipped:
            r10 = 1;
     */
    /* JADX WARNING: Missing block: B:532:0x0884, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:533:0x0886, code skipped:
            r10 = 34;
     */
    /* JADX WARNING: Missing block: B:535:0x0890, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:536:0x0892, code skipped:
            r10 = 35;
     */
    /* JADX WARNING: Missing block: B:538:0x089c, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:539:0x089e, code skipped:
            r10 = 39;
     */
    /* JADX WARNING: Missing block: B:541:0x08a8, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:542:0x08aa, code skipped:
            r10 = 40;
     */
    /* JADX WARNING: Missing block: B:544:0x08b4, code skipped:
            if (r9.equals("PINNED_INVOICE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:545:0x08b6, code skipped:
            r10 = 90;
     */
    /* JADX WARNING: Missing block: B:547:0x08c0, code skipped:
            if (r9.equals("CHAT_RETURNED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:548:0x08c2, code skipped:
            r10 = 69;
     */
    /* JADX WARNING: Missing block: B:550:0x08cc, code skipped:
            if (r9.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:551:0x08ce, code skipped:
            r10 = 92;
     */
    /* JADX WARNING: Missing block: B:553:0x08d8, code skipped:
            if (r9.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:554:0x08da, code skipped:
            r10 = 98;
     */
    /* JADX WARNING: Missing block: B:556:0x08e4, code skipped:
            if (r9.equals("MESSAGE_VIDEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:557:0x08e6, code skipped:
            r10 = 5;
     */
    /* JADX WARNING: Missing block: B:559:0x08ef, code skipped:
            if (r9.equals("MESSAGE_ROUND") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:560:0x08f1, code skipped:
            r10 = 8;
     */
    /* JADX WARNING: Missing block: B:562:0x08fb, code skipped:
            if (r9.equals("MESSAGE_PHOTO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:563:0x08fd, code skipped:
            r10 = 3;
     */
    /* JADX WARNING: Missing block: B:565:0x0906, code skipped:
            if (r9.equals("MESSAGE_MUTED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:566:0x0908, code skipped:
            r10 = 100;
     */
    /* JADX WARNING: Missing block: B:568:0x0912, code skipped:
            if (r9.equals("MESSAGE_AUDIO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:569:0x0914, code skipped:
            r10 = 11;
     */
    /* JADX WARNING: Missing block: B:571:0x091e, code skipped:
            if (r9.equals("CHAT_MESSAGES") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:572:0x0920, code skipped:
            r10 = 74;
     */
    /* JADX WARNING: Missing block: B:574:0x092a, code skipped:
            if (r9.equals("CHAT_JOINED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:575:0x092c, code skipped:
            r10 = 70;
     */
    /* JADX WARNING: Missing block: B:577:0x0936, code skipped:
            if (r9.equals("CHAT_ADD_MEMBER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:578:0x0938, code skipped:
            r10 = 65;
     */
    /* JADX WARNING: Missing block: B:580:0x0941, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:581:0x0943, code skipped:
            r10 = 38;
     */
    /* JADX WARNING: Missing block: B:583:0x094c, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:584:0x094e, code skipped:
            r10 = 36;
     */
    /* JADX WARNING: Missing block: B:586:0x0957, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:587:0x0959, code skipped:
            r10 = 30;
     */
    /* JADX WARNING: Missing block: B:589:0x0962, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:590:0x0964, code skipped:
            r10 = 42;
     */
    /* JADX WARNING: Missing block: B:592:0x096d, code skipped:
            if (r9.equals("MESSAGE_STICKER") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:593:0x096f, code skipped:
            r10 = 10;
     */
    /* JADX WARNING: Missing block: B:595:0x0978, code skipped:
            if (r9.equals("CHAT_CREATED") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:596:0x097a, code skipped:
            r10 = 61;
     */
    /* JADX WARNING: Missing block: B:598:0x0983, code skipped:
            if (r9.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:599:0x0985, code skipped:
            r10 = 33;
     */
    /* JADX WARNING: Missing block: B:601:0x098e, code skipped:
            if (r9.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0993;
     */
    /* JADX WARNING: Missing block: B:602:0x0990, code skipped:
            r10 = 19;
     */
    /* JADX WARNING: Missing block: B:603:0x0993, code skipped:
            r10 = -1;
     */
    /* JADX WARNING: Missing block: B:604:0x0994, code skipped:
            r14 = "ChannelMessageFew";
            r11 = " ";
            r32 = r7;
            r7 = "AttachSticker";
     */
    /* JADX WARNING: Missing block: B:605:0x099c, code skipped:
            switch(r10) {
                case 0: goto L_0x179c;
                case 1: goto L_0x179c;
                case 2: goto L_0x1779;
                case 3: goto L_0x175a;
                case 4: goto L_0x173b;
                case 5: goto L_0x171c;
                case 6: goto L_0x16fc;
                case 7: goto L_0x16e2;
                case 8: goto L_0x16c2;
                case 9: goto L_0x16a2;
                case 10: goto L_0x1643;
                case 11: goto L_0x1623;
                case 12: goto L_0x15fe;
                case 13: goto L_0x15d9;
                case 14: goto L_0x15b4;
                case 15: goto L_0x1594;
                case 16: goto L_0x1574;
                case 17: goto L_0x1554;
                case 18: goto L_0x152f;
                case 19: goto L_0x150e;
                case 20: goto L_0x150e;
                case 21: goto L_0x14e9;
                case 22: goto L_0x14c2;
                case 23: goto L_0x1499;
                case 24: goto L_0x1470;
                case 25: goto L_0x1458;
                case 26: goto L_0x1438;
                case 27: goto L_0x1418;
                case 28: goto L_0x13f8;
                case 29: goto L_0x13d8;
                case 30: goto L_0x13b8;
                case 31: goto L_0x1359;
                case 32: goto L_0x1339;
                case 33: goto L_0x1314;
                case 34: goto L_0x12ef;
                case 35: goto L_0x12ca;
                case 36: goto L_0x12aa;
                case 37: goto L_0x128a;
                case 38: goto L_0x126a;
                case 39: goto L_0x124a;
                case 40: goto L_0x121e;
                case 41: goto L_0x11f6;
                case 42: goto L_0x11ce;
                case 43: goto L_0x11b7;
                case 44: goto L_0x1194;
                case 45: goto L_0x116f;
                case 46: goto L_0x114a;
                case 47: goto L_0x1125;
                case 48: goto L_0x1100;
                case 49: goto L_0x10db;
                case 50: goto L_0x1059;
                case 51: goto L_0x1032;
                case 52: goto L_0x100b;
                case 53: goto L_0x0fe4;
                case 54: goto L_0x0fbd;
                case 55: goto L_0x0f9a;
                case 56: goto L_0x0var_;
                case 57: goto L_0x0var_;
                case 58: goto L_0x0f2c;
                case 59: goto L_0x0var_;
                case 60: goto L_0x0edd;
                case 61: goto L_0x0ec3;
                case 62: goto L_0x0ec3;
                case 63: goto L_0x0ea9;
                case 64: goto L_0x0e8f;
                case 65: goto L_0x0e70;
                case 66: goto L_0x0e56;
                case 67: goto L_0x0e3c;
                case 68: goto L_0x0e22;
                case 69: goto L_0x0e08;
                case 70: goto L_0x0dee;
                case 71: goto L_0x0dc0;
                case 72: goto L_0x0d93;
                case 73: goto L_0x0d66;
                case 74: goto L_0x0d4a;
                case 75: goto L_0x0d13;
                case 76: goto L_0x0ce3;
                case 77: goto L_0x0cb6;
                case 78: goto L_0x0CLASSNAME;
                case 79: goto L_0x0c5a;
                case 80: goto L_0x0c2b;
                case 81: goto L_0x0bae;
                case 82: goto L_0x0b7f;
                case 83: goto L_0x0b45;
                case 84: goto L_0x0b0b;
                case 85: goto L_0x0ad5;
                case 86: goto L_0x0aa5;
                case 87: goto L_0x0a7a;
                case 88: goto L_0x0a4f;
                case 89: goto L_0x0a22;
                case 90: goto L_0x09f5;
                case 91: goto L_0x09c8;
                case 92: goto L_0x09ad;
                case 93: goto L_0x09a7;
                case 94: goto L_0x09a7;
                case 95: goto L_0x09a7;
                case 96: goto L_0x09a7;
                case 97: goto L_0x09a7;
                case 98: goto L_0x09a7;
                case 99: goto L_0x09a7;
                case 100: goto L_0x09a7;
                case 101: goto L_0x09a7;
                default: goto L_0x099f;
            };
     */
    /* JADX WARNING: Missing block: B:606:0x099f, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:609:0x09a7, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:611:?, code skipped:
            r7 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
            r21 = r1;
            r25 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:612:0x09c5, code skipped:
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:613:0x09c8, code skipped:
            if (r1 == 0) goto L_0x09e2;
     */
    /* JADX WARNING: Missing block: B:614:0x09ca, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:615:0x09e2, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:616:0x09f5, code skipped:
            if (r1 == 0) goto L_0x0a0f;
     */
    /* JADX WARNING: Missing block: B:617:0x09f7, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:618:0x0a0f, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:619:0x0a22, code skipped:
            if (r1 == 0) goto L_0x0a3c;
     */
    /* JADX WARNING: Missing block: B:620:0x0a24, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:621:0x0a3c, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:622:0x0a4f, code skipped:
            if (r1 == 0) goto L_0x0a68;
     */
    /* JADX WARNING: Missing block: B:623:0x0a51, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:624:0x0a68, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:625:0x0a7a, code skipped:
            if (r1 == 0) goto L_0x0a93;
     */
    /* JADX WARNING: Missing block: B:626:0x0a7c, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:627:0x0a93, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:628:0x0aa5, code skipped:
            if (r1 == 0) goto L_0x0abe;
     */
    /* JADX WARNING: Missing block: B:629:0x0aa7, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:630:0x0abe, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:631:0x0acf, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:632:0x0ad5, code skipped:
            if (r1 == 0) goto L_0x0af4;
     */
    /* JADX WARNING: Missing block: B:633:0x0ad7, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Missing block: B:634:0x0af4, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:635:0x0b0b, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:636:0x0b0d, code skipped:
            if (r1 == 0) goto L_0x0b2d;
     */
    /* JADX WARNING: Missing block: B:637:0x0b0f, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Missing block: B:638:0x0b2d, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:639:0x0b45, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:640:0x0b47, code skipped:
            if (r1 == 0) goto L_0x0b67;
     */
    /* JADX WARNING: Missing block: B:641:0x0b49, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Missing block: B:642:0x0b67, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:643:0x0b7f, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:644:0x0b81, code skipped:
            if (r1 == 0) goto L_0x0b9b;
     */
    /* JADX WARNING: Missing block: B:645:0x0b83, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:646:0x0b9b, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:647:0x0bae, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:648:0x0bb0, code skipped:
            if (r1 == 0) goto L_0x0bf4;
     */
    /* JADX WARNING: Missing block: B:650:0x0bb4, code skipped:
            if (r15.length <= 2) goto L_0x0bdc;
     */
    /* JADX WARNING: Missing block: B:652:0x0bbc, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x0bdc;
     */
    /* JADX WARNING: Missing block: B:653:0x0bbe, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r15[0], r15[2], r15[1]);
     */
    /* JADX WARNING: Missing block: B:654:0x0bdc, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:656:0x0bf6, code skipped:
            if (r15.length <= 1) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:658:0x0bfe, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:659:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:660:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:661:0x0c2b, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:662:0x0c2d, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:663:0x0c2f, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:664:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:665:0x0c5a, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:666:0x0c5c, code skipped:
            if (r1 == 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Missing block: B:667:0x0c5e, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:668:0x0CLASSNAME, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:669:0x0CLASSNAME, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:670:0x0c8b, code skipped:
            if (r1 == 0) goto L_0x0ca4;
     */
    /* JADX WARNING: Missing block: B:671:0x0c8d, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:672:0x0ca4, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:673:0x0cb6, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:674:0x0cb8, code skipped:
            if (r1 == 0) goto L_0x0cd1;
     */
    /* JADX WARNING: Missing block: B:675:0x0cba, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:676:0x0cd1, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:677:0x0ce3, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:678:0x0ce5, code skipped:
            if (r1 == 0) goto L_0x0cfe;
     */
    /* JADX WARNING: Missing block: B:679:0x0ce7, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:680:0x0cfe, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:681:0x0d0f, code skipped:
            r21 = r1;
     */
    /* JADX WARNING: Missing block: B:682:0x0d13, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:683:0x0d15, code skipped:
            if (r1 == 0) goto L_0x0d33;
     */
    /* JADX WARNING: Missing block: B:684:0x0d17, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:685:0x0d33, code skipped:
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:686:0x0d4a, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:687:0x0d62, code skipped:
            r21 = r1;
     */
    /* JADX WARNING: Missing block: B:688:0x0d66, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:689:0x0d93, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:690:0x0dc0, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r15[0], r15[1], org.telegram.messenger.LocaleController.formatPluralString(r8, org.telegram.messenger.Utilities.parseInt(r15[2]).intValue()));
     */
    /* JADX WARNING: Missing block: B:691:0x0dee, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:692:0x0e08, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:693:0x0e22, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:694:0x0e3c, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:695:0x0e56, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:696:0x0e70, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:697:0x0e8f, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:698:0x0ea9, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:699:0x0ec3, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r15[0], r15[1]);
     */
    /* JADX WARNING: Missing block: B:700:0x0edd, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:701:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r15[0], r15[1], r15[2], r15[3]);
     */
    /* JADX WARNING: Missing block: B:702:0x0f2c, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:703:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:704:0x0var_, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:705:0x0f9a, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:706:0x0fbd, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:707:0x0fe4, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Missing block: B:708:0x100b, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r15[0], r15[1], r15[2]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:709:0x1032, code skipped:
            r10 = r32;
            r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r15[0], r15[1]);
            r8 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:710:0x1053, code skipped:
            r21 = r1;
            r16 = r8;
     */
    /* JADX WARNING: Missing block: B:711:0x1059, code skipped:
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:712:0x105d, code skipped:
            if (r15.length <= 2) goto L_0x10a4;
     */
    /* JADX WARNING: Missing block: B:714:0x1065, code skipped:
            if (android.text.TextUtils.isEmpty(r15[2]) != false) goto L_0x10a4;
     */
    /* JADX WARNING: Missing block: B:715:0x1067, code skipped:
            r21 = r1;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r15[0], r15[1], r15[2]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[2]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:716:0x10a4, code skipped:
            r21 = r1;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:717:0x10db, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:718:0x1100, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:719:0x1125, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:720:0x114a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:721:0x116f, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:722:0x1194, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r15[0], r15[1], r15[2]);
            r7 = r15[2];
     */
    /* JADX WARNING: Missing block: B:723:0x11b7, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:724:0x11ce, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:725:0x11f6, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:726:0x121e, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString(r14, NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()).toLowerCase());
     */
    /* JADX WARNING: Missing block: B:727:0x124a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:728:0x126a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:729:0x128a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:730:0x12aa, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:731:0x12ca, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:732:0x12ef, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Missing block: B:733:0x1314, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:734:0x1339, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:735:0x1359, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:736:0x135f, code skipped:
            if (r15.length <= 1) goto L_0x139e;
     */
    /* JADX WARNING: Missing block: B:738:0x1367, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x139e;
     */
    /* JADX WARNING: Missing block: B:739:0x1369, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:740:0x139e, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString(r7, NUM);
     */
    /* JADX WARNING: Missing block: B:741:0x13b8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:742:0x13d8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:743:0x13f8, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:744:0x1418, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:745:0x1438, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:746:0x1458, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r15[0]);
     */
    /* JADX WARNING: Missing block: B:747:0x146d, code skipped:
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:748:0x1470, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:749:0x1499, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:750:0x14c2, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r15[0], org.telegram.messenger.LocaleController.formatPluralString(r8, org.telegram.messenger.Utilities.parseInt(r15[1]).intValue()));
     */
    /* JADX WARNING: Missing block: B:751:0x14e9, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Missing block: B:752:0x150e, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r15[0], r15[1], r15[2]);
     */
    /* JADX WARNING: Missing block: B:753:0x152f, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Missing block: B:754:0x1554, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Missing block: B:755:0x1574, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:756:0x1594, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Missing block: B:757:0x15b4, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Missing block: B:758:0x15d9, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Missing block: B:759:0x15fe, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r15[0], r15[1]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Missing block: B:760:0x1623, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Missing block: B:761:0x1643, code skipped:
            r21 = r1;
            r10 = r32;
     */
    /* JADX WARNING: Missing block: B:762:0x1649, code skipped:
            if (r15.length <= 1) goto L_0x1688;
     */
    /* JADX WARNING: Missing block: B:764:0x1651, code skipped:
            if (android.text.TextUtils.isEmpty(r15[1]) != false) goto L_0x1688;
     */
    /* JADX WARNING: Missing block: B:765:0x1653, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r15[0], r15[1]);
            r8 = new java.lang.StringBuilder();
            r8.append(r15[1]);
            r8.append(r11);
            r8.append(org.telegram.messenger.LocaleController.getString(r7, NUM));
            r7 = r8.toString();
     */
    /* JADX WARNING: Missing block: B:766:0x1688, code skipped:
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString(r7, NUM);
     */
    /* JADX WARNING: Missing block: B:767:0x16a2, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Missing block: B:768:0x16c2, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Missing block: B:769:0x16e2, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r15[0]);
     */
    /* JADX WARNING: Missing block: B:770:0x16f8, code skipped:
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:771:0x16f9, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:772:0x16fc, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:773:0x171c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Missing block: B:774:0x173b, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:775:0x175a, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Missing block: B:776:0x1779, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r15[0]);
            r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Missing block: B:777:0x1797, code skipped:
            r16 = r7;
            r7 = r1;
     */
    /* JADX WARNING: Missing block: B:778:0x179a, code skipped:
            r1 = false;
     */
    /* JADX WARNING: Missing block: B:779:0x179c, code skipped:
            r21 = r1;
            r10 = r32;
            r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r15[0], r15[1]);
            r7 = r15[1];
     */
    /* JADX WARNING: Missing block: B:780:0x17b9, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x17cf;
     */
    /* JADX WARNING: Missing block: B:781:0x17bb, code skipped:
            r1 = new java.lang.StringBuilder();
            r1.append("unhandled loc_key = ");
            r1.append(r9);
            org.telegram.messenger.FileLog.w(r1.toString());
     */
    /* JADX WARNING: Missing block: B:782:0x17cf, code skipped:
            r1 = false;
            r7 = null;
     */
    /* JADX WARNING: Missing block: B:783:0x17d1, code skipped:
            r16 = null;
     */
    /* JADX WARNING: Missing block: B:784:0x17d3, code skipped:
            if (r7 == null) goto L_0x1872;
     */
    /* JADX WARNING: Missing block: B:786:?, code skipped:
            r8 = new org.telegram.tgnet.TLRPC.TL_message();
            r8.id = r10;
            r8.random_id = r3;
     */
    /* JADX WARNING: Missing block: B:787:0x17de, code skipped:
            if (r16 == null) goto L_0x17e3;
     */
    /* JADX WARNING: Missing block: B:788:0x17e0, code skipped:
            r3 = r16;
     */
    /* JADX WARNING: Missing block: B:789:0x17e3, code skipped:
            r3 = r7;
     */
    /* JADX WARNING: Missing block: B:790:0x17e4, code skipped:
            r8.message = r3;
            r8.date = (int) (r37 / 1000);
     */
    /* JADX WARNING: Missing block: B:791:0x17ed, code skipped:
            if (r5 == null) goto L_0x17f6;
     */
    /* JADX WARNING: Missing block: B:793:?, code skipped:
            r8.action = new org.telegram.tgnet.TLRPC.TL_messageActionPinMessage();
     */
    /* JADX WARNING: Missing block: B:794:0x17f6, code skipped:
            if (r6 == null) goto L_0x17ff;
     */
    /* JADX WARNING: Missing block: B:795:0x17f8, code skipped:
            r8.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Missing block: B:797:?, code skipped:
            r8.dialog_id = r12;
     */
    /* JADX WARNING: Missing block: B:798:0x1801, code skipped:
            if (r2 == 0) goto L_0x180f;
     */
    /* JADX WARNING: Missing block: B:800:?, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChannel();
            r8.to_id.channel_id = r2;
     */
    /* JADX WARNING: Missing block: B:801:0x180f, code skipped:
            if (r22 == 0) goto L_0x181f;
     */
    /* JADX WARNING: Missing block: B:802:0x1811, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerChat();
            r8.to_id.chat_id = r22;
     */
    /* JADX WARNING: Missing block: B:804:?, code skipped:
            r8.to_id = new org.telegram.tgnet.TLRPC.TL_peerUser();
            r8.to_id.user_id = r24;
     */
    /* JADX WARNING: Missing block: B:805:0x182c, code skipped:
            r8.flags |= 256;
            r8.from_id = r21;
     */
    /* JADX WARNING: Missing block: B:806:0x1836, code skipped:
            if (r20 != null) goto L_0x183d;
     */
    /* JADX WARNING: Missing block: B:807:0x1838, code skipped:
            if (r5 == null) goto L_0x183b;
     */
    /* JADX WARNING: Missing block: B:809:0x183b, code skipped:
            r2 = false;
     */
    /* JADX WARNING: Missing block: B:810:0x183d, code skipped:
            r2 = true;
     */
    /* JADX WARNING: Missing block: B:811:0x183e, code skipped:
            r8.mentioned = r2;
            r8.silent = r19;
            r8.from_scheduled = r23;
            r19 = new org.telegram.messenger.MessageObject(r30, r8, r7, r25, r31, r1, r26, r27);
            r1 = new java.util.ArrayList();
            r1.add(r19);
     */
    /* JADX WARNING: Missing block: B:812:0x1867, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:814:?, code skipped:
            org.telegram.messenger.NotificationsController.getInstance(r30).processNewMessages(r1, true, true, r3.countDownLatch);
            r28 = null;
     */
    /* JADX WARNING: Missing block: B:815:0x1872, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:816:0x1875, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:817:0x1876, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:818:0x1879, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:819:0x187a, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:820:0x187d, code skipped:
            r3 = r35;
     */
    /* JADX WARNING: Missing block: B:821:0x1880, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:822:0x1881, code skipped:
            r3 = r1;
     */
    /* JADX WARNING: Missing block: B:823:0x1883, code skipped:
            r3 = r1;
            r29 = r14;
     */
    /* JADX WARNING: Missing block: B:824:0x1886, code skipped:
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:825:0x1888, code skipped:
            r28 = 1;
     */
    /* JADX WARNING: Missing block: B:826:0x188a, code skipped:
            if (r28 == null) goto L_0x1891;
     */
    /* JADX WARNING: Missing block: B:827:0x188c, code skipped:
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:828:0x1891, code skipped:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);
            org.telegram.tgnet.ConnectionsManager.getInstance(r30).resumeNetworkMaybe();
     */
    /* JADX WARNING: Missing block: B:829:0x189d, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:831:0x18a5, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:832:0x18a6, code skipped:
            r3 = r1;
            r29 = r14;
            r30 = r15;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:833:0x18ae, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:834:0x18af, code skipped:
            r3 = r1;
            r29 = r7;
     */
    /* JADX WARNING: Missing block: B:835:0x18b2, code skipped:
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:836:0x18b6, code skipped:
            r3 = r1;
            r29 = r7;
            r30 = r15;
     */
    /* JADX WARNING: Missing block: B:841:0x18bf, code skipped:
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:845:?, code skipped:
            org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4(r15));
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:846:0x18cc, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:847:0x18cd, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:848:0x18d0, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:849:0x18d1, code skipped:
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:850:0x18d5, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:851:0x18d6, code skipped:
            r15 = r30;
     */
    /* JADX WARNING: Missing block: B:852:0x18da, code skipped:
            r3 = r1;
            r29 = r7;
     */
    /* JADX WARNING: Missing block: B:856:?, code skipped:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ(r15));
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:857:0x18ea, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:858:0x18eb, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:860:0x18ee, code skipped:
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
    /* JADX WARNING: Missing block: B:864:?, code skipped:
            org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo(r15, r2));
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:865:0x1936, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:866:0x1937, code skipped:
            r3 = r1;
            r29 = r7;
            r1 = r11.getInt("dc");
            r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Missing block: B:867:0x194e, code skipped:
            if (r2.length == 2) goto L_0x1956;
     */
    /* JADX WARNING: Missing block: B:868:0x1950, code skipped:
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:869:0x1955, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:870:0x1956, code skipped:
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:871:0x1973, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:872:0x1974, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:885:0x199b, code skipped:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
            org.telegram.tgnet.ConnectionsManager.getInstance(r15).resumeNetworkMaybe();
            r3.countDownLatch.countDown();
     */
    /* JADX WARNING: Missing block: B:886:0x19ab, code skipped:
            onDecryptError();
     */
    /* JADX WARNING: Missing block: B:889:0x19b2, code skipped:
            r2 = new java.lang.StringBuilder();
            r2.append("error in loc_key = ");
            r2.append(r9);
            r2.append(" json ");
            r2.append(r14);
            org.telegram.messenger.FileLog.e(r2.toString());
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
        r5 = r2.get(r5);	 Catch:{ all -> 0x1992 }
        r6 = r5 instanceof java.lang.String;	 Catch:{ all -> 0x1992 }
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
        goto L_0x1999;
    L_0x002d:
        r5 = (java.lang.String) r5;	 Catch:{ all -> 0x1992 }
        r6 = 8;
        r5 = android.util.Base64.decode(r5, r6);	 Catch:{ all -> 0x1992 }
        r7 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x1992 }
        r8 = r5.length;	 Catch:{ all -> 0x1992 }
        r7.<init>(r8);	 Catch:{ all -> 0x1992 }
        r7.writeBytes(r5);	 Catch:{ all -> 0x1992 }
        r8 = 0;
        r7.position(r8);	 Catch:{ all -> 0x1992 }
        r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x1992 }
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
        r9 = new byte[r6];	 Catch:{ all -> 0x1992 }
        r10 = 1;
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x1992 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId;	 Catch:{ all -> 0x1992 }
        r11 = java.util.Arrays.equals(r11, r9);	 Catch:{ all -> 0x1992 }
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
        r9 = new byte[r9];	 Catch:{ all -> 0x1992 }
        r7.readBytes(r9, r10);	 Catch:{ all -> 0x1992 }
        r11 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x1992 }
        r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13);	 Catch:{ all -> 0x1992 }
        r14 = r7.buffer;	 Catch:{ all -> 0x1992 }
        r15 = r11.aesKey;	 Catch:{ all -> 0x1992 }
        r11 = r11.aesIv;	 Catch:{ all -> 0x1992 }
        r17 = 0;
        r18 = 0;
        r19 = 24;
        r5 = r5.length;	 Catch:{ all -> 0x1992 }
        r20 = r5 + -24;
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ all -> 0x1992 }
        r21 = org.telegram.messenger.SharedConfig.pushAuthKey;	 Catch:{ all -> 0x1992 }
        r22 = 96;
        r23 = 32;
        r5 = r7.buffer;	 Catch:{ all -> 0x1992 }
        r25 = 24;
        r11 = r7.buffer;	 Catch:{ all -> 0x1992 }
        r26 = r11.limit();	 Catch:{ all -> 0x1992 }
        r24 = r5;
        r5 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26);	 Catch:{ all -> 0x1992 }
        r5 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r5, r6);	 Catch:{ all -> 0x1992 }
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
        r5 = r7.readInt32(r10);	 Catch:{ all -> 0x1992 }
        r5 = new byte[r5];	 Catch:{ all -> 0x1992 }
        r7.readBytes(r5, r10);	 Catch:{ all -> 0x1992 }
        r7 = new java.lang.String;	 Catch:{ all -> 0x1992 }
        r7.<init>(r5);	 Catch:{ all -> 0x1992 }
        r5 = new org.json.JSONObject;	 Catch:{ all -> 0x1988 }
        r5.<init>(r7);	 Catch:{ all -> 0x1988 }
        r9 = "loc_key";
        r9 = r5.has(r9);	 Catch:{ all -> 0x1988 }
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
        r11 = r5.get(r11);	 Catch:{ all -> 0x197f }
        r11 = r11 instanceof org.json.JSONObject;	 Catch:{ all -> 0x197f }
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
        r11 = new org.json.JSONObject;	 Catch:{ all -> 0x197f }
        r11.<init>();	 Catch:{ all -> 0x197f }
    L_0x0131:
        r14 = "user_id";
        r14 = r5.has(r14);	 Catch:{ all -> 0x197f }
        if (r14 == 0) goto L_0x0142;
    L_0x013a:
        r14 = "user_id";
        r14 = r5.get(r14);	 Catch:{ all -> 0x0126 }
        goto L_0x0143;
    L_0x0142:
        r14 = 0;
    L_0x0143:
        if (r14 != 0) goto L_0x0150;
    L_0x0145:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x0126 }
        goto L_0x0174;
    L_0x0150:
        r15 = r14 instanceof java.lang.Integer;	 Catch:{ all -> 0x197f }
        if (r15 == 0) goto L_0x015b;
    L_0x0154:
        r14 = (java.lang.Integer) r14;	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0174;
    L_0x015b:
        r15 = r14 instanceof java.lang.String;	 Catch:{ all -> 0x197f }
        if (r15 == 0) goto L_0x016a;
    L_0x015f:
        r14 = (java.lang.String) r14;	 Catch:{ all -> 0x0126 }
        r14 = org.telegram.messenger.Utilities.parseInt(r14);	 Catch:{ all -> 0x0126 }
        r14 = r14.intValue();	 Catch:{ all -> 0x0126 }
        goto L_0x0174;
    L_0x016a:
        r14 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x197f }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ all -> 0x197f }
        r14 = r14.getClientUserId();	 Catch:{ all -> 0x197f }
    L_0x0174:
        r15 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ all -> 0x197f }
        r4 = 0;
    L_0x0177:
        if (r4 >= r12) goto L_0x018a;
    L_0x0179:
        r17 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ all -> 0x0126 }
        r6 = r17.getClientUserId();	 Catch:{ all -> 0x0126 }
        if (r6 != r14) goto L_0x0185;
    L_0x0183:
        r15 = r4;
        goto L_0x018a;
    L_0x0185:
        r4 = r4 + 1;
        r6 = 8;
        goto L_0x0177;
    L_0x018a:
        r4 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ all -> 0x1976 }
        r4 = r4.isClientActivated();	 Catch:{ all -> 0x1976 }
        if (r4 != 0) goto L_0x01a9;
    L_0x0194:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x01a3 }
        if (r2 == 0) goto L_0x019d;
    L_0x0198:
        r2 = "GCM ACCOUNT NOT ACTIVATED";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x01a3 }
    L_0x019d:
        r2 = r1.countDownLatch;	 Catch:{ all -> 0x01a3 }
        r2.countDown();	 Catch:{ all -> 0x01a3 }
        return;
    L_0x01a3:
        r0 = move-exception;
        r3 = r1;
        r14 = r7;
    L_0x01a6:
        r2 = -1;
        goto L_0x002a;
    L_0x01a9:
        r4 = "google.sent_time";
        r2.get(r4);	 Catch:{ all -> 0x1976 }
        r2 = r9.hashCode();	 Catch:{ all -> 0x1976 }
        switch(r2) {
            case -1963663249: goto L_0x01d4;
            case -920689527: goto L_0x01ca;
            case 633004703: goto L_0x01c0;
            case 1365673842: goto L_0x01b6;
            default: goto L_0x01b5;
        };
    L_0x01b5:
        goto L_0x01de;
    L_0x01b6:
        r2 = "GEO_LIVE_PENDING";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a3 }
        if (r2 == 0) goto L_0x01de;
    L_0x01be:
        r2 = 3;
        goto L_0x01df;
    L_0x01c0:
        r2 = "MESSAGE_ANNOUNCEMENT";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a3 }
        if (r2 == 0) goto L_0x01de;
    L_0x01c8:
        r2 = 1;
        goto L_0x01df;
    L_0x01ca:
        r2 = "DC_UPDATE";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a3 }
        if (r2 == 0) goto L_0x01de;
    L_0x01d2:
        r2 = 0;
        goto L_0x01df;
    L_0x01d4:
        r2 = "SESSION_REVOKE";
        r2 = r9.equals(r2);	 Catch:{ all -> 0x01a3 }
        if (r2 == 0) goto L_0x01de;
    L_0x01dc:
        r2 = 2;
        goto L_0x01df;
    L_0x01de:
        r2 = -1;
    L_0x01df:
        if (r2 == 0) goto L_0x1937;
    L_0x01e1:
        if (r2 == r10) goto L_0x18ee;
    L_0x01e3:
        if (r2 == r13) goto L_0x18da;
    L_0x01e5:
        if (r2 == r12) goto L_0x18b6;
    L_0x01e7:
        r2 = "channel_id";
        r2 = r11.has(r2);	 Catch:{ all -> 0x18ae }
        r19 = 0;
        if (r2 == 0) goto L_0x01fa;
    L_0x01f1:
        r2 = "channel_id";
        r2 = r11.getInt(r2);	 Catch:{ all -> 0x01a3 }
        r4 = -r2;
        r3 = (long) r4;
        goto L_0x01fd;
    L_0x01fa:
        r3 = r19;
        r2 = 0;
    L_0x01fd:
        r14 = "from_id";
        r14 = r11.has(r14);	 Catch:{ all -> 0x18ae }
        if (r14 == 0) goto L_0x0217;
    L_0x0205:
        r3 = "from_id";
        r3 = r11.getInt(r3);	 Catch:{ all -> 0x0213 }
        r14 = r7;
        r6 = (long) r3;
        r33 = r6;
        r6 = r3;
        r3 = r33;
        goto L_0x0219;
    L_0x0213:
        r0 = move-exception;
        r14 = r7;
    L_0x0215:
        r3 = r1;
        goto L_0x01a6;
    L_0x0217:
        r14 = r7;
        r6 = 0;
    L_0x0219:
        r7 = "chat_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x18a5 }
        if (r7 == 0) goto L_0x022c;
    L_0x0221:
        r3 = "chat_id";
        r3 = r11.getInt(r3);	 Catch:{ all -> 0x022a }
        r4 = -r3;
        r12 = (long) r4;
        goto L_0x022e;
    L_0x022a:
        r0 = move-exception;
        goto L_0x0215;
    L_0x022c:
        r12 = r3;
        r3 = 0;
    L_0x022e:
        r4 = "encryption_id";
        r4 = r11.has(r4);	 Catch:{ all -> 0x18a5 }
        if (r4 == 0) goto L_0x0240;
    L_0x0236:
        r4 = "encryption_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x022a }
        r12 = (long) r4;
        r4 = 32;
        r12 = r12 << r4;
    L_0x0240:
        r4 = "schedule";
        r4 = r11.has(r4);	 Catch:{ all -> 0x18a5 }
        if (r4 == 0) goto L_0x0252;
    L_0x0248:
        r4 = "schedule";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x022a }
        if (r4 != r10) goto L_0x0252;
    L_0x0250:
        r4 = 1;
        goto L_0x0253;
    L_0x0252:
        r4 = 0;
    L_0x0253:
        r21 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r21 != 0) goto L_0x0264;
    L_0x0257:
        r7 = "ENCRYPTED_MESSAGE";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x022a }
        if (r7 == 0) goto L_0x0264;
    L_0x025f:
        r12 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
    L_0x0264:
        r7 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1));
        if (r7 == 0) goto L_0x1883;
    L_0x0268:
        r7 = "READ_HISTORY";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x18a5 }
        r10 = " for dialogId = ";
        if (r7 == 0) goto L_0x02e0;
    L_0x0272:
        r4 = "max_id";
        r4 = r11.getInt(r4);	 Catch:{ all -> 0x022a }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x022a }
        r5.<init>();	 Catch:{ all -> 0x022a }
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x022a }
        if (r7 == 0) goto L_0x029b;
    L_0x0281:
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x022a }
        r7.<init>();	 Catch:{ all -> 0x022a }
        r8 = "GCM received read notification max_id = ";
        r7.append(r8);	 Catch:{ all -> 0x022a }
        r7.append(r4);	 Catch:{ all -> 0x022a }
        r7.append(r10);	 Catch:{ all -> 0x022a }
        r7.append(r12);	 Catch:{ all -> 0x022a }
        r7 = r7.toString();	 Catch:{ all -> 0x022a }
        org.telegram.messenger.FileLog.d(r7);	 Catch:{ all -> 0x022a }
    L_0x029b:
        if (r2 == 0) goto L_0x02aa;
    L_0x029d:
        r3 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox;	 Catch:{ all -> 0x022a }
        r3.<init>();	 Catch:{ all -> 0x022a }
        r3.channel_id = r2;	 Catch:{ all -> 0x022a }
        r3.max_id = r4;	 Catch:{ all -> 0x022a }
        r5.add(r3);	 Catch:{ all -> 0x022a }
        goto L_0x02cd;
    L_0x02aa:
        r2 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox;	 Catch:{ all -> 0x022a }
        r2.<init>();	 Catch:{ all -> 0x022a }
        if (r6 == 0) goto L_0x02bd;
    L_0x02b1:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x022a }
        r3.<init>();	 Catch:{ all -> 0x022a }
        r2.peer = r3;	 Catch:{ all -> 0x022a }
        r3 = r2.peer;	 Catch:{ all -> 0x022a }
        r3.user_id = r6;	 Catch:{ all -> 0x022a }
        goto L_0x02c8;
    L_0x02bd:
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x022a }
        r6.<init>();	 Catch:{ all -> 0x022a }
        r2.peer = r6;	 Catch:{ all -> 0x022a }
        r6 = r2.peer;	 Catch:{ all -> 0x022a }
        r6.chat_id = r3;	 Catch:{ all -> 0x022a }
    L_0x02c8:
        r2.max_id = r4;	 Catch:{ all -> 0x022a }
        r5.add(r2);	 Catch:{ all -> 0x022a }
    L_0x02cd:
        r16 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x022a }
        r18 = 0;
        r19 = 0;
        r20 = 0;
        r21 = 0;
        r17 = r5;
        r16.processUpdateArray(r17, r18, r19, r20, r21);	 Catch:{ all -> 0x022a }
        goto L_0x1883;
    L_0x02e0:
        r7 = "MESSAGE_DELETED";
        r7 = r7.equals(r9);	 Catch:{ all -> 0x18a5 }
        r8 = "messages";
        if (r7 == 0) goto L_0x034d;
    L_0x02ea:
        r3 = r11.getString(r8);	 Catch:{ all -> 0x022a }
        r4 = ",";
        r3 = r3.split(r4);	 Catch:{ all -> 0x022a }
        r4 = new android.util.SparseArray;	 Catch:{ all -> 0x022a }
        r4.<init>();	 Catch:{ all -> 0x022a }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x022a }
        r5.<init>();	 Catch:{ all -> 0x022a }
        r6 = 0;
    L_0x02ff:
        r7 = r3.length;	 Catch:{ all -> 0x022a }
        if (r6 >= r7) goto L_0x030e;
    L_0x0302:
        r7 = r3[r6];	 Catch:{ all -> 0x022a }
        r7 = org.telegram.messenger.Utilities.parseInt(r7);	 Catch:{ all -> 0x022a }
        r5.add(r7);	 Catch:{ all -> 0x022a }
        r6 = r6 + 1;
        goto L_0x02ff;
    L_0x030e:
        r4.put(r2, r5);	 Catch:{ all -> 0x022a }
        r3 = org.telegram.messenger.NotificationsController.getInstance(r15);	 Catch:{ all -> 0x022a }
        r3.removeDeletedMessagesFromNotifications(r4);	 Catch:{ all -> 0x022a }
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x022a }
        r3.deleteMessagesByPush(r12, r5, r2);	 Catch:{ all -> 0x022a }
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x022a }
        if (r2 == 0) goto L_0x1883;
    L_0x0323:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x022a }
        r2.<init>();	 Catch:{ all -> 0x022a }
        r3 = "GCM received ";
        r2.append(r3);	 Catch:{ all -> 0x022a }
        r2.append(r9);	 Catch:{ all -> 0x022a }
        r2.append(r10);	 Catch:{ all -> 0x022a }
        r2.append(r12);	 Catch:{ all -> 0x022a }
        r3 = " mids = ";
        r2.append(r3);	 Catch:{ all -> 0x022a }
        r3 = ",";
        r3 = android.text.TextUtils.join(r3, r5);	 Catch:{ all -> 0x022a }
        r2.append(r3);	 Catch:{ all -> 0x022a }
        r2 = r2.toString();	 Catch:{ all -> 0x022a }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ all -> 0x022a }
        goto L_0x1883;
    L_0x034d:
        r7 = android.text.TextUtils.isEmpty(r9);	 Catch:{ all -> 0x18a5 }
        if (r7 != 0) goto L_0x1883;
    L_0x0353:
        r7 = "msg_id";
        r7 = r11.has(r7);	 Catch:{ all -> 0x18a5 }
        if (r7 == 0) goto L_0x0364;
    L_0x035b:
        r7 = "msg_id";
        r7 = r11.getInt(r7);	 Catch:{ all -> 0x022a }
        r29 = r14;
        goto L_0x0367;
    L_0x0364:
        r29 = r14;
        r7 = 0;
    L_0x0367:
        r14 = "random_id";
        r14 = r11.has(r14);	 Catch:{ all -> 0x1880 }
        if (r14 == 0) goto L_0x038b;
    L_0x036f:
        r14 = "random_id";
        r14 = r11.getString(r14);	 Catch:{ all -> 0x0385 }
        r14 = org.telegram.messenger.Utilities.parseLong(r14);	 Catch:{ all -> 0x0385 }
        r22 = r14.longValue();	 Catch:{ all -> 0x0385 }
        r14 = r4;
        r33 = r22;
        r22 = r3;
        r3 = r33;
        goto L_0x0390;
    L_0x0385:
        r0 = move-exception;
        r3 = r1;
        r14 = r29;
        goto L_0x01a6;
    L_0x038b:
        r22 = r3;
        r14 = r4;
        r3 = r19;
    L_0x0390:
        if (r7 == 0) goto L_0x03d5;
    L_0x0392:
        r23 = r14;
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x03cc }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x03cc }
        r1 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x03cc }
        r1 = r14.get(r1);	 Catch:{ all -> 0x03cc }
        r1 = (java.lang.Integer) r1;	 Catch:{ all -> 0x03cc }
        if (r1 != 0) goto L_0x03c3;
    L_0x03a6:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x03cc }
        r14 = 0;
        r1 = r1.getDialogReadMax(r14, r12);	 Catch:{ all -> 0x03cc }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x03cc }
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ all -> 0x03cc }
        r14 = r14.dialogs_read_inbox_max;	 Catch:{ all -> 0x03cc }
        r24 = r6;
        r6 = java.lang.Long.valueOf(r12);	 Catch:{ all -> 0x03cc }
        r14.put(r6, r1);	 Catch:{ all -> 0x03cc }
        goto L_0x03c5;
    L_0x03c3:
        r24 = r6;
    L_0x03c5:
        r1 = r1.intValue();	 Catch:{ all -> 0x03cc }
        if (r7 <= r1) goto L_0x03e9;
    L_0x03cb:
        goto L_0x03e7;
    L_0x03cc:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r29;
        goto L_0x1999;
    L_0x03d5:
        r24 = r6;
        r23 = r14;
        r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1));
        if (r1 == 0) goto L_0x03e9;
    L_0x03dd:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r15);	 Catch:{ all -> 0x03cc }
        r1 = r1.checkMessageByRandomId(r3);	 Catch:{ all -> 0x03cc }
        if (r1 != 0) goto L_0x03e9;
    L_0x03e7:
        r1 = 1;
        goto L_0x03ea;
    L_0x03e9:
        r1 = 0;
    L_0x03ea:
        if (r1 == 0) goto L_0x187d;
    L_0x03ec:
        r1 = "chat_from_id";
        r1 = r11.has(r1);	 Catch:{ all -> 0x1879 }
        if (r1 == 0) goto L_0x03fb;
    L_0x03f4:
        r1 = "chat_from_id";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x03cc }
        goto L_0x03fc;
    L_0x03fb:
        r1 = 0;
    L_0x03fc:
        r6 = "mention";
        r6 = r11.has(r6);	 Catch:{ all -> 0x1879 }
        if (r6 == 0) goto L_0x040e;
    L_0x0404:
        r6 = "mention";
        r6 = r11.getInt(r6);	 Catch:{ all -> 0x03cc }
        if (r6 == 0) goto L_0x040e;
    L_0x040c:
        r6 = 1;
        goto L_0x040f;
    L_0x040e:
        r6 = 0;
    L_0x040f:
        r14 = "silent";
        r14 = r11.has(r14);	 Catch:{ all -> 0x1879 }
        if (r14 == 0) goto L_0x0423;
    L_0x0417:
        r14 = "silent";
        r14 = r11.getInt(r14);	 Catch:{ all -> 0x03cc }
        if (r14 == 0) goto L_0x0423;
    L_0x041f:
        r30 = r15;
        r14 = 1;
        goto L_0x0426;
    L_0x0423:
        r30 = r15;
        r14 = 0;
    L_0x0426:
        r15 = "loc_args";
        r15 = r5.has(r15);	 Catch:{ all -> 0x1875 }
        if (r15 == 0) goto L_0x0458;
    L_0x042e:
        r15 = "loc_args";
        r5 = r5.getJSONArray(r15);	 Catch:{ all -> 0x044d }
        r15 = r5.length();	 Catch:{ all -> 0x044d }
        r15 = new java.lang.String[r15];	 Catch:{ all -> 0x044d }
        r20 = r6;
        r19 = r14;
        r14 = 0;
    L_0x043f:
        r6 = r15.length;	 Catch:{ all -> 0x044d }
        if (r14 >= r6) goto L_0x044b;
    L_0x0442:
        r6 = r5.getString(r14);	 Catch:{ all -> 0x044d }
        r15[r14] = r6;	 Catch:{ all -> 0x044d }
        r14 = r14 + 1;
        goto L_0x043f;
    L_0x044b:
        r5 = 0;
        goto L_0x045e;
    L_0x044d:
        r0 = move-exception;
        r2 = -1;
        r3 = r35;
        r1 = r0;
        r14 = r29;
        r15 = r30;
        goto L_0x1999;
    L_0x0458:
        r20 = r6;
        r19 = r14;
        r5 = 0;
        r15 = 0;
    L_0x045e:
        r6 = r15[r5];	 Catch:{ all -> 0x1875 }
        r5 = "edit_date";
        r27 = r11.has(r5);	 Catch:{ all -> 0x1875 }
        r5 = "CHAT_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x1875 }
        if (r5 == 0) goto L_0x047c;
    L_0x046e:
        if (r2 == 0) goto L_0x0472;
    L_0x0470:
        r5 = 1;
        goto L_0x0473;
    L_0x0472:
        r5 = 0;
    L_0x0473:
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r11 = r6;
        r26 = 0;
        r6 = r5;
        r5 = 0;
        goto L_0x04a4;
    L_0x047c:
        r5 = "PINNED_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x1875 }
        if (r5 == 0) goto L_0x0490;
    L_0x0484:
        if (r1 == 0) goto L_0x0488;
    L_0x0486:
        r5 = 1;
        goto L_0x0489;
    L_0x0488:
        r5 = 0;
    L_0x0489:
        r14 = r6;
        r11 = 0;
        r26 = 0;
        r6 = r5;
        r5 = 1;
        goto L_0x04a4;
    L_0x0490:
        r5 = "CHANNEL_";
        r5 = r9.startsWith(r5);	 Catch:{ all -> 0x1875 }
        r14 = r6;
        if (r5 == 0) goto L_0x049f;
    L_0x0499:
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r26 = 1;
        goto L_0x04a4;
    L_0x049f:
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r26 = 0;
    L_0x04a4:
        r25 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x1875 }
        if (r25 == 0) goto L_0x04cf;
    L_0x04a8:
        r25 = r14;
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044d }
        r14.<init>();	 Catch:{ all -> 0x044d }
        r31 = r11;
        r11 = "GCM received message notification ";
        r14.append(r11);	 Catch:{ all -> 0x044d }
        r14.append(r9);	 Catch:{ all -> 0x044d }
        r14.append(r10);	 Catch:{ all -> 0x044d }
        r14.append(r12);	 Catch:{ all -> 0x044d }
        r10 = " mid = ";
        r14.append(r10);	 Catch:{ all -> 0x044d }
        r14.append(r7);	 Catch:{ all -> 0x044d }
        r10 = r14.toString();	 Catch:{ all -> 0x044d }
        org.telegram.messenger.FileLog.d(r10);	 Catch:{ all -> 0x044d }
        goto L_0x04d3;
    L_0x04cf:
        r31 = r11;
        r25 = r14;
    L_0x04d3:
        r10 = r9.hashCode();	 Catch:{ all -> 0x1875 }
        switch(r10) {
            case -2100047043: goto L_0x0988;
            case -2091498420: goto L_0x097d;
            case -2053872415: goto L_0x0972;
            case -2039746363: goto L_0x0967;
            case -2023218804: goto L_0x095c;
            case -1979538588: goto L_0x0951;
            case -1979536003: goto L_0x0946;
            case -1979535888: goto L_0x093b;
            case -1969004705: goto L_0x0930;
            case -1946699248: goto L_0x0924;
            case -1528047021: goto L_0x0918;
            case -1493579426: goto L_0x090c;
            case -1482481933: goto L_0x0900;
            case -1480102982: goto L_0x08f5;
            case -1478041834: goto L_0x08e9;
            case -1474543101: goto L_0x08de;
            case -1465695932: goto L_0x08d2;
            case -1374906292: goto L_0x08c6;
            case -1372940586: goto L_0x08ba;
            case -1264245338: goto L_0x08ae;
            case -1236086700: goto L_0x08a2;
            case -1236077786: goto L_0x0896;
            case -1235796237: goto L_0x088a;
            case -1235760759: goto L_0x087e;
            case -1235686303: goto L_0x0873;
            case -1198046100: goto L_0x0868;
            case -1124254527: goto L_0x085c;
            case -1085137927: goto L_0x0850;
            case -1084856378: goto L_0x0844;
            case -1084820900: goto L_0x0838;
            case -1084746444: goto L_0x082c;
            case -819729482: goto L_0x0820;
            case -772141857: goto L_0x0814;
            case -638310039: goto L_0x0808;
            case -590403924: goto L_0x07fc;
            case -589196239: goto L_0x07f0;
            case -589193654: goto L_0x07e4;
            case -589193539: goto L_0x07d8;
            case -440169325: goto L_0x07cc;
            case -412748110: goto L_0x07c0;
            case -228518075: goto L_0x07b4;
            case -213586509: goto L_0x07a8;
            case -115582002: goto L_0x079c;
            case -112621464: goto L_0x0790;
            case -108522133: goto L_0x0784;
            case -107572034: goto L_0x0779;
            case -40534265: goto L_0x076d;
            case 65254746: goto L_0x0761;
            case 141040782: goto L_0x0755;
            case 309993049: goto L_0x0749;
            case 309995634: goto L_0x073d;
            case 309995749: goto L_0x0731;
            case 320532812: goto L_0x0725;
            case 328933854: goto L_0x0719;
            case 331340546: goto L_0x070d;
            case 344816990: goto L_0x0701;
            case 346878138: goto L_0x06f5;
            case 350376871: goto L_0x06e9;
            case 615714517: goto L_0x06de;
            case 715508879: goto L_0x06d2;
            case 728985323: goto L_0x06c6;
            case 731046471: goto L_0x06ba;
            case 734545204: goto L_0x06ae;
            case 802032552: goto L_0x06a2;
            case 991498806: goto L_0x0696;
            case 1007364121: goto L_0x068a;
            case 1019917311: goto L_0x067e;
            case 1019926225: goto L_0x0672;
            case 1020207774: goto L_0x0666;
            case 1020243252: goto L_0x065a;
            case 1020317708: goto L_0x064e;
            case 1060349560: goto L_0x0642;
            case 1060358474: goto L_0x0636;
            case 1060640023: goto L_0x062a;
            case 1060675501: goto L_0x061e;
            case 1060749957: goto L_0x0613;
            case 1073049781: goto L_0x0607;
            case 1078101399: goto L_0x05fb;
            case 1110103437: goto L_0x05ef;
            case 1160762272: goto L_0x05e3;
            case 1172918249: goto L_0x05d7;
            case 1234591620: goto L_0x05cb;
            case 1281128640: goto L_0x05bf;
            case 1281131225: goto L_0x05b3;
            case 1281131340: goto L_0x05a7;
            case 1310789062: goto L_0x059c;
            case 1333118583: goto L_0x0590;
            case 1361447897: goto L_0x0584;
            case 1498266155: goto L_0x0578;
            case 1533804208: goto L_0x056c;
            case 1547988151: goto L_0x0560;
            case 1561464595: goto L_0x0554;
            case 1563525743: goto L_0x0548;
            case 1567024476: goto L_0x053c;
            case 1810705077: goto L_0x0530;
            case 1815177512: goto L_0x0524;
            case 1963241394: goto L_0x0518;
            case 2014789757: goto L_0x050c;
            case 2022049433: goto L_0x0500;
            case 2048733346: goto L_0x04f4;
            case 2099392181: goto L_0x04e8;
            case 2140162142: goto L_0x04dc;
            default: goto L_0x04da;
        };
    L_0x04da:
        goto L_0x0993;
    L_0x04dc:
        r10 = "CHAT_MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x04e4:
        r10 = 56;
        goto L_0x0994;
    L_0x04e8:
        r10 = "CHANNEL_MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x04f0:
        r10 = 41;
        goto L_0x0994;
    L_0x04f4:
        r10 = "CHANNEL_MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x04fc:
        r10 = 26;
        goto L_0x0994;
    L_0x0500:
        r10 = "PINNED_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0508:
        r10 = 83;
        goto L_0x0994;
    L_0x050c:
        r10 = "CHAT_PHOTO_EDITED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0514:
        r10 = 64;
        goto L_0x0994;
    L_0x0518:
        r10 = "LOCKED_MESSAGE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0520:
        r10 = 96;
        goto L_0x0994;
    L_0x0524:
        r10 = "CHANNEL_MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x052c:
        r10 = 43;
        goto L_0x0994;
    L_0x0530:
        r10 = "MESSAGE_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0538:
        r10 = 21;
        goto L_0x0994;
    L_0x053c:
        r10 = "CHAT_MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0544:
        r10 = 47;
        goto L_0x0994;
    L_0x0548:
        r10 = "CHAT_MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0550:
        r10 = 48;
        goto L_0x0994;
    L_0x0554:
        r10 = "CHAT_MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x055c:
        r10 = 46;
        goto L_0x0994;
    L_0x0560:
        r10 = "CHAT_MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0568:
        r10 = 51;
        goto L_0x0994;
    L_0x056c:
        r10 = "MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0574:
        r10 = 24;
        goto L_0x0994;
    L_0x0578:
        r10 = "PHONE_CALL_MISSED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0580:
        r10 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        goto L_0x0994;
    L_0x0584:
        r10 = "MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x058c:
        r10 = 23;
        goto L_0x0994;
    L_0x0590:
        r10 = "CHAT_MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0598:
        r10 = 73;
        goto L_0x0994;
    L_0x059c:
        r10 = "MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05a4:
        r10 = 2;
        goto L_0x0994;
    L_0x05a7:
        r10 = "MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05af:
        r10 = 17;
        goto L_0x0994;
    L_0x05b3:
        r10 = "MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05bb:
        r10 = 15;
        goto L_0x0994;
    L_0x05bf:
        r10 = "MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05c7:
        r10 = 9;
        goto L_0x0994;
    L_0x05cb:
        r10 = "CHAT_MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05d3:
        r10 = 59;
        goto L_0x0994;
    L_0x05d7:
        r10 = "CHANNEL_MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05df:
        r10 = 37;
        goto L_0x0994;
    L_0x05e3:
        r10 = "CHAT_MESSAGE_PHOTOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05eb:
        r10 = 72;
        goto L_0x0994;
    L_0x05ef:
        r10 = "CHAT_MESSAGE_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x05f7:
        r10 = 45;
        goto L_0x0994;
    L_0x05fb:
        r10 = "CHAT_TITLE_EDITED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0603:
        r10 = 63;
        goto L_0x0994;
    L_0x0607:
        r10 = "PINNED_NOTEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x060f:
        r10 = 76;
        goto L_0x0994;
    L_0x0613:
        r10 = "MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x061b:
        r10 = 0;
        goto L_0x0994;
    L_0x061e:
        r10 = "MESSAGE_QUIZ";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0626:
        r10 = 13;
        goto L_0x0994;
    L_0x062a:
        r10 = "MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0632:
        r10 = 14;
        goto L_0x0994;
    L_0x0636:
        r10 = "MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x063e:
        r10 = 18;
        goto L_0x0994;
    L_0x0642:
        r10 = "MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x064a:
        r10 = 22;
        goto L_0x0994;
    L_0x064e:
        r10 = "CHAT_MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0656:
        r10 = 44;
        goto L_0x0994;
    L_0x065a:
        r10 = "CHAT_MESSAGE_QUIZ";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0662:
        r10 = 53;
        goto L_0x0994;
    L_0x0666:
        r10 = "CHAT_MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x066e:
        r10 = 54;
        goto L_0x0994;
    L_0x0672:
        r10 = "CHAT_MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x067a:
        r10 = 58;
        goto L_0x0994;
    L_0x067e:
        r10 = "CHAT_MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0686:
        r10 = 71;
        goto L_0x0994;
    L_0x068a:
        r10 = "CHANNEL_MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0692:
        r10 = 20;
        goto L_0x0994;
    L_0x0696:
        r10 = "PINNED_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x069e:
        r10 = 87;
        goto L_0x0994;
    L_0x06a2:
        r10 = "MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06aa:
        r10 = 12;
        goto L_0x0994;
    L_0x06ae:
        r10 = "PINNED_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06b6:
        r10 = 78;
        goto L_0x0994;
    L_0x06ba:
        r10 = "PINNED_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06c2:
        r10 = 79;
        goto L_0x0994;
    L_0x06c6:
        r10 = "PINNED_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06ce:
        r10 = 77;
        goto L_0x0994;
    L_0x06d2:
        r10 = "PINNED_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06da:
        r10 = 82;
        goto L_0x0994;
    L_0x06de:
        r10 = "MESSAGE_PHOTO_SECRET";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06e6:
        r10 = 4;
        goto L_0x0994;
    L_0x06e9:
        r10 = "CHANNEL_MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06f1:
        r10 = 28;
        goto L_0x0994;
    L_0x06f5:
        r10 = "CHANNEL_MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x06fd:
        r10 = 29;
        goto L_0x0994;
    L_0x0701:
        r10 = "CHANNEL_MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0709:
        r10 = 27;
        goto L_0x0994;
    L_0x070d:
        r10 = "CHANNEL_MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0715:
        r10 = 32;
        goto L_0x0994;
    L_0x0719:
        r10 = "CHAT_MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0721:
        r10 = 50;
        goto L_0x0994;
    L_0x0725:
        r10 = "MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x072d:
        r10 = 25;
        goto L_0x0994;
    L_0x0731:
        r10 = "CHAT_MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0739:
        r10 = 57;
        goto L_0x0994;
    L_0x073d:
        r10 = "CHAT_MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0745:
        r10 = 55;
        goto L_0x0994;
    L_0x0749:
        r10 = "CHAT_MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0751:
        r10 = 49;
        goto L_0x0994;
    L_0x0755:
        r10 = "CHAT_LEFT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x075d:
        r10 = 68;
        goto L_0x0994;
    L_0x0761:
        r10 = "CHAT_ADD_YOU";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0769:
        r10 = 62;
        goto L_0x0994;
    L_0x076d:
        r10 = "CHAT_DELETE_MEMBER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0775:
        r10 = 66;
        goto L_0x0994;
    L_0x0779:
        r10 = "MESSAGE_SCREENSHOT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0781:
        r10 = 7;
        goto L_0x0994;
    L_0x0784:
        r10 = "AUTH_REGION";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x078c:
        r10 = 95;
        goto L_0x0994;
    L_0x0790:
        r10 = "CONTACT_JOINED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0798:
        r10 = 93;
        goto L_0x0994;
    L_0x079c:
        r10 = "CHAT_MESSAGE_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07a4:
        r10 = 60;
        goto L_0x0994;
    L_0x07a8:
        r10 = "ENCRYPTION_REQUEST";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07b0:
        r10 = 97;
        goto L_0x0994;
    L_0x07b4:
        r10 = "MESSAGE_GEOLIVE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07bc:
        r10 = 16;
        goto L_0x0994;
    L_0x07c0:
        r10 = "CHAT_DELETE_YOU";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07c8:
        r10 = 67;
        goto L_0x0994;
    L_0x07cc:
        r10 = "AUTH_UNKNOWN";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07d4:
        r10 = 94;
        goto L_0x0994;
    L_0x07d8:
        r10 = "PINNED_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07e0:
        r10 = 91;
        goto L_0x0994;
    L_0x07e4:
        r10 = "PINNED_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07ec:
        r10 = 86;
        goto L_0x0994;
    L_0x07f0:
        r10 = "PINNED_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x07f8:
        r10 = 80;
        goto L_0x0994;
    L_0x07fc:
        r10 = "PINNED_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0804:
        r10 = 89;
        goto L_0x0994;
    L_0x0808:
        r10 = "CHANNEL_MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0810:
        r10 = 31;
        goto L_0x0994;
    L_0x0814:
        r10 = "PHONE_CALL_REQUEST";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x081c:
        r10 = 99;
        goto L_0x0994;
    L_0x0820:
        r10 = "PINNED_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0828:
        r10 = 81;
        goto L_0x0994;
    L_0x082c:
        r10 = "PINNED_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0834:
        r10 = 75;
        goto L_0x0994;
    L_0x0838:
        r10 = "PINNED_QUIZ";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0840:
        r10 = 84;
        goto L_0x0994;
    L_0x0844:
        r10 = "PINNED_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x084c:
        r10 = 85;
        goto L_0x0994;
    L_0x0850:
        r10 = "PINNED_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0858:
        r10 = 88;
        goto L_0x0994;
    L_0x085c:
        r10 = "CHAT_MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0864:
        r10 = 52;
        goto L_0x0994;
    L_0x0868:
        r10 = "MESSAGE_VIDEO_SECRET";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0870:
        r10 = 6;
        goto L_0x0994;
    L_0x0873:
        r10 = "CHANNEL_MESSAGE_TEXT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x087b:
        r10 = 1;
        goto L_0x0994;
    L_0x087e:
        r10 = "CHANNEL_MESSAGE_QUIZ";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0886:
        r10 = 34;
        goto L_0x0994;
    L_0x088a:
        r10 = "CHANNEL_MESSAGE_POLL";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0892:
        r10 = 35;
        goto L_0x0994;
    L_0x0896:
        r10 = "CHANNEL_MESSAGE_GAME";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x089e:
        r10 = 39;
        goto L_0x0994;
    L_0x08a2:
        r10 = "CHANNEL_MESSAGE_FWDS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08aa:
        r10 = 40;
        goto L_0x0994;
    L_0x08ae:
        r10 = "PINNED_INVOICE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08b6:
        r10 = 90;
        goto L_0x0994;
    L_0x08ba:
        r10 = "CHAT_RETURNED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08c2:
        r10 = 69;
        goto L_0x0994;
    L_0x08c6:
        r10 = "ENCRYPTED_MESSAGE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08ce:
        r10 = 92;
        goto L_0x0994;
    L_0x08d2:
        r10 = "ENCRYPTION_ACCEPT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08da:
        r10 = 98;
        goto L_0x0994;
    L_0x08de:
        r10 = "MESSAGE_VIDEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08e6:
        r10 = 5;
        goto L_0x0994;
    L_0x08e9:
        r10 = "MESSAGE_ROUND";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08f1:
        r10 = 8;
        goto L_0x0994;
    L_0x08f5:
        r10 = "MESSAGE_PHOTO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x08fd:
        r10 = 3;
        goto L_0x0994;
    L_0x0900:
        r10 = "MESSAGE_MUTED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0908:
        r10 = 100;
        goto L_0x0994;
    L_0x090c:
        r10 = "MESSAGE_AUDIO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0914:
        r10 = 11;
        goto L_0x0994;
    L_0x0918:
        r10 = "CHAT_MESSAGES";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0920:
        r10 = 74;
        goto L_0x0994;
    L_0x0924:
        r10 = "CHAT_JOINED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x092c:
        r10 = 70;
        goto L_0x0994;
    L_0x0930:
        r10 = "CHAT_ADD_MEMBER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0938:
        r10 = 65;
        goto L_0x0994;
    L_0x093b:
        r10 = "CHANNEL_MESSAGE_GIF";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0943:
        r10 = 38;
        goto L_0x0994;
    L_0x0946:
        r10 = "CHANNEL_MESSAGE_GEO";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x094e:
        r10 = 36;
        goto L_0x0994;
    L_0x0951:
        r10 = "CHANNEL_MESSAGE_DOC";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0959:
        r10 = 30;
        goto L_0x0994;
    L_0x095c:
        r10 = "CHANNEL_MESSAGE_VIDEOS";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0964:
        r10 = 42;
        goto L_0x0994;
    L_0x0967:
        r10 = "MESSAGE_STICKER";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x096f:
        r10 = 10;
        goto L_0x0994;
    L_0x0972:
        r10 = "CHAT_CREATED";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x097a:
        r10 = 61;
        goto L_0x0994;
    L_0x097d:
        r10 = "CHANNEL_MESSAGE_CONTACT";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0985:
        r10 = 33;
        goto L_0x0994;
    L_0x0988:
        r10 = "MESSAGE_GAME_SCORE";
        r10 = r9.equals(r10);	 Catch:{ all -> 0x044d }
        if (r10 == 0) goto L_0x0993;
    L_0x0990:
        r10 = 19;
        goto L_0x0994;
    L_0x0993:
        r10 = -1;
    L_0x0994:
        r14 = "ChannelMessageFew";
        r11 = " ";
        r32 = r7;
        r7 = "AttachSticker";
        switch(r10) {
            case 0: goto L_0x179c;
            case 1: goto L_0x179c;
            case 2: goto L_0x1779;
            case 3: goto L_0x175a;
            case 4: goto L_0x173b;
            case 5: goto L_0x171c;
            case 6: goto L_0x16fc;
            case 7: goto L_0x16e2;
            case 8: goto L_0x16c2;
            case 9: goto L_0x16a2;
            case 10: goto L_0x1643;
            case 11: goto L_0x1623;
            case 12: goto L_0x15fe;
            case 13: goto L_0x15d9;
            case 14: goto L_0x15b4;
            case 15: goto L_0x1594;
            case 16: goto L_0x1574;
            case 17: goto L_0x1554;
            case 18: goto L_0x152f;
            case 19: goto L_0x150e;
            case 20: goto L_0x150e;
            case 21: goto L_0x14e9;
            case 22: goto L_0x14c2;
            case 23: goto L_0x1499;
            case 24: goto L_0x1470;
            case 25: goto L_0x1458;
            case 26: goto L_0x1438;
            case 27: goto L_0x1418;
            case 28: goto L_0x13f8;
            case 29: goto L_0x13d8;
            case 30: goto L_0x13b8;
            case 31: goto L_0x1359;
            case 32: goto L_0x1339;
            case 33: goto L_0x1314;
            case 34: goto L_0x12ef;
            case 35: goto L_0x12ca;
            case 36: goto L_0x12aa;
            case 37: goto L_0x128a;
            case 38: goto L_0x126a;
            case 39: goto L_0x124a;
            case 40: goto L_0x121e;
            case 41: goto L_0x11f6;
            case 42: goto L_0x11ce;
            case 43: goto L_0x11b7;
            case 44: goto L_0x1194;
            case 45: goto L_0x116f;
            case 46: goto L_0x114a;
            case 47: goto L_0x1125;
            case 48: goto L_0x1100;
            case 49: goto L_0x10db;
            case 50: goto L_0x1059;
            case 51: goto L_0x1032;
            case 52: goto L_0x100b;
            case 53: goto L_0x0fe4;
            case 54: goto L_0x0fbd;
            case 55: goto L_0x0f9a;
            case 56: goto L_0x0var_;
            case 57: goto L_0x0var_;
            case 58: goto L_0x0f2c;
            case 59: goto L_0x0var_;
            case 60: goto L_0x0edd;
            case 61: goto L_0x0ec3;
            case 62: goto L_0x0ec3;
            case 63: goto L_0x0ea9;
            case 64: goto L_0x0e8f;
            case 65: goto L_0x0e70;
            case 66: goto L_0x0e56;
            case 67: goto L_0x0e3c;
            case 68: goto L_0x0e22;
            case 69: goto L_0x0e08;
            case 70: goto L_0x0dee;
            case 71: goto L_0x0dc0;
            case 72: goto L_0x0d93;
            case 73: goto L_0x0d66;
            case 74: goto L_0x0d4a;
            case 75: goto L_0x0d13;
            case 76: goto L_0x0ce3;
            case 77: goto L_0x0cb6;
            case 78: goto L_0x0CLASSNAME;
            case 79: goto L_0x0c5a;
            case 80: goto L_0x0c2b;
            case 81: goto L_0x0bae;
            case 82: goto L_0x0b7f;
            case 83: goto L_0x0b45;
            case 84: goto L_0x0b0b;
            case 85: goto L_0x0ad5;
            case 86: goto L_0x0aa5;
            case 87: goto L_0x0a7a;
            case 88: goto L_0x0a4f;
            case 89: goto L_0x0a22;
            case 90: goto L_0x09f5;
            case 91: goto L_0x09c8;
            case 92: goto L_0x09ad;
            case 93: goto L_0x09a7;
            case 94: goto L_0x09a7;
            case 95: goto L_0x09a7;
            case 96: goto L_0x09a7;
            case 97: goto L_0x09a7;
            case 98: goto L_0x09a7;
            case 99: goto L_0x09a7;
            case 100: goto L_0x09a7;
            case 101: goto L_0x09a7;
            default: goto L_0x099f;
        };
    L_0x099f:
        r21 = r1;
        r10 = r32;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x1875 }
        goto L_0x17b9;
    L_0x09a7:
        r21 = r1;
        r10 = r32;
        goto L_0x17cf;
    L_0x09ad:
        r7 = "YouHaveNewMessage";
        r8 = NUM; // 0x7f0e0cda float:1.888171E38 double:1.053163782E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        r8 = "SecretChatName";
        r10 = NUM; // 0x7f0e09f7 float:1.8880212E38 double:1.053163417E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r10);	 Catch:{ all -> 0x044d }
        r21 = r1;
        r25 = r8;
        r10 = r32;
    L_0x09c5:
        r1 = 1;
        goto L_0x17d1;
    L_0x09c8:
        if (r1 == 0) goto L_0x09e2;
    L_0x09ca:
        r7 = "NotificationActionPinnedGif";
        r8 = NUM; // 0x7f0e06eb float:1.887863E38 double:1.0531630316E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x09e2:
        r7 = "NotificationActionPinnedGifChannel";
        r8 = NUM; // 0x7f0e06ec float:1.8878632E38 double:1.053163032E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044d }
        r11[r10] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x09f5:
        if (r1 == 0) goto L_0x0a0f;
    L_0x09f7:
        r7 = "NotificationActionPinnedInvoice";
        r8 = NUM; // 0x7f0e06ed float:1.8878634E38 double:1.0531630326E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a0f:
        r7 = "NotificationActionPinnedInvoiceChannel";
        r8 = NUM; // 0x7f0e06ee float:1.8878636E38 double:1.053163033E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044d }
        r11[r10] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a22:
        if (r1 == 0) goto L_0x0a3c;
    L_0x0a24:
        r7 = "NotificationActionPinnedGameScore";
        r8 = NUM; // 0x7f0e06e5 float:1.8878617E38 double:1.0531630287E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a3c:
        r7 = "NotificationActionPinnedGameScoreChannel";
        r8 = NUM; // 0x7f0e06e6 float:1.887862E38 double:1.053163029E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044d }
        r11[r10] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a4f:
        if (r1 == 0) goto L_0x0a68;
    L_0x0a51:
        r7 = "NotificationActionPinnedGame";
        r8 = NUM; // 0x7f0e06e3 float:1.8878613E38 double:1.0531630277E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a68:
        r7 = "NotificationActionPinnedGameChannel";
        r8 = NUM; // 0x7f0e06e4 float:1.8878615E38 double:1.053163028E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044d }
        r11[r10] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a7a:
        if (r1 == 0) goto L_0x0a93;
    L_0x0a7c:
        r7 = "NotificationActionPinnedGeoLive";
        r8 = NUM; // 0x7f0e06e9 float:1.8878625E38 double:1.0531630306E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0a93:
        r7 = "NotificationActionPinnedGeoLiveChannel";
        r8 = NUM; // 0x7f0e06ea float:1.8878628E38 double:1.053163031E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044d }
        r11[r10] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0aa5:
        if (r1 == 0) goto L_0x0abe;
    L_0x0aa7:
        r7 = "NotificationActionPinnedGeo";
        r8 = NUM; // 0x7f0e06e7 float:1.8878621E38 double:1.0531630296E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0abe:
        r7 = "NotificationActionPinnedGeoChannel";
        r8 = NUM; // 0x7f0e06e8 float:1.8878623E38 double:1.05316303E-314;
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r10 = 0;
        r14 = r15[r10];	 Catch:{ all -> 0x044d }
        r11[r10] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
    L_0x0acf:
        r21 = r1;
        r10 = r32;
        goto L_0x16f9;
    L_0x0ad5:
        if (r1 == 0) goto L_0x0af4;
    L_0x0ad7:
        r7 = "NotificationActionPinnedPoll2";
        r8 = NUM; // 0x7f0e06f5 float:1.887865E38 double:1.0531630366E-314;
        r10 = 3;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r17 = 1;
        r10[r17] = r14;	 Catch:{ all -> 0x044d }
        r14 = r15[r17];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0af4:
        r7 = "NotificationActionPinnedPollChannel2";
        r8 = NUM; // 0x7f0e06f6 float:1.8878652E38 double:1.053163037E-314;
        r10 = 2;
        r10 = new java.lang.Object[r10];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r10[r11] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);	 Catch:{ all -> 0x044d }
        goto L_0x0acf;
    L_0x0b0b:
        r10 = r32;
        if (r1 == 0) goto L_0x0b2d;
    L_0x0b0f:
        r8 = "NotificationActionPinnedQuiz2";
        r11 = NUM; // 0x7f0e06f7 float:1.8878654E38 double:1.0531630375E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r18 = 1;
        r7[r18] = r17;	 Catch:{ all -> 0x044d }
        r15 = r15[r18];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0b2d:
        r7 = "NotificationActionPinnedQuizChannel2";
        r8 = NUM; // 0x7f0e06f8 float:1.8878656E38 double:1.053163038E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0b45:
        r10 = r32;
        if (r1 == 0) goto L_0x0b67;
    L_0x0b49:
        r8 = "NotificationActionPinnedContact2";
        r11 = NUM; // 0x7f0e06df float:1.8878605E38 double:1.0531630257E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r18 = 1;
        r7[r18] = r17;	 Catch:{ all -> 0x044d }
        r15 = r15[r18];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0b67:
        r7 = "NotificationActionPinnedContactChannel2";
        r8 = NUM; // 0x7f0e06e0 float:1.8878607E38 double:1.053163026E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0b7f:
        r10 = r32;
        if (r1 == 0) goto L_0x0b9b;
    L_0x0b83:
        r7 = "NotificationActionPinnedVoice";
        r8 = NUM; // 0x7f0e0703 float:1.8878678E38 double:1.0531630435E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0b9b:
        r7 = "NotificationActionPinnedVoiceChannel";
        r8 = NUM; // 0x7f0e0704 float:1.887868E38 double:1.053163044E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0bae:
        r10 = r32;
        if (r1 == 0) goto L_0x0bf4;
    L_0x0bb2:
        r8 = r15.length;	 Catch:{ all -> 0x044d }
        r11 = 2;
        if (r8 <= r11) goto L_0x0bdc;
    L_0x0bb6:
        r8 = r15[r11];	 Catch:{ all -> 0x044d }
        r8 = android.text.TextUtils.isEmpty(r8);	 Catch:{ all -> 0x044d }
        if (r8 != 0) goto L_0x0bdc;
    L_0x0bbe:
        r8 = "NotificationActionPinnedStickerEmoji";
        r11 = NUM; // 0x7f0e06fd float:1.8878666E38 double:1.0531630405E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r18 = 1;
        r7[r18] = r17;	 Catch:{ all -> 0x044d }
        r15 = r15[r18];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0bdc:
        r7 = "NotificationActionPinnedSticker";
        r8 = NUM; // 0x7f0e06fb float:1.8878662E38 double:1.0531630395E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0bf4:
        r7 = r15.length;	 Catch:{ all -> 0x044d }
        r8 = 1;
        if (r7 <= r8) goto L_0x0CLASSNAME;
    L_0x0bf8:
        r7 = r15[r8];	 Catch:{ all -> 0x044d }
        r7 = android.text.TextUtils.isEmpty(r7);	 Catch:{ all -> 0x044d }
        if (r7 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedStickerEmojiChannel";
        r8 = NUM; // 0x7f0e06fe float:1.8878668E38 double:1.053163041E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedStickerChannel";
        r8 = NUM; // 0x7f0e06fc float:1.8878664E38 double:1.05316304E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0c2b:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c2f:
        r7 = "NotificationActionPinnedFile";
        r8 = NUM; // 0x7f0e06e1 float:1.887861E38 double:1.0531630267E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedFileChannel";
        r8 = NUM; // 0x7f0e06e2 float:1.8878611E38 double:1.053163027E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0c5a:
        r10 = r32;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c5e:
        r7 = "NotificationActionPinnedRound";
        r8 = NUM; // 0x7f0e06f9 float:1.8878658E38 double:1.0531630385E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0CLASSNAME:
        r7 = "NotificationActionPinnedRoundChannel";
        r8 = NUM; // 0x7f0e06fa float:1.887866E38 double:1.053163039E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0CLASSNAME:
        r10 = r32;
        if (r1 == 0) goto L_0x0ca4;
    L_0x0c8d:
        r7 = "NotificationActionPinnedVideo";
        r8 = NUM; // 0x7f0e0701 float:1.8878674E38 double:1.0531630425E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0ca4:
        r7 = "NotificationActionPinnedVideoChannel";
        r8 = NUM; // 0x7f0e0702 float:1.8878676E38 double:1.053163043E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0cb6:
        r10 = r32;
        if (r1 == 0) goto L_0x0cd1;
    L_0x0cba:
        r7 = "NotificationActionPinnedPhoto";
        r8 = NUM; // 0x7f0e06f3 float:1.8878646E38 double:1.0531630356E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0cd1:
        r7 = "NotificationActionPinnedPhotoChannel";
        r8 = NUM; // 0x7f0e06f4 float:1.8878648E38 double:1.053163036E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0ce3:
        r10 = r32;
        if (r1 == 0) goto L_0x0cfe;
    L_0x0ce7:
        r7 = "NotificationActionPinnedNoText";
        r8 = NUM; // 0x7f0e06f1 float:1.8878642E38 double:1.0531630346E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0cfe:
        r7 = "NotificationActionPinnedNoTextChannel";
        r8 = NUM; // 0x7f0e06f2 float:1.8878644E38 double:1.053163035E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r14);	 Catch:{ all -> 0x044d }
    L_0x0d0f:
        r21 = r1;
        goto L_0x16f9;
    L_0x0d13:
        r10 = r32;
        if (r1 == 0) goto L_0x0d33;
    L_0x0d17:
        r8 = "NotificationActionPinnedText";
        r11 = NUM; // 0x7f0e06ff float:1.887867E38 double:1.0531630415E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0d33:
        r7 = "NotificationActionPinnedTextChannel";
        r8 = NUM; // 0x7f0e0700 float:1.8878672E38 double:1.053163042E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0d4a:
        r10 = r32;
        r7 = "NotificationGroupAlbum";
        r8 = NUM; // 0x7f0e070c float:1.8878696E38 double:1.053163048E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
    L_0x0d62:
        r21 = r1;
        goto L_0x09c5;
    L_0x0d66:
        r10 = r32;
        r8 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0e070d float:1.8878699E38 double:1.0531630484E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = "Videos";
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x044d }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044d }
        r15 = r15.intValue();	 Catch:{ all -> 0x044d }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x044d }
        r7[r17] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d62;
    L_0x0d93:
        r10 = r32;
        r8 = "NotificationGroupFew";
        r11 = NUM; // 0x7f0e070d float:1.8878699E38 double:1.0531630484E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = "Photos";
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x044d }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044d }
        r15 = r15.intValue();	 Catch:{ all -> 0x044d }
        r14 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15);	 Catch:{ all -> 0x044d }
        r7[r17] = r14;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d62;
    L_0x0dc0:
        r10 = r32;
        r11 = "NotificationGroupForwardedFew";
        r14 = NUM; // 0x7f0e070e float:1.88787E38 double:1.053163049E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x044d }
        r7[r18] = r21;	 Catch:{ all -> 0x044d }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x044d }
        r7[r18] = r21;	 Catch:{ all -> 0x044d }
        r17 = 2;
        r15 = r15[r17];	 Catch:{ all -> 0x044d }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044d }
        r15 = r15.intValue();	 Catch:{ all -> 0x044d }
        r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15);	 Catch:{ all -> 0x044d }
        r7[r17] = r8;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r11, r14, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d62;
    L_0x0dee:
        r10 = r32;
        r7 = "NotificationGroupAddSelfMega";
        r8 = NUM; // 0x7f0e070b float:1.8878694E38 double:1.0531630474E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0e08:
        r10 = r32;
        r7 = "NotificationGroupAddSelf";
        r8 = NUM; // 0x7f0e070a float:1.8878692E38 double:1.053163047E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0e22:
        r10 = r32;
        r7 = "NotificationGroupLeftMember";
        r8 = NUM; // 0x7f0e0711 float:1.8878707E38 double:1.0531630504E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0e3c:
        r10 = r32;
        r7 = "NotificationGroupKickYou";
        r8 = NUM; // 0x7f0e0710 float:1.8878705E38 double:1.05316305E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0e56:
        r10 = r32;
        r7 = "NotificationGroupKickMember";
        r8 = NUM; // 0x7f0e070f float:1.8878703E38 double:1.0531630494E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0e70:
        r10 = r32;
        r8 = "NotificationGroupAddMember";
        r11 = NUM; // 0x7f0e0709 float:1.887869E38 double:1.0531630464E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r18 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r18;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0e8f:
        r10 = r32;
        r7 = "NotificationEditedGroupPhoto";
        r8 = NUM; // 0x7f0e0708 float:1.8878688E38 double:1.053163046E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0ea9:
        r10 = r32;
        r7 = "NotificationEditedGroupName";
        r8 = NUM; // 0x7f0e0707 float:1.8878686E38 double:1.0531630455E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0ec3:
        r10 = r32;
        r7 = "NotificationInvitedToGroup";
        r8 = NUM; // 0x7f0e0716 float:1.8878717E38 double:1.053163053E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0edd:
        r10 = r32;
        r8 = "NotificationMessageGroupInvoice";
        r11 = NUM; // 0x7f0e0727 float:1.8878751E38 double:1.0531630613E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        r8 = "PaymentInvoice";
        r11 = NUM; // 0x7f0e088f float:1.8879481E38 double:1.053163239E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x0var_:
        r10 = r32;
        r8 = "NotificationMessageGroupGameScored";
        r11 = NUM; // 0x7f0e0725 float:1.8878747E38 double:1.0531630603E-314;
        r14 = 4;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044d }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x044d }
        r14[r18] = r21;	 Catch:{ all -> 0x044d }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x044d }
        r14[r18] = r21;	 Catch:{ all -> 0x044d }
        r17 = 2;
        r18 = r15[r17];	 Catch:{ all -> 0x044d }
        r14[r17] = r18;	 Catch:{ all -> 0x044d }
        r7 = 3;
        r15 = r15[r7];	 Catch:{ all -> 0x044d }
        r14[r7] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14);	 Catch:{ all -> 0x044d }
        goto L_0x0d0f;
    L_0x0f2c:
        r10 = r32;
        r8 = "NotificationMessageGroupGame";
        r11 = NUM; // 0x7f0e0724 float:1.8878745E38 double:1.05316306E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        r8 = "AttachGame";
        r11 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x0var_:
        r10 = r32;
        r7 = "NotificationMessageGroupGif";
        r8 = NUM; // 0x7f0e0726 float:1.887875E38 double:1.053163061E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        r8 = "AttachGif";
        r11 = NUM; // 0x7f0e0157 float:1.8875733E38 double:1.053162326E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x0var_:
        r10 = r32;
        r7 = "NotificationMessageGroupLiveLocation";
        r8 = NUM; // 0x7f0e0728 float:1.8878753E38 double:1.053163062E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        r8 = "AttachLiveLocation";
        r11 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x0f9a:
        r10 = r32;
        r7 = "NotificationMessageGroupMap";
        r8 = NUM; // 0x7f0e0729 float:1.8878755E38 double:1.0531630623E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        r8 = "AttachLocation";
        r11 = NUM; // 0x7f0e015e float:1.8875747E38 double:1.0531623296E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x0fbd:
        r10 = r32;
        r8 = "NotificationMessageGroupPoll2";
        r11 = NUM; // 0x7f0e072d float:1.8878763E38 double:1.053163064E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        r8 = "Poll";
        r11 = NUM; // 0x7f0e08fe float:1.8879707E38 double:1.053163294E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x0fe4:
        r10 = r32;
        r8 = "NotificationMessageGroupQuiz2";
        r11 = NUM; // 0x7f0e072e float:1.8878765E38 double:1.0531630647E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        r8 = "PollQuiz";
        r11 = NUM; // 0x7f0e0905 float:1.887972E38 double:1.0531632974E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x100b:
        r10 = r32;
        r8 = "NotificationMessageGroupContact2";
        r11 = NUM; // 0x7f0e0722 float:1.8878741E38 double:1.053163059E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 2;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r7[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r8, r11, r7);	 Catch:{ all -> 0x044d }
        r8 = "AttachContact";
        r11 = NUM; // 0x7f0e0152 float:1.8875723E38 double:1.0531623236E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
        goto L_0x1053;
    L_0x1032:
        r10 = r32;
        r7 = "NotificationMessageGroupAudio";
        r8 = NUM; // 0x7f0e0721 float:1.887874E38 double:1.0531630583E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r16 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r16;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r15;	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11);	 Catch:{ all -> 0x044d }
        r8 = "AttachAudio";
        r11 = NUM; // 0x7f0e0150 float:1.887572E38 double:1.0531623226E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r11);	 Catch:{ all -> 0x044d }
    L_0x1053:
        r21 = r1;
        r16 = r8;
        goto L_0x179a;
    L_0x1059:
        r10 = r32;
        r14 = r15.length;	 Catch:{ all -> 0x044d }
        r8 = 2;
        if (r14 <= r8) goto L_0x10a4;
    L_0x105f:
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r8 = android.text.TextUtils.isEmpty(r14);	 Catch:{ all -> 0x044d }
        if (r8 != 0) goto L_0x10a4;
    L_0x1067:
        r8 = "NotificationMessageGroupStickerEmoji";
        r14 = 3;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044d }
        r18 = 0;
        r21 = r15[r18];	 Catch:{ all -> 0x044d }
        r14[r18] = r21;	 Catch:{ all -> 0x044d }
        r18 = 1;
        r21 = r15[r18];	 Catch:{ all -> 0x044d }
        r14[r18] = r21;	 Catch:{ all -> 0x044d }
        r17 = 2;
        r18 = r15[r17];	 Catch:{ all -> 0x044d }
        r14[r17] = r18;	 Catch:{ all -> 0x044d }
        r21 = r1;
        r1 = NUM; // 0x7f0e0731 float:1.8878772E38 double:1.053163066E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r8, r1, r14);	 Catch:{ all -> 0x044d }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044d }
        r8.<init>();	 Catch:{ all -> 0x044d }
        r14 = r15[r17];	 Catch:{ all -> 0x044d }
        r8.append(r14);	 Catch:{ all -> 0x044d }
        r8.append(r11);	 Catch:{ all -> 0x044d }
        r11 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044d }
        r8.append(r7);	 Catch:{ all -> 0x044d }
        r7 = r8.toString();	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x10a4:
        r21 = r1;
        r1 = "NotificationMessageGroupSticker";
        r8 = NUM; // 0x7f0e0730 float:1.887877E38 double:1.0531630657E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044d }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x044d }
        r14[r16] = r17;	 Catch:{ all -> 0x044d }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x044d }
        r14[r16] = r17;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044d }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044d }
        r8.<init>();	 Catch:{ all -> 0x044d }
        r14 = r15[r16];	 Catch:{ all -> 0x044d }
        r8.append(r14);	 Catch:{ all -> 0x044d }
        r8.append(r11);	 Catch:{ all -> 0x044d }
        r11 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044d }
        r8.append(r7);	 Catch:{ all -> 0x044d }
        r7 = r8.toString();	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x10db:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupDocument";
        r7 = NUM; // 0x7f0e0723 float:1.8878743E38 double:1.0531630593E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e0155 float:1.887573E38 double:1.053162325E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1100:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupRound";
        r7 = NUM; // 0x7f0e072f float:1.8878767E38 double:1.053163065E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e0164 float:1.887576E38 double:1.0531623325E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1125:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupVideo";
        r7 = NUM; // 0x7f0e0733 float:1.8878776E38 double:1.053163067E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0168 float:1.8875768E38 double:1.0531623345E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x114a:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupPhoto";
        r7 = NUM; // 0x7f0e072c float:1.8878761E38 double:1.0531630637E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e0162 float:1.8875756E38 double:1.0531623315E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x116f:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupNoText";
        r7 = NUM; // 0x7f0e072b float:1.887876E38 double:1.053163063E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0647 float:1.8878297E38 double:1.0531629506E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1194:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGroupText";
        r8 = NUM; // 0x7f0e0732 float:1.8878774E38 double:1.0531630667E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r7[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r7[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r7[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7);	 Catch:{ all -> 0x044d }
        r7 = r15[r11];	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x11b7:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageAlbum";
        r7 = NUM; // 0x7f0e0269 float:1.8876289E38 double:1.0531624615E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x11ce:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x044d }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x044d }
        r1[r7] = r8;	 Catch:{ all -> 0x044d }
        r7 = "Videos";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x044d }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x044d }
        r11 = r11.intValue();	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x044d }
        r1[r8] = r7;	 Catch:{ all -> 0x044d }
        r7 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x11f6:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x044d }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x044d }
        r1[r7] = r8;	 Catch:{ all -> 0x044d }
        r7 = "Photos";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x044d }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x044d }
        r11 = r11.intValue();	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x044d }
        r1[r8] = r7;	 Catch:{ all -> 0x044d }
        r7 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x121e:
        r21 = r1;
        r10 = r32;
        r1 = 2;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x044d }
        r7 = 0;
        r8 = r15[r7];	 Catch:{ all -> 0x044d }
        r1[r7] = r8;	 Catch:{ all -> 0x044d }
        r7 = "ForwardedMessageCount";
        r8 = 1;
        r11 = r15[r8];	 Catch:{ all -> 0x044d }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ all -> 0x044d }
        r11 = r11.intValue();	 Catch:{ all -> 0x044d }
        r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r11);	 Catch:{ all -> 0x044d }
        r7 = r7.toLowerCase();	 Catch:{ all -> 0x044d }
        r1[r8] = r7;	 Catch:{ all -> 0x044d }
        r7 = NUM; // 0x7f0e026d float:1.8876297E38 double:1.0531624634E-314;
        r1 = org.telegram.messenger.LocaleController.formatString(r14, r7, r1);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x124a:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGame";
        r7 = NUM; // 0x7f0e071e float:1.8878733E38 double:1.053163057E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachGame";
        r8 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x126a:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageGIF";
        r7 = NUM; // 0x7f0e026e float:1.88763E38 double:1.053162464E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachGif";
        r8 = NUM; // 0x7f0e0157 float:1.8875733E38 double:1.053162326E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x128a:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageLiveLocation";
        r7 = NUM; // 0x7f0e026f float:1.8876301E38 double:1.0531624644E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachLiveLocation";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x12aa:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageMap";
        r7 = NUM; // 0x7f0e0270 float:1.8876303E38 double:1.053162465E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachLocation";
        r8 = NUM; // 0x7f0e015e float:1.8875747E38 double:1.0531623296E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x12ca:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessagePoll2";
        r7 = NUM; // 0x7f0e0274 float:1.8876311E38 double:1.053162467E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "Poll";
        r8 = NUM; // 0x7f0e08fe float:1.8879707E38 double:1.053163294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x12ef:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageQuiz2";
        r7 = NUM; // 0x7f0e0275 float:1.8876313E38 double:1.0531624674E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "QuizPoll";
        r8 = NUM; // 0x7f0e094e float:1.8879869E38 double:1.0531633335E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1314:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageContact2";
        r7 = NUM; // 0x7f0e026b float:1.8876293E38 double:1.0531624625E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachContact";
        r8 = NUM; // 0x7f0e0152 float:1.8875723E38 double:1.0531623236E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1339:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageAudio";
        r7 = NUM; // 0x7f0e026a float:1.887629E38 double:1.053162462E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachAudio";
        r8 = NUM; // 0x7f0e0150 float:1.887572E38 double:1.0531623226E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1359:
        r21 = r1;
        r10 = r32;
        r1 = r15.length;	 Catch:{ all -> 0x044d }
        r8 = 1;
        if (r1 <= r8) goto L_0x139e;
    L_0x1361:
        r1 = r15[r8];	 Catch:{ all -> 0x044d }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x044d }
        if (r1 != 0) goto L_0x139e;
    L_0x1369:
        r1 = "ChannelMessageStickerEmoji";
        r8 = NUM; // 0x7f0e0278 float:1.887632E38 double:1.053162469E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044d }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x044d }
        r14[r16] = r17;	 Catch:{ all -> 0x044d }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x044d }
        r14[r16] = r17;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044d }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044d }
        r8.<init>();	 Catch:{ all -> 0x044d }
        r14 = r15[r16];	 Catch:{ all -> 0x044d }
        r8.append(r14);	 Catch:{ all -> 0x044d }
        r8.append(r11);	 Catch:{ all -> 0x044d }
        r11 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044d }
        r8.append(r7);	 Catch:{ all -> 0x044d }
        r7 = r8.toString();	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x139e:
        r1 = "ChannelMessageSticker";
        r8 = NUM; // 0x7f0e0277 float:1.8876317E38 double:1.0531624684E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044d }
        r8 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x13b8:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageDocument";
        r7 = NUM; // 0x7f0e026c float:1.8876295E38 double:1.053162463E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e0155 float:1.887573E38 double:1.053162325E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x13d8:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageRound";
        r7 = NUM; // 0x7f0e0276 float:1.8876315E38 double:1.053162468E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e0164 float:1.887576E38 double:1.0531623325E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x13f8:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageVideo";
        r7 = NUM; // 0x7f0e0279 float:1.8876321E38 double:1.0531624694E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0168 float:1.8875768E38 double:1.0531623345E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1418:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessagePhoto";
        r7 = NUM; // 0x7f0e0273 float:1.887631E38 double:1.0531624664E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e0162 float:1.8875756E38 double:1.0531623315E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1438:
        r21 = r1;
        r10 = r32;
        r1 = "ChannelMessageNoText";
        r7 = NUM; // 0x7f0e0272 float:1.8876307E38 double:1.053162466E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0647 float:1.8878297E38 double:1.0531629506E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1458:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageAlbum";
        r7 = NUM; // 0x7f0e0718 float:1.887872E38 double:1.053163054E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
    L_0x146d:
        r7 = r1;
        goto L_0x09c5;
    L_0x1470:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageFew";
        r7 = NUM; // 0x7f0e071c float:1.8878729E38 double:1.053163056E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = "Videos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044d }
        r15 = r15.intValue();	 Catch:{ all -> 0x044d }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ all -> 0x044d }
        r8[r14] = r11;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x1499:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageFew";
        r7 = NUM; // 0x7f0e071c float:1.8878729E38 double:1.053163056E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = "Photos";
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044d }
        r15 = r15.intValue();	 Catch:{ all -> 0x044d }
        r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r15);	 Catch:{ all -> 0x044d }
        r8[r14] = r11;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x14c2:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageForwardFew";
        r7 = NUM; // 0x7f0e071d float:1.887873E38 double:1.0531630563E-314;
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r14 = 0;
        r17 = r15[r14];	 Catch:{ all -> 0x044d }
        r11[r14] = r17;	 Catch:{ all -> 0x044d }
        r14 = 1;
        r15 = r15[r14];	 Catch:{ all -> 0x044d }
        r15 = org.telegram.messenger.Utilities.parseInt(r15);	 Catch:{ all -> 0x044d }
        r15 = r15.intValue();	 Catch:{ all -> 0x044d }
        r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r15);	 Catch:{ all -> 0x044d }
        r11[r14] = r8;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        goto L_0x146d;
    L_0x14e9:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageInvoice";
        r7 = NUM; // 0x7f0e0734 float:1.8878778E38 double:1.0531630677E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "PaymentInvoice";
        r8 = NUM; // 0x7f0e088f float:1.8879481E38 double:1.053163239E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x150e:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGameScored";
        r8 = NUM; // 0x7f0e071f float:1.8878735E38 double:1.0531630573E-314;
        r7 = 3;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r7[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r7[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 2;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r7[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r7);	 Catch:{ all -> 0x044d }
        goto L_0x16f8;
    L_0x152f:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGame";
        r7 = NUM; // 0x7f0e071e float:1.8878733E38 double:1.053163057E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachGame";
        r8 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1554:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageGif";
        r7 = NUM; // 0x7f0e0720 float:1.8878737E38 double:1.053163058E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachGif";
        r8 = NUM; // 0x7f0e0157 float:1.8875733E38 double:1.053162326E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1574:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageLiveLocation";
        r7 = NUM; // 0x7f0e0735 float:1.887878E38 double:1.053163068E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachLiveLocation";
        r8 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1594:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageMap";
        r7 = NUM; // 0x7f0e0736 float:1.8878782E38 double:1.0531630687E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachLocation";
        r8 = NUM; // 0x7f0e015e float:1.8875747E38 double:1.0531623296E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x15b4:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessagePoll2";
        r7 = NUM; // 0x7f0e073a float:1.887879E38 double:1.0531630707E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "Poll";
        r8 = NUM; // 0x7f0e08fe float:1.8879707E38 double:1.053163294E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x15d9:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageQuiz2";
        r7 = NUM; // 0x7f0e073b float:1.8878792E38 double:1.053163071E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "QuizPoll";
        r8 = NUM; // 0x7f0e094e float:1.8879869E38 double:1.0531633335E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x15fe:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageContact2";
        r7 = NUM; // 0x7f0e071a float:1.8878725E38 double:1.053163055E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = "AttachContact";
        r8 = NUM; // 0x7f0e0152 float:1.8875723E38 double:1.0531623236E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1623:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageAudio";
        r7 = NUM; // 0x7f0e0719 float:1.8878723E38 double:1.0531630543E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachAudio";
        r8 = NUM; // 0x7f0e0150 float:1.887572E38 double:1.0531623226E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1643:
        r21 = r1;
        r10 = r32;
        r1 = r15.length;	 Catch:{ all -> 0x044d }
        r8 = 1;
        if (r1 <= r8) goto L_0x1688;
    L_0x164b:
        r1 = r15[r8];	 Catch:{ all -> 0x044d }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ all -> 0x044d }
        if (r1 != 0) goto L_0x1688;
    L_0x1653:
        r1 = "NotificationMessageStickerEmoji";
        r8 = NUM; // 0x7f0e0742 float:1.8878806E38 double:1.0531630746E-314;
        r14 = 2;
        r14 = new java.lang.Object[r14];	 Catch:{ all -> 0x044d }
        r16 = 0;
        r17 = r15[r16];	 Catch:{ all -> 0x044d }
        r14[r16] = r17;	 Catch:{ all -> 0x044d }
        r16 = 1;
        r17 = r15[r16];	 Catch:{ all -> 0x044d }
        r14[r16] = r17;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044d }
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044d }
        r8.<init>();	 Catch:{ all -> 0x044d }
        r14 = r15[r16];	 Catch:{ all -> 0x044d }
        r8.append(r14);	 Catch:{ all -> 0x044d }
        r8.append(r11);	 Catch:{ all -> 0x044d }
        r11 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r11);	 Catch:{ all -> 0x044d }
        r8.append(r7);	 Catch:{ all -> 0x044d }
        r7 = r8.toString();	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1688:
        r1 = "NotificationMessageSticker";
        r8 = NUM; // 0x7f0e0741 float:1.8878804E38 double:1.053163074E-314;
        r11 = 1;
        r14 = new java.lang.Object[r11];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r15 = r15[r11];	 Catch:{ all -> 0x044d }
        r14[r11] = r15;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r8, r14);	 Catch:{ all -> 0x044d }
        r8 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x16a2:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageDocument";
        r7 = NUM; // 0x7f0e071b float:1.8878727E38 double:1.0531630553E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachDocument";
        r8 = NUM; // 0x7f0e0155 float:1.887573E38 double:1.053162325E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x16c2:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageRound";
        r7 = NUM; // 0x7f0e073c float:1.8878794E38 double:1.0531630716E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachRound";
        r8 = NUM; // 0x7f0e0164 float:1.887576E38 double:1.0531623325E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x16e2:
        r21 = r1;
        r10 = r32;
        r1 = "ActionTakeScreenshoot";
        r7 = NUM; // 0x7f0e0094 float:1.8875338E38 double:1.0531622298E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r7);	 Catch:{ all -> 0x044d }
        r7 = "un1";
        r8 = 0;
        r11 = r15[r8];	 Catch:{ all -> 0x044d }
        r1 = r1.replace(r7, r11);	 Catch:{ all -> 0x044d }
    L_0x16f8:
        r7 = r1;
    L_0x16f9:
        r1 = 0;
        goto L_0x17d1;
    L_0x16fc:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageSDVideo";
        r7 = NUM; // 0x7f0e073e float:1.8878798E38 double:1.0531630726E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachDestructingVideo";
        r8 = NUM; // 0x7f0e0154 float:1.8875727E38 double:1.0531623246E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x171c:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageVideo";
        r7 = NUM; // 0x7f0e0744 float:1.887881E38 double:1.0531630756E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachVideo";
        r8 = NUM; // 0x7f0e0168 float:1.8875768E38 double:1.0531623345E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x173b:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageSDPhoto";
        r7 = NUM; // 0x7f0e073d float:1.8878796E38 double:1.053163072E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachDestructingPhoto";
        r8 = NUM; // 0x7f0e0153 float:1.8875725E38 double:1.053162324E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x175a:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessagePhoto";
        r7 = NUM; // 0x7f0e0739 float:1.8878788E38 double:1.05316307E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "AttachPhoto";
        r8 = NUM; // 0x7f0e0162 float:1.8875756E38 double:1.0531623315E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x1779:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageNoText";
        r7 = NUM; // 0x7f0e0738 float:1.8878786E38 double:1.0531630697E-314;
        r8 = 1;
        r11 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r8 = 0;
        r14 = r15[r8];	 Catch:{ all -> 0x044d }
        r11[r8] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11);	 Catch:{ all -> 0x044d }
        r7 = "Message";
        r8 = NUM; // 0x7f0e0647 float:1.8878297E38 double:1.0531629506E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);	 Catch:{ all -> 0x044d }
    L_0x1797:
        r16 = r7;
        r7 = r1;
    L_0x179a:
        r1 = 0;
        goto L_0x17d3;
    L_0x179c:
        r21 = r1;
        r10 = r32;
        r1 = "NotificationMessageText";
        r7 = NUM; // 0x7f0e0743 float:1.8878808E38 double:1.053163075E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ all -> 0x044d }
        r11 = 0;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r11 = 1;
        r14 = r15[r11];	 Catch:{ all -> 0x044d }
        r8[r11] = r14;	 Catch:{ all -> 0x044d }
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r8);	 Catch:{ all -> 0x044d }
        r7 = r15[r11];	 Catch:{ all -> 0x044d }
        goto L_0x1797;
    L_0x17b9:
        if (r1 == 0) goto L_0x17cf;
    L_0x17bb:
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x044d }
        r1.<init>();	 Catch:{ all -> 0x044d }
        r7 = "unhandled loc_key = ";
        r1.append(r7);	 Catch:{ all -> 0x044d }
        r1.append(r9);	 Catch:{ all -> 0x044d }
        r1 = r1.toString();	 Catch:{ all -> 0x044d }
        org.telegram.messenger.FileLog.w(r1);	 Catch:{ all -> 0x044d }
    L_0x17cf:
        r1 = 0;
        r7 = 0;
    L_0x17d1:
        r16 = 0;
    L_0x17d3:
        if (r7 == 0) goto L_0x1872;
    L_0x17d5:
        r8 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ all -> 0x1875 }
        r8.<init>();	 Catch:{ all -> 0x1875 }
        r8.id = r10;	 Catch:{ all -> 0x1875 }
        r8.random_id = r3;	 Catch:{ all -> 0x1875 }
        if (r16 == 0) goto L_0x17e3;
    L_0x17e0:
        r3 = r16;
        goto L_0x17e4;
    L_0x17e3:
        r3 = r7;
    L_0x17e4:
        r8.message = r3;	 Catch:{ all -> 0x1875 }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r37 / r3;
        r4 = (int) r3;	 Catch:{ all -> 0x1875 }
        r8.date = r4;	 Catch:{ all -> 0x1875 }
        if (r5 == 0) goto L_0x17f6;
    L_0x17ef:
        r3 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;	 Catch:{ all -> 0x044d }
        r3.<init>();	 Catch:{ all -> 0x044d }
        r8.action = r3;	 Catch:{ all -> 0x044d }
    L_0x17f6:
        if (r6 == 0) goto L_0x17ff;
    L_0x17f8:
        r3 = r8.flags;	 Catch:{ all -> 0x044d }
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r4;
        r8.flags = r3;	 Catch:{ all -> 0x044d }
    L_0x17ff:
        r8.dialog_id = r12;	 Catch:{ all -> 0x1875 }
        if (r2 == 0) goto L_0x180f;
    L_0x1803:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel;	 Catch:{ all -> 0x044d }
        r3.<init>();	 Catch:{ all -> 0x044d }
        r8.to_id = r3;	 Catch:{ all -> 0x044d }
        r3 = r8.to_id;	 Catch:{ all -> 0x044d }
        r3.channel_id = r2;	 Catch:{ all -> 0x044d }
        goto L_0x182c;
    L_0x180f:
        if (r22 == 0) goto L_0x181f;
    L_0x1811:
        r2 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ all -> 0x044d }
        r2.<init>();	 Catch:{ all -> 0x044d }
        r8.to_id = r2;	 Catch:{ all -> 0x044d }
        r2 = r8.to_id;	 Catch:{ all -> 0x044d }
        r3 = r22;
        r2.chat_id = r3;	 Catch:{ all -> 0x044d }
        goto L_0x182c;
    L_0x181f:
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x1875 }
        r2.<init>();	 Catch:{ all -> 0x1875 }
        r8.to_id = r2;	 Catch:{ all -> 0x1875 }
        r2 = r8.to_id;	 Catch:{ all -> 0x1875 }
        r3 = r24;
        r2.user_id = r3;	 Catch:{ all -> 0x1875 }
    L_0x182c:
        r2 = r8.flags;	 Catch:{ all -> 0x1875 }
        r2 = r2 | 256;
        r8.flags = r2;	 Catch:{ all -> 0x1875 }
        r2 = r21;
        r8.from_id = r2;	 Catch:{ all -> 0x1875 }
        if (r20 != 0) goto L_0x183d;
    L_0x1838:
        if (r5 == 0) goto L_0x183b;
    L_0x183a:
        goto L_0x183d;
    L_0x183b:
        r2 = 0;
        goto L_0x183e;
    L_0x183d:
        r2 = 1;
    L_0x183e:
        r8.mentioned = r2;	 Catch:{ all -> 0x1875 }
        r2 = r19;
        r8.silent = r2;	 Catch:{ all -> 0x1875 }
        r4 = r23;
        r8.from_scheduled = r4;	 Catch:{ all -> 0x1875 }
        r2 = new org.telegram.messenger.MessageObject;	 Catch:{ all -> 0x1875 }
        r19 = r2;
        r20 = r30;
        r21 = r8;
        r22 = r7;
        r23 = r25;
        r24 = r31;
        r25 = r1;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27);	 Catch:{ all -> 0x1875 }
        r1 = new java.util.ArrayList;	 Catch:{ all -> 0x1875 }
        r1.<init>();	 Catch:{ all -> 0x1875 }
        r1.add(r2);	 Catch:{ all -> 0x1875 }
        r2 = org.telegram.messenger.NotificationsController.getInstance(r30);	 Catch:{ all -> 0x1875 }
        r3 = r35;
        r4 = r3.countDownLatch;	 Catch:{ all -> 0x189d }
        r5 = 1;
        r2.processNewMessages(r1, r5, r5, r4);	 Catch:{ all -> 0x189d }
        r28 = 0;
        goto L_0x188a;
    L_0x1872:
        r3 = r35;
        goto L_0x1888;
    L_0x1875:
        r0 = move-exception;
        r3 = r35;
        goto L_0x189e;
    L_0x1879:
        r0 = move-exception;
        r3 = r35;
        goto L_0x18b2;
    L_0x187d:
        r3 = r35;
        goto L_0x1886;
    L_0x1880:
        r0 = move-exception;
        r3 = r1;
        goto L_0x18b2;
    L_0x1883:
        r3 = r1;
        r29 = r14;
    L_0x1886:
        r30 = r15;
    L_0x1888:
        r28 = 1;
    L_0x188a:
        if (r28 == 0) goto L_0x1891;
    L_0x188c:
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x189d }
        r1.countDown();	 Catch:{ all -> 0x189d }
    L_0x1891:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r30);	 Catch:{ all -> 0x189d }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r30);	 Catch:{ all -> 0x189d }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x189d }
        goto L_0x19d1;
    L_0x189d:
        r0 = move-exception;
    L_0x189e:
        r1 = r0;
        r14 = r29;
        r15 = r30;
        goto L_0x197d;
    L_0x18a5:
        r0 = move-exception;
        r3 = r1;
        r29 = r14;
        r30 = r15;
        r1 = r0;
        goto L_0x197d;
    L_0x18ae:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
    L_0x18b2:
        r30 = r15;
        goto L_0x197a;
    L_0x18b6:
        r3 = r1;
        r29 = r7;
        r30 = r15;
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ all -> 0x18d5 }
        r2 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$aCTiHH_b4RhBMfcCkAHdgReKcg4;	 Catch:{ all -> 0x18d0 }
        r15 = r30;
        r2.<init>(r15);	 Catch:{ all -> 0x18cd }
        r1.postRunnable(r2);	 Catch:{ all -> 0x1974 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1974 }
        r1.countDown();	 Catch:{ all -> 0x1974 }
        return;
    L_0x18cd:
        r0 = move-exception;
        goto L_0x197a;
    L_0x18d0:
        r0 = move-exception;
        r15 = r30;
        goto L_0x197a;
    L_0x18d5:
        r0 = move-exception;
        r15 = r30;
        goto L_0x197a;
    L_0x18da:
        r3 = r1;
        r29 = r7;
        r1 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$A_uUL_LENsDGfvOUUl8zWJ6XmBQ;	 Catch:{ all -> 0x18eb }
        r1.<init>(r15);	 Catch:{ all -> 0x18eb }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ all -> 0x1974 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1974 }
        r1.countDown();	 Catch:{ all -> 0x1974 }
        return;
    L_0x18eb:
        r0 = move-exception;
        goto L_0x197a;
    L_0x18ee:
        r3 = r1;
        r29 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification;	 Catch:{ all -> 0x1974 }
        r1.<init>();	 Catch:{ all -> 0x1974 }
        r2 = 0;
        r1.popup = r2;	 Catch:{ all -> 0x1974 }
        r2 = 2;
        r1.flags = r2;	 Catch:{ all -> 0x1974 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r37 / r6;
        r2 = (int) r6;	 Catch:{ all -> 0x1974 }
        r1.inbox_date = r2;	 Catch:{ all -> 0x1974 }
        r2 = "message";
        r2 = r5.getString(r2);	 Catch:{ all -> 0x1974 }
        r1.message = r2;	 Catch:{ all -> 0x1974 }
        r2 = "announcement";
        r1.type = r2;	 Catch:{ all -> 0x1974 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ all -> 0x1974 }
        r2.<init>();	 Catch:{ all -> 0x1974 }
        r1.media = r2;	 Catch:{ all -> 0x1974 }
        r2 = new org.telegram.tgnet.TLRPC$TL_updates;	 Catch:{ all -> 0x1974 }
        r2.<init>();	 Catch:{ all -> 0x1974 }
        r4 = r2.updates;	 Catch:{ all -> 0x1974 }
        r4.add(r1);	 Catch:{ all -> 0x1974 }
        r1 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ all -> 0x1974 }
        r4 = new org.telegram.messenger.-$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo;	 Catch:{ all -> 0x18eb }
        r4.<init>(r15, r2);	 Catch:{ all -> 0x18eb }
        r1.postRunnable(r4);	 Catch:{ all -> 0x1974 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x1974 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x1974 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1974 }
        r1.countDown();	 Catch:{ all -> 0x1974 }
        return;
    L_0x1937:
        r3 = r1;
        r29 = r7;
        r1 = "dc";
        r1 = r11.getInt(r1);	 Catch:{ all -> 0x1974 }
        r2 = "addr";
        r2 = r11.getString(r2);	 Catch:{ all -> 0x1974 }
        r4 = ":";
        r2 = r2.split(r4);	 Catch:{ all -> 0x1974 }
        r4 = r2.length;	 Catch:{ all -> 0x1974 }
        r5 = 2;
        if (r4 == r5) goto L_0x1956;
    L_0x1950:
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1974 }
        r1.countDown();	 Catch:{ all -> 0x1974 }
        return;
    L_0x1956:
        r4 = 0;
        r4 = r2[r4];	 Catch:{ all -> 0x1974 }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ all -> 0x1974 }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ all -> 0x1974 }
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x1974 }
        r5.applyDatacenterAddress(r1, r4, r2);	 Catch:{ all -> 0x1974 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);	 Catch:{ all -> 0x1974 }
        r1.resumeNetworkMaybe();	 Catch:{ all -> 0x1974 }
        r1 = r3.countDownLatch;	 Catch:{ all -> 0x1974 }
        r1.countDown();	 Catch:{ all -> 0x1974 }
        return;
    L_0x1974:
        r0 = move-exception;
        goto L_0x197a;
    L_0x1976:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
    L_0x197a:
        r1 = r0;
        r14 = r29;
    L_0x197d:
        r2 = -1;
        goto L_0x1999;
    L_0x197f:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
        r1 = r0;
        r14 = r29;
        r2 = -1;
        goto L_0x1998;
    L_0x1988:
        r0 = move-exception;
        r3 = r1;
        r29 = r7;
        r1 = r0;
        r14 = r29;
        r2 = -1;
        r9 = 0;
        goto L_0x1998;
    L_0x1992:
        r0 = move-exception;
        r3 = r1;
        r1 = r0;
        r2 = -1;
        r9 = 0;
        r14 = 0;
    L_0x1998:
        r15 = -1;
    L_0x1999:
        if (r15 == r2) goto L_0x19ab;
    L_0x199b:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r15);
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2.resumeNetworkMaybe();
        r2 = r3.countDownLatch;
        r2.countDown();
        goto L_0x19ae;
    L_0x19ab:
        r35.onDecryptError();
    L_0x19ae:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x19ce;
    L_0x19b2:
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
    L_0x19ce:
        org.telegram.messenger.FileLog.e(r1);
    L_0x19d1:
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
