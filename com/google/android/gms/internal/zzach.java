package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

public final class zzach
  implements zzacg
{
  public PendingResult<Status> zzg(GoogleApiClient paramGoogleApiClient)
  {
    paramGoogleApiClient.zzb(new zzaci.zza(paramGoogleApiClient)
    {
      protected void zza(zzacj paramAnonymouszzacj)
        throws RemoteException
      {
        ((zzacl)paramAnonymouszzacj.zzxD()).zza(new zzach.zza(this));
      }
    });
  }
  
  private static class zza
    extends zzace
  {
    private final zzaad.zzb<Status> zzaGN;
    
    public zza(zzaad.zzb<Status> paramzzb)
    {
      this.zzaGN = paramzzb;
    }
    
    public void zzdd(int paramInt)
      throws RemoteException
    {
      this.zzaGN.setResult(new Status(paramInt));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzach.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */