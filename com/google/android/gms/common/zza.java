package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class zza implements ServiceConnection {
    private boolean zzazV = false;
    private final BlockingQueue<IBinder> zzazW = new LinkedBlockingQueue();

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.zzazW.add(iBinder);
    }

    public final void onServiceDisconnected(ComponentName componentName) {
    }

    public final IBinder zza(long j, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        zzbo.zzcG("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
        if (this.zzazV) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.zzazV = true;
        IBinder iBinder = (IBinder) this.zzazW.poll(10000, timeUnit);
        if (iBinder != null) {
            return iBinder;
        }
        throw new TimeoutException("Timed out waiting for the service connection");
    }

    public final IBinder zzoV() throws InterruptedException {
        zzbo.zzcG("BlockingServiceConnection.getService() called on main thread");
        if (this.zzazV) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.zzazV = true;
        return (IBinder) this.zzazW.take();
    }
}
