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
import com.google.android.gms.auth.api.signin.internal.zzn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzaf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzr;
import java.util.HashSet;
import java.util.Set;

public class zzabr
  extends zzbam
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private static Api.zza<? extends zzbai, zzbaj> zzaDg = zzbah.zzaie;
  private final Context mContext;
  private final Handler mHandler;
  private zzg zzaAL;
  private zzbai zzaBs;
  private final boolean zzaDh;
  private zza zzaDi;
  private Set<Scope> zzakq;
  private final Api.zza<? extends zzbai, zzbaj> zzayH;
  
  @WorkerThread
  public zzabr(Context paramContext, Handler paramHandler)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.zzayH = zzaDg;
    this.zzaDh = true;
  }
  
  @WorkerThread
  public zzabr(Context paramContext, Handler paramHandler, zzg paramzzg, Api.zza<? extends zzbai, zzbaj> paramzza)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.zzaAL = paramzzg;
    this.zzakq = paramzzg.zzxL();
    this.zzayH = paramzza;
    this.zzaDh = false;
  }
  
  @WorkerThread
  private void zzc(zzbaw paramzzbaw)
  {
    Object localObject = paramzzbaw.zzyh();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramzzbaw.zzPW();
      paramzzbaw = ((zzaf)localObject).zzyh();
      if (!paramzzbaw.isSuccess())
      {
        localObject = String.valueOf(paramzzbaw);
        Log.wtf("SignInCoordinator", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        this.zzaDi.zzi(paramzzbaw);
        this.zzaBs.disconnect();
        return;
      }
      this.zzaDi.zzb(((zzaf)localObject).zzyg(), this.zzakq);
    }
    for (;;)
    {
      this.zzaBs.disconnect();
      return;
      this.zzaDi.zzi((ConnectionResult)localObject);
    }
  }
  
  @WorkerThread
  public void onConnected(@Nullable Bundle paramBundle)
  {
    this.zzaBs.zza(this);
  }
  
  @WorkerThread
  public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
  {
    this.zzaDi.zzi(paramConnectionResult);
  }
  
  @WorkerThread
  public void onConnectionSuspended(int paramInt)
  {
    this.zzaBs.disconnect();
  }
  
  @WorkerThread
  public void zza(zza paramzza)
  {
    if (this.zzaBs != null) {
      this.zzaBs.disconnect();
    }
    if (this.zzaDh)
    {
      localObject = zzn.zzas(this.mContext).zzrC();
      if (localObject != null) {
        break label128;
      }
    }
    label128:
    for (Object localObject = new HashSet();; localObject = new HashSet(((GoogleSignInOptions)localObject).zzrj()))
    {
      this.zzakq = ((Set)localObject);
      this.zzaAL = new zzg(null, this.zzakq, null, 0, null, null, null, zzbaj.zzbEl);
      this.zzaBs = ((zzbai)this.zzayH.zza(this.mContext, this.mHandler.getLooper(), this.zzaAL, this.zzaAL.zzxR(), this, this));
      this.zzaDi = paramzza;
      this.zzaBs.connect();
      return;
    }
  }
  
  @BinderThread
  public void zzb(final zzbaw paramzzbaw)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        zzabr.zza(zzabr.this, paramzzbaw);
      }
    });
  }
  
  public zzbai zzwO()
  {
    return this.zzaBs;
  }
  
  public void zzwY()
  {
    this.zzaBs.disconnect();
  }
  
  @WorkerThread
  public static abstract interface zza
  {
    public abstract void zzb(zzr paramzzr, Set<Scope> paramSet);
    
    public abstract void zzi(ConnectionResult paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */