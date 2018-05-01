package com.google.android.gms.internal.config;

import java.util.List;
import java.util.Map;

public final class zzao
{
  private Map<String, Map<String, byte[]>> zzaw;
  private long zzax;
  private List<byte[]> zzs;
  
  public zzao(Map<String, Map<String, byte[]>> paramMap, long paramLong, List<byte[]> paramList)
  {
    this.zzaw = paramMap;
    this.zzax = paramLong;
    this.zzs = paramList;
  }
  
  public final long getTimestamp()
  {
    return this.zzax;
  }
  
  public final void setTimestamp(long paramLong)
  {
    this.zzax = paramLong;
  }
  
  public final boolean zzb(String paramString)
  {
    boolean bool;
    if (paramString == null) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      if ((zzq()) && (this.zzaw.get(paramString) != null) && (!((Map)this.zzaw.get(paramString)).isEmpty())) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public final boolean zzb(String paramString1, String paramString2)
  {
    if ((zzq()) && (zzb(paramString2)) && (zzc(paramString1, paramString2) != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final byte[] zzc(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (!zzb(paramString2))) {}
    for (paramString1 = null;; paramString1 = (byte[])((Map)this.zzaw.get(paramString2)).get(paramString1)) {
      return paramString1;
    }
  }
  
  public final List<byte[]> zzg()
  {
    return this.zzs;
  }
  
  public final Map<String, Map<String, byte[]>> zzp()
  {
    return this.zzaw;
  }
  
  public final boolean zzq()
  {
    if ((this.zzaw != null) && (!this.zzaw.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzao.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */