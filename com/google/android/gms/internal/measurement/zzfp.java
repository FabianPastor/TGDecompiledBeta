package com.google.android.gms.internal.measurement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.gms.common.internal.Preconditions;

class zzfp
  extends BroadcastReceiver
{
  private static final String zzaar = zzfp.class.getName();
  private boolean zzaas;
  private boolean zzaat;
  private final zzgl zzacr;
  
  zzfp(zzgl paramzzgl)
  {
    Preconditions.checkNotNull(paramzzgl);
    this.zzacr = paramzzgl;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.zzacr.zzch();
    paramContext = paramIntent.getAction();
    this.zzacr.zzgg().zzir().zzg("NetworkBroadcastReceiver received action", paramContext);
    if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramContext))
    {
      boolean bool = this.zzacr.zzjq().zzew();
      if (this.zzaat != bool)
      {
        this.zzaat = bool;
        this.zzacr.zzgf().zzc(new zzfq(this, bool));
      }
    }
    for (;;)
    {
      return;
      this.zzacr.zzgg().zzin().zzg("NetworkBroadcastReceiver received unknown action", paramContext);
    }
  }
  
  public final void unregister()
  {
    this.zzacr.zzch();
    this.zzacr.zzgf().zzab();
    this.zzacr.zzgf().zzab();
    if (!this.zzaas) {}
    for (;;)
    {
      return;
      this.zzacr.zzgg().zzir().log("Unregistering connectivity change receiver");
      this.zzaas = false;
      this.zzaat = false;
      Context localContext = this.zzacr.getContext();
      try
      {
        localContext.unregisterReceiver(this);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        this.zzacr.zzgg().zzil().zzg("Failed to unregister the network broadcast receiver", localIllegalArgumentException);
      }
    }
  }
  
  public final void zzet()
  {
    this.zzacr.zzch();
    this.zzacr.zzgf().zzab();
    if (this.zzaas) {}
    for (;;)
    {
      return;
      this.zzacr.getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
      this.zzaat = this.zzacr.zzjq().zzew();
      this.zzacr.zzgg().zzir().zzg("Registering connectivity change receiver. Network connected", Boolean.valueOf(this.zzaat));
      this.zzaas = true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */