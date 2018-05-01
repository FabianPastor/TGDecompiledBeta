package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import java.util.ArrayList;

public class NotificationCenter
{
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
  private static volatile NotificationCenter globalInstance;
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
  
  static
  {
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
  
  public NotificationCenter(int paramInt)
  {
    this.currentAccount = paramInt;
  }
  
  /* Error */
  public static NotificationCenter getGlobalInstance()
  {
    // Byte code:
    //   0: getstatic 414	org/telegram/messenger/NotificationCenter:globalInstance	Lorg/telegram/messenger/NotificationCenter;
    //   3: astore_0
    //   4: aload_0
    //   5: astore_1
    //   6: aload_0
    //   7: ifnonnull +32 -> 39
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 414	org/telegram/messenger/NotificationCenter:globalInstance	Lorg/telegram/messenger/NotificationCenter;
    //   16: astore_0
    //   17: aload_0
    //   18: astore_1
    //   19: aload_0
    //   20: ifnonnull +16 -> 36
    //   23: new 2	org/telegram/messenger/NotificationCenter
    //   26: astore_1
    //   27: aload_1
    //   28: iconst_m1
    //   29: invokespecial 415	org/telegram/messenger/NotificationCenter:<init>	(I)V
    //   32: aload_1
    //   33: putstatic 414	org/telegram/messenger/NotificationCenter:globalInstance	Lorg/telegram/messenger/NotificationCenter;
    //   36: ldc 2
    //   38: monitorexit
    //   39: aload_1
    //   40: areturn
    //   41: astore_1
    //   42: ldc 2
    //   44: monitorexit
    //   45: aload_1
    //   46: athrow
    //   47: astore_1
    //   48: goto -6 -> 42
    // Local variable table:
    //   start	length	slot	name	signature
    //   3	17	0	localNotificationCenter1	NotificationCenter
    //   5	35	1	localNotificationCenter2	NotificationCenter
    //   41	5	1	localObject1	Object
    //   47	1	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   13	17	41	finally
    //   23	32	41	finally
    //   36	39	41	finally
    //   42	45	41	finally
    //   32	36	47	finally
  }
  
  public static NotificationCenter getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/NotificationCenter;
        ((NotificationCenter)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (NotificationCenter)localObject2;
    }
    finally {}
  }
  
  public void addObserver(Object paramObject, int paramInt)
  {
    if ((BuildVars.DEBUG_VERSION) && (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread())) {
      throw new RuntimeException("addObserver allowed only from MAIN thread");
    }
    Object localObject1;
    Object localObject2;
    if (this.broadcasting != 0)
    {
      localObject1 = (ArrayList)this.addAfterBroadcast.get(paramInt);
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = new ArrayList();
        this.addAfterBroadcast.put(paramInt, localObject2);
      }
      ((ArrayList)localObject2).add(paramObject);
    }
    for (;;)
    {
      return;
      localObject1 = (ArrayList)this.observers.get(paramInt);
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = this.observers;
        localObject2 = new ArrayList();
        ((SparseArray)localObject1).put(paramInt, localObject2);
      }
      if (!((ArrayList)localObject2).contains(paramObject)) {
        ((ArrayList)localObject2).add(paramObject);
      }
    }
  }
  
  public boolean isAnimationInProgress()
  {
    return this.animationInProgress;
  }
  
  public void postNotificationName(int paramInt, Object... paramVarArgs)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (this.allowedNotifications != null) {}
    for (int i = 0;; i++)
    {
      bool2 = bool1;
      if (i < this.allowedNotifications.length)
      {
        if (this.allowedNotifications[i] == paramInt) {
          bool2 = true;
        }
      }
      else
      {
        postNotificationNameInternal(paramInt, bool2, paramVarArgs);
        return;
      }
    }
  }
  
  public void postNotificationNameInternal(int paramInt, boolean paramBoolean, Object... paramVarArgs)
  {
    if ((BuildVars.DEBUG_VERSION) && (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread())) {
      throw new RuntimeException("postNotificationName allowed only from MAIN thread");
    }
    Object localObject;
    if ((!paramBoolean) && (this.animationInProgress))
    {
      localObject = new DelayedPost(paramInt, paramVarArgs, null);
      this.delayedPosts.add(localObject);
      if (BuildVars.LOGS_ENABLED) {
        FileLog.e("delay post notification " + paramInt + " with args count = " + paramVarArgs.length);
      }
    }
    for (;;)
    {
      return;
      this.broadcasting += 1;
      localObject = (ArrayList)this.observers.get(paramInt);
      int i;
      if ((localObject != null) && (!((ArrayList)localObject).isEmpty())) {
        for (i = 0; i < ((ArrayList)localObject).size(); i++) {
          ((NotificationCenterDelegate)((ArrayList)localObject).get(i)).didReceivedNotification(paramInt, this.currentAccount, paramVarArgs);
        }
      }
      this.broadcasting -= 1;
      if (this.broadcasting == 0)
      {
        int j;
        if (this.removeAfterBroadcast.size() != 0)
        {
          for (paramInt = 0; paramInt < this.removeAfterBroadcast.size(); paramInt++)
          {
            j = this.removeAfterBroadcast.keyAt(paramInt);
            paramVarArgs = (ArrayList)this.removeAfterBroadcast.get(j);
            for (i = 0; i < paramVarArgs.size(); i++) {
              removeObserver(paramVarArgs.get(i), j);
            }
          }
          this.removeAfterBroadcast.clear();
        }
        if (this.addAfterBroadcast.size() != 0)
        {
          for (paramInt = 0; paramInt < this.addAfterBroadcast.size(); paramInt++)
          {
            j = this.addAfterBroadcast.keyAt(paramInt);
            paramVarArgs = (ArrayList)this.addAfterBroadcast.get(j);
            for (i = 0; i < paramVarArgs.size(); i++) {
              addObserver(paramVarArgs.get(i), j);
            }
          }
          this.addAfterBroadcast.clear();
        }
      }
    }
  }
  
  public void removeObserver(Object paramObject, int paramInt)
  {
    if ((BuildVars.DEBUG_VERSION) && (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread())) {
      throw new RuntimeException("removeObserver allowed only from MAIN thread");
    }
    ArrayList localArrayList2;
    if (this.broadcasting != 0)
    {
      ArrayList localArrayList1 = (ArrayList)this.removeAfterBroadcast.get(paramInt);
      localArrayList2 = localArrayList1;
      if (localArrayList1 == null)
      {
        localArrayList2 = new ArrayList();
        this.removeAfterBroadcast.put(paramInt, localArrayList2);
      }
      localArrayList2.add(paramObject);
    }
    for (;;)
    {
      return;
      localArrayList2 = (ArrayList)this.observers.get(paramInt);
      if (localArrayList2 != null) {
        localArrayList2.remove(paramObject);
      }
    }
  }
  
  public void setAllowedNotificationsDutingAnimation(int[] paramArrayOfInt)
  {
    this.allowedNotifications = paramArrayOfInt;
  }
  
  public void setAnimationInProgress(boolean paramBoolean)
  {
    this.animationInProgress = paramBoolean;
    if ((!this.animationInProgress) && (!this.delayedPosts.isEmpty()))
    {
      for (int i = 0; i < this.delayedPosts.size(); i++)
      {
        DelayedPost localDelayedPost = (DelayedPost)this.delayedPosts.get(i);
        postNotificationNameInternal(localDelayedPost.id, true, localDelayedPost.args);
      }
      this.delayedPosts.clear();
    }
  }
  
  private class DelayedPost
  {
    private Object[] args;
    private int id;
    
    private DelayedPost(int paramInt, Object[] paramArrayOfObject)
    {
      this.id = paramInt;
      this.args = paramArrayOfObject;
    }
  }
  
  public static abstract interface NotificationCenterDelegate
  {
    public abstract void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NotificationCenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */