package com.google.android.gms.common.api.internal;

import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.AvailabilityException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

final class zzy
  implements OnCompleteListener<Map<zzh<?>, String>>
{
  private zzy(zzw paramzzw) {}
  
  public final void onComplete(Task<Map<zzh<?>, String>> paramTask)
  {
    zzw.zza(this.zzgu).lock();
    Object localObject1;
    try
    {
      boolean bool = zzw.zzb(this.zzgu);
      if (!bool) {
        return;
      }
      if (paramTask.isSuccessful())
      {
        paramTask = this.zzgu;
        localObject1 = new android/support/v4/util/ArrayMap;
        ((ArrayMap)localObject1).<init>(zzw.zzc(this.zzgu).size());
        zzw.zza(paramTask, (Map)localObject1);
        paramTask = zzw.zzc(this.zzgu).values().iterator();
        while (paramTask.hasNext())
        {
          localObject1 = (zzv)paramTask.next();
          zzw.zzd(this.zzgu).put(((GoogleApi)localObject1).zzm(), ConnectionResult.RESULT_SUCCESS);
        }
      }
      if (!(paramTask.getException() instanceof AvailabilityException)) {
        break label456;
      }
    }
    finally
    {
      zzw.zza(this.zzgu).unlock();
    }
    paramTask = (AvailabilityException)paramTask.getException();
    if (zzw.zze(this.zzgu))
    {
      Object localObject2 = this.zzgu;
      localObject1 = new android/support/v4/util/ArrayMap;
      ((ArrayMap)localObject1).<init>(zzw.zzc(this.zzgu).size());
      zzw.zza((zzw)localObject2, (Map)localObject1);
      localObject2 = zzw.zzc(this.zzgu).values().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = (zzv)((Iterator)localObject2).next();
        localObject1 = ((GoogleApi)localObject3).zzm();
        Object localObject4 = paramTask.getConnectionResult((GoogleApi)localObject3);
        if (zzw.zza(this.zzgu, (zzv)localObject3, (ConnectionResult)localObject4))
        {
          localObject4 = zzw.zzd(this.zzgu);
          localObject3 = new com/google/android/gms/common/ConnectionResult;
          ((ConnectionResult)localObject3).<init>(16);
          ((Map)localObject4).put(localObject1, localObject3);
        }
        else
        {
          zzw.zzd(this.zzgu).put(localObject1, localObject4);
        }
      }
    }
    zzw.zza(this.zzgu, paramTask.zzl());
    zzw.zza(this.zzgu, zzw.zzf(this.zzgu));
    label361:
    if (zzw.zzg(this.zzgu) != null)
    {
      zzw.zzd(this.zzgu).putAll(zzw.zzg(this.zzgu));
      zzw.zza(this.zzgu, zzw.zzf(this.zzgu));
    }
    if (zzw.zzh(this.zzgu) == null)
    {
      zzw.zzi(this.zzgu);
      zzw.zzj(this.zzgu);
    }
    for (;;)
    {
      zzw.zzl(this.zzgu).signalAll();
      zzw.zza(this.zzgu).unlock();
      break;
      label456:
      Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
      zzw.zza(this.zzgu, Collections.emptyMap());
      localObject1 = this.zzgu;
      paramTask = new com/google/android/gms/common/ConnectionResult;
      paramTask.<init>(8);
      zzw.zza((zzw)localObject1, paramTask);
      break label361;
      zzw.zza(this.zzgu, false);
      zzw.zzk(this.zzgu).zzc(zzw.zzh(this.zzgu));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */