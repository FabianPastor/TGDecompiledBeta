package com.google.android.gms.internal;

import android.os.Handler;
import java.util.concurrent.Executor;

public final class zzh
  implements zzw
{
  private final Executor zzr;
  
  public zzh(Handler paramHandler)
  {
    this.zzr = new zzi(this, paramHandler);
  }
  
  public final void zza(zzp<?> paramzzp, zzaa paramzzaa)
  {
    paramzzp.zzb("post-error");
    paramzzaa = zzt.zzc(paramzzaa);
    this.zzr.execute(new zzj(this, paramzzp, paramzzaa, null));
  }
  
  public final void zza(zzp<?> paramzzp, zzt<?> paramzzt)
  {
    zza(paramzzp, paramzzt, null);
  }
  
  public final void zza(zzp<?> paramzzp, zzt<?> paramzzt, Runnable paramRunnable)
  {
    paramzzp.zzk();
    paramzzp.zzb("post-response");
    this.zzr.execute(new zzj(this, paramzzp, paramzzt, paramRunnable));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */