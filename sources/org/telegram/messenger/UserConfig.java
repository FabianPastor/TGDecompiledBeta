package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Base64;
import java.io.File;
import java.util.Arrays;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC$TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC$TL_help_appUpdate;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.tgnet.TLRPC$User;

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
    private TLRPC$User currentUser;
    public boolean draftsLoaded;
    public boolean filtersLoaded;
    public boolean hasSecureData;
    public boolean hasValidDialogLoadIds;
    public int lastBroadcastId = -1;
    public int lastContactsSyncTime;
    public int lastHintsSyncTime;
    public int lastMyLocationShareTime;
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
    public TLRPC$TL_help_appUpdate pendingAppUpdate;
    public int pendingAppUpdateBuildVersion;
    public long pendingAppUpdateInstallTime;
    public int ratingLoadTime;
    public boolean registeredForPush;
    public volatile byte[] savedPasswordHash;
    public volatile long savedPasswordTime;
    public volatile byte[] savedSaltedPassword;
    public int sharingMyLocationUntil;
    public boolean suggestContacts = true;
    private final Object sync = new Object();
    public boolean syncContacts = true;
    public TLRPC$TL_account_tmpPassword tmpPassword;
    public int tonBadPasscodeTries;
    public boolean tonCreationFinished;
    public String tonEncryptedData;
    public String tonKeyName;
    public long tonLastUptimeMillis;
    public long tonPasscodeRetryInMs;
    public byte[] tonPasscodeSalt;
    public int tonPasscodeType = -1;
    public String tonPublicKey;
    public TLRPC$TL_help_termsOfService unacceptedTermsOfService;
    public boolean unreadDialogsLoaded = true;

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
            this.lastSendMessageId = i - 1;
        }
        return i;
    }

    public void saveConfig(boolean z) {
        saveConfig(z, (File) null);
    }

    public void saveConfig(boolean z, File file) {
        synchronized (this.sync) {
            try {
                SharedPreferences.Editor edit = getPreferences().edit();
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
                edit.putInt("sharingMyLocationUntil", this.sharingMyLocationUntil);
                edit.putInt("lastMyLocationShareTime", this.lastMyLocationShareTime);
                edit.putBoolean("filtersLoaded", this.filtersLoaded);
                String str = this.tonEncryptedData;
                if (str != null) {
                    edit.putString("tonEncryptedData", str);
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
                edit.putInt("6migrateOffsetId", this.migrateOffsetId);
                if (this.migrateOffsetId != -1) {
                    edit.putInt("6migrateOffsetDate", this.migrateOffsetDate);
                    edit.putInt("6migrateOffsetUserId", this.migrateOffsetUserId);
                    edit.putInt("6migrateOffsetChatId", this.migrateOffsetChatId);
                    edit.putInt("6migrateOffsetChannelId", this.migrateOffsetChannelId);
                    edit.putLong("6migrateOffsetAccess", this.migrateOffsetAccess);
                }
                TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService = this.unacceptedTermsOfService;
                if (tLRPC$TL_help_termsOfService != null) {
                    try {
                        SerializedData serializedData = new SerializedData(tLRPC$TL_help_termsOfService.getObjectSize());
                        this.unacceptedTermsOfService.serializeToStream(serializedData);
                        edit.putString("terms", Base64.encodeToString(serializedData.toByteArray(), 0));
                        serializedData.cleanup();
                    } catch (Exception unused) {
                    }
                } else {
                    edit.remove("terms");
                }
                if (this.currentAccount == 0) {
                    TLRPC$TL_help_appUpdate tLRPC$TL_help_appUpdate = this.pendingAppUpdate;
                    if (tLRPC$TL_help_appUpdate != null) {
                        try {
                            SerializedData serializedData2 = new SerializedData(tLRPC$TL_help_appUpdate.getObjectSize());
                            this.pendingAppUpdate.serializeToStream(serializedData2);
                            edit.putString("appUpdate", Base64.encodeToString(serializedData2.toByteArray(), 0));
                            edit.putInt("appUpdateBuild", this.pendingAppUpdateBuildVersion);
                            edit.putLong("appUpdateTime", this.pendingAppUpdateInstallTime);
                            edit.putLong("appUpdateCheckTime", this.lastUpdateCheckTime);
                            serializedData2.cleanup();
                        } catch (Exception unused2) {
                        }
                    } else {
                        edit.remove("appUpdate");
                    }
                }
                SharedConfig.saveConfig();
                if (this.tmpPassword != null) {
                    SerializedData serializedData3 = new SerializedData();
                    this.tmpPassword.serializeToStream(serializedData3);
                    edit.putString("tmpPassword", Base64.encodeToString(serializedData3.toByteArray(), 0));
                    serializedData3.cleanup();
                } else {
                    edit.remove("tmpPassword");
                }
                if (this.currentUser == null) {
                    edit.remove("user");
                } else if (z) {
                    SerializedData serializedData4 = new SerializedData();
                    this.currentUser.serializeToStream(serializedData4);
                    edit.putString("user", Base64.encodeToString(serializedData4.toByteArray(), 0));
                    serializedData4.cleanup();
                }
                edit.commit();
                if (file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
            TLRPC$User tLRPC$User = this.currentUser;
            i = tLRPC$User != null ? tLRPC$User.id : 0;
        }
        return i;
    }

    public String getClientPhone() {
        String str;
        synchronized (this.sync) {
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User == null || (str = tLRPC$User.phone) == null) {
                str = "";
            }
        }
        return str;
    }

    public TLRPC$User getCurrentUser() {
        TLRPC$User tLRPC$User;
        synchronized (this.sync) {
            tLRPC$User = this.currentUser;
        }
        return tLRPC$User;
    }

    public void setCurrentUser(TLRPC$User tLRPC$User) {
        synchronized (this.sync) {
            this.currentUser = tLRPC$User;
            this.clientUserId = tLRPC$User.id;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0134 A[SYNTHETIC, Splitter:B:19:0x0134] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0188 A[Catch:{ Exception -> 0x015b }] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0213 A[Catch:{ Exception -> 0x015b }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x027f A[Catch:{ Exception -> 0x015b }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadConfig() {
        /*
            r13 = this;
            java.lang.Object r0 = r13.sync
            monitor-enter(r0)
            boolean r1 = r13.configLoaded     // Catch:{ all -> 0x0287 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0287 }
            return
        L_0x0009:
            android.content.SharedPreferences r1 = r13.getPreferences()     // Catch:{ all -> 0x0287 }
            int r2 = r13.currentAccount     // Catch:{ all -> 0x0287 }
            r3 = 0
            if (r2 != 0) goto L_0x001a
            java.lang.String r2 = "selectedAccount"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            selectedAccount = r2     // Catch:{ all -> 0x0287 }
        L_0x001a:
            java.lang.String r2 = "registeredForPush"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.registeredForPush = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "lastSendMessageId"
            r4 = -210000(0xfffffffffffccbb0, float:NaN)
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x0287 }
            r13.lastSendMessageId = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "contactsSavedCount"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.contactsSavedCount = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "lastBroadcastId"
            r4 = -1
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x0287 }
            r13.lastBroadcastId = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "lastContactsSyncTime"
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0287 }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r7
            int r6 = (int) r5     // Catch:{ all -> 0x0287 }
            r5 = 82800(0x14370, float:1.16028E-40)
            int r6 = r6 - r5
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x0287 }
            r13.lastContactsSyncTime = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "lastHintsSyncTime"
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0287 }
            long r5 = r5 / r7
            int r6 = (int) r5     // Catch:{ all -> 0x0287 }
            r5 = 90000(0x15var_, float:1.26117E-40)
            int r6 = r6 - r5
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x0287 }
            r13.lastHintsSyncTime = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "draftsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.draftsLoaded = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "unreadDialogsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.unreadDialogsLoaded = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "contactsReimported"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.contactsReimported = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "ratingLoadTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.ratingLoadTime = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "botRatingLoadTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.botRatingLoadTime = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "loginTime"
            int r5 = r13.currentAccount     // Catch:{ all -> 0x0287 }
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x0287 }
            r13.loginTime = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "syncContacts"
            r5 = 1
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x0287 }
            r13.syncContacts = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "suggestContacts"
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x0287 }
            r13.suggestContacts = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "hasSecureData"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.hasSecureData = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "notificationsSettingsLoaded3"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.notificationsSettingsLoaded = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "notificationsSignUpSettingsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.notificationsSignUpSettingsLoaded = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "autoDownloadConfigLoadTime"
            r6 = 0
            long r8 = r1.getLong(r2, r6)     // Catch:{ all -> 0x0287 }
            r13.autoDownloadConfigLoadTime = r8     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "2dialogsLoadOffsetId"
            boolean r2 = r1.contains(r2)     // Catch:{ all -> 0x0287 }
            if (r2 != 0) goto L_0x00dc
            java.lang.String r2 = "hasValidDialogLoadIds"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            if (r2 == 0) goto L_0x00da
            goto L_0x00dc
        L_0x00da:
            r2 = 0
            goto L_0x00dd
        L_0x00dc:
            r2 = 1
        L_0x00dd:
            r13.hasValidDialogLoadIds = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "tonEncryptedData"
            r8 = 0
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x0287 }
            r13.tonEncryptedData = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "tonPublicKey"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x0287 }
            r13.tonPublicKey = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "tonKeyName"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0287 }
            r9.<init>()     // Catch:{ all -> 0x0287 }
            java.lang.String r10 = "walletKey"
            r9.append(r10)     // Catch:{ all -> 0x0287 }
            int r10 = r13.currentAccount     // Catch:{ all -> 0x0287 }
            r9.append(r10)     // Catch:{ all -> 0x0287 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = r1.getString(r2, r9)     // Catch:{ all -> 0x0287 }
            r13.tonKeyName = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "tonCreationFinished"
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x0287 }
            r13.tonCreationFinished = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "sharingMyLocationUntil"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.sharingMyLocationUntil = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "lastMyLocationShareTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.lastMyLocationShareTime = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "filtersLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.filtersLoaded = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "tonPasscodeSalt"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x0287 }
            if (r2 == 0) goto L_0x015f
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ Exception -> 0x015b }
            r13.tonPasscodeSalt = r2     // Catch:{ Exception -> 0x015b }
            java.lang.String r2 = "tonPasscodeType"
            int r2 = r1.getInt(r2, r4)     // Catch:{ Exception -> 0x015b }
            r13.tonPasscodeType = r2     // Catch:{ Exception -> 0x015b }
            java.lang.String r2 = "tonPasscodeRetryInMs"
            long r9 = r1.getLong(r2, r6)     // Catch:{ Exception -> 0x015b }
            r13.tonPasscodeRetryInMs = r9     // Catch:{ Exception -> 0x015b }
            java.lang.String r2 = "tonLastUptimeMillis"
            long r9 = r1.getLong(r2, r6)     // Catch:{ Exception -> 0x015b }
            r13.tonLastUptimeMillis = r9     // Catch:{ Exception -> 0x015b }
            java.lang.String r2 = "tonBadPasscodeTries"
            int r2 = r1.getInt(r2, r3)     // Catch:{ Exception -> 0x015b }
            r13.tonBadPasscodeTries = r2     // Catch:{ Exception -> 0x015b }
            goto L_0x015f
        L_0x015b:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0287 }
        L_0x015f:
            java.lang.String r2 = "terms"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ Exception -> 0x0180 }
            if (r2 == 0) goto L_0x0184
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ Exception -> 0x0180 }
            if (r2 == 0) goto L_0x0184
            org.telegram.tgnet.SerializedData r9 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0180 }
            r9.<init>((byte[]) r2)     // Catch:{ Exception -> 0x0180 }
            int r2 = r9.readInt32(r3)     // Catch:{ Exception -> 0x0180 }
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r2 = org.telegram.tgnet.TLRPC$TL_help_termsOfService.TLdeserialize(r9, r2, r3)     // Catch:{ Exception -> 0x0180 }
            r13.unacceptedTermsOfService = r2     // Catch:{ Exception -> 0x0180 }
            r9.cleanup()     // Catch:{ Exception -> 0x0180 }
            goto L_0x0184
        L_0x0180:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0287 }
        L_0x0184:
            int r2 = r13.currentAccount     // Catch:{ all -> 0x0287 }
            if (r2 != 0) goto L_0x0209
            java.lang.String r2 = "appUpdateCheckTime"
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0287 }
            long r9 = r1.getLong(r2, r9)     // Catch:{ all -> 0x0287 }
            r13.lastUpdateCheckTime = r9     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "appUpdate"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ Exception -> 0x0205 }
            if (r2 == 0) goto L_0x01cc
            java.lang.String r9 = "appUpdateBuild"
            int r10 = org.telegram.messenger.BuildVars.BUILD_VERSION     // Catch:{ Exception -> 0x0205 }
            int r9 = r1.getInt(r9, r10)     // Catch:{ Exception -> 0x0205 }
            r13.pendingAppUpdateBuildVersion = r9     // Catch:{ Exception -> 0x0205 }
            java.lang.String r9 = "appUpdateTime"
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0205 }
            long r9 = r1.getLong(r9, r10)     // Catch:{ Exception -> 0x0205 }
            r13.pendingAppUpdateInstallTime = r9     // Catch:{ Exception -> 0x0205 }
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ Exception -> 0x0205 }
            if (r2 == 0) goto L_0x01cc
            org.telegram.tgnet.SerializedData r9 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0205 }
            r9.<init>((byte[]) r2)     // Catch:{ Exception -> 0x0205 }
            int r2 = r9.readInt32(r3)     // Catch:{ Exception -> 0x0205 }
            org.telegram.tgnet.TLRPC$help_AppUpdate r2 = org.telegram.tgnet.TLRPC$help_AppUpdate.TLdeserialize(r9, r2, r3)     // Catch:{ Exception -> 0x0205 }
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r2 = (org.telegram.tgnet.TLRPC$TL_help_appUpdate) r2     // Catch:{ Exception -> 0x0205 }
            r13.pendingAppUpdate = r2     // Catch:{ Exception -> 0x0205 }
            r9.cleanup()     // Catch:{ Exception -> 0x0205 }
        L_0x01cc:
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r2 = r13.pendingAppUpdate     // Catch:{ Exception -> 0x0205 }
            if (r2 == 0) goto L_0x0209
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01e9 }
            android.content.pm.PackageManager r2 = r2.getPackageManager()     // Catch:{ Exception -> 0x01e9 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01e9 }
            java.lang.String r9 = r9.getPackageName()     // Catch:{ Exception -> 0x01e9 }
            android.content.pm.PackageInfo r2 = r2.getPackageInfo(r9, r3)     // Catch:{ Exception -> 0x01e9 }
            long r9 = r2.lastUpdateTime     // Catch:{ Exception -> 0x01e9 }
            long r11 = r2.firstInstallTime     // Catch:{ Exception -> 0x01e9 }
            long r9 = java.lang.Math.max(r9, r11)     // Catch:{ Exception -> 0x01e9 }
            goto L_0x01ee
        L_0x01e9:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0205 }
            r9 = r6
        L_0x01ee:
            int r2 = r13.pendingAppUpdateBuildVersion     // Catch:{ Exception -> 0x0205 }
            int r11 = org.telegram.messenger.BuildVars.BUILD_VERSION     // Catch:{ Exception -> 0x0205 }
            if (r2 != r11) goto L_0x01fa
            long r11 = r13.pendingAppUpdateInstallTime     // Catch:{ Exception -> 0x0205 }
            int r2 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r2 >= 0) goto L_0x0209
        L_0x01fa:
            r13.pendingAppUpdate = r8     // Catch:{ Exception -> 0x0205 }
            org.telegram.messenger.-$$Lambda$UserConfig$mbgV_Rqcpo8e1nQzwR-3h8PHEow r2 = new org.telegram.messenger.-$$Lambda$UserConfig$mbgV_Rqcpo8e1nQzwR-3h8PHEow     // Catch:{ Exception -> 0x0205 }
            r2.<init>()     // Catch:{ Exception -> 0x0205 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)     // Catch:{ Exception -> 0x0205 }
            goto L_0x0209
        L_0x0205:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0287 }
        L_0x0209:
            java.lang.String r2 = "6migrateOffsetId"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.migrateOffsetId = r2     // Catch:{ all -> 0x0287 }
            if (r2 == r4) goto L_0x023b
            java.lang.String r2 = "6migrateOffsetDate"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.migrateOffsetDate = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "6migrateOffsetUserId"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.migrateOffsetUserId = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "6migrateOffsetChatId"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.migrateOffsetChatId = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "6migrateOffsetChannelId"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x0287 }
            r13.migrateOffsetChannelId = r2     // Catch:{ all -> 0x0287 }
            java.lang.String r2 = "6migrateOffsetAccess"
            long r6 = r1.getLong(r2, r6)     // Catch:{ all -> 0x0287 }
            r13.migrateOffsetAccess = r6     // Catch:{ all -> 0x0287 }
        L_0x023b:
            java.lang.String r2 = "tmpPassword"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x0287 }
            if (r2 == 0) goto L_0x025b
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x0287 }
            if (r2 == 0) goto L_0x025b
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x0287 }
            r4.<init>((byte[]) r2)     // Catch:{ all -> 0x0287 }
            int r2 = r4.readInt32(r3)     // Catch:{ all -> 0x0287 }
            org.telegram.tgnet.TLRPC$TL_account_tmpPassword r2 = org.telegram.tgnet.TLRPC$TL_account_tmpPassword.TLdeserialize(r4, r2, r3)     // Catch:{ all -> 0x0287 }
            r13.tmpPassword = r2     // Catch:{ all -> 0x0287 }
            r4.cleanup()     // Catch:{ all -> 0x0287 }
        L_0x025b:
            java.lang.String r2 = "user"
            java.lang.String r1 = r1.getString(r2, r8)     // Catch:{ all -> 0x0287 }
            if (r1 == 0) goto L_0x027b
            byte[] r1 = android.util.Base64.decode(r1, r3)     // Catch:{ all -> 0x0287 }
            if (r1 == 0) goto L_0x027b
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x0287 }
            r2.<init>((byte[]) r1)     // Catch:{ all -> 0x0287 }
            int r1 = r2.readInt32(r3)     // Catch:{ all -> 0x0287 }
            org.telegram.tgnet.TLRPC$User r1 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r2, r1, r3)     // Catch:{ all -> 0x0287 }
            r13.currentUser = r1     // Catch:{ all -> 0x0287 }
            r2.cleanup()     // Catch:{ all -> 0x0287 }
        L_0x027b:
            org.telegram.tgnet.TLRPC$User r1 = r13.currentUser     // Catch:{ all -> 0x0287 }
            if (r1 == 0) goto L_0x0283
            int r1 = r1.id     // Catch:{ all -> 0x0287 }
            r13.clientUserId = r1     // Catch:{ all -> 0x0287 }
        L_0x0283:
            r13.configLoaded = r5     // Catch:{ all -> 0x0287 }
            monitor-exit(r0)     // Catch:{ all -> 0x0287 }
            return
        L_0x0287:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0287 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserConfig.loadConfig():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadConfig$0 */
    public /* synthetic */ void lambda$loadConfig$0$UserConfig() {
        saveConfig(false);
    }

    public boolean isConfigLoaded() {
        return this.configLoaded;
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
        this.savedPasswordTime = 0;
        if (this.savedPasswordHash != null) {
            Arrays.fill(this.savedPasswordHash, (byte) 0);
            this.savedPasswordHash = null;
        }
        if (this.savedSaltedPassword != null) {
            Arrays.fill(this.savedSaltedPassword, (byte) 0);
            this.savedSaltedPassword = null;
        }
    }

    private SharedPreferences getPreferences() {
        if (this.currentAccount == 0) {
            return ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
        }
        Context context = ApplicationLoader.applicationContext;
        return context.getSharedPreferences("userconfig" + this.currentAccount, 0);
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
        boolean z = false;
        this.sharingMyLocationUntil = 0;
        this.lastMyLocationShareTime = 0;
        this.currentUser = null;
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
        this.filtersLoaded = false;
        this.pendingAppUpdate = null;
        this.hasSecureData = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        resetSavedPassword();
        int i = 0;
        while (true) {
            if (i >= 3) {
                break;
            } else if (AccountInstance.getInstance(i).getUserConfig().isClientActivated()) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (!z) {
            SharedConfig.clearConfig();
        }
        saveConfig(true);
    }

    public boolean isPinnedDialogsLoaded(int i) {
        SharedPreferences preferences = getPreferences();
        return preferences.getBoolean("2pinnedDialogsLoaded" + i, false);
    }

    public void setPinnedDialogsLoaded(int i, boolean z) {
        SharedPreferences.Editor edit = getPreferences().edit();
        edit.putBoolean("2pinnedDialogsLoaded" + i, z).commit();
    }

    public int getTotalDialogsCount(int i) {
        SharedPreferences preferences = getPreferences();
        StringBuilder sb = new StringBuilder();
        sb.append("2totalDialogsLoadCount");
        sb.append(i == 0 ? "" : Integer.valueOf(i));
        return preferences.getInt(sb.toString(), 0);
    }

    public void setTotalDialogsCount(int i, int i2) {
        SharedPreferences.Editor edit = getPreferences().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("2totalDialogsLoadCount");
        sb.append(i == 0 ? "" : Integer.valueOf(i));
        edit.putInt(sb.toString(), i2).commit();
    }

    public int[] getDialogLoadOffsets(int i) {
        SharedPreferences preferences = getPreferences();
        StringBuilder sb = new StringBuilder();
        sb.append("2dialogsLoadOffsetId");
        Object obj = "";
        sb.append(i == 0 ? obj : Integer.valueOf(i));
        int i2 = -1;
        int i3 = preferences.getInt(sb.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("2dialogsLoadOffsetDate");
        sb2.append(i == 0 ? obj : Integer.valueOf(i));
        int i4 = preferences.getInt(sb2.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("2dialogsLoadOffsetUserId");
        sb3.append(i == 0 ? obj : Integer.valueOf(i));
        int i5 = preferences.getInt(sb3.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetChatId");
        sb4.append(i == 0 ? obj : Integer.valueOf(i));
        int i6 = preferences.getInt(sb4.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChannelId");
        sb5.append(i == 0 ? obj : Integer.valueOf(i));
        String sb6 = sb5.toString();
        if (this.hasValidDialogLoadIds) {
            i2 = 0;
        }
        int i7 = preferences.getInt(sb6, i2);
        StringBuilder sb7 = new StringBuilder();
        sb7.append("2dialogsLoadOffsetAccess");
        if (i != 0) {
            obj = Integer.valueOf(i);
        }
        sb7.append(obj);
        long j = preferences.getLong(sb7.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        return new int[]{i3, i4, i5, i6, i7, (int) j, (int) (j >> 32)};
    }

    public void setDialogsLoadOffset(int i, int i2, int i3, int i4, int i5, int i6, long j) {
        SharedPreferences.Editor edit = getPreferences().edit();
        StringBuilder sb = new StringBuilder();
        sb.append("2dialogsLoadOffsetId");
        Object obj = "";
        sb.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(sb.toString(), i2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("2dialogsLoadOffsetDate");
        sb2.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(sb2.toString(), i3);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("2dialogsLoadOffsetUserId");
        sb3.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(sb3.toString(), i4);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetChatId");
        sb4.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(sb4.toString(), i5);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChannelId");
        sb5.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putInt(sb5.toString(), i6);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("2dialogsLoadOffsetAccess");
        if (i != 0) {
            obj = Integer.valueOf(i);
        }
        sb6.append(obj);
        edit.putLong(sb6.toString(), j);
        edit.putBoolean("hasValidDialogLoadIds", true);
        edit.commit();
    }
}
