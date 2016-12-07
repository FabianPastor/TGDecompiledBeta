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

public class zzaah extends GoogleApiClient {
    private final UnsupportedOperationException zzazJ;

    public zzaah(String str) {
        this.zzazJ = new UnsupportedOperationException(str);
    }

    public ConnectionResult blockingConnect() {
        throw this.zzazJ;
    }

    public ConnectionResult blockingConnect(long j, @NonNull TimeUnit timeUnit) {
        throw this.zzazJ;
    }

    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        throw this.zzazJ;
    }

    public void connect() {
        throw this.zzazJ;
    }

    public void disconnect() {
        throw this.zzazJ;
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        throw this.zzazJ;
    }

    @NonNull
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        throw this.zzazJ;
    }

    public boolean hasConnectedApi(@NonNull Api<?> api) {
        throw this.zzazJ;
    }

    public boolean isConnected() {
        throw this.zzazJ;
    }

    public boolean isConnecting() {
        throw this.zzazJ;
    }

    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.zzazJ;
    }

    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.zzazJ;
    }

    public void reconnect() {
        throw this.zzazJ;
    }

    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.zzazJ;
    }

    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.zzazJ;
    }

    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        throw this.zzazJ;
    }

    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw this.zzazJ;
    }

    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw this.zzazJ;
    }
}
