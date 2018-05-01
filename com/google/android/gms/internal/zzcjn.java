package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.EventInterceptor;
import com.google.android.gms.measurement.AppMeasurement.OnEventListener;
import com.google.android.gms.measurement.AppMeasurement.zzb;
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
import java.util.concurrent.atomic.AtomicReference;

public final class zzcjn
  extends zzcjl
{
  protected zzckb zzjgx;
  private AppMeasurement.EventInterceptor zzjgy;
  private final Set<AppMeasurement.OnEventListener> zzjgz = new CopyOnWriteArraySet();
  private boolean zzjha;
  private final AtomicReference<String> zzjhb = new AtomicReference();
  
  protected zzcjn(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final void zza(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    long l = zzws().currentTimeMillis();
    zzbq.checkNotNull(paramConditionalUserProperty);
    zzbq.zzgm(paramConditionalUserProperty.mName);
    zzbq.zzgm(paramConditionalUserProperty.mOrigin);
    zzbq.checkNotNull(paramConditionalUserProperty.mValue);
    paramConditionalUserProperty.mCreationTimestamp = l;
    String str = paramConditionalUserProperty.mName;
    Object localObject1 = paramConditionalUserProperty.mValue;
    if (zzawu().zzkd(str) != 0)
    {
      zzawy().zzazd().zzj("Invalid conditional user property name", zzawt().zzjj(str));
      return;
    }
    if (zzawu().zzl(str, localObject1) != 0)
    {
      zzawy().zzazd().zze("Invalid conditional user property value", zzawt().zzjj(str), localObject1);
      return;
    }
    Object localObject2 = zzawu().zzm(str, localObject1);
    if (localObject2 == null)
    {
      zzawy().zzazd().zze("Unable to normalize conditional user property value", zzawt().zzjj(str), localObject1);
      return;
    }
    paramConditionalUserProperty.mValue = localObject2;
    l = paramConditionalUserProperty.mTriggerTimeout;
    if ((!TextUtils.isEmpty(paramConditionalUserProperty.mTriggerEventName)) && ((l > 15552000000L) || (l < 1L)))
    {
      zzawy().zzazd().zze("Invalid conditional user property timeout", zzawt().zzjj(str), Long.valueOf(l));
      return;
    }
    l = paramConditionalUserProperty.mTimeToLive;
    if ((l > 15552000000L) || (l < 1L))
    {
      zzawy().zzazd().zze("Invalid conditional user property time to live", zzawt().zzjj(str), Long.valueOf(l));
      return;
    }
    zzawx().zzg(new zzcjp(this, paramConditionalUserProperty));
  }
  
  private final void zza(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    if (paramBundle == null)
    {
      paramBundle = new Bundle();
      zzawx().zzg(new zzcjv(this, paramString1, paramString2, paramLong, paramBundle, paramBoolean1, paramBoolean2, paramBoolean3, paramString3));
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
          i = 0;
          while (i < paramBundle.length)
          {
            if ((paramBundle[i] instanceof Bundle)) {
              paramBundle[i] = new Bundle((Bundle)paramBundle[i]);
            }
            i += 1;
          }
        }
        else if ((localObject instanceof ArrayList))
        {
          paramBundle = (ArrayList)localObject;
          i = 0;
          while (i < paramBundle.size())
          {
            localObject = paramBundle.get(i);
            if ((localObject instanceof Bundle)) {
              paramBundle.set(i, new Bundle((Bundle)localObject));
            }
            i += 1;
          }
        }
      }
    }
  }
  
  private final void zza(String paramString1, String paramString2, long paramLong, Object paramObject)
  {
    zzawx().zzg(new zzcjw(this, paramString1, paramString2, paramObject, paramLong));
  }
  
  private final void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zza(paramString1, paramString2, zzws().currentTimeMillis(), paramBundle, true, paramBoolean2, paramBoolean3, null);
  }
  
  private final void zza(String paramString1, String paramString2, Object paramObject, long paramLong)
  {
    zzbq.zzgm(paramString1);
    zzbq.zzgm(paramString2);
    zzve();
    zzxf();
    if (!this.zziwf.isEnabled()) {
      zzawy().zzazi().log("User property not set since app measurement is disabled");
    }
    while (!this.zziwf.zzazv()) {
      return;
    }
    zzawy().zzazi().zze("Setting user property (FE)", zzawt().zzjh(paramString2), paramObject);
    paramString1 = new zzcln(paramString2, paramLong, paramObject, paramString1);
    zzawp().zzb(paramString1);
  }
  
  private final void zza(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    long l = zzws().currentTimeMillis();
    zzbq.zzgm(paramString2);
    AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
    localConditionalUserProperty.mAppId = paramString1;
    localConditionalUserProperty.mName = paramString2;
    localConditionalUserProperty.mCreationTimestamp = l;
    if (paramString3 != null)
    {
      localConditionalUserProperty.mExpiredEventName = paramString3;
      localConditionalUserProperty.mExpiredEventParams = paramBundle;
    }
    zzawx().zzg(new zzcjq(this, localConditionalUserProperty));
  }
  
  private final Map<String, Object> zzb(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (zzawx().zzazs())
    {
      zzawy().zzazd().log("Cannot get user properties from analytics worker thread");
      return Collections.emptyMap();
    }
    zzawx();
    if (zzcih.zzau())
    {
      zzawy().zzazd().log("Cannot get user properties from main thread");
      return Collections.emptyMap();
    }
    synchronized (new AtomicReference())
    {
      this.zziwf.zzawx().zzg(new zzcjs(this, ???, paramString1, paramString2, paramString3, paramBoolean));
      try
      {
        ???.wait(5000L);
        paramString2 = (List)???.get();
        if (paramString2 == null)
        {
          zzawy().zzazf().log("Timed out waiting for get user properties");
          return Collections.emptyMap();
        }
      }
      catch (InterruptedException paramString1)
      {
        for (;;)
        {
          zzawy().zzazf().zzj("Interrupted waiting for get user properties", paramString1);
        }
      }
    }
    paramString1 = new ArrayMap(paramString2.size());
    paramString2 = paramString2.iterator();
    while (paramString2.hasNext())
    {
      paramString3 = (zzcln)paramString2.next();
      paramString1.put(paramString3.name, paramString3.getValue());
    }
    return paramString1;
  }
  
  private final void zzb(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzve();
    zzxf();
    zzbq.checkNotNull(paramConditionalUserProperty);
    zzbq.zzgm(paramConditionalUserProperty.mName);
    zzbq.zzgm(paramConditionalUserProperty.mOrigin);
    zzbq.checkNotNull(paramConditionalUserProperty.mValue);
    if (!this.zziwf.isEnabled())
    {
      zzawy().zzazi().log("Conditional property not sent since Firebase Analytics is disabled");
      return;
    }
    zzcln localzzcln = new zzcln(paramConditionalUserProperty.mName, paramConditionalUserProperty.mTriggeredTimestamp, paramConditionalUserProperty.mValue, paramConditionalUserProperty.mOrigin);
    try
    {
      zzcha localzzcha1 = zzawu().zza(paramConditionalUserProperty.mTriggeredEventName, paramConditionalUserProperty.mTriggeredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      zzcha localzzcha2 = zzawu().zza(paramConditionalUserProperty.mTimedOutEventName, paramConditionalUserProperty.mTimedOutEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      zzcha localzzcha3 = zzawu().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, 0L, true, false);
      paramConditionalUserProperty = new zzcgl(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzcln, paramConditionalUserProperty.mCreationTimestamp, false, paramConditionalUserProperty.mTriggerEventName, localzzcha2, paramConditionalUserProperty.mTriggerTimeout, localzzcha1, paramConditionalUserProperty.mTimeToLive, localzzcha3);
      zzawp().zzf(paramConditionalUserProperty);
      return;
    }
    catch (IllegalArgumentException paramConditionalUserProperty) {}
  }
  
  private final void zzb(String paramString1, String paramString2, long paramLong, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString3)
  {
    zzbq.zzgm(paramString1);
    zzbq.zzgm(paramString2);
    zzbq.checkNotNull(paramBundle);
    zzve();
    zzxf();
    if (!this.zziwf.isEnabled()) {
      zzawy().zzazi().log("Event not sent since app measurement is disabled");
    }
    label102:
    boolean bool1;
    do
    {
      return;
      if (!this.zzjha) {
        this.zzjha = true;
      }
      try
      {
        localClass = Class.forName("com.google.android.gms.tagmanager.TagManagerService");
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        for (;;)
        {
          Class localClass;
          boolean bool2;
          zzawy().zzazh().log("Tag Manager is not found and thus will not be used");
        }
      }
      try
      {
        localClass.getDeclaredMethod("initialize", new Class[] { Context.class }).invoke(null, new Object[] { getContext() });
        bool1 = "am".equals(paramString1);
        bool2 = zzclq.zzki(paramString2);
        if ((paramBoolean1) && (this.zzjgy != null) && (!bool2) && (!bool1))
        {
          zzawy().zzazi().zze("Passing event to registered event handler (FE)", zzawt().zzjh(paramString2), zzawt().zzx(paramBundle));
          this.zzjgy.interceptEvent(paramString1, paramString2, paramBundle, paramLong);
          return;
        }
      }
      catch (Exception localException)
      {
        zzawy().zzazf().zzj("Failed to invoke Tag Manager's initialize() method", localException);
        break label102;
      }
    } while (!this.zziwf.zzazv());
    int j = zzawu().zzkb(paramString2);
    if (j != 0)
    {
      zzawu();
      paramString1 = zzclq.zza(paramString2, 40, true);
      if (paramString2 != null) {}
      for (i = paramString2.length();; i = 0)
      {
        this.zziwf.zzawu().zza(paramString3, j, "_ev", paramString1, i);
        return;
      }
    }
    Object localObject1 = Collections.singletonList("_o");
    Bundle localBundle1 = zzawu().zza(paramString2, paramBundle, (List)localObject1, paramBoolean3, true);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(localBundle1);
    long l = zzawu().zzbaz().nextLong();
    int i = 0;
    paramBundle = (String[])localBundle1.keySet().toArray(new String[paramBundle.size()]);
    Arrays.sort(paramBundle);
    int m = paramBundle.length;
    j = 0;
    label392:
    if (j < m)
    {
      String str = paramBundle[j];
      Object localObject2 = localBundle1.get(str);
      zzawu();
      localObject2 = zzclq.zzaf(localObject2);
      if (localObject2 == null) {
        break label884;
      }
      localBundle1.putInt(str, localObject2.length);
      int k = 0;
      while (k < localObject2.length)
      {
        Bundle localBundle2 = localObject2[k];
        localBundle2 = zzawu().zza("_ep", localBundle2, (List)localObject1, paramBoolean3, false);
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
    label668:
    label836:
    label842:
    label845:
    label884:
    for (;;)
    {
      j += 1;
      break label392;
      if (i != 0)
      {
        localBundle1.putLong("_eid", l);
        localBundle1.putInt("_epc", i);
      }
      localObject1 = zzawq().zzbao();
      if ((localObject1 != null) && (!localBundle1.containsKey("_sc"))) {
        ((zzckf)localObject1).zzjib = true;
      }
      i = 0;
      while (i < localArrayList.size())
      {
        localBundle1 = (Bundle)localArrayList.get(i);
        if (i != 0)
        {
          j = 1;
          if (j == 0) {
            break label836;
          }
          paramBundle = "_ep";
          localBundle1.putString("_o", paramString1);
          if (!localBundle1.containsKey("_sc")) {
            zzckc.zza((AppMeasurement.zzb)localObject1, localBundle1);
          }
          if (!paramBoolean2) {
            break label842;
          }
          localBundle1 = zzawu().zzy(localBundle1);
        }
        for (;;)
        {
          zzawy().zzazi().zze("Logging event (FE)", zzawt().zzjh(paramString2), zzawt().zzx(localBundle1));
          paramBundle = new zzcha(paramBundle, new zzcgx(localBundle1), paramString1, paramLong);
          zzawp().zzc(paramBundle, paramString3);
          if (bool1) {
            break label845;
          }
          paramBundle = this.zzjgz.iterator();
          while (paramBundle.hasNext()) {
            ((AppMeasurement.OnEventListener)paramBundle.next()).onEvent(paramString1, paramString2, new Bundle(localBundle1), paramLong);
          }
          j = 0;
          break;
          paramBundle = paramString2;
          break label668;
        }
        i += 1;
      }
      if ((zzawq().zzbao() == null) || (!"_ae".equals(paramString2))) {
        break;
      }
      zzaww().zzbs(true);
      return;
    }
  }
  
  private final void zzbp(boolean paramBoolean)
  {
    zzve();
    zzxf();
    zzawy().zzazi().zzj("Setting app measurement enabled (FE)", Boolean.valueOf(paramBoolean));
    zzawz().setMeasurementEnabled(paramBoolean);
    zzawp().zzbaq();
  }
  
  private final void zzc(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzve();
    zzxf();
    zzbq.checkNotNull(paramConditionalUserProperty);
    zzbq.zzgm(paramConditionalUserProperty.mName);
    if (!this.zziwf.isEnabled())
    {
      zzawy().zzazi().log("Conditional property not cleared since Firebase Analytics is disabled");
      return;
    }
    zzcln localzzcln = new zzcln(paramConditionalUserProperty.mName, 0L, null, null);
    try
    {
      zzcha localzzcha = zzawu().zza(paramConditionalUserProperty.mExpiredEventName, paramConditionalUserProperty.mExpiredEventParams, paramConditionalUserProperty.mOrigin, paramConditionalUserProperty.mCreationTimestamp, true, false);
      paramConditionalUserProperty = new zzcgl(paramConditionalUserProperty.mAppId, paramConditionalUserProperty.mOrigin, localzzcln, paramConditionalUserProperty.mCreationTimestamp, paramConditionalUserProperty.mActive, paramConditionalUserProperty.mTriggerEventName, null, paramConditionalUserProperty.mTriggerTimeout, null, paramConditionalUserProperty.mTimeToLive, localzzcha);
      zzawp().zzf(paramConditionalUserProperty);
      return;
    }
    catch (IllegalArgumentException paramConditionalUserProperty) {}
  }
  
  private final List<AppMeasurement.ConditionalUserProperty> zzk(String paramString1, String paramString2, String paramString3)
  {
    if (zzawx().zzazs())
    {
      zzawy().zzazd().log("Cannot get conditional user properties from analytics worker thread");
      return Collections.emptyList();
    }
    zzawx();
    if (zzcih.zzau())
    {
      zzawy().zzazd().log("Cannot get conditional user properties from main thread");
      return Collections.emptyList();
    }
    synchronized (new AtomicReference())
    {
      this.zziwf.zzawx().zzg(new zzcjr(this, (AtomicReference)???, paramString1, paramString2, paramString3));
      try
      {
        ???.wait(5000L);
        ??? = (List)((AtomicReference)???).get();
        if (??? == null)
        {
          zzawy().zzazf().zzj("Timed out waiting for get conditional user properties", paramString1);
          return Collections.emptyList();
        }
      }
      catch (InterruptedException paramString3)
      {
        for (;;)
        {
          zzawy().zzazf().zze("Interrupted waiting for get conditional user properties", paramString1, paramString3);
        }
      }
    }
    paramString3 = new ArrayList(((List)???).size());
    ??? = ((List)???).iterator();
    while (((Iterator)???).hasNext())
    {
      zzcgl localzzcgl = (zzcgl)((Iterator)???).next();
      AppMeasurement.ConditionalUserProperty localConditionalUserProperty = new AppMeasurement.ConditionalUserProperty();
      localConditionalUserProperty.mAppId = paramString1;
      localConditionalUserProperty.mOrigin = paramString2;
      localConditionalUserProperty.mCreationTimestamp = localzzcgl.zziyh;
      localConditionalUserProperty.mName = localzzcgl.zziyg.name;
      localConditionalUserProperty.mValue = localzzcgl.zziyg.getValue();
      localConditionalUserProperty.mActive = localzzcgl.zziyi;
      localConditionalUserProperty.mTriggerEventName = localzzcgl.zziyj;
      if (localzzcgl.zziyk != null)
      {
        localConditionalUserProperty.mTimedOutEventName = localzzcgl.zziyk.name;
        if (localzzcgl.zziyk.zzizt != null) {
          localConditionalUserProperty.mTimedOutEventParams = localzzcgl.zziyk.zzizt.zzayx();
        }
      }
      localConditionalUserProperty.mTriggerTimeout = localzzcgl.zziyl;
      if (localzzcgl.zziym != null)
      {
        localConditionalUserProperty.mTriggeredEventName = localzzcgl.zziym.name;
        if (localzzcgl.zziym.zzizt != null) {
          localConditionalUserProperty.mTriggeredEventParams = localzzcgl.zziym.zzizt.zzayx();
        }
      }
      localConditionalUserProperty.mTriggeredTimestamp = localzzcgl.zziyg.zzjji;
      localConditionalUserProperty.mTimeToLive = localzzcgl.zziyn;
      if (localzzcgl.zziyo != null)
      {
        localConditionalUserProperty.mExpiredEventName = localzzcgl.zziyo.name;
        if (localzzcgl.zziyo.zzizt != null) {
          localConditionalUserProperty.mExpiredEventParams = localzzcgl.zziyo.zzizt.zzayx();
        }
      }
      paramString3.add(localConditionalUserProperty);
    }
    return paramString3;
  }
  
  public final void clearConditionalUserProperty(String paramString1, String paramString2, Bundle paramBundle)
  {
    zza(null, paramString1, paramString2, paramBundle);
  }
  
  public final void clearConditionalUserPropertyAs(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    zzbq.zzgm(paramString1);
    zzawi();
    zza(paramString1, paramString2, paramString3, paramBundle);
  }
  
  public final Task<String> getAppInstanceId()
  {
    try
    {
      Object localObject = zzawz().zzazn();
      if (localObject != null) {
        return Tasks.forResult(localObject);
      }
      localObject = Tasks.call(zzawx().zzazt(), new zzcjy(this));
      return (Task<String>)localObject;
    }
    catch (Exception localException)
    {
      zzawy().zzazf().log("Failed to schedule task for getAppInstanceId");
      return Tasks.forException(localException);
    }
  }
  
  public final List<AppMeasurement.ConditionalUserProperty> getConditionalUserProperties(String paramString1, String paramString2)
  {
    return zzk(null, paramString1, paramString2);
  }
  
  public final List<AppMeasurement.ConditionalUserProperty> getConditionalUserPropertiesAs(String paramString1, String paramString2, String paramString3)
  {
    zzbq.zzgm(paramString1);
    zzawi();
    return zzk(paramString1, paramString2, paramString3);
  }
  
  public final Map<String, Object> getUserProperties(String paramString1, String paramString2, boolean paramBoolean)
  {
    return zzb(null, paramString1, paramString2, paramBoolean);
  }
  
  public final Map<String, Object> getUserPropertiesAs(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    zzbq.zzgm(paramString1);
    zzawi();
    return zzb(paramString1, paramString2, paramString3, paramBoolean);
  }
  
  public final void registerOnMeasurementEventListener(AppMeasurement.OnEventListener paramOnEventListener)
  {
    zzxf();
    zzbq.checkNotNull(paramOnEventListener);
    if (!this.zzjgz.add(paramOnEventListener)) {
      zzawy().zzazf().log("OnEventListener already registered");
    }
  }
  
  public final void resetAnalyticsData()
  {
    zzawx().zzg(new zzcka(this));
  }
  
  public final void setConditionalUserProperty(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzbq.checkNotNull(paramConditionalUserProperty);
    paramConditionalUserProperty = new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty);
    if (!TextUtils.isEmpty(paramConditionalUserProperty.mAppId)) {
      zzawy().zzazf().log("Package name should be null when calling setConditionalUserProperty");
    }
    paramConditionalUserProperty.mAppId = null;
    zza(paramConditionalUserProperty);
  }
  
  public final void setConditionalUserPropertyAs(AppMeasurement.ConditionalUserProperty paramConditionalUserProperty)
  {
    zzbq.checkNotNull(paramConditionalUserProperty);
    zzbq.zzgm(paramConditionalUserProperty.mAppId);
    zzawi();
    zza(new AppMeasurement.ConditionalUserProperty(paramConditionalUserProperty));
  }
  
  public final void setEventInterceptor(AppMeasurement.EventInterceptor paramEventInterceptor)
  {
    zzve();
    zzxf();
    if ((paramEventInterceptor != null) && (paramEventInterceptor != this.zzjgy)) {
      if (this.zzjgy != null) {
        break label42;
      }
    }
    label42:
    for (boolean bool = true;; bool = false)
    {
      zzbq.zza(bool, "EventInterceptor already set.");
      this.zzjgy = paramEventInterceptor;
      return;
    }
  }
  
  public final void setMeasurementEnabled(boolean paramBoolean)
  {
    zzxf();
    zzawx().zzg(new zzcjo(this, paramBoolean));
  }
  
  public final void setMinimumSessionDuration(long paramLong)
  {
    zzawx().zzg(new zzcjt(this, paramLong));
  }
  
  public final void setSessionTimeoutDuration(long paramLong)
  {
    zzawx().zzg(new zzcju(this, paramLong));
  }
  
  public final void unregisterOnMeasurementEventListener(AppMeasurement.OnEventListener paramOnEventListener)
  {
    zzxf();
    zzbq.checkNotNull(paramOnEventListener);
    if (!this.zzjgz.remove(paramOnEventListener)) {
      zzawy().zzazf().log("OnEventListener had not been registered");
    }
  }
  
  public final void zza(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    zza(paramString1, paramString2, paramLong, paramBundle, false, true, true, null);
  }
  
  public final void zza(String paramString1, String paramString2, Bundle paramBundle, boolean paramBoolean)
  {
    if ((this.zzjgy == null) || (zzclq.zzki(paramString2))) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      zza(paramString1, paramString2, paramBundle, true, paramBoolean, true, null);
      return;
    }
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  public final String zzazn()
  {
    return (String)this.zzjhb.get();
  }
  
  public final void zzb(String paramString1, String paramString2, Object paramObject)
  {
    int i = 0;
    int j = 0;
    zzbq.zzgm(paramString1);
    long l = zzws().currentTimeMillis();
    int k = zzawu().zzkd(paramString2);
    if (k != 0)
    {
      zzawu();
      paramString1 = zzclq.zza(paramString2, 24, true);
      i = j;
      if (paramString2 != null) {
        i = paramString2.length();
      }
      this.zziwf.zzawu().zza(k, "_ev", paramString1, i);
    }
    do
    {
      return;
      if (paramObject == null) {
        break;
      }
      j = zzawu().zzl(paramString2, paramObject);
      if (j != 0)
      {
        zzawu();
        paramString1 = zzclq.zza(paramString2, 24, true);
        if (((paramObject instanceof String)) || ((paramObject instanceof CharSequence))) {
          i = String.valueOf(paramObject).length();
        }
        this.zziwf.zzawu().zza(j, "_ev", paramString1, i);
        return;
      }
      paramObject = zzawu().zzm(paramString2, paramObject);
    } while (paramObject == null);
    zza(paramString1, paramString2, l, paramObject);
    return;
    zza(paramString1, paramString2, l, null);
  }
  
  final String zzbd(long paramLong)
  {
    synchronized (new AtomicReference())
    {
      zzawx().zzg(new zzcjz(this, ???));
      try
      {
        ???.wait(paramLong);
        return (String)???.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        zzawy().zzazf().log("Interrupted waiting for app instance id");
        return null;
      }
    }
  }
  
  public final List<zzcln> zzbq(boolean paramBoolean)
  {
    zzxf();
    zzawy().zzazi().log("Fetching user attributes (FE)");
    if (zzawx().zzazs())
    {
      zzawy().zzazd().log("Cannot get all user properties from analytics worker thread");
      ??? = Collections.emptyList();
    }
    for (;;)
    {
      return (List<zzcln>)???;
      zzawx();
      if (zzcih.zzau())
      {
        zzawy().zzazd().log("Cannot get all user properties from main thread");
        return Collections.emptyList();
      }
      synchronized (new AtomicReference())
      {
        this.zziwf.zzawx().zzg(new zzcjx(this, (AtomicReference)???, paramBoolean));
        try
        {
          ???.wait(5000L);
          List localList = (List)((AtomicReference)???).get();
          ??? = localList;
          if (localList != null) {
            continue;
          }
          zzawy().zzazf().log("Timed out waiting for get user properties");
          return Collections.emptyList();
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            zzawy().zzazf().zzj("Interrupted waiting for get user properties", localInterruptedException);
          }
        }
      }
    }
  }
  
  public final void zzc(String paramString1, String paramString2, Bundle paramBundle)
  {
    if ((this.zzjgy == null) || (zzclq.zzki(paramString2))) {}
    for (boolean bool = true;; bool = false)
    {
      zza(paramString1, paramString2, paramBundle, true, bool, false, null);
      return;
    }
  }
  
  final void zzjp(String paramString)
  {
    this.zzjhb.set(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcjn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */