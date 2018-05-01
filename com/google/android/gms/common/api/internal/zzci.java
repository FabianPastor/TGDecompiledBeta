package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultTransform;
import java.lang.ref.WeakReference;

final class zzci
  implements Runnable
{
  zzci(zzch paramzzch, Result paramResult) {}
  
  public final void run()
  {
    try
    {
      BasePendingResult.zzez.set(Boolean.valueOf(true));
      Object localObject1 = zzch.zzc(this.zzml).onSuccess(this.zzmk);
      zzch.zzd(this.zzml).sendMessage(zzch.zzd(this.zzml).obtainMessage(0, localObject1));
      BasePendingResult.zzez.set(Boolean.valueOf(false));
      zzch.zza(this.zzml, this.zzmk);
      localObject1 = (GoogleApiClient)zzch.zze(this.zzml).get();
      if (localObject1 != null) {
        ((GoogleApiClient)localObject1).zzb(this.zzml);
      }
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      for (;;)
      {
        zzch.zzd(this.zzml).sendMessage(zzch.zzd(this.zzml).obtainMessage(1, localRuntimeException));
        BasePendingResult.zzez.set(Boolean.valueOf(false));
        zzch.zza(this.zzml, this.zzmk);
        GoogleApiClient localGoogleApiClient1 = (GoogleApiClient)zzch.zze(this.zzml).get();
        if (localGoogleApiClient1 != null) {
          localGoogleApiClient1.zzb(this.zzml);
        }
      }
    }
    finally
    {
      BasePendingResult.zzez.set(Boolean.valueOf(false));
      zzch.zza(this.zzml, this.zzmk);
      GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zzch.zze(this.zzml).get();
      if (localGoogleApiClient2 != null) {
        localGoogleApiClient2.zzb(this.zzml);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzci.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */