package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Base64;
import java.io.File;
import org.telegram.messenger.exoplayer2.C;

public class SharedConfig {
    public static boolean allowBigEmoji;
    public static boolean allowScreenCapture;
    public static boolean appLocked;
    public static int autoLockIn = 3600;
    public static boolean autoplayGifs = true;
    private static boolean configLoaded;
    public static boolean customTabs = true;
    public static boolean directShare = true;
    public static int fontSize = AndroidUtilities.dp(16.0f);
    public static boolean groupPhotosEnabled = true;
    public static boolean inappCamera = true;
    public static boolean isWaitingForPasscodeEnter;
    public static long lastAppPauseTime;
    public static int lastLocalId = -210000;
    public static int lastPauseTime;
    public static String lastUpdateVersion;
    public static String passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
    public static byte[] passcodeSalt = new byte[0];
    public static int passcodeType;
    public static boolean playOrderReversed;
    public static String pushString = TtmlNode.ANONYMOUS_REGION_ID;
    public static boolean raiseToSpeak = true;
    public static int repeatMode;
    public static boolean roundCamera16to9 = false;
    public static boolean saveIncomingPhotos;
    public static boolean saveToGallery;
    public static boolean shuffleMusic;
    private static final Object sync = new Object();
    public static boolean useFingerprint = true;
    public static boolean useSystemEmoji;

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
                editor.putInt("lastLocalId", lastLocalId);
                editor.commit();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
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
            roundCamera16to9 = preferences.getBoolean("roundCamera16to9", false);
            groupPhotosEnabled = preferences.getBoolean("groupPhotosEnabled", true);
            repeatMode = preferences.getInt("repeatMode", 0);
            fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
            allowBigEmoji = preferences.getBoolean("allowBigEmoji", false);
            useSystemEmoji = preferences.getBoolean("useSystemEmoji", false);
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
                    passcodeBytes = passcode.getBytes(C.UTF8_NAME);
                    bytes = new byte[(passcodeBytes.length + 32)];
                    System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                    saveConfig();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } else {
            try {
                passcodeBytes = passcode.getBytes(C.UTF8_NAME);
                bytes = new byte[(passcodeBytes.length + 32)];
                System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                result = passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length)));
            } catch (Throwable e2) {
                FileLog.e(e2);
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
            FileLog.e(e);
        }
    }
}
