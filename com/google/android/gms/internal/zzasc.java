package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

final class zzasc
{
  final byte[] btQ;
  final int tag;
  
  zzasc(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.btQ = paramArrayOfByte;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzasc)) {
        return false;
      }
      paramObject = (zzasc)paramObject;
    } while ((this.tag == ((zzasc)paramObject).tag) && (Arrays.equals(this.btQ, ((zzasc)paramObject).btQ)));
    return false;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.btQ);
  }
  
  void zza(zzart paramzzart)
    throws IOException
  {
    paramzzart.zzahd(this.tag);
    paramzzart.zzbh(this.btQ);
  }
  
  int zzx()
  {
    return zzart.zzahe(this.tag) + 0 + this.btQ.length;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */