package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

public final class zzsp
  implements zzso
{
  public PendingResult<Status> zzg(GoogleApiClient paramGoogleApiClient)
  {
    paramGoogleApiClient.zzb(new zzsq.zza(paramGoogleApiClient)
    {
      protected void zza(zzsr paramAnonymouszzsr)
        throws RemoteException
      {
        ((zzst)paramAnonymouszzsr.zzavg()).zza(new zzsp.zza(this));
      }
    });
  }
  
  private static class zza
    extends zzsm
  {
    private final zzqo.zzb<Status> EW;
    
    public zza(zzqo.zzb<Status> paramzzb)
    {
      this.EW = paramzzb;
    }
    
    public void zzgv(int paramInt)
      throws RemoteException
    {
      this.EW.setResult(new Status(paramInt));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */