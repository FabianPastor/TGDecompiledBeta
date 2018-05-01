package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import javax.annotation.concurrent.GuardedBy;

final class zzao
  extends zzbe
{
  zzao(zzam paramzzam, zzbc paramzzbc, BaseGmsClient.ConnectionProgressReportCallbacks paramConnectionProgressReportCallbacks)
  {
    super(paramzzbc);
  }
  
  @GuardedBy("mLock")
  public final void zzaq()
  {
    this.zzia.onReportServiceBinding(new ConnectionResult(16, null));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzao.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */