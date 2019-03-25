package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.util.Base64;
import java.io.File;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.TL_help_appUpdate;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.tgnet.TLRPC.User;

public class UserConfig {
    private static volatile UserConfig[] Instance = new UserConfig[3];
    public static final int MAX_ACCOUNT_COUNT = 3;
    public static int selectedAccount;
    public long autoDownloadConfigLoadTime;
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
    public boolean notificationsSignUpSettingsLoaded;
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

    public static UserConfig getInstance(int num) {
        Throwable th;
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
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
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
            SerializedData data;
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
            editor.putBoolean("notificationsSettingsLoaded3", this.notificationsSettingsLoaded);
            editor.putBoolean("notificationsSignUpSettingsLoaded", this.notificationsSignUpSettingsLoaded);
            editor.putLong("autoDownloadConfigLoadTime", this.autoDownloadConfigLoadTime);
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
                    data = new SerializedData(this.unacceptedTermsOfService.getObjectSize());
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
            } catch (Exception e3) {
                FileLog.e(e3);
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
            str = (this.currentUser == null || this.currentUser.phone == null) ? "" : this.currentUser.phone;
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

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    public void loadConfig() {
        /*
        r22 = this;
        r0 = r22;
        r0 = r0.sync;
        r16 = r0;
        monitor-enter(r16);
        r0 = r22;
        r13 = r0.configLoaded;	 Catch:{ all -> 0x03ab }
        if (r13 == 0) goto L_0x000f;
    L_0x000d:
        monitor-exit(r16);	 Catch:{ all -> 0x03ab }
    L_0x000e:
        return;
    L_0x000f:
        r0 = r22;
        r13 = r0.currentAccount;	 Catch:{ all -> 0x03ab }
        if (r13 != 0) goto L_0x03ae;
    L_0x0015:
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x03ab }
        r17 = "userconfing";
        r18 = 0;
        r0 = r17;
        r1 = r18;
        r9 = r13.getSharedPreferences(r0, r1);	 Catch:{ all -> 0x03ab }
        r13 = "selectedAccount";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        selectedAccount = r13;	 Catch:{ all -> 0x03ab }
    L_0x0031:
        r13 = "registeredForPush";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.registeredForPush = r13;	 Catch:{ all -> 0x03ab }
        r13 = "lastSendMessageId";
        r17 = -210000; // 0xfffffffffffccbb0 float:NaN double:NaN;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.lastSendMessageId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "contactsSavedCount";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.contactsSavedCount = r13;	 Catch:{ all -> 0x03ab }
        r13 = "lastBroadcastId";
        r17 = -1;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.lastBroadcastId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "blockedUsersLoaded";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.blockedUsersLoaded = r13;	 Catch:{ all -> 0x03ab }
        r13 = "lastContactsSyncTime";
        r18 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x03ab }
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r18 = r18 / r20;
        r0 = r18;
        r0 = (int) r0;	 Catch:{ all -> 0x03ab }
        r17 = r0;
        r18 = 82800; // 0x14370 float:1.16028E-40 double:4.09086E-319;
        r17 = r17 - r18;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.lastContactsSyncTime = r13;	 Catch:{ all -> 0x03ab }
        r13 = "lastHintsSyncTime";
        r18 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x03ab }
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r18 = r18 / r20;
        r0 = r18;
        r0 = (int) r0;	 Catch:{ all -> 0x03ab }
        r17 = r0;
        r18 = 90000; // 0x15var_ float:1.26117E-40 double:4.4466E-319;
        r17 = r17 - r18;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.lastHintsSyncTime = r13;	 Catch:{ all -> 0x03ab }
        r13 = "draftsLoaded";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.draftsLoaded = r13;	 Catch:{ all -> 0x03ab }
        r13 = "pinnedDialogsLoaded";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.pinnedDialogsLoaded = r13;	 Catch:{ all -> 0x03ab }
        r13 = "unreadDialogsLoaded";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.unreadDialogsLoaded = r13;	 Catch:{ all -> 0x03ab }
        r13 = "contactsReimported";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.contactsReimported = r13;	 Catch:{ all -> 0x03ab }
        r13 = "ratingLoadTime";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.ratingLoadTime = r13;	 Catch:{ all -> 0x03ab }
        r13 = "botRatingLoadTime";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.botRatingLoadTime = r13;	 Catch:{ all -> 0x03ab }
        r13 = "loginTime";
        r0 = r22;
        r0 = r0.currentAccount;	 Catch:{ all -> 0x03ab }
        r17 = r0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.loginTime = r13;	 Catch:{ all -> 0x03ab }
        r13 = "syncContacts";
        r17 = 1;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.syncContacts = r13;	 Catch:{ all -> 0x03ab }
        r13 = "suggestContacts";
        r17 = 1;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.suggestContacts = r13;	 Catch:{ all -> 0x03ab }
        r13 = "hasSecureData";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.hasSecureData = r13;	 Catch:{ all -> 0x03ab }
        r13 = "notificationsSettingsLoaded3";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.notificationsSettingsLoaded = r13;	 Catch:{ all -> 0x03ab }
        r13 = "notificationsSignUpSettingsLoaded";
        r17 = 0;
        r0 = r17;
        r13 = r9.getBoolean(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.notificationsSignUpSettingsLoaded = r13;	 Catch:{ all -> 0x03ab }
        r13 = "autoDownloadConfigLoadTime";
        r18 = 0;
        r0 = r18;
        r18 = r9.getLong(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r18;
        r2 = r22;
        r2.autoDownloadConfigLoadTime = r0;	 Catch:{ all -> 0x03ab }
        r13 = "terms";
        r17 = 0;
        r0 = r17;
        r11 = r9.getString(r13, r0);	 Catch:{ Exception -> 0x03d6 }
        if (r11 == 0) goto L_0x01b1;
    L_0x0191:
        r13 = 0;
        r4 = android.util.Base64.decode(r11, r13);	 Catch:{ Exception -> 0x03d6 }
        if (r4 == 0) goto L_0x01b1;
    L_0x0198:
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x03d6 }
        r6.<init>(r4);	 Catch:{ Exception -> 0x03d6 }
        r13 = 0;
        r13 = r6.readInt32(r13);	 Catch:{ Exception -> 0x03d6 }
        r17 = 0;
        r0 = r17;
        r13 = org.telegram.tgnet.TLRPC.TL_help_termsOfService.TLdeserialize(r6, r13, r0);	 Catch:{ Exception -> 0x03d6 }
        r0 = r22;
        r0.unacceptedTermsOfService = r13;	 Catch:{ Exception -> 0x03d6 }
        r6.cleanup();	 Catch:{ Exception -> 0x03d6 }
    L_0x01b1:
        r0 = r22;
        r13 = r0.currentAccount;	 Catch:{ all -> 0x03ab }
        if (r13 != 0) goto L_0x0268;
    L_0x01b7:
        r13 = "appUpdateCheckTime";
        r18 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x03ab }
        r0 = r18;
        r18 = r9.getLong(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r18;
        r2 = r22;
        r2.lastUpdateCheckTime = r0;	 Catch:{ all -> 0x03ab }
        r13 = "appUpdate";
        r17 = 0;
        r0 = r17;
        r12 = r9.getString(r13, r0);	 Catch:{ Exception -> 0x03e2 }
        if (r12 == 0) goto L_0x021b;
    L_0x01d7:
        r13 = "appUpdateBuild";
        r17 = org.telegram.messenger.BuildVars.BUILD_VERSION;	 Catch:{ Exception -> 0x03e2 }
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ Exception -> 0x03e2 }
        r0 = r22;
        r0.pendingAppUpdateBuildVersion = r13;	 Catch:{ Exception -> 0x03e2 }
        r13 = "appUpdateTime";
        r18 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x03e2 }
        r0 = r18;
        r18 = r9.getLong(r13, r0);	 Catch:{ Exception -> 0x03e2 }
        r0 = r18;
        r2 = r22;
        r2.pendingAppUpdateInstallTime = r0;	 Catch:{ Exception -> 0x03e2 }
        r13 = 0;
        r4 = android.util.Base64.decode(r12, r13);	 Catch:{ Exception -> 0x03e2 }
        if (r4 == 0) goto L_0x021b;
    L_0x0200:
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x03e2 }
        r6.<init>(r4);	 Catch:{ Exception -> 0x03e2 }
        r13 = 0;
        r13 = r6.readInt32(r13);	 Catch:{ Exception -> 0x03e2 }
        r17 = 0;
        r0 = r17;
        r13 = org.telegram.tgnet.TLRPC.help_AppUpdate.TLdeserialize(r6, r13, r0);	 Catch:{ Exception -> 0x03e2 }
        r13 = (org.telegram.tgnet.TLRPC.TL_help_appUpdate) r13;	 Catch:{ Exception -> 0x03e2 }
        r0 = r22;
        r0.pendingAppUpdate = r13;	 Catch:{ Exception -> 0x03e2 }
        r6.cleanup();	 Catch:{ Exception -> 0x03e2 }
    L_0x021b:
        r0 = r22;
        r13 = r0.pendingAppUpdate;	 Catch:{ Exception -> 0x03e2 }
        if (r13 == 0) goto L_0x0268;
    L_0x0221:
        r14 = 0;
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x03dc }
        r13 = r13.getPackageManager();	 Catch:{ Exception -> 0x03dc }
        r17 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x03dc }
        r17 = r17.getPackageName();	 Catch:{ Exception -> 0x03dc }
        r18 = 0;
        r0 = r17;
        r1 = r18;
        r8 = r13.getPackageInfo(r0, r1);	 Catch:{ Exception -> 0x03dc }
        r0 = r8.lastUpdateTime;	 Catch:{ Exception -> 0x03dc }
        r18 = r0;
        r0 = r8.firstInstallTime;	 Catch:{ Exception -> 0x03dc }
        r20 = r0;
        r14 = java.lang.Math.max(r18, r20);	 Catch:{ Exception -> 0x03dc }
    L_0x0245:
        r0 = r22;
        r13 = r0.pendingAppUpdateBuildVersion;	 Catch:{ Exception -> 0x03e2 }
        r17 = org.telegram.messenger.BuildVars.BUILD_VERSION;	 Catch:{ Exception -> 0x03e2 }
        r0 = r17;
        if (r13 != r0) goto L_0x0259;
    L_0x024f:
        r0 = r22;
        r0 = r0.pendingAppUpdateInstallTime;	 Catch:{ Exception -> 0x03e2 }
        r18 = r0;
        r13 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1));
        if (r13 >= 0) goto L_0x0268;
    L_0x0259:
        r13 = 0;
        r0 = r22;
        r0.pendingAppUpdate = r13;	 Catch:{ Exception -> 0x03e2 }
        r13 = new org.telegram.messenger.UserConfig$$Lambda$0;	 Catch:{ Exception -> 0x03e2 }
        r0 = r22;
        r13.<init>(r0);	 Catch:{ Exception -> 0x03e2 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);	 Catch:{ Exception -> 0x03e2 }
    L_0x0268:
        r13 = "3migrateOffsetId";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.migrateOffsetId = r13;	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r13 = r0.migrateOffsetId;	 Catch:{ all -> 0x03ab }
        r17 = -1;
        r0 = r17;
        if (r13 == r0) goto L_0x02ce;
    L_0x0281:
        r13 = "3migrateOffsetDate";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.migrateOffsetDate = r13;	 Catch:{ all -> 0x03ab }
        r13 = "3migrateOffsetUserId";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.migrateOffsetUserId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "3migrateOffsetChatId";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.migrateOffsetChatId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "3migrateOffsetChannelId";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.migrateOffsetChannelId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "3migrateOffsetAccess";
        r18 = 0;
        r0 = r18;
        r18 = r9.getLong(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r18;
        r2 = r22;
        r2.migrateOffsetAccess = r0;	 Catch:{ all -> 0x03ab }
    L_0x02ce:
        r13 = "2dialogsLoadOffsetId";
        r17 = -1;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.dialogsLoadOffsetId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "2totalDialogsLoadCount";
        r17 = 0;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.totalDialogsLoadCount = r13;	 Catch:{ all -> 0x03ab }
        r13 = "2dialogsLoadOffsetDate";
        r17 = -1;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.dialogsLoadOffsetDate = r13;	 Catch:{ all -> 0x03ab }
        r13 = "2dialogsLoadOffsetUserId";
        r17 = -1;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.dialogsLoadOffsetUserId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "2dialogsLoadOffsetChatId";
        r17 = -1;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.dialogsLoadOffsetChatId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "2dialogsLoadOffsetChannelId";
        r17 = -1;
        r0 = r17;
        r13 = r9.getInt(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.dialogsLoadOffsetChannelId = r13;	 Catch:{ all -> 0x03ab }
        r13 = "2dialogsLoadOffsetAccess";
        r18 = -1;
        r0 = r18;
        r18 = r9.getLong(r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r18;
        r2 = r22;
        r2.dialogsLoadOffsetAccess = r0;	 Catch:{ all -> 0x03ab }
        r13 = "tmpPassword";
        r17 = 0;
        r0 = r17;
        r10 = r9.getString(r13, r0);	 Catch:{ all -> 0x03ab }
        if (r10 == 0) goto L_0x0366;
    L_0x0346:
        r13 = 0;
        r5 = android.util.Base64.decode(r10, r13);	 Catch:{ all -> 0x03ab }
        if (r5 == 0) goto L_0x0366;
    L_0x034d:
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ all -> 0x03ab }
        r6.<init>(r5);	 Catch:{ all -> 0x03ab }
        r13 = 0;
        r13 = r6.readInt32(r13);	 Catch:{ all -> 0x03ab }
        r17 = 0;
        r0 = r17;
        r13 = org.telegram.tgnet.TLRPC.TL_account_tmpPassword.TLdeserialize(r6, r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.tmpPassword = r13;	 Catch:{ all -> 0x03ab }
        r6.cleanup();	 Catch:{ all -> 0x03ab }
    L_0x0366:
        r13 = "user";
        r17 = 0;
        r0 = r17;
        r10 = r9.getString(r13, r0);	 Catch:{ all -> 0x03ab }
        if (r10 == 0) goto L_0x0393;
    L_0x0373:
        r13 = 0;
        r5 = android.util.Base64.decode(r10, r13);	 Catch:{ all -> 0x03ab }
        if (r5 == 0) goto L_0x0393;
    L_0x037a:
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ all -> 0x03ab }
        r6.<init>(r5);	 Catch:{ all -> 0x03ab }
        r13 = 0;
        r13 = r6.readInt32(r13);	 Catch:{ all -> 0x03ab }
        r17 = 0;
        r0 = r17;
        r13 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r6, r13, r0);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.currentUser = r13;	 Catch:{ all -> 0x03ab }
        r6.cleanup();	 Catch:{ all -> 0x03ab }
    L_0x0393:
        r0 = r22;
        r13 = r0.currentUser;	 Catch:{ all -> 0x03ab }
        if (r13 == 0) goto L_0x03a3;
    L_0x0399:
        r0 = r22;
        r13 = r0.currentUser;	 Catch:{ all -> 0x03ab }
        r13 = r13.id;	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0.clientUserId = r13;	 Catch:{ all -> 0x03ab }
    L_0x03a3:
        r13 = 1;
        r0 = r22;
        r0.configLoaded = r13;	 Catch:{ all -> 0x03ab }
        monitor-exit(r16);	 Catch:{ all -> 0x03ab }
        goto L_0x000e;
    L_0x03ab:
        r13 = move-exception;
        monitor-exit(r16);	 Catch:{ all -> 0x03ab }
        throw r13;
    L_0x03ae:
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x03ab }
        r17 = new java.lang.StringBuilder;	 Catch:{ all -> 0x03ab }
        r17.<init>();	 Catch:{ all -> 0x03ab }
        r18 = "userconfig";
        r17 = r17.append(r18);	 Catch:{ all -> 0x03ab }
        r0 = r22;
        r0 = r0.currentAccount;	 Catch:{ all -> 0x03ab }
        r18 = r0;
        r17 = r17.append(r18);	 Catch:{ all -> 0x03ab }
        r17 = r17.toString();	 Catch:{ all -> 0x03ab }
        r18 = 0;
        r0 = r17;
        r1 = r18;
        r9 = r13.getSharedPreferences(r0, r1);	 Catch:{ all -> 0x03ab }
        goto L_0x0031;
    L_0x03d6:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x03ab }
        goto L_0x01b1;
    L_0x03dc:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ Exception -> 0x03e2 }
        goto L_0x0245;
    L_0x03e2:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x03ab }
        goto L_0x0268;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserConfig.loadConfig():void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadConfig$0$UserConfig() {
        saveConfig(false);
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
        this.notificationsSignUpSettingsLoaded = false;
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
