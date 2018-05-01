package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

final class zzbbs
  implements OnCompleteListener<Void>
{
  private zzbei zzaCQ;
  
  zzbbs(zzbbp paramzzbbp, zzbei paramzzbei)
  {
    this.zzaCQ = paramzzbei;
  }
  
  final void cancel()
  {
    this.zzaCQ.zzmF();
  }
  
  public final void onComplete(@NonNull Task<Void> paramTask)
  {
    zzbbp.zza(this.zzaCP).lock();
    Object localObject;
    try
    {
      if (!zzbbp.zzb(this.zzaCP))
      {
        this.zzaCQ.zzmF();
        return;
      }
      if (paramTask.isSuccessful())
      {
        zzbbp.zzb(this.zzaCP, new ArrayMap(zzbbp.zzm(this.zzaCP).size()));
        paramTask = zzbbp.zzm(this.zzaCP).values().iterator();
        while (paramTask.hasNext())
        {
          localObject = (zzbbo)paramTask.next();
          zzbbp.zzg(this.zzaCP).put(((zzbbo)localObject).zzph(), ConnectionResult.zzazX);
        }
      }
      if (!(paramTask.getException() instanceof zza)) {
        break label417;
      }
    }
    finally
    {
      zzbbp.zza(this.zzaCP).unlock();
    }
    paramTask = (zza)paramTask.getException();
    if (zzbbp.zze(this.zzaCP))
    {
      zzbbp.zzb(this.zzaCP, new ArrayMap(zzbbp.zzm(this.zzaCP).size()));
      localObject = zzbbp.zzm(this.zzaCP).values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        zzbbo localzzbbo = (zzbbo)((Iterator)localObject).next();
        zzbat localzzbat = localzzbbo.zzph();
        ConnectionResult localConnectionResult = paramTask.zza(localzzbbo);
        if (zzbbp.zza(this.zzaCP, localzzbbo, localConnectionResult)) {
          zzbbp.zzg(this.zzaCP).put(localzzbat, new ConnectionResult(16));
        } else {
          zzbbp.zzg(this.zzaCP).put(localzzbat, localConnectionResult);
        }
      }
    }
    zzbbp.zzb(this.zzaCP, paramTask.zzpf());
    for (;;)
    {
      if (this.zzaCP.isConnected())
      {
        zzbbp.zzd(this.zzaCP).putAll(zzbbp.zzg(this.zzaCP));
        if (zzbbp.zzf(this.zzaCP) == null)
        {
          zzbbp.zzi(this.zzaCP);
          zzbbp.zzj(this.zzaCP);
          zzbbp.zzl(this.zzaCP).signalAll();
        }
      }
      this.zzaCQ.zzmF();
      zzbbp.zza(this.zzaCP).unlock();
      return;
      label417:
      Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
      zzbbp.zzb(this.zzaCP, Collections.emptyMap());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */