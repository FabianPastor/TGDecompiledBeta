package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import javax.annotation.Nullable;

final class zzad
  extends BroadcastReceiver
{
  @Nullable
  private zzac zzbsf;
  
  public zzad(zzac paramzzac)
  {
    this.zzbsf = paramzzac;
  }
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    if (this.zzbsf == null) {}
    for (;;)
    {
      return;
      if (this.zzbsf.zztg())
      {
        if (FirebaseInstanceId.zzsj()) {
          Log.d("FirebaseInstanceId", "Connectivity changed. Starting background sync.");
        }
        FirebaseInstanceId.zza(this.zzbsf, 0L);
        this.zzbsf.getContext().unregisterReceiver(this);
        this.zzbsf = null;
      }
    }
  }
  
  public final void zzth()
  {
    if (FirebaseInstanceId.zzsj()) {
      Log.d("FirebaseInstanceId", "Connectivity change received registered");
    }
    IntentFilter localIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    this.zzbsf.getContext().registerReceiver(this, localIntentFilter);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */