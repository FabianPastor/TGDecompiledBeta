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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.JoinPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
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
    private static volatile ConnectionsManager Instance = null;
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    private static AsyncTask currentTask;
    private static final int dnsConfigVersion = 0;
    private static long lastDnsRequestTime;
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState = native_getConnectionState();
    private boolean isUpdating;
    private int lastClassGuid = 1;
    private long lastPauseTime = System.currentTimeMillis();
    private AtomicInteger lastRequestToken = new AtomicInteger(1);
    private WakeLock wakeLock;

    private static class DnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private DnsLoadTask() {
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf;
            byte[] bytes;
            NativeByteBuffer buffer;
            try {
                URL downloadUrl;
                if (ConnectionsManager.native_isTestBackend() != 0) {
                    downloadUrl = new URL("https://google.com/test/");
                } else {
                    downloadUrl = new URL("https://google.com");
                }
                URLConnection httpConnection = downloadUrl.openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("Host", String.format(Locale.US, "dns-telegram%1$s.appspot.com", new Object[]{""}));
                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.connect();
                InputStream httpConnectionStream = httpConnection.getInputStream();
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (!isCancelled()) {
                    int read = httpConnectionStream.read(data);
                    if (read > 0) {
                        outbuf.write(data, 0, read);
                    } else {
                        if (read == -1) {
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                        bytes = Base64.decode(outbuf.toByteArray(), 0);
                        buffer = new NativeByteBuffer(bytes.length);
                        buffer.writeBytes(bytes);
                        return buffer;
                    }
                }
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
            } catch (Throwable e) {
                FileLog.e(e);
                return null;
            }
            bytes = Base64.decode(outbuf.toByteArray(), 0);
            buffer = new NativeByteBuffer(bytes.length);
            buffer.writeBytes(bytes);
            return buffer;
        }

        protected void onPostExecute(NativeByteBuffer result) {
            if (result != null) {
                ConnectionsManager.currentTask = null;
                ConnectionsManager.native_applyDnsConfig(result.address);
                return;
            }
            DnsTxtLoadTask task = new DnsTxtLoadTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            ConnectionsManager.currentTask = task;
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private DnsTxtLoadTask() {
        }

        protected NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf;
            JSONArray array;
            int len;
            ArrayList<String> arrayList;
            int a;
            StringBuilder builder;
            byte[] bytes;
            NativeByteBuffer buffer;
            try {
                URLConnection httpConnection = new URL("https://google.com/resolve?name=" + String.format(Locale.US, ConnectionsManager.native_isTestBackend() != 0 ? "tap%1$s.stel.com" : "ap%1$s.stel.com", new Object[]{""}) + "&type=16").openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("Host", "dns.google.com");
                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.connect();
                InputStream httpConnectionStream = httpConnection.getInputStream();
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (!isCancelled()) {
                    int read = httpConnectionStream.read(data);
                    if (read > 0) {
                        outbuf.write(data, 0, read);
                    } else {
                        if (read == -1) {
                        }
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                        array = new JSONObject(new String(outbuf.toByteArray(), "UTF-8")).getJSONArray("Answer");
                        len = array.length();
                        arrayList = new ArrayList(len);
                        for (a = 0; a < len; a++) {
                            arrayList.add(array.getJSONObject(a).getString("data"));
                        }
                        Collections.sort(arrayList, new Comparator<String>() {
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
                        });
                        builder = new StringBuilder();
                        for (a = 0; a < arrayList.size(); a++) {
                            builder.append(((String) arrayList.get(a)).replace("\"", ""));
                        }
                        bytes = Base64.decode(builder.toString(), 0);
                        buffer = new NativeByteBuffer(bytes.length);
                        buffer.writeBytes(bytes);
                        return buffer;
                    }
                }
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
            } catch (Throwable e) {
                FileLog.e(e);
                return null;
            }
            array = new JSONObject(new String(outbuf.toByteArray(), "UTF-8")).getJSONArray("Answer");
            len = array.length();
            arrayList = new ArrayList(len);
            for (a = 0; a < len; a++) {
                arrayList.add(array.getJSONObject(a).getString("data"));
            }
            Collections.sort(arrayList, /* anonymous class already generated */);
            builder = new StringBuilder();
            for (a = 0; a < arrayList.size(); a++) {
                builder.append(((String) arrayList.get(a)).replace("\"", ""));
            }
            bytes = Base64.decode(builder.toString(), 0);
            buffer = new NativeByteBuffer(bytes.length);
            buffer.writeBytes(bytes);
            return buffer;
        }

        protected void onPostExecute(NativeByteBuffer result) {
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(result.address);
            }
            ConnectionsManager.currentTask = null;
        }
    }

    public static native void native_applyDatacenterAddress(int i, String str, int i2);

    public static native void native_applyDnsConfig(int i);

    public static native void native_bindRequestToGuid(int i, int i2);

    public static native void native_cancelRequest(int i, boolean z);

    public static native void native_cancelRequestsForGuid(int i);

    public static native void native_cleanUp();

    public static native int native_getConnectionState();

    public static native int native_getCurrentTime();

    public static native long native_getCurrentTimeMillis();

    public static native int native_getTimeDifference();

    public static native void native_init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i4, boolean z, boolean z2, int i5);

    public static native int native_isTestBackend();

    public static native void native_pauseNetwork();

    public static native void native_resumeNetwork(boolean z);

    public static native void native_sendRequest(int i, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(String str);

    public static native void native_setNetworkAvailable(boolean z, int i);

    public static native void native_setProxySettings(String str, int i, String str2, String str3);

    public static native void native_setPushConnectionEnabled(boolean z);

    public static native void native_setUseIpv6(boolean z);

    public static native void native_setUserId(int i);

    public static native void native_switchBackend();

    public static native void native_updateDcSettings();

    public static ConnectionsManager getInstance() {
        ConnectionsManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ConnectionsManager localInstance2 = new ConnectionsManager();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public ConnectionsManager() {
        String systemLangCode;
        String langCode;
        String deviceModel;
        String appVersion;
        String systemVersion;
        String configPath = ApplicationLoader.getFilesDirFixed().toString();
        boolean enablePushConnection = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushConnection", true);
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639();
            langCode = LocaleController.getLocaleStringIso639();
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
            langCode = "en";
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
        UserConfig.loadConfig();
        init(BuildVars.BUILD_VERSION, 73, BuildVars.APP_ID, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), enablePushConnection);
        try {
            this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, JoinPoint.SYNCHRONIZATION_LOCK);
            this.wakeLock.setReferenceCounted(false);
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis();
    }

    public int getCurrentTime() {
        return native_getCurrentTime();
    }

    public int getTimeDifference() {
        return native_getTimeDifference();
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
        final int requestToken = this.lastRequestToken.getAndIncrement();
        final TLObject tLObject = object;
        final RequestDelegate requestDelegate = onComplete;
        final QuickAckDelegate quickAckDelegate = onQuickAck;
        final WriteToSocketDelegate writeToSocketDelegate = onWriteToSocket;
        final int i = flags;
        final int i2 = datacenterId;
        final int i3 = connetionType;
        final boolean z = immediate;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                FileLog.d("send request " + tLObject + " with token = " + requestToken);
                try {
                    NativeByteBuffer buffer = new NativeByteBuffer(tLObject.getObjectSize());
                    tLObject.serializeToStream(buffer);
                    tLObject.freeResources();
                    ConnectionsManager.native_sendRequest(buffer.address, new RequestDelegateInternal() {
                        public void run(int response, int errorCode, String errorText, int networkType) {
                            TLObject resp = null;
                            TL_error error = null;
                            if (response != 0) {
                                try {
                                    NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                                    buff.reused = true;
                                    resp = tLObject.deserializeResponse(buff, buff.readInt32(true), true);
                                } catch (Exception e) {
                                    e = e;
                                    FileLog.e(e);
                                    return;
                                }
                            } else if (errorText != null) {
                                TL_error error2 = new TL_error();
                                try {
                                    error2.code = errorCode;
                                    error2.text = errorText;
                                    FileLog.e(tLObject + " got error " + error2.code + " " + error2.text);
                                    error = error2;
                                } catch (Exception e2) {
                                    Throwable e3;
                                    e3 = e2;
                                    error = error2;
                                    FileLog.e(e3);
                                    return;
                                }
                            }
                            if (resp != null) {
                                resp.networkType = networkType;
                            }
                            FileLog.d("java received " + resp + " error = " + error);
                            final TLObject finalResponse = resp;
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
                    }, quickAckDelegate, writeToSocketDelegate, i, i2, i3, z, requestToken);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
        return requestToken;
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(token, notifyServer);
    }

    public void cleanup() {
        native_cleanUp();
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port) {
        native_applyDatacenterAddress(datacenterId, ipAddress, port);
    }

    public int getConnectionState() {
        if (this.connectionState == 3 && this.isUpdating) {
            return 5;
        }
        return this.connectionState;
    }

    public void setUserId(int id) {
        native_setUserId(id);
    }

    private void checkConnection() {
        native_setUseIpv6(useIpv6Address());
        native_setNetworkAvailable(isNetworkOnline(), getCurrentNetworkType());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, int userId, boolean enablePushConnection) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", "");
        String proxyUsername = preferences.getString("proxy_user", "");
        String proxyPassword = preferences.getString("proxy_pass", "");
        int proxyPort = preferences.getInt("proxy_port", 1080);
        if (preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(proxyAddress)) {
            native_setProxySettings(proxyAddress, proxyPort, proxyUsername, proxyPassword);
        }
        native_init(version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, userId, enablePushConnection, isNetworkOnline(), getCurrentNetworkType());
        checkConnection();
        ApplicationLoader.applicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ConnectionsManager.this.checkConnection();
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void setLangCode(String langCode) {
        native_setLangCode(langCode);
    }

    public void switchBackend() {
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("language_showed2").commit();
        native_switchBackend();
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(true);
    }

    public void updateDcSettings() {
        native_updateDcSettings();
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            this.appPaused = value;
            FileLog.d("app paused = " + value);
            if (value) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            FileLog.d("app resume count " + this.appResumeCount);
            if (this.appResumeCount < 0) {
                this.appResumeCount = 0;
            }
        }
        if (this.appResumeCount == 0) {
            if (this.lastPauseTime == 0) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork();
        } else if (!this.appPaused) {
            FileLog.e("reset app pause time");
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                ContactsController.getInstance().checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(false);
        }
    }

    public static void onUnparsedMessageReceived(int address) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            final TLObject message = TLClassStore.Instance().TLdeserialize(buff, buff.readInt32(true), true);
            if (message instanceof Updates) {
                FileLog.d("java received " + message);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (ConnectionsManager.getInstance().wakeLock.isHeld()) {
                            FileLog.d("release wakelock");
                            ConnectionsManager.getInstance().wakeLock.release();
                        }
                    }
                });
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesController.getInstance().processUpdates((Updates) message, false);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onUpdate() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.getInstance().updateTimerProc();
            }
        });
    }

    public static void onSessionCreated() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.getInstance().getDifference();
            }
        });
    }

    public static void onConnectionStateChanged(final int state) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ConnectionsManager.getInstance().connectionState = state;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
            }
        });
    }

    public static void onLogout() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (UserConfig.getClientUserId() != 0) {
                    UserConfig.clearConfig();
                    MessagesController.getInstance().performLogout(false);
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

    public static void onBytesSent(int amount, int networkType) {
        try {
            StatsController.getInstance().incrementSentBytesCount(networkType, 6, (long) amount);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(int second) {
        if (currentTask != null) {
            return;
        }
        if ((second == 1 || Math.abs(lastDnsRequestTime - System.currentTimeMillis()) >= 10000) && isNetworkOnline()) {
            lastDnsRequestTime = System.currentTimeMillis();
            if (second == 1) {
                DnsTxtLoadTask task = new DnsTxtLoadTask();
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task;
                return;
            }
            DnsLoadTask task2 = new DnsLoadTask();
            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            currentTask = task2;
        }
    }

    public static void onBytesReceived(int amount, int networkType) {
        try {
            StatsController.getInstance().incrementReceivedBytesCount(networkType, 6, (long) amount);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onUpdateConfig(int address) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            final TL_config message = TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesController.getInstance().updateConfig(message);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void onInternalPushReceived() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                try {
                    if (!ConnectionsManager.getInstance().wakeLock.isHeld()) {
                        ConnectionsManager.getInstance().wakeLock.acquire(10000);
                        FileLog.d("acquire wakelock");
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
    }

    public int generateClassGuid() {
        int i = this.lastClassGuid;
        this.lastClassGuid = i + 1;
        return i;
    }

    public static boolean isRoaming() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (netInfo != null) {
                return netInfo.isRoaming();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            State state = netInfo.getState();
            if (netInfo != null && (state == State.CONNECTED || state == State.CONNECTING || state == State.SUSPENDED)) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectedToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            if (netInfo != null && netInfo.getState() == State.CONNECTED) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        return false;
    }

    public void setIsUpdating(final boolean value) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (ConnectionsManager.this.isUpdating != value) {
                    ConnectionsManager.this.isUpdating = value;
                    if (ConnectionsManager.this.connectionState == 3) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
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
        NetworkInterface networkInterface;
        List<InterfaceAddress> interfaceAddresses;
        int a;
        InetAddress inetAddress;
        if (BuildVars.DEBUG_VERSION) {
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (!(!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.getInterfaceAddresses().isEmpty())) {
                        FileLog.e("valid interface: " + networkInterface);
                        interfaceAddresses = networkInterface.getInterfaceAddresses();
                        for (a = 0; a < interfaceAddresses.size(); a++) {
                            inetAddress = ((InterfaceAddress) interfaceAddresses.get(a)).getAddress();
                            if (BuildVars.DEBUG_VERSION) {
                                FileLog.e("address: " + inetAddress.getHostAddress());
                            }
                            if (!(inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress() || !BuildVars.DEBUG_VERSION)) {
                                FileLog.e("address is good");
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
            FileLog.e(e);
            return true;
        }
    }
}
