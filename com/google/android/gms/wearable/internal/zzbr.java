package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.internal.Preconditions;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public final class zzbr
  extends zzej
{
  private final Object lock = new Object();
  @Nullable
  @GuardedBy("lock")
  private zzav zzcw;
  @Nullable
  @GuardedBy("lock")
  private zzbs zzda;
  
  public final void zza(int paramInt1, int paramInt2)
  {
    synchronized (this.lock)
    {
      zzbs localzzbs = this.zzda;
      zzav localzzav = new com/google/android/gms/wearable/internal/zzav;
      localzzav.<init>(paramInt1, paramInt2);
      this.zzcw = localzzav;
      if (localzzbs != null) {
        localzzbs.zzb(localzzav);
      }
      return;
    }
  }
  
  public final void zza(zzbs paramzzbs)
  {
    synchronized (this.lock)
    {
      this.zzda = ((zzbs)Preconditions.checkNotNull(paramzzbs));
      zzav localzzav = this.zzcw;
      if (localzzav != null) {
        paramzzbs.zzb(localzzav);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzbr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */