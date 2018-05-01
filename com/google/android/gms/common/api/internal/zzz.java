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

final class zzz
  implements OnCompleteListener<Map<zzh<?>, String>>
{
  private SignInConnectionListener zzgv;
  
  final void cancel()
  {
    this.zzgv.onComplete();
  }
  
  public final void onComplete(Task<Map<zzh<?>, String>> paramTask)
  {
    zzw.zza(this.zzgu).lock();
    Object localObject1;
    try
    {
      if (!zzw.zzb(this.zzgu))
      {
        this.zzgv.onComplete();
        return;
      }
      if (paramTask.isSuccessful())
      {
        localObject1 = this.zzgu;
        paramTask = new android/support/v4/util/ArrayMap;
        paramTask.<init>(zzw.zzm(this.zzgu).size());
        zzw.zzb((zzw)localObject1, paramTask);
        localObject1 = zzw.zzm(this.zzgu).values().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          paramTask = (zzv)((Iterator)localObject1).next();
          zzw.zzg(this.zzgu).put(paramTask.zzm(), ConnectionResult.RESULT_SUCCESS);
        }
      }
      if (!(paramTask.getException() instanceof AvailabilityException)) {
        break label437;
      }
    }
    finally
    {
      zzw.zza(this.zzgu).unlock();
    }
    paramTask = (AvailabilityException)paramTask.getException();
    if (zzw.zze(this.zzgu))
    {
      localObject1 = this.zzgu;
      Object localObject2 = new android/support/v4/util/ArrayMap;
      ((ArrayMap)localObject2).<init>(zzw.zzm(this.zzgu).size());
      zzw.zzb((zzw)localObject1, (Map)localObject2);
      localObject1 = zzw.zzm(this.zzgu).values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject3 = (zzv)((Iterator)localObject1).next();
        localObject2 = ((GoogleApi)localObject3).zzm();
        ConnectionResult localConnectionResult = paramTask.getConnectionResult((GoogleApi)localObject3);
        if (zzw.zza(this.zzgu, (zzv)localObject3, localConnectionResult))
        {
          localObject3 = zzw.zzg(this.zzgu);
          localConnectionResult = new com/google/android/gms/common/ConnectionResult;
          localConnectionResult.<init>(16);
          ((Map)localObject3).put(localObject2, localConnectionResult);
        }
        else
        {
          zzw.zzg(this.zzgu).put(localObject2, localConnectionResult);
        }
      }
    }
    zzw.zzb(this.zzgu, paramTask.zzl());
    for (;;)
    {
      if (this.zzgu.isConnected())
      {
        zzw.zzd(this.zzgu).putAll(zzw.zzg(this.zzgu));
        if (zzw.zzf(this.zzgu) == null)
        {
          zzw.zzi(this.zzgu);
          zzw.zzj(this.zzgu);
          zzw.zzl(this.zzgu).signalAll();
        }
      }
      this.zzgv.onComplete();
      zzw.zza(this.zzgu).unlock();
      break;
      label437:
      Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
      zzw.zzb(this.zzgu, Collections.emptyMap());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */