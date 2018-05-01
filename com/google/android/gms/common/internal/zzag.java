package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.ServiceConnection;

public abstract class zzag
{
  private static final Object zzgai = new Object();
  private static zzag zzgaj;
  
  public static zzag zzco(Context paramContext)
  {
    synchronized (zzgai)
    {
      if (zzgaj == null) {
        zzgaj = new zzai(paramContext.getApplicationContext());
      }
      return zzgaj;
    }
  }
  
  public final void zza(String paramString1, String paramString2, int paramInt, ServiceConnection paramServiceConnection, String paramString3)
  {
    zzb(new zzah(paramString1, paramString2, paramInt), paramServiceConnection, paramString3);
  }
  
  protected abstract boolean zza(zzah paramzzah, ServiceConnection paramServiceConnection, String paramString);
  
  protected abstract void zzb(zzah paramzzah, ServiceConnection paramServiceConnection, String paramString);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */