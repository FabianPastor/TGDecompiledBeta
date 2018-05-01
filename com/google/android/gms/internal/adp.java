package com.google.android.gms.internal;

import java.io.IOException;

public abstract class adp
{
  protected volatile int zzcsx = -1;
  
  public static final <T extends adp> T zza(T paramT, byte[] paramArrayOfByte)
    throws ado
  {
    return zza(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private static <T extends adp> T zza(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ado
  {
    try
    {
      paramArrayOfByte = adg.zzb(paramArrayOfByte, 0, paramInt2);
      paramT.zza(paramArrayOfByte);
      paramArrayOfByte.zzcl(0);
      return paramT;
    }
    catch (ado paramT)
    {
      throw paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
    }
  }
  
  public static final byte[] zzc(adp paramadp)
  {
    byte[] arrayOfByte = new byte[paramadp.zzLV()];
    int i = arrayOfByte.length;
    try
    {
      adh localadh = adh.zzc(arrayOfByte, 0, i);
      paramadp.zza(localadh);
      localadh.zzLM();
      return arrayOfByte;
    }
    catch (IOException paramadp)
    {
      throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", paramadp);
    }
  }
  
  public String toString()
  {
    return adq.zzd(this);
  }
  
  public adp zzLO()
    throws CloneNotSupportedException
  {
    return (adp)super.clone();
  }
  
  public final int zzLU()
  {
    if (this.zzcsx < 0) {
      zzLV();
    }
    return this.zzcsx;
  }
  
  public final int zzLV()
  {
    int i = zzn();
    this.zzcsx = i;
    return i;
  }
  
  public abstract adp zza(adg paramadg)
    throws IOException;
  
  public void zza(adh paramadh)
    throws IOException
  {}
  
  protected int zzn()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */