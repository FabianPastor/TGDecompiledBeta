package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class zzbcx implements zzbbj, zzbdp {
    private final Context mContext;
    private zza<? extends zzctk, zzctl> zzaBe;
    private zzq zzaCA;
    private Map<Api<?>, Boolean> zzaCD;
    private final zze zzaCF;
    final zzbcp zzaCl;
    private final Lock zzaCv;
    final Map<zzc<?>, Api.zze> zzaDF;
    private final Condition zzaDS;
    private final zzbcz zzaDT;
    final Map<zzc<?>, ConnectionResult> zzaDU = new HashMap();
    private volatile zzbcw zzaDV;
    private ConnectionResult zzaDW = null;
    int zzaDX;
    final zzbdq zzaDY;

    public zzbcx(Context context, zzbcp com_google_android_gms_internal_zzbcp, Lock lock, Looper looper, zze com_google_android_gms_common_zze, Map<zzc<?>, Api.zze> map, zzq com_google_android_gms_common_internal_zzq, Map<Api<?>, Boolean> map2, zza<? extends zzctk, zzctl> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl, ArrayList<zzbbi> arrayList, zzbdq com_google_android_gms_internal_zzbdq) {
        this.mContext = context;
        this.zzaCv = lock;
        this.zzaCF = com_google_android_gms_common_zze;
        this.zzaDF = map;
        this.zzaCA = com_google_android_gms_common_internal_zzq;
        this.zzaCD = map2;
        this.zzaBe = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzctk__com_google_android_gms_internal_zzctl;
        this.zzaCl = com_google_android_gms_internal_zzbcp;
        this.zzaDY = com_google_android_gms_internal_zzbdq;
        ArrayList arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            ((zzbbi) obj).zza(this);
        }
        this.zzaDT = new zzbcz(this, looper);
        this.zzaDS = lock.newCondition();
        this.zzaDV = new zzbco(this);
    }

    public final ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzaDS.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzazX : this.zzaDW != null ? this.zzaDW : new ConnectionResult(13, null);
    }

    public final ConnectionResult blockingConnect(long j, TimeUnit timeUnit) {
        connect();
        long toNanos = timeUnit.toNanos(j);
        while (isConnecting()) {
            if (toNanos <= 0) {
                try {
                    disconnect();
                    return new ConnectionResult(14, null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ConnectionResult(15, null);
                }
            }
            toNanos = this.zzaDS.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.zzazX : this.zzaDW != null ? this.zzaDW : new ConnectionResult(13, null);
    }

    public final void connect() {
        this.zzaDV.connect();
    }

    public final void disconnect() {
        if (this.zzaDV.disconnect()) {
            this.zzaDU.clear();
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.zzaDV);
        for (Api api : this.zzaCD.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((Api.zze) this.zzaDF.get(api.zzpd())).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }

    @Nullable
    public final ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        zzc zzpd = api.zzpd();
        if (this.zzaDF.containsKey(zzpd)) {
            if (((Api.zze) this.zzaDF.get(zzpd)).isConnected()) {
                return ConnectionResult.zzazX;
            }
            if (this.zzaDU.containsKey(zzpd)) {
                return (ConnectionResult) this.zzaDU.get(zzpd);
            }
        }
        return null;
    }

    public final boolean isConnected() {
        return this.zzaDV instanceof zzbca;
    }

    public final boolean isConnecting() {
        return this.zzaDV instanceof zzbcd;
    }

    public final void onConnected(@Nullable Bundle bundle) {
        this.zzaCv.lock();
        try {
            this.zzaDV.onConnected(bundle);
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void onConnectionSuspended(int i) {
        this.zzaCv.lock();
        try {
            this.zzaDV.onConnectionSuspended(i);
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void zza(@NonNull ConnectionResult connectionResult, @NonNull Api<?> api, boolean z) {
        this.zzaCv.lock();
        try {
            this.zzaDV.zza(connectionResult, api, z);
        } finally {
            this.zzaCv.unlock();
        }
    }

    final void zza(zzbcy com_google_android_gms_internal_zzbcy) {
        this.zzaDT.sendMessage(this.zzaDT.obtainMessage(1, com_google_android_gms_internal_zzbcy));
    }

    final void zza(RuntimeException runtimeException) {
        this.zzaDT.sendMessage(this.zzaDT.obtainMessage(2, runtimeException));
    }

    public final boolean zza(zzbei com_google_android_gms_internal_zzbei) {
        return false;
    }

    public final <A extends zzb, R extends Result, T extends zzbay<R, A>> T zzd(@NonNull T t) {
        t.zzpC();
        return this.zzaDV.zzd(t);
    }

    public final <A extends zzb, T extends zzbay<? extends Result, A>> T zze(@NonNull T t) {
        t.zzpC();
        return this.zzaDV.zze(t);
    }

    final void zzg(ConnectionResult connectionResult) {
        this.zzaCv.lock();
        try {
            this.zzaDW = connectionResult;
            this.zzaDV = new zzbco(this);
            this.zzaDV.begin();
            this.zzaDS.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }

    public final void zzpE() {
        if (isConnected()) {
            ((zzbca) this.zzaDV).zzpU();
        }
    }

    public final void zzpl() {
    }

    final void zzqh() {
        this.zzaCv.lock();
        try {
            this.zzaDV = new zzbcd(this, this.zzaCA, this.zzaCD, this.zzaCF, this.zzaBe, this.zzaCv, this.mContext);
            this.zzaDV.begin();
            this.zzaDS.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }

    final void zzqi() {
        this.zzaCv.lock();
        try {
            this.zzaCl.zzqe();
            this.zzaDV = new zzbca(this);
            this.zzaDV.begin();
            this.zzaDS.signalAll();
        } finally {
            this.zzaCv.unlock();
        }
    }
}
