package com.google.android.gms.measurement.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;

class zzr
  extends BroadcastReceiver
{
  static final String dq = zzr.class.getName();
  private final zzx anq;
  private boolean dr;
  private boolean ds;
  
  zzr(zzx paramzzx)
  {
    zzac.zzy(paramzzx);
    this.anq = paramzzx;
  }
  
  private Context getContext()
  {
    return this.anq.getContext();
  }
  
  private zzp zzbvg()
  {
    return this.anq.zzbvg();
  }
  
  @WorkerThread
  public boolean isRegistered()
  {
    this.anq.zzyl();
    return this.dr;
  }
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.anq.zzaax();
    paramContext = paramIntent.getAction();
    zzbvg().zzbwj().zzj("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      final boolean bool = this.anq.zzbxa().zzafa();
      if (this.ds != bool)
      {
        this.ds = bool;
        this.anq.zzbvf().zzm(new Runnable()
        {
          public void run()
          {
            zzr.zza(zzr.this).zzav(bool);
          }
        });
      }
      return;
    }
    zzbvg().zzbwe().zzj("NetworkBroadcastReceiver received unknown action", paramContext);
  }
  
  @WorkerThread
  public void unregister()
  {
    this.anq.zzaax();
    this.anq.zzyl();
    if (!isRegistered()) {
      return;
    }
    zzbvg().zzbwj().log("Unregistering connectivity change receiver");
    this.dr = false;
    this.ds = false;
    Context localContext = getContext();
    try
    {
      localContext.unregisterReceiver(this);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      zzbvg().zzbwc().zzj("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
    }
  }
  
  @WorkerThread
  public void zzaex()
  {
    this.anq.zzaax();
    this.anq.zzyl();
    if (this.dr) {
      return;
    }
    getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    this.ds = this.anq.zzbxa().zzafa();
    zzbvg().zzbwj().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.ds));
    this.dr = true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */