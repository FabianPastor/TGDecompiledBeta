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
import com.google.android.gms.common.util.zzq;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public final class zzbaw
  implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2
{
  private static final zzbaw zzaBJ = new zzbaw();
  private final ArrayList<zzbax> mListeners = new ArrayList();
  private final AtomicBoolean zzaBK = new AtomicBoolean();
  private final AtomicBoolean zzaBL = new AtomicBoolean();
  private boolean zzafK = false;
  
  public static void zza(Application paramApplication)
  {
    synchronized (zzaBJ)
    {
      if (!zzaBJ.zzafK)
      {
        paramApplication.registerActivityLifecycleCallbacks(zzaBJ);
        paramApplication.registerComponentCallbacks(zzaBJ);
        zzaBJ.zzafK = true;
      }
      return;
    }
  }
  
  private final void zzac(boolean paramBoolean)
  {
    synchronized (zzaBJ)
    {
      ArrayList localArrayList = (ArrayList)this.mListeners;
      int j = localArrayList.size();
      int i = 0;
      if (i < j)
      {
        Object localObject2 = localArrayList.get(i);
        i += 1;
        ((zzbax)localObject2).zzac(paramBoolean);
      }
    }
  }
  
  public static zzbaw zzpv()
  {
    return zzaBJ;
  }
  
  public final void onActivityCreated(Activity paramActivity, Bundle paramBundle)
  {
    boolean bool = this.zzaBK.compareAndSet(true, false);
    this.zzaBL.set(true);
    if (bool) {
      zzac(false);
    }
  }
  
  public final void onActivityDestroyed(Activity paramActivity) {}
  
  public final void onActivityPaused(Activity paramActivity) {}
  
  public final void onActivityResumed(Activity paramActivity)
  {
    boolean bool = this.zzaBK.compareAndSet(true, false);
    this.zzaBL.set(true);
    if (bool) {
      zzac(false);
    }
  }
  
  public final void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
  
  public final void onActivityStarted(Activity paramActivity) {}
  
  public final void onActivityStopped(Activity paramActivity) {}
  
  public final void onConfigurationChanged(Configuration paramConfiguration) {}
  
  public final void onLowMemory() {}
  
  public final void onTrimMemory(int paramInt)
  {
    if ((paramInt == 20) && (this.zzaBK.compareAndSet(false, true)))
    {
      this.zzaBL.set(true);
      zzac(true);
    }
  }
  
  public final void zza(zzbax paramzzbax)
  {
    synchronized (zzaBJ)
    {
      this.mListeners.add(paramzzbax);
      return;
    }
  }
  
  @TargetApi(16)
  public final boolean zzab(boolean paramBoolean)
  {
    paramBoolean = true;
    if (!this.zzaBL.get())
    {
      if (!zzq.zzrZ()) {
        return paramBoolean;
      }
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
      ActivityManager.getMyMemoryState(localRunningAppProcessInfo);
      if ((!this.zzaBL.getAndSet(true)) && (localRunningAppProcessInfo.importance > 100)) {
        this.zzaBK.set(true);
      }
    }
    paramBoolean = this.zzaBK.get();
    return paramBoolean;
  }
  
  public final boolean zzpw()
  {
    return this.zzaBK.get();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbaw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */