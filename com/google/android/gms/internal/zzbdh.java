package com.google.android.gms.internal;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzj;
import java.util.Map;
import java.util.Set;

final class zzbdh
  implements zzj, zzbel
{
  private final zzbat<?> zzaAK;
  private final Api.zze zzaCy;
  private zzal zzaDl = null;
  private boolean zzaEx = false;
  private Set<Scope> zzame = null;
  
  public zzbdh(Api.zze paramzze, zzbat<?> paramzzbat)
  {
    this.zzaCy = paramzzbat;
    zzbat localzzbat;
    this.zzaAK = localzzbat;
  }
  
  @WorkerThread
  private final void zzqz()
  {
    if ((this.zzaEx) && (this.zzaDl != null)) {
      this.zzaCy.zza(this.zzaDl, this.zzame);
    }
  }
  
  @WorkerThread
  public final void zzb(zzal paramzzal, Set<Scope> paramSet)
  {
    if ((paramzzal == null) || (paramSet == null))
    {
      Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
      zzh(new ConnectionResult(4));
      return;
    }
    this.zzaDl = paramzzal;
    this.zzame = paramSet;
    zzqz();
  }
  
  public final void zzf(@NonNull ConnectionResult paramConnectionResult)
  {
    zzbdb.zza(this.zzaEm).post(new zzbdi(this, paramConnectionResult));
  }
  
  @WorkerThread
  public final void zzh(ConnectionResult paramConnectionResult)
  {
    ((zzbdd)zzbdb.zzj(this.zzaEm).get(this.zzaAK)).zzh(paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */