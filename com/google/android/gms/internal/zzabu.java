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

public final class zzabu
  extends Fragment
  implements zzabf
{
  private static WeakHashMap<FragmentActivity, WeakReference<zzabu>> zzaCS = new WeakHashMap();
  private int zzJO = 0;
  private Map<String, zzabe> zzaCT = new ArrayMap();
  private Bundle zzaCU;
  
  public static zzabu zza(FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)zzaCS.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (zzabu)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzabu)localObject;
      }
    }
    try
    {
      zzabu localzzabu = (zzabu)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
      if (localzzabu != null)
      {
        localObject = localzzabu;
        if (!localzzabu.isRemoving()) {}
      }
      else
      {
        localObject = new zzabu();
        paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
      }
      zzaCS.put(paramFragmentActivity, new WeakReference(localObject));
      return (zzabu)localObject;
    }
    catch (ClassCastException paramFragmentActivity)
    {
      throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
    }
  }
  
  private void zzb(final String paramString, @NonNull final zzabe paramzzabe)
  {
    if (this.zzJO > 0) {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          zzabe localzzabe;
          if (zzabu.zza(zzabu.this) >= 1)
          {
            localzzabe = paramzzabe;
            if (zzabu.zzb(zzabu.this) == null) {
              break label101;
            }
          }
          label101:
          for (Bundle localBundle = zzabu.zzb(zzabu.this).getBundle(paramString);; localBundle = null)
          {
            localzzabe.onCreate(localBundle);
            if (zzabu.zza(zzabu.this) >= 2) {
              paramzzabe.onStart();
            }
            if (zzabu.zza(zzabu.this) >= 3) {
              paramzzabe.onStop();
            }
            if (zzabu.zza(zzabu.this) >= 4) {
              paramzzabe.onDestroy();
            }
            return;
          }
        }
      });
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
  
  public FragmentActivity zzwZ()
  {
    return getActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */