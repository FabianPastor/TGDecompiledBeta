package com.google.firebase.iid;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Intent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class zzd
{
  final Intent intent;
  private final BroadcastReceiver.PendingResult zzbpu;
  private boolean zzbpv = false;
  private final ScheduledFuture<?> zzbpw;
  
  zzd(Intent paramIntent, BroadcastReceiver.PendingResult paramPendingResult, ScheduledExecutorService paramScheduledExecutorService)
  {
    this.intent = paramIntent;
    this.zzbpu = paramPendingResult;
    this.zzbpw = paramScheduledExecutorService.schedule(new zze(this, paramIntent), 9500L, TimeUnit.MILLISECONDS);
  }
  
  final void finish()
  {
    try
    {
      if (!this.zzbpv)
      {
        this.zzbpu.finish();
        this.zzbpw.cancel(false);
        this.zzbpv = true;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */