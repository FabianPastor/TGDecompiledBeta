package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzd;

public class zzqu<O extends Api.ApiOptions>
  extends zzql
{
  private final zzd<O> yN;
  
  public zzqu(zzd<O> paramzzd)
  {
    super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
    this.yN = paramzzd;
  }
  
  public Looper getLooper()
  {
    return this.yN.getLooper();
  }
  
  public void zza(zzrp paramzzrp)
  {
    this.yN.zzapu();
  }
  
  public void zzb(zzrp paramzzrp)
  {
    this.yN.zzapv();
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(@NonNull T paramT)
  {
    return this.yN.zza(paramT);
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzd(@NonNull T paramT)
  {
    return this.yN.zzb(paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */