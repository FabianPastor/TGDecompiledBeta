package org.telegram.messenger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import org.json.JSONObject;

public class WearDataLayerListenerService extends WearableListenerService {
    private static boolean watchConnected;
    private int currentAccount = UserConfig.selectedAccount;

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("WearableDataLayer service created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("WearableDataLayer service destroyed");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:116:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:116:? A:{SYNTHETIC, RETURN} */
    public void onChannelOpened(com.google.android.gms.wearable.Channel r33) {
        /*
        r32 = this;
        r28 = new com.google.android.gms.common.api.GoogleApiClient$Builder;
        r0 = r28;
        r1 = r32;
        r0.<init>(r1);
        r29 = com.google.android.gms.wearable.Wearable.API;
        r28 = r28.addApi(r29);
        r6 = r28.build();
        r28 = r6.blockingConnect();
        r28 = r28.isSuccess();
        if (r28 != 0) goto L_0x0028;
    L_0x001d:
        r28 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r28 == 0) goto L_0x0027;
    L_0x0021:
        r28 = "failed to connect google api client";
        org.telegram.messenger.FileLog.e(r28);
    L_0x0027:
        return;
    L_0x0028:
        r20 = r33.getPath();
        r28 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r28 == 0) goto L_0x004b;
    L_0x0030:
        r28 = new java.lang.StringBuilder;
        r28.<init>();
        r29 = "wear channel path: ";
        r28 = r28.append(r29);
        r0 = r28;
        r1 = r20;
        r28 = r0.append(r1);
        r28 = r28.toString();
        org.telegram.messenger.FileLog.d(r28);
    L_0x004b:
        r28 = "/getCurrentUser";
        r0 = r28;
        r1 = r20;
        r28 = r0.equals(r1);	 Catch:{ Exception -> 0x01a0 }
        if (r28 == 0) goto L_0x01c4;
    L_0x0058:
        r19 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x01a0 }
        r29 = new java.io.BufferedOutputStream;	 Catch:{ Exception -> 0x01a0 }
        r0 = r33;
        r28 = r0.getOutputStream(r6);	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.await();	 Catch:{ Exception -> 0x01a0 }
        r28 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r28;	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.getOutputStream();	 Catch:{ Exception -> 0x01a0 }
        r0 = r29;
        r1 = r28;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r19;
        r1 = r29;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r32;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r28 = org.telegram.messenger.UserConfig.getInstance(r28);	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.isClientActivated();	 Catch:{ Exception -> 0x01a0 }
        if (r28 == 0) goto L_0x01ba;
    L_0x008a:
        r0 = r32;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r28 = org.telegram.messenger.UserConfig.getInstance(r28);	 Catch:{ Exception -> 0x01a0 }
        r26 = r28.getCurrentUser();	 Catch:{ Exception -> 0x01a0 }
        r0 = r26;
        r0 = r0.id;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r26;
        r0 = r0.first_name;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r19;
        r1 = r28;
        r0.writeUTF(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r26;
        r0 = r0.last_name;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r19;
        r1 = r28;
        r0.writeUTF(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r26;
        r0 = r0.phone;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r19;
        r1 = r28;
        r0.writeUTF(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r26;
        r0 = r0.photo;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        if (r28 == 0) goto L_0x01b0;
    L_0x00d4:
        r0 = r26;
        r0 = r0.photo;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r28;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r29 = 1;
        r21 = org.telegram.messenger.FileLoader.getPathToAttach(r28, r29);	 Catch:{ Exception -> 0x01a0 }
        r7 = new java.util.concurrent.CyclicBarrier;	 Catch:{ Exception -> 0x01a0 }
        r28 = 2;
        r0 = r28;
        r7.<init>(r0);	 Catch:{ Exception -> 0x01a0 }
        r28 = r21.exists();	 Catch:{ Exception -> 0x01a0 }
        if (r28 != 0) goto L_0x0129;
    L_0x00f5:
        r17 = new org.telegram.messenger.WearDataLayerListenerService$1;	 Catch:{ Exception -> 0x01a0 }
        r0 = r17;
        r1 = r32;
        r2 = r21;
        r0.<init>(r2, r7);	 Catch:{ Exception -> 0x01a0 }
        r28 = new org.telegram.messenger.WearDataLayerListenerService$2;	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r1 = r32;
        r2 = r17;
        r3 = r26;
        r0.<init>(r2, r3);	 Catch:{ Exception -> 0x01a0 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r28);	 Catch:{ Exception -> 0x01a0 }
        r28 = 10;
        r30 = java.util.concurrent.TimeUnit.SECONDS;	 Catch:{ Exception -> 0x03c8 }
        r0 = r28;
        r2 = r30;
        r7.await(r0, r2);	 Catch:{ Exception -> 0x03c8 }
    L_0x011b:
        r28 = new org.telegram.messenger.WearDataLayerListenerService$3;	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r1 = r32;
        r2 = r17;
        r0.<init>(r2);	 Catch:{ Exception -> 0x01a0 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r28);	 Catch:{ Exception -> 0x01a0 }
    L_0x0129:
        r28 = r21.exists();	 Catch:{ Exception -> 0x01a0 }
        if (r28 == 0) goto L_0x0196;
    L_0x012f:
        r28 = r21.length();	 Catch:{ Exception -> 0x01a0 }
        r30 = 52428800; // 0x3200000 float:4.7019774E-37 double:2.5903269E-316;
        r28 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1));
        if (r28 > 0) goto L_0x0196;
    L_0x013a:
        r28 = r21.length();	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r0 = (int) r0;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r28;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x01a0 }
        r22 = r0;
        r23 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x01a0 }
        r0 = r23;
        r1 = r21;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r28 = new java.io.DataInputStream;	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r1 = r23;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r1 = r22;
        r0.readFully(r1);	 Catch:{ Exception -> 0x01a0 }
        r23.close();	 Catch:{ Exception -> 0x01a0 }
        r0 = r22;
        r0 = r0.length;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x01a0 }
        r0 = r19;
        r1 = r22;
        r0.write(r1);	 Catch:{ Exception -> 0x01a0 }
    L_0x0178:
        r19.flush();	 Catch:{ Exception -> 0x01a0 }
        r19.close();	 Catch:{ Exception -> 0x01a0 }
    L_0x017e:
        r0 = r33;
        r28 = r0.close(r6);
        r28.await();
        r6.disconnect();
        r28 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r28 == 0) goto L_0x0027;
    L_0x018e:
        r28 = "WearableDataLayer channel thread exiting";
        org.telegram.messenger.FileLog.d(r28);
        goto L_0x0027;
    L_0x0196:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x01a0 }
        goto L_0x0178;
    L_0x01a0:
        r27 = move-exception;
        r28 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r28 == 0) goto L_0x017e;
    L_0x01a5:
        r28 = "error processing wear request";
        r0 = r28;
        r1 = r27;
        org.telegram.messenger.FileLog.e(r0, r1);
        goto L_0x017e;
    L_0x01b0:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x01a0 }
        goto L_0x0178;
    L_0x01ba:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x01a0 }
        goto L_0x0178;
    L_0x01c4:
        r28 = "/waitForAuthCode";
        r0 = r28;
        r1 = r20;
        r28 = r0.equals(r1);	 Catch:{ Exception -> 0x01a0 }
        if (r28 == 0) goto L_0x0275;
    L_0x01d1:
        r0 = r32;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r28 = org.telegram.tgnet.ConnectionsManager.getInstance(r28);	 Catch:{ Exception -> 0x01a0 }
        r29 = 0;
        r30 = 0;
        r28.setAppPaused(r29, r30);	 Catch:{ Exception -> 0x01a0 }
        r28 = 1;
        r0 = r28;
        r11 = new java.lang.String[r0];	 Catch:{ Exception -> 0x01a0 }
        r28 = 0;
        r29 = 0;
        r11[r28] = r29;	 Catch:{ Exception -> 0x01a0 }
        r7 = new java.util.concurrent.CyclicBarrier;	 Catch:{ Exception -> 0x01a0 }
        r28 = 2;
        r0 = r28;
        r7.<init>(r0);	 Catch:{ Exception -> 0x01a0 }
        r17 = new org.telegram.messenger.WearDataLayerListenerService$4;	 Catch:{ Exception -> 0x01a0 }
        r0 = r17;
        r1 = r32;
        r0.<init>(r11, r7);	 Catch:{ Exception -> 0x01a0 }
        r28 = new org.telegram.messenger.WearDataLayerListenerService$5;	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r1 = r32;
        r2 = r17;
        r0.<init>(r2);	 Catch:{ Exception -> 0x01a0 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r28);	 Catch:{ Exception -> 0x01a0 }
        r28 = 30;
        r30 = java.util.concurrent.TimeUnit.SECONDS;	 Catch:{ Exception -> 0x03c5 }
        r0 = r28;
        r2 = r30;
        r7.await(r0, r2);	 Catch:{ Exception -> 0x03c5 }
    L_0x0219:
        r28 = new org.telegram.messenger.WearDataLayerListenerService$6;	 Catch:{ Exception -> 0x01a0 }
        r0 = r28;
        r1 = r32;
        r2 = r17;
        r0.<init>(r2);	 Catch:{ Exception -> 0x01a0 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r28);	 Catch:{ Exception -> 0x01a0 }
        r19 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x01a0 }
        r0 = r33;
        r28 = r0.getOutputStream(r6);	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.await();	 Catch:{ Exception -> 0x01a0 }
        r28 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r28;	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.getOutputStream();	 Catch:{ Exception -> 0x01a0 }
        r0 = r19;
        r1 = r28;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r28 = 0;
        r28 = r11[r28];	 Catch:{ Exception -> 0x01a0 }
        if (r28 == 0) goto L_0x026a;
    L_0x0246:
        r28 = 0;
        r28 = r11[r28];	 Catch:{ Exception -> 0x01a0 }
        r0 = r19;
        r1 = r28;
        r0.writeUTF(r1);	 Catch:{ Exception -> 0x01a0 }
    L_0x0251:
        r19.flush();	 Catch:{ Exception -> 0x01a0 }
        r19.close();	 Catch:{ Exception -> 0x01a0 }
        r0 = r32;
        r0 = r0.currentAccount;	 Catch:{ Exception -> 0x01a0 }
        r28 = r0;
        r28 = org.telegram.tgnet.ConnectionsManager.getInstance(r28);	 Catch:{ Exception -> 0x01a0 }
        r29 = 1;
        r30 = 0;
        r28.setAppPaused(r29, r30);	 Catch:{ Exception -> 0x01a0 }
        goto L_0x017e;
    L_0x026a:
        r28 = "";
        r0 = r19;
        r1 = r28;
        r0.writeUTF(r1);	 Catch:{ Exception -> 0x01a0 }
        goto L_0x0251;
    L_0x0275:
        r28 = "/getChatPhoto";
        r0 = r28;
        r1 = r20;
        r28 = r0.equals(r1);	 Catch:{ Exception -> 0x01a0 }
        if (r28 == 0) goto L_0x017e;
    L_0x0282:
        r16 = new java.io.DataInputStream;	 Catch:{ Exception -> 0x01a0 }
        r0 = r33;
        r28 = r0.getInputStream(r6);	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.await();	 Catch:{ Exception -> 0x01a0 }
        r28 = (com.google.android.gms.wearable.Channel.GetInputStreamResult) r28;	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.getInputStream();	 Catch:{ Exception -> 0x01a0 }
        r0 = r16;
        r1 = r28;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r19 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x01a0 }
        r0 = r33;
        r28 = r0.getOutputStream(r6);	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.await();	 Catch:{ Exception -> 0x01a0 }
        r28 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r28;	 Catch:{ Exception -> 0x01a0 }
        r28 = r28.getOutputStream();	 Catch:{ Exception -> 0x01a0 }
        r0 = r19;
        r1 = r28;
        r0.<init>(r1);	 Catch:{ Exception -> 0x01a0 }
        r4 = r16.readUTF();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r25 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r0 = r25;
        r0.<init>(r4);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = "chat_id";
        r0 = r25;
        r1 = r28;
        r10 = r0.getInt(r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = "account_id";
        r0 = r25;
        r1 = r28;
        r5 = r0.getInt(r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r12 = -1;
        r15 = 0;
    L_0x02d7:
        r28 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r0 = r28;
        if (r15 >= r0) goto L_0x02ec;
    L_0x02df:
        r28 = org.telegram.messenger.UserConfig.getInstance(r15);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r28.getClientUserId();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r0 = r28;
        if (r0 != r5) goto L_0x036b;
    L_0x02eb:
        r12 = r15;
    L_0x02ec:
        r28 = -1;
        r0 = r28;
        if (r12 == r0) goto L_0x03bb;
    L_0x02f2:
        r18 = 0;
        if (r10 <= 0) goto L_0x036f;
    L_0x02f6:
        r28 = org.telegram.messenger.MessagesController.getInstance(r12);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r29 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r26 = r28.getUser(r29);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        if (r26 == 0) goto L_0x0318;
    L_0x0304:
        r0 = r26;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r0;
        if (r28 == 0) goto L_0x0318;
    L_0x030c:
        r0 = r26;
        r0 = r0.photo;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r0;
        r0 = r28;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r18 = r0;
    L_0x0318:
        if (r18 == 0) goto L_0x03b1;
    L_0x031a:
        r28 = 1;
        r0 = r18;
        r1 = r28;
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r13.exists();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        if (r28 == 0) goto L_0x039f;
    L_0x032a:
        r28 = r13.length();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r30 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;
        r28 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1));
        if (r28 >= 0) goto L_0x039f;
    L_0x0335:
        r28 = r13.length();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r0 = r28;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r14 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r14.<init>(r13);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = 10240; // 0x2800 float:1.4349E-41 double:5.059E-320;
        r0 = r28;
        r8 = new byte[r0];	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
    L_0x0350:
        r24 = r14.read(r8);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        if (r24 <= 0) goto L_0x0391;
    L_0x0356:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r2 = r24;
        r0.write(r8, r1, r2);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        goto L_0x0350;
    L_0x0362:
        r28 = move-exception;
        r16.close();	 Catch:{ Exception -> 0x01a0 }
        r19.close();	 Catch:{ Exception -> 0x01a0 }
        goto L_0x017e;
    L_0x036b:
        r15 = r15 + 1;
        goto L_0x02d7;
    L_0x036f:
        r28 = org.telegram.messenger.MessagesController.getInstance(r12);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r0 = -r10;
        r29 = r0;
        r29 = java.lang.Integer.valueOf(r29);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r9 = r28.getChat(r29);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        if (r9 == 0) goto L_0x0318;
    L_0x0380:
        r0 = r9.photo;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r0;
        if (r28 == 0) goto L_0x0318;
    L_0x0386:
        r0 = r9.photo;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r28 = r0;
        r0 = r28;
        r0 = r0.photo_small;	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r18 = r0;
        goto L_0x0318;
    L_0x0391:
        r14.close();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
    L_0x0394:
        r19.flush();	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        r16.close();	 Catch:{ Exception -> 0x01a0 }
        r19.close();	 Catch:{ Exception -> 0x01a0 }
        goto L_0x017e;
    L_0x039f:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        goto L_0x0394;
    L_0x03a9:
        r28 = move-exception;
        r16.close();	 Catch:{ Exception -> 0x01a0 }
        r19.close();	 Catch:{ Exception -> 0x01a0 }
        throw r28;	 Catch:{ Exception -> 0x01a0 }
    L_0x03b1:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        goto L_0x0394;
    L_0x03bb:
        r28 = 0;
        r0 = r19;
        r1 = r28;
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0362, all -> 0x03a9 }
        goto L_0x0394;
    L_0x03c5:
        r28 = move-exception;
        goto L_0x0219;
    L_0x03c8:
        r28 = move-exception;
        goto L_0x011b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.WearDataLayerListenerService.onChannelOpened(com.google.android.gms.wearable.Channel):void");
    }

    public void onMessageReceived(final MessageEvent messageEvent) {
        if ("/reply".equals(messageEvent.getPath())) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        ApplicationLoader.postInitApplication();
                        JSONObject jSONObject = new JSONObject(new String(messageEvent.getData(), "UTF-8"));
                        CharSequence text = jSONObject.getString("text");
                        if (text != null && text.length() != 0) {
                            long dialog_id = jSONObject.getLong("chat_id");
                            int max_id = jSONObject.getInt("max_id");
                            int currentAccount = -1;
                            int accountID = jSONObject.getInt("account_id");
                            for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                                if (UserConfig.getInstance(i).getClientUserId() == accountID) {
                                    currentAccount = i;
                                    break;
                                }
                            }
                            if (dialog_id != 0 && max_id != 0 && currentAccount != -1) {
                                SendMessagesHelper.getInstance(currentAccount).sendMessage(text.toString(), dialog_id, null, null, true, null, null, null);
                                MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true);
                            }
                        }
                    } catch (Exception x) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e(x);
                        }
                    }
                }
            });
        }
    }

    public static void sendMessageToWatch(final String path, final byte[] data, String capability) {
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability(capability, 1).addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
            public void onComplete(Task<CapabilityInfo> task) {
                CapabilityInfo info = (CapabilityInfo) task.getResult();
                if (info != null) {
                    MessageClient mc = Wearable.getMessageClient(ApplicationLoader.applicationContext);
                    for (Node node : info.getNodes()) {
                        mc.sendMessage(node.getId(), path, data);
                    }
                }
            }
        });
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        if ("remote_notifications".equals(capabilityInfo.getName())) {
            watchConnected = false;
            for (Node node : capabilityInfo.getNodes()) {
                if (node.isNearby()) {
                    watchConnected = true;
                }
            }
        }
    }

    public static void updateWatchConnectionState() {
        try {
            Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
                public void onComplete(Task<CapabilityInfo> task) {
                    WearDataLayerListenerService.watchConnected = false;
                    try {
                        CapabilityInfo capabilityInfo = (CapabilityInfo) task.getResult();
                        if (capabilityInfo != null) {
                            for (Node node : capabilityInfo.getNodes()) {
                                if (node.isNearby()) {
                                    WearDataLayerListenerService.watchConnected = true;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Throwable th) {
        }
    }

    public static boolean isWatchConnected() {
        return watchConnected;
    }
}
