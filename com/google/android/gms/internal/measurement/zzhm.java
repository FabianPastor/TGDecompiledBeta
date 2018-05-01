package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.EventInterceptor;
import com.google.android.gms.measurement.AppMeasurement.OnEventListener;
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
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class zzhm
  extends zzhk
{
  protected zzif zzaoi;
  private AppMeasurement.EventInterceptor zzaoj;
  private final Set<AppMeasurement.OnEventListener> zzaok = new CopyOnWriteArraySet();
  private boolean zzaol;
  private final AtomicReference<String> zzaom = new AtomicReference();
  protected boolean zzaon = true;
  
  protected zzhm(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final void zza(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    long l = zzbt().currentTimeMillis();
    Preconditions.checkNotNull(paramConditionalUserProperty);
    Preconditions.checkNotEmpty(paramConditionalUserProperty.mName);
    Preconditions.checkNotEmpty(paramConditionalUserProperty.mOrigin);
    Preconditions.checkNotNull(paramConditionalUserProperty.mValue);
    paramConditionalUserProperty.mCreationTimestamp = l;
    String str = paramConditionalUserProperty.mName;
    Object localObject1 = paramConditionalUserProperty.mValue;
    if (zzgc().zzby(str) != 0) {
      zzgg().zzil().zzg("Invalid conditional user property name", zzgb().zzbg(str));
    }
    for (;;)
    {
      return;
      if (zzgc().zzi(str, localObject1) != 0)
      {
        zzgg().zzil().zze("Invalid conditional user property value", zzgb().zzbg(str), localObject1);
      }
      else
      {
        Object localObject2 = zzgc().zzj(str, localObject1);
        if (localObject2 == null)
        {
          zzgg().zzil().zze("Unable to normalize conditional user property value", zzgb().zzbg(str), localObject1);
        }
        else
        {
          paramConditionalUserProperty.mValue = localObject2;
          l = paramConditionalUserProperty.mTriggerTimeout;
          if ((!TextUtils.isEmpty(paramConditionalUserProperty.mTriggerEventName)) && ((l > 15552000000L) || (l < 1L)))
          {
            zzgg().zzil().zze("Invalid conditional user property timeout", zzgb().zzbg(str), Long.valueOf(l));
          }
          else
          {
            l = paramConditionalUserProperty.mTimeToLive;
            if ((l > 15552000000L) || (l < 1L)) {
              zzgg().zzil().zze("Invalid conditional user property time to live", zzgb().zzbg(str), Long.valueOf(l));
            } else {
              zzgf().zzc(new zzht(this, paramConditionalUserProperty));
            }
          }
        }
      }
    }
  }
  
  private final void zza(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    if (paramBundle == null)
    {
      paramBundle = new Bundle();
      zzgf().zzc(new zzie(this, paramString1, paramString2, paramLong, paramBundle, paramBoolean1, paramBoolean2, paramBoolean3, paramString3));
      return;
    }
    Bundle localBundle = new Bundle(paramBundle);
    Iterator localIterator = localBundle.keySet().iterator();
    for (;;)
    {
      paramBundle = localBundle;
      if (!localIterator.hasNext()) {
        break;
      }
      paramBundle = (String)localIterator.next();
      Object localObject = localBundle.get(paramBundle);
      if ((localObject instanceof Bundle))
      {
        localBundle.putBundle(paramBundle, new Bundle((Bundle)localObject));
      }
      else
      {
        int i;
        if ((localObject instanceof Parcelable[]))
        {
          paramBundle = (Parcelable[])localObject;
          for (i = 0; i < paramBundle.length; i++) {
            if ((paramBundle[i] instanceof Bundle)) {
              paramBundle[i] = new Bundle((Bundle)paramBundle[i]);
            }
          }
        }
        else if ((localObject instanceof ArrayList))
        {
          localObject = (ArrayList)localObject;
          for (i = 0; i < ((ArrayList)localObject).size(); i++)
          {
            paramBundle = ((ArrayList)localObject).get(i);
            if ((paramBundle instanceof Bundle)) {
              ((ArrayList)localObject).set(i, new Bundle((Bundle)paramBundle));
            }
          }
        }
      }
    }
  }
  
  private final void zza(String paramString1, String paramString2, long paramLong, Object paramObject)
  {
    zzgf().zzc(new zzho(this, paramString1, paramString2, paramObject, paramLong));
  }
  
  private final void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zza(paramString1, paramString2, zzbt().currentTimeMillis(), paramBundle, true, paramBoolean2, paramBoolean3, null);
  }
  
  private final void zza(String paramString1, String paramString2, Object paramObject, long paramLong)
  {
    Preconditions.checkNotEmpty(paramString1);
    Preconditions.checkNotEmpty(paramString2);
    zzab();
    zzch();
    if (!this.zzacr.isEnabled()) {
      zzgg().zziq().log("User property not set since app measurement is disabled");
    }
    for (;;)
    {
      return;
      if (this.zzacr.zzjk())
      {
        zzgg().zziq().zze("Setting user property (FE)", zzgb().zzbe(paramString2), paramObject);
        paramString1 = new zzjs(paramString2, paramLong, paramObject, paramString1);
        zzfx().zzb(paramString1);
      }
    }
  }
  
  private final void zza(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    long l = zzbt().currentTimeMillis();
    Preconditions.checkNotEmpty(paramString2);
    AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
    localConditionalUserProperty.mAppId = paramString1;
    localConditionalUserProperty.mName = paramString2;
    localConditionalUserProperty.mCreationTimestamp = l;
    if (paramString3 != null)
    {
      localConditionalUserProperty.mExpiredEventName = paramString3;
      localConditionalUserProperty.mExpiredEventParams = paramBundle;
    }
    zzgf().zzc(new zzhu(this, localConditionalUserProperty));
  }
  
  private final Map<String, Object> zzb(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (zzgf().zzjg())
    {
      zzgg().zzil().log("Cannot get user properties from analytics worker thread");
      paramString1 = Collections.emptyMap();
    }
    for (;;)
    {
      return paramString1;
      zzgf();
      if (zzgg.isMainThread())
      {
        zzgg().zzil().log("Cannot get user properties from main thread");
        paramString1 = Collections.emptyMap();
      }
      else
      {
        synchronized (new AtomicReference())
        {
          zzgg localzzgg = this.zzacr.zzgf();
          zzhw localzzhw = new com/google/android/gms/internal/measurement/zzhw;
          localzzhw.<init>(this, ???, paramString1, paramString2, paramString3, paramBoolean);
          localzzgg.zzc(localzzhw);
          try
          {
            ???.wait(5000L);
            paramString2 = (List)???.get();
            if (paramString2 == null)
            {
              zzgg().zzin().log("Timed out waiting for get user properties");
              paramString1 = Collections.emptyMap();
            }
          }
          catch (InterruptedException paramString1)
          {
            for (;;)
            {
              zzgg().zzin().zzg("Interrupted waiting for get user properties", paramString1);
            }
          }
        }
        paramString1 = new ArrayMap(paramString2.size());
        paramString3 = paramString2.iterator();
        while (paramString3.hasNext())
        {
          paramString2 = (zzjs)paramString3.next();
          paramString1.put(paramString2.name, paramString2.getValue());
        }
      }
    }
  }
  
  private final void zzb(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzab();
    zzch();
    Preconditions.checkNotNull(paramConditionalUserProperty);
    Preconditions.checkNotEmpty(paramConditionalUserProperty.mName);
    Preconditions.checkNotEmpty(paramConditionalUserProperty.mOrigin);
    Preconditions.checkNotNull(paramConditionalUserProperty.mValue);
    if (!this.zzacr.isEnabled()) {
      zzgg().zziq().log("Conditional property not sent since Firebase Analytics is disabled");
    }
    for (;;)
    {
      return;
      zzjs localzzjs = new zzjs(paramConditionalUserProperty.mName, paramConditionalUserProperty.mTriggeredTimestamp, paramConditionalUserProperty.mValue, paramConditionalUserProperty.mOrigin);
      try
      {
        zzeu localzzeu1 = zzgc().zza(paramConditionalUserProperty.mTriggeredEventName, paramConditionalUserProperty.mTriggeredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
        zzeu localzzeu2 = zzgc().zza(paramConditionalUserProperty.mTimedOutEventName, paramConditionalUserProperty.mTimedOutEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
        zzeu localzzeu3 = zzgc().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
        paramConditionalUserProperty = new zzef(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzjs, paramConditionalUserProperty.mCreationTimestamp, false, paramConditionalUserProperty.mTriggerEventName, localzzeu2, paramConditionalUserProperty.mTriggerTimeout, localzzeu1, paramConditionalUserProperty.mTimeToLive, localzzeu3);
        zzfx().zzf(paramConditionalUserProperty);
      }
      catch (IllegalArgumentException paramConditionalUserProperty) {}
    }
  }
  
  private final void zzb(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    Preconditions.checkNotEmpty(paramString1);
    Preconditions.checkNotEmpty(paramString2);
    Preconditions.checkNotNull(paramBundle);
    zzab();
    zzch();
    if (!this.zzacr.isEnabled()) {
      zzgg().zziq().log("Event not sent since app measurement is disabled");
    }
    int i;
    label279:
    label285:
    Object localObject3;
    boolean bool2;
    label428:
    do
    {
      for (;;)
      {
        return;
        if (!this.zzaol) {
          this.zzaol = true;
        }
        try
        {
          Object localObject1 = Class.forName("com.google.android.gms.tagmanager.TagManagerService");
          try
          {
            ((Class)localObject1).getDeclaredMethod("initialize", new Class[] { Context.class }).invoke(null, new Object[] { getContext() });
            if ((paramBoolean3) && (!"_iap".equals(paramString2)))
            {
              localObject1 = this.zzacr.zzgc();
              if (!((zzjv)localObject1).zzq("event", paramString2))
              {
                i = 2;
                if (i == 0) {
                  break label285;
                }
                this.zzacr.zzgc();
                paramString1 = zzjv.zza(paramString2, 40, true);
                if (paramString2 == null) {
                  break label279;
                }
                j = paramString2.length();
                this.zzacr.zzgc().zza(i, "_ev", paramString1, j);
              }
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              zzgg().zzin().zzg("Failed to invoke Tag Manager's initialize() method", localException);
            }
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          for (;;)
          {
            zzgg().zzip().log("Tag Manager is not found and thus will not be used");
            continue;
            if (!localClassNotFoundException.zza("event", AppMeasurement.Event.zzacs, paramString2))
            {
              i = 13;
            }
            else if (!localClassNotFoundException.zza("event", 40, paramString2))
            {
              i = 2;
            }
            else
            {
              i = 0;
              continue;
              j = 0;
            }
          }
          localObject3 = zzfy().zzkk();
          if ((localObject3 != null) && (!paramBundle.containsKey("_sc"))) {
            ((zzik)localObject3).zzapq = true;
          }
          if ((paramBoolean1) && (paramBoolean3)) {}
          for (boolean bool1 = true;; bool1 = false)
          {
            zzih.zza((zzig)localObject3, paramBundle, bool1);
            bool2 = "am".equals(paramString1);
            bool1 = zzjv.zzcb(paramString2);
            if ((!paramBoolean1) || (this.zzaoj == null) || (bool1) || (bool2)) {
              break label428;
            }
            zzgg().zziq().zze("Passing event to registered event handler (FE)", zzgb().zzbe(paramString2), zzgb().zzb(paramBundle));
            this.zzaoj.interceptEvent(paramString1, paramString2, paramBundle, paramLong);
            break;
          }
        }
      }
    } while (!this.zzacr.zzjk());
    int j = zzgc().zzbw(paramString2);
    if (j != 0)
    {
      zzgc();
      paramString1 = zzjv.zza(paramString2, 40, true);
      if (paramString2 != null) {}
      for (i = paramString2.length();; i = 0)
      {
        this.zzacr.zzgc().zza(paramString3, j, "_ev", paramString1, i);
        break;
      }
    }
    List localList = CollectionUtils.listOf(new String[] { "_o", "_sn", "_sc", "_si" });
    Bundle localBundle1 = zzgc().zza(paramString2, paramBundle, localList, paramBoolean3, true);
    Object localObject2;
    if ((localBundle1 == null) || (!localBundle1.containsKey("_sc")) || (!localBundle1.containsKey("_si")))
    {
      localObject2 = null;
      if (localObject2 != null) {
        break label1153;
      }
      localObject2 = localObject3;
    }
    label664:
    label953:
    label1103:
    label1109:
    label1112:
    label1150:
    label1153:
    for (;;)
    {
      localObject3 = new ArrayList();
      ((List)localObject3).add(localBundle1);
      long l = zzgc().zzku().nextLong();
      i = 0;
      paramBundle = (String[])localBundle1.keySet().toArray(new String[paramBundle.size()]);
      Arrays.sort(paramBundle);
      int k = paramBundle.length;
      j = 0;
      if (j < k)
      {
        String str = paramBundle[j];
        Object localObject4 = localBundle1.get(str);
        zzgc();
        localObject4 = zzjv.zze(localObject4);
        if (localObject4 == null) {
          break label1150;
        }
        localBundle1.putInt(str, localObject4.length);
        int m = 0;
        for (;;)
        {
          if (m < localObject4.length)
          {
            Bundle localBundle2 = localObject4[m];
            zzih.zza((zzig)localObject2, localBundle2, true);
            localBundle2 = zzgc().zza("_ep", localBundle2, localList, paramBoolean3, false);
            localBundle2.putString("_en", paramString2);
            localBundle2.putLong("_eid", l);
            localBundle2.putString("_gn", str);
            localBundle2.putInt("_ll", localObject4.length);
            localBundle2.putInt("_i", m);
            ((List)localObject3).add(localBundle2);
            m++;
            continue;
            localObject2 = new zzik(localBundle1.getString("_sn"), localBundle1.getString("_sc"), Long.valueOf(localBundle1.getLong("_si")).longValue());
            break;
          }
        }
        i = localObject4.length + i;
      }
      for (;;)
      {
        j++;
        break label664;
        if (i != 0)
        {
          localBundle1.putLong("_eid", l);
          localBundle1.putInt("_epc", i);
        }
        for (i = 0; i < ((List)localObject3).size(); i++)
        {
          localObject2 = (Bundle)((List)localObject3).get(i);
          if (i != 0)
          {
            j = 1;
            if (j == 0) {
              break label1103;
            }
            paramBundle = "_ep";
            ((Bundle)localObject2).putString("_o", paramString1);
            if (!paramBoolean2) {
              break label1109;
            }
            localObject2 = zzgc().zzd((Bundle)localObject2);
          }
          for (;;)
          {
            zzgg().zziq().zze("Logging event (FE)", zzgb().zzbe(paramString2), zzgb().zzb((Bundle)localObject2));
            paramBundle = new zzeu(paramBundle, new zzer((Bundle)localObject2), paramString1, paramLong);
            zzfx().zzc(paramBundle, paramString3);
            if (bool2) {
              break label1112;
            }
            paramBundle = this.zzaok.iterator();
            while (paramBundle.hasNext()) {
              ((AppMeasurement.OnEventListener)paramBundle.next()).onEvent(paramString1, paramString2, new Bundle((Bundle)localObject2), paramLong);
            }
            j = 0;
            break;
            paramBundle = paramString2;
            break label953;
          }
        }
        if ((zzfy().zzkk() == null) || (!"_ae".equals(paramString2))) {
          break;
        }
        zzge().zzm(true);
        break;
      }
    }
  }
  
  private final void zzc(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzab();
    zzch();
    Preconditions.checkNotNull(paramConditionalUserProperty);
    Preconditions.checkNotEmpty(paramConditionalUserProperty.mName);
    if (!this.zzacr.isEnabled()) {
      zzgg().zziq().log("Conditional property not cleared since Firebase Analytics is disabled");
    }
    for (;;)
    {
      return;
      zzjs localzzjs = new zzjs(paramConditionalUserProperty.mName, 0L, null, null);
      try
      {
        zzeu localzzeu = zzgc().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, paramConditionalUserProperty.mCreationTimestamp, true, false);
        paramConditionalUserProperty = new zzef(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzjs, paramConditionalUserProperty.mCreationTimestamp, paramConditionalUserProperty.mActive, paramConditionalUserProperty.mTriggerEventName, null, paramConditionalUserProperty.mTriggerTimeout, null, paramConditionalUserProperty.mTimeToLive, localzzeu);
        zzfx().zzf(paramConditionalUserProperty);
      }
      catch (IllegalArgumentException paramConditionalUserProperty) {}
    }
  }
  
  private final List<AppMeasurement.ConditionalUserProperty> zzf(String paramString1, String paramString2, String paramString3)
  {
    if (zzgf().zzjg())
    {
      zzgg().zzil().log("Cannot get conditional user properties from analytics worker thread");
      paramString1 = Collections.emptyList();
    }
    for (;;)
    {
      return paramString1;
      zzgf();
      if (zzgg.isMainThread())
      {
        zzgg().zzil().log("Cannot get conditional user properties from main thread");
        paramString1 = Collections.emptyList();
      }
      else
      {
        Object localObject3;
        synchronized (new AtomicReference())
        {
          localObject2 = this.zzacr.zzgf();
          localObject3 = new com/google/android/gms/internal/measurement/zzhv;
          ((zzhv)localObject3).<init>(this, (AtomicReference)???, paramString1, paramString2, paramString3);
          ((zzgg)localObject2).zzc((Runnable)localObject3);
          try
          {
            ???.wait(5000L);
            ??? = (List)((AtomicReference)???).get();
            if (??? == null)
            {
              zzgg().zzin().zzg("Timed out waiting for get conditional user properties", paramString1);
              paramString1 = Collections.emptyList();
            }
          }
          catch (InterruptedException paramString3)
          {
            for (;;)
            {
              zzgg().zzin().zze("Interrupted waiting for get conditional user properties", paramString1, paramString3);
            }
          }
        }
        paramString3 = new ArrayList(((List)???).size());
        Object localObject2 = ((List)???).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          ??? = (zzef)((Iterator)localObject2).next();
          localObject3 = new AppMeasurement.ConditionalUserProperty();
          ((AppMeasurement.ConditionalUserProperty)localObject3).mAppId = paramString1;
          ((AppMeasurement.ConditionalUserProperty)localObject3).mOrigin = paramString2;
          ((AppMeasurement.ConditionalUserProperty)localObject3).mCreationTimestamp = ((zzef)???).zzaem;
          ((AppMeasurement.ConditionalUserProperty)localObject3).mName = ((zzef)???).zzael.name;
          ((AppMeasurement.ConditionalUserProperty)localObject3).mValue = ((zzef)???).zzael.getValue();
          ((AppMeasurement.ConditionalUserProperty)localObject3).mActive = ((zzef)???).zzaen;
          ((AppMeasurement.ConditionalUserProperty)localObject3).mTriggerEventName = ((zzef)???).zzaeo;
          if (((zzef)???).zzaep != null)
          {
            ((AppMeasurement.ConditionalUserProperty)localObject3).mTimedOutEventName = ((zzef)???).zzaep.name;
            if (((zzef)???).zzaep.zzafo != null) {
              ((AppMeasurement.ConditionalUserProperty)localObject3).mTimedOutEventParams = ((zzef)???).zzaep.zzafo.zzif();
            }
          }
          ((AppMeasurement.ConditionalUserProperty)localObject3).mTriggerTimeout = ((zzef)???).zzaeq;
          if (((zzef)???).zzaer != null)
          {
            ((AppMeasurement.ConditionalUserProperty)localObject3).mTriggeredEventName = ((zzef)???).zzaer.name;
            if (((zzef)???).zzaer.zzafo != null) {
              ((AppMeasurement.ConditionalUserProperty)localObject3).mTriggeredEventParams = ((zzef)???).zzaer.zzafo.zzif();
            }
          }
          ((AppMeasurement.ConditionalUserProperty)localObject3).mTriggeredTimestamp = ((zzef)???).zzael.zzaqu;
          ((AppMeasurement.ConditionalUserProperty)localObject3).mTimeToLive = ((zzef)???).zzaes;
          if (((zzef)???).zzaet != null)
          {
            ((AppMeasurement.ConditionalUserProperty)localObject3).mExpiredEventName = ((zzef)???).zzaet.name;
            if (((zzef)???).zzaet.zzafo != null) {
              ((AppMeasurement.ConditionalUserProperty)localObject3).mExpiredEventParams = ((zzef)???).zzaet.zzafo.zzif();
            }
          }
          paramString3.add(localObject3);
        }
        paramString1 = paramString3;
      }
    }
  }
  
  private final void zzj(boolean paramBoolean)
  {
    zzab();
    zzch();
    zzgg().zziq().zzg("Setting app measurement enabled (FE)", Boolean.valueOf(paramBoolean));
    zzgh().setMeasurementEnabled(paramBoolean);
    if (zzgi().zzav(zzfv().zzah())) {
      if ((this.zzacr.isEnabled()) && (this.zzaon))
      {
        zzgg().zziq().log("Recording app launch after enabling measurement for the first time (FE)");
        zzkj();
      }
    }
    for (;;)
    {
      return;
      zzfx().zzkm();
      continue;
      zzfx().zzkm();
    }
  }
  
  public final void clearConditionalUserProperty(String paramString1, String paramString2, Bundle paramBundle)
  {
    zza(null, paramString1, paramString2, paramBundle);
  }
  
  public final void clearConditionalUserPropertyAs(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    Preconditions.checkNotEmpty(paramString1);
    zzfq();
    zza(paramString1, paramString2, paramString3, paramBundle);
  }
  
  public final Task<String> getAppInstanceId()
  {
    for (;;)
    {
      try
      {
        localObject = zzgh().zziw();
        if (localObject == null) {
          continue;
        }
        localObject = Tasks.forResult(localObject);
      }
      catch (Exception localException)
      {
        Object localObject;
        zzhq localzzhq;
        zzgg().zzin().log("Failed to schedule task for getAppInstanceId");
        Task localTask = Tasks.forException(localException);
        continue;
      }
      return (Task<String>)localObject;
      localObject = zzgf().zzjh();
      localzzhq = new com/google/android/gms/internal/measurement/zzhq;
      localzzhq.<init>(this);
      localObject = Tasks.call((Executor)localObject, localzzhq);
    }
  }
  
  public final List<AppMeasurement.ConditionalUserProperty> getConditionalUserProperties(String paramString1, String paramString2)
  {
    return zzf(null, paramString1, paramString2);
  }
  
  public final List<AppMeasurement.ConditionalUserProperty> getConditionalUserPropertiesAs(String paramString1, String paramString2, String paramString3)
  {
    Preconditions.checkNotEmpty(paramString1);
    zzfq();
    return zzf(paramString1, paramString2, paramString3);
  }
  
  public final Map<String, Object> getUserProperties(String paramString1, String paramString2, boolean paramBoolean)
  {
    return zzb(null, paramString1, paramString2, paramBoolean);
  }
  
  public final Map<String, Object> getUserPropertiesAs(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    Preconditions.checkNotEmpty(paramString1);
    zzfq();
    return zzb(paramString1, paramString2, paramString3, paramBoolean);
  }
  
  public final void registerOnMeasurementEventListener(AppMeasurement.OnEventListener paramOnEventListener)
  {
    zzch();
    Preconditions.checkNotNull(paramOnEventListener);
    if (!this.zzaok.add(paramOnEventListener)) {
      zzgg().zzin().log("OnEventListener already registered");
    }
  }
  
  public final void resetAnalyticsData()
  {
    zzgf().zzc(new zzhs(this));
  }
  
  public final void setConditionalUserProperty(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    Preconditions.checkNotNull(paramConditionalUserProperty);
    paramConditionalUserProperty = new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty);
    if (!TextUtils.isEmpty(paramConditionalUserProperty.mAppId)) {
      zzgg().zzin().log("Package name should be null when calling setConditionalUserProperty");
    }
    paramConditionalUserProperty.mAppId = null;
    zza(paramConditionalUserProperty);
  }
  
  public final void setConditionalUserPropertyAs(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    Preconditions.checkNotNull(paramConditionalUserProperty);
    Preconditions.checkNotEmpty(paramConditionalUserProperty.mAppId);
    zzfq();
    zza(new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty));
  }
  
  public final void setEventInterceptor(AppMeasurement.EventInterceptor paramEventInterceptor)
  {
    zzab();
    zzch();
    if ((paramEventInterceptor != null) && (paramEventInterceptor != this.zzaoj)) {
      if (this.zzaoj != null) {
        break label42;
      }
    }
    label42:
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "EventInterceptor already set.");
      this.zzaoj = paramEventInterceptor;
      return;
    }
  }
  
  public final void setMeasurementEnabled(boolean paramBoolean)
  {
    zzch();
    zzgf().zzc(new zzib(this, paramBoolean));
  }
  
  public final void setMinimumSessionDuration(long paramLong)
  {
    zzgf().zzc(new zzic(this, paramLong));
  }
  
  public final void setSessionTimeoutDuration(long paramLong)
  {
    zzgf().zzc(new zzid(this, paramLong));
  }
  
  public final void unregisterOnMeasurementEventListener(AppMeasurement.OnEventListener paramOnEventListener)
  {
    zzch();
    Preconditions.checkNotNull(paramOnEventListener);
    if (!this.zzaok.remove(paramOnEventListener)) {
      zzgg().zzin().log("OnEventListener had not been registered");
    }
  }
  
  public final void zza(String paramString1, String paramString2, Bundle paramBundle)
  {
    if ((this.zzaoj == null) || (zzjv.zzcb(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, false, null);
      return;
    }
  }
  
  public final void zza(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    zza(paramString1, paramString2, paramLong, paramBundle, false, true, true, null);
  }
  
  public final void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    if ((this.zzaoj == null) || (zzjv.zzcb(paramString2))) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      zza(paramString1, paramString2, paramBundle, true, paramBoolean, true, null);
      return;
    }
  }
  
  public final void zza(String paramString1, String paramString2, Object paramObject)
  {
    int i = 0;
    int j = 0;
    Preconditions.checkNotEmpty(paramString1);
    long l = zzbt().currentTimeMillis();
    int k = zzgc().zzby(paramString2);
    if (k != 0)
    {
      zzgc();
      paramString1 = zzjv.zza(paramString2, 24, true);
      i = j;
      if (paramString2 != null) {
        i = paramString2.length();
      }
      this.zzacr.zzgc().zza(k, "_ev", paramString1, i);
    }
    for (;;)
    {
      return;
      if (paramObject != null)
      {
        j = zzgc().zzi(paramString2, paramObject);
        if (j != 0)
        {
          zzgc();
          paramString1 = zzjv.zza(paramString2, 24, true);
          if (((paramObject instanceof String)) || ((paramObject instanceof CharSequence))) {
            i = String.valueOf(paramObject).length();
          }
          this.zzacr.zzgc().zza(j, "_ev", paramString1, i);
        }
        else
        {
          paramObject = zzgc().zzj(paramString2, paramObject);
          if (paramObject != null) {
            zza(paramString1, paramString2, l, paramObject);
          }
        }
      }
      else
      {
        zza(paramString1, paramString2, l, null);
      }
    }
  }
  
  final String zzae(long paramLong)
  {
    synchronized (new AtomicReference())
    {
      Object localObject1 = zzgf();
      zzhr localzzhr = new com/google/android/gms/internal/measurement/zzhr;
      localzzhr.<init>(this, ???);
      ((zzgg)localObject1).zzc(localzzhr);
      try
      {
        ???.wait(paramLong);
        localObject1 = (String)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzgg().zzin().log("Interrupted waiting for app instance id");
          Object localObject2 = null;
        }
      }
      return (String)localObject1;
    }
  }
  
  final void zzbm(String paramString)
  {
    this.zzaom.set(paramString);
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  public final String zziw()
  {
    return (String)this.zzaom.get();
  }
  
  public final List<zzjs> zzk(boolean paramBoolean)
  {
    zzch();
    zzgg().zziq().log("Fetching user attributes (FE)");
    if (zzgf().zzjg())
    {
      zzgg().zzil().log("Cannot get all user properties from analytics worker thread");
      ??? = Collections.emptyList();
    }
    for (;;)
    {
      return (List<zzjs>)???;
      zzgf();
      if (zzgg.isMainThread())
      {
        zzgg().zzil().log("Cannot get all user properties from main thread");
        ??? = Collections.emptyList();
        continue;
      }
      synchronized (new AtomicReference())
      {
        zzgg localzzgg = this.zzacr.zzgf();
        Object localObject2 = new com/google/android/gms/internal/measurement/zzhp;
        ((zzhp)localObject2).<init>(this, (AtomicReference)???, paramBoolean);
        localzzgg.zzc((Runnable)localObject2);
        try
        {
          ???.wait(5000L);
          localObject2 = (List)((AtomicReference)???).get();
          ??? = localObject2;
          if (localObject2 != null) {
            continue;
          }
          zzgg().zzin().log("Timed out waiting for get user properties");
          ??? = Collections.emptyList();
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            zzgg().zzin().zzg("Interrupted waiting for get user properties", localInterruptedException);
          }
        }
      }
    }
  }
  
  public final Boolean zzke()
  {
    synchronized (new AtomicReference())
    {
      Object localObject1 = zzgf();
      zzhn localzzhn = new com/google/android/gms/internal/measurement/zzhn;
      localzzhn.<init>(this, ???);
      ((zzgg)localObject1).zzc(localzzhn);
      try
      {
        ???.wait(5000L);
        localObject1 = (Boolean)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzgg().zzin().log("Interrupted waiting for boolean test flag value");
          Object localObject2 = null;
        }
      }
      return (Boolean)localObject1;
    }
  }
  
  public final String zzkf()
  {
    synchronized (new AtomicReference())
    {
      Object localObject1 = zzgf();
      zzhx localzzhx = new com/google/android/gms/internal/measurement/zzhx;
      localzzhx.<init>(this, ???);
      ((zzgg)localObject1).zzc(localzzhx);
      try
      {
        ???.wait(5000L);
        localObject1 = (String)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzgg().zzin().log("Interrupted waiting for String test flag value");
          Object localObject2 = null;
        }
      }
      return (String)localObject1;
    }
  }
  
  public final Long zzkg()
  {
    synchronized (new AtomicReference())
    {
      Object localObject1 = zzgf();
      zzhy localzzhy = new com/google/android/gms/internal/measurement/zzhy;
      localzzhy.<init>(this, ???);
      ((zzgg)localObject1).zzc(localzzhy);
      try
      {
        ???.wait(5000L);
        localObject1 = (Long)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzgg().zzin().log("Interrupted waiting for long test flag value");
          Object localObject2 = null;
        }
      }
      return (Long)localObject1;
    }
  }
  
  public final Integer zzkh()
  {
    synchronized (new AtomicReference())
    {
      Object localObject1 = zzgf();
      zzhz localzzhz = new com/google/android/gms/internal/measurement/zzhz;
      localzzhz.<init>(this, ???);
      ((zzgg)localObject1).zzc(localzzhz);
      try
      {
        ???.wait(5000L);
        localObject1 = (Integer)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzgg().zzin().log("Interrupted waiting for int test flag value");
          Object localObject2 = null;
        }
      }
      return (Integer)localObject1;
    }
  }
  
  public final Double zzki()
  {
    synchronized (new AtomicReference())
    {
      Object localObject1 = zzgf();
      zzia localzzia = new com/google/android/gms/internal/measurement/zzia;
      localzzia.<init>(this, ???);
      ((zzgg)localObject1).zzc(localzzia);
      try
      {
        ???.wait(5000L);
        localObject1 = (Double)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          zzgg().zzin().log("Interrupted waiting for double test flag value");
          Object localObject2 = null;
        }
      }
      return (Double)localObject1;
    }
  }
  
  public final void zzkj()
  {
    zzab();
    zzch();
    if (!this.zzacr.zzjk()) {}
    for (;;)
    {
      return;
      zzfx().zzkj();
      this.zzaon = false;
      String str = zzgh().zziz();
      if (!TextUtils.isEmpty(str))
      {
        zzfw().zzch();
        if (!str.equals(Build.VERSION.RELEASE))
        {
          Bundle localBundle = new Bundle();
          localBundle.putString("_po", str);
          zza("auto", "_ou", localBundle);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzhm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */