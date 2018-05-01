package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import java.util.Collections;
import java.util.Map;

final class zzbdi
  implements Runnable
{
  zzbdi(zzbdh paramzzbdh, ConnectionResult paramConnectionResult) {}
  
  public final void run()
  {
    if (this.zzaEw.isSuccess())
    {
      zzbdh.zza(this.zzaEy, true);
      if (zzbdh.zza(this.zzaEy).zzmv())
      {
        zzbdh.zzb(this.zzaEy);
        return;
      }
      zzbdh.zza(this.zzaEy).zza(null, Collections.emptySet());
      return;
    }
    ((zzbdd)zzbdb.zzj(this.zzaEy.zzaEm).get(zzbdh.zzc(this.zzaEy))).onConnectionFailed(this.zzaEw);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */