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

public class zzaal
{
  private final Map<zzaaf<?>, Boolean> zzaBc = Collections.synchronizedMap(new WeakHashMap());
  private final Map<TaskCompletionSource<?>, Boolean> zzaBd = Collections.synchronizedMap(new WeakHashMap());
  
  private void zza(boolean paramBoolean, Status paramStatus)
  {
    Object localObject2;
    synchronized (this.zzaBc)
    {
      localObject2 = new HashMap(this.zzaBc);
    }
    synchronized (this.zzaBd)
    {
      ??? = new HashMap(this.zzaBd);
      localObject2 = ((Map)localObject2).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        ??? = (Map.Entry)((Iterator)localObject2).next();
        if ((paramBoolean) || (((Boolean)((Map.Entry)???).getValue()).booleanValue()))
        {
          ((zzaaf)((Map.Entry)???).getKey()).zzC(paramStatus);
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
  
  void zza(final zzaaf<? extends Result> paramzzaaf, boolean paramBoolean)
  {
    this.zzaBc.put(paramzzaaf, Boolean.valueOf(paramBoolean));
    paramzzaaf.zza(new PendingResult.zza()
    {
      public void zzy(Status paramAnonymousStatus)
      {
        zzaal.zza(zzaal.this).remove(paramzzaaf);
      }
    });
  }
  
  <TResult> void zza(final TaskCompletionSource<TResult> paramTaskCompletionSource, boolean paramBoolean)
  {
    this.zzaBd.put(paramTaskCompletionSource, Boolean.valueOf(paramBoolean));
    paramTaskCompletionSource.getTask().addOnCompleteListener(new OnCompleteListener()
    {
      public void onComplete(@NonNull Task<TResult> paramAnonymousTask)
      {
        zzaal.zzb(zzaal.this).remove(paramTaskCompletionSource);
      }
    });
  }
  
  boolean zzvY()
  {
    return (!this.zzaBc.isEmpty()) || (!this.zzaBd.isEmpty());
  }
  
  public void zzvZ()
  {
    zza(false, zzaax.zzaCn);
  }
  
  public void zzwa()
  {
    zza(true, zzaby.zzaDu);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */