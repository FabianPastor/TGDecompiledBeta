package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    public static final int didDatabaseCleared;
    public static final int didEndCall;
    public static final int didLoadChatAdmins;
    public static final int didLoadChatInviter;
    public static final int didLoadPinnedMessages;
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
    private static volatile NotificationCenter globalInstance = null;
    public static final int goingToPreviewTheme;
    public static final int groupCallTypingsUpdated;
    public static final int groupCallUpdated;
    public static final int groupCallVisibilityChanged;
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
    private static int totalEvents = 1;
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
    public static final int webRtcMicAmplitudeEvent;
    public static final int webRtcSpeakerAmplitudeEvent;
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();
    private final HashMap<Integer, int[]> allowedNotifications = new HashMap<>();
    private int animationInProgressCount;
    private int animationInProgressPointer = 1;
    private int broadcasting = 0;
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
        messagesDidLoadWithoutProcess = i11;
        int i13 = i12 + 1;
        totalEvents = i13;
        loadingMessagesFailed = i12;
        int i14 = i13 + 1;
        totalEvents = i14;
        messageReceivedByAck = i13;
        int i15 = i14 + 1;
        totalEvents = i15;
        messageReceivedByServer = i14;
        int i16 = i15 + 1;
        totalEvents = i16;
        messageSendError = i15;
        int i17 = i16 + 1;
        totalEvents = i17;
        contactsDidLoad = i16;
        int i18 = i17 + 1;
        totalEvents = i18;
        contactsImported = i17;
        int i19 = i18 + 1;
        totalEvents = i19;
        hasNewContactsToImport = i18;
        int i20 = i19 + 1;
        totalEvents = i20;
        chatDidCreated = i19;
        int i21 = i20 + 1;
        totalEvents = i21;
        chatDidFailCreate = i20;
        int i22 = i21 + 1;
        totalEvents = i22;
        chatInfoDidLoad = i21;
        int i23 = i22 + 1;
        totalEvents = i23;
        chatInfoCantLoad = i22;
        int i24 = i23 + 1;
        totalEvents = i24;
        mediaDidLoad = i23;
        int i25 = i24 + 1;
        totalEvents = i25;
        mediaCountDidLoad = i24;
        int i26 = i25 + 1;
        totalEvents = i26;
        mediaCountsDidLoad = i25;
        int i27 = i26 + 1;
        totalEvents = i27;
        encryptedChatUpdated = i26;
        int i28 = i27 + 1;
        totalEvents = i28;
        messagesReadEncrypted = i27;
        int i29 = i28 + 1;
        totalEvents = i29;
        encryptedChatCreated = i28;
        int i30 = i29 + 1;
        totalEvents = i30;
        dialogPhotosLoaded = i29;
        int i31 = i30 + 1;
        totalEvents = i31;
        reloadDialogPhotos = i30;
        int i32 = i31 + 1;
        totalEvents = i32;
        folderBecomeEmpty = i31;
        int i33 = i32 + 1;
        totalEvents = i33;
        removeAllMessagesFromDialog = i32;
        int i34 = i33 + 1;
        totalEvents = i34;
        notificationsSettingsUpdated = i33;
        int i35 = i34 + 1;
        totalEvents = i35;
        blockedUsersDidLoad = i34;
        int i36 = i35 + 1;
        totalEvents = i36;
        openedChatChanged = i35;
        int i37 = i36 + 1;
        totalEvents = i37;
        didCreatedNewDeleteTask = i36;
        int i38 = i37 + 1;
        totalEvents = i38;
        mainUserInfoChanged = i37;
        int i39 = i38 + 1;
        totalEvents = i39;
        privacyRulesUpdated = i38;
        int i40 = i39 + 1;
        totalEvents = i40;
        updateMessageMedia = i39;
        int i41 = i40 + 1;
        totalEvents = i41;
        replaceMessagesObjects = i40;
        int i42 = i41 + 1;
        totalEvents = i42;
        didSetPasscode = i41;
        int i43 = i42 + 1;
        totalEvents = i43;
        twoStepPasswordChanged = i42;
        int i44 = i43 + 1;
        totalEvents = i44;
        didSetOrRemoveTwoStepPassword = i43;
        int i45 = i44 + 1;
        totalEvents = i45;
        didRemoveTwoStepPassword = i44;
        int i46 = i45 + 1;
        totalEvents = i46;
        replyMessagesDidLoad = i45;
        int i47 = i46 + 1;
        totalEvents = i47;
        didLoadPinnedMessages = i46;
        int i48 = i47 + 1;
        totalEvents = i48;
        newSessionReceived = i47;
        int i49 = i48 + 1;
        totalEvents = i49;
        didReceivedWebpages = i48;
        totalEvents = i49 + 1;
        didReceivedWebpagesInUpdates = i49;
        int i50 = totalEvents;
        totalEvents = i50 + 1;
        stickersDidLoad = i50;
        int i51 = totalEvents;
        totalEvents = i51 + 1;
        diceStickersDidLoad = i51;
        int i52 = totalEvents;
        totalEvents = i52 + 1;
        featuredStickersDidLoad = i52;
        int i53 = totalEvents;
        totalEvents = i53 + 1;
        groupStickersDidLoad = i53;
        int i54 = totalEvents;
        totalEvents = i54 + 1;
        messagesReadContent = i54;
        int i55 = totalEvents;
        totalEvents = i55 + 1;
        botInfoDidLoad = i55;
        int i56 = totalEvents;
        totalEvents = i56 + 1;
        userInfoDidLoad = i56;
        int i57 = totalEvents;
        totalEvents = i57 + 1;
        pinnedInfoDidLoad = i57;
        int i58 = totalEvents;
        totalEvents = i58 + 1;
        botKeyboardDidLoad = i58;
        int i59 = totalEvents;
        totalEvents = i59 + 1;
        chatSearchResultsAvailable = i59;
        int i60 = totalEvents;
        totalEvents = i60 + 1;
        chatSearchResultsLoading = i60;
        int i61 = totalEvents;
        totalEvents = i61 + 1;
        musicDidLoad = i61;
        int i62 = totalEvents;
        totalEvents = i62 + 1;
        moreMusicDidLoad = i62;
        int i63 = totalEvents;
        totalEvents = i63 + 1;
        needShowAlert = i63;
        int i64 = totalEvents;
        totalEvents = i64 + 1;
        needShowPlayServicesAlert = i64;
        int i65 = totalEvents;
        totalEvents = i65 + 1;
        didUpdateMessagesViews = i65;
        int i66 = totalEvents;
        totalEvents = i66 + 1;
        needReloadRecentDialogsSearch = i66;
        int i67 = totalEvents;
        totalEvents = i67 + 1;
        peerSettingsDidLoad = i67;
        int i68 = totalEvents;
        totalEvents = i68 + 1;
        wasUnableToFindCurrentLocation = i68;
        int i69 = totalEvents;
        totalEvents = i69 + 1;
        reloadHints = i69;
        int i70 = totalEvents;
        totalEvents = i70 + 1;
        reloadInlineHints = i70;
        int i71 = totalEvents;
        totalEvents = i71 + 1;
        newDraftReceived = i71;
        int i72 = totalEvents;
        totalEvents = i72 + 1;
        recentDocumentsDidLoad = i72;
        int i73 = totalEvents;
        totalEvents = i73 + 1;
        needAddArchivedStickers = i73;
        int i74 = totalEvents;
        totalEvents = i74 + 1;
        archivedStickersCountDidLoad = i74;
        int i75 = totalEvents;
        totalEvents = i75 + 1;
        paymentFinished = i75;
        int i76 = totalEvents;
        totalEvents = i76 + 1;
        channelRightsUpdated = i76;
        int i77 = totalEvents;
        totalEvents = i77 + 1;
        openArticle = i77;
        int i78 = totalEvents;
        totalEvents = i78 + 1;
        updateMentionsCount = i78;
        int i79 = totalEvents;
        totalEvents = i79 + 1;
        didUpdatePollResults = i79;
        int i80 = totalEvents;
        totalEvents = i80 + 1;
        chatOnlineCountDidLoad = i80;
        int i81 = totalEvents;
        totalEvents = i81 + 1;
        videoLoadingStateChanged = i81;
        int i82 = totalEvents;
        totalEvents = i82 + 1;
        newPeopleNearbyAvailable = i82;
        int i83 = totalEvents;
        totalEvents = i83 + 1;
        stopAllHeavyOperations = i83;
        int i84 = totalEvents;
        totalEvents = i84 + 1;
        startAllHeavyOperations = i84;
        int i85 = totalEvents;
        totalEvents = i85 + 1;
        sendingMessagesChanged = i85;
        int i86 = totalEvents;
        totalEvents = i86 + 1;
        didUpdateReactions = i86;
        int i87 = totalEvents;
        totalEvents = i87 + 1;
        didVerifyMessagesStickers = i87;
        int i88 = totalEvents;
        totalEvents = i88 + 1;
        scheduledMessagesUpdated = i88;
        int i89 = totalEvents;
        totalEvents = i89 + 1;
        newSuggestionsAvailable = i89;
        int i90 = totalEvents;
        totalEvents = i90 + 1;
        didLoadChatInviter = i90;
        int i91 = totalEvents;
        totalEvents = i91 + 1;
        didLoadChatAdmins = i91;
        int i92 = totalEvents;
        totalEvents = i92 + 1;
        walletPendingTransactionsChanged = i92;
        int i93 = totalEvents;
        totalEvents = i93 + 1;
        walletSyncProgressChanged = i93;
        int i94 = totalEvents;
        totalEvents = i94 + 1;
        httpFileDidLoad = i94;
        int i95 = totalEvents;
        totalEvents = i95 + 1;
        httpFileDidFailedLoad = i95;
        int i96 = totalEvents;
        totalEvents = i96 + 1;
        didUpdateConnectionState = i96;
        int i97 = totalEvents;
        totalEvents = i97 + 1;
        FileDidUpload = i97;
        int i98 = totalEvents;
        totalEvents = i98 + 1;
        FileDidFailUpload = i98;
        int i99 = totalEvents;
        totalEvents = i99 + 1;
        FileUploadProgressChanged = i99;
        int i100 = totalEvents;
        totalEvents = i100 + 1;
        FileLoadProgressChanged = i100;
        int i101 = totalEvents;
        totalEvents = i101 + 1;
        fileDidLoad = i101;
        int i102 = totalEvents;
        totalEvents = i102 + 1;
        fileDidFailToLoad = i102;
        int i103 = totalEvents;
        totalEvents = i103 + 1;
        filePreparingStarted = i103;
        int i104 = totalEvents;
        totalEvents = i104 + 1;
        fileNewChunkAvailable = i104;
        int i105 = totalEvents;
        totalEvents = i105 + 1;
        filePreparingFailed = i105;
        int i106 = totalEvents;
        totalEvents = i106 + 1;
        dialogsUnreadCounterChanged = i106;
        int i107 = totalEvents;
        totalEvents = i107 + 1;
        messagePlayingProgressDidChanged = i107;
        int i108 = totalEvents;
        totalEvents = i108 + 1;
        messagePlayingDidReset = i108;
        int i109 = totalEvents;
        totalEvents = i109 + 1;
        messagePlayingPlayStateChanged = i109;
        int i110 = totalEvents;
        totalEvents = i110 + 1;
        messagePlayingDidStart = i110;
        int i111 = totalEvents;
        totalEvents = i111 + 1;
        messagePlayingDidSeek = i111;
        int i112 = totalEvents;
        totalEvents = i112 + 1;
        messagePlayingGoingToStop = i112;
        int i113 = totalEvents;
        totalEvents = i113 + 1;
        recordProgressChanged = i113;
        int i114 = totalEvents;
        totalEvents = i114 + 1;
        recordStarted = i114;
        int i115 = totalEvents;
        totalEvents = i115 + 1;
        recordStartError = i115;
        int i116 = totalEvents;
        totalEvents = i116 + 1;
        recordStopped = i116;
        int i117 = totalEvents;
        totalEvents = i117 + 1;
        screenshotTook = i117;
        int i118 = totalEvents;
        totalEvents = i118 + 1;
        albumsDidLoad = i118;
        int i119 = totalEvents;
        totalEvents = i119 + 1;
        audioDidSent = i119;
        int i120 = totalEvents;
        totalEvents = i120 + 1;
        audioRecordTooShort = i120;
        int i121 = totalEvents;
        totalEvents = i121 + 1;
        audioRouteChanged = i121;
        int i122 = totalEvents;
        totalEvents = i122 + 1;
        didStartedCall = i122;
        int i123 = totalEvents;
        totalEvents = i123 + 1;
        groupCallUpdated = i123;
        int i124 = totalEvents;
        totalEvents = i124 + 1;
        groupCallTypingsUpdated = i124;
        int i125 = totalEvents;
        totalEvents = i125 + 1;
        didEndCall = i125;
        int i126 = totalEvents;
        totalEvents = i126 + 1;
        closeInCallActivity = i126;
        int i127 = totalEvents;
        totalEvents = i127 + 1;
        groupCallVisibilityChanged = i127;
        int i128 = totalEvents;
        totalEvents = i128 + 1;
        appDidLogout = i128;
        int i129 = totalEvents;
        totalEvents = i129 + 1;
        configLoaded = i129;
        int i130 = totalEvents;
        totalEvents = i130 + 1;
        needDeleteDialog = i130;
        int i131 = totalEvents;
        totalEvents = i131 + 1;
        newEmojiSuggestionsAvailable = i131;
        int i132 = totalEvents;
        totalEvents = i132 + 1;
        themeUploadedToServer = i132;
        int i133 = totalEvents;
        totalEvents = i133 + 1;
        themeUploadError = i133;
        int i134 = totalEvents;
        totalEvents = i134 + 1;
        dialogFiltersUpdated = i134;
        int i135 = totalEvents;
        totalEvents = i135 + 1;
        filterSettingsUpdated = i135;
        int i136 = totalEvents;
        totalEvents = i136 + 1;
        suggestedFiltersLoaded = i136;
        int i137 = totalEvents;
        totalEvents = i137 + 1;
        pushMessagesUpdated = i137;
        int i138 = totalEvents;
        totalEvents = i138 + 1;
        stopEncodingService = i138;
        int i139 = totalEvents;
        totalEvents = i139 + 1;
        wallpapersDidLoad = i139;
        int i140 = totalEvents;
        totalEvents = i140 + 1;
        wallpapersNeedReload = i140;
        int i141 = totalEvents;
        totalEvents = i141 + 1;
        didReceiveSmsCode = i141;
        int i142 = totalEvents;
        totalEvents = i142 + 1;
        didReceiveCall = i142;
        int i143 = totalEvents;
        totalEvents = i143 + 1;
        emojiDidLoad = i143;
        int i144 = totalEvents;
        totalEvents = i144 + 1;
        closeOtherAppActivities = i144;
        int i145 = totalEvents;
        totalEvents = i145 + 1;
        cameraInitied = i145;
        int i146 = totalEvents;
        totalEvents = i146 + 1;
        didReplacedPhotoInMemCache = i146;
        int i147 = totalEvents;
        totalEvents = i147 + 1;
        didSetNewTheme = i147;
        int i148 = totalEvents;
        totalEvents = i148 + 1;
        themeListUpdated = i148;
        int i149 = totalEvents;
        totalEvents = i149 + 1;
        didApplyNewTheme = i149;
        int i150 = totalEvents;
        totalEvents = i150 + 1;
        themeAccentListUpdated = i150;
        int i151 = totalEvents;
        totalEvents = i151 + 1;
        needCheckSystemBarColors = i151;
        int i152 = totalEvents;
        totalEvents = i152 + 1;
        needShareTheme = i152;
        int i153 = totalEvents;
        totalEvents = i153 + 1;
        needSetDayNightTheme = i153;
        int i154 = totalEvents;
        totalEvents = i154 + 1;
        goingToPreviewTheme = i154;
        int i155 = totalEvents;
        totalEvents = i155 + 1;
        locationPermissionGranted = i155;
        int i156 = totalEvents;
        totalEvents = i156 + 1;
        reloadInterface = i156;
        int i157 = totalEvents;
        totalEvents = i157 + 1;
        suggestedLangpack = i157;
        int i158 = totalEvents;
        totalEvents = i158 + 1;
        didSetNewWallpapper = i158;
        int i159 = totalEvents;
        totalEvents = i159 + 1;
        proxySettingsChanged = i159;
        int i160 = totalEvents;
        totalEvents = i160 + 1;
        proxyCheckDone = i160;
        int i161 = totalEvents;
        totalEvents = i161 + 1;
        liveLocationsChanged = i161;
        int i162 = totalEvents;
        totalEvents = i162 + 1;
        newLocationAvailable = i162;
        int i163 = totalEvents;
        totalEvents = i163 + 1;
        liveLocationsCacheChanged = i163;
        int i164 = totalEvents;
        totalEvents = i164 + 1;
        notificationsCountUpdated = i164;
        int i165 = totalEvents;
        totalEvents = i165 + 1;
        playerDidStartPlaying = i165;
        int i166 = totalEvents;
        totalEvents = i166 + 1;
        closeSearchByActiveAction = i166;
        int i167 = totalEvents;
        totalEvents = i167 + 1;
        messagePlayingSpeedChanged = i167;
        int i168 = totalEvents;
        totalEvents = i168 + 1;
        screenStateChanged = i168;
        int i169 = totalEvents;
        totalEvents = i169 + 1;
        didDatabaseCleared = i169;
        int i170 = totalEvents;
        totalEvents = i170 + 1;
        voipServiceCreated = i170;
        int i171 = totalEvents;
        totalEvents = i171 + 1;
        webRtcMicAmplitudeEvent = i171;
        int i172 = totalEvents;
        totalEvents = i172 + 1;
        webRtcSpeakerAmplitudeEvent = i172;
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
        return setAnimationInProgress(i, iArr, true);
    }

    public int setAnimationInProgress(int i, int[] iArr, boolean z) {
        onAnimationFinish(i);
        if (this.heavyOperationsCounter.isEmpty() && z) {
            getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512);
        }
        this.animationInProgressCount++;
        int i2 = this.animationInProgressPointer + 1;
        this.animationInProgressPointer = i2;
        if (z) {
            this.heavyOperationsCounter.add(Integer.valueOf(i2));
        }
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
            this.animationInProgressCount--;
            if (!this.heavyOperationsCounter.isEmpty()) {
                this.heavyOperationsCounter.remove(Integer.valueOf(i));
                if (this.heavyOperationsCounter.isEmpty()) {
                    getGlobalInstance().postNotificationName(startAllHeavyOperations, 512);
                }
            }
            if (this.animationInProgressCount == 0) {
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
        if (!this.delayedRunnables.isEmpty()) {
            this.delayedRunnablesTmp.clear();
            this.delayedRunnablesTmp.addAll(this.delayedRunnables);
            this.delayedRunnables.clear();
            for (int i2 = 0; i2 < this.delayedRunnablesTmp.size(); i2++) {
                this.delayedRunnablesTmp.get(i2).run();
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

    public void doOnIdle(Runnable runnable) {
        if (isAnimationInProgress()) {
            this.delayedRunnables.add(runnable);
        } else {
            runnable.run();
        }
    }
}
