package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

public class zzqv
{
  private final Map<zzqq<?>, Boolean> zs = Collections.synchronizedMap(new WeakHashMap());
  private final Map<TaskCompletionSource<?>, Boolean> zt = Collections.synchronizedMap(new WeakHashMap());
  
  private void zza(boolean paramBoolean, Status paramStatus)
  {
    Object localObject2;
    synchronized (this.zs)
    {
      localObject2 = new HashMap(this.zs);
    }
    synchronized (this.zt)
    {
      ??? = new HashMap(this.zt);
      localObject2 = ((Map)localObject2).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        ??? = (Map.Entry)((Iterator)localObject2).next();
        if ((paramBoolean) || (((Boolean)((Map.Entry)???).getValue()).booleanValue()))
        {
          ((zzqq)((Map.Entry)???).getKey()).zzab(paramStatus);
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
        ((TaskCompletionSource)((Map.Entry)localObject2).getKey()).trySetException(new zza(paramStatus));
      }
    }
  }
  
  void zza(final zzqq<? extends Result> paramzzqq, boolean paramBoolean)
  {
    this.zs.put(paramzzqq, Boolean.valueOf(paramBoolean));
    paramzzqq.zza(new PendingResult.zza()
    {
      public void zzx(Status paramAnonymousStatus)
      {
        zzqv.zza(zzqv.this).remove(paramzzqq);
      }
    });
  }
  
  <TResult> void zza(final TaskCompletionSource<TResult> paramTaskCompletionSource, boolean paramBoolean)
  {
    this.zt.put(paramTaskCompletionSource, Boolean.valueOf(paramBoolean));
    paramTaskCompletionSource.getTask().addOnCompleteListener(new OnCompleteListener()
    {
      public void onComplete(@NonNull Task<TResult> paramAnonymousTask)
      {
        zzqv.zzb(zzqv.this).remove(paramTaskCompletionSource);
      }
    });
  }
  
  boolean zzasi()
  {
    return (!this.zs.isEmpty()) || (!this.zt.isEmpty());
  }
  
  public void zzasj()
  {
    zza(false, zzrh.AG);
  }
  
  public void zzask()
  {
    zza(true, zzsg.ym);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */