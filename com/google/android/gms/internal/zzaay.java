package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.zzc;

public class zzaay<O extends Api.ApiOptions>
  extends zzaap
{
  private final zzc<O> zzaCK;
  
  public zzaay(zzc<O> paramzzc)
  {
    super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
    this.zzaCK = paramzzc;
  }
  
  public Context getContext()
  {
    return this.zzaCK.getApplicationContext();
  }
  
  public Looper getLooper()
  {
    return this.zzaCK.getLooper();
  }
  
  public <A extends Api.zzb, R extends Result, T extends zzaad.zza<R, A>> T zza(@NonNull T paramT)
  {
    return this.zzaCK.doRead(paramT);
  }
  
  public void zza(zzabx paramzzabx) {}
  
  public <A extends Api.zzb, T extends zzaad.zza<? extends Result, A>> T zzb(@NonNull T paramT)
  {
    return this.zzaCK.doWrite(paramT);
  }
  
  public void zzb(zzabx paramzzabx) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */