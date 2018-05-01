package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class zzafz
{
  public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
  public static final Uri aRT = Uri.parse("content://com.google.android.gsf.gservices/prefix");
  public static final Pattern aRU = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
  public static final Pattern aRV = Pattern.compile("^(0|false|f|off|no|n)$", 2);
  static HashMap<String, String> aRW;
  private static Object aRX;
  static HashSet<String> aRY = new HashSet();
  
  public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
  {
    paramContentResolver = getString(paramContentResolver, paramString);
    long l = paramLong;
    if (paramContentResolver != null) {}
    try
    {
      l = Long.parseLong(paramContentResolver);
      return l;
    }
    catch (NumberFormatException paramContentResolver) {}
    return paramLong;
  }
  
  public static String getString(ContentResolver paramContentResolver, String paramString)
  {
    return zza(paramContentResolver, paramString, null);
  }
  
  public static String zza(ContentResolver paramContentResolver, String paramString1, String paramString2)
  {
    Object localObject2;
    try
    {
      zza(paramContentResolver);
      localObject2 = aRX;
      if (aRW.containsKey(paramString1))
      {
        paramContentResolver = (String)aRW.get(paramString1);
        if (paramContentResolver != null) {
          paramString2 = paramContentResolver;
        }
        return paramString2;
      }
      localObject1 = aRY.iterator();
      while (((Iterator)localObject1).hasNext()) {
        if (paramString1.startsWith((String)((Iterator)localObject1).next())) {
          return paramString2;
        }
      }
    }
    finally {}
    Object localObject1 = paramContentResolver.query(CONTENT_URI, null, null, new String[] { paramString1 }, null);
    if (localObject1 != null) {}
    try
    {
      if (!((Cursor)localObject1).moveToFirst())
      {
        aRW.put(paramString1, null);
        paramContentResolver = paramString2;
        return paramString2;
      }
      paramContentResolver = ((Cursor)localObject1).getString(1);
      try
      {
        if (localObject2 == aRX) {
          aRW.put(paramString1, paramContentResolver);
        }
        if (paramContentResolver != null) {
          paramString2 = paramContentResolver;
        }
        paramContentResolver = paramString2;
        return paramString2;
      }
      finally {}
      return paramContentResolver;
    }
    finally
    {
      if (localObject1 != null) {
        ((Cursor)localObject1).close();
      }
    }
  }
  
  public static Map<String, String> zza(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    paramContentResolver = paramContentResolver.query(aRT, null, null, paramVarArgs, null);
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
  
  private static void zza(final ContentResolver paramContentResolver)
  {
    if (aRW == null)
    {
      aRW = new HashMap();
      aRX = new Object();
      new Thread("Gservices")
      {
        public void run()
        {
          Looper.prepare();
          paramContentResolver.registerContentObserver(zzafz.CONTENT_URI, true, new ContentObserver(new Handler(Looper.myLooper()))
          {
            public void onChange(boolean paramAnonymous2Boolean)
            {
              try
              {
                zzafz.aRW.clear();
                zzafz.zzbd(new Object());
                if (!zzafz.aRY.isEmpty()) {
                  zzafz.zzb(zzafz.1.this.aRZ, (String[])zzafz.aRY.toArray(new String[zzafz.aRY.size()]));
                }
                return;
              }
              finally {}
            }
          });
          Looper.loop();
        }
      }.start();
    }
  }
  
  public static void zzb(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    Map localMap = zza(paramContentResolver, paramVarArgs);
    try
    {
      zza(paramContentResolver);
      aRY.addAll(Arrays.asList(paramVarArgs));
      paramContentResolver = localMap.entrySet().iterator();
      while (paramContentResolver.hasNext())
      {
        paramVarArgs = (Map.Entry)paramContentResolver.next();
        aRW.put((String)paramVarArgs.getKey(), (String)paramVarArgs.getValue());
      }
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzafz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */