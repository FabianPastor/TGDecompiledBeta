package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NotificationCenter {
    private static volatile NotificationCenter[] Instance = new NotificationCenter[3];
    public static final int activeGroupCallsUpdated;
    public static final int albumsDidLoad;
    public static final int appDidLogout;
    public static final int appUpdateAvailable;
    public static final int applyGroupCallVisibleParticipants;
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
    public static final int dialogDeleted;
    public static final int dialogFiltersUpdated;
    public static final int dialogPhotosLoaded;
    public static final int dialogsNeedReload;
    public static final int dialogsUnreadCounterChanged;
    public static final int diceStickersDidLoad;
    public static final int didApplyNewTheme;
    public static final int didClearDatabase;
    public static final int didCreatedNewDeleteTask;
    public static final int didEndCall;
    public static final int didLoadChatAdmins;
    public static final int didLoadChatInviter;
    public static final int didLoadPinnedMessages;
    public static final int didLoadSendAsPeers;
    public static final int didLoadSponsoredMessages;
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
    public static final int emojiLoaded;
    public static final int emojiPreviewThemesChanged;
    public static final int encryptedChatCreated;
    public static final int encryptedChatUpdated;
    public static final int featuredStickersDidLoad;
    public static final int fileLoadFailed;
    public static final int fileLoadProgressChanged;
    public static final int fileLoaded;
    public static final int fileNewChunkAvailable;
    public static final int filePreparingFailed;
    public static final int filePreparingStarted;
    public static final int fileUploadFailed;
    public static final int fileUploadProgressChanged;
    public static final int fileUploaded;
    public static final int filterSettingsUpdated;
    public static final int folderBecomeEmpty;
    private static volatile NotificationCenter globalInstance;
    public static final int goingToPreviewTheme;
    public static final int groupCallScreencastStateChanged;
    public static final int groupCallSpeakingUsersUpdated;
    public static final int groupCallTypingsUpdated;
    public static final int groupCallUpdated;
    public static final int groupCallVisibilityChanged;
    public static final int groupStickersDidLoad;
    public static final int hasNewContactsToImport;
    public static final int historyCleared;
    public static final int historyImportProgressChanged;
    public static final int httpFileDidFailedLoad;
    public static final int httpFileDidLoad;
    public static final int invalidateMotionBackground;
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
    public static final int messagesDidLoadWithoutProcess;
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
    public static final int onDatabaseMigration;
    public static final int onEmojiInteractionsReceived;
    public static final int openArticle;
    public static final int openedChatChanged;
    public static final int paymentFinished;
    public static final int peerSettingsDidLoad;
    public static final int pinnedInfoDidLoad;
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
    public static final int showBulletin;
    public static final int startAllHeavyOperations;
    public static final int stickersDidLoad;
    public static final int stickersImportComplete;
    public static final int stickersImportProgressChanged;
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
    public static final int updateDefaultSendAsPeer;
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
    public static final int webRtcMicAmplitudeEvent;
    public static final int webRtcSpeakerAmplitudeEvent;
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();
    private final HashMap<Integer, AllowedNotifications> allowedNotifications = new HashMap<>();
    private int animationInProgressCount;
    private int animationInProgressPointer = 1;
    private int broadcasting = 0;
    private Runnable checkForExpiredNotifications;
    private int currentAccount;
    private int currentHeavyOperationFlags;
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);
    private ArrayList<DelayedPost> delayedPostsTmp = new ArrayList<>(10);
    private ArrayList<Runnable> delayedRunnables = new ArrayList<>(10);
    private ArrayList<Runnable> delayedRunnablesTmp = new ArrayList<>(10);
    HashSet<Integer> heavyOperationsCounter = new HashSet<>();
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
        totalEvents = 1;
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
        didLoadSponsoredMessages = i11;
        int i13 = i12 + 1;
        totalEvents = i13;
        didLoadSendAsPeers = i12;
        int i14 = i13 + 1;
        totalEvents = i14;
        updateDefaultSendAsPeer = i13;
        int i15 = i14 + 1;
        totalEvents = i15;
        messagesDidLoadWithoutProcess = i14;
        int i16 = i15 + 1;
        totalEvents = i16;
        loadingMessagesFailed = i15;
        int i17 = i16 + 1;
        totalEvents = i17;
        messageReceivedByAck = i16;
        int i18 = i17 + 1;
        totalEvents = i18;
        messageReceivedByServer = i17;
        int i19 = i18 + 1;
        totalEvents = i19;
        messageSendError = i18;
        int i20 = i19 + 1;
        totalEvents = i20;
        contactsDidLoad = i19;
        int i21 = i20 + 1;
        totalEvents = i21;
        contactsImported = i20;
        int i22 = i21 + 1;
        totalEvents = i22;
        hasNewContactsToImport = i21;
        int i23 = i22 + 1;
        totalEvents = i23;
        chatDidCreated = i22;
        int i24 = i23 + 1;
        totalEvents = i24;
        chatDidFailCreate = i23;
        int i25 = i24 + 1;
        totalEvents = i25;
        chatInfoDidLoad = i24;
        int i26 = i25 + 1;
        totalEvents = i26;
        chatInfoCantLoad = i25;
        int i27 = i26 + 1;
        totalEvents = i27;
        mediaDidLoad = i26;
        int i28 = i27 + 1;
        totalEvents = i28;
        mediaCountDidLoad = i27;
        int i29 = i28 + 1;
        totalEvents = i29;
        mediaCountsDidLoad = i28;
        int i30 = i29 + 1;
        totalEvents = i30;
        encryptedChatUpdated = i29;
        int i31 = i30 + 1;
        totalEvents = i31;
        messagesReadEncrypted = i30;
        int i32 = i31 + 1;
        totalEvents = i32;
        encryptedChatCreated = i31;
        int i33 = i32 + 1;
        totalEvents = i33;
        dialogPhotosLoaded = i32;
        int i34 = i33 + 1;
        totalEvents = i34;
        reloadDialogPhotos = i33;
        int i35 = i34 + 1;
        totalEvents = i35;
        folderBecomeEmpty = i34;
        int i36 = i35 + 1;
        totalEvents = i36;
        removeAllMessagesFromDialog = i35;
        int i37 = i36 + 1;
        totalEvents = i37;
        notificationsSettingsUpdated = i36;
        int i38 = i37 + 1;
        totalEvents = i38;
        blockedUsersDidLoad = i37;
        int i39 = i38 + 1;
        totalEvents = i39;
        openedChatChanged = i38;
        int i40 = i39 + 1;
        totalEvents = i40;
        didCreatedNewDeleteTask = i39;
        int i41 = i40 + 1;
        totalEvents = i41;
        mainUserInfoChanged = i40;
        int i42 = i41 + 1;
        totalEvents = i42;
        privacyRulesUpdated = i41;
        int i43 = i42 + 1;
        totalEvents = i43;
        updateMessageMedia = i42;
        int i44 = i43 + 1;
        totalEvents = i44;
        replaceMessagesObjects = i43;
        int i45 = i44 + 1;
        totalEvents = i45;
        didSetPasscode = i44;
        int i46 = i45 + 1;
        totalEvents = i46;
        twoStepPasswordChanged = i45;
        int i47 = i46 + 1;
        totalEvents = i47;
        didSetOrRemoveTwoStepPassword = i46;
        int i48 = i47 + 1;
        totalEvents = i48;
        didRemoveTwoStepPassword = i47;
        int i49 = i48 + 1;
        totalEvents = i49;
        replyMessagesDidLoad = i48;
        int i50 = i49 + 1;
        totalEvents = i50;
        didLoadPinnedMessages = i49;
        int i51 = i50 + 1;
        totalEvents = i51;
        newSessionReceived = i50;
        int i52 = i51 + 1;
        totalEvents = i52;
        didReceivedWebpages = i51;
        int i53 = i52 + 1;
        totalEvents = i53;
        didReceivedWebpagesInUpdates = i52;
        int i54 = i53 + 1;
        totalEvents = i54;
        stickersDidLoad = i53;
        int i55 = i54 + 1;
        totalEvents = i55;
        diceStickersDidLoad = i54;
        int i56 = i55 + 1;
        totalEvents = i56;
        featuredStickersDidLoad = i55;
        int i57 = i56 + 1;
        totalEvents = i57;
        groupStickersDidLoad = i56;
        int i58 = i57 + 1;
        totalEvents = i58;
        messagesReadContent = i57;
        int i59 = i58 + 1;
        totalEvents = i59;
        botInfoDidLoad = i58;
        int i60 = i59 + 1;
        totalEvents = i60;
        userInfoDidLoad = i59;
        int i61 = i60 + 1;
        totalEvents = i61;
        pinnedInfoDidLoad = i60;
        int i62 = i61 + 1;
        totalEvents = i62;
        botKeyboardDidLoad = i61;
        int i63 = i62 + 1;
        totalEvents = i63;
        chatSearchResultsAvailable = i62;
        int i64 = i63 + 1;
        totalEvents = i64;
        chatSearchResultsLoading = i63;
        int i65 = i64 + 1;
        totalEvents = i65;
        musicDidLoad = i64;
        int i66 = i65 + 1;
        totalEvents = i66;
        moreMusicDidLoad = i65;
        int i67 = i66 + 1;
        totalEvents = i67;
        needShowAlert = i66;
        int i68 = i67 + 1;
        totalEvents = i68;
        needShowPlayServicesAlert = i67;
        int i69 = i68 + 1;
        totalEvents = i69;
        didUpdateMessagesViews = i68;
        int i70 = i69 + 1;
        totalEvents = i70;
        needReloadRecentDialogsSearch = i69;
        int i71 = i70 + 1;
        totalEvents = i71;
        peerSettingsDidLoad = i70;
        int i72 = i71 + 1;
        totalEvents = i72;
        wasUnableToFindCurrentLocation = i71;
        int i73 = i72 + 1;
        totalEvents = i73;
        reloadHints = i72;
        int i74 = i73 + 1;
        totalEvents = i74;
        reloadInlineHints = i73;
        int i75 = i74 + 1;
        totalEvents = i75;
        newDraftReceived = i74;
        int i76 = i75 + 1;
        totalEvents = i76;
        recentDocumentsDidLoad = i75;
        int i77 = i76 + 1;
        totalEvents = i77;
        needAddArchivedStickers = i76;
        int i78 = i77 + 1;
        totalEvents = i78;
        archivedStickersCountDidLoad = i77;
        int i79 = i78 + 1;
        totalEvents = i79;
        paymentFinished = i78;
        int i80 = i79 + 1;
        totalEvents = i80;
        channelRightsUpdated = i79;
        int i81 = i80 + 1;
        totalEvents = i81;
        openArticle = i80;
        int i82 = i81 + 1;
        totalEvents = i82;
        updateMentionsCount = i81;
        int i83 = i82 + 1;
        totalEvents = i83;
        didUpdatePollResults = i82;
        int i84 = i83 + 1;
        totalEvents = i84;
        chatOnlineCountDidLoad = i83;
        int i85 = i84 + 1;
        totalEvents = i85;
        videoLoadingStateChanged = i84;
        int i86 = i85 + 1;
        totalEvents = i86;
        newPeopleNearbyAvailable = i85;
        int i87 = i86 + 1;
        totalEvents = i87;
        stopAllHeavyOperations = i86;
        int i88 = i87 + 1;
        totalEvents = i88;
        startAllHeavyOperations = i87;
        int i89 = i88 + 1;
        totalEvents = i89;
        sendingMessagesChanged = i88;
        int i90 = i89 + 1;
        totalEvents = i90;
        didUpdateReactions = i89;
        int i91 = i90 + 1;
        totalEvents = i91;
        didVerifyMessagesStickers = i90;
        int i92 = i91 + 1;
        totalEvents = i92;
        scheduledMessagesUpdated = i91;
        int i93 = i92 + 1;
        totalEvents = i93;
        newSuggestionsAvailable = i92;
        int i94 = i93 + 1;
        totalEvents = i94;
        didLoadChatInviter = i93;
        int i95 = i94 + 1;
        totalEvents = i95;
        didLoadChatAdmins = i94;
        int i96 = i95 + 1;
        totalEvents = i96;
        historyImportProgressChanged = i95;
        int i97 = i96 + 1;
        totalEvents = i97;
        stickersImportProgressChanged = i96;
        int i98 = i97 + 1;
        totalEvents = i98;
        stickersImportComplete = i97;
        int i99 = i98 + 1;
        totalEvents = i99;
        dialogDeleted = i98;
        int i100 = i99 + 1;
        totalEvents = i100;
        walletPendingTransactionsChanged = i99;
        int i101 = i100 + 1;
        totalEvents = i101;
        walletSyncProgressChanged = i100;
        int i102 = i101 + 1;
        totalEvents = i102;
        httpFileDidLoad = i101;
        int i103 = i102 + 1;
        totalEvents = i103;
        httpFileDidFailedLoad = i102;
        int i104 = i103 + 1;
        totalEvents = i104;
        didUpdateConnectionState = i103;
        int i105 = i104 + 1;
        totalEvents = i105;
        fileUploaded = i104;
        int i106 = i105 + 1;
        totalEvents = i106;
        fileUploadFailed = i105;
        int i107 = i106 + 1;
        totalEvents = i107;
        fileUploadProgressChanged = i106;
        int i108 = i107 + 1;
        totalEvents = i108;
        fileLoadProgressChanged = i107;
        int i109 = i108 + 1;
        totalEvents = i109;
        fileLoaded = i108;
        int i110 = i109 + 1;
        totalEvents = i110;
        fileLoadFailed = i109;
        int i111 = i110 + 1;
        totalEvents = i111;
        filePreparingStarted = i110;
        int i112 = i111 + 1;
        totalEvents = i112;
        fileNewChunkAvailable = i111;
        int i113 = i112 + 1;
        totalEvents = i113;
        filePreparingFailed = i112;
        int i114 = i113 + 1;
        totalEvents = i114;
        dialogsUnreadCounterChanged = i113;
        int i115 = i114 + 1;
        totalEvents = i115;
        messagePlayingProgressDidChanged = i114;
        int i116 = i115 + 1;
        totalEvents = i116;
        messagePlayingDidReset = i115;
        int i117 = i116 + 1;
        totalEvents = i117;
        messagePlayingPlayStateChanged = i116;
        int i118 = i117 + 1;
        totalEvents = i118;
        messagePlayingDidStart = i117;
        int i119 = i118 + 1;
        totalEvents = i119;
        messagePlayingDidSeek = i118;
        int i120 = i119 + 1;
        totalEvents = i120;
        messagePlayingGoingToStop = i119;
        int i121 = i120 + 1;
        totalEvents = i121;
        recordProgressChanged = i120;
        int i122 = i121 + 1;
        totalEvents = i122;
        recordStarted = i121;
        int i123 = i122 + 1;
        totalEvents = i123;
        recordStartError = i122;
        int i124 = i123 + 1;
        totalEvents = i124;
        recordStopped = i123;
        int i125 = i124 + 1;
        totalEvents = i125;
        screenshotTook = i124;
        int i126 = i125 + 1;
        totalEvents = i126;
        albumsDidLoad = i125;
        int i127 = i126 + 1;
        totalEvents = i127;
        audioDidSent = i126;
        int i128 = i127 + 1;
        totalEvents = i128;
        audioRecordTooShort = i127;
        int i129 = i128 + 1;
        totalEvents = i129;
        audioRouteChanged = i128;
        int i130 = i129 + 1;
        totalEvents = i130;
        didStartedCall = i129;
        int i131 = i130 + 1;
        totalEvents = i131;
        groupCallUpdated = i130;
        int i132 = i131 + 1;
        totalEvents = i132;
        groupCallSpeakingUsersUpdated = i131;
        int i133 = i132 + 1;
        totalEvents = i133;
        groupCallScreencastStateChanged = i132;
        int i134 = i133 + 1;
        totalEvents = i134;
        activeGroupCallsUpdated = i133;
        int i135 = i134 + 1;
        totalEvents = i135;
        applyGroupCallVisibleParticipants = i134;
        int i136 = i135 + 1;
        totalEvents = i136;
        groupCallTypingsUpdated = i135;
        int i137 = i136 + 1;
        totalEvents = i137;
        didEndCall = i136;
        int i138 = i137 + 1;
        totalEvents = i138;
        closeInCallActivity = i137;
        int i139 = i138 + 1;
        totalEvents = i139;
        groupCallVisibilityChanged = i138;
        int i140 = i139 + 1;
        totalEvents = i140;
        appDidLogout = i139;
        int i141 = i140 + 1;
        totalEvents = i141;
        configLoaded = i140;
        int i142 = i141 + 1;
        totalEvents = i142;
        needDeleteDialog = i141;
        int i143 = i142 + 1;
        totalEvents = i143;
        newEmojiSuggestionsAvailable = i142;
        int i144 = i143 + 1;
        totalEvents = i144;
        themeUploadedToServer = i143;
        int i145 = i144 + 1;
        totalEvents = i145;
        themeUploadError = i144;
        int i146 = i145 + 1;
        totalEvents = i146;
        dialogFiltersUpdated = i145;
        int i147 = i146 + 1;
        totalEvents = i147;
        filterSettingsUpdated = i146;
        int i148 = i147 + 1;
        totalEvents = i148;
        suggestedFiltersLoaded = i147;
        int i149 = i148 + 1;
        totalEvents = i149;
        pushMessagesUpdated = i148;
        int i150 = i149 + 1;
        totalEvents = i150;
        stopEncodingService = i149;
        int i151 = i150 + 1;
        totalEvents = i151;
        wallpapersDidLoad = i150;
        int i152 = i151 + 1;
        totalEvents = i152;
        wallpapersNeedReload = i151;
        int i153 = i152 + 1;
        totalEvents = i153;
        didReceiveSmsCode = i152;
        int i154 = i153 + 1;
        totalEvents = i154;
        didReceiveCall = i153;
        int i155 = i154 + 1;
        totalEvents = i155;
        emojiLoaded = i154;
        int i156 = i155 + 1;
        totalEvents = i156;
        invalidateMotionBackground = i155;
        int i157 = i156 + 1;
        totalEvents = i157;
        closeOtherAppActivities = i156;
        int i158 = i157 + 1;
        totalEvents = i158;
        cameraInitied = i157;
        int i159 = i158 + 1;
        totalEvents = i159;
        didReplacedPhotoInMemCache = i158;
        int i160 = i159 + 1;
        totalEvents = i160;
        didSetNewTheme = i159;
        int i161 = i160 + 1;
        totalEvents = i161;
        themeListUpdated = i160;
        int i162 = i161 + 1;
        totalEvents = i162;
        didApplyNewTheme = i161;
        int i163 = i162 + 1;
        totalEvents = i163;
        themeAccentListUpdated = i162;
        int i164 = i163 + 1;
        totalEvents = i164;
        needCheckSystemBarColors = i163;
        int i165 = i164 + 1;
        totalEvents = i165;
        needShareTheme = i164;
        int i166 = i165 + 1;
        totalEvents = i166;
        needSetDayNightTheme = i165;
        int i167 = i166 + 1;
        totalEvents = i167;
        goingToPreviewTheme = i166;
        int i168 = i167 + 1;
        totalEvents = i168;
        locationPermissionGranted = i167;
        int i169 = i168 + 1;
        totalEvents = i169;
        reloadInterface = i168;
        int i170 = i169 + 1;
        totalEvents = i170;
        suggestedLangpack = i169;
        int i171 = i170 + 1;
        totalEvents = i171;
        didSetNewWallpapper = i170;
        int i172 = i171 + 1;
        totalEvents = i172;
        proxySettingsChanged = i171;
        int i173 = i172 + 1;
        totalEvents = i173;
        proxyCheckDone = i172;
        int i174 = i173 + 1;
        totalEvents = i174;
        liveLocationsChanged = i173;
        int i175 = i174 + 1;
        totalEvents = i175;
        newLocationAvailable = i174;
        int i176 = i175 + 1;
        totalEvents = i176;
        liveLocationsCacheChanged = i175;
        int i177 = i176 + 1;
        totalEvents = i177;
        notificationsCountUpdated = i176;
        int i178 = i177 + 1;
        totalEvents = i178;
        playerDidStartPlaying = i177;
        int i179 = i178 + 1;
        totalEvents = i179;
        closeSearchByActiveAction = i178;
        int i180 = i179 + 1;
        totalEvents = i180;
        messagePlayingSpeedChanged = i179;
        int i181 = i180 + 1;
        totalEvents = i181;
        screenStateChanged = i180;
        int i182 = i181 + 1;
        totalEvents = i182;
        didClearDatabase = i181;
        int i183 = i182 + 1;
        totalEvents = i183;
        voipServiceCreated = i182;
        int i184 = i183 + 1;
        totalEvents = i184;
        webRtcMicAmplitudeEvent = i183;
        int i185 = i184 + 1;
        totalEvents = i185;
        webRtcSpeakerAmplitudeEvent = i184;
        int i186 = i185 + 1;
        totalEvents = i186;
        showBulletin = i185;
        int i187 = i186 + 1;
        totalEvents = i187;
        appUpdateAvailable = i186;
        int i188 = i187 + 1;
        totalEvents = i188;
        onDatabaseMigration = i187;
        int i189 = i188 + 1;
        totalEvents = i189;
        onEmojiInteractionsReceived = i188;
        totalEvents = i189 + 1;
        emojiPreviewThemesChanged = i189;
    }

    private static class DelayedPost {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public int id;

        private DelayedPost(int id2, Object[] args2) {
            this.id = id2;
            this.args = args2;
        }
    }

    public static NotificationCenter getInstance(int num) {
        NotificationCenter localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    NotificationCenter[] notificationCenterArr = Instance;
                    NotificationCenter notificationCenter = new NotificationCenter(num);
                    localInstance = notificationCenter;
                    notificationCenterArr[num] = notificationCenter;
                }
            }
        }
        return localInstance;
    }

    public static NotificationCenter getGlobalInstance() {
        NotificationCenter localInstance = globalInstance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = globalInstance;
                if (localInstance == null) {
                    NotificationCenter notificationCenter = new NotificationCenter(-1);
                    localInstance = notificationCenter;
                    globalInstance = notificationCenter;
                }
            }
        }
        return localInstance;
    }

    public NotificationCenter(int account) {
        this.currentAccount = account;
    }

    public int setAnimationInProgress(int oldIndex, int[] allowedNotifications2) {
        return setAnimationInProgress(oldIndex, allowedNotifications2, true);
    }

    public int setAnimationInProgress(int oldIndex, int[] allowedNotifications2, boolean stopHeavyOperations) {
        onAnimationFinish(oldIndex);
        if (this.heavyOperationsCounter.isEmpty() && stopHeavyOperations) {
            getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512);
        }
        this.animationInProgressCount++;
        int i = this.animationInProgressPointer + 1;
        this.animationInProgressPointer = i;
        if (stopHeavyOperations) {
            this.heavyOperationsCounter.add(Integer.valueOf(i));
        }
        AllowedNotifications notifications = new AllowedNotifications();
        notifications.allowedIds = allowedNotifications2;
        this.allowedNotifications.put(Integer.valueOf(this.animationInProgressPointer), notifications);
        if (this.checkForExpiredNotifications == null) {
            NotificationCenter$$ExternalSyntheticLambda1 notificationCenter$$ExternalSyntheticLambda1 = new NotificationCenter$$ExternalSyntheticLambda1(this);
            this.checkForExpiredNotifications = notificationCenter$$ExternalSyntheticLambda1;
            AndroidUtilities.runOnUIThread(notificationCenter$$ExternalSyntheticLambda1, 1017);
        }
        return this.animationInProgressPointer;
    }

    /* access modifiers changed from: private */
    public void checkForExpiredNotifications() {
        this.checkForExpiredNotifications = null;
        if (!this.allowedNotifications.isEmpty()) {
            long minTime = Long.MAX_VALUE;
            long currentTime = SystemClock.elapsedRealtime();
            ArrayList<Integer> expiredIndices = null;
            for (Map.Entry<Integer, AllowedNotifications> entry : this.allowedNotifications.entrySet()) {
                AllowedNotifications allowedNotification = entry.getValue();
                if (currentTime - allowedNotification.time > 1000) {
                    if (expiredIndices == null) {
                        expiredIndices = new ArrayList<>();
                    }
                    expiredIndices.add(entry.getKey());
                } else {
                    minTime = Math.min(allowedNotification.time, minTime);
                }
            }
            if (expiredIndices != null) {
                for (int i = 0; i < expiredIndices.size(); i++) {
                    onAnimationFinish(expiredIndices.get(i).intValue());
                }
            }
            if (minTime != Long.MAX_VALUE) {
                AndroidUtilities.runOnUIThread(new NotificationCenter$$ExternalSyntheticLambda0(this), Math.max(17, 1017 - (currentTime - minTime)));
            }
        }
    }

    /* renamed from: lambda$checkForExpiredNotifications$0$org-telegram-messenger-NotificationCenter  reason: not valid java name */
    public /* synthetic */ void m1094xb7818d86() {
        this.checkForExpiredNotifications = new NotificationCenter$$ExternalSyntheticLambda1(this);
    }

    public void updateAllowedNotifications(int transitionAnimationIndex, int[] allowedNotifications2) {
        AllowedNotifications notifications = this.allowedNotifications.get(Integer.valueOf(transitionAnimationIndex));
        if (notifications != null) {
            notifications.allowedIds = allowedNotifications2;
        }
    }

    public void onAnimationFinish(int index) {
        if (this.allowedNotifications.remove(Integer.valueOf(index)) != null) {
            this.animationInProgressCount--;
            if (!this.heavyOperationsCounter.isEmpty()) {
                this.heavyOperationsCounter.remove(Integer.valueOf(index));
                if (this.heavyOperationsCounter.isEmpty()) {
                    getGlobalInstance().postNotificationName(startAllHeavyOperations, 512);
                }
            }
            if (this.animationInProgressCount == 0) {
                runDelayedNotifications();
            }
        }
        if (this.checkForExpiredNotifications != null && this.allowedNotifications.isEmpty()) {
            AndroidUtilities.cancelRunOnUIThread(this.checkForExpiredNotifications);
            this.checkForExpiredNotifications = null;
        }
    }

    public void runDelayedNotifications() {
        if (!this.delayedPosts.isEmpty()) {
            this.delayedPostsTmp.clear();
            this.delayedPostsTmp.addAll(this.delayedPosts);
            this.delayedPosts.clear();
            for (int a = 0; a < this.delayedPostsTmp.size(); a++) {
                DelayedPost delayedPost = this.delayedPostsTmp.get(a);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPostsTmp.clear();
        }
        if (!this.delayedRunnables.isEmpty()) {
            this.delayedRunnablesTmp.clear();
            this.delayedRunnablesTmp.addAll(this.delayedRunnables);
            this.delayedRunnables.clear();
            for (int a2 = 0; a2 < this.delayedRunnablesTmp.size(); a2++) {
                this.delayedRunnablesTmp.get(a2).run();
            }
            this.delayedRunnablesTmp.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgressCount > 0;
    }

    public int getCurrentHeavyOperationFlags() {
        return this.currentHeavyOperationFlags;
    }

    public ArrayList<NotificationCenterDelegate> getObservers(int id) {
        return this.observers.get(id);
    }

    public void postNotificationName(int id, Object... args) {
        int i = id;
        Object[] objArr = args;
        boolean allowDuringAnimation = i == startAllHeavyOperations || i == stopAllHeavyOperations || i == didReplacedPhotoInMemCache || i == closeChats || i == invalidateMotionBackground;
        ArrayList<Integer> expiredIndices = null;
        if (!allowDuringAnimation && !this.allowedNotifications.isEmpty()) {
            int size = this.allowedNotifications.size();
            int allowedCount = 0;
            long currentTime = SystemClock.elapsedRealtime();
            for (Map.Entry<Integer, AllowedNotifications> entry : this.allowedNotifications.entrySet()) {
                AllowedNotifications allowedNotification = entry.getValue();
                if (currentTime - allowedNotification.time > 1000) {
                    if (expiredIndices == null) {
                        expiredIndices = new ArrayList<>();
                    }
                    expiredIndices.add(entry.getKey());
                }
                int[] allowed = allowedNotification.allowedIds;
                if (allowed == null) {
                    break;
                }
                int a = 0;
                while (true) {
                    if (a >= allowed.length) {
                        break;
                    } else if (allowed[a] == i) {
                        allowedCount++;
                        break;
                    } else {
                        a++;
                    }
                }
            }
            allowDuringAnimation = size == allowedCount;
        }
        if (i == startAllHeavyOperations) {
            this.currentHeavyOperationFlags &= ((Integer) objArr[0]).intValue() ^ -1;
        } else if (i == stopAllHeavyOperations) {
            this.currentHeavyOperationFlags |= ((Integer) objArr[0]).intValue();
        }
        postNotificationNameInternal(i, allowDuringAnimation, objArr);
        if (expiredIndices != null) {
            for (int i2 = 0; i2 < expiredIndices.size(); i2++) {
                onAnimationFinish(expiredIndices.get(i2).intValue());
            }
        }
    }

    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (allowDuringAnimation || !isAnimationInProgress()) {
            if (!this.postponeCallbackList.isEmpty()) {
                for (int i = 0; i < this.postponeCallbackList.size(); i++) {
                    if (this.postponeCallbackList.get(i).needPostpone(id, this.currentAccount, args)) {
                        this.delayedPosts.add(new DelayedPost(id, args));
                        return;
                    }
                }
            }
            this.broadcasting++;
            ArrayList<NotificationCenterDelegate> objects = this.observers.get(id);
            if (objects != null && !objects.isEmpty()) {
                for (int a = 0; a < objects.size(); a++) {
                    objects.get(a).didReceivedNotification(id, this.currentAccount, args);
                }
            }
            int i2 = this.broadcasting - 1;
            this.broadcasting = i2;
            if (i2 == 0) {
                if (this.removeAfterBroadcast.size() != 0) {
                    for (int a2 = 0; a2 < this.removeAfterBroadcast.size(); a2++) {
                        int key = this.removeAfterBroadcast.keyAt(a2);
                        ArrayList<NotificationCenterDelegate> arrayList = this.removeAfterBroadcast.get(key);
                        for (int b = 0; b < arrayList.size(); b++) {
                            removeObserver(arrayList.get(b), key);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (int a3 = 0; a3 < this.addAfterBroadcast.size(); a3++) {
                        int key2 = this.addAfterBroadcast.keyAt(a3);
                        ArrayList<NotificationCenterDelegate> arrayList2 = this.addAfterBroadcast.get(key2);
                        for (int b2 = 0; b2 < arrayList2.size(); b2++) {
                            addObserver(arrayList2.get(b2), key2);
                        }
                    }
                    this.addAfterBroadcast.clear();
                }
            }
        } else {
            this.delayedPosts.add(new DelayedPost(id, args));
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("delay post notification " + id + " with args count = " + args.length);
            }
        }
    }

    public void addObserver(NotificationCenterDelegate observer, int id) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = this.addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
        } else {
            ArrayList<NotificationCenterDelegate> objects = this.observers.get(id);
            if (objects == null) {
                SparseArray<ArrayList<NotificationCenterDelegate>> sparseArray = this.observers;
                ArrayList<NotificationCenterDelegate> arrayList2 = new ArrayList<>();
                objects = arrayList2;
                sparseArray.put(id, arrayList2);
            }
            if (!objects.contains(observer)) {
                objects.add(observer);
            }
        }
    }

    public void removeObserver(NotificationCenterDelegate observer, int id) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = this.removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
        } else {
            ArrayList<NotificationCenterDelegate> objects = this.observers.get(id);
            if (objects != null) {
                objects.remove(observer);
            }
        }
    }

    public boolean hasObservers(int id) {
        return this.observers.indexOfKey(id) >= 0;
    }

    public void addPostponeNotificationsCallback(PostponeNotificationCallback callback) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("PostponeNotificationsCallback allowed only from MAIN thread");
        } else if (!this.postponeCallbackList.contains(callback)) {
            this.postponeCallbackList.add(callback);
        }
    }

    public void removePostponeNotificationsCallback(PostponeNotificationCallback callback) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removePostponeNotificationsCallback allowed only from MAIN thread");
        } else if (this.postponeCallbackList.remove(callback)) {
            runDelayedNotifications();
        }
    }

    public void doOnIdle(Runnable runnable) {
        if (isAnimationInProgress()) {
            this.delayedRunnables.add(runnable);
        } else {
            runnable.run();
        }
    }

    public void removeDelayed(Runnable runnable) {
        this.delayedRunnables.remove(runnable);
    }

    private static class AllowedNotifications {
        int[] allowedIds;
        final long time;

        private AllowedNotifications() {
            this.time = SystemClock.elapsedRealtime();
        }
    }
}
