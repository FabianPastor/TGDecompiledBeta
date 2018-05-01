package com.google.android.gms.common.api.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.AvailabilityException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzj
{
  private final ArrayMap<zzh<?>, ConnectionResult> zzcc = new ArrayMap();
  private final ArrayMap<zzh<?>, String> zzei = new ArrayMap();
  private final TaskCompletionSource<Map<zzh<?>, String>> zzej = new TaskCompletionSource();
  private int zzek;
  private boolean zzel = false;
  
  public zzj(Iterable<? extends GoogleApi<?>> paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      paramIterable = (GoogleApi)localIterator.next();
      this.zzcc.put(paramIterable.zzm(), null);
    }
    this.zzek = this.zzcc.keySet().size();
  }
  
  public final Task<Map<zzh<?>, String>> getTask()
  {
    return this.zzej.getTask();
  }
  
  public final void zza(zzh<?> paramzzh, ConnectionResult paramConnectionResult, String paramString)
  {
    this.zzcc.put(paramzzh, paramConnectionResult);
    this.zzei.put(paramzzh, paramString);
    this.zzek -= 1;
    if (!paramConnectionResult.isSuccess()) {
      this.zzel = true;
    }
    if (this.zzek == 0)
    {
      if (!this.zzel) {
        break label77;
      }
      paramzzh = new AvailabilityException(this.zzcc);
      this.zzej.setException(paramzzh);
    }
    for (;;)
    {
      return;
      label77:
      this.zzej.setResult(this.zzei);
    }
  }
  
  public final Set<zzh<?>> zzs()
  {
    return this.zzcc.keySet();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */