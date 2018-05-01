package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;

class zzatz
  extends BroadcastReceiver
{
  static final String zzagv = zzatz.class.getName();
  private boolean zzagw;
  private boolean zzagx;
  private final zzaue zzbqb;
  
  zzatz(zzaue paramzzaue)
  {
    zzac.zzw(paramzzaue);
    this.zzbqb = paramzzaue;
  }
  
  private Context getContext()
  {
    return this.zzbqb.getContext();
  }
  
  private zzatx zzKl()
  {
    return this.zzbqb.zzKl();
  }
  
  @WorkerThread
  public boolean isRegistered()
  {
    this.zzbqb.zzmR();
    return this.zzagw;
  }
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.zzbqb.zzob();
    paramContext = paramIntent.getAction();
    zzKl().zzMf().zzj("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      final boolean bool = this.zzbqb.zzMz().zzqa();
      if (this.zzagx != bool)
      {
        this.zzagx = bool;
        this.zzbqb.zzKk().zzm(new Runnable()
        {
          public void run()
          {
            zzatz.zza(zzatz.this).zzV(bool);
          }
        });
      }
      return;
    }
    zzKl().zzMb().zzj("NetworkBroadcastReceiver received unknown action", paramContext);
  }
  
  @WorkerThread
  public void unregister()
  {
    this.zzbqb.zzob();
    this.zzbqb.zzmR();
    if (!isRegistered()) {
      return;
    }
    zzKl().zzMf().log("Unregistering connectivity change receiver");
    this.zzagw = false;
    this.zzagx = false;
    Context localContext = getContext();
    try
    {
      localContext.unregisterReceiver(this);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      zzKl().zzLZ().zzj("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
    }
  }
  
  @WorkerThread
  public void zzpX()
  {
    this.zzbqb.zzob();
    this.zzbqb.zzmR();
    if (this.zzagw) {
      return;
    }
    getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    this.zzagx = this.zzbqb.zzMz().zzqa();
    zzKl().zzMf().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzagx));
    this.zzagw = true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */