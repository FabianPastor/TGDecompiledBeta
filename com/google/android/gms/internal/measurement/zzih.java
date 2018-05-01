package com.google.android.gms.internal.measurement;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.measurement.AppMeasurement.zza;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class zzih
  extends zzhk
{
  protected zzik zzapc;
  private volatile zzig zzapd;
  private zzig zzape;
  private long zzapf;
  private final Map<Activity, zzik> zzapg = new ArrayMap();
  private final CopyOnWriteArrayList<AppMeasurement.zza> zzaph = new CopyOnWriteArrayList();
  private boolean zzapi;
  
  public zzih(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final void zza(Activity paramActivity, zzik paramzzik, boolean paramBoolean)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    if (this.zzapd != null) {}
    zzig localzzig2;
    label299:
    label314:
    for (zzig localzzig1 = this.zzapd;; localzzig2 = null)
    {
      if (localzzig1 != null) {}
      for (localzzig1 = new zzig(localzzig1);; localzzig2 = null)
      {
        this.zzapi = true;
        for (;;)
        {
          try
          {
            Iterator localIterator = this.zzaph.iterator();
            bool1 = bool2;
            if (!localIterator.hasNext()) {
              continue;
            }
            bool1 = bool2;
            AppMeasurement.zza localzza = (AppMeasurement.zza)localIterator.next();
            try
            {
              boolean bool3 = localzza.zza(localzzig1, paramzzik);
              bool2 &= bool3;
            }
            catch (Exception localException2)
            {
              bool1 = bool2;
              zzgg().zzil().zzg("onScreenChangeCallback threw exception", localException2);
            }
            if ((this.zzape == null) || (Math.abs(zzbt().elapsedRealtime() - this.zzapf) >= 1000L)) {
              break label314;
            }
            localzzig1 = this.zzape;
            break;
          }
          catch (Exception localException1)
          {
            zzgg().zzil().zzg("onScreenChangeCallback loop threw exception", localException1);
            this.zzapi = false;
            bool2 = bool1;
            if (this.zzapd != null) {
              break label299;
            }
            localzzig2 = this.zzape;
            if (!bool2) {
              continue;
            }
            if (paramzzik.zzapa != null) {
              continue;
            }
            paramzzik.zzapa = zzbu(paramActivity.getClass().getCanonicalName());
            paramActivity = new zzik(paramzzik);
            this.zzape = this.zzapd;
            this.zzapf = zzbt().elapsedRealtime();
            this.zzapd = paramActivity;
            zzgf().zzc(new zzii(this, paramBoolean, localzzig2, paramActivity));
            return;
            this.zzapi = false;
          }
          finally
          {
            this.zzapi = false;
          }
        }
        for (;;)
        {
          localzzig2 = this.zzapd;
        }
      }
    }
  }
  
  public static void zza(zzig paramzzig, Bundle paramBundle, boolean paramBoolean)
  {
    if ((paramBundle != null) && (paramzzig != null) && ((!paramBundle.containsKey("_sc")) || (paramBoolean))) {
      if (paramzzig.zzug != null)
      {
        paramBundle.putString("_sn", paramzzig.zzug);
        paramBundle.putString("_sc", paramzzig.zzapa);
        paramBundle.putLong("_si", paramzzig.zzapb);
      }
    }
    for (;;)
    {
      return;
      paramBundle.remove("_sn");
      break;
      if ((paramBundle != null) && (paramzzig == null) && (paramBoolean))
      {
        paramBundle.remove("_sn");
        paramBundle.remove("_sc");
        paramBundle.remove("_si");
      }
    }
  }
  
  private final void zza(zzik paramzzik)
  {
    zzfs().zzk(zzbt().elapsedRealtime());
    if (zzge().zzm(paramzzik.zzapq)) {
      paramzzik.zzapq = false;
    }
  }
  
  private static String zzbu(String paramString)
  {
    paramString = paramString.split("\\.");
    if (paramString.length > 0) {}
    for (paramString = paramString[(paramString.length - 1)];; paramString = "")
    {
      String str = paramString;
      if (paramString.length() > 100) {
        str = paramString.substring(0, 100);
      }
      return str;
    }
  }
  
  public final void onActivityDestroyed(Activity paramActivity)
  {
    this.zzapg.remove(paramActivity);
  }
  
  public final void onActivityPaused(Activity paramActivity)
  {
    paramActivity = zze(paramActivity);
    this.zzape = this.zzapd;
    this.zzapf = zzbt().elapsedRealtime();
    this.zzapd = null;
    zzgf().zzc(new zzij(this, paramActivity));
  }
  
  public final void onActivityResumed(Activity paramActivity)
  {
    zza(paramActivity, zze(paramActivity), false);
    paramActivity = zzfs();
    long l = paramActivity.zzbt().elapsedRealtime();
    paramActivity.zzgf().zzc(new zzea(paramActivity, l));
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    if (paramBundle == null) {}
    for (;;)
    {
      return;
      zzik localzzik = (zzik)this.zzapg.get(paramActivity);
      if (localzzik != null)
      {
        paramActivity = new Bundle();
        paramActivity.putLong("id", localzzik.zzapb);
        paramActivity.putString("name", localzzik.zzug);
        paramActivity.putString("referrer_name", localzzik.zzapa);
        paramBundle.putBundle("com.google.firebase.analytics.screen_service", paramActivity);
      }
    }
  }
  
  public final void registerOnScreenChangeCallback(AppMeasurement.zza paramzza)
  {
    if (paramzza == null) {
      zzgg().zzin().log("Attempting to register null OnScreenChangeCallback");
    }
    for (;;)
    {
      return;
      this.zzaph.remove(paramzza);
      this.zzaph.add(paramzza);
    }
  }
  
  public final void setCurrentScreen(Activity paramActivity, String paramString1, String paramString2)
  {
    zzgf();
    if (!zzgg.isMainThread()) {
      zzgg().zzin().log("setCurrentScreen must be called from the main thread");
    }
    String str;
    for (;;)
    {
      return;
      if (this.zzapi)
      {
        zzgg().zzin().log("Cannot call setCurrentScreen from onScreenChangeCallback");
      }
      else if (this.zzapd == null)
      {
        zzgg().zzin().log("setCurrentScreen cannot be called while no activity active");
      }
      else if (this.zzapg.get(paramActivity) == null)
      {
        zzgg().zzin().log("setCurrentScreen must be called with an activity in the activity lifecycle");
      }
      else
      {
        str = paramString2;
        if (paramString2 == null) {
          str = zzbu(paramActivity.getClass().getCanonicalName());
        }
        boolean bool1 = this.zzapd.zzapa.equals(str);
        boolean bool2 = zzjv.zzs(this.zzapd.zzug, paramString1);
        if ((bool1) && (bool2))
        {
          zzgg().zzio().log("setCurrentScreen cannot be called with the same class and name");
        }
        else if ((paramString1 != null) && ((paramString1.length() <= 0) || (paramString1.length() > 100)))
        {
          zzgg().zzin().zzg("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(paramString1.length()));
        }
        else
        {
          if ((str == null) || ((str.length() > 0) && (str.length() <= 100))) {
            break;
          }
          zzgg().zzin().zzg("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
        }
      }
    }
    zzfi localzzfi = zzgg().zzir();
    if (paramString1 == null) {}
    for (paramString2 = "null";; paramString2 = paramString1)
    {
      localzzfi.zze("Setting current screen to name, class", paramString2, str);
      paramString1 = new zzik(paramString1, str, zzgc().zzkt());
      this.zzapg.put(paramActivity, paramString1);
      zza(paramActivity, paramString1, true);
      break;
    }
  }
  
  public final void unregisterOnScreenChangeCallback(AppMeasurement.zza paramzza)
  {
    this.zzaph.remove(paramzza);
  }
  
  final zzik zze(Activity paramActivity)
  {
    Preconditions.checkNotNull(paramActivity);
    zzik localzzik1 = (zzik)this.zzapg.get(paramActivity);
    zzik localzzik2 = localzzik1;
    if (localzzik1 == null)
    {
      localzzik2 = new zzik(null, zzbu(paramActivity.getClass().getCanonicalName()), zzgc().zzkt());
      this.zzapg.put(paramActivity, localzzik2);
    }
    return localzzik2;
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  public final zzik zzkk()
  {
    zzch();
    zzab();
    return this.zzapc;
  }
  
  public final zzig zzkl()
  {
    zzig localzzig = this.zzapd;
    if (localzzig == null) {}
    for (localzzig = null;; localzzig = new zzig(localzzig)) {
      return localzzig;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzih.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */