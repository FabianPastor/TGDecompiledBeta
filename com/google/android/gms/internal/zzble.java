package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class zzble
{
  public static final Uri CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");
  public static final Uri zzbVQ = Uri.parse("content://com.google.android.gsf.gservices/prefix");
  public static final Pattern zzbVR = Pattern.compile("^(1|true|t|on|yes|y)$", 2);
  public static final Pattern zzbVS = Pattern.compile("^(0|false|f|off|no|n)$", 2);
  private static final AtomicBoolean zzbVT = new AtomicBoolean();
  static HashMap<String, String> zzbVU;
  private static Object zzbVV;
  private static boolean zzbVW;
  static String[] zzbVX = new String[0];
  
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
  
  @Deprecated
  public static String getString(ContentResolver paramContentResolver, String paramString)
  {
    return zza(paramContentResolver, paramString, null);
  }
  
  public static String zza(ContentResolver paramContentResolver, String paramString1, String paramString2)
  {
    for (;;)
    {
      Object localObject2;
      Object localObject1;
      int i;
      try
      {
        zza(paramContentResolver);
        localObject2 = zzbVV;
        if (zzbVU.containsKey(paramString1))
        {
          paramContentResolver = (String)zzbVU.get(paramString1);
          if (paramContentResolver != null) {
            paramString2 = paramContentResolver;
          }
          return paramString2;
        }
        localObject1 = zzbVX;
        int j = localObject1.length;
        i = 0;
        if (i >= j) {
          break label138;
        }
        if (!paramString1.startsWith(localObject1[i])) {
          break label277;
        }
        if ((!zzbVW) || (zzbVU.isEmpty()))
        {
          zzc(paramContentResolver, zzbVX);
          if (zzbVU.containsKey(paramString1))
          {
            paramContentResolver = (String)zzbVU.get(paramString1);
            if (paramContentResolver != null) {
              paramString2 = paramContentResolver;
            }
            return paramString2;
          }
        }
      }
      finally {}
      return paramString2;
      label138:
      Cursor localCursor = paramContentResolver.query(CONTENT_URI, null, null, new String[] { paramString1 }, null);
      if (localCursor != null) {}
      try
      {
        if (!localCursor.moveToFirst())
        {
          zza(localObject2, paramString1, null);
          paramContentResolver = paramString2;
          return paramString2;
        }
        localObject1 = localCursor.getString(1);
        paramContentResolver = (ContentResolver)localObject1;
        if (localObject1 != null)
        {
          paramContentResolver = (ContentResolver)localObject1;
          if (((String)localObject1).equals(paramString2)) {
            paramContentResolver = paramString2;
          }
        }
        zza(localObject2, paramString1, paramContentResolver);
        if (paramContentResolver != null) {
          paramString2 = paramContentResolver;
        }
        paramContentResolver = paramString2;
        return paramString2;
      }
      finally
      {
        if (localCursor != null) {
          localCursor.close();
        }
      }
      return paramContentResolver;
      label277:
      i += 1;
    }
  }
  
  public static Map<String, String> zza(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    paramContentResolver = paramContentResolver.query(zzbVQ, null, null, paramVarArgs, null);
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
    if (zzbVU == null)
    {
      zzbVT.set(false);
      zzbVU = new HashMap();
      zzbVV = new Object();
      zzbVW = false;
      paramContentResolver.registerContentObserver(CONTENT_URI, true, new ContentObserver(null)
      {
        public void onChange(boolean paramAnonymousBoolean)
        {
          zzble.zzUJ().set(true);
        }
      });
    }
    while (!zzbVT.getAndSet(false)) {
      return;
    }
    zzbVU.clear();
    zzbVV = new Object();
    zzbVW = false;
  }
  
  private static void zza(Object paramObject, String paramString1, String paramString2)
  {
    try
    {
      if (paramObject == zzbVV) {
        zzbVU.put(paramString1, paramString2);
      }
      return;
    }
    finally {}
  }
  
  public static void zzb(ContentResolver paramContentResolver, String... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return;
    }
    for (;;)
    {
      try
      {
        zza(paramContentResolver);
        paramVarArgs = zzk(paramVarArgs);
        if ((!zzbVW) || (zzbVU.isEmpty()))
        {
          zzc(paramContentResolver, zzbVX);
          return;
        }
      }
      finally {}
      if (paramVarArgs.length != 0) {
        zzc(paramContentResolver, paramVarArgs);
      }
    }
  }
  
  private static void zzc(ContentResolver paramContentResolver, String[] paramArrayOfString)
  {
    zzbVU.putAll(zza(paramContentResolver, paramArrayOfString));
    zzbVW = true;
  }
  
  private static String[] zzk(String[] paramArrayOfString)
  {
    HashSet localHashSet = new HashSet((zzbVX.length + paramArrayOfString.length) * 4 / 3 + 1);
    localHashSet.addAll(Arrays.asList(zzbVX));
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      if (localHashSet.add(str)) {
        localArrayList.add(str);
      }
      i += 1;
    }
    if (localArrayList.isEmpty()) {
      return new String[0];
    }
    zzbVX = (String[])localHashSet.toArray(new String[localHashSet.size()]);
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzble.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */