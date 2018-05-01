package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbxt
{
  protected volatile int zzcuR = -1;
  
  public static final <T extends zzbxt> T zza(T paramT, byte[] paramArrayOfByte)
    throws zzbxs
  {
    return zzb(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static final void zza(zzbxt paramzzbxt, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      paramArrayOfByte = zzbxm.zzc(paramArrayOfByte, paramInt1, paramInt2);
      paramzzbxt.zza(paramArrayOfByte);
      paramArrayOfByte.zzaeG();
      return;
    }
    catch (IOException paramzzbxt)
    {
      throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", paramzzbxt);
    }
  }
  
  public static final <T extends zzbxt> T zzb(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws zzbxs
  {
    try
    {
      paramArrayOfByte = zzbxl.zzb(paramArrayOfByte, paramInt1, paramInt2);
      paramT.zzb(paramArrayOfByte);
      paramArrayOfByte.zzqX(0);
      return paramT;
    }
    catch (zzbxs paramT)
    {
      throw paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
    }
  }
  
  public static final byte[] zzf(zzbxt paramzzbxt)
  {
    byte[] arrayOfByte = new byte[paramzzbxt.zzaeT()];
    zza(paramzzbxt, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public String toString()
  {
    return zzbxu.zzg(this);
  }
  
  public void zza(zzbxm paramzzbxm)
    throws IOException
  {}
  
  public zzbxt zzaeI()
    throws CloneNotSupportedException
  {
    return (zzbxt)super.clone();
  }
  
  public int zzaeS()
  {
    if (this.zzcuR < 0) {
      zzaeT();
    }
    return this.zzcuR;
  }
  
  public int zzaeT()
  {
    int i = zzu();
    this.zzcuR = i;
    return i;
  }
  
  public abstract zzbxt zzb(zzbxl paramzzbxl)
    throws IOException;
  
  protected int zzu()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */