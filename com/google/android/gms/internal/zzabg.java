package com.google.android.gms.internal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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

public final class zzabg
  extends Fragment
  implements zzabf
{
  private static WeakHashMap<Activity, WeakReference<zzabg>> zzaCS = new WeakHashMap();
  private int zzJO = 0;
  private Map<String, zzabe> zzaCT = new ArrayMap();
  private Bundle zzaCU;
  
  private void zzb(final String paramString, @NonNull final zzabe paramzzabe)
  {
    if (this.zzJO > 0) {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          zzabe localzzabe;
          if (zzabg.zza(zzabg.this) >= 1)
          {
            localzzabe = paramzzabe;
            if (zzabg.zzb(zzabg.this) == null) {
              break label101;
            }
          }
          label101:
          for (Bundle localBundle = zzabg.zzb(zzabg.this).getBundle(paramString);; localBundle = null)
          {
            localzzabe.onCreate(localBundle);
            if (zzabg.zza(zzabg.this) >= 2) {
              paramzzabe.onStart();
            }
            if (zzabg.zza(zzabg.this) >= 3) {
              paramzzabe.onStop();
            }
            if (zzabg.zza(zzabg.this) >= 4) {
              paramzzabe.onDestroy();
            }
            return;
          }
        }
      });
    }
  }
  
  public static zzabg zzt(Activity paramActivity)
  {
    Object localObject = (WeakReference)zzaCS.get(paramActivity);
    if (localObject != null)
    {
      localObject = (zzabg)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzabg)localObject;
      }
    }
    try
    {
      zzabg localzzabg = (zzabg)paramActivity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
      if (localzzabg != null)
      {
        localObject = localzzabg;
        if (!localzzabg.isRemoving()) {}
      }
      else
      {
        localObject = new zzabg();
        paramActivity.getFragmentManager().beginTransaction().add((Fragment)localObject, "LifecycleFragmentImpl").commitAllowingStateLoss();
      }
      zzaCS.put(paramActivity, new WeakReference(localObject));
      return (zzabg)localObject;
    }
    catch (ClassCastException paramActivity)
    {
      throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", paramActivity);
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    Iterator localIterator = this.zzaCT.values().iterator();
    while (localIterator.hasNext()) {
      ((zzabe)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = this.zzaCT.values().iterator();
    while (localIterator.hasNext()) {
      ((zzabe)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzJO = 1;
    this.zzaCU = paramBundle;
    Iterator localIterator = this.zzaCT.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      zzabe localzzabe = (zzabe)((Map.Entry)localObject).getValue();
      if (paramBundle != null) {}
      for (localObject = paramBundle.getBundle((String)((Map.Entry)localObject).getKey());; localObject = null)
      {
        localzzabe.onCreate((Bundle)localObject);
        break;
      }
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.zzJO = 4;
    Iterator localIterator = this.zzaCT.values().iterator();
    while (localIterator.hasNext()) {
      ((zzabe)localIterator.next()).onDestroy();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (paramBundle == null) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.zzaCT.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Bundle localBundle = new Bundle();
        ((zzabe)localEntry.getValue()).onSaveInstanceState(localBundle);
        paramBundle.putBundle((String)localEntry.getKey(), localBundle);
      }
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.zzJO = 2;
    Iterator localIterator = this.zzaCT.values().iterator();
    while (localIterator.hasNext()) {
      ((zzabe)localIterator.next()).onStart();
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.zzJO = 3;
    Iterator localIterator = this.zzaCT.values().iterator();
    while (localIterator.hasNext()) {
      ((zzabe)localIterator.next()).onStop();
    }
  }
  
  public <T extends zzabe> T zza(String paramString, Class<T> paramClass)
  {
    return (zzabe)paramClass.cast(this.zzaCT.get(paramString));
  }
  
  public void zza(String paramString, @NonNull zzabe paramzzabe)
  {
    if (!this.zzaCT.containsKey(paramString))
    {
      this.zzaCT.put(paramString, paramzzabe);
      zzb(paramString, paramzzabe);
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
  
  public Activity zzwV()
  {
    return getActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */