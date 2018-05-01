package com.google.android.gms.common.api.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.zze;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

final class zzdl
  implements IBinder.DeathRecipient, zzdm
{
  private final WeakReference<BasePendingResult<?>> zzfvl;
  private final WeakReference<zze> zzfvm;
  private final WeakReference<IBinder> zzfvn;
  
  private zzdl(BasePendingResult<?> paramBasePendingResult, zze paramzze, IBinder paramIBinder)
  {
    this.zzfvm = new WeakReference(paramzze);
    this.zzfvl = new WeakReference(paramBasePendingResult);
    this.zzfvn = new WeakReference(paramIBinder);
  }
  
  private final void zzajv()
  {
    Object localObject = (BasePendingResult)this.zzfvl.get();
    zze localzze = (zze)this.zzfvm.get();
    if ((localzze != null) && (localObject != null)) {
      localzze.remove(((PendingResult)localObject).zzagv().intValue());
    }
    localObject = (IBinder)this.zzfvn.get();
    if (localObject != null) {}
    try
    {
      ((IBinder)localObject).unlinkToDeath(this, 0);
      return;
    }
    catch (NoSuchElementException localNoSuchElementException) {}
  }
  
  public final void binderDied()
  {
    zzajv();
  }
  
  public final void zzc(BasePendingResult<?> paramBasePendingResult)
  {
    zzajv();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzdl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */