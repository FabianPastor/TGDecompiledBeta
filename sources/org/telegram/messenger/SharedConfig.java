package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
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
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    public static boolean autoplayVideo = true;
    public static int badPasscodeTries = 0;
    public static int bubbleRadius = 10;
    private static boolean configLoaded = false;
    public static ProxyInfo currentProxy = null;
    public static boolean customTabs = true;
    private static int devicePerformanceClass = 0;
    public static boolean directShare = true;
    public static long directShareHash = 0;
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
    public static boolean playOrderReversed = false;
    public static ArrayList<ProxyInfo> proxyList = new ArrayList();
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
    public static boolean sortContactsByName = false;
    public static boolean sortFilesByName = false;
    public static boolean streamAllVideo = false;
    public static boolean streamMedia = true;
    public static boolean streamMkv = false;
    public static int suggestStickers = 0;
    private static final Object sync = new Object();
    public static int textSelectionHintShows = 0;
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;
    public static boolean useThreeLinesLayout;

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
            String str5 = "";
            if (this.address == null) {
                this.address = str5;
            }
            if (this.password == null) {
                this.password = str5;
            }
            if (this.username == null) {
                this.username = str5;
            }
            if (this.secret == null) {
                this.secret = str5;
            }
        }
    }

    static {
        loadConfig();
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                edit.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                edit.putString("passcodeHash1", passcodeHash);
                edit.putString("passcodeSalt", passcodeSalt.length > 0 ? Base64.encodeToString(passcodeSalt, 0) : "");
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
                edit.putString("pushAuthKey", pushAuthKey != null ? Base64.encodeToString(pushAuthKey, 0) : "");
                edit.putInt("lastLocalId", lastLocalId);
                edit.putString("passportConfigJson", passportConfigJson);
                edit.putInt("passportConfigHash", passportConfigHash);
                edit.putBoolean("sortContactsByName", sortContactsByName);
                edit.putBoolean("sortFilesByName", sortFilesByName);
                edit.putInt("textSelectionHintShows", textSelectionHintShows);
                edit.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                edit.commit();
            } catch (Exception e) {
                FileLog.e(e);
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

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
            saveIncomingPhotos = sharedPreferences.getBoolean("saveIncomingPhotos", false);
            passcodeHash = sharedPreferences.getString("passcodeHash1", "");
            appLocked = sharedPreferences.getBoolean("appLocked", false);
            passcodeType = sharedPreferences.getInt("passcodeType", 0);
            passcodeRetryInMs = sharedPreferences.getLong("passcodeRetryInMs", 0);
            lastUptimeMillis = sharedPreferences.getLong("lastUptimeMillis", 0);
            badPasscodeTries = sharedPreferences.getInt("badPasscodeTries", 0);
            autoLockIn = sharedPreferences.getInt("autoLockIn", 3600);
            lastPauseTime = sharedPreferences.getInt("lastPauseTime", 0);
            useFingerprint = sharedPreferences.getBoolean("useFingerprint", true);
            lastUpdateVersion = sharedPreferences.getString("lastUpdateVersion2", "3.5");
            allowScreenCapture = sharedPreferences.getBoolean("allowScreenCapture", false);
            lastLocalId = sharedPreferences.getInt("lastLocalId", -210000);
            pushString = sharedPreferences.getString("pushString2", "");
            passportConfigJson = sharedPreferences.getString("passportConfigJson", "");
            passportConfigHash = sharedPreferences.getInt("passportConfigHash", 0);
            String string = sharedPreferences.getString("pushAuthKey", null);
            if (!TextUtils.isEmpty(string)) {
                pushAuthKey = Base64.decode(string, 0);
            }
            if (passcodeHash.length() > 0 && lastPauseTime == 0) {
                lastPauseTime = (int) ((SystemClock.uptimeMillis() / 1000) - 600);
            }
            String string2 = sharedPreferences.getString("passcodeSalt", "");
            if (string2.length() > 0) {
                passcodeSalt = Base64.decode(string2, 0);
            } else {
                passcodeSalt = new byte[0];
            }
            sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            saveToGallery = sharedPreferences.getBoolean("save_gallery", false);
            autoplayGifs = sharedPreferences.getBoolean("autoplay_gif", true);
            autoplayVideo = sharedPreferences.getBoolean("autoplay_video", true);
            mapPreviewType = sharedPreferences.getInt("mapPreviewType", 2);
            raiseToSpeak = sharedPreferences.getBoolean("raise_to_speak", true);
            customTabs = sharedPreferences.getBoolean("custom_tabs", true);
            directShare = sharedPreferences.getBoolean("direct_share", true);
            shuffleMusic = sharedPreferences.getBoolean("shuffleMusic", false);
            playOrderReversed = sharedPreferences.getBoolean("playOrderReversed", false);
            inappCamera = sharedPreferences.getBoolean("inappCamera", true);
            hasCameraCache = sharedPreferences.contains("cameraCache");
            roundCamera16to9 = true;
            repeatMode = sharedPreferences.getInt("repeatMode", 0);
            fontSize = sharedPreferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
            bubbleRadius = sharedPreferences.getInt("bubbleRadius", 10);
            ivFontSize = sharedPreferences.getInt("iv_font_size", fontSize);
            allowBigEmoji = sharedPreferences.getBoolean("allowBigEmoji", true);
            useSystemEmoji = sharedPreferences.getBoolean("useSystemEmoji", false);
            streamMedia = sharedPreferences.getBoolean("streamMedia", true);
            saveStreamMedia = sharedPreferences.getBoolean("saveStreamMedia", true);
            streamAllVideo = sharedPreferences.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
            streamMkv = sharedPreferences.getBoolean("streamMkv", false);
            suggestStickers = sharedPreferences.getInt("suggestStickers", 0);
            sortContactsByName = sharedPreferences.getBoolean("sortContactsByName", false);
            sortFilesByName = sharedPreferences.getBoolean("sortFilesByName", false);
            noSoundHintShowed = sharedPreferences.getBoolean("noSoundHintShowed", false);
            directShareHash = sharedPreferences.getLong("directShareHash", 0);
            useThreeLinesLayout = sharedPreferences.getBoolean("useThreeLinesLayout", false);
            archiveHidden = sharedPreferences.getBoolean("archiveHidden", false);
            distanceSystemType = sharedPreferences.getInt("distanceSystemType", 0);
            devicePerformanceClass = sharedPreferences.getInt("devicePerformanceClass", -1);
            loopStickers = sharedPreferences.getBoolean("loopStickers", true);
            keepMedia = sharedPreferences.getInt("keep_media", 2);
            lastKeepMediaCheckTime = sharedPreferences.getInt("lastKeepMediaCheckTime", 0);
            searchMessagesAsListHintShows = sharedPreferences.getInt("searchMessagesAsListHintShows", 0);
            searchMessagesAsListUsed = sharedPreferences.getBoolean("searchMessagesAsListUsed", false);
            textSelectionHintShows = sharedPreferences.getInt("textSelectionHintShows", 0);
            scheduledOrNoSoundHintShows = sharedPreferences.getInt("scheduledOrNoSoundHintShows", 0);
            showNotificationsForAllAccounts = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("AllAccounts", true);
            configLoaded = true;
        }
    }

    public static void increaseBadPasscodeTries() {
        badPasscodeTries++;
        int i = badPasscodeTries;
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
            passportConfigMap = new HashMap();
            try {
                JSONObject jSONObject = new JSONObject(passportConfigJson);
                Iterator keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String str = (String) keys.next();
                    passportConfigMap.put(str.toUpperCase(), jSONObject.getString(str).toUpperCase());
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return passportConfigMap;
    }

    public static boolean checkPasscode(String str) {
        String str2 = "UTF-8";
        byte[] bytes;
        if (passcodeSalt.length == 0) {
            boolean equals = Utilities.MD5(str).equals(passcodeHash);
            if (equals) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    bytes = str.getBytes(str2);
                    byte[] bArr = new byte[(bytes.length + 32)];
                    System.arraycopy(passcodeSalt, 0, bArr, 0, 16);
                    System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                    System.arraycopy(passcodeSalt, 0, bArr, bytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, bArr.length));
                    saveConfig();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return equals;
        }
        try {
            bytes = str.getBytes(str2);
            byte[] bArr2 = new byte[(bytes.length + 32)];
            System.arraycopy(passcodeSalt, 0, bArr2, 0, 16);
            System.arraycopy(bytes, 0, bArr2, 16, bytes.length);
            System.arraycopy(passcodeSalt, 0, bArr2, bytes.length + 16, 16);
            return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bArr2, 0, bArr2.length)));
        } catch (Exception e2) {
            FileLog.e(e2);
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
        saveConfig();
    }

    public static void setSuggestStickers(int i) {
        suggestStickers = i;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("suggestStickers", suggestStickers);
        edit.commit();
    }

    public static void setSearchMessagesAsListUsed(boolean z) {
        searchMessagesAsListUsed = z;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("searchMessagesAsListUsed", z);
        edit.commit();
    }

    public static void increaseTextSelectionHintShowed() {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = textSelectionHintShows + 1;
        textSelectionHintShows = i;
        edit.putInt("textSelectionHintShows", i);
        edit.commit();
    }

    public static void removeTextSelectionHint() {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("textSelectionHintShows", 3);
        edit.commit();
    }

    public static void increaseScheduledOrNoSuoundHintShowed() {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = scheduledOrNoSoundHintShows + 1;
        scheduledOrNoSoundHintShows = i;
        edit.putInt("scheduledOrNoSoundHintShows", i);
        edit.commit();
    }

    public static void removeScheduledOrNoSuoundHint() {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("scheduledOrNoSoundHintShows", 3);
        edit.commit();
    }

    public static void increaseSearchAsListHintShows() {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        int i = searchMessagesAsListHintShows + 1;
        searchMessagesAsListHintShows = i;
        edit.putInt("searchMessagesAsListHintShows", i);
        edit.commit();
    }

    public static void setKeepMedia(int i) {
        keepMedia = i;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("keep_media", keepMedia);
        edit.commit();
    }

    public static void checkKeepMedia() {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(currentTimeMillis - lastKeepMediaCheckTime) >= 3600) {
            lastKeepMediaCheckTime = currentTimeMillis;
            Utilities.globalQueue.postRunnable(new -$$Lambda$SharedConfig$ydV20oC-JGSeCOfHc3df7uM2wpQ(currentTimeMillis, FileLoader.checkDirectory(4)));
        }
    }

    static /* synthetic */ void lambda$checkKeepMedia$0(int i, File file) {
        int i2 = keepMedia;
        if (i2 != 2) {
            i2 = i2 == 0 ? 7 : i2 == 1 ? 30 : 3;
            long j = (long) (i - (i2 * 86400));
            SparseArray createMediaPaths = ImageLoader.getInstance().createMediaPaths();
            for (int i3 = 0; i3 < createMediaPaths.size(); i3++) {
                if (createMediaPaths.keyAt(i3) != 4) {
                    try {
                        Utilities.clearDir(((File) createMediaPaths.valueAt(i3)).getAbsolutePath(), 0, j, false);
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
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("lastKeepMediaCheckTime", lastKeepMediaCheckTime);
        edit.commit();
    }

    public static void toggleLoopStickers() {
        loopStickers ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("loopStickers", loopStickers);
        edit.commit();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("allowBigEmoji", allowBigEmoji);
        edit.commit();
    }

    public static void toggleShuffleMusic(int i) {
        if (i == 2) {
            shuffleMusic ^= 1;
        } else {
            playOrderReversed ^= 1;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("shuffleMusic", shuffleMusic);
        edit.putBoolean("playOrderReversed", playOrderReversed);
        edit.commit();
    }

    public static void toggleRepeatMode() {
        repeatMode++;
        if (repeatMode > 2) {
            repeatMode = 0;
        }
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("repeatMode", repeatMode);
        edit.commit();
    }

    public static void toggleSaveToGallery() {
        saveToGallery ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("save_gallery", saveToGallery);
        edit.commit();
        checkSaveToGalleryFiles();
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("autoplay_gif", autoplayGifs);
        edit.commit();
    }

    public static void setUseThreeLinesLayout(boolean z) {
        useThreeLinesLayout = z;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        edit.commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
    }

    public static void toggleArchiveHidden() {
        archiveHidden ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("archiveHidden", archiveHidden);
        edit.commit();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("autoplay_video", autoplayVideo);
        edit.commit();
    }

    public static boolean isSecretMapPreviewSet() {
        return MessagesController.getGlobalMainSettings().contains("mapPreviewType");
    }

    public static void setSecretMapPreviewType(int i) {
        mapPreviewType = i;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("mapPreviewType", mapPreviewType);
        edit.commit();
    }

    public static void setNoSoundHintShowed(boolean z) {
        if (noSoundHintShowed != z) {
            noSoundHintShowed = z;
            Editor edit = MessagesController.getGlobalMainSettings().edit();
            edit.putBoolean("noSoundHintShowed", noSoundHintShowed);
            edit.commit();
        }
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("raise_to_speak", raiseToSpeak);
        edit.commit();
    }

    public static void toggleCustomTabs() {
        customTabs ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("custom_tabs", customTabs);
        edit.commit();
    }

    public static void toggleDirectShare() {
        directShare ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("direct_share", directShare);
        edit.commit();
    }

    public static void toggleStreamMedia() {
        streamMedia ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamMedia", streamMedia);
        edit.commit();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("sortContactsByName", sortContactsByName);
        edit.commit();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("sortFilesByName", sortFilesByName);
        edit.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamAllVideo", streamAllVideo);
        edit.commit();
    }

    public static void toggleStreamMkv() {
        streamMkv ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamMkv", streamMkv);
        edit.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("saveStreamMedia", saveStreamMedia);
        edit.commit();
    }

    public static void toggleInappCamera() {
        inappCamera ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("inappCamera", inappCamera);
        edit.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("roundCamera16to9", roundCamera16to9);
        edit.commit();
    }

    public static void setDistanceSystemType(int i) {
        distanceSystemType = i;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("distanceSystemType", distanceSystemType);
        edit.commit();
        LocaleController.resetImperialSystemType();
    }

    public static void loadProxyList() {
        if (!proxyListLoaded) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String str = "";
            String string = sharedPreferences.getString("proxy_ip", str);
            String string2 = sharedPreferences.getString("proxy_user", str);
            String string3 = sharedPreferences.getString("proxy_pass", str);
            String string4 = sharedPreferences.getString("proxy_secret", str);
            int i = sharedPreferences.getInt("proxy_port", 1080);
            proxyListLoaded = true;
            proxyList.clear();
            currentProxy = null;
            String string5 = sharedPreferences.getString("proxy_list", null);
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
            ProxyInfo proxyInfo = (ProxyInfo) proxyList.get(i);
            String str = proxyInfo.address;
            String str2 = "";
            if (str == null) {
                str = str2;
            }
            serializedData.writeString(str);
            serializedData.writeInt32(proxyInfo.port);
            str = proxyInfo.username;
            if (str == null) {
                str = str2;
            }
            serializedData.writeString(str);
            str = proxyInfo.password;
            if (str == null) {
                str = str2;
            }
            serializedData.writeString(str);
            String str3 = proxyInfo.secret;
            if (str3 == null) {
                str3 = str2;
            }
            serializedData.writeString(str3);
        }
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).commit();
        serializedData.cleanup();
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int size = proxyList.size();
        for (int i = 0; i < size; i++) {
            ProxyInfo proxyInfo2 = (ProxyInfo) proxyList.get(i);
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
            String str = "proxy_enabled";
            boolean z = globalMainSettings.getBoolean(str, false);
            Editor edit = globalMainSettings.edit();
            String str2 = "";
            edit.putString("proxy_ip", str2);
            edit.putString("proxy_pass", str2);
            edit.putString("proxy_user", str2);
            edit.putString("proxy_secret", str2);
            edit.putInt("proxy_port", 1080);
            edit.putBoolean(str, false);
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
            String str = ".nomedia";
            if (saveToGallery) {
                if (file2.isDirectory()) {
                    new File(file2, str).delete();
                }
                if (file3.isDirectory()) {
                    new File(file3, str).delete();
                    return;
                }
                return;
            }
            if (file2.isDirectory()) {
                new File(file2, str).createNewFile();
            }
            if (file3.isDirectory()) {
                new File(file3, str).createNewFile();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static int getDevicePerfomanceClass() {
        if (devicePerformanceClass == -1) {
            int intValue;
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                String readLine = randomAccessFile.readLine();
                intValue = readLine != null ? Utilities.parseInt(readLine).intValue() / 1000 : -1;
                try {
                    randomAccessFile.close();
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                intValue = -1;
            }
            int i = VERSION.SDK_INT;
            int i2 = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            if (i < 21 || i2 <= 2 || memoryClass <= 100 || ((i2 <= 4 && intValue != -1 && intValue <= 1250) || ((i2 <= 4 && intValue <= 1600 && memoryClass <= 128 && i <= 21) || (i2 <= 4 && intValue <= 1300 && memoryClass <= 128 && i <= 24)))) {
                devicePerformanceClass = 0;
            } else if (i2 < 8 || memoryClass <= 160 || ((intValue != -1 && intValue <= 1650) || (intValue == -1 && i2 == 8 && i <= 23))) {
                devicePerformanceClass = 1;
            } else {
                devicePerformanceClass = 2;
            }
            if (BuildVars.DEBUG_VERSION) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("device performance info (cpu_count = ");
                stringBuilder.append(i2);
                stringBuilder.append(", freq = ");
                stringBuilder.append(intValue);
                stringBuilder.append(", memoryClass = ");
                stringBuilder.append(memoryClass);
                stringBuilder.append(", android version ");
                stringBuilder.append(i);
                stringBuilder.append(")");
                FileLog.d(stringBuilder.toString());
            }
        }
        return devicePerformanceClass;
    }
}
