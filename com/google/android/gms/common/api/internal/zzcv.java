package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbt;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzcxa;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.internal.zzcxe;
import com.google.android.gms.internal.zzcxi;
import com.google.android.gms.internal.zzcxq;
import java.util.Set;

public final class zzcv
  extends zzcxi
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private static Api.zza<? extends zzcxd, zzcxe> zzfut = zzcxa.zzebg;
  private final Context mContext;
  private final Handler mHandler;
  private Set<Scope> zzehs;
  private final Api.zza<? extends zzcxd, zzcxe> zzfls;
  private zzr zzfpx;
  private zzcxd zzfrd;
  private zzcy zzfuu;
  
  public zzcv(Context paramContext, Handler paramHandler, zzr paramzzr)
  {
    this(paramContext, paramHandler, paramzzr, zzfut);
  }
  
  public zzcv(Context paramContext, Handler paramHandler, zzr paramzzr, Api.zza<? extends zzcxd, zzcxe> paramzza)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.zzfpx = ((zzr)zzbq.checkNotNull(paramzzr, "ClientSettings must not be null"));
    this.zzehs = paramzzr.zzakv();
    this.zzfls = paramzza;
  }
  
  private final void zzc(zzcxq paramzzcxq)
  {
    Object localObject = paramzzcxq.zzahf();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramzzcxq.zzbdi();
      paramzzcxq = ((zzbt)localObject).zzahf();
      if (!paramzzcxq.isSuccess())
      {
        localObject = String.valueOf(paramzzcxq);
        Log.wtf("SignInCoordinator", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        this.zzfuu.zzh(paramzzcxq);
        this.zzfrd.disconnect();
        return;
      }
      this.zzfuu.zzb(((zzbt)localObject).zzalp(), this.zzehs);
    }
    for (;;)
    {
      this.zzfrd.disconnect();
      return;
      this.zzfuu.zzh((ConnectionResult)localObject);
    }
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    this.zzfrd.zza(this);
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    this.zzfuu.zzh(paramConnectionResult);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzfrd.disconnect();
  }
  
  public final void zza(zzcy paramzzcy)
  {
    if (this.zzfrd != null) {
      this.zzfrd.disconnect();
    }
    this.zzfpx.zzc(Integer.valueOf(System.identityHashCode(this)));
    this.zzfrd = ((zzcxd)this.zzfls.zza(this.mContext, this.mHandler.getLooper(), this.zzfpx, this.zzfpx.zzalb(), this, this));
    this.zzfuu = paramzzcy;
    if ((this.zzehs == null) || (this.zzehs.isEmpty()))
    {
      this.mHandler.post(new zzcw(this));
      return;
    }
    this.zzfrd.connect();
  }
  
  public final zzcxd zzaje()
  {
    return this.zzfrd;
  }
  
  public final void zzajq()
  {
    if (this.zzfrd != null) {
      this.zzfrd.disconnect();
    }
  }
  
  public final void zzb(zzcxq paramzzcxq)
  {
    this.mHandler.post(new zzcx(this, paramzzcxq));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */