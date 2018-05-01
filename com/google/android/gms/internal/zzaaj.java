package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzg;

public final class zzaaj<O extends Api.ApiOptions>
  extends zzc<O>
{
  private final Api.zze zzaAJ;
  private final zzaag zzaAK;
  private final zzg zzaAL;
  private final Api.zza<? extends zzbai, zzbaj> zzazo;
  
  public zzaaj(@NonNull Context paramContext, Api<O> paramApi, Looper paramLooper, @NonNull Api.zze paramzze, @NonNull zzaag paramzzaag, zzg paramzzg, Api.zza<? extends zzbai, zzbaj> paramzza)
  {
    super(paramContext, paramApi, paramLooper);
    this.zzaAJ = paramzze;
    this.zzaAK = paramzzaag;
    this.zzaAL = paramzzg;
    this.zzazo = paramzza;
    this.zzayX.zzb(this);
  }
  
  public Api.zze buildApiClient(Looper paramLooper, zzaax.zza<O> paramzza)
  {
    this.zzaAK.zza(paramzza);
    return this.zzaAJ;
  }
  
  public zzabr createSignInCoordinator(Context paramContext, Handler paramHandler)
  {
    return new zzabr(paramContext, paramHandler, this.zzaAL, this.zzazo);
  }
  
  public Api.zze zzvU()
  {
    return this.zzaAJ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaaj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */