package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    public static final int didSetOrRemoveTwoStepPassword;
    public static final int didSetPasscode;
    public static final int didStartedCall;
    public static final int didUpdateConnectionState;
    public static final int didUpdateMessagesViews;
    public static final int didUpdatePollResults;
    public static final int didUpdateReactions;
    public static final int didVerifyMessagesStickers;
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
    public static final int loadingMessagesFailed;
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
    public static final int twoStepPasswordChanged;
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
    private final HashMap<Integer, int[]> allowedNotifications = new HashMap<>();
    private int animationInProgressCount;
    private int animationInProgressPointer = 1;
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
        loadingMessagesFailed = i8;
        int i10 = i9 + 1;
        totalEvents = i10;
        messageReceivedByAck = i9;
        int i11 = i10 + 1;
        totalEvents = i11;
        messageReceivedByServer = i10;
        int i12 = i11 + 1;
        totalEvents = i12;
        messageSendError = i11;
        int i13 = i12 + 1;
        totalEvents = i13;
        contactsDidLoad = i12;
        int i14 = i13 + 1;
        totalEvents = i14;
        contactsImported = i13;
        int i15 = i14 + 1;
        totalEvents = i15;
        hasNewContactsToImport = i14;
        int i16 = i15 + 1;
        totalEvents = i16;
        chatDidCreated = i15;
        int i17 = i16 + 1;
        totalEvents = i17;
        chatDidFailCreate = i16;
        int i18 = i17 + 1;
        totalEvents = i18;
        chatInfoDidLoad = i17;
        int i19 = i18 + 1;
        totalEvents = i19;
        chatInfoCantLoad = i18;
        int i20 = i19 + 1;
        totalEvents = i20;
        mediaDidLoad = i19;
        int i21 = i20 + 1;
        totalEvents = i21;
        mediaCountDidLoad = i20;
        int i22 = i21 + 1;
        totalEvents = i22;
        mediaCountsDidLoad = i21;
        int i23 = i22 + 1;
        totalEvents = i23;
        encryptedChatUpdated = i22;
        int i24 = i23 + 1;
        totalEvents = i24;
        messagesReadEncrypted = i23;
        int i25 = i24 + 1;
        totalEvents = i25;
        encryptedChatCreated = i24;
        int i26 = i25 + 1;
        totalEvents = i26;
        dialogPhotosLoaded = i25;
        int i27 = i26 + 1;
        totalEvents = i27;
        folderBecomeEmpty = i26;
        int i28 = i27 + 1;
        totalEvents = i28;
        removeAllMessagesFromDialog = i27;
        int i29 = i28 + 1;
        totalEvents = i29;
        notificationsSettingsUpdated = i28;
        int i30 = i29 + 1;
        totalEvents = i30;
        blockedUsersDidLoad = i29;
        int i31 = i30 + 1;
        totalEvents = i31;
        openedChatChanged = i30;
        int i32 = i31 + 1;
        totalEvents = i32;
        didCreatedNewDeleteTask = i31;
        int i33 = i32 + 1;
        totalEvents = i33;
        mainUserInfoChanged = i32;
        int i34 = i33 + 1;
        totalEvents = i34;
        privacyRulesUpdated = i33;
        int i35 = i34 + 1;
        totalEvents = i35;
        updateMessageMedia = i34;
        int i36 = i35 + 1;
        totalEvents = i36;
        replaceMessagesObjects = i35;
        int i37 = i36 + 1;
        totalEvents = i37;
        didSetPasscode = i36;
        int i38 = i37 + 1;
        totalEvents = i38;
        twoStepPasswordChanged = i37;
        int i39 = i38 + 1;
        totalEvents = i39;
        didSetOrRemoveTwoStepPassword = i38;
        int i40 = i39 + 1;
        totalEvents = i40;
        didRemoveTwoStepPassword = i39;
        int i41 = i40 + 1;
        totalEvents = i41;
        replyMessagesDidLoad = i40;
        int i42 = i41 + 1;
        totalEvents = i42;
        pinnedMessageDidLoad = i41;
        int i43 = i42 + 1;
        totalEvents = i43;
        newSessionReceived = i42;
        int i44 = i43 + 1;
        totalEvents = i44;
        didReceivedWebpages = i43;
        int i45 = i44 + 1;
        totalEvents = i45;
        didReceivedWebpagesInUpdates = i44;
        int i46 = i45 + 1;
        totalEvents = i46;
        stickersDidLoad = i45;
        int i47 = i46 + 1;
        totalEvents = i47;
        diceStickersDidLoad = i46;
        int i48 = i47 + 1;
        totalEvents = i48;
        featuredStickersDidLoad = i47;
        int i49 = i48 + 1;
        totalEvents = i49;
        groupStickersDidLoad = i48;
        int i50 = i49 + 1;
        totalEvents = i50;
        messagesReadContent = i49;
        int i51 = i50 + 1;
        totalEvents = i51;
        botInfoDidLoad = i50;
        int i52 = i51 + 1;
        totalEvents = i52;
        userInfoDidLoad = i51;
        int i53 = i52 + 1;
        totalEvents = i53;
        botKeyboardDidLoad = i52;
        int i54 = i53 + 1;
        totalEvents = i54;
        chatSearchResultsAvailable = i53;
        int i55 = i54 + 1;
        totalEvents = i55;
        chatSearchResultsLoading = i54;
        int i56 = i55 + 1;
        totalEvents = i56;
        musicDidLoad = i55;
        int i57 = i56 + 1;
        totalEvents = i57;
        needShowAlert = i56;
        int i58 = i57 + 1;
        totalEvents = i58;
        needShowPlayServicesAlert = i57;
        int i59 = i58 + 1;
        totalEvents = i59;
        didUpdateMessagesViews = i58;
        int i60 = i59 + 1;
        totalEvents = i60;
        needReloadRecentDialogsSearch = i59;
        int i61 = i60 + 1;
        totalEvents = i61;
        peerSettingsDidLoad = i60;
        int i62 = i61 + 1;
        totalEvents = i62;
        wasUnableToFindCurrentLocation = i61;
        int i63 = i62 + 1;
        totalEvents = i63;
        reloadHints = i62;
        int i64 = i63 + 1;
        totalEvents = i64;
        reloadInlineHints = i63;
        int i65 = i64 + 1;
        totalEvents = i65;
        newDraftReceived = i64;
        int i66 = i65 + 1;
        totalEvents = i66;
        recentDocumentsDidLoad = i65;
        int i67 = i66 + 1;
        totalEvents = i67;
        needAddArchivedStickers = i66;
        int i68 = i67 + 1;
        totalEvents = i68;
        archivedStickersCountDidLoad = i67;
        int i69 = i68 + 1;
        totalEvents = i69;
        paymentFinished = i68;
        int i70 = i69 + 1;
        totalEvents = i70;
        channelRightsUpdated = i69;
        int i71 = i70 + 1;
        totalEvents = i71;
        openArticle = i70;
        int i72 = i71 + 1;
        totalEvents = i72;
        updateMentionsCount = i71;
        int i73 = i72 + 1;
        totalEvents = i73;
        didUpdatePollResults = i72;
        int i74 = i73 + 1;
        totalEvents = i74;
        chatOnlineCountDidLoad = i73;
        int i75 = i74 + 1;
        totalEvents = i75;
        videoLoadingStateChanged = i74;
        int i76 = i75 + 1;
        totalEvents = i76;
        newPeopleNearbyAvailable = i75;
        int i77 = i76 + 1;
        totalEvents = i77;
        stopAllHeavyOperations = i76;
        int i78 = i77 + 1;
        totalEvents = i78;
        startAllHeavyOperations = i77;
        int i79 = i78 + 1;
        totalEvents = i79;
        sendingMessagesChanged = i78;
        int i80 = i79 + 1;
        totalEvents = i80;
        didUpdateReactions = i79;
        int i81 = i80 + 1;
        totalEvents = i81;
        didVerifyMessagesStickers = i80;
        int i82 = i81 + 1;
        totalEvents = i82;
        scheduledMessagesUpdated = i81;
        int i83 = i82 + 1;
        totalEvents = i83;
        walletPendingTransactionsChanged = i82;
        int i84 = i83 + 1;
        totalEvents = i84;
        walletSyncProgressChanged = i83;
        int i85 = i84 + 1;
        totalEvents = i85;
        httpFileDidLoad = i84;
        int i86 = i85 + 1;
        totalEvents = i86;
        httpFileDidFailedLoad = i85;
        int i87 = i86 + 1;
        totalEvents = i87;
        didUpdateConnectionState = i86;
        int i88 = i87 + 1;
        totalEvents = i88;
        FileDidUpload = i87;
        int i89 = i88 + 1;
        totalEvents = i89;
        FileDidFailUpload = i88;
        int i90 = i89 + 1;
        totalEvents = i90;
        FileUploadProgressChanged = i89;
        int i91 = i90 + 1;
        totalEvents = i91;
        FileLoadProgressChanged = i90;
        int i92 = i91 + 1;
        totalEvents = i92;
        fileDidLoad = i91;
        int i93 = i92 + 1;
        totalEvents = i93;
        fileDidFailToLoad = i92;
        int i94 = i93 + 1;
        totalEvents = i94;
        filePreparingStarted = i93;
        int i95 = i94 + 1;
        totalEvents = i95;
        fileNewChunkAvailable = i94;
        int i96 = i95 + 1;
        totalEvents = i96;
        filePreparingFailed = i95;
        int i97 = i96 + 1;
        totalEvents = i97;
        dialogsUnreadCounterChanged = i96;
        int i98 = i97 + 1;
        totalEvents = i98;
        messagePlayingProgressDidChanged = i97;
        int i99 = i98 + 1;
        totalEvents = i99;
        messagePlayingDidReset = i98;
        int i100 = i99 + 1;
        totalEvents = i100;
        messagePlayingPlayStateChanged = i99;
        int i101 = i100 + 1;
        totalEvents = i101;
        messagePlayingDidStart = i100;
        int i102 = i101 + 1;
        totalEvents = i102;
        messagePlayingDidSeek = i101;
        int i103 = i102 + 1;
        totalEvents = i103;
        messagePlayingGoingToStop = i102;
        int i104 = i103 + 1;
        totalEvents = i104;
        recordProgressChanged = i103;
        int i105 = i104 + 1;
        totalEvents = i105;
        recordStarted = i104;
        int i106 = i105 + 1;
        totalEvents = i106;
        recordStartError = i105;
        int i107 = i106 + 1;
        totalEvents = i107;
        recordStopped = i106;
        int i108 = i107 + 1;
        totalEvents = i108;
        screenshotTook = i107;
        int i109 = i108 + 1;
        totalEvents = i109;
        albumsDidLoad = i108;
        int i110 = i109 + 1;
        totalEvents = i110;
        audioDidSent = i109;
        int i111 = i110 + 1;
        totalEvents = i111;
        audioRecordTooShort = i110;
        int i112 = i111 + 1;
        totalEvents = i112;
        audioRouteChanged = i111;
        int i113 = i112 + 1;
        totalEvents = i113;
        didStartedCall = i112;
        int i114 = i113 + 1;
        totalEvents = i114;
        didEndCall = i113;
        int i115 = i114 + 1;
        totalEvents = i115;
        closeInCallActivity = i114;
        int i116 = i115 + 1;
        totalEvents = i116;
        appDidLogout = i115;
        int i117 = i116 + 1;
        totalEvents = i117;
        configLoaded = i116;
        int i118 = i117 + 1;
        totalEvents = i118;
        needDeleteDialog = i117;
        int i119 = i118 + 1;
        totalEvents = i119;
        newEmojiSuggestionsAvailable = i118;
        int i120 = i119 + 1;
        totalEvents = i120;
        themeUploadedToServer = i119;
        int i121 = i120 + 1;
        totalEvents = i121;
        themeUploadError = i120;
        int i122 = i121 + 1;
        totalEvents = i122;
        dialogFiltersUpdated = i121;
        int i123 = i122 + 1;
        totalEvents = i123;
        filterSettingsUpdated = i122;
        int i124 = i123 + 1;
        totalEvents = i124;
        suggestedFiltersLoaded = i123;
        int i125 = i124 + 1;
        totalEvents = i125;
        pushMessagesUpdated = i124;
        int i126 = i125 + 1;
        totalEvents = i126;
        stopEncodingService = i125;
        int i127 = i126 + 1;
        totalEvents = i127;
        wallpapersDidLoad = i126;
        int i128 = i127 + 1;
        totalEvents = i128;
        wallpapersNeedReload = i127;
        int i129 = i128 + 1;
        totalEvents = i129;
        didReceiveSmsCode = i128;
        int i130 = i129 + 1;
        totalEvents = i130;
        didReceiveCall = i129;
        int i131 = i130 + 1;
        totalEvents = i131;
        emojiDidLoad = i130;
        int i132 = i131 + 1;
        totalEvents = i132;
        closeOtherAppActivities = i131;
        int i133 = i132 + 1;
        totalEvents = i133;
        cameraInitied = i132;
        int i134 = i133 + 1;
        totalEvents = i134;
        didReplacedPhotoInMemCache = i133;
        int i135 = i134 + 1;
        totalEvents = i135;
        didSetNewTheme = i134;
        int i136 = i135 + 1;
        totalEvents = i136;
        themeListUpdated = i135;
        int i137 = i136 + 1;
        totalEvents = i137;
        didApplyNewTheme = i136;
        int i138 = i137 + 1;
        totalEvents = i138;
        themeAccentListUpdated = i137;
        int i139 = i138 + 1;
        totalEvents = i139;
        needCheckSystemBarColors = i138;
        int i140 = i139 + 1;
        totalEvents = i140;
        needShareTheme = i139;
        int i141 = i140 + 1;
        totalEvents = i141;
        needSetDayNightTheme = i140;
        int i142 = i141 + 1;
        totalEvents = i142;
        goingToPreviewTheme = i141;
        int i143 = i142 + 1;
        totalEvents = i143;
        locationPermissionGranted = i142;
        int i144 = i143 + 1;
        totalEvents = i144;
        reloadInterface = i143;
        int i145 = i144 + 1;
        totalEvents = i145;
        suggestedLangpack = i144;
        int i146 = i145 + 1;
        totalEvents = i146;
        didSetNewWallpapper = i145;
        int i147 = i146 + 1;
        totalEvents = i147;
        proxySettingsChanged = i146;
        int i148 = i147 + 1;
        totalEvents = i148;
        proxyCheckDone = i147;
        int i149 = i148 + 1;
        totalEvents = i149;
        liveLocationsChanged = i148;
        int i150 = i149 + 1;
        totalEvents = i150;
        newLocationAvailable = i149;
        int i151 = i150 + 1;
        totalEvents = i151;
        liveLocationsCacheChanged = i150;
        int i152 = i151 + 1;
        totalEvents = i152;
        notificationsCountUpdated = i151;
        int i153 = i152 + 1;
        totalEvents = i153;
        playerDidStartPlaying = i152;
        int i154 = i153 + 1;
        totalEvents = i154;
        closeSearchByActiveAction = i153;
        int i155 = i154 + 1;
        totalEvents = i155;
        messagePlayingSpeedChanged = i154;
        totalEvents = i155 + 1;
        screenStateChanged = i155;
    }

    private static class DelayedPost {
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

    public int setAnimationInProgress(int i, int[] iArr) {
        onAnimationFinish(i);
        if (this.animationInProgressCount == 0) {
            getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512);
        }
        this.animationInProgressCount++;
        this.animationInProgressPointer++;
        if (iArr == null) {
            iArr = new int[0];
        }
        this.allowedNotifications.put(Integer.valueOf(this.animationInProgressPointer), iArr);
        return this.animationInProgressPointer;
    }

    public void updateAllowedNotifications(int i, int[] iArr) {
        if (this.allowedNotifications.containsKey(Integer.valueOf(i))) {
            if (iArr == null) {
                iArr = new int[0];
            }
            this.allowedNotifications.put(Integer.valueOf(i), iArr);
        }
    }

    public void onAnimationFinish(int i) {
        if (this.allowedNotifications.remove(Integer.valueOf(i)) != null) {
            int i2 = this.animationInProgressCount - 1;
            this.animationInProgressCount = i2;
            if (i2 == 0) {
                getGlobalInstance().postNotificationName(startAllHeavyOperations, 512);
                if (!this.delayedPosts.isEmpty()) {
                    for (int i3 = 0; i3 < this.delayedPosts.size(); i3++) {
                        DelayedPost delayedPost = this.delayedPosts.get(i3);
                        postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
                    }
                    this.delayedPosts.clear();
                }
            }
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgressCount > 0;
    }

    public int getCurrentHeavyOperationFlags() {
        return this.currentHeavyOperationFlags;
    }

    public void postNotificationName(int i, Object... objArr) {
        int[] iArr;
        boolean z = true;
        boolean z2 = i == startAllHeavyOperations || i == stopAllHeavyOperations;
        if (!z2 && !this.allowedNotifications.isEmpty()) {
            int size = this.allowedNotifications.size();
            Iterator<Integer> it = this.allowedNotifications.keySet().iterator();
            int i2 = 0;
            while (it.hasNext() && (iArr = this.allowedNotifications.get(it.next())) != null) {
                int i3 = 0;
                while (true) {
                    if (i3 >= iArr.length) {
                        break;
                    } else if (iArr[i3] == i) {
                        i2++;
                        break;
                    } else {
                        i3++;
                    }
                }
            }
            if (size != i2) {
                z = false;
            }
            z2 = z;
        }
        if (i == startAllHeavyOperations) {
            this.currentHeavyOperationFlags = (objArr[0].intValue() ^ -1) & this.currentHeavyOperationFlags;
        } else if (i == stopAllHeavyOperations) {
            this.currentHeavyOperationFlags = objArr[0].intValue() | this.currentHeavyOperationFlags;
        }
        postNotificationNameInternal(i, z2, objArr);
    }

    public void postNotificationNameInternal(int i, boolean z, Object... objArr) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (z || !isAnimationInProgress()) {
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
