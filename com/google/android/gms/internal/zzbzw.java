package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzw
  extends zzbzu<Boolean>
{
  public zzbzw(int paramInt, String paramString, Boolean paramBoolean)
  {
    super(0, paramString, paramBoolean, null);
  }
  
  private final Boolean zzb(zzcac paramzzcac)
  {
    try
    {
      boolean bool = paramzzcac.getBooleanFlagValue(getKey(), ((Boolean)zzdI()).booleanValue(), getSource());
      return Boolean.valueOf(bool);
    }
    catch (RemoteException paramzzcac) {}
    return (Boolean)zzdI();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */