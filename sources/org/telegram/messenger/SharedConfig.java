package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import androidx.core.content.pm.ShortcutManagerCompat;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;

public class SharedConfig {
    public static final int PERFORMANCE_CLASS_AVERAGE = 1;
    public static final int PERFORMANCE_CLASS_HIGH = 2;
    public static final int PERFORMANCE_CLASS_LOW = 0;
    public static boolean allowBigEmoji;
    public static boolean allowScreenCapture;
    public static boolean appLocked;
    public static boolean archiveHidden;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    public static boolean autoplayVideo = true;
    public static int badPasscodeTries;
    public static int bubbleRadius = 10;
    public static boolean chatBubbles = (Build.VERSION.SDK_INT >= 30);
    private static int chatSwipeAction;
    private static boolean configLoaded;
    public static ProxyInfo currentProxy;
    public static boolean customTabs = true;
    public static int dayNightThemeSwitchHintCount;
    private static int devicePerformanceClass;
    public static boolean directShare = true;
    public static String directShareHash;
    public static boolean disableVoiceAudioEffects;
    public static int distanceSystemType;
    public static boolean dontAskManageStorage;
    public static boolean drawDialogIcons;
    public static int emojiInteractionsHintCount;
    public static int fastScrollHintCount = 3;
    public static int fontSize = 16;
    public static boolean forwardingOptionsHintShown;
    public static boolean hasCameraCache;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter;
    public static int ivFontSize = 16;
    public static int keepMedia = 2;
    public static int lastKeepMediaCheckTime;
    private static int lastLocalId = -210000;
    public static int lastLogsCheckTime;
    public static int lastPauseTime;
    public static long lastUpdateCheckTime;
    public static String lastUpdateVersion;
    public static long lastUptimeMillis;
    private static final Object localIdSync = new Object();
    public static int lockRecordAudioVideoHint;
    public static boolean loopStickers;
    public static int mapPreviewType = 2;
    public static int mediaColumnsCount = 3;
    public static int messageSeenHintCount;
    public static boolean noSoundHintShowed = false;
    public static boolean noStatusBar;
    public static boolean noiseSupression;
    public static String passcodeHash = "";
    public static long passcodeRetryInMs;
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType;
    public static int passportConfigHash;
    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap;
    public static boolean pauseMusicOnRecord = true;
    public static TLRPC.TL_help_appUpdate pendingAppUpdate;
    public static int pendingAppUpdateBuildVersion;
    public static boolean playOrderReversed;
    public static ArrayList<ProxyInfo> proxyList = new ArrayList<>();
    private static boolean proxyListLoaded;
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;
    public static boolean pushStatSent;
    public static String pushString = "";
    public static long pushStringGetTimeEnd;
    public static long pushStringGetTimeStart;
    public static String pushStringStatus = "";
    public static boolean raiseToSpeak = true;
    public static int repeatMode;
    public static boolean roundCamera16to9 = true;
    public static boolean saveIncomingPhotos;
    public static boolean saveStreamMedia = true;
    public static boolean saveToGallery;
    public static int scheduledOrNoSoundHintShows;
    public static int searchMessagesAsListHintShows;
    public static boolean searchMessagesAsListUsed;
    public static boolean showNotificationsForAllAccounts = true;
    public static boolean shuffleMusic;
    public static boolean smoothKeyboard = true;
    public static boolean sortContactsByName;
    public static boolean sortFilesByName;
    public static boolean stickersReorderingHintUsed;
    public static String storageCacheDir;
    public static boolean streamAllVideo = false;
    public static boolean streamMedia = true;
    public static boolean streamMkv = false;
    public static int suggestStickers;
    private static final Object sync = new Object();
    public static int textSelectionHintShows;
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;
    public static boolean useThreeLinesLayout;

    static {
        loadConfig();
    }

    public static class ProxyInfo {
        public String address;
        public boolean available;
        public long availableCheckTime;
        public boolean checking;
        public String password;
        public long ping;
        public int port;
        public long proxyCheckPingId;
        public String secret;
        public String username;

        public ProxyInfo(String a, int p, String u, String pw, String s) {
            this.address = a;
            this.port = p;
            this.username = u;
            this.password = pw;
            this.secret = s;
            if (a == null) {
                this.address = "";
            }
            if (pw == null) {
                this.password = "";
            }
            if (u == null) {
                this.username = "";
            }
            if (s == null) {
                this.secret = "";
            }
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                editor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                editor.putString("passcodeHash1", passcodeHash);
                byte[] bArr = passcodeSalt;
                editor.putString("passcodeSalt", bArr.length > 0 ? Base64.encodeToString(bArr, 0) : "");
                editor.putBoolean("appLocked", appLocked);
                editor.putInt("passcodeType", passcodeType);
                editor.putLong("passcodeRetryInMs", passcodeRetryInMs);
                editor.putLong("lastUptimeMillis", lastUptimeMillis);
                editor.putInt("badPasscodeTries", badPasscodeTries);
                editor.putInt("autoLockIn", autoLockIn);
                editor.putInt("lastPauseTime", lastPauseTime);
                editor.putString("lastUpdateVersion2", lastUpdateVersion);
                editor.putBoolean("useFingerprint", useFingerprint);
                editor.putBoolean("allowScreenCapture", allowScreenCapture);
                editor.putString("pushString2", pushString);
                editor.putBoolean("pushStatSent", pushStatSent);
                byte[] bArr2 = pushAuthKey;
                editor.putString("pushAuthKey", bArr2 != null ? Base64.encodeToString(bArr2, 0) : "");
                editor.putInt("lastLocalId", lastLocalId);
                editor.putString("passportConfigJson", passportConfigJson);
                editor.putInt("passportConfigHash", passportConfigHash);
                editor.putBoolean("sortContactsByName", sortContactsByName);
                editor.putBoolean("sortFilesByName", sortFilesByName);
                editor.putInt("textSelectionHintShows", textSelectionHintShows);
                editor.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                editor.putBoolean("forwardingOptionsHintShown", forwardingOptionsHintShown);
                editor.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                editor.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                if (pendingAppUpdate != null) {
                    try {
                        SerializedData data = new SerializedData(pendingAppUpdate.getObjectSize());
                        pendingAppUpdate.serializeToStream(data);
                        editor.putString("appUpdate", Base64.encodeToString(data.toByteArray(), 0));
                        editor.putInt("appUpdateBuild", pendingAppUpdateBuildVersion);
                        data.cleanup();
                    } catch (Exception e) {
                    }
                } else {
                    editor.remove("appUpdate");
                }
                editor.putLong("appUpdateCheckTime", lastUpdateCheckTime);
                editor.commit();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public static int getLastLocalId() {
        int value;
        synchronized (localIdSync) {
            value = lastLocalId;
            lastLocalId = value - 1;
        }
        return value;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0364, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void loadConfig() {
        /*
            java.lang.Object r0 = sync
            monitor-enter(r0)
            boolean r1 = configLoaded     // Catch:{ all -> 0x0365 }
            if (r1 != 0) goto L_0x0363
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0365 }
            if (r1 != 0) goto L_0x000d
            goto L_0x0363
        L_0x000d:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "userconfing"
            r3 = 0
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "saveIncomingPhotos"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0365 }
            saveIncomingPhotos = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "passcodeHash1"
            java.lang.String r4 = ""
            java.lang.String r2 = r1.getString(r2, r4)     // Catch:{ all -> 0x0365 }
            passcodeHash = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "appLocked"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0365 }
            appLocked = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "passcodeType"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0365 }
            passcodeType = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "passcodeRetryInMs"
            r4 = 0
            long r6 = r1.getLong(r2, r4)     // Catch:{ all -> 0x0365 }
            passcodeRetryInMs = r6     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "lastUptimeMillis"
            long r4 = r1.getLong(r2, r4)     // Catch:{ all -> 0x0365 }
            lastUptimeMillis = r4     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "badPasscodeTries"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0365 }
            badPasscodeTries = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "autoLockIn"
            r4 = 3600(0xe10, float:5.045E-42)
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x0365 }
            autoLockIn = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "lastPauseTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0365 }
            lastPauseTime = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "useFingerprint"
            r4 = 1
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0365 }
            useFingerprint = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "lastUpdateVersion2"
            java.lang.String r5 = "3.5"
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0365 }
            lastUpdateVersion = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "allowScreenCapture"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0365 }
            allowScreenCapture = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "lastLocalId"
            r5 = -210000(0xfffffffffffccbb0, float:NaN)
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x0365 }
            lastLocalId = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "pushString2"
            java.lang.String r5 = ""
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0365 }
            pushString = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "pushStatSent"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0365 }
            pushStatSent = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "passportConfigJson"
            java.lang.String r5 = ""
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0365 }
            passportConfigJson = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "passportConfigHash"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0365 }
            passportConfigHash = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "storageCacheDir"
            r5 = 0
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0365 }
            storageCacheDir = r2     // Catch:{ all -> 0x0365 }
            java.lang.String r2 = "pushAuthKey"
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0365 }
            boolean r6 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0365 }
            if (r6 != 0) goto L_0x00c9
            byte[] r6 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x0365 }
            pushAuthKey = r6     // Catch:{ all -> 0x0365 }
        L_0x00c9:
            java.lang.String r6 = passcodeHash     // Catch:{ all -> 0x0365 }
            int r6 = r6.length()     // Catch:{ all -> 0x0365 }
            if (r6 <= 0) goto L_0x00e2
            int r6 = lastPauseTime     // Catch:{ all -> 0x0365 }
            if (r6 != 0) goto L_0x00e2
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0365 }
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            r8 = 600(0x258, double:2.964E-321)
            long r6 = r6 - r8
            int r7 = (int) r6     // Catch:{ all -> 0x0365 }
            lastPauseTime = r7     // Catch:{ all -> 0x0365 }
        L_0x00e2:
            java.lang.String r6 = "passcodeSalt"
            java.lang.String r7 = ""
            java.lang.String r6 = r1.getString(r6, r7)     // Catch:{ all -> 0x0365 }
            int r7 = r6.length()     // Catch:{ all -> 0x0365 }
            if (r7 <= 0) goto L_0x00f7
            byte[] r7 = android.util.Base64.decode(r6, r3)     // Catch:{ all -> 0x0365 }
            passcodeSalt = r7     // Catch:{ all -> 0x0365 }
            goto L_0x00fb
        L_0x00f7:
            byte[] r7 = new byte[r3]     // Catch:{ all -> 0x0365 }
            passcodeSalt = r7     // Catch:{ all -> 0x0365 }
        L_0x00fb:
            java.lang.String r7 = "appUpdateCheckTime"
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0365 }
            long r7 = r1.getLong(r7, r8)     // Catch:{ all -> 0x0365 }
            lastUpdateCheckTime = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "appUpdate"
            java.lang.String r7 = r1.getString(r7, r5)     // Catch:{ Exception -> 0x017c }
            if (r7 == 0) goto L_0x0133
            java.lang.String r8 = "appUpdateBuild"
            int r9 = org.telegram.messenger.BuildVars.BUILD_VERSION     // Catch:{ Exception -> 0x017c }
            int r8 = r1.getInt(r8, r9)     // Catch:{ Exception -> 0x017c }
            pendingAppUpdateBuildVersion = r8     // Catch:{ Exception -> 0x017c }
            byte[] r8 = android.util.Base64.decode(r7, r3)     // Catch:{ Exception -> 0x017c }
            if (r8 == 0) goto L_0x0133
            org.telegram.tgnet.SerializedData r9 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x017c }
            r9.<init>((byte[]) r8)     // Catch:{ Exception -> 0x017c }
            int r10 = r9.readInt32(r3)     // Catch:{ Exception -> 0x017c }
            org.telegram.tgnet.TLRPC$help_AppUpdate r10 = org.telegram.tgnet.TLRPC.help_AppUpdate.TLdeserialize(r9, r10, r3)     // Catch:{ Exception -> 0x017c }
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r10 = (org.telegram.tgnet.TLRPC.TL_help_appUpdate) r10     // Catch:{ Exception -> 0x017c }
            pendingAppUpdate = r10     // Catch:{ Exception -> 0x017c }
            r9.cleanup()     // Catch:{ Exception -> 0x017c }
        L_0x0133:
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r8 = pendingAppUpdate     // Catch:{ Exception -> 0x017c }
            if (r8 == 0) goto L_0x017b
            r8 = 0
            r10 = 0
            r11 = 0
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0152 }
            android.content.pm.PackageManager r12 = r12.getPackageManager()     // Catch:{ Exception -> 0x0152 }
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0152 }
            java.lang.String r13 = r13.getPackageName()     // Catch:{ Exception -> 0x0152 }
            android.content.pm.PackageInfo r12 = r12.getPackageInfo(r13, r3)     // Catch:{ Exception -> 0x0152 }
            int r13 = r12.versionCode     // Catch:{ Exception -> 0x0152 }
            r10 = r13
            java.lang.String r13 = r12.versionName     // Catch:{ Exception -> 0x0152 }
            r11 = r13
            goto L_0x0156
        L_0x0152:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ Exception -> 0x017c }
        L_0x0156:
            if (r10 != 0) goto L_0x015b
            int r12 = org.telegram.messenger.BuildVars.BUILD_VERSION     // Catch:{ Exception -> 0x017c }
            r10 = r12
        L_0x015b:
            if (r11 != 0) goto L_0x0160
            java.lang.String r12 = org.telegram.messenger.BuildVars.BUILD_VERSION_STRING     // Catch:{ Exception -> 0x017c }
            r11 = r12
        L_0x0160:
            int r12 = pendingAppUpdateBuildVersion     // Catch:{ Exception -> 0x017c }
            if (r12 != r10) goto L_0x0174
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r12 = pendingAppUpdate     // Catch:{ Exception -> 0x017c }
            java.lang.String r12 = r12.version     // Catch:{ Exception -> 0x017c }
            if (r12 == 0) goto L_0x0174
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r12 = pendingAppUpdate     // Catch:{ Exception -> 0x017c }
            java.lang.String r12 = r12.version     // Catch:{ Exception -> 0x017c }
            int r12 = r11.compareTo(r12)     // Catch:{ Exception -> 0x017c }
            if (r12 < 0) goto L_0x017b
        L_0x0174:
            pendingAppUpdate = r5     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.SharedConfig$$ExternalSyntheticLambda3 r12 = org.telegram.messenger.SharedConfig$$ExternalSyntheticLambda3.INSTANCE     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)     // Catch:{ Exception -> 0x017c }
        L_0x017b:
            goto L_0x0180
        L_0x017c:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x0365 }
        L_0x0180:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0365 }
            java.lang.String r8 = "mainconfig"
            android.content.SharedPreferences r7 = r7.getSharedPreferences(r8, r3)     // Catch:{ all -> 0x0365 }
            r1 = r7
            java.lang.String r7 = "save_gallery"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            saveToGallery = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "autoplay_gif"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            autoplayGifs = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "autoplay_video"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            autoplayVideo = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "mapPreviewType"
            r8 = 2
            int r7 = r1.getInt(r7, r8)     // Catch:{ all -> 0x0365 }
            mapPreviewType = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "raise_to_speak"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            raiseToSpeak = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "custom_tabs"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            customTabs = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "direct_share"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            directShare = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "shuffleMusic"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            shuffleMusic = r7     // Catch:{ all -> 0x0365 }
            if (r7 != 0) goto L_0x01d6
            java.lang.String r7 = "playOrderReversed"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            if (r7 == 0) goto L_0x01d6
            r7 = 1
            goto L_0x01d7
        L_0x01d6:
            r7 = 0
        L_0x01d7:
            playOrderReversed = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "inappCamera"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            inappCamera = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "cameraCache"
            boolean r7 = r1.contains(r7)     // Catch:{ all -> 0x0365 }
            hasCameraCache = r7     // Catch:{ all -> 0x0365 }
            roundCamera16to9 = r4     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "repeatMode"
            int r7 = r1.getInt(r7, r3)     // Catch:{ all -> 0x0365 }
            repeatMode = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "fons_size"
            boolean r9 = org.telegram.messenger.AndroidUtilities.isTablet()     // Catch:{ all -> 0x0365 }
            if (r9 == 0) goto L_0x01fe
            r9 = 18
            goto L_0x0200
        L_0x01fe:
            r9 = 16
        L_0x0200:
            int r7 = r1.getInt(r7, r9)     // Catch:{ all -> 0x0365 }
            fontSize = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "bubbleRadius"
            r9 = 10
            int r7 = r1.getInt(r7, r9)     // Catch:{ all -> 0x0365 }
            bubbleRadius = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "iv_font_size"
            int r9 = fontSize     // Catch:{ all -> 0x0365 }
            int r7 = r1.getInt(r7, r9)     // Catch:{ all -> 0x0365 }
            ivFontSize = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "allowBigEmoji"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            allowBigEmoji = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "useSystemEmoji"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            useSystemEmoji = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "streamMedia"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            streamMedia = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "saveStreamMedia"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            saveStreamMedia = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "smoothKeyboard2"
            boolean r7 = r1.getBoolean(r7, r4)     // Catch:{ all -> 0x0365 }
            smoothKeyboard = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "pauseMusicOnRecord"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            pauseMusicOnRecord = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "streamAllVideo"
            boolean r9 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ all -> 0x0365 }
            boolean r7 = r1.getBoolean(r7, r9)     // Catch:{ all -> 0x0365 }
            streamAllVideo = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "streamMkv"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            streamMkv = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "suggestStickers"
            int r7 = r1.getInt(r7, r3)     // Catch:{ all -> 0x0365 }
            suggestStickers = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "sortContactsByName"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            sortContactsByName = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "sortFilesByName"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            sortFilesByName = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "noSoundHintShowed"
            boolean r7 = r1.getBoolean(r7, r3)     // Catch:{ all -> 0x0365 }
            noSoundHintShowed = r7     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "directShareHash2"
            java.lang.String r5 = r1.getString(r7, r5)     // Catch:{ all -> 0x0365 }
            directShareHash = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "useThreeLinesLayout"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            useThreeLinesLayout = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "archiveHidden"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            archiveHidden = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "distanceSystemType"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            distanceSystemType = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "devicePerformanceClass"
            r7 = -1
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            devicePerformanceClass = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "loopStickers"
            boolean r5 = r1.getBoolean(r5, r4)     // Catch:{ all -> 0x0365 }
            loopStickers = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "keep_media"
            int r5 = r1.getInt(r5, r8)     // Catch:{ all -> 0x0365 }
            keepMedia = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "noStatusBar"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            noStatusBar = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "lastKeepMediaCheckTime"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            lastKeepMediaCheckTime = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "lastLogsCheckTime"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            lastLogsCheckTime = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "searchMessagesAsListHintShows"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            searchMessagesAsListHintShows = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "searchMessagesAsListUsed"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            searchMessagesAsListUsed = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "stickersReorderingHintUsed"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            stickersReorderingHintUsed = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "textSelectionHintShows"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            textSelectionHintShows = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "scheduledOrNoSoundHintShows"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            scheduledOrNoSoundHintShows = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "forwardingOptionsHintShown"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            forwardingOptionsHintShown = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "lockRecordAudioVideoHint"
            int r5 = r1.getInt(r5, r3)     // Catch:{ all -> 0x0365 }
            lockRecordAudioVideoHint = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "disableVoiceAudioEffects"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            disableVoiceAudioEffects = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "noiseSupression"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            noiseSupression = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "ChatSwipeAction"
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            chatSwipeAction = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "messageSeenCount"
            r7 = 3
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            messageSeenHintCount = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "emojiInteractionsHintCount"
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            emojiInteractionsHintCount = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "dayNightThemeSwitchHintCount"
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            dayNightThemeSwitchHintCount = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "mediaColumnsCount"
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            mediaColumnsCount = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "fastScrollHintCount"
            int r5 = r1.getInt(r5, r7)     // Catch:{ all -> 0x0365 }
            fastScrollHintCount = r5     // Catch:{ all -> 0x0365 }
            java.lang.String r5 = "dontAskManageStorage"
            boolean r5 = r1.getBoolean(r5, r3)     // Catch:{ all -> 0x0365 }
            dontAskManageStorage = r5     // Catch:{ all -> 0x0365 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0365 }
            java.lang.String r7 = "Notifications"
            android.content.SharedPreferences r3 = r5.getSharedPreferences(r7, r3)     // Catch:{ all -> 0x0365 }
            r1 = r3
            java.lang.String r3 = "AllAccounts"
            boolean r3 = r1.getBoolean(r3, r4)     // Catch:{ all -> 0x0365 }
            showNotificationsForAllAccounts = r3     // Catch:{ all -> 0x0365 }
            configLoaded = r4     // Catch:{ all -> 0x0365 }
            monitor-exit(r0)     // Catch:{ all -> 0x0365 }
            return
        L_0x0363:
            monitor-exit(r0)     // Catch:{ all -> 0x0365 }
            return
        L_0x0365:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0365 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SharedConfig.loadConfig():void");
    }

    public static void increaseBadPasscodeTries() {
        int i = badPasscodeTries + 1;
        badPasscodeTries = i;
        if (i >= 3) {
            switch (i) {
                case 3:
                    passcodeRetryInMs = 5000;
                    break;
                case 4:
                    passcodeRetryInMs = 10000;
                    break;
                case 5:
                    passcodeRetryInMs = 15000;
                    break;
                case 6:
                    passcodeRetryInMs = 20000;
                    break;
                case 7:
                    passcodeRetryInMs = 25000;
                    break;
                default:
                    passcodeRetryInMs = 30000;
                    break;
            }
            lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static boolean isPassportConfigLoaded() {
        return passportConfigMap != null;
    }

    public static void setPassportConfig(String json, int hash) {
        passportConfigMap = null;
        passportConfigJson = json;
        passportConfigHash = hash;
        saveConfig();
        getCountryLangs();
    }

    public static HashMap<String, String> getCountryLangs() {
        if (passportConfigMap == null) {
            passportConfigMap = new HashMap<>();
            try {
                JSONObject object = new JSONObject(passportConfigJson);
                Iterator<String> iter = object.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    passportConfigMap.put(key.toUpperCase(), object.getString(key).toUpperCase());
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        return passportConfigMap;
    }

    public static boolean isAppUpdateAvailable() {
        int currentVersion;
        TLRPC.TL_help_appUpdate tL_help_appUpdate = pendingAppUpdate;
        if (tL_help_appUpdate == null || tL_help_appUpdate.document == null || !BuildVars.isStandaloneApp()) {
            return false;
        }
        try {
            currentVersion = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            currentVersion = BuildVars.BUILD_VERSION;
        }
        if (pendingAppUpdateBuildVersion == currentVersion) {
            return true;
        }
        return false;
    }

    public static boolean setNewAppVersionAvailable(TLRPC.TL_help_appUpdate update) {
        String updateVersionString = null;
        int versionCode = 0;
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            updateVersionString = packageInfo.versionName;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (versionCode == 0) {
            versionCode = BuildVars.BUILD_VERSION;
        }
        if (updateVersionString == null) {
            updateVersionString = BuildVars.BUILD_VERSION_STRING;
        }
        if (update.version == null || updateVersionString.compareTo(update.version) >= 0) {
            return false;
        }
        pendingAppUpdate = update;
        pendingAppUpdateBuildVersion = versionCode;
        saveConfig();
        return true;
    }

    public static boolean checkPasscode(String passcode) {
        if (passcodeSalt.length == 0) {
            boolean result = Utilities.MD5(passcode).equals(passcodeHash);
            if (result) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    byte[] passcodeBytes = passcode.getBytes("UTF-8");
                    byte[] bytes = new byte[(passcodeBytes.length + 32)];
                    System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                    saveConfig();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            return result;
        }
        try {
            byte[] passcodeBytes2 = passcode.getBytes("UTF-8");
            byte[] bytes2 = new byte[(passcodeBytes2.length + 32)];
            System.arraycopy(passcodeSalt, 0, bytes2, 0, 16);
            System.arraycopy(passcodeBytes2, 0, bytes2, 16, passcodeBytes2.length);
            System.arraycopy(passcodeSalt, 0, bytes2, passcodeBytes2.length + 16, 16);
            return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bytes2, 0, bytes2.length)));
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return false;
        }
    }

    public static void clearConfig() {
        saveIncomingPhotos = false;
        appLocked = false;
        passcodeType = 0;
        passcodeRetryInMs = 0;
        lastUptimeMillis = 0;
        badPasscodeTries = 0;
        passcodeHash = "";
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        lastPauseTime = 0;
        useFingerprint = true;
        isWaitingForPasscodeEnter = false;
        allowScreenCapture = false;
        lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
        textSelectionHintShows = 0;
        scheduledOrNoSoundHintShows = 0;
        lockRecordAudioVideoHint = 0;
        forwardingOptionsHintShown = false;
        messageSeenHintCount = 3;
        emojiInteractionsHintCount = 3;
        dayNightThemeSwitchHintCount = 3;
        saveConfig();
    }

    public static void setSuggestStickers(int type) {
        suggestStickers = type;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("suggestStickers", suggestStickers);
        editor.commit();
    }

    public static void setSearchMessagesAsListUsed(boolean value) {
        searchMessagesAsListUsed = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        editor.commit();
    }

    public static void setStickersReorderingHintUsed(boolean value) {
        stickersReorderingHintUsed = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("stickersReorderingHintUsed", stickersReorderingHintUsed);
        editor.commit();
    }

    public static void increaseTextSelectionHintShowed() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        int i = textSelectionHintShows + 1;
        textSelectionHintShows = i;
        editor.putInt("textSelectionHintShows", i);
        editor.commit();
    }

    public static void removeTextSelectionHint() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("textSelectionHintShows", 3);
        editor.commit();
    }

    public static void increaseScheduledOrNoSuoundHintShowed() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        int i = scheduledOrNoSoundHintShows + 1;
        scheduledOrNoSoundHintShows = i;
        editor.putInt("scheduledOrNoSoundHintShows", i);
        editor.commit();
    }

    public static void forwardingOptionsHintHintShowed() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        forwardingOptionsHintShown = true;
        editor.putBoolean("forwardingOptionsHintShown", true);
        editor.commit();
    }

    public static void removeScheduledOrNoSuoundHint() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("scheduledOrNoSoundHintShows", 3);
        editor.commit();
    }

    public static void increaseLockRecordAudioVideoHintShowed() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        int i = lockRecordAudioVideoHint + 1;
        lockRecordAudioVideoHint = i;
        editor.putInt("lockRecordAudioVideoHint", i);
        editor.commit();
    }

    public static void removeLockRecordAudioVideoHint() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("lockRecordAudioVideoHint", 3);
        editor.commit();
    }

    public static void increaseSearchAsListHintShows() {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        int i = searchMessagesAsListHintShows + 1;
        searchMessagesAsListHintShows = i;
        editor.putInt("searchMessagesAsListHintShows", i);
        editor.commit();
    }

    public static void setKeepMedia(int value) {
        keepMedia = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("keep_media", keepMedia);
        editor.commit();
    }

    public static void checkLogsToDelete() {
        if (BuildVars.LOGS_ENABLED) {
            int time = (int) (System.currentTimeMillis() / 1000);
            if (Math.abs(time - lastLogsCheckTime) >= 3600) {
                lastLogsCheckTime = time;
                Utilities.cacheClearQueue.postRunnable(new SharedConfig$$ExternalSyntheticLambda0(time));
            }
        }
    }

    static /* synthetic */ void lambda$checkLogsToDelete$0(int time) {
        long currentTime = (long) (time - 864000);
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
            Utilities.clearDir(new File(sdCard.getAbsolutePath() + "/logs").getAbsolutePath(), 0, currentTime, false);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("lastLogsCheckTime", lastLogsCheckTime);
        editor.commit();
    }

    public static void checkKeepMedia() {
        int time = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(time - lastKeepMediaCheckTime) >= 3600) {
            lastKeepMediaCheckTime = time;
            Utilities.cacheClearQueue.postRunnable(new SharedConfig$$ExternalSyntheticLambda1(time, FileLoader.checkDirectory(4)));
        }
    }

    static /* synthetic */ void lambda$checkKeepMedia$1(int time, File cacheDir) {
        int days;
        int i = keepMedia;
        if (i != 2) {
            if (i == 0) {
                days = 7;
            } else if (i == 1) {
                days = 30;
            } else {
                days = 3;
            }
            long currentTime = (long) (time - (days * 86400));
            SparseArray<File> paths = ImageLoader.getInstance().createMediaPaths();
            for (int a = 0; a < paths.size(); a++) {
                if (paths.keyAt(a) != 4) {
                    try {
                        Utilities.clearDir(paths.valueAt(a).getAbsolutePath(), 0, currentTime, false);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        File stickersPath = new File(cacheDir, "acache");
        if (stickersPath.exists()) {
            try {
                Utilities.clearDir(stickersPath.getAbsolutePath(), 0, (long) (time - 86400), false);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("lastKeepMediaCheckTime", lastKeepMediaCheckTime);
        editor.commit();
    }

    public static void toggleDisableVoiceAudioEffects() {
        disableVoiceAudioEffects = !disableVoiceAudioEffects;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
        editor.commit();
    }

    public static void toggleNoiseSupression() {
        noiseSupression = !noiseSupression;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("noiseSupression", noiseSupression);
        editor.commit();
    }

    public static void toggleNoStatusBar() {
        noStatusBar = !noStatusBar;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("noStatusBar", noStatusBar);
        editor.commit();
    }

    public static void toggleLoopStickers() {
        loopStickers = !loopStickers;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("loopStickers", loopStickers);
        editor.commit();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("allowBigEmoji", allowBigEmoji);
        editor.commit();
    }

    public static void setPlaybackOrderType(int type) {
        if (type == 2) {
            shuffleMusic = true;
            playOrderReversed = false;
        } else if (type == 1) {
            playOrderReversed = true;
            shuffleMusic = false;
        } else {
            playOrderReversed = false;
            shuffleMusic = false;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("shuffleMusic", shuffleMusic);
        editor.putBoolean("playOrderReversed", playOrderReversed);
        editor.commit();
    }

    public static void setRepeatMode(int mode) {
        repeatMode = mode;
        if (mode < 0 || mode > 2) {
            repeatMode = 0;
        }
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("repeatMode", repeatMode);
        editor.commit();
    }

    public static void toggleSaveToGallery() {
        saveToGallery = !saveToGallery;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("save_gallery", saveToGallery);
        editor.commit();
        checkSaveToGalleryFiles();
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("autoplay_gif", autoplayGifs);
        editor.commit();
    }

    public static void setUseThreeLinesLayout(boolean value) {
        useThreeLinesLayout = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        editor.commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("archiveHidden", archiveHidden);
        editor.commit();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo = !autoplayVideo;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("autoplay_video", autoplayVideo);
        editor.commit();
    }

    public static boolean isSecretMapPreviewSet() {
        return MessagesController.getGlobalMainSettings().contains("mapPreviewType");
    }

    public static void setSecretMapPreviewType(int value) {
        mapPreviewType = value;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("mapPreviewType", mapPreviewType);
        editor.commit();
    }

    public static void setNoSoundHintShowed(boolean value) {
        if (noSoundHintShowed != value) {
            noSoundHintShowed = value;
            SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
            editor.putBoolean("noSoundHintShowed", noSoundHintShowed);
            editor.commit();
        }
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.commit();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("custom_tabs", customTabs);
        editor.commit();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("direct_share", directShare);
        editor.commit();
        ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        MediaDataController.getInstance(UserConfig.selectedAccount).buildShortcuts();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamMedia", streamMedia);
        editor.commit();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("sortContactsByName", sortContactsByName);
        editor.commit();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName = !sortFilesByName;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("sortFilesByName", sortFilesByName);
        editor.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamAllVideo", streamAllVideo);
        editor.commit();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamMkv", streamMkv);
        editor.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("saveStreamMedia", saveStreamMedia);
        editor.commit();
    }

    public static void toggleSmoothKeyboard() {
        smoothKeyboard = !smoothKeyboard;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("smoothKeyboard2", smoothKeyboard);
        editor.commit();
    }

    public static void togglePauseMusicOnRecord() {
        pauseMusicOnRecord = !pauseMusicOnRecord;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("pauseMusicOnRecord", pauseMusicOnRecord);
        editor.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("inappCamera", inappCamera);
        editor.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("roundCamera16to9", roundCamera16to9);
        editor.commit();
    }

    public static void setDistanceSystemType(int type) {
        distanceSystemType = type;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("distanceSystemType", distanceSystemType);
        editor.commit();
        LocaleController.resetImperialSystemType();
    }

    public static void loadProxyList() {
        if (!proxyListLoaded) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String proxyAddress = preferences.getString("proxy_ip", "");
            String proxyUsername = preferences.getString("proxy_user", "");
            String proxyPassword = preferences.getString("proxy_pass", "");
            String proxySecret = preferences.getString("proxy_secret", "");
            int proxyPort = preferences.getInt("proxy_port", 1080);
            proxyListLoaded = true;
            proxyList.clear();
            currentProxy = null;
            String list = preferences.getString("proxy_list", (String) null);
            if (!TextUtils.isEmpty(list)) {
                SerializedData data = new SerializedData(Base64.decode(list, 0));
                int count = data.readInt32(false);
                for (int a = 0; a < count; a++) {
                    ProxyInfo proxyInfo = new ProxyInfo(data.readString(false), data.readInt32(false), data.readString(false), data.readString(false), data.readString(false));
                    proxyList.add(proxyInfo);
                    if (currentProxy == null && !TextUtils.isEmpty(proxyAddress) && proxyAddress.equals(proxyInfo.address) && proxyPort == proxyInfo.port && proxyUsername.equals(proxyInfo.username) && proxyPassword.equals(proxyInfo.password)) {
                        currentProxy = proxyInfo;
                    }
                }
                data.cleanup();
            }
            if (currentProxy == null && !TextUtils.isEmpty(proxyAddress)) {
                ProxyInfo info = new ProxyInfo(proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
                currentProxy = info;
                proxyList.add(0, info);
            }
        }
    }

    public static void saveProxyList() {
        SerializedData serializedData = new SerializedData();
        int count = proxyList.size();
        serializedData.writeInt32(count);
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            String str = "";
            serializedData.writeString(info.address != null ? info.address : str);
            serializedData.writeInt32(info.port);
            serializedData.writeString(info.username != null ? info.username : str);
            serializedData.writeString(info.password != null ? info.password : str);
            if (info.secret != null) {
                str = info.secret;
            }
            serializedData.writeString(str);
        }
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).commit();
        serializedData.cleanup();
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int count = proxyList.size();
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            if (proxyInfo.address.equals(info.address) && proxyInfo.port == info.port && proxyInfo.username.equals(info.username) && proxyInfo.password.equals(info.password) && proxyInfo.secret.equals(info.secret)) {
                return info;
            }
        }
        proxyList.add(proxyInfo);
        saveProxyList();
        return proxyInfo;
    }

    public static void deleteProxy(ProxyInfo proxyInfo) {
        if (currentProxy == proxyInfo) {
            currentProxy = null;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean enabled = preferences.getBoolean("proxy_enabled", false);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("proxy_ip", "");
            editor.putString("proxy_pass", "");
            editor.putString("proxy_user", "");
            editor.putString("proxy_secret", "");
            editor.putInt("proxy_port", 1080);
            editor.putBoolean("proxy_enabled", false);
            editor.putBoolean("proxy_enabled_calls", false);
            editor.commit();
            if (enabled) {
                ConnectionsManager.setProxySettings(false, "", 0, "", "", "");
            }
        }
        proxyList.remove(proxyInfo);
        saveProxyList();
    }

    public static void checkSaveToGalleryFiles() {
        Utilities.globalQueue.postRunnable(SharedConfig$$ExternalSyntheticLambda2.INSTANCE);
    }

    static /* synthetic */ void lambda$checkSaveToGalleryFiles$2() {
        try {
            File telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
            File imagePath = new File(telegramPath, "Telegram Images");
            imagePath.mkdir();
            File videoPath = new File(telegramPath, "Telegram Video");
            videoPath.mkdir();
            if (saveToGallery) {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").delete();
                }
                if (videoPath.isDirectory()) {
                    new File(videoPath, ".nomedia").delete();
                    return;
                }
                return;
            }
            if (imagePath.isDirectory()) {
                AndroidUtilities.createEmptyFile(new File(imagePath, ".nomedia"));
            }
            if (videoPath.isDirectory()) {
                AndroidUtilities.createEmptyFile(new File(videoPath, ".nomedia"));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static int getChatSwipeAction(int currentAccount) {
        int i = chatSwipeAction;
        if (i < 0) {
            return !MessagesController.getInstance(currentAccount).dialogFilters.isEmpty() ? 5 : 2;
        }
        if (i != 5 || !MessagesController.getInstance(currentAccount).dialogFilters.isEmpty()) {
            return chatSwipeAction;
        }
        return 2;
    }

    public static void updateChatListSwipeSetting(int newAction) {
        chatSwipeAction = newAction;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("ChatSwipeAction", chatSwipeAction).apply();
    }

    public static void updateMessageSeenHintCount(int count) {
        messageSeenHintCount = count;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("messageSeenCount", messageSeenHintCount).apply();
    }

    public static void updateEmojiInteractionsHintCount(int count) {
        emojiInteractionsHintCount = count;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("emojiInteractionsHintCount", emojiInteractionsHintCount).apply();
    }

    public static void updateDayNightThemeSwitchHintCount(int count) {
        dayNightThemeSwitchHintCount = count;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("dayNightThemeSwitchHintCount", dayNightThemeSwitchHintCount).apply();
    }

    public static int getDevicePerformanceClass() {
        if (devicePerformanceClass == -1) {
            int maxCpuFreq = -1;
            try {
                RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                String line = reader.readLine();
                if (line != null) {
                    maxCpuFreq = Utilities.parseInt(line).intValue() / 1000;
                }
                reader.close();
            } catch (Throwable th) {
            }
            int androidVersion = Build.VERSION.SDK_INT;
            int cpuCount = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || ((cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250) || ((cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21) || (cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24)))) {
                devicePerformanceClass = 0;
            } else if (cpuCount < 8 || memoryClass <= 160 || ((maxCpuFreq != -1 && maxCpuFreq <= 1650) || (maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23))) {
                devicePerformanceClass = 1;
            } else {
                devicePerformanceClass = 2;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("device performance info (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + ")");
            }
        }
        return devicePerformanceClass;
    }

    public static void setMediaColumnsCount(int count) {
        if (mediaColumnsCount != count) {
            mediaColumnsCount = count;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("mediaColumnsCount", mediaColumnsCount).apply();
        }
    }

    public static void setFastScrollHintCount(int count) {
        if (fastScrollHintCount != count) {
            fastScrollHintCount = count;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("fastScrollHintCount", fastScrollHintCount).apply();
        }
    }

    public static void setDontAskManageStorage(boolean b) {
        dontAskManageStorage = b;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("dontAskManageStorage", dontAskManageStorage).apply();
    }
}
