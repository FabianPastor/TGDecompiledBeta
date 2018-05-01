package com.google.android.gms.common.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.stats.ConnectionTracker;
import java.util.HashMap;
import javax.annotation.concurrent.GuardedBy;

final class zzh
  extends GmsClientSupervisor
  implements Handler.Callback
{
  private final Handler mHandler;
  private final Context zzau;
  @GuardedBy("mConnectionStatus")
  private final HashMap<GmsClientSupervisor.ConnectionStatusConfig, zzi> zztr = new HashMap();
  private final ConnectionTracker zzts;
  private final long zztt;
  private final long zztu;
  
  zzh(Context paramContext)
  {
    this.zzau = paramContext.getApplicationContext();
    this.mHandler = new Handler(paramContext.getMainLooper(), this);
    this.zzts = ConnectionTracker.getInstance();
    this.zztt = 5000L;
    this.zztu = 300000L;
  }
  
  protected final boolean bindService(GmsClientSupervisor.ConnectionStatusConfig paramConnectionStatusConfig, ServiceConnection paramServiceConnection, String paramString)
  {
    Preconditions.checkNotNull(paramServiceConnection, "ServiceConnection must not be null");
    for (;;)
    {
      zzi localzzi;
      synchronized (this.zztr)
      {
        localzzi = (zzi)this.zztr.get(paramConnectionStatusConfig);
        if (localzzi == null)
        {
          localzzi = new com/google/android/gms/common/internal/zzi;
          localzzi.<init>(this, paramConnectionStatusConfig);
          localzzi.zza(paramServiceConnection, paramString);
          localzzi.zzj(paramString);
          this.zztr.put(paramConnectionStatusConfig, localzzi);
          paramConnectionStatusConfig = localzzi;
          boolean bool = paramConnectionStatusConfig.isBound();
          return bool;
        }
        this.mHandler.removeMessages(0, paramConnectionStatusConfig);
        if (localzzi.zza(paramServiceConnection))
        {
          paramServiceConnection = new java/lang/IllegalStateException;
          paramString = String.valueOf(paramConnectionStatusConfig);
          int i = String.valueOf(paramString).length();
          paramConnectionStatusConfig = new java/lang/StringBuilder;
          paramConnectionStatusConfig.<init>(i + 81);
          paramServiceConnection.<init>("Trying to bind a GmsServiceConnection that was already connected before.  config=" + paramString);
          throw paramServiceConnection;
        }
      }
      localzzi.zza(paramServiceConnection, paramString);
      switch (localzzi.getState())
      {
      default: 
        paramConnectionStatusConfig = localzzi;
        break;
      case 1: 
        paramServiceConnection.onServiceConnected(localzzi.getComponentName(), localzzi.getBinder());
        paramConnectionStatusConfig = localzzi;
        break;
      case 2: 
        localzzi.zzj(paramString);
        paramConnectionStatusConfig = localzzi;
      }
    }
  }
  
  public final boolean handleMessage(Message paramMessage)
  {
    boolean bool;
    switch (paramMessage.what)
    {
    default: 
      bool = false;
    case 0: 
      for (;;)
      {
        return bool;
        synchronized (this.zztr)
        {
          paramMessage = (GmsClientSupervisor.ConnectionStatusConfig)paramMessage.obj;
          ??? = (zzi)this.zztr.get(paramMessage);
          if ((??? != null) && (((zzi)???).zzcv()))
          {
            if (((zzi)???).isBound()) {
              ((zzi)???).zzk("GmsClientSupervisor");
            }
            this.zztr.remove(paramMessage);
          }
          bool = true;
        }
      }
    }
    for (;;)
    {
      synchronized (this.zztr)
      {
        GmsClientSupervisor.ConnectionStatusConfig localConnectionStatusConfig = (GmsClientSupervisor.ConnectionStatusConfig)paramMessage.obj;
        zzi localzzi = (zzi)this.zztr.get(localConnectionStatusConfig);
        if ((localzzi != null) && (localzzi.getState() == 3))
        {
          ??? = String.valueOf(localConnectionStatusConfig);
          int i = String.valueOf(???).length();
          paramMessage = new java/lang/StringBuilder;
          paramMessage.<init>(i + 47);
          ??? = "Timeout waiting for ServiceConnection callback " + (String)???;
          paramMessage = new java/lang/Exception;
          paramMessage.<init>();
          Log.wtf("GmsClientSupervisor", (String)???, paramMessage);
          ??? = localzzi.getComponentName();
          paramMessage = (Message)???;
          if (??? == null) {
            paramMessage = localConnectionStatusConfig.getComponentName();
          }
          if (paramMessage == null)
          {
            paramMessage = new android/content/ComponentName;
            paramMessage.<init>(localConnectionStatusConfig.getPackage(), "unknown");
            localzzi.onServiceDisconnected(paramMessage);
          }
        }
        else
        {
          bool = true;
        }
      }
    }
  }
  
  protected final void unbindService(GmsClientSupervisor.ConnectionStatusConfig paramConnectionStatusConfig, ServiceConnection paramServiceConnection, String paramString)
  {
    Preconditions.checkNotNull(paramServiceConnection, "ServiceConnection must not be null");
    zzi localzzi;
    int i;
    synchronized (this.zztr)
    {
      localzzi = (zzi)this.zztr.get(paramConnectionStatusConfig);
      if (localzzi == null)
      {
        paramServiceConnection = new java/lang/IllegalStateException;
        paramConnectionStatusConfig = String.valueOf(paramConnectionStatusConfig);
        i = String.valueOf(paramConnectionStatusConfig).length();
        paramString = new java/lang/StringBuilder;
        paramString.<init>(i + 50);
        paramServiceConnection.<init>("Nonexistent connection status for service config: " + paramConnectionStatusConfig);
        throw paramServiceConnection;
      }
    }
    if (!localzzi.zza(paramServiceConnection))
    {
      paramServiceConnection = new java/lang/IllegalStateException;
      paramConnectionStatusConfig = String.valueOf(paramConnectionStatusConfig);
      i = String.valueOf(paramConnectionStatusConfig).length();
      paramString = new java/lang/StringBuilder;
      paramString.<init>(i + 76);
      paramServiceConnection.<init>("Trying to unbind a GmsServiceConnection  that was not bound before.  config=" + paramConnectionStatusConfig);
      throw paramServiceConnection;
    }
    localzzi.zzb(paramServiceConnection, paramString);
    if (localzzi.zzcv())
    {
      paramConnectionStatusConfig = this.mHandler.obtainMessage(0, paramConnectionStatusConfig);
      this.mHandler.sendMessageDelayed(paramConnectionStatusConfig, this.zztt);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */