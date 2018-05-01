package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public final class zzbeo
  extends Fragment
  implements zzbdt
{
  private static WeakHashMap<FragmentActivity, WeakReference<zzbeo>> zzaEH = new WeakHashMap();
  private int zzLe = 0;
  private Map<String, zzbds> zzaEI = new ArrayMap();
  private Bundle zzaEJ;
  
  public static zzbeo zza(FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)zzaEH.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (zzbeo)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzbeo)localObject;
      }
    }
    try
    {
      zzbeo localzzbeo = (zzbeo)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
      if (localzzbeo != null)
      {
        localObject = localzzbeo;
        if (!localzzbeo.isRemoving()) {}
      }
      else
      {
        localObject = new zzbeo();
        paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
      }
      zzaEH.put(paramFragmentActivity, new WeakReference(localObject));
      return (zzbeo)localObject;
    }
    catch (ClassCastException paramFragmentActivity)
    {
      throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    Iterator localIterator = this.zzaEI.values().iterator();
    while (localIterator.hasNext()) {
      ((zzbds)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = this.zzaEI.values().iterator();
    while (localIterator.hasNext()) {
      ((zzbds)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzLe = 1;
    this.zzaEJ = paramBundle;
    Iterator localIterator = this.zzaEI.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      zzbds localzzbds = (zzbds)((Map.Entry)localObject).getValue();
      if (paramBundle != null) {}
      for (localObject = paramBundle.getBundle((String)((Map.Entry)localObject).getKey());; localObject = null)
      {
        localzzbds.onCreate((Bundle)localObject);
        break;
      }
    }
  }
  
  public final void onDestroy()
  {
    super.onDestroy();
    this.zzLe = 5;
    Iterator localIterator = this.zzaEI.values().iterator();
    while (localIterator.hasNext()) {
      ((zzbds)localIterator.next()).onDestroy();
    }
  }
  
  public final void onResume()
  {
    super.onResume();
    this.zzLe = 3;
    Iterator localIterator = this.zzaEI.values().iterator();
    while (localIterator.hasNext()) {
      ((zzbds)localIterator.next()).onResume();
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (paramBundle == null) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.zzaEI.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Bundle localBundle = new Bundle();
        ((zzbds)localEntry.getValue()).onSaveInstanceState(localBundle);
        paramBundle.putBundle((String)localEntry.getKey(), localBundle);
      }
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    this.zzLe = 2;
    Iterator localIterator = this.zzaEI.values().iterator();
    while (localIterator.hasNext()) {
      ((zzbds)localIterator.next()).onStart();
    }
  }
  
  public final void onStop()
  {
    super.onStop();
    this.zzLe = 4;
    Iterator localIterator = this.zzaEI.values().iterator();
    while (localIterator.hasNext()) {
      ((zzbds)localIterator.next()).onStop();
    }
  }
  
  public final <T extends zzbds> T zza(String paramString, Class<T> paramClass)
  {
    return (zzbds)paramClass.cast(this.zzaEI.get(paramString));
  }
  
  public final void zza(String paramString, @NonNull zzbds paramzzbds)
  {
    if (!this.zzaEI.containsKey(paramString))
    {
      this.zzaEI.put(paramString, paramzzbds);
      if (this.zzLe > 0) {
        new Handler(Looper.getMainLooper()).post(new zzbep(this, paramzzbds, paramString));
      }
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbeo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */