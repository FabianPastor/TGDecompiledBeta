package com.google.android.gms.common.api.internal;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzdj
{
  public static final Status zzfvg = new Status(8, "The connection to Google Play services was lost");
  private static final BasePendingResult<?>[] zzfvh = new BasePendingResult[0];
  private final Map<Api.zzc<?>, Api.zze> zzfsb;
  final Set<BasePendingResult<?>> zzfvi = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zzdm zzfvj = new zzdk(this);
  
  public zzdj(Map<Api.zzc<?>, Api.zze> paramMap)
  {
    this.zzfsb = paramMap;
  }
  
  public final void release()
  {
    BasePendingResult[] arrayOfBasePendingResult = (BasePendingResult[])this.zzfvi.toArray(zzfvh);
    int j = arrayOfBasePendingResult.length;
    int i = 0;
    while (i < j)
    {
      BasePendingResult localBasePendingResult = arrayOfBasePendingResult[i];
      localBasePendingResult.zza(null);
      if (localBasePendingResult.zzagv() == null)
      {
        if (localBasePendingResult.zzahh()) {
          this.zzfvi.remove(localBasePendingResult);
        }
        i += 1;
      }
      else
      {
        localBasePendingResult.setResultCallback(null);
        IBinder localIBinder = ((Api.zze)this.zzfsb.get(((zzm)localBasePendingResult).zzagf())).zzagh();
        if (localBasePendingResult.isReady()) {
          localBasePendingResult.zza(new zzdl(localBasePendingResult, null, localIBinder, null));
        }
        for (;;)
        {
          this.zzfvi.remove(localBasePendingResult);
          break;
          if ((localIBinder != null) && (localIBinder.isBinderAlive()))
          {
            zzdl localzzdl = new zzdl(localBasePendingResult, null, localIBinder, null);
            localBasePendingResult.zza(localzzdl);
            try
            {
              localIBinder.linkToDeath(localzzdl, 0);
            }
            catch (RemoteException localRemoteException)
            {
              localBasePendingResult.cancel();
              localBasePendingResult.zzagv().intValue();
              throw new NullPointerException();
            }
          }
        }
        localBasePendingResult.zza(null);
        localBasePendingResult.cancel();
        localBasePendingResult.zzagv().intValue();
        throw new NullPointerException();
      }
    }
  }
  
  public final void zzaju()
  {
    BasePendingResult[] arrayOfBasePendingResult = (BasePendingResult[])this.zzfvi.toArray(zzfvh);
    int j = arrayOfBasePendingResult.length;
    int i = 0;
    while (i < j)
    {
      arrayOfBasePendingResult[i].zzv(zzfvg);
      i += 1;
    }
  }
  
  final void zzb(BasePendingResult<? extends Result> paramBasePendingResult)
  {
    this.zzfvi.add(paramBasePendingResult);
    paramBasePendingResult.zza(this.zzfvj);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */