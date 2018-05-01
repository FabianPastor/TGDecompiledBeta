package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Iterator;
import java.util.Set;

public final class zzzu
{
  private final ArrayMap<zzzs<?>, ConnectionResult> zzaxy = new ArrayMap();
  private final TaskCompletionSource<Void> zzayC = new TaskCompletionSource();
  private int zzayD;
  private boolean zzayE = false;
  
  public zzzu(Iterable<zzc<? extends Api.ApiOptions>> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      zzc localzzc = (zzc)paramIterable.next();
      this.zzaxy.put(localzzc.getApiKey(), null);
    }
    this.zzayD = this.zzaxy.keySet().size();
  }
  
  public Task<Void> getTask()
  {
    return this.zzayC.getTask();
  }
  
  public void zza(zzzs<?> paramzzzs, ConnectionResult paramConnectionResult)
  {
    this.zzaxy.put(paramzzzs, paramConnectionResult);
    this.zzayD -= 1;
    if (!paramConnectionResult.isSuccess()) {
      this.zzayE = true;
    }
    if (this.zzayD == 0)
    {
      if (this.zzayE)
      {
        paramzzzs = new zzb(this.zzaxy);
        this.zzayC.setException(paramzzzs);
      }
    }
    else {
      return;
    }
    this.zzayC.setResult(null);
  }
  
  public Set<zzzs<?>> zzuY()
  {
    return this.zzaxy.keySet();
  }
  
  public void zzuZ()
  {
    this.zzayC.setResult(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */