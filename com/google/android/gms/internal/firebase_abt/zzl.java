package com.google.android.gms.internal.firebase_abt;

import java.util.Arrays;

final class zzl
{
  final int tag;
  final byte[] zzac;
  
  zzl(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzac = paramArrayOfByte;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzl))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzl)paramObject;
        if ((this.tag != ((zzl)paramObject).tag) || (!Arrays.equals(this.zzac, ((zzl)paramObject).zzac))) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzac);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */