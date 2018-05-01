package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import javax.annotation.concurrent.GuardedBy;

final class zzan
  extends zzbe
{
  zzan(zzam paramzzam, zzbc paramzzbc, ConnectionResult paramConnectionResult)
  {
    super(paramzzbc);
  }
  
  @GuardedBy("mLock")
  public final void zzaq()
  {
    zzaj.zza(this.zzhz.zzhv, this.zzhy);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */