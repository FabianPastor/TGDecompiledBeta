package com.google.android.gms.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.zzq;

public final class zzbbo<O extends Api.ApiOptions>
  extends GoogleApi<O>
{
  private final Api.zza<? extends zzctk, zzctl> zzaBe;
  private final zzq zzaCA;
  private final Api.zze zzaCy;
  private final zzbbi zzaCz;
  
  public zzbbo(@NonNull Context paramContext, Api<O> paramApi, Looper paramLooper, @NonNull Api.zze paramzze, @NonNull zzbbi paramzzbbi, zzq paramzzq, Api.zza<? extends zzctk, zzctl> paramzza)
  {
    super(paramContext, paramApi, paramLooper);
    this.zzaCy = paramzze;
    this.zzaCz = paramzzbbi;
    this.zzaCA = paramzzq;
    this.zzaBe = paramzza;
    this.zzaAN.zzb(this);
  }
  
  public final Api.zze zza(Looper paramLooper, zzbdd<O> paramzzbdd)
  {
    this.zzaCz.zza(paramzzbdd);
    return this.zzaCy;
  }
  
  public final zzbej zza(Context paramContext, Handler paramHandler)
  {
    return new zzbej(paramContext, paramHandler, this.zzaCA, this.zzaBe);
  }
  
  public final Api.zze zzpJ()
  {
    return this.zzaCy;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */