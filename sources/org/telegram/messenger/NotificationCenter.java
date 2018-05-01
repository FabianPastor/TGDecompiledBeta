package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

public class NotificationCenter {
    public static final int FileDidFailUpload;
    public static final int FileDidFailedLoad;
    public static final int FileDidLoaded;
    public static final int FileDidUpload;
    public static final int FileLoadProgressChanged;
    public static final int FileNewChunkAvailable;
    public static final int FilePreparingFailed;
    public static final int FilePreparingStarted;
    public static final int FileUploadProgressChanged;
    private static volatile NotificationCenter[] Instance = new NotificationCenter[3];
    public static final int albumsDidLoaded;
    public static final int appDidLogout;
    public static final int archivedStickersCountDidLoaded;
    public static final int audioDidSent;
    public static final int audioRouteChanged;
    public static final int blockedUsersDidLoaded;
    public static final int botInfoDidLoaded;
    public static final int botKeyboardDidLoaded;
    public static final int cameraInitied;
    public static final int channelRightsUpdated;
    public static final int chatDidCreated;
    public static final int chatDidFailCreate;
    public static final int chatInfoCantLoad;
    public static final int chatInfoDidLoaded;
    public static final int chatSearchResultsAvailable;
    public static final int chatSearchResultsLoading;
    public static final int closeChats;
    public static final int closeInCallActivity;
    public static final int closeOtherAppActivities;
    public static final int contactsDidLoaded;
    public static final int contactsImported;
    public static final int dialogPhotosLoaded;
    public static final int dialogsNeedReload;
    public static final int didCreatedNewDeleteTask;
    public static final int didEndedCall;
    public static final int didLoadedPinnedMessage;
    public static final int didLoadedReplyMessages;
    public static final int didReceiveCall;
    public static final int didReceiveSmsCode;
    public static final int didReceivedNewMessages;
    public static final int didReceivedWebpages;
    public static final int didReceivedWebpagesInUpdates;
    public static final int didRemovedTwoStepPassword;
    public static final int didReplacedPhotoInMemCache;
    public static final int didSetNewTheme;
    public static final int didSetNewWallpapper;
    public static final int didSetPasscode;
    public static final int didSetTwoStepPassword;
    public static final int didStartedCall;
    public static final int didUpdatedConnectionState;
    public static final int didUpdatedMessagesViews;
    public static final int emojiDidLoaded;
    public static final int encryptedChatCreated;
    public static final int encryptedChatUpdated;
    public static final int featuredStickersDidLoaded;
    private static volatile NotificationCenter globalInstance = null;
    public static final int groupStickersDidLoaded;
    public static final int hasNewContactsToImport;
    public static final int historyCleared;
    public static final int httpFileDidFailedLoad;
    public static final int httpFileDidLoaded;
    public static final int liveLocationsCacheChanged;
    public static final int liveLocationsChanged;
    public static final int locationPermissionGranted;
    public static final int mainUserInfoChanged;
    public static final int mediaCountDidLoaded;
    public static final int mediaDidLoaded;
    public static final int messagePlayingDidReset;
    public static final int messagePlayingDidStarted;
    public static final int messagePlayingPlayStateChanged;
    public static final int messagePlayingProgressDidChanged;
    public static final int messageReceivedByAck;
    public static final int messageReceivedByServer;
    public static final int messageSendError;
    public static final int messageThumbGenerated;
    public static final int messagesDeleted;
    public static final int messagesDidLoaded;
    public static final int messagesRead;
    public static final int messagesReadContent;
    public static final int messagesReadEncrypted;
    public static final int musicDidLoaded;
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
    public static final int peerSettingsDidLoaded;
    public static final int playerDidStartPlaying;
    public static final int privacyRulesUpdated;
    public static final int proxySettingsChanged;
    public static final int pushMessagesUpdated;
    public static final int recentDocumentsDidLoaded;
    public static final int recentImagesDidLoaded;
    public static final int recordProgressChanged;
    public static final int recordStartError;
    public static final int recordStarted;
    public static final int recordStopped;
    public static final int reloadHints;
    public static final int reloadInlineHints;
    public static final int reloadInterface;
    public static final int removeAllMessagesFromDialog;
    public static final int replaceMessagesObjects;
    public static final int screenshotTook;
    public static final int stickersDidLoaded;
    public static final int stopEncodingService;
    public static final int suggestedLangpack;
    private static int totalEvents = 1;
    public static final int updateInterfaces;
    public static final int updateMentionsCount;
    public static final int updateMessageMedia;
    public static final int userInfoDidLoaded;
    public static final int wallpapersDidLoaded;
    public static final int wasUnableToFindCurrentLocation;
    private SparseArray<ArrayList<Object>> addAfterBroadcast = new SparseArray();
    private int[] allowedNotifications;
    private boolean animationInProgress;
    private int broadcasting = 0;
    private int currentAccount;
    private ArrayList<DelayedPost> delayedPosts = new ArrayList(10);
    private SparseArray<ArrayList<Object>> observers = new SparseArray();
    private SparseArray<ArrayList<Object>> removeAfterBroadcast = new SparseArray();

    private class DelayedPost {
        private Object[] args;
        private int id;

        private DelayedPost(int i, Object[] objArr) {
            this.id = i;
            this.args = objArr;
        }
    }

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, int i2, Object... objArr);
    }

    static {
        int i = totalEvents;
        totalEvents = i + 1;
        didReceivedNewMessages = i;
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
        messagesDidLoaded = i;
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
        contactsDidLoaded = i;
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
        chatInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatInfoCantLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaCountDidLoaded = i;
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
        blockedUsersDidLoaded = i;
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
        recentImagesDidLoaded = i;
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
        didRemovedTwoStepPassword = i;
        i = totalEvents;
        totalEvents = i + 1;
        didLoadedReplyMessages = i;
        i = totalEvents;
        totalEvents = i + 1;
        didLoadedPinnedMessage = i;
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
        stickersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        featuredStickersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        groupStickersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesReadContent = i;
        i = totalEvents;
        totalEvents = i + 1;
        botInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        userInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        botKeyboardDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatSearchResultsAvailable = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatSearchResultsLoading = i;
        i = totalEvents;
        totalEvents = i + 1;
        musicDidLoaded = i;
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
        peerSettingsDidLoaded = i;
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
        recentDocumentsDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        needReloadArchivedStickers = i;
        i = totalEvents;
        totalEvents = i + 1;
        archivedStickersCountDidLoaded = i;
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
        httpFileDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        httpFileDidFailedLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        didUpdatedConnectionState = i;
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
        FileDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidFailedLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        FilePreparingStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileNewChunkAvailable = i;
        i = totalEvents;
        totalEvents = i + 1;
        FilePreparingFailed = i;
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
        messagePlayingDidStarted = i;
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
        albumsDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioDidSent = i;
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
        pushMessagesUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        stopEncodingService = i;
        i = totalEvents;
        totalEvents = i + 1;
        wallpapersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceiveSmsCode = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceiveCall = i;
        i = totalEvents;
        totalEvents = i + 1;
        emojiDidLoaded = i;
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
        messageThumbGenerated = i;
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
        this.animationInProgress = z;
        if (!this.animationInProgress && !this.delayedPosts.isEmpty()) {
            for (z = false; z < this.delayedPosts.size(); z++) {
                DelayedPost delayedPost = (DelayedPost) this.delayedPosts.get(z);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPosts.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    public void postNotificationName(int i, Object... objArr) {
        boolean z = false;
        if (this.allowedNotifications != null) {
            for (int i2 : this.allowedNotifications) {
                if (i2 == i) {
                    z = true;
                    break;
                }
            }
        }
        postNotificationNameInternal(i, z, objArr);
    }

    public void postNotificationNameInternal(int i, boolean z, Object... objArr) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (z || !this.animationInProgress) {
            int i2;
            this.broadcasting++;
            ArrayList arrayList = (ArrayList) this.observers.get(i);
            if (!(arrayList == null || arrayList.isEmpty())) {
                for (i2 = 0; i2 < arrayList.size(); i2++) {
                    ((NotificationCenterDelegate) arrayList.get(i2)).didReceivedNotification(i, this.currentAccount, objArr);
                }
            }
            this.broadcasting--;
            if (this.broadcasting == 0) {
                boolean z2;
                ArrayList arrayList2;
                if (this.removeAfterBroadcast.size() != 0) {
                    for (z2 = false; z2 < this.removeAfterBroadcast.size(); z2++) {
                        z = this.removeAfterBroadcast.keyAt(z2);
                        arrayList2 = (ArrayList) this.removeAfterBroadcast.get(z);
                        for (i2 = 0; i2 < arrayList2.size(); i2++) {
                            removeObserver(arrayList2.get(i2), z);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (z2 = false; z2 < this.addAfterBroadcast.size(); z2++) {
                        z = this.addAfterBroadcast.keyAt(z2);
                        arrayList2 = (ArrayList) this.addAfterBroadcast.get(z);
                        for (i2 = 0; i2 < arrayList2.size(); i2++) {
                            addObserver(arrayList2.get(i2), z);
                        }
                    }
                    this.addAfterBroadcast.clear();
                }
            }
        } else {
            this.delayedPosts.add(new DelayedPost(i, objArr));
            if (BuildVars.LOGS_ENABLED) {
                z = new StringBuilder();
                z.append("delay post notification ");
                z.append(i);
                z.append(" with args count = ");
                z.append(objArr.length);
                FileLog.m1e(z.toString());
            }
        }
    }

    public void addObserver(Object obj, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            r0 = (ArrayList) this.addAfterBroadcast.get(i);
            if (r0 == null) {
                r0 = new ArrayList();
                this.addAfterBroadcast.put(i, r0);
            }
            r0.add(obj);
        } else {
            r0 = (ArrayList) this.observers.get(i);
            if (r0 == null) {
                SparseArray sparseArray = this.observers;
                ArrayList arrayList = new ArrayList();
                sparseArray.put(i, arrayList);
                r0 = arrayList;
            }
            if (r0.contains(obj) == 0) {
                r0.add(obj);
            }
        }
    }

    public void removeObserver(Object obj, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList arrayList = (ArrayList) this.removeAfterBroadcast.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.removeAfterBroadcast.put(i, arrayList);
            }
            arrayList.add(obj);
        } else {
            ArrayList arrayList2 = (ArrayList) this.observers.get(i);
            if (arrayList2 != null) {
                arrayList2.remove(obj);
            }
        }
    }
}
