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

public final class zzrn
  extends Fragment
  implements zzrb
{
  private static WeakHashMap<FragmentActivity, WeakReference<zzrn>> yZ = new WeakHashMap();
  private Map<String, zzra> za = new ArrayMap();
  private Bundle zb;
  private int zzbqm = 0;
  
  public static zzrn zza(FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)yZ.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (zzrn)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzrn)localObject;
      }
    }
    try
    {
      zzrn localzzrn = (zzrn)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
      if (localzzrn != null)
      {
        localObject = localzzrn;
        if (!localzzrn.isRemoving()) {}
      }
      else
      {
        localObject = new zzrn();
        paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
      }
      yZ.put(paramFragmentActivity, new WeakReference(localObject));
      return (zzrn)localObject;
    }
    catch (ClassCastException paramFragmentActivity)
    {
      throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
    }
  }
  
  private void zzb(final String paramString, @NonNull final zzra paramzzra)
  {
    if (this.zzbqm > 0) {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          zzra localzzra;
          if (zzrn.zza(zzrn.this) >= 1)
          {
            localzzra = paramzzra;
            if (zzrn.zzb(zzrn.this) == null) {
              break label101;
            }
          }
          label101:
          for (Bundle localBundle = zzrn.zzb(zzrn.this).getBundle(paramString);; localBundle = null)
          {
            localzzra.onCreate(localBundle);
            if (zzrn.zza(zzrn.this) >= 2) {
              paramzzra.onStart();
            }
            if (zzrn.zza(zzrn.this) >= 3) {
              paramzzra.onStop();
            }
            if (zzrn.zza(zzrn.this) >= 4) {
              paramzzra.onDestroy();
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
    Iterator localIterator = this.za.values().iterator();
    while (localIterator.hasNext()) {
      ((zzra)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = this.za.values().iterator();
    while (localIterator.hasNext()) {
      ((zzra)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzbqm = 1;
    this.zb = paramBundle;
    Iterator localIterator = this.za.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      zzra localzzra = (zzra)((Map.Entry)localObject).getValue();
      if (paramBundle != null) {}
      for (localObject = paramBundle.getBundle((String)((Map.Entry)localObject).getKey());; localObject = null)
      {
        localzzra.onCreate((Bundle)localObject);
        break;
      }
    }
  }
  
  public void onDestroy()
  {
    super.onStop();
    this.zzbqm = 4;
    Iterator localIterator = this.za.values().iterator();
    while (localIterator.hasNext()) {
      ((zzra)localIterator.next()).onDestroy();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (paramBundle == null) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.za.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Bundle localBundle = new Bundle();
        ((zzra)localEntry.getValue()).onSaveInstanceState(localBundle);
        paramBundle.putBundle((String)localEntry.getKey(), localBundle);
      }
    }
  }
  
  public void onStart()
  {
    super.onStop();
    this.zzbqm = 2;
    Iterator localIterator = this.za.values().iterator();
    while (localIterator.hasNext()) {
      ((zzra)localIterator.next()).onStart();
    }
  }
  
  public void onStop()
  {
    super.onStop();
    this.zzbqm = 3;
    Iterator localIterator = this.za.values().iterator();
    while (localIterator.hasNext()) {
      ((zzra)localIterator.next()).onStop();
    }
  }
  
  public <T extends zzra> T zza(String paramString, Class<T> paramClass)
  {
    return (zzra)paramClass.cast(this.za.get(paramString));
  }
  
  public void zza(String paramString, @NonNull zzra paramzzra)
  {
    if (!this.za.containsKey(paramString))
    {
      this.za.put(paramString, paramzzra);
      zzb(paramString, paramzzra);
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramString).length() + 59 + "LifecycleCallback with tag " + paramString + " already added to this fragment.");
  }
  
  public FragmentActivity zzass()
  {
    return getActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */