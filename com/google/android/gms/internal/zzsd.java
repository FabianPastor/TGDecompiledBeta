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

public final class zzsd
  extends Fragment
  implements zzrp
{
  private static WeakHashMap<FragmentActivity, WeakReference<zzsd>> Bg = new WeakHashMap();
  private Map<String, zzro> Bh = new ArrayMap();
  private Bundle Bi;
  private int zzbtt = 0;
  
  public static zzsd zza(FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)Bg.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (zzsd)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzsd)localObject;
      }
    }
    try
    {
      zzsd localzzsd = (zzsd)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
      if (localzzsd != null)
      {
        localObject = localzzsd;
        if (!localzzsd.isRemoving()) {}
      }
      else
      {
        localObject = new zzsd();
        paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
      }
      Bg.put(paramFragmentActivity, new WeakReference(localObject));
      return (zzsd)localObject;
    }
    catch (ClassCastException paramFragmentActivity)
    {
      throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
    }
  }
  
  private void zzb(final String paramString, @NonNull final zzro paramzzro)
  {
    if (this.zzbtt > 0) {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          zzro localzzro;
          if (zzsd.zza(zzsd.this) >= 1)
          {
            localzzro = paramzzro;
            if (zzsd.zzb(zzsd.this) == null) {
              break label101;
            }
          }
          label101:
          for (Bundle localBundle = zzsd.zzb(zzsd.this).getBundle(paramString);; localBundle = null)
          {
            localzzro.onCreate(localBundle);
            if (zzsd.zza(zzsd.this) >= 2) {
              paramzzro.onStart();
            }
            if (zzsd.zza(zzsd.this) >= 3) {
              paramzzro.onStop();
            }
            if (zzsd.zza(zzsd.this) >= 4) {
              paramzzro.onDestroy();
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
    Iterator localIterator = this.Bh.values().iterator();
    while (localIterator.hasNext()) {
      ((zzro)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = this.Bh.values().iterator();
    while (localIterator.hasNext()) {
      ((zzro)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzbtt = 1;
    this.Bi = paramBundle;
    Iterator localIterator = this.Bh.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      zzro localzzro = (zzro)((Map.Entry)localObject).getValue();
      if (paramBundle != null) {}
      for (localObject = paramBundle.getBundle((String)((Map.Entry)localObject).getKey());; localObject = null)
      {
        localzzro.onCreate((Bundle)localObject);
        break;
      }
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    this.zzbtt = 4;
    Iterator localIterator = this.Bh.values().iterator();
    while (localIterator.hasNext()) {
      ((zzro)localIterator.next()).onDestroy();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (paramBundle == null) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.Bh.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Bundle localBundle = new Bundle();
        ((zzro)localEntry.getValue()).onSaveInstanceState(localBundle);
        paramBundle.putBundle((String)localEntry.getKey(), localBundle);
      }
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.zzbtt = 2;
    Iterator localIterator = this.Bh.values().iterator();
    while (localIterator.hasNext()) {
      ((zzro)localIterator.next()).onStart();
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.zzbtt = 3;
    Iterator localIterator = this.Bh.values().iterator();
    while (localIterator.hasNext()) {
      ((zzro)localIterator.next()).onStop();
    }
  }
  
  public <T extends zzro> T zza(String paramString, Class<T> paramClass)
  {
    return (zzro)paramClass.cast(this.Bh.get(paramString));
  }
  
  public void zza(String paramString, @NonNull zzro paramzzro)
  {
    if (!this.Bh.containsKey(paramString))
    {
      this.Bh.put(paramString, paramzzro);
      zzb(paramString, paramzzro);
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
  
  public FragmentActivity zzaub()
  {
    return getActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */