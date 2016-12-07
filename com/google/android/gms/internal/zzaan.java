package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.zzg;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class zzaan implements zzaau, zzzz {
    private final Context mContext;
    private final Condition zzaAE;
    private final zzb zzaAF;
    final Map<zzc<?>, ConnectionResult> zzaAG = new HashMap();
    private volatile zzaam zzaAH;
    private ConnectionResult zzaAI = null;
    int zzaAJ;
    final com.google.android.gms.internal.zzaau.zza zzaAK;
    final Map<zzc<?>, zze> zzaAr;
    final com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> zzaxY;
    final zzaal zzazd;
    private final Lock zzazn;
    final zzg zzazs;
    final Map<Api<?>, Integer> zzazu;
    private final com.google.android.gms.common.zzc zzazw;

    static abstract class zza {
        private final zzaam zzaAL;

        protected zza(zzaam com_google_android_gms_internal_zzaam) {
            this.zzaAL = com_google_android_gms_internal_zzaam;
        }

        public final void zzc(zzaan com_google_android_gms_internal_zzaan) {
            com_google_android_gms_internal_zzaan.zzazn.lock();
            try {
                if (com_google_android_gms_internal_zzaan.zzaAH == this.zzaAL) {
                    zzvA();
                    com_google_android_gms_internal_zzaan.zzazn.unlock();
                }
            } finally {
                com_google_android_gms_internal_zzaan.zzazn.unlock();
            }
        }

        protected abstract void zzvA();
    }

    final class zzb extends Handler {
        final /* synthetic */ zzaan zzaAM;

        zzb(zzaan com_google_android_gms_internal_zzaan, Looper looper) {
            this.zzaAM = com_google_android_gms_internal_zzaan;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ((zza) message.obj).zzc(this.zzaAM);
                    return;
                case 2:
                    throw ((RuntimeException) message.obj);
                default:
                    Log.w("GACStateManager", "Unknown message id: " + message.what);
                    return;
            }
        }
    }

    public zzaan(Context context, zzaal com_google_android_gms_internal_zzaal, Lock lock, Looper looper, com.google.android.gms.common.zzc com_google_android_gms_common_zzc, Map<zzc<?>, zze> map, zzg com_google_android_gms_common_internal_zzg, Map<Api<?>, Integer> map2, com.google.android.gms.common.api.Api.zza<? extends zzaxn, zzaxo> com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo, ArrayList<zzzy> arrayList, com.google.android.gms.internal.zzaau.zza com_google_android_gms_internal_zzaau_zza) {
        this.mContext = context;
        this.zzazn = lock;
        this.zzazw = com_google_android_gms_common_zzc;
        this.zzaAr = map;
        this.zzazs = com_google_android_gms_common_internal_zzg;
        this.zzazu = map2;
        this.zzaxY = com_google_android_gms_common_api_Api_zza__extends_com_google_android_gms_internal_zzaxn__com_google_android_gms_internal_zzaxo;
        this.zzazd = com_google_android_gms_internal_zzaal;
        this.zzaAK = com_google_android_gms_internal_zzaau_zza;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((zzzy) it.next()).zza(this);
        }
        this.zzaAF = new zzb(this, looper);
        this.zzaAE = lock.newCondition();
        this.zzaAH = new zzaak(this);
    }

    public ConnectionResult blockingConnect() {
        connect();
        while (isConnecting()) {
            try {
                this.zzaAE.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new ConnectionResult(15, null);
            }
        }
        return isConnected() ? ConnectionResult.zzawX : this.zzaAI != null ? this.zzaAI : new ConnectionResult(13, null);
    }

    public ConnectionResult blockingConnect(long j, TimeUnit timeUnit) {
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
            toNanos = this.zzaAE.awaitNanos(toNanos);
        }
        return isConnected() ? ConnectionResult.zzawX : this.zzaAI != null ? this.zzaAI : new ConnectionResult(13, null);
    }

    public void connect() {
        this.zzaAH.connect();
    }

    public void disconnect() {
        if (this.zzaAH.disconnect()) {
            this.zzaAG.clear();
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append(str).append("mState=").println(this.zzaAH);
        for (Api api : this.zzazu.keySet()) {
            printWriter.append(str).append(api.getName()).println(":");
            ((zze) this.zzaAr.get(api.zzuH())).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }

    @Nullable
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        zzc zzuH = api.zzuH();
        if (this.zzaAr.containsKey(zzuH)) {
            if (((zze) this.zzaAr.get(zzuH)).isConnected()) {
                return ConnectionResult.zzawX;
            }
            if (this.zzaAG.containsKey(zzuH)) {
                return (ConnectionResult) this.zzaAG.get(zzuH);
            }
        }
        return null;
    }

    public boolean isConnected() {
        return this.zzaAH instanceof zzaai;
    }

    public boolean isConnecting() {
        return this.zzaAH instanceof zzaaj;
    }

    public void onConnected(@Nullable Bundle bundle) {
        this.zzazn.lock();
        try {
            this.zzaAH.onConnected(bundle);
        } finally {
            this.zzazn.unlock();
        }
    }

    public void onConnectionSuspended(int i) {
        this.zzazn.lock();
        try {
            this.zzaAH.onConnectionSuspended(i);
        } finally {
            this.zzazn.unlock();
        }
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, R extends Result, T extends com.google.android.gms.internal.zzzv.zza<R, A>> T zza(@NonNull T t) {
        t.zzvf();
        return this.zzaAH.zza(t);
    }

    public void zza(@NonNull ConnectionResult connectionResult, @NonNull Api<?> api, int i) {
        this.zzazn.lock();
        try {
            this.zzaAH.zza(connectionResult, api, i);
        } finally {
            this.zzazn.unlock();
        }
    }

    void zza(zza com_google_android_gms_internal_zzaan_zza) {
        this.zzaAF.sendMessage(this.zzaAF.obtainMessage(1, com_google_android_gms_internal_zzaan_zza));
    }

    void zza(RuntimeException runtimeException) {
        this.zzaAF.sendMessage(this.zzaAF.obtainMessage(2, runtimeException));
    }

    public boolean zza(zzabi com_google_android_gms_internal_zzabi) {
        return false;
    }

    public <A extends com.google.android.gms.common.api.Api.zzb, T extends com.google.android.gms.internal.zzzv.zza<? extends Result, A>> T zzb(@NonNull T t) {
        t.zzvf();
        return this.zzaAH.zzb(t);
    }

    void zzh(ConnectionResult connectionResult) {
        this.zzazn.lock();
        try {
            this.zzaAI = connectionResult;
            this.zzaAH = new zzaak(this);
            this.zzaAH.begin();
            this.zzaAE.signalAll();
        } finally {
            this.zzazn.unlock();
        }
    }

    public void zzuN() {
    }

    void zzvO() {
        this.zzazn.lock();
        try {
            this.zzaAH = new zzaaj(this, this.zzazs, this.zzazu, this.zzazw, this.zzaxY, this.zzazn, this.mContext);
            this.zzaAH.begin();
            this.zzaAE.signalAll();
        } finally {
            this.zzazn.unlock();
        }
    }

    void zzvP() {
        this.zzazn.lock();
        try {
            this.zzazd.zzvL();
            this.zzaAH = new zzaai(this);
            this.zzaAH.begin();
            this.zzaAE.signalAll();
        } finally {
            this.zzazn.unlock();
        }
    }

    void zzvQ() {
        for (zze disconnect : this.zzaAr.values()) {
            disconnect.disconnect();
        }
    }

    public void zzvj() {
        if (isConnected()) {
            ((zzaai) this.zzaAH).zzvz();
        }
    }
}
