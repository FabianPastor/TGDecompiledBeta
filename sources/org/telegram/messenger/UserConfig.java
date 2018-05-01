package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import java.io.File;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.User;

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
    public int lastBroadcastId = -1;
    public int lastContactsSyncTime;
    public int lastHintsSyncTime;
    public int lastSendMessageId = -210000;
    public int loginTime;
    public long migrateOffsetAccess = -1;
    public int migrateOffsetChannelId = -1;
    public int migrateOffsetChatId = -1;
    public int migrateOffsetDate = -1;
    public int migrateOffsetId = -1;
    public int migrateOffsetUserId = -1;
    public boolean pinnedDialogsLoaded = true;
    public int ratingLoadTime;
    public boolean registeredForPush;
    private final Object sync = new Object();
    public boolean syncContacts = true;
    public TL_account_tmpPassword tmpPassword;
    public int totalDialogsLoadCount = 0;

    public static UserConfig getInstance(int i) {
        UserConfig userConfig = Instance[i];
        if (userConfig == null) {
            synchronized (UserConfig.class) {
                userConfig = Instance[i];
                if (userConfig == null) {
                    UserConfig[] userConfigArr = Instance;
                    UserConfig userConfig2 = new UserConfig(i);
                    userConfigArr[i] = userConfig2;
                    userConfig = userConfig2;
                }
            }
        }
        return userConfig;
    }

    public static int getActivatedAccountsCount() {
        int i = 0;
        int i2 = 0;
        while (i < 3) {
            if (getInstance(i).isClientActivated()) {
                i2++;
            }
            i++;
        }
        return i2;
    }

    public UserConfig(int i) {
        this.currentAccount = i;
    }

    public int getNewMessageId() {
        int i;
        synchronized (this.sync) {
            i = this.lastSendMessageId;
            this.lastSendMessageId--;
        }
        return i;
    }

    public void saveConfig(boolean z) {
        saveConfig(z, null);
    }

    public void saveConfig(boolean z, File file) {
        synchronized (this.sync) {
            try {
                SharedPreferences sharedPreferences;
                if (this.currentAccount == 0) {
                    sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                } else {
                    Context context = ApplicationLoader.applicationContext;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("userconfig");
                    stringBuilder.append(this.currentAccount);
                    sharedPreferences = context.getSharedPreferences(stringBuilder.toString(), 0);
                }
                Editor edit = sharedPreferences.edit();
                if (this.currentAccount == 0) {
                    edit.putInt("selectedAccount", selectedAccount);
                }
                edit.putBoolean("registeredForPush", this.registeredForPush);
                edit.putInt("lastSendMessageId", this.lastSendMessageId);
                edit.putInt("contactsSavedCount", this.contactsSavedCount);
                edit.putInt("lastBroadcastId", this.lastBroadcastId);
                edit.putBoolean("blockedUsersLoaded", this.blockedUsersLoaded);
                edit.putInt("lastContactsSyncTime", this.lastContactsSyncTime);
                edit.putInt("lastHintsSyncTime", this.lastHintsSyncTime);
                edit.putBoolean("draftsLoaded", this.draftsLoaded);
                edit.putBoolean("pinnedDialogsLoaded", this.pinnedDialogsLoaded);
                edit.putInt("ratingLoadTime", this.ratingLoadTime);
                edit.putInt("botRatingLoadTime", this.botRatingLoadTime);
                edit.putBoolean("contactsReimported", this.contactsReimported);
                edit.putInt("loginTime", this.loginTime);
                edit.putBoolean("syncContacts", this.syncContacts);
                edit.putInt("3migrateOffsetId", this.migrateOffsetId);
                if (this.migrateOffsetId != -1) {
                    edit.putInt("3migrateOffsetDate", this.migrateOffsetDate);
                    edit.putInt("3migrateOffsetUserId", this.migrateOffsetUserId);
                    edit.putInt("3migrateOffsetChatId", this.migrateOffsetChatId);
                    edit.putInt("3migrateOffsetChannelId", this.migrateOffsetChannelId);
                    edit.putLong("3migrateOffsetAccess", this.migrateOffsetAccess);
                }
                edit.putInt("2totalDialogsLoadCount", this.totalDialogsLoadCount);
                edit.putInt("2dialogsLoadOffsetId", this.dialogsLoadOffsetId);
                edit.putInt("2dialogsLoadOffsetDate", this.dialogsLoadOffsetDate);
                edit.putInt("2dialogsLoadOffsetUserId", this.dialogsLoadOffsetUserId);
                edit.putInt("2dialogsLoadOffsetChatId", this.dialogsLoadOffsetChatId);
                edit.putInt("2dialogsLoadOffsetChannelId", this.dialogsLoadOffsetChannelId);
                edit.putLong("2dialogsLoadOffsetAccess", this.dialogsLoadOffsetAccess);
                SharedConfig.saveConfig();
                if (this.tmpPassword != null) {
                    AbstractSerializedData serializedData = new SerializedData();
                    this.tmpPassword.serializeToStream(serializedData);
                    edit.putString("tmpPassword", Base64.encodeToString(serializedData.toByteArray(), 0));
                    serializedData.cleanup();
                } else {
                    edit.remove("tmpPassword");
                }
                if (this.currentUser == null) {
                    edit.remove("user");
                } else if (z) {
                    z = new SerializedData();
                    this.currentUser.serializeToStream(z);
                    edit.putString("user", Base64.encodeToString(z.toByteArray(), 0));
                    z.cleanup();
                }
                edit.commit();
                if (file != null) {
                    file.delete();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
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
            SharedPreferences sharedPreferences;
            if (this.currentAccount == 0) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                selectedAccount = sharedPreferences.getInt("selectedAccount", 0);
            } else {
                Context context = ApplicationLoader.applicationContext;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("userconfig");
                stringBuilder.append(this.currentAccount);
                sharedPreferences = context.getSharedPreferences(stringBuilder.toString(), 0);
            }
            this.registeredForPush = sharedPreferences.getBoolean("registeredForPush", false);
            this.lastSendMessageId = sharedPreferences.getInt("lastSendMessageId", -210000);
            this.contactsSavedCount = sharedPreferences.getInt("contactsSavedCount", 0);
            this.lastBroadcastId = sharedPreferences.getInt("lastBroadcastId", -1);
            this.blockedUsersLoaded = sharedPreferences.getBoolean("blockedUsersLoaded", false);
            this.lastContactsSyncTime = sharedPreferences.getInt("lastContactsSyncTime", ((int) (System.currentTimeMillis() / 1000)) - 82800);
            this.lastHintsSyncTime = sharedPreferences.getInt("lastHintsSyncTime", ((int) (System.currentTimeMillis() / 1000)) - 90000);
            this.draftsLoaded = sharedPreferences.getBoolean("draftsLoaded", false);
            this.pinnedDialogsLoaded = sharedPreferences.getBoolean("pinnedDialogsLoaded", false);
            this.contactsReimported = sharedPreferences.getBoolean("contactsReimported", false);
            this.ratingLoadTime = sharedPreferences.getInt("ratingLoadTime", 0);
            this.botRatingLoadTime = sharedPreferences.getInt("botRatingLoadTime", 0);
            this.loginTime = sharedPreferences.getInt("loginTime", this.currentAccount);
            this.syncContacts = sharedPreferences.getBoolean("syncContacts", this.syncContacts);
            this.migrateOffsetId = sharedPreferences.getInt("3migrateOffsetId", 0);
            if (this.migrateOffsetId != -1) {
                this.migrateOffsetDate = sharedPreferences.getInt("3migrateOffsetDate", 0);
                this.migrateOffsetUserId = sharedPreferences.getInt("3migrateOffsetUserId", 0);
                this.migrateOffsetChatId = sharedPreferences.getInt("3migrateOffsetChatId", 0);
                this.migrateOffsetChannelId = sharedPreferences.getInt("3migrateOffsetChannelId", 0);
                this.migrateOffsetAccess = sharedPreferences.getLong("3migrateOffsetAccess", 0);
            }
            this.dialogsLoadOffsetId = sharedPreferences.getInt("2dialogsLoadOffsetId", -1);
            this.totalDialogsLoadCount = sharedPreferences.getInt("2totalDialogsLoadCount", 0);
            this.dialogsLoadOffsetDate = sharedPreferences.getInt("2dialogsLoadOffsetDate", -1);
            this.dialogsLoadOffsetUserId = sharedPreferences.getInt("2dialogsLoadOffsetUserId", -1);
            this.dialogsLoadOffsetChatId = sharedPreferences.getInt("2dialogsLoadOffsetChatId", -1);
            this.dialogsLoadOffsetChannelId = sharedPreferences.getInt("2dialogsLoadOffsetChannelId", -1);
            this.dialogsLoadOffsetAccess = sharedPreferences.getLong("2dialogsLoadOffsetAccess", -1);
            String string = sharedPreferences.getString("tmpPassword", null);
            if (string != null) {
                byte[] decode = Base64.decode(string, 0);
                if (decode != null) {
                    AbstractSerializedData serializedData = new SerializedData(decode);
                    this.tmpPassword = TL_account_tmpPassword.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    serializedData.cleanup();
                }
            }
            String string2 = sharedPreferences.getString("user", null);
            if (string2 != null) {
                byte[] decode2 = Base64.decode(string2, 0);
                if (decode2 != null) {
                    AbstractSerializedData serializedData2 = new SerializedData(decode2);
                    this.currentUser = User.TLdeserialize(serializedData2, serializedData2.readInt32(false), false);
                    serializedData2.cleanup();
                }
            }
            if (this.currentUser != null) {
                this.clientUserId = this.currentUser.id;
            }
            this.configLoaded = true;
        }
    }

    public void clearConfig() {
        this.currentUser = null;
        boolean z = false;
        this.clientUserId = 0;
        this.registeredForPush = false;
        this.contactsSavedCount = 0;
        this.lastSendMessageId = -210000;
        this.lastBroadcastId = -1;
        this.blockedUsersLoaded = false;
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
        this.pinnedDialogsLoaded = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        for (int i = 0; i < 3; i++) {
            if (getInstance(i).isClientActivated()) {
                z = true;
                break;
            }
        }
        if (!z) {
            SharedConfig.clearConfig();
        }
        saveConfig(true);
    }
}
