package com.google.android.gms.common.stats;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.google.android.gms.common.util.ClientLibraryUtils;
import java.util.Collections;
import java.util.List;

public class ConnectionTracker
{
  private static final Object zztm = new Object();
  private static volatile ConnectionTracker zzyg;
  private static boolean zzyh = false;
  private final List<String> zzyi = Collections.EMPTY_LIST;
  private final List<String> zzyj = Collections.EMPTY_LIST;
  private final List<String> zzyk = Collections.EMPTY_LIST;
  private final List<String> zzyl = Collections.EMPTY_LIST;
  
  public static ConnectionTracker getInstance()
  {
    if (zzyg == null) {}
    synchronized (zztm)
    {
      if (zzyg == null)
      {
        ConnectionTracker localConnectionTracker = new com/google/android/gms/common/stats/ConnectionTracker;
        localConnectionTracker.<init>();
        zzyg = localConnectionTracker;
      }
      return zzyg;
    }
  }
  
  @SuppressLint({"UntrackedBindService"})
  private static boolean zza(Context paramContext, String paramString, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramBoolean)
    {
      paramString = paramIntent.getComponent();
      if (paramString == null)
      {
        paramBoolean = false;
        if (!paramBoolean) {
          break label53;
        }
        Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
      }
    }
    label53:
    for (paramBoolean = bool;; paramBoolean = paramContext.bindService(paramIntent, paramServiceConnection, paramInt))
    {
      return paramBoolean;
      paramBoolean = ClientLibraryUtils.isPackageStopped(paramContext, paramString.getPackageName());
      break;
    }
  }
  
  public boolean bindService(Context paramContext, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    return bindService(paramContext, paramContext.getClass().getName(), paramIntent, paramServiceConnection, paramInt);
  }
  
  public boolean bindService(Context paramContext, String paramString, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    return zza(paramContext, paramString, paramIntent, paramServiceConnection, paramInt, true);
  }
  
  public void logConnectService(Context paramContext, ServiceConnection paramServiceConnection, String paramString, Intent paramIntent) {}
  
  public void logDisconnectService(Context paramContext, ServiceConnection paramServiceConnection) {}
  
  @SuppressLint({"UntrackedBindService"})
  public void unbindService(Context paramContext, ServiceConnection paramServiceConnection)
  {
    paramContext.unbindService(paramServiceConnection);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/ConnectionTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */