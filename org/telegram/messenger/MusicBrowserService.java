package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.MediaDescription.Builder;
import android.media.MediaMetadata.Builder;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSession.QueueItem;
import android.media.session.PlaybackState.Builder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.service.media.MediaBrowserService.BrowserRoot;
import android.service.media.MediaBrowserService.Result;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.LaunchActivity;

@TargetApi(21)
public class MusicBrowserService
  extends MediaBrowserService
  implements NotificationCenter.NotificationCenterDelegate
{
  public static final String ACTION_CMD = "com.example.android.mediabrowserservice.ACTION_CMD";
  public static final String CMD_NAME = "CMD_NAME";
  public static final String CMD_PAUSE = "CMD_PAUSE";
  private static final String MEDIA_ID_ROOT = "__ROOT__";
  private static final String SLOT_RESERVATION_QUEUE = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE";
  private static final String SLOT_RESERVATION_SKIP_TO_NEXT = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
  private static final String SLOT_RESERVATION_SKIP_TO_PREV = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";
  private static final int STOP_DELAY = 30000;
  private RectF bitmapRect;
  private SparseArray<TLRPC.Chat> chats = new SparseArray();
  private boolean chatsLoaded;
  private int currentAccount = UserConfig.selectedAccount;
  private DelayedStopHandler delayedStopHandler = new DelayedStopHandler(this, null);
  private ArrayList<Integer> dialogs = new ArrayList();
  private int lastSelectedDialog;
  private boolean loadingChats;
  private MediaSession mediaSession;
  private SparseArray<ArrayList<MessageObject>> musicObjects = new SparseArray();
  private SparseArray<ArrayList<MediaSession.QueueItem>> musicQueues = new SparseArray();
  private Paint roundPaint;
  private boolean serviceStarted;
  private SparseArray<TLRPC.User> users = new SparseArray();
  
  private Bitmap createRoundBitmap(File paramFile)
  {
    try
    {
      Object localObject1 = new android/graphics/BitmapFactory$Options;
      ((BitmapFactory.Options)localObject1).<init>();
      ((BitmapFactory.Options)localObject1).inSampleSize = 2;
      Bitmap localBitmap = BitmapFactory.decodeFile(paramFile.toString(), (BitmapFactory.Options)localObject1);
      if (localBitmap == null) {
        break label170;
      }
      paramFile = Bitmap.createBitmap(localBitmap.getWidth(), localBitmap.getHeight(), Bitmap.Config.ARGB_8888);
      paramFile.eraseColor(0);
      Canvas localCanvas = new android/graphics/Canvas;
      localCanvas.<init>(paramFile);
      localObject1 = new android/graphics/BitmapShader;
      ((BitmapShader)localObject1).<init>(localBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      if (this.roundPaint == null)
      {
        Object localObject2 = new android/graphics/Paint;
        ((Paint)localObject2).<init>(1);
        this.roundPaint = ((Paint)localObject2);
        localObject2 = new android/graphics/RectF;
        ((RectF)localObject2).<init>();
        this.bitmapRect = ((RectF)localObject2);
      }
      this.roundPaint.setShader((Shader)localObject1);
      this.bitmapRect.set(0.0F, 0.0F, localBitmap.getWidth(), localBitmap.getHeight());
      localCanvas.drawRoundRect(this.bitmapRect, localBitmap.getWidth(), localBitmap.getHeight(), this.roundPaint);
    }
    catch (Throwable paramFile)
    {
      for (;;)
      {
        FileLog.e(paramFile);
        label170:
        paramFile = null;
      }
    }
    return paramFile;
  }
  
  private long getAvailableActions()
  {
    long l1 = 3076L;
    long l2 = l1;
    if (MediaController.getInstance().getPlayingMessageObject() != null)
    {
      if (!MediaController.getInstance().isMessagePaused()) {
        l1 = 0xC04 | 0x2;
      }
      l2 = l1 | 0x10 | 0x20;
    }
    return l2;
  }
  
  private void handlePauseRequest()
  {
    MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000L);
  }
  
  private void handlePlayRequest()
  {
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    if (!this.serviceStarted) {}
    try
    {
      Intent localIntent = new android/content/Intent;
      localIntent.<init>(getApplicationContext(), MusicBrowserService.class);
      startService(localIntent);
      this.serviceStarted = true;
      if (!this.mediaSession.isActive()) {
        this.mediaSession.setActive(true);
      }
      localObject = MediaController.getInstance().getPlayingMessageObject();
      if (localObject == null) {
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        FileLog.e(localThrowable);
        continue;
        MediaMetadata.Builder localBuilder = new MediaMetadata.Builder();
        localBuilder.putLong("android.media.metadata.DURATION", ((MessageObject)localObject).getDuration() * 1000);
        localBuilder.putString("android.media.metadata.ARTIST", ((MessageObject)localObject).getMusicAuthor());
        localBuilder.putString("android.media.metadata.TITLE", ((MessageObject)localObject).getMusicTitle());
        Object localObject = MediaController.getInstance().getAudioInfo();
        if (localObject != null)
        {
          localObject = ((AudioInfo)localObject).getCover();
          if (localObject != null) {
            localBuilder.putBitmap("android.media.metadata.ALBUM_ART", (Bitmap)localObject);
          }
        }
        this.mediaSession.setMetadata(localBuilder.build());
      }
    }
  }
  
  private void handleStopRequest(String paramString)
  {
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000L);
    updatePlaybackState(paramString);
    stopSelf();
    this.serviceStarted = false;
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
  }
  
  private void loadChildrenImpl(String paramString, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> paramResult)
  {
    ArrayList localArrayList = new ArrayList();
    int i;
    int j;
    Object localObject1;
    Object localObject2;
    if ("__ROOT__".equals(paramString))
    {
      i = 0;
      if (i < this.dialogs.size())
      {
        j = ((Integer)this.dialogs.get(i)).intValue();
        MediaDescription.Builder localBuilder = new MediaDescription.Builder().setMediaId("__CHAT_" + j);
        localObject1 = null;
        if (j > 0)
        {
          localObject2 = (TLRPC.User)this.users.get(j);
          if (localObject2 != null)
          {
            localBuilder.setTitle(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
            paramString = (String)localObject1;
            if (((TLRPC.User)localObject2).photo != null)
            {
              paramString = (String)localObject1;
              if ((((TLRPC.User)localObject2).photo.photo_small instanceof TLRPC.TL_fileLocation)) {
                paramString = ((TLRPC.User)localObject2).photo.photo_small;
              }
            }
          }
        }
        for (;;)
        {
          localObject1 = null;
          if (paramString != null)
          {
            localObject2 = createRoundBitmap(FileLoader.getPathToAttach(paramString, true));
            localObject1 = localObject2;
            if (localObject2 != null)
            {
              localBuilder.setIconBitmap((Bitmap)localObject2);
              localObject1 = localObject2;
            }
          }
          if ((paramString == null) || (localObject1 == null)) {
            localBuilder.setIconUri(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/drawable/contact_blue"));
          }
          localArrayList.add(new MediaBrowser.MediaItem(localBuilder.build(), 1));
          i++;
          break;
          localBuilder.setTitle("DELETED USER");
          paramString = (String)localObject1;
          continue;
          localObject2 = (TLRPC.Chat)this.chats.get(-j);
          if (localObject2 != null)
          {
            localBuilder.setTitle(((TLRPC.Chat)localObject2).title);
            paramString = (String)localObject1;
            if (((TLRPC.Chat)localObject2).photo != null)
            {
              paramString = (String)localObject1;
              if ((((TLRPC.Chat)localObject2).photo.photo_small instanceof TLRPC.TL_fileLocation)) {
                paramString = ((TLRPC.Chat)localObject2).photo.photo_small;
              }
            }
          }
          else
          {
            localBuilder.setTitle("DELETED CHAT");
            paramString = (String)localObject1;
          }
        }
      }
    }
    else if ((paramString != null) && (paramString.startsWith("__CHAT_")))
    {
      i = 0;
      try
      {
        j = Integer.parseInt(paramString.replace("__CHAT_", ""));
        i = j;
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          FileLog.e(paramString);
        }
      }
      paramString = (ArrayList)this.musicObjects.get(i);
      if (paramString != null) {
        for (j = 0; j < paramString.size(); j++)
        {
          localObject1 = (MessageObject)paramString.get(j);
          localObject2 = new MediaDescription.Builder().setMediaId(i + "_" + j);
          ((MediaDescription.Builder)localObject2).setTitle(((MessageObject)localObject1).getMusicTitle());
          ((MediaDescription.Builder)localObject2).setSubtitle(((MessageObject)localObject1).getMusicAuthor());
          localArrayList.add(new MediaBrowser.MediaItem(((MediaDescription.Builder)localObject2).build(), 2));
        }
      }
    }
    paramResult.sendResult(localArrayList);
  }
  
  private void updatePlaybackState(String paramString)
  {
    long l = -1L;
    MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
    if (localMessageObject != null) {
      l = localMessageObject.audioProgressSec * 1000;
    }
    PlaybackState.Builder localBuilder = new PlaybackState.Builder().setActions(getAvailableActions());
    int i;
    if (localMessageObject == null)
    {
      i = 1;
      if (paramString != null)
      {
        localBuilder.setErrorMessage(paramString);
        i = 7;
      }
      localBuilder.setState(i, l, 1.0F, SystemClock.elapsedRealtime());
      if (localMessageObject == null) {
        break label148;
      }
      localBuilder.setActiveQueueItemId(MediaController.getInstance().getPlayingMessageObjectNum());
    }
    for (;;)
    {
      this.mediaSession.setPlaybackState(localBuilder.build());
      return;
      if (MediaController.getInstance().isDownloadingCurrentMessage())
      {
        i = 6;
        break;
      }
      if (MediaController.getInstance().isMessagePaused()) {}
      for (i = 2;; i = 3) {
        break;
      }
      label148:
      localBuilder.setActiveQueueItemId(0L);
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    updatePlaybackState(null);
    handlePlayRequest();
  }
  
  public void onCreate()
  {
    super.onCreate();
    ApplicationLoader.postInitApplication();
    this.lastSelectedDialog = MessagesController.getNotificationsSettings(this.currentAccount).getInt("auto_lastSelectedDialog", 0);
    this.mediaSession = new MediaSession(this, "MusicService");
    setSessionToken(this.mediaSession.getSessionToken());
    this.mediaSession.setCallback(new MediaSessionCallback(null));
    this.mediaSession.setFlags(3);
    Object localObject = getApplicationContext();
    localObject = PendingIntent.getActivity((Context)localObject, 99, new Intent((Context)localObject, LaunchActivity.class), 134217728);
    this.mediaSession.setSessionActivity((PendingIntent)localObject);
    localObject = new Bundle();
    ((Bundle)localObject).putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE", true);
    ((Bundle)localObject).putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS", true);
    ((Bundle)localObject).putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT", true);
    this.mediaSession.setExtras((Bundle)localObject);
    updatePlaybackState(null);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
  }
  
  public void onDestroy()
  {
    handleStopRequest(null);
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    this.mediaSession.release();
  }
  
  public MediaBrowserService.BrowserRoot onGetRoot(String paramString, int paramInt, Bundle paramBundle)
  {
    if ((paramString == null) || ((1000 != paramInt) && (Process.myUid() != paramInt) && (!paramString.equals("com.google.android.mediasimulator")) && (!paramString.equals("com.google.android.projection.gearhead")))) {}
    for (paramString = null;; paramString = new MediaBrowserService.BrowserRoot("__ROOT__", null)) {
      return paramString;
    }
  }
  
  public void onLoadChildren(final String paramString, final MediaBrowserService.Result<List<MediaBrowser.MediaItem>> paramResult)
  {
    if (!this.chatsLoaded)
    {
      paramResult.detach();
      if (!this.loadingChats) {}
    }
    for (;;)
    {
      return;
      this.loadingChats = true;
      final MessagesStorage localMessagesStorage = MessagesStorage.getInstance(this.currentAccount);
      localMessagesStorage.getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ArrayList localArrayList1;
          ArrayList localArrayList2;
          int i;
          try
          {
            localArrayList1 = new java/util/ArrayList;
            localArrayList1.<init>();
            localArrayList2 = new java/util/ArrayList;
            localArrayList2.<init>();
            SQLiteCursor localSQLiteCursor1 = localMessagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v2 WHERE uid != 0 AND mid > 0 AND type = %d", new Object[] { Integer.valueOf(4) }), new Object[0]);
            for (;;)
            {
              if (localSQLiteCursor1.next())
              {
                i = (int)localSQLiteCursor1.longValue(0);
                if (i != 0)
                {
                  MusicBrowserService.this.dialogs.add(Integer.valueOf(i));
                  if (i > 0)
                  {
                    localArrayList1.add(Integer.valueOf(i));
                    continue;
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MusicBrowserService.access$802(MusicBrowserService.this, true);
                        MusicBrowserService.access$902(MusicBrowserService.this, false);
                        MusicBrowserService.this.loadChildrenImpl(MusicBrowserService.1.this.val$parentMediaId, MusicBrowserService.1.this.val$result);
                        if ((MusicBrowserService.this.lastSelectedDialog == 0) && (!MusicBrowserService.this.dialogs.isEmpty())) {
                          MusicBrowserService.access$1102(MusicBrowserService.this, ((Integer)MusicBrowserService.this.dialogs.get(0)).intValue());
                        }
                        Object localObject1;
                        Object localObject2;
                        if (MusicBrowserService.this.lastSelectedDialog != 0)
                        {
                          localObject1 = (ArrayList)MusicBrowserService.this.musicObjects.get(MusicBrowserService.this.lastSelectedDialog);
                          localObject2 = (ArrayList)MusicBrowserService.this.musicQueues.get(MusicBrowserService.this.lastSelectedDialog);
                          if ((localObject1 != null) && (!((ArrayList)localObject1).isEmpty()))
                          {
                            MusicBrowserService.this.mediaSession.setQueue((List)localObject2);
                            if (MusicBrowserService.this.lastSelectedDialog <= 0) {
                              break label370;
                            }
                            localObject2 = (TLRPC.User)MusicBrowserService.this.users.get(MusicBrowserService.this.lastSelectedDialog);
                            if (localObject2 == null) {
                              break label352;
                            }
                            MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
                          }
                        }
                        for (;;)
                        {
                          localObject2 = (MessageObject)((ArrayList)localObject1).get(0);
                          localObject1 = new MediaMetadata.Builder();
                          ((MediaMetadata.Builder)localObject1).putLong("android.media.metadata.DURATION", ((MessageObject)localObject2).getDuration() * 1000);
                          ((MediaMetadata.Builder)localObject1).putString("android.media.metadata.ARTIST", ((MessageObject)localObject2).getMusicAuthor());
                          ((MediaMetadata.Builder)localObject1).putString("android.media.metadata.TITLE", ((MessageObject)localObject2).getMusicTitle());
                          MusicBrowserService.this.mediaSession.setMetadata(((MediaMetadata.Builder)localObject1).build());
                          MusicBrowserService.this.updatePlaybackState(null);
                          return;
                          label352:
                          MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                          continue;
                          label370:
                          localObject2 = (TLRPC.Chat)MusicBrowserService.this.chats.get(-MusicBrowserService.this.lastSelectedDialog);
                          if (localObject2 != null) {
                            MusicBrowserService.this.mediaSession.setQueueTitle(((TLRPC.Chat)localObject2).title);
                          } else {
                            MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
                          }
                        }
                      }
                    });
                  }
                }
              }
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          for (;;)
          {
            return;
            i = -i;
            localArrayList2.add(Integer.valueOf(i));
            break;
            localException.dispose();
            if (!MusicBrowserService.this.dialogs.isEmpty())
            {
              Object localObject1 = TextUtils.join(",", MusicBrowserService.this.dialogs);
              SQLiteCursor localSQLiteCursor2 = localMessagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v2 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[] { localObject1, Integer.valueOf(4) }), new Object[0]);
              Object localObject4;
              while (localSQLiteCursor2.next())
              {
                localObject1 = localSQLiteCursor2.byteBufferValue(1);
                if (localObject1 != null)
                {
                  Object localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                  ((TLRPC.Message)localObject2).readAttachPath((AbstractSerializedData)localObject1, UserConfig.getInstance(MusicBrowserService.this.currentAccount).clientUserId);
                  ((NativeByteBuffer)localObject1).reuse();
                  if (MessageObject.isMusicMessage((TLRPC.Message)localObject2))
                  {
                    i = localSQLiteCursor2.intValue(0);
                    ((TLRPC.Message)localObject2).id = localSQLiteCursor2.intValue(2);
                    ((TLRPC.Message)localObject2).dialog_id = i;
                    Object localObject3 = (ArrayList)MusicBrowserService.this.musicObjects.get(i);
                    localObject1 = (ArrayList)MusicBrowserService.this.musicQueues.get(i);
                    localObject4 = localObject3;
                    if (localObject3 == null)
                    {
                      localObject4 = new java/util/ArrayList;
                      ((ArrayList)localObject4).<init>();
                      MusicBrowserService.this.musicObjects.put(i, localObject4);
                      localObject1 = new java/util/ArrayList;
                      ((ArrayList)localObject1).<init>();
                      MusicBrowserService.this.musicQueues.put(i, localObject1);
                    }
                    localObject3 = new org/telegram/messenger/MessageObject;
                    ((MessageObject)localObject3).<init>(MusicBrowserService.this.currentAccount, (TLRPC.Message)localObject2, false);
                    ((ArrayList)localObject4).add(0, localObject3);
                    MediaDescription.Builder localBuilder = new android/media/MediaDescription$Builder;
                    localBuilder.<init>();
                    localObject2 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject2).<init>();
                    localObject4 = localBuilder.setMediaId(i + "_" + ((ArrayList)localObject4).size());
                    ((MediaDescription.Builder)localObject4).setTitle(((MessageObject)localObject3).getMusicTitle());
                    ((MediaDescription.Builder)localObject4).setSubtitle(((MessageObject)localObject3).getMusicAuthor());
                    localObject3 = new android/media/session/MediaSession$QueueItem;
                    ((MediaSession.QueueItem)localObject3).<init>(((MediaDescription.Builder)localObject4).build(), ((ArrayList)localObject1).size());
                    ((ArrayList)localObject1).add(0, localObject3);
                  }
                }
              }
              localSQLiteCursor2.dispose();
              if (!localArrayList1.isEmpty())
              {
                localObject1 = new java/util/ArrayList;
                ((ArrayList)localObject1).<init>();
                localMessagesStorage.getUsersInternal(TextUtils.join(",", localArrayList1), (ArrayList)localObject1);
                for (i = 0; i < ((ArrayList)localObject1).size(); i++)
                {
                  localObject4 = (TLRPC.User)((ArrayList)localObject1).get(i);
                  MusicBrowserService.this.users.put(((TLRPC.User)localObject4).id, localObject4);
                }
              }
              if (!localArrayList2.isEmpty())
              {
                localObject1 = new java/util/ArrayList;
                ((ArrayList)localObject1).<init>();
                localMessagesStorage.getChatsInternal(TextUtils.join(",", localArrayList2), (ArrayList)localObject1);
                for (i = 0; i < ((ArrayList)localObject1).size(); i++)
                {
                  localObject4 = (TLRPC.Chat)((ArrayList)localObject1).get(i);
                  MusicBrowserService.this.chats.put(((TLRPC.Chat)localObject4).id, localObject4);
                }
              }
            }
          }
        }
      });
      continue;
      loadChildrenImpl(paramString, paramResult);
    }
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    return 1;
  }
  
  private static class DelayedStopHandler
    extends Handler
  {
    private final WeakReference<MusicBrowserService> mWeakReference;
    
    private DelayedStopHandler(MusicBrowserService paramMusicBrowserService)
    {
      this.mWeakReference = new WeakReference(paramMusicBrowserService);
    }
    
    public void handleMessage(Message paramMessage)
    {
      paramMessage = (MusicBrowserService)this.mWeakReference.get();
      if ((paramMessage == null) || ((MediaController.getInstance().getPlayingMessageObject() != null) && (!MediaController.getInstance().isMessagePaused()))) {}
      for (;;)
      {
        return;
        paramMessage.stopSelf();
        MusicBrowserService.access$1702(paramMessage, false);
      }
    }
  }
  
  private final class MediaSessionCallback
    extends MediaSession.Callback
  {
    private MediaSessionCallback() {}
    
    public void onPause()
    {
      MusicBrowserService.this.handlePauseRequest();
    }
    
    public void onPlay()
    {
      MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
      if (localMessageObject == null) {
        onPlayFromMediaId(MusicBrowserService.this.lastSelectedDialog + "_" + 0, null);
      }
      for (;;)
      {
        return;
        MediaController.getInstance().playMessage(localMessageObject);
      }
    }
    
    public void onPlayFromMediaId(String paramString, Bundle paramBundle)
    {
      paramString = paramString.split("_");
      if (paramString.length != 2) {
        return;
      }
      for (;;)
      {
        try
        {
          i = Integer.parseInt(paramString[0]);
          int j = Integer.parseInt(paramString[1]);
          paramString = (ArrayList)MusicBrowserService.this.musicObjects.get(i);
          paramBundle = (ArrayList)MusicBrowserService.this.musicQueues.get(i);
          if ((paramString == null) || (j < 0) || (j >= paramString.size())) {
            break;
          }
          MusicBrowserService.access$1102(MusicBrowserService.this, i);
          MessagesController.getNotificationsSettings(MusicBrowserService.this.currentAccount).edit().putInt("auto_lastSelectedDialog", i).commit();
          MediaController.getInstance().setPlaylist(paramString, (MessageObject)paramString.get(j), false);
          MusicBrowserService.this.mediaSession.setQueue(paramBundle);
          if (i <= 0) {
            continue;
          }
          paramString = (TLRPC.User)MusicBrowserService.this.users.get(i);
          if (paramString == null) {
            continue;
          }
          MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(paramString.first_name, paramString.last_name));
        }
        catch (Exception paramString)
        {
          int i;
          FileLog.e(paramString);
          continue;
          paramString = (TLRPC.Chat)MusicBrowserService.this.chats.get(-i);
          if (paramString == null) {
            continue;
          }
          MusicBrowserService.this.mediaSession.setQueueTitle(paramString.title);
          continue;
          MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
          continue;
        }
        MusicBrowserService.this.handlePlayRequest();
        break;
        MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
      }
    }
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle)
    {
      if ((paramString == null) || (paramString.length() == 0)) {}
      for (;;)
      {
        return;
        paramString = paramString.toLowerCase();
        int i = 0;
        label19:
        int j;
        if (i < MusicBrowserService.this.dialogs.size())
        {
          j = ((Integer)MusicBrowserService.this.dialogs.get(i)).intValue();
          if (j <= 0) {
            break label151;
          }
          paramBundle = (TLRPC.User)MusicBrowserService.this.users.get(j);
          if (paramBundle != null) {
            break label83;
          }
        }
        label83:
        label151:
        do
        {
          do
          {
            i++;
            break label19;
            break;
          } while (((paramBundle.first_name == null) || (!paramBundle.first_name.startsWith(paramString))) && ((paramBundle.last_name == null) || (!paramBundle.last_name.startsWith(paramString))));
          onPlayFromMediaId(j + "_" + 0, null);
          break;
          paramBundle = (TLRPC.Chat)MusicBrowserService.this.chats.get(-j);
        } while ((paramBundle == null) || (paramBundle.title == null) || (!paramBundle.title.toLowerCase().contains(paramString)));
        onPlayFromMediaId(j + "_" + 0, null);
      }
    }
    
    public void onSeekTo(long paramLong)
    {
      MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
      if (localMessageObject != null) {
        MediaController.getInstance().seekToProgress(localMessageObject, (float)(paramLong / 1000L) / localMessageObject.getDuration());
      }
    }
    
    public void onSkipToNext()
    {
      MediaController.getInstance().playNextMessage();
    }
    
    public void onSkipToPrevious()
    {
      MediaController.getInstance().playPreviousMessage();
    }
    
    public void onSkipToQueueItem(long paramLong)
    {
      MediaController.getInstance().playMessageAtIndex((int)paramLong);
      MusicBrowserService.this.handlePlayRequest();
    }
    
    public void onStop()
    {
      MusicBrowserService.this.handleStopRequest(null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MusicBrowserService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */