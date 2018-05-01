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

public final class zzdb
  extends Fragment
  implements zzcf
{
  private static WeakHashMap<FragmentActivity, WeakReference<zzdb>> zzfue = new WeakHashMap();
  private int zzcbc = 0;
  private Map<String, LifecycleCallback> zzfuf = new ArrayMap();
  private Bundle zzfug;
  
  public static zzdb zza(FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)zzfue.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (zzdb)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzdb)localObject;
      }
    }
    try
    {
      zzdb localzzdb = (zzdb)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
      if (localzzdb != null)
      {
        localObject = localzzdb;
        if (!localzzdb.isRemoving()) {}
      }
      else
      {
        localObject = new zzdb();
        paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
      }
      zzfue.put(paramFragmentActivity, new WeakReference(localObject));
      return (zzdb)localObject;
    }
    catch (ClassCastException paramFragmentActivity)
    {
      throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
    }
  }
  
  public final <T extends LifecycleCallback> T zza(String paramString, Class<T> paramClass)
  {
    return (LifecycleCallback)paramClass.cast(this.zzfuf.get(paramString));
  }
  
  public final void zza(String paramString, LifecycleCallback paramLifecycleCallback)
  {
    if (!this.zzfuf.containsKey(paramString))
    {
      this.zzfuf.put(paramString, paramLifecycleCallback);
      if (this.zzcbc > 0) {
        new Handler(Looper.getMainLooper()).post(new zzdc(this, paramLifecycleCallback, paramString));
      }
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */