package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.stats.zza;
import java.util.HashMap;

final class zzai
  extends zzag
  implements Handler.Callback
{
  private final Context mApplicationContext;
  private final Handler mHandler;
  private final HashMap<zzah, zzaj> zzgam = new HashMap();
  private final zza zzgan;
  private final long zzgao;
  private final long zzgap;
  
  zzai(Context paramContext)
  {
    this.mApplicationContext = paramContext.getApplicationContext();
    this.mHandler = new Handler(paramContext.getMainLooper(), this);
    this.zzgan = zza.zzamc();
    this.zzgao = 5000L;
    this.zzgap = 300000L;
  }
  
  public final boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return false;
    case 0: 
      synchronized (this.zzgam)
      {
        paramMessage = (zzah)paramMessage.obj;
        ??? = (zzaj)this.zzgam.get(paramMessage);
        if ((??? != null) && (((zzaj)???).zzalm()))
        {
          if (((zzaj)???).isBound()) {
            ((zzaj)???).zzgj("GmsClientSupervisor");
          }
          this.zzgam.remove(paramMessage);
        }
        return true;
      }
    }
    for (;;)
    {
      synchronized (this.zzgam)
      {
        zzah localzzah = (zzah)paramMessage.obj;
        zzaj localzzaj = (zzaj)this.zzgam.get(localzzah);
        if ((localzzaj != null) && (localzzaj.getState() == 3))
        {
          paramMessage = String.valueOf(localzzah);
          Log.wtf("GmsClientSupervisor", String.valueOf(paramMessage).length() + 47 + "Timeout waiting for ServiceConnection callback " + paramMessage, new Exception());
          ??? = localzzaj.getComponentName();
          paramMessage = (Message)???;
          if (??? == null) {
            paramMessage = localzzah.getComponentName();
          }
          if (paramMessage == null)
          {
            paramMessage = new ComponentName(localzzah.getPackage(), "unknown");
            localzzaj.onServiceDisconnected(paramMessage);
          }
        }
        else
        {
          return true;
        }
      }
    }
  }
  
  protected final boolean zza(zzah paramzzah, ServiceConnection paramServiceConnection, String paramString)
  {
    zzbq.checkNotNull(paramServiceConnection, "ServiceConnection must not be null");
    for (;;)
    {
      zzaj localzzaj;
      synchronized (this.zzgam)
      {
        localzzaj = (zzaj)this.zzgam.get(paramzzah);
        if (localzzaj == null)
        {
          localzzaj = new zzaj(this, paramzzah);
          localzzaj.zza(paramServiceConnection, paramString);
          localzzaj.zzgi(paramString);
          this.zzgam.put(paramzzah, localzzaj);
          paramzzah = localzzaj;
          boolean bool = paramzzah.isBound();
          return bool;
        }
        this.mHandler.removeMessages(0, paramzzah);
        if (localzzaj.zza(paramServiceConnection))
        {
          paramzzah = String.valueOf(paramzzah);
          throw new IllegalStateException(String.valueOf(paramzzah).length() + 81 + "Trying to bind a GmsServiceConnection that was already connected before.  config=" + paramzzah);
        }
      }
      localzzaj.zza(paramServiceConnection, paramString);
      switch (localzzaj.getState())
      {
      case 1: 
        paramServiceConnection.onServiceConnected(localzzaj.getComponentName(), localzzaj.getBinder());
        paramzzah = localzzaj;
        break;
      case 2: 
        localzzaj.zzgi(paramString);
        paramzzah = localzzaj;
        break;
      default: 
        paramzzah = localzzaj;
      }
    }
  }
  
  protected final void zzb(zzah paramzzah, ServiceConnection paramServiceConnection, String paramString)
  {
    zzbq.checkNotNull(paramServiceConnection, "ServiceConnection must not be null");
    zzaj localzzaj;
    synchronized (this.zzgam)
    {
      localzzaj = (zzaj)this.zzgam.get(paramzzah);
      if (localzzaj == null)
      {
        paramzzah = String.valueOf(paramzzah);
        throw new IllegalStateException(String.valueOf(paramzzah).length() + 50 + "Nonexistent connection status for service config: " + paramzzah);
      }
    }
    if (!localzzaj.zza(paramServiceConnection))
    {
      paramzzah = String.valueOf(paramzzah);
      throw new IllegalStateException(String.valueOf(paramzzah).length() + 76 + "Trying to unbind a GmsServiceConnection  that was not bound before.  config=" + paramzzah);
    }
    localzzaj.zzb(paramServiceConnection, paramString);
    if (localzzaj.zzalm())
    {
      paramzzah = this.mHandler.obtainMessage(0, paramzzah);
      this.mHandler.sendMessageDelayed(paramzzah, this.zzgao);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */