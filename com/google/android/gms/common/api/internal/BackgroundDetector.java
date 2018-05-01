package com.google.android.gms.common.api.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.gms.common.util.PlatformVersion;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.concurrent.GuardedBy;

public final class BackgroundDetector
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final BackgroundDetector zzem = new BackgroundDetector();
  private final AtomicBoolean zzen = new AtomicBoolean();
  private final AtomicBoolean zzeo = new AtomicBoolean();
  @GuardedBy("sInstance")
  private final ArrayList<BackgroundStateChangeListener> zzep = new ArrayList();
  @GuardedBy("sInstance")
  private boolean zzeq = false;
  
  public static BackgroundDetector getInstance()
  {
    return zzem;
  }
  
  public static void initialize(Application paramApplication)
  {
    synchronized (zzem)
    {
      if (!zzem.zzeq)
      {
        paramApplication.registerActivityLifecycleCallbacks(zzem);
        paramApplication.registerComponentCallbacks(zzem);
        zzem.zzeq = true;
      }
      return;
    }
  }
  
  private final void onBackgroundStateChanged(boolean paramBoolean)
  {
    synchronized (zzem)
    {
      ArrayList localArrayList = (ArrayList)this.zzep;
      int i = localArrayList.size();
      int j = 0;
      if (j < i)
      {
        Object localObject2 = localArrayList.get(j);
        j++;
        ((BackgroundStateChangeListener)localObject2).onBackgroundStateChanged(paramBoolean);
      }
    }
  }
  
  public final void addListener(BackgroundStateChangeListener paramBackgroundStateChangeListener)
  {
    synchronized (zzem)
    {
      this.zzep.add(paramBackgroundStateChangeListener);
      return;
    }
  }
  
  public final boolean isInBackground()
  {
    return this.zzen.get();
  }
  
  public final void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    boolean bool = this.zzen.compareAndSet(true, false);
    this.zzeo.set(true);
    if (bool) {
      onBackgroundStateChanged(false);
    }
  }
  
  public final void onActivityDestroyed(Activity paramActivity) {}
  
  public final void onActivityPaused(Activity paramActivity) {}
  
  public final void onActivityResumed(Activity paramActivity)
  {
    boolean bool = this.zzen.compareAndSet(true, false);
    this.zzeo.set(true);
    if (bool) {
      onBackgroundStateChanged(false);
    }
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public final void onActivityStarted(Activity paramActivity) {}
  
  public final void onActivityStopped(Activity paramActivity) {}
  
  public final void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public final void onLowMemory() {}
  
  public final void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (this.zzen.compareAndSet(false, true)))
    {
      this.zzeo.set(true);
      onBackgroundStateChanged(true);
    }
  }
  
  @TargetApi(16)
  public final boolean readCurrentStateIfPossible(boolean paramBoolean)
  {
    if (!this.zzeo.get())
    {
      if (!PlatformVersion.isAtLeastJellyBean()) {
        return paramBoolean;
      }
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
      ActivityManager.getMyMemoryState(localRunningAppProcessInfo);
      if ((!this.zzeo.getAndSet(true)) && (localRunningAppProcessInfo.importance > 100)) {
        this.zzen.set(true);
      }
    }
    paramBoolean = isInBackground();
    return paramBoolean;
  }
  
  public static abstract interface BackgroundStateChangeListener
  {
    public abstract void onBackgroundStateChanged(boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/BackgroundDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */