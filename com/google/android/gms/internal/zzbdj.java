package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;

public final class zzbdj<O extends Api.ApiOptions>
  extends zzbbz
{
  private final GoogleApi<O> zzaEz;
  
  public zzbdj(GoogleApi<O> paramGoogleApi)
  {
    super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
    this.zzaEz = paramGoogleApi;
  }
  
  public final Context getContext()
  {
    return this.zzaEz.getApplicationContext();
  }
  
  public final Looper getLooper()
  {
    return this.zzaEz.getLooper();
  }
  
  public final void zza(zzbes paramzzbes) {}
  
  public final void zzb(zzbes paramzzbes) {}
  
  public final <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T paramT)
  {
    return this.zzaEz.zza(paramT);
  }
  
  public final <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T paramT)
  {
    return this.zzaEz.zzb(paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */