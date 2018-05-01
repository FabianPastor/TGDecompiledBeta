package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzc;

public class zzri<O extends Api.ApiOptions>
  extends zzqz
{
  private final zzc<O> AY;
  
  public zzri(zzc<O> paramzzc)
  {
    super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
    this.AY = paramzzc;
  }
  
  public Looper getLooper()
  {
    return this.AY.getLooper();
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzqo.zza<R, A>> T zza(@NonNull T paramT)
  {
    return this.AY.doRead(paramT);
  }
  
  public void zza(zzsf paramzzsf) {}
  
  public <A extends Api.zzb, T extends zzqo.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    return this.AY.doWrite(paramT);
  }
  
  public void zzb(zzsf paramzzsf) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzri.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */