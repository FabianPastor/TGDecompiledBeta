package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.gms.common.util.zzt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public final class zzaac
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final zzaac zzazV = new zzaac();
  private final ArrayList<zza> mListeners = new ArrayList();
  private boolean zzadP = false;
  private final AtomicBoolean zzazW = new AtomicBoolean();
  private final AtomicBoolean zzazX = new AtomicBoolean();
  
  public static void zza(Application paramApplication)
  {
    synchronized (zzazV)
    {
      if (!zzazV.zzadP)
      {
        paramApplication.registerActivityLifecycleCallbacks(zzazV);
        paramApplication.registerComponentCallbacks(zzazV);
        zzazV.zzadP = true;
      }
      return;
    }
  }
  
  private void zzas(boolean paramBoolean)
  {
    synchronized (zzazV)
    {
      Iterator localIterator = this.mListeners.iterator();
      if (localIterator.hasNext()) {
        ((zza)localIterator.next()).zzas(paramBoolean);
      }
    }
  }
  
  public static zzaac zzvB()
  {
    return zzazV;
  }
  
  public void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    boolean bool = this.zzazW.compareAndSet(true, false);
    this.zzazX.set(true);
    if (bool) {
      zzas(false);
    }
  }
  
  public void onActivityDestroyed(Activity paramActivity) {}
  
  public void onActivityPaused(Activity paramActivity) {}
  
  public void onActivityResumed(Activity paramActivity)
  {
    boolean bool = this.zzazW.compareAndSet(true, false);
    this.zzazX.set(true);
    if (bool) {
      zzas(false);
    }
  }
  
  public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public void onActivityStarted(Activity paramActivity) {}
  
  public void onActivityStopped(Activity paramActivity) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public void onLowMemory() {}
  
  public void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (this.zzazW.compareAndSet(false, true)))
    {
      this.zzazX.set(true);
      zzas(true);
    }
  }
  
  public void zza(zza paramzza)
  {
    synchronized (zzazV)
    {
      this.mListeners.add(paramzza);
      return;
    }
  }
  
  @TargetApi(16)
  public boolean zzar(boolean paramBoolean)
  {
    if (!this.zzazX.get())
    {
      if (!zzt.zzzi()) {
        return paramBoolean;
      }
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
      ActivityManager.getMyMemoryState(localRunningAppProcessInfo);
      if ((!this.zzazX.getAndSet(true)) && (localRunningAppProcessInfo.importance > 100)) {
        this.zzazW.set(true);
      }
    }
    paramBoolean = zzvC();
    return paramBoolean;
  }
  
  public boolean zzvC()
  {
    return this.zzazW.get();
  }
  
  public static abstract interface zza
  {
    public abstract void zzas(boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */