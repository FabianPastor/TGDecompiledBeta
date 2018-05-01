package com.google.android.gms.internal;

import java.util.Arrays;

final class adr
{
  final int tag;
  final byte[] zzbws;
  
  adr(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.zzbws = paramArrayOfByte;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof adr)) {
        return false;
      }
      paramObject = (adr)paramObject;
    } while ((this.tag == ((adr)paramObject).tag) && (Arrays.equals(this.zzbws, ((adr)paramObject).zzbws)));
    return false;
  }
  
  public final int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.zzbws);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */