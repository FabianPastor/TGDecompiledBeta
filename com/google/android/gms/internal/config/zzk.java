package com.google.android.gms.internal.config;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract interface zzk
  extends Result
{
  public abstract Status getStatus();
  
  public abstract long getThrottleEndTimeMillis();
  
  public abstract byte[] zza(String paramString1, byte[] paramArrayOfByte, String paramString2);
  
  public abstract List<byte[]> zzg();
  
  public abstract Map<String, Set<String>> zzh();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */