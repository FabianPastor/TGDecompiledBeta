package com.google.android.gms.iid;

import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.stats.zza;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class zzh
  implements ServiceConnection
{
  private final Context zzair;
  private final Intent zzifb;
  private final ScheduledExecutorService zzifc;
  private final Queue<zzd> zzifd = new LinkedList();
  private zzf zzife;
  private boolean zziff = false;
  
  public zzh(Context paramContext, String paramString)
  {
    this(paramContext, paramString, new ScheduledThreadPoolExecutor(0));
  }
  
  private zzh(Context paramContext, String paramString, ScheduledExecutorService paramScheduledExecutorService)
  {
    this.zzair = paramContext.getApplicationContext();
    this.zzifb = new Intent(paramString).setPackage(this.zzair.getPackageName());
    this.zzifc = paramScheduledExecutorService;
  }
  
  private final void zzavd()
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "flush queue called");
      }
      for (;;)
      {
        if (this.zzifd.isEmpty()) {
          break label190;
        }
        if (Log.isLoggable("EnhancedIntentService", 3)) {
          Log.d("EnhancedIntentService", "found intent to be delivered");
        }
        if ((this.zzife == null) || (!this.zzife.isBinderAlive())) {
          break;
        }
        if (Log.isLoggable("EnhancedIntentService", 3)) {
          Log.d("EnhancedIntentService", "binder is alive, sending the intent.");
        }
        zzd localzzd = (zzd)this.zzifd.poll();
        this.zzife.zza(localzzd);
      }
      if (!Log.isLoggable("EnhancedIntentService", 3)) {
        break label156;
      }
    }
    finally {}
    boolean bool;
    if (!this.zziff)
    {
      bool = true;
      Log.d("EnhancedIntentService", 39 + "binder is dead. start connection? " + bool);
      label156:
      if (!this.zziff) {
        this.zziff = true;
      }
    }
    for (;;)
    {
      try
      {
        bool = zza.zzamc().zza(this.zzair, this.zzifb, this, 65);
        if (bool)
        {
          label190:
          return;
          bool = false;
          break;
        }
        Log.e("EnhancedIntentService", "binding to the service failed");
      }
      catch (SecurityException localSecurityException)
      {
        Log.e("EnhancedIntentService", "Exception while binding the service", localSecurityException);
        continue;
      }
      if (!this.zzifd.isEmpty()) {
        ((zzd)this.zzifd.poll()).finish();
      }
    }
  }
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    try
    {
      this.zziff = false;
      this.zzife = ((zzf)paramIBinder);
      if (Log.isLoggable("EnhancedIntentService", 3))
      {
        paramComponentName = String.valueOf(paramComponentName);
        Log.d("EnhancedIntentService", String.valueOf(paramComponentName).length() + 20 + "onServiceConnected: " + paramComponentName);
      }
      zzavd();
      return;
    }
    finally {}
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    if (Log.isLoggable("EnhancedIntentService", 3))
    {
      paramComponentName = String.valueOf(paramComponentName);
      Log.d("EnhancedIntentService", String.valueOf(paramComponentName).length() + 23 + "onServiceDisconnected: " + paramComponentName);
    }
    zzavd();
  }
  
  public final void zza(Intent paramIntent, BroadcastReceiver.PendingResult paramPendingResult)
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "new intent queued in the bind-strategy delivery");
      }
      this.zzifd.add(new zzd(paramIntent, paramPendingResult, this.zzifc));
      zzavd();
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */