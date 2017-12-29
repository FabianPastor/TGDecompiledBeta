package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class zzak extends GoogleApiClient {
    private final String zzfqu;

    public zzak(String str) {
        this.zzfqu = str;
    }

    public ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException(this.zzfqu);
    }

    public void connect() {
        throw new UnsupportedOperationException(this.zzfqu);
    }

    public void disconnect() {
        throw new UnsupportedOperationException(this.zzfqu);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        throw new UnsupportedOperationException(this.zzfqu);
    }

    public boolean isConnected() {
        throw new UnsupportedOperationException(this.zzfqu);
    }

    public void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        throw new UnsupportedOperationException(this.zzfqu);
    }

    public void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        throw new UnsupportedOperationException(this.zzfqu);
    }
}
