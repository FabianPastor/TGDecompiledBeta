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
    public static final int dialogPhotosLoaded;
    public static final int dialogsNeedReload;
    public static final int dialogsUnreadCounterChanged;
    public static final int didApplyNewTheme;
    public static final int didCreatedNewDeleteTask;
    public static final int didEndCall;
    public static final int didReceiveCall;
    public static final int didReceiveNewMessages;
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
    public static final int folderBecomeEmpty;
    private static volatile NotificationCenter globalInstance = null;
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
    public static final int needCheckSystemBarColors;
    public static final int needDeleteDialog;
    public static final int needReloadArchivedStickers;
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
    public static final int suggestedLangpack;
    public static final int themeAccentListUpdated;
    public static final int themeListUpdated;
    public static final int themeUploadError;
    public static final int themeUploadedToServer;
    private static int totalEvents = 1;
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
        int i = totalEvents;
        totalEvents = i + 1;
        didReceiveNewMessages = i;
        int i2 = totalEvents;
        totalEvents = i2 + 1;
        updateInterfaces = i2;
        int i3 = totalEvents;
        totalEvents = i3 + 1;
        dialogsNeedReload = i3;
        int i4 = totalEvents;
        totalEvents = i4 + 1;
        closeChats = i4;
        int i5 = totalEvents;
        totalEvents = i5 + 1;
        messagesDeleted = i5;
        int i6 = totalEvents;
        totalEvents = i6 + 1;
        historyCleared = i6;
        int i7 = totalEvents;
        totalEvents = i7 + 1;
        messagesRead = i7;
        int i8 = totalEvents;
        totalEvents = i8 + 1;
        messagesDidLoad = i8;
        int i9 = totalEvents;
        totalEvents = i9 + 1;
        messageReceivedByAck = i9;
        int i10 = totalEvents;
        totalEvents = i10 + 1;
        messageReceivedByServer = i10;
        int i11 = totalEvents;
        totalEvents = i11 + 1;
        messageSendError = i11;
        int i12 = totalEvents;
        totalEvents = i12 + 1;
        contactsDidLoad = i12;
        int i13 = totalEvents;
        totalEvents = i13 + 1;
        contactsImported = i13;
        int i14 = totalEvents;
        totalEvents = i14 + 1;
        hasNewContactsToImport = i14;
        int i15 = totalEvents;
        totalEvents = i15 + 1;
        chatDidCreated = i15;
        int i16 = totalEvents;
        totalEvents = i16 + 1;
        chatDidFailCreate = i16;
        int i17 = totalEvents;
        totalEvents = i17 + 1;
        chatInfoDidLoad = i17;
        int i18 = totalEvents;
        totalEvents = i18 + 1;
        chatInfoCantLoad = i18;
        int i19 = totalEvents;
        totalEvents = i19 + 1;
        mediaDidLoad = i19;
        int i20 = totalEvents;
        totalEvents = i20 + 1;
        mediaCountDidLoad = i20;
        int i21 = totalEvents;
        totalEvents = i21 + 1;
        mediaCountsDidLoad = i21;
        int i22 = totalEvents;
        totalEvents = i22 + 1;
        encryptedChatUpdated = i22;
        int i23 = totalEvents;
        totalEvents = i23 + 1;
        messagesReadEncrypted = i23;
        int i24 = totalEvents;
        totalEvents = i24 + 1;
        encryptedChatCreated = i24;
        int i25 = totalEvents;
        totalEvents = i25 + 1;
        dialogPhotosLoaded = i25;
        int i26 = totalEvents;
        totalEvents = i26 + 1;
        folderBecomeEmpty = i26;
        int i27 = totalEvents;
        totalEvents = i27 + 1;
        removeAllMessagesFromDialog = i27;
        int i28 = totalEvents;
        totalEvents = i28 + 1;
        notificationsSettingsUpdated = i28;
        int i29 = totalEvents;
        totalEvents = i29 + 1;
        blockedUsersDidLoad = i29;
        int i30 = totalEvents;
        totalEvents = i30 + 1;
        openedChatChanged = i30;
        int i31 = totalEvents;
        totalEvents = i31 + 1;
        didCreatedNewDeleteTask = i31;
        int i32 = totalEvents;
        totalEvents = i32 + 1;
        mainUserInfoChanged = i32;
        int i33 = totalEvents;
        totalEvents = i33 + 1;
        privacyRulesUpdated = i33;
        int i34 = totalEvents;
        totalEvents = i34 + 1;
        updateMessageMedia = i34;
        int i35 = totalEvents;
        totalEvents = i35 + 1;
        replaceMessagesObjects = i35;
        int i36 = totalEvents;
        totalEvents = i36 + 1;
        didSetPasscode = i36;
        int i37 = totalEvents;
        totalEvents = i37 + 1;
        didSetTwoStepPassword = i37;
        int i38 = totalEvents;
        totalEvents = i38 + 1;
        didRemoveTwoStepPassword = i38;
        int i39 = totalEvents;
        totalEvents = i39 + 1;
        replyMessagesDidLoad = i39;
        int i40 = totalEvents;
        totalEvents = i40 + 1;
        pinnedMessageDidLoad = i40;
        int i41 = totalEvents;
        totalEvents = i41 + 1;
        newSessionReceived = i41;
        int i42 = totalEvents;
        totalEvents = i42 + 1;
        didReceivedWebpages = i42;
        int i43 = totalEvents;
        totalEvents = i43 + 1;
        didReceivedWebpagesInUpdates = i43;
        int i44 = totalEvents;
        totalEvents = i44 + 1;
        stickersDidLoad = i44;
        int i45 = totalEvents;
        totalEvents = i45 + 1;
        featuredStickersDidLoad = i45;
        int i46 = totalEvents;
        totalEvents = i46 + 1;
        groupStickersDidLoad = i46;
        int i47 = totalEvents;
        totalEvents = i47 + 1;
        messagesReadContent = i47;
        int i48 = totalEvents;
        totalEvents = i48 + 1;
        botInfoDidLoad = i48;
        int i49 = totalEvents;
        totalEvents = i49 + 1;
        userInfoDidLoad = i49;
        int i50 = totalEvents;
        totalEvents = i50 + 1;
        botKeyboardDidLoad = i50;
        int i51 = totalEvents;
        totalEvents = i51 + 1;
        chatSearchResultsAvailable = i51;
        int i52 = totalEvents;
        totalEvents = i52 + 1;
        chatSearchResultsLoading = i52;
        int i53 = totalEvents;
        totalEvents = i53 + 1;
        musicDidLoad = i53;
        int i54 = totalEvents;
        totalEvents = i54 + 1;
        needShowAlert = i54;
        int i55 = totalEvents;
        totalEvents = i55 + 1;
        needShowPlayServicesAlert = i55;
        int i56 = totalEvents;
        totalEvents = i56 + 1;
        didUpdateMessagesViews = i56;
        int i57 = totalEvents;
        totalEvents = i57 + 1;
        needReloadRecentDialogsSearch = i57;
        int i58 = totalEvents;
        totalEvents = i58 + 1;
        peerSettingsDidLoad = i58;
        int i59 = totalEvents;
        totalEvents = i59 + 1;
        wasUnableToFindCurrentLocation = i59;
        int i60 = totalEvents;
        totalEvents = i60 + 1;
        reloadHints = i60;
        int i61 = totalEvents;
        totalEvents = i61 + 1;
        reloadInlineHints = i61;
        int i62 = totalEvents;
        totalEvents = i62 + 1;
        newDraftReceived = i62;
        int i63 = totalEvents;
        totalEvents = i63 + 1;
        recentDocumentsDidLoad = i63;
        int i64 = totalEvents;
        totalEvents = i64 + 1;
        needReloadArchivedStickers = i64;
        int i65 = totalEvents;
        totalEvents = i65 + 1;
        archivedStickersCountDidLoad = i65;
        int i66 = totalEvents;
        totalEvents = i66 + 1;
        paymentFinished = i66;
        int i67 = totalEvents;
        totalEvents = i67 + 1;
        channelRightsUpdated = i67;
        int i68 = totalEvents;
        totalEvents = i68 + 1;
        openArticle = i68;
        int i69 = totalEvents;
        totalEvents = i69 + 1;
        updateMentionsCount = i69;
        int i70 = totalEvents;
        totalEvents = i70 + 1;
        didUpdatePollResults = i70;
        int i71 = totalEvents;
        totalEvents = i71 + 1;
        chatOnlineCountDidLoad = i71;
        int i72 = totalEvents;
        totalEvents = i72 + 1;
        videoLoadingStateChanged = i72;
        int i73 = totalEvents;
        totalEvents = i73 + 1;
        newPeopleNearbyAvailable = i73;
        int i74 = totalEvents;
        totalEvents = i74 + 1;
        stopAllHeavyOperations = i74;
        int i75 = totalEvents;
        totalEvents = i75 + 1;
        startAllHeavyOperations = i75;
        int i76 = totalEvents;
        totalEvents = i76 + 1;
        sendingMessagesChanged = i76;
        int i77 = totalEvents;
        totalEvents = i77 + 1;
        didUpdateReactions = i77;
        int i78 = totalEvents;
        totalEvents = i78 + 1;
        scheduledMessagesUpdated = i78;
        int i79 = totalEvents;
        totalEvents = i79 + 1;
        walletPendingTransactionsChanged = i79;
        int i80 = totalEvents;
        totalEvents = i80 + 1;
        walletSyncProgressChanged = i80;
        int i81 = totalEvents;
        totalEvents = i81 + 1;
        httpFileDidLoad = i81;
        int i82 = totalEvents;
        totalEvents = i82 + 1;
        httpFileDidFailedLoad = i82;
        int i83 = totalEvents;
        totalEvents = i83 + 1;
        didUpdateConnectionState = i83;
        int i84 = totalEvents;
        totalEvents = i84 + 1;
        FileDidUpload = i84;
        int i85 = totalEvents;
        totalEvents = i85 + 1;
        FileDidFailUpload = i85;
        int i86 = totalEvents;
        totalEvents = i86 + 1;
        FileUploadProgressChanged = i86;
        int i87 = totalEvents;
        totalEvents = i87 + 1;
        FileLoadProgressChanged = i87;
        int i88 = totalEvents;
        totalEvents = i88 + 1;
        fileDidLoad = i88;
        int i89 = totalEvents;
        totalEvents = i89 + 1;
        fileDidFailToLoad = i89;
        int i90 = totalEvents;
        totalEvents = i90 + 1;
        filePreparingStarted = i90;
        int i91 = totalEvents;
        totalEvents = i91 + 1;
        fileNewChunkAvailable = i91;
        int i92 = totalEvents;
        totalEvents = i92 + 1;
        filePreparingFailed = i92;
        int i93 = totalEvents;
        totalEvents = i93 + 1;
        dialogsUnreadCounterChanged = i93;
        int i94 = totalEvents;
        totalEvents = i94 + 1;
        messagePlayingProgressDidChanged = i94;
        int i95 = totalEvents;
        totalEvents = i95 + 1;
        messagePlayingDidReset = i95;
        int i96 = totalEvents;
        totalEvents = i96 + 1;
        messagePlayingPlayStateChanged = i96;
        int i97 = totalEvents;
        totalEvents = i97 + 1;
        messagePlayingDidStart = i97;
        int i98 = totalEvents;
        totalEvents = i98 + 1;
        messagePlayingDidSeek = i98;
        int i99 = totalEvents;
        totalEvents = i99 + 1;
        messagePlayingGoingToStop = i99;
        int i100 = totalEvents;
        totalEvents = i100 + 1;
        recordProgressChanged = i100;
        int i101 = totalEvents;
        totalEvents = i101 + 1;
        recordStarted = i101;
        int i102 = totalEvents;
        totalEvents = i102 + 1;
        recordStartError = i102;
        int i103 = totalEvents;
        totalEvents = i103 + 1;
        recordStopped = i103;
        int i104 = totalEvents;
        totalEvents = i104 + 1;
        screenshotTook = i104;
        int i105 = totalEvents;
        totalEvents = i105 + 1;
        albumsDidLoad = i105;
        int i106 = totalEvents;
        totalEvents = i106 + 1;
        audioDidSent = i106;
        int i107 = totalEvents;
        totalEvents = i107 + 1;
        audioRecordTooShort = i107;
        int i108 = totalEvents;
        totalEvents = i108 + 1;
        audioRouteChanged = i108;
        int i109 = totalEvents;
        totalEvents = i109 + 1;
        didStartedCall = i109;
        int i110 = totalEvents;
        totalEvents = i110 + 1;
        didEndCall = i110;
        int i111 = totalEvents;
        totalEvents = i111 + 1;
        closeInCallActivity = i111;
        int i112 = totalEvents;
        totalEvents = i112 + 1;
        appDidLogout = i112;
        int i113 = totalEvents;
        totalEvents = i113 + 1;
        configLoaded = i113;
        int i114 = totalEvents;
        totalEvents = i114 + 1;
        needDeleteDialog = i114;
        int i115 = totalEvents;
        totalEvents = i115 + 1;
        newEmojiSuggestionsAvailable = i115;
        int i116 = totalEvents;
        totalEvents = i116 + 1;
        themeUploadedToServer = i116;
        int i117 = totalEvents;
        totalEvents = i117 + 1;
        themeUploadError = i117;
        int i118 = totalEvents;
        totalEvents = i118 + 1;
        pushMessagesUpdated = i118;
        int i119 = totalEvents;
        totalEvents = i119 + 1;
        stopEncodingService = i119;
        int i120 = totalEvents;
        totalEvents = i120 + 1;
        wallpapersDidLoad = i120;
        int i121 = totalEvents;
        totalEvents = i121 + 1;
        wallpapersNeedReload = i121;
        int i122 = totalEvents;
        totalEvents = i122 + 1;
        didReceiveSmsCode = i122;
        int i123 = totalEvents;
        totalEvents = i123 + 1;
        didReceiveCall = i123;
        int i124 = totalEvents;
        totalEvents = i124 + 1;
        emojiDidLoad = i124;
        int i125 = totalEvents;
        totalEvents = i125 + 1;
        closeOtherAppActivities = i125;
        int i126 = totalEvents;
        totalEvents = i126 + 1;
        cameraInitied = i126;
        int i127 = totalEvents;
        totalEvents = i127 + 1;
        didReplacedPhotoInMemCache = i127;
        int i128 = totalEvents;
        totalEvents = i128 + 1;
        didSetNewTheme = i128;
        int i129 = totalEvents;
        totalEvents = i129 + 1;
        themeListUpdated = i129;
        int i130 = totalEvents;
        totalEvents = i130 + 1;
        didApplyNewTheme = i130;
        int i131 = totalEvents;
        totalEvents = i131 + 1;
        themeAccentListUpdated = i131;
        int i132 = totalEvents;
        totalEvents = i132 + 1;
        needCheckSystemBarColors = i132;
        int i133 = totalEvents;
        totalEvents = i133 + 1;
        needShareTheme = i133;
        int i134 = totalEvents;
        totalEvents = i134 + 1;
        needSetDayNightTheme = i134;
        int i135 = totalEvents;
        totalEvents = i135 + 1;
        goingToPreviewTheme = i135;
        int i136 = totalEvents;
        totalEvents = i136 + 1;
        locationPermissionGranted = i136;
        int i137 = totalEvents;
        totalEvents = i137 + 1;
        reloadInterface = i137;
        int i138 = totalEvents;
        totalEvents = i138 + 1;
        suggestedLangpack = i138;
        int i139 = totalEvents;
        totalEvents = i139 + 1;
        didSetNewWallpapper = i139;
        int i140 = totalEvents;
        totalEvents = i140 + 1;
        proxySettingsChanged = i140;
        int i141 = totalEvents;
        totalEvents = i141 + 1;
        proxyCheckDone = i141;
        int i142 = totalEvents;
        totalEvents = i142 + 1;
        liveLocationsChanged = i142;
        int i143 = totalEvents;
        totalEvents = i143 + 1;
        newLocationAvailable = i143;
        int i144 = totalEvents;
        totalEvents = i144 + 1;
        liveLocationsCacheChanged = i144;
        int i145 = totalEvents;
        totalEvents = i145 + 1;
        notificationsCountUpdated = i145;
        int i146 = totalEvents;
        totalEvents = i146 + 1;
        playerDidStartPlaying = i146;
        int i147 = totalEvents;
        totalEvents = i147 + 1;
        closeSearchByActiveAction = i147;
        int i148 = totalEvents;
        totalEvents = i148 + 1;
        messagePlayingSpeedChanged = i148;
        int i149 = totalEvents;
        totalEvents = i149 + 1;
        screenStateChanged = i149;
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
        if (!this.animationInProgress && !this.delayedPosts.isEmpty()) {
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

    public void postNotificationName(int i, Object... objArr) {
        boolean z = i == startAllHeavyOperations || i == stopAllHeavyOperations;
        if (!z && this.allowedNotifications != null) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.allowedNotifications;
                if (i2 >= iArr.length) {
                    break;
                } else if (iArr[i2] == i) {
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
        }
        if (i == startAllHeavyOperations) {
            this.currentHeavyOperationFlags = (objArr[0].intValue() ^ -1) & this.currentHeavyOperationFlags;
        } else if (i == stopAllHeavyOperations) {
            this.currentHeavyOperationFlags = objArr[0].intValue() | this.currentHeavyOperationFlags;
        }
        postNotificationNameInternal(i, z, objArr);
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
            this.broadcasting--;
            if (this.broadcasting == 0) {
                if (this.removeAfterBroadcast.size() != 0) {
                    for (int i3 = 0; i3 < this.removeAfterBroadcast.size(); i3++) {
                        int keyAt = this.removeAfterBroadcast.keyAt(i3);
                        ArrayList arrayList2 = this.removeAfterBroadcast.get(keyAt);
                        for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                            removeObserver((NotificationCenterDelegate) arrayList2.get(i4), keyAt);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (int i5 = 0; i5 < this.addAfterBroadcast.size(); i5++) {
                        int keyAt2 = this.addAfterBroadcast.keyAt(i5);
                        ArrayList arrayList3 = this.addAfterBroadcast.get(keyAt2);
                        for (int i6 = 0; i6 < arrayList3.size(); i6++) {
                            addObserver((NotificationCenterDelegate) arrayList3.get(i6), keyAt2);
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
