package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

class zzcfu
  extends BroadcastReceiver
{
  private static String zzaiq = zzcfu.class.getName();
  private boolean mRegistered;
  private boolean zzair;
  private final zzcgl zzboe;
  
  zzcfu(zzcgl paramzzcgl)
  {
    zzbo.zzu(paramzzcgl);
    this.zzboe = paramzzcgl;
  }
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.zzboe.zzkD();
    paramContext = paramIntent.getAction();
    this.zzboe.zzwF().zzyD().zzj("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      boolean bool = this.zzboe.zzyU().zzlQ();
      if (this.zzair != bool)
      {
        this.zzair = bool;
        this.zzboe.zzwE().zzj(new zzcfv(this, bool));
      }
      return;
    }
    this.zzboe.zzwF().zzyz().zzj("NetworkBroadcastReceiver received unknown action", paramContext);
  }
  
  @WorkerThread
  public final void unregister()
  {
    this.zzboe.zzkD();
    this.zzboe.zzwE().zzjC();
    this.zzboe.zzwE().zzjC();
    if (!this.mRegistered) {
      return;
    }
    this.zzboe.zzwF().zzyD().log("Unregistering connectivity change receiver");
    this.mRegistered = false;
    this.zzair = false;
    Context localContext = this.zzboe.getContext();
    try
    {
      localContext.unregisterReceiver(this);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      this.zzboe.zzwF().zzyx().zzj("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
    }
  }
  
  @WorkerThread
  public final void zzlN()
  {
    this.zzboe.zzkD();
    this.zzboe.zzwE().zzjC();
    if (this.mRegistered) {
      return;
    }
    this.zzboe.getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    this.zzair = this.zzboe.zzyU().zzlQ();
    this.zzboe.zzwF().zzyD().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzair));
    this.mRegistered = true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */