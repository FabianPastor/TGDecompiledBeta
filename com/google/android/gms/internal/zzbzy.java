package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzy
  extends zzbzu<Long>
{
  public zzbzy(int paramInt, String paramString, Long paramLong)
  {
    super(0, paramString, paramLong, null);
  }
  
  private final Long zzd(zzcac paramzzcac)
  {
    try
    {
      long l = paramzzcac.getLongFlagValue(getKey(), ((Long)zzdI()).longValue(), getSource());
      return Long.valueOf(l);
    }
    catch (RemoteException paramzzcac) {}
    return (Long)zzdI();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */