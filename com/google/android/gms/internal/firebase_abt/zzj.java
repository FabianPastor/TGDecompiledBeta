package com.google.android.gms.internal.firebase_abt;

import java.io.IOException;

public abstract class zzj
{
  protected volatile int zzab = -1;
  
  public static final <T extends zzj> T zza(T paramT, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws zzi
  {
    try
    {
      paramArrayOfByte = zza.zza(paramArrayOfByte, 0, paramInt2);
      paramT.zza(paramArrayOfByte);
      paramArrayOfByte.zza(0);
      return paramT;
    }
    catch (zzi paramT)
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
    return zzk.zzb(this);
  }
  
  public abstract zzj zza(zza paramzza)
    throws IOException;
  
  public zzj zzj()
    throws CloneNotSupportedException
  {
    return (zzj)super.clone();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */