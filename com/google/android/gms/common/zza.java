package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class zza
  implements ServiceConnection
{
  private boolean zzfkp = false;
  private final BlockingQueue<IBinder> zzfkq = new LinkedBlockingQueue();
  
  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.zzfkq.add(paramIBinder);
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName) {}
  
  public final IBinder zza(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, TimeoutException
  {
    zzbq.zzgn("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
    if (this.zzfkp) {
      throw new IllegalStateException("Cannot call get on this connection more than once");
    }
    this.zzfkp = true;
    paramTimeUnit = (IBinder)this.zzfkq.poll(10000L, paramTimeUnit);
    if (paramTimeUnit == null) {
      throw new TimeoutException("Timed out waiting for the service connection");
    }
    return paramTimeUnit;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */