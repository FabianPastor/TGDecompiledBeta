package com.google.android.gms.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.common.internal.zzbq;

class zzchv
  extends BroadcastReceiver
{
  private static String zzdyg = zzchv.class.getName();
  private boolean mRegistered;
  private boolean zzdyh;
  private final zzcim zziwf;
  
  zzchv(zzcim paramzzcim)
  {
    zzbq.checkNotNull(paramzzcim);
    this.zziwf = paramzzcim;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.zziwf.zzxf();
    paramContext = paramIntent.getAction();
    this.zziwf.zzawy().zzazj().zzj("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      boolean bool = this.zziwf.zzbab().zzzs();
      if (this.zzdyh != bool)
      {
        this.zzdyh = bool;
        this.zziwf.zzawx().zzg(new zzchw(this, bool));
      }
      return;
    }
    this.zziwf.zzawy().zzazf().zzj("NetworkBroadcastReceiver received unknown action", paramContext);
  }
  
  public final void unregister()
  {
    this.zziwf.zzxf();
    this.zziwf.zzawx().zzve();
    this.zziwf.zzawx().zzve();
    if (!this.mRegistered) {
      return;
    }
    this.zziwf.zzawy().zzazj().log("Unregistering connectivity change receiver");
    this.mRegistered = false;
    this.zzdyh = false;
    Context localContext = this.zziwf.getContext();
    try
    {
      localContext.unregisterReceiver(this);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      this.zziwf.zzawy().zzazd().zzj("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
    }
  }
  
  public final void zzzp()
  {
    this.zziwf.zzxf();
    this.zziwf.zzawx().zzve();
    if (this.mRegistered) {
      return;
    }
    this.zziwf.getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    this.zzdyh = this.zziwf.zzbab().zzzs();
    this.zziwf.zzawy().zzazj().zzj("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzdyh));
    this.mRegistered = true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */