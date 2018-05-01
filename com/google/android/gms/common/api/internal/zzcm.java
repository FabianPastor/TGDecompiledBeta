package com.google.android.gms.common.api.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.zzc;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

final class zzcm
  implements IBinder.DeathRecipient, zzcn
{
  private final WeakReference<BasePendingResult<?>> zzmr;
  private final WeakReference<zzc> zzms;
  private final WeakReference<IBinder> zzmt;
  
  private zzcm(BasePendingResult<?> paramBasePendingResult, zzc paramzzc, IBinder paramIBinder)
  {
    this.zzms = new WeakReference(paramzzc);
    this.zzmr = new WeakReference(paramBasePendingResult);
    this.zzmt = new WeakReference(paramIBinder);
  }
  
  private final void zzcf()
  {
    Object localObject = (BasePendingResult)this.zzmr.get();
    zzc localzzc = (zzc)this.zzms.get();
    if ((localzzc != null) && (localObject != null)) {
      localzzc.remove(((PendingResult)localObject).zzo().intValue());
    }
    localObject = (IBinder)this.zzmt.get();
    if (localObject != null) {}
    try
    {
      ((IBinder)localObject).unlinkToDeath(this, 0);
      return;
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      for (;;) {}
    }
  }
  
  public final void binderDied()
  {
    zzcf();
  }
  
  public final void zzc(BasePendingResult<?> paramBasePendingResult)
  {
    zzcf();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */