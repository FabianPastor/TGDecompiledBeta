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

public class zzql extends GoogleApiClient {
    private final UnsupportedOperationException xj;

    public zzql(String str) {
        this.xj = new UnsupportedOperationException(str);
    }

    public ConnectionResult blockingConnect() {
        throw this.xj;
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw this.xj;
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        throw this.xj;
    }

    public void connect() {
        throw this.xj;
    }

    public void disconnect() {
        throw this.xj;
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        throw this.xj;
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        throw this.xj;
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        throw this.xj;
    }

    public boolean isConnected() {
        throw this.xj;
    }

    public boolean isConnecting() {
        throw this.xj;
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.xj;
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.xj;
    }

    public void reconnect() {
        throw this.xj;
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.xj;
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.xj;
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        throw this.xj;
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.xj;
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.xj;
    }
}
