package com.google.firebase.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.measurement.zzgl;
import com.google.android.gms.internal.measurement.zzhm;
import com.google.android.gms.internal.measurement.zzih;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.tasks.Task;

@Keep
public final class FirebaseAnalytics
{
  private final zzgl zzacr;
  
  public FirebaseAnalytics(zzgl paramzzgl)
  {
    Preconditions.checkNotNull(paramzzgl);
    this.zzacr = paramzzgl;
  }
  
  @Keep
  public static FirebaseAnalytics getInstance(Context paramContext)
  {
    return zzgl.zzg(paramContext).zzjp();
  }
  
  public final Task<String> getAppInstanceId()
  {
    return this.zzacr.zzfu().getAppInstanceId();
  }
  
  public final void logEvent(String paramString, Bundle paramBundle)
  {
    this.zzacr.zzjo().logEvent(paramString, paramBundle);
  }
  
  public final void resetAnalyticsData()
  {
    this.zzacr.zzfu().resetAnalyticsData();
  }
  
  public final void setAnalyticsCollectionEnabled(boolean paramBoolean)
  {
    this.zzacr.zzjo().setMeasurementEnabled(paramBoolean);
  }
  
  @Keep
  public final void setCurrentScreen(Activity paramActivity, String paramString1, String paramString2)
  {
    this.zzacr.zzfy().setCurrentScreen(paramActivity, paramString1, paramString2);
  }
  
  public final void setMinimumSessionDuration(long paramLong)
  {
    this.zzacr.zzjo().setMinimumSessionDuration(paramLong);
  }
  
  public final void setSessionTimeoutDuration(long paramLong)
  {
    this.zzacr.zzjo().setSessionTimeoutDuration(paramLong);
  }
  
  public final void setUserId(String paramString)
  {
    this.zzacr.zzjo().setUserPropertyInternal("app", "_id", paramString);
  }
  
  public final void setUserProperty(String paramString1, String paramString2)
  {
    this.zzacr.zzjo().setUserProperty(paramString1, paramString2);
  }
  
  public static class Event {}
  
  public static class Param {}
  
  public static class UserProperty {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/analytics/FirebaseAnalytics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */