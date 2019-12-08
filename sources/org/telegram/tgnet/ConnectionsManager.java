package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder;
import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;

public class ConnectionsManager extends BaseController {
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int ConnectionStateConnected = 3;
    public static final int ConnectionStateConnecting = 1;
    public static final int ConnectionStateConnectingToProxy = 4;
    public static final int ConnectionStateUpdating = 5;
    public static final int ConnectionStateWaitingForNetwork = 2;
    public static final int ConnectionTypeDownload = 2;
    public static final int ConnectionTypeDownload2 = 65538;
    public static final int ConnectionTypeGeneric = 1;
    public static final int ConnectionTypePush = 8;
    public static final int ConnectionTypeUpload = 4;
    public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
    public static final Executor DNS_THREAD_POOL_EXECUTOR;
    public static final int FileTypeAudio = 50331648;
    public static final int FileTypeFile = 67108864;
    public static final int FileTypePhoto = 16777216;
    public static final int FileTypeVideo = 33554432;
    private static volatile ConnectionsManager[] Instance = new ConnectionsManager[3];
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    private static AsyncTask currentTask;
    private static HashMap<String, ResolvedDomain> dnsCache = new HashMap();
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    private static HashMap<String, ResolveHostByNameTask> resolvingHostnameTasks = new HashMap();
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(128);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable runnable) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DnsAsyncTask #");
            stringBuilder.append(this.mCount.getAndIncrement());
            return new Thread(runnable, stringBuilder.toString());
        }
    };
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState = native_getConnectionState(this.currentAccount);
    private boolean isUpdating;
    private long lastPauseTime = System.currentTimeMillis();
    private AtomicInteger lastRequestToken = new AtomicInteger(1);

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public DnsTxtLoadTask(int i) {
            this.currentAccount = i;
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x014a A:{SYNTHETIC, Splitter:B:56:0x014a} */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x0157 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0154 A:{SYNTHETIC, Splitter:B:61:0x0154} */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r14) {
            /*
            r13 = this;
            r14 = 0;
            r0 = 0;
            r2 = r14;
            r3 = r2;
            r1 = 0;
        L_0x0005:
            r4 = 3;
            if (r1 >= r4) goto L_0x016c;
        L_0x0008:
            if (r1 != 0) goto L_0x0010;
        L_0x000a:
            r4 = "www.google.com";
            goto L_0x0018;
        L_0x000d:
            r4 = move-exception;
            goto L_0x0145;
        L_0x0010:
            r4 = 1;
            if (r1 != r4) goto L_0x0016;
        L_0x0013:
            r4 = "www.google.ru";
            goto L_0x0018;
        L_0x0016:
            r4 = "google.com";
        L_0x0018:
            r5 = r13.currentAccount;	 Catch:{ all -> 0x000d }
            r5 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r5);	 Catch:{ all -> 0x000d }
            if (r5 == 0) goto L_0x0023;
        L_0x0020:
            r5 = "tapv3.stel.com";
            goto L_0x002f;
        L_0x0023:
            r5 = r13.currentAccount;	 Catch:{ all -> 0x000d }
            r5 = org.telegram.messenger.AccountInstance.getInstance(r5);	 Catch:{ all -> 0x000d }
            r5 = r5.getMessagesController();	 Catch:{ all -> 0x000d }
            r5 = r5.dcDomainName;	 Catch:{ all -> 0x000d }
        L_0x002f:
            r6 = org.telegram.messenger.Utilities.random;	 Catch:{ all -> 0x000d }
            r7 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
            r6 = r6.nextInt(r7);	 Catch:{ all -> 0x000d }
            r6 = r6 + 13;
            r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x000d }
            r7.<init>(r6);	 Catch:{ all -> 0x000d }
            r8 = 0;
        L_0x003f:
            if (r8 >= r6) goto L_0x0055;
        L_0x0041:
            r9 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM";
            r10 = org.telegram.messenger.Utilities.random;	 Catch:{ all -> 0x000d }
            r11 = 62;
            r10 = r10.nextInt(r11);	 Catch:{ all -> 0x000d }
            r9 = r9.charAt(r10);	 Catch:{ all -> 0x000d }
            r7.append(r9);	 Catch:{ all -> 0x000d }
            r8 = r8 + 1;
            goto L_0x003f;
        L_0x0055:
            r6 = new java.net.URL;	 Catch:{ all -> 0x000d }
            r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x000d }
            r8.<init>();	 Catch:{ all -> 0x000d }
            r9 = "https://";
            r8.append(r9);	 Catch:{ all -> 0x000d }
            r8.append(r4);	 Catch:{ all -> 0x000d }
            r4 = "/resolve?name=";
            r8.append(r4);	 Catch:{ all -> 0x000d }
            r8.append(r5);	 Catch:{ all -> 0x000d }
            r4 = "&type=ANY&random_padding=";
            r8.append(r4);	 Catch:{ all -> 0x000d }
            r8.append(r7);	 Catch:{ all -> 0x000d }
            r4 = r8.toString();	 Catch:{ all -> 0x000d }
            r6.<init>(r4);	 Catch:{ all -> 0x000d }
            r4 = r6.openConnection();	 Catch:{ all -> 0x000d }
            r5 = "User-Agent";
            r6 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r4.addRequestProperty(r5, r6);	 Catch:{ all -> 0x000d }
            r5 = "Host";
            r6 = "dns.google.com";
            r4.addRequestProperty(r5, r6);	 Catch:{ all -> 0x000d }
            r5 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r4.setConnectTimeout(r5);	 Catch:{ all -> 0x000d }
            r4.setReadTimeout(r5);	 Catch:{ all -> 0x000d }
            r4.connect();	 Catch:{ all -> 0x000d }
            r3 = r4.getInputStream();	 Catch:{ all -> 0x000d }
            r4 = r4.getDate();	 Catch:{ all -> 0x000d }
            r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r4 = r4 / r6;
            r5 = (int) r4;	 Catch:{ all -> 0x000d }
            r13.responseDate = r5;	 Catch:{ all -> 0x000d }
            r4 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x000d }
            r4.<init>();	 Catch:{ all -> 0x000d }
            r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r2 = new byte[r2];	 Catch:{ all -> 0x0141 }
        L_0x00b0:
            r5 = r13.isCancelled();	 Catch:{ all -> 0x0141 }
            if (r5 == 0) goto L_0x00b7;
        L_0x00b6:
            goto L_0x00c2;
        L_0x00b7:
            r5 = r3.read(r2);	 Catch:{ all -> 0x0141 }
            if (r5 <= 0) goto L_0x00c1;
        L_0x00bd:
            r4.write(r2, r0, r5);	 Catch:{ all -> 0x0141 }
            goto L_0x00b0;
        L_0x00c1:
            r2 = -1;
        L_0x00c2:
            r2 = new org.json.JSONObject;	 Catch:{ all -> 0x0141 }
            r5 = new java.lang.String;	 Catch:{ all -> 0x0141 }
            r6 = r4.toByteArray();	 Catch:{ all -> 0x0141 }
            r5.<init>(r6);	 Catch:{ all -> 0x0141 }
            r2.<init>(r5);	 Catch:{ all -> 0x0141 }
            r5 = "Answer";
            r2 = r2.getJSONArray(r5);	 Catch:{ all -> 0x0141 }
            r5 = r2.length();	 Catch:{ all -> 0x0141 }
            r6 = new java.util.ArrayList;	 Catch:{ all -> 0x0141 }
            r6.<init>(r5);	 Catch:{ all -> 0x0141 }
            r7 = 0;
        L_0x00e0:
            if (r7 >= r5) goto L_0x00fd;
        L_0x00e2:
            r8 = r2.getJSONObject(r7);	 Catch:{ all -> 0x0141 }
            r9 = "type";
            r9 = r8.getInt(r9);	 Catch:{ all -> 0x0141 }
            r10 = 16;
            if (r9 == r10) goto L_0x00f1;
        L_0x00f0:
            goto L_0x00fa;
        L_0x00f1:
            r9 = "data";
            r8 = r8.getString(r9);	 Catch:{ all -> 0x0141 }
            r6.add(r8);	 Catch:{ all -> 0x0141 }
        L_0x00fa:
            r7 = r7 + 1;
            goto L_0x00e0;
        L_0x00fd:
            r2 = org.telegram.tgnet.-$$Lambda$ConnectionsManager$DnsTxtLoadTask$BEcjqZFmP4raPbtfXzTVfRUBAsw.INSTANCE;	 Catch:{ all -> 0x0141 }
            java.util.Collections.sort(r6, r2);	 Catch:{ all -> 0x0141 }
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0141 }
            r2.<init>();	 Catch:{ all -> 0x0141 }
            r5 = 0;
        L_0x0108:
            r7 = r6.size();	 Catch:{ all -> 0x0141 }
            if (r5 >= r7) goto L_0x0122;
        L_0x010e:
            r7 = r6.get(r5);	 Catch:{ all -> 0x0141 }
            r7 = (java.lang.String) r7;	 Catch:{ all -> 0x0141 }
            r8 = "\"";
            r9 = "";
            r7 = r7.replace(r8, r9);	 Catch:{ all -> 0x0141 }
            r2.append(r7);	 Catch:{ all -> 0x0141 }
            r5 = r5 + 1;
            goto L_0x0108;
        L_0x0122:
            r2 = r2.toString();	 Catch:{ all -> 0x0141 }
            r2 = android.util.Base64.decode(r2, r0);	 Catch:{ all -> 0x0141 }
            r5 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x0141 }
            r6 = r2.length;	 Catch:{ all -> 0x0141 }
            r5.<init>(r6);	 Catch:{ all -> 0x0141 }
            r5.writeBytes(r2);	 Catch:{ all -> 0x0141 }
            if (r3 == 0) goto L_0x013d;
        L_0x0135:
            r3.close();	 Catch:{ all -> 0x0139 }
            goto L_0x013d;
        L_0x0139:
            r14 = move-exception;
            org.telegram.messenger.FileLog.e(r14);
        L_0x013d:
            r4.close();	 Catch:{ Exception -> 0x0140 }
        L_0x0140:
            return r5;
        L_0x0141:
            r2 = move-exception;
            r12 = r4;
            r4 = r2;
            r2 = r12;
        L_0x0145:
            org.telegram.messenger.FileLog.e(r4);	 Catch:{ all -> 0x015b }
            if (r3 == 0) goto L_0x0152;
        L_0x014a:
            r3.close();	 Catch:{ all -> 0x014e }
            goto L_0x0152;
        L_0x014e:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
        L_0x0152:
            if (r2 == 0) goto L_0x0157;
        L_0x0154:
            r2.close();	 Catch:{ Exception -> 0x0157 }
        L_0x0157:
            r1 = r1 + 1;
            goto L_0x0005;
        L_0x015b:
            r14 = move-exception;
            if (r3 == 0) goto L_0x0166;
        L_0x015e:
            r3.close();	 Catch:{ all -> 0x0162 }
            goto L_0x0166;
        L_0x0162:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0166:
            if (r2 == 0) goto L_0x016b;
        L_0x0168:
            r2.close();	 Catch:{ Exception -> 0x016b }
        L_0x016b:
            throw r14;
        L_0x016c:
            return r14;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$DnsTxtLoadTask$Y_uiONB1DXfH_CyjIpbAd-79WxM(this, nativeByteBuffer));
        }

        public /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$DnsTxtLoadTask(NativeByteBuffer nativeByteBuffer) {
            ConnectionsManager.currentTask = null;
            if (nativeByteBuffer != null) {
                int i = this.currentAccount;
                ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
                FileLog.d("start google task");
            }
            GoogleDnsLoadTask googleDnsLoadTask = new GoogleDnsLoadTask(this.currentAccount);
            googleDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            ConnectionsManager.currentTask = googleDnsLoadTask;
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        /* Access modifiers changed, original: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
        }

        public FirebaseTask(int i) {
            this.currentAccount = i;
        }

        /* Access modifiers changed, original: protected|varargs */
        public NativeByteBuffer doInBackground(Void... voidArr) {
            try {
                if (ConnectionsManager.native_isTestBackend(this.currentAccount) == 0) {
                    this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                    Builder builder = new Builder();
                    builder.setDeveloperModeEnabled(false);
                    this.firebaseRemoteConfig.setConfigSettings(builder.build());
                    String string = this.firebaseRemoteConfig.getString("ipconfigv3");
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("current firebase value = ");
                        stringBuilder.append(string);
                        FileLog.d(stringBuilder.toString());
                    }
                    this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new -$$Lambda$ConnectionsManager$FirebaseTask$oSwUtRnRPbxyDkUaydPY1RLuswg(this));
                    return null;
                }
                throw new Exception("test backend");
            } catch (Throwable th) {
                Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$FirebaseTask$MpFuuAucqzd493iw_K_pK0R5oKk(this));
                FileLog.e(th);
            }
        }

        public /* synthetic */ void lambda$doInBackground$1$ConnectionsManager$FirebaseTask(Task task) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$FirebaseTask$v_BUEJ-aiKpxOCLASSNAMEgi4s4n7o6lI(this, task.isSuccessful()));
        }

        public /* synthetic */ void lambda$null$0$ConnectionsManager$FirebaseTask(boolean z) {
            CharSequence string;
            ConnectionsManager.currentTask = null;
            if (z) {
                this.firebaseRemoteConfig.activateFetched();
                string = this.firebaseRemoteConfig.getString("ipconfigv3");
            } else {
                string = null;
            }
            if (TextUtils.isEmpty(string)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to get firebase result");
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask dnsTxtLoadTask = new DnsTxtLoadTask(this.currentAccount);
                dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                ConnectionsManager.currentTask = dnsTxtLoadTask;
                return;
            }
            byte[] decode = Base64.decode(string, 0);
            try {
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(decode.length);
                nativeByteBuffer.writeBytes(decode);
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, nativeByteBuffer.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), (int) (this.firebaseRemoteConfig.getInfo().getFetchTimeMillis() / 1000));
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public /* synthetic */ void lambda$doInBackground$2$ConnectionsManager$FirebaseTask() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get firebase result");
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask dnsTxtLoadTask = new DnsTxtLoadTask(this.currentAccount);
            dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            ConnectionsManager.currentTask = dnsTxtLoadTask;
        }
    }

    private static class GoogleDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public GoogleDnsLoadTask(int i) {
            this.currentAccount = i;
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x012c A:{SYNTHETIC, Splitter:B:52:0x012c} */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x0136 A:{SYNTHETIC, Splitter:B:57:0x0136} */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x012c A:{SYNTHETIC, Splitter:B:52:0x012c} */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x0136 A:{SYNTHETIC, Splitter:B:57:0x0136} */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r12) {
            /*
            r11 = this;
            r12 = 0;
            r0 = r11.currentAccount;	 Catch:{ all -> 0x0124 }
            r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0);	 Catch:{ all -> 0x0124 }
            if (r0 == 0) goto L_0x000c;
        L_0x0009:
            r0 = "tapv3.stel.com";
            goto L_0x0018;
        L_0x000c:
            r0 = r11.currentAccount;	 Catch:{ all -> 0x0124 }
            r0 = org.telegram.messenger.AccountInstance.getInstance(r0);	 Catch:{ all -> 0x0124 }
            r0 = r0.getMessagesController();	 Catch:{ all -> 0x0124 }
            r0 = r0.dcDomainName;	 Catch:{ all -> 0x0124 }
        L_0x0018:
            r1 = org.telegram.messenger.Utilities.random;	 Catch:{ all -> 0x0124 }
            r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
            r1 = r1.nextInt(r2);	 Catch:{ all -> 0x0124 }
            r1 = r1 + 13;
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0124 }
            r2.<init>(r1);	 Catch:{ all -> 0x0124 }
            r3 = 0;
            r4 = 0;
        L_0x0029:
            if (r4 >= r1) goto L_0x003f;
        L_0x002b:
            r5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM";
            r6 = org.telegram.messenger.Utilities.random;	 Catch:{ all -> 0x0124 }
            r7 = 62;
            r6 = r6.nextInt(r7);	 Catch:{ all -> 0x0124 }
            r5 = r5.charAt(r6);	 Catch:{ all -> 0x0124 }
            r2.append(r5);	 Catch:{ all -> 0x0124 }
            r4 = r4 + 1;
            goto L_0x0029;
        L_0x003f:
            r1 = new java.net.URL;	 Catch:{ all -> 0x0124 }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0124 }
            r4.<init>();	 Catch:{ all -> 0x0124 }
            r5 = "https://dns.google.com/resolve?name=";
            r4.append(r5);	 Catch:{ all -> 0x0124 }
            r4.append(r0);	 Catch:{ all -> 0x0124 }
            r0 = "&type=ANY&random_padding=";
            r4.append(r0);	 Catch:{ all -> 0x0124 }
            r4.append(r2);	 Catch:{ all -> 0x0124 }
            r0 = r4.toString();	 Catch:{ all -> 0x0124 }
            r1.<init>(r0);	 Catch:{ all -> 0x0124 }
            r0 = r1.openConnection();	 Catch:{ all -> 0x0124 }
            r1 = "User-Agent";
            r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r0.addRequestProperty(r1, r2);	 Catch:{ all -> 0x0124 }
            r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r0.setConnectTimeout(r1);	 Catch:{ all -> 0x0124 }
            r0.setReadTimeout(r1);	 Catch:{ all -> 0x0124 }
            r0.connect();	 Catch:{ all -> 0x0124 }
            r1 = r0.getInputStream();	 Catch:{ all -> 0x0124 }
            r4 = r0.getDate();	 Catch:{ all -> 0x0121 }
            r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r4 = r4 / r6;
            r0 = (int) r4;	 Catch:{ all -> 0x0121 }
            r11.responseDate = r0;	 Catch:{ all -> 0x0121 }
            r0 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x0121 }
            r0.<init>();	 Catch:{ all -> 0x0121 }
            r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r2 = new byte[r2];	 Catch:{ all -> 0x011c }
        L_0x008b:
            r4 = r11.isCancelled();	 Catch:{ all -> 0x011c }
            if (r4 == 0) goto L_0x0092;
        L_0x0091:
            goto L_0x009d;
        L_0x0092:
            r4 = r1.read(r2);	 Catch:{ all -> 0x011c }
            if (r4 <= 0) goto L_0x009c;
        L_0x0098:
            r0.write(r2, r3, r4);	 Catch:{ all -> 0x011c }
            goto L_0x008b;
        L_0x009c:
            r2 = -1;
        L_0x009d:
            r2 = new org.json.JSONObject;	 Catch:{ all -> 0x011c }
            r4 = new java.lang.String;	 Catch:{ all -> 0x011c }
            r5 = r0.toByteArray();	 Catch:{ all -> 0x011c }
            r4.<init>(r5);	 Catch:{ all -> 0x011c }
            r2.<init>(r4);	 Catch:{ all -> 0x011c }
            r4 = "Answer";
            r2 = r2.getJSONArray(r4);	 Catch:{ all -> 0x011c }
            r4 = r2.length();	 Catch:{ all -> 0x011c }
            r5 = new java.util.ArrayList;	 Catch:{ all -> 0x011c }
            r5.<init>(r4);	 Catch:{ all -> 0x011c }
            r6 = 0;
        L_0x00bb:
            if (r6 >= r4) goto L_0x00d8;
        L_0x00bd:
            r7 = r2.getJSONObject(r6);	 Catch:{ all -> 0x011c }
            r8 = "type";
            r8 = r7.getInt(r8);	 Catch:{ all -> 0x011c }
            r9 = 16;
            if (r8 == r9) goto L_0x00cc;
        L_0x00cb:
            goto L_0x00d5;
        L_0x00cc:
            r8 = "data";
            r7 = r7.getString(r8);	 Catch:{ all -> 0x011c }
            r5.add(r7);	 Catch:{ all -> 0x011c }
        L_0x00d5:
            r6 = r6 + 1;
            goto L_0x00bb;
        L_0x00d8:
            r2 = org.telegram.tgnet.-$$Lambda$ConnectionsManager$GoogleDnsLoadTask$7geEd5QmUa4Hgb9F_2w_Jc5Hu4M.INSTANCE;	 Catch:{ all -> 0x011c }
            java.util.Collections.sort(r5, r2);	 Catch:{ all -> 0x011c }
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x011c }
            r2.<init>();	 Catch:{ all -> 0x011c }
            r4 = 0;
        L_0x00e3:
            r6 = r5.size();	 Catch:{ all -> 0x011c }
            if (r4 >= r6) goto L_0x00fd;
        L_0x00e9:
            r6 = r5.get(r4);	 Catch:{ all -> 0x011c }
            r6 = (java.lang.String) r6;	 Catch:{ all -> 0x011c }
            r7 = "\"";
            r8 = "";
            r6 = r6.replace(r7, r8);	 Catch:{ all -> 0x011c }
            r2.append(r6);	 Catch:{ all -> 0x011c }
            r4 = r4 + 1;
            goto L_0x00e3;
        L_0x00fd:
            r2 = r2.toString();	 Catch:{ all -> 0x011c }
            r2 = android.util.Base64.decode(r2, r3);	 Catch:{ all -> 0x011c }
            r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x011c }
            r4 = r2.length;	 Catch:{ all -> 0x011c }
            r3.<init>(r4);	 Catch:{ all -> 0x011c }
            r3.writeBytes(r2);	 Catch:{ all -> 0x011c }
            if (r1 == 0) goto L_0x0118;
        L_0x0110:
            r1.close();	 Catch:{ all -> 0x0114 }
            goto L_0x0118;
        L_0x0114:
            r12 = move-exception;
            org.telegram.messenger.FileLog.e(r12);
        L_0x0118:
            r0.close();	 Catch:{ Exception -> 0x011b }
        L_0x011b:
            return r3;
        L_0x011c:
            r2 = move-exception;
            r10 = r2;
            r2 = r0;
            r0 = r10;
            goto L_0x0127;
        L_0x0121:
            r0 = move-exception;
            r2 = r12;
            goto L_0x0127;
        L_0x0124:
            r0 = move-exception;
            r1 = r12;
            r2 = r1;
        L_0x0127:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x013a }
            if (r1 == 0) goto L_0x0134;
        L_0x012c:
            r1.close();	 Catch:{ all -> 0x0130 }
            goto L_0x0134;
        L_0x0130:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0134:
            if (r2 == 0) goto L_0x0139;
        L_0x0136:
            r2.close();	 Catch:{ Exception -> 0x0139 }
        L_0x0139:
            return r12;
        L_0x013a:
            r12 = move-exception;
            if (r1 == 0) goto L_0x0145;
        L_0x013d:
            r1.close();	 Catch:{ all -> 0x0141 }
            goto L_0x0145;
        L_0x0141:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0145:
            if (r2 == 0) goto L_0x014a;
        L_0x0147:
            r2.close();	 Catch:{ Exception -> 0x014a }
        L_0x014a:
            goto L_0x014c;
        L_0x014b:
            throw r12;
        L_0x014c:
            goto L_0x014b;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager$GoogleDnsLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$GoogleDnsLoadTask$K2wVx-2U-zy10pPXbhoeKUDCHHA(this, nativeByteBuffer));
        }

        public /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$GoogleDnsLoadTask(NativeByteBuffer nativeByteBuffer) {
            ConnectionsManager.currentTask = null;
            if (nativeByteBuffer != null) {
                int i = this.currentAccount;
                ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get google result");
                FileLog.d("start mozilla task");
            }
            MozillaDnsLoadTask mozillaDnsLoadTask = new MozillaDnsLoadTask(this.currentAccount);
            mozillaDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            ConnectionsManager.currentTask = mozillaDnsLoadTask;
        }
    }

    private static class MozillaDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public MozillaDnsLoadTask(int i) {
            this.currentAccount = i;
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0133 A:{SYNTHETIC, Splitter:B:52:0x0133} */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x013d A:{SYNTHETIC, Splitter:B:57:0x013d} */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0133 A:{SYNTHETIC, Splitter:B:52:0x0133} */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x013d A:{SYNTHETIC, Splitter:B:57:0x013d} */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r12) {
            /*
            r11 = this;
            r12 = 0;
            r0 = r11.currentAccount;	 Catch:{ all -> 0x012b }
            r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0);	 Catch:{ all -> 0x012b }
            if (r0 == 0) goto L_0x000c;
        L_0x0009:
            r0 = "tapv3.stel.com";
            goto L_0x0018;
        L_0x000c:
            r0 = r11.currentAccount;	 Catch:{ all -> 0x012b }
            r0 = org.telegram.messenger.AccountInstance.getInstance(r0);	 Catch:{ all -> 0x012b }
            r0 = r0.getMessagesController();	 Catch:{ all -> 0x012b }
            r0 = r0.dcDomainName;	 Catch:{ all -> 0x012b }
        L_0x0018:
            r1 = org.telegram.messenger.Utilities.random;	 Catch:{ all -> 0x012b }
            r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
            r1 = r1.nextInt(r2);	 Catch:{ all -> 0x012b }
            r1 = r1 + 13;
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x012b }
            r2.<init>(r1);	 Catch:{ all -> 0x012b }
            r3 = 0;
            r4 = 0;
        L_0x0029:
            if (r4 >= r1) goto L_0x003f;
        L_0x002b:
            r5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM";
            r6 = org.telegram.messenger.Utilities.random;	 Catch:{ all -> 0x012b }
            r7 = 62;
            r6 = r6.nextInt(r7);	 Catch:{ all -> 0x012b }
            r5 = r5.charAt(r6);	 Catch:{ all -> 0x012b }
            r2.append(r5);	 Catch:{ all -> 0x012b }
            r4 = r4 + 1;
            goto L_0x0029;
        L_0x003f:
            r1 = new java.net.URL;	 Catch:{ all -> 0x012b }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x012b }
            r4.<init>();	 Catch:{ all -> 0x012b }
            r5 = "https://mozilla.cloudflare-dns.com/dns-query?name=";
            r4.append(r5);	 Catch:{ all -> 0x012b }
            r4.append(r0);	 Catch:{ all -> 0x012b }
            r0 = "&type=16&random_padding=";
            r4.append(r0);	 Catch:{ all -> 0x012b }
            r4.append(r2);	 Catch:{ all -> 0x012b }
            r0 = r4.toString();	 Catch:{ all -> 0x012b }
            r1.<init>(r0);	 Catch:{ all -> 0x012b }
            r0 = r1.openConnection();	 Catch:{ all -> 0x012b }
            r1 = "User-Agent";
            r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r0.addRequestProperty(r1, r2);	 Catch:{ all -> 0x012b }
            r1 = "accept";
            r2 = "application/dns-json";
            r0.addRequestProperty(r1, r2);	 Catch:{ all -> 0x012b }
            r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r0.setConnectTimeout(r1);	 Catch:{ all -> 0x012b }
            r0.setReadTimeout(r1);	 Catch:{ all -> 0x012b }
            r0.connect();	 Catch:{ all -> 0x012b }
            r1 = r0.getInputStream();	 Catch:{ all -> 0x012b }
            r4 = r0.getDate();	 Catch:{ all -> 0x0128 }
            r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r4 = r4 / r6;
            r0 = (int) r4;	 Catch:{ all -> 0x0128 }
            r11.responseDate = r0;	 Catch:{ all -> 0x0128 }
            r0 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x0128 }
            r0.<init>();	 Catch:{ all -> 0x0128 }
            r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r2 = new byte[r2];	 Catch:{ all -> 0x0123 }
        L_0x0092:
            r4 = r11.isCancelled();	 Catch:{ all -> 0x0123 }
            if (r4 == 0) goto L_0x0099;
        L_0x0098:
            goto L_0x00a4;
        L_0x0099:
            r4 = r1.read(r2);	 Catch:{ all -> 0x0123 }
            if (r4 <= 0) goto L_0x00a3;
        L_0x009f:
            r0.write(r2, r3, r4);	 Catch:{ all -> 0x0123 }
            goto L_0x0092;
        L_0x00a3:
            r2 = -1;
        L_0x00a4:
            r2 = new org.json.JSONObject;	 Catch:{ all -> 0x0123 }
            r4 = new java.lang.String;	 Catch:{ all -> 0x0123 }
            r5 = r0.toByteArray();	 Catch:{ all -> 0x0123 }
            r4.<init>(r5);	 Catch:{ all -> 0x0123 }
            r2.<init>(r4);	 Catch:{ all -> 0x0123 }
            r4 = "Answer";
            r2 = r2.getJSONArray(r4);	 Catch:{ all -> 0x0123 }
            r4 = r2.length();	 Catch:{ all -> 0x0123 }
            r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0123 }
            r5.<init>(r4);	 Catch:{ all -> 0x0123 }
            r6 = 0;
        L_0x00c2:
            if (r6 >= r4) goto L_0x00df;
        L_0x00c4:
            r7 = r2.getJSONObject(r6);	 Catch:{ all -> 0x0123 }
            r8 = "type";
            r8 = r7.getInt(r8);	 Catch:{ all -> 0x0123 }
            r9 = 16;
            if (r8 == r9) goto L_0x00d3;
        L_0x00d2:
            goto L_0x00dc;
        L_0x00d3:
            r8 = "data";
            r7 = r7.getString(r8);	 Catch:{ all -> 0x0123 }
            r5.add(r7);	 Catch:{ all -> 0x0123 }
        L_0x00dc:
            r6 = r6 + 1;
            goto L_0x00c2;
        L_0x00df:
            r2 = org.telegram.tgnet.-$$Lambda$ConnectionsManager$MozillaDnsLoadTask$ef_f-SQUiYt6RD6fDvDkND7eCLASSNAME.INSTANCE;	 Catch:{ all -> 0x0123 }
            java.util.Collections.sort(r5, r2);	 Catch:{ all -> 0x0123 }
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0123 }
            r2.<init>();	 Catch:{ all -> 0x0123 }
            r4 = 0;
        L_0x00ea:
            r6 = r5.size();	 Catch:{ all -> 0x0123 }
            if (r4 >= r6) goto L_0x0104;
        L_0x00f0:
            r6 = r5.get(r4);	 Catch:{ all -> 0x0123 }
            r6 = (java.lang.String) r6;	 Catch:{ all -> 0x0123 }
            r7 = "\"";
            r8 = "";
            r6 = r6.replace(r7, r8);	 Catch:{ all -> 0x0123 }
            r2.append(r6);	 Catch:{ all -> 0x0123 }
            r4 = r4 + 1;
            goto L_0x00ea;
        L_0x0104:
            r2 = r2.toString();	 Catch:{ all -> 0x0123 }
            r2 = android.util.Base64.decode(r2, r3);	 Catch:{ all -> 0x0123 }
            r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ all -> 0x0123 }
            r4 = r2.length;	 Catch:{ all -> 0x0123 }
            r3.<init>(r4);	 Catch:{ all -> 0x0123 }
            r3.writeBytes(r2);	 Catch:{ all -> 0x0123 }
            if (r1 == 0) goto L_0x011f;
        L_0x0117:
            r1.close();	 Catch:{ all -> 0x011b }
            goto L_0x011f;
        L_0x011b:
            r12 = move-exception;
            org.telegram.messenger.FileLog.e(r12);
        L_0x011f:
            r0.close();	 Catch:{ Exception -> 0x0122 }
        L_0x0122:
            return r3;
        L_0x0123:
            r2 = move-exception;
            r10 = r2;
            r2 = r0;
            r0 = r10;
            goto L_0x012e;
        L_0x0128:
            r0 = move-exception;
            r2 = r12;
            goto L_0x012e;
        L_0x012b:
            r0 = move-exception;
            r1 = r12;
            r2 = r1;
        L_0x012e:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0141 }
            if (r1 == 0) goto L_0x013b;
        L_0x0133:
            r1.close();	 Catch:{ all -> 0x0137 }
            goto L_0x013b;
        L_0x0137:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x013b:
            if (r2 == 0) goto L_0x0140;
        L_0x013d:
            r2.close();	 Catch:{ Exception -> 0x0140 }
        L_0x0140:
            return r12;
        L_0x0141:
            r12 = move-exception;
            if (r1 == 0) goto L_0x014c;
        L_0x0144:
            r1.close();	 Catch:{ all -> 0x0148 }
            goto L_0x014c;
        L_0x0148:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x014c:
            if (r2 == 0) goto L_0x0151;
        L_0x014e:
            r2.close();	 Catch:{ Exception -> 0x0151 }
        L_0x0151:
            goto L_0x0153;
        L_0x0152:
            throw r12;
        L_0x0153:
            goto L_0x0152;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager$MozillaDnsLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$MozillaDnsLoadTask$_t5C-W7FV9GIIL0quYTnAJF2h8Y(this, nativeByteBuffer));
        }

        public /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$MozillaDnsLoadTask(NativeByteBuffer nativeByteBuffer) {
            ConnectionsManager.currentTask = null;
            if (nativeByteBuffer != null) {
                int i = this.currentAccount;
                ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), this.responseDate);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get mozilla txt result");
            }
        }
    }

    private static class ResolveHostByNameTask extends AsyncTask<Void, Void, ResolvedDomain> {
        private ArrayList<Long> addresses = new ArrayList();
        private String currentHostName;

        public ResolveHostByNameTask(String str) {
            this.currentHostName = str;
        }

        public void addAddress(long j) {
            if (!this.addresses.contains(Long.valueOf(j))) {
                this.addresses.add(Long.valueOf(j));
            }
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00d4 A:{SYNTHETIC, Splitter:B:54:0x00d4} */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x00c3 A:{SYNTHETIC, Splitter:B:45:0x00c3} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00cd A:{SYNTHETIC, Splitter:B:50:0x00cd} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00d4 A:{SYNTHETIC, Splitter:B:54:0x00d4} */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x00c3 A:{SYNTHETIC, Splitter:B:45:0x00c3} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00cd A:{SYNTHETIC, Splitter:B:50:0x00cd} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00d4 A:{SYNTHETIC, Splitter:B:54:0x00d4} */
        public org.telegram.tgnet.ConnectionsManager.ResolvedDomain doInBackground(java.lang.Void... r11) {
            /*
            r10 = this;
            r11 = "Answer";
            r0 = 1;
            r1 = 0;
            r2 = 0;
            r3 = new java.net.URL;	 Catch:{ all -> 0x00bb }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00bb }
            r4.<init>();	 Catch:{ all -> 0x00bb }
            r5 = "https://www.google.com/resolve?name=";
            r4.append(r5);	 Catch:{ all -> 0x00bb }
            r5 = r10.currentHostName;	 Catch:{ all -> 0x00bb }
            r4.append(r5);	 Catch:{ all -> 0x00bb }
            r5 = "&type=A";
            r4.append(r5);	 Catch:{ all -> 0x00bb }
            r4 = r4.toString();	 Catch:{ all -> 0x00bb }
            r3.<init>(r4);	 Catch:{ all -> 0x00bb }
            r3 = r3.openConnection();	 Catch:{ all -> 0x00bb }
            r4 = "User-Agent";
            r5 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r3.addRequestProperty(r4, r5);	 Catch:{ all -> 0x00bb }
            r4 = "Host";
            r5 = "dns.google.com";
            r3.addRequestProperty(r4, r5);	 Catch:{ all -> 0x00bb }
            r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r3.setConnectTimeout(r4);	 Catch:{ all -> 0x00bb }
            r4 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
            r3.setReadTimeout(r4);	 Catch:{ all -> 0x00bb }
            r3.connect();	 Catch:{ all -> 0x00bb }
            r3 = r3.getInputStream();	 Catch:{ all -> 0x00bb }
            r4 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x00b8 }
            r4.<init>();	 Catch:{ all -> 0x00b8 }
            r5 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r5 = new byte[r5];	 Catch:{ all -> 0x00b6 }
        L_0x004f:
            r6 = r3.read(r5);	 Catch:{ all -> 0x00b6 }
            if (r6 <= 0) goto L_0x0059;
        L_0x0055:
            r4.write(r5, r1, r6);	 Catch:{ all -> 0x00b6 }
            goto L_0x004f;
        L_0x0059:
            r5 = -1;
            r5 = new org.json.JSONObject;	 Catch:{ all -> 0x00b6 }
            r6 = new java.lang.String;	 Catch:{ all -> 0x00b6 }
            r7 = r4.toByteArray();	 Catch:{ all -> 0x00b6 }
            r6.<init>(r7);	 Catch:{ all -> 0x00b6 }
            r5.<init>(r6);	 Catch:{ all -> 0x00b6 }
            r6 = r5.has(r11);	 Catch:{ all -> 0x00b6 }
            if (r6 == 0) goto L_0x00a7;
        L_0x006e:
            r11 = r5.getJSONArray(r11);	 Catch:{ all -> 0x00b6 }
            r5 = r11.length();	 Catch:{ all -> 0x00b6 }
            if (r5 <= 0) goto L_0x00a7;
        L_0x0078:
            r6 = new java.util.ArrayList;	 Catch:{ all -> 0x00b6 }
            r6.<init>(r5);	 Catch:{ all -> 0x00b6 }
            r7 = 0;
        L_0x007e:
            if (r7 >= r5) goto L_0x0090;
        L_0x0080:
            r8 = r11.getJSONObject(r7);	 Catch:{ all -> 0x00b6 }
            r9 = "data";
            r8 = r8.getString(r9);	 Catch:{ all -> 0x00b6 }
            r6.add(r8);	 Catch:{ all -> 0x00b6 }
            r7 = r7 + 1;
            goto L_0x007e;
        L_0x0090:
            r11 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain;	 Catch:{ all -> 0x00b6 }
            r7 = android.os.SystemClock.elapsedRealtime();	 Catch:{ all -> 0x00b6 }
            r11.<init>(r6, r7);	 Catch:{ all -> 0x00b6 }
            if (r3 == 0) goto L_0x00a3;
        L_0x009b:
            r3.close();	 Catch:{ all -> 0x009f }
            goto L_0x00a3;
        L_0x009f:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00a3:
            r4.close();	 Catch:{ Exception -> 0x00a6 }
        L_0x00a6:
            return r11;
        L_0x00a7:
            if (r3 == 0) goto L_0x00b1;
        L_0x00a9:
            r3.close();	 Catch:{ all -> 0x00ad }
            goto L_0x00b1;
        L_0x00ad:
            r11 = move-exception;
            org.telegram.messenger.FileLog.e(r11);
        L_0x00b1:
            r4.close();	 Catch:{ Exception -> 0x00b4 }
        L_0x00b4:
            r1 = 1;
            goto L_0x00d2;
        L_0x00b6:
            r11 = move-exception;
            goto L_0x00be;
        L_0x00b8:
            r11 = move-exception;
            r4 = r2;
            goto L_0x00be;
        L_0x00bb:
            r11 = move-exception;
            r3 = r2;
            r4 = r3;
        L_0x00be:
            org.telegram.messenger.FileLog.e(r11);	 Catch:{ all -> 0x00f5 }
            if (r3 == 0) goto L_0x00cb;
        L_0x00c3:
            r3.close();	 Catch:{ all -> 0x00c7 }
            goto L_0x00cb;
        L_0x00c7:
            r11 = move-exception;
            org.telegram.messenger.FileLog.e(r11);
        L_0x00cb:
            if (r4 == 0) goto L_0x00d2;
        L_0x00cd:
            r4.close();	 Catch:{ Exception -> 0x00d1 }
            goto L_0x00d2;
        L_0x00d2:
            if (r1 != 0) goto L_0x00f4;
        L_0x00d4:
            r11 = r10.currentHostName;	 Catch:{ Exception -> 0x00f0 }
            r11 = java.net.InetAddress.getByName(r11);	 Catch:{ Exception -> 0x00f0 }
            r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00f0 }
            r1.<init>(r0);	 Catch:{ Exception -> 0x00f0 }
            r11 = r11.getHostAddress();	 Catch:{ Exception -> 0x00f0 }
            r1.add(r11);	 Catch:{ Exception -> 0x00f0 }
            r11 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain;	 Catch:{ Exception -> 0x00f0 }
            r3 = android.os.SystemClock.elapsedRealtime();	 Catch:{ Exception -> 0x00f0 }
            r11.<init>(r1, r3);	 Catch:{ Exception -> 0x00f0 }
            return r11;
        L_0x00f0:
            r11 = move-exception;
            org.telegram.messenger.FileLog.e(r11);
        L_0x00f4:
            return r2;
        L_0x00f5:
            r11 = move-exception;
            if (r3 == 0) goto L_0x0100;
        L_0x00f8:
            r3.close();	 Catch:{ all -> 0x00fc }
            goto L_0x0100;
        L_0x00fc:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0100:
            if (r4 == 0) goto L_0x0105;
        L_0x0102:
            r4.close();	 Catch:{ Exception -> 0x0105 }
        L_0x0105:
            goto L_0x0107;
        L_0x0106:
            throw r11;
        L_0x0107:
            goto L_0x0106;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager$ResolveHostByNameTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.ConnectionsManager$ResolvedDomain");
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(ResolvedDomain resolvedDomain) {
            int i = 0;
            if (resolvedDomain != null) {
                ConnectionsManager.dnsCache.put(this.currentHostName, resolvedDomain);
                int size = this.addresses.size();
                while (i < size) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, ((Long) this.addresses.get(i)).longValue(), resolvedDomain.getAddress());
                    i++;
                }
            } else {
                int size2 = this.addresses.size();
                while (i < size2) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, ((Long) this.addresses.get(i)).longValue(), "");
                    i++;
                }
            }
            ConnectionsManager.resolvingHostnameTasks.remove(this.currentHostName);
        }
    }

    private static class ResolvedDomain {
        public ArrayList<String> addresses;
        long ttl;

        public ResolvedDomain(ArrayList<String> arrayList, long j) {
            this.addresses = arrayList;
            this.ttl = j;
        }

        public String getAddress() {
            ArrayList arrayList = this.addresses;
            return (String) arrayList.get(Utilities.random.nextInt(arrayList.size()));
        }
    }

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j, String str, int i2);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native long native_checkProxy(int i, String str, int i2, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate);

    public static native void native_cleanUp(int i, boolean z);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i5, boolean z, boolean z2, int i6);

    public static native int native_isTestBackend(int i);

    public static native void native_onHostNameResolved(String str, long j, String str2);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_seSystemLangCode(int i, String str);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2, boolean z2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3, String str4);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setRegId(int i, String str);

    public static native void native_setSystemLangCode(int i, String str);

    public static native void native_setUseIpv6(int i, boolean z);

    public static native void native_setUserId(int i, int i2);

    public static native void native_switchBackend(int i);

    public static native void native_updateDcSettings(int i);

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        DNS_THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public static ConnectionsManager getInstance(int i) {
        ConnectionsManager connectionsManager = Instance[i];
        if (connectionsManager == null) {
            synchronized (ConnectionsManager.class) {
                connectionsManager = Instance[i];
                if (connectionsManager == null) {
                    ConnectionsManager[] connectionsManagerArr = Instance;
                    ConnectionsManager connectionsManager2 = new ConnectionsManager(i);
                    connectionsManagerArr[i] = connectionsManager2;
                    connectionsManager = connectionsManager2;
                }
            }
        }
        return connectionsManager;
    }

    public ConnectionsManager(int i) {
        String toLowerCase;
        String toLowerCase2;
        String stringBuilder;
        String stringBuilder2;
        String str;
        int i2 = i;
        String str2 = "SDK ";
        String str3 = "App version unknown";
        String str4 = "Android unknown";
        String str5 = "en";
        super(i);
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (i2 != 0) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("account");
            stringBuilder3.append(i2);
            File file = new File(filesDirFixed, stringBuilder3.toString());
            file.mkdirs();
            filesDirFixed = file;
        }
        String file2 = filesDirFixed.toString();
        boolean z = MessagesController.getGlobalNotificationsSettings().getBoolean("pushConnection", true);
        try {
            toLowerCase = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            toLowerCase2 = LocaleController.getLocaleStringIso639().toLowerCase();
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append(Build.MANUFACTURER);
            stringBuilder4.append(Build.MODEL);
            stringBuilder = stringBuilder4.toString();
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            StringBuilder stringBuilder5 = new StringBuilder();
            stringBuilder5.append(packageInfo.versionName);
            stringBuilder5.append(" (");
            stringBuilder5.append(packageInfo.versionCode);
            stringBuilder5.append(")");
            stringBuilder2 = stringBuilder5.toString();
            stringBuilder5 = new StringBuilder();
            stringBuilder5.append(str2);
            stringBuilder5.append(VERSION.SDK_INT);
            str2 = stringBuilder5.toString();
            str = toLowerCase2;
        } catch (Exception unused) {
            StringBuilder stringBuilder6 = new StringBuilder();
            stringBuilder6.append(str2);
            stringBuilder6.append(VERSION.SDK_INT);
            str2 = stringBuilder6.toString();
            str = "";
            stringBuilder2 = str3;
            stringBuilder = str4;
            toLowerCase = str5;
        }
        String str6 = toLowerCase.trim().length() == 0 ? str5 : toLowerCase;
        str5 = stringBuilder.trim().length() == 0 ? str4 : stringBuilder;
        stringBuilder = stringBuilder2.trim().length() == 0 ? str3 : stringBuilder2;
        toLowerCase2 = str2.trim().length() == 0 ? "SDK Unknown" : str2;
        getUserConfig().loadConfig();
        toLowerCase = SharedConfig.pushString;
        if (TextUtils.isEmpty(toLowerCase) && !TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            toLowerCase = SharedConfig.pushStringStatus;
        }
        init(BuildVars.BUILD_VERSION, 106, BuildVars.APP_ID, str5, toLowerCase2, stringBuilder, str, str6, file2, FileLog.getNetworkLogPath(), toLowerCase, getUserConfig().getClientUserId(), z);
        i2 = this.currentAccount;
        if (i2 == 0 && BuildVars.DEBUG_PRIVATE_VERSION) {
            new MozillaDnsLoadTask(i2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
        }
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis(this.currentAccount);
    }

    public int getCurrentTime() {
        return native_getCurrentTime(this.currentAccount);
    }

    public int getTimeDifference() {
        return native_getTimeDifference(this.currentAccount);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate) {
        return sendRequest(tLObject, requestDelegate, null, 0);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, null, null, i, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i, int i2) {
        return sendRequest(tLObject, requestDelegate, null, null, i, Integer.MAX_VALUE, i2, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, quickAckDelegate, null, i, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i, int i2, int i3, boolean z) {
        int andIncrement = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$csmhNL7gP4ZbIN5-kTApilG8kBQ(this, tLObject, andIncrement, requestDelegate, quickAckDelegate, writeToSocketDelegate, i, i2, i3, z));
        return andIncrement;
    }

    public /* synthetic */ void lambda$sendRequest$2$ConnectionsManager(TLObject tLObject, int i, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z) {
        Throwable e;
        TLObject tLObject2 = tLObject;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("send request ");
            stringBuilder.append(tLObject2);
            stringBuilder.append(" with token = ");
            stringBuilder.append(i);
            FileLog.d(stringBuilder.toString());
        } else {
            int i5 = i;
        }
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject2.serializeToStream(nativeByteBuffer);
            tLObject.freeResources();
            try {
                native_sendRequest(this.currentAccount, nativeByteBuffer.address, new -$$Lambda$ConnectionsManager$qOa5d09BI1fBg0o1upbl6aXFnSY(tLObject2, requestDelegate), quickAckDelegate, writeToSocketDelegate, i2, i3, i4, z, i);
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
        }
    }

    static /* synthetic */ void lambda$null$1(TLObject tLObject, RequestDelegate requestDelegate, long j, int i, String str, int i2) {
        Object obj;
        TLObject tLObject2 = null;
        if (j != 0) {
            try {
                NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
                wrap.reused = true;
                tLObject = tLObject.deserializeResponse(wrap, wrap.readInt32(true), true);
                obj = null;
                tLObject2 = tLObject;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        } else if (str != null) {
            obj = new TL_error();
            obj.code = i;
            obj.text = str;
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(tLObject);
                stringBuilder.append(" got error ");
                stringBuilder.append(obj.code);
                stringBuilder.append(" ");
                stringBuilder.append(obj.text);
                FileLog.e(stringBuilder.toString());
            }
        } else {
            obj = null;
        }
        if (tLObject2 != null) {
            tLObject2.networkType = i2;
        }
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("java received ");
            stringBuilder2.append(tLObject2);
            stringBuilder2.append(" error = ");
            stringBuilder2.append(obj);
            FileLog.d(stringBuilder2.toString());
        }
        Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$N1Ud38cKJRQ_sqp20Fcot5ApM0Y(requestDelegate, tLObject2, obj));
    }

    static /* synthetic */ void lambda$null$0(RequestDelegate requestDelegate, TLObject tLObject, TL_error tL_error) {
        requestDelegate.run(tLObject, tL_error);
        if (tLObject != null) {
            tLObject.freeResources();
        }
    }

    public void cancelRequest(int i, boolean z) {
        native_cancelRequest(this.currentAccount, i, z);
    }

    public void cleanup(boolean z) {
        native_cleanUp(this.currentAccount, z);
    }

    public void cancelRequestsForGuid(int i) {
        native_cancelRequestsForGuid(this.currentAccount, i);
    }

    public void bindRequestToGuid(int i, int i2) {
        native_bindRequestToGuid(this.currentAccount, i, i2);
    }

    public void applyDatacenterAddress(int i, String str, int i2) {
        native_applyDatacenterAddress(this.currentAccount, i, str, i2);
    }

    public int getConnectionState() {
        if (this.connectionState == 3 && this.isUpdating) {
            return 5;
        }
        return this.connectionState;
    }

    public void setUserId(int i) {
        native_setUserId(this.currentAccount, i);
    }

    public void checkConnection() {
        native_setUseIpv6(this.currentAccount, useIpv6Address());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean z) {
        native_setPushConnectionEnabled(this.currentAccount, z);
    }

    public void init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, int i4, boolean z) {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String str9 = "";
        String string = sharedPreferences.getString("proxy_ip", str9);
        String string2 = sharedPreferences.getString("proxy_user", str9);
        String string3 = sharedPreferences.getString("proxy_pass", str9);
        String string4 = sharedPreferences.getString("proxy_secret", str9);
        int i5 = sharedPreferences.getInt("proxy_port", 1080);
        if (sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(string)) {
            native_setProxySettings(this.currentAccount, string, i5, string2, string3, string4);
        }
        native_init(this.currentAccount, i, i2, i3, str, str2, str3, str4, str5, str6, str7, str8, i4, z, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
        checkConnection();
    }

    public static void setLangCode(String str) {
        str = str.replace('_', '-').toLowerCase();
        for (int i = 0; i < 3; i++) {
            native_setLangCode(i, str);
        }
    }

    public static void setRegId(String str, String str2) {
        if (TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            str = str2;
        }
        for (int i = 0; i < 3; i++) {
            native_setRegId(i, str);
        }
    }

    public static void setSystemLangCode(String str) {
        str = str.replace('_', '-').toLowerCase();
        for (int i = 0; i < 3; i++) {
            native_setSystemLangCode(i, str);
        }
    }

    public void switchBackend() {
        MessagesController.getGlobalMainSettings().edit().remove("language_showed2").commit();
        native_switchBackend(this.currentAccount);
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(this.currentAccount, true);
    }

    public void updateDcSettings() {
        native_updateDcSettings(this.currentAccount);
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public long checkProxy(String str, int i, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        String str5 = "";
        return native_checkProxy(this.currentAccount, str == null ? str5 : str, i, str2 == null ? str5 : str2, str3 == null ? str5 : str3, str4 == null ? str5 : str4, requestTimeDelegate);
    }

    public void setAppPaused(boolean z, boolean z2) {
        if (!z2) {
            this.appPaused = z;
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("app paused = ");
                stringBuilder.append(z);
                FileLog.d(stringBuilder.toString());
            }
            if (z) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("app resume count ");
                stringBuilder2.append(this.appResumeCount);
                FileLog.d(stringBuilder2.toString());
            }
            if (this.appResumeCount < 0) {
                this.appResumeCount = 0;
            }
        }
        if (this.appResumeCount == 0) {
            if (this.lastPauseTime == 0) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork(this.currentAccount);
        } else if (!this.appPaused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset app pause time");
            }
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > 5000) {
                getContactsController().checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long j, int i) {
        try {
            NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
            wrap.reused = true;
            TLObject TLdeserialize = TLClassStore.Instance().TLdeserialize(wrap, wrap.readInt32(true), true);
            if (TLdeserialize instanceof Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("java received ");
                    stringBuilder.append(TLdeserialize);
                    FileLog.d(stringBuilder.toString());
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$R5V1iXmwj8PWON-tb_jcTaBhzJo(i, TLdeserialize));
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", new Object[]{Integer.valueOf(r0)}));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onUpdate(int i) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$GiXNWNTneL61N5XH1sI4IVkif4k(i));
    }

    public static void onSessionCreated(int i) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$c-kbk6lmCmsTzziA11WOyMsJtqY(i));
    }

    public static void onConnectionStateChanged(int i, int i2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ConnectionsManager$wMpd1-zDWgiLp6x8fjImjIX349A(i2, i));
    }

    static /* synthetic */ void lambda$onConnectionStateChanged$6(int i, int i2) {
        getInstance(i).connectionState = i2;
        AccountInstance.getInstance(i).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ConnectionsManager$WUMuAbrjCLASSNAMEkWFnK3JYXcq2eB4E(i));
    }

    static /* synthetic */ void lambda$onLogout$7(int i) {
        AccountInstance instance = AccountInstance.getInstance(i);
        if (instance.getUserConfig().getClientUserId() != 0) {
            instance.getUserConfig().clearConfig();
            instance.getMessagesController().performLogout(0);
        }
    }

    public static int getInitFlags() {
        return EmuDetector.with(ApplicationLoader.applicationContext).detect() ? 1024 : 0;
    }

    public static void onBytesSent(int i, int i2, int i3) {
        try {
            AccountInstance.getInstance(i3).getStatsController().incrementSentBytesCount(i2, 6, (long) i);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(int i, int i2) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$Vntp1UzbcZLxSUJY5_RFJvLOrY4(i, i2));
    }

    static /* synthetic */ void lambda$onRequestNewServerIpAndPort$8(int i, int i2) {
        if (currentTask != null || ((i == 0 && Math.abs(lastDnsRequestTime - System.currentTimeMillis()) < 10000) || !ApplicationLoader.isNetworkOnline())) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("don't start task, current task = ");
                stringBuilder.append(currentTask);
                stringBuilder.append(" next task = ");
                stringBuilder.append(i);
                stringBuilder.append(" time diff = ");
                stringBuilder.append(Math.abs(lastDnsRequestTime - System.currentTimeMillis()));
                stringBuilder.append(" network = ");
                stringBuilder.append(ApplicationLoader.isNetworkOnline());
                FileLog.d(stringBuilder.toString());
            }
            return;
        }
        lastDnsRequestTime = System.currentTimeMillis();
        if (i == 3) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start mozilla txt task");
            }
            MozillaDnsLoadTask mozillaDnsLoadTask = new MozillaDnsLoadTask(i2);
            mozillaDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            currentTask = mozillaDnsLoadTask;
        } else if (i == 2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start google txt task");
            }
            GoogleDnsLoadTask googleDnsLoadTask = new GoogleDnsLoadTask(i2);
            googleDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            currentTask = googleDnsLoadTask;
        } else if (i == 1) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask dnsTxtLoadTask = new DnsTxtLoadTask(i2);
            dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            currentTask = dnsTxtLoadTask;
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start firebase task");
            }
            FirebaseTask firebaseTask = new FirebaseTask(i2);
            firebaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            currentTask = firebaseTask;
        }
    }

    public static void onProxyError() {
        AndroidUtilities.runOnUIThread(-$$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU.INSTANCE);
    }

    public static void getHostByName(String str, long j) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ConnectionsManager$UeSwrOtvar_uU_P2dN0wFOiKOlIA(str, j));
    }

    static /* synthetic */ void lambda$getHostByName$10(String str, long j) {
        ResolvedDomain resolvedDomain = (ResolvedDomain) dnsCache.get(str);
        if (resolvedDomain == null || SystemClock.elapsedRealtime() - resolvedDomain.ttl >= 300000) {
            ResolveHostByNameTask resolveHostByNameTask = (ResolveHostByNameTask) resolvingHostnameTasks.get(str);
            if (resolveHostByNameTask == null) {
                resolveHostByNameTask = new ResolveHostByNameTask(str);
                try {
                    resolveHostByNameTask.executeOnExecutor(DNS_THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    resolvingHostnameTasks.put(str, resolveHostByNameTask);
                } catch (Throwable th) {
                    FileLog.e(th);
                    native_onHostNameResolved(str, j, "");
                    return;
                }
            }
            resolveHostByNameTask.addAddress(j);
        } else {
            native_onHostNameResolved(str, j, resolvedDomain.getAddress());
        }
    }

    public static void onBytesReceived(int i, int i2, int i3) {
        try {
            StatsController.getInstance(i3).incrementReceivedBytesCount(i2, 6, (long) i);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onUpdateConfig(long j, int i) {
        try {
            NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
            wrap.reused = true;
            TL_config TLdeserialize = TL_config.TLdeserialize(wrap, wrap.readInt32(true), true);
            if (TLdeserialize != null) {
                Utilities.stageQueue.postRunnable(new -$$Lambda$ConnectionsManager$wiVMBnjTO-Ju65Uh4sMTb_loS8A(i, TLdeserialize));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onInternalPushReceived(int i) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean z, String str, int i, String str2, String str3, String str4) {
        CharSequence str5;
        String str6 = "";
        if (str5 == null) {
            str5 = str6;
        }
        if (str2 == null) {
            str2 = str6;
        }
        if (str3 == null) {
            str3 = str6;
        }
        if (str4 == null) {
            str4 = str6;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            if (!z || TextUtils.isEmpty(str5)) {
                native_setProxySettings(i2, "", 1080, "", "", "");
            } else {
                native_setProxySettings(i2, str5, i, str2, str3, str4);
            }
            AccountInstance instance = AccountInstance.getInstance(i2);
            if (instance.getUserConfig().isClientActivated()) {
                instance.getMessagesController().checkProxyInfo(true);
            }
        }
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public void setIsUpdating(boolean z) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ConnectionsManager$YsEmHm13cAxaF3h53HNgd07NbfA(this, z));
    }

    public /* synthetic */ void lambda$setIsUpdating$12$ConnectionsManager(boolean z) {
        if (this.isUpdating != z) {
            this.isUpdating = z;
            if (this.connectionState == 3) {
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
            }
        }
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (VERSION.SDK_INT < 19) {
            return false;
        }
        Enumeration networkInterfaces;
        if (BuildVars.LOGS_ENABLED) {
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("valid interface: ");
                                stringBuilder.append(networkInterface);
                                FileLog.d(stringBuilder.toString());
                            }
                            List interfaceAddresses = networkInterface.getInterfaceAddresses();
                            for (int i = 0; i < interfaceAddresses.size(); i++) {
                                InetAddress address = ((InterfaceAddress) interfaceAddresses.get(i)).getAddress();
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("address: ");
                                    stringBuilder2.append(address.getHostAddress());
                                    FileLog.d(stringBuilder2.toString());
                                }
                                if (!(address.isLinkLocalAddress() || address.isLoopbackAddress())) {
                                    if (!address.isMulticastAddress()) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("address is good");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Object obj = null;
            Object obj2 = null;
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface2 = (NetworkInterface) networkInterfaces.nextElement();
                if (networkInterface2.isUp()) {
                    if (!networkInterface2.isLoopback()) {
                        List interfaceAddresses2 = networkInterface2.getInterfaceAddresses();
                        Object obj3 = obj2;
                        obj2 = obj;
                        for (int i2 = 0; i2 < interfaceAddresses2.size(); i2++) {
                            InetAddress address2 = ((InterfaceAddress) interfaceAddresses2.get(i2)).getAddress();
                            if (!(address2.isLinkLocalAddress() || address2.isLoopbackAddress())) {
                                if (!address2.isMulticastAddress()) {
                                    if (address2 instanceof Inet6Address) {
                                        obj3 = 1;
                                    } else if ((address2 instanceof Inet4Address) && !address2.getHostAddress().startsWith("192.0.0.")) {
                                        obj2 = 1;
                                    }
                                }
                            }
                        }
                        obj = obj2;
                        obj2 = obj3;
                    }
                }
            }
            if (obj != null || obj2 == null) {
                return false;
            }
            return true;
        } catch (Throwable th2) {
            FileLog.e(th2);
        }
    }
}
