package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbyj
{
  protected volatile int zzcwL = -1;
  
  public static final <T extends zzbyj> T zza(T paramT, byte[] paramArrayOfByte)
    throws zzbyi
  {
    return zzb(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static final void zza(zzbyj paramzzbyj, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      paramArrayOfByte = zzbyc.zzc(paramArrayOfByte, paramInt1, paramInt2);
      paramzzbyj.zza(paramArrayOfByte);
      paramArrayOfByte.zzafo();
      return;
    }
    catch (IOException paramzzbyj)
    {
      throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", paramzzbyj);
    }
  }
  
  public static final <T extends zzbyj> T zzb(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws zzbyi
  {
    try
    {
      paramArrayOfByte = zzbyb.zzb(paramArrayOfByte, paramInt1, paramInt2);
      paramT.zzb(paramArrayOfByte);
      paramArrayOfByte.zzrc(0);
      return paramT;
    }
    catch (zzbyi paramT)
    {
      throw paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
    }
  }
  
  public static final byte[] zzf(zzbyj paramzzbyj)
  {
    byte[] arrayOfByte = new byte[paramzzbyj.zzafB()];
    zza(paramzzbyj, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public String toString()
  {
    return zzbyk.zzg(this);
  }
  
  public void zza(zzbyc paramzzbyc)
    throws IOException
  {}
  
  public int zzafA()
  {
    if (this.zzcwL < 0) {
      zzafB();
    }
    return this.zzcwL;
  }
  
  public int zzafB()
  {
    int i = zzu();
    this.zzcwL = i;
    return i;
  }
  
  public zzbyj zzafq()
    throws CloneNotSupportedException
  {
    return (zzbyj)super.clone();
  }
  
  public abstract zzbyj zzb(zzbyb paramzzbyb)
    throws IOException;
  
  protected int zzu()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */