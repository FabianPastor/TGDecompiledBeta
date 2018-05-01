package com.google.android.gms.internal;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Iterator;
import java.util.Set;

public final class zzbav
{
  private final ArrayMap<zzbat<?>, ConnectionResult> zzaAB = new ArrayMap();
  private final TaskCompletionSource<Void> zzaBG = new TaskCompletionSource();
  private int zzaBH;
  private boolean zzaBI = false;
  
  public zzbav(Iterable<? extends GoogleApi<?>> paramIterable)
  {
    paramIterable = paramIterable.iterator();
    while (paramIterable.hasNext())
    {
      GoogleApi localGoogleApi = (GoogleApi)paramIterable.next();
      this.zzaAB.put(localGoogleApi.zzph(), null);
    }
    this.zzaBH = this.zzaAB.keySet().size();
  }
  
  public final Task<Void> getTask()
  {
    return this.zzaBG.getTask();
  }
  
  public final void zza(zzbat<?> paramzzbat, ConnectionResult paramConnectionResult)
  {
    this.zzaAB.put(paramzzbat, paramConnectionResult);
    this.zzaBH -= 1;
    if (!paramConnectionResult.isSuccess()) {
      this.zzaBI = true;
    }
    if (this.zzaBH == 0)
    {
      if (this.zzaBI)
      {
        paramzzbat = new zza(this.zzaAB);
        this.zzaBG.setException(paramzzbat);
      }
    }
    else {
      return;
    }
    this.zzaBG.setResult(null);
  }
  
  public final Set<zzbat<?>> zzpt()
  {
    return this.zzaAB.keySet();
  }
  
  public final void zzpu()
  {
    this.zzaBG.setResult(null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbav.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */