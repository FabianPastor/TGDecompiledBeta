package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb
  extends Service
{
  @VisibleForTesting
  final ExecutorService aDx = Executors.newSingleThreadExecutor();
  private int aeC;
  private int aeD = 0;
  MessengerCompat afZ = new MessengerCompat(new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i = MessengerCompat.zzc(paramAnonymousMessage);
      zzf.zzdj(zzb.this);
      zzb.this.getPackageManager();
      if ((i != zzf.agl) && (i != zzf.agk))
      {
        int j = zzf.agk;
        int k = zzf.agl;
        Log.w("FirebaseInstanceId", 77 + "Message from unexpected caller " + i + " mine=" + j + " appid=" + k);
        return;
      }
      zzb.this.zzm((Intent)paramAnonymousMessage.obj);
    }
  });
  private final Object zzakd = new Object();
  
  private void zzaf(Intent arg1)
  {
    if (??? != null) {
      WakefulBroadcastReceiver.completeWakefulIntent(???);
    }
    synchronized (this.zzakd)
    {
      this.aeD -= 1;
      if (this.aeD == 0) {
        zztk(this.aeC);
      }
      return;
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    if ((paramIntent != null) && ("com.google.firebase.INSTANCE_ID_EVENT".equals(paramIntent.getAction()))) {
      return this.afZ.getBinder();
    }
    return null;
  }
  
  public final int onStartCommand(final Intent paramIntent, int paramInt1, int paramInt2)
  {
    synchronized (this.zzakd)
    {
      this.aeC = paramInt2;
      this.aeD += 1;
      ??? = zzae(paramIntent);
      if (??? == null)
      {
        zzaf(paramIntent);
        return 2;
      }
    }
    if (zzag((Intent)???))
    {
      zzaf(paramIntent);
      return 2;
    }
    this.aDx.execute(new Runnable()
    {
      public void run()
      {
        zzb.this.zzm(localObject);
        zzb.zza(zzb.this, paramIntent);
      }
    });
    return 3;
  }
  
  protected abstract Intent zzae(Intent paramIntent);
  
  public boolean zzag(Intent paramIntent)
  {
    return false;
  }
  
  public abstract void zzm(Intent paramIntent);
  
  boolean zztk(int paramInt)
  {
    return stopSelfResult(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */