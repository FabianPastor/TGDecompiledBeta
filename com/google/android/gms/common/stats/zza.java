package com.google.android.gms.common.stats;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.google.android.gms.common.util.zzc;
import java.util.Collections;
import java.util.List;

public final class zza
{
  private static final Object zzgai = new Object();
  private static volatile zza zzgcx;
  private static boolean zzgcy = false;
  private final List<String> zzgcz = Collections.EMPTY_LIST;
  private final List<String> zzgda = Collections.EMPTY_LIST;
  private final List<String> zzgdb = Collections.EMPTY_LIST;
  private final List<String> zzgdc = Collections.EMPTY_LIST;
  
  public static zza zzamc()
  {
    if (zzgcx == null) {}
    synchronized (zzgai)
    {
      if (zzgcx == null) {
        zzgcx = new zza();
      }
      return zzgcx;
    }
  }
  
  public final boolean zza(Context paramContext, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    return zza(paramContext, paramContext.getClass().getName(), paramIntent, paramServiceConnection, paramInt);
  }
  
  public final boolean zza(Context paramContext, String paramString, Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt)
  {
    paramString = paramIntent.getComponent();
    if (paramString == null) {}
    for (boolean bool = false; bool; bool = zzc.zzz(paramContext, paramString.getPackageName()))
    {
      Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
      return false;
    }
    return paramContext.bindService(paramIntent, paramServiceConnection, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/stats/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */