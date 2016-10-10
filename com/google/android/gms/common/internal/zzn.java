package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzn
{
  private static zzn CA;
  private static final Object Cz = new Object();
  
  public static zzn zzcf(Context paramContext)
  {
    synchronized (Cz)
    {
      if (CA == null) {
        CA = new zzo(paramContext.getApplicationContext());
      }
      return CA;
    }
  }
  
  public abstract boolean zza(ComponentName paramComponentName, ServiceConnection paramServiceConnection, String paramString);
  
  public abstract boolean zza(String paramString1, String paramString2, ServiceConnection paramServiceConnection, String paramString3);
  
  public abstract void zzb(ComponentName paramComponentName, ServiceConnection paramServiceConnection, String paramString);
  
  public abstract void zzb(String paramString1, String paramString2, ServiceConnection paramServiceConnection, String paramString3);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */