package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzrk;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class zzd
  extends zzz
{
  static final String ari = String.valueOf(zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
  private Boolean eb;
  
  zzd(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  public long zza(String paramString, zzl.zza<Long> paramzza)
  {
    if (paramString == null) {
      return ((Long)paramzza.get()).longValue();
    }
    paramString = zzbvy().zzaw(paramString, paramzza.getKey());
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
  
  /* Error */
  public boolean zzaef()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 98	com/google/android/gms/measurement/internal/zzd:eb	Ljava/lang/Boolean;
    //   4: ifnonnull +83 -> 87
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 98	com/google/android/gms/measurement/internal/zzd:eb	Ljava/lang/Boolean;
    //   13: ifnonnull +72 -> 85
    //   16: aload_0
    //   17: invokevirtual 99	com/google/android/gms/measurement/internal/zzd:getContext	()Landroid/content/Context;
    //   20: invokevirtual 105	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 110	com/google/android/gms/common/util/zzt:zzayz	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_3
    //   29: ifnull +30 -> 59
    //   32: aload_3
    //   33: getfield 115	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnull +57 -> 95
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 119	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +49 -> 95
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: iload_1
    //   53: invokestatic 124	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 98	com/google/android/gms/measurement/internal/zzd:eb	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 98	com/google/android/gms/measurement/internal/zzd:eb	Ljava/lang/Boolean;
    //   63: ifnonnull +22 -> 85
    //   66: aload_0
    //   67: getstatic 127	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 98	com/google/android/gms/measurement/internal/zzd:eb	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 131	com/google/android/gms/measurement/internal/zzd:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   77: invokevirtual 137	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   80: ldc -117
    //   82: invokevirtual 145	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_0
    //   88: getfield 98	com/google/android/gms/measurement/internal/zzd:eb	Ljava/lang/Boolean;
    //   91: invokevirtual 148	java/lang/Boolean:booleanValue	()Z
    //   94: ireturn
    //   95: iconst_0
    //   96: istore_1
    //   97: goto -46 -> 51
    //   100: astore_2
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_2
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	zzd
    //   50	47	1	bool	boolean
    //   27	16	2	str	String
    //   100	4	2	localObject1	Object
    //   23	19	3	localObject2	Object
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
  
  long zzaez()
  {
    return ((Long)zzl.asu.get()).longValue();
  }
  
  public String zzafe()
  {
    return "google_app_measurement.db";
  }
  
  public long zzafj()
  {
    return Math.max(0L, ((Long)zzl.arR.get()).longValue());
  }
  
  public String zzao(String paramString1, String paramString2)
  {
    Uri.Builder localBuilder1 = new Uri.Builder();
    Uri.Builder localBuilder2 = localBuilder1.scheme((String)zzl.arT.get()).encodedAuthority((String)zzl.arU.get());
    paramString1 = String.valueOf(paramString1);
    if (paramString1.length() != 0) {}
    for (paramString1 = "config/app/".concat(paramString1);; paramString1 = new String("config/app/"))
    {
      localBuilder2.path(paramString1).appendQueryParameter("app_instance_id", paramString2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(9877L));
      return localBuilder1.build().toString();
    }
  }
  
  public boolean zzatu()
  {
    return zzrk.zzatu();
  }
  
  public boolean zzayi()
  {
    return false;
  }
  
  public int zzb(String paramString, zzl.zza<Integer> paramzza)
  {
    if (paramString == null) {
      return ((Integer)paramzza.get()).intValue();
    }
    paramString = zzbvy().zzaw(paramString, paramzza.getKey());
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
  
  public long zzbto()
  {
    return 9877L;
  }
  
  String zzbub()
  {
    return (String)zzl.arP.get();
  }
  
  public int zzbuc()
  {
    return 25;
  }
  
  public int zzbud()
  {
    return 32;
  }
  
  public int zzbue()
  {
    return 24;
  }
  
  int zzbuf()
  {
    return 24;
  }
  
  int zzbug()
  {
    return 36;
  }
  
  int zzbuh()
  {
    return 256;
  }
  
  public int zzbui()
  {
    return 36;
  }
  
  public int zzbuj()
  {
    return 2048;
  }
  
  int zzbuk()
  {
    return 500;
  }
  
  public long zzbul()
  {
    return ((Integer)zzl.arZ.get()).intValue();
  }
  
  public long zzbum()
  {
    return ((Integer)zzl.asb.get()).intValue();
  }
  
  int zzbun()
  {
    return 25;
  }
  
  int zzbuo()
  {
    return 50;
  }
  
  long zzbup()
  {
    return 3600000L;
  }
  
  long zzbuq()
  {
    return 60000L;
  }
  
  long zzbur()
  {
    return 61000L;
  }
  
  String zzbus()
  {
    return "google_app_measurement_local.db";
  }
  
  public boolean zzbut()
  {
    Boolean localBoolean = zzlu("firebase_analytics_collection_deactivated");
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public Boolean zzbuu()
  {
    return zzlu("firebase_analytics_collection_enabled");
  }
  
  public long zzbuv()
  {
    return ((Long)zzl.asr.get()).longValue();
  }
  
  public long zzbuw()
  {
    return ((Long)zzl.asm.get()).longValue();
  }
  
  public long zzbux()
  {
    return ((Long)zzl.asn.get()).longValue();
  }
  
  public long zzbuy()
  {
    return 1000L;
  }
  
  public int zzbuz()
  {
    return Math.max(0, ((Integer)zzl.arX.get()).intValue());
  }
  
  public int zzbva()
  {
    return Math.max(1, ((Integer)zzl.arY.get()).intValue());
  }
  
  public int zzbvb()
  {
    return 100000;
  }
  
  public String zzbvc()
  {
    return (String)zzl.asf.get();
  }
  
  public long zzbvd()
  {
    return ((Long)zzl.arS.get()).longValue();
  }
  
  public long zzbve()
  {
    return Math.max(0L, ((Long)zzl.asg.get()).longValue());
  }
  
  public long zzbvf()
  {
    return Math.max(0L, ((Long)zzl.asi.get()).longValue());
  }
  
  public long zzbvg()
  {
    return Math.max(0L, ((Long)zzl.asj.get()).longValue());
  }
  
  public long zzbvh()
  {
    return Math.max(0L, ((Long)zzl.ask.get()).longValue());
  }
  
  public long zzbvi()
  {
    return Math.max(0L, ((Long)zzl.asl.get()).longValue());
  }
  
  public long zzbvj()
  {
    return ((Long)zzl.ash.get()).longValue();
  }
  
  public long zzbvk()
  {
    return Math.max(0L, ((Long)zzl.aso.get()).longValue());
  }
  
  public long zzbvl()
  {
    return Math.max(0L, ((Long)zzl.asp.get()).longValue());
  }
  
  public int zzbvm()
  {
    return Math.min(20, Math.max(0, ((Integer)zzl.asq.get()).intValue()));
  }
  
  public String zzbvn()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "firebase.analytics.debug-mode", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzbwb().zzbwy().zzj("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
  
  public int zzlo(@Size(min=1L) String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzl.asa)));
  }
  
  public int zzlp(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzl.asc);
  }
  
  public int zzlq(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzl.asd);
  }
  
  long zzlr(String paramString)
  {
    return zza(paramString, zzl.arQ);
  }
  
  int zzls(String paramString)
  {
    return zzb(paramString, zzl.ass);
  }
  
  int zzlt(String paramString)
  {
    return Math.max(0, Math.min(2000, zzb(paramString, zzl.ast)));
  }
  
  @Nullable
  Boolean zzlu(@Size(min=1L) String paramString)
  {
    Boolean localBoolean = null;
    zzaa.zzib(paramString);
    Object localObject;
    try
    {
      localObject = getContext().getPackageManager();
      if (localObject == null)
      {
        zzbwb().zzbwy().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localObject = ((PackageManager)localObject).getApplicationInfo(getContext().getPackageName(), 128);
      if (localObject == null)
      {
        zzbwb().zzbwy().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      zzbwb().zzbwy().zzj("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (((ApplicationInfo)localObject).metaData == null)
    {
      zzbwb().zzbwy().log("Failed to load metadata: Metadata bundle is null");
      return null;
    }
    if (((ApplicationInfo)localObject).metaData.containsKey(paramString))
    {
      boolean bool = ((ApplicationInfo)localObject).metaData.getBoolean(paramString);
      localBoolean = Boolean.valueOf(bool);
    }
    return localBoolean;
  }
  
  public int zzlv(String paramString)
  {
    return zzb(paramString, zzl.arV);
  }
  
  public int zzlw(String paramString)
  {
    return Math.max(0, zzb(paramString, zzl.arW));
  }
  
  public int zzlx(String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzl.ase)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */