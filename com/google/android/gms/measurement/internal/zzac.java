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
  protected zza auO;
  private AppMeasurement.zzb auP;
  private final Set<AppMeasurement.zzc> auQ = new CopyOnWriteArraySet();
  private boolean auR;
  
  protected zzac(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zza(paramString1, paramString2, zzabz().currentTimeMillis(), paramBundle, paramBoolean1, paramBoolean2, paramBoolean3, paramString3);
  }
  
  @WorkerThread
  private void zza(String paramString1, String paramString2, Object paramObject, long paramLong)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString1);
    com.google.android.gms.common.internal.zzaa.zzib(paramString2);
    zzzx();
    zzaby();
    zzacj();
    if (!this.aqw.isEnabled()) {
      zzbwb().zzbxd().log("User property not set since app measurement is disabled");
    }
    while (!this.aqw.zzbxq()) {
      return;
    }
    zzbwb().zzbxd().zze("Setting user property (FE)", paramString2, paramObject);
    paramString1 = new UserAttributeParcel(paramString2, paramLong, paramObject, paramString1);
    zzbvt().zzb(paramString1);
  }
  
  @WorkerThread
  private void zzb(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString1);
    com.google.android.gms.common.internal.zzaa.zzib(paramString2);
    com.google.android.gms.common.internal.zzaa.zzy(paramBundle);
    zzzx();
    zzacj();
    if (!this.aqw.isEnabled()) {
      zzbwb().zzbxd().log("Event not sent since app measurement is disabled");
    }
    do
    {
      return;
      if (!this.auR)
      {
        this.auR = true;
        zzbyr();
      }
      boolean bool = zzal.zzne(paramString2);
      if ((paramBoolean1) && (this.auP != null) && (!bool))
      {
        zzbwb().zzbxd().zze("Passing event to registered event handler (FE)", paramString2, paramBundle);
        this.auP.zzb(paramString1, paramString2, paramBundle, paramLong);
        return;
      }
    } while (!this.aqw.zzbxq());
    int j = zzbvx().zzmw(paramString2);
    if (j != 0)
    {
      paramString1 = zzbvx().zza(paramString2, zzbwd().zzbud(), true);
      if (paramString2 != null) {}
      for (int i = paramString2.length();; i = 0)
      {
        this.aqw.zzbvx().zza(j, "_ev", paramString1, i);
        return;
      }
    }
    paramBundle.putString("_o", paramString1);
    Object localObject = zzf.zzz("_o");
    localObject = zzbvx().zza(paramString2, paramBundle, (List)localObject, paramBoolean3);
    if (!paramBundle.containsKey("_sc"))
    {
      zzbwd().zzayi();
      paramBundle = zzbvu().zzbyt();
      if (paramBundle != null) {
        paramBundle.avr = true;
      }
      zzad.zza(paramBundle, (Bundle)localObject);
    }
    if (paramBoolean2) {}
    for (paramBundle = zzam((Bundle)localObject);; paramBundle = (Bundle)localObject)
    {
      zzbwb().zzbxd().zze("Logging event (FE)", paramString2, paramBundle);
      localObject = new EventParcel(paramString2, new EventParams(paramBundle), paramString1, paramLong);
      zzbvt().zzc((EventParcel)localObject, paramString3);
      paramString3 = this.auQ.iterator();
      while (paramString3.hasNext()) {
        ((AppMeasurement.zzc)paramString3.next()).zzc(paramString1, paramString2, new Bundle(paramBundle), paramLong);
      }
      break;
    }
  }
  
  @WorkerThread
  private void zzbyr()
  {
    try
    {
      zzg(Class.forName(zzbys()));
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzbwb().zzbxc().log("Tag Manager is not found and thus will not be used");
    }
  }
  
  private String zzbys()
  {
    return "com.google.android.gms.tagmanager.TagManagerService";
  }
  
  @WorkerThread
  private void zzci(boolean paramBoolean)
  {
    zzzx();
    zzaby();
    zzacj();
    zzbwb().zzbxd().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(paramBoolean));
    zzbwc().setMeasurementEnabled(paramBoolean);
    zzbvt().zzbyv();
  }
  
  public void setMeasurementEnabled(final boolean paramBoolean)
  {
    zzacj();
    zzaby();
    zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzac.zza(zzac.this, paramBoolean);
      }
    });
  }
  
  public void setMinimumSessionDuration(final long paramLong)
  {
    zzaby();
    zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzac.this.zzbwc().ati.set(paramLong);
        zzac.this.zzbwb().zzbxd().zzj("Minimum session duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  public void setSessionTimeoutDuration(final long paramLong)
  {
    zzaby();
    zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzac.this.zzbwc().atj.set(paramLong);
        zzac.this.zzbwb().zzbxd().zzj("Session timeout duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  @WorkerThread
  public void zza(AppMeasurement.zzb paramzzb)
  {
    zzzx();
    zzaby();
    zzacj();
    if ((paramzzb != null) && (paramzzb != this.auP)) {
      if (this.auP != null) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      com.google.android.gms.common.internal.zzaa.zza(bool, "EventInterceptor already set.");
      this.auP = paramzzb;
      return;
    }
  }
  
  public void zza(AppMeasurement.zzc paramzzc)
  {
    zzaby();
    zzacj();
    com.google.android.gms.common.internal.zzaa.zzy(paramzzc);
    if (!this.auQ.add(paramzzc)) {
      zzbwb().zzbxa().log("OnEventListener already registered");
    }
  }
  
  protected void zza(final String paramString1, final String paramString2, final long paramLong, Bundle paramBundle, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final String paramString3)
  {
    if (paramBundle != null) {}
    for (paramBundle = new Bundle(paramBundle);; paramBundle = new Bundle())
    {
      zzbwa().zzm(new Runnable()
      {
        public void run()
        {
          zzac.zza(zzac.this, paramString1, paramString2, paramLong, paramBoolean1, paramBoolean2, paramBoolean3, paramString3, this.alm);
        }
      });
      return;
    }
  }
  
  void zza(final String paramString1, final String paramString2, final long paramLong, final Object paramObject)
  {
    zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzac.zza(zzac.this, paramString1, paramString2, paramObject, paramLong);
      }
    });
  }
  
  public void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    zzaby();
    if ((this.auP == null) || (zzal.zzne(paramString2))) {}
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
        Object localObject = zzbvx().zzl(str, paramBundle.get(str));
        if (localObject == null) {
          zzbwb().zzbxa().zzj("Param value can't be null", str);
        } else {
          zzbvx().zza(localBundle, str, localObject);
        }
      }
    }
    return localBundle;
  }
  
  @TargetApi(14)
  public void zzbyp()
  {
    if ((getContext().getApplicationContext() instanceof Application))
    {
      Application localApplication = (Application)getContext().getApplicationContext();
      if (this.auO == null) {
        this.auO = new zza(null);
      }
      localApplication.unregisterActivityLifecycleCallbacks(this.auO);
      localApplication.registerActivityLifecycleCallbacks(this.auO);
      zzbwb().zzbxe().log("Registered activity lifecycle callback");
    }
  }
  
  @WorkerThread
  public void zzbyq()
  {
    zzzx();
    zzaby();
    zzacj();
    if (!this.aqw.zzbxq()) {}
    String str;
    do
    {
      return;
      zzbvt().zzbyq();
      str = zzbwc().zzbxn();
    } while ((TextUtils.isEmpty(str)) || (str.equals(zzbvs().zzbws())));
    Bundle localBundle = new Bundle();
    localBundle.putString("_po", str);
    zzf("auto", "_ou", localBundle);
  }
  
  public List<UserAttributeParcel> zzcj(final boolean paramBoolean)
  {
    zzaby();
    zzacj();
    zzbwb().zzbxd().log("Fetching user attributes (FE)");
    synchronized (new AtomicReference())
    {
      this.aqw.zzbwa().zzm(new Runnable()
      {
        public void run()
        {
          zzac.this.zzbvt().zza(localObject1, paramBoolean);
        }
      });
      try
      {
        ???.wait(5000L);
        List localList = (List)((AtomicReference)???).get();
        ??? = localList;
        if (localList == null)
        {
          zzbwb().zzbxa().log("Timed out waiting for get user properties");
          ??? = Collections.emptyList();
        }
        return (List<UserAttributeParcel>)???;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzbwb().zzbxa().zzj("Interrupted waiting for get user properties", localInterruptedException);
        }
      }
    }
  }
  
  public void zzd(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    zzaby();
    zza(paramString1, paramString2, paramLong, paramBundle, false, true, true, null);
  }
  
  public void zzd(String paramString1, String paramString2, Object paramObject)
  {
    int i = 0;
    int j = 0;
    com.google.android.gms.common.internal.zzaa.zzib(paramString1);
    long l = zzabz().currentTimeMillis();
    int k = zzbvx().zzmy(paramString2);
    if (k != 0)
    {
      paramString1 = zzbvx().zza(paramString2, zzbwd().zzbue(), true);
      i = j;
      if (paramString2 != null) {
        i = paramString2.length();
      }
      this.aqw.zzbvx().zza(k, "_ev", paramString1, i);
    }
    do
    {
      return;
      if (paramObject == null) {
        break;
      }
      j = zzbvx().zzm(paramString2, paramObject);
      if (j != 0)
      {
        paramString1 = zzbvx().zza(paramString2, zzbwd().zzbue(), true);
        if (((paramObject instanceof String)) || ((paramObject instanceof CharSequence))) {
          i = String.valueOf(paramObject).length();
        }
        this.aqw.zzbvx().zza(j, "_ev", paramString1, i);
        return;
      }
      paramObject = zzbvx().zzn(paramString2, paramObject);
    } while (paramObject == null);
    zza(paramString1, paramString2, l, paramObject);
    return;
    zza(paramString1, paramString2, l, null);
  }
  
  public void zzf(String paramString1, String paramString2, Bundle paramBundle)
  {
    zzaby();
    if ((this.auP == null) || (zzal.zzne(paramString2))) {}
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
      zzbwb().zzbxa().zzj("Failed to invoke Tag Manager's initialize() method", paramClass);
    }
  }
  
  protected void zzzy() {}
  
  @TargetApi(14)
  @MainThread
  private class zza
    implements Application.ActivityLifecycleCallbacks
  {
    private zza() {}
    
    private boolean zzms(String paramString)
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
      for (;;)
      {
        int i;
        try
        {
          zzac.this.zzbwb().zzbxe().log("onActivityCreated");
          Object localObject = paramActivity.getIntent();
          if (localObject == null) {
            continue;
          }
          localObject = ((Intent)localObject).getData();
          if ((localObject == null) || (!((Uri)localObject).isHierarchical())) {
            continue;
          }
          if (paramBundle == null)
          {
            Bundle localBundle = zzac.this.zzbvx().zzu((Uri)localObject);
            if (localBundle != null) {
              zzac.this.zzf("auto", "_cmp", localBundle);
            }
          }
          localObject = ((Uri)localObject).getQueryParameter("referrer");
          if (TextUtils.isEmpty((CharSequence)localObject)) {
            return;
          }
          if (!((String)localObject).contains("gclid")) {
            break label215;
          }
          if ((((String)localObject).contains("utm_campaign")) || (((String)localObject).contains("utm_source")) || (((String)localObject).contains("utm_medium")) || (((String)localObject).contains("utm_term"))) {
            break label247;
          }
          if (!((String)localObject).contains("utm_content")) {
            break label215;
          }
        }
        catch (Throwable localThrowable)
        {
          zzac.this.zzbwb().zzbwy().zzj("Throwable caught in onActivityCreated", localThrowable);
          zzac.this.zzbvu().onActivityCreated(paramActivity, paramBundle);
          return;
        }
        if (i == 0)
        {
          zzac.this.zzbwb().zzbxd().log("Activity created with data 'referrer' param without gclid and at least one utm field");
          return;
          label215:
          i = 0;
        }
        else
        {
          zzac.this.zzbwb().zzbxd().zzj("Activity created with referrer", localThrowable);
          zzms(localThrowable);
          continue;
          label247:
          i = 1;
        }
      }
    }
    
    public void onActivityDestroyed(Activity paramActivity)
    {
      zzac.this.zzbvu().onActivityDestroyed(paramActivity);
    }
    
    @MainThread
    public void onActivityPaused(Activity paramActivity)
    {
      zzac.this.zzbvu().onActivityPaused(paramActivity);
      zzac.this.zzbvz().zzbzd();
    }
    
    @MainThread
    public void onActivityResumed(Activity paramActivity)
    {
      zzac.this.zzbvu().onActivityResumed(paramActivity);
      zzac.this.zzbvz().zzbzb();
    }
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
    {
      zzac.this.zzbvu().onActivitySaveInstanceState(paramActivity, paramBundle);
    }
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */