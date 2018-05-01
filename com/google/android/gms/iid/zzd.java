package com.google.android.gms.iid;

import android.content.BroadcastReceiver.PendingResult;
import android.content.Intent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class zzd
{
  final Intent intent;
  private final BroadcastReceiver.PendingResult zzieu;
  private boolean zziev = false;
  private final ScheduledFuture<?> zziew;
  
  zzd(Intent paramIntent, BroadcastReceiver.PendingResult paramPendingResult, ScheduledExecutorService paramScheduledExecutorService)
  {
    this.intent = paramIntent;
    this.zzieu = paramPendingResult;
    this.zziew = paramScheduledExecutorService.schedule(new zze(this, paramIntent), 9500L, TimeUnit.MILLISECONDS);
  }
  
  final void finish()
  {
    try
    {
      if (!this.zziev)
      {
        this.zzieu.finish();
        this.zziew.cancel(false);
        this.zziev = true;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */