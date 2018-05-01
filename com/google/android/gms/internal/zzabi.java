package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class zzabi
{
  private final Set<zzabh<?>> zzasL = Collections.newSetFromMap(new WeakHashMap());
  
  public static <L> zzabh.zzb<L> zza(@NonNull L paramL, @NonNull String paramString)
  {
    zzac.zzb(paramL, "Listener must not be null");
    zzac.zzb(paramString, "Listener type must not be null");
    zzac.zzh(paramString, "Listener type must not be empty");
    return new zzabh.zzb(paramL, paramString);
  }
  
  public static <L> zzabh<L> zzb(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    zzac.zzb(paramL, "Listener must not be null");
    zzac.zzb(paramLooper, "Looper must not be null");
    zzac.zzb(paramString, "Listener type must not be null");
    return new zzabh(paramLooper, paramL, paramString);
  }
  
  public void release()
  {
    Iterator localIterator = this.zzasL.iterator();
    while (localIterator.hasNext()) {
      ((zzabh)localIterator.next()).clear();
    }
    this.zzasL.clear();
  }
  
  public <L> zzabh<L> zza(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    paramL = zzb(paramL, paramLooper, paramString);
    this.zzasL.add(paramL);
    return paramL;
  }
  
  public <L> zzabh<L> zzb(@NonNull L paramL, Looper paramLooper)
  {
    return zza(paramL, paramLooper, "NO_TYPE");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */