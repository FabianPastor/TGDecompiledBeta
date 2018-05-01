package org.telegram.messenger.voip;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount.Builder;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.VoIPPermissionActivity;

public abstract class VoIPBaseService
  extends Service
  implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, VoIPController.ConnectionStateListener
{
  public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
  public static final int AUDIO_ROUTE_BLUETOOTH = 2;
  public static final int AUDIO_ROUTE_EARPIECE = 0;
  public static final int AUDIO_ROUTE_SPEAKER = 1;
  public static final int DISCARD_REASON_DISCONNECT = 2;
  public static final int DISCARD_REASON_HANGUP = 1;
  public static final int DISCARD_REASON_LINE_BUSY = 4;
  public static final int DISCARD_REASON_MISSED = 3;
  protected static final int ID_INCOMING_CALL_NOTIFICATION = 202;
  protected static final int ID_ONGOING_CALL_NOTIFICATION = 201;
  protected static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
  public static final int STATE_ENDED = 11;
  public static final int STATE_ESTABLISHED = 3;
  public static final int STATE_FAILED = 4;
  public static final int STATE_RECONNECTING = 5;
  public static final int STATE_WAIT_INIT = 1;
  public static final int STATE_WAIT_INIT_ACK = 2;
  protected static final boolean USE_CONNECTION_SERVICE = ;
  protected static VoIPBaseService sharedInstance;
  protected Runnable afterSoundRunnable = new Runnable()
  {
    public void run()
    {
      VoIPBaseService.this.soundPool.release();
      if (VoIPBaseService.USE_CONNECTION_SERVICE) {}
      for (;;)
      {
        return;
        if (VoIPBaseService.this.isBtHeadsetConnected) {
          ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio")).stopBluetoothSco();
        }
        ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio")).setSpeakerphoneOn(false);
      }
    }
  };
  protected boolean audioConfigured;
  protected int audioRouteToSet = 2;
  protected boolean bluetoothScoActive = false;
  protected BluetoothAdapter btAdapter;
  protected int callDiscardReason;
  protected VoIPController controller;
  protected boolean controllerStarted;
  protected PowerManager.WakeLock cpuWakelock;
  protected int currentAccount = -1;
  protected int currentState = 0;
  protected boolean haveAudioFocus;
  protected boolean isBtHeadsetConnected;
  protected boolean isHeadsetPlugged;
  protected boolean isOutgoing;
  protected boolean isProximityNear;
  protected int lastError;
  protected long lastKnownDuration = 0L;
  protected NetworkInfo lastNetInfo;
  private Boolean mHasEarpiece = null;
  protected boolean micMute;
  protected boolean needPlayEndSound;
  protected boolean needSwitchToBluetoothAfterScoActivates = false;
  protected Notification ongoingCallNotification;
  protected boolean playingSound;
  protected VoIPController.Stats prevStats = new VoIPController.Stats();
  protected PowerManager.WakeLock proximityWakelock;
  protected BroadcastReceiver receiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      boolean bool1 = true;
      boolean bool2 = true;
      if ("android.intent.action.HEADSET_PLUG".equals(paramAnonymousIntent.getAction()))
      {
        paramAnonymousContext = VoIPBaseService.this;
        if (paramAnonymousIntent.getIntExtra("state", 0) == 1)
        {
          paramAnonymousContext.isHeadsetPlugged = bool2;
          if ((VoIPBaseService.this.isHeadsetPlugged) && (VoIPBaseService.this.proximityWakelock != null) && (VoIPBaseService.this.proximityWakelock.isHeld())) {
            VoIPBaseService.this.proximityWakelock.release();
          }
          VoIPBaseService.this.isProximityNear = false;
          VoIPBaseService.this.updateOutputGainControlState();
        }
      }
      for (;;)
      {
        return;
        bool2 = false;
        break;
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramAnonymousIntent.getAction()))
        {
          VoIPBaseService.this.updateNetworkType();
        }
        else
        {
          if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(paramAnonymousIntent.getAction()))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("bt headset state = " + paramAnonymousIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0));
            }
            paramAnonymousContext = VoIPBaseService.this;
            if (paramAnonymousIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) == 2) {}
            for (bool2 = bool1;; bool2 = false)
            {
              paramAnonymousContext.updateBluetoothHeadsetState(bool2);
              break;
            }
          }
          if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(paramAnonymousIntent.getAction()))
          {
            int i = paramAnonymousIntent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
            if (BuildVars.LOGS_ENABLED) {
              FileLog.e("Bluetooth SCO state updated: " + i);
            }
            if ((i == 0) && (VoIPBaseService.this.isBtHeadsetConnected) && ((!VoIPBaseService.this.btAdapter.isEnabled()) || (VoIPBaseService.this.btAdapter.getProfileConnectionState(1) != 2)))
            {
              VoIPBaseService.this.updateBluetoothHeadsetState(false);
            }
            else
            {
              paramAnonymousContext = VoIPBaseService.this;
              if (i == 1) {}
              for (bool2 = true;; bool2 = false)
              {
                paramAnonymousContext.bluetoothScoActive = bool2;
                if ((VoIPBaseService.this.bluetoothScoActive) && (VoIPBaseService.this.needSwitchToBluetoothAfterScoActivates))
                {
                  VoIPBaseService.this.needSwitchToBluetoothAfterScoActivates = false;
                  paramAnonymousContext = (AudioManager)VoIPBaseService.this.getSystemService("audio");
                  paramAnonymousContext.setSpeakerphoneOn(false);
                  paramAnonymousContext.setBluetoothScoOn(true);
                }
                paramAnonymousContext = VoIPBaseService.this.stateListeners.iterator();
                while (paramAnonymousContext.hasNext()) {
                  ((VoIPBaseService.StateListener)paramAnonymousContext.next()).onAudioSettingsChanged();
                }
                break;
              }
            }
          }
          else if ("android.intent.action.PHONE_STATE".equals(paramAnonymousIntent.getAction()))
          {
            paramAnonymousContext = paramAnonymousIntent.getStringExtra("state");
            if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(paramAnonymousContext)) {
              VoIPBaseService.this.hangUp();
            }
          }
        }
      }
    }
  };
  protected MediaPlayer ringtonePlayer;
  protected int signalBarCount;
  protected SoundPool soundPool;
  protected int spBusyId;
  protected int spConnectingId;
  protected int spEndId;
  protected int spFailedID;
  protected int spPlayID;
  protected int spRingbackID;
  protected boolean speakerphoneStateToSet;
  protected ArrayList<StateListener> stateListeners = new ArrayList();
  protected VoIPController.Stats stats = new VoIPController.Stats();
  protected CallConnection systemCallConnection;
  protected Runnable timeoutRunnable;
  protected Vibrator vibrator;
  private boolean wasEstablished;
  
  private void acceptIncomingCallFromNotification()
  {
    showNotification();
    if ((Build.VERSION.SDK_INT >= 23) && (checkSelfPermission("android.permission.RECORD_AUDIO") != 0)) {}
    for (;;)
    {
      try
      {
        Intent localIntent1 = new android/content/Intent;
        localIntent1.<init>(this, VoIPPermissionActivity.class);
        PendingIntent.getActivity(this, 0, localIntent1.addFlags(268435456), 0).send();
        return;
      }
      catch (Exception localException1)
      {
        if (!BuildVars.LOGS_ENABLED) {
          continue;
        }
        FileLog.e("Error starting permission activity", localException1);
        continue;
      }
      acceptIncomingCall();
      try
      {
        Intent localIntent2 = new android/content/Intent;
        localIntent2.<init>(this, getUIActivityClass());
        PendingIntent.getActivity(this, 0, localIntent2.addFlags(805306368), 0).send();
      }
      catch (Exception localException2) {}
      if (BuildVars.LOGS_ENABLED) {
        FileLog.e("Error starting incall activity", localException2);
      }
    }
  }
  
  public static VoIPBaseService getSharedInstance()
  {
    return sharedInstance;
  }
  
  public static boolean isAnyKindOfCallActive()
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (VoIPService.getSharedInstance() != null)
    {
      bool2 = bool1;
      if (VoIPService.getSharedInstance().getCallState() != 15) {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  private static boolean isDeviceCompatibleWithConnectionServiceAPI()
  {
    boolean bool = false;
    if (Build.VERSION.SDK_INT < 26) {}
    for (;;)
    {
      return bool;
      if (("angler".equals(Build.PRODUCT)) || ("bullhead".equals(Build.PRODUCT)) || ("sailfish".equals(Build.PRODUCT)) || ("marlin".equals(Build.PRODUCT)) || ("walleye".equals(Build.PRODUCT)) || ("taimen".equals(Build.PRODUCT))) {
        bool = true;
      }
    }
  }
  
  public abstract void acceptIncomingCall();
  
  @TargetApi(26)
  protected PhoneAccountHandle addAccountToTelecomManager()
  {
    TelecomManager localTelecomManager = (TelecomManager)getSystemService("telecom");
    TLRPC.User localUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
    PhoneAccountHandle localPhoneAccountHandle = new PhoneAccountHandle(new ComponentName(this, TelegramConnectionService.class), "" + localUser.id);
    localTelecomManager.registerPhoneAccount(new PhoneAccount.Builder(localPhoneAccountHandle, ContactsController.formatName(localUser.first_name, localUser.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, NUM)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
    return localPhoneAccountHandle;
  }
  
  protected void callEnded()
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("Call " + getCallID() + " ended");
    }
    dispatchStateChanged(11);
    if (this.needPlayEndSound)
    {
      this.playingSound = true;
      this.soundPool.play(this.spEndId, 1.0F, 1.0F, 0, 0, 1.0F);
      AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 700L);
    }
    if (this.timeoutRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
      this.timeoutRunnable = null;
    }
    if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null)) {}
    switch (this.callDiscardReason)
    {
    default: 
      this.systemCallConnection.setDisconnected(new DisconnectCause(3));
    case 1: 
    case 2: 
    case 4: 
      for (;;)
      {
        this.systemCallConnection.destroy();
        this.systemCallConnection = null;
        stopSelf();
        return;
        localCallConnection = this.systemCallConnection;
        if (this.isOutgoing) {}
        for (i = 2;; i = 6)
        {
          localCallConnection.setDisconnected(new DisconnectCause(i));
          break;
        }
        this.systemCallConnection.setDisconnected(new DisconnectCause(1));
        continue;
        this.systemCallConnection.setDisconnected(new DisconnectCause(7));
      }
    }
    CallConnection localCallConnection = this.systemCallConnection;
    if (this.isOutgoing) {}
    for (int i = 4;; i = 5)
    {
      localCallConnection.setDisconnected(new DisconnectCause(i));
      break;
    }
  }
  
  protected void callFailed()
  {
    if ((this.controller != null) && (this.controllerStarted)) {}
    for (int i = this.controller.getLastError();; i = 0)
    {
      callFailed(i);
      return;
    }
  }
  
  protected void callFailed(int paramInt)
  {
    try
    {
      Exception localException1 = new java/lang/Exception;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localException1.<init>("Call " + getCallID() + " failed with error code " + paramInt);
      throw localException1;
    }
    catch (Exception localException2)
    {
      FileLog.e(localException2);
      this.lastError = paramInt;
      dispatchStateChanged(4);
      if ((paramInt != -3) && (this.soundPool != null))
      {
        this.playingSound = true;
        this.soundPool.play(this.spFailedID, 1.0F, 1.0F, 0, 0, 1.0F);
        AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000L);
      }
      if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null))
      {
        this.systemCallConnection.setDisconnected(new DisconnectCause(1));
        this.systemCallConnection.destroy();
        this.systemCallConnection = null;
      }
      stopSelf();
    }
  }
  
  void callFailedFromConnectionService()
  {
    if (this.isOutgoing) {
      callFailed(-5);
    }
    for (;;)
    {
      return;
      hangUp();
    }
  }
  
  protected void configureDeviceForCall()
  {
    int i = 5;
    this.needPlayEndSound = true;
    Object localObject = (AudioManager)getSystemService("audio");
    if (!USE_CONNECTION_SERVICE)
    {
      ((AudioManager)localObject).setMode(3);
      ((AudioManager)localObject).requestAudioFocus(this, 0, 1);
      if ((isBluetoothHeadsetConnected()) && (hasEarpiece())) {
        switch (this.audioRouteToSet)
        {
        }
      }
    }
    for (;;)
    {
      updateOutputGainControlState();
      this.audioConfigured = true;
      localObject = (SensorManager)getSystemService("sensor");
      Sensor localSensor = ((SensorManager)localObject).getDefaultSensor(8);
      if (localSensor != null) {}
      try
      {
        this.proximityWakelock = ((PowerManager)getSystemService("power")).newWakeLock(32, "telegram-voip-prx");
        ((SensorManager)localObject).registerListener(this, localSensor, 3);
        return;
        ((AudioManager)localObject).setBluetoothScoOn(true);
        ((AudioManager)localObject).setSpeakerphoneOn(false);
        continue;
        ((AudioManager)localObject).setBluetoothScoOn(false);
        ((AudioManager)localObject).setSpeakerphoneOn(false);
        continue;
        ((AudioManager)localObject).setBluetoothScoOn(false);
        ((AudioManager)localObject).setSpeakerphoneOn(true);
        continue;
        if (isBluetoothHeadsetConnected())
        {
          ((AudioManager)localObject).setBluetoothScoOn(this.speakerphoneStateToSet);
          continue;
        }
        ((AudioManager)localObject).setSpeakerphoneOn(this.speakerphoneStateToSet);
        continue;
        if ((isBluetoothHeadsetConnected()) && (hasEarpiece())) {}
        switch (this.audioRouteToSet)
        {
        default: 
          break;
        case 0: 
          this.systemCallConnection.setAudioRoute(5);
          break;
        case 2: 
          this.systemCallConnection.setAudioRoute(2);
          break;
        case 1: 
          this.systemCallConnection.setAudioRoute(8);
          continue;
          if (hasEarpiece())
          {
            localObject = this.systemCallConnection;
            if (!this.speakerphoneStateToSet) {}
            for (;;)
            {
              ((CallConnection)localObject).setAudioRoute(i);
              break;
              i = 8;
            }
          }
          localObject = this.systemCallConnection;
          if (!this.speakerphoneStateToSet) {}
          for (;;)
          {
            ((CallConnection)localObject).setAudioRoute(i);
            break;
            i = 2;
          }
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Error initializing proximity sensor", localException);
          }
        }
      }
    }
  }
  
  protected VoIPController createController()
  {
    return new VoIPController();
  }
  
  public abstract void declineIncomingCall();
  
  public abstract void declineIncomingCall(int paramInt, Runnable paramRunnable);
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.appDidLogout) {
      callEnded();
    }
  }
  
  protected void dispatchStateChanged(int paramInt)
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("== Call " + getCallID() + " state changed to " + paramInt + " ==");
    }
    this.currentState = paramInt;
    if ((USE_CONNECTION_SERVICE) && (paramInt == 3) && (this.systemCallConnection != null)) {
      this.systemCallConnection.setActive();
    }
    for (int i = 0; i < this.stateListeners.size(); i++) {
      ((StateListener)this.stateListeners.get(i)).onStateChanged(paramInt);
    }
  }
  
  public int getAccount()
  {
    return this.currentAccount;
  }
  
  public long getCallDuration()
  {
    long l;
    if ((!this.controllerStarted) || (this.controller == null)) {
      l = this.lastKnownDuration;
    }
    for (;;)
    {
      return l;
      l = this.controller.getCallDuration();
      this.lastKnownDuration = l;
    }
  }
  
  public abstract long getCallID();
  
  public int getCallState()
  {
    return this.currentState;
  }
  
  public abstract CallConnection getConnectionAndStartCall();
  
  public int getCurrentAudioRoute()
  {
    int i = 2;
    if (USE_CONNECTION_SERVICE)
    {
      if ((this.systemCallConnection != null) && (this.systemCallConnection.getCallAudioState() != null)) {}
      switch (this.systemCallConnection.getCallAudioState().getRoute())
      {
      case 3: 
      case 5: 
      case 6: 
      case 7: 
      default: 
        i = this.audioRouteToSet;
      }
    }
    for (;;)
    {
      return i;
      i = 0;
      continue;
      i = 1;
      continue;
      if (this.audioConfigured)
      {
        AudioManager localAudioManager = (AudioManager)getSystemService("audio");
        if (!localAudioManager.isBluetoothScoOn()) {
          if (localAudioManager.isSpeakerphoneOn()) {
            i = 1;
          } else {
            i = 0;
          }
        }
      }
      else
      {
        i = this.audioRouteToSet;
      }
    }
  }
  
  public String getDebugString()
  {
    return this.controller.getDebugString();
  }
  
  public int getLastError()
  {
    return this.lastError;
  }
  
  protected Bitmap getRoundAvatarBitmap(TLObject paramTLObject)
  {
    Object localObject1 = null;
    Object localObject2;
    Object localObject3;
    if ((paramTLObject instanceof TLRPC.User))
    {
      localObject2 = (TLRPC.User)paramTLObject;
      localObject3 = localObject1;
      if (((TLRPC.User)localObject2).photo != null)
      {
        localObject3 = localObject1;
        if (((TLRPC.User)localObject2).photo.photo_small != null)
        {
          localObject3 = ImageLoader.getInstance().getImageFromMemory(((TLRPC.User)localObject2).photo.photo_small, null, "50_50");
          if (localObject3 == null) {
            break label236;
          }
          localObject3 = ((BitmapDrawable)localObject3).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        }
      }
      localObject1 = localObject3;
      if (localObject3 == null)
      {
        Theme.createDialogsResources(this);
        if (!(paramTLObject instanceof TLRPC.User)) {
          break label411;
        }
      }
    }
    label236:
    label411:
    for (paramTLObject = new AvatarDrawable((TLRPC.User)paramTLObject);; paramTLObject = new AvatarDrawable((TLRPC.Chat)paramTLObject))
    {
      localObject1 = Bitmap.createBitmap(AndroidUtilities.dp(42.0F), AndroidUtilities.dp(42.0F), Bitmap.Config.ARGB_8888);
      paramTLObject.setBounds(0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
      paramTLObject.draw(new Canvas((Bitmap)localObject1));
      localObject2 = new Canvas((Bitmap)localObject1);
      localObject3 = new Path();
      ((Path)localObject3).addCircle(((Bitmap)localObject1).getWidth() / 2, ((Bitmap)localObject1).getHeight() / 2, ((Bitmap)localObject1).getWidth() / 2, Path.Direction.CW);
      ((Path)localObject3).toggleInverseFillType();
      paramTLObject = new Paint(1);
      paramTLObject.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      ((Canvas)localObject2).drawPath((Path)localObject3, paramTLObject);
      return (Bitmap)localObject1;
      try
      {
        localObject3 = new android/graphics/BitmapFactory$Options;
        ((BitmapFactory.Options)localObject3).<init>();
        ((BitmapFactory.Options)localObject3).inMutable = true;
        localObject3 = BitmapFactory.decodeFile(FileLoader.getPathToAttach(((TLRPC.User)localObject2).photo.photo_small, true).toString(), (BitmapFactory.Options)localObject3);
      }
      catch (Throwable localThrowable1)
      {
        FileLog.e(localThrowable1);
        localObject4 = localObject1;
      }
      break;
      localObject2 = (TLRPC.Chat)paramTLObject;
      Object localObject4 = localObject1;
      if (((TLRPC.Chat)localObject2).photo == null) {
        break;
      }
      localObject4 = localObject1;
      if (((TLRPC.Chat)localObject2).photo.photo_small == null) {
        break;
      }
      localObject4 = ImageLoader.getInstance().getImageFromMemory(((TLRPC.Chat)localObject2).photo.photo_small, null, "50_50");
      if (localObject4 != null)
      {
        localObject4 = ((BitmapDrawable)localObject4).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        break;
      }
      try
      {
        localObject4 = new android/graphics/BitmapFactory$Options;
        ((BitmapFactory.Options)localObject4).<init>();
        ((BitmapFactory.Options)localObject4).inMutable = true;
        localObject4 = BitmapFactory.decodeFile(FileLoader.getPathToAttach(((TLRPC.Chat)localObject2).photo.photo_small, true).toString(), (BitmapFactory.Options)localObject4);
      }
      catch (Throwable localThrowable2)
      {
        FileLog.e(localThrowable2);
        Object localObject5 = localObject1;
      }
      break;
    }
  }
  
  protected int getStatsNetworkType()
  {
    int i = 1;
    int j = i;
    if (this.lastNetInfo != null)
    {
      j = i;
      if (this.lastNetInfo.getType() == 0) {
        if (!this.lastNetInfo.isRoaming()) {
          break label37;
        }
      }
    }
    label37:
    for (j = 2;; j = 0) {
      return j;
    }
  }
  
  protected abstract Class<? extends Activity> getUIActivityClass();
  
  public void handleNotificationAction(Intent paramIntent)
  {
    if ((getPackageName() + ".END_CALL").equals(paramIntent.getAction()))
    {
      stopForeground(true);
      hangUp();
    }
    for (;;)
    {
      return;
      if ((getPackageName() + ".DECLINE_CALL").equals(paramIntent.getAction()))
      {
        stopForeground(true);
        declineIncomingCall(4, null);
      }
      else if ((getPackageName() + ".ANSWER_CALL").equals(paramIntent.getAction()))
      {
        acceptIncomingCallFromNotification();
      }
    }
  }
  
  public abstract void hangUp();
  
  public abstract void hangUp(Runnable paramRunnable);
  
  public boolean hasEarpiece()
  {
    boolean bool = false;
    if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null) && (this.systemCallConnection.getCallAudioState() != null)) {
      if ((this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 0x5) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      if (((TelephonyManager)getSystemService("phone")).getPhoneType() != 0)
      {
        bool = true;
        continue;
      }
      if (this.mHasEarpiece != null)
      {
        bool = this.mHasEarpiece.booleanValue();
        continue;
      }
      try
      {
        AudioManager localAudioManager = (AudioManager)getSystemService("audio");
        Method localMethod = AudioManager.class.getMethod("getDevicesForStream", new Class[] { Integer.TYPE });
        int i = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt(null);
        if ((((Integer)localMethod.invoke(localAudioManager, new Object[] { Integer.valueOf(0) })).intValue() & i) == i) {}
        for (this.mHasEarpiece = Boolean.TRUE;; this.mHasEarpiece = Boolean.FALSE)
        {
          bool = this.mHasEarpiece.booleanValue();
          break;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Error while checking earpiece! ", localThrowable);
          }
          this.mHasEarpiece = Boolean.TRUE;
        }
      }
    }
  }
  
  protected void initializeAccountRelatedThings()
  {
    updateServerConfig();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
    ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
    this.controller = createController();
    this.controller.setConnectionStateListener(this);
  }
  
  public boolean isBluetoothHeadsetConnected()
  {
    boolean bool;
    if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null) && (this.systemCallConnection.getCallAudioState() != null)) {
      if ((this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 0x2) != 0) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      bool = this.isBtHeadsetConnected;
    }
  }
  
  protected boolean isFinished()
  {
    if ((this.currentState == 11) || (this.currentState == 4)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isMicMute()
  {
    return this.micMute;
  }
  
  public boolean isOutgoing()
  {
    return this.isOutgoing;
  }
  
  public boolean isSpeakerphoneOn()
  {
    boolean bool = true;
    int i;
    if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null) && (this.systemCallConnection.getCallAudioState() != null))
    {
      i = this.systemCallConnection.getCallAudioState().getRoute();
      if (hasEarpiece()) {
        if (i != 8) {}
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (i != 2)
      {
        bool = false;
        continue;
        if ((this.audioConfigured) && (!USE_CONNECTION_SERVICE))
        {
          AudioManager localAudioManager = (AudioManager)getSystemService("audio");
          if (hasEarpiece()) {
            bool = localAudioManager.isSpeakerphoneOn();
          } else {
            bool = localAudioManager.isBluetoothScoOn();
          }
        }
        else
        {
          bool = this.speakerphoneStateToSet;
        }
      }
    }
  }
  
  public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
  
  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == 1) {}
    for (this.haveAudioFocus = true;; this.haveAudioFocus = false) {
      return;
    }
  }
  
  public void onConnectionStateChanged(int paramInt)
  {
    if (paramInt == 4)
    {
      callFailed();
      return;
    }
    if (paramInt == 3)
    {
      if (this.spPlayID != 0)
      {
        this.soundPool.stop(this.spPlayID);
        this.spPlayID = 0;
      }
      if (!this.wasEstablished)
      {
        this.wasEstablished = true;
        if (!this.isProximityNear)
        {
          Vibrator localVibrator = (Vibrator)getSystemService("vibrator");
          if (localVibrator.hasVibrator()) {
            localVibrator.vibrate(100L);
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (VoIPBaseService.this.controller == null) {}
            for (;;)
            {
              return;
              int i = VoIPBaseService.this.getStatsNetworkType();
              StatsController.getInstance(VoIPBaseService.this.currentAccount).incrementTotalCallsTime(i, 5);
              AndroidUtilities.runOnUIThread(this, 5000L);
            }
          }
        }, 5000L);
        if (!this.isOutgoing) {
          break label170;
        }
        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(getStatsNetworkType(), 0, 1);
      }
    }
    for (;;)
    {
      if (paramInt == 5)
      {
        if (this.spPlayID != 0) {
          this.soundPool.stop(this.spPlayID);
        }
        this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0F, 1.0F, 0, -1, 1.0F);
      }
      dispatchStateChanged(paramInt);
      break;
      label170:
      StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
    }
  }
  
  protected void onControllerPreRelease() {}
  
  public void onCreate()
  {
    super.onCreate();
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("=============== VoIPService STARTING ===============");
    }
    AudioManager localAudioManager = (AudioManager)getSystemService("audio");
    if ((Build.VERSION.SDK_INT >= 17) && (localAudioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") != null)) {
      VoIPController.setNativeBufferSize(Integer.parseInt(localAudioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
    }
    for (;;)
    {
      try
      {
        this.cpuWakelock = ((PowerManager)getSystemService("power")).newWakeLock(1, "telegram-voip");
        this.cpuWakelock.acquire();
        if (!localAudioManager.isBluetoothScoAvailableOffCall()) {
          break label391;
        }
        Object localObject1 = BluetoothAdapter.getDefaultAdapter();
        this.btAdapter = ((BluetoothAdapter)localObject1);
        localObject1 = new android/content/IntentFilter;
        ((IntentFilter)localObject1).<init>();
        ((IntentFilter)localObject1).addAction("android.net.conn.CONNECTIVITY_CHANGE");
        if (!USE_CONNECTION_SERVICE)
        {
          ((IntentFilter)localObject1).addAction("android.intent.action.HEADSET_PLUG");
          if (this.btAdapter != null)
          {
            ((IntentFilter)localObject1).addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
            ((IntentFilter)localObject1).addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
          }
          ((IntentFilter)localObject1).addAction("android.intent.action.PHONE_STATE");
        }
        registerReceiver(this.receiver, (IntentFilter)localObject1);
        localObject1 = new android/media/SoundPool;
        ((SoundPool)localObject1).<init>(1, 0, 0);
        this.soundPool = ((SoundPool)localObject1);
        this.spConnectingId = this.soundPool.load(this, NUM, 1);
        this.spRingbackID = this.soundPool.load(this, NUM, 1);
        this.spFailedID = this.soundPool.load(this, NUM, 1);
        this.spEndId = this.soundPool.load(this, NUM, 1);
        this.spBusyId = this.soundPool.load(this, NUM, 1);
        localObject1 = new android/content/ComponentName;
        ((ComponentName)localObject1).<init>(this, VoIPMediaButtonReceiver.class);
        localAudioManager.registerMediaButtonEventReceiver((ComponentName)localObject1);
        if ((!USE_CONNECTION_SERVICE) && (this.btAdapter != null) && (this.btAdapter.isEnabled()))
        {
          if (this.btAdapter.getProfileConnectionState(1) != 2) {
            break label396;
          }
          bool = true;
          updateBluetoothHeadsetState(bool);
          localObject1 = this.stateListeners.iterator();
          if (((Iterator)localObject1).hasNext())
          {
            ((StateListener)((Iterator)localObject1).next()).onAudioSettingsChanged();
            continue;
          }
        }
        return;
      }
      catch (Exception localException)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("error initializing voip controller", localException);
        }
        callFailed();
      }
      VoIPController.setNativeBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
      continue;
      label391:
      Object localObject2 = null;
      continue;
      label396:
      boolean bool = false;
    }
  }
  
  public void onDestroy()
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("=============== VoIPService STOPPING ===============");
    }
    stopForeground(true);
    stopRinging();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
    Object localObject = (SensorManager)getSystemService("sensor");
    if (((SensorManager)localObject).getDefaultSensor(8) != null) {
      ((SensorManager)localObject).unregisterListener(this);
    }
    if ((this.proximityWakelock != null) && (this.proximityWakelock.isHeld())) {
      this.proximityWakelock.release();
    }
    unregisterReceiver(this.receiver);
    if (this.timeoutRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.timeoutRunnable);
      this.timeoutRunnable = null;
    }
    super.onDestroy();
    sharedInstance = null;
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndedCall, new Object[0]);
      }
    });
    if ((this.controller != null) && (this.controllerStarted))
    {
      this.lastKnownDuration = this.controller.getCallDuration();
      updateStats();
      StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), (int)(this.lastKnownDuration / 1000L) % 5);
      onControllerPreRelease();
      this.controller.release();
      this.controller = null;
    }
    this.cpuWakelock.release();
    localObject = (AudioManager)getSystemService("audio");
    if (!USE_CONNECTION_SERVICE) {
      if ((this.isBtHeadsetConnected) && (!this.playingSound))
      {
        ((AudioManager)localObject).stopBluetoothSco();
        ((AudioManager)localObject).setSpeakerphoneOn(false);
      }
    }
    try
    {
      ((AudioManager)localObject).setMode(0);
      ((AudioManager)localObject).abandonAudioFocus(this);
      ((AudioManager)localObject).unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
      if (this.haveAudioFocus) {
        ((AudioManager)localObject).abandonAudioFocus(this);
      }
      if (!this.playingSound) {
        this.soundPool.release();
      }
      if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null)) {
        this.systemCallConnection.destroy();
      }
      ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
      org.telegram.ui.Components.voip.VoIPHelper.lastCallTime = System.currentTimeMillis();
      return;
    }
    catch (SecurityException localSecurityException)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("Error setting audio more to normal", localSecurityException);
        }
      }
    }
  }
  
  @SuppressLint({"NewApi"})
  public void onSensorChanged(SensorEvent paramSensorEvent)
  {
    boolean bool = true;
    if (paramSensorEvent.sensor.getType() == 8)
    {
      AudioManager localAudioManager = (AudioManager)getSystemService("audio");
      if ((!this.isHeadsetPlugged) && (!localAudioManager.isSpeakerphoneOn()) && ((!isBluetoothHeadsetConnected()) || (!localAudioManager.isBluetoothScoOn()))) {
        break label54;
      }
    }
    for (;;)
    {
      return;
      label54:
      if (paramSensorEvent.values[0] < Math.min(paramSensorEvent.sensor.getMaximumRange(), 3.0F)) {}
      for (;;)
      {
        if (bool == this.isProximityNear) {
          break label147;
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("proximity " + bool);
        }
        this.isProximityNear = bool;
        try
        {
          if (!this.isProximityNear) {
            break label149;
          }
          this.proximityWakelock.acquire();
        }
        catch (Exception paramSensorEvent)
        {
          FileLog.e(paramSensorEvent);
        }
        break;
        bool = false;
      }
      label147:
      continue;
      label149:
      this.proximityWakelock.release(1);
    }
  }
  
  public void onSignalBarCountChanged(int paramInt)
  {
    this.signalBarCount = paramInt;
    for (int i = 0; i < this.stateListeners.size(); i++) {
      ((StateListener)this.stateListeners.get(i)).onSignalBarsCountChanged(paramInt);
    }
  }
  
  public void registerStateListener(StateListener paramStateListener)
  {
    this.stateListeners.add(paramStateListener);
    if (this.currentState != 0) {
      paramStateListener.onStateChanged(this.currentState);
    }
    if (this.signalBarCount != 0) {
      paramStateListener.onSignalBarsCountChanged(this.signalBarCount);
    }
  }
  
  public void setMicMute(boolean paramBoolean)
  {
    this.micMute = paramBoolean;
    if (this.controller != null) {
      this.controller.setMicMute(paramBoolean);
    }
  }
  
  protected void showIncomingNotification(String paramString, CharSequence paramCharSequence, TLObject paramTLObject, List<TLRPC.User> paramList, int paramInt, Class<? extends Activity> paramClass)
  {
    Object localObject1 = new Intent(this, paramClass);
    ((Intent)localObject1).addFlags(805306368);
    Notification.Builder localBuilder = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipInCallBranding", NUM)).setContentText(paramString).setSmallIcon(NUM).setSubText(paramCharSequence).setContentIntent(PendingIntent.getActivity(this, 0, (Intent)localObject1, 0));
    Object localObject2;
    int i;
    int k;
    if (Build.VERSION.SDK_INT >= 26)
    {
      localObject2 = MessagesController.getGlobalNotificationsSettings();
      i = ((SharedPreferences)localObject2).getInt("calls_notification_channel", 0);
      paramList = (NotificationManager)getSystemService("notification");
      paramClass = paramList.getNotificationChannel("incoming_calls" + i);
      int j = 1;
      paramInt = i;
      k = j;
      if (paramClass != null)
      {
        if ((paramClass.getImportance() < 4) || (paramClass.getSound() != null) || (paramClass.getVibrationPattern() != null))
        {
          FileLog.d("User messed up the notification channel; deleting it and creating a proper one");
          paramList.deleteNotificationChannel("incoming_calls" + i);
          paramInt = i + 1;
          ((SharedPreferences)localObject2).edit().putInt("calls_notification_channel", paramInt).commit();
          k = j;
        }
      }
      else
      {
        if (k != 0)
        {
          paramClass = new NotificationChannel("incoming_calls" + paramInt, LocaleController.getString("IncomingCalls", NUM), 4);
          paramClass.setSound(null, null);
          paramClass.enableVibration(false);
          paramClass.enableLights(false);
          paramList.createNotificationChannel(paramClass);
        }
        localBuilder.setChannelId("incoming_calls" + paramInt);
      }
    }
    else
    {
      localObject2 = new Intent(this, VoIPActionsReceiver.class);
      ((Intent)localObject2).setAction(getPackageName() + ".DECLINE_CALL");
      ((Intent)localObject2).putExtra("call_id", getCallID());
      paramClass = LocaleController.getString("VoipDeclineCall", NUM);
      paramList = paramClass;
      if (Build.VERSION.SDK_INT >= 24)
      {
        paramList = new SpannableString(paramClass);
        ((SpannableString)paramList).setSpan(new ForegroundColorSpan(-769226), 0, paramList.length(), 0);
      }
      localObject2 = PendingIntent.getBroadcast(this, 0, (Intent)localObject2, 268435456);
      localBuilder.addAction(NUM, paramList, (PendingIntent)localObject2);
      Intent localIntent = new Intent(this, VoIPActionsReceiver.class);
      localIntent.setAction(getPackageName() + ".ANSWER_CALL");
      localIntent.putExtra("call_id", getCallID());
      paramClass = LocaleController.getString("VoipAnswerCall", NUM);
      paramList = paramClass;
      if (Build.VERSION.SDK_INT >= 24)
      {
        paramList = new SpannableString(paramClass);
        ((SpannableString)paramList).setSpan(new ForegroundColorSpan(-16733696), 0, paramList.length(), 0);
      }
      paramClass = PendingIntent.getBroadcast(this, 0, localIntent, 268435456);
      localBuilder.addAction(NUM, paramList, paramClass);
      localBuilder.setPriority(2);
      if (Build.VERSION.SDK_INT >= 17) {
        localBuilder.setShowWhen(false);
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        localBuilder.setColor(-13851168);
        localBuilder.setVibrate(new long[0]);
        localBuilder.setCategory("call");
        localBuilder.setFullScreenIntent(PendingIntent.getActivity(this, 0, (Intent)localObject1, 0), true);
      }
      paramList = localBuilder.getNotification();
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = getPackageName();
        if (!LocaleController.isRTL) {
          break label914;
        }
        paramInt = NUM;
        label721:
        localObject1 = new RemoteViews((String)localObject1, paramInt);
        ((RemoteViews)localObject1).setTextViewText(NUM, paramString);
        if (!TextUtils.isEmpty(paramCharSequence)) {
          break label942;
        }
        ((RemoteViews)localObject1).setViewVisibility(NUM, 8);
        if (UserConfig.getActivatedAccountsCount() <= 1) {
          break label922;
        }
        paramString = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ((RemoteViews)localObject1).setTextViewText(NUM, LocaleController.formatString("VoipInCallBrandingWithName", NUM, new Object[] { ContactsController.formatName(paramString.first_name, paramString.last_name) }));
      }
    }
    for (;;)
    {
      ((RemoteViews)localObject1).setTextViewText(NUM, LocaleController.getString("VoipAnswerCall", NUM));
      ((RemoteViews)localObject1).setTextViewText(NUM, LocaleController.getString("VoipDeclineCall", NUM));
      ((RemoteViews)localObject1).setImageViewBitmap(NUM, getRoundAvatarBitmap(paramTLObject));
      ((RemoteViews)localObject1).setOnClickPendingIntent(NUM, paramClass);
      ((RemoteViews)localObject1).setOnClickPendingIntent(NUM, (PendingIntent)localObject2);
      paramList.bigContentView = ((RemoteViews)localObject1);
      paramList.headsUpContentView = ((RemoteViews)localObject1);
      startForeground(202, paramList);
      return;
      k = 0;
      paramInt = i;
      break;
      label914:
      paramInt = NUM;
      break label721;
      label922:
      ((RemoteViews)localObject1).setTextViewText(NUM, LocaleController.getString("VoipInCallBranding", NUM));
    }
    label942:
    if (UserConfig.getActivatedAccountsCount() > 1)
    {
      paramString = UserConfig.getInstance(this.currentAccount).getCurrentUser();
      ((RemoteViews)localObject1).setTextViewText(NUM, LocaleController.formatString("VoipAnsweringAsAccount", NUM, new Object[] { ContactsController.formatName(paramString.first_name, paramString.last_name) }));
    }
    for (;;)
    {
      ((RemoteViews)localObject1).setTextViewText(NUM, paramCharSequence);
      break;
      ((RemoteViews)localObject1).setViewVisibility(NUM, 8);
    }
  }
  
  protected abstract void showNotification();
  
  protected void showNotification(String paramString, TLRPC.FileLocation paramFileLocation, Class<? extends Activity> paramClass)
  {
    paramClass = new Intent(this, paramClass);
    paramClass.addFlags(805306368);
    paramString = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", NUM)).setContentText(paramString).setSmallIcon(NUM).setContentIntent(PendingIntent.getActivity(this, 0, paramClass, 0));
    if (Build.VERSION.SDK_INT >= 16)
    {
      paramClass = new Intent(this, VoIPActionsReceiver.class);
      paramClass.setAction(getPackageName() + ".END_CALL");
      paramString.addAction(NUM, LocaleController.getString("VoipEndCall", NUM), PendingIntent.getBroadcast(this, 0, paramClass, 134217728));
      paramString.setPriority(2);
    }
    if (Build.VERSION.SDK_INT >= 17) {
      paramString.setShowWhen(false);
    }
    if (Build.VERSION.SDK_INT >= 21) {
      paramString.setColor(-13851168);
    }
    if (Build.VERSION.SDK_INT >= 26) {
      paramString.setChannelId("Other3");
    }
    if (paramFileLocation != null)
    {
      paramClass = ImageLoader.getInstance().getImageFromMemory(paramFileLocation, null, "50_50");
      if (paramClass != null) {
        paramString.setLargeIcon(paramClass.getBitmap());
      }
    }
    else
    {
      this.ongoingCallNotification = paramString.getNotification();
      startForeground(201, this.ongoingCallNotification);
      return;
    }
    for (;;)
    {
      float f;
      try
      {
        f = 160.0F / AndroidUtilities.dp(50.0F);
        paramClass = new android/graphics/BitmapFactory$Options;
        paramClass.<init>();
        if (f >= 1.0F) {
          break label304;
        }
        i = 1;
        paramClass.inSampleSize = i;
        paramFileLocation = BitmapFactory.decodeFile(FileLoader.getPathToAttach(paramFileLocation, true).toString(), paramClass);
        if (paramFileLocation == null) {
          break;
        }
        paramString.setLargeIcon(paramFileLocation);
      }
      catch (Throwable paramFileLocation)
      {
        FileLog.e(paramFileLocation);
      }
      break;
      label304:
      int i = (int)f;
    }
  }
  
  protected abstract void startRinging();
  
  protected void startRingtoneAndVibration(int paramInt)
  {
    localSharedPreferences = MessagesController.getNotificationsSettings(this.currentAccount);
    AudioManager localAudioManager = (AudioManager)getSystemService("audio");
    if (localAudioManager.getRingerMode() != 0)
    {
      i = 1;
      j = i;
      if (Build.VERSION.SDK_INT < 21) {}
    }
    try
    {
      int k = Settings.Global.getInt(getContentResolver(), "zen_mode");
      j = i;
      if (i != 0)
      {
        if (k != 0) {
          break label389;
        }
        j = 1;
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        label70:
        j = i;
      }
    }
    if (j != 0)
    {
      if (!USE_CONNECTION_SERVICE) {
        localAudioManager.requestAudioFocus(this, 2, 1);
      }
      this.ringtonePlayer = new MediaPlayer();
      this.ringtonePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
      {
        public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
        {
          VoIPBaseService.this.ringtonePlayer.start();
        }
      });
      this.ringtonePlayer.setLooping(true);
      this.ringtonePlayer.setAudioStreamType(2);
    }
    for (;;)
    {
      try
      {
        localObject = new java/lang/StringBuilder;
        ((StringBuilder)localObject).<init>();
        if (!localSharedPreferences.getBoolean("custom_" + paramInt, false)) {
          continue;
        }
        localObject = new java/lang/StringBuilder;
        ((StringBuilder)localObject).<init>();
        localObject = localSharedPreferences.getString("ringtone_path_" + paramInt, RingtoneManager.getDefaultUri(1).toString());
        this.ringtonePlayer.setDataSource(this, Uri.parse((String)localObject));
        this.ringtonePlayer.prepareAsync();
      }
      catch (Exception localException1)
      {
        Object localObject;
        label389:
        FileLog.e(localException1);
        if (this.ringtonePlayer == null) {
          continue;
        }
        this.ringtonePlayer.release();
        this.ringtonePlayer = null;
        continue;
        paramInt = localSharedPreferences.getInt("vibrate_calls", 0);
        continue;
        if (paramInt != 3) {
          continue;
        }
        long l = 700L * 2L;
        continue;
      }
      if (!localSharedPreferences.getBoolean("custom_" + paramInt, false)) {
        continue;
      }
      paramInt = localSharedPreferences.getInt("calls_vibrate_" + paramInt, 0);
      if (((paramInt != 2) && (paramInt != 4) && ((localAudioManager.getRingerMode() == 1) || (localAudioManager.getRingerMode() == 2))) || ((paramInt == 4) && (localAudioManager.getRingerMode() == 1)))
      {
        this.vibrator = ((Vibrator)getSystemService("vibrator"));
        l = 700L;
        if (paramInt != 1) {
          continue;
        }
        l = 700L / 2L;
        this.vibrator.vibrate(new long[] { 0L, l, 500L }, 0);
      }
      return;
      i = 0;
      break;
      j = 0;
      break label70;
      localObject = localSharedPreferences.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
    }
  }
  
  public void stopRinging()
  {
    if (this.ringtonePlayer != null)
    {
      this.ringtonePlayer.stop();
      this.ringtonePlayer.release();
      this.ringtonePlayer = null;
    }
    if (this.vibrator != null)
    {
      this.vibrator.cancel();
      this.vibrator = null;
    }
  }
  
  public void toggleSpeakerphoneOrShowRouteSheet(Activity paramActivity)
  {
    int i;
    if ((isBluetoothHeadsetConnected()) && (hasEarpiece()))
    {
      BottomSheet.Builder localBuilder = new BottomSheet.Builder(paramActivity);
      String str1 = LocaleController.getString("VoipAudioRoutingBluetooth", NUM);
      paramActivity = LocaleController.getString("VoipAudioRoutingEarpiece", NUM);
      String str2 = LocaleController.getString("VoipAudioRoutingSpeaker", NUM);
      DialogInterface.OnClickListener local3 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = (AudioManager)VoIPBaseService.this.getSystemService("audio");
          if (VoIPBaseService.getSharedInstance() == null) {
            return;
          }
          if ((VoIPBaseService.USE_CONNECTION_SERVICE) && (VoIPBaseService.this.systemCallConnection != null)) {
            switch (paramAnonymousInt)
            {
            }
          }
          for (;;)
          {
            paramAnonymousDialogInterface = VoIPBaseService.this.stateListeners.iterator();
            while (paramAnonymousDialogInterface.hasNext()) {
              ((VoIPBaseService.StateListener)paramAnonymousDialogInterface.next()).onAudioSettingsChanged();
            }
            VoIPBaseService.this.systemCallConnection.setAudioRoute(2);
            continue;
            VoIPBaseService.this.systemCallConnection.setAudioRoute(5);
            continue;
            VoIPBaseService.this.systemCallConnection.setAudioRoute(8);
            continue;
            if ((VoIPBaseService.this.audioConfigured) && (!VoIPBaseService.USE_CONNECTION_SERVICE))
            {
              switch (paramAnonymousInt)
              {
              }
              for (;;)
              {
                VoIPBaseService.this.updateOutputGainControlState();
                break;
                if (!VoIPBaseService.this.bluetoothScoActive)
                {
                  VoIPBaseService.this.needSwitchToBluetoothAfterScoActivates = true;
                  paramAnonymousDialogInterface.startBluetoothSco();
                }
                else
                {
                  paramAnonymousDialogInterface.setBluetoothScoOn(true);
                  paramAnonymousDialogInterface.setSpeakerphoneOn(false);
                  continue;
                  if (VoIPBaseService.this.bluetoothScoActive) {
                    paramAnonymousDialogInterface.stopBluetoothSco();
                  }
                  paramAnonymousDialogInterface.setSpeakerphoneOn(false);
                  paramAnonymousDialogInterface.setBluetoothScoOn(false);
                  continue;
                  if (VoIPBaseService.this.bluetoothScoActive) {
                    paramAnonymousDialogInterface.stopBluetoothSco();
                  }
                  paramAnonymousDialogInterface.setBluetoothScoOn(false);
                  paramAnonymousDialogInterface.setSpeakerphoneOn(true);
                }
              }
            }
            switch (paramAnonymousInt)
            {
            default: 
              break;
            case 0: 
              VoIPBaseService.this.audioRouteToSet = 2;
              break;
            case 1: 
              VoIPBaseService.this.audioRouteToSet = 0;
              break;
            case 2: 
              VoIPBaseService.this.audioRouteToSet = 1;
            }
          }
        }
      };
      paramActivity = localBuilder.setItems(new CharSequence[] { str1, paramActivity, str2 }, new int[] { NUM, NUM, NUM }, local3).create();
      paramActivity.setBackgroundColor(-13948117);
      paramActivity.show();
      paramActivity = paramActivity.getSheetContainer();
      i = 0;
    }
    while (i < paramActivity.getChildCount())
    {
      ((BottomSheet.BottomSheetCell)paramActivity.getChildAt(i)).setTextColor(-1);
      i++;
      continue;
      if ((USE_CONNECTION_SERVICE) && (this.systemCallConnection != null) && (this.systemCallConnection.getCallAudioState() != null))
      {
        if (hasEarpiece())
        {
          paramActivity = this.systemCallConnection;
          if (this.systemCallConnection.getCallAudioState().getRoute() == 8) {}
          for (i = 5;; i = 8)
          {
            paramActivity.setAudioRoute(i);
            paramActivity = this.stateListeners.iterator();
            while (paramActivity.hasNext()) {
              ((StateListener)paramActivity.next()).onAudioSettingsChanged();
            }
          }
        }
        paramActivity = this.systemCallConnection;
        if (this.systemCallConnection.getCallAudioState().getRoute() == 2) {}
        for (i = 5;; i = 2)
        {
          paramActivity.setAudioRoute(i);
          break;
        }
      }
      if ((this.audioConfigured) && (!USE_CONNECTION_SERVICE))
      {
        paramActivity = (AudioManager)getSystemService("audio");
        if (hasEarpiece())
        {
          if (!paramActivity.isSpeakerphoneOn()) {}
          for (bool = true;; bool = false)
          {
            paramActivity.setSpeakerphoneOn(bool);
            updateOutputGainControlState();
            break;
          }
        }
        if (!paramActivity.isBluetoothScoOn()) {}
        for (bool = true;; bool = false)
        {
          paramActivity.setBluetoothScoOn(bool);
          break;
        }
      }
      if (!this.speakerphoneStateToSet) {}
      for (boolean bool = true;; bool = false)
      {
        this.speakerphoneStateToSet = bool;
        break;
      }
    }
  }
  
  public void unregisterStateListener(StateListener paramStateListener)
  {
    this.stateListeners.remove(paramStateListener);
  }
  
  protected void updateBluetoothHeadsetState(boolean paramBoolean)
  {
    if (paramBoolean == this.isBtHeadsetConnected) {
      return;
    }
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("updateBluetoothHeadsetState: " + paramBoolean);
    }
    this.isBtHeadsetConnected = paramBoolean;
    final Object localObject = (AudioManager)getSystemService("audio");
    if (paramBoolean) {
      if (this.bluetoothScoActive)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("SCO already active, setting audio routing");
        }
        ((AudioManager)localObject).setSpeakerphoneOn(false);
        ((AudioManager)localObject).setBluetoothScoOn(true);
      }
    }
    for (;;)
    {
      localObject = this.stateListeners.iterator();
      while (((Iterator)localObject).hasNext()) {
        ((StateListener)((Iterator)localObject).next()).onAudioSettingsChanged();
      }
      break;
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("startBluetoothSco");
      }
      this.needSwitchToBluetoothAfterScoActivates = true;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          localObject.startBluetoothSco();
        }
      }, 500L);
      continue;
      this.bluetoothScoActive = false;
    }
  }
  
  protected void updateNetworkType()
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
    this.lastNetInfo = localNetworkInfo;
    int i = 0;
    int j = i;
    if (localNetworkInfo != null) {
      switch (localNetworkInfo.getType())
      {
      default: 
        j = i;
      }
    }
    for (;;)
    {
      if (this.controller != null) {
        this.controller.setNetworkType(j);
      }
      return;
      switch (localNetworkInfo.getSubtype())
      {
      case 4: 
      case 11: 
      case 14: 
      default: 
        j = 11;
        break;
      case 1: 
        j = 1;
        break;
      case 2: 
      case 7: 
        j = 2;
        break;
      case 3: 
      case 5: 
        j = 3;
        break;
      case 6: 
      case 8: 
      case 9: 
      case 10: 
      case 12: 
      case 15: 
        j = 4;
        break;
      case 13: 
        j = 5;
        continue;
        j = 6;
        continue;
        j = 7;
      }
    }
  }
  
  public void updateOutputGainControlState()
  {
    int i = 0;
    int j = 1;
    if ((this.controller == null) || (!this.controllerStarted)) {
      return;
    }
    Object localObject;
    boolean bool;
    if (!USE_CONNECTION_SERVICE)
    {
      localObject = (AudioManager)getSystemService("audio");
      VoIPController localVoIPController = this.controller;
      if ((hasEarpiece()) && (!((AudioManager)localObject).isSpeakerphoneOn()) && (!((AudioManager)localObject).isBluetoothScoOn()) && (!this.isHeadsetPlugged)) {}
      for (bool = true;; bool = false)
      {
        localVoIPController.setAudioOutputGainControlEnabled(bool);
        localVoIPController = this.controller;
        if (!this.isHeadsetPlugged)
        {
          i = j;
          if (hasEarpiece())
          {
            i = j;
            if (!((AudioManager)localObject).isSpeakerphoneOn())
            {
              i = j;
              if (!((AudioManager)localObject).isBluetoothScoOn())
              {
                i = j;
                if (this.isHeadsetPlugged) {}
              }
            }
          }
        }
        else
        {
          i = 0;
        }
        localVoIPController.setEchoCancellationStrength(i);
        break;
      }
    }
    if (this.systemCallConnection.getCallAudioState().getRoute() == 1)
    {
      bool = true;
      label163:
      this.controller.setAudioOutputGainControlEnabled(bool);
      localObject = this.controller;
      if (!bool) {
        break label196;
      }
    }
    for (;;)
    {
      ((VoIPController)localObject).setEchoCancellationStrength(i);
      break;
      bool = false;
      break label163;
      label196:
      i = 1;
    }
  }
  
  protected abstract void updateServerConfig();
  
  protected void updateStats()
  {
    this.controller.getStats(this.stats);
    long l1 = this.stats.bytesSentWifi - this.prevStats.bytesSentWifi;
    long l2 = this.stats.bytesRecvdWifi - this.prevStats.bytesRecvdWifi;
    long l3 = this.stats.bytesSentMobile - this.prevStats.bytesSentMobile;
    long l4 = this.stats.bytesRecvdMobile - this.prevStats.bytesRecvdMobile;
    Object localObject = this.stats;
    this.stats = this.prevStats;
    this.prevStats = ((VoIPController.Stats)localObject);
    if (l1 > 0L) {
      StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, l1);
    }
    if (l2 > 0L) {
      StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, l2);
    }
    if (l3 > 0L)
    {
      localObject = StatsController.getInstance(this.currentAccount);
      if ((this.lastNetInfo != null) && (this.lastNetInfo.isRoaming()))
      {
        i = 2;
        ((StatsController)localObject).incrementSentBytesCount(i, 0, l3);
      }
    }
    else if (l4 > 0L)
    {
      localObject = StatsController.getInstance(this.currentAccount);
      if ((this.lastNetInfo == null) || (!this.lastNetInfo.isRoaming())) {
        break label234;
      }
    }
    label234:
    for (int i = 2;; i = 0)
    {
      ((StatsController)localObject).incrementReceivedBytesCount(i, 0, l4);
      return;
      i = 0;
      break;
    }
  }
  
  @TargetApi(26)
  public class CallConnection
    extends Connection
  {
    public CallConnection()
    {
      setConnectionProperties(128);
      setAudioModeIsVoip(true);
    }
    
    public void onAnswer()
    {
      VoIPBaseService.this.acceptIncomingCallFromNotification();
    }
    
    public void onCallAudioStateChanged(CallAudioState paramCallAudioState)
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("ConnectionService call audio state changed: " + paramCallAudioState);
      }
      paramCallAudioState = VoIPBaseService.this.stateListeners.iterator();
      while (paramCallAudioState.hasNext()) {
        ((VoIPBaseService.StateListener)paramCallAudioState.next()).onAudioSettingsChanged();
      }
    }
    
    public void onCallEvent(String paramString, Bundle paramBundle)
    {
      super.onCallEvent(paramString, paramBundle);
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("ConnectionService onCallEvent " + paramString);
      }
    }
    
    public void onDisconnect()
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("ConnectionService onDisconnect");
      }
      setDisconnected(new DisconnectCause(2));
      destroy();
      VoIPBaseService.this.systemCallConnection = null;
      VoIPBaseService.this.hangUp();
    }
    
    public void onReject()
    {
      VoIPBaseService.this.declineIncomingCall(1, null);
    }
    
    public void onShowIncomingCallUi()
    {
      VoIPBaseService.this.startRinging();
    }
    
    public void onStateChanged(int paramInt)
    {
      super.onStateChanged(paramInt);
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("ConnectionService onStateChanged " + paramInt);
      }
    }
  }
  
  public static abstract interface StateListener
  {
    public abstract void onAudioSettingsChanged();
    
    public abstract void onSignalBarsCountChanged(int paramInt);
    
    public abstract void onStateChanged(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/VoIPBaseService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */