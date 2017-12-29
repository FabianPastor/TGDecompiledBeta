package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public interface zzcc {
    ConnectionResult blockingConnect();

    void connect();

    void disconnect();

    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    boolean isConnected();

    void zzahk();

    <A extends zzb, R extends Result, T extends zzm<R, A>> T zzd(T t);

    <A extends zzb, T extends zzm<? extends Result, A>> T zze(T t);
}
