package com.google.android.gms.internal;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.measurement.AppMeasurement;

final class zzcie
  implements Runnable
{
  zzcie(zzcid paramzzcid, zzcim paramzzcim, long paramLong, Bundle paramBundle, Context paramContext, zzchm paramzzchm, BroadcastReceiver.PendingResult paramPendingResult) {}
  
  public final void run()
  {
    zzclp localzzclp = this.zzjdt.zzaws().zzag(this.zzjdt.zzawn().getAppId(), "_fot");
    if ((localzzclp != null) && ((localzzclp.mValue instanceof Long))) {}
    for (long l1 = ((Long)localzzclp.mValue).longValue();; l1 = 0L)
    {
      long l2 = this.zzjdu;
      if ((l1 > 0L) && ((l2 >= l1) || (l2 <= 0L))) {}
      for (l1 -= 1L;; l1 = l2)
      {
        if (l1 > 0L) {
          this.zzjdv.putLong("click_timestamp", l1);
        }
        AppMeasurement.getInstance(this.val$context).logEventInternal("auto", "_cmp", this.zzjdv);
        this.zzjdw.zzazj().log("Install campaign recorded");
        if (this.zzdop != null) {
          this.zzdop.finish();
        }
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */