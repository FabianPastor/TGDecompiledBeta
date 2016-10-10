package com.google.android.gms.common.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zzg;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class zzai<T extends IInterface>
  extends zzl<T>
{
  private final Api.zzg<T> Db;
  
  public zzai(Context paramContext, Looper paramLooper, int paramInt, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, zzh paramzzh, Api.zzg<T> paramzzg)
  {
    super(paramContext, paramLooper, paramInt, paramzzh, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.Db = paramzzg;
  }
  
  public Api.zzg<T> zzavk()
  {
    return this.Db;
  }
  
  protected void zzc(int paramInt, T paramT)
  {
    this.Db.zza(paramInt, paramT);
  }
  
  protected T zzh(IBinder paramIBinder)
  {
    return this.Db.zzh(paramIBinder);
  }
  
  protected String zzix()
  {
    return this.Db.zzix();
  }
  
  protected String zziy()
  {
    return this.Db.zziy();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */