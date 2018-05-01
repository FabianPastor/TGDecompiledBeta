package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class zzeh
  extends zzhj
{
  private Boolean zzxu;
  
  zzeh(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  public static long zzhj()
  {
    return ((Long)zzew.zzaho.get()).longValue();
  }
  
  public static long zzhk()
  {
    return ((Long)zzew.zzago.get()).longValue();
  }
  
  public static boolean zzhm()
  {
    return ((Boolean)zzew.zzagj.get()).booleanValue();
  }
  
  public final long zza(String paramString, zzex<Long> paramzzex)
  {
    long l;
    if (paramString == null) {
      l = ((Long)paramzzex.get()).longValue();
    }
    for (;;)
    {
      return l;
      paramString = zzgd().zzm(paramString, paramzzex.getKey());
      if (TextUtils.isEmpty(paramString)) {
        l = ((Long)paramzzex.get()).longValue();
      } else {
        try
        {
          l = ((Long)paramzzex.get(Long.valueOf(Long.parseLong(paramString)))).longValue();
        }
        catch (NumberFormatException paramString)
        {
          l = ((Long)paramzzex.get()).longValue();
        }
      }
    }
  }
  
  public final int zzar(String paramString)
  {
    return zzb(paramString, zzew.zzagz);
  }
  
  final Boolean zzas(String paramString)
  {
    localObject1 = null;
    Preconditions.checkNotEmpty(paramString);
    for (;;)
    {
      try
      {
        if (getContext().getPackageManager() != null) {
          continue;
        }
        zzgg().zzil().log("Failed to load metadata: PackageManager is null");
        localObject2 = localObject1;
      }
      catch (PackageManager.NameNotFoundException paramString)
      {
        ApplicationInfo localApplicationInfo;
        zzgg().zzil().zzg("Failed to load metadata: Package name not found", paramString);
        Object localObject2 = localObject1;
        continue;
        if (localApplicationInfo.metaData != null) {
          continue;
        }
        zzgg().zzil().log("Failed to load metadata: Metadata bundle is null");
        localObject2 = localObject1;
        continue;
        localObject2 = localObject1;
        if (!localApplicationInfo.metaData.containsKey(paramString)) {
          continue;
        }
        boolean bool = localApplicationInfo.metaData.getBoolean(paramString);
        localObject2 = Boolean.valueOf(bool);
        continue;
      }
      return (Boolean)localObject2;
      localApplicationInfo = Wrappers.packageManager(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
      if (localApplicationInfo != null) {
        continue;
      }
      zzgg().zzil().log("Failed to load metadata: ApplicationInfo is null");
      localObject2 = localObject1;
    }
  }
  
  public final boolean zzat(String paramString)
  {
    return "1".equals(zzgd().zzm(paramString, "gaia_collection_enabled"));
  }
  
  final boolean zzau(String paramString)
  {
    return zzd(paramString, zzew.zzahx);
  }
  
  final boolean zzav(String paramString)
  {
    return zzd(paramString, zzew.zzaic);
  }
  
  public final int zzb(String paramString, zzex<Integer> paramzzex)
  {
    int i;
    if (paramString == null) {
      i = ((Integer)paramzzex.get()).intValue();
    }
    for (;;)
    {
      return i;
      paramString = zzgd().zzm(paramString, paramzzex.getKey());
      if (TextUtils.isEmpty(paramString)) {
        i = ((Integer)paramzzex.get()).intValue();
      } else {
        try
        {
          i = ((Integer)paramzzex.get(Integer.valueOf(Integer.parseInt(paramString)))).intValue();
        }
        catch (NumberFormatException paramString)
        {
          i = ((Integer)paramzzex.get()).intValue();
        }
      }
    }
  }
  
  public final double zzc(String paramString, zzex<Double> paramzzex)
  {
    double d;
    if (paramString == null) {
      d = ((Double)paramzzex.get()).doubleValue();
    }
    for (;;)
    {
      return d;
      paramString = zzgd().zzm(paramString, paramzzex.getKey());
      if (TextUtils.isEmpty(paramString)) {
        d = ((Double)paramzzex.get()).doubleValue();
      } else {
        try
        {
          d = ((Double)paramzzex.get(Double.valueOf(Double.parseDouble(paramString)))).doubleValue();
        }
        catch (NumberFormatException paramString)
        {
          d = ((Double)paramzzex.get()).doubleValue();
        }
      }
    }
  }
  
  public final boolean zzd(String paramString, zzex<Boolean> paramzzex)
  {
    boolean bool;
    if (paramString == null) {
      bool = ((Boolean)paramzzex.get()).booleanValue();
    }
    for (;;)
    {
      return bool;
      paramString = zzgd().zzm(paramString, paramzzex.getKey());
      if (TextUtils.isEmpty(paramString)) {
        bool = ((Boolean)paramzzex.get()).booleanValue();
      } else {
        bool = ((Boolean)paramzzex.get(Boolean.valueOf(Boolean.parseBoolean(paramString)))).booleanValue();
      }
    }
  }
  
  /* Error */
  public final boolean zzds()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 238	com/google/android/gms/internal/measurement/zzeh:zzxu	Ljava/lang/Boolean;
    //   4: ifnonnull +83 -> 87
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 238	com/google/android/gms/internal/measurement/zzeh:zzxu	Ljava/lang/Boolean;
    //   13: ifnonnull +72 -> 85
    //   16: aload_0
    //   17: invokevirtual 48	com/google/android/gms/internal/measurement/zzhj:getContext	()Landroid/content/Context;
    //   20: invokevirtual 241	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_1
    //   24: invokestatic 246	com/google/android/gms/common/util/ProcessUtils:getMyProcessName	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_1
    //   29: ifnull +30 -> 59
    //   32: aload_1
    //   33: getfield 250	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_1
    //   37: aload_1
    //   38: ifnull +57 -> 95
    //   41: aload_1
    //   42: aload_2
    //   43: invokevirtual 186	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +49 -> 95
    //   49: iconst_1
    //   50: istore_3
    //   51: aload_0
    //   52: iload_3
    //   53: invokestatic 175	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 238	com/google/android/gms/internal/measurement/zzeh:zzxu	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 238	com/google/android/gms/internal/measurement/zzeh:zzxu	Ljava/lang/Boolean;
    //   63: ifnonnull +22 -> 85
    //   66: aload_0
    //   67: getstatic 253	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 238	com/google/android/gms/internal/measurement/zzeh:zzxu	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 118	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   77: invokevirtual 124	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   80: ldc -1
    //   82: invokevirtual 132	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_0
    //   88: getfield 238	com/google/android/gms/internal/measurement/zzeh:zzxu	Ljava/lang/Boolean;
    //   91: invokevirtual 44	java/lang/Boolean:booleanValue	()Z
    //   94: ireturn
    //   95: iconst_0
    //   96: istore_3
    //   97: goto -46 -> 51
    //   100: astore_2
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_2
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	zzeh
    //   23	19	1	localObject1	Object
    //   27	16	2	str	String
    //   100	4	2	localObject2	Object
    //   50	47	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   9	28	100	finally
    //   32	37	100	finally
    //   41	49	100	finally
    //   51	59	100	finally
    //   59	85	100	finally
    //   85	87	100	finally
    //   101	103	100	finally
  }
  
  public final boolean zzhi()
  {
    Boolean localBoolean = zzas("firebase_analytics_collection_deactivated");
    if ((localBoolean != null) && (localBoolean.booleanValue())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final String zzhl()
  {
    try
    {
      String str1 = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "debug.firebase.analytics.app", "" });
      return str1;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Could not find SystemProperties class", localClassNotFoundException);
        String str2 = "";
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzgg().zzil().zzg("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzeh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */