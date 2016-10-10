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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzqw;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class zzd
  extends zzz
{
  static final String anZ = String.valueOf(zzc.GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000).replaceAll("(\\d+)(\\d)(\\d\\d)", "$1.$2.$3");
  private Boolean bU;
  
  zzd(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  public long zza(String paramString, zzl.zza<Long> paramzza)
  {
    if (paramString == null) {
      return ((Long)paramzza.get()).longValue();
    }
    paramString = zzbvd().zzaw(paramString, paramzza.getKey());
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
  
  public boolean zzact()
  {
    return false;
  }
  
  /* Error */
  public boolean zzacu()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 99	com/google/android/gms/measurement/internal/zzd:bU	Ljava/lang/Boolean;
    //   4: ifnonnull +83 -> 87
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 99	com/google/android/gms/measurement/internal/zzd:bU	Ljava/lang/Boolean;
    //   13: ifnonnull +72 -> 85
    //   16: aload_0
    //   17: invokevirtual 100	com/google/android/gms/measurement/internal/zzd:getContext	()Landroid/content/Context;
    //   20: invokevirtual 106	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   23: astore_3
    //   24: invokestatic 111	com/google/android/gms/common/util/zzt:zzaxy	()Ljava/lang/String;
    //   27: astore_2
    //   28: aload_3
    //   29: ifnull +30 -> 59
    //   32: aload_3
    //   33: getfield 116	android/content/pm/ApplicationInfo:processName	Ljava/lang/String;
    //   36: astore_3
    //   37: aload_3
    //   38: ifnull +57 -> 95
    //   41: aload_3
    //   42: aload_2
    //   43: invokevirtual 120	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +49 -> 95
    //   49: iconst_1
    //   50: istore_1
    //   51: aload_0
    //   52: iload_1
    //   53: invokestatic 125	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   56: putfield 99	com/google/android/gms/measurement/internal/zzd:bU	Ljava/lang/Boolean;
    //   59: aload_0
    //   60: getfield 99	com/google/android/gms/measurement/internal/zzd:bU	Ljava/lang/Boolean;
    //   63: ifnonnull +22 -> 85
    //   66: aload_0
    //   67: getstatic 128	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   70: putfield 99	com/google/android/gms/measurement/internal/zzd:bU	Ljava/lang/Boolean;
    //   73: aload_0
    //   74: invokevirtual 132	com/google/android/gms/measurement/internal/zzd:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   77: invokevirtual 138	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   80: ldc -116
    //   82: invokevirtual 146	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_0
    //   88: getfield 99	com/google/android/gms/measurement/internal/zzd:bU	Ljava/lang/Boolean;
    //   91: invokevirtual 149	java/lang/Boolean:booleanValue	()Z
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
  
  long zzado()
  {
    return ((Long)zzl.api.get()).longValue();
  }
  
  public String zzadt()
  {
    return "google_app_measurement.db";
  }
  
  public String zzadu()
  {
    return "google_app_measurement2.db";
  }
  
  public long zzadz()
  {
    return Math.max(0L, ((Long)zzl.aoG.get()).longValue());
  }
  
  public String zzap(String paramString1, String paramString2)
  {
    Uri.Builder localBuilder1 = new Uri.Builder();
    Uri.Builder localBuilder2 = localBuilder1.scheme((String)zzl.aoI.get()).encodedAuthority((String)zzl.aoJ.get());
    paramString1 = String.valueOf(paramString1);
    if (paramString1.length() != 0) {}
    for (paramString1 = "config/app/".concat(paramString1);; paramString1 = new String("config/app/"))
    {
      localBuilder2.path(paramString1).appendQueryParameter("app_instance_id", paramString2).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", String.valueOf(zzbsy()));
      return localBuilder1.build().toString();
    }
  }
  
  public boolean zzasm()
  {
    return zzqw.zzasm();
  }
  
  public int zzb(String paramString, zzl.zza<Integer> paramzza)
  {
    if (paramString == null) {
      return ((Integer)paramzza.get()).intValue();
    }
    paramString = zzbvd().zzaw(paramString, paramzza.getKey());
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
  
  public long zzbsy()
  {
    return 9683L;
  }
  
  String zzbtl()
  {
    return (String)zzl.aoE.get();
  }
  
  public int zzbtm()
  {
    return 25;
  }
  
  public int zzbtn()
  {
    return 32;
  }
  
  public int zzbto()
  {
    return 24;
  }
  
  int zzbtp()
  {
    return 24;
  }
  
  int zzbtq()
  {
    return 36;
  }
  
  int zzbtr()
  {
    return 256;
  }
  
  public int zzbts()
  {
    return 36;
  }
  
  public int zzbtt()
  {
    return 2048;
  }
  
  int zzbtu()
  {
    return 500;
  }
  
  public long zzbtv()
  {
    return ((Integer)zzl.aoO.get()).intValue();
  }
  
  public long zzbtw()
  {
    return ((Integer)zzl.aoQ.get()).intValue();
  }
  
  int zzbtx()
  {
    return 25;
  }
  
  int zzbty()
  {
    return 50;
  }
  
  long zzbtz()
  {
    return 3600000L;
  }
  
  long zzbua()
  {
    return 60000L;
  }
  
  long zzbub()
  {
    return 61000L;
  }
  
  public boolean zzbuc()
  {
    if (zzact()) {}
    Boolean localBoolean;
    do
    {
      return false;
      localBoolean = zzlu("firebase_analytics_collection_deactivated");
    } while ((localBoolean == null) || (!localBoolean.booleanValue()));
    return true;
  }
  
  public Boolean zzbud()
  {
    if (zzact()) {
      return null;
    }
    return zzlu("firebase_analytics_collection_enabled");
  }
  
  public long zzbue()
  {
    return ((Long)zzl.apf.get()).longValue();
  }
  
  public long zzbuf()
  {
    return ((Long)zzl.apb.get()).longValue();
  }
  
  public long zzbug()
  {
    return 1000L;
  }
  
  public int zzbuh()
  {
    return Math.max(0, ((Integer)zzl.aoM.get()).intValue());
  }
  
  public int zzbui()
  {
    return Math.max(1, ((Integer)zzl.aoN.get()).intValue());
  }
  
  public String zzbuj()
  {
    return (String)zzl.aoU.get();
  }
  
  public long zzbuk()
  {
    return ((Long)zzl.aoH.get()).longValue();
  }
  
  public long zzbul()
  {
    return Math.max(0L, ((Long)zzl.aoV.get()).longValue());
  }
  
  public long zzbum()
  {
    return Math.max(0L, ((Long)zzl.aoX.get()).longValue());
  }
  
  public long zzbun()
  {
    return Math.max(0L, ((Long)zzl.aoY.get()).longValue());
  }
  
  public long zzbuo()
  {
    return Math.max(0L, ((Long)zzl.aoZ.get()).longValue());
  }
  
  public long zzbup()
  {
    return Math.max(0L, ((Long)zzl.apa.get()).longValue());
  }
  
  public long zzbuq()
  {
    return ((Long)zzl.aoW.get()).longValue();
  }
  
  public long zzbur()
  {
    return Math.max(0L, ((Long)zzl.apc.get()).longValue());
  }
  
  public long zzbus()
  {
    return Math.max(0L, ((Long)zzl.apd.get()).longValue());
  }
  
  public int zzbut()
  {
    return Math.min(20, Math.max(0, ((Integer)zzl.ape.get()).intValue()));
  }
  
  public String zzbuu()
  {
    try
    {
      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { "firebase.analytics.debug-mode", "" });
      return str;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzbvg().zzbwc().zzj("Could not find SystemProperties class", localClassNotFoundException);
      return "";
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("Could not find SystemProperties.get() method", localNoSuchMethodException);
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("Could not access SystemProperties.get()", localIllegalAccessException);
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("SystemProperties.get() threw an exception", localInvocationTargetException);
      }
    }
  }
  
  public int zzlo(@Size(min=1L) String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzl.aoP)));
  }
  
  public int zzlp(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzl.aoR);
  }
  
  public int zzlq(@Size(min=1L) String paramString)
  {
    return zzb(paramString, zzl.aoS);
  }
  
  long zzlr(String paramString)
  {
    return zza(paramString, zzl.aoF);
  }
  
  int zzls(String paramString)
  {
    return zzb(paramString, zzl.apg);
  }
  
  int zzlt(String paramString)
  {
    return Math.max(0, Math.min(2000, zzb(paramString, zzl.aph)));
  }
  
  @Nullable
  Boolean zzlu(@Size(min=1L) String paramString)
  {
    Boolean localBoolean = null;
    zzac.zzhz(paramString);
    Object localObject;
    try
    {
      localObject = getContext().getPackageManager();
      if (localObject == null)
      {
        zzbvg().zzbwc().log("Failed to load metadata: PackageManager is null");
        return null;
      }
      localObject = ((PackageManager)localObject).getApplicationInfo(getContext().getPackageName(), 128);
      if (localObject == null)
      {
        zzbvg().zzbwc().log("Failed to load metadata: ApplicationInfo is null");
        return null;
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      zzbvg().zzbwc().zzj("Failed to load metadata: Package name not found", paramString);
      return null;
    }
    if (((ApplicationInfo)localObject).metaData == null)
    {
      zzbvg().zzbwc().log("Failed to load metadata: Metadata bundle is null");
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
    return zzb(paramString, zzl.aoK);
  }
  
  public int zzlw(String paramString)
  {
    return Math.max(0, zzb(paramString, zzl.aoL));
  }
  
  public int zzlx(String paramString)
  {
    return Math.max(0, Math.min(1000000, zzb(paramString, zzl.aoT)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */