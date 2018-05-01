package com.google.android.gms.common.api.internal;

import android.os.Looper;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Result;

public final class zzbo<O extends Api.ApiOptions>
  extends zzaf
{
  private final GoogleApi<O> zzks;
  
  public zzbo(GoogleApi<O> paramGoogleApi)
  {
    super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
    this.zzks = paramGoogleApi;
  }
  
  public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation.ApiMethodImpl<R, A>> T enqueue(T paramT)
  {
    return this.zzks.doRead(paramT);
  }
  
  public final <A extends Api.AnyClient, T extends BaseImplementation.ApiMethodImpl<? extends Result, A>> T execute(T paramT)
  {
    return this.zzks.doWrite(paramT);
  }
  
  public final Looper getLooper()
  {
    return this.zzks.getLooper();
  }
  
  public final void zza(zzch paramzzch) {}
  
  public final void zzb(zzch paramzzch) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */