package com.google.android.gms.internal;

import java.util.Arrays;

final class zzfju
{
  final int tag;
  final byte[] zzjng;
  
  zzfju(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzjng = paramArrayOfByte;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzfju)) {
        return false;
      }
      paramObject = (zzfju)paramObject;
    } while ((this.tag == ((zzfju)paramObject).tag) && (Arrays.equals(this.zzjng, ((zzfju)paramObject).zzjng)));
    return false;
  }
  
  public final int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzjng);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfju.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */