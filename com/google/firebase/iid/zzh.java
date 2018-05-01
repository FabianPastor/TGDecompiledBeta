package com.google.firebase.iid;

import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.stats.ConnectionTracker;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class zzh
  implements ServiceConnection
{
  private final Intent zzbqb;
  private final ScheduledExecutorService zzbqc;
  private final Queue<zzd> zzbqd = new ArrayDeque();
  private zzf zzbqe;
  private boolean zzbqf = false;
  private final Context zzqs;
  
  public zzh(Context paramContext, String paramString)
  {
    this(paramContext, paramString, new ScheduledThreadPoolExecutor(0));
  }
  
  private zzh(Context paramContext, String paramString, ScheduledExecutorService paramScheduledExecutorService)
  {
    this.zzqs = paramContext.getApplicationContext();
    this.zzbqb = new Intent(paramString).setPackage(this.zzqs.getPackageName());
    this.zzbqc = paramScheduledExecutorService;
  }
  
  private final void zzsd()
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "flush queue called");
      }
      for (;;)
      {
        if (this.zzbqd.isEmpty()) {
          break label192;
        }
        if (Log.isLoggable("EnhancedIntentService", 3)) {
          Log.d("EnhancedIntentService", "found intent to be delivered");
        }
        if ((this.zzbqe == null) || (!this.zzbqe.isBinderAlive())) {
          break;
        }
        if (Log.isLoggable("EnhancedIntentService", 3)) {
          Log.d("EnhancedIntentService", "binder is alive, sending the intent.");
        }
        zzd localzzd = (zzd)this.zzbqd.poll();
        this.zzbqe.zza(localzzd);
      }
      if (!Log.isLoggable("EnhancedIntentService", 3)) {
        break label158;
      }
    }
    finally {}
    boolean bool;
    if (!this.zzbqf)
    {
      bool = true;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>(39);
      Log.d("EnhancedIntentService", "binder is dead. start connection? " + bool);
      label158:
      if (!this.zzbqf) {
        this.zzbqf = true;
      }
    }
    for (;;)
    {
      try
      {
        bool = ConnectionTracker.getInstance().bindService(this.zzqs, this.zzbqb, this, 65);
        if (bool)
        {
          label192:
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
      if (!this.zzbqd.isEmpty()) {
        ((zzd)this.zzbqd.poll()).finish();
      }
    }
  }
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    try
    {
      this.zzbqf = false;
      this.zzbqe = ((zzf)paramIBinder);
      if (Log.isLoggable("EnhancedIntentService", 3))
      {
        paramComponentName = String.valueOf(paramComponentName);
        int i = String.valueOf(paramComponentName).length();
        paramIBinder = new java/lang/StringBuilder;
        paramIBinder.<init>(i + 20);
        Log.d("EnhancedIntentService", "onServiceConnected: " + paramComponentName);
      }
      zzsd();
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
    zzsd();
  }
  
  public final void zza(Intent paramIntent, BroadcastReceiver.PendingResult paramPendingResult)
  {
    try
    {
      if (Log.isLoggable("EnhancedIntentService", 3)) {
        Log.d("EnhancedIntentService", "new intent queued in the bind-strategy delivery");
      }
      Queue localQueue = this.zzbqd;
      zzd localzzd = new com/google/firebase/iid/zzd;
      localzzd.<init>(paramIntent, paramPendingResult, this.zzbqc);
      localQueue.add(localzzd);
      zzsd();
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */