package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;

public final class zzbdw<L>
{
  private volatile L mListener;
  private final zzbdx zzaEM;
  private final zzbdy<L> zzaEN;
  
  zzbdw(@NonNull Looper paramLooper, @NonNull L paramL, @NonNull String paramString)
  {
    this.zzaEM = new zzbdx(this, paramLooper);
    this.mListener = zzbo.zzb(paramL, "Listener must not be null");
    this.zzaEN = new zzbdy(paramL, zzbo.zzcF(paramString));
  }
  
  public final void clear()
  {
    this.mListener = null;
  }
  
  public final void zza(zzbdz<? super L> paramzzbdz)
  {
    zzbo.zzb(paramzzbdz, "Notifier must not be null");
    paramzzbdz = this.zzaEM.obtainMessage(1, paramzzbdz);
    this.zzaEM.sendMessage(paramzzbdz);
  }
  
  final void zzb(zzbdz<? super L> paramzzbdz)
  {
    Object localObject = this.mListener;
    if (localObject == null)
    {
      paramzzbdz.zzpT();
      return;
    }
    try
    {
      paramzzbdz.zzq(localObject);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      paramzzbdz.zzpT();
      throw localRuntimeException;
    }
  }
  
  public final boolean zzoc()
  {
    return this.mListener != null;
  }
  
  @NonNull
  public final zzbdy<L> zzqG()
  {
    return this.zzaEN;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */