package org.telegram.messenger.voip;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build.VERSION;
import android.os.SystemClock;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.TL_phoneConnection;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPController
{
  public static final int DATA_SAVING_ALWAYS = 2;
  public static final int DATA_SAVING_MOBILE = 1;
  public static final int DATA_SAVING_NEVER = 0;
  public static final int ERROR_AUDIO_IO = 3;
  public static final int ERROR_CONNECTION_SERVICE = -5;
  public static final int ERROR_INCOMPATIBLE = 1;
  public static final int ERROR_INSECURE_UPGRADE = -4;
  public static final int ERROR_LOCALIZED = -3;
  public static final int ERROR_PEER_OUTDATED = -1;
  public static final int ERROR_PRIVACY = -2;
  public static final int ERROR_TIMEOUT = 2;
  public static final int ERROR_UNKNOWN = 0;
  public static final int NET_TYPE_3G = 3;
  public static final int NET_TYPE_DIALUP = 10;
  public static final int NET_TYPE_EDGE = 2;
  public static final int NET_TYPE_ETHERNET = 7;
  public static final int NET_TYPE_GPRS = 1;
  public static final int NET_TYPE_HSPA = 4;
  public static final int NET_TYPE_LTE = 5;
  public static final int NET_TYPE_OTHER_HIGH_SPEED = 8;
  public static final int NET_TYPE_OTHER_LOW_SPEED = 9;
  public static final int NET_TYPE_OTHER_MOBILE = 11;
  public static final int NET_TYPE_UNKNOWN = 0;
  public static final int NET_TYPE_WIFI = 6;
  public static final int PEER_CAP_GROUP_CALLS = 1;
  public static final int STATE_ESTABLISHED = 3;
  public static final int STATE_FAILED = 4;
  public static final int STATE_RECONNECTING = 5;
  public static final int STATE_WAIT_INIT = 1;
  public static final int STATE_WAIT_INIT_ACK = 2;
  protected long callStartTime;
  protected ConnectionStateListener listener;
  protected long nativeInst = 0L;
  
  private void callUpgradeRequestReceived()
  {
    if (this.listener != null) {
      this.listener.onCallUpgradeRequestReceived();
    }
  }
  
  private String getLogFilePath(long paramLong)
  {
    File localFile1 = VoIPHelper.getLogsDir();
    if (!BuildConfig.DEBUG)
    {
      Object localObject = localFile1.listFiles();
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(Arrays.asList((Object[])localObject));
      while (localArrayList.size() > 20)
      {
        localObject = (File)localArrayList.get(0);
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          File localFile2 = (File)localIterator.next();
          if ((localFile2.getName().endsWith(".log")) && (localFile2.lastModified() < ((File)localObject).lastModified())) {
            localObject = localFile2;
          }
        }
        ((File)localObject).delete();
        localArrayList.remove(localObject);
      }
    }
    return new File(localFile1, paramLong + ".log").getAbsolutePath();
  }
  
  private String getLogFilePath(String paramString)
  {
    Calendar localCalendar = Calendar.getInstance();
    return new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[] { Integer.valueOf(localCalendar.get(5)), Integer.valueOf(localCalendar.get(2) + 1), Integer.valueOf(localCalendar.get(1)), Integer.valueOf(localCalendar.get(11)), Integer.valueOf(localCalendar.get(12)), Integer.valueOf(localCalendar.get(13)), paramString })).getAbsolutePath();
  }
  
  public static String getVersion()
  {
    return nativeGetVersion();
  }
  
  private void groupCallKeyReceived(byte[] paramArrayOfByte)
  {
    if (this.listener != null) {
      this.listener.onGroupCallKeyReceived(paramArrayOfByte);
    }
  }
  
  private void groupCallKeySent()
  {
    if (this.listener != null) {
      this.listener.onGroupCallKeySent();
    }
  }
  
  private void handleSignalBarsChange(int paramInt)
  {
    if (this.listener != null) {
      this.listener.onSignalBarCountChanged(paramInt);
    }
  }
  
  private void handleStateChange(int paramInt)
  {
    if ((paramInt == 3) && (this.callStartTime == 0L)) {
      this.callStartTime = SystemClock.elapsedRealtime();
    }
    if (this.listener != null) {
      this.listener.onConnectionStateChanged(paramInt);
    }
  }
  
  private native void nativeConnect(long paramLong);
  
  private native void nativeDebugCtl(long paramLong, int paramInt1, int paramInt2);
  
  private native String nativeGetDebugLog(long paramLong);
  
  private native String nativeGetDebugString(long paramLong);
  
  private native int nativeGetLastError(long paramLong);
  
  private native int nativeGetPeerCapabilities(long paramLong);
  
  private native long nativeGetPreferredRelayID(long paramLong);
  
  private native void nativeGetStats(long paramLong, Stats paramStats);
  
  private static native String nativeGetVersion();
  
  private native long nativeInit();
  
  private native void nativeRelease(long paramLong);
  
  private native void nativeRequestCallUpgrade(long paramLong);
  
  private native void nativeSendGroupCallKey(long paramLong, byte[] paramArrayOfByte);
  
  private native void nativeSetAudioOutputGainControlEnabled(long paramLong, boolean paramBoolean);
  
  private native void nativeSetConfig(long paramLong, double paramDouble1, double paramDouble2, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString1, String paramString2);
  
  private native void nativeSetEchoCancellationStrength(long paramLong, int paramInt);
  
  private native void nativeSetEncryptionKey(long paramLong, byte[] paramArrayOfByte, boolean paramBoolean);
  
  private native void nativeSetMicMute(long paramLong, boolean paramBoolean);
  
  private static native void nativeSetNativeBufferSize(int paramInt);
  
  private native void nativeSetNetworkType(long paramLong, int paramInt);
  
  private native void nativeSetProxy(long paramLong, String paramString1, int paramInt, String paramString2, String paramString3);
  
  private native void nativeSetRemoteEndpoints(long paramLong, TLRPC.TL_phoneConnection[] paramArrayOfTL_phoneConnection, boolean paramBoolean1, boolean paramBoolean2, int paramInt);
  
  private native void nativeStart(long paramLong);
  
  public static void setNativeBufferSize(int paramInt)
  {
    nativeSetNativeBufferSize(paramInt);
  }
  
  public void connect()
  {
    ensureNativeInstance();
    nativeConnect(this.nativeInst);
  }
  
  public void debugCtl(int paramInt1, int paramInt2)
  {
    ensureNativeInstance();
    nativeDebugCtl(this.nativeInst, paramInt1, paramInt2);
  }
  
  protected void ensureNativeInstance()
  {
    if (this.nativeInst == 0L) {
      throw new IllegalStateException("Native instance is not valid");
    }
  }
  
  public long getCallDuration()
  {
    return SystemClock.elapsedRealtime() - this.callStartTime;
  }
  
  public String getDebugLog()
  {
    ensureNativeInstance();
    return nativeGetDebugLog(this.nativeInst);
  }
  
  public String getDebugString()
  {
    ensureNativeInstance();
    return nativeGetDebugString(this.nativeInst);
  }
  
  public int getLastError()
  {
    ensureNativeInstance();
    return nativeGetLastError(this.nativeInst);
  }
  
  public int getPeerCapabilities()
  {
    ensureNativeInstance();
    return nativeGetPeerCapabilities(this.nativeInst);
  }
  
  public long getPreferredRelayID()
  {
    ensureNativeInstance();
    return nativeGetPreferredRelayID(this.nativeInst);
  }
  
  public void getStats(Stats paramStats)
  {
    ensureNativeInstance();
    if (paramStats == null) {
      throw new NullPointerException("You're not supposed to pass null here");
    }
    nativeGetStats(this.nativeInst, paramStats);
  }
  
  public void release()
  {
    ensureNativeInstance();
    nativeRelease(this.nativeInst);
    this.nativeInst = 0L;
  }
  
  public void requestCallUpgrade()
  {
    ensureNativeInstance();
    nativeRequestCallUpgrade(this.nativeInst);
  }
  
  public void sendGroupCallKey(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("key can not be null");
    }
    if (paramArrayOfByte.length != 256) {
      throw new IllegalArgumentException("key must be 256 bytes long, got " + paramArrayOfByte.length);
    }
    ensureNativeInstance();
    nativeSendGroupCallKey(this.nativeInst, paramArrayOfByte);
  }
  
  public void setAudioOutputGainControlEnabled(boolean paramBoolean)
  {
    ensureNativeInstance();
    nativeSetAudioOutputGainControlEnabled(this.nativeInst, paramBoolean);
  }
  
  public void setConfig(double paramDouble1, double paramDouble2, int paramInt, long paramLong)
  {
    ensureNativeInstance();
    boolean bool1 = false;
    boolean bool2 = false;
    bool3 = false;
    bool4 = bool3;
    if (Build.VERSION.SDK_INT >= 16) {
      bool2 = bool1;
    }
    try
    {
      bool1 = AcousticEchoCanceler.isAvailable();
      bool2 = bool1;
      bool4 = AcousticEchoCanceler.isAvailable();
      bool2 = bool1;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        long l;
        String str1;
        String str2;
        bool4 = bool3;
      }
    }
    bool1 = MessagesController.getGlobalMainSettings().getBoolean("dbg_dump_call_stats", false);
    l = this.nativeInst;
    if ((!bool2) || (!VoIPServerConfig.getBoolean("use_system_aec", true)))
    {
      bool2 = true;
      if ((bool4) && (VoIPServerConfig.getBoolean("use_system_ns", true))) {
        break label164;
      }
      bool4 = true;
      label103:
      if (!BuildConfig.DEBUG) {
        break label170;
      }
      str1 = getLogFilePath("voip");
      label118:
      if ((!BuildConfig.DEBUG) || (!bool1)) {
        break label181;
      }
    }
    label164:
    label170:
    label181:
    for (str2 = getLogFilePath("voipStats");; str2 = null)
    {
      nativeSetConfig(l, paramDouble1, paramDouble2, paramInt, bool2, bool4, true, str1, str2);
      return;
      bool2 = false;
      break;
      bool4 = false;
      break label103;
      str1 = getLogFilePath(paramLong);
      break label118;
    }
  }
  
  public void setConnectionStateListener(ConnectionStateListener paramConnectionStateListener)
  {
    this.listener = paramConnectionStateListener;
  }
  
  public void setEchoCancellationStrength(int paramInt)
  {
    ensureNativeInstance();
    nativeSetEchoCancellationStrength(this.nativeInst, paramInt);
  }
  
  public void setEncryptionKey(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramArrayOfByte.length != 256) {
      throw new IllegalArgumentException("key length must be exactly 256 bytes but is " + paramArrayOfByte.length);
    }
    ensureNativeInstance();
    nativeSetEncryptionKey(this.nativeInst, paramArrayOfByte, paramBoolean);
  }
  
  public void setMicMute(boolean paramBoolean)
  {
    ensureNativeInstance();
    nativeSetMicMute(this.nativeInst, paramBoolean);
  }
  
  public void setNetworkType(int paramInt)
  {
    ensureNativeInstance();
    nativeSetNetworkType(this.nativeInst, paramInt);
  }
  
  public void setProxy(String paramString1, int paramInt, String paramString2, String paramString3)
  {
    ensureNativeInstance();
    if (paramString1 == null) {
      throw new NullPointerException("address can't be null");
    }
    nativeSetProxy(this.nativeInst, paramString1, paramInt, paramString2, paramString3);
  }
  
  public void setRemoteEndpoints(TLRPC.TL_phoneConnection[] paramArrayOfTL_phoneConnection, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if (paramArrayOfTL_phoneConnection.length == 0) {
      throw new IllegalArgumentException("endpoints size is 0");
    }
    for (int i = 0; i < paramArrayOfTL_phoneConnection.length; i++)
    {
      TLRPC.TL_phoneConnection localTL_phoneConnection = paramArrayOfTL_phoneConnection[i];
      if ((localTL_phoneConnection.ip == null) || (localTL_phoneConnection.ip.length() == 0)) {
        throw new IllegalArgumentException("endpoint " + localTL_phoneConnection + " has empty/null ipv4");
      }
      if ((localTL_phoneConnection.peer_tag != null) && (localTL_phoneConnection.peer_tag.length != 16)) {
        throw new IllegalArgumentException("endpoint " + localTL_phoneConnection + " has peer_tag of wrong length");
      }
    }
    ensureNativeInstance();
    nativeSetRemoteEndpoints(this.nativeInst, paramArrayOfTL_phoneConnection, paramBoolean1, paramBoolean2, paramInt);
  }
  
  public void start()
  {
    ensureNativeInstance();
    nativeStart(this.nativeInst);
  }
  
  public static abstract interface ConnectionStateListener
  {
    public abstract void onCallUpgradeRequestReceived();
    
    public abstract void onConnectionStateChanged(int paramInt);
    
    public abstract void onGroupCallKeyReceived(byte[] paramArrayOfByte);
    
    public abstract void onGroupCallKeySent();
    
    public abstract void onSignalBarCountChanged(int paramInt);
  }
  
  public static class Stats
  {
    public long bytesRecvdMobile;
    public long bytesRecvdWifi;
    public long bytesSentMobile;
    public long bytesSentWifi;
    
    public String toString()
    {
      return "Stats{bytesRecvdMobile=" + this.bytesRecvdMobile + ", bytesSentWifi=" + this.bytesSentWifi + ", bytesRecvdWifi=" + this.bytesRecvdWifi + ", bytesSentMobile=" + this.bytesSentMobile + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/VoIPController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */