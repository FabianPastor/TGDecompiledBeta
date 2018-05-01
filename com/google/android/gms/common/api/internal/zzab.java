package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.PendingResult.StatusListener;
import com.google.android.gms.common.api.Status;
import java.util.Map;

final class zzab
  implements PendingResult.StatusListener
{
  zzab(zzaa paramzzaa, BasePendingResult paramBasePendingResult) {}
  
  public final void onComplete(Status paramStatus)
  {
    zzaa.zza(this.zzgz).remove(this.zzgy);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzab.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */