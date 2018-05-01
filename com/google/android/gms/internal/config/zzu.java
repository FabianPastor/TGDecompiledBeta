package com.google.android.gms.internal.config;

import com.google.android.gms.common.api.Status;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class zzu
  implements zzk
{
  private final Status mStatus;
  private final Map<String, TreeMap<String, byte[]>> zzq;
  private final long zzr;
  private final List<byte[]> zzs;
  
  public zzu(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap)
  {
    this(paramStatus, paramMap, -1L);
  }
  
  private zzu(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap, long paramLong)
  {
    this(paramStatus, paramMap, -1L, null);
  }
  
  public zzu(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap, long paramLong, List<byte[]> paramList)
  {
    this.mStatus = paramStatus;
    this.zzq = paramMap;
    this.zzr = paramLong;
    this.zzs = paramList;
  }
  
  public zzu(Status paramStatus, Map<String, TreeMap<String, byte[]>> paramMap, List<byte[]> paramList)
  {
    this(paramStatus, paramMap, -1L, paramList);
  }
  
  public final Status getStatus()
  {
    return this.mStatus;
  }
  
  public final long getThrottleEndTimeMillis()
  {
    return this.zzr;
  }
  
  public final byte[] zza(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    int i;
    if ((this.zzq == null) || (this.zzq.get(paramString2) == null))
    {
      i = 0;
      if (i == 0) {
        break label83;
      }
    }
    label83:
    for (paramString1 = (byte[])((TreeMap)this.zzq.get(paramString2)).get(paramString1);; paramString1 = null)
    {
      return paramString1;
      if (((TreeMap)this.zzq.get(paramString2)).get(paramString1) != null)
      {
        i = 1;
        break;
      }
      i = 0;
      break;
    }
  }
  
  public final List<byte[]> zzg()
  {
    return this.zzs;
  }
  
  public final Map<String, Set<String>> zzh()
  {
    HashMap localHashMap = new HashMap();
    if (this.zzq != null)
    {
      Iterator localIterator = this.zzq.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Map localMap = (Map)this.zzq.get(str);
        if (localMap != null) {
          localHashMap.put(str, localMap.keySet());
        }
      }
    }
    return localHashMap;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */