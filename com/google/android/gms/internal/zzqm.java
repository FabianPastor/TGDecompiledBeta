package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzai;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class zzqm
  implements zzqq
{
  private final zzqr xk;
  private boolean xl = false;
  
  public zzqm(zzqr paramzzqr)
  {
    this.xk = paramzzqr;
  }
  
  private <A extends Api.zzb> void zzf(zzqc.zza<? extends Result, A> paramzza)
    throws DeadObjectException
  {
    this.xk.wV.yc.zzb(paramzza);
    Api.zze localzze = this.xk.wV.zzb(paramzza.zzapp());
    if ((!localzze.isConnected()) && (this.xk.yl.containsKey(paramzza.zzapp())))
    {
      paramzza.zzz(new Status(17));
      return;
    }
    Object localObject = localzze;
    if ((localzze instanceof zzai)) {
      localObject = ((zzai)localzze).zzavk();
    }
    paramzza.zzb((Api.zzb)localObject);
  }
  
  public void begin() {}
  
  public void connect()
  {
    if (this.xl)
    {
      this.xl = false;
      this.xk.zza(new zzqr.zza(this)
      {
        public void zzari()
        {
          zzqm.zza(zzqm.this).yp.zzn(null);
        }
      });
    }
  }
  
  public boolean disconnect()
  {
    if (this.xl) {
      return false;
    }
    if (this.xk.wV.zzaru())
    {
      this.xl = true;
      Iterator localIterator = this.xk.wV.yb.iterator();
      while (localIterator.hasNext()) {
        ((zzrp)localIterator.next()).zzasu();
      }
      return false;
    }
    this.xk.zzi(null);
    return true;
  }
  
  public void onConnected(Bundle paramBundle) {}
  
  public void onConnectionSuspended(int paramInt)
  {
    this.xk.zzi(null);
    this.xk.yp.zzc(paramInt, this.xl);
  }
  
  public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt) {}
  
  void zzarh()
  {
    if (this.xl)
    {
      this.xl = false;
      this.xk.wV.yc.release();
      disconnect();
    }
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(T paramT)
  {
    return zzd(paramT);
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzd(T paramT)
  {
    try
    {
      zzf(paramT);
      return paramT;
    }
    catch (DeadObjectException localDeadObjectException)
    {
      this.xk.zza(new zzqr.zza(this)
      {
        public void zzari()
        {
          zzqm.this.onConnectionSuspended(1);
        }
      });
    }
    return paramT;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */