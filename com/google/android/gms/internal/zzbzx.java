package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzx
  extends zzbzu<Integer>
{
  public zzbzx(int paramInt, String paramString, Integer paramInteger)
  {
    super(0, paramString, paramInteger, null);
  }
  
  private final Integer zzc(zzcac paramzzcac)
  {
    try
    {
      int i = paramzzcac.getIntFlagValue(getKey(), ((Integer)zzdI()).intValue(), getSource());
      return Integer.valueOf(i);
    }
    catch (RemoteException paramzzcac) {}
    return (Integer)zzdI();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */