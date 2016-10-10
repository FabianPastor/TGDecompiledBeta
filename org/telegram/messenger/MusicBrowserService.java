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
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
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
  private static final String AUTO_APP_PACKAGE_NAME = "com.google.android.projection.gearhead";
  public static final String CMD_NAME = "CMD_NAME";
  public static final String CMD_PAUSE = "CMD_PAUSE";
  private static final String MEDIA_ID_ROOT = "__ROOT__";
  private static final String SLOT_RESERVATION_QUEUE = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE";
  private static final String SLOT_RESERVATION_SKIP_TO_NEXT = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
  private static final String SLOT_RESERVATION_SKIP_TO_PREV = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";
  private static final int STOP_DELAY = 30000;
  private RectF bitmapRect;
  private HashMap<Integer, TLRPC.Chat> chats = new HashMap();
  private boolean chatsLoaded;
  private DelayedStopHandler delayedStopHandler = new DelayedStopHandler(this, null);
  private ArrayList<Integer> dialogs = new ArrayList();
  private int lastSelectedDialog;
  private boolean loadingChats;
  private MediaSession mediaSession;
  private HashMap<Integer, ArrayList<MessageObject>> musicObjects = new HashMap();
  private HashMap<Integer, ArrayList<MediaSession.QueueItem>> musicQueues = new HashMap();
  private Paint roundPaint;
  private boolean serviceStarted;
  private HashMap<Integer, TLRPC.User> users = new HashMap();
  
  private Bitmap createRoundBitmap(File paramFile)
  {
    try
    {
      Object localObject = new BitmapFactory.Options();
      ((BitmapFactory.Options)localObject).inSampleSize = 2;
      paramFile = BitmapFactory.decodeFile(paramFile.toString(), (BitmapFactory.Options)localObject);
      if (paramFile != null)
      {
        localObject = Bitmap.createBitmap(paramFile.getWidth(), paramFile.getHeight(), Bitmap.Config.ARGB_8888);
        ((Bitmap)localObject).eraseColor(0);
        Canvas localCanvas = new Canvas((Bitmap)localObject);
        BitmapShader localBitmapShader = new BitmapShader(paramFile, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (this.roundPaint == null)
        {
          this.roundPaint = new Paint(1);
          this.bitmapRect = new RectF();
        }
        this.roundPaint.setShader(localBitmapShader);
        this.bitmapRect.set(0.0F, 0.0F, paramFile.getWidth(), paramFile.getHeight());
        localCanvas.drawRoundRect(this.bitmapRect, paramFile.getWidth(), paramFile.getHeight(), this.roundPaint);
        return (Bitmap)localObject;
      }
    }
    catch (Throwable paramFile)
    {
      FileLog.e("tmessages", paramFile);
    }
    return null;
  }
  
  private long getAvailableActions()
  {
    long l1 = 3076L;
    long l2 = l1;
    if (MediaController.getInstance().getPlayingMessageObject() != null)
    {
      if (!MediaController.getInstance().isAudioPaused()) {
        l1 = 0xC04 | 0x2;
      }
      l2 = l1 | 0x10 | 0x20;
    }
    return l2;
  }
  
  private void handlePauseRequest()
  {
    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000L);
  }
  
  private void handlePlayRequest()
  {
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    if (!this.serviceStarted)
    {
      startService(new Intent(getApplicationContext(), MusicBrowserService.class));
      this.serviceStarted = true;
    }
    if (!this.mediaSession.isActive()) {
      this.mediaSession.setActive(true);
    }
    Object localObject = MediaController.getInstance().getPlayingMessageObject();
    if (localObject == null) {
      return;
    }
    MediaMetadata.Builder localBuilder = new MediaMetadata.Builder();
    localBuilder.putLong("android.media.metadata.DURATION", ((MessageObject)localObject).getDuration() * 1000);
    localBuilder.putString("android.media.metadata.ARTIST", ((MessageObject)localObject).getMusicAuthor());
    localBuilder.putString("android.media.metadata.TITLE", ((MessageObject)localObject).getMusicTitle());
    localObject = MediaController.getInstance().getAudioInfo();
    if (localObject != null)
    {
      localObject = ((AudioInfo)localObject).getCover();
      if (localObject != null) {
        localBuilder.putBitmap("android.media.metadata.ALBUM_ART", (Bitmap)localObject);
      }
    }
    this.mediaSession.setMetadata(localBuilder.build());
  }
  
  private void handleStopRequest(String paramString)
  {
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000L);
    updatePlaybackState(paramString);
    stopSelf();
    this.serviceStarted = false;
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
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
          localObject2 = (TLRPC.User)this.users.get(Integer.valueOf(j));
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
          i += 1;
          break;
          localBuilder.setTitle("DELETED USER");
          paramString = (String)localObject1;
          continue;
          localObject2 = (TLRPC.Chat)this.chats.get(Integer.valueOf(-j));
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
          FileLog.e("tmessages", paramString);
        }
      }
      paramString = (ArrayList)this.musicObjects.get(Integer.valueOf(i));
      if (paramString != null)
      {
        j = 0;
        while (j < paramString.size())
        {
          localObject1 = (MessageObject)paramString.get(j);
          localObject2 = new MediaDescription.Builder().setMediaId(i + "_" + j);
          ((MediaDescription.Builder)localObject2).setTitle(((MessageObject)localObject1).getMusicTitle());
          ((MediaDescription.Builder)localObject2).setSubtitle(((MessageObject)localObject1).getMusicAuthor());
          localArrayList.add(new MediaBrowser.MediaItem(((MediaDescription.Builder)localObject2).build(), 2));
          j += 1;
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
        break label142;
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
      if (MediaController.getInstance().isAudioPaused()) {}
      for (i = 2;; i = 3) {
        break;
      }
      label142:
      localBuilder.setActiveQueueItemId(0L);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    updatePlaybackState(null);
    handlePlayRequest();
  }
  
  public void onCreate()
  {
    super.onCreate();
    ApplicationLoader.postInitApplication();
    this.lastSelectedDialog = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("auto_lastSelectedDialog", 0);
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
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
  }
  
  public void onDestroy()
  {
    handleStopRequest(null);
    this.delayedStopHandler.removeCallbacksAndMessages(null);
    this.mediaSession.release();
  }
  
  public MediaBrowserService.BrowserRoot onGetRoot(String paramString, int paramInt, Bundle paramBundle)
  {
    if ((paramString == null) || ((1000 != paramInt) && (Process.myUid() != paramInt) && (!paramString.equals("com.google.android.mediasimulator")) && (!paramString.equals("com.google.android.projection.gearhead")))) {
      return null;
    }
    return new MediaBrowserService.BrowserRoot("__ROOT__", null);
  }
  
  public void onLoadChildren(final String paramString, final MediaBrowserService.Result<List<MediaBrowser.MediaItem>> paramResult)
  {
    if (!this.chatsLoaded)
    {
      paramResult.detach();
      if (this.loadingChats) {
        return;
      }
      this.loadingChats = true;
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ArrayList localArrayList2;
          ArrayList localArrayList1;
          int i;
          try
          {
            localArrayList2 = new ArrayList();
            localArrayList1 = new ArrayList();
            SQLiteCursor localSQLiteCursor1 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v2 WHERE uid != 0 AND mid > 0 AND type = %d", new Object[] { Integer.valueOf(4) }), new Object[0]);
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
                    localArrayList2.add(Integer.valueOf(i));
                    continue;
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MusicBrowserService.access$702(MusicBrowserService.this, true);
                        MusicBrowserService.access$802(MusicBrowserService.this, false);
                        MusicBrowserService.this.loadChildrenImpl(MusicBrowserService.1.this.val$parentMediaId, MusicBrowserService.1.this.val$result);
                        if ((MusicBrowserService.this.lastSelectedDialog == 0) && (!MusicBrowserService.this.dialogs.isEmpty())) {
                          MusicBrowserService.access$1002(MusicBrowserService.this, ((Integer)MusicBrowserService.this.dialogs.get(0)).intValue());
                        }
                        Object localObject1;
                        Object localObject2;
                        if (MusicBrowserService.this.lastSelectedDialog != 0)
                        {
                          localObject1 = (ArrayList)MusicBrowserService.this.musicObjects.get(Integer.valueOf(MusicBrowserService.this.lastSelectedDialog));
                          localObject2 = (ArrayList)MusicBrowserService.this.musicQueues.get(Integer.valueOf(MusicBrowserService.this.lastSelectedDialog));
                          if ((localObject1 != null) && (!((ArrayList)localObject1).isEmpty()))
                          {
                            MusicBrowserService.this.mediaSession.setQueue((List)localObject2);
                            if (MusicBrowserService.this.lastSelectedDialog <= 0) {
                              break label379;
                            }
                            localObject2 = (TLRPC.User)MusicBrowserService.this.users.get(Integer.valueOf(MusicBrowserService.this.lastSelectedDialog));
                            if (localObject2 == null) {
                              break label361;
                            }
                            MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
                          }
                        }
                        for (;;)
                        {
                          localObject1 = (MessageObject)((ArrayList)localObject1).get(0);
                          localObject2 = new MediaMetadata.Builder();
                          ((MediaMetadata.Builder)localObject2).putLong("android.media.metadata.DURATION", ((MessageObject)localObject1).getDuration() * 1000);
                          ((MediaMetadata.Builder)localObject2).putString("android.media.metadata.ARTIST", ((MessageObject)localObject1).getMusicAuthor());
                          ((MediaMetadata.Builder)localObject2).putString("android.media.metadata.TITLE", ((MessageObject)localObject1).getMusicTitle());
                          MusicBrowserService.this.mediaSession.setMetadata(((MediaMetadata.Builder)localObject2).build());
                          MusicBrowserService.this.updatePlaybackState(null);
                          return;
                          label361:
                          MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                          continue;
                          label379:
                          localObject2 = (TLRPC.Chat)MusicBrowserService.this.chats.get(Integer.valueOf(-MusicBrowserService.this.lastSelectedDialog));
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
            FileLog.e("tmessages", localException);
          }
          for (;;)
          {
            return;
            i = -i;
            localArrayList1.add(Integer.valueOf(i));
            break;
            localException.dispose();
            if (!MusicBrowserService.this.dialogs.isEmpty())
            {
              Object localObject1 = TextUtils.join(",", MusicBrowserService.this.dialogs);
              SQLiteCursor localSQLiteCursor2 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v2 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[] { localObject1, Integer.valueOf(4) }), new Object[0]);
              Object localObject2;
              while (localSQLiteCursor2.next())
              {
                localObject1 = localSQLiteCursor2.byteBufferValue(1);
                if (localObject1 != null)
                {
                  TLRPC.Message localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                  ((NativeByteBuffer)localObject1).reuse();
                  if (MessageObject.isMusicMessage(localMessage))
                  {
                    i = localSQLiteCursor2.intValue(0);
                    localMessage.id = localSQLiteCursor2.intValue(2);
                    localMessage.dialog_id = i;
                    Object localObject3 = (ArrayList)MusicBrowserService.this.musicObjects.get(Integer.valueOf(i));
                    localObject1 = (ArrayList)MusicBrowserService.this.musicQueues.get(Integer.valueOf(i));
                    localObject2 = localObject3;
                    if (localObject3 == null)
                    {
                      localObject2 = new ArrayList();
                      MusicBrowserService.this.musicObjects.put(Integer.valueOf(i), localObject2);
                      localObject1 = new ArrayList();
                      MusicBrowserService.this.musicQueues.put(Integer.valueOf(i), localObject1);
                    }
                    localObject3 = new MessageObject(localMessage, null, false);
                    ((ArrayList)localObject2).add(0, localObject3);
                    localObject2 = new MediaDescription.Builder().setMediaId(i + "_" + ((ArrayList)localObject2).size());
                    ((MediaDescription.Builder)localObject2).setTitle(((MessageObject)localObject3).getMusicTitle());
                    ((MediaDescription.Builder)localObject2).setSubtitle(((MessageObject)localObject3).getMusicAuthor());
                    ((ArrayList)localObject1).add(0, new MediaSession.QueueItem(((MediaDescription.Builder)localObject2).build(), ((ArrayList)localObject1).size()));
                  }
                }
              }
              localSQLiteCursor2.dispose();
              if (!localArrayList2.isEmpty())
              {
                localObject1 = new ArrayList();
                MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList2), (ArrayList)localObject1);
                i = 0;
                while (i < ((ArrayList)localObject1).size())
                {
                  localObject2 = (TLRPC.User)((ArrayList)localObject1).get(i);
                  MusicBrowserService.this.users.put(Integer.valueOf(((TLRPC.User)localObject2).id), localObject2);
                  i += 1;
                }
              }
              if (!localArrayList1.isEmpty())
              {
                localObject1 = new ArrayList();
                MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList1), (ArrayList)localObject1);
                i = 0;
                while (i < ((ArrayList)localObject1).size())
                {
                  localObject2 = (TLRPC.Chat)((ArrayList)localObject1).get(i);
                  MusicBrowserService.this.chats.put(Integer.valueOf(((TLRPC.Chat)localObject2).id), localObject2);
                  i += 1;
                }
              }
            }
          }
        }
      });
      return;
    }
    loadChildrenImpl(paramString, paramResult);
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
      if ((paramMessage == null) || ((MediaController.getInstance().getPlayingMessageObject() != null) && (!MediaController.getInstance().isAudioPaused()))) {
        return;
      }
      paramMessage.stopSelf();
      MusicBrowserService.access$1602(paramMessage, false);
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
      if (localMessageObject == null)
      {
        onPlayFromMediaId(MusicBrowserService.this.lastSelectedDialog + "_" + 0, null);
        return;
      }
      MediaController.getInstance().playAudio(localMessageObject);
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
          paramString = (ArrayList)MusicBrowserService.this.musicObjects.get(Integer.valueOf(i));
          paramBundle = (ArrayList)MusicBrowserService.this.musicQueues.get(Integer.valueOf(i));
          if ((paramString == null) || (j < 0) || (j >= paramString.size())) {
            break;
          }
          MusicBrowserService.access$1002(MusicBrowserService.this, i);
          ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("auto_lastSelectedDialog", i).commit();
          MediaController.getInstance().setPlaylist(paramString, (MessageObject)paramString.get(j), false);
          MusicBrowserService.this.mediaSession.setQueue(paramBundle);
          if (i <= 0) {
            continue;
          }
          paramString = (TLRPC.User)MusicBrowserService.this.users.get(Integer.valueOf(i));
          if (paramString == null) {
            continue;
          }
          MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(paramString.first_name, paramString.last_name));
        }
        catch (Exception paramString)
        {
          int i;
          FileLog.e("tmessages", paramString);
          continue;
          paramString = (TLRPC.Chat)MusicBrowserService.this.chats.get(Integer.valueOf(-i));
          if (paramString == null) {
            continue;
          }
          MusicBrowserService.this.mediaSession.setQueueTitle(paramString.title);
          continue;
          MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
          continue;
        }
        MusicBrowserService.this.handlePlayRequest();
        return;
        MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
      }
    }
    
    public void onPlayFromSearch(String paramString, Bundle paramBundle)
    {
      if ((paramString == null) || (paramString.length() == 0)) {
        return;
      }
      paramString = paramString.toLowerCase();
      int i = 0;
      label19:
      int j;
      if (i < MusicBrowserService.this.dialogs.size())
      {
        j = ((Integer)MusicBrowserService.this.dialogs.get(i)).intValue();
        if (j <= 0) {
          break label153;
        }
        paramBundle = (TLRPC.User)MusicBrowserService.this.users.get(Integer.valueOf(j));
        if (paramBundle != null) {
          break label87;
        }
      }
      label87:
      label153:
      do
      {
        do
        {
          i += 1;
          break label19;
          break;
        } while (((paramBundle.first_name == null) || (!paramBundle.first_name.startsWith(paramString))) && ((paramBundle.last_name == null) || (!paramBundle.last_name.startsWith(paramString))));
        onPlayFromMediaId(j + "_" + 0, null);
        return;
        paramBundle = (TLRPC.Chat)MusicBrowserService.this.chats.get(Integer.valueOf(-j));
      } while ((paramBundle == null) || (paramBundle.title == null) || (!paramBundle.title.toLowerCase().contains(paramString)));
      onPlayFromMediaId(j + "_" + 0, null);
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