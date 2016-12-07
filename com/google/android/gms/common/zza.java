package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class zza implements ServiceConnection {
    boolean zzawV = false;
    private final BlockingQueue<IBinder> zzawW = new LinkedBlockingQueue();

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.zzawW.add(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }

    public IBinder zza(long j, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        zzac.zzdo("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
        if (this.zzawV) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.zzawV = true;
        IBinder iBinder = (IBinder) this.zzawW.poll(j, timeUnit);
        if (iBinder != null) {
            return iBinder;
        }
        throw new TimeoutException("Timed out waiting for the service connection");
    }

    public IBinder zzuy() throws InterruptedException {
        zzac.zzdo("BlockingServiceConnection.getService() called on main thread");
        if (this.zzawV) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.zzawV = true;
        return (IBinder) this.zzawW.take();
    }
}
