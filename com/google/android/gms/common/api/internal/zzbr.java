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

public final class zzbr
  extends Fragment
  implements LifecycleFragment
{
  private static WeakHashMap<Activity, WeakReference<zzbr>> zzla = new WeakHashMap();
  private Map<String, LifecycleCallback> zzlb = new ArrayMap();
  private int zzlc = 0;
  private Bundle zzld;
  
  public static zzbr zzc(Activity paramActivity)
  {
    Object localObject = (WeakReference)zzla.get(paramActivity);
    if (localObject != null)
    {
      localObject = (zzbr)((WeakReference)localObject).get();
      if (localObject == null) {}
    }
    for (;;)
    {
      return (zzbr)localObject;
      try
      {
        zzbr localzzbr = (zzbr)paramActivity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
        if (localzzbr != null)
        {
          localObject = localzzbr;
          if (!localzzbr.isRemoving()) {}
        }
        else
        {
          localObject = new zzbr();
          paramActivity.getFragmentManager().beginTransaction().add((Fragment)localObject, "LifecycleFragmentImpl").commitAllowingStateLoss();
        }
        zzla.put(paramActivity, new WeakReference(localObject));
      }
      catch (ClassCastException paramActivity)
      {
        throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", paramActivity);
      }
    }
  }
  
  public final void addCallback(String paramString, LifecycleCallback paramLifecycleCallback)
  {
    if (!this.zzlb.containsKey(paramString))
    {
      this.zzlb.put(paramString, paramLifecycleCallback);
      if (this.zzlc > 0) {
        new Handler(Looper.getMainLooper()).post(new zzbs(this, paramLifecycleCallback, paramString));
      }
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    Iterator localIterator = this.zzlb.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final <T extends LifecycleCallback> T getCallbackOrNull(String paramString, Class<T> paramClass)
  {
    return (LifecycleCallback)paramClass.cast(this.zzlb.get(paramString));
  }
  
  public final Activity getLifecycleActivity()
  {
    return getActivity();
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = this.zzlb.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzlc = 1;
    this.zzld = paramBundle;
    Iterator localIterator = this.zzlb.entrySet().iterator();
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
    this.zzlc = 5;
    Iterator localIterator = this.zzlb.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onDestroy();
    }
  }
  
  public final void onResume()
  {
    super.onResume();
    this.zzlc = 3;
    Iterator localIterator = this.zzlb.values().iterator();
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
      Iterator localIterator = this.zzlb.entrySet().iterator();
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
    this.zzlc = 2;
    Iterator localIterator = this.zzlb.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onStart();
    }
  }
  
  public final void onStop()
  {
    super.onStop();
    this.zzlc = 4;
    Iterator localIterator = this.zzlb.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onStop();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */