package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbut
{
  protected volatile int zzcsg = -1;
  
  public static final <T extends zzbut> T zza(T paramT, byte[] paramArrayOfByte)
    throws zzbus
  {
    return zzb(paramT, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static final void zza(zzbut paramzzbut, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      paramArrayOfByte = zzbum.zzc(paramArrayOfByte, paramInt1, paramInt2);
      paramzzbut.zza(paramArrayOfByte);
      paramArrayOfByte.zzacM();
      return;
    }
    catch (IOException paramzzbut)
    {
      throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", paramzzbut);
    }
  }
  
  public static final <T extends zzbut> T zzb(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws zzbus
  {
    try
    {
      paramArrayOfByte = zzbul.zzb(paramArrayOfByte, paramInt1, paramInt2);
      paramT.zzb(paramArrayOfByte);
      paramArrayOfByte.zzqg(0);
      return paramT;
    }
    catch (zzbus paramT)
    {
      throw paramT;
    }
    catch (IOException paramT)
    {
      throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).");
    }
  }
  
  public static final byte[] zzf(zzbut paramzzbut)
  {
    byte[] arrayOfByte = new byte[paramzzbut.zzacZ()];
    zza(paramzzbut, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public String toString()
  {
    return zzbuu.zzg(this);
  }
  
  public void zza(zzbum paramzzbum)
    throws IOException
  {}
  
  public zzbut zzacO()
    throws CloneNotSupportedException
  {
    return (zzbut)super.clone();
  }
  
  public int zzacY()
  {
    if (this.zzcsg < 0) {
      zzacZ();
    }
    return this.zzcsg;
  }
  
  public int zzacZ()
  {
    int i = zzv();
    this.zzcsg = i;
    return i;
  }
  
  public abstract zzbut zzb(zzbul paramzzbul)
    throws IOException;
  
  protected int zzv()
  {
    return 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */