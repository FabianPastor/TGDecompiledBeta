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
public class zzanq
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final zzanq bkR = new zzanq();
  private final AtomicBoolean bkS = new AtomicBoolean();
  private boolean cR;
  
  public static zzanq N()
  {
    return bkR;
  }
  
  public static void zza(Application paramApplication)
  {
    paramApplication.registerActivityLifecycleCallbacks(bkR);
    paramApplication.registerComponentCallbacks(bkR);
    bkR.cR = true;
  }
  
  public boolean O()
  {
    return this.bkS.get();
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    if (this.bkS.compareAndSet(true, false)) {
      FirebaseApp.zzcr(false);
    }
  }
  
  public void onActivityDestroyed(Activity paramActivity) {}
  
  public void onActivityPaused(Activity paramActivity) {}
  
  public void onActivityResumed(Activity paramActivity)
  {
    if (this.bkS.compareAndSet(true, false)) {
      FirebaseApp.zzcr(false);
    }
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity) {}
  
  public void onActivityStopped(Activity paramActivity) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory() {}
  
  public void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (this.bkS.compareAndSet(false, true))) {
      FirebaseApp.zzcr(true);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzanq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */