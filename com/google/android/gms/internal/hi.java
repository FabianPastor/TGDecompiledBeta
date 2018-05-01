package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class hi
{
  private static Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
  private static Uri zzbUa = Uri.parse("content://com.google.android.gsf.gservices/prefix");
  private static Pattern zzbUb = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
  private static Pattern zzbUc = Pattern.compile("^(0|false|f|off|no|n)$", 2);
  private static final AtomicBoolean zzbUd = new AtomicBoolean();
  private static HashMap<String, String> zzbUe;
  private static HashMap<String, Boolean> zzbUf = new HashMap();
  private static HashMap<String, Integer> zzbUg = new HashMap();
  private static HashMap<String, Long> zzbUh = new HashMap();
  private static HashMap<String, Float> zzbUi = new HashMap();
  private static Object zzbUj;
  private static boolean zzbUk;
  private static String[] zzbUl = new String[0];
  
  public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
  {
    Object localObject2 = zzb(paramContentResolver);
    Object localObject1 = (Long)zza(zzbUh, paramString, Long.valueOf(0L));
    if (localObject1 != null) {
      return ((Long)localObject1).longValue();
    }
    paramContentResolver = zza(paramContentResolver, paramString, null);
    if (paramContentResolver == null)
    {
      paramContentResolver = (ContentResolver)localObject1;
      paramLong = 0L;
    }
    for (;;)
    {
      localObject1 = zzbUh;
      try
      {
        if (localObject2 == zzbUj)
        {
          ((HashMap)localObject1).put(paramString, paramContentResolver);
          zzbUe.remove(paramString);
        }
        return paramLong;
      }
      finally {}
      try
      {
        paramLong = Long.parseLong(paramContentResolver);
        paramContentResolver = Long.valueOf(paramLong);
      }
      catch (NumberFormatException paramContentResolver)
      {
        paramContentResolver = (ContentResolver)localObject1;
        paramLong = 0L;
      }
    }
  }
  
  private static <T> T zza(HashMap<String, T> paramHashMap, String paramString, T paramT)
  {
    for (;;)
    {
      try
      {
        if (paramHashMap.containsKey(paramString))
        {
          paramHashMap = paramHashMap.get(paramString);
          if (paramHashMap != null) {
            return paramHashMap;
          }
        }
        else
        {
          return null;
        }
      }
      finally {}
      paramHashMap = paramT;
    }
  }
  
  public static String zza(ContentResolver paramContentResolver, String paramString1, String paramString2)
  {
    String str = null;
    paramString2 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    for (;;)
    {
      Object localObject3;
      int i;
      try
      {
        zza(paramContentResolver);
        localObject3 = zzbUj;
        if (zzbUe.containsKey(paramString1))
        {
          paramString1 = (String)zzbUe.get(paramString1);
          paramContentResolver = (ContentResolver)localObject2;
          if (paramString1 != null) {
            paramContentResolver = paramString1;
          }
          return paramContentResolver;
        }
        localObject2 = zzbUl;
        int j = localObject2.length;
        i = 0;
        if (i >= j) {
          break label155;
        }
        if (!paramString1.startsWith(localObject2[i])) {
          break label297;
        }
        if ((!zzbUk) || (zzbUe.isEmpty()))
        {
          zzc(paramContentResolver, zzbUl);
          if (zzbUe.containsKey(paramString1))
          {
            paramString1 = (String)zzbUe.get(paramString1);
            paramContentResolver = str;
            if (paramString1 != null) {
              paramContentResolver = paramString1;
            }
            return paramContentResolver;
          }
        }
      }
      finally {}
      return null;
      label155:
      localObject2 = paramContentResolver.query(CONTENT_URI, null, null, new String[] { paramString1 }, null);
      if (localObject2 != null) {}
      try
      {
        if (!((Cursor)localObject2).moveToFirst())
        {
          zza(localObject3, paramString1, null);
          paramContentResolver = (ContentResolver)localObject1;
          return null;
        }
        str = ((Cursor)localObject2).getString(1);
        paramContentResolver = str;
        if (str != null)
        {
          paramContentResolver = str;
          if (str.equals(null)) {
            paramContentResolver = null;
          }
        }
        zza(localObject3, paramString1, paramContentResolver);
        paramString1 = paramString2;
        if (paramContentResolver != null) {
          paramString1 = paramContentResolver;
        }
        paramContentResolver = paramString1;
        return paramString1;
      }
      finally
      {
        if (localObject2 != null) {
          ((Cursor)localObject2).close();
        }
      }
      return paramContentResolver;
      label297:
      i += 1;
    }
  }
  
  private static Map<String, String> zza(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    paramContentResolver = paramContentResolver.query(zzbUa, null, null, paramVarArgs, null);
    paramVarArgs = new TreeMap();
    if (paramContentResolver == null) {
      return paramVarArgs;
    }
    try
    {
      if (paramContentResolver.moveToNext()) {
        paramVarArgs.put(paramContentResolver.getString(0), paramContentResolver.getString(1));
      }
      return paramVarArgs;
    }
    finally
    {
      paramContentResolver.close();
    }
  }
  
  private static void zza(ContentResolver paramContentResolver)
  {
    if (zzbUe == null)
    {
      zzbUd.set(false);
      zzbUe = new HashMap();
      zzbUj = new Object();
      zzbUk = false;
      paramContentResolver.registerContentObserver(CONTENT_URI, true, new hj(null));
    }
    while (!zzbUd.getAndSet(false)) {
      return;
    }
    zzbUe.clear();
    zzbUf.clear();
    zzbUg.clear();
    zzbUh.clear();
    zzbUi.clear();
    zzbUj = new Object();
    zzbUk = false;
  }
  
  private static void zza(Object paramObject, String paramString1, String paramString2)
  {
    try
    {
      if (paramObject == zzbUj) {
        zzbUe.put(paramString1, paramString2);
      }
      return;
    }
    finally {}
  }
  
  private static Object zzb(ContentResolver paramContentResolver)
  {
    try
    {
      zza(paramContentResolver);
      paramContentResolver = zzbUj;
      return paramContentResolver;
    }
    finally {}
  }
  
  public static void zzb(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    int i = 0;
    if (paramVarArgs.length == 0) {
      return;
    }
    for (;;)
    {
      HashSet localHashSet;
      ArrayList localArrayList;
      try
      {
        zza(paramContentResolver);
        localHashSet = new HashSet((zzbUl.length + paramVarArgs.length << 2) / 3 + 1);
        localHashSet.addAll(Arrays.asList(zzbUl));
        localArrayList = new ArrayList();
        int j = paramVarArgs.length;
        if (i < j)
        {
          String str = paramVarArgs[i];
          if (!localHashSet.add(str)) {
            break label189;
          }
          localArrayList.add(str);
          break label189;
        }
        if (localArrayList.isEmpty())
        {
          paramVarArgs = new String[0];
          if ((zzbUk) && (!zzbUe.isEmpty())) {
            break label176;
          }
          zzc(paramContentResolver, zzbUl);
          return;
        }
      }
      finally {}
      zzbUl = (String[])localHashSet.toArray(new String[localHashSet.size()]);
      paramVarArgs = (String[])localArrayList.toArray(new String[localArrayList.size()]);
      continue;
      label176:
      if (paramVarArgs.length != 0)
      {
        zzc(paramContentResolver, paramVarArgs);
        continue;
        label189:
        i += 1;
      }
    }
  }
  
  private static void zzc(ContentResolver paramContentResolver, String[] paramArrayOfString)
  {
    zzbUe.putAll(zza(paramContentResolver, paramArrayOfString));
    zzbUk = true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/hi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */