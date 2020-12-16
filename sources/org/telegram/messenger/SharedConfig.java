package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
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

public class SharedConfig {
    public static final int PERFORMANCE_CLASS_AVERAGE = 1;
    public static final int PERFORMANCE_CLASS_HIGH = 2;
    public static final int PERFORMANCE_CLASS_LOW = 0;
    public static boolean allowBigEmoji = false;
    public static boolean allowScreenCapture = false;
    public static boolean appLocked = false;
    public static boolean archiveHidden = false;
    public static boolean assistantSupport = false;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    public static boolean autoplayVideo = true;
    public static int badPasscodeTries = 0;
    public static int bubbleRadius = 10;
    public static boolean chatBubbles = (Build.VERSION.SDK_INT >= 30);
    private static boolean configLoaded = false;
    public static ProxyInfo currentProxy = null;
    public static boolean customTabs = true;
    private static int devicePerformanceClass = 0;
    public static boolean directShare = true;
    public static String directShareHash = null;
    public static boolean disableVoiceAudioEffects = false;
    public static int distanceSystemType = 0;
    public static boolean drawDialogIcons = false;
    public static int fontSize = 16;
    public static boolean hasCameraCache = false;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter = false;
    public static int ivFontSize = 16;
    public static int keepMedia = 2;
    public static int lastKeepMediaCheckTime = 0;
    private static int lastLocalId = -210000;
    public static int lastPauseTime = 0;
    public static String lastUpdateVersion = null;
    public static long lastUptimeMillis = 0;
    private static final Object localIdSync = new Object();
    public static int lockRecordAudioVideoHint = 0;
    public static boolean loopStickers = false;
    public static int mapPreviewType = 2;
    public static boolean noSoundHintShowed = false;
    public static String passcodeHash = "";
    public static long passcodeRetryInMs = 0;
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType = 0;
    public static int passportConfigHash = 0;
    private static String passportConfigJson = "";
    private static HashMap<String, String> passportConfigMap = null;
    public static boolean pauseMusicOnRecord = true;
    public static boolean playOrderReversed = false;
    public static ArrayList<ProxyInfo> proxyList = new ArrayList<>();
    private static boolean proxyListLoaded = false;
    public static byte[] pushAuthKey = null;
    public static byte[] pushAuthKeyId = null;
    public static String pushString = "";
    public static String pushStringStatus = "";
    public static boolean raiseToSpeak = true;
    public static int repeatMode = 0;
    public static boolean roundCamera16to9 = true;
    public static boolean saveIncomingPhotos = false;
    public static boolean saveStreamMedia = true;
    public static boolean saveToGallery = false;
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
    public static int suggestStickers = 0;
    private static final Object sync = new Object();
    public static int textSelectionHintShows = 0;
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
                byte[] bArr2 = pushAuthKey;
                edit.putString("pushAuthKey", bArr2 != null ? Base64.encodeToString(bArr2, 0) : "");
                edit.putInt("lastLocalId", lastLocalId);
                edit.putString("passportConfigJson", passportConfigJson);
                edit.putInt("passportConfigHash", passportConfigHash);
                edit.putBoolean("sortContactsByName", sortContactsByName);
                edit.putBoolean("sortFilesByName", sortFilesByName);
                edit.putInt("textSelectionHintShows", textSelectionHintShows);
                edit.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                edit.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                edit.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
                edit.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                edit.commit();
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

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0288, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void loadConfig() {
        /*
            java.lang.Object r0 = sync
            monitor-enter(r0)
            boolean r1 = configLoaded     // Catch:{ all -> 0x0289 }
            if (r1 != 0) goto L_0x0287
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0289 }
            if (r1 != 0) goto L_0x000d
            goto L_0x0287
        L_0x000d:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "userconfing"
            r3 = 0
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "saveIncomingPhotos"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            saveIncomingPhotos = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "passcodeHash1"
            java.lang.String r4 = ""
            java.lang.String r2 = r1.getString(r2, r4)     // Catch:{ all -> 0x0289 }
            passcodeHash = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "appLocked"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            appLocked = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "passcodeType"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            passcodeType = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "passcodeRetryInMs"
            r4 = 0
            long r6 = r1.getLong(r2, r4)     // Catch:{ all -> 0x0289 }
            passcodeRetryInMs = r6     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "lastUptimeMillis"
            long r4 = r1.getLong(r2, r4)     // Catch:{ all -> 0x0289 }
            lastUptimeMillis = r4     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "badPasscodeTries"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            badPasscodeTries = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "autoLockIn"
            r4 = 3600(0xe10, float:5.045E-42)
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x0289 }
            autoLockIn = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "lastPauseTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            lastPauseTime = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "useFingerprint"
            r4 = 1
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            useFingerprint = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "lastUpdateVersion2"
            java.lang.String r5 = "3.5"
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0289 }
            lastUpdateVersion = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "allowScreenCapture"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            allowScreenCapture = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "lastLocalId"
            r5 = -210000(0xfffffffffffccbb0, float:NaN)
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x0289 }
            lastLocalId = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "pushString2"
            java.lang.String r5 = ""
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0289 }
            pushString = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "passportConfigJson"
            java.lang.String r5 = ""
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0289 }
            passportConfigJson = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "passportConfigHash"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            passportConfigHash = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "storageCacheDir"
            r5 = 0
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0289 }
            storageCacheDir = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "pushAuthKey"
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0289 }
            boolean r6 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x0289 }
            if (r6 != 0) goto L_0x00c3
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x0289 }
            pushAuthKey = r2     // Catch:{ all -> 0x0289 }
        L_0x00c3:
            java.lang.String r2 = passcodeHash     // Catch:{ all -> 0x0289 }
            int r2 = r2.length()     // Catch:{ all -> 0x0289 }
            if (r2 <= 0) goto L_0x00dc
            int r2 = lastPauseTime     // Catch:{ all -> 0x0289 }
            if (r2 != 0) goto L_0x00dc
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0289 }
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            r8 = 600(0x258, double:2.964E-321)
            long r6 = r6 - r8
            int r2 = (int) r6     // Catch:{ all -> 0x0289 }
            lastPauseTime = r2     // Catch:{ all -> 0x0289 }
        L_0x00dc:
            java.lang.String r2 = "passcodeSalt"
            java.lang.String r6 = ""
            java.lang.String r1 = r1.getString(r2, r6)     // Catch:{ all -> 0x0289 }
            int r2 = r1.length()     // Catch:{ all -> 0x0289 }
            if (r2 <= 0) goto L_0x00f1
            byte[] r1 = android.util.Base64.decode(r1, r3)     // Catch:{ all -> 0x0289 }
            passcodeSalt = r1     // Catch:{ all -> 0x0289 }
            goto L_0x00f5
        L_0x00f1:
            byte[] r1 = new byte[r3]     // Catch:{ all -> 0x0289 }
            passcodeSalt = r1     // Catch:{ all -> 0x0289 }
        L_0x00f5:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "mainconfig"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "save_gallery"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            saveToGallery = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "autoplay_gif"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            autoplayGifs = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "autoplay_video"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            autoplayVideo = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "mapPreviewType"
            r6 = 2
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x0289 }
            mapPreviewType = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "raise_to_speak"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            raiseToSpeak = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "custom_tabs"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            customTabs = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "direct_share"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            directShare = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "shuffleMusic"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            shuffleMusic = r2     // Catch:{ all -> 0x0289 }
            if (r2 != 0) goto L_0x014a
            java.lang.String r2 = "playOrderReversed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            if (r2 == 0) goto L_0x014a
            r2 = 1
            goto L_0x014b
        L_0x014a:
            r2 = 0
        L_0x014b:
            playOrderReversed = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "inappCamera"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            inappCamera = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "cameraCache"
            boolean r2 = r1.contains(r2)     // Catch:{ all -> 0x0289 }
            hasCameraCache = r2     // Catch:{ all -> 0x0289 }
            roundCamera16to9 = r4     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "assistantSupport"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            assistantSupport = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "repeatMode"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            repeatMode = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "fons_size"
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()     // Catch:{ all -> 0x0289 }
            if (r7 == 0) goto L_0x017a
            r7 = 18
            goto L_0x017c
        L_0x017a:
            r7 = 16
        L_0x017c:
            int r2 = r1.getInt(r2, r7)     // Catch:{ all -> 0x0289 }
            fontSize = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "bubbleRadius"
            r7 = 10
            int r2 = r1.getInt(r2, r7)     // Catch:{ all -> 0x0289 }
            bubbleRadius = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "iv_font_size"
            int r7 = fontSize     // Catch:{ all -> 0x0289 }
            int r2 = r1.getInt(r2, r7)     // Catch:{ all -> 0x0289 }
            ivFontSize = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "allowBigEmoji"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            allowBigEmoji = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "useSystemEmoji"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            useSystemEmoji = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "streamMedia"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            streamMedia = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "saveStreamMedia"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            saveStreamMedia = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "smoothKeyboard2"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            smoothKeyboard = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "pauseMusicOnRecord"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            pauseMusicOnRecord = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "streamAllVideo"
            boolean r7 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ all -> 0x0289 }
            boolean r2 = r1.getBoolean(r2, r7)     // Catch:{ all -> 0x0289 }
            streamAllVideo = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "streamMkv"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            streamMkv = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "suggestStickers"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            suggestStickers = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "sortContactsByName"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            sortContactsByName = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "sortFilesByName"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            sortFilesByName = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "noSoundHintShowed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            noSoundHintShowed = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "directShareHash2"
            java.lang.String r2 = r1.getString(r2, r5)     // Catch:{ all -> 0x0289 }
            directShareHash = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "useThreeLinesLayout"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            useThreeLinesLayout = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "archiveHidden"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            archiveHidden = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "distanceSystemType"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            distanceSystemType = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "devicePerformanceClass"
            r5 = -1
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x0289 }
            devicePerformanceClass = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "loopStickers"
            boolean r2 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            loopStickers = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "keep_media"
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x0289 }
            keepMedia = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "lastKeepMediaCheckTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            lastKeepMediaCheckTime = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "searchMessagesAsListHintShows"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            searchMessagesAsListHintShows = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "searchMessagesAsListUsed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            searchMessagesAsListUsed = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "stickersReorderingHintUsed"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            stickersReorderingHintUsed = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "textSelectionHintShows"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            textSelectionHintShows = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "scheduledOrNoSoundHintShows"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            scheduledOrNoSoundHintShows = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "lockRecordAudioVideoHint"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0289 }
            lockRecordAudioVideoHint = r2     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "disableVoiceAudioEffects"
            boolean r1 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0289 }
            disableVoiceAudioEffects = r1     // Catch:{ all -> 0x0289 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "Notifications"
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ all -> 0x0289 }
            java.lang.String r2 = "AllAccounts"
            boolean r1 = r1.getBoolean(r2, r4)     // Catch:{ all -> 0x0289 }
            showNotificationsForAllAccounts = r1     // Catch:{ all -> 0x0289 }
            configLoaded = r4     // Catch:{ all -> 0x0289 }
            monitor-exit(r0)     // Catch:{ all -> 0x0289 }
            return
        L_0x0287:
            monitor-exit(r0)     // Catch:{ all -> 0x0289 }
            return
        L_0x0289:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0289 }
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
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, length));
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
            return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bArr2, 0, length2)));
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
        edit.putBoolean("searchMessagesAsListUsed", z);
        edit.commit();
    }

    public static void setStickersReorderingHintUsed(boolean z) {
        stickersReorderingHintUsed = z;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("stickersReorderingHintUsed", z);
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

    public static void removeScheduledOrNoSuoundHint() {
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

    public static void checkKeepMedia() {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(currentTimeMillis - lastKeepMediaCheckTime) >= 3600) {
            lastKeepMediaCheckTime = currentTimeMillis;
            Utilities.globalQueue.postRunnable(new Runnable(currentTimeMillis, FileLoader.checkDirectory(4)) {
                public final /* synthetic */ int f$0;
                public final /* synthetic */ File f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    SharedConfig.lambda$checkKeepMedia$0(this.f$0, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$checkKeepMedia$0(int i, File file) {
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

    public static void toggleSaveToGallery() {
        saveToGallery = !saveToGallery;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("save_gallery", saveToGallery);
        edit.commit();
        checkSaveToGalleryFiles();
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

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("inappCamera", inappCamera);
        edit.commit();
    }

    public static void toggleAssistantSupport() {
        assistantSupport = !assistantSupport;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("assistantSupport", assistantSupport);
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
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "Telegram");
            File file2 = new File(file, "Telegram Images");
            file2.mkdir();
            File file3 = new File(file, "Telegram Video");
            file3.mkdir();
            if (saveToGallery) {
                if (file2.isDirectory()) {
                    new File(file2, ".nomedia").delete();
                }
                if (file3.isDirectory()) {
                    new File(file3, ".nomedia").delete();
                    return;
                }
                return;
            }
            if (file2.isDirectory()) {
                AndroidUtilities.createEmptyFile(new File(file2, ".nomedia"));
            }
            if (file3.isDirectory()) {
                AndroidUtilities.createEmptyFile(new File(file3, ".nomedia"));
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static int getDevicePerformanceClass() {
        int i;
        if (devicePerformanceClass == -1) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                String readLine = randomAccessFile.readLine();
                i = readLine != null ? Utilities.parseInt(readLine).intValue() / 1000 : -1;
                try {
                    randomAccessFile.close();
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                i = -1;
            }
            int i2 = Build.VERSION.SDK_INT;
            int i3 = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            if (i2 < 21 || i3 <= 2 || memoryClass <= 100 || ((i3 <= 4 && i != -1 && i <= 1250) || ((i3 <= 4 && i <= 1600 && memoryClass <= 128 && i2 <= 21) || (i3 <= 4 && i <= 1300 && memoryClass <= 128 && i2 <= 24)))) {
                devicePerformanceClass = 0;
            } else if (i3 < 8 || memoryClass <= 160 || ((i != -1 && i <= 1650) || (i == -1 && i3 == 8 && i2 <= 23))) {
                devicePerformanceClass = 1;
            } else {
                devicePerformanceClass = 2;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("device performance info (cpu_count = " + i3 + ", freq = " + i + ", memoryClass = " + memoryClass + ", android version " + i2 + ")");
            }
        }
        return devicePerformanceClass;
    }
}
