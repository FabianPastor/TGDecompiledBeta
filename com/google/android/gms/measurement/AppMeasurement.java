package com.google.android.gms.measurement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.api.internal.GoogleServices;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.measurement.zzdx;
import com.google.android.gms.internal.measurement.zzfg;
import com.google.android.gms.internal.measurement.zzfi;
import com.google.android.gms.internal.measurement.zzgl;
import com.google.android.gms.internal.measurement.zzhm;
import com.google.android.gms.internal.measurement.zzig;
import com.google.android.gms.internal.measurement.zzih;
import com.google.android.gms.internal.measurement.zzjs;
import com.google.android.gms.internal.measurement.zzjv;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.google.firebase.analytics.FirebaseAnalytics.UserProperty;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Deprecated
@Keep
public class AppMeasurement
{
  public static final String CRASH_ORIGIN = "crash";
  public static final String FCM_ORIGIN = "fcm";
  private final zzgl zzacr;
  
  public AppMeasurement(zzgl paramzzgl)
  {
    Preconditions.checkNotNull(paramzzgl);
    this.zzacr = paramzzgl;
  }
  
  @Deprecated
  @Keep
  public static AppMeasurement getInstance(Context paramContext)
  {
    return zzgl.zzg(paramContext).zzjo();
  }
  
  @Keep
  public void beginAdUnitExposure(String paramString)
  {
    this.zzacr.zzfs().beginAdUnitExposure(paramString);
  }
  
  @Keep
  public void clearConditionalUserProperty(String paramString1, String paramString2, Bundle paramBundle)
  {
    this.zzacr.zzfu().clearConditionalUserProperty(paramString1, paramString2, paramBundle);
  }
  
  @Keep
  protected void clearConditionalUserPropertyAs(String paramString1, String paramString2, String paramString3, Bundle paramBundle)
  {
    this.zzacr.zzfu().clearConditionalUserPropertyAs(paramString1, paramString2, paramString3, paramBundle);
  }
  
  @Keep
  public void endAdUnitExposure(String paramString)
  {
    this.zzacr.zzfs().endAdUnitExposure(paramString);
  }
  
  @Keep
  public long generateEventId()
  {
    return this.zzacr.zzgc().zzkt();
  }
  
  @Keep
  public String getAppInstanceId()
  {
    return this.zzacr.zzfu().zziw();
  }
  
  public Boolean getBoolean()
  {
    return this.zzacr.zzfu().zzke();
  }
  
  @Keep
  public List<ConditionalUserProperty> getConditionalUserProperties(String paramString1, String paramString2)
  {
    return this.zzacr.zzfu().getConditionalUserProperties(paramString1, paramString2);
  }
  
  @Keep
  protected List<ConditionalUserProperty> getConditionalUserPropertiesAs(String paramString1, String paramString2, String paramString3)
  {
    return this.zzacr.zzfu().getConditionalUserPropertiesAs(paramString1, paramString2, paramString3);
  }
  
  @Keep
  public String getCurrentScreenClass()
  {
    Object localObject = this.zzacr.zzfy().zzkl();
    if (localObject != null) {}
    for (localObject = ((zzig)localObject).zzapa;; localObject = null) {
      return (String)localObject;
    }
  }
  
  @Keep
  public String getCurrentScreenName()
  {
    Object localObject = this.zzacr.zzfy().zzkl();
    if (localObject != null) {}
    for (localObject = ((zzig)localObject).zzug;; localObject = null) {
      return (String)localObject;
    }
  }
  
  public Double getDouble()
  {
    return this.zzacr.zzfu().zzki();
  }
  
  @Keep
  public String getGmpAppId()
  {
    try
    {
      String str = GoogleServices.getGoogleAppId();
      return str;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;)
      {
        this.zzacr.zzgg().zzil().zzg("getGoogleAppId failed with exception", localIllegalStateException);
        Object localObject = null;
      }
    }
  }
  
  public Integer getInteger()
  {
    return this.zzacr.zzfu().zzkh();
  }
  
  public Long getLong()
  {
    return this.zzacr.zzfu().zzkg();
  }
  
  @Keep
  public int getMaxUserProperties(String paramString)
  {
    this.zzacr.zzfu();
    Preconditions.checkNotEmpty(paramString);
    return 25;
  }
  
  public String getString()
  {
    return this.zzacr.zzfu().zzkf();
  }
  
  @Keep
  protected Map<String, Object> getUserProperties(String paramString1, String paramString2, boolean paramBoolean)
  {
    return this.zzacr.zzfu().getUserProperties(paramString1, paramString2, paramBoolean);
  }
  
  public Map<String, Object> getUserProperties(boolean paramBoolean)
  {
    Object localObject = this.zzacr.zzfu().zzk(paramBoolean);
    ArrayMap localArrayMap = new ArrayMap(((List)localObject).size());
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      zzjs localzzjs = (zzjs)((Iterator)localObject).next();
      localArrayMap.put(localzzjs.name, localzzjs.getValue());
    }
    return localArrayMap;
  }
  
  @Keep
  protected Map<String, Object> getUserPropertiesAs(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    return this.zzacr.zzfu().getUserPropertiesAs(paramString1, paramString2, paramString3, paramBoolean);
  }
  
  public final void logEvent(String paramString, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    this.zzacr.zzfu().zza("app", paramString, localBundle, true);
  }
  
  @Keep
  public void logEventInternal(String paramString1, String paramString2, Bundle paramBundle)
  {
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    this.zzacr.zzfu().zza(paramString1, paramString2, localBundle);
  }
  
  public void logEventInternalNoInterceptor(String paramString1, String paramString2, Bundle paramBundle, long paramLong)
  {
    if (paramBundle == null) {
      paramBundle = new Bundle();
    }
    for (;;)
    {
      this.zzacr.zzfu().zza(paramString1, paramString2, paramBundle, paramLong);
      return;
    }
  }
  
  public void registerOnMeasurementEventListener(OnEventListener paramOnEventListener)
  {
    this.zzacr.zzfu().registerOnMeasurementEventListener(paramOnEventListener);
  }
  
  @Keep
  public void registerOnScreenChangeCallback(zza paramzza)
  {
    this.zzacr.zzfy().registerOnScreenChangeCallback(paramzza);
  }
  
  @Keep
  public void setConditionalUserProperty(ConditionalUserProperty paramConditionalUserProperty)
  {
    this.zzacr.zzfu().setConditionalUserProperty(paramConditionalUserProperty);
  }
  
  @Keep
  protected void setConditionalUserPropertyAs(ConditionalUserProperty paramConditionalUserProperty)
  {
    this.zzacr.zzfu().setConditionalUserPropertyAs(paramConditionalUserProperty);
  }
  
  public void setEventInterceptor(EventInterceptor paramEventInterceptor)
  {
    this.zzacr.zzfu().setEventInterceptor(paramEventInterceptor);
  }
  
  @Deprecated
  public void setMeasurementEnabled(boolean paramBoolean)
  {
    this.zzacr.zzfu().setMeasurementEnabled(paramBoolean);
  }
  
  public final void setMinimumSessionDuration(long paramLong)
  {
    this.zzacr.zzfu().setMinimumSessionDuration(paramLong);
  }
  
  public final void setSessionTimeoutDuration(long paramLong)
  {
    this.zzacr.zzfu().setSessionTimeoutDuration(paramLong);
  }
  
  public final void setUserProperty(String paramString1, String paramString2)
  {
    int i = this.zzacr.zzgc().zzbx(paramString1);
    int j;
    if (i != 0)
    {
      this.zzacr.zzgc();
      paramString2 = zzjv.zza(paramString1, 24, true);
      if (paramString1 != null)
      {
        j = paramString1.length();
        this.zzacr.zzgc().zza(i, "_ev", paramString2, j);
      }
    }
    for (;;)
    {
      return;
      j = 0;
      break;
      setUserPropertyInternal("app", paramString1, paramString2);
    }
  }
  
  public void setUserPropertyInternal(String paramString1, String paramString2, Object paramObject)
  {
    this.zzacr.zzfu().zza(paramString1, paramString2, paramObject);
  }
  
  public void unregisterOnMeasurementEventListener(OnEventListener paramOnEventListener)
  {
    this.zzacr.zzfu().unregisterOnMeasurementEventListener(paramOnEventListener);
  }
  
  @Keep
  public void unregisterOnScreenChangeCallback(zza paramzza)
  {
    this.zzacr.zzfy().unregisterOnScreenChangeCallback(paramzza);
  }
  
  public static class ConditionalUserProperty
  {
    @Keep
    public boolean mActive;
    @Keep
    public String mAppId;
    @Keep
    public long mCreationTimestamp;
    @Keep
    public String mExpiredEventName;
    @Keep
    public Bundle mExpiredEventParams;
    @Keep
    public String mName;
    @Keep
    public String mOrigin;
    @Keep
    public long mTimeToLive;
    @Keep
    public String mTimedOutEventName;
    @Keep
    public Bundle mTimedOutEventParams;
    @Keep
    public String mTriggerEventName;
    @Keep
    public long mTriggerTimeout;
    @Keep
    public String mTriggeredEventName;
    @Keep
    public Bundle mTriggeredEventParams;
    @Keep
    public long mTriggeredTimestamp;
    @Keep
    public Object mValue;
    
    public ConditionalUserProperty() {}
    
    public ConditionalUserProperty(ConditionalUserProperty paramConditionalUserProperty)
    {
      Preconditions.checkNotNull(paramConditionalUserProperty);
      this.mAppId = paramConditionalUserProperty.mAppId;
      this.mOrigin = paramConditionalUserProperty.mOrigin;
      this.mCreationTimestamp = paramConditionalUserProperty.mCreationTimestamp;
      this.mName = paramConditionalUserProperty.mName;
      if (paramConditionalUserProperty.mValue != null)
      {
        this.mValue = zzjv.zzf(paramConditionalUserProperty.mValue);
        if (this.mValue == null) {
          this.mValue = paramConditionalUserProperty.mValue;
        }
      }
      this.mValue = paramConditionalUserProperty.mValue;
      this.mActive = paramConditionalUserProperty.mActive;
      this.mTriggerEventName = paramConditionalUserProperty.mTriggerEventName;
      this.mTriggerTimeout = paramConditionalUserProperty.mTriggerTimeout;
      this.mTimedOutEventName = paramConditionalUserProperty.mTimedOutEventName;
      if (paramConditionalUserProperty.mTimedOutEventParams != null) {
        this.mTimedOutEventParams = new Bundle(paramConditionalUserProperty.mTimedOutEventParams);
      }
      this.mTriggeredEventName = paramConditionalUserProperty.mTriggeredEventName;
      if (paramConditionalUserProperty.mTriggeredEventParams != null) {
        this.mTriggeredEventParams = new Bundle(paramConditionalUserProperty.mTriggeredEventParams);
      }
      this.mTriggeredTimestamp = paramConditionalUserProperty.mTriggeredTimestamp;
      this.mTimeToLive = paramConditionalUserProperty.mTimeToLive;
      this.mExpiredEventName = paramConditionalUserProperty.mExpiredEventName;
      if (paramConditionalUserProperty.mExpiredEventParams != null) {
        this.mExpiredEventParams = new Bundle(paramConditionalUserProperty.mExpiredEventParams);
      }
    }
  }
  
  public static final class Event
    extends FirebaseAnalytics.Event
  {
    public static final String[] zzacs = { "app_clear_data", "app_exception", "app_remove", "app_upgrade", "app_install", "app_update", "firebase_campaign", "error", "first_open", "first_visit", "in_app_purchase", "notification_dismiss", "notification_foreground", "notification_open", "notification_receive", "os_update", "session_start", "user_engagement", "ad_exposure", "adunit_exposure", "ad_query", "ad_activeview", "ad_impression", "ad_click", "ad_reward", "screen_view", "ga_extra_parameter" };
    public static final String[] zzact = { "_cd", "_ae", "_ui", "_ug", "_in", "_au", "_cmp", "_err", "_f", "_v", "_iap", "_nd", "_nf", "_no", "_nr", "_ou", "_s", "_e", "_xa", "_xu", "_aq", "_aa", "_ai", "_ac", "_ar", "_vs", "_ep" };
    
    public static String zzak(String paramString)
    {
      return zzjv.zza(paramString, zzacs, zzact);
    }
  }
  
  public static abstract interface EventInterceptor
  {
    public abstract void interceptEvent(String paramString1, String paramString2, Bundle paramBundle, long paramLong);
  }
  
  public static abstract interface OnEventListener
  {
    public abstract void onEvent(String paramString1, String paramString2, Bundle paramBundle, long paramLong);
  }
  
  public static final class Param
    extends FirebaseAnalytics.Param
  {
    public static final String[] zzacu = { "firebase_conversion", "engagement_time_msec", "exposure_time", "ad_event_id", "ad_unit_id", "firebase_error", "firebase_error_value", "firebase_error_length", "firebase_event_origin", "firebase_screen", "firebase_screen_class", "firebase_screen_id", "firebase_previous_screen", "firebase_previous_class", "firebase_previous_id", "message_device_time", "message_id", "message_name", "message_time", "previous_app_version", "previous_os_version", "topic", "update_with_analytics", "previous_first_open_count", "system_app", "system_app_update", "previous_install_count", "ga_event_id", "ga_extra_params_ct", "ga_group_name", "ga_list_length", "ga_index", "ga_event_name", "campaign_info_source", "deferred_analytics_collection" };
    public static final String[] zzacv = { "_c", "_et", "_xt", "_aeid", "_ai", "_err", "_ev", "_el", "_o", "_sn", "_sc", "_si", "_pn", "_pc", "_pi", "_ndt", "_nmid", "_nmn", "_nmt", "_pv", "_po", "_nt", "_uwa", "_pfo", "_sys", "_sysu", "_pin", "_eid", "_epc", "_gn", "_ll", "_i", "_en", "_cis", "_dac" };
    
    public static String zzak(String paramString)
    {
      return zzjv.zza(paramString, zzacu, zzacv);
    }
  }
  
  public static final class UserProperty
    extends FirebaseAnalytics.UserProperty
  {
    public static final String[] zzacw = { "firebase_last_notification", "first_open_time", "first_visit_time", "last_deep_link_referrer", "user_id", "first_open_after_install", "lifetime_user_engagement" };
    public static final String[] zzacx = { "_ln", "_fot", "_fvt", "_ldl", "_id", "_fi", "_lte" };
    
    public static String zzak(String paramString)
    {
      return zzjv.zza(paramString, zzacw, zzacx);
    }
  }
  
  public static abstract interface zza
  {
    public abstract boolean zza(zzig paramzzig1, zzig paramzzig2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */