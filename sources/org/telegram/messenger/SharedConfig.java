package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import java.io.File;
import org.telegram.messenger.exoplayer2.C0542C;

public class SharedConfig {
    public static boolean allowBigEmoji = false;
    public static boolean allowScreenCapture = false;
    public static boolean appLocked = false;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    private static boolean configLoaded = false;
    public static boolean customTabs = true;
    public static boolean directShare = true;
    public static int fontSize = AndroidUtilities.dp(16.0f);
    public static boolean groupPhotosEnabled = true;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter = false;
    public static long lastAppPauseTime = 0;
    private static int lastLocalId = -210000;
    public static int lastPauseTime = 0;
    public static String lastUpdateVersion = null;
    private static final Object localIdSync = new Object();
    public static String passcodeHash = "";
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType = 0;
    public static boolean playOrderReversed = false;
    public static byte[] pushAuthKey = null;
    public static byte[] pushAuthKeyId = null;
    public static String pushString = "";
    public static boolean raiseToSpeak = true;
    public static int repeatMode = 0;
    public static boolean roundCamera16to9 = true;
    public static boolean saveIncomingPhotos = false;
    public static boolean saveStreamMedia = true;
    public static boolean saveToGallery = false;
    public static boolean shuffleMusic = false;
    public static boolean streamAllVideo = false;
    public static boolean streamMedia = true;
    public static int suggestStickers = 0;
    private static final Object sync = new Object();
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;

    static {
        loadConfig();
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                edit.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                edit.putString("passcodeHash1", passcodeHash);
                edit.putString("passcodeSalt", passcodeSalt.length > 0 ? Base64.encodeToString(passcodeSalt, 0) : TtmlNode.ANONYMOUS_REGION_ID);
                edit.putBoolean("appLocked", appLocked);
                edit.putInt("passcodeType", passcodeType);
                edit.putInt("autoLockIn", autoLockIn);
                edit.putInt("lastPauseTime", lastPauseTime);
                edit.putLong("lastAppPauseTime", lastAppPauseTime);
                edit.putString("lastUpdateVersion2", lastUpdateVersion);
                edit.putBoolean("useFingerprint", useFingerprint);
                edit.putBoolean("allowScreenCapture", allowScreenCapture);
                edit.putString("pushString2", pushString);
                edit.putString("pushAuthKey", pushAuthKey != null ? Base64.encodeToString(pushAuthKey, 0) : TtmlNode.ANONYMOUS_REGION_ID);
                edit.putInt("lastLocalId", lastLocalId);
                edit.commit();
            } catch (Throwable e) {
                FileLog.m3e(e);
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
            passcodeHash = sharedPreferences.getString("passcodeHash1", TtmlNode.ANONYMOUS_REGION_ID);
            appLocked = sharedPreferences.getBoolean("appLocked", false);
            passcodeType = sharedPreferences.getInt("passcodeType", 0);
            autoLockIn = sharedPreferences.getInt("autoLockIn", 3600);
            lastPauseTime = sharedPreferences.getInt("lastPauseTime", 0);
            lastAppPauseTime = sharedPreferences.getLong("lastAppPauseTime", 0);
            useFingerprint = sharedPreferences.getBoolean("useFingerprint", true);
            lastUpdateVersion = sharedPreferences.getString("lastUpdateVersion2", "3.5");
            allowScreenCapture = sharedPreferences.getBoolean("allowScreenCapture", false);
            lastLocalId = sharedPreferences.getInt("lastLocalId", -210000);
            pushString = sharedPreferences.getString("pushString2", TtmlNode.ANONYMOUS_REGION_ID);
            Object string = sharedPreferences.getString("pushAuthKey", null);
            if (!TextUtils.isEmpty(string)) {
                pushAuthKey = Base64.decode(string, 0);
            }
            if (passcodeHash.length() > 0 && lastPauseTime == 0) {
                lastPauseTime = (int) ((System.currentTimeMillis() / 1000) - 600);
            }
            String string2 = sharedPreferences.getString("passcodeSalt", TtmlNode.ANONYMOUS_REGION_ID);
            if (string2.length() > 0) {
                passcodeSalt = Base64.decode(string2, 0);
            } else {
                passcodeSalt = new byte[0];
            }
            sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            saveToGallery = sharedPreferences.getBoolean("save_gallery", false);
            autoplayGifs = sharedPreferences.getBoolean("autoplay_gif", true);
            raiseToSpeak = sharedPreferences.getBoolean("raise_to_speak", true);
            customTabs = sharedPreferences.getBoolean("custom_tabs", true);
            directShare = sharedPreferences.getBoolean("direct_share", true);
            shuffleMusic = sharedPreferences.getBoolean("shuffleMusic", false);
            playOrderReversed = sharedPreferences.getBoolean("playOrderReversed", false);
            inappCamera = sharedPreferences.getBoolean("inappCamera", true);
            roundCamera16to9 = true;
            groupPhotosEnabled = sharedPreferences.getBoolean("groupPhotosEnabled", true);
            repeatMode = sharedPreferences.getInt("repeatMode", 0);
            fontSize = sharedPreferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
            allowBigEmoji = sharedPreferences.getBoolean("allowBigEmoji", false);
            useSystemEmoji = sharedPreferences.getBoolean("useSystemEmoji", false);
            streamMedia = sharedPreferences.getBoolean("streamMedia", true);
            saveStreamMedia = sharedPreferences.getBoolean("saveStreamMedia", true);
            streamAllVideo = sharedPreferences.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
            suggestStickers = sharedPreferences.getInt("suggestStickers", 0);
            configLoaded = true;
        }
    }

    public static boolean checkPasscode(String str) {
        if (passcodeSalt.length == 0) {
            boolean equals = Utilities.MD5(str).equals(passcodeHash);
            if (equals) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    str = str.getBytes(C0542C.UTF8_NAME);
                    Object obj = new byte[(32 + str.length)];
                    System.arraycopy(passcodeSalt, 0, obj, 0, 16);
                    System.arraycopy(str, 0, obj, 16, str.length);
                    System.arraycopy(passcodeSalt, 0, obj, str.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
                    saveConfig();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            return equals;
        }
        try {
            str = str.getBytes(C0542C.UTF8_NAME);
            Object obj2 = new byte[(32 + str.length)];
            System.arraycopy(passcodeSalt, 0, obj2, 0, 16);
            System.arraycopy(str, 0, obj2, 16, str.length);
            System.arraycopy(passcodeSalt, 0, obj2, str.length + 16, 16);
            return passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(obj2, 0, obj2.length)));
        } catch (Throwable e2) {
            FileLog.m3e(e2);
            return false;
        }
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

    public static void setSuggestStickers(int i) {
        suggestStickers = i;
        i = MessagesController.getGlobalMainSettings().edit();
        i.putInt("suggestStickers", suggestStickers);
        i.commit();
    }

    public static void toggleShuffleMusic(int i) {
        if (i == 2) {
            shuffleMusic ^= 1;
        } else {
            playOrderReversed ^= 1;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        i = MessagesController.getGlobalMainSettings().edit();
        i.putBoolean("shuffleMusic", shuffleMusic);
        i.putBoolean("playOrderReversed", playOrderReversed);
        i.commit();
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

    public static void toggleStreamAllVideo() {
        streamAllVideo ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("streamAllVideo", streamAllVideo);
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
        edit.putBoolean("direct_share", inappCamera);
        edit.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("roundCamera16to9", roundCamera16to9);
        edit.commit();
    }

    public static void toggleGroupPhotosEnabled() {
        groupPhotosEnabled ^= 1;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("groupPhotosEnabled", groupPhotosEnabled);
        edit.commit();
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
                new File(file2, ".nomedia").createNewFile();
            }
            if (file3.isDirectory()) {
                new File(file3, ".nomedia").createNewFile();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }
}
