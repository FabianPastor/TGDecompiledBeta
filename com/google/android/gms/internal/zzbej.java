package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zzy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzq;
import java.util.HashSet;
import java.util.Set;

public final class zzbej
  extends zzctp
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private static Api.zza<? extends zzctk, zzctl> zzaEV = zzctg.zzajS;
  private final Context mContext;
  private final Handler mHandler;
  private final Api.zza<? extends zzctk, zzctl> zzaAx;
  private zzq zzaCA;
  private zzctk zzaDh;
  private final boolean zzaEW;
  private zzbel zzaEX;
  private Set<Scope> zzame;
  
  @WorkerThread
  public zzbej(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.zzaAx = zzaEV;
    this.zzaEW = true;
  }
  
  @WorkerThread
  public zzbej(Context paramContext, Handler paramHandler, @NonNull zzq paramzzq, Api.zza<? extends zzctk, zzctl> paramzza)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.zzaCA = ((zzq)zzbo.zzb(paramzzq, "ClientSettings must not be null"));
    this.zzame = paramzzq.zzrn();
    this.zzaAx = paramzza;
    this.zzaEW = false;
  }
  
  @WorkerThread
  private final void zzc(zzctx paramzzctx)
  {
    Object localObject = paramzzctx.zzpz();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramzzctx.zzAx();
      paramzzctx = ((zzbr)localObject).zzpz();
      if (!paramzzctx.isSuccess())
      {
        localObject = String.valueOf(paramzzctx);
        Log.wtf("SignInCoordinator", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        this.zzaEX.zzh(paramzzctx);
        this.zzaDh.disconnect();
        return;
      }
      this.zzaEX.zzb(((zzbr)localObject).zzrH(), this.zzame);
    }
    for (;;)
    {
      this.zzaDh.disconnect();
      return;
      this.zzaEX.zzh((ConnectionResult)localObject);
    }
  }
  
  @WorkerThread
  public final void onConnected(@Nullable Bundle paramBundle)
  {
    this.zzaDh.zza(this);
  }
  
  @WorkerThread
  public final void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
  {
    this.zzaEX.zzh(paramConnectionResult);
  }
  
  @WorkerThread
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzaDh.disconnect();
  }
  
  @WorkerThread
  public final void zza(zzbel paramzzbel)
  {
    if (this.zzaDh != null) {
      this.zzaDh.disconnect();
    }
    if (this.zzaEW)
    {
      localObject = zzy.zzaj(this.mContext).zzmO();
      if (localObject != null) {
        break label142;
      }
    }
    label142:
    for (Object localObject = new HashSet();; localObject = new HashSet(((GoogleSignInOptions)localObject).zzmA()))
    {
      this.zzame = ((Set)localObject);
      this.zzaCA = new zzq(null, this.zzame, null, 0, null, null, null, zzctl.zzbCM);
      this.zzaCA.zzc(Integer.valueOf(System.identityHashCode(this)));
      this.zzaDh = ((zzctk)this.zzaAx.zza(this.mContext, this.mHandler.getLooper(), this.zzaCA, this.zzaCA.zzrt(), this, this));
      this.zzaEX = paramzzbel;
      this.zzaDh.connect();
      return;
    }
  }
  
  @BinderThread
  public final void zzb(zzctx paramzzctx)
  {
    this.mHandler.post(new zzbek(this, paramzzctx));
  }
  
  public final void zzqI()
  {
    if (this.zzaDh != null) {
      this.zzaDh.disconnect();
    }
  }
  
  public final zzctk zzqy()
  {
    return this.zzaDh;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbej.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */