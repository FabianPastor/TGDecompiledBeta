package com.google.android.gms.internal.config;

import java.util.Arrays;

final class zzbj
{
  final int tag;
  final byte[] zzcs;
  
  zzbj(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzcs = paramArrayOfByte;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzbj))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzbj)paramObject;
        if ((this.tag != ((zzbj)paramObject).tag) || (!Arrays.equals(this.zzcs, ((zzbj)paramObject).zzcs))) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzcs);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzbj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */