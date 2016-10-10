package com.google.android.gms.measurement.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzf;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import com.google.android.gms.measurement.AppMeasurement.zzc;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public class zzac
  extends zzaa
{
  private zza arA;
  private AppMeasurement.zzb arB;
  private final Set<AppMeasurement.zzc> arC = new CopyOnWriteArraySet();
  private boolean arD;
  
  protected zzac(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zza(paramString1, paramString2, zzaan().currentTimeMillis(), paramBundle, paramBoolean1, paramBoolean2, paramBoolean3, paramString3);
  }
  
  @WorkerThread
  private void zza(String paramString1, String paramString2, Object paramObject, long paramLong)
  {
    com.google.android.gms.common.internal.zzac.zzhz(paramString1);
    com.google.android.gms.common.internal.zzac.zzhz(paramString2);
    zzyl();
    zzaam();
    zzaax();
    if (!this.anq.isEnabled()) {
      zzbvg().zzbwi().log("User property not set since app measurement is disabled");
    }
    while (!this.anq.zzbwv()) {
      return;
    }
    zzbvg().zzbwi().zze("Setting user property (FE)", paramString2, paramObject);
    paramString1 = new UserAttributeParcel(paramString2, paramLong, paramObject, paramString1);
    zzbva().zza(paramString1);
  }
  
  @WorkerThread
  private void zzb(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    com.google.android.gms.common.internal.zzac.zzhz(paramString1);
    com.google.android.gms.common.internal.zzac.zzhz(paramString2);
    com.google.android.gms.common.internal.zzac.zzy(paramBundle);
    zzyl();
    zzaax();
    if (!this.anq.isEnabled()) {
      zzbvg().zzbwi().log("Event not sent since app measurement is disabled");
    }
    do
    {
      return;
      if (!this.arD)
      {
        this.arD = true;
        zzbxx();
      }
      boolean bool = zzal.zznh(paramString2);
      if ((paramBoolean1) && (this.arB != null) && (!bool))
      {
        zzbvg().zzbwi().zze("Passing event to registered event handler (FE)", paramString2, paramBundle);
        this.arB.zzb(paramString1, paramString2, paramBundle, paramLong);
        return;
      }
    } while (!this.anq.zzbwv());
    int j = zzbvc().zzmz(paramString2);
    if (j != 0)
    {
      paramString1 = zzbvc().zza(paramString2, zzbvi().zzbtn(), true);
      if (paramString2 != null) {}
      for (int i = paramString2.length();; i = 0)
      {
        this.anq.zzbvc().zza(j, "_ev", paramString1, i);
        return;
      }
    }
    paramBundle.putString("_o", paramString1);
    Object localObject = zzf.zzz("_o");
    paramBundle = zzbvc().zza(paramString2, paramBundle, (List)localObject, paramBoolean3);
    if (paramBoolean2) {
      paramBundle = zzam(paramBundle);
    }
    for (;;)
    {
      zzbvg().zzbwi().zze("Logging event (FE)", paramString2, paramBundle);
      localObject = new EventParcel(paramString2, new EventParams(paramBundle), paramString1, paramLong);
      zzbva().zzc((EventParcel)localObject, paramString3);
      paramString3 = this.arC.iterator();
      while (paramString3.hasNext()) {
        ((AppMeasurement.zzc)paramString3.next()).zzc(paramString1, paramString2, new Bundle(paramBundle), paramLong);
      }
      break;
    }
  }
  
  @WorkerThread
  private void zzbxx()
  {
    try
    {
      zzg(Class.forName(zzbxy()));
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzbvg().zzbwh().log("Tag Manager is not found and thus will not be used");
    }
  }
  
  private String zzbxy()
  {
    return "com.google.android.gms.tagmanager.TagManagerService";
  }
  
  @WorkerThread
  private void zzch(boolean paramBoolean)
  {
    zzyl();
    zzaam();
    zzaax();
    zzbvg().zzbwi().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(paramBoolean));
    zzbvh().setMeasurementEnabled(paramBoolean);
    zzbva().zzbxz();
  }
  
  public void setMeasurementEnabled(final boolean paramBoolean)
  {
    zzaax();
    zzaam();
    zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzac.zza(zzac.this, paramBoolean);
      }
    });
  }
  
  public void setMinimumSessionDuration(final long paramLong)
  {
    zzaam();
    zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzac.this.zzbvh().apZ.set(paramLong);
        zzac.this.zzbvg().zzbwi().zzj("Minimum session duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  public void setSessionTimeoutDuration(final long paramLong)
  {
    zzaam();
    zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzac.this.zzbvh().aqa.set(paramLong);
        zzac.this.zzbvg().zzbwi().zzj("Session timeout duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  @WorkerThread
  public void zza(AppMeasurement.zzb paramzzb)
  {
    zzyl();
    zzaam();
    zzaax();
    if ((paramzzb != null) && (paramzzb != this.arB)) {
      if (this.arB != null) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      com.google.android.gms.common.internal.zzac.zza(bool, "EventInterceptor already set.");
      this.arB = paramzzb;
      return;
    }
  }
  
  public void zza(AppMeasurement.zzc paramzzc)
  {
    zzaam();
    zzaax();
    com.google.android.gms.common.internal.zzac.zzy(paramzzc);
    if (!this.arC.add(paramzzc)) {
      zzbvg().zzbwe().log("OnEventListener already registered");
    }
  }
  
  protected void zza(final String paramString1, final String paramString2, final long paramLong, Bundle paramBundle, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final String paramString3)
  {
    if (paramBundle != null) {}
    for (paramBundle = new Bundle(paramBundle);; paramBundle = new Bundle())
    {
      zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzac.zza(zzac.this, paramString1, paramString2, paramLong, paramBoolean1, paramBoolean2, paramBoolean3, paramString3, this.aip);
        }
      });
      return;
    }
  }
  
  void zza(final String paramString1, final String paramString2, final long paramLong, final Object paramObject)
  {
    zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzac.zza(zzac.this, paramString1, paramString2, paramObject, paramLong);
      }
    });
  }
  
  public void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    zzaam();
    if ((this.arB == null) || (zzal.zznh(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, paramBoolean, null);
      return;
    }
  }
  
  Bundle zzam(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = zzbvc().zzl(str, paramBundle.get(str));
        if (localObject == null) {
          zzbvg().zzbwe().zzj("Param value can't be null", str);
        } else {
          zzbvc().zza(localBundle, str, localObject);
        }
      }
    }
    return localBundle;
  }
  
  @TargetApi(14)
  public void zzbxv()
  {
    if ((getContext().getApplicationContext() instanceof Application))
    {
      Application localApplication = (Application)getContext().getApplicationContext();
      if (this.arA == null) {
        this.arA = new zza(null);
      }
      localApplication.unregisterActivityLifecycleCallbacks(this.arA);
      localApplication.registerActivityLifecycleCallbacks(this.arA);
      zzbvg().zzbwj().log("Registered activity lifecycle callback");
    }
  }
  
  @WorkerThread
  public void zzbxw()
  {
    zzyl();
    zzaam();
    zzaax();
    if (!this.anq.zzbwv()) {}
    String str;
    do
    {
      return;
      zzbva().zzbxw();
      str = zzbvh().zzbws();
    } while ((TextUtils.isEmpty(str)) || (str.equals(zzbuz().zzbvv())));
    Bundle localBundle = new Bundle();
    localBundle.putString("_po", str);
    zzf("auto", "_ou", localBundle);
  }
  
  public List<UserAttributeParcel> zzci(final boolean paramBoolean)
  {
    zzaam();
    zzaax();
    zzbvg().zzbwi().log("Fetching user attributes (FE)");
    synchronized (new AtomicReference())
    {
      this.anq.zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzac.this.zzbva().zza(localObject1, paramBoolean);
        }
      });
      try
      {
        ???.wait(5000L);
        List localList = (List)((AtomicReference)???).get();
        ??? = localList;
        if (localList == null)
        {
          zzbvg().zzbwe().log("Timed out waiting for get user properties");
          ??? = Collections.emptyList();
        }
        return (List<UserAttributeParcel>)???;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzbvg().zzbwe().zzj("Interrupted waiting for get user properties", localInterruptedException);
        }
      }
    }
  }
  
  public void zzd(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    zzaam();
    zza(paramString1, paramString2, paramLong, paramBundle, false, true, true, null);
  }
  
  public void zzd(String paramString1, String paramString2, Object paramObject)
  {
    int i = 0;
    int j = 0;
    com.google.android.gms.common.internal.zzac.zzhz(paramString1);
    long l = zzaan().currentTimeMillis();
    int k = zzbvc().zznb(paramString2);
    if (k != 0)
    {
      paramString1 = zzbvc().zza(paramString2, zzbvi().zzbto(), true);
      i = j;
      if (paramString2 != null) {
        i = paramString2.length();
      }
      this.anq.zzbvc().zza(k, "_ev", paramString1, i);
    }
    do
    {
      return;
      if (paramObject == null) {
        break;
      }
      j = zzbvc().zzm(paramString2, paramObject);
      if (j != 0)
      {
        paramString1 = zzbvc().zza(paramString2, zzbvi().zzbto(), true);
        if (((paramObject instanceof String)) || ((paramObject instanceof CharSequence))) {
          i = String.valueOf(paramObject).length();
        }
        this.anq.zzbvc().zza(j, "_ev", paramString1, i);
        return;
      }
      paramObject = zzbvc().zzn(paramString2, paramObject);
    } while (paramObject == null);
    zza(paramString1, paramString2, l, paramObject);
    return;
    zza(paramString1, paramString2, l, null);
  }
  
  public void zzf(String paramString1, String paramString2, Bundle paramBundle)
  {
    zzaam();
    if ((this.arB == null) || (zzal.zznh(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, false, null);
      return;
    }
  }
  
  @WorkerThread
  public void zzg(Class<?> paramClass)
  {
    try
    {
      paramClass.getDeclaredMethod("initialize", new Class[] { Context.class }).invoke(null, new Object[] { getContext() });
      return;
    }
    catch (Exception paramClass)
    {
      zzbvg().zzbwe().zzj("Failed to invoke Tag Manager's initialize() method", paramClass);
    }
  }
  
  protected void zzym() {}
  
  @TargetApi(14)
  @MainThread
  private class zza
    implements Application.ActivityLifecycleCallbacks
  {
    private zza() {}
    
    private boolean zzmv(String paramString)
    {
      if (!TextUtils.isEmpty(paramString))
      {
        zzac.this.zzd("auto", "_ldl", paramString);
        return true;
      }
      return false;
    }
    
    public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
    {
      try
      {
        zzac.this.zzbvg().zzbwj().log("onActivityCreated");
        paramActivity = paramActivity.getIntent();
        if (paramActivity == null) {
          return;
        }
        paramActivity = paramActivity.getData();
        if ((paramActivity == null) || (!paramActivity.isHierarchical())) {
          return;
        }
        if (paramBundle == null)
        {
          paramBundle = zzac.this.zzbvc().zzt(paramActivity);
          if (paramBundle != null) {
            zzac.this.zzf("auto", "_cmp", paramBundle);
          }
        }
        paramActivity = paramActivity.getQueryParameter("referrer");
        if (TextUtils.isEmpty(paramActivity)) {
          return;
        }
        if (!paramActivity.contains("gclid"))
        {
          zzac.this.zzbvg().zzbwi().log("Activity created with data 'referrer' param without gclid");
          return;
        }
      }
      catch (Throwable paramActivity)
      {
        zzac.this.zzbvg().zzbwc().zzj("Throwable caught in onActivityCreated", paramActivity);
        return;
      }
      zzac.this.zzbvg().zzbwi().zzj("Activity created with referrer", paramActivity);
      zzmv(paramActivity);
    }
    
    public void onActivityDestroyed(Activity paramActivity) {}
    
    @MainThread
    public void onActivityPaused(Activity paramActivity)
    {
      zzac.this.zzbve().zzbyh();
    }
    
    @MainThread
    public void onActivityResumed(Activity paramActivity)
    {
      zzac.this.zzbve().zzbyf();
    }
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */