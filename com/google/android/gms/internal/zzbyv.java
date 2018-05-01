package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyv
  implements zzbza
{
  private final zzbza zzcxY;
  
  public void close()
    throws IOException
  {
    this.zzcxY.close();
  }
  
  public void flush()
    throws IOException
  {
    this.zzcxY.flush();
  }
  
  public String toString()
  {
    return getClass().getSimpleName() + "(" + this.zzcxY.toString() + ")";
  }
  
  public void write(zzbyr paramzzbyr, long paramLong)
    throws IOException
  {
    this.zzcxY.write(paramzzbyr, paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */