package org.telegram.messenger;

import android.text.TextUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;

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

    /* JADX WARNING: Can't wrap try/catch for region: R(5:93|94|95|96|97) */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:18|19|20|21|22|23) */
    /* JADX WARNING: Can't wrap try/catch for region: R(8:35|36|37|38|39|40|(1:42)(1:43)|44) */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0240, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
        throw r3;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:104:0x024b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x00b6 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x012f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:96:0x0244 */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:120:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x014e A[Catch:{ Exception -> 0x024e }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0154 A[Catch:{ Exception -> 0x024e }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:22:0x00b6=Splitter:B:22:0x00b6, B:39:0x012f=Splitter:B:39:0x012f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onChannelOpened(com.google.android.gms.wearable.Channel r12) {
        /*
            r11 = this;
            com.google.android.gms.common.api.GoogleApiClient$Builder r0 = new com.google.android.gms.common.api.GoogleApiClient$Builder
            r0.<init>(r11)
            com.google.android.gms.common.api.Api<com.google.android.gms.wearable.Wearable$WearableOptions> r1 = com.google.android.gms.wearable.Wearable.API
            com.google.android.gms.common.api.GoogleApiClient$Builder r0 = r0.addApi(r1)
            com.google.android.gms.common.api.GoogleApiClient r0 = r0.build()
            com.google.android.gms.common.ConnectionResult r1 = r0.blockingConnect()
            boolean r1 = r1.isSuccess()
            if (r1 != 0) goto L_0x0023
            boolean r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r12 == 0) goto L_0x0022
            java.lang.String r12 = "failed to connect google api client"
            org.telegram.messenger.FileLog.e((java.lang.String) r12)
        L_0x0022:
            return
        L_0x0023:
            java.lang.String r1 = r12.getPath()
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0040
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "wear channel path: "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0040:
            java.lang.String r2 = "/getCurrentUser"
            boolean r2 = r2.equals(r1)     // Catch:{ Exception -> 0x024e }
            r3 = 2
            r4 = 1
            r5 = 0
            if (r2 == 0) goto L_0x0100
            java.io.DataOutputStream r1 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x024e }
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x024e }
            com.google.android.gms.common.api.PendingResult r6 = r12.getOutputStream(r0)     // Catch:{ Exception -> 0x024e }
            com.google.android.gms.common.api.Result r6 = r6.await()     // Catch:{ Exception -> 0x024e }
            com.google.android.gms.wearable.Channel$GetOutputStreamResult r6 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r6     // Catch:{ Exception -> 0x024e }
            java.io.OutputStream r6 = r6.getOutputStream()     // Catch:{ Exception -> 0x024e }
            r2.<init>(r6)     // Catch:{ Exception -> 0x024e }
            r1.<init>(r2)     // Catch:{ Exception -> 0x024e }
            int r2 = r11.currentAccount     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x024e }
            boolean r2 = r2.isClientActivated()     // Catch:{ Exception -> 0x024e }
            if (r2 == 0) goto L_0x00f5
            int r2 = r11.currentAccount     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x024e }
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()     // Catch:{ Exception -> 0x024e }
            int r6 = r2.id     // Catch:{ Exception -> 0x024e }
            r1.writeInt(r6)     // Catch:{ Exception -> 0x024e }
            java.lang.String r6 = r2.first_name     // Catch:{ Exception -> 0x024e }
            r1.writeUTF(r6)     // Catch:{ Exception -> 0x024e }
            java.lang.String r6 = r2.last_name     // Catch:{ Exception -> 0x024e }
            r1.writeUTF(r6)     // Catch:{ Exception -> 0x024e }
            java.lang.String r6 = r2.phone     // Catch:{ Exception -> 0x024e }
            r1.writeUTF(r6)     // Catch:{ Exception -> 0x024e }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r2.photo     // Catch:{ Exception -> 0x024e }
            if (r6 == 0) goto L_0x00f1
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ Exception -> 0x024e }
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r4)     // Catch:{ Exception -> 0x024e }
            java.util.concurrent.CyclicBarrier r6 = new java.util.concurrent.CyclicBarrier     // Catch:{ Exception -> 0x024e }
            r6.<init>(r3)     // Catch:{ Exception -> 0x024e }
            boolean r3 = r4.exists()     // Catch:{ Exception -> 0x024e }
            if (r3 != 0) goto L_0x00be
            org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$8U0-_yHRCazmJqUiYuBHLzQc1Zw r3 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$8U0-_yHRCazmJqUiYuBHLzQc1Zw     // Catch:{ Exception -> 0x024e }
            r3.<init>(r4, r6)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$ydGZBP8a_DgE_oUlcF-P734PK4E r7 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$ydGZBP8a_DgE_oUlcF-P734PK4E     // Catch:{ Exception -> 0x024e }
            r7.<init>(r3, r2)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)     // Catch:{ Exception -> 0x024e }
            r7 = 10
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ Exception -> 0x00b6 }
            r6.await(r7, r2)     // Catch:{ Exception -> 0x00b6 }
        L_0x00b6:
            org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$-E3pdqIpzem9HMQdYOOW1Pr-JLY r2 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$-E3pdqIpzem9HMQdYOOW1Pr-JLY     // Catch:{ Exception -> 0x024e }
            r2.<init>(r3)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x024e }
        L_0x00be:
            boolean r2 = r4.exists()     // Catch:{ Exception -> 0x024e }
            if (r2 == 0) goto L_0x00ed
            long r2 = r4.length()     // Catch:{ Exception -> 0x024e }
            r6 = 52428800(0x3200000, double:2.5903269E-316)
            int r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r8 > 0) goto L_0x00ed
            long r2 = r4.length()     // Catch:{ Exception -> 0x024e }
            int r3 = (int) r2     // Catch:{ Exception -> 0x024e }
            byte[] r2 = new byte[r3]     // Catch:{ Exception -> 0x024e }
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ Exception -> 0x024e }
            r5.<init>(r4)     // Catch:{ Exception -> 0x024e }
            java.io.DataInputStream r4 = new java.io.DataInputStream     // Catch:{ Exception -> 0x024e }
            r4.<init>(r5)     // Catch:{ Exception -> 0x024e }
            r4.readFully(r2)     // Catch:{ Exception -> 0x024e }
            r5.close()     // Catch:{ Exception -> 0x024e }
            r1.writeInt(r3)     // Catch:{ Exception -> 0x024e }
            r1.write(r2)     // Catch:{ Exception -> 0x024e }
            goto L_0x00f8
        L_0x00ed:
            r1.writeInt(r5)     // Catch:{ Exception -> 0x024e }
            goto L_0x00f8
        L_0x00f1:
            r1.writeInt(r5)     // Catch:{ Exception -> 0x024e }
            goto L_0x00f8
        L_0x00f5:
            r1.writeInt(r5)     // Catch:{ Exception -> 0x024e }
        L_0x00f8:
            r1.flush()     // Catch:{ Exception -> 0x024e }
            r1.close()     // Catch:{ Exception -> 0x024e }
            goto L_0x0258
        L_0x0100:
            java.lang.String r2 = "/waitForAuthCode"
            boolean r2 = r2.equals(r1)     // Catch:{ Exception -> 0x024e }
            r6 = 0
            if (r2 == 0) goto L_0x016a
            int r1 = r11.currentAccount     // Catch:{ Exception -> 0x024e }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)     // Catch:{ Exception -> 0x024e }
            r1.setAppPaused(r5, r5)     // Catch:{ Exception -> 0x024e }
            java.lang.String[] r1 = new java.lang.String[r4]     // Catch:{ Exception -> 0x024e }
            r1[r5] = r6     // Catch:{ Exception -> 0x024e }
            java.util.concurrent.CyclicBarrier r2 = new java.util.concurrent.CyclicBarrier     // Catch:{ Exception -> 0x024e }
            r2.<init>(r3)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$TyUn_inIn_bL15Zp21g9ds7kIqo r3 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$TyUn_inIn_bL15Zp21g9ds7kIqo     // Catch:{ Exception -> 0x024e }
            r3.<init>(r1, r2)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$1H-JF3ZpPL0EQHpem6g8cKThkx0 r6 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$1H-JF3ZpPL0EQHpem6g8cKThkx0     // Catch:{ Exception -> 0x024e }
            r6.<init>(r3)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)     // Catch:{ Exception -> 0x024e }
            r6 = 30
            java.util.concurrent.TimeUnit r8 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ Exception -> 0x012f }
            r2.await(r6, r8)     // Catch:{ Exception -> 0x012f }
        L_0x012f:
            org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$--vZ1ZRAnaS9dH9su4DFTPSXoSU r2 = new org.telegram.messenger.-$$Lambda$WearDataLayerListenerService$--vZ1ZRAnaS9dH9su4DFTPSXoSU     // Catch:{ Exception -> 0x024e }
            r2.<init>(r3)     // Catch:{ Exception -> 0x024e }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x024e }
            java.io.DataOutputStream r2 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x024e }
            com.google.android.gms.common.api.PendingResult r3 = r12.getOutputStream(r0)     // Catch:{ Exception -> 0x024e }
            com.google.android.gms.common.api.Result r3 = r3.await()     // Catch:{ Exception -> 0x024e }
            com.google.android.gms.wearable.Channel$GetOutputStreamResult r3 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r3     // Catch:{ Exception -> 0x024e }
            java.io.OutputStream r3 = r3.getOutputStream()     // Catch:{ Exception -> 0x024e }
            r2.<init>(r3)     // Catch:{ Exception -> 0x024e }
            r3 = r1[r5]     // Catch:{ Exception -> 0x024e }
            if (r3 == 0) goto L_0x0154
            r1 = r1[r5]     // Catch:{ Exception -> 0x024e }
            r2.writeUTF(r1)     // Catch:{ Exception -> 0x024e }
            goto L_0x0159
        L_0x0154:
            java.lang.String r1 = ""
            r2.writeUTF(r1)     // Catch:{ Exception -> 0x024e }
        L_0x0159:
            r2.flush()     // Catch:{ Exception -> 0x024e }
            r2.close()     // Catch:{ Exception -> 0x024e }
            int r1 = r11.currentAccount     // Catch:{ Exception -> 0x024e }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)     // Catch:{ Exception -> 0x024e }
            r1.setAppPaused(r4, r5)     // Catch:{ Exception -> 0x024e }
            goto L_0x0258
        L_0x016a:
            java.lang.String r2 = "/getChatPhoto"
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x024e }
            if (r1 == 0) goto L_0x0258
            java.io.DataInputStream r1 = new java.io.DataInputStream     // Catch:{ Exception -> 0x024c }
            com.google.android.gms.common.api.PendingResult r2 = r12.getInputStream(r0)     // Catch:{ Exception -> 0x024c }
            com.google.android.gms.common.api.Result r2 = r2.await()     // Catch:{ Exception -> 0x024c }
            com.google.android.gms.wearable.Channel$GetInputStreamResult r2 = (com.google.android.gms.wearable.Channel.GetInputStreamResult) r2     // Catch:{ Exception -> 0x024c }
            java.io.InputStream r2 = r2.getInputStream()     // Catch:{ Exception -> 0x024c }
            r1.<init>(r2)     // Catch:{ Exception -> 0x024c }
            java.io.DataOutputStream r2 = new java.io.DataOutputStream     // Catch:{ all -> 0x0245 }
            com.google.android.gms.common.api.PendingResult r3 = r12.getOutputStream(r0)     // Catch:{ all -> 0x0245 }
            com.google.android.gms.common.api.Result r3 = r3.await()     // Catch:{ all -> 0x0245 }
            com.google.android.gms.wearable.Channel$GetOutputStreamResult r3 = (com.google.android.gms.wearable.Channel.GetOutputStreamResult) r3     // Catch:{ all -> 0x0245 }
            java.io.OutputStream r3 = r3.getOutputStream()     // Catch:{ all -> 0x0245 }
            r2.<init>(r3)     // Catch:{ all -> 0x0245 }
            java.lang.String r3 = r1.readUTF()     // Catch:{ all -> 0x023e }
            org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ all -> 0x023e }
            r7.<init>(r3)     // Catch:{ all -> 0x023e }
            java.lang.String r3 = "chat_id"
            int r3 = r7.getInt(r3)     // Catch:{ all -> 0x023e }
            java.lang.String r8 = "account_id"
            int r7 = r7.getInt(r8)     // Catch:{ all -> 0x023e }
            r8 = 0
        L_0x01ae:
            int r9 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ all -> 0x023e }
            r10 = -1
            if (r8 >= r9) goto L_0x01c3
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r8)     // Catch:{ all -> 0x023e }
            int r9 = r9.getClientUserId()     // Catch:{ all -> 0x023e }
            if (r9 != r7) goto L_0x01c0
            goto L_0x01c4
        L_0x01c0:
            int r8 = r8 + 1
            goto L_0x01ae
        L_0x01c3:
            r8 = -1
        L_0x01c4:
            if (r8 == r10) goto L_0x0231
            if (r3 <= 0) goto L_0x01de
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r8)     // Catch:{ all -> 0x023e }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x023e }
            org.telegram.tgnet.TLRPC$User r3 = r7.getUser(r3)     // Catch:{ all -> 0x023e }
            if (r3 == 0) goto L_0x01f3
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r3.photo     // Catch:{ all -> 0x023e }
            if (r3 == 0) goto L_0x01f3
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_small     // Catch:{ all -> 0x023e }
            r6 = r3
            goto L_0x01f3
        L_0x01de:
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r8)     // Catch:{ all -> 0x023e }
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x023e }
            org.telegram.tgnet.TLRPC$Chat r3 = r7.getChat(r3)     // Catch:{ all -> 0x023e }
            if (r3 == 0) goto L_0x01f3
            org.telegram.tgnet.TLRPC$ChatPhoto r3 = r3.photo     // Catch:{ all -> 0x023e }
            if (r3 == 0) goto L_0x01f3
            org.telegram.tgnet.TLRPC$FileLocation r6 = r3.photo_small     // Catch:{ all -> 0x023e }
        L_0x01f3:
            if (r6 == 0) goto L_0x022d
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r4)     // Catch:{ all -> 0x023e }
            boolean r4 = r3.exists()     // Catch:{ all -> 0x023e }
            if (r4 == 0) goto L_0x0229
            long r6 = r3.length()     // Catch:{ all -> 0x023e }
            r8 = 102400(0x19000, double:5.05923E-319)
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0229
            long r6 = r3.length()     // Catch:{ all -> 0x023e }
            int r4 = (int) r6     // Catch:{ all -> 0x023e }
            r2.writeInt(r4)     // Catch:{ all -> 0x023e }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ all -> 0x023e }
            r4.<init>(r3)     // Catch:{ all -> 0x023e }
            r3 = 10240(0x2800, float:1.4349E-41)
            byte[] r3 = new byte[r3]     // Catch:{ all -> 0x023e }
        L_0x021b:
            int r6 = r4.read(r3)     // Catch:{ all -> 0x023e }
            if (r6 <= 0) goto L_0x0225
            r2.write(r3, r5, r6)     // Catch:{ all -> 0x023e }
            goto L_0x021b
        L_0x0225:
            r4.close()     // Catch:{ all -> 0x023e }
            goto L_0x0234
        L_0x0229:
            r2.writeInt(r5)     // Catch:{ all -> 0x023e }
            goto L_0x0234
        L_0x022d:
            r2.writeInt(r5)     // Catch:{ all -> 0x023e }
            goto L_0x0234
        L_0x0231:
            r2.writeInt(r5)     // Catch:{ all -> 0x023e }
        L_0x0234:
            r2.flush()     // Catch:{ all -> 0x023e }
            r2.close()     // Catch:{ all -> 0x0245 }
            r1.close()     // Catch:{ Exception -> 0x024c }
            goto L_0x0258
        L_0x023e:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0240 }
        L_0x0240:
            r3 = move-exception
            r2.close()     // Catch:{ all -> 0x0244 }
        L_0x0244:
            throw r3     // Catch:{ all -> 0x0245 }
        L_0x0245:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0247 }
        L_0x0247:
            r2 = move-exception
            r1.close()     // Catch:{ all -> 0x024b }
        L_0x024b:
            throw r2     // Catch:{ Exception -> 0x024c }
        L_0x024c:
            goto L_0x0258
        L_0x024e:
            r1 = move-exception
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0258
            java.lang.String r2 = "error processing wear request"
            org.telegram.messenger.FileLog.e(r2, r1)
        L_0x0258:
            com.google.android.gms.common.api.PendingResult r12 = r12.close(r0)
            r12.await()
            r0.disconnect()
            boolean r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r12 == 0) goto L_0x026b
            java.lang.String r12 = "WearableDataLayer channel thread exiting"
            org.telegram.messenger.FileLog.d(r12)
        L_0x026b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.WearDataLayerListenerService.onChannelOpened(com.google.android.gms.wearable.Channel):void");
    }

    static /* synthetic */ void lambda$onChannelOpened$0(File file, CyclicBarrier cyclicBarrier, int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.fileLoaded) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("file loaded: " + objArr[0] + " " + objArr[0].getClass().getName());
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChannelOpened$1 */
    public /* synthetic */ void lambda$onChannelOpened$1$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate notificationCenterDelegate, TLRPC$User tLRPC$User) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(notificationCenterDelegate, NotificationCenter.fileLoaded);
        FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForUserOrChat(tLRPC$User, 1), tLRPC$User, (String) null, 1, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChannelOpened$2 */
    public /* synthetic */ void lambda$onChannelOpened$2$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate notificationCenterDelegate) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegate, NotificationCenter.fileLoaded);
    }

    static /* synthetic */ void lambda$onChannelOpened$3(String[] strArr, CyclicBarrier cyclicBarrier, int i, int i2, Object[] objArr) {
        if (i == NotificationCenter.didReceiveNewMessages && objArr[0].longValue() == 777000) {
            ArrayList arrayList = objArr[1];
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChannelOpened$4 */
    public /* synthetic */ void lambda$onChannelOpened$4$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate notificationCenterDelegate) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(notificationCenterDelegate, NotificationCenter.didReceiveNewMessages);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChannelOpened$5 */
    public /* synthetic */ void lambda$onChannelOpened$5$WearDataLayerListenerService(NotificationCenter.NotificationCenterDelegate notificationCenterDelegate) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(notificationCenterDelegate, NotificationCenter.didReceiveNewMessages);
    }

    public void onMessageReceived(MessageEvent messageEvent) {
        if ("/reply".equals(messageEvent.getPath())) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WearDataLayerListenerService.lambda$onMessageReceived$6(MessageEvent.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$onMessageReceived$6(MessageEvent messageEvent) {
        int i;
        try {
            ApplicationLoader.postInitApplication();
            JSONObject jSONObject = new JSONObject(new String(messageEvent.getData(), "UTF-8"));
            String string = jSONObject.getString("text");
            if (string == null) {
                return;
            }
            if (string.length() != 0) {
                long j = jSONObject.getLong("chat_id");
                int i2 = jSONObject.getInt("max_id");
                int i3 = jSONObject.getInt("account_id");
                int i4 = 0;
                while (true) {
                    if (i4 >= UserConfig.getActivatedAccountsCount()) {
                        i = -1;
                        break;
                    } else if (UserConfig.getInstance(i4).getClientUserId() == i3) {
                        i = i4;
                        break;
                    } else {
                        i4++;
                    }
                }
                if (j != 0 && i2 != 0) {
                    if (i != -1) {
                        SendMessagesHelper.getInstance(i).sendMessage(string.toString(), j, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, 0, true, 0);
                    }
                }
            }
        } catch (Exception e) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static void sendMessageToWatch(String str, byte[] bArr, String str2) {
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability(str2, 1).addOnCompleteListener(new OnCompleteListener(str, bArr) {
            public final /* synthetic */ String f$0;
            public final /* synthetic */ byte[] f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onComplete(Task task) {
                WearDataLayerListenerService.lambda$sendMessageToWatch$7(this.f$0, this.f$1, task);
            }
        });
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
            Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener($$Lambda$WearDataLayerListenerService$OxFnbwNlLoIOEBS2zP2MedWVWRM.INSTANCE);
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
