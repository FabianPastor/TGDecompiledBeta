package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultTransform;
import java.lang.ref.WeakReference;

final class zzdh
  implements Runnable
{
  zzdh(zzdg paramzzdg, Result paramResult) {}
  
  public final void run()
  {
    try
    {
      BasePendingResult.zzfot.set(Boolean.valueOf(true));
      Object localObject1 = zzdg.zzc(this.zzfvf).onSuccess(this.zzfve);
      zzdg.zzd(this.zzfvf).sendMessage(zzdg.zzd(this.zzfvf).obtainMessage(0, localObject1));
      BasePendingResult.zzfot.set(Boolean.valueOf(false));
      zzdg.zza(this.zzfvf, this.zzfve);
      localObject1 = (GoogleApiClient)zzdg.zze(this.zzfvf).get();
      if (localObject1 != null) {
        ((GoogleApiClient)localObject1).zzb(this.zzfvf);
      }
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      zzdg.zzd(this.zzfvf).sendMessage(zzdg.zzd(this.zzfvf).obtainMessage(1, localRuntimeException));
      GoogleApiClient localGoogleApiClient1;
      return;
    }
    finally
    {
      BasePendingResult.zzfot.set(Boolean.valueOf(false));
      zzdg.zza(this.zzfvf, this.zzfve);
      GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zzdg.zze(this.zzfvf).get();
      if (localGoogleApiClient2 != null) {
        localGoogleApiClient2.zzb(this.zzfvf);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */