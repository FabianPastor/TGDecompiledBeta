package com.google.android.gms.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.internal.zzcxs;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb
  extends Service
{
  private final Object mLock = new Object();
  final ExecutorService zzieo = Executors.newSingleThreadExecutor();
  private Binder zziep;
  private int zzieq;
  private int zzier = 0;
  
  private final void zzh(Intent arg1)
  {
    if (??? != null) {
      zzcxs.completeWakefulIntent(???);
    }
    synchronized (this.mLock)
    {
      this.zzier -= 1;
      if (this.zzier == 0) {
        stopSelfResult(this.zzieq);
      }
      return;
    }
  }
  
  public abstract void handleIntent(Intent paramIntent);
  
  public final IBinder onBind(Intent paramIntent)
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "Service received bind request");
      }
      if (this.zziep == null) {
        this.zziep = new zzf(this);
      }
      paramIntent = this.zziep;
      return paramIntent;
    }
    finally {}
  }
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.zzieq = paramInt2;
      this.zzier += 1;
      if (paramIntent == null)
      {
        zzh(paramIntent);
        return 2;
      }
    }
    this.zzieo.execute(new zzc(this, paramIntent, paramIntent));
    return 3;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */