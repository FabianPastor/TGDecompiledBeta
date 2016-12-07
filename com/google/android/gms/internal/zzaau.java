package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public interface zzaau {

    public interface zza {
        void zzc(int i, boolean z);

        void zzc(ConnectionResult connectionResult);

        void zzo(Bundle bundle);
    }

    ConnectionResult blockingConnect();

    ConnectionResult blockingConnect(long j, TimeUnit timeUnit);

    void connect();

    void disconnect();

    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    @Nullable
    ConnectionResult getConnectionResult(@NonNull Api<?> api);

    boolean isConnected();

    boolean isConnecting();

    <A extends zzb, R extends Result, T extends com.google.android.gms.internal.zzzv.zza<R, A>> T zza(@NonNull T t);

    boolean zza(zzabi com_google_android_gms_internal_zzabi);

    <A extends zzb, T extends com.google.android.gms.internal.zzzv.zza<? extends Result, A>> T zzb(@NonNull T t);

    void zzuN();

    void zzvj();
}
