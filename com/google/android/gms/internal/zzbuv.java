package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbuv
{
  final int tag;
  final byte[] zzcsh;
  
  zzbuv(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzcsh = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbuv)) {
        return false;
      }
      paramObject = (zzbuv)paramObject;
    } while ((this.tag == ((zzbuv)paramObject).tag) && (Arrays.equals(this.zzcsh, ((zzbuv)paramObject).zzcsh)));
    return false;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzcsh);
  }
  
  void zza(zzbum paramzzbum)
    throws IOException
  {
    paramzzbum.zzqt(this.tag);
    paramzzbum.zzah(this.zzcsh);
  }
  
  int zzv()
  {
    return zzbum.zzqu(this.tag) + 0 + this.zzcsh.length;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbuv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */