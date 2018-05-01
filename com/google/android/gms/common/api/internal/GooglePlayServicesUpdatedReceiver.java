package com.google.android.gms.common.api.internal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class GooglePlayServicesUpdatedReceiver
  extends BroadcastReceiver
{
  private Context mContext;
  private final Callback zzkt;
  
  public GooglePlayServicesUpdatedReceiver(Callback paramCallback)
  {
    this.zzkt = paramCallback;
  }
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    paramIntent = paramIntent.getData();
    paramContext = null;
    if (paramIntent != null) {
      paramContext = paramIntent.getSchemeSpecificPart();
    }
    if ("com.google.android.gms".equals(paramContext))
    {
      this.zzkt.zzv();
      unregister();
    }
  }
  
  public final void unregister()
  {
    try
    {
      if (this.mContext != null) {
        this.mContext.unregisterReceiver(this);
      }
      this.mContext = null;
      return;
    }
    finally {}
  }
  
  public final void zzc(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public static abstract class Callback
  {
    public abstract void zzv();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/GooglePlayServicesUpdatedReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */