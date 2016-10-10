package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class zza
  implements ServiceConnection
{
  boolean uH = false;
  private final BlockingQueue<IBinder> uI = new LinkedBlockingQueue();
  
  public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    this.uI.add(paramIBinder);
  }
  
  public void onServiceDisconnected(ComponentName paramComponentName) {}
  
  public IBinder zza(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException, TimeoutException
  {
    zzac.zzhr("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
    if (this.uH) {
      throw new IllegalStateException("Cannot call get on this connection more than once");
    }
    this.uH = true;
    paramTimeUnit = (IBinder)this.uI.poll(paramLong, paramTimeUnit);
    if (paramTimeUnit == null) {
      throw new TimeoutException("Timed out waiting for the service connection");
    }
    return paramTimeUnit;
  }
  
  public IBinder zzapc()
    throws InterruptedException
  {
    zzac.zzhr("BlockingServiceConnection.getService() called on main thread");
    if (this.uH) {
      throw new IllegalStateException("Cannot call get on this connection more than once");
    }
    this.uH = true;
    return (IBinder)this.uI.take();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */