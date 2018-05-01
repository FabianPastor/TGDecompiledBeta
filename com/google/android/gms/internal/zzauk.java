package com.google.android.gms.internal;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class zzauk
  extends zzauh
{
  protected zza zzbvp;
  private volatile AppMeasurement.zzf zzbvq;
  private AppMeasurement.zzf zzbvr;
  private long zzbvs;
  private final Map<Activity, zza> zzbvt = new ArrayMap();
  private final CopyOnWriteArrayList<AppMeasurement.zzd> zzbvu = new CopyOnWriteArrayList();
  private boolean zzbvv;
  private AppMeasurement.zzf zzbvw;
  private String zzbvx;
  
  public zzauk(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  @MainThread
  private void zza(final Activity paramActivity, zza paramzza, final boolean paramBoolean)
  {
    boolean bool2 = true;
    boolean bool1 = true;
    if (this.zzbvq != null) {}
    label164:
    Object localObject;
    label290:
    for (AppMeasurement.zzf localzzf = this.zzbvq;; localObject = null)
    {
      if (localzzf != null) {}
      for (localzzf = new AppMeasurement.zzf(localzzf);; localObject = null)
      {
        this.zzbvv = true;
        try
        {
          Iterator localIterator = this.zzbvu.iterator();
          for (;;)
          {
            bool2 = bool1;
            if (!localIterator.hasNext()) {
              break label164;
            }
            bool2 = bool1;
            AppMeasurement.zzd localzzd = (AppMeasurement.zzd)localIterator.next();
            try
            {
              boolean bool3 = localzzd.zza(localzzf, paramzza);
              bool1 = bool3 & bool1;
            }
            catch (Exception localException2)
            {
              for (;;)
              {
                bool2 = bool1;
                zzKl().zzLZ().zzj("onScreenChangeCallback threw exception", localException2);
              }
            }
          }
          if ((this.zzbvr == null) || (Math.abs(zznR().elapsedRealtime() - this.zzbvs) >= 1000L)) {
            break label290;
          }
          localzzf = this.zzbvr;
          break;
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            zzKl().zzLZ().zzj("onScreenChangeCallback loop threw exception", localException1);
            this.zzbvv = false;
            bool1 = bool2;
          }
        }
        finally
        {
          this.zzbvv = false;
        }
        if (bool1)
        {
          if (paramzza.zzbqf == null) {
            paramzza.zzbqf = zzfS(paramActivity.getClass().getCanonicalName());
          }
          paramActivity = new zza(paramzza);
          this.zzbvr = this.zzbvq;
          this.zzbvs = zznR().elapsedRealtime();
          this.zzbvq = paramActivity;
          zzKk().zzm(new Runnable()
          {
            public void run()
            {
              if ((paramBoolean) && (zzauk.this.zzbvp != null)) {
                zzauk.zza(zzauk.this, zzauk.this.zzbvp);
              }
              zzauk.this.zzbvp = paramActivity;
              zzauk.this.zzKd().zza(paramActivity);
            }
          });
        }
        return;
      }
    }
  }
  
  @WorkerThread
  private void zza(@NonNull zza paramzza)
  {
    zzJY().zzW(zznR().elapsedRealtime());
    if (zzKj().zzaN(paramzza.zzbvC)) {
      paramzza.zzbvC = false;
    }
  }
  
  public static void zza(AppMeasurement.zzf paramzzf, Bundle paramBundle)
  {
    if ((paramBundle != null) && (paramzzf != null) && (!paramBundle.containsKey("_sc")))
    {
      if (paramzzf.zzbqe != null) {
        paramBundle.putString("_sn", paramzzf.zzbqe);
      }
      paramBundle.putString("_sc", paramzzf.zzbqf);
      paramBundle.putLong("_si", paramzzf.zzbqg);
    }
  }
  
  static String zzfS(String paramString)
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
  
  @MainThread
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      paramBundle = paramBundle.getBundle("com.google.firebase.analytics.screen_service");
    } while (paramBundle == null);
    paramActivity = zzv(paramActivity);
    paramActivity.zzbqg = paramBundle.getLong("id");
    paramActivity.zzbqe = paramBundle.getString("name");
    paramActivity.zzbqf = paramBundle.getString("referrer_name");
  }
  
  @MainThread
  public void onActivityDestroyed(Activity paramActivity)
  {
    this.zzbvt.remove(paramActivity);
  }
  
  @MainThread
  public void onActivityPaused(final Activity paramActivity)
  {
    paramActivity = zzv(paramActivity);
    this.zzbvr = this.zzbvq;
    this.zzbvs = zznR().elapsedRealtime();
    this.zzbvq = null;
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauk.zza(zzauk.this, paramActivity);
        zzauk.this.zzbvp = null;
        zzauk.this.zzKd().zza(null);
      }
    });
  }
  
  @MainThread
  public void onActivityResumed(Activity paramActivity)
  {
    zza(paramActivity, zzv(paramActivity), false);
    zzJY().zzJU();
  }
  
  @MainThread
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      paramActivity = (zza)this.zzbvt.get(paramActivity);
    } while (paramActivity == null);
    Bundle localBundle = new Bundle();
    localBundle.putLong("id", paramActivity.zzbqg);
    localBundle.putString("name", paramActivity.zzbqe);
    localBundle.putString("referrer_name", paramActivity.zzbqf);
    paramBundle.putBundle("com.google.firebase.analytics.screen_service", localBundle);
  }
  
  @MainThread
  public void registerOnScreenChangeCallback(@NonNull AppMeasurement.zzd paramzzd)
  {
    zzJW();
    if (paramzzd == null)
    {
      zzKl().zzMb().log("Attempting to register null OnScreenChangeCallback");
      return;
    }
    this.zzbvu.remove(paramzzd);
    this.zzbvu.add(paramzzd);
  }
  
  @MainThread
  public void setCurrentScreen(@NonNull Activity paramActivity, @Nullable @Size(max=36L, min=1L) String paramString1, @Nullable @Size(max=36L, min=1L) String paramString2)
  {
    int i = Build.VERSION.SDK_INT;
    if (paramActivity == null)
    {
      zzKl().zzMb().log("setCurrentScreen must be called with a non-null activity");
      return;
    }
    if (!zzKk().zzbc())
    {
      zzKl().zzMb().log("setCurrentScreen must be called from the main thread");
      return;
    }
    if (this.zzbvv)
    {
      zzKl().zzMb().log("Cannot call setCurrentScreen from onScreenChangeCallback");
      return;
    }
    if (this.zzbvq == null)
    {
      zzKl().zzMb().log("setCurrentScreen cannot be called while no activity active");
      return;
    }
    if (this.zzbvt.get(paramActivity) == null)
    {
      zzKl().zzMb().log("setCurrentScreen must be called with an activity in the activity lifecycle");
      return;
    }
    String str = paramString2;
    if (paramString2 == null) {
      str = zzfS(paramActivity.getClass().getCanonicalName());
    }
    boolean bool = this.zzbvq.zzbqf.equals(str);
    if (((this.zzbvq.zzbqe == null) && (paramString1 == null)) || ((this.zzbvq.zzbqe != null) && (this.zzbvq.zzbqe.equals(paramString1)))) {}
    for (i = 1; (bool) && (i != 0); i = 0)
    {
      zzKl().zzMc().log("setCurrentScreen cannot be called with the same class and name");
      return;
    }
    if ((paramString1 != null) && ((paramString1.length() < 1) || (paramString1.length() > zzKn().zzKP())))
    {
      zzKl().zzMb().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(paramString1.length()));
      return;
    }
    if ((str != null) && ((str.length() < 1) || (str.length() > zzKn().zzKP())))
    {
      zzKl().zzMb().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
      return;
    }
    zzatx.zza localzza = zzKl().zzMf();
    if (paramString1 == null) {}
    for (paramString2 = "null";; paramString2 = paramString1)
    {
      localzza.zze("Setting current screen to name, class", paramString2, str);
      paramString1 = new zza(paramString1, str, zzKh().zzNk());
      this.zzbvt.put(paramActivity, paramString1);
      zza(paramActivity, paramString1, true);
      return;
    }
  }
  
  @MainThread
  public void unregisterOnScreenChangeCallback(@NonNull AppMeasurement.zzd paramzzd)
  {
    zzJW();
    this.zzbvu.remove(paramzzd);
  }
  
  @WorkerThread
  public zza zzMW()
  {
    zzob();
    zzmR();
    return this.zzbvp;
  }
  
  public AppMeasurement.zzf zzMX()
  {
    zzJW();
    AppMeasurement.zzf localzzf = this.zzbvq;
    if (localzzf == null) {
      return null;
    }
    return new AppMeasurement.zzf(localzzf);
  }
  
  @WorkerThread
  public void zza(String paramString, AppMeasurement.zzf paramzzf)
  {
    zzmR();
    try
    {
      if ((this.zzbvx == null) || (this.zzbvx.equals(paramString)) || (paramzzf != null))
      {
        this.zzbvx = paramString;
        this.zzbvw = paramzzf;
      }
      return;
    }
    finally {}
  }
  
  protected void zzmS() {}
  
  @MainThread
  zza zzv(@NonNull Activity paramActivity)
  {
    zzac.zzw(paramActivity);
    zza localzza2 = (zza)this.zzbvt.get(paramActivity);
    zza localzza1 = localzza2;
    if (localzza2 == null)
    {
      localzza1 = new zza(null, zzfS(paramActivity.getClass().getCanonicalName()), zzKh().zzNk());
      this.zzbvt.put(paramActivity, localzza1);
    }
    return localzza1;
  }
  
  static class zza
    extends AppMeasurement.zzf
  {
    public boolean zzbvC;
    
    public zza(zza paramzza)
    {
      this.zzbqe = paramzza.zzbqe;
      this.zzbqf = paramzza.zzbqf;
      this.zzbqg = paramzza.zzbqg;
      this.zzbvC = paramzza.zzbvC;
    }
    
    public zza(String paramString1, String paramString2, long paramLong)
    {
      this.zzbqe = paramString1;
      this.zzbqf = paramString2;
      this.zzbqg = paramLong;
      this.zzbvC = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */