package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzf;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import com.google.android.gms.measurement.AppMeasurement.zzc;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class zzauj
  extends zzauh
{
  protected zza zzbuY;
  private AppMeasurement.zzb zzbuZ;
  private final Set<AppMeasurement.zzc> zzbva = new CopyOnWriteArraySet();
  private boolean zzbvb;
  private String zzbvc = null;
  private String zzbvd = null;
  
  protected zzauj(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  private Bundle zzM(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return new Bundle();
    }
    paramBundle = new Bundle(paramBundle);
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject1 = (String)localIterator.next();
      Object localObject2 = paramBundle.get((String)localObject1);
      if ((localObject2 instanceof Bundle))
      {
        paramBundle.putBundle((String)localObject1, new Bundle((Bundle)localObject2));
      }
      else
      {
        int i;
        if ((localObject2 instanceof Parcelable[]))
        {
          localObject1 = (Parcelable[])localObject2;
          i = 0;
          while (i < localObject1.length)
          {
            if ((localObject1[i] instanceof Bundle)) {
              localObject1[i] = new Bundle((Bundle)localObject1[i]);
            }
            i += 1;
          }
        }
        else if ((localObject2 instanceof ArrayList))
        {
          localObject1 = (ArrayList)localObject2;
          i = 0;
          while (i < ((ArrayList)localObject1).size())
          {
            localObject2 = ((ArrayList)localObject1).get(i);
            if ((localObject2 instanceof Bundle)) {
              ((ArrayList)localObject1).set(i, new Bundle((Bundle)localObject2));
            }
            i += 1;
          }
        }
      }
    }
    return paramBundle;
  }
  
  @WorkerThread
  private void zzMU()
  {
    try
    {
      zzf(Class.forName(zzMV()));
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      zzKl().zzMd().log("Tag Manager is not found and thus will not be used");
    }
  }
  
  private String zzMV()
  {
    return "com.google.android.gms.tagmanager.TagManagerService";
  }
  
  private void zza(final AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    long l = zznR().currentTimeMillis();
    zzac.zzw(paramConditionalUserProperty);
    zzac.zzdr(paramConditionalUserProperty.mName);
    zzac.zzdr(paramConditionalUserProperty.mOrigin);
    zzac.zzw(paramConditionalUserProperty.mValue);
    paramConditionalUserProperty.mCreationTimestamp = l;
    String str = paramConditionalUserProperty.mName;
    Object localObject1 = paramConditionalUserProperty.mValue;
    if (zzKh().zzfX(str) != 0)
    {
      zzKl().zzLZ().zzj("Invalid conditional user property name", str);
      return;
    }
    if (zzKh().zzl(str, localObject1) != 0)
    {
      zzKl().zzLZ().zze("Invalid conditional user property value", str, localObject1);
      return;
    }
    Object localObject2 = zzKh().zzm(str, localObject1);
    if (localObject2 == null)
    {
      zzKl().zzLZ().zze("Unable to normalize conditional user property value", str, localObject1);
      return;
    }
    paramConditionalUserProperty.mValue = localObject2;
    l = paramConditionalUserProperty.mTriggerTimeout;
    if ((l > zzKn().zzLb()) || (l < 1L))
    {
      zzKl().zzLZ().zze("Invalid conditional user property timeout", str, Long.valueOf(l));
      return;
    }
    l = paramConditionalUserProperty.mTimeToLive;
    if ((l > zzKn().zzLc()) || (l < 1L))
    {
      zzKl().zzLZ().zze("Invalid conditional user property time to live", str, Long.valueOf(l));
      return;
    }
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.zza(zzauj.this, paramConditionalUserProperty);
      }
    });
  }
  
  private void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zza(paramString1, paramString2, zznR().currentTimeMillis(), paramBundle, paramBoolean1, paramBoolean2, paramBoolean3, paramString3);
  }
  
  @WorkerThread
  private void zza(String paramString1, String paramString2, Object paramObject, long paramLong)
  {
    zzac.zzdr(paramString1);
    zzac.zzdr(paramString2);
    zzmR();
    zzJW();
    zzob();
    if (!this.zzbqb.isEnabled()) {
      zzKl().zzMe().log("User property not set since app measurement is disabled");
    }
    while (!this.zzbqb.zzMu()) {
      return;
    }
    zzKl().zzMe().zze("Setting user property (FE)", paramString2, paramObject);
    paramString1 = new zzauq(paramString2, paramLong, paramObject, paramString1);
    zzKd().zzb(paramString1);
  }
  
  private void zza(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    long l = zznR().currentTimeMillis();
    zzac.zzdr(paramString2);
    final AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
    localConditionalUserProperty.mAppId = paramString1;
    localConditionalUserProperty.mName = paramString2;
    localConditionalUserProperty.mCreationTimestamp = l;
    if (paramString3 != null)
    {
      localConditionalUserProperty.mExpiredEventName = paramString3;
      localConditionalUserProperty.mExpiredEventParams = paramBundle;
    }
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.zzb(zzauj.this, localConditionalUserProperty);
      }
    });
  }
  
  @WorkerThread
  private void zzaL(boolean paramBoolean)
  {
    zzmR();
    zzJW();
    zzob();
    zzKl().zzMe().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(paramBoolean));
    zzKm().setMeasurementEnabled(paramBoolean);
    zzKd().zzMY();
  }
  
  private Map<String, Object> zzb(final String paramString1, final String paramString2, final String paramString3, final boolean paramBoolean)
  {
    synchronized (new AtomicReference())
    {
      this.zzbqb.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauj.this.zzbqb.zzKd().zza(localAtomicReference, paramString1, paramString2, paramString3, paramBoolean);
        }
      });
      try
      {
        ???.wait(5000L);
        paramString2 = (List)???.get();
        if (paramString2 == null)
        {
          zzKl().zzMb().log("Timed out waiting for get user properties");
          return Collections.emptyMap();
        }
      }
      catch (InterruptedException paramString1)
      {
        for (;;)
        {
          zzKl().zzMb().zzj("Interrupted waiting for get user properties", paramString1);
        }
      }
    }
    paramString1 = new ArrayMap(paramString2.size());
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext())
    {
      paramString3 = (zzauq)paramString2.next();
      paramString1.put(paramString3.name, paramString3.getValue());
    }
    return paramString1;
  }
  
  @WorkerThread
  private void zzb(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzmR();
    zzob();
    zzac.zzw(paramConditionalUserProperty);
    zzac.zzdr(paramConditionalUserProperty.mName);
    zzac.zzdr(paramConditionalUserProperty.mOrigin);
    zzac.zzw(paramConditionalUserProperty.mValue);
    if (!this.zzbqb.isEnabled())
    {
      zzKl().zzMe().log("Conditional property not sent since Firebase Analytics is disabled");
      return;
    }
    zzauq localzzauq = new zzauq(paramConditionalUserProperty.mName, paramConditionalUserProperty.mTriggeredTimestamp, paramConditionalUserProperty.mValue, paramConditionalUserProperty.mOrigin);
    try
    {
      zzatq localzzatq1 = zzKh().zza(paramConditionalUserProperty.mTriggeredEventName, paramConditionalUserProperty.mTriggeredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      zzatq localzzatq2 = zzKh().zza(paramConditionalUserProperty.mTimedOutEventName, paramConditionalUserProperty.mTimedOutEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      zzatq localzzatq3 = zzKh().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      paramConditionalUserProperty = new zzatg(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzauq, paramConditionalUserProperty.mCreationTimestamp, false, paramConditionalUserProperty.mTriggerEventName, localzzatq2, paramConditionalUserProperty.mTriggerTimeout, localzzatq1, paramConditionalUserProperty.mTimeToLive, localzzatq3);
      zzKd().zzf(paramConditionalUserProperty);
      return;
    }
    catch (IllegalArgumentException paramConditionalUserProperty) {}
  }
  
  @WorkerThread
  private void zzb(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zzac.zzdr(paramString1);
    zzac.zzdr(paramString2);
    zzac.zzw(paramBundle);
    zzmR();
    zzob();
    if (!this.zzbqb.isEnabled()) {
      zzKl().zzMe().log("Event not sent since app measurement is disabled");
    }
    for (;;)
    {
      return;
      if (!this.zzbvb)
      {
        this.zzbvb = true;
        zzMU();
      }
      boolean bool1 = "am".equals(paramString1);
      boolean bool2 = zzaut.zzgd(paramString2);
      if ((paramBoolean1) && (this.zzbuZ != null) && (!bool2) && (!bool1))
      {
        zzKl().zzMe().zze("Passing event to registered event handler (FE)", paramString2, paramBundle);
        this.zzbuZ.zzb(paramString1, paramString2, paramBundle, paramLong);
        return;
      }
      if (this.zzbqb.zzMu())
      {
        int j = zzKh().zzfV(paramString2);
        if (j != 0)
        {
          paramString1 = zzKh().zza(paramString2, zzKn().zzKM(), true);
          if (paramString2 != null) {}
          for (i = paramString2.length();; i = 0)
          {
            this.zzbqb.zzKh().zza(j, "_ev", paramString1, i);
            return;
          }
        }
        Object localObject1 = zzf.zzx("_o");
        Bundle localBundle1 = zzKh().zza(paramString2, paramBundle, (List)localObject1, paramBoolean3, true);
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(localBundle1);
        long l = zzKm().zzMh().nextLong();
        int i = 0;
        paramBundle = (String[])localBundle1.keySet().toArray(new String[paramBundle.size()]);
        Arrays.sort(paramBundle);
        int m = paramBundle.length;
        j = 0;
        if (j < m)
        {
          String str = paramBundle[j];
          Object localObject2 = localBundle1.get(str);
          localObject2 = zzKh().zzH(localObject2);
          if (localObject2 == null) {}
          for (;;)
          {
            j += 1;
            break;
            localBundle1.putInt(str, localObject2.length);
            int k = 0;
            while (k < localObject2.length)
            {
              Bundle localBundle2 = localObject2[k];
              localBundle2 = zzKh().zza("_ep", localBundle2, (List)localObject1, paramBoolean3, false);
              localBundle2.putString("_en", paramString2);
              localBundle2.putLong("_eid", l);
              localBundle2.putString("_gn", str);
              localBundle2.putInt("_ll", localObject2.length);
              localBundle2.putInt("_i", k);
              localArrayList.add(localBundle2);
              k += 1;
            }
            i = localObject2.length + i;
          }
        }
        if (i != 0)
        {
          localBundle1.putLong("_eid", l);
          localBundle1.putInt("_epc", i);
        }
        zzKn().zzLh();
        localObject1 = zzKe().zzMW();
        if ((localObject1 != null) && (!localBundle1.containsKey("_sc"))) {
          ((zzauk.zza)localObject1).zzbvC = true;
        }
        i = 0;
        while (i < localArrayList.size())
        {
          localBundle1 = (Bundle)localArrayList.get(i);
          if (i != 0)
          {
            j = 1;
            if (j == 0) {
              break label744;
            }
            paramBundle = "_ep";
            label590:
            localBundle1.putString("_o", paramString1);
            if (!localBundle1.containsKey("_sc")) {
              zzauk.zza((AppMeasurement.zzf)localObject1, localBundle1);
            }
            if (!paramBoolean2) {
              break label750;
            }
            localBundle1 = zzKh().zzN(localBundle1);
          }
          label744:
          label750:
          for (;;)
          {
            zzKl().zzMe().zze("Logging event (FE)", paramString2, localBundle1);
            paramBundle = new zzatq(paramBundle, new zzato(localBundle1), paramString1, paramLong);
            zzKd().zzc(paramBundle, paramString3);
            if (bool1) {
              break label753;
            }
            paramBundle = this.zzbva.iterator();
            while (paramBundle.hasNext()) {
              ((AppMeasurement.zzc)paramBundle.next()).zzc(paramString1, paramString2, new Bundle(localBundle1), paramLong);
            }
            j = 0;
            break;
            paramBundle = paramString2;
            break label590;
          }
          label753:
          i += 1;
        }
      }
    }
  }
  
  @WorkerThread
  private void zzc(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzmR();
    zzob();
    zzac.zzw(paramConditionalUserProperty);
    zzac.zzdr(paramConditionalUserProperty.mName);
    if (!this.zzbqb.isEnabled())
    {
      zzKl().zzMe().log("Conditional property not cleared since Firebase Analytics is disabled");
      return;
    }
    zzauq localzzauq = new zzauq(paramConditionalUserProperty.mName, 0L, null, null);
    try
    {
      zzatq localzzatq = zzKh().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, paramConditionalUserProperty.mCreationTimestamp, true, false);
      paramConditionalUserProperty = new zzatg(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzauq, paramConditionalUserProperty.mCreationTimestamp, paramConditionalUserProperty.mActive, paramConditionalUserProperty.mTriggerEventName, null, paramConditionalUserProperty.mTriggerTimeout, null, paramConditionalUserProperty.mTimeToLive, localzzatq);
      zzKd().zzf(paramConditionalUserProperty);
      return;
    }
    catch (IllegalArgumentException paramConditionalUserProperty) {}
  }
  
  private List<AppMeasurement.ConditionalUserProperty> zzo(final String paramString1, final String paramString2, final String paramString3)
  {
    synchronized (new AtomicReference())
    {
      this.zzbqb.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauj.this.zzbqb.zzKd().zza(localObject, paramString1, paramString2, paramString3);
        }
      });
      try
      {
        ???.wait(5000L);
        ??? = (List)((AtomicReference)???).get();
        if (??? == null)
        {
          zzKl().zzMb().zzj("Timed out waiting for get conditional user properties", paramString1);
          return Collections.emptyList();
        }
      }
      catch (InterruptedException paramString3)
      {
        for (;;)
        {
          zzKl().zzMb().zze("Interrupted waiting for get conditional user properties", paramString1, paramString3);
        }
      }
    }
    paramString3 = new ArrayList(((List)???).size());
    ??? = ((List)???).iterator();
    while (((Iterator)???).hasNext())
    {
      zzatg localzzatg = (zzatg)((Iterator)???).next();
      AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
      localConditionalUserProperty.mAppId = paramString1;
      localConditionalUserProperty.mOrigin = paramString2;
      localConditionalUserProperty.mCreationTimestamp = localzzatg.zzbqY;
      localConditionalUserProperty.mName = localzzatg.zzbqX.name;
      localConditionalUserProperty.mValue = localzzatg.zzbqX.getValue();
      localConditionalUserProperty.mActive = localzzatg.zzbqZ;
      localConditionalUserProperty.mTriggerEventName = localzzatg.zzbra;
      if (localzzatg.zzbrb != null)
      {
        localConditionalUserProperty.mTimedOutEventName = localzzatg.zzbrb.name;
        if (localzzatg.zzbrb.zzbrH != null) {
          localConditionalUserProperty.mTimedOutEventParams = localzzatg.zzbrb.zzbrH.zzLW();
        }
      }
      localConditionalUserProperty.mTriggerTimeout = localzzatg.zzbrc;
      if (localzzatg.zzbrd != null)
      {
        localConditionalUserProperty.mTriggeredEventName = localzzatg.zzbrd.name;
        if (localzzatg.zzbrd.zzbrH != null) {
          localConditionalUserProperty.mTriggeredEventParams = localzzatg.zzbrd.zzbrH.zzLW();
        }
      }
      localConditionalUserProperty.mTriggeredTimestamp = localzzatg.zzbqX.zzbwf;
      localConditionalUserProperty.mTimeToLive = localzzatg.zzbre;
      if (localzzatg.zzbrf != null)
      {
        localConditionalUserProperty.mExpiredEventName = localzzatg.zzbrf.name;
        if (localzzatg.zzbrf.zzbrH != null) {
          localConditionalUserProperty.mExpiredEventParams = localzzatg.zzbrf.zzbrH.zzLW();
        }
      }
      paramString3.add(localConditionalUserProperty);
    }
    return paramString3;
  }
  
  public void clearConditionalUserProperty(String paramString1, String paramString2, Bundle paramBundle)
  {
    zzJW();
    zza(null, paramString1, paramString2, paramBundle);
  }
  
  public void clearConditionalUserPropertyAs(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    zzac.zzdr(paramString1);
    zzJV();
    zza(paramString1, paramString2, paramString3, paramBundle);
  }
  
  public Task<String> getAppInstanceId()
  {
    try
    {
      Object localObject = zzKm().zzMm();
      if (localObject != null) {
        return Tasks.forResult(localObject);
      }
      localObject = Tasks.call(zzKk().zzMs(), new Callable()
      {
        public String zzbY()
          throws Exception
        {
          String str = zzauj.this.zzKm().zzMm();
          if (str != null) {
            return str;
          }
          str = zzauj.this.zzKa().zzar(120000L);
          if (str == null) {
            throw new TimeoutException();
          }
          zzauj.this.zzKm().zzfJ(str);
          return str;
        }
      });
      return (Task<String>)localObject;
    }
    catch (Exception localException)
    {
      zzKl().zzMb().log("Failed to schedule task for getAppInstanceId");
      return Tasks.forException(localException);
    }
  }
  
  public List<AppMeasurement.ConditionalUserProperty> getConditionalUserProperties(String paramString1, String paramString2)
  {
    zzJW();
    return zzo(null, paramString1, paramString2);
  }
  
  public List<AppMeasurement.ConditionalUserProperty> getConditionalUserPropertiesAs(String paramString1, String paramString2, String paramString3)
  {
    zzac.zzdr(paramString1);
    zzJV();
    return zzo(paramString1, paramString2, paramString3);
  }
  
  public int getMaxUserProperties(String paramString)
  {
    zzac.zzdr(paramString);
    return zzKn().zzKZ();
  }
  
  public Map<String, Object> getUserProperties(String paramString1, String paramString2, boolean paramBoolean)
  {
    zzJW();
    return zzb(null, paramString1, paramString2, paramBoolean);
  }
  
  public Map<String, Object> getUserPropertiesAs(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    zzac.zzdr(paramString1);
    zzJV();
    return zzb(paramString1, paramString2, paramString3, paramBoolean);
  }
  
  public void setConditionalUserProperty(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzac.zzw(paramConditionalUserProperty);
    zzJW();
    paramConditionalUserProperty = new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty);
    if (!TextUtils.isEmpty(paramConditionalUserProperty.mAppId)) {
      zzKl().zzMb().log("Package name should be null when calling setConditionalUserProperty");
    }
    paramConditionalUserProperty.mAppId = null;
    zza(paramConditionalUserProperty);
  }
  
  public void setConditionalUserPropertyAs(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzac.zzw(paramConditionalUserProperty);
    zzac.zzdr(paramConditionalUserProperty.mAppId);
    zzJV();
    zza(new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty));
  }
  
  public void setMeasurementEnabled(final boolean paramBoolean)
  {
    zzob();
    zzJW();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.zza(zzauj.this, paramBoolean);
      }
    });
  }
  
  public void setMinimumSessionDuration(final long paramLong)
  {
    zzJW();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.this.zzKm().zzbtn.set(paramLong);
        zzauj.this.zzKl().zzMe().zzj("Minimum session duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  public void setSessionTimeoutDuration(final long paramLong)
  {
    zzJW();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.this.zzKm().zzbto.set(paramLong);
        zzauj.this.zzKl().zzMe().zzj("Session timeout duration set", Long.valueOf(paramLong));
      }
    });
  }
  
  @TargetApi(14)
  public void zzMS()
  {
    if ((getContext().getApplicationContext() instanceof Application))
    {
      Application localApplication = (Application)getContext().getApplicationContext();
      if (this.zzbuY == null) {
        this.zzbuY = new zza(null);
      }
      localApplication.unregisterActivityLifecycleCallbacks(this.zzbuY);
      localApplication.registerActivityLifecycleCallbacks(this.zzbuY);
      zzKl().zzMf().log("Registered activity lifecycle callback");
    }
  }
  
  @WorkerThread
  public void zzMT()
  {
    zzmR();
    zzJW();
    zzob();
    if (!this.zzbqb.zzMu()) {}
    String str;
    do
    {
      return;
      zzKd().zzMT();
      str = zzKm().zzMp();
    } while ((TextUtils.isEmpty(str)) || (str.equals(zzKc().zzLS())));
    Bundle localBundle = new Bundle();
    localBundle.putString("_po", str);
    zze("auto", "_ou", localBundle);
  }
  
  @WorkerThread
  public void zza(AppMeasurement.zzb paramzzb)
  {
    zzmR();
    zzJW();
    zzob();
    if ((paramzzb != null) && (paramzzb != this.zzbuZ)) {
      if (this.zzbuZ != null) {
        break label46;
      }
    }
    label46:
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "EventInterceptor already set.");
      this.zzbuZ = paramzzb;
      return;
    }
  }
  
  public void zza(AppMeasurement.zzc paramzzc)
  {
    zzJW();
    zzob();
    zzac.zzw(paramzzc);
    if (!this.zzbva.add(paramzzc)) {
      zzKl().zzMb().log("OnEventListener already registered");
    }
  }
  
  protected void zza(final String paramString1, final String paramString2, final long paramLong, Bundle paramBundle, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final String paramString3)
  {
    paramBundle = zzM(paramBundle);
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.zza(zzauj.this, paramString1, paramString2, paramLong, paramBoolean1, paramBoolean2, paramBoolean3, paramString3, this.zzbkV);
      }
    });
  }
  
  void zza(final String paramString1, final String paramString2, final long paramLong, final Object paramObject)
  {
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzauj.zza(zzauj.this, paramString1, paramString2, paramObject, paramLong);
      }
    });
  }
  
  public void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    zzJW();
    if ((this.zzbuZ == null) || (zzaut.zzgd(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, paramBoolean, null);
      return;
    }
  }
  
  public List<zzauq> zzaM(final boolean paramBoolean)
  {
    zzJW();
    zzob();
    zzKl().zzMe().log("Fetching user attributes (FE)");
    synchronized (new AtomicReference())
    {
      this.zzbqb.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauj.this.zzKd().zza(localObject1, paramBoolean);
        }
      });
      try
      {
        ???.wait(5000L);
        List localList = (List)((AtomicReference)???).get();
        ??? = localList;
        if (localList == null)
        {
          zzKl().zzMb().log("Timed out waiting for get user properties");
          ??? = Collections.emptyList();
        }
        return (List<zzauq>)???;
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzKl().zzMb().zzj("Interrupted waiting for get user properties", localInterruptedException);
        }
      }
    }
  }
  
  @Nullable
  String zzar(long paramLong)
  {
    Object localObject = null;
    if (zzKk().zzMr()) {
      zzKl().zzLZ().log("Cannot retrieve app instance id from analytics worker thread");
    }
    long l;
    do
    {
      String str;
      do
      {
        return (String)localObject;
        if (zzKk().zzbc())
        {
          zzKl().zzLZ().log("Cannot retrieve app instance id from main thread");
          return null;
        }
        l = zznR().elapsedRealtime();
        str = zzas(paramLong);
        l = zznR().elapsedRealtime() - l;
        localObject = str;
      } while (str != null);
      localObject = str;
    } while (l >= paramLong);
    return zzas(paramLong - l);
  }
  
  @Nullable
  String zzas(long paramLong)
  {
    synchronized (new AtomicReference())
    {
      zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzauj.this.zzKd().zza(localAtomicReference);
        }
      });
      try
      {
        ???.wait(paramLong);
        return (String)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        zzKl().zzMb().log("Interrupted waiting for app instance id");
        return null;
      }
    }
  }
  
  public void zzd(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    zzJW();
    zza(paramString1, paramString2, paramLong, paramBundle, false, true, true, null);
  }
  
  public void zzd(String paramString1, String paramString2, Object paramObject)
  {
    int i = 0;
    int j = 0;
    zzac.zzdr(paramString1);
    long l = zznR().currentTimeMillis();
    int k = zzKh().zzfX(paramString2);
    if (k != 0)
    {
      paramString1 = zzKh().zza(paramString2, zzKn().zzKN(), true);
      i = j;
      if (paramString2 != null) {
        i = paramString2.length();
      }
      this.zzbqb.zzKh().zza(k, "_ev", paramString1, i);
    }
    do
    {
      return;
      if (paramObject == null) {
        break;
      }
      j = zzKh().zzl(paramString2, paramObject);
      if (j != 0)
      {
        paramString1 = zzKh().zza(paramString2, zzKn().zzKN(), true);
        if (((paramObject instanceof String)) || ((paramObject instanceof CharSequence))) {
          i = String.valueOf(paramObject).length();
        }
        this.zzbqb.zzKh().zza(j, "_ev", paramString1, i);
        return;
      }
      paramObject = zzKh().zzm(paramString2, paramObject);
    } while (paramObject == null);
    zza(paramString1, paramString2, l, paramObject);
    return;
    zza(paramString1, paramString2, l, null);
  }
  
  public void zze(String paramString1, String paramString2, Bundle paramBundle)
  {
    zzJW();
    if ((this.zzbuZ == null) || (zzaut.zzgd(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, false, null);
      return;
    }
  }
  
  @WorkerThread
  public void zzf(Class<?> paramClass)
  {
    try
    {
      paramClass.getDeclaredMethod("initialize", new Class[] { Context.class }).invoke(null, new Object[] { getContext() });
      return;
    }
    catch (Exception paramClass)
    {
      zzKl().zzMb().zzj("Failed to invoke Tag Manager's initialize() method", paramClass);
    }
  }
  
  /* Error */
  @Nullable
  @WorkerThread
  public String zzfQ(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 291	com/google/android/gms/internal/zzauj:zzob	()V
    //   6: aload_0
    //   7: invokevirtual 288	com/google/android/gms/internal/zzauj:zzJW	()V
    //   10: aload_1
    //   11: ifnull +23 -> 34
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 58	com/google/android/gms/internal/zzauj:zzbvd	Ljava/lang/String;
    //   19: invokevirtual 460	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   22: ifeq +12 -> 34
    //   25: aload_0
    //   26: getfield 56	com/google/android/gms/internal/zzauj:zzbvc	Ljava/lang/String;
    //   29: astore_1
    //   30: aload_0
    //   31: monitorexit
    //   32: aload_1
    //   33: areturn
    //   34: aload_0
    //   35: ldc2_w 925
    //   38: invokevirtual 928	com/google/android/gms/internal/zzauj:zzar	(J)Ljava/lang/String;
    //   41: astore_2
    //   42: aload_2
    //   43: ifnonnull +8 -> 51
    //   46: aconst_null
    //   47: astore_1
    //   48: goto -18 -> 30
    //   51: aload_0
    //   52: aload_1
    //   53: putfield 58	com/google/android/gms/internal/zzauj:zzbvd	Ljava/lang/String;
    //   56: aload_0
    //   57: aload_2
    //   58: putfield 56	com/google/android/gms/internal/zzauj:zzbvc	Ljava/lang/String;
    //   61: aload_0
    //   62: getfield 56	com/google/android/gms/internal/zzauj:zzbvc	Ljava/lang/String;
    //   65: astore_1
    //   66: goto -36 -> 30
    //   69: astore_1
    //   70: aload_0
    //   71: monitorexit
    //   72: aload_1
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	this	zzauj
    //   0	74	1	paramString	String
    //   41	17	2	str	String
    // Exception table:
    //   from	to	target	type
    //   2	10	69	finally
    //   14	30	69	finally
    //   34	42	69	finally
    //   51	66	69	finally
  }
  
  protected void zzmS() {}
  
  @TargetApi(14)
  @MainThread
  private class zza
    implements Application.ActivityLifecycleCallbacks
  {
    private zza() {}
    
    private boolean zzfR(String paramString)
    {
      if (!TextUtils.isEmpty(paramString))
      {
        zzauj.this.zzd("auto", "_ldl", paramString);
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
          zzauj.this.zzKl().zzMf().log("onActivityCreated");
          Object localObject = paramActivity.getIntent();
          if (localObject == null) {
            continue;
          }
          Uri localUri = ((Intent)localObject).getData();
          if ((localUri == null) || (!localUri.isHierarchical())) {
            continue;
          }
          if (paramBundle == null)
          {
            Bundle localBundle = zzauj.this.zzKh().zzu(localUri);
            if (!zzauj.this.zzKh().zzA((Intent)localObject)) {
              break label266;
            }
            localObject = "gs";
            if (localBundle != null) {
              zzauj.this.zze((String)localObject, "_cmp", localBundle);
            }
          }
          localObject = localUri.getQueryParameter("referrer");
          if (TextUtils.isEmpty((CharSequence)localObject)) {
            return;
          }
          if (!((String)localObject).contains("gclid")) {
            break label234;
          }
          if ((((String)localObject).contains("utm_campaign")) || (((String)localObject).contains("utm_source")) || (((String)localObject).contains("utm_medium")) || (((String)localObject).contains("utm_term"))) {
            break label273;
          }
          if (!((String)localObject).contains("utm_content")) {
            break label234;
          }
        }
        catch (Throwable localThrowable)
        {
          zzauj.this.zzKl().zzLZ().zzj("Throwable caught in onActivityCreated", localThrowable);
          zzauj.this.zzKe().onActivityCreated(paramActivity, paramBundle);
          return;
        }
        if (i == 0)
        {
          zzauj.this.zzKl().zzMe().log("Activity created with data 'referrer' param without gclid and at least one utm field");
          return;
          label234:
          i = 0;
        }
        else
        {
          zzauj.this.zzKl().zzMe().zzj("Activity created with referrer", localThrowable);
          zzfR(localThrowable);
          continue;
          label266:
          String str = "auto";
          continue;
          label273:
          i = 1;
        }
      }
    }
    
    public void onActivityDestroyed(Activity paramActivity)
    {
      zzauj.this.zzKe().onActivityDestroyed(paramActivity);
    }
    
    @MainThread
    public void onActivityPaused(Activity paramActivity)
    {
      zzauj.this.zzKe().onActivityPaused(paramActivity);
      zzauj.this.zzKj().zzNg();
    }
    
    @MainThread
    public void onActivityResumed(Activity paramActivity)
    {
      zzauj.this.zzKe().onActivityResumed(paramActivity);
      zzauj.this.zzKj().zzNe();
    }
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle)
    {
      zzauj.this.zzKe().onActivitySaveInstanceState(paramActivity, paramBundle);
    }
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */