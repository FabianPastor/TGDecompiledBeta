package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.webkit.WebView;
import androidx.core.content.pm.ShortcutManagerCompat;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC$TL_help_appUpdate;

public class SharedConfig {
    public static final int PASSCODE_TYPE_PASSWORD = 1;
    public static final int PASSCODE_TYPE_PIN = 0;
    public static final int PERFORMANCE_CLASS_AVERAGE = 1;
    public static final int PERFORMANCE_CLASS_HIGH = 2;
    public static final int PERFORMANCE_CLASS_LOW = 0;
    public static final int SAVE_TO_GALLERY_FLAG_CHANNELS = 4;
    public static final int SAVE_TO_GALLERY_FLAG_GROUP = 2;
    public static final int SAVE_TO_GALLERY_FLAG_PEER = 1;
    public static boolean allowBigEmoji = false;
    public static boolean allowScreenCapture = false;
    private static Boolean animationsEnabled = null;
    public static boolean appLocked = false;
    public static boolean archiveHidden = false;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    public static boolean autoplayVideo = true;
    public static int badPasscodeTries = 0;
    public static int bubbleRadius = 17;
    public static boolean chatBlur = true;
    public static boolean chatBubbles = (Build.VERSION.SDK_INT >= 30);
    private static int chatSwipeAction = 0;
    private static boolean configLoaded = false;
    public static ProxyInfo currentProxy = null;
    public static boolean customTabs = true;
    public static int dayNightThemeSwitchHintCount = 0;
    public static boolean debugWebView = false;
    private static int devicePerformanceClass = 0;
    public static boolean directShare = true;
    public static String directShareHash = null;
    public static boolean disableVoiceAudioEffects = false;
    public static int distanceSystemType = 0;
    public static boolean dontAskManageStorage = false;
    public static boolean drawDialogIcons = false;
    public static int emojiInteractionsHintCount = 0;
    public static int fastScrollHintCount = 3;
    public static int fontSize = 16;
    public static boolean forceDisableTabletMode = false;
    public static boolean forceRtmpStream = false;
    public static boolean forwardingOptionsHintShown = false;
    public static boolean hasCameraCache = false;
    public static boolean hasEmailLogin = false;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter = false;
    public static int ivFontSize = 16;
    public static int keepMedia = 2;
    public static int lastKeepMediaCheckTime = 0;
    private static int lastLocalId = -210000;
    public static int lastLogsCheckTime = 0;
    public static int lastPauseTime = 0;
    public static long lastUpdateCheckTime = 0;
    public static String lastUpdateVersion = null;
    public static long lastUptimeMillis = 0;
    private static final Object localIdSync = new Object();
    public static int lockRecordAudioVideoHint = 0;
    public static boolean loopStickers = false;
    public static int mapPreviewType = 2;
    public static int mediaColumnsCount = 3;
    public static int messageSeenHintCount = 0;
    public static boolean noSoundHintShowed = false;
    public static boolean noStatusBar = true;
    public static boolean noiseSupression = false;
    public static String passcodeHash = "";
    public static long passcodeRetryInMs = 0;
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType = 0;
    public static int passportConfigHash = 0;
    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap = null;
    public static boolean pauseMusicOnRecord = true;
    public static TLRPC$TL_help_appUpdate pendingAppUpdate = null;
    public static int pendingAppUpdateBuildVersion = 0;
    public static boolean playOrderReversed = false;
    public static ArrayList<ProxyInfo> proxyList = new ArrayList<>();
    private static boolean proxyListLoaded = false;
    public static byte[] pushAuthKey = null;
    public static byte[] pushAuthKeyId = null;
    public static boolean pushStatSent = false;
    public static String pushString = "";
    public static long pushStringGetTimeEnd = 0;
    public static long pushStringGetTimeStart = 0;
    public static String pushStringStatus = "";
    public static int pushType = 2;
    public static boolean raiseToSpeak = false;
    public static boolean recordViaSco = true;
    public static int repeatMode = 0;
    public static boolean roundCamera16to9 = true;
    public static boolean saveIncomingPhotos = false;
    public static boolean saveStreamMedia = true;
    public static int saveToGalleryFlags = 0;
    public static int scheduledOrNoSoundHintShows = 0;
    public static int searchMessagesAsListHintShows = 0;
    public static boolean searchMessagesAsListUsed = false;
    public static boolean showNotificationsForAllAccounts = true;
    public static boolean shuffleMusic = false;
    public static boolean smoothKeyboard = true;
    public static boolean sortContactsByName = false;
    public static boolean sortFilesByName = false;
    public static boolean stickersReorderingHintUsed = false;
    public static String storageCacheDir = null;
    public static boolean streamAllVideo = false;
    public static boolean streamMedia = true;
    public static boolean streamMkv = false;
    public static boolean suggestAnimatedEmoji = false;
    public static int suggestStickers = 0;
    private static final Object sync = new Object();
    public static int textSelectionHintShows = 0;
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;
    public static boolean useThreeLinesLayout;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PasscodeType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PerformanceClass {
    }

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

        public ProxyInfo(String str, int i, String str2, String str3, String str4) {
            this.address = str;
            this.port = i;
            this.username = str2;
            this.password = str3;
            this.secret = str4;
            if (str == null) {
                this.address = "";
            }
            if (str3 == null) {
                this.password = "";
            }
            if (str2 == null) {
                this.username = "";
            }
            if (str4 == null) {
                this.secret = "";
            }
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                edit.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                edit.putString("passcodeHash1", passcodeHash);
                byte[] bArr = passcodeSalt;
                edit.putString("passcodeSalt", bArr.length > 0 ? Base64.encodeToString(bArr, 0) : "");
                edit.putBoolean("appLocked", appLocked);
                edit.putInt("passcodeType", passcodeType);
                edit.putLong("passcodeRetryInMs", passcodeRetryInMs);
                edit.putLong("lastUptimeMillis", lastUptimeMillis);
                edit.putInt("badPasscodeTries", badPasscodeTries);
                edit.putInt("autoLockIn", autoLockIn);
                edit.putInt("lastPauseTime", lastPauseTime);
                edit.putString("lastUpdateVersion2", lastUpdateVersion);
                edit.putBoolean("useFingerprint", useFingerprint);
                edit.putBoolean("allowScreenCapture", allowScreenCapture);
                edit.putString("pushString2", pushString);
                edit.putInt("pushType", pushType);
                edit.putBoolean("pushStatSent", pushStatSent);
                byte[] bArr2 = pushAuthKey;
                edit.putString("pushAuthKey", bArr2 != null ? Base64.encodeToString(bArr2, 0) : "");
                edit.putInt("lastLocalId", lastLocalId);
                edit.putString("passportConfigJson", passportConfigJson);
                edit.putInt("passportConfigHash", passportConfigHash);
                edit.putBoolean("sortContactsByName", sortContactsByName);
                edit.putBoolean("sortFilesByName", sortFilesByName);
                edit.putInt("textSelectionHintShows", textSelectionHintShows);
                edit.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                edit.putBoolean("forwardingOptionsHintShown", forwardingOptionsHintShown);
                edit.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                edit.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                edit.putBoolean("hasEmailLogin", hasEmailLogin);
                TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = pendingAppUpdate;
                if (tLRPC$TL_help_appUpdate != null) {
                    try {
                        SerializedData serializedData = new SerializedData(tLRPC$TL_help_appUpdate.getObjectSize());
                        pendingAppUpdate.serializeToStream(serializedData);
                        edit.putString("appUpdate", Base64.encodeToString(serializedData.toByteArray(), 0));
                        edit.putInt("appUpdateBuild", pendingAppUpdateBuildVersion);
                        serializedData.cleanup();
                    } catch (Exception unused) {
                    }
                } else {
                    edit.remove("appUpdate");
                }
                edit.putLong("appUpdateCheckTime", lastUpdateCheckTime);
                edit.apply();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static int getLastLocalId() {
        int i;
        synchronized (localIdSync) {
            i = lastLocalId;
            lastLocalId = i - 1;
        }
        return i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:82:0x03dc, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x016a A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x016e A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x01a0 A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x01b9 A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x020b A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x020d A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0232 A[Catch:{ Exception -> 0x0188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0235 A[Catch:{ Exception -> 0x0188 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void loadConfig() {
        /*
            java.lang.Object r0 = sync
            monitor-enter(r0)
            boolean r1 = configLoaded     // Catch:{ all -> 0x03dd }
            if (r1 != 0) goto L_0x03db
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03dd }
            if (r1 != 0) goto L_0x000d
            goto L_0x03db
        L_0x000d:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "background_activity"
            r3 = 0
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x03dd }
            android.content.SharedPreferences unused = org.telegram.messenger.SharedConfig.BackgroundActivityPrefs.prefs = r1     // Catch:{ all -> 0x03dd }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "userconfing"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "saveIncomingPhotos"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            saveIncomingPhotos = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "passcodeHash1"
            java.lang.String r4 = ""
            java.lang.String r2 = r1.getString(r2, r4)     // Catch:{ all -> 0x03dd }
            passcodeHash = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "appLocked"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            appLocked = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "passcodeType"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            passcodeType = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "passcodeRetryInMs"
            r4 = 0
            long r6 = r1.getLong(r2, r4)     // Catch:{ all -> 0x03dd }
            passcodeRetryInMs = r6     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lastUptimeMillis"
            long r4 = r1.getLong(r2, r4)     // Catch:{ all -> 0x03dd }
            lastUptimeMillis = r4     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "badPasscodeTries"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            badPasscodeTries = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "autoLockIn"
            r4 = 3600(0xe10, float:5.045E-42)
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x03dd }
            autoLockIn = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lastPauseTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            lastPauseTime = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "useFingerprint"
            r4 = 1
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            useFingerprint = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lastUpdateVersion2"
            java.lang.String r5 = "3.5"
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x03dd }
            lastUpdateVersion = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "allowScreenCapture"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            allowScreenCapture = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lastLocalId"
            r5 = -210000(0xfffffffffffccbb0, float:NaN)
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            lastLocalId = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "pushString2"
            java.lang.String r5 = ""
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x03dd }
            pushString = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "pushType"
            r5 = 2
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            pushType = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "pushStatSent"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            pushStatSent = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "passportConfigJson"
            java.lang.String r6 = ""
            java.lang.String r2 = r1.getString(r2, r6)     // Catch:{ all -> 0x03dd }
            passportConfigJson = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "passportConfigHash"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            passportConfigHash = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "storageCacheDir"
            r6 = 0
            java.lang.String r2 = r1.getString(r2, r6)     // Catch:{ all -> 0x03dd }
            storageCacheDir = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "pushAuthKey"
            java.lang.String r2 = r1.getString(r2, r6)     // Catch:{ all -> 0x03dd }
            boolean r7 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x03dd }
            if (r7 != 0) goto L_0x00dd
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x03dd }
            pushAuthKey = r2     // Catch:{ all -> 0x03dd }
        L_0x00dd:
            java.lang.String r2 = passcodeHash     // Catch:{ all -> 0x03dd }
            int r2 = r2.length()     // Catch:{ all -> 0x03dd }
            if (r2 <= 0) goto L_0x00f6
            int r2 = lastPauseTime     // Catch:{ all -> 0x03dd }
            if (r2 != 0) goto L_0x00f6
            long r7 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x03dd }
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            r9 = 600(0x258, double:2.964E-321)
            long r7 = r7 - r9
            int r2 = (int) r7     // Catch:{ all -> 0x03dd }
            lastPauseTime = r2     // Catch:{ all -> 0x03dd }
        L_0x00f6:
            java.lang.String r2 = "passcodeSalt"
            java.lang.String r7 = ""
            java.lang.String r2 = r1.getString(r2, r7)     // Catch:{ all -> 0x03dd }
            int r7 = r2.length()     // Catch:{ all -> 0x03dd }
            if (r7 <= 0) goto L_0x010b
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x03dd }
            passcodeSalt = r2     // Catch:{ all -> 0x03dd }
            goto L_0x010f
        L_0x010b:
            byte[] r2 = new byte[r3]     // Catch:{ all -> 0x03dd }
            passcodeSalt = r2     // Catch:{ all -> 0x03dd }
        L_0x010f:
            java.lang.String r2 = "appUpdateCheckTime"
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x03dd }
            long r7 = r1.getLong(r2, r7)     // Catch:{ all -> 0x03dd }
            lastUpdateCheckTime = r7     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "appUpdate"
            java.lang.String r2 = r1.getString(r2, r6)     // Catch:{ Exception -> 0x0188 }
            if (r2 == 0) goto L_0x0147
            java.lang.String r7 = "appUpdateBuild"
            int r8 = org.telegram.messenger.BuildVars.BUILD_VERSION     // Catch:{ Exception -> 0x0188 }
            int r1 = r1.getInt(r7, r8)     // Catch:{ Exception -> 0x0188 }
            pendingAppUpdateBuildVersion = r1     // Catch:{ Exception -> 0x0188 }
            byte[] r1 = android.util.Base64.decode(r2, r3)     // Catch:{ Exception -> 0x0188 }
            if (r1 == 0) goto L_0x0147
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0188 }
            r2.<init>((byte[]) r1)     // Catch:{ Exception -> 0x0188 }
            int r1 = r2.readInt32(r3)     // Catch:{ Exception -> 0x0188 }
            org.telegram.tgnet.TLRPC$help_AppUpdate r1 = org.telegram.tgnet.TLRPC$help_AppUpdate.TLdeserialize(r2, r1, r3)     // Catch:{ Exception -> 0x0188 }
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = (org.telegram.tgnet.TLRPC$TL_help_appUpdate) r1     // Catch:{ Exception -> 0x0188 }
            pendingAppUpdate = r1     // Catch:{ Exception -> 0x0188 }
            r2.cleanup()     // Catch:{ Exception -> 0x0188 }
        L_0x0147:
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r1 = pendingAppUpdate     // Catch:{ Exception -> 0x0188 }
            if (r1 == 0) goto L_0x018c
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0162 }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x0162 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0162 }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ Exception -> 0x0162 }
            android.content.pm.PackageInfo r1 = r1.getPackageInfo(r2, r3)     // Catch:{ Exception -> 0x0162 }
            int r2 = r1.versionCode     // Catch:{ Exception -> 0x0162 }
            java.lang.String r1 = r1.versionName     // Catch:{ Exception -> 0x0160 }
            goto L_0x0168
        L_0x0160:
            r1 = move-exception
            goto L_0x0164
        L_0x0162:
            r1 = move-exception
            r2 = 0
        L_0x0164:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0188 }
            r1 = r6
        L_0x0168:
            if (r2 != 0) goto L_0x016c
            int r2 = org.telegram.messenger.BuildVars.BUILD_VERSION     // Catch:{ Exception -> 0x0188 }
        L_0x016c:
            if (r1 != 0) goto L_0x0170
            java.lang.String r1 = org.telegram.messenger.BuildVars.BUILD_VERSION_STRING     // Catch:{ Exception -> 0x0188 }
        L_0x0170:
            int r7 = pendingAppUpdateBuildVersion     // Catch:{ Exception -> 0x0188 }
            if (r7 != r2) goto L_0x0180
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r2 = pendingAppUpdate     // Catch:{ Exception -> 0x0188 }
            java.lang.String r2 = r2.version     // Catch:{ Exception -> 0x0188 }
            if (r2 == 0) goto L_0x0180
            int r1 = r1.compareTo(r2)     // Catch:{ Exception -> 0x0188 }
            if (r1 < 0) goto L_0x018c
        L_0x0180:
            pendingAppUpdate = r6     // Catch:{ Exception -> 0x0188 }
            org.telegram.messenger.SharedConfig$$ExternalSyntheticLambda4 r1 = org.telegram.messenger.SharedConfig$$ExternalSyntheticLambda4.INSTANCE     // Catch:{ Exception -> 0x0188 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x0188 }
            goto L_0x018c
        L_0x0188:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x03dd }
        L_0x018c:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "mainconfig"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "save_gallery"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            if (r2 == 0) goto L_0x01b9
            boolean r2 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE     // Catch:{ all -> 0x03dd }
            if (r2 == 0) goto L_0x01b9
            r2 = 7
            saveToGalleryFlags = r2     // Catch:{ all -> 0x03dd }
            android.content.SharedPreferences$Editor r2 = r1.edit()     // Catch:{ all -> 0x03dd }
            java.lang.String r7 = "save_gallery"
            android.content.SharedPreferences$Editor r2 = r2.remove(r7)     // Catch:{ all -> 0x03dd }
            java.lang.String r7 = "save_gallery_flags"
            int r8 = saveToGalleryFlags     // Catch:{ all -> 0x03dd }
            android.content.SharedPreferences$Editor r2 = r2.putInt(r7, r8)     // Catch:{ all -> 0x03dd }
            r2.apply()     // Catch:{ all -> 0x03dd }
            goto L_0x01c1
        L_0x01b9:
            java.lang.String r2 = "save_gallery_flags"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            saveToGalleryFlags = r2     // Catch:{ all -> 0x03dd }
        L_0x01c1:
            java.lang.String r2 = "autoplay_gif"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            autoplayGifs = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "autoplay_video"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            autoplayVideo = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "mapPreviewType"
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            mapPreviewType = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "raise_to_speak"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            raiseToSpeak = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "record_via_sco"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            recordViaSco = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "custom_tabs"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            customTabs = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "direct_share"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            directShare = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "shuffleMusic"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            shuffleMusic = r2     // Catch:{ all -> 0x03dd }
            if (r2 != 0) goto L_0x020d
            java.lang.String r2 = "playOrderReversed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            if (r2 == 0) goto L_0x020d
            r2 = 1
            goto L_0x020e
        L_0x020d:
            r2 = 0
        L_0x020e:
            playOrderReversed = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "inappCamera"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            inappCamera = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "cameraCache"
            boolean r2 = r1.contains(r2)     // Catch:{ all -> 0x03dd }
            hasCameraCache = r2     // Catch:{ all -> 0x03dd }
            roundCamera16to9 = r4     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "repeatMode"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            repeatMode = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "fons_size"
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()     // Catch:{ all -> 0x03dd }
            if (r7 == 0) goto L_0x0235
            r7 = 18
            goto L_0x0237
        L_0x0235:
            r7 = 16
        L_0x0237:
            int r2 = r1.getInt(r2, r7)     // Catch:{ all -> 0x03dd }
            fontSize = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "bubbleRadius"
            r7 = 17
            int r2 = r1.getInt(r2, r7)     // Catch:{ all -> 0x03dd }
            bubbleRadius = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "iv_font_size"
            int r7 = fontSize     // Catch:{ all -> 0x03dd }
            int r2 = r1.getInt(r2, r7)     // Catch:{ all -> 0x03dd }
            ivFontSize = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "allowBigEmoji"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            allowBigEmoji = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "useSystemEmoji"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            useSystemEmoji = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "streamMedia"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            streamMedia = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "saveStreamMedia"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            saveStreamMedia = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "smoothKeyboard2"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            smoothKeyboard = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "pauseMusicOnRecord"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            pauseMusicOnRecord = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "chatBlur"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            chatBlur = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "forceDisableTabletMode"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            forceDisableTabletMode = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "streamAllVideo"
            boolean r7 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ all -> 0x03dd }
            boolean r2 = r1.getBoolean(r2, r7)     // Catch:{ all -> 0x03dd }
            streamAllVideo = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "streamMkv"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            streamMkv = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "suggestStickers"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            suggestStickers = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "suggestAnimatedEmoji"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            suggestAnimatedEmoji = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "sortContactsByName"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            sortContactsByName = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "sortFilesByName"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            sortFilesByName = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "noSoundHintShowed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            noSoundHintShowed = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "directShareHash2"
            java.lang.String r2 = r1.getString(r2, r6)     // Catch:{ all -> 0x03dd }
            directShareHash = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "useThreeLinesLayout"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            useThreeLinesLayout = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "archiveHidden"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            archiveHidden = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "distanceSystemType"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            distanceSystemType = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "devicePerformanceClass"
            r6 = -1
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x03dd }
            devicePerformanceClass = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "loopStickers"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            loopStickers = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "keep_media"
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            keepMedia = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "noStatusBar"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            noStatusBar = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "forceRtmpStream"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            forceRtmpStream = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "debugWebView"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            debugWebView = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lastKeepMediaCheckTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            lastKeepMediaCheckTime = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lastLogsCheckTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            lastLogsCheckTime = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "searchMessagesAsListHintShows"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            searchMessagesAsListHintShows = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "searchMessagesAsListUsed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            searchMessagesAsListUsed = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "stickersReorderingHintUsed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            stickersReorderingHintUsed = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "textSelectionHintShows"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            textSelectionHintShows = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "scheduledOrNoSoundHintShows"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            scheduledOrNoSoundHintShows = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "forwardingOptionsHintShown"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            forwardingOptionsHintShown = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "lockRecordAudioVideoHint"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x03dd }
            lockRecordAudioVideoHint = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "disableVoiceAudioEffects"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            disableVoiceAudioEffects = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "noiseSupression"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            noiseSupression = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "ChatSwipeAction"
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x03dd }
            chatSwipeAction = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "messageSeenCount"
            r5 = 3
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            messageSeenHintCount = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "emojiInteractionsHintCount"
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            emojiInteractionsHintCount = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "dayNightThemeSwitchHintCount"
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            dayNightThemeSwitchHintCount = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "mediaColumnsCount"
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            mediaColumnsCount = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "fastScrollHintCount"
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x03dd }
            fastScrollHintCount = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "dontAskManageStorage"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            dontAskManageStorage = r2     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "hasEmailLogin"
            boolean r1 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x03dd }
            hasEmailLogin = r1     // Catch:{ all -> 0x03dd }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "Notifications"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x03dd }
            java.lang.String r2 = "AllAccounts"
            boolean r1 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x03dd }
            showNotificationsForAllAccounts = r1     // Catch:{ all -> 0x03dd }
            configLoaded = r4     // Catch:{ all -> 0x03dd }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x03d5 }
            r2 = 19
            if (r1 < r2) goto L_0x03d9
            boolean r1 = debugWebView     // Catch:{ Exception -> 0x03d5 }
            if (r1 == 0) goto L_0x03d9
            android.webkit.WebView.setWebContentsDebuggingEnabled(r4)     // Catch:{ Exception -> 0x03d5 }
            goto L_0x03d9
        L_0x03d5:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x03dd }
        L_0x03d9:
            monitor-exit(r0)     // Catch:{ all -> 0x03dd }
            return
        L_0x03db:
            monitor-exit(r0)     // Catch:{ all -> 0x03dd }
            return
        L_0x03dd:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x03dd }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SharedConfig.loadConfig():void");
    }

    public static void increaseBadPasscodeTries() {
        int i = badPasscodeTries + 1;
        badPasscodeTries = i;
        if (i >= 3) {
            if (i == 3) {
                passcodeRetryInMs = 5000;
            } else if (i == 4) {
                passcodeRetryInMs = 10000;
            } else if (i == 5) {
                passcodeRetryInMs = 15000;
            } else if (i == 6) {
                passcodeRetryInMs = 20000;
            } else if (i != 7) {
                passcodeRetryInMs = 30000;
            } else {
                passcodeRetryInMs = 25000;
            }
            lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static boolean isPassportConfigLoaded() {
        return passportConfigMap != null;
    }

    public static void setPassportConfig(String str, int i) {
        passportConfigMap = null;
        passportConfigJson = str;
        passportConfigHash = i;
        saveConfig();
        getCountryLangs();
    }

    public static HashMap<String, String> getCountryLangs() {
        if (passportConfigMap == null) {
            passportConfigMap = new HashMap<>();
            try {
                JSONObject jSONObject = new JSONObject(passportConfigJson);
                Iterator<String> keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String next = keys.next();
                    passportConfigMap.put(next.toUpperCase(), jSONObject.getString(next).toUpperCase());
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return passportConfigMap;
    }

    public static boolean isAppUpdateAvailable() {
        int i;
        TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = pendingAppUpdate;
        if (tLRPC$TL_help_appUpdate == null || tLRPC$TL_help_appUpdate.document == null || !BuildVars.isStandaloneApp()) {
            return false;
        }
        try {
            i = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            i = BuildVars.BUILD_VERSION;
        }
        if (pendingAppUpdateBuildVersion == i) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0020  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0031  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003a A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean setNewAppVersionAvailable(org.telegram.tgnet.TLRPC$TL_help_appUpdate r4) {
        /*
            r0 = 0
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0018 }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x0018 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0018 }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ Exception -> 0x0018 }
            android.content.pm.PackageInfo r1 = r1.getPackageInfo(r2, r0)     // Catch:{ Exception -> 0x0018 }
            int r2 = r1.versionCode     // Catch:{ Exception -> 0x0018 }
            java.lang.String r1 = r1.versionName     // Catch:{ Exception -> 0x0016 }
            goto L_0x001e
        L_0x0016:
            r1 = move-exception
            goto L_0x001a
        L_0x0018:
            r1 = move-exception
            r2 = 0
        L_0x001a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            r1 = 0
        L_0x001e:
            if (r2 != 0) goto L_0x0022
            int r2 = org.telegram.messenger.BuildVars.BUILD_VERSION
        L_0x0022:
            if (r1 != 0) goto L_0x0026
            java.lang.String r1 = org.telegram.messenger.BuildVars.BUILD_VERSION_STRING
        L_0x0026:
            java.lang.String r3 = r4.version
            if (r3 == 0) goto L_0x003a
            int r1 = r1.compareTo(r3)
            if (r1 < 0) goto L_0x0031
            goto L_0x003a
        L_0x0031:
            pendingAppUpdate = r4
            pendingAppUpdateBuildVersion = r2
            saveConfig()
            r4 = 1
            return r4
        L_0x003a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SharedConfig.setNewAppVersionAvailable(org.telegram.tgnet.TLRPC$TL_help_appUpdate):boolean");
    }

    public static boolean checkPasscode(String str) {
        if (passcodeSalt.length == 0) {
            boolean equals = Utilities.MD5(str).equals(passcodeHash);
            if (equals) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    byte[] bytes = str.getBytes("UTF-8");
                    int length = bytes.length + 32;
                    byte[] bArr = new byte[length];
                    System.arraycopy(passcodeSalt, 0, bArr, 0, 16);
                    System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                    System.arraycopy(passcodeSalt, 0, bArr, bytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, (long) length));
                    saveConfig();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            return equals;
        }
        try {
            byte[] bytes2 = str.getBytes("UTF-8");
            int length2 = bytes2.length + 32;
            byte[] bArr2 = new byte[length2];
            System.arraycopy(passcodeSalt, 0, bArr2, 0, 16);
            System.arraycopy(bytes2, 0, bArr2, 16, bytes2.length);
            System.arraycopy(passcodeSalt, 0, bArr2, bytes2.length + 16, 16);
            return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bArr2, 0, (long) length2)));
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

    public static void setSuggestStickers(int i) {
        suggestStickers = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("suggestStickers", suggestStickers);
        edit.commit();
    }

    public static void setSearchMessagesAsListUsed(boolean z) {
        searchMessagesAsListUsed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        edit.commit();
    }

    public static void setStickersReorderingHintUsed(boolean z) {
        stickersReorderingHintUsed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("stickersReorderingHintUsed", stickersReorderingHintUsed);
        edit.commit();
    }

    public static void increaseTextSelectionHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = textSelectionHintShows + 1;
        textSelectionHintShows = i;
        edit.putInt("textSelectionHintShows", i);
        edit.commit();
    }

    public static void removeTextSelectionHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("textSelectionHintShows", 3);
        edit.commit();
    }

    public static void increaseScheduledOrNoSuoundHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = scheduledOrNoSoundHintShows + 1;
        scheduledOrNoSoundHintShows = i;
        edit.putInt("scheduledOrNoSoundHintShows", i);
        edit.commit();
    }

    public static void forwardingOptionsHintHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        forwardingOptionsHintShown = true;
        edit.putBoolean("forwardingOptionsHintShown", true);
        edit.commit();
    }

    public static void removeScheduledOrNoSoundHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("scheduledOrNoSoundHintShows", 3);
        edit.commit();
    }

    public static void increaseLockRecordAudioVideoHintShowed() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = lockRecordAudioVideoHint + 1;
        lockRecordAudioVideoHint = i;
        edit.putInt("lockRecordAudioVideoHint", i);
        edit.commit();
    }

    public static void removeLockRecordAudioVideoHint() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("lockRecordAudioVideoHint", 3);
        edit.commit();
    }

    public static void increaseSearchAsListHintShows() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = searchMessagesAsListHintShows + 1;
        searchMessagesAsListHintShows = i;
        edit.putInt("searchMessagesAsListHintShows", i);
        edit.commit();
    }

    public static void setKeepMedia(int i) {
        keepMedia = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("keep_media", keepMedia);
        edit.commit();
    }

    public static void checkLogsToDelete() {
        if (BuildVars.LOGS_ENABLED) {
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            if (Math.abs(currentTimeMillis - lastLogsCheckTime) >= 3600) {
                lastLogsCheckTime = currentTimeMillis;
                Utilities.cacheClearQueue.postRunnable(new SharedConfig$$ExternalSyntheticLambda0(currentTimeMillis));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkLogsToDelete$0(int i) {
        long j = (long) (i - 864000);
        try {
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir((String) null);
            Utilities.clearDir(new File(externalFilesDir.getAbsolutePath() + "/logs").getAbsolutePath(), 0, j, false);
        } catch (Throwable th) {
            FileLog.e(th);
        }
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("lastLogsCheckTime", lastLogsCheckTime);
        edit.commit();
    }

    public static void checkKeepMedia() {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(currentTimeMillis - lastKeepMediaCheckTime) >= 3600) {
            lastKeepMediaCheckTime = currentTimeMillis;
            Utilities.cacheClearQueue.postRunnable(new SharedConfig$$ExternalSyntheticLambda1(currentTimeMillis, FileLoader.checkDirectory(4)));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkKeepMedia$1(int i, File file) {
        int i2 = keepMedia;
        if (i2 != 2) {
            long j = (long) (i - ((i2 == 0 ? 7 : i2 == 1 ? 30 : 3) * 86400));
            SparseArray<File> createMediaPaths = ImageLoader.getInstance().createMediaPaths();
            for (int i3 = 0; i3 < createMediaPaths.size(); i3++) {
                if (createMediaPaths.keyAt(i3) != 4) {
                    try {
                        Utilities.clearDir(createMediaPaths.valueAt(i3).getAbsolutePath(), 0, j, false);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
            }
        }
        File file2 = new File(file, "acache");
        if (file2.exists()) {
            try {
                Utilities.clearDir(file2.getAbsolutePath(), 0, (long) (i - 86400), false);
            } catch (Throwable th2) {
                FileLog.e(th2);
            }
        }
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("lastKeepMediaCheckTime", lastKeepMediaCheckTime);
        edit.commit();
    }

    public static void toggleDisableVoiceAudioEffects() {
        disableVoiceAudioEffects = !disableVoiceAudioEffects;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
        edit.commit();
    }

    public static void toggleNoiseSupression() {
        noiseSupression = !noiseSupression;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("noiseSupression", noiseSupression);
        edit.commit();
    }

    public static void toggleForceRTMPStream() {
        forceRtmpStream = !forceRtmpStream;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("forceRtmpStream", forceRtmpStream);
        edit.apply();
    }

    public static void toggleDebugWebView() {
        boolean z = !debugWebView;
        debugWebView = z;
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(z);
        }
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("debugWebView", debugWebView);
        edit.apply();
    }

    public static void toggleNoStatusBar() {
        noStatusBar = !noStatusBar;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("noStatusBar", noStatusBar);
        edit.commit();
    }

    public static void toggleLoopStickers() {
        loopStickers = !loopStickers;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("loopStickers", loopStickers);
        edit.commit();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("allowBigEmoji", allowBigEmoji);
        edit.commit();
    }

    public static void toggleSuggestAnimatedEmoji() {
        suggestAnimatedEmoji = !suggestAnimatedEmoji;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("suggestAnimatedEmoji", suggestAnimatedEmoji);
        edit.commit();
    }

    public static void setPlaybackOrderType(int i) {
        if (i == 2) {
            shuffleMusic = true;
            playOrderReversed = false;
        } else if (i == 1) {
            playOrderReversed = true;
            shuffleMusic = false;
        } else {
            playOrderReversed = false;
            shuffleMusic = false;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("shuffleMusic", shuffleMusic);
        edit.putBoolean("playOrderReversed", playOrderReversed);
        edit.commit();
    }

    public static void setRepeatMode(int i) {
        repeatMode = i;
        if (i < 0 || i > 2) {
            repeatMode = 0;
        }
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("repeatMode", repeatMode);
        edit.commit();
    }

    public static void toggleSaveToGalleryFlag(int i) {
        int i2 = saveToGalleryFlags;
        if ((i2 & i) != 0) {
            saveToGalleryFlags = (i ^ -1) & i2;
        } else {
            saveToGalleryFlags = i | i2;
        }
        MessagesController.getGlobalMainSettings().edit().putInt("save_gallery_flags", saveToGalleryFlags).apply();
        ImageLoader.getInstance().checkMediaPaths();
        ImageLoader.getInstance().getCacheOutQueue().postRunnable(SharedConfig$$ExternalSyntheticLambda3.INSTANCE);
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("autoplay_gif", autoplayGifs);
        edit.commit();
    }

    public static void setUseThreeLinesLayout(boolean z) {
        useThreeLinesLayout = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        edit.commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("archiveHidden", archiveHidden);
        edit.commit();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo = !autoplayVideo;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("autoplay_video", autoplayVideo);
        edit.commit();
    }

    public static boolean isSecretMapPreviewSet() {
        return MessagesController.getGlobalMainSettings().contains("mapPreviewType");
    }

    public static void setSecretMapPreviewType(int i) {
        mapPreviewType = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("mapPreviewType", mapPreviewType);
        edit.commit();
    }

    public static void setNoSoundHintShowed(boolean z) {
        if (noSoundHintShowed != z) {
            noSoundHintShowed = z;
            SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
            edit.putBoolean("noSoundHintShowed", noSoundHintShowed);
            edit.commit();
        }
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("raise_to_speak", raiseToSpeak);
        edit.commit();
    }

    public static void toggleRecordViaSco() {
        recordViaSco = !recordViaSco;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("record_via_sco", recordViaSco);
        edit.commit();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("custom_tabs", customTabs);
        edit.commit();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("direct_share", directShare);
        edit.commit();
        ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        MediaDataController.getInstance(UserConfig.selectedAccount).buildShortcuts();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamMedia", streamMedia);
        edit.commit();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("sortContactsByName", sortContactsByName);
        edit.commit();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName = !sortFilesByName;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("sortFilesByName", sortFilesByName);
        edit.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamAllVideo", streamAllVideo);
        edit.commit();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamMkv", streamMkv);
        edit.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("saveStreamMedia", saveStreamMedia);
        edit.commit();
    }

    public static void toggleSmoothKeyboard() {
        smoothKeyboard = !smoothKeyboard;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("smoothKeyboard2", smoothKeyboard);
        edit.commit();
    }

    public static void togglePauseMusicOnRecord() {
        pauseMusicOnRecord = !pauseMusicOnRecord;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("pauseMusicOnRecord", pauseMusicOnRecord);
        edit.commit();
    }

    public static void toggleChatBlur() {
        chatBlur = !chatBlur;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("chatBlur", chatBlur);
        edit.commit();
    }

    public static void toggleForceDisableTabletMode() {
        forceDisableTabletMode = !forceDisableTabletMode;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("forceDisableTabletMode", forceDisableTabletMode);
        edit.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("inappCamera", inappCamera);
        edit.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("roundCamera16to9", roundCamera16to9);
        edit.commit();
    }

    public static void setDistanceSystemType(int i) {
        distanceSystemType = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("distanceSystemType", distanceSystemType);
        edit.commit();
        LocaleController.resetImperialSystemType();
    }

    public static void loadProxyList() {
        if (!proxyListLoaded) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String string = sharedPreferences.getString("proxy_ip", "");
            String string2 = sharedPreferences.getString("proxy_user", "");
            String string3 = sharedPreferences.getString("proxy_pass", "");
            String string4 = sharedPreferences.getString("proxy_secret", "");
            int i = sharedPreferences.getInt("proxy_port", 1080);
            proxyListLoaded = true;
            proxyList.clear();
            currentProxy = null;
            String string5 = sharedPreferences.getString("proxy_list", (String) null);
            if (!TextUtils.isEmpty(string5)) {
                SerializedData serializedData = new SerializedData(Base64.decode(string5, 0));
                int readInt32 = serializedData.readInt32(false);
                for (int i2 = 0; i2 < readInt32; i2++) {
                    ProxyInfo proxyInfo = new ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                    proxyList.add(proxyInfo);
                    if (currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo.address) && i == proxyInfo.port && string2.equals(proxyInfo.username) && string3.equals(proxyInfo.password)) {
                        currentProxy = proxyInfo;
                    }
                }
                serializedData.cleanup();
            }
            if (currentProxy == null && !TextUtils.isEmpty(string)) {
                ProxyInfo proxyInfo2 = new ProxyInfo(string, i, string2, string3, string4);
                currentProxy = proxyInfo2;
                proxyList.add(0, proxyInfo2);
            }
        }
    }

    public static void saveProxyList() {
        SerializedData serializedData = new SerializedData();
        int size = proxyList.size();
        serializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            ProxyInfo proxyInfo = proxyList.get(i);
            String str = proxyInfo.address;
            String str2 = "";
            if (str == null) {
                str = str2;
            }
            serializedData.writeString(str);
            serializedData.writeInt32(proxyInfo.port);
            String str3 = proxyInfo.username;
            if (str3 == null) {
                str3 = str2;
            }
            serializedData.writeString(str3);
            String str4 = proxyInfo.password;
            if (str4 == null) {
                str4 = str2;
            }
            serializedData.writeString(str4);
            String str5 = proxyInfo.secret;
            if (str5 != null) {
                str2 = str5;
            }
            serializedData.writeString(str2);
        }
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).commit();
        serializedData.cleanup();
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int size = proxyList.size();
        for (int i = 0; i < size; i++) {
            ProxyInfo proxyInfo2 = proxyList.get(i);
            if (proxyInfo.address.equals(proxyInfo2.address) && proxyInfo.port == proxyInfo2.port && proxyInfo.username.equals(proxyInfo2.username) && proxyInfo.password.equals(proxyInfo2.password) && proxyInfo.secret.equals(proxyInfo2.secret)) {
                return proxyInfo2;
            }
        }
        proxyList.add(proxyInfo);
        saveProxyList();
        return proxyInfo;
    }

    public static void deleteProxy(ProxyInfo proxyInfo) {
        if (currentProxy == proxyInfo) {
            currentProxy = null;
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            boolean z = globalMainSettings.getBoolean("proxy_enabled", false);
            SharedPreferences.Editor edit = globalMainSettings.edit();
            edit.putString("proxy_ip", "");
            edit.putString("proxy_pass", "");
            edit.putString("proxy_user", "");
            edit.putString("proxy_secret", "");
            edit.putInt("proxy_port", 1080);
            edit.putBoolean("proxy_enabled", false);
            edit.putBoolean("proxy_enabled_calls", false);
            edit.commit();
            if (z) {
                ConnectionsManager.setProxySettings(false, "", 0, "", "", "");
            }
        }
        proxyList.remove(proxyInfo);
        saveProxyList();
    }

    public static void checkSaveToGalleryFiles() {
        Utilities.globalQueue.postRunnable(SharedConfig$$ExternalSyntheticLambda2.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkSaveToGalleryFiles$3() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "Telegram");
            File file2 = new File(file, "Telegram Images");
            file2.mkdir();
            File file3 = new File(file, "Telegram Video");
            file3.mkdir();
            if (saveToGalleryFlags == 0) {
                if (BuildVars.NO_SCOPED_STORAGE) {
                    if (file2.isDirectory()) {
                        AndroidUtilities.createEmptyFile(new File(file2, ".nomedia"));
                    }
                    if (file3.isDirectory()) {
                        AndroidUtilities.createEmptyFile(new File(file3, ".nomedia"));
                        return;
                    }
                    return;
                }
            }
            if (file2.isDirectory()) {
                new File(file2, ".nomedia").delete();
            }
            if (file3.isDirectory()) {
                new File(file3, ".nomedia").delete();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static int getChatSwipeAction(int i) {
        int i2 = chatSwipeAction;
        if (i2 < 0) {
            return !MessagesController.getInstance(i).dialogFilters.isEmpty() ? 5 : 2;
        }
        if (i2 != 5 || !MessagesController.getInstance(i).dialogFilters.isEmpty()) {
            return chatSwipeAction;
        }
        return 2;
    }

    public static void updateChatListSwipeSetting(int i) {
        chatSwipeAction = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("ChatSwipeAction", chatSwipeAction).apply();
    }

    public static void updateMessageSeenHintCount(int i) {
        messageSeenHintCount = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("messageSeenCount", messageSeenHintCount).apply();
    }

    public static void updateEmojiInteractionsHintCount(int i) {
        emojiInteractionsHintCount = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("emojiInteractionsHintCount", emojiInteractionsHintCount).apply();
    }

    public static void updateDayNightThemeSwitchHintCount(int i) {
        dayNightThemeSwitchHintCount = i;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("dayNightThemeSwitchHintCount", dayNightThemeSwitchHintCount).apply();
    }

    public static int getDevicePerformanceClass() {
        int i;
        if (devicePerformanceClass == -1) {
            int i2 = Build.VERSION.SDK_INT;
            int i3 = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            int i4 = 0;
            int i5 = 0;
            for (int i6 = 0; i6 < i3; i6++) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(String.format(Locale.ENGLISH, "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", new Object[]{Integer.valueOf(i6)}), "r");
                    String readLine = randomAccessFile.readLine();
                    if (readLine != null) {
                        i5 += Utilities.parseInt((CharSequence) readLine).intValue() / 1000;
                        i4++;
                    }
                    randomAccessFile.close();
                } catch (Throwable unused) {
                }
            }
            if (i4 == 0) {
                i = -1;
            } else {
                i = (int) Math.ceil((double) (((float) i5) / ((float) i4)));
            }
            if (i2 < 21 || i3 <= 2 || memoryClass <= 100 || ((i3 <= 4 && i != -1 && i <= 1250) || ((i3 <= 4 && i <= 1600 && memoryClass <= 128 && i2 <= 21) || (i3 <= 4 && i <= 1300 && memoryClass <= 128 && i2 <= 24)))) {
                devicePerformanceClass = 0;
            } else if (i3 < 8 || memoryClass <= 160 || ((i != -1 && i <= 2050) || (i == -1 && i3 == 8 && i2 <= 23))) {
                devicePerformanceClass = 1;
            } else {
                devicePerformanceClass = 2;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("device performance info selected_class = " + devicePerformanceClass + " (cpu_count = " + i3 + ", freq = " + i + ", memoryClass = " + memoryClass + ", android version " + i2 + ")");
            }
        }
        return devicePerformanceClass;
    }

    public static void setMediaColumnsCount(int i) {
        if (mediaColumnsCount != i) {
            mediaColumnsCount = i;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("mediaColumnsCount", mediaColumnsCount).apply();
        }
    }

    public static void setFastScrollHintCount(int i) {
        if (fastScrollHintCount != i) {
            fastScrollHintCount = i;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("fastScrollHintCount", fastScrollHintCount).apply();
        }
    }

    public static void setDontAskManageStorage(boolean z) {
        dontAskManageStorage = z;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("dontAskManageStorage", dontAskManageStorage).apply();
    }

    public static boolean canBlurChat() {
        return getDevicePerformanceClass() == 2;
    }

    public static boolean chatBlurEnabled() {
        return canBlurChat() && chatBlur;
    }

    public static class BackgroundActivityPrefs {
        /* access modifiers changed from: private */
        public static SharedPreferences prefs;

        public static long getLastCheckedBackgroundActivity() {
            return prefs.getLong("last_checked", 0);
        }

        public static void setLastCheckedBackgroundActivity(long j) {
            prefs.edit().putLong("last_checked", j).apply();
        }
    }

    public static void setAnimationsEnabled(boolean z) {
        animationsEnabled = Boolean.valueOf(z);
    }

    public static boolean animationsEnabled() {
        if (animationsEnabled == null) {
            animationsEnabled = Boolean.valueOf(MessagesController.getGlobalMainSettings().getBoolean("view_animations", true));
        }
        return animationsEnabled.booleanValue();
    }
}
