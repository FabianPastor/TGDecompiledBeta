package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultTransform;
import java.lang.ref.WeakReference;

final class zzbet
  implements Runnable
{
  zzbet(zzbes paramzzbes, Result paramResult) {}
  
  @WorkerThread
  public final void run()
  {
    try
    {
      zzbbe.zzaBV.set(Boolean.valueOf(true));
      Object localObject1 = zzbes.zzc(this.zzaFi).onSuccess(this.zzaFh);
      zzbes.zzd(this.zzaFi).sendMessage(zzbes.zzd(this.zzaFi).obtainMessage(0, localObject1));
      zzbbe.zzaBV.set(Boolean.valueOf(false));
      zzbes.zza(this.zzaFi, this.zzaFh);
      localObject1 = (GoogleApiClient)zzbes.zze(this.zzaFi).get();
      if (localObject1 != null) {
        ((GoogleApiClient)localObject1).zzb(this.zzaFi);
      }
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      zzbes.zzd(this.zzaFi).sendMessage(zzbes.zzd(this.zzaFi).obtainMessage(1, localRuntimeException));
      GoogleApiClient localGoogleApiClient1;
      return;
    }
    finally
    {
      zzbbe.zzaBV.set(Boolean.valueOf(false));
      zzbes.zza(this.zzaFi, this.zzaFh);
      GoogleApiClient localGoogleApiClient2 = (GoogleApiClient)zzbes.zze(this.zzaFi).get();
      if (localGoogleApiClient2 != null) {
        localGoogleApiClient2.zzb(this.zzaFi);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */