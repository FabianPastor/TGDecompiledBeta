package com.google.android.gms.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class zza implements ServiceConnection {
    private boolean zzfkp = false;
    private final BlockingQueue<IBinder> zzfkq = new LinkedBlockingQueue();

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.zzfkq.add(iBinder);
    }

    public final void onServiceDisconnected(ComponentName componentName) {
    }

    public final IBinder zza(long j, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        zzbq.zzgn("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
        if (this.zzfkp) {
            throw new IllegalStateException("Cannot call get on this connection more than once");
        }
        this.zzfkp = true;
        IBinder iBinder = (IBinder) this.zzfkq.poll(10000, timeUnit);
        if (iBinder != null) {
            return iBinder;
        }
        throw new TimeoutException("Timed out waiting for the service connection");
    }
}
