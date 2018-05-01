package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbea
{
  private final Set<zzbdw<?>> zzauB = Collections.newSetFromMap(new WeakHashMap());
  
  public static <L> zzbdy<L> zza(@NonNull L paramL, @NonNull String paramString)
  {
    zzbo.zzb(paramL, "Listener must not be null");
    zzbo.zzb(paramString, "Listener type must not be null");
    zzbo.zzh(paramString, "Listener type must not be empty");
    return new zzbdy(paramL, paramString);
  }
  
  public static <L> zzbdw<L> zzb(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    zzbo.zzb(paramL, "Listener must not be null");
    zzbo.zzb(paramLooper, "Looper must not be null");
    zzbo.zzb(paramString, "Listener type must not be null");
    return new zzbdw(paramLooper, paramL, paramString);
  }
  
  public final void release()
  {
    Iterator localIterator = this.zzauB.iterator();
    while (localIterator.hasNext()) {
      ((zzbdw)localIterator.next()).clear();
    }
    this.zzauB.clear();
  }
  
  public final <L> zzbdw<L> zza(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    paramL = zzb(paramL, paramLooper, paramString);
    this.zzauB.add(paramL);
    return paramL;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */