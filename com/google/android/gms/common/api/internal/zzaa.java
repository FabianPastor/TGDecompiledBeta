package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.PendingResult;
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

public final class zzaa
{
  private final Map<BasePendingResult<?>, Boolean> zzgw = Collections.synchronizedMap(new WeakHashMap());
  private final Map<TaskCompletionSource<?>, Boolean> zzgx = Collections.synchronizedMap(new WeakHashMap());
  
  private final void zza(boolean paramBoolean, Status paramStatus)
  {
    Object localObject2;
    synchronized (this.zzgw)
    {
      localObject2 = new java/util/HashMap;
      ((HashMap)localObject2).<init>(this.zzgw);
    }
    synchronized (this.zzgx)
    {
      ??? = new java/util/HashMap;
      ((HashMap)???).<init>(this.zzgx);
      ??? = ((Map)localObject2).entrySet().iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)???).next();
        if ((paramBoolean) || (((Boolean)((Map.Entry)localObject2).getValue()).booleanValue()))
        {
          ((BasePendingResult)((Map.Entry)localObject2).getKey()).zzb(paramStatus);
          continue;
          paramStatus = finally;
          throw paramStatus;
        }
      }
    }
    ??? = ((Map)???).entrySet().iterator();
    while (((Iterator)???).hasNext())
    {
      ??? = (Map.Entry)((Iterator)???).next();
      if ((paramBoolean) || (((Boolean)((Map.Entry)???).getValue()).booleanValue())) {
        ((TaskCompletionSource)((Map.Entry)???).getKey()).trySetException(new ApiException(paramStatus));
      }
    }
  }
  
  final void zza(BasePendingResult<? extends Result> paramBasePendingResult, boolean paramBoolean)
  {
    this.zzgw.put(paramBasePendingResult, Boolean.valueOf(paramBoolean));
    paramBasePendingResult.addStatusListener(new zzab(this, paramBasePendingResult));
  }
  
  final <TResult> void zza(TaskCompletionSource<TResult> paramTaskCompletionSource, boolean paramBoolean)
  {
    this.zzgx.put(paramTaskCompletionSource, Boolean.valueOf(paramBoolean));
    paramTaskCompletionSource.getTask().addOnCompleteListener(new zzac(this, paramTaskCompletionSource));
  }
  
  final boolean zzaj()
  {
    if ((!this.zzgw.isEmpty()) || (!this.zzgx.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void zzak()
  {
    zza(false, GoogleApiManager.zzjj);
  }
  
  public final void zzal()
  {
    zza(true, zzck.zzmm);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzaa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */