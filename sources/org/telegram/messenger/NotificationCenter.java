package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

public class NotificationCenter {
    public static final int FileDidFailUpload;
    public static final int FileDidUpload;
    public static final int FileLoadProgressChanged;
    public static final int FileUploadProgressChanged;
    private static volatile NotificationCenter[] Instance = new NotificationCenter[3];
    public static final int albumsDidLoad;
    public static final int appDidLogout;
    public static final int archivedStickersCountDidLoad;
    public static final int audioDidSent;
    public static final int audioRecordTooShort;
    public static final int audioRouteChanged;
    public static final int blockedUsersDidLoad;
    public static final int botInfoDidLoad;
    public static final int botKeyboardDidLoad;
    public static final int cameraInitied;
    public static final int channelRightsUpdated;
    public static final int chatDidCreated;
    public static final int chatDidFailCreate;
    public static final int chatInfoCantLoad;
    public static final int chatInfoDidLoad;
    public static final int chatOnlineCountDidLoad;
    public static final int chatSearchResultsAvailable;
    public static final int chatSearchResultsLoading;
    public static final int closeChats;
    public static final int closeInCallActivity;
    public static final int closeOtherAppActivities;
    public static final int closeSearchByActiveAction;
    public static final int configLoaded;
    public static final int contactsDidLoad;
    public static final int contactsImported;
    public static final int dialogFiltersUpdated;
    public static final int dialogPhotosLoaded;
    public static final int dialogsNeedReload;
    public static final int dialogsUnreadCounterChanged;
    public static final int diceStickersDidLoad;
    public static final int didApplyNewTheme;
    public static final int didCreatedNewDeleteTask;
    public static final int didEndCall;
    public static final int didReceiveCall;
    public static final int didReceiveNewMessages = 1;
    public static final int didReceiveSmsCode;
    public static final int didReceivedWebpages;
    public static final int didReceivedWebpagesInUpdates;
    public static final int didRemoveTwoStepPassword;
    public static final int didReplacedPhotoInMemCache;
    public static final int didSetNewTheme;
    public static final int didSetNewWallpapper;
    public static final int didSetPasscode;
    public static final int didSetTwoStepPassword;
    public static final int didStartedCall;
    public static final int didUpdateConnectionState;
    public static final int didUpdateMessagesViews;
    public static final int didUpdatePollResults;
    public static final int didUpdateReactions;
    public static final int emojiDidLoad;
    public static final int encryptedChatCreated;
    public static final int encryptedChatUpdated;
    public static final int featuredStickersDidLoad;
    public static final int fileDidFailToLoad;
    public static final int fileDidLoad;
    public static final int fileNewChunkAvailable;
    public static final int filePreparingFailed;
    public static final int filePreparingStarted;
    public static final int filterSettingsUpdated;
    public static final int folderBecomeEmpty;
    private static volatile NotificationCenter globalInstance;
    public static final int goingToPreviewTheme;
    public static final int groupStickersDidLoad;
    public static final int hasNewContactsToImport;
    public static final int historyCleared;
    public static final int httpFileDidFailedLoad;
    public static final int httpFileDidLoad;
    public static final int liveLocationsCacheChanged;
    public static final int liveLocationsChanged;
    public static final int locationPermissionGranted;
    public static final int mainUserInfoChanged;
    public static final int mediaCountDidLoad;
    public static final int mediaCountsDidLoad;
    public static final int mediaDidLoad;
    public static final int messagePlayingDidReset;
    public static final int messagePlayingDidSeek;
    public static final int messagePlayingDidStart;
    public static final int messagePlayingGoingToStop;
    public static final int messagePlayingPlayStateChanged;
    public static final int messagePlayingProgressDidChanged;
    public static final int messagePlayingSpeedChanged;
    public static final int messageReceivedByAck;
    public static final int messageReceivedByServer;
    public static final int messageSendError;
    public static final int messagesDeleted;
    public static final int messagesDidLoad;
    public static final int messagesRead;
    public static final int messagesReadContent;
    public static final int messagesReadEncrypted;
    public static final int musicDidLoad;
    public static final int needAddArchivedStickers;
    public static final int needCheckSystemBarColors;
    public static final int needDeleteDialog;
    public static final int needReloadRecentDialogsSearch;
    public static final int needSetDayNightTheme;
    public static final int needShareTheme;
    public static final int needShowAlert;
    public static final int needShowPlayServicesAlert;
    public static final int newDraftReceived;
    public static final int newEmojiSuggestionsAvailable;
    public static final int newLocationAvailable;
    public static final int newPeopleNearbyAvailable;
    public static final int newSessionReceived;
    public static final int notificationsCountUpdated;
    public static final int notificationsSettingsUpdated;
    public static final int openArticle;
    public static final int openedChatChanged;
    public static final int paymentFinished;
    public static final int peerSettingsDidLoad;
    public static final int pinnedMessageDidLoad;
    public static final int playerDidStartPlaying;
    public static final int privacyRulesUpdated;
    public static final int proxyCheckDone;
    public static final int proxySettingsChanged;
    public static final int pushMessagesUpdated;
    public static final int recentDocumentsDidLoad;
    public static final int recordProgressChanged;
    public static final int recordStartError;
    public static final int recordStarted;
    public static final int recordStopped;
    public static final int reloadHints;
    public static final int reloadInlineHints;
    public static final int reloadInterface;
    public static final int removeAllMessagesFromDialog;
    public static final int replaceMessagesObjects;
    public static final int replyMessagesDidLoad;
    public static final int scheduledMessagesUpdated;
    public static final int screenStateChanged;
    public static final int screenshotTook;
    public static final int sendingMessagesChanged;
    public static final int startAllHeavyOperations;
    public static final int stickersDidLoad;
    public static final int stopAllHeavyOperations;
    public static final int stopEncodingService;
    public static final int suggestedFiltersLoaded;
    public static final int suggestedLangpack;
    public static final int themeAccentListUpdated;
    public static final int themeListUpdated;
    public static final int themeUploadError;
    public static final int themeUploadedToServer;
    private static int totalEvents;
    public static final int updateInterfaces;
    public static final int updateMentionsCount;
    public static final int updateMessageMedia;
    public static final int userInfoDidLoad;
    public static final int videoLoadingStateChanged;
    public static final int walletPendingTransactionsChanged;
    public static final int walletSyncProgressChanged;
    public static final int wallpapersDidLoad;
    public static final int wallpapersNeedReload;
    public static final int wasUnableToFindCurrentLocation;
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();
    private int[] allowedNotifications;
    private boolean animationInProgress;
    private int broadcasting = 0;
    private int currentAccount;
    private int currentHeavyOperationFlags;
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);
    private SparseArray<ArrayList<NotificationCenterDelegate>> observers = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> removeAfterBroadcast = new SparseArray<>();

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, int i2, Object... objArr);
    }

    static {
        int i = 1 + 1;
        totalEvents = i;
        int i2 = i + 1;
        totalEvents = i2;
        updateInterfaces = i;
        int i3 = i2 + 1;
        totalEvents = i3;
        dialogsNeedReload = i2;
        int i4 = i3 + 1;
        totalEvents = i4;
        closeChats = i3;
        int i5 = i4 + 1;
        totalEvents = i5;
        messagesDeleted = i4;
        int i6 = i5 + 1;
        totalEvents = i6;
        historyCleared = i5;
        int i7 = i6 + 1;
        totalEvents = i7;
        messagesRead = i6;
        int i8 = i7 + 1;
        totalEvents = i8;
        messagesDidLoad = i7;
        int i9 = i8 + 1;
        totalEvents = i9;
        messageReceivedByAck = i8;
        int i10 = i9 + 1;
        totalEvents = i10;
        messageReceivedByServer = i9;
        int i11 = i10 + 1;
        totalEvents = i11;
        messageSendError = i10;
        int i12 = i11 + 1;
        totalEvents = i12;
        contactsDidLoad = i11;
        int i13 = i12 + 1;
        totalEvents = i13;
        contactsImported = i12;
        int i14 = i13 + 1;
        totalEvents = i14;
        hasNewContactsToImport = i13;
        int i15 = i14 + 1;
        totalEvents = i15;
        chatDidCreated = i14;
        int i16 = i15 + 1;
        totalEvents = i16;
        chatDidFailCreate = i15;
        int i17 = i16 + 1;
        totalEvents = i17;
        chatInfoDidLoad = i16;
        int i18 = i17 + 1;
        totalEvents = i18;
        chatInfoCantLoad = i17;
        int i19 = i18 + 1;
        totalEvents = i19;
        mediaDidLoad = i18;
        int i20 = i19 + 1;
        totalEvents = i20;
        mediaCountDidLoad = i19;
        int i21 = i20 + 1;
        totalEvents = i21;
        mediaCountsDidLoad = i20;
        int i22 = i21 + 1;
        totalEvents = i22;
        encryptedChatUpdated = i21;
        int i23 = i22 + 1;
        totalEvents = i23;
        messagesReadEncrypted = i22;
        int i24 = i23 + 1;
        totalEvents = i24;
        encryptedChatCreated = i23;
        int i25 = i24 + 1;
        totalEvents = i25;
        dialogPhotosLoaded = i24;
        int i26 = i25 + 1;
        totalEvents = i26;
        folderBecomeEmpty = i25;
        int i27 = i26 + 1;
        totalEvents = i27;
        removeAllMessagesFromDialog = i26;
        int i28 = i27 + 1;
        totalEvents = i28;
        notificationsSettingsUpdated = i27;
        int i29 = i28 + 1;
        totalEvents = i29;
        blockedUsersDidLoad = i28;
        int i30 = i29 + 1;
        totalEvents = i30;
        openedChatChanged = i29;
        int i31 = i30 + 1;
        totalEvents = i31;
        didCreatedNewDeleteTask = i30;
        int i32 = i31 + 1;
        totalEvents = i32;
        mainUserInfoChanged = i31;
        int i33 = i32 + 1;
        totalEvents = i33;
        privacyRulesUpdated = i32;
        int i34 = i33 + 1;
        totalEvents = i34;
        updateMessageMedia = i33;
        int i35 = i34 + 1;
        totalEvents = i35;
        replaceMessagesObjects = i34;
        int i36 = i35 + 1;
        totalEvents = i36;
        didSetPasscode = i35;
        int i37 = i36 + 1;
        totalEvents = i37;
        didSetTwoStepPassword = i36;
        int i38 = i37 + 1;
        totalEvents = i38;
        didRemoveTwoStepPassword = i37;
        int i39 = i38 + 1;
        totalEvents = i39;
        replyMessagesDidLoad = i38;
        int i40 = i39 + 1;
        totalEvents = i40;
        pinnedMessageDidLoad = i39;
        int i41 = i40 + 1;
        totalEvents = i41;
        newSessionReceived = i40;
        int i42 = i41 + 1;
        totalEvents = i42;
        didReceivedWebpages = i41;
        int i43 = i42 + 1;
        totalEvents = i43;
        didReceivedWebpagesInUpdates = i42;
        int i44 = i43 + 1;
        totalEvents = i44;
        stickersDidLoad = i43;
        int i45 = i44 + 1;
        totalEvents = i45;
        diceStickersDidLoad = i44;
        int i46 = i45 + 1;
        totalEvents = i46;
        featuredStickersDidLoad = i45;
        int i47 = i46 + 1;
        totalEvents = i47;
        groupStickersDidLoad = i46;
        int i48 = i47 + 1;
        totalEvents = i48;
        messagesReadContent = i47;
        int i49 = i48 + 1;
        totalEvents = i49;
        botInfoDidLoad = i48;
        int i50 = i49 + 1;
        totalEvents = i50;
        userInfoDidLoad = i49;
        int i51 = i50 + 1;
        totalEvents = i51;
        botKeyboardDidLoad = i50;
        int i52 = i51 + 1;
        totalEvents = i52;
        chatSearchResultsAvailable = i51;
        int i53 = i52 + 1;
        totalEvents = i53;
        chatSearchResultsLoading = i52;
        int i54 = i53 + 1;
        totalEvents = i54;
        musicDidLoad = i53;
        int i55 = i54 + 1;
        totalEvents = i55;
        needShowAlert = i54;
        int i56 = i55 + 1;
        totalEvents = i56;
        needShowPlayServicesAlert = i55;
        int i57 = i56 + 1;
        totalEvents = i57;
        didUpdateMessagesViews = i56;
        int i58 = i57 + 1;
        totalEvents = i58;
        needReloadRecentDialogsSearch = i57;
        int i59 = i58 + 1;
        totalEvents = i59;
        peerSettingsDidLoad = i58;
        int i60 = i59 + 1;
        totalEvents = i60;
        wasUnableToFindCurrentLocation = i59;
        int i61 = i60 + 1;
        totalEvents = i61;
        reloadHints = i60;
        int i62 = i61 + 1;
        totalEvents = i62;
        reloadInlineHints = i61;
        int i63 = i62 + 1;
        totalEvents = i63;
        newDraftReceived = i62;
        int i64 = i63 + 1;
        totalEvents = i64;
        recentDocumentsDidLoad = i63;
        int i65 = i64 + 1;
        totalEvents = i65;
        needAddArchivedStickers = i64;
        int i66 = i65 + 1;
        totalEvents = i66;
        archivedStickersCountDidLoad = i65;
        int i67 = i66 + 1;
        totalEvents = i67;
        paymentFinished = i66;
        int i68 = i67 + 1;
        totalEvents = i68;
        channelRightsUpdated = i67;
        int i69 = i68 + 1;
        totalEvents = i69;
        openArticle = i68;
        int i70 = i69 + 1;
        totalEvents = i70;
        updateMentionsCount = i69;
        int i71 = i70 + 1;
        totalEvents = i71;
        didUpdatePollResults = i70;
        int i72 = i71 + 1;
        totalEvents = i72;
        chatOnlineCountDidLoad = i71;
        int i73 = i72 + 1;
        totalEvents = i73;
        videoLoadingStateChanged = i72;
        int i74 = i73 + 1;
        totalEvents = i74;
        newPeopleNearbyAvailable = i73;
        int i75 = i74 + 1;
        totalEvents = i75;
        stopAllHeavyOperations = i74;
        int i76 = i75 + 1;
        totalEvents = i76;
        startAllHeavyOperations = i75;
        int i77 = i76 + 1;
        totalEvents = i77;
        sendingMessagesChanged = i76;
        int i78 = i77 + 1;
        totalEvents = i78;
        didUpdateReactions = i77;
        int i79 = i78 + 1;
        totalEvents = i79;
        scheduledMessagesUpdated = i78;
        int i80 = i79 + 1;
        totalEvents = i80;
        walletPendingTransactionsChanged = i79;
        int i81 = i80 + 1;
        totalEvents = i81;
        walletSyncProgressChanged = i80;
        int i82 = i81 + 1;
        totalEvents = i82;
        httpFileDidLoad = i81;
        int i83 = i82 + 1;
        totalEvents = i83;
        httpFileDidFailedLoad = i82;
        int i84 = i83 + 1;
        totalEvents = i84;
        didUpdateConnectionState = i83;
        int i85 = i84 + 1;
        totalEvents = i85;
        FileDidUpload = i84;
        int i86 = i85 + 1;
        totalEvents = i86;
        FileDidFailUpload = i85;
        int i87 = i86 + 1;
        totalEvents = i87;
        FileUploadProgressChanged = i86;
        int i88 = i87 + 1;
        totalEvents = i88;
        FileLoadProgressChanged = i87;
        int i89 = i88 + 1;
        totalEvents = i89;
        fileDidLoad = i88;
        int i90 = i89 + 1;
        totalEvents = i90;
        fileDidFailToLoad = i89;
        int i91 = i90 + 1;
        totalEvents = i91;
        filePreparingStarted = i90;
        int i92 = i91 + 1;
        totalEvents = i92;
        fileNewChunkAvailable = i91;
        int i93 = i92 + 1;
        totalEvents = i93;
        filePreparingFailed = i92;
        int i94 = i93 + 1;
        totalEvents = i94;
        dialogsUnreadCounterChanged = i93;
        int i95 = i94 + 1;
        totalEvents = i95;
        messagePlayingProgressDidChanged = i94;
        int i96 = i95 + 1;
        totalEvents = i96;
        messagePlayingDidReset = i95;
        int i97 = i96 + 1;
        totalEvents = i97;
        messagePlayingPlayStateChanged = i96;
        int i98 = i97 + 1;
        totalEvents = i98;
        messagePlayingDidStart = i97;
        int i99 = i98 + 1;
        totalEvents = i99;
        messagePlayingDidSeek = i98;
        int i100 = i99 + 1;
        totalEvents = i100;
        messagePlayingGoingToStop = i99;
        int i101 = i100 + 1;
        totalEvents = i101;
        recordProgressChanged = i100;
        int i102 = i101 + 1;
        totalEvents = i102;
        recordStarted = i101;
        int i103 = i102 + 1;
        totalEvents = i103;
        recordStartError = i102;
        int i104 = i103 + 1;
        totalEvents = i104;
        recordStopped = i103;
        int i105 = i104 + 1;
        totalEvents = i105;
        screenshotTook = i104;
        int i106 = i105 + 1;
        totalEvents = i106;
        albumsDidLoad = i105;
        int i107 = i106 + 1;
        totalEvents = i107;
        audioDidSent = i106;
        int i108 = i107 + 1;
        totalEvents = i108;
        audioRecordTooShort = i107;
        int i109 = i108 + 1;
        totalEvents = i109;
        audioRouteChanged = i108;
        int i110 = i109 + 1;
        totalEvents = i110;
        didStartedCall = i109;
        int i111 = i110 + 1;
        totalEvents = i111;
        didEndCall = i110;
        int i112 = i111 + 1;
        totalEvents = i112;
        closeInCallActivity = i111;
        int i113 = i112 + 1;
        totalEvents = i113;
        appDidLogout = i112;
        int i114 = i113 + 1;
        totalEvents = i114;
        configLoaded = i113;
        int i115 = i114 + 1;
        totalEvents = i115;
        needDeleteDialog = i114;
        int i116 = i115 + 1;
        totalEvents = i116;
        newEmojiSuggestionsAvailable = i115;
        int i117 = i116 + 1;
        totalEvents = i117;
        themeUploadedToServer = i116;
        int i118 = i117 + 1;
        totalEvents = i118;
        themeUploadError = i117;
        int i119 = i118 + 1;
        totalEvents = i119;
        dialogFiltersUpdated = i118;
        int i120 = i119 + 1;
        totalEvents = i120;
        filterSettingsUpdated = i119;
        int i121 = i120 + 1;
        totalEvents = i121;
        suggestedFiltersLoaded = i120;
        int i122 = i121 + 1;
        totalEvents = i122;
        pushMessagesUpdated = i121;
        int i123 = i122 + 1;
        totalEvents = i123;
        stopEncodingService = i122;
        int i124 = i123 + 1;
        totalEvents = i124;
        wallpapersDidLoad = i123;
        int i125 = i124 + 1;
        totalEvents = i125;
        wallpapersNeedReload = i124;
        int i126 = i125 + 1;
        totalEvents = i126;
        didReceiveSmsCode = i125;
        int i127 = i126 + 1;
        totalEvents = i127;
        didReceiveCall = i126;
        int i128 = i127 + 1;
        totalEvents = i128;
        emojiDidLoad = i127;
        int i129 = i128 + 1;
        totalEvents = i129;
        closeOtherAppActivities = i128;
        int i130 = i129 + 1;
        totalEvents = i130;
        cameraInitied = i129;
        int i131 = i130 + 1;
        totalEvents = i131;
        didReplacedPhotoInMemCache = i130;
        int i132 = i131 + 1;
        totalEvents = i132;
        didSetNewTheme = i131;
        int i133 = i132 + 1;
        totalEvents = i133;
        themeListUpdated = i132;
        int i134 = i133 + 1;
        totalEvents = i134;
        didApplyNewTheme = i133;
        int i135 = i134 + 1;
        totalEvents = i135;
        themeAccentListUpdated = i134;
        int i136 = i135 + 1;
        totalEvents = i136;
        needCheckSystemBarColors = i135;
        int i137 = i136 + 1;
        totalEvents = i137;
        needShareTheme = i136;
        int i138 = i137 + 1;
        totalEvents = i138;
        needSetDayNightTheme = i137;
        int i139 = i138 + 1;
        totalEvents = i139;
        goingToPreviewTheme = i138;
        int i140 = i139 + 1;
        totalEvents = i140;
        locationPermissionGranted = i139;
        int i141 = i140 + 1;
        totalEvents = i141;
        reloadInterface = i140;
        int i142 = i141 + 1;
        totalEvents = i142;
        suggestedLangpack = i141;
        int i143 = i142 + 1;
        totalEvents = i143;
        didSetNewWallpapper = i142;
        int i144 = i143 + 1;
        totalEvents = i144;
        proxySettingsChanged = i143;
        int i145 = i144 + 1;
        totalEvents = i145;
        proxyCheckDone = i144;
        int i146 = i145 + 1;
        totalEvents = i146;
        liveLocationsChanged = i145;
        int i147 = i146 + 1;
        totalEvents = i147;
        newLocationAvailable = i146;
        int i148 = i147 + 1;
        totalEvents = i148;
        liveLocationsCacheChanged = i147;
        int i149 = i148 + 1;
        totalEvents = i149;
        notificationsCountUpdated = i148;
        int i150 = i149 + 1;
        totalEvents = i150;
        playerDidStartPlaying = i149;
        int i151 = i150 + 1;
        totalEvents = i151;
        closeSearchByActiveAction = i150;
        int i152 = i151 + 1;
        totalEvents = i152;
        messagePlayingSpeedChanged = i151;
        totalEvents = i152 + 1;
        screenStateChanged = i152;
    }

    private class DelayedPost {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public int id;

        private DelayedPost(int i, Object[] objArr) {
            this.id = i;
            this.args = objArr;
        }
    }

    public static NotificationCenter getInstance(int i) {
        NotificationCenter notificationCenter = Instance[i];
        if (notificationCenter == null) {
            synchronized (NotificationCenter.class) {
                notificationCenter = Instance[i];
                if (notificationCenter == null) {
                    NotificationCenter[] notificationCenterArr = Instance;
                    NotificationCenter notificationCenter2 = new NotificationCenter(i);
                    notificationCenterArr[i] = notificationCenter2;
                    notificationCenter = notificationCenter2;
                }
            }
        }
        return notificationCenter;
    }

    public static NotificationCenter getGlobalInstance() {
        NotificationCenter notificationCenter = globalInstance;
        if (notificationCenter == null) {
            synchronized (NotificationCenter.class) {
                notificationCenter = globalInstance;
                if (notificationCenter == null) {
                    notificationCenter = new NotificationCenter(-1);
                    globalInstance = notificationCenter;
                }
            }
        }
        return notificationCenter;
    }

    public NotificationCenter(int i) {
        this.currentAccount = i;
    }

    public void setAllowedNotificationsDutingAnimation(int[] iArr) {
        this.allowedNotifications = iArr;
    }

    public void setAnimationInProgress(boolean z) {
        if (z) {
            getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512);
        } else {
            getGlobalInstance().postNotificationName(startAllHeavyOperations, 512);
        }
        this.animationInProgress = z;
        if (!z && !this.delayedPosts.isEmpty()) {
            for (int i = 0; i < this.delayedPosts.size(); i++) {
                DelayedPost delayedPost = this.delayedPosts.get(i);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPosts.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    public int getCurrentHeavyOperationFlags() {
        return this.currentHeavyOperationFlags;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0037  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void postNotificationName(int r7, java.lang.Object... r8) {
        /*
            r6 = this;
            int r0 = startAllHeavyOperations
            r1 = 1
            r2 = 0
            if (r7 == r0) goto L_0x000d
            int r0 = stopAllHeavyOperations
            if (r7 != r0) goto L_0x000b
            goto L_0x000d
        L_0x000b:
            r0 = 0
            goto L_0x000e
        L_0x000d:
            r0 = 1
        L_0x000e:
            if (r0 != 0) goto L_0x0022
            int[] r3 = r6.allowedNotifications
            if (r3 == 0) goto L_0x0022
            r3 = 0
        L_0x0015:
            int[] r4 = r6.allowedNotifications
            int r5 = r4.length
            if (r3 >= r5) goto L_0x0022
            r4 = r4[r3]
            if (r4 != r7) goto L_0x001f
            goto L_0x0023
        L_0x001f:
            int r3 = r3 + 1
            goto L_0x0015
        L_0x0022:
            r1 = r0
        L_0x0023:
            int r0 = startAllHeavyOperations
            if (r7 != r0) goto L_0x0037
            r0 = r8[r2]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r2 = r6.currentHeavyOperationFlags
            int r0 = r0.intValue()
            r0 = r0 ^ -1
            r0 = r0 & r2
            r6.currentHeavyOperationFlags = r0
            goto L_0x0048
        L_0x0037:
            int r0 = stopAllHeavyOperations
            if (r7 != r0) goto L_0x0048
            r0 = r8[r2]
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r2 = r6.currentHeavyOperationFlags
            int r0 = r0.intValue()
            r0 = r0 | r2
            r6.currentHeavyOperationFlags = r0
        L_0x0048:
            r6.postNotificationNameInternal(r7, r1, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationCenter.postNotificationName(int, java.lang.Object[]):void");
    }

    public void postNotificationNameInternal(int i, boolean z, Object... objArr) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (z || !this.animationInProgress) {
            this.broadcasting++;
            ArrayList arrayList = this.observers.get(i);
            if (arrayList != null && !arrayList.isEmpty()) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ((NotificationCenterDelegate) arrayList.get(i2)).didReceivedNotification(i, this.currentAccount, objArr);
                }
            }
            int i3 = this.broadcasting - 1;
            this.broadcasting = i3;
            if (i3 == 0) {
                if (this.removeAfterBroadcast.size() != 0) {
                    for (int i4 = 0; i4 < this.removeAfterBroadcast.size(); i4++) {
                        int keyAt = this.removeAfterBroadcast.keyAt(i4);
                        ArrayList arrayList2 = this.removeAfterBroadcast.get(keyAt);
                        for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                            removeObserver((NotificationCenterDelegate) arrayList2.get(i5), keyAt);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (int i6 = 0; i6 < this.addAfterBroadcast.size(); i6++) {
                        int keyAt2 = this.addAfterBroadcast.keyAt(i6);
                        ArrayList arrayList3 = this.addAfterBroadcast.get(keyAt2);
                        for (int i7 = 0; i7 < arrayList3.size(); i7++) {
                            addObserver((NotificationCenterDelegate) arrayList3.get(i7), keyAt2);
                        }
                    }
                    this.addAfterBroadcast.clear();
                }
            }
        } else {
            this.delayedPosts.add(new DelayedPost(i, objArr));
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("delay post notification " + i + " with args count = " + objArr.length);
            }
        }
    }

    public void addObserver(NotificationCenterDelegate notificationCenterDelegate, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList arrayList = this.addAfterBroadcast.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.addAfterBroadcast.put(i, arrayList);
            }
            arrayList.add(notificationCenterDelegate);
        } else {
            ArrayList arrayList2 = this.observers.get(i);
            if (arrayList2 == null) {
                SparseArray<ArrayList<NotificationCenterDelegate>> sparseArray = this.observers;
                ArrayList arrayList3 = new ArrayList();
                sparseArray.put(i, arrayList3);
                arrayList2 = arrayList3;
            }
            if (!arrayList2.contains(notificationCenterDelegate)) {
                arrayList2.add(notificationCenterDelegate);
            }
        }
    }

    public void removeObserver(NotificationCenterDelegate notificationCenterDelegate, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList arrayList = this.removeAfterBroadcast.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.removeAfterBroadcast.put(i, arrayList);
            }
            arrayList.add(notificationCenterDelegate);
        } else {
            ArrayList arrayList2 = this.observers.get(i);
            if (arrayList2 != null) {
                arrayList2.remove(notificationCenterDelegate);
            }
        }
    }

    public boolean hasObservers(int i) {
        return this.observers.indexOfKey(i) >= 0;
    }
}
