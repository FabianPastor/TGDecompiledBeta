package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.zze;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class zzcem
  extends zzchi
{
  private static String zzbpm = String.valueOf(zze.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
  private Boolean zzagU;
  
  zzcem(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  public static boolean zzqB()
  {
    return zzbdm.zzqB();
  }
  
  public static long zzwP()
  {
    return 11020L;
  }
  
  static long zzxA()
  {
    return 61000L;
  }
  
  static long zzxB()
  {
    return ((Long)zzcfb.zzbqB.get()).longValue();
  }
  
  public static String zzxC()
  {
    return "google_app_measurement.db";
  }
  
  static String zzxD()
  {
    return "google_app_measurement_local.db";
  }
  
  public static boolean zzxE()
  {
    return false;
  }
  
  public static long zzxG()
  {
    return ((Long)zzcfb.zzbqy.get()).longValue();
  }
  
  public static long zzxH()
  {
    return ((Long)zzcfb.zzbqt.get()).longValue();
  }
  
  public static long zzxI()
  {
    return ((Long)zzcfb.zzbqu.get()).longValue();
  }
  
  public static long zzxJ()
  {
    return 1000L;
  }
  
  public static long zzxK()
  {
    return Math.max(0L, ((Long)zzcfb.zzbpX.get()).longValue());
  }
  
  public static int zzxL()
  {
    return Math.max(0, ((Integer)zzcfb.zzbqd.get()).intValue());
  }
  
  public static int zzxM()
  {
    return Math.max(1, ((Integer)zzcfb.zzbqe.get()).intValue());
  }
  
  public static int zzxN()
  {
    return 100000;
  }
  
  public static String zzxO()
  {
    return (String)zzcfb.zzbql.get();
  }
  
  public static long zzxP()
  {
    return ((Long)zzcfb.zzbpY.get()).longValue();
  }
  
  public static long zzxQ()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqm.get()).longValue());
  }
  
  public static long zzxR()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqo.get()).longValue());
  }
  
  public static long zzxS()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqp.get()).longValue());
  }
  
  public static long zzxT()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqq.get()).longValue());
  }
  
  public static long zzxU()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqr.get()).longValue());
  }
  
  public static long zzxV()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqs.get()).longValue());
  }
  
  public static long zzxW()
  {
    return ((Long)zzcfb.zzbqn.get()).longValue();
  }
  
  public static long zzxX()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqv.get()).longValue());
  }
  
  public static long zzxY()
  {
    return Math.max(0L, ((Long)zzcfb.zzbqw.get()).longValue());
  }
  
  public static int zzxZ()
  {
    return Math.min(20, Math.max(0, ((Integer)zzcfb.zzbqx.get()).intValue()));
  }
  
  static String zzxf()
  {
    return (String)zzcfb.zzbpV.get();
  }
  
  public static int zzxg()
  {
    return 25;
  }
  
  public static int zzxh()
  {
    return 40;
  }
  
  public static int zzxi()
  {
    return 24;
  }
  
  static int zzxj()
  {
    return 40;
  }
  
  static int zzxk()
  {
    return 100;
  }
  
  static int zzxl()
  {
    return 256;
  }
  
  static int zzxm()
  {
    return 1000;
  }
  
  public static int zzxn()
  {
    return 36;
  }
  
  public static int zzxo()
  {
    return 2048;
  }
  
  static int zzxp()
  {
    return 500;
  }
  
  public static long zzxq()
  {
    return ((Integer)zzcfb.zzbqf.get()).intValue();
  }
  
  public static long zzxr()
  {
    return ((Integer)zzcfb.zzbqh.get()).intValue();
  }
  
  static int zzxs()
  {
    return 25;
  }
  
  static int zzxt()
  {
    return 1000;
  }
  
  static int zzxu()
  {
    return 25;
  }
  
  static int zzxv()
  {
    return 1000;
  }
  
  static long zzxw()
  {
    return 15552000000L;
  }
  
  static long zzxx()
  {
    return 15552000000L;
  }
  
  static long zzxy()
  {
    return 3600000L;
  }
  
  static long zzxz()
  {
    return 60000L;
  }
  
  public static boolean zzyb()
  {
    return ((Boolean)zzcfb.zzbpU.get()).booleanValue();
  }
  
  public final long zza(String paramString, zzcfc<Long> paramzzcfc)
  {
    if (paramString == null) {
      return ((Long)paramzzcfc.get()).longValue();
    }
    paramString = super.zzwC().zzM(paramString, paramzzcfc.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Long)paramzzcfc.get()).longValue();
    }
    try
    {
      long l = ((Long)paramzzcfc.get(Long.valueOf(Long.valueOf(paramString).longValue()))).longValue();
      return l;
    }
    catch (NumberFormatException paramString) {}
    return ((Long)paramzzcfc.get()).longValue();
  }
  
  public final int zzb(String paramString, zzcfc<Integer> paramzzcfc)
  {
    if (paramString == null) {
      return ((Integer)paramzzcfc.get()).intValue();
    }
    paramString = super.zzwC().zzM(paramString, paramzzcfc.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Integer)paramzzcfc.get()).intValue();
    }
    try
    {
      int i = ((Integer)paramzzcfc.get(Integer.valueOf(Integer.valueOf(paramString).intValue()))).intValue();
      return i;
    }
    catch (NumberFormatException paramString) {}
    return ((Integer)paramzzcfc.get()).intValue();
  }
  
  public final int zzdM(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzcfb.zzbqj);
  }
  
  @Nullable
  final Boolean zzdN(@Size(min=1L) String paramString)
  {
    Boolean localBoolean = null;
    zzbo.zzcF(paramString);
    ApplicationInfo localApplicationInfo;
    try
    {
      if (super.getContext().getPackageManager() == null)
      {
        super.zzwF().zzyx().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localApplicationInfo = zzbha.zzaP(super.getContext()).getApplicationInfo(super.getContext().getPackageName(), 128);
      if (localApplicationInfo == null)
      {
        super.zzwF().zzyx().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      super.zzwF().zzyx().zzj("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (localApplicationInfo.metaData == null)
    {
      super.zzwF().zzyx().log("Failed to load metadata: Metadata bundle is null");
      return null;
    }
    if (localApplicationInfo.metaData.containsKey(paramString))
    {
      boolean bool = localApplicationInfo.metaData.getBoolean(paramString);
      localBoolean = Boolean.valueOf(bool);
    }
    return localBoolean;
  }
  
  public final boolean zzdO(String paramString)
  {
    return "1".equals(super.zzwC().zzM(paramString, "gaia_collection_enabled"));
  }
  
  /* Error */
  public final boolean zzln()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 371	com/google/android/gms/internal/zzcem:zzagU	Ljava/lang/Boolean;
    //   4: ifnonnull +84 -> 88
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 371	com/google/android/gms/internal/zzcem:zzagU	Ljava/lang/Boolean;
    //   13: ifnonnull +73 -> 86
    //   16: aload_0
    //   17: invokespecial 220	com/google/android/gms/internal/zzchi:getContext	()Landroid/content/Context;
    //   20: invokevirtual 374	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 379	com/google/android/gms/common/util/zzr:zzsf	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_3
    //   29: ifnull +30 -> 59
    //   32: aload_3
    //   33: getfield 382	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnull +58 -> 96
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 361	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +50 -> 96
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: iload_1
    //   53: invokestatic 351	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 371	com/google/android/gms/internal/zzcem:zzagU	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 371	com/google/android/gms/internal/zzcem:zzagU	Ljava/lang/Boolean;
    //   63: ifnonnull +23 -> 86
    //   66: aload_0
    //   67: getstatic 385	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 371	com/google/android/gms/internal/zzcem:zzagU	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokespecial 294	com/google/android/gms/internal/zzchi:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   77: invokevirtual 300	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   80: ldc_w 387
    //   83: invokevirtual 308	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_0
    //   89: getfield 371	com/google/android/gms/internal/zzcem:zzagU	Ljava/lang/Boolean;
    //   92: invokevirtual 216	java/lang/Boolean:booleanValue	()Z
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
    //   0	106	0	this	zzcem
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
  
  public final boolean zzxF()
  {
    Boolean localBoolean = zzdN("firebase_analytics_collection_deactivated");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public final String zzya()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "debug.firebase.analytics.app", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      super.zzwF().zzyx().zzj("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        super.zzwF().zzyx().zzj("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        super.zzwF().zzyx().zzj("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        super.zzwF().zzyx().zzj("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */