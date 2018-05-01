package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public final class zzcc
  extends Fragment
  implements LifecycleFragment
{
  private static WeakHashMap<FragmentActivity, WeakReference<zzcc>> zzla = new WeakHashMap();
  private Map<String, LifecycleCallback> zzlb = new ArrayMap();
  private int zzlc = 0;
  private Bundle zzld;
  
  public static zzcc zza(FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)zzla.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (zzcc)((WeakReference)localObject).get();
      if (localObject == null) {}
    }
    for (;;)
    {
      return (zzcc)localObject;
      try
      {
        zzcc localzzcc = (zzcc)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
        if (localzzcc != null)
        {
          localObject = localzzcc;
          if (!localzzcc.isRemoving()) {}
        }
        else
        {
          localObject = new zzcc();
          paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
        }
        zzla.put(paramFragmentActivity, new WeakReference(localObject));
      }
      catch (ClassCastException paramFragmentActivity)
      {
        throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
      }
    }
  }
  
  public final void addCallback(String paramString, LifecycleCallback paramLifecycleCallback)
  {
    if (!this.zzlb.containsKey(paramString))
    {
      this.zzlb.put(paramString, paramLifecycleCallback);
      if (this.zzlc > 0) {
        new Handler(Looper.getMainLooper()).post(new zzcd(this, paramLifecycleCallback, paramString));
      }
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
  
  public final <T extends LifecycleCallback> T getCallbackOrNull(String paramString, Class<T> paramClass)
  {
    return (LifecycleCallback)paramClass.cast(this.zzlb.get(paramString));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */