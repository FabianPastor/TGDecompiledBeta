package org.telegram.messenger;

import android.content.Context;
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

public class UserConfig extends BaseController {
    private static volatile UserConfig[] Instance = new UserConfig[3];
    public static final int MAX_ACCOUNT_COUNT = 3;
    public static final int i_dialogsLoadOffsetAccess_1 = 5;
    public static final int i_dialogsLoadOffsetAccess_2 = 6;
    public static final int i_dialogsLoadOffsetChannelId = 4;
    public static final int i_dialogsLoadOffsetChatId = 3;
    public static final int i_dialogsLoadOffsetDate = 1;
    public static final int i_dialogsLoadOffsetId = 0;
    public static final int i_dialogsLoadOffsetUserId = 2;
    public static int selectedAccount;
    public long autoDownloadConfigLoadTime;
    public int botRatingLoadTime;
    public int clientUserId;
    private boolean configLoaded;
    public boolean contactsReimported;
    public int contactsSavedCount;
    private User currentUser;
    public boolean draftsLoaded;
    public boolean hasSecureData;
    public boolean hasValidDialogLoadIds;
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
    public int ratingLoadTime;
    public boolean registeredForPush;
    public volatile byte[] savedPasswordHash;
    public volatile long savedPasswordTime;
    public volatile byte[] savedSaltedPassword;
    public boolean suggestContacts = true;
    private final Object sync = new Object();
    public boolean syncContacts = true;
    public TL_account_tmpPassword tmpPassword;
    public int tonBadPasscodeTries;
    public boolean tonCreationFinished;
    public String tonEncryptedData;
    public String tonKeyName;
    public long tonLastUptimeMillis;
    public long tonPasscodeRetryInMs;
    public byte[] tonPasscodeSalt;
    public int tonPasscodeType = -1;
    public String tonPublicKey;
    public TL_help_termsOfService unacceptedTermsOfService;
    public boolean unreadDialogsLoaded = true;
    public String walletBlockchainName;
    public String walletConfig;
    public String walletConfigFromUrl;
    public int walletConfigType;
    public String walletConfigUrl;

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
        for (int i2 = 0; i2 < 3; i2++) {
            if (AccountInstance.getInstance(i2).getUserConfig().isClientActivated()) {
                i++;
            }
        }
        return i;
    }

    public UserConfig(int i) {
        super(i);
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
                SerializedData serializedData;
                Editor edit = getPreferences().edit();
                if (this.currentAccount == 0) {
                    edit.putInt("selectedAccount", selectedAccount);
                }
                edit.putBoolean("registeredForPush", this.registeredForPush);
                edit.putInt("lastSendMessageId", this.lastSendMessageId);
                edit.putInt("contactsSavedCount", this.contactsSavedCount);
                edit.putInt("lastBroadcastId", this.lastBroadcastId);
                edit.putInt("lastContactsSyncTime", this.lastContactsSyncTime);
                edit.putInt("lastHintsSyncTime", this.lastHintsSyncTime);
                edit.putBoolean("draftsLoaded", this.draftsLoaded);
                edit.putBoolean("unreadDialogsLoaded", this.unreadDialogsLoaded);
                edit.putInt("ratingLoadTime", this.ratingLoadTime);
                edit.putInt("botRatingLoadTime", this.botRatingLoadTime);
                edit.putBoolean("contactsReimported", this.contactsReimported);
                edit.putInt("loginTime", this.loginTime);
                edit.putBoolean("syncContacts", this.syncContacts);
                edit.putBoolean("suggestContacts", this.suggestContacts);
                edit.putBoolean("hasSecureData", this.hasSecureData);
                edit.putBoolean("notificationsSettingsLoaded3", this.notificationsSettingsLoaded);
                edit.putBoolean("notificationsSignUpSettingsLoaded", this.notificationsSignUpSettingsLoaded);
                edit.putLong("autoDownloadConfigLoadTime", this.autoDownloadConfigLoadTime);
                edit.putBoolean("hasValidDialogLoadIds", this.hasValidDialogLoadIds);
                if (this.tonEncryptedData != null) {
                    edit.putString("tonEncryptedData", this.tonEncryptedData);
                    edit.putString("tonPublicKey", this.tonPublicKey);
                    edit.putString("tonKeyName", this.tonKeyName);
                    edit.putBoolean("tonCreationFinished", this.tonCreationFinished);
                    if (this.tonPasscodeSalt != null) {
                        edit.putInt("tonPasscodeType", this.tonPasscodeType);
                        edit.putString("tonPasscodeSalt", Base64.encodeToString(this.tonPasscodeSalt, 0));
                        edit.putLong("tonPasscodeRetryInMs", this.tonPasscodeRetryInMs);
                        edit.putLong("tonLastUptimeMillis", this.tonLastUptimeMillis);
                        edit.putInt("tonBadPasscodeTries", this.tonBadPasscodeTries);
                    }
                } else {
                    edit.remove("tonEncryptedData").remove("tonPublicKey").remove("tonKeyName").remove("tonPasscodeType").remove("tonPasscodeSalt").remove("tonPasscodeRetryInMs").remove("tonBadPasscodeTries").remove("tonLastUptimeMillis").remove("tonCreationFinished");
                }
                edit.putString("walletConfig", this.walletConfig);
                edit.putString("walletConfigUrl", this.walletConfigUrl);
                edit.putInt("walletConfigType", this.walletConfigType);
                edit.putString("walletBlockchainName", this.walletBlockchainName);
                edit.putString("walletConfigFromUrl", this.walletConfigFromUrl);
                edit.putInt("6migrateOffsetId", this.migrateOffsetId);
                if (this.migrateOffsetId != -1) {
                    edit.putInt("6migrateOffsetDate", this.migrateOffsetDate);
                    edit.putInt("6migrateOffsetUserId", this.migrateOffsetUserId);
                    edit.putInt("6migrateOffsetChatId", this.migrateOffsetChatId);
                    edit.putInt("6migrateOffsetChannelId", this.migrateOffsetChannelId);
                    edit.putLong("6migrateOffsetAccess", this.migrateOffsetAccess);
                }
                if (this.unacceptedTermsOfService != null) {
                    try {
                        serializedData = new SerializedData(this.unacceptedTermsOfService.getObjectSize());
                        this.unacceptedTermsOfService.serializeToStream(serializedData);
                        edit.putString("terms", Base64.encodeToString(serializedData.toByteArray(), 0));
                        serializedData.cleanup();
                    } catch (Exception unused) {
                    }
                } else {
                    edit.remove("terms");
                }
                if (this.currentAccount == 0) {
                    if (this.pendingAppUpdate != null) {
                        try {
                            serializedData = new SerializedData(this.pendingAppUpdate.getObjectSize());
                            this.pendingAppUpdate.serializeToStream(serializedData);
                            edit.putString("appUpdate", Base64.encodeToString(serializedData.toByteArray(), 0));
                            edit.putInt("appUpdateBuild", this.pendingAppUpdateBuildVersion);
                            edit.putLong("appUpdateTime", this.pendingAppUpdateInstallTime);
                            edit.putLong("appUpdateCheckTime", this.lastUpdateCheckTime);
                            serializedData.cleanup();
                        } catch (Exception unused2) {
                        }
                    } else {
                        edit.remove("appUpdate");
                    }
                }
                SharedConfig.saveConfig();
                if (this.tmpPassword != null) {
                    serializedData = new SerializedData();
                    this.tmpPassword.serializeToStream(serializedData);
                    edit.putString("tmpPassword", Base64.encodeToString(serializedData.toByteArray(), 0));
                    serializedData.cleanup();
                } else {
                    edit.remove("tmpPassword");
                }
                if (this.currentUser == null) {
                    edit.remove("user");
                } else if (z) {
                    SerializedData serializedData2 = new SerializedData();
                    this.currentUser.serializeToStream(serializedData2);
                    edit.putString("user", Base64.encodeToString(serializedData2.toByteArray(), 0));
                    serializedData2.cleanup();
                }
                edit.commit();
                if (file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                FileLog.e(e);
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

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0122 A:{SYNTHETIC, Splitter:B:19:0x0122} */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x016b A:{Catch:{ Exception -> 0x0184 }} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x018c A:{Catch:{ Exception -> 0x014d }} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0219 A:{Catch:{ Exception -> 0x014d }} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x024a A:{Catch:{ Exception -> 0x014d }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x026b A:{Catch:{ Exception -> 0x014d }} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0287 A:{Catch:{ Exception -> 0x014d }} */
    public void loadConfig() {
        /*
        r13 = this;
        r0 = r13.sync;
        monitor-enter(r0);
        r1 = r13.configLoaded;	 Catch:{ all -> 0x0291 }
        if (r1 == 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r0);	 Catch:{ all -> 0x0291 }
        return;
    L_0x0009:
        r1 = r13.getPreferences();	 Catch:{ all -> 0x0291 }
        r2 = r13.currentAccount;	 Catch:{ all -> 0x0291 }
        r3 = 0;
        if (r2 != 0) goto L_0x001a;
    L_0x0012:
        r2 = "selectedAccount";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        selectedAccount = r2;	 Catch:{ all -> 0x0291 }
    L_0x001a:
        r2 = "registeredForPush";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.registeredForPush = r2;	 Catch:{ all -> 0x0291 }
        r2 = "lastSendMessageId";
        r4 = -210000; // 0xfffffffffffccbb0 float:NaN double:NaN;
        r2 = r1.getInt(r2, r4);	 Catch:{ all -> 0x0291 }
        r13.lastSendMessageId = r2;	 Catch:{ all -> 0x0291 }
        r2 = "contactsSavedCount";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.contactsSavedCount = r2;	 Catch:{ all -> 0x0291 }
        r2 = "lastBroadcastId";
        r4 = -1;
        r2 = r1.getInt(r2, r4);	 Catch:{ all -> 0x0291 }
        r13.lastBroadcastId = r2;	 Catch:{ all -> 0x0291 }
        r2 = "lastContactsSyncTime";
        r5 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0291 }
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r7;
        r6 = (int) r5;	 Catch:{ all -> 0x0291 }
        r5 = 82800; // 0x14370 float:1.16028E-40 double:4.09086E-319;
        r6 = r6 - r5;
        r2 = r1.getInt(r2, r6);	 Catch:{ all -> 0x0291 }
        r13.lastContactsSyncTime = r2;	 Catch:{ all -> 0x0291 }
        r2 = "lastHintsSyncTime";
        r5 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0291 }
        r5 = r5 / r7;
        r6 = (int) r5;	 Catch:{ all -> 0x0291 }
        r5 = 90000; // 0x15var_ float:1.26117E-40 double:4.4466E-319;
        r6 = r6 - r5;
        r2 = r1.getInt(r2, r6);	 Catch:{ all -> 0x0291 }
        r13.lastHintsSyncTime = r2;	 Catch:{ all -> 0x0291 }
        r2 = "draftsLoaded";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.draftsLoaded = r2;	 Catch:{ all -> 0x0291 }
        r2 = "unreadDialogsLoaded";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.unreadDialogsLoaded = r2;	 Catch:{ all -> 0x0291 }
        r2 = "contactsReimported";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.contactsReimported = r2;	 Catch:{ all -> 0x0291 }
        r2 = "ratingLoadTime";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.ratingLoadTime = r2;	 Catch:{ all -> 0x0291 }
        r2 = "botRatingLoadTime";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.botRatingLoadTime = r2;	 Catch:{ all -> 0x0291 }
        r2 = "loginTime";
        r5 = r13.currentAccount;	 Catch:{ all -> 0x0291 }
        r2 = r1.getInt(r2, r5);	 Catch:{ all -> 0x0291 }
        r13.loginTime = r2;	 Catch:{ all -> 0x0291 }
        r2 = "syncContacts";
        r5 = 1;
        r2 = r1.getBoolean(r2, r5);	 Catch:{ all -> 0x0291 }
        r13.syncContacts = r2;	 Catch:{ all -> 0x0291 }
        r2 = "suggestContacts";
        r2 = r1.getBoolean(r2, r5);	 Catch:{ all -> 0x0291 }
        r13.suggestContacts = r2;	 Catch:{ all -> 0x0291 }
        r2 = "hasSecureData";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.hasSecureData = r2;	 Catch:{ all -> 0x0291 }
        r2 = "notificationsSettingsLoaded3";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.notificationsSettingsLoaded = r2;	 Catch:{ all -> 0x0291 }
        r2 = "notificationsSignUpSettingsLoaded";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.notificationsSignUpSettingsLoaded = r2;	 Catch:{ all -> 0x0291 }
        r2 = "autoDownloadConfigLoadTime";
        r6 = 0;
        r8 = r1.getLong(r2, r6);	 Catch:{ all -> 0x0291 }
        r13.autoDownloadConfigLoadTime = r8;	 Catch:{ all -> 0x0291 }
        r2 = "2dialogsLoadOffsetId";
        r2 = r1.contains(r2);	 Catch:{ all -> 0x0291 }
        if (r2 != 0) goto L_0x00dd;
    L_0x00d2:
        r2 = "hasValidDialogLoadIds";
        r2 = r1.getBoolean(r2, r3);	 Catch:{ all -> 0x0291 }
        if (r2 == 0) goto L_0x00db;
    L_0x00da:
        goto L_0x00dd;
    L_0x00db:
        r2 = 0;
        goto L_0x00de;
    L_0x00dd:
        r2 = 1;
    L_0x00de:
        r13.hasValidDialogLoadIds = r2;	 Catch:{ all -> 0x0291 }
        r2 = "tonEncryptedData";
        r8 = 0;
        r2 = r1.getString(r2, r8);	 Catch:{ all -> 0x0291 }
        r13.tonEncryptedData = r2;	 Catch:{ all -> 0x0291 }
        r2 = "tonPublicKey";
        r2 = r1.getString(r2, r8);	 Catch:{ all -> 0x0291 }
        r13.tonPublicKey = r2;	 Catch:{ all -> 0x0291 }
        r2 = "tonKeyName";
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0291 }
        r9.<init>();	 Catch:{ all -> 0x0291 }
        r10 = "walletKey";
        r9.append(r10);	 Catch:{ all -> 0x0291 }
        r10 = r13.currentAccount;	 Catch:{ all -> 0x0291 }
        r9.append(r10);	 Catch:{ all -> 0x0291 }
        r9 = r9.toString();	 Catch:{ all -> 0x0291 }
        r2 = r1.getString(r2, r9);	 Catch:{ all -> 0x0291 }
        r13.tonKeyName = r2;	 Catch:{ all -> 0x0291 }
        r2 = "tonCreationFinished";
        r2 = r1.getBoolean(r2, r5);	 Catch:{ all -> 0x0291 }
        r13.tonCreationFinished = r2;	 Catch:{ all -> 0x0291 }
        r2 = "tonPasscodeSalt";
        r2 = r1.getString(r2, r8);	 Catch:{ all -> 0x0291 }
        if (r2 == 0) goto L_0x0151;
    L_0x0122:
        r2 = android.util.Base64.decode(r2, r3);	 Catch:{ Exception -> 0x014d }
        r13.tonPasscodeSalt = r2;	 Catch:{ Exception -> 0x014d }
        r2 = "tonPasscodeType";
        r2 = r1.getInt(r2, r4);	 Catch:{ Exception -> 0x014d }
        r13.tonPasscodeType = r2;	 Catch:{ Exception -> 0x014d }
        r2 = "tonPasscodeRetryInMs";
        r9 = r1.getLong(r2, r6);	 Catch:{ Exception -> 0x014d }
        r13.tonPasscodeRetryInMs = r9;	 Catch:{ Exception -> 0x014d }
        r2 = "tonLastUptimeMillis";
        r9 = r1.getLong(r2, r6);	 Catch:{ Exception -> 0x014d }
        r13.tonLastUptimeMillis = r9;	 Catch:{ Exception -> 0x014d }
        r2 = "tonBadPasscodeTries";
        r2 = r1.getInt(r2, r3);	 Catch:{ Exception -> 0x014d }
        r13.tonBadPasscodeTries = r2;	 Catch:{ Exception -> 0x014d }
        goto L_0x0151;
    L_0x014d:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0291 }
    L_0x0151:
        r2 = "";
        r13.walletConfig = r2;	 Catch:{ all -> 0x0291 }
        r2 = "https://test.ton.org/config.json";
        r13.walletConfigUrl = r2;	 Catch:{ all -> 0x0291 }
        r13.walletConfigType = r5;	 Catch:{ all -> 0x0291 }
        r2 = "";
        r13.walletBlockchainName = r2;	 Catch:{ all -> 0x0291 }
        r2 = "";
        r13.walletConfigFromUrl = r2;	 Catch:{ all -> 0x0291 }
        r2 = "terms";
        r2 = r1.getString(r2, r8);	 Catch:{ Exception -> 0x0184 }
        if (r2 == 0) goto L_0x0188;
    L_0x016b:
        r2 = android.util.Base64.decode(r2, r3);	 Catch:{ Exception -> 0x0184 }
        if (r2 == 0) goto L_0x0188;
    L_0x0171:
        r9 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0184 }
        r9.<init>(r2);	 Catch:{ Exception -> 0x0184 }
        r2 = r9.readInt32(r3);	 Catch:{ Exception -> 0x0184 }
        r2 = org.telegram.tgnet.TLRPC.TL_help_termsOfService.TLdeserialize(r9, r2, r3);	 Catch:{ Exception -> 0x0184 }
        r13.unacceptedTermsOfService = r2;	 Catch:{ Exception -> 0x0184 }
        r9.cleanup();	 Catch:{ Exception -> 0x0184 }
        goto L_0x0188;
    L_0x0184:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0291 }
    L_0x0188:
        r2 = r13.currentAccount;	 Catch:{ all -> 0x0291 }
        if (r2 != 0) goto L_0x020d;
    L_0x018c:
        r2 = "appUpdateCheckTime";
        r9 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0291 }
        r9 = r1.getLong(r2, r9);	 Catch:{ all -> 0x0291 }
        r13.lastUpdateCheckTime = r9;	 Catch:{ all -> 0x0291 }
        r2 = "appUpdate";
        r2 = r1.getString(r2, r8);	 Catch:{ Exception -> 0x0209 }
        if (r2 == 0) goto L_0x01d0;
    L_0x01a0:
        r9 = "appUpdateBuild";
        r10 = org.telegram.messenger.BuildVars.BUILD_VERSION;	 Catch:{ Exception -> 0x0209 }
        r9 = r1.getInt(r9, r10);	 Catch:{ Exception -> 0x0209 }
        r13.pendingAppUpdateBuildVersion = r9;	 Catch:{ Exception -> 0x0209 }
        r9 = "appUpdateTime";
        r10 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0209 }
        r9 = r1.getLong(r9, r10);	 Catch:{ Exception -> 0x0209 }
        r13.pendingAppUpdateInstallTime = r9;	 Catch:{ Exception -> 0x0209 }
        r2 = android.util.Base64.decode(r2, r3);	 Catch:{ Exception -> 0x0209 }
        if (r2 == 0) goto L_0x01d0;
    L_0x01bc:
        r9 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0209 }
        r9.<init>(r2);	 Catch:{ Exception -> 0x0209 }
        r2 = r9.readInt32(r3);	 Catch:{ Exception -> 0x0209 }
        r2 = org.telegram.tgnet.TLRPC.help_AppUpdate.TLdeserialize(r9, r2, r3);	 Catch:{ Exception -> 0x0209 }
        r2 = (org.telegram.tgnet.TLRPC.TL_help_appUpdate) r2;	 Catch:{ Exception -> 0x0209 }
        r13.pendingAppUpdate = r2;	 Catch:{ Exception -> 0x0209 }
        r9.cleanup();	 Catch:{ Exception -> 0x0209 }
    L_0x01d0:
        r2 = r13.pendingAppUpdate;	 Catch:{ Exception -> 0x0209 }
        if (r2 == 0) goto L_0x020d;
    L_0x01d4:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x01ed }
        r2 = r2.getPackageManager();	 Catch:{ Exception -> 0x01ed }
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x01ed }
        r9 = r9.getPackageName();	 Catch:{ Exception -> 0x01ed }
        r2 = r2.getPackageInfo(r9, r3);	 Catch:{ Exception -> 0x01ed }
        r9 = r2.lastUpdateTime;	 Catch:{ Exception -> 0x01ed }
        r11 = r2.firstInstallTime;	 Catch:{ Exception -> 0x01ed }
        r9 = java.lang.Math.max(r9, r11);	 Catch:{ Exception -> 0x01ed }
        goto L_0x01f2;
    L_0x01ed:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x0209 }
        r9 = r6;
    L_0x01f2:
        r2 = r13.pendingAppUpdateBuildVersion;	 Catch:{ Exception -> 0x0209 }
        r11 = org.telegram.messenger.BuildVars.BUILD_VERSION;	 Catch:{ Exception -> 0x0209 }
        if (r2 != r11) goto L_0x01fe;
    L_0x01f8:
        r11 = r13.pendingAppUpdateInstallTime;	 Catch:{ Exception -> 0x0209 }
        r2 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r2 >= 0) goto L_0x020d;
    L_0x01fe:
        r13.pendingAppUpdate = r8;	 Catch:{ Exception -> 0x0209 }
        r2 = new org.telegram.messenger.-$$Lambda$UserConfig$HoXioNChxQlw-svExyMbii8fWo0;	 Catch:{ Exception -> 0x0209 }
        r2.<init>(r13);	 Catch:{ Exception -> 0x0209 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0209 }
        goto L_0x020d;
    L_0x0209:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0291 }
    L_0x020d:
        r2 = "6migrateOffsetId";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.migrateOffsetId = r2;	 Catch:{ all -> 0x0291 }
        r2 = r13.migrateOffsetId;	 Catch:{ all -> 0x0291 }
        if (r2 == r4) goto L_0x0241;
    L_0x0219:
        r2 = "6migrateOffsetDate";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.migrateOffsetDate = r2;	 Catch:{ all -> 0x0291 }
        r2 = "6migrateOffsetUserId";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.migrateOffsetUserId = r2;	 Catch:{ all -> 0x0291 }
        r2 = "6migrateOffsetChatId";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.migrateOffsetChatId = r2;	 Catch:{ all -> 0x0291 }
        r2 = "6migrateOffsetChannelId";
        r2 = r1.getInt(r2, r3);	 Catch:{ all -> 0x0291 }
        r13.migrateOffsetChannelId = r2;	 Catch:{ all -> 0x0291 }
        r2 = "6migrateOffsetAccess";
        r6 = r1.getLong(r2, r6);	 Catch:{ all -> 0x0291 }
        r13.migrateOffsetAccess = r6;	 Catch:{ all -> 0x0291 }
    L_0x0241:
        r2 = "tmpPassword";
        r2 = r1.getString(r2, r8);	 Catch:{ all -> 0x0291 }
        if (r2 == 0) goto L_0x0262;
    L_0x024a:
        r2 = android.util.Base64.decode(r2, r3);	 Catch:{ all -> 0x0291 }
        if (r2 == 0) goto L_0x0262;
    L_0x0250:
        r4 = new org.telegram.tgnet.SerializedData;	 Catch:{ all -> 0x0291 }
        r4.<init>(r2);	 Catch:{ all -> 0x0291 }
        r2 = r4.readInt32(r3);	 Catch:{ all -> 0x0291 }
        r2 = org.telegram.tgnet.TLRPC.TL_account_tmpPassword.TLdeserialize(r4, r2, r3);	 Catch:{ all -> 0x0291 }
        r13.tmpPassword = r2;	 Catch:{ all -> 0x0291 }
        r4.cleanup();	 Catch:{ all -> 0x0291 }
    L_0x0262:
        r2 = "user";
        r1 = r1.getString(r2, r8);	 Catch:{ all -> 0x0291 }
        if (r1 == 0) goto L_0x0283;
    L_0x026b:
        r1 = android.util.Base64.decode(r1, r3);	 Catch:{ all -> 0x0291 }
        if (r1 == 0) goto L_0x0283;
    L_0x0271:
        r2 = new org.telegram.tgnet.SerializedData;	 Catch:{ all -> 0x0291 }
        r2.<init>(r1);	 Catch:{ all -> 0x0291 }
        r1 = r2.readInt32(r3);	 Catch:{ all -> 0x0291 }
        r1 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r2, r1, r3);	 Catch:{ all -> 0x0291 }
        r13.currentUser = r1;	 Catch:{ all -> 0x0291 }
        r2.cleanup();	 Catch:{ all -> 0x0291 }
    L_0x0283:
        r1 = r13.currentUser;	 Catch:{ all -> 0x0291 }
        if (r1 == 0) goto L_0x028d;
    L_0x0287:
        r1 = r13.currentUser;	 Catch:{ all -> 0x0291 }
        r1 = r1.id;	 Catch:{ all -> 0x0291 }
        r13.clientUserId = r1;	 Catch:{ all -> 0x0291 }
    L_0x028d:
        r13.configLoaded = r5;	 Catch:{ all -> 0x0291 }
        monitor-exit(r0);	 Catch:{ all -> 0x0291 }
        return;
    L_0x0291:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0291 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserConfig.loadConfig():void");
    }

    public /* synthetic */ void lambda$loadConfig$0$UserConfig() {
        saveConfig(false);
    }

    public void savePassword(byte[] bArr, byte[] bArr2) {
        this.savedPasswordTime = SystemClock.elapsedRealtime();
        this.savedPasswordHash = bArr;
        this.savedSaltedPassword = bArr2;
    }

    public void checkSavedPassword() {
        if (!(this.savedSaltedPassword == null && this.savedPasswordHash == null) && Math.abs(SystemClock.elapsedRealtime() - this.savedPasswordTime) >= 1800000) {
            resetSavedPassword();
        }
    }

    public void resetSavedPassword() {
        int i;
        this.savedPasswordTime = 0;
        if (this.savedPasswordHash != null) {
            for (i = 0; i < this.savedPasswordHash.length; i++) {
                this.savedPasswordHash[i] = (byte) 0;
            }
            this.savedPasswordHash = null;
        }
        if (this.savedSaltedPassword != null) {
            for (i = 0; i < this.savedSaltedPassword.length; i++) {
                this.savedSaltedPassword[i] = (byte) 0;
            }
            this.savedSaltedPassword = null;
        }
    }

    private SharedPreferences getPreferences() {
        if (this.currentAccount == 0) {
            return ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
        }
        Context context = ApplicationLoader.applicationContext;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("userconfig");
        stringBuilder.append(this.currentAccount);
        return context.getSharedPreferences(stringBuilder.toString(), 0);
    }

    public void clearTonConfig() {
        this.tonEncryptedData = null;
        this.tonKeyName = null;
        this.tonPublicKey = null;
        this.tonPasscodeType = -1;
        this.tonPasscodeSalt = null;
        this.tonCreationFinished = false;
        this.tonPasscodeRetryInMs = 0;
        this.tonLastUptimeMillis = 0;
        this.tonBadPasscodeTries = 0;
    }

    public void clearConfig() {
        getPreferences().edit().clear().commit();
        clearTonConfig();
        this.currentUser = null;
        int i = 0;
        this.clientUserId = 0;
        this.registeredForPush = false;
        this.contactsSavedCount = 0;
        this.lastSendMessageId = -210000;
        this.lastBroadcastId = -1;
        this.notificationsSettingsLoaded = false;
        this.notificationsSignUpSettingsLoaded = false;
        this.migrateOffsetId = -1;
        this.migrateOffsetDate = -1;
        this.migrateOffsetUserId = -1;
        this.migrateOffsetChatId = -1;
        this.migrateOffsetChannelId = -1;
        this.migrateOffsetAccess = -1;
        this.ratingLoadTime = 0;
        this.botRatingLoadTime = 0;
        this.draftsLoaded = false;
        this.contactsReimported = true;
        this.syncContacts = true;
        this.suggestContacts = true;
        this.unreadDialogsLoaded = true;
        this.hasValidDialogLoadIds = true;
        this.unacceptedTermsOfService = null;
        this.pendingAppUpdate = null;
        this.hasSecureData = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        resetSavedPassword();
        for (int i2 = 0; i2 < 3; i2++) {
            if (AccountInstance.getInstance(i2).getUserConfig().isClientActivated()) {
                i = 1;
                break;
            }
        }
        if (i == 0) {
            SharedConfig.clearConfig();
        }
        saveConfig(true);
    }

    public boolean isPinnedDialogsLoaded(int i) {
        SharedPreferences preferences = getPreferences();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("2pinnedDialogsLoaded");
        stringBuilder.append(i);
        return preferences.getBoolean(stringBuilder.toString(), false);
    }

    public void setPinnedDialogsLoaded(int i, boolean z) {
        Editor edit = getPreferences().edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("2pinnedDialogsLoaded");
        stringBuilder.append(i);
        edit.putBoolean(stringBuilder.toString(), z).commit();
    }

    public int getTotalDialogsCount(int i) {
        SharedPreferences preferences = getPreferences();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("2totalDialogsLoadCount");
        stringBuilder.append(i == 0 ? "" : Integer.valueOf(i));
        return preferences.getInt(stringBuilder.toString(), 0);
    }

    public void setTotalDialogsCount(int i, int i2) {
        Editor edit = getPreferences().edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("2totalDialogsLoadCount");
        stringBuilder.append(i == 0 ? "" : Integer.valueOf(i));
        edit.putInt(stringBuilder.toString(), i2).commit();
    }

    public int[] getDialogLoadOffsets(int i) {
        SharedPreferences preferences = getPreferences();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("2dialogsLoadOffsetId");
        Object obj = "";
        stringBuilder.append(i == 0 ? obj : Integer.valueOf(i));
        int i2 = -1;
        int i3 = preferences.getInt(stringBuilder.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("2dialogsLoadOffsetDate");
        stringBuilder2.append(i == 0 ? obj : Integer.valueOf(i));
        int i4 = preferences.getInt(stringBuilder2.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("2dialogsLoadOffsetUserId");
        stringBuilder3.append(i == 0 ? obj : Integer.valueOf(i));
        int i5 = preferences.getInt(stringBuilder3.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("2dialogsLoadOffsetChatId");
        stringBuilder4.append(i == 0 ? obj : Integer.valueOf(i));
        int i6 = preferences.getInt(stringBuilder4.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append("2dialogsLoadOffsetChannelId");
        stringBuilder5.append(i == 0 ? obj : Integer.valueOf(i));
        String stringBuilder6 = stringBuilder5.toString();
        if (this.hasValidDialogLoadIds) {
            i2 = 0;
        }
        i2 = preferences.getInt(stringBuilder6, i2);
        stringBuilder5 = new StringBuilder();
        stringBuilder5.append("2dialogsLoadOffsetAccess");
        if (i != 0) {
            obj = Integer.valueOf(i);
        }
        stringBuilder5.append(obj);
        long j = preferences.getLong(stringBuilder5.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        return new int[]{i3, i4, i5, i6, i2, (int) j, (int) (j >> 32)};
    }

    public void setDialogsLoadOffset(int i, int i2, int i3, int i4, int i5, int i6, long j) {
        Editor edit = getPreferences().edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("2dialogsLoadOffsetId");
        Object obj = "";
        stringBuilder.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(stringBuilder.toString(), i2);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("2dialogsLoadOffsetDate");
        stringBuilder2.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(stringBuilder2.toString(), i3);
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("2dialogsLoadOffsetUserId");
        stringBuilder2.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(stringBuilder2.toString(), i4);
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("2dialogsLoadOffsetChatId");
        stringBuilder2.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(stringBuilder2.toString(), i5);
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("2dialogsLoadOffsetChannelId");
        stringBuilder2.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(stringBuilder2.toString(), i6);
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("2dialogsLoadOffsetAccess");
        if (i != 0) {
            obj = Integer.valueOf(i);
        }
        stringBuilder2.append(obj);
        edit.putLong(stringBuilder2.toString(), j);
        edit.putBoolean("hasValidDialogLoadIds", true);
        edit.commit();
    }
}
