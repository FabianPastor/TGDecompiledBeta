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

final class zzbbr
  implements OnCompleteListener<Void>
{
  private zzbbr(zzbbp paramzzbbp) {}
  
  public final void onComplete(@NonNull Task<Void> paramTask)
  {
    zzbbp.zza(this.zzaCP).lock();
    Object localObject;
    try
    {
      boolean bool = zzbbp.zzb(this.zzaCP);
      if (!bool) {
        return;
      }
      if (paramTask.isSuccessful())
      {
        zzbbp.zza(this.zzaCP, new ArrayMap(zzbbp.zzc(this.zzaCP).size()));
        paramTask = zzbbp.zzc(this.zzaCP).values().iterator();
        while (paramTask.hasNext())
        {
          localObject = (zzbbo)paramTask.next();
          zzbbp.zzd(this.zzaCP).put(((zzbbo)localObject).zzph(), ConnectionResult.zzazX);
        }
      }
      if (!(paramTask.getException() instanceof zza)) {
        break label435;
      }
    }
    finally
    {
      zzbbp.zza(this.zzaCP).unlock();
    }
    paramTask = (zza)paramTask.getException();
    if (zzbbp.zze(this.zzaCP))
    {
      zzbbp.zza(this.zzaCP, new ArrayMap(zzbbp.zzc(this.zzaCP).size()));
      localObject = zzbbp.zzc(this.zzaCP).values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        zzbbo localzzbbo = (zzbbo)((Iterator)localObject).next();
        zzbat localzzbat = localzzbbo.zzph();
        ConnectionResult localConnectionResult = paramTask.zza(localzzbbo);
        if (zzbbp.zza(this.zzaCP, localzzbbo, localConnectionResult)) {
          zzbbp.zzd(this.zzaCP).put(localzzbat, new ConnectionResult(16));
        } else {
          zzbbp.zzd(this.zzaCP).put(localzzbat, localConnectionResult);
        }
      }
    }
    zzbbp.zza(this.zzaCP, paramTask.zzpf());
    zzbbp.zza(this.zzaCP, zzbbp.zzf(this.zzaCP));
    if (zzbbp.zzg(this.zzaCP) != null)
    {
      zzbbp.zzd(this.zzaCP).putAll(zzbbp.zzg(this.zzaCP));
      zzbbp.zza(this.zzaCP, zzbbp.zzf(this.zzaCP));
    }
    if (zzbbp.zzh(this.zzaCP) == null)
    {
      zzbbp.zzi(this.zzaCP);
      zzbbp.zzj(this.zzaCP);
    }
    for (;;)
    {
      zzbbp.zzl(this.zzaCP).signalAll();
      zzbbp.zza(this.zzaCP).unlock();
      return;
      label435:
      Log.e("ConnectionlessGAC", "Unexpected availability exception", paramTask.getException());
      zzbbp.zza(this.zzaCP, Collections.emptyMap());
      zzbbp.zza(this.zzaCP, new ConnectionResult(8));
      break;
      zzbbp.zza(this.zzaCP, false);
      zzbbp.zzk(this.zzaCP).zzc(zzbbp.zzh(this.zzaCP));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */