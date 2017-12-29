package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzcxd;
import com.google.android.gms.internal.zzcxe;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzbi implements zzcc, zzu {
    private final Context mContext;
    private zza<? extends zzcxd, zzcxe> zzfmz;
    final zzba zzfpi;
    private final Lock zzfps;
    private zzr zzfpx;
    private Map<Api<?>, Boolean> zzfqa;
    private final zzf zzfqc;
    final Map<zzc<?>, zze> zzfsb;
    private final Condition zzfso;
    private final zzbk zzfsp;
    final Map<zzc<?>, ConnectionResult> zzfsq = new HashMap();
    private volatile zzbh zzfsr;
    private ConnectionResult zzfss = null;
    int zzfst;
    final zzcd zzfsu;

    public zzbi(Context context, zzba com_google_android_gms_common_api_internal_zzba, Lock lock, Looper looper, zzf com_google_android_gms_common_zzf, Map<zzc<?>, zze> map, zzr com_google_android_gms_common_internal_zzr, Map<Api<?>, Boolean> map2, zza<? extends zzcxd, zzcxe> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe, ArrayList<zzt> arrayList, zzcd com_google_android_gms_common_api_internal_zzcd) {
        this.mContext = context;
        this.zzfps = lock;
        this.zzfqc = com_google_android_gms_common_zzf;
        this.zzfsb = map;
        this.zzfpx = com_google_android_gms_common_internal_zzr;
        this.zzfqa = map2;
        this.zzfmz = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzcxd__com_google_android_gms_internal_zzcxe;
        this.zzfpi = com_google_android_gms_common_api_internal_zzba;
        this.zzfsu = com_google_android_gms_common_api_internal_zzcd;
        ArrayList arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            ((zzt) obj).zza(this);
        }
        this.zzfsp = new zzbk(this, looper);
        this.zzfso = lock.newCondition();
        this.zzfsr = new zzaz(this);
    }

    public final ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzfso.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzfkr : this.zzfss != null ? this.zzfss : new ConnectionResult(13, null);
    }

    public final void connect() {
        this.zzfsr.connect();
    }

    public final void disconnect() {
        if (this.zzfsr.disconnect()) {
            this.zzfsq.clear();
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.zzfsr);
        for (Api api : this.zzfqa.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((zze) this.zzfsb.get(api.zzagf())).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }

    public final boolean isConnected() {
        return this.zzfsr instanceof zzal;
    }

    public final boolean isConnecting() {
        return this.zzfsr instanceof zzao;
    }

    public final void onConnected(Bundle bundle) {
        this.zzfps.lock();
        try {
            this.zzfsr.onConnected(bundle);
        } finally {
            this.zzfps.unlock();
        }
    }

    public final void onConnectionSuspended(int i) {
        this.zzfps.lock();
        try {
            this.zzfsr.onConnectionSuspended(i);
        } finally {
            this.zzfps.unlock();
        }
    }

    public final void zza(ConnectionResult connectionResult, Api<?> api, boolean z) {
        this.zzfps.lock();
        try {
            this.zzfsr.zza(connectionResult, api, z);
        } finally {
            this.zzfps.unlock();
        }
    }

    final void zza(zzbj com_google_android_gms_common_api_internal_zzbj) {
        this.zzfsp.sendMessage(this.zzfsp.obtainMessage(1, com_google_android_gms_common_api_internal_zzbj));
    }

    final void zza(RuntimeException runtimeException) {
        this.zzfsp.sendMessage(this.zzfsp.obtainMessage(2, runtimeException));
    }

    public final void zzahk() {
        if (isConnected()) {
            ((zzal) this.zzfsr).zzaia();
        }
    }

    final void zzain() {
        this.zzfps.lock();
        try {
            this.zzfsr = new zzao(this, this.zzfpx, this.zzfqa, this.zzfqc, this.zzfmz, this.zzfps, this.mContext);
            this.zzfsr.begin();
            this.zzfso.signalAll();
        } finally {
            this.zzfps.unlock();
        }
    }

    final void zzaio() {
        this.zzfps.lock();
        try {
            this.zzfpi.zzaik();
            this.zzfsr = new zzal(this);
            this.zzfsr.begin();
            this.zzfso.signalAll();
        } finally {
            this.zzfps.unlock();
        }
    }

    public final <A extends zzb, R extends Result, T extends zzm<R, A>> T zzd(T t) {
        t.zzahi();
        return this.zzfsr.zzd(t);
    }

    public final <A extends zzb, T extends zzm<? extends Result, A>> T zze(T t) {
        t.zzahi();
        return this.zzfsr.zze(t);
    }

    final void zzg(ConnectionResult connectionResult) {
        this.zzfps.lock();
        try {
            this.zzfss = connectionResult;
            this.zzfsr = new zzaz(this);
            this.zzfsr.begin();
            this.zzfso.signalAll();
        } finally {
            this.zzfps.unlock();
        }
    }
}
