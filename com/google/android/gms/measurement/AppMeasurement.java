package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zzf;
import com.google.android.gms.measurement.internal.UserAttributeParcel;
import com.google.android.gms.measurement.internal.zzal;
import com.google.android.gms.measurement.internal.zzd;
import com.google.android.gms.measurement.internal.zzx;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.UserProperty;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Deprecated
public class AppMeasurement
{
  private final zzx anq;
  
  public AppMeasurement(zzx paramzzx)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramzzx);
    this.anq = paramzzx;
  }
  
  @Deprecated
  @Keep
  @RequiresPermission(allOf={"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
  public static AppMeasurement getInstance(Context paramContext)
  {
    return zzx.zzdt(paramContext).zzbwy();
  }
  
  private void zzc(String paramString1, String paramString2, Object paramObject)
  {
    this.anq.zzbux().zzd(paramString1, paramString2, paramObject);
  }
  
  public void logEvent(@NonNull @Size(max=32L, min=1L) String paramString, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    if ((this.anq.zzbvi().zzact()) || (!"_iap".equals(paramString)))
    {
      int j = this.anq.zzbvc().zzmy(paramString);
      if (j != 0)
      {
        paramBundle = this.anq.zzbvc().zza(paramString, this.anq.zzbvi().zzbtn(), true);
        if (paramString != null) {}
        for (int i = paramString.length();; i = 0)
        {
          this.anq.zzbvc().zza(j, "_ev", paramBundle, i);
          return;
        }
      }
    }
    this.anq.zzbux().zza("app", paramString, localBundle, true);
  }
  
  @Deprecated
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.anq.zzbux().setMeasurementEnabled(paramBoolean);
  }
  
  public void setMinimumSessionDuration(long paramLong)
  {
    this.anq.zzbux().setMinimumSessionDuration(paramLong);
  }
  
  public void setSessionTimeoutDuration(long paramLong)
  {
    this.anq.zzbux().setSessionTimeoutDuration(paramLong);
  }
  
  public void setUserId(String paramString)
  {
    zzb("app", "_id", paramString);
  }
  
  public void setUserProperty(@NonNull @Size(max=24L, min=1L) String paramString1, @Nullable @Size(max=36L) String paramString2)
  {
    int j = this.anq.zzbvc().zzna(paramString1);
    if (j != 0)
    {
      paramString2 = this.anq.zzbvc().zza(paramString1, this.anq.zzbvi().zzbto(), true);
      if (paramString1 != null) {}
      for (int i = paramString1.length();; i = 0)
      {
        this.anq.zzbvc().zza(j, "_ev", paramString2, i);
        return;
      }
    }
    zzb("app", paramString1, paramString2);
  }
  
  @WorkerThread
  public void zza(zzb paramzzb)
  {
    this.anq.zzbux().zza(paramzzb);
  }
  
  public void zza(zzc paramzzc)
  {
    this.anq.zzbux().zza(paramzzc);
  }
  
  public void zza(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    if (paramBundle == null) {
      paramBundle = new Bundle();
    }
    for (;;)
    {
      this.anq.zzbux().zzd(paramString1, paramString2, paramBundle, paramLong);
      return;
    }
  }
  
  public void zzb(String paramString1, String paramString2, Object paramObject)
  {
    zzc(paramString1, paramString2, paramObject);
  }
  
  @WorkerThread
  public Map<String, Object> zzce(boolean paramBoolean)
  {
    Object localObject = this.anq.zzbux().zzci(paramBoolean);
    HashMap localHashMap = new HashMap(((List)localObject).size());
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      UserAttributeParcel localUserAttributeParcel = (UserAttributeParcel)((Iterator)localObject).next();
      localHashMap.put(localUserAttributeParcel.name, localUserAttributeParcel.getValue());
    }
    return localHashMap;
  }
  
  public void zze(String paramString1, String paramString2, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    this.anq.zzbux().zzf(paramString1, paramString2, localBundle);
  }
  
  public static final class zza
    extends FirebaseAnalytics.Event
  {
    public static final Map<String, String> anr = zzf.zzb(new String[] { "app_clear_data", "app_exception", "app_remove", "app_update", "firebase_campaign", "error", "first_open", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement" }, new String[] { "_cd", "_ae", "_ui", "_au", "_cmp", "_err", "_f", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e" });
  }
  
  public static abstract interface zzb
  {
    @WorkerThread
    public abstract void zzb(String paramString1, String paramString2, Bundle paramBundle, long paramLong);
  }
  
  public static abstract interface zzc
  {
    @WorkerThread
    public abstract void zzc(String paramString1, String paramString2, Bundle paramBundle, long paramLong);
  }
  
  public static final class zzd
    extends FirebaseAnalytics.Param
  {
    public static final Map<String, String> ans = zzf.zzb(new String[] { "firebase_conversion", "engagement_time_msec", "firebase_error", "firebase_error_value", "firebase_error_length", "debug", "realtime", "firebase_event_origin", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update" }, new String[] { "_c", "_et", "_err", "_ev", "_el", "_dbg", "_r", "_o", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu" });
  }
  
  public static final class zze
    extends FirebaseAnalytics.UserProperty
  {
    public static final Map<String, String> ant = zzf.zzb(new String[] { "firebase_last_notification", "first_open_time", "last_deep_link_referrer", "user_id" }, new String[] { "_ln", "_fot", "_ldl", "_id" });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */