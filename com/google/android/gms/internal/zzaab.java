package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.zzb;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Iterator;
import java.util.Set;

public final class zzaab
{
  private final ArrayMap<zzzz<?>, ConnectionResult> zzayL = new ArrayMap();
  private final TaskCompletionSource<Void> zzazS = new TaskCompletionSource();
  private int zzazT;
  private boolean zzazU = false;
  
  public zzaab(Iterable<? extends zzc<?>> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      zzc localzzc = (zzc)paramIterable.next();
      this.zzayL.put(localzzc.getApiKey(), null);
    }
    this.zzazT = this.zzayL.keySet().size();
  }
  
  public Task<Void> getTask()
  {
    return this.zzazS.getTask();
  }
  
  public void zza(zzzz<?> paramzzzz, ConnectionResult paramConnectionResult)
  {
    this.zzayL.put(paramzzzz, paramConnectionResult);
    this.zzazT -= 1;
    if (!paramConnectionResult.isSuccess()) {
      this.zzazU = true;
    }
    if (this.zzazT == 0)
    {
      if (this.zzazU)
      {
        paramzzzz = new zzb(this.zzayL);
        this.zzazS.setException(paramzzzz);
      }
    }
    else {
      return;
    }
    this.zzazS.setResult(null);
  }
  
  public void zzvA()
  {
    this.zzazS.setResult(null);
  }
  
  public Set<zzzz<?>> zzvz()
  {
    return this.zzayL.keySet();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */