package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzfjs
{
  protected volatile int zzpfd = -1;
  
  public String toString()
  {
    return zzfjt.zzd(this);
  }
  
  public abstract zzfjs zza(zzfjj paramzzfjj)
    throws IOException;
  
  public void zza(zzfjk paramzzfjk)
    throws IOException
  {}
  
  public zzfjs zzdag()
    throws CloneNotSupportedException
  {
    return (zzfjs)super.clone();
  }
  
  public final int zzdam()
  {
    if (this.zzpfd < 0) {
      zzho();
    }
    return this.zzpfd;
  }
  
  public final int zzho()
  {
    int i = zzq();
    this.zzpfd = i;
    return i;
  }
  
  protected int zzq()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */