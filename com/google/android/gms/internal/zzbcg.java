package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.zze;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class zzbcg
  extends zzbcn
{
  private final Map<Api.zze, zzbcf> zzaDr;
  
  public zzbcg(Map<Api.zze, zzbcf> paramMap)
  {
    super(paramMap, null);
    Map localMap;
    this.zzaDr = localMap;
  }
  
  @WorkerThread
  public final void zzpV()
  {
    int n = 1;
    int m = 0;
    Object localObject = this.zzaDr.keySet().iterator();
    int j = 1;
    int i = 0;
    Api.zze localzze;
    int k;
    if (((Iterator)localObject).hasNext())
    {
      localzze = (Api.zze)((Iterator)localObject).next();
      if (localzze.zzpe())
      {
        if (zzbcf.zza((zzbcf)this.zzaDr.get(localzze))) {
          break label301;
        }
        i = 1;
        k = n;
      }
    }
    for (;;)
    {
      if (k != 0) {
        m = zzbcd.zzb(this.zzaDp).isGooglePlayServicesAvailable(zzbcd.zza(this.zzaDp));
      }
      if ((m != 0) && ((i != 0) || (j != 0)))
      {
        localObject = new ConnectionResult(m, null);
        zzbcd.zzd(this.zzaDp).zza(new zzbch(this, this.zzaDp, (ConnectionResult)localObject));
        label155:
        return;
        k = 0;
        j = i;
        i = k;
      }
      for (;;)
      {
        k = j;
        j = i;
        i = k;
        break;
        if (zzbcd.zze(this.zzaDp)) {
          zzbcd.zzf(this.zzaDp).connect();
        }
        localObject = this.zzaDr.keySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          localzze = (Api.zze)((Iterator)localObject).next();
          zzj localzzj = (zzj)this.zzaDr.get(localzze);
          if ((localzze.zzpe()) && (m != 0)) {
            zzbcd.zzd(this.zzaDp).zza(new zzbci(this, this.zzaDp, localzzj));
          } else {
            localzze.zza(localzzj);
          }
        }
        break label155;
        label301:
        i = j;
        j = 1;
      }
      k = i;
      i = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */