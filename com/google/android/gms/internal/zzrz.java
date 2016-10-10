package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

public final class zzrz
  implements zzry
{
  public PendingResult<Status> zzg(GoogleApiClient paramGoogleApiClient)
  {
    paramGoogleApiClient.zzd(new zzsa.zza(paramGoogleApiClient)
    {
      protected void zza(zzsb paramAnonymouszzsb)
        throws RemoteException
      {
        ((zzsd)paramAnonymouszzsb.zzatx()).zza(new zzrz.zza(this));
      }
    });
  }
  
  private static class zza
    extends zzrw
  {
    private final zzqc.zzb<Status> Dj;
    
    public zza(zzqc.zzb<Status> paramzzb)
    {
      this.Dj = paramzzb;
    }
    
    public void zzgw(int paramInt)
      throws RemoteException
    {
      this.Dj.setResult(new Status(paramInt));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */