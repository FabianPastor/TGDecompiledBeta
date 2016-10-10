package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;

public class ConnectionsManager
{
  public static final int ConnectionStateConnected = 3;
  public static final int ConnectionStateConnecting = 1;
  public static final int ConnectionStateUpdating = 4;
  public static final int ConnectionStateWaitingForNetwork = 2;
  public static final int ConnectionTypeDownload = 2;
  public static final int ConnectionTypeDownload2 = 65538;
  public static final int ConnectionTypeGeneric = 1;
  public static final int ConnectionTypePush = 8;
  public static final int ConnectionTypeUpload = 4;
  public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
  private static volatile ConnectionsManager Instance = null;
  public static final int RequestFlagCanCompress = 4;
  public static final int RequestFlagEnableUnauthorized = 1;
  public static final int RequestFlagFailOnServerErrors = 2;
  public static final int RequestFlagForceDownload = 32;
  public static final int RequestFlagInvokeAfter = 64;
  public static final int RequestFlagNeedQuickAck = 128;
  public static final int RequestFlagTryDifferentDc = 16;
  public static final int RequestFlagWithoutLogin = 8;
  private boolean appPaused = true;
  private int connectionState = native_getConnectionState();
  private boolean isUpdating = false;
  private int lastClassGuid = 1;
  private long lastPauseTime = System.currentTimeMillis();
  private AtomicInteger lastRequestToken = new AtomicInteger(1);
  private PowerManager.WakeLock wakeLock = null;
  
  public ConnectionsManager()
  {
    try
    {
      this.wakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
      this.wakeLock.setReferenceCounted(false);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void checkConnection()
  {
    native_setUseIpv6(useIpv6Address());
    native_setNetworkAvailable(isNetworkOnline());
  }
  
  public static ConnectionsManager getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          ConnectionsManager localConnectionsManager2 = Instance;
          localObject1 = localConnectionsManager2;
          if (localConnectionsManager2 == null) {
            localObject1 = new ConnectionsManager();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (ConnectionsManager)localObject1;
          return (ConnectionsManager)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localConnectionsManager1;
  }
  
  public static boolean isConnectedToWiFi()
  {
    try
    {
      Object localObject = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
      if (localObject != null)
      {
        localObject = ((NetworkInfo)localObject).getState();
        NetworkInfo.State localState = NetworkInfo.State.CONNECTED;
        if (localObject == localState) {
          return true;
        }
      }
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return false;
  }
  
  public static boolean isNetworkOnline()
  {
    try
    {
      Object localObject = (ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity");
      NetworkInfo localNetworkInfo = ((ConnectivityManager)localObject).getActiveNetworkInfo();
      if (localNetworkInfo != null)
      {
        if (localNetworkInfo.isConnectedOrConnecting()) {
          break label82;
        }
        if (localNetworkInfo.isAvailable()) {
          return true;
        }
      }
      localNetworkInfo = ((ConnectivityManager)localObject).getNetworkInfo(0);
      if ((localNetworkInfo == null) || (!localNetworkInfo.isConnectedOrConnecting()))
      {
        localObject = ((ConnectivityManager)localObject).getNetworkInfo(1);
        if (localObject != null)
        {
          boolean bool = ((NetworkInfo)localObject).isConnectedOrConnecting();
          if (bool) {}
        }
        else
        {
          return false;
        }
      }
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    label82:
    return true;
  }
  
  public static boolean isRoaming()
  {
    try
    {
      NetworkInfo localNetworkInfo = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
      if (localNetworkInfo != null)
      {
        boolean bool = localNetworkInfo.isRoaming();
        return bool;
      }
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return false;
  }
  
  public static native void native_applyDatacenterAddress(int paramInt1, String paramString, int paramInt2);
  
  public static native void native_bindRequestToGuid(int paramInt1, int paramInt2);
  
  public static native void native_cancelRequest(int paramInt, boolean paramBoolean);
  
  public static native void native_cancelRequestsForGuid(int paramInt);
  
  public static native void native_cleanUp();
  
  public static native int native_getConnectionState();
  
  public static native int native_getCurrentTime();
  
  public static native long native_getCurrentTimeMillis();
  
  public static native int native_getTimeDifference();
  
  public static native void native_init(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt4, boolean paramBoolean);
  
  public static native void native_pauseNetwork();
  
  public static native void native_resumeNetwork(boolean paramBoolean);
  
  public static native void native_sendRequest(int paramInt1, RequestDelegateInternal paramRequestDelegateInternal, QuickAckDelegate paramQuickAckDelegate, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5);
  
  public static native void native_setJava(boolean paramBoolean);
  
  public static native void native_setNetworkAvailable(boolean paramBoolean);
  
  public static native void native_setPushConnectionEnabled(boolean paramBoolean);
  
  public static native void native_setUseIpv6(boolean paramBoolean);
  
  public static native void native_setUserId(int paramInt);
  
  public static native void native_switchBackend();
  
  public static native void native_updateDcSettings();
  
  public static void onConnectionStateChanged(int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ConnectionsManager.access$202(ConnectionsManager.getInstance(), this.val$state);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
      }
    });
  }
  
  public static void onInternalPushReceived()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        try
        {
          if (!ConnectionsManager.getInstance().wakeLock.isHeld())
          {
            ConnectionsManager.getInstance().wakeLock.acquire(10000L);
            FileLog.d("tmessages", "acquire wakelock");
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public static void onLogout()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (UserConfig.getClientUserId() != 0)
        {
          UserConfig.clearConfig();
          MessagesController.getInstance().performLogout(false);
        }
      }
    });
  }
  
  public static void onSessionCreated()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().getDifference();
      }
    });
  }
  
  public static void onUnparsedMessageReceived(int paramInt)
  {
    try
    {
      Object localObject = NativeByteBuffer.wrap(paramInt);
      ((NativeByteBuffer)localObject).reused = true;
      localObject = TLClassStore.Instance().TLdeserialize((NativeByteBuffer)localObject, ((NativeByteBuffer)localObject).readInt32(true), true);
      if ((localObject instanceof TLRPC.Updates))
      {
        FileLog.d("tmessages", "java received " + localObject);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (ConnectionsManager.getInstance().wakeLock.isHeld())
            {
              FileLog.d("tmessages", "release wakelock");
              ConnectionsManager.getInstance().wakeLock.release();
            }
          }
        });
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesController.getInstance().processUpdates((TLRPC.Updates)this.val$message, false);
          }
        });
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public static void onUpdate()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.getInstance().updateTimerProc();
      }
    });
  }
  
  public static void onUpdateConfig(int paramInt)
  {
    try
    {
      Object localObject = NativeByteBuffer.wrap(paramInt);
      ((NativeByteBuffer)localObject).reused = true;
      localObject = TLRPC.TL_config.TLdeserialize((AbstractSerializedData)localObject, ((NativeByteBuffer)localObject).readInt32(true), true);
      if (localObject != null) {
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesController.getInstance().updateConfig(this.val$message);
          }
        });
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  @SuppressLint({"NewApi"})
  protected static boolean useIpv6Address()
  {
    if (Build.VERSION.SDK_INT < 19) {
      return false;
    }
    Object localObject;
    int i;
    label107:
    InetAddress localInetAddress;
    if (BuildVars.DEBUG_VERSION) {
      try
      {
        Enumeration localEnumeration1 = NetworkInterface.getNetworkInterfaces();
        while (localEnumeration1.hasMoreElements())
        {
          localObject = (NetworkInterface)localEnumeration1.nextElement();
          if ((((NetworkInterface)localObject).isUp()) && (!((NetworkInterface)localObject).isLoopback()) && (!((NetworkInterface)localObject).getInterfaceAddresses().isEmpty()))
          {
            FileLog.e("tmessages", "valid interface: " + localObject);
            localObject = ((NetworkInterface)localObject).getInterfaceAddresses();
            i = 0;
            if (i < ((List)localObject).size())
            {
              localInetAddress = ((InterfaceAddress)((List)localObject).get(i)).getAddress();
              if (BuildVars.DEBUG_VERSION) {
                FileLog.e("tmessages", "address: " + localInetAddress.getHostAddress());
              }
              if ((localInetAddress.isLinkLocalAddress()) || (localInetAddress.isLoopbackAddress()) || (localInetAddress.isMulticastAddress()) || (!BuildVars.DEBUG_VERSION)) {
                break label440;
              }
              FileLog.e("tmessages", "address is good");
            }
          }
        }
      }
      catch (Throwable localThrowable1)
      {
        FileLog.e("tmessages", localThrowable1);
      }
    }
    for (;;)
    {
      int n;
      int m;
      try
      {
        Enumeration localEnumeration2 = NetworkInterface.getNetworkInterfaces();
        n = 0;
        m = 0;
        if (localEnumeration2.hasMoreElements())
        {
          localObject = (NetworkInterface)localEnumeration2.nextElement();
          if ((!((NetworkInterface)localObject).isUp()) || (((NetworkInterface)localObject).isLoopback())) {
            continue;
          }
          localObject = ((NetworkInterface)localObject).getInterfaceAddresses();
          i = 0;
          k = m;
          j = n;
          n = j;
          m = k;
          if (i >= ((List)localObject).size()) {
            continue;
          }
          localInetAddress = ((InterfaceAddress)((List)localObject).get(i)).getAddress();
          m = j;
          n = k;
          if (localInetAddress.isLinkLocalAddress()) {
            break label447;
          }
          m = j;
          n = k;
          if (localInetAddress.isLoopbackAddress()) {
            break label447;
          }
          if (localInetAddress.isMulticastAddress())
          {
            m = j;
            n = k;
            break label447;
          }
          if ((localInetAddress instanceof Inet6Address))
          {
            n = 1;
            m = j;
            break label447;
          }
          m = j;
          n = k;
          if (!(localInetAddress instanceof Inet4Address)) {
            break label447;
          }
          boolean bool = localInetAddress.getHostAddress().startsWith("192.0.0.");
          m = j;
          n = k;
          if (bool) {
            break label447;
          }
          m = 1;
          n = k;
          break label447;
        }
        if ((n != 0) || (m == 0)) {
          break;
        }
        return true;
      }
      catch (Throwable localThrowable2)
      {
        FileLog.e("tmessages", localThrowable2);
        return false;
      }
      label440:
      i += 1;
      break label107;
      label447:
      i += 1;
      int j = m;
      int k = n;
    }
  }
  
  public void applyCountryPortNumber(String paramString) {}
  
  public void applyDatacenterAddress(int paramInt1, String paramString, int paramInt2)
  {
    native_applyDatacenterAddress(paramInt1, paramString, paramInt2);
  }
  
  public void bindRequestToGuid(int paramInt1, int paramInt2)
  {
    native_bindRequestToGuid(paramInt1, paramInt2);
  }
  
  public void cancelRequest(int paramInt, boolean paramBoolean)
  {
    native_cancelRequest(paramInt, paramBoolean);
  }
  
  public void cancelRequestsForGuid(int paramInt)
  {
    native_cancelRequestsForGuid(paramInt);
  }
  
  public void cleanup() {}
  
  public int generateClassGuid()
  {
    int i = this.lastClassGuid;
    this.lastClassGuid = (i + 1);
    return i;
  }
  
  public int getConnectionState()
  {
    if ((this.connectionState == 3) && (this.isUpdating)) {
      return 4;
    }
    return this.connectionState;
  }
  
  public int getCurrentTime()
  {
    return native_getCurrentTime();
  }
  
  public long getCurrentTimeMillis()
  {
    return native_getCurrentTimeMillis();
  }
  
  public long getPauseTime()
  {
    return this.lastPauseTime;
  }
  
  public int getTimeDifference()
  {
    return native_getTimeDifference();
  }
  
  public void init(int paramInt1, int paramInt2, int paramInt3, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int paramInt4, boolean paramBoolean)
  {
    native_init(paramInt1, paramInt2, paramInt3, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramInt4, paramBoolean);
    checkConnection();
    paramString1 = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        ConnectionsManager.this.checkConnection();
      }
    };
    paramString2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    ApplicationLoader.applicationContext.registerReceiver(paramString1, paramString2);
  }
  
  public void resumeNetworkMaybe()
  {
    native_resumeNetwork(true);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, 0);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, int paramInt)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, paramInt, Integer.MAX_VALUE, 1, true);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, int paramInt1, int paramInt2)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, null, paramInt1, Integer.MAX_VALUE, paramInt2, true);
  }
  
  public int sendRequest(TLObject paramTLObject, RequestDelegate paramRequestDelegate, QuickAckDelegate paramQuickAckDelegate, int paramInt)
  {
    return sendRequest(paramTLObject, paramRequestDelegate, paramQuickAckDelegate, paramInt, Integer.MAX_VALUE, 1, true);
  }
  
  public int sendRequest(final TLObject paramTLObject, final RequestDelegate paramRequestDelegate, final QuickAckDelegate paramQuickAckDelegate, final int paramInt1, final int paramInt2, final int paramInt3, final boolean paramBoolean)
  {
    final int i = this.lastRequestToken.getAndIncrement();
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileLog.d("tmessages", "send request " + paramTLObject + " with token = " + i);
        try
        {
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(paramTLObject.getObjectSize());
          paramTLObject.serializeToStream(localNativeByteBuffer);
          paramTLObject.freeResources();
          ConnectionsManager.native_sendRequest(localNativeByteBuffer.address, new RequestDelegateInternal()
          {
            public void run(int paramAnonymous2Int1, int paramAnonymous2Int2, String paramAnonymous2String)
            {
              Object localObject3 = null;
              final Object localObject2 = null;
              if (paramAnonymous2Int1 != 0) {}
              try
              {
                paramAnonymous2String = NativeByteBuffer.wrap(paramAnonymous2Int1);
                paramAnonymous2String.reused = true;
                localObject1 = ConnectionsManager.1.this.val$object.deserializeResponse(paramAnonymous2String, paramAnonymous2String.readInt32(true), true);
                do
                {
                  FileLog.d("tmessages", "java received " + localObject1 + " error = " + localObject2);
                  Utilities.stageQueue.postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      ConnectionsManager.1.this.val$onComplete.run(localObject1, localObject2);
                      if (localObject1 != null) {
                        localObject1.freeResources();
                      }
                    }
                  });
                  return;
                  localObject1 = localObject3;
                } while (paramAnonymous2String == null);
                localObject2 = new TLRPC.TL_error();
              }
              catch (Exception paramAnonymous2String)
              {
                try
                {
                  ((TLRPC.TL_error)localObject2).code = paramAnonymous2Int2;
                  ((TLRPC.TL_error)localObject2).text = paramAnonymous2String;
                  FileLog.e("tmessages", ConnectionsManager.1.this.val$object + " got error " + ((TLRPC.TL_error)localObject2).code + " " + ((TLRPC.TL_error)localObject2).text);
                  final Object localObject1 = localObject3;
                }
                catch (Exception paramAnonymous2String)
                {
                  for (;;) {}
                }
                paramAnonymous2String = paramAnonymous2String;
              }
              FileLog.e("tmessages", paramAnonymous2String);
            }
          }, paramQuickAckDelegate, paramInt1, paramInt2, paramInt3, paramBoolean, i);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
    return i;
  }
  
  public void setAppPaused(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean2)
    {
      this.appPaused = paramBoolean1;
      FileLog.d("tmessages", "app paused = " + paramBoolean1);
    }
    if (paramBoolean1)
    {
      if (this.lastPauseTime == 0L) {
        this.lastPauseTime = System.currentTimeMillis();
      }
      native_pauseNetwork();
    }
    while (this.appPaused) {
      return;
    }
    FileLog.e("tmessages", "reset app pause time");
    if ((this.lastPauseTime != 0L) && (System.currentTimeMillis() - this.lastPauseTime > 5000L)) {
      ContactsController.getInstance().checkContacts();
    }
    this.lastPauseTime = 0L;
    native_resumeNetwork(false);
  }
  
  public void setIsUpdating(final boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (ConnectionsManager.this.isUpdating == paramBoolean) {}
        do
        {
          return;
          ConnectionsManager.access$302(ConnectionsManager.this, paramBoolean);
        } while (ConnectionsManager.this.connectionState != 3);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
      }
    });
  }
  
  public void setPushConnectionEnabled(boolean paramBoolean)
  {
    native_setPushConnectionEnabled(paramBoolean);
  }
  
  public void setUserId(int paramInt)
  {
    native_setUserId(paramInt);
  }
  
  public void switchBackend() {}
  
  public void updateDcSettings() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/ConnectionsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */