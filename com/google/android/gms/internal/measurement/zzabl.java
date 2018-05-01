package com.google.android.gms.internal.measurement;

import java.util.Arrays;

final class zzabl
{
  final int tag;
  final byte[] zzbto;
  
  zzabl(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzbto = paramArrayOfByte;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzabl))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzabl)paramObject;
        if ((this.tag != ((zzabl)paramObject).tag) || (!Arrays.equals(this.zzbto, ((zzabl)paramObject).zzbto))) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzbto);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */