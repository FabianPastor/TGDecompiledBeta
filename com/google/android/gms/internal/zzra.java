package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzag;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class zzra
  implements zzre
{
  private final zzrf zA;
  private boolean zB = false;
  
  public zzra(zzrf paramzzrf)
  {
    this.zA = paramzzrf;
  }
  
  private <A extends Api.zzb> void zzd(zzqo.zza<? extends Result, A> paramzza)
    throws DeadObjectException
  {
    this.zA.yW.Ap.zzb(paramzza);
    Api.zze localzze = this.zA.yW.zzb(paramzza.zzaqv());
    if ((!localzze.isConnected()) && (this.zA.Ay.containsKey(paramzza.zzaqv())))
    {
      paramzza.zzaa(new Status(17));
      return;
    }
    Object localObject = localzze;
    if ((localzze instanceof zzag)) {
      localObject = ((zzag)localzze).zzawt();
    }
    paramzza.zzb((Api.zzb)localObject);
  }
  
  public void begin() {}
  
  public void connect()
  {
    if (this.zB)
    {
      this.zB = false;
      this.zA.zza(new zzrf.zza(this)
      {
        public void zzaso()
        {
          zzra.zza(zzra.this).AC.zzn(null);
        }
      });
    }
  }
  
  public boolean disconnect()
  {
    if (this.zB) {
      return false;
    }
    if (this.zA.yW.zzata())
    {
      this.zB = true;
      Iterator localIterator = this.zA.yW.Ao.iterator();
      while (localIterator.hasNext()) {
        ((zzsf)localIterator.next()).zzaud();
      }
      return false;
    }
    this.zA.zzh(null);
    return true;
  }
  
  public void onConnected(Bundle paramBundle) {}
  
  public void onConnectionSuspended(int paramInt)
  {
    this.zA.zzh(null);
    this.zA.AC.zzc(paramInt, this.zB);
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(T paramT)
  {
    return zzb(paramT);
  }
  
  public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt) {}
  
  void zzasn()
  {
    if (this.zB)
    {
      this.zB = false;
      this.zA.yW.Ap.release();
      disconnect();
    }
  }
  
  public <A extends Api.zzb, T extends zzqo.zza<? extends Result, A>> T zzb(T paramT)
  {
    try
    {
      zzd(paramT);
      return paramT;
    }
    catch (DeadObjectException localDeadObjectException)
    {
      this.zA.zza(new zzrf.zza(this)
      {
        public void zzaso()
        {
          zzra.this.onConnectionSuspended(1);
        }
      });
    }
    return paramT;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzra.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */