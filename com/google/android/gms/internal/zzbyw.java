package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyw
  implements zzbzb
{
  private final zzbzb zzcxZ;
  
  public void close()
    throws IOException
  {
    this.zzcxZ.close();
  }
  
  public long read(zzbyr paramzzbyr, long paramLong)
    throws IOException
  {
    return this.zzcxZ.read(paramzzbyr, paramLong);
  }
  
  public String toString()
  {
    return getClass().getSimpleName() + "(" + this.zzcxZ.toString() + ")";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */