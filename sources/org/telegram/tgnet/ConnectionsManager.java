package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;
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
    private static ThreadLocal<HashMap<String, ResolvedDomain>> dnsCache = new C06891();
    private static final int dnsConfigVersion = 0;
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState;
    private int currentAccount;
    private boolean isUpdating;
    private long lastPauseTime = System.currentTimeMillis();
    private AtomicInteger lastRequestToken = new AtomicInteger(1);

    /* renamed from: org.telegram.tgnet.ConnectionsManager$1 */
    static class C06891 extends ThreadLocal<HashMap<String, ResolvedDomain>> {
        C06891() {
        }

        protected HashMap<String, ResolvedDomain> initialValue() {
            return new HashMap();
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager$3 */
    class C06913 extends BroadcastReceiver {
        C06913() {
        }

        public void onReceive(Context context, Intent intent) {
            ConnectionsManager.this.checkConnection();
        }
    }

    private static class AzureLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;

        public AzureLoadTask(int instance) {
            this.currentAccount = instance;
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                URL downloadUrl;
                byte[] bytes;
                NativeByteBuffer buffer;
                if (ConnectionsManager.native_isTestBackend(this.currentAccount) != 0) {
                    downloadUrl = new URL("https://software-download.microsoft.com/test/config.txt");
                } else {
                    downloadUrl = new URL("https://software-download.microsoft.com/prod/config.txt");
                }
                URLConnection httpConnection = downloadUrl.openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("Host", "tcdnb.azureedge.net");
                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (!isCancelled()) {
                    int read = httpConnectionStream.read(data);
                    if (read > 0) {
                        outbuf.write(data, 0, read);
                    } else {
                        if (read == -1) {
                        }
                        bytes = Base64.decode(outbuf.toByteArray(), 0);
                        buffer = new NativeByteBuffer(bytes.length);
                        buffer.writeBytes(bytes);
                        if (httpConnectionStream != null) {
                            try {
                                httpConnectionStream.close();
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        if (outbuf != null) {
                            try {
                                outbuf.close();
                            } catch (Exception e2) {
                            }
                        }
                        return buffer;
                    }
                }
                bytes = Base64.decode(outbuf.toByteArray(), 0);
                buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
                if (outbuf != null) {
                    outbuf.close();
                }
                return buffer;
            } catch (Throwable e3) {
                FileLog.m3e(e3);
                if (outbuf != null) {
                    outbuf.close();
                }
                return null;
            }
        }

        protected void onPostExecute(final NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    if (result != null) {
                        ConnectionsManager.currentTask = null;
                        ConnectionsManager.native_applyDnsConfig(AzureLoadTask.this.currentAccount, result.address);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("failed to get azure result");
                        FileLog.m0d("start dns txt task");
                    }
                    DnsTxtLoadTask task = new DnsTxtLoadTask(AzureLoadTask.this.currentAccount);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    ConnectionsManager.currentTask = task;
                }
            });
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;

        /* renamed from: org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$1 */
        class C06981 implements Comparator<String> {
            C06981() {
            }

            public int compare(String o1, String o2) {
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
        }

        public DnsTxtLoadTask(int instance) {
            this.currentAccount = instance;
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                JSONArray array;
                int len;
                ArrayList<String> arrayList;
                int a;
                DnsTxtLoadTask dnsTxtLoadTask;
                StringBuilder builder;
                byte[] bytes;
                NativeByteBuffer buffer;
                String domain = String.format(Locale.US, ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tap%1$s.stel.com" : "ap%1$s.stel.com", new Object[]{TtmlNode.ANONYMOUS_REGION_ID});
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://google.com/resolve?name=");
                stringBuilder.append(domain);
                stringBuilder.append("&type=16");
                URLConnection httpConnection = new URL(stringBuilder.toString()).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("Host", "dns.google.com");
                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (!isCancelled()) {
                    int read = httpConnectionStream.read(data);
                    if (read > 0) {
                        outbuf.write(data, 0, read);
                    } else {
                        if (read == -1) {
                        }
                        array = new JSONObject(new String(outbuf.toByteArray(), C0539C.UTF8_NAME)).getJSONArray("Answer");
                        len = array.length();
                        arrayList = new ArrayList(len);
                        for (a = 0; a < len; a++) {
                            arrayList.add(array.getJSONObject(a).getString(DataSchemeDataSource.SCHEME_DATA));
                        }
                        Collections.sort(arrayList, new C06981());
                        builder = new StringBuilder();
                        a = 0;
                        while (a < arrayList.size()) {
                            builder.append(((String) arrayList.get(a)).replace("\"", TtmlNode.ANONYMOUS_REGION_ID));
                            a++;
                            dnsTxtLoadTask = this;
                        }
                        bytes = Base64.decode(builder.toString(), 0);
                        buffer = new NativeByteBuffer(bytes.length);
                        buffer.writeBytes(bytes);
                        if (httpConnectionStream != null) {
                            try {
                                httpConnectionStream.close();
                            } catch (Throwable th) {
                                FileLog.m3e(th);
                            }
                        }
                        if (outbuf != null) {
                            try {
                                outbuf.close();
                            } catch (Exception e) {
                            }
                        }
                        return buffer;
                    }
                }
                array = new JSONObject(new String(outbuf.toByteArray(), C0539C.UTF8_NAME)).getJSONArray("Answer");
                len = array.length();
                arrayList = new ArrayList(len);
                for (a = 0; a < len; a++) {
                    arrayList.add(array.getJSONObject(a).getString(DataSchemeDataSource.SCHEME_DATA));
                }
                Collections.sort(arrayList, new C06981());
                builder = new StringBuilder();
                a = 0;
                while (a < arrayList.size()) {
                    builder.append(((String) arrayList.get(a)).replace("\"", TtmlNode.ANONYMOUS_REGION_ID));
                    a++;
                    dnsTxtLoadTask = this;
                }
                bytes = Base64.decode(builder.toString(), 0);
                buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
                if (outbuf != null) {
                    outbuf.close();
                }
                return buffer;
            } catch (Throwable th2) {
                FileLog.m3e(th2);
                if (outbuf != null) {
                    outbuf.close();
                }
                return null;
            }
        }

        protected void onPostExecute(final NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    if (result != null) {
                        ConnectionsManager.native_applyDnsConfig(DnsTxtLoadTask.this.currentAccount, result.address);
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("failed to get dns txt result");
                    }
                    ConnectionsManager.currentTask = null;
                }
            });
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        /* renamed from: org.telegram.tgnet.ConnectionsManager$FirebaseTask$2 */
        class C24172 implements Runnable {
            C24172() {
            }

            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("failed to get firebase result");
                    FileLog.m0d("start azure task");
                }
                AzureLoadTask task = new AzureLoadTask(FirebaseTask.this.currentAccount);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                ConnectionsManager.currentTask = task;
            }
        }

        /* renamed from: org.telegram.tgnet.ConnectionsManager$FirebaseTask$1 */
        class C24231 implements OnCompleteListener<Void> {
            C24231() {
            }

            public void onComplete(Task<Void> finishedTask) {
                final boolean success = finishedTask.isSuccessful();
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        ConnectionsManager.currentTask = null;
                        String config = null;
                        if (success) {
                            FirebaseTask.this.firebaseRemoteConfig.activateFetched();
                            config = FirebaseTask.this.firebaseRemoteConfig.getString("ipconfig");
                        }
                        if (TextUtils.isEmpty(config)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("failed to get firebase result");
                                FileLog.m0d("start azure task");
                            }
                            AzureLoadTask task = new AzureLoadTask(FirebaseTask.this.currentAccount);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = task;
                            return;
                        }
                        byte[] bytes = Base64.decode(config, 0);
                        try {
                            NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                            buffer.writeBytes(bytes);
                            ConnectionsManager.native_applyDnsConfig(FirebaseTask.this.currentAccount, buffer.address);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }

        public FirebaseTask(int instance) {
            this.currentAccount = instance;
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            try {
                this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                this.firebaseRemoteConfig.setConfigSettings(new Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build());
                String currentValue = this.firebaseRemoteConfig.getString("ipconfig");
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("current firebase value = ");
                    stringBuilder.append(currentValue);
                    FileLog.m0d(stringBuilder.toString());
                }
                this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new C24231());
            } catch (Throwable e) {
                Utilities.stageQueue.postRunnable(new C24172());
                FileLog.m3e(e);
            }
            return null;
        }

        protected void onPostExecute(NativeByteBuffer result) {
        }
    }

    private static class ResolvedDomain {
        public String address;
        long ttl;

        public ResolvedDomain(String a, long t) {
            this.address = a;
            this.ttl = t;
        }
    }

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native void native_cleanUp(int i);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i5, boolean z, boolean z2, int i6);

    public static native int native_isTestBackend(int i);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setUseIpv6(int i, boolean z);

    public static native void native_setUserId(int i, int i2);

    public static native void native_switchBackend(int i);

    public static native void native_updateDcSettings(int i);

    public static ConnectionsManager getInstance(int num) {
        ConnectionsManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    ConnectionsManager[] connectionsManagerArr = Instance;
                    ConnectionsManager connectionsManager = new ConnectionsManager(num);
                    localInstance = connectionsManager;
                    connectionsManagerArr[num] = connectionsManager;
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
        String langCode2;
        String deviceModel2;
        String appVersion2;
        int i = instance;
        this.currentAccount = i;
        this.connectionState = native_getConnectionState(this.currentAccount);
        File config = ApplicationLoader.getFilesDirFixed();
        if (i != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("account");
            stringBuilder.append(i);
            config = new File(config, stringBuilder.toString());
            config.mkdirs();
        }
        File config2 = config;
        String configPath = config2.toString();
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        boolean enablePushConnection = preferences.getBoolean("pushConnection", true);
        StringBuilder stringBuilder2;
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            langCode = LocaleController.getLocaleStringIso639().toLowerCase();
            deviceModel = new StringBuilder();
            deviceModel.append(Build.MANUFACTURER);
            deviceModel.append(Build.MODEL);
            deviceModel = deviceModel.toString();
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = new StringBuilder();
            appVersion.append(pInfo.versionName);
            appVersion.append(" (");
            appVersion.append(pInfo.versionCode);
            appVersion.append(")");
            appVersion = appVersion.toString();
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("SDK ");
            stringBuilder2.append(VERSION.SDK_INT);
            langCode2 = langCode;
            langCode = systemLangCode;
            systemLangCode = stringBuilder2.toString();
        } catch (Exception e) {
            langCode = "en";
            deviceModel = TtmlNode.ANONYMOUS_REGION_ID;
            appVersion = "App version unknown";
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("SDK ");
            stringBuilder2.append(VERSION.SDK_INT);
            systemLangCode = stringBuilder2.toString();
            langCode2 = deviceModel;
            deviceModel = "Android unknown";
        }
        if (langCode.trim().length() == 0) {
            langCode = "en";
        }
        String systemLangCode2 = langCode;
        if (deviceModel.trim().length() == 0) {
            deviceModel2 = "Android unknown";
        } else {
            deviceModel2 = deviceModel;
        }
        if (appVersion.trim().length() == 0) {
            appVersion2 = "App version unknown";
        } else {
            appVersion2 = appVersion;
        }
        if (systemLangCode.trim().length() == 0) {
            systemLangCode = "SDK Unknown";
        }
        String systemVersion = systemLangCode;
        UserConfig.getInstance(r14.currentAccount).loadConfig();
        int i2 = BuildVars.BUILD_VERSION;
        int i3 = BuildVars.APP_ID;
        String networkLogPath = FileLog.getNetworkLogPath();
        String str = networkLogPath;
        init(i2, 76, i3, deviceModel2, systemVersion, appVersion2, langCode2, systemLangCode2, configPath, str, UserConfig.getInstance(r14.currentAccount).getClientUserId(), enablePushConnection);
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
        return sendRequest(object, completionBlock, null, null, flags, DEFAULT_DATACENTER_ID, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, null, null, flags, DEFAULT_DATACENTER_ID, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, quickAckBlock, null, flags, DEFAULT_DATACENTER_ID, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        int requestToken = this.lastRequestToken.getAndIncrement();
        final TLObject tLObject = object;
        final int i = requestToken;
        final RequestDelegate requestDelegate = onComplete;
        final QuickAckDelegate quickAckDelegate = onQuickAck;
        final WriteToSocketDelegate writeToSocketDelegate = onWriteToSocket;
        final int i2 = flags;
        final int i3 = datacenterId;
        final int i4 = connetionType;
        final boolean z = immediate;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.tgnet.ConnectionsManager$2$1 */
            class C24221 implements RequestDelegateInternal {
                C24221() {
                }

                public void run(long response, int errorCode, String errorText, int networkType) {
                    Exception e = null;
                    TL_error error = null;
                    if (response != 0) {
                        try {
                            NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                            buff.reused = true;
                            e = tLObject.deserializeResponse(buff, buff.readInt32(true), true);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                            return;
                        }
                    } else if (errorText != null) {
                        error = new TL_error();
                        error.code = errorCode;
                        error.text = errorText;
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(tLObject);
                            stringBuilder.append(" got error ");
                            stringBuilder.append(error.code);
                            stringBuilder.append(" ");
                            stringBuilder.append(error.text);
                            FileLog.m1e(stringBuilder.toString());
                        }
                    }
                    if (e != null) {
                        e.networkType = networkType;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("java received ");
                        stringBuilder.append(e);
                        stringBuilder.append(" error = ");
                        stringBuilder.append(error);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    final Exception finalResponse = e;
                    final TL_error finalError = error;
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            requestDelegate.run(finalResponse, finalError);
                            if (finalResponse != null) {
                                finalResponse.freeResources();
                            }
                        }
                    });
                }
            }

            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("send request ");
                    stringBuilder.append(tLObject);
                    stringBuilder.append(" with token = ");
                    stringBuilder.append(i);
                    FileLog.m0d(stringBuilder.toString());
                }
                try {
                    NativeByteBuffer buffer = new NativeByteBuffer(tLObject.getObjectSize());
                    tLObject.serializeToStream(buffer);
                    tLObject.freeResources();
                    ConnectionsManager.native_sendRequest(ConnectionsManager.this.currentAccount, buffer.address, new C24221(), quickAckDelegate, writeToSocketDelegate, i2, i3, i4, z, i);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
        return requestToken;
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(this.currentAccount, token, notifyServer);
    }

    public void cleanup() {
        native_cleanUp(this.currentAccount);
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

    private void checkConnection() {
        native_setUseIpv6(this.currentAccount, useIpv6Address());
        native_setNetworkAvailable(this.currentAccount, isNetworkOnline(), getCurrentNetworkType());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(this.currentAccount, value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, int userId, boolean enablePushConnection) {
        ConnectionsManager connectionsManager = this;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID);
        String proxyUsername = preferences.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID);
        String proxyPassword = preferences.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID);
        int proxyPort = preferences.getInt("proxy_port", 1080);
        if (preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(proxyAddress)) {
            native_setProxySettings(connectionsManager.currentAccount, proxyAddress, proxyPort, proxyUsername, proxyPassword);
        }
        native_init(connectionsManager.currentAccount, version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, userId, enablePushConnection, isNetworkOnline(), getCurrentNetworkType());
        checkConnection();
        ApplicationLoader.applicationContext.registerReceiver(new C06913(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public static void setLangCode(String langCode) {
        langCode = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setLangCode(a, langCode);
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

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            StringBuilder stringBuilder;
            this.appPaused = value;
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("app paused = ");
                stringBuilder.append(value);
                FileLog.m0d(stringBuilder.toString());
            }
            if (value) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("app resume count ");
                stringBuilder.append(this.appResumeCount);
                FileLog.m0d(stringBuilder.toString());
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
                FileLog.m0d("reset app pause time");
            }
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                ContactsController.getInstance(this.currentAccount).checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long address, final int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            final TLObject message = TLClassStore.Instance().TLdeserialize(buff, buff.readInt32(true), true);
            if (message instanceof Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("java received ");
                    stringBuilder.append(message);
                    FileLog.m0d(stringBuilder.toString());
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesController.getInstance(currentAccount).processUpdates((Updates) message, false);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onUpdate(final int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.getInstance(currentAccount).updateTimerProc();
            }
        });
    }

    public static void onSessionCreated(final int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.getInstance(currentAccount).getDifference();
            }
        });
    }

    public static void onConnectionStateChanged(final int state, final int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ConnectionsManager.getInstance(currentAccount).connectionState = state;
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
            }
        });
    }

    public static void onLogout(final int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (UserConfig.getInstance(currentAccount).getClientUserId() != 0) {
                    UserConfig.getInstance(currentAccount).clearConfig();
                    MessagesController.getInstance(currentAccount).performLogout(false);
                }
            }
        });
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return 1;
        }
        if (isRoaming()) {
            return 2;
        }
        return 0;
    }

    public static void onBytesSent(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementSentBytesCount(networkType, 6, (long) amount);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(final int second, final int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (ConnectionsManager.currentTask == null && (second != 0 || Math.abs(ConnectionsManager.lastDnsRequestTime - System.currentTimeMillis()) >= 10000)) {
                    if (ConnectionsManager.isNetworkOnline()) {
                        ConnectionsManager.lastDnsRequestTime = System.currentTimeMillis();
                        if (second == 2) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start dns txt task");
                            }
                            DnsTxtLoadTask task = new DnsTxtLoadTask(currentAccount);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = task;
                        } else if (second == 1) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start azure dns task");
                            }
                            AzureLoadTask task2 = new AzureLoadTask(currentAccount);
                            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = task2;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start firebase task");
                            }
                            FirebaseTask task3 = new FirebaseTask(currentAccount);
                            task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = task3;
                        }
                        return;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("don't start task, current task = ");
                    stringBuilder.append(ConnectionsManager.currentTask);
                    stringBuilder.append(" next task = ");
                    stringBuilder.append(second);
                    stringBuilder.append(" time diff = ");
                    stringBuilder.append(Math.abs(ConnectionsManager.lastDnsRequestTime - System.currentTimeMillis()));
                    stringBuilder.append(" network = ");
                    stringBuilder.append(ConnectionsManager.isNetworkOnline());
                    FileLog.m0d(stringBuilder.toString());
                }
            }
        });
    }

    public static String getHostByName(String domain, int currentAccount) {
        HashMap<String, ResolvedDomain> cache = (HashMap) dnsCache.get();
        ResolvedDomain resolvedDomain = (ResolvedDomain) cache.get(domain);
        if (resolvedDomain != null && SystemClock.uptimeMillis() - resolvedDomain.ttl < 300000) {
            return resolvedDomain.address;
        }
        ByteArrayOutputStream outbuf = null;
        InputStream httpConnectionStream = null;
        try {
            int read;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://google.com/resolve?name=");
            stringBuilder.append(domain);
            stringBuilder.append("&type=A");
            URLConnection httpConnection = new URL(stringBuilder.toString()).openConnection();
            httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
            httpConnection.addRequestProperty("Host", "dns.google.com");
            httpConnection.setConnectTimeout(1000);
            httpConnection.setReadTimeout(2000);
            httpConnection.connect();
            httpConnectionStream = httpConnection.getInputStream();
            outbuf = new ByteArrayOutputStream();
            byte[] data = new byte[32768];
            while (true) {
                read = httpConnectionStream.read(data);
                if (read <= 0) {
                    break;
                }
                outbuf.write(data, 0, read);
            }
            if (read == -1) {
            }
            JSONArray array = new JSONObject(new String(outbuf.toByteArray())).getJSONArray("Answer");
            if (array.length() > 0) {
                String ip = array.getJSONObject(Utilities.random.nextInt(array.length())).getString(DataSchemeDataSource.SCHEME_DATA);
                cache.put(domain, new ResolvedDomain(ip, SystemClock.uptimeMillis()));
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                if (outbuf != null) {
                    try {
                        outbuf.close();
                    } catch (Exception e2) {
                    }
                }
                return ip;
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
                }
            }
            if (outbuf != null) {
                try {
                    outbuf.close();
                } catch (Exception e4) {
                }
            }
            return TtmlNode.ANONYMOUS_REGION_ID;
        } catch (Throwable e32) {
            FileLog.m3e(e32);
            if (outbuf != null) {
                outbuf.close();
            }
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static void onBytesReceived(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementReceivedBytesCount(networkType, 6, (long) amount);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onUpdateConfig(long address, final int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            final TL_config message = TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesController.getInstance(currentAccount).updateConfig(message);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onInternalPushReceived(int currentAccount) {
        KeepAliveJob.startJob();
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public static boolean isRoaming() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (netInfo != null) {
                return netInfo.isRoaming();
            }
            return false;
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            State state = netInfo.getState();
            if (netInfo == null || (state != State.CONNECTED && state != State.CONNECTING && state != State.SUSPENDED)) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static boolean isConnectedToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            if (netInfo == null || netInfo.getState() != State.CONNECTED) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void setIsUpdating(final boolean value) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (ConnectionsManager.this.isUpdating != value) {
                    ConnectionsManager.this.isUpdating = value;
                    if (ConnectionsManager.this.connectionState == 3) {
                        NotificationCenter.getInstance(ConnectionsManager.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
                    }
                }
            }
        });
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (VERSION.SDK_INT < 19) {
            return false;
        }
        Enumeration<NetworkInterface> networkInterfaces;
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
                                FileLog.m0d(stringBuilder.toString());
                            }
                            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                            for (int a = 0; a < interfaceAddresses.size(); a++) {
                                InetAddress inetAddress = ((InterfaceAddress) interfaceAddresses.get(a)).getAddress();
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("address: ");
                                    stringBuilder2.append(inetAddress.getHostAddress());
                                    FileLog.m0d(stringBuilder2.toString());
                                }
                                if (!(inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress())) {
                                    if (!inetAddress.isMulticastAddress()) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("address is good");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface2 = (NetworkInterface) networkInterfaces.nextElement();
                if (networkInterface2.isUp()) {
                    if (!networkInterface2.isLoopback()) {
                        List<InterfaceAddress> interfaceAddresses2 = networkInterface2.getInterfaceAddresses();
                        boolean hasIpv62 = hasIpv6;
                        for (int a2 = 0; a2 < interfaceAddresses2.size(); a2++) {
                            InetAddress inetAddress2 = ((InterfaceAddress) interfaceAddresses2.get(a2)).getAddress();
                            if (!(inetAddress2.isLinkLocalAddress() || inetAddress2.isLoopbackAddress())) {
                                if (!inetAddress2.isMulticastAddress()) {
                                    if (inetAddress2 instanceof Inet6Address) {
                                        hasIpv62 = true;
                                    } else if ((inetAddress2 instanceof Inet4Address) && !inetAddress2.getHostAddress().startsWith("192.0.0.")) {
                                        hasIpv4 = true;
                                    }
                                }
                            }
                        }
                        hasIpv6 = hasIpv62;
                    }
                }
            }
            if (hasIpv4 || !hasIpv6) {
                return false;
            }
            return true;
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    public static boolean isNetworkOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
                return true;
            }
            netInfo = connectivityManager.getNetworkInfo(0);
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            netInfo = connectivityManager.getNetworkInfo(1);
            if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return true;
        }
    }
}
