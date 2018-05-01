package com.google.android.gms.common.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.WorkSource;
import android.util.Log;
import com.google.android.gms.internal.zzadf;
import com.google.android.gms.internal.zzadg;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class zzz
{
  private static final Method zzaIq = ;
  private static final Method zzaIr = zzzt();
  private static final Method zzaIs = zzzu();
  private static final Method zzaIt = zzzv();
  private static final Method zzaIu = zzzw();
  
  public static WorkSource zzG(Context paramContext, String paramString)
  {
    if ((paramContext == null) || (paramContext.getPackageManager() == null)) {
      return null;
    }
    try
    {
      paramContext = zzadg.zzbi(paramContext).getApplicationInfo(paramString, 0);
      if (paramContext != null) {
        break label110;
      }
      paramContext = String.valueOf(paramString);
      if (paramContext.length() == 0) {
        break label97;
      }
      paramContext = "Could not get applicationInfo from package: ".concat(paramContext);
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext = String.valueOf(paramString);
        if (paramContext.length() != 0) {}
        for (paramContext = "Could not find package: ".concat(paramContext);; paramContext = new String("Could not find package: "))
        {
          Log.e("WorkSourceUtil", paramContext);
          return null;
        }
        paramContext = new String("Could not get applicationInfo from package: ");
      }
    }
    Log.e("WorkSourceUtil", paramContext);
    return null;
    label97:
    label110:
    return zzf(paramContext.uid, paramString);
  }
  
  public static int zza(WorkSource paramWorkSource)
  {
    if (zzaIs != null) {
      try
      {
        int i = ((Integer)zzaIs.invoke(paramWorkSource, new Object[0])).intValue();
        return i;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
      }
    }
    return 0;
  }
  
  public static String zza(WorkSource paramWorkSource, int paramInt)
  {
    if (zzaIu != null) {
      try
      {
        paramWorkSource = (String)zzaIu.invoke(paramWorkSource, new Object[] { Integer.valueOf(paramInt) });
        return paramWorkSource;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
      }
    }
    return null;
  }
  
  public static void zza(WorkSource paramWorkSource, int paramInt, String paramString)
  {
    if (zzaIr != null)
    {
      str = paramString;
      if (paramString == null) {
        str = "";
      }
    }
    while (zzaIq == null) {
      try
      {
        String str;
        zzaIr.invoke(paramWorkSource, new Object[] { Integer.valueOf(paramInt), str });
        return;
      }
      catch (Exception paramWorkSource)
      {
        Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
        return;
      }
    }
    try
    {
      zzaIq.invoke(paramWorkSource, new Object[] { Integer.valueOf(paramInt) });
      return;
    }
    catch (Exception paramWorkSource)
    {
      Log.wtf("WorkSourceUtil", "Unable to assign blame through WorkSource", paramWorkSource);
    }
  }
  
  public static List<String> zzb(WorkSource paramWorkSource)
  {
    int j = 0;
    if (paramWorkSource == null) {}
    Object localObject;
    for (int i = 0; i == 0; i = zza(paramWorkSource))
    {
      localObject = Collections.EMPTY_LIST;
      return (List<String>)localObject;
    }
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      localObject = localArrayList;
      if (j >= i) {
        break;
      }
      localObject = zza(paramWorkSource, j);
      if (!zzw.zzdz((String)localObject)) {
        localArrayList.add(localObject);
      }
      j += 1;
    }
  }
  
  public static boolean zzbf(Context paramContext)
  {
    if (paramContext == null) {}
    while ((paramContext.getPackageManager() == null) || (zzadg.zzbi(paramContext).checkPermission("android.permission.UPDATE_DEVICE_STATS", paramContext.getPackageName()) != 0)) {
      return false;
    }
    return true;
  }
  
  public static WorkSource zzf(int paramInt, String paramString)
  {
    WorkSource localWorkSource = new WorkSource();
    zza(localWorkSource, paramInt, paramString);
    return localWorkSource;
  }
  
  private static Method zzzs()
  {
    try
    {
      Method localMethod = WorkSource.class.getMethod("add", new Class[] { Integer.TYPE });
      return localMethod;
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static Method zzzt()
  {
    Method localMethod = null;
    if (zzt.zzzk()) {}
    try
    {
      localMethod = WorkSource.class.getMethod("add", new Class[] { Integer.TYPE, String.class });
      return localMethod;
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static Method zzzu()
  {
    try
    {
      Method localMethod = WorkSource.class.getMethod("size", new Class[0]);
      return localMethod;
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static Method zzzv()
  {
    try
    {
      Method localMethod = WorkSource.class.getMethod("get", new Class[] { Integer.TYPE });
      return localMethod;
    }
    catch (Exception localException) {}
    return null;
  }
  
  private static Method zzzw()
  {
    Method localMethod = null;
    if (zzt.zzzk()) {}
    try
    {
      localMethod = WorkSource.class.getMethod("getName", new Class[] { Integer.TYPE });
      return localMethod;
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */