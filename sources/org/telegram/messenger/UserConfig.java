package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.SystemClock;
import android.util.Base64;
import java.io.File;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.help_AppUpdate;

public class UserConfig {
    private static volatile UserConfig[] Instance = new UserConfig[3];
    public static final int MAX_ACCOUNT_COUNT = 3;
    public static int selectedAccount;
    public boolean blockedUsersLoaded;
    public int botRatingLoadTime;
    public int clientUserId;
    private boolean configLoaded;
    public boolean contactsReimported;
    public int contactsSavedCount;
    private int currentAccount;
    private User currentUser;
    public long dialogsLoadOffsetAccess = 0;
    public int dialogsLoadOffsetChannelId = 0;
    public int dialogsLoadOffsetChatId = 0;
    public int dialogsLoadOffsetDate = 0;
    public int dialogsLoadOffsetId = 0;
    public int dialogsLoadOffsetUserId = 0;
    public boolean draftsLoaded;
    public boolean hasSecureData;
    public int lastBroadcastId = -1;
    public int lastContactsSyncTime;
    public int lastHintsSyncTime;
    public int lastSendMessageId = -210000;
    public long lastUpdateCheckTime;
    public int loginTime;
    public long migrateOffsetAccess = -1;
    public int migrateOffsetChannelId = -1;
    public int migrateOffsetChatId = -1;
    public int migrateOffsetDate = -1;
    public int migrateOffsetId = -1;
    public int migrateOffsetUserId = -1;
    public boolean notificationsSettingsLoaded;
    public TL_help_appUpdate pendingAppUpdate;
    public int pendingAppUpdateBuildVersion;
    public long pendingAppUpdateInstallTime;
    public boolean pinnedDialogsLoaded = true;
    public int ratingLoadTime;
    public boolean registeredForPush;
    public volatile byte[] savedPasswordHash;
    public volatile long savedPasswordTime;
    public volatile byte[] savedSaltedPassword;
    public boolean suggestContacts = true;
    private final Object sync = new Object();
    public boolean syncContacts = true;
    public TL_account_tmpPassword tmpPassword;
    public int totalDialogsLoadCount = 0;
    public TL_help_termsOfService unacceptedTermsOfService;
    public boolean unreadDialogsLoaded = true;

    /* renamed from: org.telegram.messenger.UserConfig$1 */
    class C05871 implements Runnable {
        C05871() {
        }

        public void run() {
            UserConfig.this.saveConfig(false);
        }
    }

    public static UserConfig getInstance(int num) {
        UserConfig localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (UserConfig.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        UserConfig[] userConfigArr = Instance;
                        UserConfig localInstance2 = new UserConfig(num);
                        try {
                            userConfigArr[num] = localInstance2;
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

    public static int getActivatedAccountsCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            if (getInstance(a).isClientActivated()) {
                count++;
            }
        }
        return count;
    }

    public UserConfig(int instance) {
        this.currentAccount = instance;
    }

    public int getNewMessageId() {
        int id;
        synchronized (this.sync) {
            id = this.lastSendMessageId;
            this.lastSendMessageId--;
        }
        return id;
    }

    public void saveConfig(boolean withFile) {
        saveConfig(withFile, null);
    }

    public void saveConfig(boolean withFile, File oldFile) {
        synchronized (this.sync) {
            SharedPreferences preferences;
            if (this.currentAccount == 0) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
            } else {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfig" + this.currentAccount, 0);
            }
            Editor editor = preferences.edit();
            if (this.currentAccount == 0) {
                editor.putInt("selectedAccount", selectedAccount);
            }
            editor.putBoolean("registeredForPush", this.registeredForPush);
            editor.putInt("lastSendMessageId", this.lastSendMessageId);
            editor.putInt("contactsSavedCount", this.contactsSavedCount);
            editor.putInt("lastBroadcastId", this.lastBroadcastId);
            editor.putBoolean("blockedUsersLoaded", this.blockedUsersLoaded);
            editor.putInt("lastContactsSyncTime", this.lastContactsSyncTime);
            editor.putInt("lastHintsSyncTime", this.lastHintsSyncTime);
            editor.putBoolean("draftsLoaded", this.draftsLoaded);
            editor.putBoolean("pinnedDialogsLoaded", this.pinnedDialogsLoaded);
            editor.putBoolean("unreadDialogsLoaded", this.unreadDialogsLoaded);
            editor.putInt("ratingLoadTime", this.ratingLoadTime);
            editor.putInt("botRatingLoadTime", this.botRatingLoadTime);
            editor.putBoolean("contactsReimported", this.contactsReimported);
            editor.putInt("loginTime", this.loginTime);
            editor.putBoolean("syncContacts", this.syncContacts);
            editor.putBoolean("suggestContacts", this.suggestContacts);
            editor.putBoolean("hasSecureData", this.hasSecureData);
            editor.putBoolean("notificationsSettingsLoaded", this.notificationsSettingsLoaded);
            editor.putInt("3migrateOffsetId", this.migrateOffsetId);
            if (this.migrateOffsetId != -1) {
                editor.putInt("3migrateOffsetDate", this.migrateOffsetDate);
                editor.putInt("3migrateOffsetUserId", this.migrateOffsetUserId);
                editor.putInt("3migrateOffsetChatId", this.migrateOffsetChatId);
                editor.putInt("3migrateOffsetChannelId", this.migrateOffsetChannelId);
                editor.putLong("3migrateOffsetAccess", this.migrateOffsetAccess);
            }
            if (this.unacceptedTermsOfService != null) {
                try {
                    SerializedData data = new SerializedData(this.unacceptedTermsOfService.getObjectSize());
                    this.unacceptedTermsOfService.serializeToStream(data);
                    editor.putString("terms", Base64.encodeToString(data.toByteArray(), 0));
                    data.cleanup();
                } catch (Exception e) {
                }
            } else {
                editor.remove("terms");
            }
            try {
                if (this.currentAccount == 0) {
                    if (this.pendingAppUpdate != null) {
                        try {
                            data = new SerializedData(this.pendingAppUpdate.getObjectSize());
                            this.pendingAppUpdate.serializeToStream(data);
                            editor.putString("appUpdate", Base64.encodeToString(data.toByteArray(), 0));
                            editor.putInt("appUpdateBuild", this.pendingAppUpdateBuildVersion);
                            editor.putLong("appUpdateTime", this.pendingAppUpdateInstallTime);
                            editor.putLong("appUpdateCheckTime", this.lastUpdateCheckTime);
                            data.cleanup();
                        } catch (Exception e2) {
                        }
                    } else {
                        editor.remove("appUpdate");
                    }
                }
                editor.putInt("2totalDialogsLoadCount", this.totalDialogsLoadCount);
                editor.putInt("2dialogsLoadOffsetId", this.dialogsLoadOffsetId);
                editor.putInt("2dialogsLoadOffsetDate", this.dialogsLoadOffsetDate);
                editor.putInt("2dialogsLoadOffsetUserId", this.dialogsLoadOffsetUserId);
                editor.putInt("2dialogsLoadOffsetChatId", this.dialogsLoadOffsetChatId);
                editor.putInt("2dialogsLoadOffsetChannelId", this.dialogsLoadOffsetChannelId);
                editor.putLong("2dialogsLoadOffsetAccess", this.dialogsLoadOffsetAccess);
                SharedConfig.saveConfig();
                if (this.tmpPassword != null) {
                    data = new SerializedData();
                    this.tmpPassword.serializeToStream(data);
                    editor.putString("tmpPassword", Base64.encodeToString(data.toByteArray(), 0));
                    data.cleanup();
                } else {
                    editor.remove("tmpPassword");
                }
                if (this.currentUser == null) {
                    editor.remove("user");
                } else if (withFile) {
                    data = new SerializedData();
                    this.currentUser.serializeToStream(data);
                    editor.putString("user", Base64.encodeToString(data.toByteArray(), 0));
                    data.cleanup();
                }
                editor.commit();
                if (oldFile != null) {
                    oldFile.delete();
                }
            } catch (Throwable e3) {
                FileLog.m3e(e3);
            }
        }
    }

    public boolean isClientActivated() {
        boolean z;
        synchronized (this.sync) {
            z = this.currentUser != null;
        }
        return z;
    }

    public int getClientUserId() {
        int i;
        synchronized (this.sync) {
            i = this.currentUser != null ? this.currentUser.id : 0;
        }
        return i;
    }

    public String getClientPhone() {
        String str;
        synchronized (this.sync) {
            str = (this.currentUser == null || this.currentUser.phone == null) ? TtmlNode.ANONYMOUS_REGION_ID : this.currentUser.phone;
        }
        return str;
    }

