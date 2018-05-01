package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzbyl
{
  final int tag;
  final byte[] zzbyc;
  
  zzbyl(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzbyc = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbyl)) {
        return false;
      }
      paramObject = (zzbyl)paramObject;
    } while ((this.tag == ((zzbyl)paramObject).tag) && (Arrays.equals(this.zzbyc, ((zzbyl)paramObject).zzbyc)));
    return false;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzbyc);
  }
  
  void zza(zzbyc paramzzbyc)
    throws IOException
  {
    paramzzbyc.zzrp(this.tag);
    paramzzbyc.zzak(this.zzbyc);
  }
  
  int zzu()
  {
    return zzbyc.zzrq(this.tag) + 0 + this.zzbyc.length;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */