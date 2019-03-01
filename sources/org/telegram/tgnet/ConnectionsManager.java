package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;

public class ConnectionsManager {
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
    public static final int FileTypeAudio = 50331648;
    public static final int FileTypeFile = 67108864;
    public static final int FileTypePhoto = 16777216;
    public static final int FileTypeVideo = 33554432;
    private static volatile ConnectionsManager[] Instance = new ConnectionsManager[3];
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    private static AsyncTask currentTask;
    private static ThreadLocal<HashMap<String, ResolvedDomain>> dnsCache = new ThreadLocal<HashMap<String, ResolvedDomain>>() {
        protected HashMap<String, ResolvedDomain> initialValue() {
            return new HashMap();
        }
    };
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState;
    private int currentAccount;
    private boolean isUpdating;
    private long lastPauseTime = System.currentTimeMillis();
    private AtomicInteger lastRequestToken = new AtomicInteger(1);

    private static class AzureLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;

        public AzureLoadTask(int instance) {
            this.currentAccount = instance;
        }

        /* JADX WARNING: Removed duplicated region for block: B:45:0x009d A:{SYNTHETIC, Splitter: B:45:0x009d} */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00a2 A:{SYNTHETIC, Splitter: B:48:0x00a2} */
        protected org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r13) {
            /*
            r12 = this;
            r7 = 0;
            r6 = 0;
            r10 = r12.currentAccount;	 Catch:{ Throwable -> 0x00b4 }
            r10 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r10);	 Catch:{ Throwable -> 0x00b4 }
            if (r10 == 0) goto L_0x0067;
        L_0x000a:
            r3 = new java.net.URL;	 Catch:{ Throwable -> 0x00b4 }
            r10 = "https://software-download.microsoft.com/testv2/config.txt";
            r3.<init>(r10);	 Catch:{ Throwable -> 0x00b4 }
        L_0x0012:
            r5 = r3.openConnection();	 Catch:{ Throwable -> 0x00b4 }
            r10 = "User-Agent";
            r11 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r5.addRequestProperty(r10, r11);	 Catch:{ Throwable -> 0x00b4 }
            r10 = "Host";
            r11 = "tcdnb.azureedge.net";
            r5.addRequestProperty(r10, r11);	 Catch:{ Throwable -> 0x00b4 }
            r10 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r5.setConnectTimeout(r10);	 Catch:{ Throwable -> 0x00b4 }
            r10 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r5.setReadTimeout(r10);	 Catch:{ Throwable -> 0x00b4 }
            r5.connect();	 Catch:{ Throwable -> 0x00b4 }
            r6 = r5.getInputStream();	 Catch:{ Throwable -> 0x00b4 }
            r8 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x00b4 }
            r8.<init>();	 Catch:{ Throwable -> 0x00b4 }
            r10 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r2 = new byte[r10];	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
        L_0x0043:
            r10 = r12.isCancelled();	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            if (r10 == 0) goto L_0x0070;
        L_0x0049:
            r10 = r8.toByteArray();	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            r11 = 0;
            r1 = android.util.Base64.decode(r10, r11);	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            r0 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            r10 = r1.length;	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            r0.<init>(r10);	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            r0.writeBytes(r1);	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            if (r6 == 0) goto L_0x0060;
        L_0x005d:
            r6.close();	 Catch:{ Throwable -> 0x0090 }
        L_0x0060:
            if (r8 == 0) goto L_0x0065;
        L_0x0062:
            r8.close();	 Catch:{ Exception -> 0x00ab }
        L_0x0065:
            r7 = r8;
        L_0x0066:
            return r0;
        L_0x0067:
            r3 = new java.net.URL;	 Catch:{ Throwable -> 0x00b4 }
            r10 = "https://software-download.microsoft.com/prodv2/config.txt";
            r3.<init>(r10);	 Catch:{ Throwable -> 0x00b4 }
            goto L_0x0012;
        L_0x0070:
            r9 = r6.read(r2);	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            if (r9 <= 0) goto L_0x008c;
        L_0x0076:
            r10 = 0;
            r8.write(r2, r10, r9);	 Catch:{ Throwable -> 0x007b, all -> 0x00b1 }
            goto L_0x0043;
        L_0x007b:
            r4 = move-exception;
            r7 = r8;
        L_0x007d:
            org.telegram.messenger.FileLog.e(r4);	 Catch:{ all -> 0x009a }
            if (r6 == 0) goto L_0x0085;
        L_0x0082:
            r6.close();	 Catch:{ Throwable -> 0x0095 }
        L_0x0085:
            if (r7 == 0) goto L_0x008a;
        L_0x0087:
            r7.close();	 Catch:{ Exception -> 0x00ad }
        L_0x008a:
            r0 = 0;
            goto L_0x0066;
        L_0x008c:
            r10 = -1;
            if (r9 != r10) goto L_0x0049;
        L_0x008f:
            goto L_0x0049;
        L_0x0090:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
            goto L_0x0060;
        L_0x0095:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
            goto L_0x0085;
        L_0x009a:
            r10 = move-exception;
        L_0x009b:
            if (r6 == 0) goto L_0x00a0;
        L_0x009d:
            r6.close();	 Catch:{ Throwable -> 0x00a6 }
        L_0x00a0:
            if (r7 == 0) goto L_0x00a5;
        L_0x00a2:
            r7.close();	 Catch:{ Exception -> 0x00af }
        L_0x00a5:
            throw r10;
        L_0x00a6:
            r4 = move-exception;
            org.telegram.messenger.FileLog.e(r4);
            goto L_0x00a0;
        L_0x00ab:
            r10 = move-exception;
            goto L_0x0065;
        L_0x00ad:
            r10 = move-exception;
            goto L_0x008a;
        L_0x00af:
            r11 = move-exception;
            goto L_0x00a5;
        L_0x00b1:
            r10 = move-exception;
            r7 = r8;
            goto L_0x009b;
        L_0x00b4:
            r4 = move-exception;
            goto L_0x007d;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.AzureLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        protected void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$AzureLoadTask$$Lambda$0(this, result));
        }

        final /* synthetic */ void lambda$onPostExecute$0$ConnectionsManager$AzureLoadTask(NativeByteBuffer result) {
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, UserConfig.getInstance(this.currentAccount).getClientPhone());
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get azure result");
            }
            ConnectionsManager.currentTask = null;
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;

        public DnsTxtLoadTask(int instance) {
            this.currentAccount = instance;
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            Throwable e;
            Throwable th;
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            int i = 0;
            while (true) {
                ByteArrayOutputStream outbuf2 = outbuf;
                if (i < 3) {
                    String googleDomain;
                    int a;
                    if (i == 0) {
                        try {
                            googleDomain = "www.google.com";
                        } catch (Throwable th2) {
                            th = th2;
                            outbuf = outbuf2;
                            if (httpConnectionStream != null) {
                                try {
                                    httpConnectionStream.close();
                                } catch (Throwable e2) {
                                    FileLog.e(e2);
                                }
                            }
                            if (outbuf != null) {
                                try {
                                    outbuf.close();
                                } catch (Exception e3) {
                                }
                            }
                            throw th;
                        }
                    } else if (i == 1) {
                        googleDomain = "www.google.ru";
                    } else {
                        googleDomain = "google.com";
                    }
                    String domain = ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv2.stel.com" : MessagesController.getInstance(this.currentAccount).dcDomainName;
                    int len = Utilities.random.nextInt(116) + 13;
                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM";
                    StringBuilder stringBuilder = new StringBuilder(len);
                    for (a = 0; a < len; a++) {
                        stringBuilder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM".charAt(Utilities.random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM".length())));
                    }
                    URLConnection httpConnection = new URL("https://" + googleDomain + "/resolve?name=" + domain + "&type=ANY&random_padding=" + stringBuilder).openConnection();
                    httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    httpConnection.addRequestProperty("Host", "dns.google.com");
                    httpConnection.setConnectTimeout(5000);
                    httpConnection.setReadTimeout(5000);
                    httpConnection.connect();
                    httpConnectionStream = httpConnection.getInputStream();
                    outbuf = new ByteArrayOutputStream();
                    try {
                        byte[] data = new byte[32768];
                        while (!isCancelled()) {
                            int read = httpConnectionStream.read(data);
                            if (read > 0) {
                                outbuf.write(data, 0, read);
                            } else if (read == -1) {
                            }
                        }
                        JSONArray array = new JSONObject(new String(outbuf.toByteArray(), "UTF-8")).getJSONArray("Answer");
                        len = array.length();
                        ArrayList<String> arrayList = new ArrayList(len);
                        for (a = 0; a < len; a++) {
                            JSONObject object = array.getJSONObject(a);
                            if (object.getInt("type") == 16) {
                                arrayList.add(object.getString("data"));
                            }
                        }
                        Collections.sort(arrayList, ConnectionsManager$DnsTxtLoadTask$$Lambda$0.$instance);
                        StringBuilder builder = new StringBuilder();
                        for (a = 0; a < arrayList.size(); a++) {
                            builder.append(((String) arrayList.get(a)).replace("\"", ""));
                        }
                        byte[] bytes = Base64.decode(builder.toString(), 0);
                        NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                        buffer.writeBytes(bytes);
                        if (httpConnectionStream != null) {
                            try {
                                httpConnectionStream.close();
                            } catch (Throwable e22) {
                                FileLog.e(e22);
                            }
                        }
                        if (outbuf == null) {
                            return buffer;
                        }
                        try {
                            outbuf.close();
                            return buffer;
                        } catch (Exception e4) {
                            return buffer;
                        }
                    } catch (Throwable th3) {
                        e22 = th3;
                    }
                } else {
                    outbuf = outbuf2;
                    return null;
                }
                i++;
            }
        }

        static final /* synthetic */ int lambda$doInBackground$0$ConnectionsManager$DnsTxtLoadTask(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        protected void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$DnsTxtLoadTask$$Lambda$1(this, result));
        }

        final /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$DnsTxtLoadTask(NativeByteBuffer result) {
            if (result != null) {
                ConnectionsManager.currentTask = null;
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, UserConfig.getInstance(this.currentAccount).getClientPhone());
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
                FileLog.d("start azure task");
            }
            AzureLoadTask task = new AzureLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            ConnectionsManager.currentTask = task;
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        public FirebaseTask(int instance) {
            this.currentAccount = instance;
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            try {
                if (ConnectionsManager.native_isTestBackend(this.currentAccount) != 0) {
                    throw new Exception("test backend");
                }
                this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                this.firebaseRemoteConfig.setConfigSettings(new Builder().setDeveloperModeEnabled(false).build());
                String currentValue = this.firebaseRemoteConfig.getString("ipconfigv2");
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current firebase value = " + currentValue);
                }
                this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new ConnectionsManager$FirebaseTask$$Lambda$0(this));
                return null;
            } catch (Throwable e) {
                Utilities.stageQueue.postRunnable(new ConnectionsManager$FirebaseTask$$Lambda$1(this));
                FileLog.e(e);
            }
        }

        final /* synthetic */ void lambda$doInBackground$1$ConnectionsManager$FirebaseTask(Task finishedTask) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$FirebaseTask$$Lambda$2(this, finishedTask.isSuccessful()));
        }

        final /* synthetic */ void lambda$null$0$ConnectionsManager$FirebaseTask(boolean success) {
            ConnectionsManager.currentTask = null;
            String config = null;
            if (success) {
                this.firebaseRemoteConfig.activateFetched();
                config = this.firebaseRemoteConfig.getString("ipconfigv2");
            }
            if (TextUtils.isEmpty(config)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to get firebase result");
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask task = new DnsTxtLoadTask(this.currentAccount);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                ConnectionsManager.currentTask = task;
                return;
            }
            byte[] bytes = Base64.decode(config, 0);
            try {
                NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, buffer.address, UserConfig.getInstance(this.currentAccount).getClientPhone());
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        final /* synthetic */ void lambda$doInBackground$2$ConnectionsManager$FirebaseTask() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get firebase result");
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask task = new DnsTxtLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            ConnectionsManager.currentTask = task;
        }

        protected void onPostExecute(NativeByteBuffer result) {
        }
    }

    private static class ResolvedDomain {
        public ArrayList<String> addresses;
        long ttl;

        public ResolvedDomain(ArrayList<String> a, long t) {
            this.addresses = a;
            this.ttl = t;
        }

        public String getAddress() {
            return (String) this.addresses.get(Utilities.random.nextInt(this.addresses.size()));
        }
    }

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j, String str);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native long native_checkProxy(int i, String str, int i2, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate);

    public static native void native_cleanUp(int i, boolean z);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i5, boolean z, boolean z2, int i6);

    public static native int native_isTestBackend(int i);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_seSystemLangCode(int i, String str);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2, boolean z2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3, String str4);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setSystemLangCode(int i, String str);

    public static native void native_setUseIpv6(int i, boolean z);

    public static native void native_setUserId(int i, int i2);

    public static native void native_switchBackend(int i);

    public static native void native_updateDcSettings(int i);

    public static ConnectionsManager getInstance(int num) {
        Throwable th;
        ConnectionsManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        ConnectionsManager[] connectionsManagerArr = Instance;
                        ConnectionsManager localInstance2 = new ConnectionsManager(num);
                        try {
                            connectionsManagerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public ConnectionsManager(int instance) {
        String systemLangCode;
        String langCode;
        String deviceModel;
        String appVersion;
        String systemVersion;
        this.currentAccount = instance;
        this.connectionState = native_getConnectionState(this.currentAccount);
        File config = ApplicationLoader.getFilesDirFixed();
        if (instance != 0) {
            File file = new File(config, "account" + instance);
            file.mkdirs();
            config = file;
        }
        String configPath = config.toString();
        boolean enablePushConnection = MessagesController.getGlobalNotificationsSettings().getBoolean("pushConnection", true);
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            langCode = LocaleController.getLocaleStringIso639().toLowerCase();
            deviceModel = Build.MANUFACTURER + Build.MODEL;
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
            systemVersion = "SDK " + VERSION.SDK_INT;
        } catch (Exception e) {
            systemLangCode = "en";
            langCode = "";
            deviceModel = "Android unknown";
            appVersion = "App version unknown";
            systemVersion = "SDK " + VERSION.SDK_INT;
        }
        if (systemLangCode.trim().length() == 0) {
            systemLangCode = "en";
        }
        if (deviceModel.trim().length() == 0) {
            deviceModel = "Android unknown";
        }
        if (appVersion.trim().length() == 0) {
            appVersion = "App version unknown";
        }
        if (systemVersion.trim().length() == 0) {
            systemVersion = "SDK Unknown";
        }
        UserConfig.getInstance(this.currentAccount).loadConfig();
        init(BuildVars.BUILD_VERSION, 96, BuildVars.APP_ID, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, FileLog.getNetworkLogPath(), UserConfig.getInstance(this.currentAccount).getClientUserId(), enablePushConnection);
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

    public int sendRequest(TLObject object, RequestDelegate completionBlock) {
        return sendRequest(object, completionBlock, null, 0);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags) {
        return sendRequest(object, completionBlock, null, null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, null, null, flags, Integer.MAX_VALUE, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, quickAckBlock, null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        int requestToken = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$0(this, object, requestToken, onComplete, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate));
        return requestToken;
    }

    final /* synthetic */ void lambda$sendRequest$2$ConnectionsManager(TLObject object, int requestToken, RequestDelegate onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("send request " + object + " with token = " + requestToken);
        }
        try {
            NativeByteBuffer buffer = new NativeByteBuffer(object.getObjectSize());
            object.serializeToStream(buffer);
            object.freeResources();
            native_sendRequest(this.currentAccount, buffer.address, new ConnectionsManager$$Lambda$10(object, onComplete), onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate, requestToken);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    static final /* synthetic */ void lambda$null$1$ConnectionsManager(TLObject object, RequestDelegate onComplete, long response, int errorCode, String errorText, int networkType) {
        Throwable e;
        TLObject resp = null;
        TL_error error = null;
        if (response != 0) {
            try {
                NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                buff.reused = true;
                resp = object.deserializeResponse(buff, buff.readInt32(true), true);
            } catch (Exception e2) {
                e = e2;
                FileLog.e(e);
                return;
            }
        } else if (errorText != null) {
            TL_error error2 = new TL_error();
            try {
                error2.code = errorCode;
                error2.text = errorText;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(object + " got error " + error2.code + " " + error2.text);
                }
                error = error2;
            } catch (Exception e3) {
                e = e3;
                error = error2;
                FileLog.e(e);
                return;
            }
        }
        if (resp != null) {
            resp.networkType = networkType;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("java received " + resp + " error = " + error);
        }
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$11(onComplete, resp, error));
    }

    static final /* synthetic */ void lambda$null$0$ConnectionsManager(RequestDelegate onComplete, TLObject finalResponse, TL_error finalError) {
        onComplete.run(finalResponse, finalError);
        if (finalResponse != null) {
            finalResponse.freeResources();
        }
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(this.currentAccount, token, notifyServer);
    }

    public void cleanup(boolean resetKeys) {
        native_cleanUp(this.currentAccount, resetKeys);
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(this.currentAccount, guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(this.currentAccount, requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port) {
        native_applyDatacenterAddress(this.currentAccount, datacenterId, ipAddress, port);
    }

    public int getConnectionState() {
        if (this.connectionState == 3 && this.isUpdating) {
            return 5;
        }
        return this.connectionState;
    }

    public void setUserId(int id) {
        native_setUserId(this.currentAccount, id);
    }

    public void checkConnection() {
        native_setUseIpv6(this.currentAccount, useIpv6Address());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(this.currentAccount, value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, int userId, boolean enablePushConnection) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", "");
        String proxyUsername = preferences.getString("proxy_user", "");
        String proxyPassword = preferences.getString("proxy_pass", "");
        String proxySecret = preferences.getString("proxy_secret", "");
        int proxyPort = preferences.getInt("proxy_port", 1080);
        if (preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(proxyAddress)) {
            native_setProxySettings(this.currentAccount, proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
        }
        native_init(this.currentAccount, version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, userId, enablePushConnection, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
        checkConnection();
    }

    public static void setLangCode(String langCode) {
        langCode = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setLangCode(a, langCode);
        }
    }

    public static void setSystemLangCode(String langCode) {
        langCode = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setSystemLangCode(a, langCode);
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

    public long checkProxy(String address, int port, String username, String password, String secret, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(address)) {
            return 0;
        }
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        return native_checkProxy(this.currentAccount, address, port, username, password, secret, requestTimeDelegate);
    }

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            this.appPaused = value;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app paused = " + value);
            }
            if (value) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app resume count " + this.appResumeCount);
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
                ContactsController.getInstance(this.currentAccount).checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long address, int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            TLObject message = TLClassStore.Instance().TLdeserialize(buff, buff.readInt32(true), true);
            if (message instanceof Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("java received " + message);
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$1(currentAccount, message));
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", new Object[]{Integer.valueOf(constructor)}));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onUpdate(int currentAccount) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$2(currentAccount));
    }

    public static void onSessionCreated(int currentAccount) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$3(currentAccount));
    }

    public static void onConnectionStateChanged(int state, int currentAccount) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$Lambda$4(currentAccount, state));
    }

    static final /* synthetic */ void lambda$onConnectionStateChanged$6$ConnectionsManager(int currentAccount, int state) {
        getInstance(currentAccount).connectionState = state;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(int currentAccount) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$Lambda$5(currentAccount));
    }

    static final /* synthetic */ void lambda$onLogout$7$ConnectionsManager(int currentAccount) {
        if (UserConfig.getInstance(currentAccount).getClientUserId() != 0) {
            UserConfig.getInstance(currentAccount).clearConfig();
            MessagesController.getInstance(currentAccount).performLogout(0);
        }
    }

    public static int getInitFlags() {
        if (EmuDetector.with(ApplicationLoader.applicationContext).detect()) {
            return 1024;
        }
        return 0;
    }

    public static void onBytesSent(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementSentBytesCount(networkType, 6, (long) amount);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(int second, int currentAccount) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$6(second, currentAccount));
    }

    static final /* synthetic */ void lambda$onRequestNewServerIpAndPort$8$ConnectionsManager(int second, int currentAccount) {
        if (currentTask == null && ((second != 0 || Math.abs(lastDnsRequestTime - System.currentTimeMillis()) >= 10000) && ApplicationLoader.isNetworkOnline())) {
            lastDnsRequestTime = System.currentTimeMillis();
            if (second == 2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start azure dns task");
                }
                AzureLoadTask task = new AzureLoadTask(currentAccount);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task;
            } else if (second == 1) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask task2 = new DnsTxtLoadTask(currentAccount);
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task2;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start firebase task");
                }
                FirebaseTask task3 = new FirebaseTask(currentAccount);
                task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task3;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("don't start task, current task = " + currentTask + " next task = " + second + " time diff = " + Math.abs(lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ApplicationLoader.isNetworkOnline());
        }
    }

    public static void onProxyError() {
        AndroidUtilities.runOnUIThread(ConnectionsManager$$Lambda$7.$instance);
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x00a8 A:{SYNTHETIC, Splitter: B:20:0x00a8} */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00ad A:{SYNTHETIC, Splitter: B:23:0x00ad} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x013d A:{SYNTHETIC, Splitter: B:59:0x013d} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0142 A:{SYNTHETIC, Splitter: B:62:0x0142} */
    public static java.lang.String getHostByName(java.lang.String r22, int r23) {
        /*
        r18 = dnsCache;
        r5 = r18.get();
        r5 = (java.util.HashMap) r5;
        r0 = r22;
        r17 = r5.get(r0);
        r17 = (org.telegram.tgnet.ConnectionsManager.ResolvedDomain) r17;
        if (r17 == 0) goto L_0x002a;
    L_0x0012:
        r18 = android.os.SystemClock.elapsedRealtime();
        r0 = r17;
        r0 = r0.ttl;
        r20 = r0;
        r18 = r18 - r20;
        r20 = 300000; // 0x493e0 float:4.2039E-40 double:1.482197E-318;
        r18 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r18 >= 0) goto L_0x002a;
    L_0x0025:
        r18 = r17.getAddress();
    L_0x0029:
        return r18;
    L_0x002a:
        r14 = 0;
        r10 = 0;
        r7 = new java.net.URL;	 Catch:{ Throwable -> 0x0153 }
        r18 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0153 }
        r18.<init>();	 Catch:{ Throwable -> 0x0153 }
        r19 = "https://www.google.com/resolve?name=";
        r18 = r18.append(r19);	 Catch:{ Throwable -> 0x0153 }
        r0 = r18;
        r1 = r22;
        r18 = r0.append(r1);	 Catch:{ Throwable -> 0x0153 }
        r19 = "&type=A";
        r18 = r18.append(r19);	 Catch:{ Throwable -> 0x0153 }
        r18 = r18.toString();	 Catch:{ Throwable -> 0x0153 }
        r0 = r18;
        r7.<init>(r0);	 Catch:{ Throwable -> 0x0153 }
        r9 = r7.openConnection();	 Catch:{ Throwable -> 0x0153 }
        r18 = "User-Agent";
        r19 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
        r0 = r18;
        r1 = r19;
        r9.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x0153 }
        r18 = "Host";
        r19 = "dns.google.com";
        r0 = r18;
        r1 = r19;
        r9.addRequestProperty(r0, r1);	 Catch:{ Throwable -> 0x0153 }
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r18;
        r9.setConnectTimeout(r0);	 Catch:{ Throwable -> 0x0153 }
        r18 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
        r0 = r18;
        r9.setReadTimeout(r0);	 Catch:{ Throwable -> 0x0153 }
        r9.connect();	 Catch:{ Throwable -> 0x0153 }
        r10 = r9.getInputStream();	 Catch:{ Throwable -> 0x0153 }
        r15 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x0153 }
        r15.<init>();	 Catch:{ Throwable -> 0x0153 }
        r18 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r0 = r18;
        r6 = new byte[r0];	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
    L_0x0091:
        r16 = r10.read(r6);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        if (r16 <= 0) goto L_0x00b5;
    L_0x0097:
        r18 = 0;
        r0 = r18;
        r1 = r16;
        r15.write(r6, r0, r1);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        goto L_0x0091;
    L_0x00a1:
        r8 = move-exception;
        r14 = r15;
    L_0x00a3:
        org.telegram.messenger.FileLog.e(r8);	 Catch:{ all -> 0x013a }
        if (r10 == 0) goto L_0x00ab;
    L_0x00a8:
        r10.close();	 Catch:{ Throwable -> 0x0134 }
    L_0x00ab:
        if (r14 == 0) goto L_0x00b0;
    L_0x00ad:
        r14.close();	 Catch:{ Exception -> 0x014b }
    L_0x00b0:
        r18 = "";
        goto L_0x0029;
    L_0x00b5:
        r18 = -1;
        r0 = r16;
        r1 = r18;
        if (r0 != r1) goto L_0x00bd;
    L_0x00bd:
        r11 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r18 = new java.lang.String;	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r19 = r15.toByteArray();	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r18.<init>(r19);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r0 = r18;
        r11.<init>(r0);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r18 = "Answer";
        r0 = r18;
        r4 = r11.getJSONArray(r0);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r12 = r4.length();	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        if (r12 <= 0) goto L_0x011f;
    L_0x00dc:
        r3 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r3.<init>(r12);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r2 = 0;
    L_0x00e2:
        if (r2 >= r12) goto L_0x00f7;
    L_0x00e4:
        r18 = r4.getJSONObject(r2);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r19 = "data";
        r18 = r18.getString(r19);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r0 = r18;
        r3.add(r0);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r2 = r2 + 1;
        goto L_0x00e2;
    L_0x00f7:
        r13 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain;	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r18 = android.os.SystemClock.elapsedRealtime();	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r0 = r18;
        r13.<init>(r3, r0);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r0 = r22;
        r5.put(r0, r13);	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        r18 = r13.getAddress();	 Catch:{ Throwable -> 0x00a1, all -> 0x0150 }
        if (r10 == 0) goto L_0x0110;
    L_0x010d:
        r10.close();	 Catch:{ Throwable -> 0x011a }
    L_0x0110:
        if (r15 == 0) goto L_0x0029;
    L_0x0112:
        r15.close();	 Catch:{ Exception -> 0x0117 }
        goto L_0x0029;
    L_0x0117:
        r19 = move-exception;
        goto L_0x0029;
    L_0x011a:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x0110;
    L_0x011f:
        if (r10 == 0) goto L_0x0124;
    L_0x0121:
        r10.close();	 Catch:{ Throwable -> 0x012b }
    L_0x0124:
        if (r15 == 0) goto L_0x0129;
    L_0x0126:
        r15.close();	 Catch:{ Exception -> 0x0130 }
    L_0x0129:
        r14 = r15;
        goto L_0x00b0;
    L_0x012b:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x0124;
    L_0x0130:
        r18 = move-exception;
        r14 = r15;
        goto L_0x00b0;
    L_0x0134:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x00ab;
    L_0x013a:
        r18 = move-exception;
    L_0x013b:
        if (r10 == 0) goto L_0x0140;
    L_0x013d:
        r10.close();	 Catch:{ Throwable -> 0x0146 }
    L_0x0140:
        if (r14 == 0) goto L_0x0145;
    L_0x0142:
        r14.close();	 Catch:{ Exception -> 0x014e }
    L_0x0145:
        throw r18;
    L_0x0146:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x0140;
    L_0x014b:
        r18 = move-exception;
        goto L_0x00b0;
    L_0x014e:
        r19 = move-exception;
        goto L_0x0145;
    L_0x0150:
        r18 = move-exception;
        r14 = r15;
        goto L_0x013b;
    L_0x0153:
        r8 = move-exception;
        goto L_0x00a3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.getHostByName(java.lang.String, int):java.lang.String");
    }

    public static void onBytesReceived(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementReceivedBytesCount(networkType, 6, (long) amount);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onUpdateConfig(long address, int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            TL_config message = TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new ConnectionsManager$$Lambda$8(currentAccount, message));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onInternalPushReceived(int currentAccount) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean enabled, String address, int port, String username, String password, String secret) {
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        for (int a = 0; a < 3; a++) {
            if (!enabled || TextUtils.isEmpty(address)) {
                native_setProxySettings(a, "", 1080, "", "", "");
            } else {
                native_setProxySettings(a, address, port, username, password, secret);
            }
            if (UserConfig.getInstance(a).isClientActivated()) {
                MessagesController.getInstance(a).checkProxyInfo(true);
            }
        }
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public void setIsUpdating(boolean value) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$Lambda$9(this, value));
    }

    final /* synthetic */ void lambda$setIsUpdating$11$ConnectionsManager(boolean value) {
        if (this.isUpdating != value) {
            this.isUpdating = value;
            if (this.connectionState == 3) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
            }
        }
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (VERSION.SDK_INT < 19) {
            return false;
        }
        Enumeration<NetworkInterface> networkInterfaces;
        NetworkInterface networkInterface;
        List<InterfaceAddress> interfaceAddresses;
        int a;
        InetAddress inetAddress;
        if (BuildVars.LOGS_ENABLED) {
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (!(!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.getInterfaceAddresses().isEmpty())) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("valid interface: " + networkInterface);
                        }
                        interfaceAddresses = networkInterface.getInterfaceAddresses();
                        for (a = 0; a < interfaceAddresses.size(); a++) {
                            inetAddress = ((InterfaceAddress) interfaceAddresses.get(a)).getAddress();
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("address: " + inetAddress.getHostAddress());
                            }
                            if (!(inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress() || !BuildVars.LOGS_ENABLED)) {
                                FileLog.d("address is good");
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    interfaceAddresses = networkInterface.getInterfaceAddresses();
                    for (a = 0; a < interfaceAddresses.size(); a++) {
                        inetAddress = ((InterfaceAddress) interfaceAddresses.get(a)).getAddress();
                        if (!(inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress())) {
                            if (inetAddress instanceof Inet6Address) {
                                hasIpv6 = true;
                            } else if ((inetAddress instanceof Inet4Address) && !inetAddress.getHostAddress().startsWith("192.0.0.")) {
                                hasIpv4 = true;
                            }
                        }
                    }
                }
            }
            if (hasIpv4 || !hasIpv6) {
                return false;
            }
            return true;
        } catch (Throwable e2) {
            FileLog.e(e2);
            return false;
        }
    }
}