    public User getCurrentUser() {
        User user;
        synchronized (this.sync) {
            user = this.currentUser;
        }
        return user;
    }

    public void setCurrentUser(User user) {
        synchronized (this.sync) {
            this.currentUser = user;
            this.clientUserId = user.id;
        }
    }

    public void loadConfig() {
        synchronized (this.sync) {
            if (this.configLoaded) {
                return;
            }
            SharedPreferences preferences;
            byte[] arr;
            byte[] bytes;
            if (this.currentAccount == 0) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                selectedAccount = preferences.getInt("selectedAccount", 0);
            } else {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfig" + this.currentAccount, 0);
            }
            this.registeredForPush = preferences.getBoolean("registeredForPush", false);
            this.lastSendMessageId = preferences.getInt("lastSendMessageId", -210000);
            this.contactsSavedCount = preferences.getInt("contactsSavedCount", 0);
            this.lastBroadcastId = preferences.getInt("lastBroadcastId", -1);
            this.blockedUsersLoaded = preferences.getBoolean("blockedUsersLoaded", false);
            this.lastContactsSyncTime = preferences.getInt("lastContactsSyncTime", ((int) (System.currentTimeMillis() / 1000)) - 82800);
            this.lastHintsSyncTime = preferences.getInt("lastHintsSyncTime", ((int) (System.currentTimeMillis() / 1000)) - 90000);
            this.draftsLoaded = preferences.getBoolean("draftsLoaded", false);
            this.pinnedDialogsLoaded = preferences.getBoolean("pinnedDialogsLoaded", false);
            this.unreadDialogsLoaded = preferences.getBoolean("unreadDialogsLoaded", false);
            this.contactsReimported = preferences.getBoolean("contactsReimported", false);
            this.ratingLoadTime = preferences.getInt("ratingLoadTime", 0);
            this.botRatingLoadTime = preferences.getInt("botRatingLoadTime", 0);
            this.loginTime = preferences.getInt("loginTime", this.currentAccount);
            this.syncContacts = preferences.getBoolean("syncContacts", true);
            this.suggestContacts = preferences.getBoolean("suggestContacts", true);
            this.hasSecureData = preferences.getBoolean("hasSecureData", false);
            this.notificationsSettingsLoaded = preferences.getBoolean("notificationsSettingsLoaded", false);
            try {
                String terms = preferences.getString("terms", null);
                if (terms != null) {
                    arr = Base64.decode(terms, 0);
                    if (arr != null) {
                        SerializedData data = new SerializedData(arr);
                        this.unacceptedTermsOfService = TL_help_termsOfService.TLdeserialize(data, data.readInt32(false), false);
                        data.cleanup();
                    }
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (this.currentAccount == 0) {
                this.lastUpdateCheckTime = preferences.getLong("appUpdateCheckTime", System.currentTimeMillis());
                try {
                    String update = preferences.getString("appUpdate", null);
                    if (update != null) {
                        this.pendingAppUpdateBuildVersion = preferences.getInt("appUpdateBuild", BuildVars.BUILD_VERSION);
                        this.pendingAppUpdateInstallTime = preferences.getLong("appUpdateTime", System.currentTimeMillis());
                        arr = Base64.decode(update, 0);
                        if (arr != null) {
                            data = new SerializedData(arr);
                            this.pendingAppUpdate = (TL_help_appUpdate) help_AppUpdate.TLdeserialize(data, data.readInt32(false), false);
                            data.cleanup();
                        }
                    }
                    if (this.pendingAppUpdate != null) {
                        long updateTime = 0;
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            updateTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                        if (this.pendingAppUpdateBuildVersion != BuildVars.BUILD_VERSION || this.pendingAppUpdateInstallTime < updateTime) {
                            this.pendingAppUpdate = null;
                            AndroidUtilities.runOnUIThread(new C05871());
                        }
                    }
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
            this.migrateOffsetId = preferences.getInt("3migrateOffsetId", 0);
            if (this.migrateOffsetId != -1) {
                this.migrateOffsetDate = preferences.getInt("3migrateOffsetDate", 0);
                this.migrateOffsetUserId = preferences.getInt("3migrateOffsetUserId", 0);
                this.migrateOffsetChatId = preferences.getInt("3migrateOffsetChatId", 0);
                this.migrateOffsetChannelId = preferences.getInt("3migrateOffsetChannelId", 0);
                this.migrateOffsetAccess = preferences.getLong("3migrateOffsetAccess", 0);
            }
            this.dialogsLoadOffsetId = preferences.getInt("2dialogsLoadOffsetId", -1);
            this.totalDialogsLoadCount = preferences.getInt("2totalDialogsLoadCount", 0);
            this.dialogsLoadOffsetDate = preferences.getInt("2dialogsLoadOffsetDate", -1);
            this.dialogsLoadOffsetUserId = preferences.getInt("2dialogsLoadOffsetUserId", -1);
            this.dialogsLoadOffsetChatId = preferences.getInt("2dialogsLoadOffsetChatId", -1);
            this.dialogsLoadOffsetChannelId = preferences.getInt("2dialogsLoadOffsetChannelId", -1);
            this.dialogsLoadOffsetAccess = preferences.getLong("2dialogsLoadOffsetAccess", -1);
            String string = preferences.getString("tmpPassword", null);
            if (string != null) {
                bytes = Base64.decode(string, 0);
                if (bytes != null) {
                    data = new SerializedData(bytes);
                    this.tmpPassword = TL_account_tmpPassword.TLdeserialize(data, data.readInt32(false), false);
                    data.cleanup();
                }
            }
            string = preferences.getString("user", null);
            if (string != null) {
                bytes = Base64.decode(string, 0);
                if (bytes != null) {
                    data = new SerializedData(bytes);
                    this.currentUser = User.TLdeserialize(data, data.readInt32(false), false);
                    data.cleanup();
                }
            }
            if (this.currentUser != null) {
                this.clientUserId = this.currentUser.id;
            }
            this.configLoaded = true;
        }
    }

    public void savePassword(byte[] hash, byte[] salted) {
        this.savedPasswordTime = SystemClock.elapsedRealtime();
        this.savedPasswordHash = hash;
        this.savedSaltedPassword = salted;
    }

    public void checkSavedPassword() {
        if (!(this.savedSaltedPassword == null && this.savedPasswordHash == null) && Math.abs(SystemClock.elapsedRealtime() - this.savedPasswordTime) >= 1800000) {
            resetSavedPassword();
        }
    }

    public void resetSavedPassword() {
        int a;
        this.savedPasswordTime = 0;
        if (this.savedPasswordHash != null) {
            for (a = 0; a < this.savedPasswordHash.length; a++) {
                this.savedPasswordHash[a] = (byte) 0;
            }
            this.savedPasswordHash = null;
        }
        if (this.savedSaltedPassword != null) {
            for (a = 0; a < this.savedSaltedPassword.length; a++) {
                this.savedSaltedPassword[a] = (byte) 0;
            }
            this.savedSaltedPassword = null;
        }
    }

    public void clearConfig() {
        this.currentUser = null;
        this.clientUserId = 0;
        this.registeredForPush = false;
        this.contactsSavedCount = 0;
        this.lastSendMessageId = -210000;
        this.lastBroadcastId = -1;
        this.blockedUsersLoaded = false;
        this.notificationsSettingsLoaded = false;
        this.migrateOffsetId = -1;
        this.migrateOffsetDate = -1;
        this.migrateOffsetUserId = -1;
        this.migrateOffsetChatId = -1;
        this.migrateOffsetChannelId = -1;
        this.migrateOffsetAccess = -1;
        this.dialogsLoadOffsetId = 0;
        this.totalDialogsLoadCount = 0;
        this.dialogsLoadOffsetDate = 0;
        this.dialogsLoadOffsetUserId = 0;
        this.dialogsLoadOffsetChatId = 0;
        this.dialogsLoadOffsetChannelId = 0;
        this.dialogsLoadOffsetAccess = 0;
        this.ratingLoadTime = 0;
        this.botRatingLoadTime = 0;
        this.draftsLoaded = true;
        this.contactsReimported = true;
        this.syncContacts = true;
        this.suggestContacts = true;
        this.pinnedDialogsLoaded = false;
        this.unreadDialogsLoaded = true;
        this.unacceptedTermsOfService = null;
        this.pendingAppUpdate = null;
        this.hasSecureData = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        resetSavedPassword();
        boolean hasActivated = false;
        for (int a = 0; a < 3; a++) {
            if (getInstance(a).isClientActivated()) {
                hasActivated = true;
                break;
            }
        }
        if (!hasActivated) {
            SharedConfig.clearConfig();
        }
        saveConfig(true);
    }
}
