package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class zzqz extends GoogleApiClient {
    private final UnsupportedOperationException zz;

    public zzqz(String str) {
        this.zz = new UnsupportedOperationException(str);
    }

    public ConnectionResult blockingConnect() {
        throw this.zz;
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw this.zz;
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        throw this.zz;
    }

    public void connect() {
        throw this.zz;
    }

    public void disconnect() {
        throw this.zz;
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        throw this.zz;
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        throw this.zz;
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        throw this.zz;
    }

    public boolean isConnected() {
        throw this.zz;
    }

    public boolean isConnecting() {
        throw this.zz;
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.zz;
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.zz;
    }

    public void reconnect() {
        throw this.zz;
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.zz;
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.zz;
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        throw this.zz;
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.zz;
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.zz;
    }
}
