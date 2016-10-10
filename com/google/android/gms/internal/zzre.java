package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class zzre
{
  private final Set<zzrd<?>> pA = Collections.newSetFromMap(new WeakHashMap());
  
  public void release()
  {
    Iterator localIterator = this.pA.iterator();
    while (localIterator.hasNext()) {
      ((zzrd)localIterator.next()).clear();
    }
    this.pA.clear();
  }
  
  public <L> zzrd<L> zza(@NonNull L paramL, @NonNull Looper paramLooper, @NonNull String paramString)
  {
    zzac.zzb(paramL, "Listener must not be null");
    zzac.zzb(paramLooper, "Looper must not be null");
    zzac.zzb(paramString, "Listener type must not be null");
    paramL = new zzrd(paramLooper, paramL, paramString);
    this.pA.add(paramL);
    return paramL;
  }
  
  public <L> zzrd<L> zzb(@NonNull L paramL, Looper paramLooper)
  {
    return zza(paramL, paramLooper, "NO_TYPE");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzre.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */