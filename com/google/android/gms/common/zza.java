package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzaa;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class zza implements ServiceConnection {
    boolean wM = false;
    private final BlockingQueue<IBinder> wN = new LinkedBlockingQueue();

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.wN.add(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }

    public IBinder zza(long j, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        zzaa.zzht("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
        if (this.wM) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.wM = true;
        IBinder iBinder = (IBinder) this.wN.poll(j, timeUnit);
        if (iBinder != null) {
            return iBinder;
        }
        throw new TimeoutException("Timed out waiting for the service connection");
    }

    public IBinder zzaqk() throws InterruptedException {
        zzaa.zzht("BlockingServiceConnection.getService() called on main thread");
        if (this.wM) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.wM = true;
        return (IBinder) this.wN.take();
    }
}
