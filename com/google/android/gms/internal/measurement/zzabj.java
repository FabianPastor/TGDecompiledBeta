package com.google.android.gms.internal.measurement;

import java.io.IOException;

public abstract class zzabj
{
  protected volatile int zzbzs = -1;
  
  public static final <T extends zzabj> T zza(T paramT, byte[] paramArrayOfByte)
    throws zzabi
  {
    return zzb(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private static final <T extends zzabj> T zzb(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws zzabi
  {
    try
    {
      paramArrayOfByte = zzaba.zza(paramArrayOfByte, 0, paramInt2);
      paramT.zzb(paramArrayOfByte);
      paramArrayOfByte.zzal(0);
      return paramT;
    }
    catch (zzabi paramT)
    {
      throw paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", paramT);
    }
  }
  
  public String toString()
  {
    return zzabk.zzc(this);
  }
  
  protected int zza()
  {
    return 0;
  }
  
  public void zza(zzabb paramzzabb)
    throws IOException
  {}
  
  public abstract zzabj zzb(zzaba paramzzaba)
    throws IOException;
  
  public zzabj zzvz()
    throws CloneNotSupportedException
  {
    return (zzabj)super.clone();
  }
  
  public final int zzwf()
  {
    if (this.zzbzs < 0) {
      zzwg();
    }
    return this.zzbzs;
  }
  
  public final int zzwg()
  {
    int i = zza();
    this.zzbzs = i;
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */