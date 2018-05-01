package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzz
  extends zzbzu<String>
{
  public zzbzz(int paramInt, String paramString1, String paramString2)
  {
    super(0, paramString1, paramString2, null);
  }
  
  private final String zze(zzcac paramzzcac)
  {
    try
    {
      paramzzcac = paramzzcac.getStringFlagValue(getKey(), (String)zzdI(), getSource());
      return paramzzcac;
    }
    catch (RemoteException paramzzcac) {}
    return (String)zzdI();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */