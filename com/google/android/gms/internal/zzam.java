package com.google.android.gms.internal;

import java.util.Date;
import java.util.Map;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

public final class zzam
{
  public static String zza(Map<String, String> paramMap)
  {
    paramMap = (String)paramMap.get("Content-Type");
    if (paramMap != null)
    {
      paramMap = paramMap.split(";");
      int i = 1;
      while (i < paramMap.length)
      {
        String[] arrayOfString = paramMap[i].trim().split("=");
        if ((arrayOfString.length == 2) && (arrayOfString[0].equals("charset"))) {
          return arrayOfString[1];
        }
        i += 1;
      }
    }
    return "ISO-8859-1";
  }
  
  public static zzc zzb(zzn paramzzn)
  {
    long l6 = System.currentTimeMillis();
    Map localMap = paramzzn.zzy;
    long l3 = 0L;
    long l2 = 0L;
    long l1 = 0L;
    Object localObject1 = (String)localMap.get("Date");
    if (localObject1 != null) {
      l3 = zzf((String)localObject1);
    }
    localObject1 = (String)localMap.get("Cache-Control");
    int i;
    Object localObject2;
    if (localObject1 != null)
    {
      localObject1 = ((String)localObject1).split(",");
      j = 0;
      i = 0;
      l1 = 0L;
      l2 = 0L;
      if (j < localObject1.length)
      {
        localObject2 = localObject1[j].trim();
        if ((((String)localObject2).equals("no-cache")) || (((String)localObject2).equals("no-store"))) {
          return null;
        }
        if (((String)localObject2).startsWith("max-age=")) {}
        for (;;)
        {
          try
          {
            l5 = Long.parseLong(((String)localObject2).substring(8));
            l4 = l1;
          }
          catch (Exception localException2)
          {
            long l4 = l1;
            long l5 = l2;
            continue;
            l1 = 0L;
            l2 = 0L;
            continue;
            l4 = 0L;
            continue;
            l5 = 0L;
            continue;
          }
          j += 1;
          l1 = l4;
          l2 = l5;
          break;
          if (((String)localObject2).startsWith("stale-while-revalidate=")) {}
          try
          {
            l4 = Long.parseLong(((String)localObject2).substring(23));
            l5 = l2;
          }
          catch (Exception localException1)
          {
            l4 = l1;
            l5 = l2;
          }
          if (!((String)localObject2).equals("must-revalidate"))
          {
            l4 = l1;
            l5 = l2;
            if (!((String)localObject2).equals("proxy-revalidate")) {}
          }
          else
          {
            i = 1;
            l4 = l1;
            l5 = l2;
          }
        }
      }
    }
    for (int j = 1;; j = 0)
    {
      localObject1 = (String)localMap.get("Expires");
      if (localObject1 != null)
      {
        l5 = zzf((String)localObject1);
        localObject1 = (String)localMap.get("Last-Modified");
        if (localObject1 != null)
        {
          l4 = zzf((String)localObject1);
          localObject1 = (String)localMap.get("ETag");
          if (j != 0)
          {
            l2 = l6 + 1000L * l2;
            if (i != 0) {
              l1 = l2;
            }
          }
          for (;;)
          {
            localObject2 = new zzc();
            ((zzc)localObject2).data = paramzzn.data;
            ((zzc)localObject2).zza = ((String)localObject1);
            ((zzc)localObject2).zze = l2;
            ((zzc)localObject2).zzd = l1;
            ((zzc)localObject2).zzb = l3;
            ((zzc)localObject2).zzc = l4;
            ((zzc)localObject2).zzf = localMap;
            return (zzc)localObject2;
            l1 = 1000L * l1 + l2;
            continue;
            if ((l3 <= 0L) || (l5 < l3)) {
              break;
            }
            l1 = l5 - l3 + l6;
            l2 = l1;
          }
          break;
        }
      }
      i = 0;
    }
  }
  
  private static long zzf(String paramString)
  {
    try
    {
      long l = DateUtils.parseDate(paramString).getTime();
      return l;
    }
    catch (DateParseException paramString) {}
    return 0L;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzam.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */