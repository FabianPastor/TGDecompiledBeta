package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;

public final class zzv<O extends Api.ApiOptions>
  extends GoogleApi<O>
{
  private final Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzdh;
  private final Api.Client zzgd;
  private final zzp zzge;
  private final ClientSettings zzgf;
  
  public zzv(Context paramContext, Api<O> paramApi, Looper paramLooper, Api.Client paramClient, zzp paramzzp, ClientSettings paramClientSettings, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder)
  {
    super(paramContext, paramApi, paramLooper);
    this.zzgd = paramClient;
    this.zzge = paramzzp;
    this.zzgf = paramClientSettings;
    this.zzdh = paramAbstractClientBuilder;
    this.zzcq.zza(this);
  }
  
  public final Api.Client zza(Looper paramLooper, GoogleApiManager.zza<O> paramzza)
  {
    this.zzge.zza(paramzza);
    return this.zzgd;
  }
  
  public final zzby zza(Context paramContext, Handler paramHandler)
  {
    return new zzby(paramContext, paramHandler, this.zzgf, this.zzdh);
  }
  
  public final Api.Client zzae()
  {
    return this.zzgd;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */