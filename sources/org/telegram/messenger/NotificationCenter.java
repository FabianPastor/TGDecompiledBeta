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
    public static final int didCreatedNewDeleteTask;
    public static final int didEndedCall;
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
    public static final int didUpdatePollResults;
    public static final int didUpdatedMessagesViews;
    public static final int emojiDidLoad;
    public static final int encryptedChatCreated;
    public static final int encryptedChatUpdated;
    public static final int featuredStickersDidLoad;
    public static final int fileDidFailedLoad;
    public static final int fileDidLoad;
    public static final int fileNewChunkAvailable;
    public static final int filePreparingFailed;
    public static final int filePreparingStarted;
    private static volatile NotificationCenter globalInstance;
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
    public static final int messagePlayingPlayStateChanged;
    public static final int messagePlayingProgressDidChanged;
    public static final int messageReceivedByAck;
    public static final int messageReceivedByServer;
    public static final int messageSendError;
    public static final int messagesDeleted;
    public static final int messagesDidLoad;
    public static final int messagesRead;
    public static final int messagesReadContent;
    public static final int messagesReadEncrypted;
    public static final int musicDidLoad;
    public static final int needDeleteDialog;
    public static final int needReloadArchivedStickers;
    public static final int needReloadRecentDialogsSearch;
    public static final int needSetDayNightTheme;
    public static final int needShowAlert;
    public static final int newDraftReceived;
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
    public static final int recentImagesDidLoad;
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
    public static final int screenshotTook;
    public static final int stickersDidLoad;
    public static final int stopEncodingService;
    public static final int suggestedLangpack;
    private static int totalEvents;
    public static final int updateInterfaces;
    public static final int updateMentionsCount;
    public static final int updateMessageMedia;
    public static final int userInfoDidLoad;
    public static final int wallpapersDidLoad;
    public static final int wasUnableToFindCurrentLocation;
    private SparseArray<ArrayList<Object>> addAfterBroadcast = new SparseArray();
    private int[] allowedNotifications;
    private boolean animationInProgress;
    private int broadcasting = 0;
    private int currentAccount;
    private ArrayList<DelayedPost> delayedPosts = new ArrayList(10);
    private SparseArray<ArrayList<Object>> observers = new SparseArray();
    private SparseArray<ArrayList<Object>> removeAfterBroadcast = new SparseArray();

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, int i2, Object... objArr);
    }

    private class DelayedPost {
        private Object[] args;
        private int id;

        private DelayedPost(int id, Object[] args) {
            this.id = id;
            this.args = args;
        }
    }

    static {
        totalEvents = 1;
        int i = totalEvents;
        totalEvents = i + 1;
        didReceiveNewMessages = i;
        i = totalEvents;
        totalEvents = i + 1;
        updateInterfaces = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogsNeedReload = i;
        i = totalEvents;
        totalEvents = i + 1;
        closeChats = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesDeleted = i;
        i = totalEvents;
        totalEvents = i + 1;
        historyCleared = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesRead = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageReceivedByAck = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageReceivedByServer = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageSendError = i;
        i = totalEvents;
        totalEvents = i + 1;
        contactsDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        contactsImported = i;
        i = totalEvents;
        totalEvents = i + 1;
        hasNewContactsToImport = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatDidCreated = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatDidFailCreate = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatInfoDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatInfoCantLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaCountDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaCountsDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        encryptedChatUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesReadEncrypted = i;
        i = totalEvents;
        totalEvents = i + 1;
        encryptedChatCreated = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogPhotosLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        removeAllMessagesFromDialog = i;
        i = totalEvents;
        totalEvents = i + 1;
        notificationsSettingsUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        blockedUsersDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        openedChatChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        didCreatedNewDeleteTask = i;
        i = totalEvents;
        totalEvents = i + 1;
        mainUserInfoChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        privacyRulesUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        updateMessageMedia = i;
        i = totalEvents;
        totalEvents = i + 1;
        recentImagesDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        replaceMessagesObjects = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetPasscode = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetTwoStepPassword = i;
        i = totalEvents;
        totalEvents = i + 1;
        didRemoveTwoStepPassword = i;
        i = totalEvents;
        totalEvents = i + 1;
        replyMessagesDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        pinnedMessageDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        newSessionReceived = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceivedWebpages = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceivedWebpagesInUpdates = i;
        i = totalEvents;
        totalEvents = i + 1;
        stickersDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        featuredStickersDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        groupStickersDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesReadContent = i;
        i = totalEvents;
        totalEvents = i + 1;
        botInfoDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        userInfoDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        botKeyboardDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatSearchResultsAvailable = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatSearchResultsLoading = i;
        i = totalEvents;
        totalEvents = i + 1;
        musicDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        needShowAlert = i;
        i = totalEvents;
        totalEvents = i + 1;
        didUpdatedMessagesViews = i;
        i = totalEvents;
        totalEvents = i + 1;
        needReloadRecentDialogsSearch = i;
        i = totalEvents;
        totalEvents = i + 1;
        peerSettingsDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        wasUnableToFindCurrentLocation = i;
        i = totalEvents;
        totalEvents = i + 1;
        reloadHints = i;
        i = totalEvents;
        totalEvents = i + 1;
        reloadInlineHints = i;
        i = totalEvents;
        totalEvents = i + 1;
        newDraftReceived = i;
        i = totalEvents;
        totalEvents = i + 1;
        recentDocumentsDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        needReloadArchivedStickers = i;
        i = totalEvents;
        totalEvents = i + 1;
        archivedStickersCountDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        paymentFinished = i;
        i = totalEvents;
        totalEvents = i + 1;
        channelRightsUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        openArticle = i;
        i = totalEvents;
        totalEvents = i + 1;
        updateMentionsCount = i;
        i = totalEvents;
        totalEvents = i + 1;
        didUpdatePollResults = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatOnlineCountDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        httpFileDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        httpFileDidFailedLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        didUpdateConnectionState = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidUpload = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidFailUpload = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileUploadProgressChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileLoadProgressChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        fileDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        fileDidFailedLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        filePreparingStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        fileNewChunkAvailable = i;
        i = totalEvents;
        totalEvents = i + 1;
        filePreparingFailed = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogsUnreadCounterChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagePlayingProgressDidChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagePlayingDidReset = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagePlayingPlayStateChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagePlayingDidStart = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagePlayingDidSeek = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordProgressChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordStartError = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordStopped = i;
        i = totalEvents;
        totalEvents = i + 1;
        screenshotTook = i;
        i = totalEvents;
        totalEvents = i + 1;
        albumsDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioDidSent = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioRecordTooShort = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioRouteChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        didStartedCall = i;
        i = totalEvents;
        totalEvents = i + 1;
        didEndedCall = i;
        i = totalEvents;
        totalEvents = i + 1;
        closeInCallActivity = i;
        i = totalEvents;
        totalEvents = i + 1;
        appDidLogout = i;
        i = totalEvents;
        totalEvents = i + 1;
        configLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        needDeleteDialog = i;
        i = totalEvents;
        totalEvents = i + 1;
        pushMessagesUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        stopEncodingService = i;
        i = totalEvents;
        totalEvents = i + 1;
        wallpapersDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceiveSmsCode = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceiveCall = i;
        i = totalEvents;
        totalEvents = i + 1;
        emojiDidLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        closeOtherAppActivities = i;
        i = totalEvents;
        totalEvents = i + 1;
        cameraInitied = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReplacedPhotoInMemCache = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetNewTheme = i;
        i = totalEvents;
        totalEvents = i + 1;
        needSetDayNightTheme = i;
        i = totalEvents;
        totalEvents = i + 1;
        locationPermissionGranted = i;
        i = totalEvents;
        totalEvents = i + 1;
        reloadInterface = i;
        i = totalEvents;
        totalEvents = i + 1;
        suggestedLangpack = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetNewWallpapper = i;
        i = totalEvents;
        totalEvents = i + 1;
        proxySettingsChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        proxyCheckDone = i;
        i = totalEvents;
        totalEvents = i + 1;
        liveLocationsChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        liveLocationsCacheChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        notificationsCountUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        playerDidStartPlaying = i;
        i = totalEvents;
        totalEvents = i + 1;
        closeSearchByActiveAction = i;
    }

    public static NotificationCenter getInstance(int num) {
        Throwable th;
        NotificationCenter localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        NotificationCenter[] notificationCenterArr = Instance;
                        NotificationCenter localInstance2 = new NotificationCenter(num);
                        try {
                            notificationCenterArr[num] = localInstance2;
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

    public static NotificationCenter getGlobalInstance() {
        Throwable th;
        NotificationCenter localInstance = globalInstance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                try {
                    localInstance = globalInstance;
                    if (localInstance == null) {
                        NotificationCenter localInstance2 = new NotificationCenter(-1);
                        try {
                            globalInstance = localInstance2;
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

    public NotificationCenter(int account) {
        this.currentAccount = account;
    }

    public void setAllowedNotificationsDutingAnimation(int[] notifications) {
        this.allowedNotifications = notifications;
    }

    public void setAnimationInProgress(boolean value) {
        this.animationInProgress = value;
        if (!this.animationInProgress && !this.delayedPosts.isEmpty()) {
            for (int a = 0; a < this.delayedPosts.size(); a++) {
                DelayedPost delayedPost = (DelayedPost) this.delayedPosts.get(a);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPosts.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = false;
        if (this.allowedNotifications != null) {
            for (int i : this.allowedNotifications) {
                if (i == id) {
                    allowDuringAnimation = true;
                    break;
                }
            }
        }
        postNotificationNameInternal(id, allowDuringAnimation, args);
    }

    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (allowDuringAnimation || !this.animationInProgress) {
            int a;
            this.broadcasting++;
            ArrayList<Object> objects = (ArrayList) this.observers.get(id);
            if (!(objects == null || objects.isEmpty())) {
                for (a = 0; a < objects.size(); a++) {
                    ((NotificationCenterDelegate) objects.get(a)).didReceivedNotification(id, this.currentAccount, args);
                }
            }
            this.broadcasting--;
            if (this.broadcasting == 0) {
                int key;
                ArrayList<Object> arrayList;
                int b;
                if (this.removeAfterBroadcast.size() != 0) {
                    for (a = 0; a < this.removeAfterBroadcast.size(); a++) {
                        key = this.removeAfterBroadcast.keyAt(a);
                        arrayList = (ArrayList) this.removeAfterBroadcast.get(key);
                        for (b = 0; b < arrayList.size(); b++) {
                            removeObserver(arrayList.get(b), key);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (a = 0; a < this.addAfterBroadcast.size(); a++) {
                        key = this.addAfterBroadcast.keyAt(a);
                        arrayList = (ArrayList) this.addAfterBroadcast.get(key);
                        for (b = 0; b < arrayList.size(); b++) {
                            addObserver(arrayList.get(b), key);
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

    public void addObserver(Object observer, int id) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList<Object> arrayList = (ArrayList) this.addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
        } else {
            ArrayList<Object> objects = (ArrayList) this.observers.get(id);
            if (objects == null) {
                SparseArray sparseArray = this.observers;
                objects = new ArrayList();
                sparseArray.put(id, objects);
            }
            if (!objects.contains(observer)) {
                objects.add(observer);
            }
        }
    }

    public void removeObserver(Object observer, int id) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList<Object> arrayList = (ArrayList) this.removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
        } else {
            ArrayList<Object> objects = (ArrayList) this.observers.get(id);
            if (objects != null) {
                objects.remove(observer);
            }
        }
    }

    public boolean hasObservers(int id) {
        return this.observers.indexOfKey(id) >= 0;
    }
}
