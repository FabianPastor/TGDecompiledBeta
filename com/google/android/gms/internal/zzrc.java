package com.google.android.gms.internal;

import android.annotation.TargetApi;
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

@TargetApi(11)
public final class zzrc
  extends Fragment
  implements zzrb
{
  private static WeakHashMap<Activity, WeakReference<zzrc>> yZ = new WeakHashMap();
  private Map<String, zzra> za = new ArrayMap();
  private Bundle zb;
  private int zzbqm = 0;
  
  private void zzb(final String paramString, @NonNull final zzra paramzzra)
  {
    if (this.zzbqm > 0) {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          zzra localzzra;
          if (zzrc.zza(zzrc.this) >= 1)
          {
            localzzra = paramzzra;
            if (zzrc.zzb(zzrc.this) == null) {
              break label101;
            }
          }
          label101:
          for (Bundle localBundle = zzrc.zzb(zzrc.this).getBundle(paramString);; localBundle = null)
          {
            localzzra.onCreate(localBundle);
            if (zzrc.zza(zzrc.this) >= 2) {
              paramzzra.onStart();
            }
            if (zzrc.zza(zzrc.this) >= 3) {
              paramzzra.onStop();
            }
            if (zzrc.zza(zzrc.this) >= 4) {
              paramzzra.onDestroy();
            }
            return;
          }
        }
      });
    }
  }
  
  public static zzrc zzt(Activity paramActivity)
  {
    Object localObject = (WeakReference)yZ.get(paramActivity);
    if (localObject != null)
    {
      localObject = (zzrc)((WeakReference)localObject).get();
      if (localObject != null) {
        return (zzrc)localObject;
      }
    }
    try
    {
      zzrc localzzrc = (zzrc)paramActivity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
      if (localzzrc != null)
      {
        localObject = localzzrc;
        if (!localzzrc.isRemoving()) {}
      }
      else
      {
        localObject = new zzrc();
        paramActivity.getFragmentManager().beginTransaction().add((Fragment)localObject, "LifecycleFragmentImpl").commitAllowingStateLoss();
      }
      yZ.put(paramActivity, new WeakReference(localObject));
      return (zzrc)localObject;
    }
    catch (ClassCastException paramActivity)
    {
      throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", paramActivity);
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
  
  public Activity zzasq()
  {
    return getActivity();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */