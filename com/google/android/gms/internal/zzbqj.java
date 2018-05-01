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
public class zzbqj
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final zzbqj zzcjk = new zzbqj();
  private boolean zzacO;
  private final AtomicBoolean zzcjl = new AtomicBoolean();
  
  public static void zza(Application paramApplication)
  {
    paramApplication.registerActivityLifecycleCallbacks(zzcjk);
    paramApplication.registerComponentCallbacks(zzcjk);
    zzcjk.zzacO = true;
  }
  
  public static zzbqj zzaan()
  {
    return zzcjk;
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    if (this.zzcjl.compareAndSet(true, false)) {
      FirebaseApp.zzaQ(false);
    }
  }
  
  public void onActivityDestroyed(Activity paramActivity) {}
  
  public void onActivityPaused(Activity paramActivity) {}
  
  public void onActivityResumed(Activity paramActivity)
  {
    if (this.zzcjl.compareAndSet(true, false)) {
      FirebaseApp.zzaQ(false);
    }
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity) {}
  
  public void onActivityStopped(Activity paramActivity) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory() {}
  
  public void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (this.zzcjl.compareAndSet(false, true))) {
      FirebaseApp.zzaQ(true);
    }
  }
  
  public boolean zzaao()
  {
    return this.zzcjl.get();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbqj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */