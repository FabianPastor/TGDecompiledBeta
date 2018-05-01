package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(14)
public class zzamz
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final zzamz bhF = new zzamz();
  private boolean aJ;
  private final AtomicBoolean bhG = new AtomicBoolean();
  
  public static zzamz L()
  {
    return bhF;
  }
  
  public static void zza(Application paramApplication)
  {
    paramApplication.registerActivityLifecycleCallbacks(bhF);
    paramApplication.registerComponentCallbacks(bhF);
    bhF.aJ = true;
  }
  
  public boolean M()
  {
    return this.bhG.get();
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    if (this.bhG.compareAndSet(true, false)) {
      FirebaseApp.zzcp(false);
    }
  }
  
  public void onActivityDestroyed(Activity paramActivity) {}
  
  public void onActivityPaused(Activity paramActivity) {}
  
  public void onActivityResumed(Activity paramActivity)
  {
    if (this.bhG.compareAndSet(true, false)) {
      FirebaseApp.zzcp(false);
    }
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity) {}
  
  public void onActivityStopped(Activity paramActivity) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory() {}
  
  public void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (this.bhG.compareAndSet(false, true))) {
      FirebaseApp.zzcp(true);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzamz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */