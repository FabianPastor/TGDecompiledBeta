package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzark
{
  protected volatile int bqE = -1;
  
  public static final <T extends zzark> T zza(T paramT, byte[] paramArrayOfByte)
    throws zzarj
  {
    return zzb(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static final void zza(zzark paramzzark, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      paramArrayOfByte = zzard.zzc(paramArrayOfByte, paramInt1, paramInt2);
      paramzzark.zza(paramArrayOfByte);
      paramArrayOfByte.cO();
      return;
    }
    catch (IOException paramzzark)
    {
      throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", paramzzark);
    }
  }
  
  public static final <T extends zzark> T zzb(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws zzarj
  {
    try
    {
      paramArrayOfByte = zzarc.zzb(paramArrayOfByte, paramInt1, paramInt2);
      paramT.zzb(paramArrayOfByte);
      paramArrayOfByte.zzagz(0);
      return paramT;
    }
    catch (zzarj paramT)
    {
      throw paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
    }
  }
  
  public static final byte[] zzf(zzark paramzzark)
  {
    byte[] arrayOfByte = new byte[paramzzark.db()];
    zza(paramzzark, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public zzark cQ()
    throws CloneNotSupportedException
  {
    return (zzark)super.clone();
  }
  
  public int da()
  {
    if (this.bqE < 0) {
      db();
    }
    return this.bqE;
  }
  
  public int db()
  {
    int i = zzx();
    this.bqE = i;
    return i;
  }
  
  public String toString()
  {
    return zzarl.zzg(this);
  }
  
  public void zza(zzard paramzzard)
    throws IOException
  {}
  
  public abstract zzark zzb(zzarc paramzzarc)
    throws IOException;
  
  protected int zzx()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzark.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */