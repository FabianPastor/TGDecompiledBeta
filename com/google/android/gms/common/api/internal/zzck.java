package com.google.android.gms.common.api.internal;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class zzck
{
  public static final Status zzmm = new Status(8, "The connection to Google Play services was lost");
  private static final BasePendingResult<?>[] zzmn = new BasePendingResult[0];
  private final Map<Api.AnyClientKey<?>, Api.Client> zzil;
  final Set<BasePendingResult<?>> zzmo = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zzcn zzmp = new zzcl(this);
  
  public zzck(Map<Api.AnyClientKey<?>, Api.Client> paramMap)
  {
    this.zzil = paramMap;
  }
  
  public final void release()
  {
    BasePendingResult[] arrayOfBasePendingResult = (BasePendingResult[])this.zzmo.toArray(zzmn);
    int i = arrayOfBasePendingResult.length;
    int j = 0;
    while (j < i)
    {
      BasePendingResult localBasePendingResult = arrayOfBasePendingResult[j];
      localBasePendingResult.zza(null);
      if (localBasePendingResult.zzo() == null)
      {
        if (localBasePendingResult.zzw()) {
          this.zzmo.remove(localBasePendingResult);
        }
        j++;
      }
      else
      {
        localBasePendingResult.setResultCallback(null);
        IBinder localIBinder = ((Api.Client)this.zzil.get(((BaseImplementation.ApiMethodImpl)localBasePendingResult).getClientKey())).getServiceBrokerBinder();
        if (localBasePendingResult.isReady()) {
          localBasePendingResult.zza(new zzcm(localBasePendingResult, null, localIBinder, null));
        }
        for (;;)
        {
          this.zzmo.remove(localBasePendingResult);
          break;
          if ((localIBinder != null) && (localIBinder.isBinderAlive()))
          {
            zzcm localzzcm = new zzcm(localBasePendingResult, null, localIBinder, null);
            localBasePendingResult.zza(localzzcm);
            try
            {
              localIBinder.linkToDeath(localzzcm, 0);
            }
            catch (RemoteException localRemoteException)
            {
              localBasePendingResult.cancel();
              localBasePendingResult.zzo().intValue();
              throw new NullPointerException();
            }
          }
        }
        localBasePendingResult.zza(null);
        localBasePendingResult.cancel();
        localBasePendingResult.zzo().intValue();
        throw new NullPointerException();
      }
    }
  }
  
  final void zzb(BasePendingResult<? extends Result> paramBasePendingResult)
  {
    this.zzmo.add(paramBasePendingResult);
    paramBasePendingResult.zza(this.zzmp);
  }
  
  public final void zzce()
  {
    BasePendingResult[] arrayOfBasePendingResult = (BasePendingResult[])this.zzmo.toArray(zzmn);
    int i = arrayOfBasePendingResult.length;
    for (int j = 0; j < i; j++) {
      arrayOfBasePendingResult[j].zzb(zzmm);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */