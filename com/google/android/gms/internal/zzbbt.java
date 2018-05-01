package com.google.android.gms.internal;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzbbt
{
  private final Map<zzbbe<?>, Boolean> zzaCR = Collections.synchronizedMap(new WeakHashMap());
  private final Map<TaskCompletionSource<?>, Boolean> zzaCS = Collections.synchronizedMap(new WeakHashMap());
  
  private final void zza(boolean paramBoolean, Status paramStatus)
  {
    Object localObject2;
    synchronized (this.zzaCR)
    {
      localObject2 = new HashMap(this.zzaCR);
    }
    synchronized (this.zzaCS)
    {
      ??? = new HashMap(this.zzaCS);
      localObject2 = ((Map)localObject2).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        ??? = (Map.Entry)((Iterator)localObject2).next();
        if ((paramBoolean) || (((Boolean)((Map.Entry)???).getValue()).booleanValue()))
        {
          ((zzbbe)((Map.Entry)???).getKey()).zzs(paramStatus);
          continue;
          paramStatus = finally;
          throw paramStatus;
        }
      }
    }
    ??? = ((Map)???).entrySet().iterator();
    while (((Iterator)???).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)???).next();
      if ((paramBoolean) || (((Boolean)((Map.Entry)localObject2).getValue()).booleanValue())) {
        ((TaskCompletionSource)((Map.Entry)localObject2).getKey()).trySetException(new ApiException(paramStatus));
      }
    }
  }
  
  final void zza(zzbbe<? extends Result> paramzzbbe, boolean paramBoolean)
  {
    this.zzaCR.put(paramzzbbe, Boolean.valueOf(paramBoolean));
    paramzzbbe.zza(new zzbbu(this, paramzzbbe));
  }
  
  final <TResult> void zza(TaskCompletionSource<TResult> paramTaskCompletionSource, boolean paramBoolean)
  {
    this.zzaCS.put(paramTaskCompletionSource, Boolean.valueOf(paramBoolean));
    paramTaskCompletionSource.getTask().addOnCompleteListener(new zzbbv(this, paramTaskCompletionSource));
  }
  
  final boolean zzpO()
  {
    return (!this.zzaCR.isEmpty()) || (!this.zzaCS.isEmpty());
  }
  
  public final void zzpP()
  {
    zza(false, zzbdb.zzaEc);
  }
  
  public final void zzpQ()
  {
    zza(true, zzbev.zzaFj);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */