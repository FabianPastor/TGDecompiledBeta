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
import com.google.android.gms.common.zze;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class zzati
  extends zzaug
{
  static final String zzbrg = String.valueOf(zze.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
  private Boolean zzaeZ;
  
  zzati(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  String zzKK()
  {
    return (String)zzats.zzbrP.get();
  }
  
  public int zzKL()
  {
    return 25;
  }
  
  public int zzKM()
  {
    return 40;
  }
  
  public int zzKN()
  {
    return 24;
  }
  
  int zzKO()
  {
    return 40;
  }
  
  int zzKP()
  {
    return 100;
  }
  
  int zzKQ()
  {
    return 256;
  }
  
  int zzKR()
  {
    return 1000;
  }
  
  public int zzKS()
  {
    return 36;
  }
  
  public int zzKT()
  {
    return 2048;
  }
  
  int zzKU()
  {
    return 500;
  }
  
  public long zzKV()
  {
    return ((Integer)zzats.zzbrZ.get()).intValue();
  }
  
  public long zzKW()
  {
    return ((Integer)zzats.zzbsb.get()).intValue();
  }
  
  int zzKX()
  {
    return 25;
  }
  
  int zzKY()
  {
    return 1000;
  }
  
  int zzKZ()
  {
    return 25;
  }
  
  public long zzKv()
  {
    return 10260L;
  }
  
  public long zzLA()
  {
    return Math.max(0L, ((Long)zzats.zzbsp.get()).longValue());
  }
  
  public long zzLB()
  {
    return Math.max(0L, ((Long)zzats.zzbsq.get()).longValue());
  }
  
  public int zzLC()
  {
    return Math.min(20, Math.max(0, ((Integer)zzats.zzbsr.get()).intValue()));
  }
  
  public String zzLD()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "debug.firebase.analytics.app", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzKl().zzLZ().zzj("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzKl().zzLZ().zzj("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzKl().zzLZ().zzj("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzKl().zzLZ().zzj("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
  
  int zzLa()
  {
    return 1000;
  }
  
  long zzLb()
  {
    return 15552000000L;
  }
  
  long zzLc()
  {
    return 15552000000L;
  }
  
  long zzLd()
  {
    return 3600000L;
  }
  
  long zzLe()
  {
    return 60000L;
  }
  
  long zzLf()
  {
    return 61000L;
  }
  
  String zzLg()
  {
    return "google_app_measurement_local.db";
  }
  
  public boolean zzLh()
  {
    return false;
  }
  
  public boolean zzLi()
  {
    Boolean localBoolean = zzfp("firebase_analytics_collection_deactivated");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public Boolean zzLj()
  {
    return zzfp("firebase_analytics_collection_enabled");
  }
  
  public long zzLk()
  {
    return ((Long)zzats.zzbss.get()).longValue();
  }
  
  public long zzLl()
  {
    return ((Long)zzats.zzbsn.get()).longValue();
  }
  
  public long zzLm()
  {
    return ((Long)zzats.zzbso.get()).longValue();
  }
  
  public long zzLn()
  {
    return 1000L;
  }
  
  public int zzLo()
  {
    return Math.max(0, ((Integer)zzats.zzbrX.get()).intValue());
  }
  
  public int zzLp()
  {
    return Math.max(1, ((Integer)zzats.zzbrY.get()).intValue());
  }
  
  public int zzLq()
  {
    return 100000;
  }
  
  public String zzLr()
  {
    return (String)zzats.zzbsf.get();
  }
  
  public long zzLs()
  {
    return ((Long)zzats.zzbrS.get()).longValue();
  }
  
  public long zzLt()
  {
    return Math.max(0L, ((Long)zzats.zzbsg.get()).longValue());
  }
  
  public long zzLu()
  {
    return Math.max(0L, ((Long)zzats.zzbsi.get()).longValue());
  }
  
  public long zzLv()
  {
    return Math.max(0L, ((Long)zzats.zzbsj.get()).longValue());
  }
  
  public long zzLw()
  {
    return Math.max(0L, ((Long)zzats.zzbsk.get()).longValue());
  }
  
  public long zzLx()
  {
    return Math.max(0L, ((Long)zzats.zzbsl.get()).longValue());
  }
  
  public long zzLy()
  {
    return Math.max(0L, ((Long)zzats.zzbsm.get()).longValue());
  }
  
  public long zzLz()
  {
    return ((Long)zzats.zzbsh.get()).longValue();
  }
  
  public String zzP(String paramString1, String paramString2)
  {
    Uri.Builder localBuilder1 = new Uri.Builder();
    Uri.Builder localBuilder2 = localBuilder1.scheme((String)zzats.zzbrT.get()).encodedAuthority((String)zzats.zzbrU.get());
    paramString1 = String.valueOf(paramString1);
    if (paramString1.length() != 0) {}
    for (paramString1 = "config/app/".concat(paramString1);; paramString1 = new String("config/app/"))
    {
      localBuilder2.path(paramString1).appendQueryParameter("app_instance_id", paramString2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(10260L));
      return localBuilder1.build().toString();
    }
  }
  
  public long zza(String paramString, zzats.zza<Long> paramzza)
  {
    if (paramString == null) {
      return ((Long)paramzza.get()).longValue();
    }
    paramString = zzKi().zzZ(paramString, paramzza.getKey());
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
  
  public int zzb(String paramString, zzats.zza<Integer> paramzza)
  {
    if (paramString == null) {
      return ((Integer)paramzza.get()).intValue();
    }
    paramString = zzKi().zzZ(paramString, paramzza.getKey());
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
  
  public int zzfj(@Size(min=1L) String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzats.zzbsa)));
  }
  
  public int zzfk(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzats.zzbsc);
  }
  
  public int zzfl(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzats.zzbsd);
  }
  
  long zzfm(String paramString)
  {
    return zza(paramString, zzats.zzbrQ);
  }
  
  int zzfn(String paramString)
  {
    return zzb(paramString, zzats.zzbst);
  }
  
  int zzfo(String paramString)
  {
    return Math.max(0, Math.min(2000, zzb(paramString, zzats.zzbsu)));
  }
  
  @Nullable
  Boolean zzfp(@Size(min=1L) String paramString)
  {
    Boolean localBoolean = null;
    zzac.zzdr(paramString);
    ApplicationInfo localApplicationInfo;
    try
    {
      if (getContext().getPackageManager() == null)
      {
        zzKl().zzLZ().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localApplicationInfo = zzadg.zzbi(getContext()).getApplicationInfo(getContext().getPackageName(), 128);
      if (localApplicationInfo == null)
      {
        zzKl().zzLZ().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      zzKl().zzLZ().zzj("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (localApplicationInfo.metaData == null)
    {
      zzKl().zzLZ().log("Failed to load metadata: Metadata bundle is null");
      return null;
    }
    if (localApplicationInfo.metaData.containsKey(paramString))
    {
      boolean bool = localApplicationInfo.metaData.getBoolean(paramString);
      localBoolean = Boolean.valueOf(bool);
    }
    return localBoolean;
  }
  
  public int zzfq(String paramString)
  {
    return zzb(paramString, zzats.zzbrV);
  }
  
  public int zzfr(String paramString)
  {
    return Math.max(0, zzb(paramString, zzats.zzbrW));
  }
  
  public int zzfs(String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzats.zzbse)));
  }
  
  /* Error */
  public boolean zzoW()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 555	com/google/android/gms/internal/zzati:zzaeZ	Ljava/lang/Boolean;
    //   4: ifnonnull +84 -> 88
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 555	com/google/android/gms/internal/zzati:zzaeZ	Ljava/lang/Boolean;
    //   13: ifnonnull +73 -> 86
    //   16: aload_0
    //   17: invokevirtual 482	com/google/android/gms/internal/zzati:getContext	()Landroid/content/Context;
    //   20: invokevirtual 558	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 563	com/google/android/gms/common/util/zzu:zzzr	()Ljava/lang/String;
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
    //   53: invokestatic 532	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 555	com/google/android/gms/internal/zzati:zzaeZ	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 555	com/google/android/gms/internal/zzati:zzaeZ	Ljava/lang/Boolean;
    //   63: ifnonnull +23 -> 86
    //   66: aload_0
    //   67: getstatic 573	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 555	com/google/android/gms/internal/zzati:zzaeZ	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 223	com/google/android/gms/internal/zzati:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   77: invokevirtual 229	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   80: ldc_w 575
    //   83: invokevirtual 493	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
    //   86: aload_0
    //   87: monitorexit
    //   88: aload_0
    //   89: getfield 555	com/google/android/gms/internal/zzati:zzaeZ	Ljava/lang/Boolean;
    //   92: invokevirtual 274	java/lang/Boolean:booleanValue	()Z
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
    //   0	106	0	this	zzati
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
  
  long zzpq()
  {
    return ((Long)zzats.zzbsv.get()).longValue();
  }
  
  public String zzpv()
  {
    return "google_app_measurement.db";
  }
  
  public long zzpz()
  {
    return Math.max(0L, ((Long)zzats.zzbrR.get()).longValue());
  }
  
  public boolean zzwR()
  {
    return zzaba.zzwR();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzati.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */