package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdw;

final class zzb<T>
  extends zzn<Status>
{
  private T mListener;
  private zzbdw<T> zzaEU;
  private zzc<T> zzbRI;
  
  private zzb(GoogleApiClient paramGoogleApiClient, T paramT, zzbdw<T> paramzzbdw, zzc<T> paramzzc)
  {
    super(paramGoogleApiClient);
    this.mListener = zzbo.zzu(paramT);
    this.zzaEU = ((zzbdw)zzbo.zzu(paramzzbdw));
    this.zzbRI = ((zzc)zzbo.zzu(paramzzc));
  }
  
  static <T> PendingResult<Status> zza(GoogleApiClient paramGoogleApiClient, zzc<T> paramzzc, T paramT)
  {
    return paramGoogleApiClient.zzd(new zzb(paramGoogleApiClient, paramT, paramGoogleApiClient.zzp(paramT), paramzzc));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */