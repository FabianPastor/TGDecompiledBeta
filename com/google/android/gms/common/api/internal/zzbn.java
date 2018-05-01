package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.Client;
import java.util.Collections;
import java.util.Map;

final class zzbn
  implements Runnable
{
  zzbn(GoogleApiManager.zzc paramzzc, ConnectionResult paramConnectionResult) {}
  
  public final void run()
  {
    if (this.zzkl.isSuccess())
    {
      GoogleApiManager.zzc.zza(this.zzkr, true);
      if (GoogleApiManager.zzc.zza(this.zzkr).requiresSignIn()) {
        GoogleApiManager.zzc.zzb(this.zzkr);
      }
    }
    for (;;)
    {
      return;
      GoogleApiManager.zzc.zza(this.zzkr).getRemoteService(null, Collections.emptySet());
      continue;
      ((GoogleApiManager.zza)GoogleApiManager.zzj(this.zzkr.zzjy).get(GoogleApiManager.zzc.zzc(this.zzkr))).onConnectionFailed(this.zzkl);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */