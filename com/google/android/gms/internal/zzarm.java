package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzarm
{
  final byte[] avk;
  final int tag;
  
  zzarm(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.avk = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzarm)) {
        return false;
      }
      paramObject = (zzarm)paramObject;
    } while ((this.tag == ((zzarm)paramObject).tag) && (Arrays.equals(this.avk, ((zzarm)paramObject).avk)));
    return false;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.avk);
  }
  
  void zza(zzard paramzzard)
    throws IOException
  {
    paramzzard.zzahm(this.tag);
    paramzzard.zzbh(this.avk);
  }
  
  int zzx()
  {
    return zzard.zzahn(this.tag) + 0 + this.avk.length;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzarm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */