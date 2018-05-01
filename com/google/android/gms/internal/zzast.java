package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zzc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class zzast
  extends zzatr
{
  static final String zzbqo = String.valueOf(zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
  private Boolean zzadY;
  
  zzast(zzatp paramzzatp)
  {
    super(paramzzatp);
  }
  
  public long zzJD()
  {
    return 10084L;
  }
  
  String zzJS()
  {
    return (String)zzatd.zzbqV.get();
  }
  
  public int zzJT()
  {
    return 25;
  }
  
  public int zzJU()
  {
    return 40;
  }
  
  public int zzJV()
  {
    return 24;
  }
  
  int zzJW()
  {
    return 40;
  }
  
  int zzJX()
  {
    return 100;
  }
  
  int zzJY()
  {
    return 256;
  }
  
  public int zzJZ()
  {
    return 36;
  }
  
  public long zzKA()
  {
    return Math.max(0L, ((Long)zzatd.zzbrr.get()).longValue());
  }
  
  public long zzKB()
  {
    return ((Long)zzatd.zzbrn.get()).longValue();
  }
  
  public long zzKC()
  {
    return Math.max(0L, ((Long)zzatd.zzbru.get()).longValue());
  }
  
  public long zzKD()
  {
    return Math.max(0L, ((Long)zzatd.zzbrv.get()).longValue());
  }
  
  public int zzKE()
  {
    return Math.min(20, Math.max(0, ((Integer)zzatd.zzbrw.get()).intValue()));
  }
  
  public String zzKF()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "debug.firebase.analytics.app", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzJt().zzLa().zzj("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzJt().zzLa().zzj("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzJt().zzLa().zzj("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzJt().zzLa().zzj("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
  
  public int zzKa()
  {
    return 2048;
  }
  
  int zzKb()
  {
    return 500;
  }
  
  public long zzKc()
  {
    return ((Integer)zzatd.zzbrf.get()).intValue();
  }
  
  public long zzKd()
  {
    return ((Integer)zzatd.zzbrh.get()).intValue();
  }
  
  int zzKe()
  {
    return 25;
  }
  
  int zzKf()
  {
    return 50;
  }
  
  long zzKg()
  {
    return 3600000L;
  }
  
  long zzKh()
  {
    return 60000L;
  }
  
  long zzKi()
  {
    return 61000L;
  }
  
  String zzKj()
  {
    return "google_app_measurement_local.db";
  }
  
  public boolean zzKk()
  {
    return false;
  }
  
  public boolean zzKl()
  {
    Boolean localBoolean = zzft("firebase_analytics_collection_deactivated");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public Boolean zzKm()
  {
    return zzft("firebase_analytics_collection_enabled");
  }
  
  public long zzKn()
  {
    return ((Long)zzatd.zzbrx.get()).longValue();
  }
  
  public long zzKo()
  {
    return ((Long)zzatd.zzbrs.get()).longValue();
  }
  
  public long zzKp()
  {
    return ((Long)zzatd.zzbrt.get()).longValue();
  }
  
  public long zzKq()
  {
    return 1000L;
  }
  
  public int zzKr()
  {
    return Math.max(0, ((Integer)zzatd.zzbrd.get()).intValue());
  }
  
  public int zzKs()
  {
    return Math.max(1, ((Integer)zzatd.zzbre.get()).intValue());
  }
  
  public int zzKt()
  {
    return 100000;
  }
  
  public String zzKu()
  {
    return (String)zzatd.zzbrl.get();
  }
  
  public long zzKv()
  {
    return ((Long)zzatd.zzbqY.get()).longValue();
  }
  
  public long zzKw()
  {
    return Math.max(0L, ((Long)zzatd.zzbrm.get()).longValue());
  }
  
  public long zzKx()
  {
    return Math.max(0L, ((Long)zzatd.zzbro.get()).longValue());
  }
  
  public long zzKy()
  {
    return Math.max(0L, ((Long)zzatd.zzbrp.get()).longValue());
  }
  
  public long zzKz()
  {
    return Math.max(0L, ((Long)zzatd.zzbrq.get()).longValue());
  }
  
  public String zzO(String paramString1, String paramString2)
  {
    Uri.Builder localBuilder1 = new Uri.Builder();
    Uri.Builder localBuilder2 = localBuilder1.scheme((String)zzatd.zzbqZ.get()).encodedAuthority((String)zzatd.zzbra.get());
    paramString1 = String.valueOf(paramString1);
    if (paramString1.length() != 0) {}
    for (paramString1 = "config/app/".concat(paramString1);; paramString1 = new String("config/app/"))
    {
      localBuilder2.path(paramString1).appendQueryParameter("app_instance_id", paramString2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(10084L));
      return localBuilder1.build().toString();
    }
  }
  
  public long zza(String paramString, zzatd.zza<Long> paramzza)
  {
    if (paramString == null) {
      return ((Long)paramzza.get()).longValue();
    }
    paramString = zzJq().zzW(paramString, paramzza.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Long)paramzza.get()).longValue();
    }
    try
    {
      long l = ((Long)paramzza.get(Long.valueOf(Long.valueOf(paramString).longValue()))).longValue();
      return l;
    }
    catch (NumberFormatException paramString) {}
    return ((Long)paramzza.get()).longValue();
  }
  
  public int zzb(String paramString, zzatd.zza<Integer> paramzza)
  {
    if (paramString == null) {
      return ((Integer)paramzza.get()).intValue();
    }
    paramString = zzJq().zzW(paramString, paramzza.getKey());
    if (TextUtils.isEmpty(paramString)) {
      return ((Integer)paramzza.get()).intValue();
    }
    try
    {
      int i = ((Integer)paramzza.get(Integer.valueOf(Integer.valueOf(paramString).intValue()))).intValue();
      return i;
    }
    catch (NumberFormatException paramString) {}
    return ((Integer)paramzza.get()).intValue();
  }
  
  public int zzfn(@Size(min=1L) String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzatd.zzbrg)));
  }
  
  public int zzfo(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzatd.zzbri);
  }
  
  public int zzfp(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzatd.zzbrj);
  }
  
  long zzfq(String paramString)
  {
    return zza(paramString, zzatd.zzbqW);
  }
  
  int zzfr(String paramString)
  {
    return zzb(paramString, zzatd.zzbry);
  }
  
  int zzfs(String paramString)
  {
    return Math.max(0, Math.min(2000, zzb(paramString, zzatd.zzbrz)));
  }
  
  @Nullable
  Boolean zzft(@Size(min=1L) String paramString)
  {
    Boolean localBoolean = null;
    zzac.zzdv(paramString);
    ApplicationInfo localApplicationInfo;
    try
    {
      if (getContext().getPackageManager() == null)
      {
        zzJt().zzLa().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localApplicationInfo = zzacx.zzaQ(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
      if (localApplicationInfo == null)
      {
        zzJt().zzLa().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      zzJt().zzLa().zzj("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (localApplicationInfo.metaData == null)
    {
      zzJt().zzLa().log("Failed to load metadata: Metadata bundle is null");
      return null;
    }
    if (localApplicationInfo.metaData.containsKey(paramString))
    {
      boolean bool = localApplicationInfo.metaData.getBoolean(paramString);
      localBoolean = Boolean.valueOf(bool);
    }
    return localBoolean;
  }
  
  public int zzfu(String paramString)
  {
    return zzb(paramString, zzatd.zzbrb);
  }
  
  public int zzfv(String paramString)
  {
    return Math.max(0, zzb(paramString, zzatd.zzbrc));
  }
  
  public int zzfw(String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzatd.zzbrk)));
  }
  
  long zzoQ()
  {
    return ((Long)zzatd.zzbrA.get()).longValue();
  }
  
  public String zzoV()
  {
    return "google_app_measurement.db";
  }
  
  public long zzoZ()
  {
    return Math.max(0L, ((Long)zzatd.zzbqX.get()).longValue());
  }
  
  /* Error */
  public boolean zzow()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 555	com/google/android/gms/internal/zzast:zzadY	Ljava/lang/Boolean;
    //   4: ifnonnull +84 -> 88
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 555	com/google/android/gms/internal/zzast:zzadY	Ljava/lang/Boolean;
    //   13: ifnonnull +73 -> 86
    //   16: aload_0
    //   17: invokevirtual 471	com/google/android/gms/internal/zzast:getContext	()Landroid/content/Context;
    //   20: invokevirtual 558	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 563	com/google/android/gms/common/util/zzt:zzyK	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_3
    //   29: ifnull +30 -> 59
    //   32: aload_3
    //   33: getfield 566	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnull +58 -> 96
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 570	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +50 -> 96
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: iload_1
    //   53: invokestatic 521	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 555	com/google/android/gms/internal/zzast:zzadY	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 555	com/google/android/gms/internal/zzast:zzadY	Ljava/lang/Boolean;
    //   63: ifnonnull +23 -> 86
    //   66: aload_0
    //   67: getstatic 573	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 555	com/google/android/gms/internal/zzast:zzadY	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 217	com/google/android/gms/internal/zzast:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   77: invokevirtual 223	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   80: ldc_w 575
    //   83: invokevirtual 482	com/google/android/gms/internal/zzati$zza:log	(Ljava/lang/String;)V
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_0
    //   89: getfield 555	com/google/android/gms/internal/zzast:zzadY	Ljava/lang/Boolean;
    //   92: invokevirtual 275	java/lang/Boolean:booleanValue	()Z
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
    //   0	106	0	this	zzast
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
  
  public boolean zzwk()
  {
    return zzaas.zzwk();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */