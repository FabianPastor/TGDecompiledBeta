package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Action.Builder;
import android.app.Notification.Builder;
import android.app.Notification.MediaStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata.Builder;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.PlaybackState.Builder;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.ui.LaunchActivity;

public class MusicPlayerService
  extends Service
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int ID_NOTIFICATION = 5;
  public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
  public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
  public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
  public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
  public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
  private static boolean supportBigNotifications;
  private static boolean supportLockScreenControls;
  private Bitmap albumArtPlaceholder;
  private AudioManager audioManager;
  private MediaSession mediaSession;
  private PlaybackState.Builder playbackState;
  private RemoteControlClient remoteControlClient;
  
  static
  {
    boolean bool1 = true;
    if (Build.VERSION.SDK_INT >= 16)
    {
      bool2 = true;
      supportBigNotifications = bool2;
      if (Build.VERSION.SDK_INT >= 21) {
        break label36;
      }
    }
    label36:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      supportLockScreenControls = bool2;
      return;
      bool2 = false;
      break;
    }
  }
  
  @SuppressLint({"NewApi"})
  private void createNotification(MessageObject paramMessageObject)
  {
    String str1 = paramMessageObject.getMusicTitle();
    String str2 = paramMessageObject.getMusicAuthor();
    AudioInfo localAudioInfo = MediaController.getInstance().getAudioInfo();
    paramMessageObject = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
    paramMessageObject.setAction("com.tmessages.openplayer");
    paramMessageObject.setFlags(32768);
    PendingIntent localPendingIntent1 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, paramMessageObject, 0);
    if (Build.VERSION.SDK_INT >= 21) {
      if (localAudioInfo != null) {
        paramMessageObject = localAudioInfo.getSmallCover();
      }
    }
    for (;;)
    {
      Object localObject1;
      label87:
      boolean bool;
      label99:
      PendingIntent localPendingIntent2;
      Object localObject2;
      Object localObject3;
      label197:
      PendingIntent localPendingIntent4;
      PendingIntent localPendingIntent5;
      if (localAudioInfo != null)
      {
        localObject1 = localAudioInfo.getCover();
        if (MediaController.getInstance().isMessagePaused()) {
          break label666;
        }
        bool = true;
        localPendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
        PendingIntent localPendingIntent3 = PendingIntent.getService(getApplicationContext(), 0, new Intent(this, getClass()).setAction(getPackageName() + ".STOP_PLAYER"), 268435456);
        localObject2 = getApplicationContext();
        if (!bool) {
          break label672;
        }
        localObject3 = "org.telegram.android.musicplayer.pause";
        localPendingIntent4 = PendingIntent.getBroadcast((Context)localObject2, 0, new Intent((String)localObject3).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
        localPendingIntent5 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), 268435456);
        localObject2 = new Notification.Builder(this);
        Notification.Builder localBuilder = ((Notification.Builder)localObject2).setSmallIcon(NUM).setOngoing(bool).setContentTitle(str1).setContentText(str2);
        if (localAudioInfo == null) {
          break label679;
        }
        localObject3 = localAudioInfo.getAlbum();
        label307:
        localBuilder.setSubText((CharSequence)localObject3).setContentIntent(localPendingIntent1).setDeleteIntent(localPendingIntent3).setShowWhen(false).setCategory("transport").setPriority(2).setStyle(new Notification.MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(new int[] { 0, 1, 2 }));
        if (Build.VERSION.SDK_INT >= 26) {
          ((Notification.Builder)localObject2).setChannelId("Other3");
        }
        if (paramMessageObject == null) {
          break label685;
        }
        ((Notification.Builder)localObject2).setLargeIcon(paramMessageObject);
        label403:
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
          break label698;
        }
        this.playbackState.setState(6, 0L, 1.0F).setActions(0L);
        ((Notification.Builder)localObject2).addAction(new Notification.Action.Builder(NUM, "", localPendingIntent2).build()).addAction(new Notification.Action.Builder(NUM, "", null).build()).addAction(new Notification.Action.Builder(NUM, "", localPendingIntent5).build());
        this.mediaSession.setPlaybackState(this.playbackState.build());
        localObject1 = new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", (Bitmap)localObject1).putString("android.media.metadata.ALBUM_ARTIST", str2).putString("android.media.metadata.TITLE", str1);
        if (localAudioInfo == null) {
          break label852;
        }
        paramMessageObject = localAudioInfo.getAlbum();
        label549:
        paramMessageObject = ((MediaMetadata.Builder)localObject1).putString("android.media.metadata.ALBUM", paramMessageObject);
        this.mediaSession.setMetadata(paramMessageObject.build());
        ((Notification.Builder)localObject2).setVisibility(1);
        paramMessageObject = ((Notification.Builder)localObject2).build();
        if (!bool) {
          break label857;
        }
        startForeground(5, paramMessageObject);
        label594:
        if (this.remoteControlClient != null)
        {
          paramMessageObject = this.remoteControlClient.editMetadata(true);
          paramMessageObject.putString(2, str2);
          paramMessageObject.putString(7, str1);
          if ((localAudioInfo == null) || (localAudioInfo.getCover() == null)) {}
        }
      }
      try
      {
        paramMessageObject.putBitmap(100, localAudioInfo.getCover());
        paramMessageObject.apply();
        return;
        paramMessageObject = null;
        continue;
        localObject1 = null;
        break label87;
        label666:
        bool = false;
        break label99;
        label672:
        localObject3 = "org.telegram.android.musicplayer.play";
        break label197;
        label679:
        localObject3 = null;
        break label307;
        label685:
        ((Notification.Builder)localObject2).setLargeIcon(this.albumArtPlaceholder);
        break label403;
        label698:
        paramMessageObject = this.playbackState;
        label711:
        float f;
        if (bool)
        {
          i = 3;
          long l = MediaController.getInstance().getPlayingMessageObject().audioProgressSec;
          if (!bool) {
            break label838;
          }
          f = 1.0F;
          label731:
          paramMessageObject.setState(i, l * 1000L, f).setActions(566L);
          paramMessageObject = ((Notification.Builder)localObject2).addAction(new Notification.Action.Builder(NUM, "", localPendingIntent2).build());
          if (!bool) {
            break label844;
          }
        }
        label838:
        label844:
        for (int i = NUM;; i = NUM)
        {
          paramMessageObject.addAction(new Notification.Action.Builder(i, "", localPendingIntent4).build()).addAction(new Notification.Action.Builder(NUM, "", localPendingIntent5).build());
          break;
          i = 2;
          break label711;
          f = 0.0F;
          break label731;
        }
        label852:
        paramMessageObject = null;
        break label549;
        label857:
        stopForeground(false);
        ((NotificationManager)getSystemService("notification")).notify(5, paramMessageObject);
        break label594;
        localObject3 = new RemoteViews(getApplicationContext().getPackageName(), NUM);
        paramMessageObject = null;
        if (supportBigNotifications) {
          paramMessageObject = new RemoteViews(getApplicationContext().getPackageName(), NUM);
        }
        localObject1 = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(NUM).setContentIntent(localPendingIntent1).setChannelId("Other3").setContentTitle(str1).build();
        ((Notification)localObject1).contentView = ((RemoteViews)localObject3);
        if (supportBigNotifications) {
          ((Notification)localObject1).bigContentView = paramMessageObject;
        }
        setListeners((RemoteViews)localObject3);
        if (supportBigNotifications) {
          setListeners(paramMessageObject);
        }
        if (localAudioInfo != null)
        {
          paramMessageObject = localAudioInfo.getSmallCover();
          label1007:
          if (paramMessageObject == null) {
            break label1303;
          }
          ((Notification)localObject1).contentView.setImageViewBitmap(NUM, paramMessageObject);
          if (supportBigNotifications) {
            ((Notification)localObject1).bigContentView.setImageViewBitmap(NUM, paramMessageObject);
          }
          label1041:
          if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            break label1340;
          }
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 0);
          if (supportBigNotifications)
          {
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 0);
          }
          label1184:
          ((Notification)localObject1).contentView.setTextViewText(NUM, str1);
          ((Notification)localObject1).contentView.setTextViewText(NUM, str2);
          if (supportBigNotifications)
          {
            ((Notification)localObject1).bigContentView.setTextViewText(NUM, str1);
            ((Notification)localObject1).bigContentView.setTextViewText(NUM, str2);
            localObject3 = ((Notification)localObject1).bigContentView;
            if ((localAudioInfo == null) || (TextUtils.isEmpty(localAudioInfo.getAlbum()))) {
              break label1547;
            }
          }
        }
        label1303:
        label1340:
        label1547:
        for (paramMessageObject = localAudioInfo.getAlbum();; paramMessageObject = "")
        {
          ((RemoteViews)localObject3).setTextViewText(NUM, paramMessageObject);
          ((Notification)localObject1).flags |= 0x2;
          startForeground(5, (Notification)localObject1);
          break;
          paramMessageObject = null;
          break label1007;
          ((Notification)localObject1).contentView.setImageViewResource(NUM, NUM);
          if (!supportBigNotifications) {
            break label1041;
          }
          ((Notification)localObject1).bigContentView.setImageViewResource(NUM, NUM);
          break label1041;
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 0);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 0);
          if (supportBigNotifications)
          {
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 0);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 0);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
          }
          if (MediaController.getInstance().isMessagePaused())
          {
            ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
            ((Notification)localObject1).contentView.setViewVisibility(NUM, 0);
            if (!supportBigNotifications) {
              break label1184;
            }
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
            ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 0);
            break label1184;
          }
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 0);
          ((Notification)localObject1).contentView.setViewVisibility(NUM, 8);
          if (!supportBigNotifications) {
            break label1184;
          }
          ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 0);
          ((Notification)localObject1).bigContentView.setViewVisibility(NUM, 8);
          break label1184;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e(localThrowable);
        }
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.messagePlayingPlayStateChanged)
    {
      paramVarArgs = MediaController.getInstance().getPlayingMessageObject();
      if (paramVarArgs == null) {
        break label24;
      }
      createNotification(paramVarArgs);
    }
    for (;;)
    {
      return;
      label24:
      stopSelf();
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onCreate()
  {
    this.audioManager = ((AudioManager)getSystemService("audio"));
    for (int i = 0; i < 3; i++)
    {
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
      this.playbackState = new PlaybackState.Builder();
      this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0F), AndroidUtilities.dp(102.0F), Bitmap.Config.ARGB_8888);
      Drawable localDrawable = getResources().getDrawable(NUM);
      localDrawable.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
      localDrawable.draw(new Canvas(this.albumArtPlaceholder));
      this.mediaSession.setCallback(new MediaSession.Callback()
      {
        public void onPause()
        {
          MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
        }
        
        public void onPlay()
        {
          MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        }
        
        public void onSkipToNext()
        {
          MediaController.getInstance().playNextMessage();
        }
        
        public void onSkipToPrevious()
        {
          MediaController.getInstance().playPreviousMessage();
        }
        
        public void onStop() {}
      });
      this.mediaSession.setActive(true);
    }
    super.onCreate();
  }
  
  @SuppressLint({"NewApi"})
  public void onDestroy()
  {
    super.onDestroy();
    if (this.remoteControlClient != null)
    {
      RemoteControlClient.MetadataEditor localMetadataEditor = this.remoteControlClient.editMetadata(true);
      localMetadataEditor.clear();
      localMetadataEditor.apply();
      this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
    }
    if (Build.VERSION.SDK_INT >= 21) {
      this.mediaSession.release();
    }
    for (int i = 0; i < 3; i++)
    {
      NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
      NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    }
  }
  
  @SuppressLint({"NewApi"})
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    paramInt1 = 1;
    if (paramIntent != null) {}
    for (;;)
    {
      Object localObject1;
      try
      {
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        if (!(getPackageName() + ".STOP_PLAYER").equals(paramIntent.getAction())) {
          continue;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
        paramInt1 = 2;
      }
      catch (Exception paramIntent)
      {
        paramIntent.printStackTrace();
        continue;
        if (!supportLockScreenControls) {
          break label201;
        }
        localObject2 = new android/content/ComponentName;
        ((ComponentName)localObject2).<init>(getApplicationContext(), MusicPlayerReceiver.class.getName());
      }
      return paramInt1;
      paramIntent = MediaController.getInstance().getPlayingMessageObject();
      if (paramIntent == null)
      {
        paramIntent = new org/telegram/messenger/MusicPlayerService$2;
        paramIntent.<init>(this);
        AndroidUtilities.runOnUIThread(paramIntent);
      }
      try
      {
        Object localObject2;
        if (this.remoteControlClient == null)
        {
          this.audioManager.registerMediaButtonEventReceiver((ComponentName)localObject2);
          localObject1 = new android/content/Intent;
          ((Intent)localObject1).<init>("android.intent.action.MEDIA_BUTTON");
          ((Intent)localObject1).setComponent((ComponentName)localObject2);
          localObject1 = PendingIntent.getBroadcast(this, 0, (Intent)localObject1, 0);
          localObject2 = new android/media/RemoteControlClient;
          ((RemoteControlClient)localObject2).<init>((PendingIntent)localObject1);
          this.remoteControlClient = ((RemoteControlClient)localObject2);
          this.audioManager.registerRemoteControlClient(this.remoteControlClient);
        }
        this.remoteControlClient.setTransportControlFlags(189);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          label201:
          FileLog.e(localException);
        }
      }
      createNotification(paramIntent);
    }
  }
  
  public void setListeners(RemoteViews paramRemoteViews)
  {
    paramRemoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.close"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.pause"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.play"), 134217728));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MusicPlayerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */