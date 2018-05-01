package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.common.util.concurrent.NamedThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb
  extends Service
{
  private final Object lock;
  final ExecutorService zzall;
  private Binder zzbpo;
  private int zzbpp;
  private int zzbpq;
  
  public zzb()
  {
    String str = String.valueOf(getClass().getSimpleName());
    if (str.length() != 0) {}
    for (str = "Firebase-".concat(str);; str = new String("Firebase-"))
    {
      this.zzall = Executors.newSingleThreadExecutor(new NamedThreadFactory(str));
      this.lock = new Object();
      this.zzbpq = 0;
      return;
    }
  }
  
  private final void zze(Intent paramIntent)
  {
    if (paramIntent != null) {
      WakefulBroadcastReceiver.completeWakefulIntent(paramIntent);
    }
    synchronized (this.lock)
    {
      this.zzbpq -= 1;
      if (this.zzbpq == 0) {
        stopSelfResult(this.zzbpp);
      }
      return;
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "Service received bind request");
      }
      if (this.zzbpo == null)
      {
        paramIntent = new com/google/firebase/iid/zzf;
        paramIntent.<init>(this);
        this.zzbpo = paramIntent;
      }
      paramIntent = this.zzbpo;
      return paramIntent;
    }
    finally {}
  }
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    paramInt1 = 2;
    for (;;)
    {
      synchronized (this.lock)
      {
        this.zzbpp = paramInt2;
        this.zzbpq += 1;
        ??? = zzf(paramIntent);
        if (??? == null)
        {
          zze(paramIntent);
          return paramInt1;
        }
      }
      if (zzg((Intent)???))
      {
        zze(paramIntent);
      }
      else
      {
        this.zzall.execute(new zzc(this, (Intent)???, paramIntent));
        paramInt1 = 3;
      }
    }
  }
  
  protected Intent zzf(Intent paramIntent)
  {
    return paramIntent;
  }
  
  public boolean zzg(Intent paramIntent)
  {
    return false;
  }
  
  public abstract void zzh(Intent paramIntent);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */