package com.google.android.gms.internal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class zzckc
  extends zzcjl
{
  protected zzckf zzjhn;
  private volatile AppMeasurement.zzb zzjho;
  private AppMeasurement.zzb zzjhp;
  private long zzjhq;
  private final Map<Activity, zzckf> zzjhr = new ArrayMap();
  private final CopyOnWriteArrayList<AppMeasurement.zza> zzjhs = new CopyOnWriteArrayList();
  private boolean zzjht;
  private AppMeasurement.zzb zzjhu;
  private String zzjhv;
  
  public zzckc(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final void zza(Activity paramActivity, zzckf paramzzckf, boolean paramBoolean)
  {
    int i = 1;
    boolean bool1 = true;
    if (this.zzjho != null) {}
    AppMeasurement.zzb localzzb2;
    label299:
    label314:
    for (AppMeasurement.zzb localzzb1 = this.zzjho;; localzzb2 = null)
    {
      if (localzzb1 != null) {}
      for (localzzb1 = new AppMeasurement.zzb(localzzb1);; localzzb2 = null)
      {
        this.zzjht = true;
        for (;;)
        {
          try
          {
            Iterator localIterator = this.zzjhs.iterator();
            i = bool1;
            if (!localIterator.hasNext()) {
              continue;
            }
            i = bool1;
            AppMeasurement.zza localzza = (AppMeasurement.zza)localIterator.next();
            try
            {
              boolean bool2 = localzza.zza(localzzb1, paramzzckf);
              bool1 &= bool2;
            }
            catch (Exception localException2)
            {
              i = bool1;
              zzawy().zzazd().zzj("onScreenChangeCallback threw exception", localException2);
            }
            if ((this.zzjhp == null) || (Math.abs(zzws().elapsedRealtime() - this.zzjhq) >= 1000L)) {
              break label314;
            }
            localzzb1 = this.zzjhp;
            break;
          }
          catch (Exception localException1)
          {
            zzawy().zzazd().zzj("onScreenChangeCallback loop threw exception", localException1);
            this.zzjht = false;
            if (this.zzjho != null) {
              break label299;
            }
            localzzb2 = this.zzjhp;
            if (i == 0) {
              continue;
            }
            if (paramzzckf.zziwl != null) {
              continue;
            }
            paramzzckf.zziwl = zzjy(paramActivity.getClass().getCanonicalName());
            paramActivity = new zzckf(paramzzckf);
            this.zzjhp = this.zzjho;
            this.zzjhq = zzws().elapsedRealtime();
            this.zzjho = paramActivity;
            zzawx().zzg(new zzckd(this, paramBoolean, localzzb2, paramActivity));
            return;
            this.zzjht = false;
            i = bool1;
          }
          finally
          {
            this.zzjht = false;
          }
        }
        for (;;)
        {
          localzzb2 = this.zzjho;
        }
      }
    }
  }
  
  private final void zza(zzckf paramzzckf)
  {
    zzawk().zzaj(zzws().elapsedRealtime());
    if (zzaww().zzbs(paramzzckf.zzjib)) {
      paramzzckf.zzjib = false;
    }
  }
  
  public static void zza(AppMeasurement.zzb paramzzb, Bundle paramBundle)
  {
    if ((paramBundle != null) && (paramzzb != null) && (!paramBundle.containsKey("_sc")))
    {
      if (paramzzb.zziwk != null) {
        paramBundle.putString("_sn", paramzzb.zziwk);
      }
      paramBundle.putString("_sc", paramzzb.zziwl);
      paramBundle.putLong("_si", paramzzb.zziwm);
    }
  }
  
  private static String zzjy(String paramString)
  {
    Object localObject = paramString.split("\\.");
    if (localObject.length == 0) {
      paramString = paramString.substring(0, 36);
    }
    do
    {
      return paramString;
      localObject = localObject[(localObject.length - 1)];
      paramString = (String)localObject;
    } while (((String)localObject).length() <= 36);
    return ((String)localObject).substring(0, 36);
  }
  
  public final void onActivityDestroyed(Activity paramActivity)
  {
    this.zzjhr.remove(paramActivity);
  }
  
  public final void onActivityPaused(Activity paramActivity)
  {
    paramActivity = zzq(paramActivity);
    this.zzjhp = this.zzjho;
    this.zzjhq = zzws().elapsedRealtime();
    this.zzjho = null;
    zzawx().zzg(new zzcke(this, paramActivity));
  }
  
  public final void onActivityResumed(Activity paramActivity)
  {
    zza(paramActivity, zzq(paramActivity), false);
    paramActivity = zzawk();
    long l = paramActivity.zzws().elapsedRealtime();
    paramActivity.zzawx().zzg(new zzcgg(paramActivity, l));
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      paramActivity = (zzckf)this.zzjhr.get(paramActivity);
    } while (paramActivity == null);
    Bundle localBundle = new Bundle();
    localBundle.putLong("id", paramActivity.zziwm);
    localBundle.putString("name", paramActivity.zziwk);
    localBundle.putString("referrer_name", paramActivity.zziwl);
    paramBundle.putBundle("com.google.firebase.analytics.screen_service", localBundle);
  }
  
  public final void registerOnScreenChangeCallback(AppMeasurement.zza paramzza)
  {
    if (paramzza == null)
    {
      zzawy().zzazf().log("Attempting to register null OnScreenChangeCallback");
      return;
    }
    this.zzjhs.remove(paramzza);
    this.zzjhs.add(paramzza);
  }
  
  public final void setCurrentScreen(Activity paramActivity, String paramString1, String paramString2)
  {
    if (paramActivity == null)
    {
      zzawy().zzazf().log("setCurrentScreen must be called with a non-null activity");
      return;
    }
    zzawx();
    if (!zzcih.zzau())
    {
      zzawy().zzazf().log("setCurrentScreen must be called from the main thread");
      return;
    }
    if (this.zzjht)
    {
      zzawy().zzazf().log("Cannot call setCurrentScreen from onScreenChangeCallback");
      return;
    }
    if (this.zzjho == null)
    {
      zzawy().zzazf().log("setCurrentScreen cannot be called while no activity active");
      return;
    }
    if (this.zzjhr.get(paramActivity) == null)
    {
      zzawy().zzazf().log("setCurrentScreen must be called with an activity in the activity lifecycle");
      return;
    }
    String str = paramString2;
    if (paramString2 == null) {
      str = zzjy(paramActivity.getClass().getCanonicalName());
    }
    boolean bool1 = this.zzjho.zziwl.equals(str);
    boolean bool2 = zzclq.zzas(this.zzjho.zziwk, paramString1);
    if ((bool1) && (bool2))
    {
      zzawy().zzazg().log("setCurrentScreen cannot be called with the same class and name");
      return;
    }
    if ((paramString1 != null) && ((paramString1.length() <= 0) || (paramString1.length() > 100)))
    {
      zzawy().zzazf().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(paramString1.length()));
      return;
    }
    if ((str != null) && ((str.length() <= 0) || (str.length() > 100)))
    {
      zzawy().zzazf().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
      return;
    }
    zzcho localzzcho = zzawy().zzazj();
    if (paramString1 == null) {}
    for (paramString2 = "null";; paramString2 = paramString1)
    {
      localzzcho.zze("Setting current screen to name, class", paramString2, str);
      paramString1 = new zzckf(paramString1, str, zzawu().zzbay());
      this.zzjhr.put(paramActivity, paramString1);
      zza(paramActivity, paramString1, true);
      return;
    }
  }
  
  public final void unregisterOnScreenChangeCallback(AppMeasurement.zza paramzza)
  {
    this.zzjhs.remove(paramzza);
  }
  
  public final void zza(String paramString, AppMeasurement.zzb paramzzb)
  {
    zzve();
    try
    {
      if ((this.zzjhv == null) || (this.zzjhv.equals(paramString)) || (paramzzb != null))
      {
        this.zzjhv = paramString;
        this.zzjhu = paramzzb;
      }
      return;
    }
    finally {}
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  public final zzckf zzbao()
  {
    zzxf();
    zzve();
    return this.zzjhn;
  }
  
  public final AppMeasurement.zzb zzbap()
  {
    AppMeasurement.zzb localzzb = this.zzjho;
    if (localzzb == null) {
      return null;
    }
    return new AppMeasurement.zzb(localzzb);
  }
  
  final zzckf zzq(Activity paramActivity)
  {
    zzbq.checkNotNull(paramActivity);
    zzckf localzzckf2 = (zzckf)this.zzjhr.get(paramActivity);
    zzckf localzzckf1 = localzzckf2;
    if (localzzckf2 == null)
    {
      localzzckf1 = new zzckf(null, zzjy(paramActivity.getClass().getCanonicalName()), zzawu().zzbay());
      this.zzjhr.put(paramActivity, localzzckf1);
    }
    return localzzckf1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */