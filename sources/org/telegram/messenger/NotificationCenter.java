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
    public static final int changeRepliesCounter;
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
    public static final int commentsRead;
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
    public static final int moreMusicDidLoad;
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
    public static final int newSuggestionsAvailable;
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
    public static final int reloadDialogPhotos;
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
    public static final int threadMessagesRead;
    private static int totalEvents;
    public static final int twoStepPasswordChanged;
    public static final int updateInterfaces;
    public static final int updateMentionsCount;
    public static final int updateMessageMedia;
    public static final int userInfoDidLoad;
    public static final int videoLoadingStateChanged;
    public static final int voipServiceCreated;
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
    private ArrayList<DelayedPost> delayedPostsTmp = new ArrayList<>(10);
    private SparseArray<ArrayList<NotificationCenterDelegate>> observers = new SparseArray<>();
    private ArrayList<PostponeNotificationCallback> postponeCallbackList = new ArrayList<>(10);
    private SparseArray<ArrayList<NotificationCenterDelegate>> removeAfterBroadcast = new SparseArray<>();

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, int i2, Object... objArr);
    }

    public interface PostponeNotificationCallback {
        boolean needPostpone(int i, int i2, Object[] objArr);
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
        threadMessagesRead = i7;
        int i9 = i8 + 1;
        totalEvents = i9;
        commentsRead = i8;
        int i10 = i9 + 1;
        totalEvents = i10;
        changeRepliesCounter = i9;
        int i11 = i10 + 1;
        totalEvents = i11;
        messagesDidLoad = i10;
        int i12 = i11 + 1;
        totalEvents = i12;
        loadingMessagesFailed = i11;
        int i13 = i12 + 1;
        totalEvents = i13;
        messageReceivedByAck = i12;
        int i14 = i13 + 1;
        totalEvents = i14;
        messageReceivedByServer = i13;
        int i15 = i14 + 1;
        totalEvents = i15;
        messageSendError = i14;
        int i16 = i15 + 1;
        totalEvents = i16;
        contactsDidLoad = i15;
        int i17 = i16 + 1;
        totalEvents = i17;
        contactsImported = i16;
        int i18 = i17 + 1;
        totalEvents = i18;
        hasNewContactsToImport = i17;
        int i19 = i18 + 1;
        totalEvents = i19;
        chatDidCreated = i18;
        int i20 = i19 + 1;
        totalEvents = i20;
        chatDidFailCreate = i19;
        int i21 = i20 + 1;
        totalEvents = i21;
        chatInfoDidLoad = i20;
        int i22 = i21 + 1;
        totalEvents = i22;
        chatInfoCantLoad = i21;
        int i23 = i22 + 1;
        totalEvents = i23;
        mediaDidLoad = i22;
        int i24 = i23 + 1;
        totalEvents = i24;
        mediaCountDidLoad = i23;
        int i25 = i24 + 1;
        totalEvents = i25;
        mediaCountsDidLoad = i24;
        int i26 = i25 + 1;
        totalEvents = i26;
        encryptedChatUpdated = i25;
        int i27 = i26 + 1;
        totalEvents = i27;
        messagesReadEncrypted = i26;
        int i28 = i27 + 1;
        totalEvents = i28;
        encryptedChatCreated = i27;
        int i29 = i28 + 1;
        totalEvents = i29;
        dialogPhotosLoaded = i28;
        int i30 = i29 + 1;
        totalEvents = i30;
        reloadDialogPhotos = i29;
        int i31 = i30 + 1;
        totalEvents = i31;
        folderBecomeEmpty = i30;
        int i32 = i31 + 1;
        totalEvents = i32;
        removeAllMessagesFromDialog = i31;
        int i33 = i32 + 1;
        totalEvents = i33;
        notificationsSettingsUpdated = i32;
        int i34 = i33 + 1;
        totalEvents = i34;
        blockedUsersDidLoad = i33;
        int i35 = i34 + 1;
        totalEvents = i35;
        openedChatChanged = i34;
        int i36 = i35 + 1;
        totalEvents = i36;
        didCreatedNewDeleteTask = i35;
        int i37 = i36 + 1;
        totalEvents = i37;
        mainUserInfoChanged = i36;
        int i38 = i37 + 1;
        totalEvents = i38;
        privacyRulesUpdated = i37;
        int i39 = i38 + 1;
        totalEvents = i39;
        updateMessageMedia = i38;
        int i40 = i39 + 1;
        totalEvents = i40;
        replaceMessagesObjects = i39;
        int i41 = i40 + 1;
        totalEvents = i41;
        didSetPasscode = i40;
        int i42 = i41 + 1;
        totalEvents = i42;
        twoStepPasswordChanged = i41;
        int i43 = i42 + 1;
        totalEvents = i43;
        didSetOrRemoveTwoStepPassword = i42;
        int i44 = i43 + 1;
        totalEvents = i44;
        didRemoveTwoStepPassword = i43;
        int i45 = i44 + 1;
        totalEvents = i45;
        replyMessagesDidLoad = i44;
        int i46 = i45 + 1;
        totalEvents = i46;
        pinnedMessageDidLoad = i45;
        int i47 = i46 + 1;
        totalEvents = i47;
        newSessionReceived = i46;
        int i48 = i47 + 1;
        totalEvents = i48;
        didReceivedWebpages = i47;
        int i49 = i48 + 1;
        totalEvents = i49;
        didReceivedWebpagesInUpdates = i48;
        int i50 = i49 + 1;
        totalEvents = i50;
        stickersDidLoad = i49;
        int i51 = i50 + 1;
        totalEvents = i51;
        diceStickersDidLoad = i50;
        int i52 = i51 + 1;
        totalEvents = i52;
        featuredStickersDidLoad = i51;
        int i53 = i52 + 1;
        totalEvents = i53;
        groupStickersDidLoad = i52;
        int i54 = i53 + 1;
        totalEvents = i54;
        messagesReadContent = i53;
        int i55 = i54 + 1;
        totalEvents = i55;
        botInfoDidLoad = i54;
        int i56 = i55 + 1;
        totalEvents = i56;
        userInfoDidLoad = i55;
        int i57 = i56 + 1;
        totalEvents = i57;
        botKeyboardDidLoad = i56;
        int i58 = i57 + 1;
        totalEvents = i58;
        chatSearchResultsAvailable = i57;
        int i59 = i58 + 1;
        totalEvents = i59;
        chatSearchResultsLoading = i58;
        int i60 = i59 + 1;
        totalEvents = i60;
        musicDidLoad = i59;
        int i61 = i60 + 1;
        totalEvents = i61;
        moreMusicDidLoad = i60;
        int i62 = i61 + 1;
        totalEvents = i62;
        needShowAlert = i61;
        int i63 = i62 + 1;
        totalEvents = i63;
        needShowPlayServicesAlert = i62;
        int i64 = i63 + 1;
        totalEvents = i64;
        didUpdateMessagesViews = i63;
        int i65 = i64 + 1;
        totalEvents = i65;
        needReloadRecentDialogsSearch = i64;
        int i66 = i65 + 1;
        totalEvents = i66;
        peerSettingsDidLoad = i65;
        int i67 = i66 + 1;
        totalEvents = i67;
        wasUnableToFindCurrentLocation = i66;
        int i68 = i67 + 1;
        totalEvents = i68;
        reloadHints = i67;
        int i69 = i68 + 1;
        totalEvents = i69;
        reloadInlineHints = i68;
        int i70 = i69 + 1;
        totalEvents = i70;
        newDraftReceived = i69;
        int i71 = i70 + 1;
        totalEvents = i71;
        recentDocumentsDidLoad = i70;
        int i72 = i71 + 1;
        totalEvents = i72;
        needAddArchivedStickers = i71;
        int i73 = i72 + 1;
        totalEvents = i73;
        archivedStickersCountDidLoad = i72;
        int i74 = i73 + 1;
        totalEvents = i74;
        paymentFinished = i73;
        int i75 = i74 + 1;
        totalEvents = i75;
        channelRightsUpdated = i74;
        int i76 = i75 + 1;
        totalEvents = i76;
        openArticle = i75;
        int i77 = i76 + 1;
        totalEvents = i77;
        updateMentionsCount = i76;
        int i78 = i77 + 1;
        totalEvents = i78;
        didUpdatePollResults = i77;
        int i79 = i78 + 1;
        totalEvents = i79;
        chatOnlineCountDidLoad = i78;
        int i80 = i79 + 1;
        totalEvents = i80;
        videoLoadingStateChanged = i79;
        int i81 = i80 + 1;
        totalEvents = i81;
        newPeopleNearbyAvailable = i80;
        int i82 = i81 + 1;
        totalEvents = i82;
        stopAllHeavyOperations = i81;
        int i83 = i82 + 1;
        totalEvents = i83;
        startAllHeavyOperations = i82;
        int i84 = i83 + 1;
        totalEvents = i84;
        sendingMessagesChanged = i83;
        int i85 = i84 + 1;
        totalEvents = i85;
        didUpdateReactions = i84;
        int i86 = i85 + 1;
        totalEvents = i86;
        didVerifyMessagesStickers = i85;
        int i87 = i86 + 1;
        totalEvents = i87;
        scheduledMessagesUpdated = i86;
        int i88 = i87 + 1;
        totalEvents = i88;
        newSuggestionsAvailable = i87;
        int i89 = i88 + 1;
        totalEvents = i89;
        walletPendingTransactionsChanged = i88;
        int i90 = i89 + 1;
        totalEvents = i90;
        walletSyncProgressChanged = i89;
        int i91 = i90 + 1;
        totalEvents = i91;
        httpFileDidLoad = i90;
        int i92 = i91 + 1;
        totalEvents = i92;
        httpFileDidFailedLoad = i91;
        int i93 = i92 + 1;
        totalEvents = i93;
        didUpdateConnectionState = i92;
        int i94 = i93 + 1;
        totalEvents = i94;
        FileDidUpload = i93;
        int i95 = i94 + 1;
        totalEvents = i95;
        FileDidFailUpload = i94;
        int i96 = i95 + 1;
        totalEvents = i96;
        FileUploadProgressChanged = i95;
        int i97 = i96 + 1;
        totalEvents = i97;
        FileLoadProgressChanged = i96;
        int i98 = i97 + 1;
        totalEvents = i98;
        fileDidLoad = i97;
        int i99 = i98 + 1;
        totalEvents = i99;
        fileDidFailToLoad = i98;
        int i100 = i99 + 1;
        totalEvents = i100;
        filePreparingStarted = i99;
        int i101 = i100 + 1;
        totalEvents = i101;
        fileNewChunkAvailable = i100;
        int i102 = i101 + 1;
        totalEvents = i102;
        filePreparingFailed = i101;
        int i103 = i102 + 1;
        totalEvents = i103;
        dialogsUnreadCounterChanged = i102;
        int i104 = i103 + 1;
        totalEvents = i104;
        messagePlayingProgressDidChanged = i103;
        int i105 = i104 + 1;
        totalEvents = i105;
        messagePlayingDidReset = i104;
        int i106 = i105 + 1;
        totalEvents = i106;
        messagePlayingPlayStateChanged = i105;
        int i107 = i106 + 1;
        totalEvents = i107;
        messagePlayingDidStart = i106;
        int i108 = i107 + 1;
        totalEvents = i108;
        messagePlayingDidSeek = i107;
        int i109 = i108 + 1;
        totalEvents = i109;
        messagePlayingGoingToStop = i108;
        int i110 = i109 + 1;
        totalEvents = i110;
        recordProgressChanged = i109;
        int i111 = i110 + 1;
        totalEvents = i111;
        recordStarted = i110;
        int i112 = i111 + 1;
        totalEvents = i112;
        recordStartError = i111;
        int i113 = i112 + 1;
        totalEvents = i113;
        recordStopped = i112;
        int i114 = i113 + 1;
        totalEvents = i114;
        screenshotTook = i113;
        int i115 = i114 + 1;
        totalEvents = i115;
        albumsDidLoad = i114;
        int i116 = i115 + 1;
        totalEvents = i116;
        audioDidSent = i115;
        int i117 = i116 + 1;
        totalEvents = i117;
        audioRecordTooShort = i116;
        int i118 = i117 + 1;
        totalEvents = i118;
        audioRouteChanged = i117;
        int i119 = i118 + 1;
        totalEvents = i119;
        didStartedCall = i118;
        int i120 = i119 + 1;
        totalEvents = i120;
        didEndCall = i119;
        int i121 = i120 + 1;
        totalEvents = i121;
        closeInCallActivity = i120;
        int i122 = i121 + 1;
        totalEvents = i122;
        appDidLogout = i121;
        int i123 = i122 + 1;
        totalEvents = i123;
        configLoaded = i122;
        int i124 = i123 + 1;
        totalEvents = i124;
        needDeleteDialog = i123;
        int i125 = i124 + 1;
        totalEvents = i125;
        newEmojiSuggestionsAvailable = i124;
        int i126 = i125 + 1;
        totalEvents = i126;
        themeUploadedToServer = i125;
        int i127 = i126 + 1;
        totalEvents = i127;
        themeUploadError = i126;
        int i128 = i127 + 1;
        totalEvents = i128;
        dialogFiltersUpdated = i127;
        int i129 = i128 + 1;
        totalEvents = i129;
        filterSettingsUpdated = i128;
        int i130 = i129 + 1;
        totalEvents = i130;
        suggestedFiltersLoaded = i129;
        int i131 = i130 + 1;
        totalEvents = i131;
        pushMessagesUpdated = i130;
        int i132 = i131 + 1;
        totalEvents = i132;
        stopEncodingService = i131;
        int i133 = i132 + 1;
        totalEvents = i133;
        wallpapersDidLoad = i132;
        int i134 = i133 + 1;
        totalEvents = i134;
        wallpapersNeedReload = i133;
        int i135 = i134 + 1;
        totalEvents = i135;
        didReceiveSmsCode = i134;
        int i136 = i135 + 1;
        totalEvents = i136;
        didReceiveCall = i135;
        int i137 = i136 + 1;
        totalEvents = i137;
        emojiDidLoad = i136;
        int i138 = i137 + 1;
        totalEvents = i138;
        closeOtherAppActivities = i137;
        int i139 = i138 + 1;
        totalEvents = i139;
        cameraInitied = i138;
        int i140 = i139 + 1;
        totalEvents = i140;
        didReplacedPhotoInMemCache = i139;
        int i141 = i140 + 1;
        totalEvents = i141;
        didSetNewTheme = i140;
        int i142 = i141 + 1;
        totalEvents = i142;
        themeListUpdated = i141;
        int i143 = i142 + 1;
        totalEvents = i143;
        didApplyNewTheme = i142;
        int i144 = i143 + 1;
        totalEvents = i144;
        themeAccentListUpdated = i143;
        int i145 = i144 + 1;
        totalEvents = i145;
        needCheckSystemBarColors = i144;
        int i146 = i145 + 1;
        totalEvents = i146;
        needShareTheme = i145;
        int i147 = i146 + 1;
        totalEvents = i147;
        needSetDayNightTheme = i146;
        int i148 = i147 + 1;
        totalEvents = i148;
        goingToPreviewTheme = i147;
        int i149 = i148 + 1;
        totalEvents = i149;
        locationPermissionGranted = i148;
        int i150 = i149 + 1;
        totalEvents = i150;
        reloadInterface = i149;
        int i151 = i150 + 1;
        totalEvents = i151;
        suggestedLangpack = i150;
        int i152 = i151 + 1;
        totalEvents = i152;
        didSetNewWallpapper = i151;
        int i153 = i152 + 1;
        totalEvents = i153;
        proxySettingsChanged = i152;
        int i154 = i153 + 1;
        totalEvents = i154;
        proxyCheckDone = i153;
        int i155 = i154 + 1;
        totalEvents = i155;
        liveLocationsChanged = i154;
        int i156 = i155 + 1;
        totalEvents = i156;
        newLocationAvailable = i155;
        int i157 = i156 + 1;
        totalEvents = i157;
        liveLocationsCacheChanged = i156;
        int i158 = i157 + 1;
        totalEvents = i158;
        notificationsCountUpdated = i157;
        int i159 = i158 + 1;
        totalEvents = i159;
        playerDidStartPlaying = i158;
        int i160 = i159 + 1;
        totalEvents = i160;
        closeSearchByActiveAction = i159;
        int i161 = i160 + 1;
        totalEvents = i161;
        messagePlayingSpeedChanged = i160;
        int i162 = i161 + 1;
        totalEvents = i162;
        screenStateChanged = i161;
        totalEvents = i162 + 1;
        voipServiceCreated = i162;
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
                runDelayedNotifications();
            }
        }
    }

    public void runDelayedNotifications() {
        if (!this.delayedPosts.isEmpty()) {
            this.delayedPostsTmp.clear();
            this.delayedPostsTmp.addAll(this.delayedPosts);
            this.delayedPosts.clear();
            for (int i = 0; i < this.delayedPostsTmp.size(); i++) {
                DelayedPost delayedPost = this.delayedPostsTmp.get(i);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPostsTmp.clear();
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
        boolean z2 = i == startAllHeavyOperations || i == stopAllHeavyOperations || i == didReplacedPhotoInMemCache;
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
            if (!this.postponeCallbackList.isEmpty()) {
                for (int i2 = 0; i2 < this.postponeCallbackList.size(); i2++) {
                    if (this.postponeCallbackList.get(i2).needPostpone(i, this.currentAccount, objArr)) {
                        this.delayedPosts.add(new DelayedPost(i, objArr));
                        return;
                    }
                }
            }
            this.broadcasting++;
            ArrayList arrayList = this.observers.get(i);
            if (arrayList != null && !arrayList.isEmpty()) {
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    ((NotificationCenterDelegate) arrayList.get(i3)).didReceivedNotification(i, this.currentAccount, objArr);
                }
            }
            int i4 = this.broadcasting - 1;
            this.broadcasting = i4;
            if (i4 == 0) {
                if (this.removeAfterBroadcast.size() != 0) {
                    for (int i5 = 0; i5 < this.removeAfterBroadcast.size(); i5++) {
                        int keyAt = this.removeAfterBroadcast.keyAt(i5);
                        ArrayList arrayList2 = this.removeAfterBroadcast.get(keyAt);
                        for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                            removeObserver((NotificationCenterDelegate) arrayList2.get(i6), keyAt);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (int i7 = 0; i7 < this.addAfterBroadcast.size(); i7++) {
                        int keyAt2 = this.addAfterBroadcast.keyAt(i7);
                        ArrayList arrayList3 = this.addAfterBroadcast.get(keyAt2);
                        for (int i8 = 0; i8 < arrayList3.size(); i8++) {
                            addObserver((NotificationCenterDelegate) arrayList3.get(i8), keyAt2);
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

    public void addPostponeNotificationsCallback(PostponeNotificationCallback postponeNotificationCallback) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("PostponeNotificationsCallback allowed only from MAIN thread");
        } else if (!this.postponeCallbackList.contains(postponeNotificationCallback)) {
            this.postponeCallbackList.add(postponeNotificationCallback);
        }
    }

    public void removePostponeNotificationsCallback(PostponeNotificationCallback postponeNotificationCallback) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removePostponeNotificationsCallback allowed only from MAIN thread");
        } else if (this.postponeCallbackList.remove(postponeNotificationCallback)) {
            runDelayedNotifications();
        }
    }
}
