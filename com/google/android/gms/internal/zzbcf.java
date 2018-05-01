package com.google.android.gms.internal;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;

final class zzbcf
  implements zzj
{
  private final boolean zzaCj;
  private final WeakReference<zzbcd> zzaDq;
  private final Api<?> zzayW;
  
  public zzbcf(zzbcd paramzzbcd, Api<?> paramApi, boolean paramBoolean)
  {
    this.zzaDq = new WeakReference(paramzzbcd);
    this.zzayW = paramApi;
    this.zzaCj = paramBoolean;
  }
  
  public final void zzf(@NonNull ConnectionResult paramConnectionResult)
  {
    boolean bool = false;
    zzbcd localzzbcd = (zzbcd)this.zzaDq.get();
    if (localzzbcd == null) {
      return;
    }
    if (Looper.myLooper() == zzbcd.zzd(localzzbcd).zzaCl.getLooper()) {
      bool = true;
    }
    zzbo.zza(bool, "onReportServiceBinding must be called on the GoogleApiClient handler thread");
    zzbcd.zzc(localzzbcd).lock();
    try
    {
      bool = zzbcd.zza(localzzbcd, 0);
      if (!bool) {
        return;
      }
      if (!paramConnectionResult.isSuccess()) {
        zzbcd.zza(localzzbcd, paramConnectionResult, this.zzayW, this.zzaCj);
      }
      if (zzbcd.zzk(localzzbcd)) {
        zzbcd.zzj(localzzbcd);
      }
      return;
    }
    finally
    {
      zzbcd.zzc(localzzbcd).unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */