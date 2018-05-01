package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.SimpleClientAdapter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzag
  implements zzbc
{
  private final zzbd zzhf;
  private boolean zzhg = false;
  
  public zzag(zzbd paramzzbd)
  {
    this.zzhf = paramzzbd;
  }
  
  public final void begin() {}
  
  public final void connect()
  {
    if (this.zzhg)
    {
      this.zzhg = false;
      this.zzhf.zza(new zzai(this, this));
    }
  }
  
  public final boolean disconnect()
  {
    boolean bool = true;
    if (this.zzhg) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      if (this.zzhf.zzfq.zzba())
      {
        this.zzhg = true;
        Iterator localIterator = this.zzhf.zzfq.zziq.iterator();
        while (localIterator.hasNext()) {
          ((zzch)localIterator.next()).zzcc();
        }
        bool = false;
      }
      else
      {
        this.zzhf.zzf(null);
      }
    }
  }
  
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    return execute(paramT);
  }
  
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    try
    {
      this.zzhf.zzfq.zzir.zzb(paramT);
      Object localObject1 = this.zzhf.zzfq;
      Object localObject2 = paramT.getClientKey();
      localObject1 = (Api.Client)((zzav)localObject1).zzil.get(localObject2);
      Preconditions.checkNotNull(localObject1, "Appropriate Api was not requested.");
      if ((!((Api.Client)localObject1).isConnected()) && (this.zzhf.zzjb.containsKey(paramT.getClientKey())))
      {
        localObject2 = new com/google/android/gms/common/api/Status;
        ((Status)localObject2).<init>(17);
        paramT.setFailedResult((Status)localObject2);
      }
      for (;;)
      {
        return paramT;
        localObject2 = localObject1;
        if ((localObject1 instanceof SimpleClientAdapter)) {
          localObject2 = ((SimpleClientAdapter)localObject1).getClient();
        }
        paramT.run((Api.AnyClient)localObject2);
      }
    }
    catch (DeadObjectException localDeadObjectException)
    {
      for (;;)
      {
        this.zzhf.zza(new zzah(this, this));
      }
    }
  }
  
  public final void onConnected(Bundle paramBundle) {}
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzhf.zzf(null);
    this.zzhf.zzjf.zzb(paramInt, this.zzhg);
  }
  
  public final void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean) {}
  
  final void zzap()
  {
    if (this.zzhg)
    {
      this.zzhg = false;
      this.zzhf.zzfq.zzir.release();
      disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */