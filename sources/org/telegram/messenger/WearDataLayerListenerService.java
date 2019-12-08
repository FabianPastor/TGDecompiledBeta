package org.telegram.messenger;

import android.text.TextUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.User;

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

    /* JADX WARNING: Removed duplicated region for block: B:113:0x026b  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0155 A:{Catch:{ Exception -> 0x0253 }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x014f A:{Catch:{ Exception -> 0x0253 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x0130 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:96:0x0249 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x00b6 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:104:0x0250 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:93|94|95|96|97) */
    /* JADX WARNING: Missing block: B:95:?, code skipped:
            r2.close();
     */
    /* JADX WARNING: Missing block: B:103:?, code skipped:
            r1.close();
     */
    public void onChannelOpened(com.google.android.gms.wearable.Channel r12) {
        /*
        r11 = this;
        r0 = new com.google.android.gms.common.api.GoogleApiClient$Builder;
        r0.<init>(r11);
        r1 = com.google.android.gms.wearable.Wearable.API;
        r0.addApi(r1);
        r0 = r0.build();
        r1 = r0.blockingConnect();
        r1 = r1.isSuccess();
        if (r1 != 0) goto L_0x0022;
    L_0x0018:
        r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r12 == 0) goto L_0x0021;
    L_0x001c:
        r12 = "failed to connect google api client";
        org.telegram.messenger.FileLog.e(r12);
    L_0x0021:
        return;
    L_0x0022:
        r1 = r12.getPath();
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x003e;
    L_0x002a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "wear channel path: ";
        r2.append(r3);
        r2.append(r1);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x003e:
        r2 = "/getCurrentUser";
        r2 = r2.equals(r1);	 Catch:{ Exception -> 0x0253 }
        r3 = 2;
        r4 = 1;
        r5 = 0;
        if (r2 == 0) goto L_0x0101;
    L_0x0049:
        r1 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x0253 }
        r2 = new java.io.BufferedOutputStream;	 Catch:{ Exception -> 0x0253 }
        r6 = r12.getOutputStream(r0);	 Catch:{ Exception -> 0x0253 }
        r6 = r6.await();	 Catch:{ Exception -> 0x0253 }
        r6 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r6;	 Catch:{ Exception -> 0x0253 }
        r6 = r6.getOutputStream();	 Catch:{ Exception -> 0x0253 }
        r2.<init>(r6);	 Catch:{ Exception -> 0x0253 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0253 }
        r2 = r11.currentAccount;	 Catch:{ Exception -> 0x0253 }
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ Exception -> 0x0253 }
        r2 = r2.isClientActivated();	 Catch:{ Exception -> 0x0253 }
        if (r2 == 0) goto L_0x00f6;
    L_0x006d:
        r2 = r11.currentAccount;	 Catch:{ Exception -> 0x0253 }
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ Exception -> 0x0253 }
        r2 = r2.getCurrentUser();	 Catch:{ Exception -> 0x0253 }
        r6 = r2.id;	 Catch:{ Exception -> 0x0253 }
        r1.writeInt(r6);	 Catch:{ Exception -> 0x0253 }
        r6 = r2.first_name;	 Catch:{ Exception -> 0x0253 }
        r1.writeUTF(r6);	 Catch:{ Exception -> 0x0253 }
        r6 = r2.last_name;	 Catch:{ Exception -> 0x0253 }
        r1.writeUTF(r6);	 Catch:{ Exception -> 0x0253 }
        r6 = r2.phone;	 Catch:{ Exception -> 0x0253 }
        r1.writeUTF(r6);	 Catch:{ Exception -> 0x0253 }
        r6 = r2.photo;	 Catch:{ Exception -> 0x0253 }
        if (r6 == 0) goto L_0x00f2;
    L_0x008f:
        r6 = r2.photo;	 Catch:{ Exception -> 0x0253 }
        r6 = r6.photo_small;	 Catch:{ Exception -> 0x0253 }
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r4);	 Catch:{ Exception -> 0x0253 }
        r6 = new java.util.concurrent.CyclicBarrier;	 Catch:{ Exception -> 0x0253 }
        r6.<init>(r3);	 Catch:{ Exception -> 0x0253 }
        r3 = r4.exists();	 Catch:{ Exception -> 0x0253 }
        if (r3 != 0) goto L_0x00be;
    L_0x00a2:
        r3 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$zwyD_S0-u0WbjrTZjewMNF-0WGA;	 Catch:{ Exception -> 0x0253 }
        r3.<init>(r4, r6);	 Catch:{ Exception -> 0x0253 }
        r7 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$CP3JJJCVrqGAns9jccdML_vquXc;	 Catch:{ Exception -> 0x0253 }
        r7.<init>(r11, r3, r2);	 Catch:{ Exception -> 0x0253 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x0253 }
        r7 = 10;
        r2 = java.util.concurrent.TimeUnit.SECONDS;	 Catch:{ Exception -> 0x00b6 }
        r6.await(r7, r2);	 Catch:{ Exception -> 0x00b6 }
    L_0x00b6:
        r2 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$I2aG1wmNzR_z_5pgp2oULLH-i7k;	 Catch:{ Exception -> 0x0253 }
        r2.<init>(r11, r3);	 Catch:{ Exception -> 0x0253 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0253 }
    L_0x00be:
        r2 = r4.exists();	 Catch:{ Exception -> 0x0253 }
        if (r2 == 0) goto L_0x00ee;
    L_0x00c4:
        r2 = r4.length();	 Catch:{ Exception -> 0x0253 }
        r6 = 52428800; // 0x3200000 float:4.7019774E-37 double:2.5903269E-316;
        r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r8 > 0) goto L_0x00ee;
    L_0x00cf:
        r2 = r4.length();	 Catch:{ Exception -> 0x0253 }
        r3 = (int) r2;	 Catch:{ Exception -> 0x0253 }
        r2 = new byte[r3];	 Catch:{ Exception -> 0x0253 }
        r3 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0253 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x0253 }
        r4 = new java.io.DataInputStream;	 Catch:{ Exception -> 0x0253 }
        r4.<init>(r3);	 Catch:{ Exception -> 0x0253 }
        r4.readFully(r2);	 Catch:{ Exception -> 0x0253 }
        r3.close();	 Catch:{ Exception -> 0x0253 }
        r3 = r2.length;	 Catch:{ Exception -> 0x0253 }
        r1.writeInt(r3);	 Catch:{ Exception -> 0x0253 }
        r1.write(r2);	 Catch:{ Exception -> 0x0253 }
        goto L_0x00f9;
    L_0x00ee:
        r1.writeInt(r5);	 Catch:{ Exception -> 0x0253 }
        goto L_0x00f9;
    L_0x00f2:
        r1.writeInt(r5);	 Catch:{ Exception -> 0x0253 }
        goto L_0x00f9;
    L_0x00f6:
        r1.writeInt(r5);	 Catch:{ Exception -> 0x0253 }
    L_0x00f9:
        r1.flush();	 Catch:{ Exception -> 0x0253 }
        r1.close();	 Catch:{ Exception -> 0x0253 }
        goto L_0x025d;
    L_0x0101:
        r2 = "/waitForAuthCode";
        r2 = r2.equals(r1);	 Catch:{ Exception -> 0x0253 }
        r6 = 0;
        if (r2 == 0) goto L_0x016b;
    L_0x010a:
        r1 = r11.currentAccount;	 Catch:{ Exception -> 0x0253 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x0253 }
        r1.setAppPaused(r5, r5);	 Catch:{ Exception -> 0x0253 }
        r1 = new java.lang.String[r4];	 Catch:{ Exception -> 0x0253 }
        r1[r5] = r6;	 Catch:{ Exception -> 0x0253 }
        r2 = new java.util.concurrent.CyclicBarrier;	 Catch:{ Exception -> 0x0253 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0253 }
        r3 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$6oPgRZPy8Zd89HYJVV7iQJ7-ETw;	 Catch:{ Exception -> 0x0253 }
        r3.<init>(r1, r2);	 Catch:{ Exception -> 0x0253 }
        r6 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$66bw8jSTVlEhrlsDPfucvOtg7dU;	 Catch:{ Exception -> 0x0253 }
        r6.<init>(r11, r3);	 Catch:{ Exception -> 0x0253 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r6);	 Catch:{ Exception -> 0x0253 }
        r6 = 30;
        r8 = java.util.concurrent.TimeUnit.SECONDS;	 Catch:{ Exception -> 0x0130 }
        r2.await(r6, r8);	 Catch:{ Exception -> 0x0130 }
    L_0x0130:
        r2 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$tkmP0IqL8QLTNQYYgIYqoX-Q4VA;	 Catch:{ Exception -> 0x0253 }
        r2.<init>(r11, r3);	 Catch:{ Exception -> 0x0253 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0253 }
        r2 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x0253 }
        r3 = r12.getOutputStream(r0);	 Catch:{ Exception -> 0x0253 }
        r3 = r3.await();	 Catch:{ Exception -> 0x0253 }
        r3 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r3;	 Catch:{ Exception -> 0x0253 }
        r3 = r3.getOutputStream();	 Catch:{ Exception -> 0x0253 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0253 }
        r3 = r1[r5];	 Catch:{ Exception -> 0x0253 }
        if (r3 == 0) goto L_0x0155;
    L_0x014f:
        r1 = r1[r5];	 Catch:{ Exception -> 0x0253 }
        r2.writeUTF(r1);	 Catch:{ Exception -> 0x0253 }
        goto L_0x015a;
    L_0x0155:
        r1 = "";
        r2.writeUTF(r1);	 Catch:{ Exception -> 0x0253 }
    L_0x015a:
        r2.flush();	 Catch:{ Exception -> 0x0253 }
        r2.close();	 Catch:{ Exception -> 0x0253 }
        r1 = r11.currentAccount;	 Catch:{ Exception -> 0x0253 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x0253 }
        r1.setAppPaused(r4, r5);	 Catch:{ Exception -> 0x0253 }
        goto L_0x025d;
    L_0x016b:
        r2 = "/getChatPhoto";
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0253 }
        if (r1 == 0) goto L_0x025d;
    L_0x0173:
        r1 = new java.io.DataInputStream;	 Catch:{ Exception -> 0x0251 }
        r2 = r12.getInputStream(r0);	 Catch:{ Exception -> 0x0251 }
        r2 = r2.await();	 Catch:{ Exception -> 0x0251 }
        r2 = (com.google.android.gms.wearable.Channel.GetInputStreamResult) r2;	 Catch:{ Exception -> 0x0251 }
        r2 = r2.getInputStream();	 Catch:{ Exception -> 0x0251 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0251 }
        r2 = new java.io.DataOutputStream;	 Catch:{ all -> 0x024a }
        r3 = r12.getOutputStream(r0);	 Catch:{ all -> 0x024a }
        r3 = r3.await();	 Catch:{ all -> 0x024a }
        r3 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r3;	 Catch:{ all -> 0x024a }
        r3 = r3.getOutputStream();	 Catch:{ all -> 0x024a }
        r2.<init>(r3);	 Catch:{ all -> 0x024a }
        r3 = r1.readUTF();	 Catch:{ all -> 0x0243 }
        r7 = new org.json.JSONObject;	 Catch:{ all -> 0x0243 }
        r7.<init>(r3);	 Catch:{ all -> 0x0243 }
        r3 = "chat_id";
        r3 = r7.getInt(r3);	 Catch:{ all -> 0x0243 }
        r8 = "account_id";
        r7 = r7.getInt(r8);	 Catch:{ all -> 0x0243 }
        r8 = 0;
    L_0x01af:
        r9 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ all -> 0x0243 }
        r10 = -1;
        if (r8 >= r9) goto L_0x01c4;
    L_0x01b6:
        r9 = org.telegram.messenger.UserConfig.getInstance(r8);	 Catch:{ all -> 0x0243 }
        r9 = r9.getClientUserId();	 Catch:{ all -> 0x0243 }
        if (r9 != r7) goto L_0x01c1;
    L_0x01c0:
        goto L_0x01c5;
    L_0x01c1:
        r8 = r8 + 1;
        goto L_0x01af;
    L_0x01c4:
        r8 = -1;
    L_0x01c5:
        if (r8 == r10) goto L_0x0236;
    L_0x01c7:
        if (r3 <= 0) goto L_0x01e1;
    L_0x01c9:
        r7 = org.telegram.messenger.MessagesController.getInstance(r8);	 Catch:{ all -> 0x0243 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x0243 }
        r3 = r7.getUser(r3);	 Catch:{ all -> 0x0243 }
        if (r3 == 0) goto L_0x01f8;
    L_0x01d7:
        r7 = r3.photo;	 Catch:{ all -> 0x0243 }
        if (r7 == 0) goto L_0x01f8;
    L_0x01db:
        r3 = r3.photo;	 Catch:{ all -> 0x0243 }
        r3 = r3.photo_small;	 Catch:{ all -> 0x0243 }
        r6 = r3;
        goto L_0x01f8;
    L_0x01e1:
        r7 = org.telegram.messenger.MessagesController.getInstance(r8);	 Catch:{ all -> 0x0243 }
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x0243 }
        r3 = r7.getChat(r3);	 Catch:{ all -> 0x0243 }
        if (r3 == 0) goto L_0x01f8;
    L_0x01f0:
        r7 = r3.photo;	 Catch:{ all -> 0x0243 }
        if (r7 == 0) goto L_0x01f8;
    L_0x01f4:
        r3 = r3.photo;	 Catch:{ all -> 0x0243 }
        r6 = r3.photo_small;	 Catch:{ all -> 0x0243 }
    L_0x01f8:
        if (r6 == 0) goto L_0x0232;
    L_0x01fa:
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r4);	 Catch:{ all -> 0x0243 }
        r4 = r3.exists();	 Catch:{ all -> 0x0243 }
        if (r4 == 0) goto L_0x022e;
    L_0x0204:
        r6 = r3.length();	 Catch:{ all -> 0x0243 }
        r8 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 >= 0) goto L_0x022e;
    L_0x020f:
        r6 = r3.length();	 Catch:{ all -> 0x0243 }
        r4 = (int) r6;	 Catch:{ all -> 0x0243 }
        r2.writeInt(r4);	 Catch:{ all -> 0x0243 }
        r4 = new java.io.FileInputStream;	 Catch:{ all -> 0x0243 }
        r4.<init>(r3);	 Catch:{ all -> 0x0243 }
        r3 = 10240; // 0x2800 float:1.4349E-41 double:5.059E-320;
        r3 = new byte[r3];	 Catch:{ all -> 0x0243 }
    L_0x0220:
        r6 = r4.read(r3);	 Catch:{ all -> 0x0243 }
        if (r6 <= 0) goto L_0x022a;
    L_0x0226:
        r2.write(r3, r5, r6);	 Catch:{ all -> 0x0243 }
        goto L_0x0220;
    L_0x022a:
        r4.close();	 Catch:{ all -> 0x0243 }
        goto L_0x0239;
    L_0x022e:
        r2.writeInt(r5);	 Catch:{ all -> 0x0243 }
        goto L_0x0239;
    L_0x0232:
        r2.writeInt(r5);	 Catch:{ all -> 0x0243 }
        goto L_0x0239;
    L_0x0236:
        r2.writeInt(r5);	 Catch:{ all -> 0x0243 }
    L_0x0239:
        r2.flush();	 Catch:{ all -> 0x0243 }
        r2.close();	 Catch:{ all -> 0x024a }
        r1.close();	 Catch:{ Exception -> 0x0251 }
        goto L_0x025d;
    L_0x0243:
        r3 = move-exception;
        throw r3;	 Catch:{ all -> 0x0245 }
    L_0x0245:
        r3 = move-exception;
        r2.close();	 Catch:{ all -> 0x0249 }
    L_0x0249:
        throw r3;	 Catch:{ all -> 0x024a }
    L_0x024a:
        r2 = move-exception;
        throw r2;	 Catch:{ all -> 0x024c }
    L_0x024c:
        r2 = move-exception;
        r1.close();	 Catch:{ all -> 0x0250 }
    L_0x0250:
        throw r2;	 Catch:{ Exception -> 0x0251 }
        goto L_0x025d;
    L_0x0253:
        r1 = move-exception;
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x025d;
    L_0x0258:
        r2 = "error processing wear request";
        org.telegram.messenger.FileLog.e(r2, r1);
    L_0x025d:
        r12 = r12.close(r0);
        r12.await();
        r0.disconnect();
        r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r12 == 0) goto L_0x0270;
    L_0x026b:
        r12 = "WearableDataLayer channel thread exiting";
        org.telegram.messenger.FileLog.d(r12);
    L_0x0270:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.WearDataLayerListenerService.onChannelOpened(com.google.android.gms.wearable.Channel):void");
    }

    static /* synthetic */ void lambda$onChannelOpened$0(File file, CyclicBarrier cyclicBarrier, int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("file loaded: ");
                stringBuilder.append(objArr[0]);
                stringBuilder.append(" ");
                stringBuilder.append(objArr[0].getClass().getName());
                FileLog.d(stringBuilder.toString());
            }
            if (objArr[0].equals(file.getName())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("LOADED USER PHOTO");
                }
                try {
                    cyclicBarrier.await(10, TimeUnit.MILLISECONDS);
                } catch (Exception unused) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$onChannelOpened$1$WearDataLayerListenerService(NotificationCenterDelegate notificationCenterDelegate, User user) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(notificationCenterDelegate, NotificationCenter.fileDidLoad);
        FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForUser(user, false), user, null, 1, 1);
    }

    public /* synthetic */ void lambda$onChannelOpened$2$WearDataLayerListenerService(NotificationCenterDelegate notificationCenterDelegate) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegate, NotificationCenter.fileDidLoad);
    }

    static /* synthetic */ void lambda$onChannelOpened$3(String[] strArr, CyclicBarrier cyclicBarrier, int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.didReceiveNewMessages && ((Long) objArr[0]).longValue() == 777000) {
            ArrayList arrayList = (ArrayList) objArr[1];
            if (arrayList.size() > 0) {
                MessageObject messageObject = (MessageObject) arrayList.get(0);
                if (!TextUtils.isEmpty(messageObject.messageText)) {
                    Matcher matcher = Pattern.compile("[0-9]+").matcher(messageObject.messageText);
                    if (matcher.find()) {
                        strArr[0] = matcher.group();
                        try {
                            cyclicBarrier.await(10, TimeUnit.MILLISECONDS);
                        } catch (Exception unused) {
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$onChannelOpened$4$WearDataLayerListenerService(NotificationCenterDelegate notificationCenterDelegate) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(notificationCenterDelegate, NotificationCenter.didReceiveNewMessages);
    }

    public /* synthetic */ void lambda$onChannelOpened$5$WearDataLayerListenerService(NotificationCenterDelegate notificationCenterDelegate) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegate, NotificationCenter.didReceiveNewMessages);
    }

    public void onMessageReceived(MessageEvent messageEvent) {
        if ("/reply".equals(messageEvent.getPath())) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$WearDataLayerListenerService$WKZsZk9AkXh4ljfhPJ8xDGsJALg(messageEvent));
        }
    }

    static /* synthetic */ void lambda$onMessageReceived$6(MessageEvent messageEvent) {
        try {
            ApplicationLoader.postInitApplication();
            JSONObject jSONObject = new JSONObject(new String(messageEvent.getData(), "UTF-8"));
            String string = jSONObject.getString("text");
            if (string != null) {
                if (string.length() != 0) {
                    long j = jSONObject.getLong("chat_id");
                    int i = jSONObject.getInt("max_id");
                    int i2 = jSONObject.getInt("account_id");
                    for (int i3 = 0; i3 < UserConfig.getActivatedAccountsCount(); i3++) {
                        if (UserConfig.getInstance(i3).getClientUserId() == i2) {
                            i2 = i3;
                            break;
                        }
                    }
                    i2 = -1;
                    if (!(j == 0 || i == 0)) {
                        if (i2 != -1) {
                            SendMessagesHelper.getInstance(i2).sendMessage(string.toString(), j, null, null, true, null, null, null, true, 0);
                            MessagesController.getInstance(i2).markDialogAsRead(j, i, i, 0, false, 0, true, 0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(e);
            }
        }
    }

    public static void sendMessageToWatch(String str, byte[] bArr, String str2) {
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability(str2, 1).addOnCompleteListener(new -$$Lambda$WearDataLayerListenerService$RaGAAXsdKhxe-vpoR0F8ujrQJFI(str, bArr));
    }

    static /* synthetic */ void lambda$sendMessageToWatch$7(String str, byte[] bArr, Task task) {
        CapabilityInfo capabilityInfo = (CapabilityInfo) task.getResult();
        if (capabilityInfo != null) {
            MessageClient messageClient = Wearable.getMessageClient(ApplicationLoader.applicationContext);
            for (Node id : capabilityInfo.getNodes()) {
                messageClient.sendMessage(id.getId(), str, bArr);
            }
        }
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        if ("remote_notifications".equals(capabilityInfo.getName())) {
            watchConnected = false;
            for (Node isNearby : capabilityInfo.getNodes()) {
                if (isNearby.isNearby()) {
                    watchConnected = true;
                }
            }
        }
    }

    public static void updateWatchConnectionState() {
        try {
            Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener(-$$Lambda$WearDataLayerListenerService$gOGDgE93vZnJRS0bNwigjGIcLFc.INSTANCE);
        } catch (Throwable unused) {
        }
    }

    static /* synthetic */ void lambda$updateWatchConnectionState$8(Task task) {
        watchConnected = false;
        try {
            CapabilityInfo capabilityInfo = (CapabilityInfo) task.getResult();
            if (capabilityInfo != null) {
                for (Node isNearby : capabilityInfo.getNodes()) {
                    if (isNearby.isNearby()) {
                        watchConnected = true;
                    }
                }
            }
        } catch (Exception unused) {
        }
    }

    public static boolean isWatchConnected() {
        return watchConnected;
    }
}
