package com.google.android.gms.measurement.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzaa;

class zzs
  extends BroadcastReceiver
{
  static final String fx = zzs.class.getName();
  private final zzx aqw;
  private boolean fy;
  private boolean fz;
  
  zzs(zzx paramzzx)
  {
    zzaa.zzy(paramzzx);
    this.aqw = paramzzx;
  }
  
  private Context getContext()
  {
    return this.aqw.getContext();
  }
  
  private zzq zzbwb()
  {
    return this.aqw.zzbwb();
  }
  
  @WorkerThread
  public boolean isRegistered()
  {
    this.aqw.zzzx();
    return this.fy;
  }
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.aqw.zzacj();
    paramContext = paramIntent.getAction();
    zzbwb().zzbxe().zzj("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      final boolean bool = this.aqw.zzbxv().zzagk();
      if (this.fz != bool)
      {
        this.fz = bool;
        this.aqw.zzbwa().zzm(new Runnable()
        {
          public void run()
          {
            zzs.zza(zzs.this).zzaw(bool);
          }
        });
      }
      return;
    }
    zzbwb().zzbxa().zzj("NetworkBroadcastReceiver received unknown action", paramContext);
  }
  
  @WorkerThread
  public void unregister()
  {
    this.aqw.zzacj();
    this.aqw.zzzx();
    if (!isRegistered()) {
      return;
    }
    zzbwb().zzbxe().log("Unregistering connectivity change receiver");
    this.fy = false;
    this.fz = false;
    Context localContext = getContext();
    try
    {
      localContext.unregisterReceiver(this);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      zzbwb().zzbwy().zzj("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
    }
  }
  
  @WorkerThread
  public void zzagh()
  {
    this.aqw.zzacj();
    this.aqw.zzzx();
    if (this.fy) {
      return;
    }
    getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    this.fz = this.aqw.zzbxv().zzagk();
    zzbwb().zzbxe().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.fz));
    this.fy = true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */