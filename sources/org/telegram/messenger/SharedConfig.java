package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C0555C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;

public class SharedConfig {
    public static boolean allowBigEmoji;
    public static boolean allowScreenCapture;
    public static boolean appLocked;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    private static boolean configLoaded;
    public static ProxyInfo currentProxy;
    public static boolean customTabs = true;
    public static boolean directShare = true;
    public static int fontSize = AndroidUtilities.dp(16.0f);
    public static boolean groupPhotosEnabled = true;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter;
    public static long lastAppPauseTime;
    private static int lastLocalId = -210000;
    public static int lastPauseTime;
    public static String lastUpdateVersion;
    private static final Object localIdSync = new Object();
    public static String passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType;
    public static boolean playOrderReversed;
    public static ArrayList<ProxyInfo> proxyList = new ArrayList();
    private static boolean proxyListLoaded;
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;
    public static String pushString = TtmlNode.ANONYMOUS_REGION_ID;
    public static boolean raiseToSpeak = true;
    public static int repeatMode;
    public static boolean roundCamera16to9 = true;
    public static boolean saveIncomingPhotos;
    public static boolean saveStreamMedia = true;
    public static boolean saveToGallery;
    public static boolean shuffleMusic;
    public static boolean streamAllVideo = false;
    public static boolean streamMedia = true;
    public static int suggestStickers;
    private static final Object sync = new Object();
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;

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
            if (this.address == null) {
                this.address = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (this.password == null) {
                this.password = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (this.username == null) {
                this.username = TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (this.secret == null) {
                this.secret = TtmlNode.ANONYMOUS_REGION_ID;
            }
        }
    }

    static {
        loadConfig();
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                editor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                editor.putString("passcodeHash1", passcodeHash);
                editor.putString("passcodeSalt", passcodeSalt.length > 0 ? Base64.encodeToString(passcodeSalt, 0) : TtmlNode.ANONYMOUS_REGION_ID);
                editor.putBoolean("appLocked", appLocked);
                editor.putInt("passcodeType", passcodeType);
                editor.putInt("autoLockIn", autoLockIn);
                editor.putInt("lastPauseTime", lastPauseTime);
                editor.putLong("lastAppPauseTime", lastAppPauseTime);
                editor.putString("lastUpdateVersion2", lastUpdateVersion);
                editor.putBoolean("useFingerprint", useFingerprint);
                editor.putBoolean("allowScreenCapture", allowScreenCapture);
                editor.putString("pushString2", pushString);
                editor.putString("pushAuthKey", pushAuthKey != null ? Base64.encodeToString(pushAuthKey, 0) : TtmlNode.ANONYMOUS_REGION_ID);
                editor.putInt("lastLocalId", lastLocalId);
                editor.commit();
            } catch (Throwable e) {
                FileLog.m3e(e);
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

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
            saveIncomingPhotos = preferences.getBoolean("saveIncomingPhotos", false);
            passcodeHash = preferences.getString("passcodeHash1", TtmlNode.ANONYMOUS_REGION_ID);
            appLocked = preferences.getBoolean("appLocked", false);
            passcodeType = preferences.getInt("passcodeType", 0);
            autoLockIn = preferences.getInt("autoLockIn", 3600);
            lastPauseTime = preferences.getInt("lastPauseTime", 0);
            lastAppPauseTime = preferences.getLong("lastAppPauseTime", 0);
            useFingerprint = preferences.getBoolean("useFingerprint", true);
            lastUpdateVersion = preferences.getString("lastUpdateVersion2", "3.5");
            allowScreenCapture = preferences.getBoolean("allowScreenCapture", false);
            lastLocalId = preferences.getInt("lastLocalId", -210000);
            pushString = preferences.getString("pushString2", TtmlNode.ANONYMOUS_REGION_ID);
            String authKeyString = preferences.getString("pushAuthKey", null);
            if (!TextUtils.isEmpty(authKeyString)) {
                pushAuthKey = Base64.decode(authKeyString, 0);
            }
            if (passcodeHash.length() > 0 && lastPauseTime == 0) {
                lastPauseTime = (int) ((System.currentTimeMillis() / 1000) - 600);
            }
            String passcodeSaltString = preferences.getString("passcodeSalt", TtmlNode.ANONYMOUS_REGION_ID);
            if (passcodeSaltString.length() > 0) {
                passcodeSalt = Base64.decode(passcodeSaltString, 0);
            } else {
                passcodeSalt = new byte[0];
            }
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            saveToGallery = preferences.getBoolean("save_gallery", false);
            autoplayGifs = preferences.getBoolean("autoplay_gif", true);
            raiseToSpeak = preferences.getBoolean("raise_to_speak", true);
            customTabs = preferences.getBoolean("custom_tabs", true);
            directShare = preferences.getBoolean("direct_share", true);
            shuffleMusic = preferences.getBoolean("shuffleMusic", false);
            playOrderReversed = preferences.getBoolean("playOrderReversed", false);
            inappCamera = preferences.getBoolean("inappCamera", true);
            roundCamera16to9 = true;
            groupPhotosEnabled = preferences.getBoolean("groupPhotosEnabled", true);
            repeatMode = preferences.getInt("repeatMode", 0);
            fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
            allowBigEmoji = preferences.getBoolean("allowBigEmoji", false);
            useSystemEmoji = preferences.getBoolean("useSystemEmoji", false);
            streamMedia = preferences.getBoolean("streamMedia", true);
            saveStreamMedia = preferences.getBoolean("saveStreamMedia", true);
            streamAllVideo = preferences.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
            suggestStickers = preferences.getInt("suggestStickers", 0);
            configLoaded = true;
        }
    }

    public static boolean checkPasscode(String passcode) {
        boolean result = false;
        byte[] passcodeBytes;
        byte[] bytes;
        if (passcodeSalt.length == 0) {
            result = Utilities.MD5(passcode).equals(passcodeHash);
            if (result) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    passcodeBytes = passcode.getBytes(C0555C.UTF8_NAME);
                    bytes = new byte[(passcodeBytes.length + 32)];
                    System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                    saveConfig();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        } else {
            try {
                passcodeBytes = passcode.getBytes(C0555C.UTF8_NAME);
                bytes = new byte[(passcodeBytes.length + 32)];
                System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                result = passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length)));
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
        return result;
    }

    public static void clearConfig() {
        saveIncomingPhotos = false;
        appLocked = false;
        passcodeType = 0;
        passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        lastPauseTime = 0;
        useFingerprint = true;
        isWaitingForPasscodeEnter = false;
        allowScreenCapture = false;
        lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
        saveConfig();
    }

    public static void setSuggestStickers(int type) {
        suggestStickers = type;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("suggestStickers", suggestStickers);
        editor.commit();
    }

    public static void toggleShuffleMusic(int type) {
        boolean z = true;
        if (type == 2) {
            if (shuffleMusic) {
                z = false;
            }
            shuffleMusic = z;
        } else {
            if (playOrderReversed) {
                z = false;
            }
            playOrderReversed = z;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("shuffleMusic", shuffleMusic);
        editor.putBoolean("playOrderReversed", playOrderReversed);
        editor.commit();
    }

    public static void toggleRepeatMode() {
        repeatMode++;
        if (repeatMode > 2) {
            repeatMode = 0;
        }
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("repeatMode", repeatMode);
        editor.commit();
    }

    public static void toggleSaveToGallery() {
        saveToGallery = !saveToGallery;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("save_gallery", saveToGallery);
        editor.commit();
        checkSaveToGalleryFiles();
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("autoplay_gif", autoplayGifs);
        editor.commit();
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.commit();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("custom_tabs", customTabs);
        editor.commit();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("direct_share", directShare);
        editor.commit();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamMedia", streamMedia);
        editor.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("streamAllVideo", streamAllVideo);
        editor.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("saveStreamMedia", saveStreamMedia);
        editor.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("direct_share", inappCamera);
        editor.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("roundCamera16to9", roundCamera16to9);
        editor.commit();
    }

    public static void toggleGroupPhotosEnabled() {
        groupPhotosEnabled = !groupPhotosEnabled;
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("groupPhotosEnabled", groupPhotosEnabled);
        editor.commit();
    }

    public static void loadProxyList() {
        if (!proxyListLoaded) {
            ProxyInfo info;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String proxyAddress = preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID);
            String proxyUsername = preferences.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID);
            String proxyPassword = preferences.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID);
            String proxySecret = preferences.getString("proxy_secret", TtmlNode.ANONYMOUS_REGION_ID);
            int proxyPort = preferences.getInt("proxy_port", 1080);
            proxyListLoaded = true;
            proxyList.clear();
            currentProxy = null;
            String list = preferences.getString("proxy_list", null);
            if (!TextUtils.isEmpty(list)) {
                SerializedData data = new SerializedData(Base64.decode(list, 0));
                int count = data.readInt32(false);
                for (int a = 0; a < count; a++) {
                    info = new ProxyInfo(data.readString(false), data.readInt32(false), data.readString(false), data.readString(false), data.readString(false));
                    proxyList.add(info);
                    if (currentProxy == null && !TextUtils.isEmpty(proxyAddress) && proxyAddress.equals(info.address) && proxyPort == info.port) {
                        if (proxyUsername.equals(info.username) && proxyPassword.equals(info.password)) {
                            currentProxy = info;
                        }
                    }
                }
            }
            if (currentProxy == null && !TextUtils.isEmpty(proxyAddress)) {
                info = new ProxyInfo(proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
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
            String str;
            ProxyInfo info = (ProxyInfo) proxyList.get(a);
            serializedData.writeString(info.address != null ? info.address : TtmlNode.ANONYMOUS_REGION_ID);
            serializedData.writeInt32(info.port);
            serializedData.writeString(info.username != null ? info.username : TtmlNode.ANONYMOUS_REGION_ID);
            serializedData.writeString(info.password != null ? info.password : TtmlNode.ANONYMOUS_REGION_ID);
            if (info.secret != null) {
                str = info.secret;
            } else {
                str = TtmlNode.ANONYMOUS_REGION_ID;
            }
            serializedData.writeString(str);
        }
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).commit();
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int count = proxyList.size();
        for (int a = 0; a < count; a++) {
            ProxyInfo info = (ProxyInfo) proxyList.get(a);
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
            Editor editor = preferences.edit();
            editor.putString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID);
            editor.putString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID);
            editor.putString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID);
            editor.putString("proxy_secret", TtmlNode.ANONYMOUS_REGION_ID);
            editor.putInt("proxy_port", 1080);
            editor.putBoolean("proxy_enabled", false);
            editor.putBoolean("proxy_enabled_calls", false);
            editor.commit();
            if (enabled) {
                ConnectionsManager.setProxySettings(false, TtmlNode.ANONYMOUS_REGION_ID, 0, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
            }
        }
        proxyList.remove(proxyInfo);
        saveProxyList();
    }

    public static void checkSaveToGalleryFiles() {
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
                new File(imagePath, ".nomedia").createNewFile();
            }
            if (videoPath.isDirectory()) {
                new File(videoPath, ".nomedia").createNewFile();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }
}
