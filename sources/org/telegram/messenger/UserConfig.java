package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$InputStorePaymentPurpose;
import org.telegram.tgnet.TLRPC$TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.tgnet.TLRPC$User;

public class UserConfig extends BaseController {
    private static volatile UserConfig[] Instance = new UserConfig[4];
    public static final int MAX_ACCOUNT_COUNT = 4;
    public static final int MAX_ACCOUNT_DEFAULT_COUNT = 3;
    public static final int i_dialogsLoadOffsetAccess = 5;
    public static final int i_dialogsLoadOffsetChannelId = 4;
    public static final int i_dialogsLoadOffsetChatId = 3;
    public static final int i_dialogsLoadOffsetDate = 1;
    public static final int i_dialogsLoadOffsetId = 0;
    public static final int i_dialogsLoadOffsetUserId = 2;
    public static int selectedAccount;
    public long autoDownloadConfigLoadTime;
    public List<String> awaitBillingProductIds = new ArrayList();
    public TLRPC$InputStorePaymentPurpose billingPaymentPurpose;
    public int botRatingLoadTime;
    public long clientUserId;
    private boolean configLoaded;
    public boolean contactsReimported;
    public int contactsSavedCount;
    private TLRPC$User currentUser;
    public boolean draftsLoaded;
    public boolean filtersLoaded;
    public String genericAnimationsStickerPack;
    public boolean hasSecureData;
    public boolean hasValidDialogLoadIds;
    public int lastBroadcastId = -1;
    public int lastContactsSyncTime;
    public int lastHintsSyncTime;
    public int lastMyLocationShareTime;
    public int lastSendMessageId = -210000;
    public long lastUpdatedGenericAnimations;
    public long lastUpdatedPremiumGiftsStickerPack;
    public int loginTime;
    public long migrateOffsetAccess = -1;
    public long migrateOffsetChannelId = -1;
    public long migrateOffsetChatId = -1;
    public int migrateOffsetDate = -1;
    public int migrateOffsetId = -1;
    public long migrateOffsetUserId = -1;
    public boolean notificationsSettingsLoaded;
    public boolean notificationsSignUpSettingsLoaded;
    public String premiumGiftsStickerPack;
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
        for (int i2 = 0; i2 < 4; i2++) {
            if (AccountInstance.getInstance(i2).getUserConfig().isClientActivated()) {
                i++;
            }
        }
        return i;
    }

    public UserConfig(int i) {
        super(i);
    }

    public static boolean hasPremiumOnAccounts() {
        for (int i = 0; i < 4; i++) {
            if (AccountInstance.getInstance(i).getUserConfig().isClientActivated() && AccountInstance.getInstance(i).getUserConfig().getUserConfig().isPremium()) {
                return true;
            }
        }
        return false;
    }

    public static int getMaxAccountCount() {
        return hasPremiumOnAccounts() ? 5 : 3;
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
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new UserConfig$$ExternalSyntheticLambda1(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveConfig$0(boolean z) {
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
                edit.putStringSet("awaitBillingProductIds", new HashSet(this.awaitBillingProductIds));
                TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose = this.billingPaymentPurpose;
                if (tLRPC$InputStorePaymentPurpose != null) {
                    SerializedData serializedData = new SerializedData(tLRPC$InputStorePaymentPurpose.getObjectSize());
                    this.billingPaymentPurpose.serializeToStream(serializedData);
                    edit.putString("billingPaymentPurpose", Base64.encodeToString(serializedData.toByteArray(), 0));
                    serializedData.cleanup();
                } else {
                    edit.remove("billingPaymentPurpose");
                }
                edit.putString("premiumGiftsStickerPack", this.premiumGiftsStickerPack);
                edit.putLong("lastUpdatedPremiumGiftsStickerPack", this.lastUpdatedPremiumGiftsStickerPack);
                edit.putString("genericAnimationsStickerPack", this.genericAnimationsStickerPack);
                edit.putLong("lastUpdatedGenericAnimations", this.lastUpdatedGenericAnimations);
                edit.putInt("6migrateOffsetId", this.migrateOffsetId);
                if (this.migrateOffsetId != -1) {
                    edit.putInt("6migrateOffsetDate", this.migrateOffsetDate);
                    edit.putLong("6migrateOffsetUserId", this.migrateOffsetUserId);
                    edit.putLong("6migrateOffsetChatId", this.migrateOffsetChatId);
                    edit.putLong("6migrateOffsetChannelId", this.migrateOffsetChannelId);
                    edit.putLong("6migrateOffsetAccess", this.migrateOffsetAccess);
                }
                TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService = this.unacceptedTermsOfService;
                if (tLRPC$TL_help_termsOfService != null) {
                    try {
                        SerializedData serializedData2 = new SerializedData(tLRPC$TL_help_termsOfService.getObjectSize());
                        this.unacceptedTermsOfService.serializeToStream(serializedData2);
                        edit.putString("terms", Base64.encodeToString(serializedData2.toByteArray(), 0));
                        serializedData2.cleanup();
                    } catch (Exception unused) {
                    }
                } else {
                    edit.remove("terms");
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static boolean isValidAccount(int i) {
        return i >= 0 && i < 4 && getInstance(i).isClientActivated();
    }

    public boolean isClientActivated() {
        boolean z;
        synchronized (this.sync) {
            z = this.currentUser != null;
        }
        return z;
    }

    public long getClientUserId() {
        long j;
        synchronized (this.sync) {
            TLRPC$User tLRPC$User = this.currentUser;
            j = tLRPC$User != null ? tLRPC$User.id : 0;
        }
        return j;
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
            TLRPC$User tLRPC$User2 = this.currentUser;
            this.currentUser = tLRPC$User;
            this.clientUserId = tLRPC$User.id;
            checkPremium(tLRPC$User2, tLRPC$User);
        }
    }

    private void checkPremium(TLRPC$User tLRPC$User, TLRPC$User tLRPC$User2) {
        if (tLRPC$User == null || !(tLRPC$User2 == null || tLRPC$User.premium == tLRPC$User2.premium)) {
            AndroidUtilities.runOnUIThread(new UserConfig$$ExternalSyntheticLambda0(this, tLRPC$User2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPremium$1(TLRPC$User tLRPC$User) {
        getMessagesController().updatePremium(tLRPC$User.premium);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.currentUserPremiumStatusChanged, new Object[0]);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.premiumStatusChangedGlobal, new Object[0]);
        getMediaDataController().loadPremiumPromo(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0180 A[Catch:{ Exception -> 0x0172 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01ec A[Catch:{ Exception -> 0x0172 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadConfig() {
        /*
            r11 = this;
            java.lang.Object r0 = r11.sync
            monitor-enter(r0)
            boolean r1 = r11.configLoaded     // Catch:{ all -> 0x01f9 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x01f9 }
            return
        L_0x0009:
            android.content.SharedPreferences r1 = r11.getPreferences()     // Catch:{ all -> 0x01f9 }
            int r2 = r11.currentAccount     // Catch:{ all -> 0x01f9 }
            r3 = 0
            if (r2 != 0) goto L_0x001a
            java.lang.String r2 = "selectedAccount"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            selectedAccount = r2     // Catch:{ all -> 0x01f9 }
        L_0x001a:
            java.lang.String r2 = "registeredForPush"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.registeredForPush = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastSendMessageId"
            r4 = -210000(0xfffffffffffccbb0, float:NaN)
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x01f9 }
            r11.lastSendMessageId = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "contactsSavedCount"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.contactsSavedCount = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastBroadcastId"
            r4 = -1
            int r2 = r1.getInt(r2, r4)     // Catch:{ all -> 0x01f9 }
            r11.lastBroadcastId = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastContactsSyncTime"
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x01f9 }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r7
            int r6 = (int) r5     // Catch:{ all -> 0x01f9 }
            r5 = 82800(0x14370, float:1.16028E-40)
            int r6 = r6 - r5
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.lastContactsSyncTime = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastHintsSyncTime"
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x01f9 }
            long r5 = r5 / r7
            int r6 = (int) r5     // Catch:{ all -> 0x01f9 }
            r5 = 90000(0x15var_, float:1.26117E-40)
            int r6 = r6 - r5
            int r2 = r1.getInt(r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.lastHintsSyncTime = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "draftsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.draftsLoaded = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "unreadDialogsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.unreadDialogsLoaded = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "contactsReimported"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.contactsReimported = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "ratingLoadTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.ratingLoadTime = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "botRatingLoadTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.botRatingLoadTime = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "loginTime"
            int r5 = r11.currentAccount     // Catch:{ all -> 0x01f9 }
            int r2 = r1.getInt(r2, r5)     // Catch:{ all -> 0x01f9 }
            r11.loginTime = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "syncContacts"
            r5 = 1
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x01f9 }
            r11.syncContacts = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "suggestContacts"
            boolean r2 = r1.getBoolean(r2, r5)     // Catch:{ all -> 0x01f9 }
            r11.suggestContacts = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "hasSecureData"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.hasSecureData = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "notificationsSettingsLoaded3"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.notificationsSettingsLoaded = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "notificationsSignUpSettingsLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.notificationsSignUpSettingsLoaded = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "autoDownloadConfigLoadTime"
            r6 = 0
            long r8 = r1.getLong(r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.autoDownloadConfigLoadTime = r8     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "2dialogsLoadOffsetId"
            boolean r2 = r1.contains(r2)     // Catch:{ all -> 0x01f9 }
            if (r2 != 0) goto L_0x00dc
            java.lang.String r2 = "hasValidDialogLoadIds"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            if (r2 == 0) goto L_0x00da
            goto L_0x00dc
        L_0x00da:
            r2 = 0
            goto L_0x00dd
        L_0x00dc:
            r2 = 1
        L_0x00dd:
            r11.hasValidDialogLoadIds = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "sharingMyLocationUntil"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.sharingMyLocationUntil = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastMyLocationShareTime"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.lastMyLocationShareTime = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "filtersLoaded"
            boolean r2 = r1.getBoolean(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.filtersLoaded = r2     // Catch:{ all -> 0x01f9 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x01f9 }
            java.lang.String r8 = "awaitBillingProductIds"
            java.util.Set r9 = java.util.Collections.emptySet()     // Catch:{ all -> 0x01f9 }
            java.util.Set r8 = r1.getStringSet(r8, r9)     // Catch:{ all -> 0x01f9 }
            r2.<init>(r8)     // Catch:{ all -> 0x01f9 }
            r11.awaitBillingProductIds = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "billingPaymentPurpose"
            boolean r2 = r1.contains(r2)     // Catch:{ all -> 0x01f9 }
            r8 = 0
            if (r2 == 0) goto L_0x0131
            java.lang.String r2 = "billingPaymentPurpose"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x01f9 }
            if (r2 == 0) goto L_0x0131
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x01f9 }
            if (r2 == 0) goto L_0x0131
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x01f9 }
            r2.<init>()     // Catch:{ all -> 0x01f9 }
            int r9 = r2.readInt32(r3)     // Catch:{ all -> 0x01f9 }
            org.telegram.tgnet.TLRPC$InputStorePaymentPurpose r9 = org.telegram.tgnet.TLRPC$InputStorePaymentPurpose.TLdeserialize(r2, r9, r3)     // Catch:{ all -> 0x01f9 }
            r11.billingPaymentPurpose = r9     // Catch:{ all -> 0x01f9 }
            r2.cleanup()     // Catch:{ all -> 0x01f9 }
        L_0x0131:
            java.lang.String r2 = "premiumGiftsStickerPack"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x01f9 }
            r11.premiumGiftsStickerPack = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastUpdatedPremiumGiftsStickerPack"
            long r9 = r1.getLong(r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.lastUpdatedPremiumGiftsStickerPack = r9     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "genericAnimationsStickerPack"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x01f9 }
            r11.genericAnimationsStickerPack = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "lastUpdatedGenericAnimations"
            long r9 = r1.getLong(r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.lastUpdatedGenericAnimations = r9     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "terms"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ Exception -> 0x0172 }
            if (r2 == 0) goto L_0x0176
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ Exception -> 0x0172 }
            if (r2 == 0) goto L_0x0176
            org.telegram.tgnet.SerializedData r9 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0172 }
            r9.<init>((byte[]) r2)     // Catch:{ Exception -> 0x0172 }
            int r2 = r9.readInt32(r3)     // Catch:{ Exception -> 0x0172 }
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r2 = org.telegram.tgnet.TLRPC$TL_help_termsOfService.TLdeserialize(r9, r2, r3)     // Catch:{ Exception -> 0x0172 }
            r11.unacceptedTermsOfService = r2     // Catch:{ Exception -> 0x0172 }
            r9.cleanup()     // Catch:{ Exception -> 0x0172 }
            goto L_0x0176
        L_0x0172:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x01f9 }
        L_0x0176:
            java.lang.String r2 = "6migrateOffsetId"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.migrateOffsetId = r2     // Catch:{ all -> 0x01f9 }
            if (r2 == r4) goto L_0x01a8
            java.lang.String r2 = "6migrateOffsetDate"
            int r2 = r1.getInt(r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.migrateOffsetDate = r2     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "6migrateOffsetUserId"
            long r9 = org.telegram.messenger.AndroidUtilities.getPrefIntOrLong(r1, r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.migrateOffsetUserId = r9     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "6migrateOffsetChatId"
            long r9 = org.telegram.messenger.AndroidUtilities.getPrefIntOrLong(r1, r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.migrateOffsetChatId = r9     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "6migrateOffsetChannelId"
            long r9 = org.telegram.messenger.AndroidUtilities.getPrefIntOrLong(r1, r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.migrateOffsetChannelId = r9     // Catch:{ all -> 0x01f9 }
            java.lang.String r2 = "6migrateOffsetAccess"
            long r6 = r1.getLong(r2, r6)     // Catch:{ all -> 0x01f9 }
            r11.migrateOffsetAccess = r6     // Catch:{ all -> 0x01f9 }
        L_0x01a8:
            java.lang.String r2 = "tmpPassword"
            java.lang.String r2 = r1.getString(r2, r8)     // Catch:{ all -> 0x01f9 }
            if (r2 == 0) goto L_0x01c8
            byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x01f9 }
            if (r2 == 0) goto L_0x01c8
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x01f9 }
            r4.<init>((byte[]) r2)     // Catch:{ all -> 0x01f9 }
            int r2 = r4.readInt32(r3)     // Catch:{ all -> 0x01f9 }
            org.telegram.tgnet.TLRPC$TL_account_tmpPassword r2 = org.telegram.tgnet.TLRPC$TL_account_tmpPassword.TLdeserialize(r4, r2, r3)     // Catch:{ all -> 0x01f9 }
            r11.tmpPassword = r2     // Catch:{ all -> 0x01f9 }
            r4.cleanup()     // Catch:{ all -> 0x01f9 }
        L_0x01c8:
            java.lang.String r2 = "user"
            java.lang.String r1 = r1.getString(r2, r8)     // Catch:{ all -> 0x01f9 }
            if (r1 == 0) goto L_0x01e8
            byte[] r1 = android.util.Base64.decode(r1, r3)     // Catch:{ all -> 0x01f9 }
            if (r1 == 0) goto L_0x01e8
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData     // Catch:{ all -> 0x01f9 }
            r2.<init>((byte[]) r1)     // Catch:{ all -> 0x01f9 }
            int r1 = r2.readInt32(r3)     // Catch:{ all -> 0x01f9 }
            org.telegram.tgnet.TLRPC$User r1 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r2, r1, r3)     // Catch:{ all -> 0x01f9 }
            r11.currentUser = r1     // Catch:{ all -> 0x01f9 }
            r2.cleanup()     // Catch:{ all -> 0x01f9 }
        L_0x01e8:
            org.telegram.tgnet.TLRPC$User r1 = r11.currentUser     // Catch:{ all -> 0x01f9 }
            if (r1 == 0) goto L_0x01f5
            r11.checkPremium(r8, r1)     // Catch:{ all -> 0x01f9 }
            org.telegram.tgnet.TLRPC$User r1 = r11.currentUser     // Catch:{ all -> 0x01f9 }
            long r1 = r1.id     // Catch:{ all -> 0x01f9 }
            r11.clientUserId = r1     // Catch:{ all -> 0x01f9 }
        L_0x01f5:
            r11.configLoaded = r5     // Catch:{ all -> 0x01f9 }
            monitor-exit(r0)     // Catch:{ all -> 0x01f9 }
            return
        L_0x01f9:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x01f9 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserConfig.loadConfig():void");
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

    public void clearConfig() {
        getPreferences().edit().clear().apply();
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
        this.hasSecureData = false;
        this.loginTime = (int) (System.currentTimeMillis() / 1000);
        this.lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        this.lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        resetSavedPassword();
        int i = 0;
        while (true) {
            if (i >= 4) {
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

    public long[] getDialogLoadOffsets(int i) {
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
        String sb3 = sb2.toString();
        if (this.hasValidDialogLoadIds) {
            i2 = 0;
        }
        int i4 = preferences.getInt(sb3, i2);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetUserId");
        sb4.append(i == 0 ? obj : Integer.valueOf(i));
        long j = -1;
        long prefIntOrLong = AndroidUtilities.getPrefIntOrLong(preferences, sb4.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChatId");
        sb5.append(i == 0 ? obj : Integer.valueOf(i));
        long prefIntOrLong2 = AndroidUtilities.getPrefIntOrLong(preferences, sb5.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("2dialogsLoadOffsetChannelId");
        sb6.append(i == 0 ? obj : Integer.valueOf(i));
        long prefIntOrLong3 = AndroidUtilities.getPrefIntOrLong(preferences, sb6.toString(), this.hasValidDialogLoadIds ? 0 : -1);
        StringBuilder sb7 = new StringBuilder();
        sb7.append("2dialogsLoadOffsetAccess");
        if (i != 0) {
            obj = Integer.valueOf(i);
        }
        sb7.append(obj);
        String sb8 = sb7.toString();
        if (this.hasValidDialogLoadIds) {
            j = 0;
        }
        return new long[]{(long) i3, (long) i4, prefIntOrLong, prefIntOrLong2, prefIntOrLong3, preferences.getLong(sb8, j)};
    }

    public void setDialogsLoadOffset(int i, int i2, int i3, long j, long j2, long j3, long j4) {
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
        edit.putLong(sb3.toString(), j);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("2dialogsLoadOffsetChatId");
        sb4.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putLong(sb4.toString(), j2);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("2dialogsLoadOffsetChannelId");
        sb5.append(i == 0 ? obj : Integer.valueOf(i));
        edit.putLong(sb5.toString(), j3);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("2dialogsLoadOffsetAccess");
        if (i != 0) {
            obj = Integer.valueOf(i);
        }
        sb6.append(obj);
        edit.putLong(sb6.toString(), j4);
        edit.putBoolean("hasValidDialogLoadIds", true);
        edit.commit();
    }

    public boolean isPremium() {
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User == null) {
            return false;
        }
        return tLRPC$User.premium;
    }

    public Long getEmojiStatus() {
        TLRPC$User tLRPC$User = this.currentUser;
        if (tLRPC$User == null) {
            return null;
        }
        TLRPC$EmojiStatus tLRPC$EmojiStatus = tLRPC$User.emoji_status;
        if ((tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) && ((TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus).until > ((int) (System.currentTimeMillis() / 1000))) {
            return Long.valueOf(((TLRPC$TL_emojiStatusUntil) this.currentUser.emoji_status).document_id);
        }
        TLRPC$EmojiStatus tLRPC$EmojiStatus2 = this.currentUser.emoji_status;
        if (tLRPC$EmojiStatus2 instanceof TLRPC$TL_emojiStatus) {
            return Long.valueOf(((TLRPC$TL_emojiStatus) tLRPC$EmojiStatus2).document_id);
        }
        return null;
    }
}
