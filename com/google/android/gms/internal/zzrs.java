package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzaa;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class zzrs
{
  private final Set<zzrr<?>> rK = Collections.newSetFromMap(new WeakHashMap());
  
  public static <L> zzrr<L> zzb(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    zzaa.zzb(paramL, "Listener must not be null");
    zzaa.zzb(paramLooper, "Looper must not be null");
    zzaa.zzb(paramString, "Listener type must not be null");
    return new zzrr(paramLooper, paramL, paramString);
  }
  
  public void release()
  {
    Iterator localIterator = this.rK.iterator();
    while (localIterator.hasNext()) {
      ((zzrr)localIterator.next()).clear();
    }
    this.rK.clear();
  }
  
  public <L> zzrr<L> zza(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    paramL = zzb(paramL, paramLooper, paramString);
    this.rK.add(paramL);
    return paramL;
  }
  
  public <L> zzrr<L> zzb(@NonNull L paramL, Looper paramLooper)
  {
    return zza(paramL, paramLooper, "NO_TYPE");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */