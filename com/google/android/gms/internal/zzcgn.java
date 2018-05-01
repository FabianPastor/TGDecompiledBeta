package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class zzcgn
  extends zzcjk
{
  private Boolean zzdvl;
  
  zzcgn(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  public static long zzayb()
  {
    return ((Long)zzchc.zzjbg.get()).longValue();
  }
  
  public static long zzayc()
  {
    return ((Long)zzchc.zzjag.get()).longValue();
  }
  
  public static boolean zzaye()
  {
    return ((Boolean)zzchc.zzjab.get()).booleanValue();
  }
  
  public final long zza(String paramString, zzchd<Long> paramzzchd)
  {
    if (paramString == null) {
      return ((Long)paramzzchd.get()).longValue();
    }
    paramString = zzawv().zzam(paramString, paramzzchd.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Long)paramzzchd.get()).longValue();
    }
    try
    {
      long l = ((Long)paramzzchd.get(Long.valueOf(Long.valueOf(paramString).longValue()))).longValue();
      return l;
    }
    catch (NumberFormatException paramString) {}
    return ((Long)paramzzchd.get()).longValue();
  }
  
  public final boolean zzaya()
  {
    Boolean localBoolean = zziy("firebase_analytics_collection_deactivated");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public final String zzayd()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "debug.firebase.analytics.app", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzawy().zzazd().zzj("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzawy().zzazd().zzj("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzawy().zzazd().zzj("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzawy().zzazd().zzj("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
  
  public final int zzb(String paramString, zzchd<Integer> paramzzchd)
  {
    if (paramString == null) {
      return ((Integer)paramzzchd.get()).intValue();
    }
    paramString = zzawv().zzam(paramString, paramzzchd.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Integer)paramzzchd.get()).intValue();
    }
    try
    {
      int i = ((Integer)paramzzchd.get(Integer.valueOf(Integer.valueOf(paramString).intValue()))).intValue();
      return i;
    }
    catch (NumberFormatException paramString) {}
    return ((Integer)paramzzchd.get()).intValue();
  }
  
  public final int zzix(String paramString)
  {
    return zzb(paramString, zzchc.zzjar);
  }
  
  final Boolean zziy(String paramString)
  {
    Boolean localBoolean = null;
    zzbq.zzgm(paramString);
    ApplicationInfo localApplicationInfo;
    try
    {
      if (getContext().getPackageManager() == null)
      {
        zzawy().zzazd().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localApplicationInfo = zzbhf.zzdb(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
      if (localApplicationInfo == null)
      {
        zzawy().zzazd().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      zzawy().zzazd().zzj("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (localApplicationInfo.metaData == null)
    {
      zzawy().zzazd().log("Failed to load metadata: Metadata bundle is null");
      return null;
    }
    if (localApplicationInfo.metaData.containsKey(paramString))
    {
      boolean bool = localApplicationInfo.metaData.getBoolean(paramString);
      localBoolean = Boolean.valueOf(bool);
    }
    return localBoolean;
  }
  
  public final boolean zziz(String paramString)
  {
    return "1".equals(zzawv().zzam(paramString, "gaia_collection_enabled"));
  }
  
  /* Error */
  public final boolean zzyp()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 318	com/google/android/gms/internal/zzcgn:zzdvl	Ljava/lang/Boolean;
    //   4: ifnonnull +84 -> 88
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 318	com/google/android/gms/internal/zzcgn:zzdvl	Ljava/lang/Boolean;
    //   13: ifnonnull +73 -> 86
    //   16: aload_0
    //   17: invokevirtual 48	com/google/android/gms/internal/zzcjk:getContext	()Landroid/content/Context;
    //   20: invokevirtual 321	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 326	com/google/android/gms/common/util/zzs:zzamo	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_3
    //   29: ifnull +30 -> 59
    //   32: aload_3
    //   33: getfield 330	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnull +58 -> 96
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 308	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +50 -> 96
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: iload_1
    //   53: invokestatic 299	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 318	com/google/android/gms/internal/zzcgn:zzdvl	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 318	com/google/android/gms/internal/zzcgn:zzdvl	Ljava/lang/Boolean;
    //   63: ifnonnull +23 -> 86
    //   66: aload_0
    //   67: getstatic 333	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 318	com/google/android/gms/internal/zzcgn:zzdvl	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 147	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   77: invokevirtual 204	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   80: ldc_w 335
    //   83: invokevirtual 260	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_0
    //   89: getfield 318	com/google/android/gms/internal/zzcgn:zzdvl	Ljava/lang/Boolean;
    //   92: invokevirtual 44	java/lang/Boolean:booleanValue	()Z
    //   95: ireturn
    //   96: iconst_0
    //   97: istore_1
    //   98: goto -47 -> 51
    //   101: astore_2
    //   102: aload_0
    //   103: monitorexit
    //   104: aload_2
    //   105: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	zzcgn
    //   50	48	1	bool	boolean
    //   27	16	2	str	String
    //   101	4	2	localObject1	Object
    //   23	19	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   9	28	101	finally
    //   32	37	101	finally
    //   41	49	101	finally
    //   51	59	101	finally
    //   59	86	101	finally
    //   86	88	101	finally
    //   102	104	101	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */