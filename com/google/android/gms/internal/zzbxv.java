package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbxv
{
  final int tag;
  final byte[] zzbxZ;
  
  zzbxv(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzbxZ = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbxv)) {
        return false;
      }
      paramObject = (zzbxv)paramObject;
    } while ((this.tag == ((zzbxv)paramObject).tag) && (Arrays.equals(this.zzbxZ, ((zzbxv)paramObject).zzbxZ)));
    return false;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzbxZ);
  }
  
  void zza(zzbxm paramzzbxm)
    throws IOException
  {
    paramzzbxm.zzrk(this.tag);
    paramzzbxm.zzaj(this.zzbxZ);
  }
  
  int zzu()
  {
    return zzbxm.zzrl(this.tag) + 0 + this.zzbxZ.length;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */