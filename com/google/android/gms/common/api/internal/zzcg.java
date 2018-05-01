package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzcg
  extends Fragment
  implements zzcf
{
  private static WeakHashMap<Activity, WeakReference<zzcg>> zzfue = new WeakHashMap();
  private int zzcbc = 0;
  private Map<String, LifecycleCallback> zzfuf = new ArrayMap();
  private Bundle zzfug;
  
  public static zzcg zzo(Activity paramActivity)
  {
    Object localObject = (WeakReference)zzfue.get(paramActivity);
    if (localObject != null)
    {
      localObject = (zzcg)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzcg)localObject;
      }
    }
    try
    {
      zzcg localzzcg = (zzcg)paramActivity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
      if (localzzcg != null)
      {
        localObject = localzzcg;
        if (!localzzcg.isRemoving()) {}
      }
      else
      {
        localObject = new zzcg();
        paramActivity.getFragmentManager().beginTransaction().add((Fragment)localObject, "LifecycleFragmentImpl").commitAllowingStateLoss();
      }
      zzfue.put(paramActivity, new WeakReference(localObject));
      return (zzcg)localObject;
    }
    catch (ClassCastException paramActivity)
    {
      throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", paramActivity);
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    Iterator localIterator = this.zzfuf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = this.zzfuf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzcbc = 1;
    this.zzfug = paramBundle;
    Iterator localIterator = this.zzfuf.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      LifecycleCallback localLifecycleCallback = (LifecycleCallback)((Map.Entry)localObject).getValue();
      if (paramBundle != null) {}
      for (localObject = paramBundle.getBundle((String)((Map.Entry)localObject).getKey());; localObject = null)
      {
        localLifecycleCallback.onCreate((Bundle)localObject);
        break;
      }
    }
  }
  
  public final void onDestroy()
  {
    super.onDestroy();
    this.zzcbc = 5;
    Iterator localIterator = this.zzfuf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onDestroy();
    }
  }
  
  public final void onResume()
  {
    super.onResume();
    this.zzcbc = 3;
    Iterator localIterator = this.zzfuf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onResume();
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (paramBundle == null) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.zzfuf.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Bundle localBundle = new Bundle();
        ((LifecycleCallback)localEntry.getValue()).onSaveInstanceState(localBundle);
        paramBundle.putBundle((String)localEntry.getKey(), localBundle);
      }
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    this.zzcbc = 2;
    Iterator localIterator = this.zzfuf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onStart();
    }
  }
  
  public final void onStop()
  {
    super.onStop();
    this.zzcbc = 4;
    Iterator localIterator = this.zzfuf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onStop();
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
        new Handler(Looper.getMainLooper()).post(new zzch(this, paramLifecycleCallback, paramString));
      }
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
  
  public final Activity zzajn()
  {
    return getActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */